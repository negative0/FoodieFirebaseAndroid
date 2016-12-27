package com.fireblaze.evento.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    public String userID;
    public String name;
    public String accountType;
    public boolean isVolunteer;
    public boolean isOrganizer;



    public User(){

    }

    public User(String userID,String name){
        this.userID = userID;
        this.name = name;
    }
    public User(String userID,String name,boolean isOrganizer){
        this.userID = userID;
        this.name = name;
        this.isOrganizer = isOrganizer;
    }
}
