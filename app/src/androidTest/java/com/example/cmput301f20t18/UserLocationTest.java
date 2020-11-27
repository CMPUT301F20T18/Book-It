package com.example.cmput301f20t18;

import org.testng.annotations.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserLocationTest {
    private String defaultTitle = "DEFAULT_TITLE";
    private double defaultLatitiude = -1.0;
    private double defaultLongitude = 1.0;
    private UserLocation mockUserLocation(){
        return new UserLocation(defaultTitle, defaultLatitiude, defaultLongitude);
    }

    @Test
    void testGetTitle(){
        UserLocation userLocation = mockUserLocation();
        assertTrue(userLocation.getTitle().equals(defaultTitle));
    }
    @Test
    void testSetTitle(){
        UserLocation userLocation = mockUserLocation();
        String title = "Hello World";
        userLocation.setTitle(title);
        assertTrue(userLocation.getTitle().equals(title));
    }
    @Test
    void testGetLatitude(){
        UserLocation userLocation = mockUserLocation();
        assertEquals((double)defaultLatitiude, (double)userLocation.getLatitude(), 0.05);
    }
    @Test
    void testSetLatitude(){
        UserLocation userLocation = mockUserLocation();
        double latitude = 2.0;
        userLocation.setLatitude(latitude);
        assertEquals((double)latitude, (double)userLocation.getLatitude(), 0.05);
    }
    @Test
    void testGetLongitude(){
        UserLocation userLocation = mockUserLocation();
        assertEquals((double)defaultLongitude, (double)userLocation.getLongitude(), 0.05);
    }
    @Test
    void testSetLongitude(){
        UserLocation userLocation = mockUserLocation();
        double longitude = 2.0;
        userLocation.setLongitude(longitude);
        assertEquals((double)longitude, (double)userLocation.getLongitude(), 0.05);
    }
}
