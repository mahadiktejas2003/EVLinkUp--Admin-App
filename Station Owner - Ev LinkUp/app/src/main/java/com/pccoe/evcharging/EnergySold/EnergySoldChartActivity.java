package com.pccoe.evcharging.EnergySold;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.pccoe.evcharging.databinding.ActivityEnergySoldChartBinding;
import com.pccoe.evcharging.models.EnergySold;
import com.pccoe.evcharging.models.Payment;

import java.util.ArrayList;
import java.util.List;

public class EnergySoldChartActivity extends AppCompatActivity {


    List<Payment> list = new ArrayList<>();
    private ActivityEnergySoldChartBinding binding;
    private int mani_amount, mani_energy_sold, mani_user_served;
    private ArrayList<Entry> amount, energy_sold, user_served;
    private ArrayList<ILineDataSet> iLineDataSets;
    private LineData lineData;
    private LineDataSet lineDataSet_amount, lineDataSet_energy_sold, lineDataSet_user_served;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEnergySoldChartBinding.inflate(getLayoutInflater());

        init();

        setEventList();

        getDataFromFirebase();

        setContentView(binding.getRoot());
    }

    private void setEventList() {
        binding.btnAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchAmount();
            }
        });

        binding.btnEnergySold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchEnergySold();
            }
        });

        binding.btnUserServed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchUserServed();
            }
        });
    }

    private void switchAmount() {

        if (mani_amount == 1) {
            iLineDataSets.remove(lineDataSet_amount);
        } else {
            if (amount == null) return;
            lineDataSet_amount = new LineDataSet(amount, "Amount");
            iLineDataSets.add(lineDataSet_amount);
        }

        binding.lcChat.invalidate();

        mani_amount = 1 - mani_amount;
    }

    private void switchEnergySold() {

        if (mani_energy_sold == 1) {
            iLineDataSets.remove(lineDataSet_energy_sold);
        } else {
            if (energy_sold == null) return;
            lineDataSet_energy_sold = new LineDataSet(energy_sold, "Energy Sold");
            iLineDataSets.add(lineDataSet_energy_sold);
        }

        binding.lcChat.invalidate();

        mani_energy_sold = 1 - mani_energy_sold;
    }

    private void switchUserServed() {

        if (mani_user_served == 1) {
            iLineDataSets.remove(lineDataSet_user_served);
        } else {
            if (user_served == null) return;
            lineDataSet_user_served = new LineDataSet(user_served, "User Served");
            iLineDataSets.add(lineDataSet_user_served);
        }

        binding.lcChat.invalidate();

        mani_user_served = 1 - mani_user_served;
    }

    private void getDataFromFirebase(){
        firebaseFirestore
                .collection("Owner")
                .document(firebaseAuth.getCurrentUser().getEmail())
                .collection("Payments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot snaps) {
                        if(snaps==null) return;

                        list.addAll(snaps.toObjects(Payment.class));

                        setChartData();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void setChartData() {

        dataValues_amount();
        dataValues_user_served();
        dataValues_energy_sold();

        for(Payment i : list){

//            Toast.makeText(this, ""+ i.getPayment_from_name(), Toast.LENGTH_SHORT).show();
            int month = Integer.parseInt(i.getPayment_date().substring(5, 7));
            int amt = i.getPayment_energy_sold()*i.getPayment_amount();
            amount.set( month-1, new Entry(month, amt) );
            energy_sold.set( month-1, new Entry(month, i.getPayment_energy_sold()) );

            int sv = (int) user_served.get(month-1).getY()+1;

            user_served.set( month-1, new Entry(month, sv) );
        }

        //Amount
        if (amount == null) return;
        lineDataSet_amount = new LineDataSet(amount, "Amount");
        iLineDataSets.add(lineDataSet_amount);

        //Energy Sold
        if (energy_sold == null) return;
        lineDataSet_energy_sold = new LineDataSet(energy_sold, "Energy Sold");
        iLineDataSets.add(lineDataSet_energy_sold);

        //User Served
        if (user_served == null) return;
        lineDataSet_user_served = new LineDataSet(user_served, "User Served");
        iLineDataSets.add(lineDataSet_user_served);


        lineData = new LineData(iLineDataSets);

        binding.lcChat.setData(lineData);
        binding.lcChat.invalidate();
    }

    private void dataValues_amount() {
        amount = new ArrayList<>();

        amount.add(new Entry(1, 0));
        amount.add(new Entry(2, 0));
        amount.add(new Entry(3, 0));
        amount.add(new Entry(4, 0));
        amount.add(new Entry(5, 0));
        amount.add(new Entry(6, 0));
        amount.add(new Entry(7, 0));
        amount.add(new Entry(8, 0));
        amount.add(new Entry(9, 0));
        amount.add(new Entry(10, 0));
        amount.add(new Entry(11, 0));
        amount.add(new Entry(12, 0));
    }

    private void dataValues_energy_sold() {
        energy_sold = new ArrayList<>();

        energy_sold.add(new Entry(1, 0));
        energy_sold.add(new Entry(2, 0));
        energy_sold.add(new Entry(3, 0));
        energy_sold.add(new Entry(4, 0));
        energy_sold.add(new Entry(5, 0));
        energy_sold.add(new Entry(6, 0));
        energy_sold.add(new Entry(7, 0));
        energy_sold.add(new Entry(8, 0));
        energy_sold.add(new Entry(9, 0));
        energy_sold.add(new Entry(10, 0));
        energy_sold.add(new Entry(11, 0));
        energy_sold.add(new Entry(12, 0));
    }

    private void dataValues_user_served() {
        user_served = new ArrayList<>();

        user_served.add(new Entry(1, 0));
        user_served.add(new Entry(2, 0));
        user_served.add(new Entry(3, 0));
        user_served.add(new Entry(4, 0));
        user_served.add(new Entry(5, 0));
        user_served.add(new Entry(6, 0));
        user_served.add(new Entry(7, 0));
        user_served.add(new Entry(8, 0));
        user_served.add(new Entry(9, 0));
        user_served.add(new Entry(10, 0));
        user_served.add(new Entry(11, 0));
        user_served.add(new Entry(12, 0));
    }

    private void init() {
        mani_amount = mani_energy_sold = mani_user_served = 1;

        iLineDataSets = new ArrayList<>();

        //amount
        amount = null;
        lineDataSet_amount = null;

        //energy_sold
        energy_sold = null;
        lineDataSet_energy_sold = null;

        //user_served
        user_served = null;
        lineDataSet_user_served = null;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }
}