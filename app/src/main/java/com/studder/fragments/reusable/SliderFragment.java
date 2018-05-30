package com.studder.fragments.reusable;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.studder.R;

public class SliderFragment extends Fragment {

    private static final String TAG = "SliderFragment";

    private static final String ARG_DOTS_COUNT = "Dots count";

    private LinearLayout mDotSlider;
    private ImageView[] mDots;

    private int dotsCount;

    public SliderFragment() {
        // Required empty public constructor
    }

    public static SliderFragment newInstance(int dotsCount) {
        SliderFragment fragment = new SliderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_DOTS_COUNT, dotsCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dotsCount = getArguments().getInt(ARG_DOTS_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_slider, container, false);

        mDotSlider = view.findViewById(R.id.linear_layout_fragment_slider_dot_slider);
        mDots = new ImageView[dotsCount];

        for(int i = 0; i < dotsCount; i++){
            mDots[i] = new ImageView(getContext());
            mDots[i].setImageDrawable(ContextCompat.getDrawable(getActivity().getApplicationContext(), R.drawable.nonactive_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            params.setMargins(8, 0, 8, 0);

            mDotSlider.addView(mDots[i], params);
        }
        mDots[0].setImageDrawable(ContextCompat.getDrawable(getActivity(). getApplicationContext(), R.drawable.active_dot));

        return view;
    }

    public void updateDot(int position){
        for(int i = 0; i< dotsCount; i++){
            mDots[i].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.nonactive_dot));
        }
        mDots[position].setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.active_dot));
    }

}
