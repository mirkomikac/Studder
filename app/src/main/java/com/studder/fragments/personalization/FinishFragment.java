package com.studder.fragments.personalization;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.studder.NavigationActivity;
import com.studder.PersonalizeActivity;
import com.studder.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishFragment extends Fragment {

    public FinishFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FinishFragment.
     */

    public static FinishFragment newInstance() {

        FinishFragment fragment = new FinishFragment();
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
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        final Button finishButton = (Button) view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent navigationActivity = new Intent(getActivity(), NavigationActivity.class);
                startActivity(navigationActivity);
                getActivity().finish();
            }
        });

        return view;

    }
}
