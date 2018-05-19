package com.studder.model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.View;
import com.studder.R;

//class that binds to studder_card_view layout
@Layout(R.layout.studder_card_view)
public class StudderCard {

    @View(R.id.profileImageView)
    private ImageView profileImageView;

    @View(R.id.nameAgeTxt)
    private TextView namgeAgeTxt;

    @View(R.id.locationNameTxt)
    private TextView locationNameTxt;

    //private Profile mProfile;
    private Context mContext;
    private SwipePlaceHolderView mSwipeView;

    public StudderCard(){

    }

    //method to be executed, when the view is ready to be used
    @Resolve
    private void onResolved(){

    }

}
