package com.studder.fragments.personalization;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.studder.R;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserInfoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private SeekBar seekBar;
    private TextView textView;
    private Button birthdayButton;
    private TextView birthdayTextView;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private int dayOfBirth = 0;
    private int monthOfBirth = 0;
    private int yearOfBirth = 0;
    private int chosenRange = 70;

    public UserInfoFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserInfoFragment.
     */
    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
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
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        seekBar = (SeekBar) view.findViewById(R.id.user_info_fragment_range_seek_bar);
        textView = (TextView) view.findViewById(R.id.user_info_fragment_range_text_field);
        birthdayButton = (Button) view.findViewById(R.id.user_info_fragment_birthday_button);
        birthdayTextView = (TextView) view.findViewById(R.id.user_info_fragment_birthday_text_view);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                chosenRange = progress;
                textView.setText("Range is set to " + chosenRange + " km");
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                textView.setText("Range is set to " + chosenRange + " km");
                Toast.makeText(getActivity().getApplicationContext(), "Range is set to " + chosenRange + " km", Toast.LENGTH_SHORT).show();
            }

        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                dayOfBirth = dayOfMonth;
                monthOfBirth = month;
                yearOfBirth = year;
                birthdayTextView.setText("Birthday: " + dayOfMonth + "/" + month + "/" + year);
            }
        };

        birthdayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                yearOfBirth = calendar.get(Calendar.YEAR);
                monthOfBirth = calendar.get(Calendar.MONTH);
                dayOfBirth = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, yearOfBirth, monthOfBirth, dayOfBirth);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        return view;
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

}
