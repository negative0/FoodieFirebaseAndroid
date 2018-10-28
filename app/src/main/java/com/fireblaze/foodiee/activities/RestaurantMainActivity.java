package com.fireblaze.foodiee.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.fireblaze.foodiee.R;
import com.fireblaze.foodiee.adapters.RestaurantTasksGridAdapter;


public class RestaurantMainActivity extends BaseActivity {
    public static final String TAG = RestaurantMainActivity.class.getSimpleName();

    public final static int REQ_NEW_ACTIVITY = 1001;

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GridView mTasksGrid = (GridView) findViewById(R.id.organizer_tasks_grid);
        mTasksGrid.setAdapter(new RestaurantTasksGridAdapter(this));
        mTasksGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        onNewEvent();
                        break;
                    case 1:
                        intent = new Intent(RestaurantMainActivity.this,ItemsListActivity.class);
                        intent.putExtra(ItemsListActivity.ID_KEYWORD,getUid());
                        startActivity(intent);
                        break;
                    case 2:
                        ShowOrdersActivity.navigate(RestaurantMainActivity.this);

                        break;
                    default:
                        Toast.makeText(RestaurantMainActivity.this, "Item clicked!" + position, Toast.LENGTH_SHORT).show();
                }
            }
        });
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onNewEvent();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.organizer_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_NEW_ACTIVITY:
                if (resultCode == RESULT_OK)
                    Snackbar.make(getContainer(), "FoodItem Added successfully", Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_log_out:
                logOut();
                Log.d(TAG, "onOptionsItemSelected: logout success");
                return true;
            case R.id.action_user:
                startActivity(new Intent(this, UserActivity.class));
                return true;
            case R.id.action_edit_organizer_account:
                NewRestaurantActivity.navigate(this, getUid(), true);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }


    private void onNewEvent() {
        NewFoodItemActivity.navigate(this);
        //startActivityForResult(new Intent(this,NewFoodItemActivity.class),REQ_NEW_ACTIVITY);
    }
}
