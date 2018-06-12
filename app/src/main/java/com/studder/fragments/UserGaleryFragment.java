package com.studder.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;
import com.studder.adapters.ImageAdapter;
import com.studder.database.schema.UserTable;
import com.studder.model.Media;
import com.studder.model.User;
import com.studder.utils.ClientUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserGaleryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserGaleryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserGaleryFragment extends Fragment {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    private OnFragmentInteractionListener mListener;

    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView ageTextView;
    private TextView aboutTextView;
    private GridView gridImageView;
    private TextView swipesTextView;
    private TextView matchesTextView;
    private ImageView profileImageView;
    private LinearLayout lastOnlineLinearLayout;
    private TextView isOnlineTextView;
    private TextView lastOnlineFixedTextView;
    private TextView lastOnlineTextView;

    private LoadImagesTask loadImagesTask;
    private LoadProfilePicture loadProfilePictureTask;
    private FetchMatchesCount fetchMatchesCountTask;

    Integer userId;
    String username;
    String name;
    String surname;
    String about;
    Date age;

    Boolean isPrivate;
    Date lastOnline;
    Boolean onlineStatus;

    public UserGaleryFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserGaleryFragment.
     */
    public static UserGaleryFragment newInstance() {
        UserGaleryFragment fragment = new UserGaleryFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState != null){
            userId = savedInstanceState.getInt("userId", 0);
            username = savedInstanceState.getString("userUsername");
            name = savedInstanceState.getString("userName");
            surname = savedInstanceState.getString("userSurname");
            about = savedInstanceState.getString("userDescription");
            age = new Date(savedInstanceState.getLong("userBirthday", 0L));

            isPrivate = savedInstanceState.getBoolean("userIsPrivate", false);
            lastOnline = new Date(savedInstanceState.getLong("userLastOnline", 0L));
            onlineStatus = savedInstanceState.getBoolean("userOnlineStatus", false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("userId", userId);
        outState.putString("userUsername", username);

        outState.putString("userName", name);
        outState.putString("userSurname", surname);
        outState.putString("userDescription", about);
        outState.putLong("userBirthday", age.getTime());

        outState.putBoolean("userIsPrivate", isPrivate);
        outState.putLong("userLastOnline", lastOnline.getTime());
        outState.putBoolean("userOnlineStatus", onlineStatus);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_galery, container, false);

        if(getActivity().getIntent().getIntExtra("userId", -1) != -1) {
            userId = getActivity().getIntent().getIntExtra("userId", 0);
            username = getActivity().getIntent().getStringExtra("userUsername");
            name = getActivity().getIntent().getStringExtra("userName");
            surname = getActivity().getIntent().getStringExtra("userSurname");
            about = getActivity().getIntent().getStringExtra("userDescription");
            age = new Date(getActivity().getIntent().getLongExtra("userBirthday", 0L));

            isPrivate = getActivity().getIntent().getBooleanExtra("userIsPrivate", false);
            lastOnline = new Date(getActivity().getIntent().getLongExtra("userLastOnline", 0L));
            onlineStatus = getActivity().getIntent().getBooleanExtra("userOnlineStatus", false);
        } else {
            SharedPreferences preferences = getActivity().getSharedPreferences("CURRENT_PROFILE", getActivity().MODE_PRIVATE);
            userId = preferences.getInt("userId", 0);
            username = preferences.getString("userUsername", "-1");
            name = preferences.getString("userName", "-1");
            surname = preferences.getString("userSurname", "-1");
            about = preferences.getString("userDescription", "-1");
            age = new Date(preferences.getLong("userBirthday", 0L));

            isPrivate = preferences.getBoolean("userIsPrivate", false);
            lastOnline = new Date(preferences.getLong("userLastOnline", 0L));
            onlineStatus = preferences.getBoolean("userOnlineStatus", false);
        }
        nameTextView = view.findViewById(R.id.user_profile_fragment_name_text_view);
        surnameTextView = view.findViewById(R.id.user_profile_fragment_surname_text_view);
        ageTextView = view.findViewById(R.id.user_profile_fragment_age_text_view);
        aboutTextView = view.findViewById(R.id.user_profile_fragment_about_user_text_view);
        gridImageView = view.findViewById(R.id.user_profile_fragment_grid_view);
        matchesTextView = view.findViewById(R.id.user_profile_fragment_matches_text_view);
        profileImageView = view.findViewById(R.id.user_profile_fragment_profile_image);

        lastOnlineLinearLayout = view.findViewById(R.id.user_profile_fragment_online_status_linear_layout);
        isOnlineTextView = view.findViewById(R.id.user_profile_fragment_is_online_text_view);
        lastOnlineFixedTextView = view.findViewById(R.id.user_profile_fragment_last_online_fixed_text_view);
        lastOnlineTextView = view.findViewById(R.id.user_profile_fragment_last_online_text_view);

        nameTextView.setText(name);
        surnameTextView.setText(surname);
        aboutTextView.setText(about);
        ageTextView.setText(ClientUtils.calculateAge(age)+"");
        lastOnlineTextView.setText(dateFormat.format(lastOnline));

        if(onlineStatus) {
            lastOnlineLinearLayout.removeView(lastOnlineFixedTextView);
            lastOnlineLinearLayout.removeView(lastOnlineTextView);
        }else {
            lastOnlineLinearLayout.removeView(isOnlineTextView);
        }

        loadImagesTask = new LoadImagesTask();
        loadImagesTask.execute(userId);

        loadProfilePictureTask = new LoadProfilePicture();
        loadProfilePictureTask.execute(userId);

        fetchMatchesCountTask = new FetchMatchesCount();
        fetchMatchesCountTask.execute(userId);

        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private class LoadImagesTask extends AsyncTask<Integer, Void, List<Media>> {

        @Override
        protected List<Media> doInBackground(Integer... userId) {

            Integer id = userId[0];
            String ipConfig = getResources().getString(R.string.ipconfig);
            Ion.with(getContext())
                    .load("GET", "http://" + ipConfig + "/media/" + id)
                    .as(new TypeToken<List<Media>>(){})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<List<Media>>>() {
                        @Override
                        public void onCompleted(Exception e, Response<List<Media>> result) {
                            if(result.getHeaders().code() == 200){
                                List<Media> media = result.getResult();

                                for(int i = 0; i < media.size(); i++) {
                                    media.get(i).setBitmap(ClientUtils.getBitmapForMedia(media.get(i).getEncodedImage()));
                                }

                                ImageAdapter imageAdapter = new ImageAdapter(getActivity(), media);
                                gridImageView.setAdapter(imageAdapter);
                            }
                        }
                    });
            return null;
        }
    }

    private class LoadProfilePicture extends AsyncTask<Integer, Void, Media> {

        @Override
        protected Media doInBackground(Integer... userId) {

            Integer id = userId[0];
            String ipConfig = getResources().getString(R.string.ipconfig);
            Ion.with(getContext())
                    .load("GET", "http://" + ipConfig + "/media/getProfileImage/" + id)
                    .as(new TypeToken<Media>(){})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<Media>>() {
                        @Override
                        public void onCompleted(Exception e, Response<Media> result) {
                            if(result.getHeaders().code() == 200){
                                Media media = result.getResult();
                                String encodedMedia = media.getEncodedImage();
                                media.setBitmap(ClientUtils.getBitmapForMedia(encodedMedia));
                                profileImageView.setImageBitmap(media.getBitmap());

                                Intent intent = getActivity().getIntent();
                                String path = ClientUtils.saveMediaToPhoneStorage(intent.getStringExtra("userUsername"), media.getName(), encodedMedia);
                                intent.putExtra("userProfileImagePath", path);
                            }
                        }
                    });
            return null;
        }
    }

    private class FetchMatchesCount extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... userId) {

            Integer id = userId[0];

            String ipConfig = getResources().getString(R.string.ipconfig);
            Ion.with(getContext())
                    .load("GET", "http://" + ipConfig + "/matches/count/" + id)
                    .as(new TypeToken<Integer>(){})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<Integer>>() {
                        @Override
                        public void onCompleted(Exception e, Response<Integer> result) {
                            if(result.getHeaders().code() == 200){

                                Integer matchesCount = result.getResult();
                                matchesTextView.setText(matchesCount + "");
                            }
                        }
                    });
            return null;
        }
    }

}
