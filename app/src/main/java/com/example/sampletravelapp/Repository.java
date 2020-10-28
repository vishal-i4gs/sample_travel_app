package com.example.sampletravelapp;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.Journey;
import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.OrderBy;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.db.AppDatabase;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class Repository {

    private static final String TAG = Repository.class.getSimpleName();
    private static Repository shared;

    private LiveData<List<JourneyBusPlace>> searchForStartEndLocation =
            new MutableLiveData<>();

    private MediatorLiveData<List<JourneyBusPlace>> searchForStartStopMediator =
            new MediatorLiveData<>();

    private AppDatabase mDatabase;
    private final AppExecutors appExecutors;

    private Repository(Context context, final AppDatabase database, final AppExecutors appExecutors) {
        this.mDatabase = database;
        this.appExecutors = appExecutors;


        //Parsing the json file and adding the items to items table.
        String json = loadJSONFromAsset(context, "list.json");
        Moshi moshi = new Moshi.Builder()
                .add(Date.class, new Rfc3339DateJsonAdapter())
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

        String jsonBus = loadJSONFromAsset(context, "list-bus.json");
        Moshi moshiBus = new Moshi.Builder().build();
        Type typeBus = Types.newParameterizedType(List.class, Bus.class);
        JsonAdapter<List<Bus>> adapterBus = moshiBus.adapter(typeBus);
        List<Bus> listItemsBus;
        try {
            if (json != null) {
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

        String jsonPlace = loadJSONFromAsset(context, "list-place.json");
        Moshi moshiPlace = new Moshi.Builder().build();
        Type typePlace = Types.newParameterizedType(List.class, Place.class);
        JsonAdapter<List<Place>> adapterPlace = moshiPlace.adapter(typePlace);
        List<Place> listItemsPlace;
        try {
            if (json != null) {
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

    public void getItemsForName(String startLocation, String endLocation) {
        if (searchForStartEndLocation != null) {
            searchForStartStopMediator.removeSource(searchForStartEndLocation);
        }
        searchForStartEndLocation = mDatabase.journayDao().getJourneyForStartAndEndLocation(startLocation, endLocation);
        searchForStartStopMediator.addSource(searchForStartEndLocation, itemOfferCarts
                -> searchForStartStopMediator.postValue(itemOfferCarts));
    }

    public void getItemsForName(String startLocation, String endLocation, List<String> filters) {
        if (searchForStartEndLocation != null) {
            searchForStartStopMediator.removeSource(searchForStartEndLocation);
        }
        searchForStartEndLocation = mDatabase.journayDao().getJourneyForStartAndEndLocationWithFilters(startLocation, endLocation, filters);
        searchForStartStopMediator.addSource(searchForStartEndLocation, itemOfferCarts
                -> searchForStartStopMediator.postValue(itemOfferCarts));
    }

    public void getItemsForNameOrderBy(String startLocation, String endLocation, List<String> filters, OrderBy orderBy) {
        if (searchForStartEndLocation != null) {
            searchForStartStopMediator.removeSource(searchForStartEndLocation);
        }
        if (orderBy == OrderBy.PRICE) {
            searchForStartEndLocation = mDatabase.journayDao().getJourneyForStartAndEndLocationWithFiltersAndSortByPrice(startLocation, endLocation, filters);
        } else {
            searchForStartEndLocation = mDatabase.journayDao().getJourneyForStartAndEndLocationWithFiltersAndSortByRating(startLocation, endLocation, filters);
        }
        searchForStartStopMediator.addSource(searchForStartEndLocation, itemOfferCarts
                -> searchForStartStopMediator.postValue(itemOfferCarts));
    }

    //Order Related Opertions/Methods
    public LiveData<OrderItem> getOrderItem(String orderItemId) {
        return mDatabase.orderDao().loadOrder(orderItemId);
    }

    public void addOrderItem(OrderItem item) {
        appExecutors.diskIO().execute(() -> {
            mDatabase.orderDao().insert(item);
        });
    }

    public void removeOrderItem(OrderItem item) {
        appExecutors.diskIO().execute(() -> {
            mDatabase.orderDao().update(false, item.orderId);
        });
    }

    //Getters
    public LiveData<List<OrderItem>> getOrderItems() {
        return mDatabase.orderDao().loadAllOrders();
    }

    public MediatorLiveData<List<JourneyBusPlace>> getSearchForStartStopLocationMediator() {
        return searchForStartStopMediator;
    }

    public LiveData<List<Journey>> getAllJournies() {
        return mDatabase.journayDao().getAllJournies();
    }

    public LiveData<List<Bus>> getAllBuses() {
        return mDatabase.busDao().getAllBuses();
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
}
