package com.example.cmput301f20t18;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    private String title;
    private Double latitude;
    private Double longitude;

    public UserLocation(String title, Double latitude, Double longitude){
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public UserLocation(){

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
