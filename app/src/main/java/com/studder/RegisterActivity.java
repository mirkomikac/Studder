package com.studder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;

public class RegisterActivity extends AppCompatActivity {

    public static final String TAG = "RegisterActivity";

    private EditText mFullnameEditText;
    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private EditText mConfirmPasswordEditText;
    private Button mSignUpButton;
    private LinearLayout mRegisterFormLinearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(Bundle)");

        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mEmailEditText = (EditText) findViewById(R.id.edit_text_activity_register_email);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mFullnameEditText = (EditText) findViewById(R.id.edit_text_activity_register_fullname);
        mPasswordEditText = (EditText) findViewById(R.id.edit_text_activity_register_password);
        mConfirmPasswordEditText = (EditText) findViewById(R.id.edit_text_activity_register_confirm_password);
        mRegisterFormLinearLayout = findViewById(R.id.linear_layout_activity_register_register_form);

       // Log.i("JSONTEST", json.toString());

        Log.d(TAG, "onCreate -> mSignUpButton.setOnClickListener");
        mSignUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {

                String ipConfig = getResources().getString(R.string.ipconfig);

                Log.d(TAG, "mSignUpButton -> trigger::onClick");

                if(validate()){
                    return;
                }

                JsonObject  json = mapToJson();

                String url = "http://"+ipConfig+"//users";

                Ion.with(getApplicationContext())
                        .load(url)
                        .setJsonObjectBody(json)
                        .asJsonObject()
                        .withResponse()
                        .setCallback(new FutureCallback<Response<JsonObject>>() {
                            @Override
                            public void onCompleted(Exception e, Response<JsonObject>  result) {
                                if(result.getHeaders().code() == 200){
                                    Log.d(TAG, "mSignUpButton -> trigger::onClick -> Ion Response == 200");;

                                    Toast.makeText(RegisterActivity.this, R.string.login_register_activity_success, Toast.LENGTH_SHORT);

                                    Intent loginActivity = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(loginActivity);
                                    finish();
                                } else {
                                    Log.d(TAG, "mSignUpButton -> trigger::onClick -> Ion Response == " + result.getHeaders().code());

                                    Toast.makeText(RegisterActivity.this, R.string.login_register_activity_success, Toast.LENGTH_SHORT);

                                    showProgress(false);
                                }
                            }
                        });
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {

        Log.d(TAG, "showProgress(boolean) -> " + show);

        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterFormLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mRegisterFormLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormLinearLayout.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mRegisterFormLinearLayout.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterFormLinearLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    private boolean validate() {

        Log.d(TAG, "validate() ");

        // Reset errors.
        mEmailEditText.setError(null);
        mPasswordEditText.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();
        String confirmPassword = mConfirmPasswordEditText.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordEditText.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordEditText;
            cancel = true;
        }

        if (!isPasswordValid(password, confirmPassword)) {
            mConfirmPasswordEditText.setError(getString(R.string.register_activity_confirm_password_error));
            focusView = mPasswordEditText;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailEditText.setError(getString(R.string.error_field_required));
            focusView = mEmailEditText;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailEditText.setError(getString(R.string.error_invalid_email));
            focusView = mEmailEditText;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
        }
        return cancel;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isPasswordValid(String password, String confirmPassword) {
        return confirmPassword.equals(password);
    }

    private JsonObject mapToJson(){

        Log.d(TAG, "mapToJson()");;

        JsonObject  json = new JsonObject ();

        json.addProperty("username", mEmailEditText.getText().toString());
        json.addProperty("password", mPasswordEditText.getText().toString());

        Log.d(TAG, "mapToJson() -> return == " + json);;

        return json;
    }

}
