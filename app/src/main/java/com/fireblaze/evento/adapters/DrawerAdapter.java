package com.fireblaze.evento.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fireblaze.evento.R;
import com.fireblaze.evento.models.DrawerItem;

import java.util.List;

/**
 * Created by chait on 8/27/2016.
 */

public class DrawerAdapter extends ArrayAdapter<DrawerItem>{
    Context context;
    List<DrawerItem> items;
    int layoutResID;


    public DrawerAdapter(Context context,int layoutResID, List<DrawerItem> items){
        super(context,layoutResID,items);
        this.context = context;
        this.items = items;
        this.layoutResID = layoutResID;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        DrawerItemHolder drawerItemHolder;
        View view = convertView;
        if(view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            drawerItemHolder = new DrawerItemHolder();
            view = inflater.inflate(layoutResID, parent, false);
            drawerItemHolder.itemName = (TextView) view.findViewById(R.id.drawer_item_title);
            drawerItemHolder.icon = (ImageView) view.findViewById(R.id.drawer_item_icon);
            view.setTag(drawerItemHolder);

        } else {
            drawerItemHolder = (DrawerItemHolder) view.getTag();
        }
        DrawerItem d = (DrawerItem) this.items.get(position);
        drawerItemHolder.icon.setImageDrawable(view.getResources().getDrawable(d.getImgResID()));
        drawerItemHolder.itemName.setText(d.getItemName());
        return view;
    }
    private static class DrawerItemHolder{
        TextView itemName;
        ImageView icon;
    }
}
