package com.studder.fragments.personalization;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.studder.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ImageDescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ImageDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ImageDescriptionFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private static final String MPOSITION = "POSITION";
    private Integer position;

    public ImageDescriptionFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ImageDescriptionFragment.
     */
    public static ImageDescriptionFragment newInstance(int position) {
        ImageDescriptionFragment fragment = new ImageDescriptionFragment();

        Bundle args = new Bundle();
        args.putInt(MPOSITION, position);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        position = getArguments().getInt(MPOSITION);
        return inflater.inflate(R.layout.fragment_image_description, container, false);
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
