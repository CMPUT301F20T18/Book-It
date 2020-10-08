package com.example.cmput301f20t18;

import com.google.android.gms.maps.model.LatLng;

public class BorrowTransaction {
    private LatLng location;

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
