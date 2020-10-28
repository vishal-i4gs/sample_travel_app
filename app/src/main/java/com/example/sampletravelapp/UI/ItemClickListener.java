package com.example.sampletravelapp.UI;


import com.example.sampletravelapp.Model.JourneyBusPlace;

public interface ItemClickListener {
    default void itemClicked(int position) {
    }

    default void itemClicked(JourneyBusPlace journeyBusPlace) {
    }
}