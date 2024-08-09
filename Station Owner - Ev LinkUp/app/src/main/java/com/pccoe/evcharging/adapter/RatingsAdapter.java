package com.pccoe.evcharging.adapter;

import android.content.Context;
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

import com.pccoe.evcharging.R;
import com.pccoe.evcharging.models.Rating;

import java.util.List;


public class RatingsAdapter extends RecyclerView.Adapter<RatingsAdapter.LeadData> {

    List<Rating> dataholder2;

    Context context;

    public RatingsAdapter(List<Rating> dataholder2, Context context) {
//        Toast.makeText(context, "Call here" + dataholder2.size(), Toast.LENGTH_SHORT).show();
        this.dataholder2 = dataholder2;
        this.context = context;

    }

    public void setFilteredList(List<Rating> filteredList) {
//        this.dataholder2 = filteredList;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LeadData onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_layout, parent, false);
        return new LeadData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadData holder, int position) {

        holder.ratingBar.setRating((float) dataholder2.get(position).getRat_rating());
        holder.tvTitle.setText("Title : " + dataholder2.get(position).getRat_title());
        holder.tvDesc.setText("Description : " + dataholder2.get(position).getRat_desc());
        holder.tvName.setText(dataholder2.get(position).getRat_name());
        holder.tvDate.setText(dataholder2.get(position).getRat_date());
    }

    @Override
    public int getItemCount() {

//        if(dataholder2.size()>50)
//        {
//            return 50;
//        }

        return dataholder2.size();
    }


    class LeadData extends RecyclerView.ViewHolder {
        RatingBar ratingBar;
        TextView tvTitle, tvDesc, tvName, tvDate;


        public LeadData(@NonNull View itemView) {
            super(itemView);

            ratingBar = itemView.findViewById(R.id.ratingBar);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            tvName = itemView.findViewById(R.id.tvName);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }


}