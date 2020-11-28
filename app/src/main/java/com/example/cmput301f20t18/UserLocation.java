package com.example.cmput301f20t18;

public class UserLocation {
    private String title;
    private Double latitude;
    private Double longitude;

    /**
     * Default constructor for UserLocation
     * @param title The title for the location
     * @param latitude The latitude of the location
     * @param longitude The longitude of the location
     */
    public UserLocation(String title, Double latitude, Double longitude){
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Empty constructor for firestore
     */
    public UserLocation(){

    }


    /**
     * Get the title of a location
     * @return String representation of the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title for a location
     * @param title The new title for the location
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Get the locations latitude
     * @return Double representing the locations latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Set the locations latitude
     * @param latitude The new latitude for the location
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Get the locations longitude
     * @return Double representation of the longituide for the location
     */
    public Double getLongitude() {
        return longitude;
    }


    /**
     * Set the longitude for the location
     * @param longitude The new longitude for the location
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}
