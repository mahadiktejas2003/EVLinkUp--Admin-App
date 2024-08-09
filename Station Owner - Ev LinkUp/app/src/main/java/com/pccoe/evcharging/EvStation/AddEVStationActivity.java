package com.pccoe.evcharging.EvStation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pccoe.evcharging.databinding.ActivityAddEvstationBinding;
import com.pccoe.evcharging.models.EVStation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AddEVStationActivity extends AppCompatActivity {

    ActivityAddEvstationBinding binding;
    private String evs_id;
    private int evs_available, evs_energy, type;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEvstationBinding.inflate(getLayoutInflater());

        init();

        setEventLis();

        setContentView(binding.getRoot());
    }

    private void init() {
        evs_id = "-1";
        evs_available = evs_energy = type = 0;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private int check() {
        if (evs_energy == 0 || type == 0) return 0;
        return 1;
    }

    private void setEventLis() {
        binding.btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getText();
                if (check() == 1) {
                    addEVStation();
                } else {
                    Toast.makeText(AddEVStationActivity.this, "All Fields are " +
                            "Mandatory", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.btnAvailable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                evs_available = 1 - evs_available;
            }
        });
    }

    private void addEVStation() {

        List<String> slot = new ArrayList<>(Collections.nCopies(48, ""));

        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("EV_Station")
                .document(evs_id)
                .set(new EVStation(evs_id, evs_available, evs_energy, type, slot))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(AddEVStationActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
                        setNull();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEVStationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void setNull() {
        binding.etEnergy.setText("");
    }

    private void getText() {
        evs_id = UUID.randomUUID().toString();
        evs_energy = Integer.parseInt(binding.etEnergy.getText().toString().trim());
//        evs_available = Integer.parseInt(binding.etAvailable.getText().toString().trim());
    }
}