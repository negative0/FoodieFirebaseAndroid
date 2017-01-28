package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fireblaze on 15/12/16.
 */

public class NotificationToken {
    private String token;
    private boolean receiveNotifications;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationToken(String token, boolean receiveNotifications) {
        this.token = token;
        this.receiveNotifications = receiveNotifications;
    }

    public NotificationToken(String token) {
        this.token = token;
        receiveNotifications = true;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("token",token);
        result.put("receiveNotifications",receiveNotifications);
        return result;
    }

    public NotificationToken(){
        //Important
    }

    public boolean isReceiveNotifications() {
        return receiveNotifications;
    }
}
