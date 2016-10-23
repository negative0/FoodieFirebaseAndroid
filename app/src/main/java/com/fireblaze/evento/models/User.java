package com.fireblaze.evento.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


@IgnoreExtraProperties
public class User {

    public String userID;
    public String name;
    public String accountType;
    public boolean isVolunteer;
    public Map<String , Boolean> bookingsMap = new HashMap<>();
    public static final String ACCOUNT_TYPE_USER = "USER";
    public static final String ACCOUNT_TYPE_ORGANIZER = "ORGANIZER";


    public User(){

    }

    public User(String userID,String name){
        this.userID = userID;
        this.name = name;
    }
}
