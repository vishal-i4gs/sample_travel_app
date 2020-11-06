package com.example.sampletravelapp.UI.ViewHolder;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.BusAttributes;
import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.JourneyBusPlaceOrder;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.OrderStatus;
import com.example.sampletravelapp.Model.RouteStatus;
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

    public void setData(JourneyBusPlaceOrder orderItem) {
        JourneyBusPlace journeyBusPlace = orderItem.journey;
        String dateString = new SimpleDateFormat("EEE, dd MMM yyyy").
                format(orderItem.order.journeyDate);
        journeyDateInformation.setText(dateString);
        SimpleDateFormat dateformatFinal = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
        String strDate = dateformatFinal.format(journeyBusPlace.journey.startTime);
        String endDate = dateformatFinal.format(journeyBusPlace.journey.endTime);
        String journeyTimeInfo = strDate + " - " + endDate;
        journeyTimeInformation.setText(journeyTimeInfo);
        busName.setText(journeyBusPlace.bus.name);
        busType.setText(journeyBusPlace.bus.type);
        for (BusAttributes attributes : journeyBusPlace.busAttributesList) {
            busType.setText(String.format(Locale.ENGLISH, "%s %s", busType.getText().toString(), attributes.travelClass));
        }
        journeyPrice.setText(String.format(Locale.ENGLISH, "Rs %d", journeyBusPlace.journey.price));
        journeyDuration.setText(String.format(Locale.ENGLISH, "%02d hours %02d min",
                (journeyBusPlace.journey.duration / 3600),
                (journeyBusPlace.journey.duration % 3600) / 60));
        if (new Date().getTime() > orderItem.order.journeyDate.getTime()) {
            orderStateTextView.setVisibility(View.VISIBLE);
            orderStateTextView.setText("Completed");
            orderStateTextView.setBackgroundColor(Color.GRAY);
            orderStateTextView.setTextColor(Color.BLACK);
        } else {
            if(orderItem.order.active == OrderStatus.CANCELED) {
                orderStateTextView.setVisibility(View.VISIBLE);
                orderStateTextView.setText("Cancelled");
                orderStateTextView.setBackgroundColor(Color.RED);
                orderStateTextView.setTextColor(Color.WHITE);
            }
            else {
                switch (journeyBusPlace.journey.routeStatus) {
                    case RouteStatus
                            .DELAYED:
                        orderStateTextView.setVisibility(View.VISIBLE);
                        orderStateTextView.setText("Delayed");
                        orderStateTextView.setBackgroundColor(Color.BLACK);
                        orderStateTextView.setTextColor(Color.WHITE);
                        break;
                    case RouteStatus
                            .CANCELED:
                        orderStateTextView.setVisibility(View.VISIBLE);
                        orderStateTextView.setText("Cancelled By Operator");
                        orderStateTextView.setBackgroundColor(Color.RED);
                        orderStateTextView.setTextColor(Color.WHITE);
                        break;
                    default:
                        orderStateTextView.setVisibility(View.VISIBLE);
                        orderStateTextView.setText("On Schedule");
                        orderStateTextView.setBackgroundColor(Color.GREEN);
                        orderStateTextView.setTextColor(Color.BLACK);
                        break;
                }
            }
        }
    }
}