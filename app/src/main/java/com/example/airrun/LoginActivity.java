package com.example.airrun;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {

    private EditText inputName, inputAge;
    private ImageView imageProfile;
    private Bitmap profileImageBitmap;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        TextView textTitle = findViewById(R.id.text_title);

        String fullText = getString(R.string.login_title);

        SpannableString spannable = new SpannableString(fullText);

        int firstYour = fullText.indexOf("Your");
        int secondYour = fullText.indexOf("your", firstYour + 1);
        int thirdYour = fullText.lastIndexOf("your");

        ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.BLACK);
        spannable.setSpan(colorSpan, firstYour, firstYour + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(colorSpan, secondYour, secondYour + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannable.setSpan(colorSpan, thirdYour, thirdYour + 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textTitle.setText(spannable);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputName = findViewById(R.id.input_name);
        inputAge = findViewById(R.id.input_age);
        imageProfile = findViewById(R.id.image_profile);
        Button buttonTakePhoto = findViewById(R.id.button_take_photo);
        Button buttonLogin = findViewById(R.id.button_login);

        SQLiteOpenHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Ambil Foto
        buttonTakePhoto.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        });

        // Simpan Data dan pindah ke halaman utama
        buttonLogin.setOnClickListener(v -> {
            String name = inputName.getText().toString();
            String age = inputAge.getText().toString();

            if (name.isEmpty() || age.isEmpty() || profileImageBitmap == null) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            } else {
                saveToDatabase(name, Integer.parseInt(age), profileImageBitmap);
                Toast.makeText(this, "Login berhasil!", Toast.LENGTH_SHORT).show();

                // Pindah ke MainActivity
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            profileImageBitmap = (Bitmap) extras.get("data");
            imageProfile.setImageBitmap(profileImageBitmap);
        }
    }

    private void saveToDatabase(String name, int age, Bitmap profileImage) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        values.put("profile_image", imageBytes);

        database.insert("users", null, values);
    }
}
