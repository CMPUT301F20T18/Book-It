package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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


        /**
         * This method adds the values of the input into the FireStore database
         * This is a listener to be able to react to button press that ultimately creates
         * a book from the user input.
         */
        // TODO add input verification
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User current = new User();

                String book_title = bookTitle.getText().toString();
                String book_author = author.getText().toString();
                String book_isbn = isbn.getText().toString();
                String book_year = year.getText().toString();

                if (CheckBookValidity.bookValid(book_title, book_author, book_isbn, book_year)){
                    Long isbn = Long.parseLong(book_isbn);
                    Integer year = Integer.parseInt(book_year);
                    current.ownerNewBook(isbn, book_title, book_author, year);
                }
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
/**
 * CheckBookValidity is a static class which can be used to check if user input will generate
 * a valid book
 * @author Chase Warwick
 */
class CheckBookValidity{
    /** bookValid is a method which takes in EditText objects and passed them to other methods
     * which evaluate if the text contained within each EditText object is a valid input
     *
     * @param bookTitle String object containing the book title
     * @param bookAuthor String object containing the book author
     * @param bookISBN String object containing the book ISBN
     * @param bookYear String object containing the book's date of publish
     * @return boolean object representing whether the text contained within each EditText
     * will create a valid Book object
     */
    public static boolean bookValid(String bookTitle, String bookAuthor, String bookISBN,
                                     String bookYear) {
        return CheckBookValidity.checkTitle(bookTitle)
                && CheckBookValidity.checkAuthor(bookAuthor)
                && CheckBookValidity.checkISBN(bookISBN)
                && CheckBookValidity.checkYear(bookYear);
    }

    /**
     * checkTitle takes in an EditText object, reads the text contained within it,
     * then checks if the text is a valid title for a Book object
     *
     * @param bookTitle String object containing the book title
     * @return boolean object representing whether the text contained within the EditText is a
     * valid title for a Book object
     */
    private static boolean checkTitle(String bookTitle){
        return CheckInput.checkNonempty(bookTitle);
    }

    /**
     * checkAuthor takes in an EditText object, reads the text contained within it,
     * then checks if the text is a valid author for a Book object
     *
     * @param bookAuthor String object containing the book author
     * @return boolean object representing whether the text contained within the EditText is a
     * valid author for a Book object
     */
    private static boolean checkAuthor(String bookAuthor){
        return CheckInput.checkNonempty(bookAuthor);
    }

    /**
     * checkISBN takes in an EditText object, reads the text contained within it,
     * then checks if the text is a valid ISBN for a Book object
     *
     * @param bookISBN String object containing the book ISBN
     * @return boolean object representing whether the text contained within the EditText is a
     * valid ISBN for a Book object
     */
    private static boolean checkISBN(String bookISBN){
        boolean valid = true;
        final long MIN_VAL = 1000000000000L;
        final long MAX_VAL = 9999999999999L;

        Long isbnNum = -1L;

        try{
            isbnNum = Long.parseLong(bookISBN);
        } catch (NumberFormatException numberFormat) {
            return false;
        }

        try{
            if (!CheckInput.checkWithinRange(isbnNum, MIN_VAL, MAX_VAL)){
                throw new CheckInput.OutOfRangeException("ISBN is out of possible range");
            }
        } catch (CheckInput.OutOfRangeException outOfRange){
            return false;
        }

        return true;
    }

    /**
     * checkYear takes in an EditText object, reads the text contained within it,
     * then checks if the text is a valid publish date for a Book object
     *
     * @param bookYear String object containing the book's date of publish
     * @return boolean object representing whether the text contained within the EditText is a
     * valid publish date for a Book object
     */
    private static boolean checkYear(String bookYear){
        final int MIN_VAL = 0;
        final int MAX_VAL = 2147483647;
        int yearNum = -1;

        try{
            yearNum = Integer.parseInt(bookYear);
        } catch (NumberFormatException numberFormat) {
            return false;
        }

        try{
            if (!CheckInput.checkWithinRange(yearNum, MIN_VAL, MAX_VAL)){
                throw new CheckInput.OutOfRangeException("Year is out of possible range");
            }
        } catch (CheckInput.OutOfRangeException outOfRange) {
            return false;
        }

        return true;
    }

    /**
     * CheckInput is a static class which can be used to check if user input is valid in various
     * ways
     */
    private static class CheckInput{
        /**
         * checkNonempty is a method which can be used to determine if a string is nonempty
         * @param string String object to be checked
         * @return boolean object representing whether the string is nonempty
         */
        private static boolean checkNonempty(String string){
            return !string.equals("");
        }

        /**
         * checkWithinRange is an overloaded method which determines if a Number object
         * is within a set range
         * @param number Number object containing the number to be checked
         * @param minVal Number object containing the minimum value for number
         * @param maxVal Number object containing the maximum value for number
         * @return boolean object representing whether the number is within the range or not
         */
        private static boolean checkWithinRange(int number, int minVal, int maxVal){
            return number >= minVal && number <= maxVal;
        }
        private static boolean checkWithinRange(long number, long minVal, long maxVal){
            return number >= minVal && number <= maxVal;
        }
        /**
         * A custom exception to throw when user input is outside of set range
         */
        private static class OutOfRangeException extends Exception{
            OutOfRangeException(String errorMessage){
                super(errorMessage);
            }
        }
    }
}
