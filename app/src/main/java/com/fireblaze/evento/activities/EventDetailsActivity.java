package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityEventDetailsBinding;
import com.fireblaze.evento.models.Event;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetailsActivity extends BaseActivity {

    public static final String EVENT_ID_KEYWORD = "EVENT_ID";
    public static final String ORGANIZER_ID_KEYWORD = "ORGANIZER_ID";
    private DatabaseReference mDatabase;
    private Event myEvent;
    private Toolbar toolbar;
    ActivityEventDetailsBinding binding;


    public static void navigate(@NonNull Context activity, @NonNull String eventID, @NonNull String organizerID){
        Intent i = new Intent(activity,EventDetailsActivity.class);
        i.putExtra(EVENT_ID_KEYWORD,eventID);
        i.putExtra(ORGANIZER_ID_KEYWORD,organizerID);
        activity.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_event_details);
        getViews();

        final String eventID = getIntent().getStringExtra(EVENT_ID_KEYWORD);
        final String organizerID = getIntent().getStringExtra(ORGANIZER_ID_KEYWORD);
        showProgressDialog();
        mDatabase.child(Constants.EVENTS_KEYWORD).child(organizerID).child(eventID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        myEvent = dataSnapshot.getValue(Event.class);
                        setupView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }


    private void setupView(){
        binding.content.textName.setText(myEvent.getName());
        binding.content.textBookingCount.setText(String.valueOf(myEvent.getBookingsCount()));
        binding.content.textEventDetails.setText(myEvent.getDescription());

        toolbar.setTitle(myEvent.getName());
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String bookedStatus;
                if(myEvent.getBookings().containsKey(getUid())){
                    bookedStatus = "Event Unbooked!";
                } else {
                    bookedStatus = "Event Booked!";
                }
                myEvent.book(getUid());
                Snackbar.make(view, bookedStatus, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        hideProgressDialog();
    }

    private void getViews(){
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }


        mDatabase = FirebaseDatabase.getInstance().getReference();
//        mBookedEventsDatabase = mDatabase.child(Constants.BOOKED_EVENTS);



    }
    @Override
    public View getContainer() {
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
