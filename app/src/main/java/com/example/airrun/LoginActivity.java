package com.example.airrun;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;

public class LoginActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    private EditText inputName, inputAge;
    private ImageView imageProfile;
    private Bitmap profileImageBitmap;
    private SQLiteDatabase database;

    private final ActivityResultLauncher<Intent> takePictureLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null && data.getExtras() != null) {
                        profileImageBitmap = (Bitmap) data.getExtras().get("data");
                        imageProfile.setImageBitmap(profileImageBitmap);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputName = findViewById(R.id.input_name);
        inputAge = findViewById(R.id.input_age);
        imageProfile = findViewById(R.id.image_profile);
        Button buttonTakePhoto = findViewById(R.id.button_take_photo);
        Button buttonLogin = findViewById(R.id.button_login);

        // Database setup
        SQLiteOpenHelper dbHelper = new DatabaseHelper(this);
        database = dbHelper.getWritableDatabase();

        // Periksa izin kamera
        checkCameraPermission();

        // Tombol ambil foto
        buttonTakePhoto.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                // Buka kamera
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    takePictureLauncher.launch(takePictureIntent);
                }
            } else {
                // Jika izin belum diberikan
                Toast.makeText(this, "Izin kamera diperlukan untuk mengambil foto.", Toast.LENGTH_SHORT).show();
            }
        });

        // Tombol login
        buttonLogin.setOnClickListener(v -> {
            String name = inputName.getText().toString();
            String age = inputAge.getText().toString();

            if (name.isEmpty() || age.isEmpty()) {
                Toast.makeText(this, "Semua data harus diisi!", Toast.LENGTH_SHORT).show();
            } else if (profileImageBitmap == null) {
                Toast.makeText(this, "Harap ambil foto terlebih dahulu!", Toast.LENGTH_SHORT).show();
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

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            // Minta izin kamera
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Izin kamera diberikan!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Izin kamera ditolak. Anda tidak dapat mengambil foto.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveToDatabase(String name, int age, Bitmap profileImage) {
        if (profileImage == null) {
            throw new IllegalArgumentException("Profile image cannot be null");
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        profileImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] imageBytes = stream.toByteArray();

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("age", age);
        values.put("profile_image", imageBytes);

        long newRowId = database.insert("users", null, values);
        Log.d("LoginActivity", "Data inserted with ID: " + newRowId);
    }
}
