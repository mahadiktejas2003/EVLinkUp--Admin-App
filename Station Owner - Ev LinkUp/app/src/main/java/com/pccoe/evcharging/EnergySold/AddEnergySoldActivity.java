package com.pccoe.evcharging.EnergySold;

import static java.lang.System.currentTimeMillis;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pccoe.evcharging.databinding.ActivityAddEnergySoldBinding;
import com.pccoe.evcharging.models.EnergySold;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AddEnergySoldActivity extends AppCompatActivity {

    Map<String, Object> data = new HashMap<>();
    private ActivityAddEnergySoldBinding binding;
    private int es_amount_earned, es_energy_sold, es_no_of_user_served;
    private String es_date;
    private String owner_email;
    private EnergySold pre_EnergySold;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddEnergySoldBinding.inflate(getLayoutInflater());

        init();

        setContentView(binding.getRoot());
    }

    private void getPreviousData() {
        firebaseFirestore
                .collection("Owner")
                .document(owner_email)
                .collection("EnergySold")
                .document(es_date)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (doc.exists()) {
                            pre_EnergySold = doc.toObject(EnergySold.class);
                        } else {
//                            Toast.makeText(AddEnergySoldActivity.this, "Doc do not Exists", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddEnergySoldActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addEnergySold() { //call when new payment is successed

        getPreviousData();

//        es_amount_earned = owner.pricing * 30; 
        es_energy_sold = 30;
        es_no_of_user_served = 1;

        if (pre_EnergySold != null) { //If there is data of current date : update
            es_no_of_user_served += 1;
            es_amount_earned += pre_EnergySold.getEs_amount_earned();
            es_energy_sold += pre_EnergySold.getEs_energy_sold();

            //es_amount_earned, es_energy_sold, es_no_of_user_served;

            data.put("es_amount_earned", es_amount_earned);
            data.put("es_energy_sold", es_energy_sold);
            data.put("es_no_of_user_served", es_no_of_user_served);

            firebaseFirestore
                    .collection("Owner")
                    .document(owner_email)
                    .collection("EnergySold")
                    .document(es_date)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            Toast.makeText(AddEnergySoldActivity.this, "Energy Added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEnergySoldActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();     
                        }
                    });


        }else{ //add new
            firebaseFirestore
                    .collection("Owner")
                    .document(owner_email)
                    .collection("EnergySold")
                    .document(es_date)
                    .set(new EnergySold(es_amount_earned, es_energy_sold, es_no_of_user_served, es_date))
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
//                            Toast.makeText(AddEnergySoldActivity.this, "ES Added", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddEnergySoldActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        }

    }

    private void init() {

        LocalDate today = LocalDate.now();

        es_date = today.toString();

        es_amount_earned = 0;
        es_energy_sold = 0;
        es_no_of_user_served = 0;

        pre_EnergySold = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}