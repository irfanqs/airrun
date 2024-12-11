package com.example.airrun;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

public class JoggingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jogging);

        // Initialize the map fragment
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_fragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a sample route for jogging
        List<LatLng> joggingRoute = getSampleRoute();

        // Add markers at the start and end points
        if (!joggingRoute.isEmpty()) {
            mMap.addMarker(new MarkerOptions().position(joggingRoute.get(0)).title("Start"));
            mMap.addMarker(new MarkerOptions().position(joggingRoute.get(joggingRoute.size() - 1)).title("End"));

            // Draw the polyline
            PolylineOptions polylineOptions = new PolylineOptions()
                    .addAll(joggingRoute)
                    .width(8f)
                    .color(getResources().getColor(R.color.blue)); // Set the color of the route
            Polyline polyline = mMap.addPolyline(polylineOptions);

            // Move the camera to the start point
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(joggingRoute.get(0), 15f));
        }
    }

    private List<LatLng> getSampleRoute() {
        // This is a sample route; replace with actual GPS data
        List<LatLng> route = new ArrayList<>();
        route.add(new LatLng(55.8189, 37.6118)); // Start point
        route.add(new LatLng(55.8195, 37.6145));
        route.add(new LatLng(55.8201, 37.6170));
        route.add(new LatLng(55.8210, 37.6200)); // Midpoint
        route.add(new LatLng(55.8220, 37.6225));
        route.add(new LatLng(55.8230, 37.6240)); // End point
        return route;
    }
}
