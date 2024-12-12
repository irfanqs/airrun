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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;
    private TextView currentActivityDetails;
    private TextView activityDate;
    private TextView activityDetails;

    private static final int REQUEST_JOGGING_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Cek apakah pengguna sudah login
        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = preferences.getBoolean("isLoggedIn", true);

        if (!isLoggedIn) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        // Inisialisasi UI
        ImageView profileImage = findViewById(R.id.profile_image);
        TextView userName = findViewById(R.id.user_name);
        TextView userLevel = findViewById(R.id.user_level);
        Button buttonStartJogging = findViewById(R.id.button_start_jogging);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        // Tampilkan data pengguna
        loadUserData(profileImage, userName, userLevel);

        // Tampilkan aktivitas jogging terbaru
        currentActivityDetails = findViewById(R.id.current_activity_details);
        activityDate = findViewById(R.id.activity_date);
        activityDetails = findViewById(R.id.activity_details);

        loadRecentActivity();

        // Tombol untuk memulai jogging
        buttonStartJogging.setOnClickListener(v -> {
            Intent intent = new Intent(this, JoggingActivity.class);
            startActivityForResult(intent, REQUEST_JOGGING_ACTIVITY);
        });

        // Navigasi
        findViewById(R.id.nav_main).setOnClickListener(v -> {});

        findViewById(R.id.nav_achievement).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AchievementActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.nav_profile).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Memuat data pengguna dari tabel "users" di database.
     */
    private void loadUserData(ImageView profileImage, TextView userName, TextView userLevel) {
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
    }

    /**
     * Memuat aktivitas jogging terbaru dari tabel "jogging_activities".
     */
    private void loadRecentActivity() {
        Cursor cursor = database.query("jogging_activities", null, null, null, null, null, "date DESC", "1");
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String date = cursor.getString(cursor.getColumnIndex("date"));
            @SuppressLint("Range") String details = cursor.getString(cursor.getColumnIndex("details"));

            activityDate.setText(date);
            activityDetails.setText(details);
            currentActivityDetails.setText(details);
        } else {
            currentActivityDetails.setText("No recent activity");
        }
        cursor.close();
    }

    /**
     * Menangani hasil dari JoggingActivity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_JOGGING_ACTIVITY && resultCode == RESULT_OK) {
            // Memuat ulang aktivitas terbaru setelah selesai jogging
            loadRecentActivity();
        }
    }
}
