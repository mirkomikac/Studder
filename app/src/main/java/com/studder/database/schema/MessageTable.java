package com.studder.database.schema;

import android.provider.BaseColumns;

import com.studder.model.Message;

public final class MessageTable {

    private MessageTable(){}

    public static final String NAME = "message";

    public static final class Cols implements BaseColumns {
        public static final String TEXT = "text";
        public static final String MESSAGE_STATUS = "message_status";
        public static final String USER_MATCH_ID = "user_match_id";
        public static final String SENDER_ID = "sender_id";
    }
}
