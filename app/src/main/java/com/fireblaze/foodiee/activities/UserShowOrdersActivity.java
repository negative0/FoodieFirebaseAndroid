package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.models.PlacedOrder;
import com.fireblaze.foodiee.models.Restaurant;
import com.fireblaze.foodiee.models.ShoppingCartItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserShowOrdersActivity extends BaseActivity {

    String restaurant_id;
    //    List<PlacedOrder> orderList = new ArrayList<>();
    RecyclerView itemList;

    DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<PlacedOrder, MyViewHolder> recyclerAdapter;

    public static void navigate(Context mContext){
        Intent intent = new Intent(mContext, UserShowOrdersActivity.class);
        mContext.startActivity(intent);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_orders);

        itemList = findViewById(R.id.recycler);
        restaurant_id = getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        setupItemList();
    }

    void setupItemList() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemList.setLayoutManager(manager);
        Query query = mDatabase.child("myOrders").child(getUid());

        recyclerAdapter = new FirebaseRecyclerAdapter<PlacedOrder, MyViewHolder>(PlacedOrder.class, R.layout.user_placed_order_item,
                MyViewHolder.class, query) {
            @Override
            protected void populateViewHolder(final MyViewHolder viewHolder, PlacedOrder model, int position) {
                float total = 0;
                for(ShoppingCartItem item: model.getItems()){
                    total += item.getQuantity() * item.getFoodItem().getCostPrice();
                }
                final float totalAmount = total;
                mDatabase.child(Constants.RESTAURANT_KEYWORD).child(model.getRestaurant_id())
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                                if(restaurant != null){

                                    viewHolder.title.setText(restaurant.getName());
                                    viewHolder.subtitle.setText( "Rs:" + totalAmount);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
//                viewHolder.subtitle.setText(String.format(Locale.ENGLISH, "%s %d Nos.", foodItem.getCostPrice(), model.getQuantity()));

            }
        };
        itemList.setAdapter(recyclerAdapter);
    }


    @Override
    public View getContainer() {
        return findViewById(R.id.view_container);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;// init the item view's
        //        ImageView btnShowOnMap, btnDone, btnCall;
//        View btnContainer;
        public MyViewHolder( View itemView) {
            super(itemView);
            // get the reference of item view's
            title = itemView.findViewById(R.id.text_title);
            subtitle = itemView.findViewById(R.id.text_subtitle);

        }


    }
}
