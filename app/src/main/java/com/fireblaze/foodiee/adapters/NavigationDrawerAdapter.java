package com.fireblaze.foodiee.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.fireblaze.foodiee.R;

/**
 * Created by fireblaze on 18/10/16.
 */

public class NavigationDrawerAdapter {

    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;

        public MyViewHolder(View item){
            super(item);
            title = (TextView) item.findViewById(R.id.title);
        }

    }


}
