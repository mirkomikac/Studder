package com.studder.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.holders.InboxRowViewHolder;
import com.studder.model.User;
import com.studder.model.UserMatch;

import java.util.ArrayList;
import java.util.List;

public class InboxFragment extends Fragment {

    public static final String TAG = "InboxFragment";

    private RecyclerView mInboxListRecyclerView;
    private SearchView mInboxSearchSearchView;

    private InboxListAdapter mInboxListAdapter;

    private ProgressBar mProgressBar;

    private MatchedFetch mMatchedFetch;
    private FirebaseFirestore db;

    public InboxFragment() {
        // Required empty public constructor
    }

    public static InboxFragment newInstance() {
        Log.d(TAG, "InboxFragment.newInstance -> start");

        InboxFragment fragment = new InboxFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        Log.d(TAG, "InboxFragment.newInstance -> success");
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d(TAG, "InboxFragment.onCreate -> has bundle");
        } else {
            Log.d(TAG, "InboxFragment.onCreate -> no bundle");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        Log.d(TAG, "onCreateView(...) -> start");

        mInboxListAdapter = new InboxListAdapter(new ArrayList<User>());
        mInboxListRecyclerView = view.findViewById(R.id.recycler_view_content_navigation_chat_box_list);
        mProgressBar = view.findViewById(R.id.progress_refresh_matched);

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mInboxListRecyclerView.setLayoutManager(lm);

        mMatchedFetch = new MatchedFetch();
        mMatchedFetch.execute((Void) null);

        mInboxSearchSearchView = view.findViewById(R.id.search_view_fragment_inbox_search);
        mInboxSearchSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                refreshMatched(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                refreshMatched(newText);
                return false;
            }
        });


        SharedPreferences pref = getActivity().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);

        db = FirebaseFirestore.getInstance();

        Integer userId = pref.getInt(UserTable.Cols._ID, -1);
        String username = pref.getString(UserTable.Cols.USERNAME, "-1");

        if(userId != -1) {
            db = FirebaseFirestore.getInstance();

            db.collection("messages")
                    .whereEqualTo("participant1", username)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                System.err.println("Listen failed:" + e);
                                return;
                            }
                            refreshMatched("");
                        }
                    });

            db.collection("messages")
                    .whereEqualTo("participant2", username)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                            if (e != null) {
                                System.err.println("Listen failed:" + e);
                                return;
                            }
                            refreshMatched("");
                        }
                    });
        }

        Log.d(TAG, "onCreateView(...) -> success");
        return view;
    }


    private void refreshMatched(String name){
        SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        final Integer id = preferences.getInt(UserTable.Cols._ID, -1);
        final String searchParam = name;
        String ipConfig = getContext().getResources().getString(R.string.ipconfig);
        if(id != -1) {
            Ion.with(getContext())
                    .load("http://"+ipConfig+"/matches/getMatchesMe")
                    .as(new TypeToken<List<UserMatch>>() {})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<List<UserMatch>>>() {
                        @Override
                        public void onCompleted(Exception e, Response<List<UserMatch>> result) {
                            if(result == null){
                                return;
                            }
                            if(result.getHeaders().code() == 200){
                                showProgress(true);
                                mInboxListAdapter.getUserList().clear();
                                mInboxListAdapter = null;
                                mInboxListRecyclerView.setAdapter(null);
                                Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> success -> 200");
                                List<UserMatch> matches = result.getResult();
                                ArrayList<User> users = new ArrayList<>();

                                for(int i = 0;i < matches.size();i++){
                                    if(matches.get(i).getParticipant1().getId() == id){
                                        User user = matches.get(i).getParticipant2();
                                        user.setmUserMatch(matches.get(i));
                                        String fullname = user.getName() + " " + user.getSurname();
                                        if(fullname.toLowerCase().contains(searchParam.toLowerCase())) {
                                            users.add(user);
                                        }
                                    } else {
                                        User user = matches.get(i).getParticipant1();
                                        user.setmUserMatch(matches.get(i));
                                        users.add(user);
                                        String fullname = user.getName() + " " + user.getSurname();
                                        if(fullname.toLowerCase().contains(searchParam.toLowerCase())) {
                                            users.add(user);
                                        }
                                    }
                                }

                                mInboxListAdapter = new InboxListAdapter(users);
                                mInboxListRecyclerView.setAdapter(mInboxListAdapter);
                                mInboxListAdapter.notifyDataSetChanged();
                                showProgress(false);

                                Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> success -> added adapter to RecyclerView");
                            } else {
                                Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> fail -> " + result.getHeaders().code());
                            }
                        }
                    });
        }
    }


    private class InboxListAdapter extends RecyclerView.Adapter<InboxRowViewHolder> {

        private List<User> userList;

        public InboxListAdapter(List<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public InboxRowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Log.d(TAG, "InboxListAdapter -> onCreateViewHolder(...) -> start");

            LayoutInflater inflater = LayoutInflater.from(getActivity());
            View view = inflater.inflate(R.layout.chat_box_row, parent, false);

            Log.d(TAG, "InboxListAdapter -> onCreateViewHolder(...) -> success");
            return new InboxRowViewHolder(view, getActivity());
        }

        @Override
        public void onBindViewHolder(@NonNull InboxRowViewHolder holder, int position) {
            User user = userList.get(position);
            Log.d(TAG, "InboxListAdapter -> onBindViewHolder");
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            Log.d(TAG, "InboxListAdapter -> getItemCount");
            return userList.size();
        }

        public List<User> getUserList(){
            return this.userList;
        }
    }

    @Override
    public void onResume() {

        Log.d(TAG, "onResume()");

        refreshMatched("");
        super.onResume();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        Log.d(TAG, "showProgress(boolean) -> " + show);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mInboxListRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
            mInboxListRecyclerView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mInboxListRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            mInboxListRecyclerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private class MatchedFetch extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            Log.d(TAG, "MatchedFetch -> doInBackground -> start");

            SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            final Integer id = preferences.getInt(UserTable.Cols._ID, -1);

            if(id != -1) {
                String ipConfig = getResources().getString(R.string.ipconfig);
                Ion.with(getContext())
                        .load("http://"+ipConfig+"/matches/getMatchesMe")
                        //.load("http://10.0.2.2:8080/matches/getMatchesMe")
                        .as(new TypeToken<List<UserMatch>>() {})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<List<UserMatch>>>() {
                            @Override
                            public void onCompleted(Exception e, Response<List<UserMatch>> result) {
                                if(result.getHeaders().code() == 200){
                                    showProgress(true);
                                    Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> success -> 200");
                                    List<UserMatch> matches = result.getResult();
                                    ArrayList<User> users = new ArrayList<>();
                                    mInboxListAdapter.getUserList().clear();
                                    mInboxListAdapter = null;
                                    mInboxListRecyclerView.setAdapter(null);
                                    for(int i = 0;i < matches.size();i++){
                                        if(matches.get(i).getParticipant1().getId() == id){
                                            User user = matches.get(i).getParticipant2();
                                            user.setProfileImageEncoded(matches.get(i).getParticipant2().getProfileImageEncoded());
                                            user.setmUserMatch(matches.get(i));
                                            users.add(user);
                                        } else {
                                            User user = matches.get(i).getParticipant1();
                                            user.setProfileImageEncoded(matches.get(i).getParticipant1().getProfileImageEncoded());
                                            user.setmUserMatch(matches.get(i));
                                            users.add(user);
                                        }
                                    }

                                    mInboxListAdapter = new InboxListAdapter(users);
                                    mInboxListRecyclerView.setAdapter(mInboxListAdapter);
                                    mInboxListAdapter.notifyDataSetChanged();
                                    showProgress(false);

                                    Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> success -> added adapter to RecyclerView");
                                } else {
                                    Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> fail -> " + result.getHeaders().code());
                                }
                            }
                        });
            }
            return true;
        }
    }
}
