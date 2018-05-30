package com.studder;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.studder.adapters.MessageListAdapter;
import com.studder.model.Message;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;
    private Button sendButton;
    private EditText editText;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_chat);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, initMessages());
        mMessageRecycler.setLayoutManager(mLinearLayoutManager);
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
                Message m = new Message( editText.getText().toString(),"Darko","",new Date().getTime(),1L);
                editText.setText("");
                mMessageAdapter.getMessageList().add(m);
                mMessageAdapter.notifyDataSetChanged();
                mLinearLayoutManager.scrollToPosition(mMessageAdapter.getItemCount() - 1);
            }
        });

    }


    private List<Message> initMessages(){
        List<Message> messages = new ArrayList<Message>();

        Message m1 = new Message("Pozdrav","Mirko","nebitno",  1527536700000L,2L);
        Message m2 = new Message("Sta ima?","Mirko","nebitno",  1527536723000L,2L);
        Message m3 = new Message("Jel idemo posle na pivo?","Mirko","nebitno",  1527536727000L,2L);
        Message m4 = new Message("Nova poruka","Darko","nebitno",  1527537027000L,1L);
        Message m5 = new Message("Pozdrav","Darko","nebitno",  1527537033000L,1L);
        Message m6 = new Message("Cao","Mirko","nebitno",  1527626039288L,2L);

        messages.addAll(Arrays.asList(m1,m2,m3,m4,m5,m6));

        return messages;
    }

}
