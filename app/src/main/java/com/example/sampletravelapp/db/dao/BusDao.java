package com.example.sampletravelapp.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.sampletravelapp.Model.Bus;
import com.example.sampletravelapp.Model.BusWithAttributes;

import java.util.List;

@Dao
public interface BusDao {

    @Transaction
    @Query("SELECT * FROM bus")
    LiveData<List<BusWithAttributes>> getAllBuses();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Bus> items);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Bus item);
}
