package com.example.airrun;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private TextView currentActivityDetails;
    private TextView activityDate;
    private TextView activityDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", true);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        ImageView profileImage = findViewById(R.id.profile_image);
        TextView userName = findViewById(R.id.user_name);
        TextView userLevel = findViewById(R.id.user_level);
        Button buttonStartJogging = findViewById(R.id.button_start_jogging);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query("users", null, null, null, null, null, null);
        if (cursor.moveToLast()) {
            int nameIndex = cursor.getColumnIndex("name");
            int ageIndex = cursor.getColumnIndex("age");
            int imageIndex = cursor.getColumnIndex("profile_image");

            if (nameIndex != -1) {
                String name = cursor.getString(nameIndex);
                userName.setText("Hello, " + name);
            }

            if (ageIndex != -1) {
                int age = cursor.getInt(ageIndex);
                userLevel.setText(age > 25 ? "Intermediate" : "Beginner");
            }

            if (imageIndex != -1) {
                byte[] imageBytes = cursor.getBlob(imageIndex);
                if (imageBytes != null) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    profileImage.setImageBitmap(bitmap);
                }
            }
        } else {
            userName.setText("Hello, Guest");
            userLevel.setText("Beginner");
        }
        cursor.close();

        // Menampilkan aktivitas terbaru
        currentActivityDetails = findViewById(R.id.current_activity_details);
        activityDate = findViewById(R.id.activity_date);
        activityDetails = findViewById(R.id.activity_details);

        Cursor recentActivityCursor = database.query("jogging_activities", null, null, null, null, null, "date DESC", "1");
        if (recentActivityCursor.moveToFirst()) {
            @SuppressLint("Range") String date = recentActivityCursor.getString(recentActivityCursor.getColumnIndex("date"));
            @SuppressLint("Range") String details = recentActivityCursor.getString(recentActivityCursor.getColumnIndex("details"));

            activityDate.setText(date);
            activityDetails.setText(details);
            currentActivityDetails.setText(details); // Menampilkan aktivitas terbaru di bagian atas
        } else {
            currentActivityDetails.setText("No recent activity");
        }
        recentActivityCursor.close();

        // Menangani klik tombol Start Jogging
        buttonStartJogging.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoggingActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.nav_main).setOnClickListener(v -> {
        });

        findViewById(R.id.nav_achievement).setOnClickListener(v -> {
            Intent intent = new Intent(this, AchievementActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
        });
    }
}
