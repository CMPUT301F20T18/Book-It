package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

public class MyBooksAddBook extends AppCompatActivity {

    TextView labelAuthor, labelTitle, labelYear, labelISBN;
    EditText author, bookTitle, year, isbn;
    Button done, cancel;
    Toolbar toolbar;
    //ImageButton addPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_books_add_book);

        labelAuthor = findViewById(R.id.author_prompt);
        labelTitle = findViewById(R.id.book_title_prompt);
        labelYear = findViewById(R.id.year_prompt);
        labelISBN = findViewById(R.id.isbn_prompt);

        author = findViewById(R.id.author_input);
        bookTitle = findViewById(R.id.title_input);
        year = findViewById(R.id.year_input);
        isbn = findViewById(R.id.isbn_input);

        done = findViewById(R.id.done_add_book);
        cancel = findViewById(R.id.return_to_my_books);

        // This part is still in development
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

 /*             String addTitle = bookTitle.getText().toString();
                String addAuthor = author.getText().toString();
                int addYear = Integer.parseInt(year.getText().toString());
                long addISBN = Long.parseLong(isbn.getText().toString());

                Book newBook = new Book(addTitle,addISBN,addAuthor,0,0,null,addYear);
*/
                finish();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}