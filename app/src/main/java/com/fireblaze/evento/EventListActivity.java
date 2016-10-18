package com.fireblaze.evento;

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
import android.view.View;

import com.fireblaze.evento.fragments.EventFragment;
import com.fireblaze.evento.models.ImageItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventListActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private FragmentPagerAdapter fragmentPagerAdapter;
    private ViewPager mViewPager;
    private TabLayout tabs;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private FloatingActionButton fab;

    public static final String QUERY_KEYWORD = "QueryString";
    public static final String ID_KEYWORD = "KeyString";

    private boolean isBookmarked=false;

    private static final String TAG = EventListActivity.class.getSimpleName();
    @Override
    public View getContainer() {
        return findViewById(R.id.activity_event_list);
    }

    void getViews(){
        tabs = (TabLayout) findViewById(R.id.tabs);
        mViewPager = (ViewPager) findViewById(R.id.viewpager_events);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsingToolbar);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
        final String query = b.getString(QUERY_KEYWORD);
        Log.d(TAG, "onCreate: Query = "+query);
        String id = b.getString(ID_KEYWORD);
        if(id != null)
            mDatabase.child(Constants.ORGANIZER_KEYWORD).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ImageItem imageItem = dataSnapshot.getValue(ImageItem.class);
                    Log.d(TAG,"Name = "+imageItem.getName());
                    collapsingToolbarLayout.setTitle(imageItem.getName());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG,"loadName:onCancelled"+databaseError.getMessage());
                }
            });



        fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private Fragment f[] = {
                    new EventFragment(),
                    new EventFragment(),
                    new EventFragment()

            };
            private String titles[] = {
                    "All events",
                    "Presentation",
                    "Games"
            };

            @Override
            public CharSequence getPageTitle(int position) {
                return titles[position];
            }

            @Override
            public Fragment getItem(int position) {
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
    }
    private void setupFab(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBookmark();
            }
        });
    }
    private void handleBookmark(){
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
}
