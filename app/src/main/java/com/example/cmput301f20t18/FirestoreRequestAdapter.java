package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;
import java.util.Objects;

/**
 * Displays all the requests a user has for a book, updates in real time
 * Using a firestore query
 * @see User
 * @see Book
 * @author deinum
 * @author butter
 */
public class FirestoreRequestAdapter extends FirestoreRecyclerAdapter<Transaction, FirestoreRequestAdapter.requestViewHolder> {

    final static String TAG = "FRA_DEBUG";
    public FirestoreRequestAdapter(@NonNull FirestoreRecyclerOptions<Transaction> options) {
        super(options);
    }


    /**
     *
     * @param holder The view object we are binding to
     * @param position The position in our list
     * @param transaction The transaction we are binding
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(@NonNull FirestoreRequestAdapter.requestViewHolder holder, int position, @NonNull Transaction transaction) {
        holder.borrower_name.setText(transaction.getBorrower_username());
        String requestName = transaction.getBorrower_username();
        Log.d(TAG, "Requester is " + requestName);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection("users");
        collection.whereEqualTo("username", requestName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List borrowers = task.getResult().toObjects(User.class);
                        if(borrowers.size()>0) {
                            User borrower = (User) borrowers.get(0);
                            String photoString = borrower.getProfile_picture();

                            if (!photoString.equals("") && photoString != null) {
                                Bitmap bm = photoAdapter.stringToBitmap(photoString);
                                Bitmap photo = photoAdapter.makeCircularImage(bm, holder.profile_pic.getHeight());
                                holder.profile_pic.setImageBitmap(photo);
                                Log.d(TAG, "Picture attached");
                            }
                        }
                }

                else {
                    Log.d(TAG, "Error Querying for borrower information");
                }
            }
        });

        // hits the accept button
        holder.accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)v.getContext()).finish();
                Intent intent = new Intent(v.getContext(), InitializeLocationActivity.class);
                intent.putExtra("bookID", transaction.getBookID());
                intent.putExtra("t_id", transaction.getID());
                v.getContext().startActivity(intent);
            }
        });

        holder.delete_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                User current = new User();
                current.ownerDenyRequest(transaction.getID());
            }
        });
    }


    /**
     * Returns the view for a particular element
     * @param parent The parent view
     * @param viewType The view type for this element
     * @return A view
     */
    @NonNull
    @Override
    public requestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new FirestoreRequestAdapter.requestViewHolder(inflater.inflate(R.layout.card_user_request, null));
    }


    /**
     * Defines a view for a request
     */
    public class requestViewHolder extends RecyclerView.ViewHolder {

        Button delete_button;
        Button accept_button;
        TextView borrower_name;
        ImageView profile_pic;


        /**
         * Find elements for our view
         * @param itemView Access to our views and elements
         */
        public requestViewHolder(@NonNull View itemView) {
            super(itemView);
            delete_button = itemView.findViewById(R.id.button_delete_request);
            accept_button = itemView.findViewById(R.id.button_accept_request);
            borrower_name = itemView.findViewById(R.id.text_username);
            profile_pic = itemView.findViewById(R.id.profile_view);

        }
    }
}
