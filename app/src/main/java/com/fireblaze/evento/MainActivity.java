package com.fireblaze.evento;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.fireblaze.evento.viewholders.ImageItemHolder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;


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
    private boolean isShowingOrganizerProgressBar = false;

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

    private void setupNavigation(){


        mDrawerItems = new ArrayList<>();
        for(String s : getResources().getStringArray(R.array.drawerTitles)){
            mDrawerItems.add(new DrawerItem(s,R.drawable.common_google_signin_btn_icon_light));
        }
        mDrawerList.setAdapter(new DrawerAdapter(this,R.layout.drawer_list_item,mDrawerItems));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        final CharSequence mTitle = getTitle();
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

       // setOrganizersProgressBar(true);
        LinearLayoutManager horizontalLayoutManager =
                new LinearLayoutManager(MainActivity.this,LinearLayoutManager.HORIZONTAL,false);
        Query query = mDatabase.child(Constants.ORGANIZER_IMAGE).limitToFirst(10);
        organizerRecyclerAdapter = new FirebaseRecyclerAdapter<ImageItem, ImageItemHolder>(ImageItem.class,R.layout.organizer_list_item,
                ImageItemHolder.class,query) {
            @Override
            protected void populateViewHolder(ImageItemHolder viewHolder, ImageItem model, int position) {
                viewHolder.bindToPost(MainActivity.this,model);
            }

        };
        organizerRecycler.setLayoutManager(horizontalLayoutManager);
        organizerRecycler.setAdapter(organizerRecyclerAdapter);
//        organizerRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onChanged() {
//                super.onChanged();
//                setOrganizersProgressBar(false);
//            }
//        });

    }
    private void setOrganizersProgressBar(boolean show) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_organizer);
        if (show) {
            if (progressBar != null) {
                progressBar.setVisibility(View.VISIBLE);
                organizerRecycler.setVisibility(View.GONE);
                isShowingOrganizerProgressBar = true;
            }
        } else{
                progressBar.setVisibility(View.GONE);
                organizerRecycler.setVisibility(View.VISIBLE);
                isShowingOrganizerProgressBar =  false;

        }
    }

    private void setupCategoriesRecycler(){

        int[] img = {
                R.drawable.ic_coding,
                R.drawable.ic_arts,
                R.drawable.ic_adventure
        };
        List<String> names = new ArrayList<>();
        names.add("Coding");
        names.add("Arts");
        names.add("Adventure");
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
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
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        switch (item.getItemId()){
            case R.id.action_log_out:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(this, LoginActivity.class));
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
}

