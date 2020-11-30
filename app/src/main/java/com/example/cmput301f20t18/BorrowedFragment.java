package com.example.cmput301f20t18;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

/**
 * A Fragment that handles all sub-fragments for books that a user is borrowing
 * @author shuval
 * @author deinum
 */
public class BorrowedFragment extends Fragment implements fragmentListener {
    private final static String TAG = "BF_DEBUG";
    private Context mContext;

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
        
        TabLayout tabLayout = view.findViewById(R.id.borrowed_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.borrowed_viewPager);

        BorrowedPageAdapter pageAdapter = new BorrowedPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());
        viewPager.setAdapter(pageAdapter);

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    /**
     * Process results from called activities
     * @param requestCode The requested finish code
     * @param resultCode The result code for the activity
     * @param data The data returned by the called activities
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String isbn_string = data.getStringExtra("ISBN");
        Long isbn = 0L;
        if (!isbn_string.equals("")) {
            isbn = Long.parseLong(isbn_string);
        }
        int bookID = data.getIntExtra("bookID", 0);
        Long expected_isbn = data.getLongExtra("eISBN", 0);

        // debug info
        Log.d(TAG, "0 bookID: " + Integer.toString(bookID));
        Log.d(TAG, "ISBN: " + isbn);
        Log.d(TAG, "Expected ISBN: " + expected_isbn);

        if (!ScannerActivity.CHECK_ISBN || expected_isbn.equals(isbn)) {
            User current = new User();
            switch (requestCode) {

                case 1:
                    current.borrowerDropOffBook(bookID);
                    break;

                case 2:
                    current.borrowerPickupBook(bookID);
                    break;
            }
        }
        else {
            // using getContext() here instead of mContext will sometimes cause a crash since this
            // fragment may not have been attached to HomeScreen yet
            Log.d(TAG, "onActivityResult: Scanned ISBN does not match expected ISBN");
            new AlertDialog.Builder(mContext, R.style.CustomDialogTheme)
                    .setTitle("Error!")
                    .setMessage("Scanned ISBN does not match book's ISBN")
                    .setPositiveButton("OK", null)
                    .show();
        }

    }
}
