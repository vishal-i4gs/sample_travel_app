package com.example.sampletravelapp.db.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.sampletravelapp.Model.OrderItem;

import java.util.List;

@Dao
public interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY orderTime DESC")
    LiveData<List<OrderItem>> loadAllOrders();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(OrderItem orderItem);

    @Query("UPDATE orders SET active=:active WHERE orderId = :orderId")
    void update(boolean active, String orderId);

    @Query("select * from orders where orderId = :orderId")
    LiveData<OrderItem> loadOrder(String orderId);
}