package com.fireblaze.evento;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fireblaze.evento.models.Event;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EventDetailsActivity extends BaseActivity {

    public static final String EVENT_ID_KEYWORD = "EVENT_ID";
    private DatabaseReference mDatabase;

    public static void navigate(Context activity, String eventID){
        Intent i = new Intent(activity,EventDetailsActivity.class);
        i.putExtra(EVENT_ID_KEYWORD,eventID);
        activity.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.BOOKED_EVENTS);
        final String eventID = getIntent().getStringExtra(EVENT_ID_KEYWORD);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Event.book(eventID,getUid());
                Snackbar.make(view, getIntent().getStringExtra(EVENT_ID_KEYWORD), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public View getContainer() {
        return null;
    }
}
