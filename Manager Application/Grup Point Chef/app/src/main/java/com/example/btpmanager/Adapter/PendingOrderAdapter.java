package com.example.btpmanager.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.btpmanager.Activity.MainActivity;
import com.example.btpmanager.Fragment.PendingOrdersFragment;
import com.example.btpmanager.Helper.BottomSheetDialog;
import com.example.btpmanager.Helper.FoodList;
import com.example.btpmanager.Helper.FoodOrders;
import com.example.btpmanager.Model.FoodQty;
import com.example.btpmanager.Model.Order;
import com.example.btpmanager.R;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.ViewHolder> {

    private static final String TAG = "PendingOrderAdapter";
    public static Context mContext;
    private ArrayList<Order> mOrderList;
    public static int clickPos = -1;
    public static String userId=null;
    public static String selectedOrderTimestamp=null;
    public static BottomSheetDialog bottomSheetDialog;
    public static String custOrderDocID = null;


    public PendingOrderAdapter(Context mContext, ArrayList<Order> mOrderList) {
        this.mContext = mContext;
        this.mOrderList = mOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_pending_order_list, viewGroup, false);
        return new PendingOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_img);
        Log.d("xlr8","Image: "+mOrderList.get(position).getCustomerImage());
        Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mOrderList.get(position).getCustomerImage()).into(holder.custImage);

        holder.custName.setText(mOrderList.get(position).getCustomerName());
        holder.custNumber.setText(mOrderList.get(position).getCustomerNumber());
        holder.orderNo.setText("#"+(position+1));
        final String orderTimestamp = mOrderList.get(position).getTimestamp();
        //String[] timestamp = orderTimestamp.split(" ");
        //holder.orderDate.setText(timestamp[0]);
        //holder.orderTime.setText(timestamp[1]);
        Timestamp timestamp = new Timestamp(Long.valueOf(orderTimestamp));
        Log.d(TAG, "TS: "+timestamp);
        String[] timestampArr = String.valueOf(timestamp).split(" ");
        holder.orderDate.setText(timestampArr[0]);
        holder.orderTime.setText(timestampArr[1]);

        if(!mOrderList.get(position).getOrderStatus().equals("success")) {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#ffcaf8"));
        } else {
            holder.constraintLayout.setBackgroundColor(Color.parseColor("#cafff7"));
        }


        holder.orderPrice.setText("₹"+mOrderList.get(position).getAmountPaid());


        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!mOrderList.get(position).getOrderStatus().equals("success")) {
                    bottomSheetDialog = new BottomSheetDialog();
                    FoodList foodList = FoodList.getInstance();
                    foodList.bottomFoodList = mOrderList.get(position).getOrdersList();
                    bottomSheetDialog.show(((MainActivity) mContext).getSupportFragmentManager(), "bottom_nav");
                    clickPos = position;
                    userId = mOrderList.get(position).getCustomerID();
                    selectedOrderTimestamp = mOrderList.get(position).getTimestamp();
                    custOrderDocID = mOrderList.get(position).getCustOrderDocID();
                }
                //BottomSheetDialog.populateBottomDialog(mContext, foodOrders);

            }
        });



    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView custImage;
        private ConstraintLayout constraintLayout;
        private TextView custName, custNumber, orderDate, orderTime, orderPrice, orderNo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.constraintLayout = itemView.findViewById(R.id.food_order_layout);
            this.custImage = itemView.findViewById(R.id.cust_img_cv);
            this.custName = itemView.findViewById(R.id.cust_name);
            this.custNumber = itemView.findViewById(R.id.cust_number);
            this.orderDate = itemView.findViewById(R.id.order_date);
            this.orderNo = itemView.findViewById(R.id.order_no);
            this.orderPrice = itemView.findViewById(R.id.order_price);
            this.orderTime = itemView.findViewById(R.id.order_time);
        }


    }
}
