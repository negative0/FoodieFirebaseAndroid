package com.fireblaze.foodiee.models;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fireblaze on 23/10/16.
 */

public class MyLocation {
    public double latitude;
    public double longitude;

    public MyLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    MyLocation(){}

    public MyLocation(LatLng latLng){
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }


}
