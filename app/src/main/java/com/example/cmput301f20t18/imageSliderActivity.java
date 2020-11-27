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

import java.util.ArrayList;

class ImageSliderActivity extends AppCompatActivity {

    ViewPager mPager;
    ImagePageAdapter imageAdapter;
    Button sliderButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        sliderButton = findViewById(R.id.slider_return_button);

        ArrayList<String > photoStrings = (ArrayList<String >) getIntent().getExtras().get("Photos");
        mPager = findViewById(R.id.slider_viewer);
        ArrayList<Bitmap> photos = new ArrayList<>();
        for(String string: photoStrings){
            photos.add(photoAdapter.stringToBitmap(string));
        }
        imageAdapter = new ImagePageAdapter (getSupportFragmentManager(),1, photos);
        mPager.setAdapter(imageAdapter);

        sliderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(1);


            }
        });

    }
}