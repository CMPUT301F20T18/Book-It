package com.example.cmput301f20t18;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;


/**
 * This is a class that creates options for the use of the ISBN
 * The class is still under development so the elements that appear on screen are mostly visual
 * with the exception of cancel
 * @author Johnathon Gil
 * @author deinum
 */

public class PostScanActivity extends AppCompatActivity {

    Query query;
    RecyclerView recyclerViewMyBooks;
    FirestoreBookAdapter adapterMyBooks;
    RecyclerView recyclerViewBorrowed;
    FirestoreBorrowedAdapter adapterBorrowed;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference bookRef = DB.collection("books");
    CollectionReference userRef = DB.collection("users");

    TextView ISBN;
    Button searchCopies, addBook, backButton;
    String passed_isbn;
    private final static String TAG = "PSA_DEBUG";

    /**
     * This method has the purpose of creating the activity that supplies the options after a scan has been made.
     * Such options are "searching for available copies" or "add to mybooks"
     * @param  savedInstanceState this creates a state were the content of this class is shown
     * @see    View
     * @see    String
     * @see    Button
     * @see    TextView
     *
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_scan);
        passed_isbn = getIntent().getStringExtra("ISBN");
        if (passed_isbn.equals("")) {
            passed_isbn = "0";
        }

        Log.d(TAG, "onCreate: ISBN =  " + passed_isbn);

        // recycler for My Books
        recyclerViewMyBooks = findViewById(R.id.recyclerView_mybooks);
        recyclerViewMyBooks.setAdapter(adapterMyBooks);

        // recycler for Borrowed books
        recyclerViewBorrowed = findViewById(R.id.recyclerView_borrowed);
        recyclerViewBorrowed.setAdapter(adapterBorrowed);
        setUp();

        // message if no books are in My Books list
        TextView noResultsMyBooksTextView = findViewById(R.id.no_results_mybooks);
        noResultsMyBooksTextView.setText(R.string.mybooks_empy);
        adapterMyBooks.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                noResultsMyBooksTextView.setText("");
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapterMyBooks.getItemCount() == 0) {
                    noResultsMyBooksTextView.setText(R.string.mybooks_empy);
                }
            }
        });

        // message if no books are in Borrowed books list
        TextView noResultsBorrowedTextView = findViewById(R.id.no_results_borrowed);
        noResultsBorrowedTextView.setText(R.string.borrowed_empty);
        adapterBorrowed.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                noResultsBorrowedTextView.setText("");
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapterBorrowed.getItemCount() == 0) {
                    noResultsBorrowedTextView.setText(R.string.borrowed_empty);
                }
            }
        });

        //This text view would be modified with the number on the scanner
        ISBN = findViewById(R.id.isbn_text);
        ISBN.setText(passed_isbn);

        backButton = findViewById(R.id.back);
        searchCopies =  findViewById(R.id.search_copies);
        addBook =  findViewById(R.id.add_book_isbn);

        /**
         * This just returns to the HomeScreen Activity
         * This is a listner to be able to react to button press
         */

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // user hits search
        // search opens the search Fragment and populates the scanned ISBN

        /**
         * The functionality has yet to be developed
         * This is a listner to be able to react to button press
         */
        searchCopies.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                intent.putExtra("ISBN", passed_isbn);
                startActivity(intent);
            }
        });

        /**
         * The functionality has yet to be developed
         * This is a listner to be able to react to button press
         */
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyBooksAddBook.class);
                Log.d(TAG, "ONLeave: ISBN =  " + passed_isbn);
                intent.putExtra("filled_isbn", Long.parseLong(passed_isbn));
                intent.putExtra("type", MyBooksAddBook.ADD_SCAN);
                startActivityForResult(intent, 5);


            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        if (adapterMyBooks != null) {
            adapterMyBooks.startListening();
        }
        if (adapterBorrowed != null) {
            adapterBorrowed.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapterMyBooks != null) {
            adapterMyBooks.stopListening();
        }
        if (adapterBorrowed != null) {
            adapterBorrowed.stopListening();
        }
    }

    public void setUp() {
        // Getting list of books in My Books
        query = bookRef.whereEqualTo("owner_dbID", auth.getUid())
                .whereEqualTo("isbn", Long.parseLong(passed_isbn));

        FirestoreRecyclerOptions<Book> optionsMyBooks = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        adapterMyBooks = new FirestoreBookAdapter(optionsMyBooks, this);
        recyclerViewMyBooks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMyBooks.setAdapter(adapterMyBooks);

        // Getting list of books in Borrowed books
        query = userRef.document(auth.getUid()).collection("requested_books").whereEqualTo("isbn", Long.parseLong(passed_isbn));
        FirestoreRecyclerOptions<Book> optionsBorrowed = new FirestoreRecyclerOptions.Builder<Book>()
                .setQuery(query, Book.class)
                .build();

        adapterBorrowed = new FirestoreBorrowedAdapter(optionsBorrowed);
        recyclerViewBorrowed.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewBorrowed.setAdapter(adapterBorrowed);
    }
}