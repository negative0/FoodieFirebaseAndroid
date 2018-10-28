package com.fireblaze.foodiee.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.foodiee.R;


/**
 * Created by fireblaze on 7/12/16.
 */

public class RestaurantTasksGridAdapter extends BaseAdapter {

    private Context mContext;
    @Override
    public int getCount() {
        return mThumbs.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public RestaurantTasksGridAdapter(Context c){
        mContext = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)  mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_organizer_task,null);
        } else {
            view = convertView;
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        TextView title = (TextView) view.findViewById(R.id.item_title);
        imageView.setImageResource(mThumbs[position]);
        title.setText(names[position]);
        return view;
    }

    private Integer[] mThumbs ={
            R.drawable.plus_icon,
            R.drawable.database_icon,
            R.drawable.money_icon,
            R.drawable.analytics_icon

    };
    private String[] names ={
            "New FoodItem",
            "View My page",
            "Show Orders",
            "Analytics"
    };

}
