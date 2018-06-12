package com.studder.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.studder.R;
import com.studder.model.Media;
import com.studder.utils.ClientUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MediaPreviewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MediaPreviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MediaPreviewFragment extends Fragment {

    private ImageView userProfilePicture;
    private TextView usernameTextView;
    private ImageView mediaImageView;
    private TextView mediaDescriptionTextView;
    private TextView mediaDateAddedTextView;
    private String mUsername;

    private OnFragmentInteractionListener mListener;

    public MediaPreviewFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MediaPreviewFragment.
     */
    public static MediaPreviewFragment newInstance() {
        MediaPreviewFragment fragment = new MediaPreviewFragment();
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
        View view = inflater.inflate(R.layout.fragment_media_preview, container, false);
        userProfilePicture = view.findViewById(R.id.media_preview_fragment_profile_picture);
        usernameTextView = view.findViewById(R.id.media_preview_fragment_username_text_view);
        mediaImageView = view.findViewById(R.id.media_preview_fragment_media_image_view);
        mediaDescriptionTextView = view.findViewById(R.id.media_preview_fragment_description_text_view);
        mediaDateAddedTextView = view.findViewById(R.id.media_preview_fragment_media_date_added_text_view);

        Intent intent = getActivity().getIntent();
        mUsername = intent.getStringExtra("userUsername");


        String encodedProfileImage = ClientUtils.readMediaFromFile(intent.getStringExtra("userProfileImagePath"));
        Bitmap profilePicture = ClientUtils.getBitmapForMedia(encodedProfileImage);
        Long mediaId = intent.getLongExtra("mediaId", 0L);

        String encodedMedia = ClientUtils.readMediaFromFile(intent.getStringExtra("mediaPath"));
        Bitmap mediaBitmap = ClientUtils.getBitmapForMedia(encodedMedia);
        Long mediaDateAdded = intent.getLongExtra("mediaDateAdded", 0L);
        String mediaDescription = intent.getStringExtra("mediaDescription");

        userProfilePicture.setImageBitmap(profilePicture);
        usernameTextView.setText(mUsername);
        mediaImageView.setImageBitmap(mediaBitmap);
        mediaDescriptionTextView.setText(mediaDescription);
        mediaDateAddedTextView.setText(ClientUtils.formatDateTime(mediaDateAdded, ClientUtils.DEFAULT_DATE_FORMAT));

        userProfilePicture.setOnClickListener(new CloseMediaPreviewFragment());

        usernameTextView.setOnClickListener(new CloseMediaPreviewFragment());

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

    private class CloseMediaPreviewFragment implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            getActivity().finish();
        }

    }

}
