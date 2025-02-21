package com.pccoe.evcharging.review;

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
import com.pccoe.evcharging.adapter.RatingsAdapter;
import com.pccoe.evcharging.databinding.ActivityGetAllReveiwsBinding;
import com.pccoe.evcharging.models.Rating;

import java.util.ArrayList;
import java.util.List;

public class GetAllReveiwsActivity extends AppCompatActivity {

    private RatingsAdapter dishAdapter;
    private LinearLayoutManager layoutManager;
    private List<Rating> ratings;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityGetAllReveiwsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetAllReveiwsBinding.inflate(getLayoutInflater());

        init();

        getAllReviews();

        setContentView(binding.getRoot());
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        ratings = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvData.setLayoutManager(layoutManager);
    }

    private void getAllReviews() {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("Rating")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        if (snaps == null || snaps.isEmpty()) {
                            showMessage("No reviews found.");
                            return;
                        }
                        ratings.addAll(snaps.toObjects(Rating.class));
                        dishAdapter = new RatingsAdapter(ratings, GetAllReveiwsActivity.this);
                        binding.rvData.setAdapter(dishAdapter);
                        showMessage(""); // Clear any previous messages
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("Error fetching reviews: " + e.getMessage());
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
}