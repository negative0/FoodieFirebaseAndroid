package com.fireblaze.evento.activities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Toast;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.OrganizerGalleryAdapter;
import com.fireblaze.evento.databinding.ActivityOrganizerDetailsBinding;
import com.fireblaze.evento.models.Organizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class OrganizerDetailsActivity extends BaseActivity implements View.OnClickListener{

    ActivityOrganizerDetailsBinding binding;
    DatabaseReference mDatabase;

    public static final String ORGANIZER_ID = "organizerID";
    private String organizerID;

    @Override
    public View getContainer() {
        return binding.getRoot();
    }

    public static void navigate(Context context, String organizerID){
        Intent i = new Intent(context,OrganizerDetailsActivity.class);
        i.putExtra(ORGANIZER_ID,organizerID);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_organizer_details);

        setSupportActionBar(binding.toolbar);
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




    }
    private void setupView(Organizer o){
        organizerID = o.getOrganizerID();
        binding.content.textTitle.setText(o.getName());
        binding.content.textEmail.setText(o.getEmail());
        binding.content.contact.setText(o.getPhone());
        binding.content.website.setText(o.getWebsite());
        binding.content.textBookmarkCount.setText(String.valueOf(o.getBookmarkCount()));
        binding.content.btnBecomeVolunteer.setOnClickListener(this);
        setupImages();
        final String emailId = o.getEmail();
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMail(emailId);
            }
        });
    }

    private void sendMail(String email){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String []{email});
        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (ActivityNotFoundException e){
            Toast.makeText(OrganizerDetailsActivity.this,"There is no email client installed!",Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_become_volunteer){
            mDatabase.child(Constants.ORGANIZER_KEYWORD).child(organizerID).runTransaction(
                    new Transaction.Handler() {
                        @Override
                        public Transaction.Result doTransaction(MutableData mutableData) {
                            Organizer o = mutableData.getValue(Organizer.class);
                            if(o == null){
                                return Transaction.success(mutableData);
                            }
                            if(o.getVolunteers().containsKey(getUid())){
                                Snackbar.make(getContainer(), "Consider volunteering next time!",Snackbar.LENGTH_SHORT).show();
                            } else {
                                Snackbar.make(getContainer(),"You are now a volunteer", Snackbar.LENGTH_SHORT).show();
                            }
                            o.becomeVolunteer(getUid());
                            mutableData.setValue(o);
                            return Transaction.success(mutableData);
                        }

                        @Override
                        public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                        }
                    }
            );
        }
    }

}
