package com.fireblaze.evento;

import android.os.Bundle;
import android.view.View;

public class NewOrganizerActivity extends BaseActivity{
    @Override
    public View getContainer() {
        return findViewById(R.id.container);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_organizer);
    }
}
