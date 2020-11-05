package com.example.cmput301f20t18;

import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

public class UserLocation {
    private String title;
    private Address address;
    private LatLng latlng;

    public UserLocation(String title, Address address, LatLng latlng){
        this.title = title;
        this.address = address;
        this.latlng = latlng;
    }
    public UserLocation(Address address){
        this.address = address;
        this.latlng = new LatLng(address.getLatitude(), address.getLongitude());
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public LatLng getLatlng() {
        return latlng;
    }

    public void setLatlng(LatLng latlng) {
        this.latlng = latlng;
    }
}
