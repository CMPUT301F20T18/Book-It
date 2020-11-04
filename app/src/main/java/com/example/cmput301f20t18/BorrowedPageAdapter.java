package com.example.cmput301f20t18;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * Allows user to swipe between tabs in Borrowed
 */
public class BorrowedPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles;
    private int numOfTabs;

    public BorrowedPageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.numOfTabs = behavior;
        this.context = context;
        tabTitles = new String[]{context.getResources().getText(R.string.borrowed_requested).toString(),
                context.getResources().getText(R.string.borrowed_pending).toString(),
                context.getResources().getText(R.string.borrowed_borrowing).toString()};
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
                return new BorrowedRequestedFragment();
            case 1:
                return new BorrowedPendingFragment();
            case 2:
                return new BorrowedBorrowingFragment();
            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        return numOfTabs;
    }
}
