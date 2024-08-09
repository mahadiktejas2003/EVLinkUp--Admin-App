package com.pccoe.evcharging.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.pccoe.evcharging.EvStation.UpdateEvStationActivity;
import com.pccoe.evcharging.R;
import com.pccoe.evcharging.models.EVStation;
import com.pccoe.evcharging.models.History;
import com.pccoe.evcharging.models.Rating;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.LeadData> {

    List<History> dataholder2;

    Context context;

    public HistoryAdapter(List<History> dataholder2, Context context) {
//        Toast.makeText(context, "Call here" + dataholder2.size(), Toast.LENGTH_SHORT).show();
        this.dataholder2 = dataholder2;
        this.context = context;

    }

    public void setFilteredList(List<History> filteredList) {
//        this.dataholder2 = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LeadData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_layout, parent, false);
        return new LeadData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadData holder, int position) {

    }

    @Override
    public int getItemCount() {

        return dataholder2.size();
    }


    class LeadData extends RecyclerView.ViewHolder {
        //        RatingBar ratingBar;
//        TextView tvID, tvAvailable, tvEnergy;
//
//        CardView cardView;


        public LeadData(@NonNull View itemView) {
            super(itemView);

//            ratingBar = itemView.findViewById(R.id.ratingBar);
//            tvID = itemView.findViewById(R.id.tvID);
//            tvAvailable = itemView.findViewById(R.id.tvAvailable);
//            tvEnergy = itemView.findViewById(R.id.tvEnergy);
//
//            cardView = itemView.findViewById(R.id.cv);
//            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }


}