package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.databinding.ActivityRestaurantListBinding;
import com.fireblaze.foodiee.models.ImageItem;
import com.fireblaze.foodiee.viewholders.ImageItemHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class RestaurantListActivity extends BaseActivity {
    public static final String TAG = RestaurantListActivity.class.getSimpleName();
    private ActivityRestaurantListBinding binding;
    private DatabaseReference mDatabase;
    private FirebaseRecyclerAdapter<ImageItem, ImageItemHolder> mAdapter;
    @Override
    public View getContainer() {
        return binding.getRoot();
    }


    public static void navigate(Context context){
        Intent i = new Intent(context,RestaurantListActivity.class);
        context.startActivity(i);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_restaurant_list);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        if(getSupportActionBar()!= null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("All organizers");
        }
        setupView();


    }
    private void setupView(){
        Query q = mDatabase.child(Constants.RESTAURANT_IMAGE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mAdapter = new FirebaseRecyclerAdapter<ImageItem, ImageItemHolder>(ImageItem.class,
                R.layout.restaurant_list_item_horizontal,ImageItemHolder.class,q) {
            @Override
            protected void populateViewHolder(ImageItemHolder viewHolder, final ImageItem model, int position) {
                viewHolder.bindToPost(RestaurantListActivity.this, model, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG,Constants.RESTAURANT_KEYWORD +"/"+model.getName());
                        launchEventList(model.getId());
                    }
                });
            }
        };
        binding.recycler.setLayoutManager(layoutManager);
        binding.recycler.setAdapter(mAdapter);
    }
    private void launchEventList(String id){
        ItemsListActivity.navigate(this,id);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
