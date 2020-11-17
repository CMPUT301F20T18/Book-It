package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URI;

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
    ImageView cover;
    Toolbar toolbar;
    private static final int RESULT_LOAD_IMAGE = 1;

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
        addPhoto = findViewById(R.id.add_image_button);


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

                String title = bookTitle.getText().toString();
                String book_author = author.getText().toString();
                Long book_isbn = Long.parseLong(isbn.getText().toString());
                int book_year = Integer.parseInt(year.getText().toString());

                current.ownerNewBook(book_isbn, title, book_author, book_year);
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

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();

                    float scaleWidth = ((float) addPhoto.getWidth()) / width;
                    float scaleHeight = ((float) addPhoto.getHeight()) / height;

                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);

                    Bitmap finalMap = Bitmap
                            .createBitmap(bitmap, 0, 0, width, height, matrix, false)
                            .copy(Bitmap.Config.ARGB_8888, true);
                    addPhoto.setImageBitmap(finalMap);
                }
    }
}

}