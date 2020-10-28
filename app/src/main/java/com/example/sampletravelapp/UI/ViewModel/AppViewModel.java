package com.example.sampletravelapp.UI.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;


import com.example.sampletravelapp.App;
import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.Journey;
import com.example.sampletravelapp.Model.JourneyBusPlace;
import com.example.sampletravelapp.Model.OrderBy;
import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.Model.Place;
import com.example.sampletravelapp.Repository;

import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private static final String TAG = AppViewModel.class.getSimpleName();

    private Repository mRepository;

    public AppViewModel(@NonNull Application application) {
        super(application);
        mRepository = ((App) application).getRepository();
    }

    public LiveData<List<Journey>> getAllJournies() {
        return mRepository.getAllJournies();
    }
    public LiveData<List<Bus>> getAllBuses() {
        return mRepository.getAllBuses();
    }

    //Functions/Methods related to orders.
    public LiveData<List<OrderItem>> getOrderItems() {
        return mRepository.getOrderItems();
    }
    public LiveData<OrderItem> getOrderItem(String orderItemId) {
        return mRepository.getOrderItem(orderItemId);
    }
    public void addOrderItem(OrderItem orderItem) {
        mRepository.addOrderItem(orderItem);
    }
    public void removeOrderItem(OrderItem orderItem) {mRepository.removeOrderItem(orderItem);}


    //Functions/Methods related to search.
    public LiveData<List<JourneyBusPlace>> getSearchForStartStopLocationMediator() {
        return mRepository.getSearchForStartStopLocationMediator();
    }
    public void getSearchStartStopLocation(String startLocation, String stopLocation) {
        mRepository.getItemsForName(startLocation,stopLocation);
    }
    public void getSearchStartStopLocation(String startLocation, String stopLocation, List<String> filters) {
        mRepository.getItemsForName(startLocation, stopLocation, filters);
    }
    public void getItemsForNameOrderBy(String startLocation, String stopLocation, List<String> filters, OrderBy orderBy) {
        mRepository.getItemsForNameOrderBy(startLocation, stopLocation, filters, orderBy);
    }
    public LiveData<List<Place>> getPlacesForName(String search) {
        return mRepository.getPlacesForName(search);
    }

}
