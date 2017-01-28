package com.fireblaze.evento.activities;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.databinding.ActivityAboutUsBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AboutUsActivity extends AppCompatActivity {
    ActivityAboutUsBinding binding;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_about_us);
        setSupportActionBar(binding.toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();


        getFromDatabase();

    }
    private void getFromDatabase(){
        mDatabase.child(Constants.TEAM_KEYWORD).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot != null){
                            setupView(dataSnapshot);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                }
        );
    }
    private void setupView(DataSnapshot dataSnapshot){
        String anand, shivam, shankar, chaitanya;
        anand = (String) dataSnapshot.child("anand").getValue();
        shivam = (String) dataSnapshot.child("shivam").getValue();
        shankar = (String) dataSnapshot.child("shankar").getValue();
        chaitanya = (String) dataSnapshot.child("chaitanya").getValue();
        Glide.with(this).load(anand).into(binding.content.imageAnand);
        Glide.with(this).load(shankar).into(binding.content.imageShankar);
        Glide.with(this).load(shivam).into(binding.content.imageShivam);
        Glide.with(this).load(chaitanya).into(binding.content.imageChaitanya);
    }

}
