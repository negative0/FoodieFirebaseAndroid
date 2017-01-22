package com.fireblaze.evento.models;

import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class User {

    private String userID;
    private String name;
    private String accountType;
    public boolean isOrganizer;
    private String emailID;
    private String phone;
    private String collegeName;
    private boolean isValid = false;
    private long dateCreated;
    public User(){
        //Important
    }

    public User(String userID, String name, String emailID, String phone) {
        this.userID = userID;
        this.name = name;
        this.emailID = emailID;
        this.phone = phone;
        isValid = true;
        dateCreated = System.currentTimeMillis();
    }

    public User(String userID, String name){
        this.userID = userID;
        this.name = name;
        isValid = false;
    }
    public User(String userID,String emailID,boolean isOrganizer){
        this.userID = userID;
        this.isOrganizer = isOrganizer;
        this.emailID = emailID;
        if(isOrganizer)
            isValid = true;
        dateCreated = System.currentTimeMillis();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getAccountType() {
        return accountType;
    }

    public boolean isOrganizer() {
        return isOrganizer;
    }

    public String getEmailID() {
        return emailID;
    }

    public void setEmailID(String emailID) {
        this.emailID = emailID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
