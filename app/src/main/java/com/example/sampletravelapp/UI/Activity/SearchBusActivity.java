package com.example.sampletravelapp.UI.Activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.OrderBy;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.Adapters.JourneyListAdapter;
import com.example.sampletravelapp.UI.ItemClickListener;
import com.example.sampletravelapp.UI.ViewModel.AppViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class SearchBusActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private JourneyListAdapter listAdapter;
    private List<String> busFilters = new ArrayList<>();
    private List<Boolean> selectedBusFilters = new ArrayList<>();
    private OrderBy orderBy = OrderBy.RATING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
                new AlertDialog.Builder(SearchBusActivity.this)
                        .setTitle("Book ticket")
                        .setMessage("Are you sure you want to book this?")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                            journeyBusPlace.journey.date = new Date(dateInt);
                            OrderItem orderItem = new OrderItem();
                            orderItem.orderId = UUID.randomUUID().toString();
                            orderItem.active = true;
                            orderItem.orderTime = new Date();
                            orderItem.journeyBusPlace = journeyBusPlace;
                            appViewModel.addOrderItem(orderItem);
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        });
        appViewModel.getAllBuses().observe(this, new Observer<List<Bus>>() {
            @Override
            public void onChanged(List<Bus> buses) {
                for (Bus bus : buses) {
                    if (!busFilters.contains(bus.type)) {
                        busFilters.add(bus.type);
                        selectedBusFilters.add(true);
                    }
                }
                List<String> finalBusFilters = new ArrayList<>();
                for (int i = 0; i < selectedBusFilters.size(); i++) {
                    if (selectedBusFilters.get(i)) {
                        finalBusFilters.add(busFilters.get(i));
                    }
                }
                appViewModel.getItemsForNameOrderBy(startLocation.id, endLocation.id, finalBusFilters, orderBy);
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listItemView.setLayoutManager(layoutManager);
        listItemView.setItemAnimator(null);
        listItemView.setAdapter(listAdapter);

        ImageView busFilterButton = findViewById(R.id.bus_filter_button);
        busFilterButton.setOnClickListener((View.OnClickListener) view -> {
            final boolean[] selectedBusFiltersArray = new boolean[selectedBusFilters.size()];
            for (int i = 0; i < selectedBusFilters.size(); i++) {
                selectedBusFiltersArray[i] = selectedBusFilters.get(i);
            }
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchBusActivity.this);
            mBuilder.setTitle("Select preferred filter:");
            mBuilder.setMultiChoiceItems(busFilters.toArray(new String[0]),
                    selectedBusFiltersArray, (DialogInterface.OnMultiChoiceClickListener)
                            (dialog, position, isChecked) -> {
                                selectedBusFilters.set(position, isChecked);
                            });
            mBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    List<String> finalBusFilters = new ArrayList<>();
                    for (int i = 0; i < selectedBusFilters.size(); i++) {
                        if (selectedBusFilters.get(i)) {
                            finalBusFilters.add(busFilters.get(i));
                        }
                    }
                    appViewModel.getItemsForNameOrderBy(startLocation.id, endLocation.id, finalBusFilters, orderBy);
                }
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

        ImageView busSortButton = findViewById(R.id.bus_sort_button);
        busSortButton.setOnClickListener((View.OnClickListener) view -> {
            final String[] ordering = new String[2];
            int selectedIndxForOrdering = 0;
            ordering[0] = "Rating";
            ordering[1] = "Price";
            if (orderBy == OrderBy.PRICE) {
                selectedIndxForOrdering = 1;
            }
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchBusActivity.this);
            mBuilder.setTitle("Select preferred filter:");
            mBuilder.setSingleChoiceItems(ordering, selectedIndxForOrdering, (dialogInterface, i) -> {
                if (i == 0) {
                    orderBy = OrderBy.RATING;
                } else {
                    orderBy = OrderBy.PRICE;
                }
            });
            mBuilder.setPositiveButton("OK", (dialog, which) -> {
                List<String> finalBusFilters = new ArrayList<>();
                for (int i = 0; i < selectedBusFilters.size(); i++) {
                    if (selectedBusFilters.get(i)) {
                        finalBusFilters.add(busFilters.get(i));
                    }
                }
                appViewModel.getItemsForNameOrderBy(startLocation.id, endLocation.id, finalBusFilters, orderBy);
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

    }
}
