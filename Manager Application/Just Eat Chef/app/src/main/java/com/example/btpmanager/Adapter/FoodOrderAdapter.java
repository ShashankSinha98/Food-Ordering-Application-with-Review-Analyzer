package com.example.btpmanager.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.btpmanager.Model.FoodQty;
import com.example.btpmanager.R;

import java.util.ArrayList;

public class FoodOrderAdapter extends RecyclerView.Adapter<FoodOrderAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<FoodQty> mFoodQtyList;


    public FoodOrderAdapter(Context mContext, ArrayList<FoodQty> mFoodQtyList) {
        this.mContext = mContext;
        this.mFoodQtyList = mFoodQtyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.custom_bottom_dialog_food_list, viewGroup, false);
        return new FoodOrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.foodName.setText(mFoodQtyList.get(position).getFoodName());
        holder.foodPrice.setText("₹"+mFoodQtyList.get(position).getFoodPrice());
        holder.foodQty.setText("x"+mFoodQtyList.get(position).getFoodQty());
        int totalPrice = Integer.valueOf(mFoodQtyList.get(position).getFoodPrice()) * Integer.valueOf(mFoodQtyList.get(position).getFoodQty());
        holder.foodQtyTotalPrice.setText("₹"+totalPrice);

        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.default_img);
        Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mFoodQtyList.get(position).getFoodImage()).into(holder.foodImage);



    }

    @Override
    public int getItemCount() {
        return mFoodQtyList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView foodImage;
        private TextView foodName, foodPrice, foodQtyTotalPrice, foodQty;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            this.foodImage = itemView.findViewById(R.id.food_img);
            this.foodName = itemView.findViewById(R.id.food_name);
            this.foodPrice = itemView.findViewById(R.id.food_price);
            this.foodQtyTotalPrice = itemView.findViewById(R.id.food_qty_total_price);
            this.foodQty = itemView.findViewById(R.id.food_qty);
        }


    }
}
