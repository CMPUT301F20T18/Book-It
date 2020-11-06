package com.example.cmput301f20t18;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class SearchPageAdapter extends FragmentPagerAdapter {

    private Context context;
    private String[] tabTitles;
    private int numOfTabs;

    /**
     * Constructor of the class
     * @param fm
     * @param behavior
     * @param context
     */

    public SearchPageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.numOfTabs = behavior;
        this.context = context;
        tabTitles = new String[]{context.getResources().getText(R.string.search_available).toString(),
                context.getResources().getText(R.string.search_show_all).toString()};
    }

    /**
     * Method that returns tab count
     * @return numOFTabs
     */
    @Override
    public int getCount() {
        return numOfTabs;
    }

    /**
     * Gets the title of the tab
     * @param position
     * @return tabTitles
     */
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
                return new SearchAvailableFragment();
            case 1:
                return new SearchShowAllFragment();
            default:
                return null;
        }
    }

}

