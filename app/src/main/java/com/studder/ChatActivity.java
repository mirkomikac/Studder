package com.studder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.ListView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.adapters.MessageListAdapter;
import com.studder.database.schema.UserMatchTable;
import com.studder.database.schema.UserTable;
import com.studder.firestore.MessageFirestoreModel;
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
    private Button sendButton;
    private EditText editText;
    private LinearLayoutManager mLinearLayoutManager;
    private MessagesFetch mMessagesFetch;
    private UserMatch userMatch;
    private User loggedUser;
    private MessageListAdapter mRecyclerAdapter;
    private Query mQuery;
    private FirebaseFirestore db;

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


        Long userMatchId = getIntent().getLongExtra(UserMatchTable.Cols._ID,-1);

        db = FirebaseFirestore.getInstance();

        mQuery = db
                .collection("messages")
                .whereEqualTo("matchId", userMatchId)
                .orderBy("date")
                .limit(50);

        FirestoreRecyclerOptions<MessageFirestoreModel> options = new FirestoreRecyclerOptions.Builder<MessageFirestoreModel>()
                .setQuery(mQuery, MessageFirestoreModel.class)
                .build();

        mRecyclerAdapter = new MessageListAdapter(this, options);
        mMessageRecycler.setAdapter(mRecyclerAdapter);

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

            final Long userMatchId = getIntent().getLongExtra(UserMatchTable.Cols._ID,-1);

            if(userMatchId != -1){
                final String ipConfig = getResources().getString(R.string.ipconfig);
                Ion.with(getApplicationContext())
                        .load("GET","http://"+ipConfig+"/matches/" + userMatchId)
                        .as(new TypeToken<UserMatch>() {})
                        .withResponse()
                        .setCallback(new FutureCallback<Response<UserMatch>>() {
                            @Override
                            public void onCompleted(Exception e, Response<UserMatch> result) {
                                userMatch = result.getResult();
                            }
                        });
            }

            sendButton = (Button) findViewById(R.id.button_chatbox_send);
            editText = (EditText) findViewById(R.id.edittext_chatbox);

            editText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mLinearLayoutManager.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
                }
            });

            final String ipConfig = getResources().getString(R.string.ipconfig);


            sendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message m = new Message( editText.getText().toString(),new Date(),"SENT",userMatch,loggedUser);
                    JsonObject json = mapToJson(m);
                    Ion.with(getApplicationContext())
                            .load("POST","http://"+ipConfig+"/messages/" + userMatchId)
                            .setJsonObjectBody(json)
                            .as(new TypeToken<Message>(){})
                            .withResponse()
                            .setCallback(new FutureCallback<Response<Message>>() {
                                @Override
                                public void onCompleted(Exception e, Response<Message>  result) {
                                    if(result.getHeaders().code() == 200){
                                        Log.d(TAG, "code == 200");
                                        MessageFirestoreModel model = new MessageFirestoreModel();
                                        model.setId(result.getResult().getId());
                                        model.setDate(result.getResult().getTimeRecieved());
                                        model.setParticipant1(result.getResult().getMatch().getParticipant1().getUsername());
                                        model.setParticipant2(result.getResult().getMatch().getParticipant2().getUsername());
                                        model.setMatchId(result.getResult().getMatch().getId());
                                        model.setStatus("Delivered");
                                        model.setSender(result.getResult().getSender().getUsername());
                                        model.setText(result.getResult().getText());
                                        db.collection("messages").document(result.getResult().getId().toString()).set(model)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Log.d(TAG, "Success");
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d(TAG, "Fail");
                                                    }
                                                });


                                    } else {
                                        Log.d(TAG, "code != 200");
                                    }
                                }
                            });
                    editText.setText("");
                    mLinearLayoutManager.scrollToPosition(mRecyclerAdapter.getItemCount() - 1);
                }
            });

            return true;
        }

        private JsonObject mapToJson(Message m){
            Log.d(TAG, "mapToJson()");;

            JsonObject  json = new JsonObject ();

            json.addProperty("text", m.getText());
            json.addProperty("timeRecieved",Long.toString(m.getTimeRecieved().getTime()) );
            json.addProperty("status", m.getStatus());

            Log.d(TAG, "mapToJson() -> return == " + json);

            return json;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mRecyclerAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mRecyclerAdapter.stopListening();
    }
}
