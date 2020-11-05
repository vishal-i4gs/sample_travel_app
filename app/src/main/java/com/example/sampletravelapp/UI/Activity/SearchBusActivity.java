package com.example.sampletravelapp.UI.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.OrderBy;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.OrderStatus;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.Adapters.JourneyListAdapter;
import com.example.sampletravelapp.UI.Fragment.FilterDialogFragment;
import com.example.sampletravelapp.UI.ItemClickListener;
import com.example.sampletravelapp.UI.ViewModel.AppViewModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class SearchBusActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private JourneyListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_bus);

        ImageView backButtonView = findViewById(R.id.back_button);
        backButtonView.setOnClickListener(view -> finish());

        TextView sourceTextField = findViewById(R.id.source_text_field);
        TextView destionationTextField = findViewById(R.id.destination_text_field);
        TextView dateTextField = findViewById(R.id.date_text_field);
        TextView emptyJourneyField = findViewById(R.id.journey_empty_text);
        emptyJourneyField.setVisibility(View.GONE);

        RecyclerView listItemView = findViewById(R.id.journey_list_recycler_view);
        Place startLocation = (Place) getIntent().getSerializableExtra("startLoc");
        Place endLocation = (Place) getIntent().getSerializableExtra("endLoc");

        long dateInt = getIntent().getLongExtra("date", 0);
        String dateString = new SimpleDateFormat("dd - MMM - yyyy | EEEE").format(new Date(dateInt));
        dateTextField.setText(dateString);

        sourceTextField.setText(startLocation.name);
        destionationTextField.setText(endLocation.name);

        appViewModel = new ViewModelProvider(this).get(
                AppViewModel.class);
        if(savedInstanceState == null) {
            appViewModel.setBusFilterOptions(getIntent().getParcelableExtra("busFilterOptions"));
        }
        appViewModel.getSearchForStartStopLocationMediator().observe(this, journeys
                -> {
            Log.d("here", String.valueOf(journeys));
            listAdapter.setList(journeys);
            if (journeys.size() == 0) {
                emptyJourneyField.setVisibility(View.VISIBLE);
            } else {
                emptyJourneyField.setVisibility(View.GONE);
            }
        });
        listAdapter = new JourneyListAdapter(new ItemClickListener() {
            @Override
            public void itemClicked(JourneyBusPlace journeyBusPlace) {
                Log.d("here", "here");
                Date journeyDate = new Date(dateInt);
                Calendar journeyCal = Calendar.getInstance();
                journeyCal.setTime(journeyDate);
                Calendar tripCal = Calendar.getInstance();
                tripCal.setTime(journeyBusPlace.journey.startTime);
                journeyCal.set(Calendar.HOUR_OF_DAY, tripCal.get(Calendar.HOUR_OF_DAY));
                journeyCal.set(Calendar.MINUTE, tripCal.get(Calendar.MINUTE));
                journeyCal.set(Calendar.SECOND, tripCal.get(Calendar.SECOND));
                Date finalJourneyDate = journeyCal.getTime();
                if(finalJourneyDate.getTime() < new Date().getTime()) {
                    Toast.makeText(SearchBusActivity.this,"Cannot book for this time slot",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                new AlertDialog.Builder(SearchBusActivity.this)
                        .setTitle("Book ticket")
                        .setMessage("Are you sure you want to book this?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            OrderItem orderItem = new OrderItem();
                            orderItem.orderId = UUID.randomUUID().toString();
                            orderItem.active = OrderStatus.ON_SCHEDULE;
                            orderItem.orderTime = new Date();
                            orderItem.journeyDate = finalJourneyDate;
                            orderItem.journeyId = journeyBusPlace.journey.journeyId;
                            appViewModel.addOrderItem(orderItem);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        appViewModel.getAllBusAttributes().observe(this, filters ->
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                endLocation.id,
                        appViewModel.getBusFilterOptions().getBusFilters(),
                        appViewModel.getBusFilterOptions().getBusType(),
                        appViewModel.getBusFilterOptions().getBusOperators(),
                        appViewModel.getBusFilterOptions().getBusDepartureTimeRange(),
                        appViewModel.getBusFilterOptions().getBusArrivalTimeRange(),
                appViewModel.getOrderBy()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listItemView.setLayoutManager(layoutManager);
        listItemView.setItemAnimator(null);
        listItemView.setAdapter(listAdapter);

        ImageView busFilterButton = findViewById(R.id.bus_filter_button);
        busFilterButton.setOnClickListener((View.OnClickListener) view -> {
            FilterDialogFragment newFragment = FilterDialogFragment.newInstance(appViewModel.getBusFilterOptions());
            newFragment.viewItemListener = busFilterOptions -> {
                appViewModel.setBusFilterOptions(busFilterOptions);
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                        endLocation.id,
                        busFilterOptions.getBusFilters(),
                        busFilterOptions.getBusType(),
                        busFilterOptions.getBusOperators(),
                        busFilterOptions.getBusDepartureTimeRange(),
                        busFilterOptions.getBusArrivalTimeRange(),
                        appViewModel.getOrderBy());
            };
            newFragment.show(getSupportFragmentManager(), "FilterAndSortDialogFragment");
        });

        ImageView busSortButton = findViewById(R.id.bus_sort_button);
        busSortButton.setOnClickListener((View.OnClickListener) view -> {
            final String[] ordering = new String[4];
            int selectedIndxForOrdering = 0;
            ordering[0] = "Rating";
            ordering[1] = "Price";
            ordering[2] = "Deparature Time";
            ordering[3] = "Travel Duration";
            switch (appViewModel.getOrderBy()) {
                case PRICE:
                    selectedIndxForOrdering = 1;
                    break;
                case DEPARTURE_TIME:
                    selectedIndxForOrdering = 2;
                    break;
                case TRAVEL_DURATION:
                    selectedIndxForOrdering = 3;
                    break;
                default:
                    selectedIndxForOrdering = 0;
            }
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchBusActivity.this);
            mBuilder.setTitle("Select preferred sort:");
            mBuilder.setSingleChoiceItems(ordering, selectedIndxForOrdering, (dialogInterface, i) -> {
                switch (i) {
                    case 1:
                        appViewModel.setOrderBy(OrderBy.PRICE);
                        break;
                    case 2:
                        appViewModel.setOrderBy(OrderBy.DEPARTURE_TIME);
                        break;
                    case 3:
                        appViewModel.setOrderBy(OrderBy.TRAVEL_DURATION);
                        break;
                    default:
                        appViewModel.setOrderBy(OrderBy.RATING);
                }
            });
            mBuilder.setPositiveButton("OK", (dialog, which) -> {
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                        endLocation.id,
                        appViewModel.getBusFilterOptions().getBusFilters(),
                        appViewModel.getBusFilterOptions().getBusType(),
                        appViewModel.getBusFilterOptions().getBusOperators(),
                        appViewModel.getBusFilterOptions().getBusDepartureTimeRange(),
                        appViewModel.getBusFilterOptions().getBusArrivalTimeRange(),
                        appViewModel.getOrderBy());
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

    }
}
