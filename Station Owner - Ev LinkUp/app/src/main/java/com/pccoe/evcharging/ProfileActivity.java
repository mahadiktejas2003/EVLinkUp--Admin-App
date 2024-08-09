package com.pccoe.evcharging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.pccoe.evcharging.Auth.LoginActivity;
import com.pccoe.evcharging.databinding.ActivityProfileBinding;
import com.pccoe.evcharging.models.Owner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {

    //    int e=1, h=0;
    String currentLanguage = "en";
    Locale myLocale;
    private Context context;
    private Resources resources;
    String currentLang;
    private ActivityProfileBinding binding;
    Owner owner;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        init();

        currentLanguage = getIntent().getStringExtra(currentLang);

//        setLocale("en");
//
//        switch(LocaleHelper.getLanguage(ProfileActivity.this))
//        {
//            case "en":
//                binding.btnType1.setChecked(true);
//                break;
//            case "hi":
//                binding.btnType2.setChecked(true);
//                break;
//            default:
//                System.out.println("no match");
//        }

        getData();

        binding.rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);

                String ln = "";

                String s = radioButton.getText().toString();


                if (s.equals("English")) ln = "en";
                if (s.equals("Hindi")) ln = "hi";
//                if (s.equals("English")) ln = "en";


                setLocale(ln);

                context = LocaleHelper.setLocale(ProfileActivity.this, ln);
                resources = getResources();
            }
        });

        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            }
        });

        setContentView(binding.getRoot());
    }

    public void setLocale(String localeName) {
        if (!localeName.equals(currentLanguage)) {
            Context context = LocaleHelper.setLocale(ProfileActivity.this, localeName);
            //Resources resources = context.getResources();
            myLocale = new Locale(localeName);
            Resources res = context.getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            Configuration conf = res.getConfiguration();
            conf.locale = myLocale;
            res.updateConfiguration(conf, dm);
            Intent refresh = new Intent(ProfileActivity.this, MainActivity.class);
            refresh.putExtra(currentLang, localeName);
            startActivity(refresh);
        } else {
            //Toast.makeText(getActivity(), "Language already selected!", Toast.LENGTH_SHORT).show();
        }
    }
    private String getAddress() {

        String add = "";

        try {
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            addresses = geocoder.getFromLocation(owner.getOwner_location().getLatitude(), owner.getOwner_location().getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

            add = addresses.get(0).getAddressLine(0);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return add;
    }

    private void setData() {
        binding.tvName.setText(owner.getOwner_name());
        binding.tvEmail.setText(owner.getOwner_email());
        binding.tvStationName.setText(owner.getEv_station_name());
//        binding.tvAvgRating.setText("Average Rating : " + Double.toString(owner.getAvg_rating()));b
//        binding.tvAvgRating.setRating((float) );

        binding.tvAddress.setText(getAddress());
    }

    private void getData() {
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot doc) {
                        if (doc == null) return;

                        owner = doc.toObject(Owner.class);
                        setData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}