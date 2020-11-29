package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * Display all the requests for a given book to the user
 * @see User
 * @see FirestoreRequestAdapter
 * @author deinum
 */
public class ViewRequestsActivity extends AppCompatActivity {
    static final String TAG = "VRA_DEBUG";

    RecyclerView recyclerView;
    FirestoreRequestAdapter adapter;
    Query query;
    int bookID;

    // Database info
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference transRef = DB.collection("transactions");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        bookID = getIntent().getIntExtra("bookID", 0);
        Log.d(TAG, "bookID value:" + bookID);

        // Clicks back button
        findViewById(R.id.back_requests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView = findViewById(R.id.request_recycler);
        setUp();



    }

    /**
     * Sets up our recyclerview, including defining the query which will populate the list
     */
    public void setUp() {
        query = transRef.whereEqualTo("owner_dbID", auth.getUid()).whereEqualTo("status", Transaction.STATUS_REQUESTED).whereEqualTo("bookID", bookID);
        FirestoreRecyclerOptions<Transaction> options = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query, Transaction.class)
                .build();

        adapter = new FirestoreRequestAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        recyclerView.setAdapter(adapter);
    }


    // tell our adapter to start listening as soon as the activity begins
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }
    // tell our adapter to stop listening as soon as the activity ends
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}