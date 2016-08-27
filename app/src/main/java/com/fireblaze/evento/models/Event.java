package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chait on 7/24/2016.
 */

public class Event {
    private String uid;
    private String host;
    private String hostID;
    public String title;
    private String body;
    private int starCount = 0;
    private Map<String ,Boolean> stars = new HashMap<>();
    private String mainImage = null;
    private String imagePath;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getHostID() {
        return hostID;
    }

    public void setHostID(String hostID) {
        this.hostID = hostID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Event(String uid, String host, String title, String body) {
        this.uid = uid;
        this.host = host;
        this.title = title;
        this.body = body;
    }

    public Event(String uid, String host, String title, String body, String mainImage, String imagePath) {
        this.uid = uid;
        this.host = host;
        this.title = title;
        this.body = body;
        this.mainImage = mainImage;
        this.imagePath = imagePath;
    }

    @Exclude
    public Map<String, Object> toMap(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("uid",uid);
        result.put("host",host);
        result.put("hostID", hostID);
        result.put("title",title);
        result.put("body",body);
        result.put("starCount",starCount);
        result.put("stars",stars);
        if(mainImage != null){
            result.put("mainImage",mainImage);
            result.put("imagePath",imagePath);
        }

        return result;
    }

    @Exclude
    public void starClicked(String uid){
        if(stars.containsKey(uid)){
            starCount -= 1;
            stars.remove(uid);
        } else {
            starCount += 1;
            stars.put(uid,true);
        }
    }
}
