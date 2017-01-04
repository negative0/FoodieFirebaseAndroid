package com.fireblaze.evento.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

/**
 * Created by fireblaze on 3/1/17.
 */

public class CategoryRecyclerAdapter extends RecyclerView.Adapter<EventViewHolder> {
    DatabaseReference mDatabase;
    public CategoryRecyclerAdapter(Query query){
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


    @Override
    public int getItemCount() {
        return 0;
    }

    @Override
    public void onBindViewHolder(EventViewHolder holder, int position) {

    }
}
