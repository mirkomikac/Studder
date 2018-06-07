package com.studder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.studder.fragments.UserGaleryFragment;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        launchProfileFragment();
    }

    private void launchProfileFragment() {
        Fragment userGalleryFragment = UserGaleryFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putInt("userId", getIntent().getIntExtra("userId", 0));
        bundle.putString("userUsername", getIntent().getStringExtra("userUsername"));
        bundle.putString("userName", getIntent().getStringExtra("userName"));
        bundle.putString("userSurname", getIntent().getStringExtra("userSurname"));
        bundle.putString("userDescription", getIntent().getStringExtra("userDescription"));
        bundle.putLong("userBirthday", getIntent().getLongExtra("userBirthday", 0L));
        bundle.putBoolean("userIsPrivate", getIntent().getBooleanExtra("userIsPrivate", false));
        bundle.putString("userLastOnline", getIntent().getStringExtra("userLastOnline"));
        bundle.putBoolean("userOnlineStatus", getIntent().getBooleanExtra("userOnlineStatus", false));
        userGalleryFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_profile_linear_layout, userGalleryFragment);
        fragmentTransaction.commit();
    }

}
