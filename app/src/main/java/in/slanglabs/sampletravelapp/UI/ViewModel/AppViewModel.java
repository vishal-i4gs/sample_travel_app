package in.slanglabs.sampletravelapp.UI.ViewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;


import in.slanglabs.sampletravelapp.App;
import in.slanglabs.sampletravelapp.Model.BusFilterSortOptions;
import in.slanglabs.sampletravelapp.Model.BusWithAttributes;
import in.slanglabs.sampletravelapp.Model.Journey;
import in.slanglabs.sampletravelapp.Model.JourneyBusPlace;
import in.slanglabs.sampletravelapp.Model.JourneyBusPlaceOrder;
import in.slanglabs.sampletravelapp.Model.OrderBy;
import in.slanglabs.sampletravelapp.Model.OrderItem;
import in.slanglabs.sampletravelapp.Model.Place;
import in.slanglabs.sampletravelapp.Model.TimeRange;
import in.slanglabs.sampletravelapp.Repository;

import java.util.Date;
import java.util.List;

public class AppViewModel extends AndroidViewModel {

    private static final String TAG = AppViewModel.class.getSimpleName();

    private Repository mRepository;
    private MediatorLiveData<List<JourneyBusPlace>> searchForStartStopMediator =
            new MediatorLiveData<>();

    private LiveData<List<JourneyBusPlace>> searchForStartEndLocation =
            new MutableLiveData<>();

    private BusFilterSortOptions busFilterSortOptions;

    public AppViewModel(@NonNull Application application) {
        super(application);
        mRepository = ((App) application).getRepository();
        busFilterSortOptions = new BusFilterSortOptions();
    }

    public BusFilterSortOptions getBusFilterSortOptions() {
        return busFilterSortOptions;
    }

    public void setBusFilterSortOptions(BusFilterSortOptions busFilterSortOptions) {
        this.busFilterSortOptions = busFilterSortOptions;
    }

    public LiveData<List<Journey>> getAllJournies() {
        return mRepository.getAllJournies();
    }

    public LiveData<JourneyBusPlace> getJourneyItem(long journeyId) {
        return mRepository.getJourneyItem(journeyId);
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

    public LiveData<JourneyBusPlaceOrder> getOrderItem(String orderItemId) {
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
                                       Date startDate,
                                       List<String> filters,
                                       List<String> busType,
                                       List<String> busOperators,
                                       List<TimeRange> departureTimeRange,
                                       List<TimeRange> arrivalTimeRange,
                                       @OrderBy int orderBy) {
        if (searchForStartEndLocation != null) {
            searchForStartStopMediator.removeSource(searchForStartEndLocation);
        }
        searchForStartEndLocation = mRepository.getItemsForNameOrderBy(startLocation,
                stopLocation, startDate, filters, busType, busOperators, departureTimeRange, arrivalTimeRange, orderBy);
        searchForStartStopMediator.addSource(searchForStartEndLocation, itemOfferCarts
                -> searchForStartStopMediator.postValue(itemOfferCarts));
    }

    public LiveData<List<Place>> getPlacesForName(String search) {
        return mRepository.getPlacesForName(search);
    }

}
