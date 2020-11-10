package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is a class that creates a new book object through user input
 * @author  Jacob Deinum
 * @author Johnathon Gil
 * @see    Toolbar
 * @see    FirebaseAuth
 * @see    FirebaseFirestore
 * @see    CollectionReference
 */

public class MyBooksAddBook extends AppCompatActivity {

    TextView labelAuthor, labelTitle, labelYear, labelISBN;
    EditText author, bookTitle, year, isbn;
    Button done, cancel;
    Toolbar toolbar;
    FirebaseAuth auth;
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference books;
    CollectionReference users;


    //ImageButton addPic;

    /**
     * This method has the purpose of creating the activity that prompts the user to add information
     * to be able to add a book of theirs into the collection
     * Such input asked are title, author, year and ISBN
     * This class is still under development so their is a case for program crashing
     * @param  savedInstanceState this creates a state were the content of this class is shown
     *
     */

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

        // establish connection to DB
        auth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();
        books = DB.collection("system").document("System").collection("books");
        users = DB.collection("system").document("System").collection("users");


        /**
         * This method adds the values of the input into the FireStore database
         * This is a listener to be able to react to button press that ultimately creates
         * a book from the user input.
         */
        // This part is still in development
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference Baseref = db.getReference();
                DatabaseReference max_book = Baseref.child("max_book_id");
                max_book.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String addTitle = bookTitle.getText().toString();
                        String addAuthor = author.getText().toString();
                        int addYear = Integer.parseInt(year.getText().toString());
                        long addISBN = Long.parseLong(isbn.getText().toString());

                        Task<DocumentSnapshot> current_user = users.document(auth.getUid()).get();
                        current_user.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                User user = task.getResult().toObject(User.class);
                                int id = snapshot.getValue(Integer.class);

                                Book newBook = new Book(addTitle,addISBN,addAuthor,id,Book.STATUS_AVAILABLE,user,addYear);
                                books.document(Integer.toString(id)).set(newBook);



                                max_book.setValue(id + 1);
                            }
                        });

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });






                finish();

            }
        });

        /**
         * This just returns to the MyBook Fragment Activity
         * This is a listener to be able to react to button press
         */

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}