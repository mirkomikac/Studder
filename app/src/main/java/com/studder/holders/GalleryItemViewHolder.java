package com.studder.holders;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.ChatActivity;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.fragments.ProfileFragment;
import com.studder.model.Media;
import com.studder.model.User;

public class GalleryItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{

    public static final String TAG = "GalleryItemViewHolder";

    private Media mMedia;

    private ImageView mImageView;

    private Context mContext;
    private ProfileFragment mProfileFragment;

    public GalleryItemViewHolder(View itemView, Context context, ProfileFragment profileFragment) {
        super(itemView);

        Log.d(TAG, "GalleryItemViewHolder(...) -> Binding class fields to views");

        mImageView = itemView.findViewById(R.id.image_view_gallery_item);

        Log.d(TAG, "GalleryItemViewHolder(...) -> Fileds Binded");

        Log.d(TAG, "GalleryItemViewHolder(...) -> itemView.onLongClickListener - add");
        mImageView.setOnLongClickListener(this);
        Log.d(TAG, "GalleryItemViewHolder(...) -> itemView.onLongClickListener - added");

        mContext = context;
        mProfileFragment = profileFragment;
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

        Log.d(TAG, "GalleryItemViewHolder(...) -> long click " + mMedia.getId());

        AlertDialog.Builder builderSingle = new AlertDialog.Builder(mContext);

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(mContext, android.R.layout.select_dialog_item);
        String[] options = mContext.getResources().getStringArray(R.array.alert_dialog_fragment_profile_image_options);
        arrayAdapter.addAll(options);
        final String ipConfig = mContext.getResources().getString(R.string.ipconfig);
        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch(which){
                    case 0: {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                        builderInner.setMessage(strName);


                        builderInner.setTitle(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_title));
                        builderInner
                                .setPositiveButton(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        Log.d(TAG, "GalleryItemViewHolder(...) -> long click " + mMedia.getId());
                                        Ion.with(mContext).load("DELETE", "http://"+ipConfig+"/media/delete/" + mMedia.getId())
                                                .setJsonPojoBody(mMedia)
                                                .asJsonObject()
                                                .withResponse()
                                                .setCallback(new FutureCallback<Response<JsonObject>>() {
                                                    @Override
                                                    public void onCompleted(Exception e, Response<JsonObject> result) {
                                                        if(result.getHeaders().code() == 200){
                                                            Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_success), Toast.LENGTH_LONG).show();
                                                            mProfileFragment.refreshContent(null);
                                                        } else {
                                                            Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_fail), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                        break;
                    }
                    case 1: {
                        String strName = arrayAdapter.getItem(which);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(mContext);
                        builderInner.setMessage(strName);

                        builderInner.setTitle(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_title));
                        builderInner
                                .setPositiveButton(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_yes), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int which) {
                                        Ion.with(mContext).load("GET", "http://"+ipConfig+"/media/setProfileImage/" + mMedia.getId())
                                                .asJsonObject()
                                                .withResponse()
                                                .setCallback(new FutureCallback<Response<JsonObject>>() {
                                                    @Override
                                                    public void onCompleted(Exception e, Response<JsonObject> result) {
                                                        if(result.getHeaders().code() == 200){
                                                            Toast.makeText(mContext.getApplicationContext(), mContext.getResources().getString(R.string.alert_dialog_fragment_profile_set_as_profile_success), Toast.LENGTH_LONG).show();
                                                            byte[] bitmapBytes = Base64.decode(mMedia.getEncodedImage(), Base64.DEFAULT);
                                                            Bitmap bmp = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                                                            bmp = bmp.createScaledBitmap(bmp, 350, 350, false);
                                                            mProfileFragment.refreshContent(bmp);

                                                        } else {
                                                            Toast.makeText(mContext, mContext.getResources().getString(R.string.alert_dialog_fragment_profile_set_as_profile_fail), Toast.LENGTH_LONG).show();
                                                        }
                                                    }
                                                });
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(mContext.getResources().getString(R.string.alert_dialog_fragment_profile_delete_options_cancel), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builderInner.show();
                        break;
                    }
                }
            }
        });
        builderSingle.show();

        return false;
    }
}
