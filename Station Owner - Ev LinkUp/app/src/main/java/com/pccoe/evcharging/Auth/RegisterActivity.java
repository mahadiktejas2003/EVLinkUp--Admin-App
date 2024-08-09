package com.pccoe.evcharging.Auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.WriteBatch;
import com.google.type.LatLng;
import com.pccoe.evcharging.MainActivity;
import com.pccoe.evcharging.databinding.ActivityRegisterBinding;
import com.pccoe.evcharging.models.EVStation;
import com.pccoe.evcharging.models.Owner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;
    private String owner_pass;
    private String owner_id, owner_email, owner_name, ev_station_name;
    private GeoPoint owner_location;
    private double avg_rating;
    private int charging_points, price, charging_point_com_type_1, charging_point_com_type_2, charging_point_com_type_3, reviews;
    int mani_com_type_1, mani_com_type_2, mani_com_type_3;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());

        init();

        setEventLis();

        setContentView(binding.getRoot());
    }

    private void setEventLis() {
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getText();
                if (check() == 1) {
                    createNew();
                } else {
                    Toast.makeText(RegisterActivity.this, "All Fields are Mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createNew() {
        firebaseAuth
                .createUserWithEmailAndPassword(owner_email, owner_pass)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        firebaseFirestore
                                .collection("Owner")
                                .document(owner_email)
                                .set(new Owner(owner_id, owner_email, owner_name, ev_station_name, avg_rating, owner_location, charging_points, price, charging_point_com_type_1, charging_point_com_type_2, charging_point_com_type_3, reviews, 0))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(RegisterActivity.this, "Registered", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(RegisterActivity.this, "Data Entered", Toast.LENGTH_SHORT).show();
                                        addChargingPoints();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

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

        batch.commit().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
//                Toast.makeText(RegisterActivity.this, "Charging Points Created", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getLatLang(String owner_address) {
        //convert address to lat and lang
        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(owner_address, 5);
            if (address == null) {
                return;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();

            owner_location = new GeoPoint((double) (location.getLatitude() * 1E6),
                    (double) (location.getLongitude() * 1E6));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int check() {
        if (owner_email.equals("-1") || owner_pass.equals("-1") || owner_name.equals("-1") || ev_station_name.equals("-1"))
            return 0;
        if (charging_points == 0) return 0;
        if (owner_location == null) return 0;
        return 1;
    }

    private void getText() {
        owner_id = UUID.randomUUID().toString();

        owner_email = binding.etEmail.getText().toString().trim();
        owner_pass = binding.etPass.getText().toString().trim();
        owner_name = binding.etName.getText().toString().trim();
        ev_station_name = binding.etStationName.getText().toString().trim();

        charging_points = Integer.parseInt(binding.etChargingPoints.getText().toString().trim());
        price = Integer.parseInt(binding.etPricing.getText().toString().trim());

        if (binding.cbT1.isChecked()) {
            mani_com_type_1 = 1;
        }

        if (binding.cbT2.isChecked()) {
            mani_com_type_2 = 1;
        }

        if (binding.cbT3.isChecked()) {
            mani_com_type_3 = 1;
        }

        charging_point_com_type_1 = mani_com_type_1;
        charging_point_com_type_2 = mani_com_type_2;
        charging_point_com_type_3 = mani_com_type_3;

        owner_location = new GeoPoint(18.455799, 73.866631);

        if (owner_email.equals("")) owner_email = "-1";
        if (owner_pass.equals("")) owner_pass = "-1";
        if (owner_name.equals("")) owner_name = "-1";
        if (ev_station_name.equals("")) ev_station_name = "-1";
        if (charging_points == 0) charging_points = 0;


//        Log.d("TAG", owner_id + "\n" + owner_email + "\n" + owner_name + "\n" + ev_station_name + "\n" + avg_rating + "\n" + owner_location + "\n" + charging_points + "\n" + price + "\n" + charging_point_com_type_1 + "\n" + charging_point_com_type_2 + "\n" + charging_point_com_type_3 );
    }

    private void init() {
        owner_pass = "-1";
        owner_id = owner_email = owner_name = ev_station_name = "-1";
        avg_rating = 0;
        charging_points = 0;
        reviews = 0;
        price = charging_point_com_type_1 = charging_point_com_type_2 = charging_point_com_type_3 = 0;

        mani_com_type_1 = mani_com_type_2 = mani_com_type_3 = 0;

        owner_location = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}