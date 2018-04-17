package com.studder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.studder.adapters.MessageListAdapter;
import com.studder.model.Message;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new MessageListAdapter(this, initMessages());
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);

    }


    private List<Message> initMessages(){
        List<Message> messages = new ArrayList<Message>();

        Message m1 = new Message("Pozdrav","Mirko","nebitno",  120L,1L);
        Message m2 = new Message("Odgovor","Darko","nebitno",  120L,2L);
        Message m3 = new Message("Pozdrav","Mirko","nebitno",  120L,1L);

        messages.add(m1);
        messages.add(m2);
        messages.add(m3);

        return messages;
    }

}
