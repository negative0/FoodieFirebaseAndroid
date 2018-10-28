package com.fireblaze.foodiee.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.foodiee.Constants;
import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.adapters.CategoryListAdapter;
import com.fireblaze.foodiee.databinding.ActivityMainBinding;
import com.fireblaze.foodiee.models.ImageItem;
import com.fireblaze.foodiee.viewholders.ImageItemHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FirebaseRecyclerAdapter<ImageItem, ImageItemHolder> popularRecyclerAdapter;

    private View navHeader;

    private ActionBarDrawerToggle mDrawerToggle;

    private DatabaseReference mDatabase;
    private ActivityMainBinding binding;
    public static final int REQ_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main);
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getViews();
        setupNavigation();
        setupPopularList();
        setupCategoriesRecycler();

    }
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        //mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //Navigation menu
        navHeader = binding.navView.getHeaderView(0);
    }
    private void setupNavHeader(){
        navHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this,UserActivity.class),REQ_SETTINGS);
            }
        });
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            ImageView profileImage = (ImageView) navHeader.findViewById(R.id.nav_header_profile_image);
            Glide.with(MainActivity.this).load(photoUrl)

//                    .transform(new CircleTransform(this))
//                    .error(R.drawable.ic_profile)
                    .into(profileImage);

            TextView nameText = (TextView) navHeader.findViewById(R.id.nav_header_text_name);
            nameText.setText(name);

            TextView emailText = (TextView) navHeader.findViewById(R.id.nav_header_text_email);
            emailText.setText(email);


        }

    }

    @Override
    public View getContainer() {
        return binding.getRoot();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void launchEventList(String id){
        ItemsListActivity.navigate(MainActivity.this,id);

    }
    private void setupNavigation() {
        setupNavHeader();

        binding.navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Toast.makeText(MainActivity.this, "Home", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_account:

                        startActivityForResult(new Intent(MainActivity.this,UserActivity.class),REQ_SETTINGS);
                        return true;
                    case R.id.nav_booked_events:
                        startActivity(new Intent(MainActivity.this, UserShowOrdersActivity.class));
//                        BookedEventsListActivity.navigate(MainActivity.this);
                        return true;
                }
                if (item.isChecked()) {
                    item.setChecked(false);

                } else {
                    item.setChecked(true);
                }
                return true;
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);

            }

            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);

            }
        };
        binding.drawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setupPopularList(){
        binding.content.btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RestaurantListActivity.navigate(MainActivity.this);
                // startActivity(new Intent(MainActivity.this,RestaurantListActivity.class));
            }
        });
        //Show the progress dialog.
        setPopularProgressBar();
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        Query query = mDatabase.child(Constants.RESTAURANT_IMAGE).limitToFirst(10);
        popularRecyclerAdapter = new FirebaseRecyclerAdapter<ImageItem, ImageItemHolder>(ImageItem.class,R.layout.restaurant_list_item,
                ImageItemHolder.class,query) {
            @Override
            protected void populateViewHolder(ImageItemHolder viewHolder, final ImageItem model, int position) {
                viewHolder.bindToPost(MainActivity.this,model, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,Constants.RESTAURANT_KEYWORD +"/"+model.getName());
                        launchEventList(model.getId());
                    }
                });

            }
        };

        popularRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setPopularProgressBar();
                popularRecyclerAdapter.unregisterAdapterDataObserver(this);
            }
        });
        binding.content.recyclerOrganizers.setLayoutManager(horizontalLayoutManager);
        binding.content.recyclerOrganizers.setAdapter(popularRecyclerAdapter);

    }
    public void setPopularProgressBar() {
        RecyclerView organizerRecycler = binding.content.recyclerOrganizers;
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_organizer);
        if(progressBar == null){
            throw new RuntimeException("Progress bar is unexpectedly null");
        }
        if(progressBar.getVisibility() == View.VISIBLE && organizerRecycler.getVisibility() == View.GONE){
            progressBar.setVisibility(View.GONE);
            organizerRecycler.setVisibility(View.VISIBLE);
            Log.d(TAG, "setPopularProgressBar: hiding Progressbar");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            organizerRecycler.setVisibility(View.GONE);
            Log.d(TAG, "setPopularProgressBar: showing Progressbar");
        }

    }


    private void setupCategoriesRecycler(){
        RecyclerView categoriesRecycler = binding.content.recyclerCategories;
        if(categoriesRecycler == null)
            throw new RuntimeException("Categories Recycler is unexpectedly null");

        int[] img = {
                R.drawable.veg,
                R.drawable.nonveg,
                R.drawable.veg
        };
        //List<String> names = new ArrayList<>();
        String categories[] =  getResources().getStringArray(R.array.event_categories);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        CategoryListAdapter adapter = new CategoryListAdapter(MainActivity.this,categories,img);

        categoriesRecycler.setLayoutManager(layoutManager);
        categoriesRecycler.setAdapter(adapter);

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_log_out:
                logOut();
                return true;
            case R.id.action_user:
                startActivityForResult(new Intent(this,UserActivity.class),REQ_SETTINGS);
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(binding.drawerLayout.isDrawerOpen(GravityCompat.START)){
            binding.drawerLayout.closeDrawers();
            return;
        }
        exitApp();
    }

    @Override
    public void logOut(){
        popularRecyclerAdapter.cleanup();
        super.logOut();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_SETTINGS:
                setupNavHeader();
                break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
