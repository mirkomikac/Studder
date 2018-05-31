package com.studder.database.schema;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StudderSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "studder.db";

    private static final String SQL_CREATE_MESSAGE =
            "CREATE TABLE " + MessageTable.NAME + " (" +
                    MessageTable.Cols._ID + " INTEGER PRIMARY KEY," +
                    MessageTable.Cols.TEXT + " TEXT NOT NULL," +
                    MessageTable.Cols.MESSAGE_STATUS + " TEXT," +
                    "FOREIGN KEY("+MessageTable.Cols.USER_MATCH_ID+") REFERENCES "+UserMatchTable.NAME+"("+UserMatchTable.Cols._ID+")," +
                    "FOREIGN KEY("+MessageTable.Cols.SENDER_ID+") REFERENCES "+UserTable.NAME+"("+UserTable.Cols._ID+")" +
                    ")";

    private static final String SQL_DELETE_MESSAGE =
            "DROP TABLE IF EXISTS " + MessageTable.NAME;

    private static final String SQL_DELETE_USER =
            "DROP TABLE IF EXISTS " + UserTable.NAME;

    private static final String SQL_DELETE_USER_MATCH =
            "DROP TABLE IF EXISTS " + UserMatchTable.NAME;

    public StudderSQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
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
