package com.fireblaze.evento;


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

import com.fireblaze.evento.adapters.OrganizerTasksGridAdapter;


public class OrganizerMainActivity extends BaseActivity {
    public static final String TAG = OrganizerMainActivity.class.getSimpleName();

    public final static int REQ_NEW_ACTIVITY = 1001;

    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GridView mTasksGrid = (GridView) findViewById(R.id.organizer_tasks_grid);
        mTasksGrid.setAdapter(new OrganizerTasksGridAdapter(this));
        mTasksGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Intent intent;
                switch (position){
                   case 0:
                       onNewEvent();
                       break;
                   case 1:
                       intent = new Intent(OrganizerMainActivity.this,EventListActivity.class);
                       intent.putExtra(EventListActivity.ID_KEYWORD,getUid());
                       startActivity(intent);
                       break;
                   case 2:
                       intent = new Intent(OrganizerMainActivity.this,SendNotificationActivity.class);
                       startActivity(intent);
                       break;
                   default:
                       Toast.makeText(OrganizerMainActivity.this,"Item clicked!"+position,Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.organizer_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQ_NEW_ACTIVITY:
                Snackbar.make(getContainer(),"Event Added successfully",Snackbar.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_log_out:
                logOut();
                Log.d(TAG, "onOptionsItemSelected: logout success");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        exitApp();
    }


    private void onNewEvent(){
        startActivityForResult(new Intent(this,NewEventActivity.class),REQ_NEW_ACTIVITY);
    }
}
