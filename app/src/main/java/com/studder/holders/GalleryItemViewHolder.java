package com.studder.holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.studder.ChatActivity;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.model.Media;
import com.studder.model.User;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    public static final String TAG = "GalleryItemViewHolder";

    private Media mMedia;

    private ImageView mImageView;

    private Context mContext;

    public GalleryItemViewHolder(View itemView, Context context) {
        super(itemView);

        Log.d(TAG, "GalleryItemViewHolder(...) -> Binding class fields to views");

        mImageView = itemView.findViewById(R.id.image_view_gallery_item);

        Log.d(TAG, "GalleryItemViewHolder(...) -> Fileds Binded");

        Log.d(TAG, "GalleryItemViewHolder(...) -> itemView.onLongClickListener - add");
        mImageView.setOnLongClickListener(this);
        Log.d(TAG, "GalleryItemViewHolder(...) -> itemView.onLongClickListener - added");

        mContext = context;
        Log.d(TAG, "GalleryItemViewHolder initialized");
    }

    public void bind(Media media){
        Log.d(TAG, "bind(User) -> " + media.toString());
        mImageView.setImageBitmap(media.getBitmap());
        mMedia = media;
    }

    @Override
    public void onClick(View v) {
        
    }

    @Override
    public boolean onLongClick(View v) {

        return false;
    }
}
