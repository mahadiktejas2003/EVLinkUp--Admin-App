package com.pccoe.evcharging.EvStation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.adapter.EVStationAdapter;
import com.pccoe.evcharging.adapter.RatingsAdapter;
import com.pccoe.evcharging.databinding.ActivityGetEvStationsBinding;
import com.pccoe.evcharging.models.EVStation;
import com.pccoe.evcharging.models.Rating;
import com.pccoe.evcharging.review.GetAllReveiwsActivity;

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
                        if (snaps == null) return;
                        ratings.addAll(snaps.toObjects(EVStation.class));

                        dishAdapter = new EVStationAdapter(ratings, GetEvStationsActivity.this);
                        binding.rvData.setAdapter(dishAdapter);
//                        evStations.addAll(snaps.getDocuments());

//                        for(DocumentSnapshot documentChange: snaps.getDocuments()){
//
//                            EVStation station = documentChange.toObject(EVStation.class);
//                            Log.d("TAG", "onSuccess: " + station.getEvs_id());
//
//                        }
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GetEvStationsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }); 
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