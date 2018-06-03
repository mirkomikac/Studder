package com.studder.holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.studder.ChatActivity;
import com.studder.NavigationActivity;
import com.studder.R;
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

    public InboxRowViewHolder(final View itemView, final Context context) {
        super(itemView);

        Log.d(TAG, "InboxRowViewHolder(...) -> Binding class fields to views");

        mUserImageView = itemView.findViewById(R.id.image_view_chat_box_row_user_image);
        mUserNameTextView = itemView.findViewById(R.id.text_view_chat_box_row_user_name);
        mUserLastMessageTextView = itemView.findViewById(R.id.text_view_chat_box_row_last_message);
        mUserLastMessageTimeTextClock = itemView.findViewById(R.id.text_clock_chat_box_row_time);
        mUserSeenImageView = itemView.findViewById(R.id.image_view_chat_box_row_seen);

        Log.d(TAG, "InboxRowViewHolder(...) -> Fileds Binded");

        Log.d(TAG, "InboxRowViewHolder(...) -> itemView.onClickListener - add");
        itemView.setOnClickListener(this);
        Log.d(TAG, "InboxRowViewHolder(...) -> itemView.setOnClickListener - added");

        mContext = context;

        mUserImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment userGalleryFragment = UserGaleryFragment.newInstance();

                FragmentManager fragmentManager = ((NavigationActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.image_view_chat_box_row_user_image, userGalleryFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        Log.d(TAG, "InboxRowViewHolder initialized");
    }

    @Override
    public void onClick(View v) {
        Intent chatActivity = new Intent(mContext, ChatActivity.class);

        Log.d(TAG, "onClick(View) -> putting extra -> " + UserTable.Cols._ID + " " + mUser.getId());
        chatActivity.putExtra(UserTable.Cols._ID, mUser.getId());

        Log.d(TAG, "onClick(View) -> start ChatActivity");
        mContext.startActivity(chatActivity);
    }

    public void bind(User user){
        Log.d(TAG, "bind(User) -> " + user.toString());
        mUserNameTextView.setText(user.getName() + " " + user.getSurname());
        mUserLastMessageTextView.setText(user.getmUserMatch().getLastMessage());
        mUserLastMessageTimeTextClock.setText(user.getmUserMatch().getLastMessageDate().toString());

        if(!user.getmUserMatch().getLastMessageSeen()){
            mUserLastMessageTextView.setTypeface(null, Typeface.BOLD | Typeface.ITALIC);
        }

        mUser = user;
    }

}
