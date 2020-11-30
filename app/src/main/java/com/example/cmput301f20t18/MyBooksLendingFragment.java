package com.example.cmput301f20t18;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * A {@link Fragment} subclass that is responsible for displaying books a user owns
 * Firebase manages this adapter and will update in real time based on writes to firestore.
 * @see MyBooksAvailableFragment
 * @see MyBooksPendingFragment
 * @see FirestoreMyBooksAdapter
 * @author deinum
 * @author shuval
 */
public class MyBooksLendingFragment extends Fragment {

    RecyclerView recyclerView;
    TextView noResultsTextView;
    Query query;
    FirestoreMyBooksAdapter adapter;
    final static String TAG = "MBLF";

    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference bookRef = DB.collection("books");

    public MyBooksLendingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Instantiates view. The documentation recommends only inflating the layout here and doing
     * everything else in {@link #onViewCreated(View, Bundle)}.
     *
     * @param inflater Used to inflate view
     * @param container Parent view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_books_available, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        setUp();

        noResultsTextView = view.findViewById(R.id.no_results);
        noResultsTextView.setText(R.string.mybooks_lending_empty);

        // display message if list of books is empty
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                noResultsTextView.setText("");
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount() == 0) {
                    noResultsTextView.setText(R.string.mybooks_lending_empty);
                }
            }
        });

        return view;
    }



    // tell our adapter to start listening as soon as the fragment begins
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }


    }
    // tell our adapter to stop listening as soon as the fragment ends
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    /**
     * Sets up our recyclerview, including defining the query which will populate the list
     */
    public void setUp() {
        query = bookRef.whereEqualTo("owner_dbID", auth.getUid()).whereEqualTo("status", Book.STATUS_BORROWED);
        FirestoreRecyclerOptions<Book> options = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        adapter = new FirestoreMyBooksAdapter(options, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }
}