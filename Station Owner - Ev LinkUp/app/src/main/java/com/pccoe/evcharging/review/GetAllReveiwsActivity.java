package com.pccoe.evcharging.review;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.R;
import com.pccoe.evcharging.adapter.RatingsAdapter;
import com.pccoe.evcharging.databinding.ActivityGetAllReveiwsBinding;
import com.pccoe.evcharging.models.Rating;

import java.util.ArrayList;
import java.util.List;

public class GetAllReveiwsActivity extends AppCompatActivity {

//    RecyclerView recyclerView;
    //List<DataDishes> dataholder;
    private RatingsAdapter dishAdapter;
    LinearLayoutManager layoutManager;
    List<Rating> ratings;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private ActivityGetAllReveiwsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGetAllReveiwsBinding.inflate(getLayoutInflater());

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        ratings = new ArrayList<>();

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        binding.rvData.setLayoutManager(layoutManager);

        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("Rating")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        ratings.addAll(snaps.toObjects(Rating.class));

                        dishAdapter = new RatingsAdapter(ratings, GetAllReveiwsActivity.this);
                        binding.rvData.setAdapter(dishAdapter);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(GetAllReveiwsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        setContentView(binding.getRoot());
    }


}