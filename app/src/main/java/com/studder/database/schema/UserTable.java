package com.studder.database.schema;

import android.provider.BaseColumns;

public final class UserTable {

    private UserTable() {}

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
    }
}
