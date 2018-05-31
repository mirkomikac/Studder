package com.studder.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudderSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "StudderSQLiteOpenHelper";

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "studder.db";

    //check REFERENCES vs INTEGER REFERENCES
    //finished table
    private static final String SQL_CREATE_USERMATCHTABLE =
            "CREATE TABLE " + UserMatchTable.NAME + " (" +
                    UserMatchTable.Cols._ID + " INTEGER PRIMARY KEY, " +
                    UserMatchTable.Cols.MATCH_TIME + " TEXT, " +
                    UserMatchTable.Cols.PARTICIPANT1_ID + " TEXT, " +
                    UserMatchTable.Cols.PARTICIPANT2_ID + " TEXT, " +
                    "FOREIGN KEY (" + UserMatchTable.Cols.PARTICIPANT1_ID+") REFERENCES " + UserTable.NAME + "(" + UserTable.Cols._ID + "), " +
                    "FOREIGN KEY (" + UserMatchTable.Cols.PARTICIPANT2_ID+") REFERENCES " + UserTable.NAME + "(" + UserTable.Cols._ID + ")" +
                    ")";

    //add not null later..
    private static final String SQL_CREATE_USERTABLE =
            "CREATE TABLE " + UserTable.NAME + " (" +
                    UserTable.Cols._ID + " INTEGER PRIMARY KEY, " +
                    UserTable.Cols.USERNAME +  " TEXT, " +
                    UserTable.Cols.PASSWORD + " TEXT, " +
                    UserTable.Cols.NAME + " TEXT, " +
                    UserTable.Cols.SURNAME + " TEXT, " +
                    UserTable.Cols.DESCRIPTION + " TEXT, " +
                    UserTable.Cols.BIRTHDAY + " TEXT, " +
                    UserTable.Cols.ONLINE_STATUS + " INTEGER, " +
                    UserTable.Cols.LAST_ONLINE + " TEXT, " +
                    UserTable.Cols.RADIUS + " INTEGER, " +
                    UserTable.Cols.LATTITUDE + " REAL, " +
                    UserTable.Cols.LONGITUDE + " REAL, " +
                    UserTable.Cols.PROFILE_CREATED + " TEXT, " +
                    UserTable.Cols.SHARE_LOCATION + " INTEGER, " +
                    UserTable.Cols.IS_PRIVATE + " INTEGER, " +
                    UserTable.Cols.IS_DEACTIVATED + " INTEGER, " +
                    UserTable.Cols.USER_GENDER + " TEXT, " +
                    UserTable.Cols.SWIPE_THROW + " TEXT)";


    public StudderSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERMATCHTABLE);
        db.execSQL(SQL_CREATE_USERTABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
