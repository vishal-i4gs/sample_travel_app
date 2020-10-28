package com.example.sampletravelapp.UI.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sampletravelapp.Model.OrderItem;
import com.example.sampletravelapp.R;
import com.example.sampletravelapp.UI.Adapters.OrderListAdapter;
import com.example.sampletravelapp.UI.ItemClickListener;
import com.example.sampletravelapp.UI.ViewModel.AppViewModel;

import java.util.List;

public class OrderActivity extends AppCompatActivity {

    private AppViewModel appViewModel;
    private OrderListAdapter listAdapter;
    private List<OrderItem> orderItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_order);

        ImageView backButtonView = findViewById(R.id.back_button);
        backButtonView.setOnClickListener(view -> finish());

        TextView emptyJourneyField = findViewById(R.id.journey_empty_text);
        emptyJourneyField.setVisibility(View.GONE);

        RecyclerView listItemView = findViewById(R.id.journey_list_recycler_view);
        appViewModel = new ViewModelProvider(this).get(
                AppViewModel.class);

        appViewModel.getOrderItems().observe(this, new Observer<List<OrderItem>>() {
            @Override
            public void onChanged(List<OrderItem> orderItems) {
                OrderActivity.this.orderItems = orderItems;
                listAdapter.setList(orderItems);
                if (orderItems.size() == 0) {
                    emptyJourneyField.setVisibility(View.VISIBLE);
                } else {
                    emptyJourneyField.setVisibility(View.GONE);
                }
            }
        });

        listAdapter = new OrderListAdapter(new ItemClickListener() {
            @Override
            public void itemClicked(int position) {
                Log.d("here", "here");
                List<OrderItem> orderItems =  OrderActivity.this.orderItems;
                if(orderItems.size() < position) {
                    return;
                }
                OrderItem orderItem = orderItems.get(position);
                if(orderItem.active) {
                    new AlertDialog.Builder(OrderActivity.this)
                            .setTitle("Cancel ticket")
                            .setMessage("Are you sure you want to cancel this ticket?")
                            .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                appViewModel.removeOrderItem(orderItem);
                            })
                            .setNegativeButton(android.R.string.no, null)
                            .show();
                }
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listItemView.setLayoutManager(layoutManager);
        listItemView.setItemAnimator(null);
        listItemView.setAdapter(listAdapter);
    }
}