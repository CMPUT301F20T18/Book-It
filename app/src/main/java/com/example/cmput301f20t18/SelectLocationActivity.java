package com.example.cmput301f20t18;

import androidx.fragment.app.FragmentActivity;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class SelectLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Address defaultAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultAddress = getIntent().getParcelableExtra("ADDRESS");
        setContentView(R.layout.activity_select_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        List<Address> addresses;
        mMap = googleMap;
        final Geocoder geocoder = new Geocoder(this);
        LatLng defaultLocation = new LatLng(defaultAddress.getLatitude(), defaultAddress.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 18));

        GoogleMap.OnMapClickListener listener = new GoogleMap.OnMapClickListener(){
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.clear();
                Marker marker = null;
                try{
                    List<Address> possibleMarkerAddresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                    Address currentMarkerAddress = possibleMarkerAddresses.get(0);
                    marker = mMap.addMarker( new MarkerOptions().position(latLng).title(getMarkerTitle(currentMarkerAddress)));
                    marker.showInfoWindow();
                }
                catch(IOException e){
                    e.printStackTrace();
                }
            }
        };
        mMap.setOnMapClickListener(listener);
    }

    private String getMarkerTitle(Address markerAddress){
        String markerTitle = "";
        String subThoroughfare = markerAddress.getSubThoroughfare();
        String thoroughfare = markerAddress.getThoroughfare();
        String locality = markerAddress.getLocality();
        String adminArea = markerAddress.getAdminArea();
        if (subThoroughfare != null){
            markerTitle += subThoroughfare + " ";
        }
        if (thoroughfare != null){
            markerTitle += thoroughfare + " ";
        }
        if (locality != null){
            markerTitle += locality + ", ";
        }
        if (adminArea != null){
            markerTitle += adminArea;
        }
        return markerTitle;
    };
}