package com.pccoe.evcharging.EvStation;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.adapter.EVStationAdapter;
import com.pccoe.evcharging.databinding.ActivityGetEvStationsBinding;
import com.pccoe.evcharging.models.EVStation;

import java.util.ArrayList;
import java.util.List;

public class GetEvStationsActivity extends AppCompatActivity {

    private EVStationAdapter dishAdapter;
    private LinearLayoutManager layoutManager;
    private List<EVStation> ratings;
    private ActivityGetEvStationsBinding binding;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
//    List<EVStation> evStations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetEvStationsBinding.inflate(getLayoutInflater());

        init();

        getAllStations();

        setContentView(binding.getRoot());
    }

    private void getAllStations() {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("EV_Station")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        if (snaps == null || snaps.isEmpty()) {
                            showMessage("No EV stations found.");
                            return;
                        }
                        ratings.addAll(snaps.toObjects(EVStation.class));

                        dishAdapter = new EVStationAdapter(ratings, GetEvStationsActivity.this);
                        binding.rvData.setAdapter(dishAdapter);
                        showMessage(""); // Clear any previous messages
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error fetching EV stations: " + e.getMessage());
                    }
                });
    }

    private void showMessage(String message) {
        if (message.isEmpty()) {
            binding.tvMessage.setVisibility(View.GONE);
        } else {
            binding.tvMessage.setText(message);
            binding.tvMessage.setVisibility(View.VISIBLE);
        }
    }
    private void init() {
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvData.setLayoutManager(layoutManager);
        ratings = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}