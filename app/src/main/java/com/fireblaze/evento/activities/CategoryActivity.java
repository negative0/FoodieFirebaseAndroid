package com.fireblaze.evento.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoriesRecycler;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        Query query;
        query = mDatabase.child(Constants.EVENTS_KEYWORD);

        mAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class,
                R.layout.event_card, EventViewHolder.class,  query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindToPost(CategoryActivity.this, model);
            }

        };

        categoriesRecycler = (RecyclerView) findViewById(R.id.category_recycler);
    }

}
