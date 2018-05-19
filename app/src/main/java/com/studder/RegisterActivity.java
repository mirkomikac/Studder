package com.studder;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullname;
    private EditText email;
    private EditText password;
    private Button signUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fullname = (EditText) findViewById(R.id.reg_fullname);
        email = (EditText) findViewById(R.id.reg_email);
        password = (EditText) findViewById(R.id.reg_password);

        signUpButton = (Button) findViewById(R.id.sign_up_button);

        signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //rest call, register user
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
