package com.example.cmput301f20t18;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreNotificationAdapter extends FirestoreRecyclerAdapter<UserNotification, FirestoreNotificationAdapter.NotifViewHolder> {


    /**
     * Notification adapter that listens in real time for changes to the query result
     * @author deinum
     * @param options Options (including query) for the adapter
     */
    public FirestoreNotificationAdapter(@NonNull FirestoreRecyclerOptions<UserNotification> options) {
        super(options);
    }

    /**
     * Bind values to our view
     * @param holder Our view object
     * @param position The position in our list
     * @param model Our notification being binded
     */
    @Override
    protected void onBindViewHolder(@NonNull NotifViewHolder holder, int position, @NonNull UserNotification model) {
        holder.notification_text.setText(model.getMessage());
    }

    /**
     * Specify our view
     * @param parent
     * @param viewType
     * @return
     */
    @NonNull
    @Override
    public FirestoreNotificationAdapter.NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotifViewHolder(inflater.inflate(R.layout.card_notification, null));
    }


    /**
     * Specify what our view looks like
     */
    public class NotifViewHolder extends RecyclerView.ViewHolder {

        TextView notification_text;

        /**
         * Find elements in our view
         * @param itemView
         */
        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_text= itemView.findViewById(R.id.text_notification);
         }
    }




}
