package com.example.btpmanager.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.btpmanager.Activity.AddMenuItemActivity;
import com.example.btpmanager.Activity.MainActivity;
import com.example.btpmanager.Adapter.PendingOrderAdapter;
import com.example.btpmanager.Helper.FoodOrders;
import com.example.btpmanager.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 */
public class PastOrdersFragment extends Fragment {

    private static RecyclerView pastOrderRV;
    public static PendingOrderAdapter pendingOrderAdapter;
    private static Context mContext;
    private static final String TAG = "PastOrdersFragment";
    private static View view;


    public PastOrdersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_past_orders, container, false);
        mContext = view.getContext();
        pastOrderRV = view.findViewById(R.id.past_order_rv);
        MainActivity.getOrdersFromServer("past");

        return view;
    }

    public static void populateList(){

        Log.d(TAG, "Populate List Called");
        FoodOrders foodOrders = FoodOrders.getInstance();
        pendingOrderAdapter = new PendingOrderAdapter(mContext,foodOrders.mainOrderArrayList);
        Log.d("xlr8888", String.valueOf(pastOrderRV));
        LinearLayoutManager pendingOrderManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        pastOrderRV.setLayoutManager(pendingOrderManager);
        pastOrderRV.setAdapter(pendingOrderAdapter);

    }

}
