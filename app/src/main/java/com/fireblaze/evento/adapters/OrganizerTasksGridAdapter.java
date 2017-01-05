package com.fireblaze.evento.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.evento.R;


/**
 * Created by fireblaze on 7/12/16.
 */

public class OrganizerTasksGridAdapter extends BaseAdapter {

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

    public OrganizerTasksGridAdapter(Context c){
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
            "New Event",
            "View My page",
            "Send Notifications",
            "Analytics"
    };

}
