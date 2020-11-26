package com.example.cmput301f20t18;

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
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

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

        Button buttonBack = findViewById(R.id.button_back);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        addLocation.setOnClickListener(new AddLocationOnClickListener(this));
    }

    /**
     * Sets up our recyclerview, including defining the query which will populate the list
     */
    public void setUp() {
        query = userRef.document(auth.getUid()).collection("pickup_locations");
        FirestoreRecyclerOptions<Address> options = new FirestoreRecyclerOptions.Builder<Address>()
                .setQuery(query, Address.class)
                .build();

        adapter = new FirestoreLocationAdapter(options, bookID);
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
            int index = data.getIntExtra("LOCATION_INDEX", -1);
            Address address = data.getParcelableExtra("OUTPUT_ADDRESS");
            // add the new address to the users pickup_location collection
            User current = new User();
            current.ownerAddLocation(address);
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
            // intent.putExtra("INPUT_ADDRESS", address2);
            startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE);
        }
    }
}