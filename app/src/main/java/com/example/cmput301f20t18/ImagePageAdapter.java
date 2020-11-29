package com.example.cmput301f20t18;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import java.util.ArrayList;

/**
 * This is the adapter that manages to enlarge photos from books and navigate
 * through the different photos
 * @see Book
 * @author Johnathon
 * */


public class ImagePageAdapter extends PagerAdapter {


    private Context mContext;
    private ArrayList<String> photos;

    /**
     * Initialize adapter (Constructor)
     * @param mContext So the context for the ViewPager
     * @param photos The photos of a book
     */

    public ImagePageAdapter(Context mContext, ArrayList<String> photos) {
        this.mContext = mContext;
        this.photos = photos;
    }

    /**
     * Obtains the amount of photos being displayed
     * @return the amount of photos of a book object, as an integer
     */

    @Override
    public int getCount() {
        return photos.size();
    }

    /**
     * This verifies if the View (image displayed) is the same as the object photo
     * @param view image being displayed
     * @param object The photo that is to be displayed
     * @return the comparision that checks if the view is the same as the object, as a boolean
     */

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    /**
     * This what enlarges the photo and part of what makes the slider
     * @param container Is what sets the PageViewer to be a ImageView displaying the photos
     * @param position Indicative of which photo should be displayed
     * @return returns the corresponding ImageView, as an object
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        Bitmap photo = photoAdapter.stringToBitmap(photos.get(position));

        Bitmap bm = photoAdapter.scaleBitmap(photo,
                411, //imageView.getLayoutParams().width,
                 667);// imageView.getLayoutParams().height);

        imageView.setImageBitmap(bm);
        container.addView(imageView,0);

        return imageView;
    }

    /**
     * This is what destroys the current ImageView so that the method instantiateItem
     * creates another ImageView with a different photo
     * @param container Is what sets the PageViewer
     * @param position The photos of a book
     * @param object Is the ImageView of the photo displayed
     */

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView) object);
    }

}
