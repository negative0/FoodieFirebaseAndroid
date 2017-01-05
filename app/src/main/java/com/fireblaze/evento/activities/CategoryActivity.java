package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.models.Event;
import com.fireblaze.evento.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView categoriesRecycler;
    private FirebaseRecyclerAdapter<Event, EventViewHolder> mAdapter;
    private DatabaseReference mDatabase;
    private static final String CATEGORY_KEYWORD = "category";
    public static void navigate(Context context, String category){

        if(category == null || category.isEmpty()){
            Toast.makeText(context,"Error!",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent i = new Intent(context,CategoryActivity.class);
        i.putExtra(CATEGORY_KEYWORD, category);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        String category = getIntent().getExtras().getString(CATEGORY_KEYWORD);

        //Set category as title
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category + " Events");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        String[] allCategories = getResources().getStringArray(R.array.event_categories);
        Query query;
        if(allCategories[0].equals(category)){
            query = mDatabase.child(Constants.EVENTS_KEYWORD).limitToFirst(10);
        } else {
            query = mDatabase.child(Constants.EVENTS_KEYWORD).orderByChild("category").equalTo(category);
        }

        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        mAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(Event.class,
                R.layout.event_card, EventViewHolder.class,  query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, Event model, int position) {
                viewHolder.bindToPost(CategoryActivity.this, model);
            }

        };

        categoriesRecycler = (RecyclerView) findViewById(R.id.category_recycler);
        categoriesRecycler.setAdapter(mAdapter);
        categoriesRecycler.setLayoutManager(layoutManager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
