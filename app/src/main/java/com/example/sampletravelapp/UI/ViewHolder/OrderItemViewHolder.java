package com.example.sampletravelapp.UI.ViewHolder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.ItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrderItemViewHolder extends RecyclerView.ViewHolder {

    private TextView journeyTimeInformation;
    private TextView journeyDateInformation;
    private TextView busName;
    private TextView busType;
    private TextView journeyPrice;
    private TextView journeyDuration;
    private TextView orderStateTextView;

    public OrderItemViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        journeyDateInformation = itemView.findViewById(R.id.journey_date_information);
        journeyTimeInformation = itemView.findViewById(R.id.journey_time_information);
        busName = itemView.findViewById(R.id.bus_name);
        busType = itemView.findViewById(R.id.bus_type);
        journeyPrice = itemView.findViewById(R.id.journey_price);
        journeyDuration = itemView.findViewById(R.id.journey_duration);
        orderStateTextView = itemView.findViewById(R.id.journey_order_state);
        itemView.setOnClickListener(view -> itemClickListener.itemClicked(getAdapterPosition()));
    }

    public void setData(OrderItem orderItem) {
        JourneyBusPlace journeyBusPlace = orderItem.journeyBusPlace;
        String dateString = new SimpleDateFormat("EEE, dd MMM yyyy").
                format(orderItem.journeyBusPlace.journey.date);
        journeyDateInformation.setText(dateString);
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.ENGLISH);
        String startTime = String.format(Locale.ENGLISH,"%02d:%02d",
                journeyBusPlace.journey.startHours, journeyBusPlace.journey.startMinutes);
        Date startDateTime = null;
        try {
            startDateTime = dateFormat.parse(startTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date expireDate = new Date(startDateTime.getTime() + journeyBusPlace.journey.duration*1000);
        SimpleDateFormat  dateformatFinal = new SimpleDateFormat("hh:mm aa",Locale.ENGLISH);
        String strDate = dateformatFinal.format(startDateTime);
        String endDate = dateformatFinal.format(expireDate);
        String journeyTimeInfo = strDate + " - " + endDate;
        journeyTimeInformation.setText(journeyTimeInfo);
        busName.setText(journeyBusPlace.bus.name);
        busType.setText(journeyBusPlace.bus.type);
        journeyPrice.setText(String.format(Locale.ENGLISH,"Rs %d", journeyBusPlace.journey.price));
        journeyDuration.setText(String.format(Locale.ENGLISH,"%02d hours %02d min",
                (journeyBusPlace.journey.duration / 3600),
                (journeyBusPlace.journey.duration % 3600) / 60));
        if(orderItem.active) {
            orderStateTextView.setVisibility(View.VISIBLE);
            orderStateTextView.setText("Active");
            orderStateTextView.setBackgroundColor(Color.GREEN);
            orderStateTextView.setTextColor(Color.BLACK);
        }
        else {
            orderStateTextView.setVisibility(View.VISIBLE);
            orderStateTextView.setText("Cancelled");
            orderStateTextView.setBackgroundColor(Color.RED);
            orderStateTextView.setTextColor(Color.WHITE);
        }
    }
}