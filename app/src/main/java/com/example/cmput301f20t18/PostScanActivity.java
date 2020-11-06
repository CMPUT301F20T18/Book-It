package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PostScanActivity extends AppCompatActivity {

    TextView ISBN, noBooksMB, noBooksB;
    Button searchCopies, addBook, backButton;
    String passed_isbn;

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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        searchCopies.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Search by isbn
            }
        });

        addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: goto MyBooksAddBook
            }
        });

    }
}