package com.example.airrun;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JoggingActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private DatabaseHelper dbHelper;
    private Button finishJoggingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        finishJoggingButton = findViewById(R.id.finish_jogging_button);
        finishJoggingButton.setOnClickListener(v -> finishJogging());
    }

    private void finishJogging() {
        String distance = "10.9 km";
        String duration = "01:09:44";
        String calories = "539 kcal";
        String date = "November 26, 2024";
        String details = distance + " | " + duration + " | " + calories;

        // Menyimpan data ke SQLite
        ContentValues values = new ContentValues();
        values.put("date", date);
        values.put("details", details);
        database.insert("jogging_activities", null, values);

        // Menampilkan Toast dan kembali ke MainActivity
        Toast.makeText(this, "Jogging activity saved", Toast.LENGTH_SHORT).show();
        finish(); // Kembali ke MainActivity
    }
}
