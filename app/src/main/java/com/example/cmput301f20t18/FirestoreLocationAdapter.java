package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
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
    private int t_id;
    static final String TAG = "FLA_DEBUG";
    Context context;

    /**
     * Default constructor for the object
     * @param options Options for the adapter
     * @param bookID The bookID of the book
     * @param context The context where the adapter is used in
     * @param t_id The transaction ID to update the location of
     */
    public FirestoreLocationAdapter(@NonNull FirestoreRecyclerOptions<UserLocation> options, int bookID, Context context, int t_id) {
        super(options);
        this.bookID = bookID;
        this.context = context;
        this.t_id = t_id;
    }

    /**
     * Binds values to our view
     * @param holder The view object
     * @param position The position in the list
     * @param location The location object being binded to the view
     */
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


            // owner deletes a location from the their pickup_locations
            holder.deleteLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User current = new User();
                    current.ownerDeleteLocation(location);
                }
            });
        }


    /**
     * Specify our view
     * @param parent The parent view
     * @param viewType The viewtype for this view
     * @return The view for a list element
     */
    @NonNull
    @Override
    public FirestoreLocationAdapter.locationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new locationViewHolder(inflater.inflate(R.layout.card_location, null));
    }

    /**
     * Specifies the elements in our view
     */
    public class locationViewHolder extends RecyclerView.ViewHolder {
        TextView pickup_location;
        Button selectLocation;
        Button deleteLocation;


        /**
         * Find the elements to update for our view
         * @param itemView
         */
        public locationViewHolder(@NonNull View itemView) {
            super(itemView);
            pickup_location = itemView.findViewById(R.id.text_address);
            selectLocation = itemView.findViewById(R.id.button_select_location);
            deleteLocation = itemView.findViewById(R.id.button_delete_location);
        }
    }
}
