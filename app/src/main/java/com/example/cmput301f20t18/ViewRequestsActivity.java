package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {
    static final String TAG = "VRA_DEBUG";

    RecyclerView recyclerView;
    FirestoreRequestAdapter adapter;
    Query query;
    int bookID;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference transRef = DB.collection("transactions");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);
        bookID = getIntent().getIntExtra("bookID", 0);
        Log.d(TAG, "bookID value:" + bookID);

        Toolbar toolbar = findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.view_requests_header));

        recyclerView = findViewById(R.id.request_recycler);
        setUp();
    }

    public void setUp() {
        query = transRef.whereEqualTo("owner_dbID", auth.getUid()).whereEqualTo("status", Transaction.STATUS_REQUESTED);
        FirestoreRecyclerOptions<Transaction> options = new FirestoreRecyclerOptions.Builder<Transaction>()
                .setQuery(query, Transaction.class)
                .build();

        adapter = new FirestoreRequestAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getBaseContext()));
        recyclerView.setAdapter(adapter);
    }



    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }
}