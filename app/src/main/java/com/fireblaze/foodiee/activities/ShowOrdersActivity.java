package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.models.MyLocation;
import com.fireblaze.foodiee.models.PlacedOrder;
import com.fireblaze.foodiee.models.ShoppingCartItem;
import com.fireblaze.foodiee.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class ShowOrdersActivity extends BaseActivity implements View.OnClickListener {



    String restaurant_id;
    //    List<PlacedOrder> orderList = new ArrayList<>();
    RecyclerView itemList;

    DatabaseReference mDatabase;
    FirebaseRecyclerAdapter<PlacedOrder, MyViewHolder> recyclerAdapter;

    public static void navigate(Context mContext){
        Intent intent = new Intent(mContext, ShowOrdersActivity.class);
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
        Query query = mDatabase.child("orders").child(getUid());

        recyclerAdapter = new FirebaseRecyclerAdapter<PlacedOrder, MyViewHolder>(PlacedOrder.class, R.layout.restaurant_placed_order_item,
                MyViewHolder.class, query) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, PlacedOrder model, int position) {
                viewHolder.btnContainer.setTag(model);
                viewHolder.title.setText(model.getUser().getName());
                float totalAmount = 0;
                for(ShoppingCartItem item : model.getItems()){
                    totalAmount += item.getQuantity() * item.getFoodItem().getCostPrice();
                }
                viewHolder.subtitle.setText("Rs: "+totalAmount);
//                viewHolder.subtitle.setText(String.format(Locale.ENGLISH, "%s %d Nos.", foodItem.getCostPrice(), model.getQuantity()));
                viewHolder.btnShowOnMap.setOnClickListener(ShowOrdersActivity.this);
                viewHolder.btnCall.setOnClickListener(ShowOrdersActivity.this);
                viewHolder.btnDone.setOnClickListener(ShowOrdersActivity.this);
            }
        };
        itemList.setAdapter(recyclerAdapter);
    }
    @Override
    public void onClick(View v) {
        PlacedOrder order = (PlacedOrder) ((View)v.getParent()).getTag();
        User user = order.getUser();
        switch (v.getId()){
            case R.id.btn_show_on_map:

                Intent intent = new Intent(this, MapsActivity.class);
                MyLocation myLocation = order.getUser().getMyLocation();
                if(myLocation != null) {
                    intent.putExtra("latitude", myLocation.latitude);
                    intent.putExtra("longitude", myLocation.longitude);
                    intent.putExtra("name", user.getName());
                    startActivity(intent);
                }else {
                    Toast.makeText(this, "Location not provided", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_call:
                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:"+user.getPhone()));
                startActivity(i);
                break;
            case R.id.btn_done:
                mDatabase.child("orders").child(getUid()).child(order.getUser().getUserID())
                        .setValue(null);

                mDatabase.child("myOrders").child(order.getUser().getUserID()).child(getUid())
                        .setValue(null);

        }
    }

    @Override
    public View getContainer() {
        return findViewById(R.id.view_container);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;// init the item view's
        ImageView btnShowOnMap, btnDone, btnCall;
        View btnContainer;
        public MyViewHolder( View itemView) {
            super(itemView);
            // get the reference of item view's
            title = itemView.findViewById(R.id.text_title);
            subtitle = itemView.findViewById(R.id.text_subtitle);
            btnShowOnMap = itemView.findViewById(R.id.btn_show_on_map);
            btnDone = itemView.findViewById(R.id.btn_done);
            btnCall = itemView.findViewById(R.id.btn_call);
            btnContainer = itemView.findViewById(R.id.btn_container);
        }


    }
}
