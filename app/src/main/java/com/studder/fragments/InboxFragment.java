package com.studder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
    private InboxListAdapter mInboxListAdapter;

    private MatchedFetch mMatchedFetch;

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

        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        mInboxListRecyclerView.setLayoutManager(lm);

        mMatchedFetch = new MatchedFetch();
        mMatchedFetch.execute((Void) null);

        Log.d(TAG, "onCreateView(...) -> success");
        return view;
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
    }

    private class MatchedFetch extends AsyncTask<Void, Void, Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

            Log.d(TAG, "MatchedFetch -> doInBackground -> start");

            SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            final Integer id = preferences.getInt(UserTable.Cols._ID, -1);

            if(id != -1) {
                Ion.with(getContext())
                        .load("http://10.0.2.2:8080/matches/getMatches/" + id)
                        .as(new TypeToken<List<UserMatch>>() {})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<List<UserMatch>>>() {
                            @Override
                            public void onCompleted(Exception e, Response<List<UserMatch>> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.d(TAG, "MatchedFetch -> doInBackground -> ion -> success -> 200");
                                    List<UserMatch> matches = result.getResult();
                                    ArrayList<User> users = new ArrayList<>();

                                    for(int i = 0;i < matches.size();i++){
                                        if(matches.get(i).getParticipant1().getId() == id){
                                            User user = matches.get(i).getParticipant2();
                                            user.setmUserMatch(matches.get(i));
                                            users.add(user);
                                        } else {
                                            User user = matches.get(i).getParticipant1();
                                            user.setmUserMatch(matches.get(i));
                                            users.add(user);
                                        }
                                    }

                                    mInboxListAdapter = new InboxListAdapter(users);
                                    mInboxListRecyclerView.setAdapter(mInboxListAdapter);
                                    mInboxListAdapter.notifyDataSetChanged();

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
