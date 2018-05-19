package com.studder;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;

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

        //setting next person....
        /*for(Profile profile : Utils.loadProfiles(this.getApplicationContext())){
            mSwipeView.addView(new StudderCard(mContext, profile, swipePlaceHolderView));
        }*/

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
