package com.example.cmput301f20t18;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class ImageSliderActivity extends AppCompatActivity {


    ViewPager mPager;
    ImagePageAdapter imageAdapter;
    Button sliderButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_slider);

        ArrayList<Bitmap> photos = (ArrayList<Bitmap>) getIntent().getExtras().get("Photos");

        sliderButton = findViewById(R.id.slider_return_button);

        mPager = findViewById(R.id.slider_viewer);
        imageAdapter = new ImagePageAdapter (this, photos);
        mPager.setAdapter(imageAdapter);

        sliderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivity(1);

            }
        });

    }
}