package com.example.airrun;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView profileImage = findViewById(R.id.profile_image);
        TextView userName = findViewById(R.id.user_name);
        TextView userLevel = findViewById(R.id.user_level);

        DatabaseHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getReadableDatabase();

        Cursor cursor = database.query("users", null, null, null, null, null, null);
        if (cursor.moveToLast()) { // Ambil data user terakhir
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
