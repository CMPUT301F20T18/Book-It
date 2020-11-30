package com.example.cmput301f20t18;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Allows user to swipe between tabs in My Books
 * Reference: https://www.codingdemos.com/android-tablayout-example-viewpager/
 */
public class MyBooksPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles;
    private int numOfTabs;

    public MyBooksPageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.numOfTabs = behavior;
        this.context = context;
        tabTitles = new String[]{context.getResources().getText(R.string.mybooks_available).toString(),
                context.getResources().getText(R.string.mybooks_pending).toString(),
                context.getResources().getText(R.string.mybooks_lending).toString()};
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return new MyBooksAvailableFragment();
            case 1:
                return new MyBooksPendingFragment();
            case 2:
                return new MyBooksLendingFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}


