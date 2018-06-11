package com.studder.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudderSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String TAG = "StudderSQLiteOpenHelper";

    private static final int VERSION = 1;
    public static final String DATABASE_NAME = "studder.db";

    private static final String SQL_CREATE_MESSAGE =
            "CREATE TABLE " + MessageTable.NAME + " (" +
                    MessageTable.Cols._ID + " INTEGER PRIMARY KEY," +
                    MessageTable.Cols.TEXT + " TEXT NOT NULL," +
                    MessageTable.Cols.MESSAGE_STATUS + " TEXT," +
                    MessageTable.Cols.USER_MATCH_ID + " INTEGER, " +
                    MessageTable.Cols.SENDER_ID + " INTEGER, " +
                    "FOREIGN KEY("+MessageTable.Cols.USER_MATCH_ID+") REFERENCES "+UserMatchTable.NAME+"("+UserMatchTable.Cols._ID+")," +
                    "FOREIGN KEY("+MessageTable.Cols.SENDER_ID+") REFERENCES "+UserTable.NAME+"("+UserTable.Cols._ID+")" +
                    ")";

    private static final String SQL_DELETE_MESSAGE =
            "DROP TABLE IF EXISTS " + MessageTable.NAME;

    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + UserTable.NAME;

    private static final String SQL_DELETE_USER_MATCH =
            "DROP TABLE IF EXISTS " + UserMatchTable.NAME;

  
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
                    UserTable.Cols.SWIPE_THROW + " TEXT, " +
                    UserTable.Cols.CITY + " TEXT, " +
                    UserTable.Cols.FIRST_TIME_LOGIN + " INTEGER, " +
                    UserTable.Cols.FIRST_TIME + " INTEGER)";


    public StudderSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_USERTABLE);
        db.execSQL(SQL_CREATE_USERMATCHTABLE);
        db.execSQL(SQL_CREATE_MESSAGE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_MESSAGE);
        db.execSQL(SQL_DELETE_USER_MATCH);
        db.execSQL(SQL_DELETE_USER);
        onCreate(db);
    }
}
