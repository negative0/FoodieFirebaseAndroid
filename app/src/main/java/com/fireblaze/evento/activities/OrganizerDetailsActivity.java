package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.OrganizerGalleryAdapter;
import com.fireblaze.evento.databinding.ActivityOrganizerDetailsBinding;
import com.fireblaze.evento.models.Organizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OrganizerDetailsActivity extends AppCompatActivity {

    ActivityOrganizerDetailsBinding binding;
    DatabaseReference mDatabase;

    public static final String ORGANIZER_ID = "organizerID";



    public static void navigate(Context context, String organizerID){
        Intent i = new Intent(context,OrganizerDetailsActivity.class);
        i.putExtra(ORGANIZER_ID,organizerID);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_organizer_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Bundle b = getIntent().getExtras();
        final String organizerID;
        if(b== null){
            throw new RuntimeException("bundle is null");
        }
        if((organizerID = b.getString(ORGANIZER_ID))== null){
            throw new RuntimeException("Pass organizerID in intent");
        }

        mDatabase.child(Constants.ORGANIZER_KEYWORD).child(organizerID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Organizer organizer = dataSnapshot.getValue(Organizer.class);
                        setupView(organizer);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
    private void setupView(Organizer o){
        binding.content.textTitle.setText(o.getName());
        binding.content.textEmail.setText(o.getEmail());
        binding.content.textBookmarkCount.setText(String.valueOf(o.getBookmarkCount()));
        setupImages();
    }
    private void setupImages(){
        String [] images = {
                "http://placekitten.com/300/400",
                "http://placekitten.com/300/400",
                "http://placekitten.com/300/500"
        };
        OrganizerGalleryAdapter adapter = new OrganizerGalleryAdapter(this,images);
        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.content.imagesRecycler.setLayoutManager(manager);
        binding.content.imagesRecycler.setAdapter(adapter);

    }

}
