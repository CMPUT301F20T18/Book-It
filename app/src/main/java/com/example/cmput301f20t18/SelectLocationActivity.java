package com.example.cmput301f20t18;

import androidx.fragment.app.FragmentActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Address defaultAddress;

    private Address returnAddress;
    private int locationIndex;

    private OnMapClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_location);

        defaultAddress = getIntent().getParcelableExtra("INPUT_ADDRESS");
        locationIndex = getIntent().getIntExtra("LOCATION_INDEX", -1);

        findViewById(
                R.id.confirm_location_selected_button)
                .setOnClickListener(new ConfirmLocationOnClickListener());


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng defaultLocation = new LatLng(defaultAddress.getLatitude(), defaultAddress.getLongitude());

        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 18));

        listener = new OnMapClickListener();
        mMap.setOnMapClickListener(listener);

    }

    private String getAddressString(Address address){
        String addressTitle = "";
        String subThoroughfare = address.getSubThoroughfare();
        String thoroughfare = address.getThoroughfare();
        String locality = address.getLocality();
        String adminArea = address.getAdminArea();
        if (subThoroughfare != null){
            addressTitle += subThoroughfare + " ";
        }
        if (thoroughfare != null){
            addressTitle += thoroughfare + " ";
        }
        if (locality != null){
            addressTitle += locality + ", ";
        }
        if (adminArea != null){
            addressTitle += adminArea;
        }
        return addressTitle;
    }

    private void stopActivity() {
        Intent returnIntent = new Intent();
        if (locationIndex == -1){
            returnIntent.putExtra("OUTPUT_ADDRESS", returnAddress);
            returnIntent.putExtra("LOCATION_INDEX", locationIndex);
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        else{
            setResult(0, returnIntent);
            finish();
        }

    }

    private class OnMapClickListener implements GoogleMap.OnMapClickListener{
        Address address = defaultAddress;

        @Override
        public void onMapClick(LatLng latLng) {
            createMarker(latLng);
        }

        private void createMarker(LatLng latLng){
            mMap.clear();
            Marker marker = null;
            if (Geocoder.isPresent()) {
                Geocoder geocoder = new Geocoder(getApplicationContext());
                try {
                    List<Address> possibleMarkerAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    Address currentMarkerAddress = possibleMarkerAddresses.get(0);
                    String addressTitle = getAddressString(currentMarkerAddress);
                    marker = mMap.addMarker(new MarkerOptions().position(latLng).title(addressTitle));
                    marker.showInfoWindow();

                    updateAddress(latLng, addressTitle);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("Marker Placed!"));
                updateAddressLatLng(latLng);
            }
            marker.showInfoWindow();
        }

        private void updateAddress(LatLng latLng, String addressTitle) {
            this.address.setAddressLine(1, addressTitle);
            updateAddressLatLng(latLng);
        }

        private void updateAddressLatLng(LatLng latLng) {
            this.address.setLatitude(latLng.latitude);
            this.address.setLongitude(latLng.longitude);
        }


        public Address getData(){
            return address;
        }
    }

    private class ConfirmLocationOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            stopActivity();
        }
    }
}