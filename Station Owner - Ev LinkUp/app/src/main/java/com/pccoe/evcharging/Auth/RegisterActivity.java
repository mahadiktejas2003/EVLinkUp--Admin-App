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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.WriteBatch;
import com.pccoe.evcharging.MainActivity;
import com.pccoe.evcharging.databinding.ActivityRegisterBinding;
import com.pccoe.evcharging.models.EVStation;
import com.pccoe.evcharging.models.Owner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        owner_pass = "-1";
        owner_id = owner_email = owner_name = ev_station_name = "-1";
        avg_rating = 0;
        charging_points = 0;
        reviews = 0;
        price = charging_point_com_type_1 = charging_point_com_type_2 = charging_point_com_type_3 = 0;
        owner_location = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setEventListeners() {
        binding.btnFetchLocation.setOnClickListener(v -> fetchCurrentLocation());
        binding.btnRegister.setOnClickListener(v -> {
            getText();
            if (check() == 1) {
                createNew();
            } else {
                Toast.makeText(RegisterActivity.this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();
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

    private void getText() {
        owner_id = UUID.randomUUID().toString();
        owner_email = binding.etEmail.getText().toString().trim();
        owner_pass = binding.etPass.getText().toString().trim();
        owner_name = binding.etName.getText().toString().trim();
        ev_station_name = binding.etStationName.getText().toString().trim();
        charging_points = Integer.parseInt(binding.etChargingPoints.getText().toString().trim());
        price = Integer.parseInt(binding.etPricing.getText().toString().trim());

        charging_point_com_type_1 = binding.cbT1.isChecked() ? 1 : 0;
        charging_point_com_type_2 = binding.cbT2.isChecked() ? 1 : 0;
        charging_point_com_type_3 = binding.cbT3.isChecked() ? 1 : 0;

        if (owner_email.equals("")) owner_email = "-1";
        if (owner_pass.equals("")) owner_pass = "-1";
        if (owner_name.equals("")) owner_name = "-1";
        if (ev_station_name.equals("")) ev_station_name = "-1";
        if (charging_points == 0) charging_points = 0;
    }

    private int check() {
        if (owner_email.equals("-1") || owner_pass.equals("-1") || owner_name.equals("-1") || ev_station_name.equals("-1"))
            return 0;
        if (charging_points == 0) return 0;
        if (owner_location == null) return 0;
        return 1;
    }

    private void createNew() {
        firebaseAuth.createUserWithEmailAndPassword(owner_email, owner_pass)
                .addOnSuccessListener(authResult -> {
                    Owner owner = new Owner(
                            owner_id, owner_email, owner_name, ev_station_name, avg_rating, owner_location,
                            charging_points, price, charging_point_com_type_1, charging_point_com_type_2, charging_point_com_type_3, reviews, 0
                    );

                    firebaseFirestore.collection("Owner")
                            .document(owner_email)
                            .set(owner)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
                                addChargingPoints();
                            })
                            .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void addChargingPoints() {
        WriteBatch batch = firebaseFirestore.batch();
        List<String> slot = new ArrayList<>(Collections.nCopies(48, ""));

        for (int i = 0; i < charging_points; i++) {
            String id = UUID.randomUUID().toString();
            DocumentReference temp = firebaseFirestore
                    .collection("Owner")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .collection("EV_Station")
                    .document(id);

            batch.set(temp, new EVStation(id, slot));
        }

        batch.commit().addOnSuccessListener(unused -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}