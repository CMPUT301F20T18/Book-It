package com.example.cmput301f20t18;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

/**
 * A {@link FragmentPagerAdapter} subclass that allows the user to swipe between tabs in Borrowed.
 * Reference: https://www.codingdemos.com/android-tablayout-example-viewpager/
 */
public class BorrowedPageAdapter extends FragmentPagerAdapter {

    private final Context context;
    private final String[] tabTitles;   // Stores titles for the tabs
    private final int numOfTabs;

    /**
     * Class constructor.
     *
     * @param fm Fragment manager.
     * @param behavior Number of tabs.
     * @param context Context passed from caller.
     */
    public BorrowedPageAdapter(@NonNull FragmentManager fm, int behavior, Context context) {
        super(fm, behavior);
        this.numOfTabs = behavior;
        this.context = context;
        tabTitles = new String[]{context.getResources().getText(R.string.borrowed_requested).toString(),
                context.getResources().getText(R.string.borrowed_pending).toString(),
                context.getResources().getText(R.string.borrowed_borrowing).toString()};
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
                return new BorrowedRequestedFragment();
            case 1:
                return new BorrowedPendingFragment();
            case 2:
                return new BorrowedBorrowingFragment();
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
