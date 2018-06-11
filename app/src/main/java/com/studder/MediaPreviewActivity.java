package com.studder;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.studder.fragments.MediaPreviewFragment;

public class MediaPreviewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_preview);

        Toolbar toolbar = findViewById(R.id.media_preview_toolbar);
        toolbar.setTitle("Media");

        launchMediaPreviewFragment();
    }

    private void launchMediaPreviewFragment() {
        Fragment mediaPreviewFragment = MediaPreviewFragment.newInstance();
        Bundle bundle = new Bundle();
        mediaPreviewFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_media_preview_linear_layout, mediaPreviewFragment);
        fragmentTransaction.commit();
    }

}
