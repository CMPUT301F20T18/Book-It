package com.example.cmput301f20t18;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

/**
 * A {@link Fragment} subclass that is responsible for the page the user sees when in the
 * "Borrowed" section.
 */
public class BorrowedFragment extends Fragment implements fragmentListener {
    private final static String TAG = "BF_DEBUG";

    /**
     * Instantiates view. The documentation recommends only inflating the layout here and doing
     * everything else in {@link #onViewCreated(View, Bundle)}.
     *
     * @param inflater Used to inflate view
     * @param container Parent view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_borrowed, container, false);
    }

    /**
     * Sets up page adapter with the swipable tabs in the Borrowed section.
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has
     * returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Setting the header title. This may be done in XML instead
        Toolbar toolbar = view.findViewById(R.id.borrowed_toolbar);
        toolbar.setTitle(getResources().getText(R.string.borrowed_header));

        /* This is not being used right now but could be later */
        TabItem tabRequested = view.findViewById(R.id.tab_borrowed_requested);
        TabItem tabPending = view.findViewById(R.id.tab_borrowed_pending);
        TabItem tabBorrowing = view.findViewById(R.id.tab_borrowed_borrowing);
        
        TabLayout tabLayout = view.findViewById(R.id.borrowed_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.borrowed_viewPager);

        BorrowedPageAdapter pageAdapter = new BorrowedPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());
        viewPager.setAdapter(pageAdapter);

    }


    // take appropriate action from result
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String isbn_string = data.getStringExtra("ISBN");
        Long isbn = Long.parseLong(isbn_string);
        int bookID = data.getIntExtra("bookID", 0);
        Long expected_isbn = data.getLongExtra("eISBN", 0);

        Log.d(TAG, "0 bookID: " + Integer.toString(bookID));
        Log.d(TAG, "ISBN: " + isbn);
        Log.d(TAG, "Expected ISBN: " + expected_isbn);


        User current = new User();

        switch (requestCode) {

            case 0:
                break;


            case 1:

                if (expected_isbn == isbn) {
                    current.borrowerPickupBook(bookID);
                }
                else {
                    // TODO: Implement popup stating that the wrong book was returned
                    Log.d(TAG, "Wrong book pickup!");


                }
                break;

            case 2:
                if (expected_isbn == isbn) {
                    current.borrowerDropOffBook(bookID);
                }
                else {
                    // TODO: Implement popup stating that the wrong book was returned
                    Log.d(TAG, "Wrong book pickup!");
                }
                break;
        }

    }
}
