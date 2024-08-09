package com.pccoe.evcharging.EvStation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.databinding.ActivityUpdateEvStationBinding;
import com.pccoe.evcharging.models.EVStation;

import java.util.HashMap;
import java.util.Map;

public class UpdateEvStationActivity extends AppCompatActivity {

    private ActivityUpdateEvStationBinding binding;
    private String evs_id;
    private int evs_available, evs_energy, previous_energy, type;

    int[] slot = new int[48];
    Map<String, Object> data = new HashMap<>(); //for update
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdateEvStationBinding.inflate(getLayoutInflater());

        evs_id = getIntent().getStringExtra("evs_id");

        init();

        setPreviousData();

        setEventLis();

        setContentView(binding.getRoot());
    }

    private void updateData() {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("EV_Station")
                .document(evs_id)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(UpdateEvStationActivity.this, "Updated", Toast.LENGTH_SHORT).show();
//                        setText();
//                        setPreviousData();
                        call();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateEvStationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void call() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://ecoviewproperties.in/PCCOE/sendUserNotification.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the response from PHP file
//                        Log.d("Response", response);
//                        Toast.makeText(UpdateEvStationActivity.this, "Notifications Send", Toast.LENGTH_SHORT).show();

//                        Toast.makeText(UpdateEvStationActivity.this, "Hello World", Toast.LENGTH_SHORT).show();

                        firebaseFirestore
                                .collection("Owner")
                                .document(firebaseAuth.getCurrentUser().getEmail())
                                .collection("Notify")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot snaps) {
                                        // Iterate through the documents in the collection
                                        for (QueryDocumentSnapshot doc : snaps) {
                                            // Get the document ID
                                            String docId = doc.getId();
                                            Toast.makeText(UpdateEvStationActivity.this, docId, Toast.LENGTH_SHORT).show();
                                            // Delete the document
                                            firebaseFirestore.collection("Owner")
                                                    .document(firebaseAuth.getCurrentUser().getEmail())
                                                    .collection("Notify")
                                                    .document(docId)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            // Document successfully deleted
                                                            Toast.makeText(UpdateEvStationActivity.this, "Deleted", Toast.LENGTH_SHORT).show();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            // Handle errors
                                                            Toast.makeText(UpdateEvStationActivity.this, "Error deleting document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UpdateEvStationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle errors
                Log.e("Error", error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

        startActivity(new Intent(UpdateEvStationActivity.this, GetEvStationsActivity.class));
        finish();

    }

    private void setEventLis() {
        binding.btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getData();

//                Toast.makeText(UpdateEvStationActivity.this, ""+evs_available, Toast.LENGTH_SHORT).show();
//                Toast.makeText(UpdateEvStationActivity.this, ""+evs_energy, Toast.LENGTH_SHORT).show();
//                Toast.makeText(UpdateEvStationActivity.this, ""+type, Toast.LENGTH_SHORT).show();

                if (previous_energy > evs_energy) {
                    Toast.makeText(UpdateEvStationActivity.this, "Current Energy Should be Greater", Toast.LENGTH_SHORT).show();
                } else {
                    data.put("evs_available", evs_available);
                    data.put("evs_energy", evs_energy);
                    data.put("type", type);

                    updateData();

                }

//                } else {
//                    Toast.makeText(UpdateEvStationActivity.this, "Mandatory", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        binding.btnAvailable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if (b) {
                String txt = binding.btnAvailable.getText().toString();

                if (txt.equals("Unavailable")) evs_available = 0;
                else evs_available = 1;
//                }
            }
        });

        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);

                type = Integer.parseInt(radioButton.getText().toString());
            }
        });

    }

    private void setText() {
        evs_available = evs_energy = 0;
    }

    private void setPreviousData() {

        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("EV_Station")
                .document(evs_id)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        EVStation evs = doc.toObject(EVStation.class);

                        if (evs == null) return;

                        previous_energy = evs.getEvs_available();

                        type = evs.getType();

                        if (type != 0) {
                            if (type == 1) {
                                binding.btnType1.setChecked(true);
                            }
                            if (type == 2) {
                                binding.btnType2.setChecked(true);
                            }
                            if (type == 3) {
                                binding.btnType3.setChecked(true);
                            }
                        }

                        evs_available = evs.getEvs_available();

                        if (evs_available == 1) {
                            binding.btnAvailable.setChecked(true);
                        } else {
                            binding.btnAvailable.setChecked(false);
                        }

                        binding.etEmail.setText(Integer.toString(evs.getEvs_energy()));
//                        binding.etAvailable.setText(Integer.toString(previous_energy));
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UpdateEvStationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private int check() {
        if (evs_energy == 0 || evs_available == 0 || type == 0) return 0;
        return 1;
    }

    private void getData() {
        evs_energy = Integer.parseInt(binding.etEmail.getText().toString().trim());

        if (binding.btnAvailable.isChecked()) {
            evs_available = 1;
        } else {
            evs_available = 0;
        }
        if (binding.btnType1.isChecked()) type = 1;
        if (binding.btnType2.isChecked()) type = 2;
        if (binding.btnType3.isChecked()) type = 3;
    }

    private void init() {
        evs_available = evs_energy = 0;
        type = 0;
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}