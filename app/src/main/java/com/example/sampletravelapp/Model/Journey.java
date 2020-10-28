package com.example.sampletravelapp.Model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "journey")
public class Journey {

    @PrimaryKey
    @NonNull
    public int id;
    public Date date;
    public String startLocation;
    public String endLocation;
    public int startHours;
    public int startMinutes;
    public String busId;
    public long duration;
    public long price;

}
