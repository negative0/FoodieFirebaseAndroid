package com.fireblaze.evento.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import com.bumptech.glide.Glide;
import com.fireblaze.evento.Constants;
import com.fireblaze.evento.R;
import com.fireblaze.evento.fragments.EventFragment;
import com.fireblaze.evento.models.Organizer;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

public class EventListActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabs;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;
    private ImageView featuredImage;
    private Organizer organizer;

    public static final String ORGANIZER_ID = "ORGANIZER_ID";
    public static final String ID_KEYWORD = "KeyString";

    private boolean isBookmarked=false;

    private static final String TAG = EventListActivity.class.getSimpleName();
    @Override
    public View getContainer() {
        return findViewById(R.id.activity_event_list);
    }

    public static void navigate(Context context, String id){
        Intent intent = new Intent(context,EventListActivity.class);
        Bundle b = new Bundle();
        b.putString(EventListActivity.ID_KEYWORD,id);
        intent.putExtras(b);
        context.startActivity(intent);
    }
    void getViews(){
        tabs = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_events);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        featuredImage = (ImageView) findViewById(R.id.featured_image);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.event_list_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        getViews();
        //Setup toolbar as ActionBar
        setSupportActionBar(toolbar);
        //Set up button to point to parent activity
        if(getSupportActionBar()!=null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        else
            throw new RuntimeException("Action bar is null");


        Bundle b = getIntent().getExtras();
//        final String query = b.getString(QUERY_KEYWORD);
//        Log.d(TAG, "onCreate: Query = "+query);
        String id = b.getString(ID_KEYWORD);
        showProgressDialog();
        if(id != null)
            mDatabase.child(Constants.ORGANIZER_KEYWORD).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    setupViewWithData(dataSnapshot.getValue(Organizer.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG,"loadName:onCancelled"+databaseError.getMessage());
                }
            });



    }

    private void setupViewWithData(Organizer item){
        organizer = item;
        //Log.d(TAG,"Name = "+imageItem.getName());
        collapsingToolbarLayout.setTitle(item.getName());
        String resourceURL;

        if((resourceURL = item.getImageURL())!=null) {
            Glide.with(EventListActivity.this).load(resourceURL).error(R.drawable.ic_adventure).placeholder(R.drawable.loading).into(featuredImage);
        }
        if(item.bookmarks.containsKey(getUid())){
            handleBookmark(true);
        }
        final Bundle bundle = new Bundle();
        bundle.putString(EventFragment.ORGANIZER_ID_KEYWORD,organizer.getOrganizerID());
        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private Fragment f[] = {
                    new EventFragment(),
                    new EventFragment(),
                    new EventFragment()

            };

            private String titles[] =
                    getResources().getStringArray(R.array.event_categories);

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
                Log.d(TAG, "getItem: position: "+position+" title: "+titles[position]);
                bundle.putString(EventFragment.CATEGORY_KEYWORD,titles[position]);
                f[position].setArguments(bundle);
                return f[position];
            }

            @Override
            public int getCount() {
                return f.length;
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
            public void onClick(View view) {
                handleBookmark(false);
            }
        });
    }
    private void handleBookmark(boolean bookmark){
        // Set only the image drawable if bookmark is true else upload bookmark and
        // show SnackBar
        if(bookmark){
            isBookmarked = true;
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_white_24dp));
            return;
        }
        DatabaseReference ref = mDatabase.child(Constants.ORGANIZER_KEYWORD).child(organizer.getOrganizerID());
        // Run transaction to upload bookmark
        uploadBookMark(ref);
        if(!isBookmarked){
            Snackbar.make(getContainer(),"Organizer Bookmarked",Snackbar.LENGTH_SHORT)
                    .setAction("UNDO",null)
                    .show();
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_white_24dp));
            isBookmarked = true;

        }else{
            Snackbar.make(getContainer(),"Bookmark Removed",Snackbar.LENGTH_SHORT)
                    .setAction("UNDO",null)
                    .show();
            fab.setImageDrawable(getResources().getDrawable(R.drawable.ic_bookmark_border_white_24dp));
            isBookmarked = false;
        }

    }
    private void uploadBookMark(DatabaseReference postRef){
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                Organizer o = mutableData.getValue(Organizer.class);
                if(o == null){
                    return Transaction.success(mutableData);
                }
                o.bookmarkClicked(getUid());
                mutableData.setValue(o);
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                Log.d(TAG, "postTransaction:onComplete:" + databaseError);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_log_out:
                return true;
            case R.id.action_show_on_map:
                MapsActivity.navigate(this,organizer.getLocation(),organizer.getName());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
