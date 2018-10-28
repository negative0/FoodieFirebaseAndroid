package com.fireblaze.foodiee.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.fragments.EventFragment;
import com.fireblaze.foodiee.models.Restaurant;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemsListActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabs;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private ImageView featuredImage;
    private Restaurant restaurant;

    public static final String ORGANIZER_ID = "ORGANIZER_ID";
    public static final String ID_KEYWORD = "KeyString";

//    private boolean isBookmarked=false;

    private static final String TAG = ItemsListActivity.class.getSimpleName();
    @Override
    public View getContainer() {
        return findViewById(R.id.activity_event_list);
    }

    public static void navigate(Context context, String id){
        Intent intent = new Intent(context,ItemsListActivity.class);
        Bundle b = new Bundle();
        b.putString(ItemsListActivity.ID_KEYWORD,id);
        intent.putExtras(b);
        context.startActivity(intent);
    }
    private void getViews(){
        tabs = findViewById(R.id.tabs);
        mViewPager = findViewById(R.id.viewpager_events);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        fab = findViewById(R.id.fab);
        collapsingToolbarLayout = findViewById(R.id.collapsingToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        featuredImage = findViewById(R.id.featured_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_list);
        getViews();
        //Setup toolbar as ActionBar
        setSupportActionBar(toolbar);
        //Set up button to point to parent activity
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            throw new RuntimeException("Action bar is null");


        //Delete Database for cart
        mDatabase.child("cart").child(getUid()).setValue(null);

        Bundle b = getIntent().getExtras();
        String id = b.getString(ID_KEYWORD);
        showProgressDialog();
        if(id != null)
            mDatabase.child(Constants.RESTAURANT_KEYWORD).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Restaurant o = dataSnapshot.getValue(Restaurant.class);
                    if(o.getOrganizerID().equals(getUid()))
                        organizerMode();
                    setupViewWithData(o);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG,"loadName:onCancelled"+databaseError.getMessage());
                }
            });



    }

    private void setupViewWithData(Restaurant item){
        restaurant = item;
        collapsingToolbarLayout.setTitle(item.getName());
        String resourceURL;

        if((resourceURL = item.getImageURL())!=null) {
            Glide.with(ItemsListActivity.this)
                    .load(resourceURL)
                    .into(featuredImage);
        }

        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            private final String titles[] =
                    getResources().getStringArray(R.array.event_categories);

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                return EventFragment.create(restaurant.getOrganizerID(),titles[position]);
            }

            @Override
            public int getCount() {
                return titles.length;
            }
        };
        mViewPager.setAdapter(fragmentPagerAdapter);
        tabs.setupWithViewPager(mViewPager);
        setupFab();
        hideProgressDialog();
    }

    private void setupFab(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleCheckout(restaurant.getOrganizerID());
            }
        });
    }

    private void handleCheckout(final String id){
        Toast.makeText(this,"Checkout!", Toast.LENGTH_LONG).show();
        ShoppingCartActivity.navigate(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_show_info:
                RestaurantDetailsActivity.navigate(this, restaurant.getOrganizerID());
                return true;
            case R.id.action_log_out:
                logOut();
                return true;
            case R.id.action_show_on_map:
                MapsActivity.navigate(this, restaurant.getMyLocation(), restaurant.getName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void organizerMode(){
        fab.setVisibility(View.GONE);

    }


}
