package com.studder.holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.studder.ChatActivity;
import com.studder.NavigationActivity;
import com.studder.ProfileActivity;
import com.studder.R;
import com.studder.database.schema.UserMatchTable;
import com.studder.database.schema.UserTable;
import com.studder.fragments.UserGaleryFragment;
import com.studder.model.User;

public class InboxRowViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public static final String TAG = "InboxRowViewHolder";

    private User mUser;

    private ImageView mUserImageView;
    private TextView mUserNameTextView;
    private TextView mUserLastMessageTextView;
    private TextClock mUserLastMessageTimeTextClock;
    private ImageView mUserSeenImageView;

    private Context mContext;

    private ImageView mOnlineStatus;

    public InboxRowViewHolder(final View itemView, final Context context) {
        super(itemView);

        Log.d(TAG, "InboxRowViewHolder(...) -> Binding class fields to views");

        mUserImageView = itemView.findViewById(R.id.image_view_chat_box_row_user_image);
        mUserNameTextView = itemView.findViewById(R.id.text_view_chat_box_row_user_name);
        mUserLastMessageTextView = itemView.findViewById(R.id.text_view_chat_box_row_last_message);
        mUserLastMessageTimeTextClock = itemView.findViewById(R.id.text_clock_chat_box_row_time);
        mUserSeenImageView = itemView.findViewById(R.id.image_view_chat_box_row_seen);

        mOnlineStatus = itemView.findViewById(R.id.text_view_chat_box_row_online_status);

        Log.d(TAG, "InboxRowViewHolder(...) -> Fileds Binded");

        Log.d(TAG, "InboxRowViewHolder(...) -> itemView.onClickListener - add");
        itemView.setOnClickListener(this);
        Log.d(TAG, "InboxRowViewHolder(...) -> itemView.setOnClickListener - added");

        mContext = context;

        mUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent profileActivity = new Intent(mContext, ProfileActivity.class);
                profileActivity.putExtra("userId", mUser.getId());
                profileActivity.putExtra("userUsername", mUser.getUsername());
                profileActivity.putExtra("userName", mUser.getName());
                profileActivity.putExtra("userSurname", mUser.getSurname());
                profileActivity.putExtra("userDescription", mUser.getDescription());
                profileActivity.putExtra("userBirthday", mUser.getBirthday().getTime());
                profileActivity.putExtra("userIsPrivate", mUser.getIsPrivate());
                profileActivity.putExtra("userLastOnline", mUser.getLastOnline().getTime());
                profileActivity.putExtra("userOnlineStatus", mUser.getOnlineStatus());
                mContext.startActivity(profileActivity);
            }
        });

        Log.d(TAG, "InboxRowViewHolder initialized");
    }

    @Override
    public void onClick(View v) {
        Intent chatActivity = new Intent(mContext, ChatActivity.class);

        Log.d(TAG, "onClick(View) -> putting extra -> " + UserTable.Cols._ID + " " + mUser.getId());
        chatActivity.putExtra(UserTable.Cols._ID, mUser.getId());
        chatActivity.putExtra(UserMatchTable.Cols._ID, mUser.getmUserMatch().getId());

        Log.d(TAG, "onClick(View) -> start ChatActivity");
        mContext.startActivity(chatActivity);
    }

    public void bind(User user){
        Log.d(TAG, "bind(User) -> " + user.toString());
        mUserNameTextView.setText(user.getName() + " " + user.getSurname());
        if (user.getmUserMatch().getLastMessage() != null) {
            mUserLastMessageTextView.setText(user.getmUserMatch().getLastMessage());
        } else {
            mUserLastMessageTextView.setText("Say HI!");
        }
        if(user.getmUserMatch().getLastMessageDate() != null){
            mUserLastMessageTimeTextClock.setText(user.getmUserMatch().getLastMessageDate().toString());
        } else {
            mUserLastMessageTimeTextClock.setText("");
        }

        if(user.getmUserMatch().getLastMessageSeen() != null){
            if(!user.getmUserMatch().getLastMessageSeen()){
                mUserLastMessageTextView.setTypeface(null, Typeface.BOLD | Typeface.ITALIC);

                byte[] bitmapBytes = Base64.decode(user.getProfileImageEncoded(), Base64.DEFAULT);
                Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                bmp = bmp.createScaledBitmap(bmp, 50, 50, false);
                mUserSeenImageView.setImageBitmap(bmp);
            }
        } else {
            mUserLastMessageTextView.setTypeface(null, Typeface.BOLD | Typeface.ITALIC);
        }


        if(user.getProfileImageEncoded() != null) {
            byte[] bitmapBytes = Base64.decode(user.getProfileImageEncoded(), Base64.DEFAULT);
            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
            bmp = bmp.createScaledBitmap(bmp, 350, 350, false);
            mUserImageView.setImageBitmap(bmp);
        }

        if(user.getOnlineStatus()){
            mOnlineStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.active_dot, null));
        } else {
            mOnlineStatus.setImageDrawable(mContext.getResources().getDrawable(R.drawable.nonactive_dot, null));
        }

        mUser = user;
    }

}
