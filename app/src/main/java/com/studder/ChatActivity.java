package com.studder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.adapters.MessageListAdapter;
import com.studder.database.schema.UserMatchTable;
import com.studder.database.schema.UserTable;
import com.studder.fragments.InboxFragment;
import com.studder.model.Message;
import com.studder.model.User;
import com.studder.model.UserMatch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    public static final String TAG = "ChatAcitivity";

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button sendButton;
    private EditText editText;
    private LinearLayoutManager mLinearLayoutManager;
    private MessagesFetch mMessagesFetch;
    private UserMatch userMatch;
    private User loggedUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageRecycler.setLayoutManager(mLinearLayoutManager);


        mMessagesFetch = new MessagesFetch();
        mMessagesFetch.execute((Void) null);
    }

    private class MessagesFetch extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d(TAG, "MessagesFetch -> doInBackground -> start");

            try {
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
                    final Integer id = preferences.getInt(UserTable.Cols._ID, -1);
                    final String username = preferences.getString(UserTable.Cols.USERNAME, "");
                    final String name = preferences.getString(UserTable.Cols.NAME, "");
                    final String surname = preferences.getString(UserTable.Cols.SURNAME, "");
                    final String birthday = preferences.getString(UserTable.Cols.BIRTHDAY, "");
                    final String description = preferences.getString(UserTable.Cols.DESCRIPTION, "");
                    final String userGender = preferences.getString(UserTable.Cols.USER_GENDER, "");
                    final Integer radius = preferences.getInt(UserTable.Cols.RADIUS, -1);
                    final String swipeThrow = preferences.getString(UserTable.Cols.SWIPE_THROW, "");
                    final Boolean isPrivate = preferences.getBoolean(UserTable.Cols.IS_PRIVATE, true);
                    loggedUser = new User();
                    loggedUser.setId(id);
                    loggedUser.setUsername(username);
                    loggedUser.setName(name);
                    loggedUser.setSurname(surname);
                    loggedUser.setBirthday(new SimpleDateFormat("dd/MM/yyyy").parse(birthday));
                    loggedUser.setDescription(description);
                    loggedUser.setUserGender(userGender);
                    loggedUser.setSwipeThrow(swipeThrow);
                    loggedUser.setIsPrivate(isPrivate);
            } catch (Exception e) {
                e.printStackTrace();
            }

            Long participant2Id = getIntent().getLongExtra(UserTable.Cols._ID,-1);
            Long userMatchId = getIntent().getLongExtra(UserMatchTable.Cols._ID,-1);

            if(userMatchId != -1){
                String ipConfig = getResources().getString(R.string.ipconfig);
                Ion.with(getApplicationContext())
                        .load("http://"+ipConfig+"/matches/" + userMatchId)
                        .as(new TypeToken<UserMatch>() {})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<UserMatch>>() {
                            @Override
                            public void onCompleted(Exception e, Response<UserMatch> result) {
                                userMatch = result.getResult();
                            }
                        });
                Ion.with(getApplicationContext())
                        .load("http://"+ipConfig+"/messages/match/" + userMatchId)
                        .as(new TypeToken<List<Message>>() {})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<List<Message>>>() {
                            @Override
                            public void onCompleted(Exception e, Response<List<Message>> result) {
                                if(result.getHeaders().code() == 200){
                                    Log.d(TAG, "MessagesFetch -> doInBackground -> ion -> success -> 200");
                                    List<Message> messages = result.getResult();

                                    mMessageAdapter = new MessageListAdapter(getApplicationContext(), messages);
                                    mMessageRecycler.setAdapter(mMessageAdapter);

                                    sendButton = (Button) findViewById(R.id.button_chatbox_send);
                                    editText = (EditText) findViewById(R.id.edittext_chatbox);

                                    editText.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View v) {
                                            mLinearLayoutManager.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                                        }
                                    });

                                    sendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Message m = new Message( editText.getText().toString(),new Date(),"SENT",userMatch,loggedUser);
                                            editText.setText("");
                                            mMessageAdapter.getMessageList().add(m);
                                            mMessageAdapter.notifyDataSetChanged();
                                            mLinearLayoutManager.scrollToPosition(mMessageAdapter.getItemCount() - 1);
                                        }
                                    });

                                    Log.d(TAG, "MessagesFetch -> doInBackground -> ion -> success -> added adapter to RecyclerView");
                                } else {
                                    Log.d(TAG, "MessagesFetch -> doInBackground -> ion -> fail -> " + result.getHeaders().code());
                                }
                            }
                        });
            }

            return true;
        }

    }


}
