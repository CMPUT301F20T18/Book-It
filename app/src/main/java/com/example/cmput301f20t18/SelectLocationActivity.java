package com.example.cmput301f20t18;

import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * SelectLocationActivity is an Activity which allows the user to
 * select a location on the map (places a marker at the location they clicked)
 * display the address of said location (so long as the phone has access to a geocoder)
 * confirm that this is the correct location and click a button to exit
 */
public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static Marker marker;

    private static UserLocation returnUserLocation =
            new UserLocation("DEFAULT", 0.0, 0.0);

    private int locationIndex;

    private OnMapClickListener listener;

    /**
     * Called on creation of the activity
     * Grabs the address and index of the location from caller
     * Sets up the confirm button
     * Sets up the map fragment
     * @param savedInstanceState Current instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        //Set up confirm button
        findViewById(
                R.id.confirm_location_selected_button)
                .setOnClickListener(new ConfirmLocationOnClickListener());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private UserLocation getDefaultUserLocation() {
        String title = getIntent().getStringExtra("INPUT_TITLE");
        Double latitude = getIntent().getDoubleExtra("INPUT_LATITUDE", Double.MAX_VALUE);
        Double longitude = getIntent().getDoubleExtra("INPUT_LONGITUDE", Double.MIN_VALUE);
        return new UserLocation(title, latitude, longitude);
    }

    /**
     * Called on map ready
     * Sets up the map
     * Zooms the map to the address given by caller
     * Sets up a listener for map clicks
     * @param googleMap The map object being interfaced with
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        listener = new OnMapClickListener(this);
        mMap.setOnMapClickListener(listener);
    }

    /**
     * Listener for OnMapClickEvent
     * Places a marker at location of click and updates return address with relevant information
     */
    private class OnMapClickListener implements GoogleMap.OnMapClickListener{
        Context context;

        public OnMapClickListener(Context context){
            this.context = context;
        }

        @Override
        public void onMapClick(LatLng latLng) {
            placeMarker(latLng);
        }

        /**
         * Places marker at location of click and updates returnUserLocation latlng
         * @param latLng LatLng object containing latitude and longitude of location that was
         *               clicked
         */
        private void placeMarker(LatLng latLng){
            mMap.clear();
            GoogleGeocoderQueryHandler googleGeocoderQueryHandler =
                    new GoogleGeocoderQueryHandler(this.context);
            googleGeocoderQueryHandler.queryReverseGeocoder(latLng);
            marker = mMap.addMarker(new MarkerOptions().position(latLng));
            updateAddressLatLng(latLng);
        }

        /**
         * Updates latitude and longitude of returnUserLocation with provided LatLng
         * @param latLng LatLng object containing location data
         */
        private void updateAddressLatLng(LatLng latLng) {
            returnUserLocation.setLatitude(latLng.latitude);
            returnUserLocation.setLongitude(latLng.longitude);
        }
    }

    /**
     * Listener for OnButtonClickEvent
     * Sets up return intent and finishes processes
     */
    private class ConfirmLocationOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Intent returnIntent = new Intent();
            if (returnUserLocation == null){
                setResult(RESULT_CANCELED, returnIntent);
            }
            else{
                returnIntent.putExtra("OUTPUT_TITLE", returnUserLocation.getTitle());
                returnIntent.putExtra("OUTPUT_LATITUDE", returnUserLocation.getLatitude());
                returnIntent.putExtra("OUTPUT_LONGITUDE", returnUserLocation.getLongitude());
                setResult(RESULT_OK, returnIntent);
            }
            finish();
        }
    }

    /**
     * Handles Queries to Google Geocoder API
     */
    private static class GoogleGeocoderQueryHandler{
        private final String TAG = "GEOCODER";
        private RequestQueue queue;

        /**
         * Instantiates GoogleGeocoderQueryHandler Object
         * @param context
         */
        public GoogleGeocoderQueryHandler(Context context){
            this.queue = Volley.newRequestQueue(context);
        }

        /**
         * Handles Queries for reverse geocoding (LatLng to String)
         * @param latLng LatLng object representing location to be reverse geocoded
         */
        public void queryReverseGeocoder(LatLng latLng){
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                    + Double.toString(latLng.latitude) + "," + Double.toString(latLng.longitude)
                    + "&key=AIzaSyBlqu6di8MVHD--P54I9OLgla61KUQwpG0";
            JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url,
                    null, new ReverseGeocoderRequestListener(),
                    new GoogleGeocoderErrorListener());
            queue.add(jsonRequest);
        }

        /**
         * Listener for JSONObject ResponseEvent's for GoogleGeocoderAPI reverse geocoding
         */
        private class ReverseGeocoderRequestListener implements Response.Listener<JSONObject> {
            private final String TAG = "GEOCODER";

            /**
             * Method called on response of GoogleGeocoderAPI constructs string from response given
             * @param response JSONObject representing location that was reverse Gecoded
             */
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray addressComponents = (JSONArray)
                            ((JSONObject) ((JSONArray) response.get("results")).get(0))
                                    .get("address_components");
                    String address = getAddressString(addressComponents);

                    Log.d(TAG, addressComponents.toString());
                    marker.setTitle(address);
                    marker.showInfoWindow();
                    returnUserLocation.setTitle(address);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            /**
             * Constructs string from JSONArray given through Google Geocoder API's response
             * @param addressComponents JSONArray object containing components of an address
             * @return String object representing address represented in addressComponents
             */
            private String getAddressString(JSONArray addressComponents){
                String address = "";

                for (int i=0; i < addressComponents.length(); i++){
                    String addressComponent = getAddressComponent(addressComponents, i);

                    if (!addressComponent.equals("")){
                        address += addressComponent;
                    }
                }
                Log.d(TAG, address);
                return address;
            }

            /**
             * Gets a single addressComponent from addressComponents in it's short form
             * @param addressComponents JSONArray object representing an address
             * @param index int object pointing to current place in JSONArray
             * @return String object representing a single addressComponent
             */
            private String getAddressComponent(JSONArray addressComponents, int index){
                String addressComponentString = "";

                try {
                    JSONObject addressComponent = (JSONObject) addressComponents.get(index);
                    if (approvedType((JSONArray)addressComponent.get("types"))){
                        addressComponentString = ((String) addressComponent.get("short_name"));
                        if (!lastType((JSONArray)addressComponent.get("types"))){
                            addressComponentString += " ";
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Address Component at index " + Integer.toString(index)
                    + "may not be associated with LatLng");
                }
                return addressComponentString;
            }
        }

        /**
         * Checks that the current addressComponent should be added to the address
         * This is to ensure the address displayed to the user and stored in the db is simple and
         * readble
         * @param addressComponentTypes JSONArray which represents an address component's various
         *                              types ([route, street name] for example)
         * @return Boolean object representing whether the current addressComponent is one of a few
         *         approved types
         */
        private boolean approvedType(JSONArray addressComponentTypes) {
            ArrayList<String> approvedTypes = new ArrayList<>(
                    Arrays.asList("street_number", "route", "locality",
                            "administrative_area_level_1"));
            for (String type : approvedTypes){
                for (int i=0; i < addressComponentTypes.length(); i++){
                    try {
                        if (type.equals((String)addressComponentTypes.get(i))){
                            return true;
                        }
                    } catch (JSONException e) {
                        return false;
                    }
                }
            }
            return false;
        }

        /**
         * Checks if the current address component is the last one to be displayed
         * (used for string formatting ensuring that a space is not added on the last component)
         * @param addressComponentTypes addressComponentTypes JSONArray which represents an
         *                              address component's various types
         * @return Boolean object representing whether an address component is the last to be
         *         displayed
         */
        private boolean lastType(JSONArray addressComponentTypes){
            String type = "administrative_area_level_1";

                for (int i=0; i<addressComponentTypes.length(); i++){
                    try {
                        if (type.equals((String)addressComponentTypes.get(i))){
                            return true;
                        }
                    } catch (JSONException e) {
                        return false;
                    }
                }
            return false;
        }

        /**
         * Listener for Error Response Event
         */
        private class GoogleGeocoderErrorListener implements Response.ErrorListener{
            private final String TAG = "GEOCODER";

            /**
             * Method called when an error with the response occurs
             * @param error VolleyError object representing error that occured
             */
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        }
    }
}