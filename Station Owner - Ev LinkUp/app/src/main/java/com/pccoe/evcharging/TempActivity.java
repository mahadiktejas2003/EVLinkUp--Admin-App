package com.pccoe.evcharging;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.pccoe.evcharging.databinding.ActivityTempBinding;

public class TempActivity extends AppCompatActivity {

    private ActivityTempBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTempBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
    }
}