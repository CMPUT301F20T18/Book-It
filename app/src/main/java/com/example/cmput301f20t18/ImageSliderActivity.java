package com.example.cmput301f20t18;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity {


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
                finishActivity(1);

            }
        });


    }
}
