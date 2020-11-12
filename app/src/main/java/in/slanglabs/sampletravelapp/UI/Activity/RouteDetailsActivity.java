package in.slanglabs.sampletravelapp.UI.Activity;

import android.os.Bundle;

import in.slanglabs.sampletravelapp.Model.BusAttributes;
import in.slanglabs.sampletravelapp.Model.OrderItem;
import in.slanglabs.sampletravelapp.Model.OrderStatus;
import in.slanglabs.sampletravelapp.R;

import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class RouteDetailsActivity extends BaseDetailsActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        long journeyId = getIntent().getLongExtra("journeyId", 0);
        long dateInt = getIntent().getLongExtra("dateInt", 0);
        Date journeyDate = new Date(dateInt);
        orderCancelView.setText("BOOK TICKET");
        orderCancelView.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

        appViewModel.getJourneyItem(journeyId).observe(this, journeyBusPlace -> {
            orderCancelView.setOnClickListener(view -> {
                OrderItem orderItem = new OrderItem();
                orderItem.orderId = UUID.randomUUID().toString();
                orderItem.active = OrderStatus.ON_SCHEDULE;
                orderItem.orderTime = new Date();
                orderItem.journeyDate = journeyDate;
                orderItem.journeyId = journeyBusPlace.journey.journeyId;
                appViewModel.addOrderItem(orderItem);
                finish();
            });
            orderTitle.setText("Route Details");
            String s1 = sdf.format(journeyDate);
            journeyTime.setText(s1);
            journeyStartLocation.setText(journeyBusPlace.sourcePlace.name);
            journeyEndLocation.setText(journeyBusPlace.destinationPlace.name);
            journeyTotalDuration.setText(String.format(Locale.ENGLISH, "%02d hours %02d min",
                    (journeyBusPlace.journey.duration / 3600),
                    (journeyBusPlace.journey.duration % 3600) / 60));
            journeyBusTravelsName.setText(String.format(Locale.ENGLISH, "%s, %s",
                    journeyBusPlace.bus.name, journeyBusPlace.bus.travels));
            journeyBusAttributes.setText(journeyBusPlace.bus.type);
            for (BusAttributes attributes : journeyBusPlace.busAttributesList) {
                journeyBusAttributes.setText(String.format(Locale.ENGLISH, "%s %s",
                        journeyBusAttributes.getText().toString(), attributes.travelClass));
            }
            orderStatusTitleText.setText("Price");
            orderJourneyStatus.setText(String.format(Locale.ENGLISH,"Rs. %d",journeyBusPlace.journey.price));
        });
    }
}