package com.example.sampletravelapp.UI.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.sampletravelapp.Model.BusAttributes;
import com.example.sampletravelapp.Model.OrderStatus;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.ViewModel.AppViewModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TicketViewActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy, hh:mm a", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ticket_view);

        ImageView backButtonView = findViewById(R.id.back_button);
        backButtonView.setOnClickListener(view -> finish());

        TextView orderTitle = findViewById(R.id.order_title);
        TextView journeyTime = findViewById(R.id.order_journey_date);
        TextView journeyStartLocation = findViewById(R.id.order_journey_start_location);
        TextView journeyEndLocation = findViewById(R.id.order_journey_end_location);
        TextView journeyTotalDuration = findViewById(R.id.order_journey_total_duration);
        TextView journeyBusTravelsName = findViewById(R.id.order_journey_travels_name);
        TextView journeyBusAttributes = findViewById(R.id.order_journey_bus_type);
        TextView orderJourneyStatus = findViewById(R.id.order_journey_status);

        View orderCancelView = findViewById(R.id.order_cancel_button);

        String orderId = getIntent().getStringExtra("orderItemId");
        appViewModel = new ViewModelProvider(this).get(
                AppViewModel.class);
        appViewModel.getOrderItem(orderId).observe(this, orderItem -> {
            orderCancelView.setOnClickListener(view -> {
                appViewModel.removeOrderItem(orderItem.order);
                finish();
            });
            orderTitle.setText(String.format(Locale.ENGLISH,"Ticket #%s",orderItem.order.orderId.substring(0,10)));
            String s1 = sdf.format(orderItem.order.journeyDate);
            journeyTime.setText(s1);
            journeyStartLocation.setText(orderItem.journey.sourcePlace.name);
            journeyEndLocation.setText(orderItem.journey.destinationPlace.name);
            journeyTotalDuration.setText(String.format(Locale.ENGLISH, "%02d hours %02d min",
                    (orderItem.journey.journey.duration / 3600),
                    (orderItem.journey.journey.duration % 3600) / 60));
            journeyBusTravelsName.setText(String.format(Locale.ENGLISH, "%s, %s",
                    orderItem.journey.bus.name, orderItem.journey.bus.travels));
            journeyBusAttributes.setText(orderItem.journey.bus.type);
            for(BusAttributes attributes:orderItem.journey.busAttributesList) {
                journeyBusAttributes.setText(String.format(Locale.ENGLISH,"%s %s",
                        journeyBusAttributes.getText().toString(),attributes.travelClass));
            }
            switch (orderItem.order.active) {
                case OrderStatus
                        .ON_SCHEDULE:
                    orderJourneyStatus.setVisibility(View.VISIBLE);
                    orderJourneyStatus.setText("On Schedule");
                    orderJourneyStatus.setBackgroundColor(Color.GREEN);
                    orderJourneyStatus.setTextColor(Color.BLACK);
                    break;
                case OrderStatus
                        .DELAYED:
                    orderJourneyStatus.setVisibility(View.VISIBLE);
                    orderJourneyStatus.setText("Delayed");
                    orderJourneyStatus.setBackgroundColor(Color.BLACK);
                    orderJourneyStatus.setTextColor(Color.WHITE);
                    break;
            }
        });


    }


}