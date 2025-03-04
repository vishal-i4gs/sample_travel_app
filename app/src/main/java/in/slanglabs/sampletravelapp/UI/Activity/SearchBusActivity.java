package in.slanglabs.sampletravelapp.UI.Activity;

import android.content.Intent;
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

import in.slanglabs.sampletravelapp.Model.JourneyBusPlace;
import in.slanglabs.sampletravelapp.Model.OrderBy;
import in.slanglabs.sampletravelapp.Model.OrderItem;
import in.slanglabs.sampletravelapp.Model.OrderStatus;
import in.slanglabs.sampletravelapp.Model.Place;
import in.slanglabs.sampletravelapp.R;
import in.slanglabs.sampletravelapp.UI.Adapters.JourneyListAdapter;
import in.slanglabs.sampletravelapp.UI.Fragment.FilterDialogFragment;
import in.slanglabs.sampletravelapp.UI.ItemClickListener;
import in.slanglabs.sampletravelapp.UI.ViewModel.AppViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import static in.slanglabs.sampletravelapp.Model.OrderBy.DEPARTURE_TIME;
import static in.slanglabs.sampletravelapp.Model.OrderBy.PRICE;
import static in.slanglabs.sampletravelapp.Model.OrderBy.RATING;
import static in.slanglabs.sampletravelapp.Model.OrderBy.RELEVANCE;
import static in.slanglabs.sampletravelapp.Model.OrderBy.TRAVEL_DURATION;

public class SearchBusActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private JourneyListAdapter listAdapter;
    private Date startDate;

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

        if(new Date(dateInt).getTime() <= new Date().getTime()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH mm");
            try {
                startDate = dateFormat.parse(dateFormat.format(new Date()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        sourceTextField.setText(startLocation.name);
        destionationTextField.setText(endLocation.name);

        appViewModel = new ViewModelProvider(this).get(
                AppViewModel.class);
        if (savedInstanceState == null) {
            appViewModel.setBusFilterSortOptions(getIntent().getParcelableExtra("busFilterOptions"));
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
                if (finalJourneyDate.getTime() < new Date().getTime()) {
                    Toast.makeText(SearchBusActivity.this, "Cannot book for this time slot",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(SearchBusActivity.this, RouteDetailsActivity.class);
                intent.putExtra("journeyId",journeyBusPlace.journey.journeyId);
                intent.putExtra("dateInt",finalJourneyDate.getTime());
                startActivity(intent);
            }
        });
        appViewModel.getAllBusAttributes().observe(this, filters ->
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                        endLocation.id,
                        startDate,
                        appViewModel.getBusFilterSortOptions().getBusFilters(),
                        appViewModel.getBusFilterSortOptions().getBusType(),
                        appViewModel.getBusFilterSortOptions().getBusOperators(),
                        appViewModel.getBusFilterSortOptions().getBusDepartureTimeRange(),
                        appViewModel.getBusFilterSortOptions().getBusArrivalTimeRange(),
                        appViewModel.getBusFilterSortOptions().getBusOrderBy()));
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listItemView.setLayoutManager(layoutManager);
        listItemView.setItemAnimator(null);
        listItemView.setAdapter(listAdapter);

        ImageView busFilterButton = findViewById(R.id.bus_filter_button);
        busFilterButton.setOnClickListener((View.OnClickListener) view -> {
            FilterDialogFragment newFragment = FilterDialogFragment.newInstance(appViewModel.getBusFilterSortOptions());
            newFragment.viewItemListener = busFilterOptions -> {
                appViewModel.setBusFilterSortOptions(busFilterOptions);
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                        endLocation.id,
                        startDate,
                        busFilterOptions.getBusFilters(),
                        busFilterOptions.getBusType(),
                        busFilterOptions.getBusOperators(),
                        busFilterOptions.getBusDepartureTimeRange(),
                        busFilterOptions.getBusArrivalTimeRange(),
                        appViewModel.getBusFilterSortOptions().getBusOrderBy());
            };
            newFragment.show(getSupportFragmentManager(), "FilterAndSortDialogFragment");
        });

        ImageView busSortButton = findViewById(R.id.bus_sort_button);
        busSortButton.setOnClickListener((View.OnClickListener) view -> {
            final String[] ordering = new String[5];
            int selectedIndxForOrdering = 0;
            ordering[0] = "Relevance";
            ordering[1] = "Rating";
            ordering[2] = "Price";
            ordering[3] = "Deparature Time";
            ordering[4] = "Travel Duration";
            switch (appViewModel.getBusFilterSortOptions().getBusOrderBy()) {
                case RATING:
                    selectedIndxForOrdering = 1;
                    break;
                case PRICE:
                    selectedIndxForOrdering = 2;
                    break;
                case DEPARTURE_TIME:
                    selectedIndxForOrdering = 3;
                    break;
                case TRAVEL_DURATION:
                    selectedIndxForOrdering = 4;
                    break;
                default:
                    selectedIndxForOrdering = 0;
            }
            final AlertDialog.Builder mBuilder = new AlertDialog.Builder(SearchBusActivity.this);
            mBuilder.setTitle("Select preferred sort:");
            mBuilder.setSingleChoiceItems(ordering, selectedIndxForOrdering, (dialogInterface, i) -> {
                switch (i) {
                    case 1:
                        appViewModel.getBusFilterSortOptions().setBusOrderBy(RATING);
                        break;
                    case 2:
                        appViewModel.getBusFilterSortOptions().setBusOrderBy(PRICE);
                        break;
                    case 3:
                        appViewModel.getBusFilterSortOptions().setBusOrderBy(DEPARTURE_TIME);
                        break;
                    case 4:
                        appViewModel.getBusFilterSortOptions().setBusOrderBy(TRAVEL_DURATION);
                        break;
                    default:
                        appViewModel.getBusFilterSortOptions().setBusOrderBy(RELEVANCE);
                }
            });
            mBuilder.setPositiveButton("OK", (dialog, which) -> {
                appViewModel.getItemsForNameOrderBy(startLocation.id,
                        endLocation.id,
                        startDate,
                        appViewModel.getBusFilterSortOptions().getBusFilters(),
                        appViewModel.getBusFilterSortOptions().getBusType(),
                        appViewModel.getBusFilterSortOptions().getBusOperators(),
                        appViewModel.getBusFilterSortOptions().getBusDepartureTimeRange(),
                        appViewModel.getBusFilterSortOptions().getBusArrivalTimeRange(),
                        appViewModel.getBusFilterSortOptions().getBusOrderBy());
            });
            AlertDialog mDialog = mBuilder.create();
            mDialog.show();
        });

    }
}
