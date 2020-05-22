package com.example.btpmanager.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.btpmanager.Adapter.PendingOrderAdapter;
import com.example.btpmanager.Fragment.PastOrdersFragment;
import com.example.btpmanager.Fragment.PendingOrdersFragment;
import com.example.btpmanager.Helper.FoodOrders;
import com.example.btpmanager.Model.FoodQty;
import com.example.btpmanager.Model.Order;
import com.example.btpmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private static FirebaseFirestore firebaseFirestore;
    private Toolbar toolbar;
    public static ProgressDialog fetching_progress;
    public static Context mContext;





    private static final String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        toolbar = findViewById(R.id.main_toolbar);
        mContext = getApplicationContext();


        firebaseFirestore = FirebaseFirestore.getInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new PendingOrdersFragment()).addToBackStack(null).commit();
        }

        fetching_progress = new ProgressDialog(this);


        //getOrdersFromServer();

        FoodOrders foodOrders = FoodOrders.getInstance();

        // Log.d(TAG, "Main list size: "+foodOrders.mainOrderArrayList.size());

        // Updating data
        firebaseFirestore.collection("Orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                Log.d(TAG, "Updating Data....................");

                for(DocumentChange documentChange : queryDocumentSnapshots.getDocumentChanges()){
                 if(documentChange.getType() == DocumentChange.Type.ADDED){
                     Log.d(TAG, String.valueOf(documentChange.getDocument().getId()));
                     getOrdersFromServer("pending");
                 }
                }
            }
        });
    }

    public static void getOrdersFromServer(final String source) {
        Log.d(TAG,"Getting data");

        fetching_progress.setTitle("Fetching Data");
        fetching_progress.setMessage("Please wait while we fetch the data from server!");
        fetching_progress.setCanceledOnTouchOutside(false);
        fetching_progress.setCancelable(false);
        fetching_progress.show();


        FoodOrders foodOrders = FoodOrders.getInstance();
        foodOrders.mainOrderArrayList.clear();


        try {
            firebaseFirestore.collection("Pending Orders")
                    .orderBy("timestamp",Query.Direction.DESCENDING)
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {

                        ArrayList<Order> orderArrayList = new ArrayList<>();
                        String custName, custNumber, orderAmtPaid, custImage, orderTimestamp, custID, orderStatus, docID, custOrderID;

                        for (DocumentSnapshot documentSnapshot : task.getResult()) {

                            ArrayList<String> orderDetails = (ArrayList<String>) documentSnapshot.get("orderDetails");
                            Log.d(TAG, "ID: "+documentSnapshot.getId()+", Order Details: "+orderDetails.size());

                            ArrayList<FoodQty> foodQtyList = new ArrayList<>();

                            custName = documentSnapshot.getString("name");
                            custNumber = documentSnapshot.getString("number");
                            orderAmtPaid = String.valueOf((Long)documentSnapshot.get("amount_paid"));
                            custImage = documentSnapshot.getString("customer_thumb_image");
                            orderTimestamp = String.valueOf(documentSnapshot.get("timestamp"));
                            custID = documentSnapshot.getString("uid");
                            orderStatus = documentSnapshot.getString("order_status");
                            docID = documentSnapshot.getId();
                            custOrderID = documentSnapshot.getString("cust_order_doc_id");





                            for(int i=0; i<orderDetails.size(); i++){
                                String[] orderData = orderDetails.get(i).split("#");

                                String foodName=null, foodQty=null, foodPrice=null, foodID=null, foodCuisineCode=null, foodImage=null;

                                for(int j=1; j<orderData.length; j++){

                                    Log.d(TAG, "Order Data : "+j+" : "+orderData[j]);
                                    String[] temp = orderData[j].split(":");
                                    //Log.d(TAG, "Data: "+temp[1].trim());

                                    switch (j){
                                        case 1: foodName = temp[1].trim();
                                            break;
                                        case 2: foodQty =  temp[1].trim();
                                            break;
                                        case 3: foodPrice =  temp[1].trim();
                                            break;
                                        case 4: foodID =  temp[1].trim();
                                            break;
                                        case 5: foodCuisineCode =  temp[1].trim();
                                            break;
                                        case 6: foodImage =  orderData[j].substring(orderData[j].indexOf(":")+1).trim();
                                            break;
                                    }
                                }

                                foodQtyList.add(new FoodQty(foodName, foodQty, foodPrice, foodID, foodCuisineCode, foodImage));
                                Log.d(TAG, foodName+", "+foodImage);

                                foodName=null; foodQty=null; foodPrice=null; foodID=null; foodCuisineCode=null; foodImage=null;
                            }

                            orderArrayList.add(new Order(custName,custNumber,custID,custImage,orderAmtPaid,orderTimestamp,orderStatus,foodQtyList, docID, custOrderID));

                        }

                        FoodOrders foodOrders = FoodOrders.getInstance();
                        foodOrders.mainOrderArrayList = orderArrayList;
                        if(source.equals("pending"))
                        PendingOrdersFragment.populateList();
                        else if(source.equals("past"))
                        PastOrdersFragment.populateList();

                        Log.d(TAG, "Main list size2: "+foodOrders.mainOrderArrayList.size());


                        fetching_progress.dismiss();
                        Toast.makeText(mContext, task.getResult().size()+" Data fetched. Updating list...", Toast.LENGTH_SHORT).show();


                    } else {
                        fetching_progress.dismiss();
                        Toast.makeText(mContext, "Something is wrong", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Something is wrong");
                    }

                }

            });

        } catch (Exception e){
            fetching_progress.dismiss();
            Toast.makeText(mContext, "Error Occured", Toast.LENGTH_SHORT).show();
            Log.d(TAG, e.getMessage());
        }


    }



    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_pending:
                            selectedFragment = new PendingOrdersFragment();
                            toolbar.setTitle("Pending Orders");
                            break;

                        case R.id.nav_past:
                            selectedFragment = new PastOrdersFragment();
                            toolbar.setTitle("Past Orders");
                            break;

                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).addToBackStack(null).commit();

                    return true;
                }
            };

    @Override
    public void onBackPressed() {
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public static void sendConfirmDatatToServer(){



        FoodOrders foodOrders = FoodOrders.getInstance();
        final String docID = foodOrders.mainOrderArrayList.get(PendingOrderAdapter.clickPos).getDocId();



        try {
            firebaseFirestore.collection("Pending Orders").document(docID)
                    .update("order_status", "success").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        Toast.makeText(mContext, "Data updated at Pending Orders Database", Toast.LENGTH_SHORT).show();
                        sendConfirmDataToUserOrderDatabase();

                        //PendingOrderAdapter.bottomSheetDialog.dismiss();
                        //getOrdersFromServer();
                        //PendingOrdersFragment.pendingOrderAdapter.notifyDataSetChanged();
                        //Toast.makeText(MainActivity.mContext, "Order Confirmed!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }catch (Exception e){
            Log.d(TAG,e.getMessage());
            Toast.makeText(MainActivity.mContext, "Oops! Error Occured", Toast.LENGTH_SHORT).show();
        }

    }

    private static void sendConfirmDataToUserOrderDatabase() {

        try{
            firebaseFirestore.collection("Users").document(PendingOrderAdapter.userId)
                    .collection("Orders").document(PendingOrderAdapter.custOrderDocID)
                    .update("order_status","success").addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        PendingOrderAdapter.bottomSheetDialog.dismiss();
                        getOrdersFromServer("pending");
                        PendingOrdersFragment.pendingOrderAdapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.mContext, "Order Confirmed!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(mContext, "OOPS! Error Occured.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } catch (Exception e){
            Log.d(TAG, e.getMessage());
            Toast.makeText(mContext, "OOPS! Error Occured.", Toast.LENGTH_SHORT).show();
        }
    }
}
