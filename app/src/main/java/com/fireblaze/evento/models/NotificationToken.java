package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by fireblaze on 15/12/16.
 */

public class NotificationToken {
    public String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public NotificationToken(String token) {
        this.token = token;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("token",token);
        return result;
    }

    public NotificationToken(){
        //Important
    }

}
