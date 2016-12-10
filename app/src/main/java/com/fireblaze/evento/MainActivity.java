package com.fireblaze.evento;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fireblaze.evento.adapters.CategoryListAdapter;
import com.fireblaze.evento.adapters.DrawerAdapter;
import com.fireblaze.evento.models.DrawerItem;
import com.fireblaze.evento.models.ImageItem;
import com.fireblaze.evento.models.Location;
import com.fireblaze.evento.models.Organizer;
import com.fireblaze.evento.viewholders.ImageItemHolder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private FirebaseRecyclerAdapter<ImageItem, ImageItemHolder> organizerRecyclerAdapter;
    private ViewPager mViewPager;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    List<DrawerItem> mDrawerItems;
    RecyclerView organizerRecycler;
    RecyclerView categoriesRecycler;
    private DatabaseReference mDatabase;
    private boolean exit = false;

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
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        organizerRecycler = (RecyclerView) findViewById(R.id.recycler_organizers);
        categoriesRecycler = (RecyclerView) findViewById(R.id.recycler_categories);
    }

    private void setupViewPager(){
        //An adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return null;
            }

            @Override
            public int getCount() {
                return 0;
            }
        };
        mViewPager.setAdapter(mPagerAdapter);    }

    @Override
    public View getContainer() {
        return findViewById(R.id.drawer_layout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    private void launchEventList(String query,String id){
        Intent intent = new Intent(MainActivity.this,EventListActivity.class);
        Bundle b = new Bundle();
        b.putString(EventListActivity.QUERY_KEYWORD,query);
        b.putString(EventListActivity.ID_KEYWORD,id);
        intent.putExtras(b);
        startActivity(intent);
    }
    private void setupNavigation(){


        mDrawerItems = new ArrayList<>();
        for(String s : getResources().getStringArray(R.array.drawerTitles)){
            mDrawerItems.add(new DrawerItem(s,R.drawable.common_google_signin_btn_icon_light));
        }
        mDrawerList.setAdapter(new DrawerAdapter(this,R.layout.drawer_list_item,mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        //final CharSequence mTitle = getTitle();
        mDrawerToggle = new ActionBarDrawerToggle(this,mDrawerLayout,R.string.drawer_open,R.string.drawer_close){
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);

                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View v){
                super.onDrawerOpened(v);
                invalidateOptionsMenu();
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void setupOrganizerList(){
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
                        launchEventList(Constants.ORGANIZER_KEYWORD+"/"+model.getId(),model.getId());
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
                R.drawable.ic_coding,
                R.drawable.ic_arts,
                R.drawable.ic_adventure
        };
        List<String> names = new ArrayList<>();
        names.add("Coding");
        names.add("Arts");
        names.add("Adventure");
        LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
//        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        CategoryListAdapter adapter = new CategoryListAdapter(MainActivity.this,names,img);

        categoriesRecycler.setLayoutManager(layoutManager);
        categoriesRecycler.setAdapter(adapter);

    }
    private class DrawerItemClickListener implements ListView.OnItemClickListener{
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }

    }
    private void selectItem(int position){
        Toast.makeText(MainActivity.this,"Drawer item selected "+position,Toast.LENGTH_SHORT).show();
        mDrawerLayout.closeDrawer(mDrawerList);
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
                addData();
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
    private void addData(){

        String names[] ={
               "Eklavya",
                "Texaphyr",
                "PCP"
        };
        String emails[] ={
            "eklavya@eklavya.com",
            "texaphyr@mit.com",
            "techno@pcp.com"
        };
        Location[] latLngs = {
                new Location(18.503018,73.795738),
                new Location(18.510277,73.790180),
                new Location(18.651713,73.761596)

        };

        for(int i = 0;i<3;i++){
            String key = mDatabase.child(Constants.ORGANIZER_KEYWORD).push().getKey();
            Organizer item = new Organizer(key,names[i],emails[i],"+91 8888888888",null,latLngs[i],"http://placehold.it/200x200");
            ImageItem imageItem = new ImageItem(key,names[i],"http://placehold.it/350x150");
            Map<String, Object> postValues = item.toMap();
            Map<String,Object> imageValues = imageItem.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put(Constants.ORGANIZER_IMAGE+"/"+key,imageValues);
            childUpdates.put(Constants.ORGANIZER_KEYWORD+"/"+key,postValues);
            mDatabase.updateChildren(childUpdates);
        }

    }

    @Override
    public void onBackPressed() {
        exitApp();
    }

    @Override
    public void logOut(){
        organizerRecyclerAdapter.cleanup();
        super.logOut();
    }
}
