package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chait on 8/27/2016.
 */

public class ImageItem {
    private String resourceURL;
    private String name;
    private String id;

    public ImageItem() {
        //Constructor with no args *Important*
    }

    public ImageItem(String id,String name,String resourceURL) {
        this.resourceURL = resourceURL;
        this.name = name;
        this.id = id;
    }

    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("resourceURL",resourceURL);
        result.put("name",name);
        result.put("id",id);
        return result;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public String getName(){return name;}

    public String getId() {
        return id;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }
}
