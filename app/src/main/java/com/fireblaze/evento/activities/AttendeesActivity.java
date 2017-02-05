package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.AttendeesListAdapter;
import com.fireblaze.evento.databinding.ActivityAttendeesBinding;

import java.util.Map;
import java.util.Set;

public class AttendeesActivity extends BaseActivity {

    private ActivityAttendeesBinding binding;

    @Override
    public View getContainer() {
        return binding.getRoot();
    }

    public static void navigate(Context context, String eventID, Map<String, String> users, Map<String, Boolean> presentMap){
        if(users == null || presentMap == null){
            return;
        }
        Intent intent = new Intent(context,AttendeesActivity.class);
        Set<String> keys = users.keySet();
        boolean[] presentArray = new boolean[keys.size()];
        String[] items = keys.toArray(new String[keys.size()]);
        for(int i=0;i<items.length;i++){
            if(presentMap.containsKey(items[i])){
                presentArray[i] = presentMap.get(items[i]);
            }else {
                presentArray[i] = false;
            }
        }
        intent.putExtra(Constants.EVENT_ID,eventID);
        intent.putExtra("items",items);
        intent.putExtra("present",presentArray);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_attendees);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Event Attendees");
        }

        Bundle b = getIntent().getExtras();

        if(b == null) {
            return;
        }

        String[] items = b.getStringArray("items");
        boolean[] presentArray = b.getBooleanArray("present");
        final String eventID = b.getString(Constants.EVENT_ID);
        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRCodeScanActivity.navigate(AttendeesActivity.this,eventID);
            }
        });
        AttendeesListAdapter mAdapter = new AttendeesListAdapter(this,items,presentArray);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        binding.content.recycler.setAdapter(mAdapter);
        binding.content.recycler.setLayoutManager(layoutManager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }
}
