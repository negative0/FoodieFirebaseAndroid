package com.fireblaze.foodiee.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.models.PlacedOrder;
import com.fireblaze.foodiee.models.ShoppingCartItem;
import com.fireblaze.foodiee.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CheckoutActivity extends BaseActivity {

    Button checkOutButton;
    DatabaseReference mDatabase;



    private void getAllViews(){
        checkOutButton = findViewById(R.id.btn_done);
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        getAllViews();
        checkOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                final PlacedOrder order = new PlacedOrder();
                mDatabase.child("cart").child(getUid())
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        final List<ShoppingCartItem> items = new ArrayList<>();
                                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                                            ShoppingCartItem currentItem = snapshot.getValue(ShoppingCartItem.class);
                                            items.add(currentItem);
                                        }
                                        order.setItems(items);

                                        mDatabase.child(Constants.USERS_KEYWORD).child(getUid()).addListenerForSingleValueEvent(
                                                new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        hideProgressDialog();
                                                        User user = dataSnapshot.getValue(User.class);
                                                        order.setUser(user);
                                                        String restaurant_id = items.get(0).getFoodItem().getOrganizerID();
                                                        order.setRestaurant_id(restaurant_id);


                                                        //Upload to restaurant database
                                                        mDatabase.child("orders").child(restaurant_id)
                                                                .child(getUid())
                                                                .setValue(order);

                                                        //Upload to myOrders database
                                                        mDatabase.child("myOrders").child(getUid())
                                                                .child(restaurant_id)
                                                                .setValue(order);


                                                        Toast.makeText(CheckoutActivity.this, "Order placed!", Toast.LENGTH_SHORT).show();


                                                        Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);

                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        hideProgressDialog();
                                                    }
                                                }
                                        );
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                }
                        );

            }
        });



    }

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }
}
