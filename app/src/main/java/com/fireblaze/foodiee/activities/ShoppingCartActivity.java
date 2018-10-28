package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.models.FoodItem;
import com.fireblaze.foodiee.models.PlacedOrder;
import com.fireblaze.foodiee.models.ShoppingCartItem;
import com.fireblaze.foodiee.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ShoppingCartActivity extends BaseActivity {

    RecyclerView itemList;
    Button checkOutButton;
    TextView totalPrice;

    FirebaseRecyclerAdapter<ShoppingCartItem, MyViewHolder> recyclerAdapter;

    DatabaseReference mDatabase;
//    List<ShoppingCartItem> cartItems;

    public static void navigate(Context context) {

        Intent intent = new Intent(context, ShoppingCartActivity.class);
        context.startActivity(intent);
    }

    void getViews() {
        itemList = findViewById(R.id.recycler);
        checkOutButton = findViewById(R.id.checkout);
        totalPrice = findViewById(R.id.totalPrice);
    }

    void setupItemList() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        itemList.setLayoutManager(manager);
        Query query = mDatabase.child("cart").child(getUid());

        recyclerAdapter = new FirebaseRecyclerAdapter<ShoppingCartItem, MyViewHolder>(ShoppingCartItem.class, R.layout.cart_item,
                MyViewHolder.class, query) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, ShoppingCartItem model, int position) {

                FoodItem foodItem = model.getFoodItem();

                viewHolder.title.setText(foodItem.getName());
                viewHolder.subtitle.setText(String.format(Locale.ENGLISH, "%s %d Nos.", foodItem.getCostPrice(), model.getQuantity()));
            }


        };
        itemList.setAdapter(recyclerAdapter);
    }

    void getTotalPrice(){
        mDatabase.child("cart").child(getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        float price = 0;
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){

                            ShoppingCartItem item = snapshot.getValue(ShoppingCartItem.class);
                            price += (item.getQuantity() * item.getFoodItem().getCostPrice());

                        }

                        totalPrice.setText("Rs. "+price);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        getViews();
//        setupCart();
        setupItemList();
        getTotalPrice();

        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ShoppingCartActivity.this, CheckoutActivity.class));
            }
        });

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, subtitle;// init the item view's

        public MyViewHolder(View itemView) {
            super(itemView);
            // get the reference of item view's
            title = itemView.findViewById(R.id.text_title);
            subtitle = itemView.findViewById(R.id.text_subtitle);
        }

    }

    @Override
    public View getContainer() {
        return findViewById(R.id.view_container);
    }
}
