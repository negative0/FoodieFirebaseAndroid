package com.fireblaze.foodiee;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by fireblaze on 16/10/16.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
