package com.fireblaze.evento;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class InstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        SharedPreferences.Editor preferences = getSharedPreferences("INSTANCE",MODE_PRIVATE).edit();
        preferences.putString("TOKEN", FirebaseInstanceId.getInstance().getToken());
        preferences.apply();
    }
}
