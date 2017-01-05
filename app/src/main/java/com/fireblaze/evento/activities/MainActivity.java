package com.fireblaze.evento.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.adapters.CategoryListAdapter;
import com.fireblaze.evento.models.ImageItem;
import com.fireblaze.evento.other.CircleTransform;
import com.fireblaze.evento.viewholders.ImageItemHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private FirebaseRecyclerAdapter<ImageItem, ImageItemHolder> organizerRecyclerAdapter;
    private DrawerLayout mDrawerLayout;
    private View navHeader;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private RecyclerView organizerRecycler;
    private RecyclerView categoriesRecycler;
    private Button btnShowAll;
    private DatabaseReference mDatabase;
    public static final int REQ_SETTINGS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getViews();
        setupNavigation();
        setupOrganizerList();
        setupCategoriesRecycler();

    }
    private void getViews(){
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        organizerRecycler = (RecyclerView) findViewById(R.id.recycler_organizers);
        categoriesRecycler = (RecyclerView) findViewById(R.id.recycler_categories);

        btnShowAll = (Button) findViewById(R.id.btn_show_all);
        //Navigation menu
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
    }
    private void setupNavHeader(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            ImageView profileImage = (ImageView) navHeader.findViewById(R.id.nav_header_profile_image);
            Glide.with(MainActivity.this).load(photoUrl)
                    .transform(new CircleTransform(this))
                    .error(R.drawable.ic_profile)
                    .into(profileImage);

            TextView nameText = (TextView) navHeader.findViewById(R.id.nav_header_text_name);
            nameText.setText(name);

            TextView emailText = (TextView) navHeader.findViewById(R.id.nav_header_text_email);
            emailText.setText(email);


        }

    }

    @Override
    public View getContainer() {
        return findViewById(R.id.drawer_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void launchEventList(String id){
        EventListActivity.navigate(MainActivity.this,id);

    }
    private void setupNavigation() {
        setupNavHeader();

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
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
                        BookedEventsListActivity.navigate(MainActivity.this);
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
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            public void onDrawerClosed(View v) {
                super.onDrawerClosed(v);

            }

            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
    }

    private void setupOrganizerList(){
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,OrganizerListActivity.class));
            }
        });
        //Show the progress dialog.
        setOrganizersProgressBar();
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        Query query = mDatabase.child(Constants.ORGANIZER_IMAGE).limitToFirst(10);
        organizerRecyclerAdapter = new FirebaseRecyclerAdapter<ImageItem, ImageItemHolder>(ImageItem.class,R.layout.organizer_list_item,
                ImageItemHolder.class,query) {
            @Override
            protected void populateViewHolder(ImageItemHolder viewHolder, final ImageItem model, int position) {
                viewHolder.bindToPost(MainActivity.this,model, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG,Constants.ORGANIZER_KEYWORD+"/"+model.getName());
                        launchEventList(model.getId());
                    }
                });

            }
        };

        organizerRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                setOrganizersProgressBar();
                organizerRecyclerAdapter.unregisterAdapterDataObserver(this);
            }
        });
        organizerRecycler.setLayoutManager(horizontalLayoutManager);
        organizerRecycler.setAdapter(organizerRecyclerAdapter);

    }
    public void setOrganizersProgressBar() {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_organizer);
        if(progressBar == null){
            throw new RuntimeException("Progress bar is unexpectedly null");
        }
        if(progressBar.getVisibility() == View.VISIBLE && organizerRecycler.getVisibility() == View.GONE){
            progressBar.setVisibility(View.GONE);
            organizerRecycler.setVisibility(View.VISIBLE);
            Log.d(TAG, "setOrganizersProgressBar: hiding Progressbar");
        }else{
            progressBar.setVisibility(View.VISIBLE);
            organizerRecycler.setVisibility(View.GONE);
            Log.d(TAG, "setOrganizersProgressBar: showing Progressbar");
        }

    }


    private void setupCategoriesRecycler(){
        if(categoriesRecycler == null)
            throw new RuntimeException("Categories Recycler is unexpectedly null");

        int[] img = {
                R.drawable.ic_arts,
                R.drawable.ic_coding,
                R.drawable.ic_adventure
        };
        //List<String> names = new ArrayList<>();
        String categories[] =  getResources().getStringArray(R.array.event_categories);
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
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
            case R.id.action_add_data:
                //addData();
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
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
            mDrawerLayout.closeDrawers();
            return;
        }
        exitApp();
    }

    @Override
    public void logOut(){
        organizerRecyclerAdapter.cleanup();
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
