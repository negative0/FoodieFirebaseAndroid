package com.fireblaze.evento.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.evento.R;

import java.util.List;


public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {
    int[] imageIDs;
    List<String> items;
    Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_title;
        ImageView image;
        MyViewHolder(View v){
            super(v);
            txt_title = (TextView) v.findViewById(R.id.category_item_title);
            image = (ImageView) v.findViewById(R.id.category_item_image);
        }
    }

    public CategoryListAdapter(Context context, List<String> items,int[] imageIDs) {
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


        holder.txt_title.setText(items.get(position));
        holder.image.setImageResource(imageIDs[position]);


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
