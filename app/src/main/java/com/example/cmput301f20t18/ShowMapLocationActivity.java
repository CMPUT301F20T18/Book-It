package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class ShowMapLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private UserLocation pickupLocation;
    private int bookID;

    final static String TAG = "SMLA_DEBUG";

    // database info
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference userRef = DB.collection("users");
    CollectionReference transRef = DB.collection("transactions");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_map_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        bookID = getIntent().getIntExtra("bookID", 0);

    }

    private UserLocation getPickupLocation() {
        pickupLocation = new UserLocation(
                getIntent().getStringExtra("INPUT_TITLE"),
                getIntent().getDoubleExtra("INPUT_LATITUDE", 0),
                getIntent().getDoubleExtra("INPUT_LONGITUDE", 0));
        return pickupLocation;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        Log.d(TAG, "onMapReady: bookID: " + bookID);
        transRef.whereEqualTo("bookID", bookID).whereLessThanOrEqualTo("status", Transaction.STATUS_BORROWED).whereGreaterThanOrEqualTo("status", Transaction.STATUS_ACCEPTED).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Transaction transaction = task.getResult().toObjects(Transaction.class).get(0);
                    Log.d(TAG, "onMapReady: transactionID: " + transaction.getID());
                    Log.d(TAG, "LAT " + transaction.getLocation().getLatitude());
                    Log.d(TAG, "LONG " + transaction.getLocation().getLongitude());


                    mMap = googleMap;

                    // Add a marker in Sydney and move the camera
                    LatLng markerPosition = new LatLng(transaction.getLocation().getLatitude(), transaction.getLocation().getLongitude());


                    mMap.addMarker(new MarkerOptions().position(markerPosition)
                            .title(transaction.getLocation().getTitle()));

                    mMap.moveCamera(CameraUpdateFactory.newLatLng(markerPosition));
                }
            }
        });

    }

}