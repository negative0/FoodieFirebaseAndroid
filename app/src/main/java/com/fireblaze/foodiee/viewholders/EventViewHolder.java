package com.fireblaze.foodiee.viewholders;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.activities.ItemsListActivity;
import com.fireblaze.foodiee.activities.NewFoodItemActivity;
import com.fireblaze.foodiee.models.FoodItem;
import com.fireblaze.foodiee.models.ShoppingCartItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by chait on 8/27/2016.
 */

public class EventViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
        MenuItem.OnMenuItemClickListener
{
    public static final String TAG = EventViewHolder.class.getSimpleName();
    private TextView title;
    private TextView subtitle;
    private ImageView imageView;
    private ElegantNumberButton quantity;
    private View itemView;
    private boolean isOrganizer;
    private FoodItem myFoodItem;
    private Context mContext;
    private DatabaseReference mDatabase;
    public EventViewHolder(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.event_image);
        title = itemView.findViewById(R.id.text_title);
        subtitle = itemView.findViewById(R.id.text_subtitle);
        this.itemView = itemView;
        quantity = itemView.findViewById(R.id.quantity);
        isOrganizer = false;
        mDatabase = FirebaseDatabase.getInstance().getReference().child("cart").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void bindToPost(final Context context, final FoodItem foodItem, boolean hideQuantity){
        title.setText(foodItem.getName());
        Glide.with(context).load(foodItem.getImage()).into(imageView);
//        String subtitleString = foodItem.getScheduleString();
//        subtitle.setText(subtitleString);
        if(hideQuantity){
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ItemsListActivity.navigate(context, foodItem.getOrganizerID());
                }
            });
            hideQuantity();
        }else {
            quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
                @Override
                public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                    ShoppingCartItem item = new ShoppingCartItem(foodItem, newValue);
                    mDatabase.child(foodItem.getEventID()).setValue(item);
                }
            });
        }
//        itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ItemsListActivity.navigate();
//            }
//        });

        subtitle.setText("Rs. " + foodItem.getCostPrice());

        itemView.setTag(foodItem.getEventID());
        if(isOrganizer){
            itemView.setOnCreateContextMenuListener(this);
        }
        myFoodItem = foodItem;
        mContext = context;



    }

    public void hideQuantity(){
        quantity.setVisibility(View.INVISIBLE);
    }

    public void setOrganizer(boolean organizer) {
        isOrganizer = organizer;
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select an action");
        contextMenu.add(0,0,0,"Edit");
        contextMenu.add(1,1,1,"Delete");

        contextMenu.getItem(0).setOnMenuItemClickListener(this);
        contextMenu.getItem(1).setOnMenuItemClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case 0:
                NewFoodItemActivity.navigate(mContext, myFoodItem.getEventID());
                break;
            case 1:
                FoodItem.deleteEvent(myFoodItem.getEventID());
                break;


        }
        return false;
    }
}
