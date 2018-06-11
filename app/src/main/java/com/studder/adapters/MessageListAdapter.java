package com.studder.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.FirebaseOptions;
import com.studder.R;
import com.studder.database.schema.UserTable;
import com.studder.firestore.MessageFirestoreModel;
import com.studder.model.Message;
import com.studder.utils.ClientUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class MessageListAdapter extends FirestoreRecyclerAdapter<MessageFirestoreModel, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED_AFTER = 3;

    private Context mContext;
    private FirestoreRecyclerOptions mOptions;

    private List<MessageFirestoreModel> mMessagegs;

    public MessageListAdapter(Context context, FirestoreRecyclerOptions options) {
        super(options);
        mOptions = options;
        mContext = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull MessageFirestoreModel model) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(model);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(model);
            case VIEW_TYPE_MESSAGE_RECEIVED_AFTER:
                ((ReceivedMessageHolder) holder).bind(model);
        }
    }

    @Override
    public int getItemViewType(int position) {

        SharedPreferences preferences = mContext.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        final Integer loggedUserId = preferences.getInt(UserTable.Cols._ID, -1);
        final String loggedUserUsername = preferences.getString(UserTable.Cols.USERNAME, "Someone");
        MessageFirestoreModel message = (MessageFirestoreModel) getItem(position);

        if(position > 0 && getItem(position - 1).getParticipant1().equals(getItem(position).getParticipant1()) && !getItem(position).getSender().equals(loggedUserUsername)){
            return VIEW_TYPE_MESSAGE_RECEIVED_AFTER;
        }
        if (message.getSender().equals(loggedUserUsername)) {
            return VIEW_TYPE_MESSAGE_SENT;
        } else {
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED_AFTER){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            ImageView senderImage = (ImageView) view.findViewById(R.id.image_message_profile);
            TextView senderName = (TextView) view.findViewById(R.id.text_message_name);
            senderImage.setVisibility(View.INVISIBLE);
            senderName.setVisibility(View.GONE);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }


    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(MessageFirestoreModel message) {
            messageText.setText(message.getText());
            timeText.setText(ClientUtils.formatDateTime(message.getDate().getTime()));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, timeText, nameText;
        ImageView profileImage;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);
        }

        void bind(MessageFirestoreModel message) {
            messageText.setText(message.getText());

            timeText.setText(ClientUtils.formatDateTime(message.getDate().getTime()));
            nameText.setText(message.getSender());
        }
    }
}
