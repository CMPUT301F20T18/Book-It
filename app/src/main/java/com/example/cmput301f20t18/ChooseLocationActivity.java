package com.example.cmput301f20t18;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
 */
public class ChooseLocationActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Query query;
    FirestoreLocationAdapter adapter;
    Button addLocation;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference userRef = DB.collection("users");

    int bookID;



    /**
     * Initializes the list of locations and sets up adapter to display list of locations.
     *
     * @param savedInstanceState Previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);


        addLocation = findViewById(R.id.addLocationButton);
        recyclerView = findViewById(R.id.location_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookID = getIntent().getIntExtra("bookID", 0);


        // Setting the header title. This may be done in XML instead
        Toolbar toolbar = findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.choose_location_header));

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), SelectLocationActivity.class);
                // intent.putExtra("INPUT_ADDRESS", address2);
                startActivityForResult(intent, RESULT_OK);
            }
        });

    }

    public void setUp() {
        query = userRef.document(auth.getUid()).collection("pickup_locations");
        FirestoreRecyclerOptions<Address> options = new FirestoreRecyclerOptions.Builder<Address>()
                .setQuery(query, Address.class)
                .build();

        adapter = new FirestoreLocationAdapter(options, bookID);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getApplicationContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int index = data.getIntExtra("LOCATION_INDEX", -1);
        Address address = data.getParcelableExtra("OUTPUT_ADDRESS");

        User current = new User();
        current.ownerAddLocation(address);
    }


}