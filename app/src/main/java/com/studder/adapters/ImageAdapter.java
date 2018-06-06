package com.studder.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.studder.model.Media;

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
        ImageView view = new ImageView(mContext);
        view.setImageBitmap(medias.get(position).getBitmap());
        return view;
    }
}
