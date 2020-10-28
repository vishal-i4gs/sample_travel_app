package com.example.sampletravelapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.sampletravelapp.Model.Journey;
import com.example.sampletravelapp.Model.JourneyBusPlace;

import java.util.List;

@Dao
public interface JournayDao {

    @Transaction
    @Query("SELECT * FROM journey")
    LiveData<List<Journey>> getAllJournies();

    @Transaction
    @Query("SELECT * FROM journey")
    LiveData<List<Journey>> getItems();

    @Query("DELETE FROM journey")
    public void removeAllItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Journey> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Journey item);

    @Query("SELECT * FROM journey WHERE startLocation = :startLocation AND endLocation = :endLocation")
    LiveData<List<JourneyBusPlace>> getJourneyForStartAndEndLocation(String startLocation, String endLocation);


    @Query("SELECT * FROM journey JOIN bus ON bus.id = busId WHERE startLocation = :startLocation AND endLocation = :endLocation AND type IN (:filters)")
    LiveData<List<JourneyBusPlace>> getJourneyForStartAndEndLocationWithFilters(String startLocation, String endLocation, List<String> filters);

    @Query("SELECT * FROM journey JOIN bus ON bus.id = busId WHERE startLocation = :startLocation AND endLocation = :endLocation AND type IN (:filters) ORDER BY bus.starRating DESC")
    LiveData<List<JourneyBusPlace>> getJourneyForStartAndEndLocationWithFiltersAndSortByRating(String startLocation, String endLocation, List<String> filters);

    @Query("SELECT * FROM journey JOIN bus ON bus.id = busId WHERE startLocation = :startLocation AND endLocation = :endLocation AND type IN (:filters) ORDER BY price ASC")
    LiveData<List<JourneyBusPlace>> getJourneyForStartAndEndLocationWithFiltersAndSortByPrice(String startLocation, String endLocation, List<String> filters);
}

