package com.fireblaze.evento;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class EventListActivity extends BaseActivity {
    public static final String QUERY_KEYWORD = "QueryString";
    private static final String TAG = EventListActivity.class.getSimpleName();
    @Override
    public View getContainer() {
        return findViewById(R.id.activity_event_list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);
        Bundle b = getIntent().getExtras();
        Log.d(TAG, "onCreate: Query = "+b.getString(QUERY_KEYWORD));
    }
}
