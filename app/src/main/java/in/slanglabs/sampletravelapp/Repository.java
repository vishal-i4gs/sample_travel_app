package in.slanglabs.sampletravelapp;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.sqlite.db.SimpleSQLiteQuery;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import in.slanglabs.sampletravelapp.Model.Bus;
import in.slanglabs.sampletravelapp.Model.BusAttributes;
import in.slanglabs.sampletravelapp.Model.BusWithAttributes;
import in.slanglabs.sampletravelapp.Model.Journey;
import in.slanglabs.sampletravelapp.Model.JourneyBusPlace;
import in.slanglabs.sampletravelapp.Model.JourneyBusPlaceOrder;
import in.slanglabs.sampletravelapp.Model.OrderBy;
import in.slanglabs.sampletravelapp.Model.OrderItem;
import in.slanglabs.sampletravelapp.Model.OrderStatus;
import in.slanglabs.sampletravelapp.Model.Place;
import in.slanglabs.sampletravelapp.Model.RouteStatus;
import in.slanglabs.sampletravelapp.Model.TimeRange;
import in.slanglabs.sampletravelapp.db.AppDatabase;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    private static Repository shared;

    private List<String> busTypes =
            new ArrayList<>();

    private List<TimeRange> timeRanges =
            new ArrayList<>();

    private AppDatabase mDatabase;
    private final AppExecutors appExecutors;

    private Repository(Context context, final AppDatabase database, final AppExecutors appExecutors) {
        this.mDatabase = database;
        this.appExecutors = appExecutors;

        busTypes.add("A/C");
        busTypes.add("Non A/C");

        timeRanges.add(new TimeRange(1800000, 23340000));
        timeRanges.add(new TimeRange(23400000, 44940000));
        timeRanges.add(new TimeRange(45000000, 66540000));
        timeRanges.add(new TimeRange(-19800000, 1740000));

        String jsonBus = loadJSONFromAsset(context, "list-bus.json");
        Moshi moshiBus = new Moshi.Builder().build();
        Type typeBus = Types.newParameterizedType(List.class, Bus.class);
        JsonAdapter<List<Bus>> adapterBus = moshiBus.adapter(typeBus);
        List<Bus> listItemsBus;
        try {
            if (jsonBus != null) {
                listItemsBus = adapterBus.fromJson(jsonBus);
                List<Bus> finalListItems = listItemsBus;
                appExecutors.diskIO().execute(() -> {
                    database.runInTransaction(() -> {
                        database.busDao().insert(listItemsBus);
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Parsing the json file and adding the items to items table.
        String json = loadJSONFromAsset(context, "list.json");
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new TimeJsonAdapter())
                .build();
        Type type = Types.newParameterizedType(List.class, Journey.class);
        JsonAdapter<List<Journey>> adapter = moshi.adapter(type);
        List<Journey> listItems;
        try {
            if (json != null) {
                listItems = adapter.fromJson(json);
                List<Journey> finalListItems = listItems;
                appExecutors.diskIO().execute(() -> {
                    database.runInTransaction(() -> {
                        database.journayDao().insert(finalListItems);
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonPlace = loadJSONFromAsset(context, "list-place.json");
        Moshi moshiPlace = new Moshi.Builder().build();
        Type typePlace = Types.newParameterizedType(List.class, Place.class);
        JsonAdapter<List<Place>> adapterPlace = moshiPlace.adapter(typePlace);
        List<Place> listItemsPlace;
        try {
            if (jsonPlace != null) {
                listItemsPlace = adapterPlace.fromJson(jsonPlace);
                List<Place> finalListItems = listItemsPlace;
                appExecutors.diskIO().execute(() -> {
                    database.runInTransaction(() -> {
                        database.placeDao().insert(listItemsPlace);
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String jsonBusAttributes = loadJSONFromAsset(context, "list-bus-attributes.json");
        Moshi moshiBusAttributes = new Moshi.Builder().build();
        Type typeBusAttributes = Types.newParameterizedType(List.class, BusAttributes.class);
        JsonAdapter<List<BusAttributes>> adapterBusAttributes = moshiBusAttributes.adapter(typeBusAttributes);
        List<BusAttributes> busAttributes;
        try {
            if (jsonBusAttributes != null) {
                busAttributes = adapterBusAttributes.fromJson(jsonBusAttributes);
                appExecutors.diskIO().execute(() -> {
                    database.runInTransaction(() -> {
                        database.busAttributeDao().insert(busAttributes);
                    });
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        Random rng = new Random();
//        final LiveData<List<JourneyBusPlaceOrder>> userDetailObservable = mDatabase.orderDao().loadAllOrders();
//        Observer observer = new Observer<List<JourneyBusPlaceOrder>>() {
//            @Override
//            public void onChanged(List<JourneyBusPlaceOrder> journeyBusPlaceOrders) {
//                if (journeyBusPlaceOrders.size() > 0) {
//                    List<Integer> generated = new ArrayList<Integer>();
//                    int totalNumberOfOffers = (int) (journeyBusPlaceOrders.size() * 0.1);
//                    for (int i = 0; i < totalNumberOfOffers; i++) {
//                        while (true) {
//                            Integer next = rng.nextInt(journeyBusPlaceOrders.size());
//                            if (!generated.contains(next)) {
//                                generated.add(next);
//                                break;
//                            }
//                        }
//                    }
//
//                    int counter;
//                    for (counter = 0; counter < generated.size(); counter++) {
//                        int randomNumber = generated.get(counter);
//                        JourneyBusPlaceOrder journeyBusPlaceOrder = journeyBusPlaceOrders.get(randomNumber);
//                        appExecutors.diskIO().execute(() -> {
//                            if (journeyBusPlaceOrder.order.active == OrderStatus.ON_SCHEDULE) {
//                                mDatabase.orderDao().update(OrderStatus.DELAYED, journeyBusPlaceOrder.order.orderId);
//                            }
//                        });
//                    }
//                }
//                userDetailObservable.removeObserver(this);
//            }
//        };
//        userDetailObservable.observeForever(observer);


        Random rng = new Random();
        final LiveData<List<Journey>> journies = mDatabase.journayDao().getAllJournies();
        Observer observer = new Observer<List<Journey>>() {
            @Override
            public void onChanged(List<Journey> journeys) {
                if (journeys.size() > 0) {
                    List<Integer> generated = new ArrayList<Integer>();
                    int totalNumberOfOffers = (int) (journeys.size() * 0.3);
                    for (int i = 0; i < totalNumberOfOffers; i++) {
                        while (true) {
                            Integer next = rng.nextInt(journeys.size());
                            if (!generated.contains(next)) {
                                generated.add(next);
                                break;
                            }
                        }
                    }
                    int counter;
                    for (counter = 0; counter < generated.size(); counter++) {
                        int randomNumber = generated.get(counter);
                        Journey journey = journeys.get(randomNumber);
                        Log.d("JourneyId", String.valueOf(journey.journeyId));
                        if(randomNumber % 2 == 0) {
                            appExecutors.diskIO().execute(() -> {
                                mDatabase.journayDao().update(RouteStatus.DELAYED, journey.journeyId);
                            });
                        }
                        else {
                            appExecutors.diskIO().execute(() -> {
                                mDatabase.journayDao().update(RouteStatus.CANCELED, journey.journeyId);
                            });
                        }
                    }
                }
                journies.removeObserver(this);
            }
        };
        journies.observeForever(observer);
    }

    static Repository getInstance(final Context context, final AppDatabase mDatabase, final AppExecutors appExecutors) {
        if (shared == null) {
            synchronized (Repository.class) {
                if (shared == null) {
                    shared = new Repository(context, mDatabase, appExecutors);
                }
            }
        }
        return shared;
    }

    public LiveData<JourneyBusPlace> getJourneyItem(long journeyId) {
        return mDatabase.journayDao().loadJourney(journeyId);
    }

    public LiveData<List<JourneyBusPlace>> getItemsForNameOrderBy(
            String startLocation,
            String endLocation,
            Date startDate,
            List<String> filters,
            List<String> busType,
            List<String> busOperators,
            List<TimeRange> departureTimeRange,
            List<TimeRange> arrivalTimeRange,
            @OrderBy int orderBy) {

        StringBuilder stringBuilder = new StringBuilder();
        List<Object> args = new ArrayList();
        stringBuilder.append("SELECT * FROM journey");
        stringBuilder.append(" JOIN bus ON bus.id = journey.busId JOIN busAttributes ON busAttributes.busId = journey.busId");
        stringBuilder.append(" WHERE");
        stringBuilder.append(" startLocation =?");
        args.add(startLocation);
        stringBuilder.append(" AND endLocation =?");
        args.add(endLocation);

        if(startDate != null) {
            stringBuilder.append(" AND");
            stringBuilder.append(" startTime >= ?");
            args.add(startDate.getTime());
            args.addAll(filters);
        }

        if (!filters.isEmpty()) {
            stringBuilder.append(" AND");
            stringBuilder.append(" travelClass IN (");
            appendPlaceholders(stringBuilder, filters.size());
            stringBuilder.append(")");
            args.addAll(filters);
        }
        if (!busType.isEmpty()) {
            stringBuilder.append(" AND");
            stringBuilder.append(" type IN (");
            appendPlaceholders(stringBuilder, busType.size());
            stringBuilder.append(")");
            args.addAll(busType);
        }
        if (!busOperators.isEmpty()) {
            stringBuilder.append(" AND");
            stringBuilder.append(" travels IN (");
            appendPlaceholders(stringBuilder, busOperators.size());
            stringBuilder.append(")");
            args.addAll(busOperators);
        }

        if (!departureTimeRange.isEmpty()) {
            stringBuilder.append(" AND (");
            for (int i = 0; i < departureTimeRange.size(); i++) {
                TimeRange timeRange = departureTimeRange.get(i);
                if (i != 0) {
                    stringBuilder.append(" OR");
                }
                stringBuilder.append(" (startTime >= ?");
                args.add(timeRange.getStartTime());
                stringBuilder.append("AND startTime <= ?)");
                args.add(timeRange.getEndTime());
            }
            stringBuilder.append(")");
        }

        if (!arrivalTimeRange.isEmpty()) {
            stringBuilder.append(" AND (");
            for (int i = 0; i < arrivalTimeRange.size(); i++) {
                TimeRange timeRange = arrivalTimeRange.get(i);
                if (i != 0) {
                    stringBuilder.append(" OR");
                }
                stringBuilder.append(" (endTime >= ?");
                args.add(timeRange.getStartTime());
                stringBuilder.append("AND endTime <= ?)");
                args.add(timeRange.getEndTime());
            }
            stringBuilder.append(")");
        }

        stringBuilder.append(" GROUP BY id");
        if (orderBy == OrderBy.PRICE) {
            stringBuilder.append(" ORDER BY journey.price ASC");
        } else if (orderBy == OrderBy.RATING) {
            stringBuilder.append(" ORDER BY bus.starRating DESC");
        } else if (orderBy == OrderBy.DEPARTURE_TIME) {
            stringBuilder.append(" ORDER BY startTime ASC");
        } else if (orderBy == OrderBy.TRAVEL_DURATION) {
            stringBuilder.append(" ORDER BY duration ASC");
        } else if (orderBy == OrderBy.RELEVANCE) {
            stringBuilder.append(" ORDER BY travels ASC");
        }
        stringBuilder.append(";");
        return mDatabase.journayDao().getJourneyRawQuery(new SimpleSQLiteQuery(stringBuilder.toString(), args.toArray()));
    }

    //Order Related Opertions/Methods
    public LiveData<JourneyBusPlaceOrder> getOrderItem(String orderItemId) {
        return mDatabase.orderDao().loadOrder(orderItemId);
    }

    public void addOrderItem(OrderItem item) {
        appExecutors.diskIO().execute(() -> {
            mDatabase.orderDao().insert(item);
        });
    }

    public void removeOrderItem(OrderItem item) {
        appExecutors.diskIO().execute(() -> {
            mDatabase.orderDao().update(OrderStatus.CANCELED, item.orderId);
        });
    }

    //Getters
    public LiveData<List<JourneyBusPlaceOrder>> getOrderItems() {
        return mDatabase.orderDao().loadAllOrders();
    }

    public LiveData<List<Journey>> getAllJournies() {
        return mDatabase.journayDao().getAllJournies();
    }

    public List<String> getBusTypes() {
        return busTypes;
    }

    public List<TimeRange> getTimeRanges() {
        return timeRanges;
    }

    public LiveData<List<BusWithAttributes>> getAllBuses() {
        return mDatabase.busDao().getAllBuses();
    }

    public LiveData<List<String>> getAllBusAttributes() {
        return mDatabase.busAttributeDao().getAllBusAttributes();
    }

    public LiveData<List<Place>> getPlacesForName(String name) {
        return mDatabase.placeDao().getPlacesBasedOnSearch(name);
    }

    //Helpers
    public static float randFloat(float min, float max) {
        Random rand = new Random();
        return rand.nextFloat() * (max - min) + min;
    }

    private static String loadJSONFromAsset(Context context, String fileName) {
        String json;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static void appendPlaceholders(StringBuilder builder, int count) {
        for (int i = 0; i < count; i++) {
            builder.append("?");
            if (i < count - 1) {
                builder.append(",");
            }
        }
    }
}
