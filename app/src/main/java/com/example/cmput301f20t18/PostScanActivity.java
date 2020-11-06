package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

/**
 * This is a class that creates options for the use of the ISBN
 * The class is still under development so the elements that appear on screen are mostly visual
 * with the exception of cancel
 * @author Johnathon Gil
 * @author
 */

public class PostScanActivity extends AppCompatActivity {

    TextView ISBN, noBooksMB, noBooksB;
    Button searchCopies, addBook, backButton;
    String passed_isbn;

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



        //This text view would be modified with the number on the scanner
        ISBN = findViewById(R.id.isbn_text);
        ISBN.setText(passed_isbn);
        //These TextViews would be turn on or off depending if their was a book in the database or not
        noBooksMB = findViewById(R.id.nothingfound_mybooks);
        noBooksB = findViewById(R.id.nothingfound_borrowed);

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
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * The functionality has yet to be developed
         * This is a listner to be able to react to button press
         */
        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }
}