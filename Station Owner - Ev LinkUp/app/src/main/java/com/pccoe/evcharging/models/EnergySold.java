package com.pccoe.evcharging.models;

import com.google.firebase.Timestamp;

public class EnergySold {

    int es_amount_earned, es_energy_sold, es_no_of_user_served;
    String es_date;

    public EnergySold() {
    }

    public EnergySold(int es_amount_earned, int es_energy_sold, int es_no_of_user_served, String es_date) {
        this.es_amount_earned = es_amount_earned;
        this.es_energy_sold = es_energy_sold;
        this.es_no_of_user_served = es_no_of_user_served;
        this.es_date = es_date;
    }

    public int getEs_amount_earned() {
        return es_amount_earned;
    }

    public void setEs_amount_earned(int es_amount_earned) {
        this.es_amount_earned = es_amount_earned;
    }

    public int getEs_energy_sold() {
        return es_energy_sold;
    }

    public void setEs_energy_sold(int es_energy_sold) {
        this.es_energy_sold = es_energy_sold;
    }

    public int getEs_no_of_user_served() {
        return es_no_of_user_served;
    }

    public void setEs_no_of_user_served(int es_no_of_user_served) {
        this.es_no_of_user_served = es_no_of_user_served;
    }

    public String getEs_date() {
        return es_date;
    }

    public void setEs_date(String es_date) {
        this.es_date = es_date;
    }
}
