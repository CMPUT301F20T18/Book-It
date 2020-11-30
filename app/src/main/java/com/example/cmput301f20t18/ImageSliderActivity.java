package com.example.cmput301f20t18;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.ref.Reference;
import java.sql.Ref;
import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity {

    /**
     * This is the main activity for the ImageSlider in which we will enlarge the photos
     * from a particular book so the user can go through them
     * @author Johnathon Gil
     * @author Sean Butler
     * @see Book
     * */

    ViewPager mPager;
    ImagePageAdapter imageAdapter;
    Button sliderButton;

    FirebaseFirestore DB;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        sliderButton = findViewById(R.id.slider_return_button);

        mPager = findViewById(R.id.slider_viewer);
        int bookID = (int) getIntent().getExtras().get("ID");
        DB = FirebaseFirestore.getInstance();
        //This finds the particular book wanting to view the pictures of
        DB.collection("books").document(Integer.toString(bookID)).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Book book = task.getResult().toObject(Book.class);
                ArrayList<String> photos = book.getPhotos();
                imageAdapter = new ImagePageAdapter(this, photos);
                mPager.setAdapter(imageAdapter);
            }
        });

        sliderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
