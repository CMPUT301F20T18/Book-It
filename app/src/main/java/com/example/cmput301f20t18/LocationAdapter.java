package com.example.cmput301f20t18;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Custom RecyclerView Adapter for UserLocation objects
 * ** IN PROGRESS **
 */
public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.RequestViewHolder> {

    private final String TAG = "LocationAdapter";
    private Context context;
    private List<UserLocation> locationList;

    public LocationAdapter(Context context, List<UserLocation> locationList) {
        this.context = context;
        this.locationList = locationList;
    }


    @NonNull
    @Override
    public LocationAdapter.RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.card_location, null);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {

        UserLocation location = locationList.get(position);

        holder.textViewAddress.setText(location.getAddress().getAddressLine(0));

        holder.mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectLocationActivity.class);
                v.getContext().startActivity(intent);
            }
        });

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

        holder.selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // UPDATE BOOK STATUS TO ACCEPTED
                Intent intent = new Intent(v.getContext(), HomeScreen.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        TextView textViewAddress;

        Button deleteButton;
        Button selectButton;
        Button mapButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewAddress = itemView.findViewById(R.id.text_address);
            mapButton = itemView.findViewById(R.id.button_map);
            deleteButton = itemView.findViewById(R.id.button_delete_location);
            selectButton = itemView.findViewById(R.id.button_select_location);
        }
    }
}
