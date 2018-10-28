package com.fireblaze.foodiee.adapters;

import android.content.Context;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.UserOperations;
import com.fireblaze.foodiee.models.FoodItem;
import com.fireblaze.foodiee.viewholders.EventViewHolder;
import com.google.firebase.database.Query;

/**
 * Created by fireblaze on 9/12/16.
 */

public class EventListFragmentAdapter extends FirebaseRecyclerAdapter<FoodItem, EventViewHolder>  {
    Context mContext;


    @Override
    protected void populateViewHolder(EventViewHolder viewHolder, final FoodItem model, int position) {
        if(model.getOrganizerID().equals(UserOperations.getUid())){
            viewHolder.setOrganizer(true);
        }


        viewHolder.bindToPost(mContext, model, false);
    }

    public EventListFragmentAdapter(Class<FoodItem> modelClass, int modelLayout, Class<EventViewHolder> viewHolderClass, Query ref, Context c) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mContext = c;
    }

}
