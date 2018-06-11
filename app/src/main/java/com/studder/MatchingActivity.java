package com.studder;

import android.content.Context;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.studder.model.Profile;
import com.studder.model.StudderCard;
import com.studder.model.User;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MatchingActivity extends AppCompatActivity {

    private SwipePlaceHolderView swipePlaceHolderView;
    private Context context;

    private ArrayList<Profile> profiles = new ArrayList<Profile>();

    private static String TAG = "MatchingActivity";

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
        String ipConfig = getResources().getString(R.string.ipconfig);
        Ion.with(context)
                .load("http://"+ipConfig+"/users/getForSwipping")
                .as(new TypeToken<List<User>>() {})
                .withResponse()
                .setCallback(new FutureCallback<Response<List<User>>>() {
                    @Override
                    public void onCompleted(Exception e, Response<List<User>> result) {
                        if(result.getHeaders().code() == 200){
                            Log.i(TAG, "success");
                            ArrayList<User> users = (ArrayList<User>) result.getResult();
                            //profile image
                            for(User u : users){
                                Profile profile = new Profile(u.getId(), u.getName(), "https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", 20, u.getCity());
                                profiles.add(profile);
                            }
                            //setting next person....
                            for(Profile profile : profiles){
                                swipePlaceHolderView.addView(new StudderCard(context, profile, swipePlaceHolderView));
                            }
                        } else {
                            Log.e(TAG, "response != 200");
                        }

                    }
                });

        //for testing purposes
        /*ArrayList<Profile> profiles = new ArrayList<Profile>();
        for(int i = 0; i < 10; i++){
            profiles.add(new Profile("pera"+i, "https://pbs.twimg.com/profile_images/572905100960485376/GK09QnNG.jpeg", 10*i, "Lalal"));
        }*/
        //setting next person....
        /*for(Profile profile : profiles){
            swipePlaceHolderView.addView(new StudderCard(context, profile, swipePlaceHolderView));
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
