package com.studder.database.schema;

import android.provider.BaseColumns;


public final class UserMatchTable {

    private UserMatchTable(){}

    public static final String NAME = "user_match";

    public static final class Cols implements BaseColumns{
        public static final String MATCH_TIME = "match_time";
        public static final String PARTICIPANT1_ID = "participant1_id";
        public static final String PARTICIPANT2_ID = "participant2_id";
    }
}
