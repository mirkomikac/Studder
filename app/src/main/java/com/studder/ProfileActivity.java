package com.studder;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.studder.fragments.UserGaleryFragment;

import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    Integer userId;
    String username;
    String name;
    String surname;
    String about;
    Date age;

    Boolean isPrivate;
    Date lastOnline;
    Boolean onlineStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_activity_toolbar);
        toolbar.setTitle(getIntent().getStringExtra("userUsername"));

        if(getIntent().getIntExtra("userId", -1) == -1) {
            SharedPreferences preferences = getSharedPreferences("CURRENT_PROFILE", MODE_PRIVATE);
            userId = preferences.getInt("userId", 0);
            username = preferences.getString("userUsername", "-1");
            name = preferences.getString("userName", "-1");
            surname = preferences.getString("userSurname", "-1");
            about = preferences.getString("userDescription", "-1");
            age = new Date(preferences.getLong("userBirthday", 0L));

            isPrivate = preferences.getBoolean("userIsPrivate", false);
            lastOnline = new Date(preferences.getLong("userLastOnline", 0L));
            onlineStatus = preferences.getBoolean("userOnlineStatus", false);
        } else {
            userId = getIntent().getIntExtra("userId", 0);
            username = getIntent().getStringExtra("userUsername");
            name = getIntent().getStringExtra("userName");
            surname = getIntent().getStringExtra("userSurname");
            about = getIntent().getStringExtra("userDescription");
            age = new Date(getIntent().getLongExtra("userBirthday", 0L));

            isPrivate = getIntent().getBooleanExtra("userIsPrivate", false);
            lastOnline = new Date(getIntent().getLongExtra("userLastOnline", 0L));
            onlineStatus = getIntent().getBooleanExtra("userOnlineStatus", false);
        }
        if(savedInstanceState != null){
            userId = savedInstanceState.getInt("userId", 0);
            username = savedInstanceState.getString("userUsername");
            name = savedInstanceState.getString("userName");
            surname = savedInstanceState.getString("userSurname");
            about = savedInstanceState.getString("userDescription");
            age = new Date(savedInstanceState.getLong("userBirthday", 0L));

            isPrivate = savedInstanceState.getBoolean("userIsPrivate", false);
            lastOnline = new Date(savedInstanceState.getLong("userLastOnline", 0L));
            onlineStatus = savedInstanceState.getBoolean("userOnlineStatus", false);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        launchProfileFragment();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("userId", userId);
        outState.putString("userUsername", username);

        outState.putString("userName", name);
        outState.putString("userSurname", surname);
        outState.putString("userDescription", about);
        outState.putLong("userBirthday", age.getTime());

        outState.putBoolean("userIsPrivate", isPrivate);
        outState.putLong("userLastOnline", lastOnline.getTime());
        outState.putBoolean("userOnlineStatus", onlineStatus);


        SharedPreferences.Editor editor = getSharedPreferences("CURRENT_PROFILE", MODE_PRIVATE).edit();
        editor.putInt("userId", userId);
        editor.putString("userUsername", username);
        editor.putString("userName", name);
        editor.putString("userSurname", surname);
        editor.putString("userDescription", about);
        editor.putLong("userBirthday", age.getTime());
        editor.putBoolean("userIsPrivate", isPrivate);
        editor.putLong("userLastOnline", lastOnline.getTime());
        editor.putBoolean("userOnlineStatus", onlineStatus);
        editor.apply();

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt("userId", userId);
        savedInstanceState.putString("userUsername", username);

        savedInstanceState.putString("userName", name);
        savedInstanceState.putString("userSurname", surname);
        savedInstanceState.putString("userDescription", about);
        savedInstanceState.putLong("userBirthday", age.getTime());

        savedInstanceState.putBoolean("userIsPrivate", isPrivate);
        savedInstanceState.putLong("userLastOnline", lastOnline.getTime());
        savedInstanceState.putBoolean("userOnlineStatus", onlineStatus);
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void launchProfileFragment() {
        Fragment userGalleryFragment = UserGaleryFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", userId);
        bundle.putString("userUsername", username);
        bundle.putString("userName", name);
        bundle.putString("userSurname", surname);
        bundle.putString("userDescription", about);
        bundle.putLong("userBirthday", age.getTime());
        bundle.putBoolean("userIsPrivate", isPrivate);
        bundle.putLong("userLastOnline", lastOnline.getTime());
        bundle.putBoolean("userOnlineStatus", onlineStatus);
        userGalleryFragment.setArguments(bundle);



        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_profile_linear_layout, userGalleryFragment);
        fragmentTransaction.commit();
    }

}
