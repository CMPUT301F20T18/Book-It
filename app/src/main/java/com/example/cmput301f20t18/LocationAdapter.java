package com.example.cmput301f20t18;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

        //holder.imageView.setImageResource(user.getProfilePicture);
        /*holder.textViewUsername.setText(location.getAddress().getAddressLine(0));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = (new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete Request")
                        .setMessage("Are you sure you want to delete this request?")
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

        holder.acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                v.getContext().startActivity(intent);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }

    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView textViewUsername;

        Button deleteButton;
        Button acceptButton;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.profile_view);
            textViewUsername = itemView.findViewById(R.id.text_username);
            deleteButton = itemView.findViewById(R.id.button_delete_request);
            acceptButton = itemView.findViewById(R.id.button_accept_request);
        }
    }
}
