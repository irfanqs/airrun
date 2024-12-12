package com.example.airrun;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class JoggingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;
    private FusedLocationProviderClient locationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    private List<LatLng> routePoints = new ArrayList<>();
    private Polyline polyline;
    private float totalDistance = 0;

    private TextView distanceTextView, speedTextView, caloriesTextView, timeTextView;
    private Button finishJoggingButton;

    private long startTime;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        distanceTextView = findViewById(R.id.distance);
        speedTextView = findViewById(R.id.speed);
        caloriesTextView = findViewById(R.id.calories);
        timeTextView = findViewById(R.id.running_time);
        finishJoggingButton = findViewById(R.id.finish_jogging_button);

        startTime = System.currentTimeMillis();
        dbHelper = new DatabaseHelper(this);

        setupMap();
        setupLocationTracking();

        finishJoggingButton.setOnClickListener(v -> finishJogging());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private void setupLocationTracking() {
        locationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // Update setiap 5 detik
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;

                for (Location location : locationResult.getLocations()) {
                    updateRoute(location);
                }
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }
        locationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void updateRoute(Location location) {
        if (location == null) return;

        LatLng newPoint = new LatLng(location.getLatitude(), location.getLongitude());
        routePoints.add(newPoint);

        if (routePoints.size() > 1) {
            LatLng lastPoint = routePoints.get(routePoints.size() - 2);
            float[] result = new float[1];
            Location.distanceBetween(lastPoint.latitude, lastPoint.longitude, newPoint.latitude, newPoint.longitude, result);
            totalDistance += result[0] / 1000; // Konversi meter ke kilometer
        }

        if (googleMap != null) {
            if (polyline == null) {
                PolylineOptions polylineOptions = new PolylineOptions().addAll(routePoints).width(5).color(0xFF007BFF);
                polyline = googleMap.addPolyline(polylineOptions);
            } else {
                polyline.setPoints(routePoints);
            }
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newPoint, 16));
        }

        updateUI(location);
    }

    private void updateUI(Location location) {
        distanceTextView.setText(String.format("%.2f km", totalDistance));
        speedTextView.setText(String.format("%.2f km/hr", location.getSpeed() * 3.6)); // Konversi m/s ke km/h

        // Kalori: Perhitungan kasar (60 kalori per kilometer)
        caloriesTextView.setText(String.format("%.0f kcal", totalDistance * 60));

        long elapsedTime = System.currentTimeMillis() - startTime;
        long seconds = (elapsedTime / 1000) % 60;
        long minutes = (elapsedTime / (1000 * 60)) % 60;
        long hours = (elapsedTime / (1000 * 60 * 60)) % 24;
        timeTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));
    }

    private void finishJogging() {
        // Hentikan pelacakan lokasi
        locationProviderClient.removeLocationUpdates(locationCallback);

        // Hitung kecepatan rata-rata
        long elapsedTime = System.currentTimeMillis() - startTime; // Dalam ms
        float hours = elapsedTime / (1000f * 60f * 60f);
        float averageSpeed = totalDistance / hours; // km/h

        // Simpan data ke database
        ContentValues values = new ContentValues();
        values.put("date", System.currentTimeMillis()); // Gunakan timestamp
        values.put("details", String.format("Distance: %.2f km | Time: %02d:%02d:%02d | Calories: %.0f kcal | Avg Speed: %.2f km/h",
                totalDistance, elapsedTime / (1000 * 60 * 60), (elapsedTime / (1000 * 60)) % 60, (elapsedTime / 1000) % 60,
                totalDistance * 60, averageSpeed));

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        database.insert("jogging_activities", null, values);

        // Tampilkan pesan dan kembali
        Toast.makeText(this, "Jogging activity saved", Toast.LENGTH_SHORT).show();
        finish(); // Tutup aktivitas
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        this.googleMap = map;
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationProviderClient.removeLocationUpdates(locationCallback);
        saveJoggingData(); // Simpan data saat aktivitas selesai
    }

    private void saveJoggingData() {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        long elapsedTime = System.currentTimeMillis() - startTime;

        // Data yang disimpan: waktu, jarak, dan kecepatan rata-rata
        ContentValues values = new ContentValues();
        values.put("date", System.currentTimeMillis()); // Waktu saat jogging selesai
        values.put("details", String.format("Distance: %.2f km, Avg Speed: %.2f km/hr, Time: %02d:%02d:%02d",
                totalDistance,
                (totalDistance / (elapsedTime / 1000.0)) * 3.6, // Kecepatan rata-rata
                (elapsedTime / (1000 * 60 * 60)) % 24, // Jam
                (elapsedTime / (1000 * 60)) % 60, // Menit
                (elapsedTime / 1000) % 60 // Detik
        ));

        database.insert("jogging_activities", null, values);
        database.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setupLocationTracking();
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }
}
