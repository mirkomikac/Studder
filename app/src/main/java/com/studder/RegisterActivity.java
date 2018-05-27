package com.studder;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class RegisterActivity extends AppCompatActivity {

    private EditText fullname;
    private EditText email;
    private EditText password;
    private Button signUpButton;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        email = (EditText) findViewById(R.id.reg_email);

        context = getApplicationContext();

        signUpButton = (Button) findViewById(R.id.sign_up_button);

        fullname = (EditText) findViewById(R.id.reg_fullname);
        password = (EditText) findViewById(R.id.reg_password);


       // Log.i("JSONTEST", json.toString());

        signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                JsonObject  json = mapToJson();
                String url = "http://10.0.2.2:8080//users";
                Ion.with(context)
                        .load(url)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject>  result) {
                                if(result.getHeaders().code() == 200){
                                    Log.i("Completeeed?", "ok");
                                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginActivity);
                                    finish();
                                } else {
                                    Log.e("Completeeed?", "nok");
                                }

                            }
                        });
            }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private JsonObject mapToJson(){

        JsonObject  json = new JsonObject ();

        json.addProperty("username", email.getText().toString());
        json.addProperty("password", password.getText().toString());

        return json;
    }

}
