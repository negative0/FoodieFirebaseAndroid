package com.fireblaze.foodiee.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fireblaze.foodiee.R;


public class RestaurantGalleryAdapter extends RecyclerView.Adapter<RestaurantGalleryAdapter.MyViewHolder> {

    private String items[];
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        MyViewHolder(View v){
            super(v);
            image = (ImageView) v.findViewById(R.id.item_image);
        }
    }

    public RestaurantGalleryAdapter(@NonNull Context context, @NonNull String[] items) {
        this.items = items;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.image_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        Glide.with(context).load(items[position])
                .thumbnail(R.drawable.logo_black)
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
