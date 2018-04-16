package com.studder.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.studder.R;

public class InboxListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] inboxItems;
    private final Integer[] imageId;

    public InboxListAdapter(Activity context, String[] inboxItems, Integer[] imageId) {
        super(context, R.layout.inbox_item, inboxItems);
        this.context = context;
        this.inboxItems = inboxItems;
        this.imageId = imageId;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.inbox_item, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        txtTitle.setText(inboxItems[position]);

        imageView.setImageResource(imageId[position]);
        return rowView;
    }

}
