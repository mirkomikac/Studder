package com.studder;

import android.app.Application;
import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

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
                Ion.with(context)
                        .load("http://10.0.2.2:8080//users")
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .setCallback(new FutureCallback<JsonObject >() {
                            @Override
                            public void onCompleted(Exception e, JsonObject  result) {
                                Log.i("Completeeed?", "ok");
                            }
                        });
            }
        });

        /*signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                //rest call, register user
                Ion.with(context)
                        .load("http://api.icndb.com/jokes/random")
                        .asString()
                        .setCallback(new FutureCallback<String>() {
                            @Override
                            public void onCompleted(Exception e, String result) {

                                try {
                                    JSONObject obj = new JSONObject(result);
                                    JSONObject val = obj.getJSONObject("value");
                                    String joke = val.getString("joke");
                                    Log.i("JOKE", joke);
                                } catch (JSONException e1) {
                                    Log.wtf("JSONExc", "JSON object didn't arrive.");
                                }


                            }
                        });
            }
        });*/


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private JsonObject mapToJson(){

        JsonObject  json = new JsonObject ();

        json.addProperty("username", fullname.getText().toString());
        json.addProperty("password", password.getText().toString());

        return json;
    }

}
