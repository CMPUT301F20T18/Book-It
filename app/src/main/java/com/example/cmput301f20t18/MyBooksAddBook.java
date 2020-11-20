package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;

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
    ImageButton addPhoto;

    ArrayList<Bitmap> photos;

    Toolbar toolbar;
    ImageButton addPic;
    private static final int RESULT_LOAD_IMAGE = 1;
    public static final int EDIT_BOOK = 10;
    public static final int ADD_BOOK = 11;
    public static final int ADD_SCAN = 12;

    private final static String TAG = "MBAB_DEBUG";
    private int type;
    private int bookID;
    private long passed_isbn;

    FirebaseFirestore DB;



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

        DB = FirebaseFirestore.getInstance();


        type = getIntent().getIntExtra("type", 0);
        bookID = getIntent().getIntExtra("bookID", 0);
        Long passed_isbn = getIntent().getLongExtra("filled_isbn", 0);


        Log.d(TAG, "onCreate: type " + type);
        Log.d(TAG, "onCreate: bookID " + bookID);
        Log.d(TAG, "onCreate: isbn " + passed_isbn);

        if (type == ADD_BOOK || type == ADD_SCAN)  {
            setContentView(R.layout.activity_my_books_add_book);
        }
        else if ( type == EDIT_BOOK) {
            setContentView(R.layout.activity_edit_books);
        }

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
        addPhoto = findViewById(R.id.add_image_button);

        photos = new ArrayList<>();

        if (type == EDIT_BOOK) {
            DB.collection("books").document(Integer.toString(bookID)).get().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Book book = task.getResult().toObject(Book.class);
                    bookTitle.setText(book.getTitle());
                    author.setText(book.getAuthor());
                    year.setText(Integer.toString(book.getYear()));
                    isbn.setText(Long.toString(book.getISBN()));
                }

                else {
                    Log.d(TAG, "MyBooksAddBook - Error querying for book");
                }

            });

        }

        if (type == ADD_SCAN) {
            isbn.setText(Long.toString(passed_isbn));
        }








        /**
         * This method adds the values of the input into the FireStore database
         * This is a listener to be able to react to button press that ultimately creates
         * a book from the user input.
         */
        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                User current = new User();

                String book_title = bookTitle.getText().toString();
                String book_author = author.getText().toString();
                String book_isbn = isbn.getText().toString();
                String book_year = year.getText().toString();

                ArrayList<String> stringPhotos = new ArrayList<>();
                for (Bitmap bmp : photos){
                    stringPhotos.add(photoAdapter.bitmapToString(bmp));
                }


                // debug info
                Log.d(TAG, "onClick: Title " + book_title);
                Log.d(TAG, "onClick: Author " + book_author);
                Log.d(TAG, "onClick: ISBN " + book_isbn );
                Log.d(TAG, "onClick: Year " + book_year);
                Log.d(TAG, "onClick: bookID " + bookID);
                Log.d(TAG, "onClick: Type of Add " + type);

                Long isbn = Long.parseLong(book_isbn);
                Integer year = Integer.parseInt(book_year);
                if (type == ADD_BOOK) {
             
                    if (CheckBookValidity.bookValid(book_title, book_author, book_isbn, book_year)){

                        Log.d(TAG, "Validity check passed");
                        ArrayList<String> photoStrings = new ArrayList<>();
                        for (Bitmap photo: photos){
                            photoStrings
                                    .add(photoAdapter.bitmapToString(photo));
                        }
                        current.ownerNewBook(isbn, book_title, book_author, year, photoStrings);
                    }
                }

                else if (type == EDIT_BOOK) {
                    current.ownerEditBook(book_title, book_author, isbn, bookID, year);

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

        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPicture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPicture, RESULT_LOAD_IMAGE);
            }
        });
    }
    //Work in progress
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri image = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int colIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(colIndex);
                    cursor.close();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                    Bitmap outMap = photoAdapter.scaleBitmap(bitmap, (float) addPhoto.getWidth(), (float) addPhoto.getHeight());
                    photos.add(outMap);
                    addPhoto.setImageBitmap(outMap);
                }
        }
    }
}
/**
 * CheckBookValidity is a static class which can be used to check if user input will generate
 * a valid book
 * @author Chase Warwick
 */
class CheckBookValidity {
    /**
     * bookValid is a method which takes in EditText objects and passed them to other methods
     * which evaluate if the text contained within each EditText object is a valid input
     *
     * @param bookTitle  String object containing the book title
     * @param bookAuthor String object containing the book author
     * @param bookISBN   String object containing the book ISBN
     * @param bookYear   String object containing the book's date of publish
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
    private static boolean checkTitle(String bookTitle) {
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
    private static boolean checkAuthor(String bookAuthor) {
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
    private static boolean checkISBN(String bookISBN) {
        boolean valid = true;
        final long MIN_VAL = 10000000L;
        final long MAX_VAL = 9999999999999L;

        Long isbnNum = -1L;

        try {
            isbnNum = Long.parseLong(bookISBN);
        } catch (NumberFormatException numberFormat) {
            return false;
        }

        try {
            if (!CheckInput.checkWithinRange(isbnNum, MIN_VAL, MAX_VAL)) {
                throw new CheckInput.OutOfRangeException("ISBN is out of possible range");
            }
        } catch (CheckInput.OutOfRangeException outOfRange) {
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
    private static boolean checkYear(String bookYear) {
        final int MIN_VAL = 0;
        final int MAX_VAL = Calendar.getInstance().get(Calendar.YEAR);
        int yearNum = -1;

        try {
            yearNum = Integer.parseInt(bookYear);
        } catch (NumberFormatException numberFormat) {
            return false;
        }

        try {
            if (!CheckInput.checkWithinRange(yearNum, MIN_VAL, MAX_VAL)) {
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
    private static class CheckInput {
        /**
         * checkNonempty is a method which can be used to determine if a string is nonempty
         *
         * @param string String object to be checked
         * @return boolean object representing whether the string is nonempty
         */
        private static boolean checkNonempty(String string) {
            return !string.equals("");
        }

        /**
         * checkWithinRange is an overloaded method which determines if a Number object
         * is within a set range
         *
         * @param number Number object containing the number to be checked
         * @param minVal Number object containing the minimum value for number
         * @param maxVal Number object containing the maximum value for number
         * @return boolean object representing whether the number is within the range or not
         */
        private static boolean checkWithinRange(int number, int minVal, int maxVal) {
            return number >= minVal && number <= maxVal;
        }

        private static boolean checkWithinRange(long number, long minVal, long maxVal) {
            return number >= minVal && number <= maxVal;
        }

        /**
         * A custom exception to throw when user input is outside of set range
         */
        private static class OutOfRangeException extends Exception {
            OutOfRangeException(String errorMessage) {
                super(errorMessage);
            }
        }
    }
}
