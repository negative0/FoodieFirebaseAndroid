package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;

import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.AttendeesListAdapter;
import com.fireblaze.evento.databinding.ActivityEventAttendeesListBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;
import java.util.Set;

public class EventAttendeesListActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private ActivityEventAttendeesListBinding binding;


    public static void navigate(Context context, Map<String, String> users){
        if(users == null){
            return;
        }
        Intent i = new Intent(context,EventAttendeesListActivity.class);
        Set<String> keys = users.keySet();
        String[] items = keys.toArray(new String[keys.size()]);
        i.putExtra("items",items);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_event_attendees_list);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Event Attendees");
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        String[] items = getIntent().getStringArrayExtra("items");
        AttendeesListAdapter mAdapter = new AttendeesListAdapter(this,items);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.recycler.setAdapter(mAdapter);
        binding.recycler.setLayoutManager(layoutManager);
    }
}
