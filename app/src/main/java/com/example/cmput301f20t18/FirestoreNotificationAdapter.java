package com.example.cmput301f20t18;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class FirestoreNotificationAdapter extends FirestoreRecyclerAdapter<userNotification, FirestoreNotificationAdapter.NotifViewHolder> {


    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreNotificationAdapter(@NonNull FirestoreRecyclerOptions<userNotification> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NotifViewHolder holder, int position, @NonNull userNotification model) {
        holder.notification_text.setText(model.getMessage());
    }

    @NonNull
    @Override
    public FirestoreNotificationAdapter.NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new NotifViewHolder(inflater.inflate(R.layout.card_notification, null));
    }


    public class NotifViewHolder extends RecyclerView.ViewHolder {

        TextView notification_text;

        public NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            notification_text= itemView.findViewById(R.id.text_notification);
         }
    }




}
