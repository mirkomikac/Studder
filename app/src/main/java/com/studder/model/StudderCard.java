package com.studder.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.mindorks.placeholderview.annotations.swipe.*;

import com.studder.R;
import com.studder.database.schema.UserTable;

//class that binds to studder_card_view layout
@Layout(R.layout.studder_card_view)
public class StudderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView nameAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    private static final String TAG = "Event";

    public StudderCard(Context context, Profile profile, SwipePlaceHolderView swipeView){
        this.mContext = context;
        this.mProfile = profile;
        this.mSwipeView = swipeView;
    }

    //method to be executed, when the view is ready to be used
    @Resolve
    private void onResolved(){
        Glide.with(mContext).load(mProfile.getProfileImgBitmap()).into(profileImageView);
        nameAgeTxt.setText(mProfile.getName() + ", " + mProfile.getAge());
        locationNameTxt.setText(mProfile.getLocation());
    }

    @SwipeOut
    private void onSwipedOut(){
        Log.d(TAG, "onSwipedOut");
       // mSwipeView.addView(this);
        JsonObject json = new JsonObject();
        json.addProperty("isLiked", false);
        Ion.with(mContext)
                .load("http://10.0.2.2:8080/swipes/" + mProfile.getId())
                .setJsonObjectBody(json)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if(result.getHeaders().code() == 200) {
                            Log.i("TAG", "OK");

                        } else{
                            Log.e("TAG", "NOK");
                        }
                    }
                });
    }

    @SwipeCancelState
    private void onSwipeCancelState(){
        Log.d(TAG, "onSwipeCancelState");
    }

    @SwipeIn
    private void onSwipeIn(){

        Log.d(TAG, "onSwipedIn");

        JsonObject json = new JsonObject();
        json.addProperty("isLiked", true);
        Ion.with(mContext)
                .load("http://10.0.2.2:8080/swipes/" + mProfile.getId())
                .setJsonObjectBody(json)
                .asJsonObject()
                .withResponse()
                .setCallback(new FutureCallback<Response<JsonObject>>() {
                    @Override
                    public void onCompleted(Exception e, Response<JsonObject> result) {
                        if(result.getHeaders().code() == 200) {
                            Log.i("TAG", "OK");

                        } else{
                            Log.e("TAG", "NOK");
                        }
                    }
                });
    }

    @SwipeInState
    private void onSwipeInState(){
        Log.d(TAG, "onSwipeInState");
    }

    @SwipeOutState
    private void onSwipeOutState(){
        Log.d(TAG, "onSwipeOutState");
    }

}
