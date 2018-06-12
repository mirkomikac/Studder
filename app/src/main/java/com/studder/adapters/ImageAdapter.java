package com.studder.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.studder.MediaPreviewActivity;
import com.studder.ProfileActivity;
import com.studder.R;
import com.studder.model.Media;
import com.studder.utils.ClientUtils;

import java.util.List;

public class ImageAdapter extends BaseAdapter {

    private final Context mContext;
    private final List<Media> medias;

    public ImageAdapter(Context context, List<Media> medias) {
        this.mContext = context;
        this.medias = medias;
    }

    @Override
    public int getCount() {
        return medias.size();
    }

    @Override
    public Object getItem(int position) {
        return medias.get(position);
    }

    @Override
    public long getItemId(int position) {
        return medias.get(position).getId();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        final Media media = medias.get(position);

        Activity activity = (ProfileActivity) mContext;
        GridView gridView = activity.findViewById(R.id.user_profile_fragment_grid_view);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(gridView.getColumnWidth(), gridView.getColumnWidth()));

        imageView.setImageBitmap(media.getBitmap());

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent currentIntent = ((ProfileActivity) mContext).getIntent();

                String username = currentIntent.getStringExtra("userUsername");
                Intent mediaPreviewActivity = new Intent(mContext, MediaPreviewActivity.class);
                mediaPreviewActivity.putExtra("userUsername", username);
                mediaPreviewActivity.putExtra("userProfileImagePath", currentIntent.getStringExtra("userProfileImagePath"));
                mediaPreviewActivity.putExtra("mediaId", media.getId());
                mediaPreviewActivity.putExtra("mediaPath", ClientUtils.saveMediaToPhoneStorage(username, media.getName(), media.getEncodedImage()));
                mediaPreviewActivity.putExtra("mediaDateAdded", media.getTimeAdded().getTime());
                mediaPreviewActivity.putExtra("mediaDescription", media.getDescription());
                mContext.startActivity(mediaPreviewActivity);
            }
        });

        return imageView;
    }

}
