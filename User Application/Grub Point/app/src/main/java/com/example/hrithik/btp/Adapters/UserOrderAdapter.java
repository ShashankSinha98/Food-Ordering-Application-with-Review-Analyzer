package com.example.hrithik.btp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrithik.btp.Activity.FoodCartActivity;
import com.example.hrithik.btp.Activity.FoodMenuActivity;
import com.example.hrithik.btp.Activity.OrderFoodCommentActivity;
import com.example.hrithik.btp.Helper.FoodCart;
import com.example.hrithik.btp.Model.FoodCartQtyHelper;
import com.example.hrithik.btp.Model.FoodQty;
import com.example.hrithik.btp.Model.Order;
import com.example.hrithik.btp.R;

import java.sql.Timestamp;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dm7.barcodescanner.core.IViewFinder;

public class UserOrderAdapter extends RecyclerView.Adapter<UserOrderAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<Order> mOrderList;

    public UserOrderAdapter(Context mContext, ArrayList<Order> mOrderList) {
        this.mContext = mContext;
        this.mOrderList = mOrderList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_order_history_single_row, viewGroup, false);
        return new UserOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        String orderTimestamp = mOrderList.get(position).getTimestamp();
        Timestamp timestamp = new Timestamp(Long.valueOf(orderTimestamp));
        holder.orderdatenTime.setText(String.valueOf(timestamp));

        holder.orderStatus.setText("#"+mOrderList.get(position).getOrderStatus());
        holder.orderPrice.setText("₹"+mOrderList.get(position).getAmountPaid());
        holder.orderNo.setText(""+(mOrderList.size()-position));

        ArrayList<FoodQty> foodQties = mOrderList.get(position).getOrdersList();
        String orderDetails = "";


        for(int i=0; i<foodQties.size(); i++){
            orderDetails += foodQties.get(i).getFoodName()+" x "+foodQties.get(i).getFoodQty()+"\n";
        }

        holder.orderDetails.setText(orderDetails);

        if(!mOrderList.get(position).getOrderStatus().equals("success")){
            holder.feedbackIcon.setVisibility(View.INVISIBLE);
            holder.feedbackTV.setVisibility(View.INVISIBLE);
        } else {
            holder.feedbackIcon.setVisibility(View.VISIBLE);
            holder.feedbackTV.setVisibility(View.VISIBLE);
        }

        holder.orderLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mOrderList.get(position).getOrderStatus().equals("success")) {
                    Intent intent = new Intent(mContext, OrderFoodCommentActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("food_list",mOrderList.get(position).getOrdersList());
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView orderNo, orderdatenTime, orderStatus, orderDetails, orderPrice, feedbackTV;
        private ConstraintLayout orderLayout;
        private ImageView feedbackIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNo = itemView.findViewById(R.id.order_no);
            orderdatenTime = itemView.findViewById(R.id.date_n_time);
            orderStatus = itemView.findViewById(R.id.order_status);
            orderDetails = itemView.findViewById(R.id.order_details);
            orderPrice = itemView.findViewById(R.id.order_price);
            orderLayout = itemView.findViewById(R.id.order_layout);
            feedbackTV = itemView.findViewById(R.id.feedback_tv);
            feedbackIcon = itemView.findViewById(R.id.review_icon);
        }
    }
}
