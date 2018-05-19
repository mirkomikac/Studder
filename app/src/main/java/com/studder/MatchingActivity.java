package com.studder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.studder.model.Profile;
import com.studder.model.StudderCard;

import java.util.ArrayList;

public class MatchingActivity extends AppCompatActivity {

    private SwipePlaceHolderView swipePlaceHolderView;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matching);

        swipePlaceHolderView = (SwipePlaceHolderView) findViewById(R.id.swipeView);
        context = getApplicationContext();

        swipePlaceHolderView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.swipe_accept)
                        .setSwipeOutMsgLayoutId(R.layout.swipe_reject));

        //for testing purposes
        ArrayList<Profile> profiles = new ArrayList<Profile>();
        for(int i = 0; i < 10; i++){
            profiles.add(new Profile("pera"+i, "https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", 10*i, "Lalal"));
        }
        //setting next person....
        for(Profile profile : profiles){
            swipePlaceHolderView.addView(new StudderCard(context, profile, swipePlaceHolderView));
        }

        findViewById(R.id.rejectBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipePlaceHolderView.doSwipe(false);
            }
        });

        findViewById(R.id.acceptBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swipePlaceHolderView.doSwipe(true);
            }
        });

    }
}
