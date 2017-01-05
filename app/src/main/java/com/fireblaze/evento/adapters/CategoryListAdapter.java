package com.fireblaze.evento.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.evento.R;
import com.fireblaze.evento.activities.CategoryActivity;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    private int[] imageIDs;
    private String[]  items;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title;
        ImageView image;
        View item;
        MyViewHolder(View v){
            super(v);
            item = v;
            txt_title = (TextView) v.findViewById(R.id.category_item_title);
            image = (ImageView) v.findViewById(R.id.category_item_image);
        }
    }

    public CategoryListAdapter(@NonNull Context context,@NonNull String[] items, int[] imageIDs) {
        this.items = items;
        this.context = context;
        this.imageIDs = imageIDs;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.category_item,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {

        holder.txt_title.setText(items[position]);
        holder.image.setImageResource(imageIDs[position]);
        holder.item.setId(position);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryActivity.navigate(context,items[view.getId()]);
            }
        });


    }

    @Override
    public int getItemCount() {
        return items.length;
    }
}
