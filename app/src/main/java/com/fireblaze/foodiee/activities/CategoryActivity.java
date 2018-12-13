package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.databinding.ActivityCategoryBinding;
import com.fireblaze.foodiee.models.FoodItem;
import com.fireblaze.foodiee.viewholders.EventViewHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

public class CategoryActivity extends AppCompatActivity {
    public static final String TAG = CategoryActivity.class.getSimpleName();
    private FirebaseRecyclerAdapter<FoodItem, EventViewHolder> mAdapter;
    private DatabaseReference mDatabase;
    private static final String CATEGORY_KEYWORD = "category";
    private ActivityCategoryBinding binding;

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
        binding = DataBindingUtil.setContentView(this,R.layout.activity_category);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setupView();

    }
    private void setupView(){
        String category = getIntent().getExtras().getString(CATEGORY_KEYWORD);

        //Set category as title
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle(category);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        String[] allCategories = getResources().getStringArray(R.array.event_categories);
        Query query;
//        if(allCategories[0].equals(category)){
//            query = mDatabase.child(Constants.FOOD_ITEM_KEYWORD).limitToFirst(10);
//        } else {
            query = mDatabase.child(Constants.FOOD_ITEM_KEYWORD).orderByChild("category").equalTo(category);
//        }
        mAdapter = new FirebaseRecyclerAdapter<FoodItem, EventViewHolder>(FoodItem.class,
                R.layout.event_card, EventViewHolder.class,  query) {
            @Override
            protected void populateViewHolder(EventViewHolder viewHolder, FoodItem model, int position) {

                viewHolder.bindToPost(CategoryActivity.this, model, true);

                viewHolder.hideQuantity();

            }

        };
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);


        binding.categoryRecycler.setAdapter(mAdapter);
        binding.categoryRecycler.setLayoutManager(layoutManager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    private void showEvents(boolean show){
        if(show){
            binding.noContent.setVisibility(View.GONE);
            binding.categoryRecycler.setVisibility(View.VISIBLE);

        }else {
            binding.noContent.setVisibility(View.VISIBLE);
            binding.categoryRecycler.setVisibility(View.GONE);

        }
    }
}
