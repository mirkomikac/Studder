package com.studder.fragments.reusable;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.studder.R;

public class SwipeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = "SwipeFragment";

    private static final String ARG_MESSAGE_ID = "Message ID";
    private static final String ARG_IMAGE_ID = "Image ID";

    private TextView mMessageTextView;
    private ImageView mImageImageView;

    private int mMessageId;
    private int mImageId;

    public SwipeFragment() {
        // Required empty public constructor
    }

    public static SwipeFragment newInstance(int mMessageId, int mImageId) {
        SwipeFragment fragment = new SwipeFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE_ID, mMessageId);
        args.putInt(ARG_IMAGE_ID, mImageId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMessageId = getArguments().getInt(ARG_MESSAGE_ID);
            mImageId = getArguments().getInt(ARG_IMAGE_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        mMessageTextView = view.findViewById(R.id.text_view_swipe_fragment_message);
        mImageImageView = view.findViewById(R.id.image_view_swipe_fragment_image_displayed);

        mMessageTextView.setText(mMessageId);
        mImageImageView.setImageDrawable(ContextCompat.getDrawable(getContext(), mImageId));

        return view;
    }
}
