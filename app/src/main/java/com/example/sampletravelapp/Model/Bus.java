package com.example.sampletravelapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bus")
public class Bus {

    @PrimaryKey
    @NonNull
    public String id;
    public String name;
    public String type;
    public float starRating;
}
