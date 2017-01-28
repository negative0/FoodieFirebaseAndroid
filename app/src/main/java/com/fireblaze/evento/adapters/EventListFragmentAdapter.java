package com.fireblaze.evento.adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.UserOperations;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.Query;

/**
 * Created by fireblaze on 9/12/16.
 */

public class EventListFragmentAdapter extends FirebaseRecyclerAdapter<Event, EventViewHolder>  {
    Context mContext;


    @Override
    protected void populateViewHolder(EventViewHolder viewHolder, final Event model, int position) {
        if(model.getOrganizerID().equals(UserOperations.getUid())){
            viewHolder.setOrganizer(true);
        }
        viewHolder.bindToPost(mContext, model);
    }

    public EventListFragmentAdapter(Class<Event> modelClass, int modelLayout, Class<EventViewHolder> viewHolderClass, Query ref,Context c) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mContext = c;
    }

}
