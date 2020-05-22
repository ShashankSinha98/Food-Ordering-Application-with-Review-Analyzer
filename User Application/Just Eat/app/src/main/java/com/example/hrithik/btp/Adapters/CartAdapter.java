package com.example.hrithik.btp.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.hrithik.btp.Activity.FoodCartActivity;
import com.example.hrithik.btp.Activity.FoodMenuActivity;
import com.example.hrithik.btp.Helper.FoodCart;
import com.example.hrithik.btp.Model.FoodCartQtyHelper;
import com.example.hrithik.btp.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder>{

    private Context mContext;
    private ArrayList<FoodCartQtyHelper> mCartList;
    private Activity mActivity;

    public CartAdapter(Context mContext, Activity mActivity,  ArrayList<FoodCartQtyHelper> mCartList) {
        this.mContext = mContext;
        this.mCartList = mCartList;
        this.mActivity = mActivity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.single_row_food_cart_layout, viewGroup, false);
        return new CartAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        holder.cartFoodName.setText(mCartList.get(position).getFood().getFoodName());
        holder.cartFoodDesc.setText(mCartList.get(position).getFood().getFoodDesc());
        holder.cartFoodPrice.setText("₹ "+mCartList.get(position).getFood().getFoodPrice());
        holder.cartFoodQty.setText(String.valueOf(mCartList.get(position).getQuantity()));
        final RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.image_placeholder);
        Glide.with(mContext).applyDefaultRequestOptions(requestOptions).load(mCartList.get(position).getFood().getFoodImage()).into(holder.cartFoodImage);

        int singleItemTotal = Integer.valueOf(mCartList.get(position).getFood().getFoodPrice()) * mCartList.get(position).getQuantity();
        holder.cartFoodTotal.setText("₹ "+singleItemTotal);

        holder.cartfoodClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodCart cart = FoodCart.getInstance();
                cart.foodCartList.remove(position);
                notifyDataSetChanged();
                FoodCartActivity.updateCartDetails();
                FoodMenuActivity.updateCartItemCountIcon();
            }
        });

    }

    @Override
    public int getItemCount() {
        return mCartList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView cartFoodName, cartFoodQty, cartFoodDesc, cartFoodPrice, cartFoodTotal, cartfoodClose;
        private CircleImageView cartFoodImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            cartFoodName = itemView.findViewById(R.id.cart_food_name_tv);
            cartFoodQty = itemView.findViewById(R.id.cart_food_count_tv);
            cartFoodDesc = itemView.findViewById(R.id.cart_food_desc_tv);
            cartFoodPrice = itemView.findViewById(R.id.cart_food_single_price_tv);
            cartFoodTotal = itemView.findViewById(R.id.single_item_total_cost_tv);
            cartfoodClose = itemView.findViewById(R.id.remove_food_cart_tv);
            cartFoodImage = itemView.findViewById(R.id.food_cart_iv);
        }
    }
}
