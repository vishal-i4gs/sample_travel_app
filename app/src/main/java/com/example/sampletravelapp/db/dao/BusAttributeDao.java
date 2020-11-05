package com.example.sampletravelapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.sampletravelapp.Model.BusAttributes;

import java.util.List;

@Dao
public interface BusAttributeDao {

    @Transaction
    @Query("SELECT DISTINCT travelClass FROM busAttributes")
    LiveData<List<String>> getAllBusAttributes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<BusAttributes> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(BusAttributes item);
}
