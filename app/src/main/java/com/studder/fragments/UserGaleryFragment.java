package com.studder.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.R;
import com.studder.adapters.ImageAdapter;
import com.studder.database.schema.UserTable;
import com.studder.model.Media;

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

    private OnFragmentInteractionListener mListener;

    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView ageTextView;
    private TextView aboutTextView;
    private GridView gridImageView;
    private TextView swipesTextView;
    private TextView matchesTextView;
    private ImageView profileImageView;

    private LoadImagesTask loadImagesTask;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_galery, container, false);

        SharedPreferences pref = getContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        Long userId = 1L;
        String name = pref.getString(UserTable.Cols.NAME, "Unknown");
        String surname = pref.getString(UserTable.Cols.SURNAME, "Unknown");
        String about = pref.getString(UserTable.Cols.DESCRIPTION, "Unknown");
        String age = pref.getString(UserTable.Cols.BIRTHDAY, "Unknown");

        nameTextView = view.findViewById(R.id.user_profile_fragment_name_text_view);
        surnameTextView = view.findViewById(R.id.user_profile_fragment_surname_text_view);
        ageTextView = view.findViewById(R.id.user_profile_fragment_age_text_view);
        aboutTextView = view.findViewById(R.id.user_profile_fragment_about_user_text_view);
        gridImageView = view.findViewById(R.id.user_profile_fragment_grid_view);
        swipesTextView = view.findViewById(R.id.user_profile_fragment_swipes_text_view);
        matchesTextView = view.findViewById(R.id.user_profile_fragment_matches_text_view);
        profileImageView = view.findViewById(R.id.user_profile_fragment_profile_image);

        nameTextView.setText(name);
        surnameTextView.setText(surname);
        aboutTextView.setText(about);
        //ageTextView.setText(calculateAge(age));

        loadImagesTask = new LoadImagesTask();
        loadImagesTask.execute(userId);
        return view;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private int calculateAge(String dateString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(dateString);
            calendar1.setTime(date);
            int diff = calendar2.get(Calendar.YEAR) - calendar1.get(Calendar.YEAR);
            if (calendar1.get(Calendar.MONTH) > calendar2.get(Calendar.MONTH) || (calendar1.get(Calendar.MONTH)
                    == calendar2.get(Calendar.MONTH) && calendar1.get(Calendar.DATE) > calendar2.get(Calendar.DATE))) {
                diff--;
            }
            return diff;
        } catch (ParseException e) {
            return 0;
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

    private class LoadImagesTask extends AsyncTask<Long, Void, Void> {

        @Override
        protected Void doInBackground(Long... userId) {
            Ion.with(getContext())
                    .load("GET", "http://10.0.2.2:8080/media/" + userId)
                    .as(new TypeToken<List<Media>>(){})
                    .withResponse()
                    .setCallback(new FutureCallback<Response<List<Media>>>() {
                        @Override
                        public void onCompleted(Exception e, Response<List<Media>> result) {
                            if(result.getHeaders().code() == 200){
                                List<Media> media = result.getResult();

                                for(int i =0;i < media.size();i++) {
                                    byte[] bitmapBytes = Base64.decode(media.get(i).getPath(), Base64.DEFAULT);
                                    Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                    bmp = bmp.createScaledBitmap(bmp, 200, 200, false);
                                    media.get(i).setBitmap(BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length));
                                }

                                ImageAdapter booksAdapter = new ImageAdapter(getActivity(), media);
                                gridImageView.setAdapter(booksAdapter);
                            }
                        }
                    });
            return null;
        }
    }

}
