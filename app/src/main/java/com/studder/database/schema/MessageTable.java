package com.studder.database.schema;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.studder.model.Message;

import java.util.HashMap;

public final class MessageTable extends ContentProvider {


    public static final String PROVIDER_NAME = "com.studder.Studder.MessageProvider";

    public static final String URL = "content://" + PROVIDER_NAME + "/messages";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static HashMap<String, String> MESSAGES_PROJECTION_MAP;

    public static final int MESSAGES= 1;
    public static final int MESSAGES_ID = 2;

    public static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "messages", MESSAGES);
        uriMatcher.addURI(PROVIDER_NAME, "messages/#", MESSAGES_ID);
    }

    private SQLiteDatabase db;

    public static final String NAME = "message";

    public static final class Cols implements BaseColumns {
        public static final String TEXT = "text";
        public static final String MESSAGE_STATUS = "message_status";
        public static final String USER_MATCH_ID = "user_match_id";
        public static final String SENDER_ID = "sender_id";
    }

    private MessageTable(){}

    @Override
    public boolean onCreate() {
        StudderSQLiteOpenHelper helper = new StudderSQLiteOpenHelper(getContext());
        db = helper.getWritableDatabase();

        return (db == null) ? false : true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NAME);

        switch (uriMatcher.match(uri)) {
            case MESSAGES:
                qb.setProjectionMap(MESSAGES_PROJECTION_MAP);
                break;

            case MESSAGES_ID:
                qb.appendWhere( MessageTable.Cols._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }


        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, null);
        /**
         * register to watch a content URI for changes
         */
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch(uriMatcher.match(uri)){
            case MESSAGES:
                return "vnd.android.cursor.dir/vnd.studder.messages";
            case MESSAGES_ID:
                return "vnd.android.cursor.item/vnd.studder.messages";
            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long rowId = db.insert(NAME, "", values );

        if(rowId > 0){
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }

        throw new SQLException("Failed to add a record into " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;

        switch (uriMatcher.match(uri)){
            case MESSAGES: {
                count = db.delete(NAME, selection, selectionArgs);
                break;
            }
            case MESSAGES_ID: {
                String id = uri.getPathSegments().get(1);
                count = db.delete(NAME, UserTable.Cols._ID + " = " + id +
                        (!TextUtils.isEmpty(selection) ?
                                "AND (" + selection + ')' : ""), selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Unkown URI " + uri);

        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int count = 0;
        switch (uriMatcher.match(uri)) {
            case MESSAGES: {
                count = db.update(NAME, values, selection, selectionArgs);
                break;
            }
            case MESSAGES_ID: {
                count = db.update(NAME, values,
                        UserTable.Cols._ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ?
                                        "AND (" + selection + ')' : ""), selectionArgs);
                break;
            }
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

}
