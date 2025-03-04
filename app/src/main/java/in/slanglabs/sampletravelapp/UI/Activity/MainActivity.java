package in.slanglabs.sampletravelapp.UI.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import in.slanglabs.sampletravelapp.Model.BusFilterSortOptions;
import in.slanglabs.sampletravelapp.Model.Place;
import in.slanglabs.sampletravelapp.R;
import in.slanglabs.sampletravelapp.UI.Fragment.SearchDialogFragment;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button search_buses;
    private final Place sourcePlace = new Place();
    private final Place destinationPlace = new Place();
    private long selectedDateInt;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        ImageView myBookingsButton = findViewById(R.id.my_booking_button);
        myBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });

        search_buses = findViewById(R.id.search_buses);

        TextView destinationTextField = findViewById(R.id.destination_text_field);
        destinationTextField.setOnClickListener(view -> showDialog(destinationTextField, destinationPlace));

        TextView sourceTextField = findViewById(R.id.source_text_field);
        sourceTextField.setOnClickListener(view -> showDialog(sourceTextField, sourcePlace));

        TextView dateTextField = findViewById(R.id.date_text_field);
        String s1 = sdf.format(new Date());
        dateTextField.setText(s1);
        selectedDateInt = new Date().getTime();

        dateTextField.setOnClickListener(view -> {

            // Get Current Date
            final Calendar c = Calendar.getInstance();
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);

            Calendar minDate = Calendar.getInstance();
            minDate.set(mYear, mMonth, mDay);

            Calendar maxDate = Calendar.getInstance();
            maxDate.set(mYear, mMonth, mDay + 30);

            DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this,
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        String s = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                        Date date = null;
                        try {
                            date = formatter.parse(s);
                            String dateString = sdf.format(date.getTime());
                            selectedDateInt = date.getTime();
                            dateTextField.setText(dateString);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }, mYear, mMonth, mDay);

            final DatePicker datePicker = datePickerDialog.getDatePicker();
            datePicker.setMinDate(minDate.getTimeInMillis());
            datePicker.setMaxDate(maxDate.getTimeInMillis());

            datePickerDialog.show();
        });

        search_buses.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), SearchBusActivity.class);
            if (TextUtils.isEmpty(sourcePlace.id) || TextUtils.isEmpty(destinationPlace.id)) {
                return;
            }
            if(sourcePlace.id.equals(destinationPlace.id)) {
                return;
            }
            intent.putExtra("startLoc", (Serializable) sourcePlace);
            intent.putExtra("endLoc", (Serializable) destinationPlace);
            intent.putExtra("date", selectedDateInt);
            BusFilterSortOptions busFilterSortOptions = new BusFilterSortOptions();
            List<String> busTypes = new ArrayList<>();
            List<String> busFilters = new ArrayList<>();
            busFilterSortOptions.setBusType(busTypes);
            busFilterSortOptions.setBusFilters(busFilters);
            intent.putExtra("busFilterOptions", busFilterSortOptions);
            startActivity(intent);
        });
    }

    protected void showDialog(TextView textView, Place placeSourceDestination) {
        SearchDialogFragment newFragment = SearchDialogFragment.newInstance("");
        newFragment.viewItemListener = place -> {
            textView.setText(place.name);
            placeSourceDestination.id = place.id;
            placeSourceDestination.name = place.name;
        };
        newFragment.show(getSupportFragmentManager(), "autoCompleteFragment");
    }
}
