package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;


/**
 * Handles displaying pickup addresses for users
 * @see User
 * @author deinum
 */
public class FirestoreLocationAdapter extends FirestoreRecyclerAdapter<UserLocation, FirestoreLocationAdapter.locationViewHolder> {

    private int bookID;
    static final String TAG = "FLA_DEBUG";
    Context context;

    public FirestoreLocationAdapter(@NonNull FirestoreRecyclerOptions<UserLocation> options, int bookID, Context context) {
        super(options);
        this.bookID = bookID;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull FirestoreLocationAdapter.locationViewHolder holder, int position, @NonNull UserLocation location) {

        Log.d(TAG, "onBindViewHolder: Title: " + location.getTitle());
            holder.pickup_location.setText(location.getTitle());
            holder.selectLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User current = new User();
                    current.ownerSetPickupLocation(location, bookID);
                    ((Activity)context).finish();

                }
            });

            holder.deleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User current = new User();
                }
            });
        }


    @NonNull
    @Override
    public FirestoreLocationAdapter.locationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new locationViewHolder(inflater.inflate(R.layout.card_location, null));
    }

    public class locationViewHolder extends RecyclerView.ViewHolder {
        TextView pickup_location;
        Button selectLocation;
        Button deleteLocation;


        public locationViewHolder(@NonNull View itemView) {
            super(itemView);
            pickup_location = itemView.findViewById(R.id.text_address);
            selectLocation = itemView.findViewById(R.id.button_select_location);
            deleteLocation = itemView.findViewById(R.id.button_delete_location);
        }
    }
}
