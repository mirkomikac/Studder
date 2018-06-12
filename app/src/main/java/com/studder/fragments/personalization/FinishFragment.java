package com.studder.fragments.personalization;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.NavigationActivity;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.model.User;
import com.studder.utils.ClientUtils;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FinishFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FinishFragment extends Fragment {

    public static String TAG = "FinishFragment";

    private Button mFinishButton;

    public FinishFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FinishFragment.
     */

    private Intent getFinishIntent(){
        return  new Intent(getActivity(), NavigationActivity.class);
    }

    public static FinishFragment newInstance() {

        Log.d(TAG, "newInstance()");

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

        Log.d(TAG, "onCreate(Bundle)");

        View view = inflater.inflate(R.layout.fragment_finish, container, false);

        mFinishButton = (Button) view.findViewById(R.id.finishButton);

        Log.d(TAG, "onCreate -> mFinishButton.setOnClickListener");
        mFinishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UpdateUserInfoTask().doInBackground();
            }
        });

        return view;

    }

    public class UpdateUserInfoTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent = getActivity().getIntent();

            final String userName = intent.getStringExtra("userName");
            final String userSurname = intent.getStringExtra("userSurname");
            final String aboutUser = intent.getStringExtra("aboutUser");
            final String userBirthday = intent.getStringExtra("userBirthday");
            Date birthday = ClientUtils.parseDateTime(userBirthday, ClientUtils.DEFAULT_DATE_FORMAT);
            final Integer chosenRange = intent.getIntExtra("chosenRange", 2);

            final SharedPreferences preferences = getActivity().getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
            final String username = preferences.getString(UserTable.Cols.USERNAME, "");

            if(birthday == null) {
                birthday = new Date();
            }

            JsonObject json = new JsonObject();
            json.addProperty("username", username);
            json.addProperty("name", userName);
            json.addProperty("surname", userSurname);
            json.addProperty("description", aboutUser);
            json.addProperty("birthday", birthday.getTime());
            json.addProperty("radius", chosenRange);

            String ipConfig = getResources().getString(R.string.ipconfig);

            Ion.with(getContext())
                    .load("POST","http://"+ipConfig+"/users/update")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if(result.getHeaders().code() == 200) {
                                Intent navigationActivity = getFinishIntent();
                                startActivity(navigationActivity);
                                getActivity().finish();

                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString(UserTable.Cols.USERNAME, username);
                                editor.putString(UserTable.Cols.NAME, userName);
                                editor.putString(UserTable.Cols.SURNAME, userSurname);
                                editor.putString(UserTable.Cols.DESCRIPTION, aboutUser);
                                editor.putString(UserTable.Cols.BIRTHDAY, userBirthday);
                                editor.putInt(UserTable.Cols.RADIUS, chosenRange);
                            }
                        }
                    });
            return null;
        }
    }

}
