package com.fireblaze.evento.models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chait on 8/27/2016.
 */

public class ImageItem {
    private String resourceURL;

    public ImageItem() {
        //Constructor with no args *Important*
    }

    public ImageItem(String resourceURL) {
        this.resourceURL = resourceURL;
    }
    @Exclude
    public Map<String,Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("resourceURL",resourceURL);
        return result;
    }

    public String getResourceURL() {
        return resourceURL;
    }

    public void setResourceURL(String resourceURL) {
        this.resourceURL = resourceURL;
    }
}
