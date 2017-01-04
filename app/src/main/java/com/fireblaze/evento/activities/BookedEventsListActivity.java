package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.UserOperations;
import com.fireblaze.evento.databinding.ActivityBookedEventsListBinding;
import com.fireblaze.evento.models.BookedEvent;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.BookedEventViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class BookedEventsListActivity extends AppCompatActivity {
    private ActivityBookedEventsListBinding binding;
    private FirebaseRecyclerAdapter<BookedEvent,BookedEventViewHolder> mAdapter;
    private DatabaseReference mDatabase;

    public static void navigate(Context context){
        context.startActivity(new Intent(context,BookedEventsListActivity.class));

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_booked_events_list);
        getSupportActionBar().setTitle("Booked Events");
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Query q = mDatabase.child(Constants.BOOKED_EVENTS).orderByChild("userID").equalTo(UserOperations.getUid());
        mAdapter = new FirebaseRecyclerAdapter<BookedEvent, BookedEventViewHolder>(BookedEvent.class,
                R.layout.booked_event_item,BookedEventViewHolder.class, q) {
            @Override
            protected void populateViewHolder(final BookedEventViewHolder viewHolder, final BookedEvent model, int position) {
                mDatabase.child(Constants.EVENTS_KEYWORD).child(model.getOrganizerID()).child(model.getEventID())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Event event = dataSnapshot.getValue(Event.class);
                                viewHolder.bindToPost(BookedEventsListActivity.this,model,event);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

            }
        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(mAdapter);

    }
}
