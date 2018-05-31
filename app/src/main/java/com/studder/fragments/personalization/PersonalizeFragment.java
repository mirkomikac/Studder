package com.studder.fragments.personalization;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.studder.NavigationActivity;
import com.studder.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalizeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalizeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalizeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private OnFragmentInteractionListener mListener;

    private static final String MPOSITION = "POSITION";
    private static String TAG = "PersonalizeFragment";

    private Integer position;

    private Button mSkipButton;

    // In case additional extra needs to be provided for the next activity
    private Intent newNavigationIntent(){

        Log.d(TAG, "newNavigationIntent()");
        Intent navigationIntent = new Intent(getActivity(), NavigationActivity.class);
        return navigationIntent;
    }

    public PersonalizeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment PersonalizeFragment.
     */
    public static PersonalizeFragment newInstance(int position) {

        Log.d(TAG,"newInstance(int position) -> " + position);

        PersonalizeFragment fragment = new PersonalizeFragment();

        Bundle args = new Bundle();
        args.putInt(MPOSITION, position);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView(...)");

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personalize, container, false);

        mSkipButton = (Button) view.findViewById(R.id.button_personalize_personalize_skip_personalization);

        Log.d(TAG, "onCreateView -> mSkipButton.setOnClickListener");

        mSkipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationIntent = newNavigationIntent();
                startActivity(navigationIntent);
                getActivity().finish();
            }
        });

        position = getArguments().getInt(MPOSITION);
        return view;
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
}
