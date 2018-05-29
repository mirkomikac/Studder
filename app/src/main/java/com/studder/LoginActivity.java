package com.studder;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.adapters.ViewPagerAdapter;
import com.studder.fragments.reusable.SliderFragment;
import com.studder.fragments.reusable.SwipeFragment;
import com.studder.sharedpreferconfiguration.SaveSharedPreferences;

import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginACtivity";

    private static final int REQUEST_READ_CONTACTS = 0;

    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private ViewPager viewPager;
    private int dotscount;
    private ImageView[] dots;
    private LinearLayout sliderDotsPanel;
    private Button signUpButton;
    private Button mEmailSignInButton;

    private SliderFragment mSliderFragment;
    private boolean loginSuccessful;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check if user is already logged in
        //if he is send him to main panel, but authentication needs to be established(again login request to server)?
        //user/pw sharedpref vs sqlite

        Log.d(TAG, "onCreate(Bundle)");

        if(SaveSharedPreferences.getLoggedIn(getApplicationContext())){
            Log.d(TAG, "onCreate(Bundle) : already logged in");
            Intent navigationActivity = new Intent(LoginActivity.this, NavigationActivity.class);
            startActivity(navigationActivity);
            finish();
        }

        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Log.d(TAG, "onCreate -> mPasswordView.setOnEditorActionListener");
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        sliderDotsPanel = (LinearLayout) findViewById(R.id.SliderDots);

        mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);

        Log.d(TAG, "onCreate -> mEmailSignInButton.setOnClickListener");
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        FragmentManager fm = getSupportFragmentManager();
        SwipePagerAdapter swipePagerAdapter = new SwipePagerAdapter(fm);

        viewPager.setAdapter(swipePagerAdapter);
        dotscount = swipePagerAdapter.getCount();

        Log.d(TAG, "onCreate -> adding slider fragment");

        mSliderFragment = (SliderFragment) fm.findFragmentById(R.id.slider_fragment_holder);
        if(mSliderFragment == null){
            mSliderFragment = SliderFragment.newInstance(dotscount);
            fm.beginTransaction().add(R.id.slider_fragment_holder, mSliderFragment).commit();
        }

        Log.d(TAG, "onCreate -> viewPager.addOnPageChangeListener");
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) { }

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onCreate -> trigger::viewPager.onPageSelected(int position) -> " + position);
                mSliderFragment.updateDot(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) { }
        });

        ImageView logoImageView;
        logoImageView = (ImageView) findViewById(R.id.logoImageView);
        logoImageView.setImageResource(R.drawable.logo);

        signUpButton = (Button) findViewById(R.id.email_sign_up_button);

        Log.d(TAG, "onCreate -> signUpButton.setOnClickListener");
        signUpButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                Intent registerActivity = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerActivity);
            }
        });
    }

    private void attemptLogin() {

        Log.d(TAG, "attemptLogin() ");

        if (mAuthTask != null) {
            Log.d(TAG, "attemptLogin : mAuthTask != null");
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    // TODO: 1. Populate with previously used emails
    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            Log.d(TAG, "UserLoginTask -> doInBackground(...)");

            // Tim6: Our Code Database
            JsonObject json = new JsonObject();
            json.addProperty("username", mEmail);
            json.addProperty("password", mPassword);

            Ion.with(getApplicationContext())
                    .load("http://10.0.2.2:8080/auth/login")
                    .setJsonObjectBody(json)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if (result.getHeaders().code() == 200) {

                                Log.d(TAG, "UserLoginTask -> doInBackground -> Ion Response == 200");

                                Toast.makeText(LoginActivity.this, R.string.login_register_activity_success, Toast.LENGTH_SHORT);

                                SaveSharedPreferences.setLoggedIn(getApplicationContext(), true);
                                // Tim6 -> If First Time
                                Intent personalizeActivity = new Intent(LoginActivity.this, PersonalizeActivity.class);
                                startActivity(personalizeActivity);

                                finish();
                            } else {
                                Log.d(TAG, "UserLoginTask -> doInBackground -> Ion Response == " + result.getHeaders().code());

                                showProgress(false);

                                Toast.makeText(LoginActivity.this, R.string.login_register_activity_fail, Toast.LENGTH_SHORT);

                                mPasswordView.setError(getString(R.string.error_incorrect_password));
                                mPasswordView.requestFocus();
                            }
                            mAuthTask = null;
                        }
                    });
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

    public class SwipePagerAdapter extends FragmentPagerAdapter {

        public SwipePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.

            Log.d(TAG, "SwipePagerAdapter -> getItem(int position) -> " + position);

            switch(position){
                case 0 : {
                    return SwipeFragment.newInstance(R.string.swipe_fragment_message_discover_people, R.drawable.login_swipe_fragment_default);
                }
                case 1 : {
                    return SwipeFragment.newInstance(R.string.swipe_fragment_message_swipe, R.drawable.login_swipe_fragment_default);
                }
                case 2 : {
                    return SwipeFragment.newInstance(R.string.swipe_fragment_message_swipe_match, R.drawable.login_swipe_fragment_default);
                }
                case 3 : {
                    return SwipeFragment.newInstance(R.string.swipe_fragment_message_swipe_message, R.drawable.login_swipe_fragment_default);
                }
                default: {
                    return SwipeFragment.newInstance(R.string.swipe_fragment_message_discover_people, R.drawable.login_swipe_fragment_default);
                }
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}

