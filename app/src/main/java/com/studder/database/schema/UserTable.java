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

import java.util.HashMap;

public final class UserTable extends ContentProvider{

    public static final String PROVIDER_NAME = "com.studder.Studder.UserProvider";

    public static final String URL = "content://" + PROVIDER_NAME + "/users";
    public static final Uri CONTENT_URI = Uri.parse(URL);

    private static HashMap<String, String> USERS_PROJECTION_MAP;

    public static final int USERS = 1;
    public static final int USER_ID = 2;

    public static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, "users", USERS);
        uriMatcher.addURI(PROVIDER_NAME, "users/#", USER_ID);
    }

    private SQLiteDatabase db;

    public static final String NAME = "user";

    public static final class Cols implements BaseColumns{
        public static final String USERNAME = "username";
        public static final String PASSWORD = "password";
        public static final String NAME = "name";
        public static final String SURNAME = "surname";
        public static final String DESCRIPTION = "description";
        public static final String BIRTHDAY = "birthday";
        public static final String ONLINE_STATUS = "online_status";
        public static final String LAST_ONLINE = "last_online";
        public static final String RADIUS = "radius";
        public static final String LATTITUDE = "lattitude";
        public static final String LONGITUDE = "longitude";
        public static final String PROFILE_CREATED = "profile_created";
        public static final String SHARE_LOCATION = "share_location";
        public static final String IS_PRIVATE = "is_private";
        public static final String IS_DEACTIVATED = "is_deactivated";
        public static final String USER_GENDER = "user_gender";
        public static final String SWIPE_THROW = "swipe_throw";
        public static final String CITY = "city";
        public static final String FIRST_TIME_LOGIN = "first_time_login";
        public static final String FIRST_TIME = "first_time";
    }

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
            case USERS:
                qb.setProjectionMap(USERS_PROJECTION_MAP);
                break;

            case USER_ID:
                qb.appendWhere( Cols._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
        }

        if (sortOrder == null || sortOrder == ""){
            /**
             * By default sort on user names
             */
            sortOrder = Cols.ONLINE_STATUS;
        }

        Cursor c = qb.query(db,	projection,	selection,
                selectionArgs,null, null, sortOrder);
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
           case USERS:
               return "vnd.android.cursor.dir/vnd.studder.users";
            case USER_ID:
                return "vnd.android.cursor.item/vnd.studder.users";
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
            case USERS: {
                count = db.delete(NAME, selection, selectionArgs);
                break;
            }
            case USER_ID: {
                String id = uri.getPathSegments().get(1);
                count = db.delete(NAME, Cols._ID + " = " + id +
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
            case USERS: {
                count = db.update(NAME, values, selection, selectionArgs);
                break;
            }
            case USER_ID: {
                count = db.update(NAME, values,
                        Cols._ID + " = " + uri.getPathSegments().get(1) +
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
