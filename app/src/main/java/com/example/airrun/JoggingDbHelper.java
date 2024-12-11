package com.example.airrun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class JoggingDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "jogging.db";
    private static final int DATABASE_VERSION = 1;

    public JoggingDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE jogging_data (" +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "start_time LONG," +
                "end_time LONG," +
                "distance DOUBLE," +
                "calories DOUBLE," +
                "date LONG);";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS jogging_data");
        onCreate(db);
    }
}
