package com.fireblaze.evento.models;


import com.google.android.gms.maps.model.LatLng;

/**
 * Created by fireblaze on 23/10/16.
 */

public class Location {
    public double latitude;
    public double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
    Location(){}

    public Location(LatLng latLng){
        latitude = latLng.latitude;
        longitude = latLng.longitude;
    }


}
