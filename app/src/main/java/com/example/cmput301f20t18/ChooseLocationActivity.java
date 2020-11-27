package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static androidx.camera.core.CameraX.getContext;

/**
 * Activity where the user can select a pick up location.
 * Adding a new location requires the use of SelectPickUpLocation
 * @see SelectLocationActivity
 * @author deinum
 */
public class ChooseLocationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Query query;
    FirestoreLocationAdapter adapter;
    Button addLocation;
    int bookID;
    int t_id;
    final static String TAG = "CLA_DEBUG";
    Button cancel;


    private static final int SELECT_LOCATION_REQUEST_CODE = 0;

    // DB info
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference userRef = DB.collection("users");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_location);


        addLocation = findViewById(R.id.addLocationButton);
        recyclerView = findViewById(R.id.location_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookID = getIntent().getIntExtra("bookID", 0);
        t_id = getIntent().getIntExtra("t_id", 0);

        setUp();

        addLocation.setOnClickListener(new AddLocationOnClickListener(this));

        cancel = findViewById(R.id.button_back);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * Sets up our recyclerview, including defining the query which will populate the list
     */
    public void setUp() {
        query = userRef.document(auth.getUid()).collection("pickup_locations");
        FirestoreRecyclerOptions<UserLocation> options = new FirestoreRecyclerOptions.Builder<UserLocation>()
                .setQuery(query, UserLocation.class)
                .build();

        adapter = new FirestoreLocationAdapter(options, bookID, ChooseLocationActivity.this, t_id);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    // tell our adapter to start listening as soon as the fragment begins
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }
    // tell our adapter to stop listening as soon as the fragment ends
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }




    // Handle returned address
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            String title = data.getStringExtra("OUTPUT_TITLE");
            double latitude = data.getDoubleExtra("OUTPUT_LATITUDE", 0);
            double longitude = data.getDoubleExtra("OUTPUT_LONGITUDE", 0);


            Log.d(TAG, "Title: " + title);
            Log.d(TAG, "Lat: " + latitude);
            Log.d(TAG, "Long: " + longitude);



            UserLocation location = new UserLocation(title, latitude, longitude);
            // add the new address to the users pickup_location collection
            User current = new User();
            current.ownerAddLocation(location);
        }


    }

    private class AddLocationOnClickListener implements View.OnClickListener{
        private Context parentContext;

        AddLocationOnClickListener(Context parentContext){
            this.parentContext = parentContext;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(parentContext, SelectLocationActivity.class);
            startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE);
        }
    }
}