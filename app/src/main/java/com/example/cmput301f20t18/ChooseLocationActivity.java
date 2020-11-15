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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity where the user can select a pick up location.
 */
public class ChooseLocationActivity extends AppCompatActivity {
    private ArrayList<UserLocation> locationList = new ArrayList<>();
    private LocationAdapter locationAdapter = new LocationAdapter(this, locationList);

    RecyclerView recyclerView;


    /**
     * Initializes the list of locations and sets up adapter to display list of locations.
     *
     * @param savedInstanceState Previous saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_location);

        recyclerView = findViewById(R.id.location_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setting the header title. This may be done in XML instead
        Toolbar toolbar = findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.choose_location_header));

        // hardcoded some addresses to display
        Address address1 = new Address(Locale.getDefault());
        address1.setAddressLine(0, "116 St & 85 Ave");
        address1.setLatitude(53.5207963);
        address1.setLongitude(-113.529820);
        Address address2 = new Address(Locale.getDefault());
        address2.setAddressLine(0, "11455 87 Ave NW");
        address2.setLatitude(53.5220);
        address2.setLongitude(-113.5288);


        locationList.add(new UserLocation(address1, null));
        locationList.add(new UserLocation(address2, null));

        recyclerView.setAdapter(locationAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int index = data.getIntExtra("LOCATION_INDEX", -1);
        Address address = data.getParcelableExtra("OUTPUT_ADDRESS");
        locationList.remove(index);
        locationList.add(index, new UserLocation(address,null));
        locationAdapter.notifyDataSetChanged();
    }
}