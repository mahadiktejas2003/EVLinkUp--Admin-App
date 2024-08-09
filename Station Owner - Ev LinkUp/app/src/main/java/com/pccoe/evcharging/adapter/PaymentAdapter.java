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
import com.pccoe.evcharging.models.Payment;
import com.pccoe.evcharging.models.Rating;

import java.util.List;


public class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.LeadData> {

    List<Payment> dataholder2;
    Context context;

    public PaymentAdapter(List<Payment> dataholder2, Context context) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_layout, parent, false);
        return new LeadData(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeadData holder, int position) {

        Toast.makeText(context, "Hello World", Toast.LENGTH_SHORT).show();
        
        holder.user_name.setText(dataholder2.get(position).getPayment_from_name());
        holder.payment_money.setText(Integer.toString(dataholder2.get(position).getPayment_amount()*30));
//        holder.payment_time.setText(dataholder2.get(position).());

//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(context, UpdateEvStationActivity.class);
//
//                intent.putExtra("evs_id", dataholder2.get(position).getEvs_id());
//
//                context.startActivity(intent);
//            }
//        });
//
////        holder.ratingBar.setRating((float) dataholder2.get(position).getRat_rating());
//        holder.tvID.setText(dataholder2.get(position).getEvs_id());
//        if(dataholder2.get(position).getEvs_available()==1){
//            holder.tvAvailable.setText("Available");
//        }else{
//            holder.tvAvailable.setText("Not Available");
//        }
//        holder.tvEnergy.setText(Integer.toString(dataholder2.get(position).getEvs_energy()));
    }

    @Override
    public int getItemCount() {

        return dataholder2.size();
    }


    class LeadData extends RecyclerView.ViewHolder {
        TextView user_name, payment_time, payment_money, payment_status;
        public LeadData(@NonNull View itemView) {
            super(itemView);

            user_name = itemView.findViewById(R.id.user_name);
//            payment_time = itemView.findViewById(R.id.payment_time);
            payment_money = itemView.findViewById(R.id.payment_money);
//            payment_status = itemView.findViewById(R.id.payment_status);

        }
    }

    public boolean isNumeric(String str) {
        return str.matches("\\d+");
    }


}