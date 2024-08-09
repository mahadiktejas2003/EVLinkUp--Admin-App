package com.pccoe.evcharging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.pccoe.evcharging.EnergySold.EnergySoldChartActivity;
import com.pccoe.evcharging.EvStation.GetEvStationsActivity;
import com.pccoe.evcharging.EvStation.UpdateEvStationActivity;
import com.pccoe.evcharging.databinding.ActivityMainBinding;
import com.pccoe.evcharging.history.AddHistoryActivity;
import com.pccoe.evcharging.history.GetAllHistoryActivity;
import com.pccoe.evcharging.models.Notification;
import com.pccoe.evcharging.payment.ViewAllPaymentsActivity;
import com.pccoe.evcharging.review.GetAllReveiwsActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Notification notification;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private int check = 1;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        init();

        setEventLis();

//        setLocale("hi");
//        recreate();

        checkPre();

        setContentView(binding.getRoot());
    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);

        Context context = createConfigurationContext(configuration);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        // Restart the activity to apply changes
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }


    private void init() {
        notification = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void checkPre() {
        firebaseFirestore
                .collection("Notification")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (!doc.exists()) {
                            check = 0;
                        }

                        getData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void getData() {
        if (check == 1) { //already there
            firebaseFirestore
                    .collection("Notification")
                    .document(firebaseAuth.getCurrentUser().getEmail())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot doc) {
                            notification = doc.toObject(Notification.class);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else { //new
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            String tokens = task.getResult();

                            notification = new Notification();

                            notification.setNoti_email(firebaseAuth.getCurrentUser().getEmail());
                            notification.setNoti_token(tokens);
//                            Toast.makeText(this, "" + tokens, Toast.LENGTH_SHORT).show();

                            firebaseFirestore
                                    .collection("Notification")
                                    .document(notification.getNoti_email())
                                    .set(notification)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(MainActivity.this, "Notification Values Added", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            // Handle token retrieval error
                        }
                    });

        }
    }

//    private String generateToken() {
//        String token = "";
//
//
//        return token;
//    }

    private void setEventLis() {
//        binding.btnViewHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, GetAllHistoryActivity.class));
//            }
//        });

//        binding.btnAddHistory.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(MainActivity.this, AddHistoryActivity.class));
//            }
//        });

        binding.btnManageEV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GetEvStationsActivity.class));
            }
        });

        binding.btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
            }
        });

        binding.btnGetAllReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, GetAllReveiwsActivity.class));
            }
        });

        binding.btnPaymentHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ViewAllPaymentsActivity.class));
            }
        });

        binding.btnAnalytics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, EnergySoldChartActivity.class));
            }
        });
    }
}