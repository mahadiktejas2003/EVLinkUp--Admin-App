package com.pccoe.evcharging.Auth;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.pccoe.evcharging.MainActivity;
import com.pccoe.evcharging.databinding.ActivityRegisterBinding;
import com.pccoe.evcharging.models.Owner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String owner_pass, owner_id, owner_email, owner_name, ev_station_name;
    private GeoPoint owner_location;
    private double avg_rating;
    private int charging_points, price, charging_point_com_type_1, charging_point_com_type_2, charging_point_com_type_3, reviews;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setEventListeners();
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        owner_location = null;
        avg_rating = 0;
        charging_points = price = charging_point_com_type_1 = charging_point_com_type_2 = charging_point_com_type_3 = reviews = 0;
    }

    private void setEventListeners() {
        binding.btnFetchLocation.setOnClickListener(v -> fetchCurrentLocation());
        binding.btnRegister.setOnClickListener(v -> {
            if (validateInputs() && processLocation()) {
                createNew();
            }
        });
    }

    private void fetchCurrentLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        updateLocationUI(location.getLatitude(), location.getLongitude());
                    } else {
                        showLocationError("Couldn't get current location");
                    }
                })
                .addOnFailureListener(e -> showLocationError("Location request failed"));
    }

    private boolean processLocation() {
        String locationInput = binding.etAddress.getText().toString().trim();

        if (locationInput.isEmpty()) {
            showLocationError("Please enter location or fetch current location");
            return false;
        }

        try {
            // Check if input is coordinate pair
            if (locationInput.matches("^-?\\d+\\.?\\d*,\\s*-?\\d+\\.?\\d*$")) {
                String[] parts = locationInput.split(",");
                double lat = Double.parseDouble(parts[0].trim());
                double lng = Double.parseDouble(parts[1].trim());
                owner_location = new GeoPoint(lat, lng);
                return true;
            } else {
                // Geocode address string
                return geocodeAddress(locationInput);
            }
        } catch (NumberFormatException e) {
            showLocationError("Invalid coordinate format");
            return false;
        }
    }

    private boolean geocodeAddress(String address) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address location = addresses.get(0);
                updateLocationUI(location.getLatitude(), location.getLongitude());
                return true;
            }
            showLocationError("Location not found");
            return false;
        } catch (IOException e) {
            showLocationError("Geocoding service error");
            return false;
        }
    }

    private void updateLocationUI(double latitude, double longitude) {
        owner_location = new GeoPoint(latitude, longitude);
        binding.etAddress.setText(latitude + ", " + longitude);
        binding.tvLocationMessage.setText("Location verified");
        binding.tvLocationMessage.setVisibility(View.VISIBLE);
    }

    private void showLocationError(String message) {
        binding.tvLocationMessage.setText(message);
        binding.tvLocationMessage.setVisibility(View.VISIBLE);
        owner_location = null;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchCurrentLocation();
        } else {
            showLocationError("Location permission required");
        }
    }

    private boolean validateInputs() {
        owner_email = binding.etEmail.getText().toString().trim();
        owner_pass = binding.etPass.getText().toString().trim();
        owner_name = binding.etName.getText().toString().trim();
        ev_station_name = binding.etStationName.getText().toString().trim();

        if (owner_email.isEmpty() || owner_pass.isEmpty() || owner_name.isEmpty() || ev_station_name.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return false;
        }

        try {
            charging_points = Integer.parseInt(binding.etChargingPoints.getText().toString().trim());
            price = Integer.parseInt(binding.etPricing.getText().toString().trim());
            if (charging_points <= 0 || price <= 0) {
                Toast.makeText(this, "Charging points and price must be positive numbers", Toast.LENGTH_SHORT).show();
                return false;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            return false;
        }

        charging_point_com_type_1 = binding.cbT1.isChecked() ? 1 : 0;
        charging_point_com_type_2 = binding.cbT2.isChecked() ? 1 : 0;
        charging_point_com_type_3 = binding.cbT3.isChecked() ? 1 : 0;

        if ((charging_point_com_type_1 + charging_point_com_type_2 + charging_point_com_type_3) == 0) {
            Toast.makeText(this, "Select at least one connector type", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void createNew() {
        firebaseAuth.createUserWithEmailAndPassword(owner_email, owner_pass)
                .addOnSuccessListener(authResult -> {
                    Owner owner = new Owner(
                            UUID.randomUUID().toString(),
                            owner_email,
                            owner_name,
                            ev_station_name,
                            0.0,
                            owner_location,
                            charging_points,
                            price,
                            charging_point_com_type_1,
                            charging_point_com_type_2,
                            charging_point_com_type_3,
                            0,
                            0
                    );

                    firebaseFirestore.collection("Owner")
                            .document(owner_email)
                            .set(owner)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                finish();
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Firestore error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Authentication failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}