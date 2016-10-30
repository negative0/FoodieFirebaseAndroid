package com.fireblaze.evento.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class EventFragment extends Fragment {
    private DatabaseReference mDatabase;
    private RecyclerView mEventsRecycler;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> mFirebaseAdapter;
    private String UID;

    public EventFragment() {

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(getArguments()!= null){
            UID = getArguments().getString("UID");
        } else
            UID = "orgID1";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_test,container,false);
        mEventsRecycler = (RecyclerView) rootView.findViewById(R.id.recycler_events);
        Query query = mDatabase.child(Constants.EVENTS_KEYWORD).child(UID).limitToFirst(10);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class,R.layout.event_card,
                EventViewHolder.class,query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindToPost(getContext(), model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(getContext(),"Event clicked",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        LinearLayoutManager verticalLayoutManager =
                new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        mEventsRecycler.setLayoutManager(verticalLayoutManager);
        mEventsRecycler.setAdapter(mFirebaseAdapter);
        return rootView;
    }
}
