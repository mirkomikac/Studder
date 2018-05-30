package com.studder.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.studder.R;

public class ChatBoxRowViewHolder extends RecyclerView.ViewHolder {


    private ImageView mUserImageView;
    private TextView mUserNameTextView;
    private TextView mUserLastMessageTextView;
    private TextClock mUserLastMessageTimeTextClock;
    private ImageView mUserSeenImageView;

    public ChatBoxRowViewHolder(View itemView) {
        super(itemView);
        mUserImageView = itemView.findViewById(R.id.image_view_chat_box_row_user_image);
        mUserNameTextView = itemView.findViewById(R.id.text_view_chat_box_row_user_name);
        mUserLastMessageTextView = itemView.findViewById(R.id.text_view_chat_box_row_last_message);
        mUserLastMessageTimeTextClock = itemView.findViewById(R.id.text_clock_chat_box_row_time);
        mUserSeenImageView = itemView.findViewById(R.id.image_view_chat_box_row_seen);
    }
}
