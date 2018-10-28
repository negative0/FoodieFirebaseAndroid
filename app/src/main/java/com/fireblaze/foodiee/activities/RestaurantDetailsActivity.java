package com.fireblaze.foodiee.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.adapters.RestaurantGalleryAdapter;
import com.fireblaze.foodiee.databinding.ActivityRestaurantDetailsBinding;
import com.fireblaze.foodiee.models.Restaurant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RestaurantDetailsActivity extends BaseActivity implements View.OnClickListener{

    private ActivityRestaurantDetailsBinding binding;
    private DatabaseReference mDatabase;
    private Restaurant mRestaurant;

    public static final String ORGANIZER_ID = "organizerID";
    private String organizerID;

    @Override
    public View getContainer() {
        return binding.getRoot();
    }

    public static void navigate(Context context, String organizerID){
        Intent i = new Intent(context,RestaurantDetailsActivity.class);
        i.putExtra(ORGANIZER_ID,organizerID);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_restaurant_details);

        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar() != null){
            binding.toolbar.setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle b = getIntent().getExtras();
        final String organizerID;

        if(b== null){
            throw new RuntimeException("bundle is null");
        }
        if((organizerID = b.getString(ORGANIZER_ID))== null){
            throw new RuntimeException("Pass organizerID in intent");
        }

        mDatabase.child(Constants.RESTAURANT_KEYWORD).child(organizerID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Restaurant restaurant = dataSnapshot.getValue(Restaurant.class);
                        setupView(restaurant);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );




    }
    private void setupView(Restaurant o){
        showProgressDialog();
        mRestaurant = o;
        organizerID = o.getOrganizerID();
        binding.content.textTitle.setText(o.getName());
        binding.content.textEmail.setText(o.getEmail());
        binding.content.textContact.setText(o.getPhone());
        binding.content.textWebsite.setText(o.getWebsite());
        binding.content.textBookingCount.setText(String.valueOf(o.getBookmarkCount()));
//        binding.content.btnBecomeVolunteer.setOnClickListener(this);
       // TODO: setupImages();
        binding.content.textEmail.setOnClickListener(this);
        binding.fab.setOnClickListener(this);
//        updateVolunteerStatus();
        hideProgressDialog();
    }

    private void sendMail(String email){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String []{email});
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (ActivityNotFoundException e){
            Toast.makeText(RestaurantDetailsActivity.this,"There is no email client installed!",Toast.LENGTH_SHORT).show();
        }
    }
    private void setupImages(){
        String [] images = {
                "http://placekitten.com/300/400",
                "http://placekitten.com/300/400",
                "http://placekitten.com/300/400"
        };
        RestaurantGalleryAdapter adapter = new RestaurantGalleryAdapter(this,images);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.content.imagesRecycler.setLayoutManager(manager);
        binding.content.imagesRecycler.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
//
            case R.id.text_email:
                sendMail(mRestaurant.getEmail());
                break;
            case R.id.fab:
                sendMail(mRestaurant.getEmail());
                break;


        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
