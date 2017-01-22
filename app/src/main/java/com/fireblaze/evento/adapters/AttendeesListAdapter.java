package com.fireblaze.evento.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.User;
import com.fireblaze.evento.viewholders.UserViewHolder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by fireblaze on 22/1/17.
 */

public class AttendeesListAdapter extends RecyclerView.Adapter<UserViewHolder> {

    private static DatabaseReference mDatabase;

    private String[] keys;
    private Context context;

    public AttendeesListAdapter(@NonNull Context context, String[] items) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        keys = items;
        this.context = context;

    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewItem = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_booked_event_item,parent,false);
        return new UserViewHolder(viewItem);
    }

    @Override
    public void onBindViewHolder(final UserViewHolder holder, int position) {
        mDatabase.child(Constants.USERS_KEYWORD).child(keys[position])
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if(user != null){
                            holder.bindToPost(context,user);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public int getItemCount() {
        return keys.length;
    }
}
