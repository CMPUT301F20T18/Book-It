package com.example.cmput301f20t18;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} subclass that allows the user to swipe between tabs in My Books.
 * Reference: https://www.codingdemos.com/android-tablayout-example-viewpager/
 */
public class MyBooksPageAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final String[] tabTitles;     // Stores titles for the tabs
    private final int numOfTabs;

    /**
     * Class constructor.
     *
     * @param fm Fragment manager.
     * @param behavior Number of tabs.
     * @param context Context passed from caller.
     */
    public MyBooksPageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.numOfTabs = behavior;
        this.context = context;
        tabTitles = new String[]{context.getResources().getText(R.string.mybooks_available).toString(),
                context.getResources().getText(R.string.mybooks_pending).toString(),
                context.getResources().getText(R.string.mybooks_lending).toString()};
    }

    /**
     * This method is called by the ViewPager to obtain a title string
     * to describe the specified tab.
     *
     * @param position The position of the title requested.
     * @return A title for the requested page.
     */
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    /**
     * Returns the fragment for the tab at the given position.
     *
     * @param position which tab is selected.
     * @return fragment for tab.
     */
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

    /**
     * Returns number of tabs.
     *
     * @return returns number of tabs.
     */
    @Override
    public int getCount() {
        return numOfTabs;
    }
}


