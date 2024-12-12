package com.example.airrun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "airrun.db";
    private static final int DATABASE_VERSION = 2; // Ubah versi jika ada perubahan pada struktur DB

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Tabel untuk data pengguna
        db.execSQL("CREATE TABLE users (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "age INTEGER, " +
                "profile_image BLOB)");

        // Tabel untuk data aktivitas jogging
        db.execSQL("CREATE TABLE activities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "date TEXT, " +
                "distance REAL, " +
                "duration TEXT, " +
                "calories REAL, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");

        // Tabel untuk data jogging activities
        db.execSQL("CREATE TABLE jogging_activities (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, " +
                "date TEXT, " +
                "details TEXT, " +
                "FOREIGN KEY(user_id) REFERENCES users(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS activities");
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS jogging_activities"); // Drop tabel jogging_activities jika perlu
        onCreate(db);
    }
}
