package com.pccoe.evcharging.history;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.pccoe.evcharging.databinding.ActivityAddHistoryBinding;
import com.pccoe.evcharging.models.History;

import java.time.LocalDateTime;
import java.util.UUID;

public class AddHistoryActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private String history_id, history_time;
    private int history_energy, history_price;
    private GeoPoint history_location;
    private ActivityAddHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddHistoryBinding.inflate(getLayoutInflater());

        init();

        setEventLis();

        setContentView(binding.getRoot());
    }

    private void addHistory(){

        LocalDateTime now = LocalDateTime.now();

        String dateTime = now.toString();
        
        history_id = UUID.randomUUID().toString();
        history_energy = 30;
//        history_price = owner.price*30;
        history_time = dateTime;
//        history_location = owner.location;

        firebaseFirestore
                .collection("User")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("History")
                .document(history_id)
                .set(new History(history_id, history_time, history_energy, history_price, history_location))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
//                        Toast.makeText(AddHistoryActivity.this, "History Added", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init(){

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void setEventLis(){

    }
}