package com.fireblaze.foodiee.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Restaurant {
    private String organizerID;
    private String name;
    private String email;
    private int bookmarkCount = 0;
    private int volunteerCount = 0;
//    private Map<String, Boolean> volunteers = new HashMap<>();
//    public Map<String ,Boolean> bookmarks = new HashMap<>();
    private String phone;
    private MyLocation myLocation;
    private String imageURL;
    private String website;
    private boolean isValid = false;
    private long dateCreated;

    public boolean getIsValid() {
        return isValid;
    }


    public void setIsValid(boolean valid) {
        isValid = valid;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setBookmarkCount(int bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }


    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("organizerID",organizerID);
        result.put("name",name);
        result.put("email",email);
        result.put("bookmarkCount",bookmarkCount);
        result.put("volunteerCount",volunteerCount);

        result.put("phone",phone);
        result.put("myLocation", myLocation);
        result.put("imageURL",imageURL);
        result.put("isValid",isValid);
        return result;
    }

    public int getVolunteerCount() {
        return volunteerCount;
    }


    public boolean isValid() {
        return isValid;
    }

    public String getOrganizerID() {
        return organizerID;
    }

    public void setOrganizerID(String organizerID) {
        this.organizerID = organizerID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public MyLocation getMyLocation() {
        return myLocation;
    }

    public void setMyLocation(MyLocation myLocation) {
        this.myLocation = myLocation;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getBookmarkCount() {
        return bookmarkCount;
    }

    public Restaurant(String organizerID, String name, String email, String phone, String website, MyLocation myLocation, String imageURL) {
        this.organizerID = organizerID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.website = website;
        this.myLocation = myLocation;
        this.imageURL = imageURL;
        this.isValid = true;
        dateCreated = System.currentTimeMillis();
    }

    public Restaurant() {
        // Important
    }
    public Restaurant(String organizerID){
        this.organizerID = organizerID;
    }

}
