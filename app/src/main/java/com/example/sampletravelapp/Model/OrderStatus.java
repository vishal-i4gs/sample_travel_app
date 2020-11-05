package com.example.sampletravelapp.Model;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.example.sampletravelapp.Model.OrderStatus.CANCELED;
import static com.example.sampletravelapp.Model.OrderStatus.ON_SCHEDULE;
import static com.example.sampletravelapp.Model.OrderStatus.DELAYED;

@IntDef({CANCELED, ON_SCHEDULE, DELAYED})
@Retention(RetentionPolicy.SOURCE)
public @interface OrderStatus {
    int CANCELED = 0;
    int ON_SCHEDULE = 1;
    int DELAYED = 2;
}