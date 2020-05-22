package com.example.btpmanager.Helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.btpmanager.Activity.MainActivity;
import com.example.btpmanager.Adapter.FoodOrderAdapter;
import com.example.btpmanager.Adapter.PendingOrderAdapter;
import com.example.btpmanager.Model.FoodQty;
import com.example.btpmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetDialog";
    private static RecyclerView foodRecyclerView;
    private static FoodOrderAdapter foodOrderAdapter;
    private static TextView totalPrice;
    private Button confirmBtn;
    private FirebaseFirestore firebaseFirestore;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_bottom_dialog, container, false);
        foodRecyclerView = view.findViewById(R.id.food_rv);
        totalPrice = view.findViewById(R.id.total_price);
        confirmBtn = view.findViewById(R.id.confirm_btn);

        firebaseFirestore = FirebaseFirestore.getInstance();




        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.mContext, "Please wait while we confirm the order!", Toast.LENGTH_SHORT).show();
                MainActivity.sendConfirmDatatToServer();
            }
        });


        Log.d("xlr8","bottom sheet called");

        populateBottomDialog(PendingOrderAdapter.mContext, FoodList.getInstance().bottomFoodList);

        return view;
    }

    public static void populateBottomDialog(Context context, ArrayList<FoodQty> foodList){
        Log.d("xlr8","list populated");

        int total = 0;
        for(int i=0; i<foodList.size(); i++){
            total+= (Integer.valueOf(foodList.get(i).getFoodPrice()) * Integer.valueOf(foodList.get(i).getFoodQty()));
        }

        totalPrice.setText("₹"+total);

        foodOrderAdapter = new FoodOrderAdapter(context,foodList);
        LinearLayoutManager foodManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        foodRecyclerView.setLayoutManager(foodManager);
        foodRecyclerView.setAdapter(foodOrderAdapter);

    }


}
