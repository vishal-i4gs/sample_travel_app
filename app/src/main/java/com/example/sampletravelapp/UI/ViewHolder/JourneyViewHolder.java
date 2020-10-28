package com.example.sampletravelapp.UI.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.ItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class JourneyViewHolder extends RecyclerView.ViewHolder {

    private TextView journeyTimeInformation;
    private TextView busName;
    private TextView busType;
    private TextView journeyPrice;
    private TextView journeyDuration;
    private TextView busStarRating;
    private JourneyBusPlace journeyBusPlace;

    public JourneyViewHolder(@NonNull View itemView, ItemClickListener itemClickListener) {
        super(itemView);
        journeyTimeInformation = itemView.findViewById(R.id.journey_time_information);
        busName = itemView.findViewById(R.id.bus_name);
        busType = itemView.findViewById(R.id.bus_type);
        journeyPrice = itemView.findViewById(R.id.journey_price);
        journeyDuration = itemView.findViewById(R.id.journey_duration);
        busStarRating = itemView.findViewById(R.id.bus_star_rating);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemClickListener.itemClicked(journeyBusPlace);
            }
        });
    }

    public void setData(JourneyBusPlace journeyBusPlace) {
        this.journeyBusPlace = journeyBusPlace;
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm",Locale.ENGLISH);
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
        busStarRating.setText(String.format(Locale.ENGLISH,"%.2f",journeyBusPlace.bus.starRating));
    }
}
