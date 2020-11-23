package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Custom RecyclerView Adapter for UserLocation objects in ChooseLocationActivity.
 *
 * @see UserLocation
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.RequestViewHolder> {

    private final String TAG = "LocationAdapter";
    private final Context context;
    private final List<UserLocation> locationList;

    private Intent selectLocationIntent;
    private Activity forDataReturn;

    /**
     * Class constructor
     * @param context Context to inflate from
     * @param locationList List of UserLocations
     */
    public LocationAdapter(Context context, List<UserLocation> locationList) {
        this.context = context;
        this.locationList = locationList;
    }

    /**
     * Tells the RecyclerView how to represent UserLocation objects.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds the inflated card_location layout.
     */
    @NonNull
    @Override
    public LocationAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_location, null);
        return new RequestViewHolder(view);
    }

    /**
     * Tells the RecyclerView how to display a UserLocation at a specified location.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *               item at the given position in the data set.
     * @param position The position in the list
     */
    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        UserLocation location = locationList.get(position);     // retrieve position of UserLocation

        // Get the address
        holder.textViewAddress.setText(location.getAddress().getAddressLine(0));

        // User clicks on map button
        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectLocation(location.getAddress(), position);
            }
        });

        // User clicks the delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Location")
                        .setMessage("Are you sure you want to delete this location?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                locationList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", null)).show();

                Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                buttonPositive.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
                Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                buttonNegative.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
            }
        });

        // User clicks the "select" button
        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: Update book status to accepted */
                Intent intent = new Intent(v.getContext(), HomeScreen.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    /**
     * This method is called to pass the address information to SelectLocationActivity
     *
     * @param address Address to send to SelectLocationActivity
     * @param index The position of the UserLocation in the list
     */
    private void startSelectLocation(Address address, int index) {
        selectLocationIntent = new Intent(context, SelectLocationActivity.class);
        selectLocationIntent.putExtra("INPUT_ADDRESS", address);
        selectLocationIntent.putExtra("LOCATION_INDEX", index);
        forDataReturn = (Activity) context;
        forDataReturn.startActivityForResult(selectLocationIntent,1);
    }

    // TODO: will this be used? ->
    //  We need to pull data from select activity. So if not this way then some other way
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        if (requestCode == 1){
            int index = data.getIntExtra("LOCATION_INDEX", -1);
            Address address = data.getParcelableExtra("OUTPUT_ADDRESS");
            locationList.remove(index);
            locationList.add(index, new UserLocation(address,null));
        }
    }

    /**
     * Returns the number of UserLocations in the adapter
     *
     * @return The number of UserLocations in the adapter
     */
    @Override
    public int getItemCount() {
        return locationList.size();
    }

    /**
     * @author Chase-Warwick
     * parseString takes in a string describing a location and if Geocoder is present attempts
     * to return an Address object representing the location. If Geocoder is not present or Geocoder
     * fails to provide an address then it returns an address with LatLng set to the middle of
     * Canada
     * @param context Context object representing current context
     * @param locationName String object representing a specified location
     * @return Address representing either the location specified in locationName or the middle of
     *         Canada
     */
    public static Address parseString(Context context, String locationName){
        Address returnValue = new Address(Locale.getDefault());
        returnValue.setLatitude(43.651070);
        returnValue.setLongitude(-79.347015);
        if (Geocoder.isPresent()) {
            Geocoder geocoder = new Geocoder(context);
            try {
                List<Address> possibleAddress = geocoder.getFromLocationName(
                        locationName, 1);
                if (possibleAddress.size() == 1) {
                    returnValue = possibleAddress.get(0);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        return returnValue;
    }

    /**
     * Caches Views from layout
     *
     * @see #onBindViewHolder(RequestViewHolder, int)
     */
    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAddress;

        Button deleteButton;
        Button selectButton;
        Button mapButton;

        /**
         * Class constructor.
         *
         * @param itemView Used to retrieve Views from layout file.
         */
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAddress = itemView.findViewById(R.id.text_address);

            mapButton = itemView.findViewById(R.id.button_map);
            deleteButton = itemView.findViewById(R.id.button_delete_location);
            selectButton = itemView.findViewById(R.id.button_select_location);
        }
    }
}
