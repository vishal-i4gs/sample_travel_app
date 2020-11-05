package com.example.sampletravelapp.UI.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.sqlite.db.SimpleSQLiteQuery;


import com.example.sampletravelapp.App;
import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.BusAttributes;
import com.example.sampletravelapp.Model.BusFilterOptions;
import com.example.sampletravelapp.Model.BusWithAttributes;
import com.example.sampletravelapp.Model.Journey;
import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.JourneyBusPlaceOrder;
import com.example.sampletravelapp.Model.OrderBy;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.Model.TimeRange;
import com.example.sampletravelapp.Repository;

import java.util.ArrayList;
import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private static final String TAG = AppViewModel.class.getSimpleName();

    private Repository mRepository;
    private MediatorLiveData<List<JourneyBusPlace>> searchForStartStopMediator =
            new MediatorLiveData<>();

    private LiveData<List<JourneyBusPlace>> searchForStartEndLocation =
            new MutableLiveData<>();

    private OrderBy orderBy = OrderBy.RATING;
    private BusFilterOptions busFilterOptions;

    public AppViewModel(@NonNull Application application) {
        super(application);
        mRepository = ((App) application).getRepository();
        busFilterOptions = new BusFilterOptions();
    }

    public BusFilterOptions getBusFilterOptions() {
        return busFilterOptions;
    }

    public void setBusFilterOptions(BusFilterOptions busFilterOptions) {
        this.busFilterOptions = busFilterOptions;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }


    public LiveData<List<Journey>> getAllJournies() {
        return mRepository.getAllJournies();
    }

    public List<String> getBusTypes() {
        return mRepository.getBusTypes();
    }

    public List<TimeRange> getTimeRanges() {
        return mRepository.getTimeRanges();
    }

    public LiveData<List<BusWithAttributes>> getAllBuses() {
        return mRepository.getAllBuses();
    }

    public LiveData<List<String>> getAllBusAttributes() {
        return mRepository.getAllBusAttributes();
    }

    //Functions/Methods related to orders.
    public LiveData<List<JourneyBusPlaceOrder>> getOrderItems() {
        return mRepository.getOrderItems();
    }

    public LiveData<OrderItem> getOrderItem(String orderItemId) {
        return mRepository.getOrderItem(orderItemId);
    }

    public void addOrderItem(OrderItem orderItem) {
        mRepository.addOrderItem(orderItem);
    }

    public void removeOrderItem(OrderItem orderItem) {
        mRepository.removeOrderItem(orderItem);
    }


    //Functions/Methods related to search.
    public MediatorLiveData<List<JourneyBusPlace>> getSearchForStartStopLocationMediator() {
        return searchForStartStopMediator;
    }

    public void getItemsForNameOrderBy(String startLocation,
                                       String stopLocation,
                                       List<String> filters,
                                       List<String> busType,
                                       List<String> busOperators,
                                       List<TimeRange> departureTimeRange,
                                       List<TimeRange> arrivalTimeRange,
                                       OrderBy orderBy) {
        if (searchForStartEndLocation != null) {
            searchForStartStopMediator.removeSource(searchForStartEndLocation);
        }
        searchForStartEndLocation = mRepository.getItemsForNameOrderBy(startLocation,
                stopLocation, filters, busType, busOperators, departureTimeRange, arrivalTimeRange, orderBy);
        searchForStartStopMediator.addSource(searchForStartEndLocation, itemOfferCarts
                -> searchForStartStopMediator.postValue(itemOfferCarts));
    }

    public LiveData<List<Place>> getPlacesForName(String search) {
        return mRepository.getPlacesForName(search);
    }

}
