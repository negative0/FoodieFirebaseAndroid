package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;


public class Organizer {
    private String organizerID;
    private String name;
    private String email;
    private int bookmarkCount = 0;
    public Map<String ,Boolean> bookmarks = new HashMap<>();
    private String phone;
    private Location location;
    private String imageURL;
    private String website;

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setBookmarkCount(int bookmarkCount) {
        this.bookmarkCount = bookmarkCount;
    }

    public Map<String, Boolean> getBookmarks() {
        return bookmarks;
    }

    public void setBookmarks(Map<String, Boolean> bookmarks) {
        this.bookmarks = bookmarks;
    }

    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("organizerID",organizerID);
        result.put("name",name);
        result.put("email",email);
        result.put("bookmarkCount",bookmarkCount);
        result.put("bookmarks",bookmarks);
        result.put("phone",phone);
        result.put("location",location);
        result.put("imageURL",imageURL);
        return result;
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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
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

    public Organizer(String organizerID, String name, String email, String phone, Location location, String imageURL) {
        this.organizerID = organizerID;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.location = location;
        this.imageURL = imageURL;
    }

    public Organizer() {

    }

    @Exclude
    public void bookmarkClicked(String uid){
        if(bookmarks.containsKey(uid)){
            bookmarkCount -= 1;
            bookmarks.remove(uid);
        } else {
            bookmarkCount += 1;
            bookmarks.put(uid,true);
        }
    }

}
