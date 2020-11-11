package com.example.cmput301f20t18;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

/**
 * A {@link Fragment} subclass that is responsible for the page the user sees when in the
 * "My Books" section.
 */
public class MyBooksFragment extends Fragment implements fragmentListener {
    final static String TAG = "MBF";


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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_mybooks, container, false);
    }

    /**
     * Sets up page adapter with the swipable tabs in the My Books section.
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
        Toolbar toolbar = view.findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.mybooks_header));

        /* This is not being used right now but could be later */
        TabItem tabAvailable = view.findViewById(R.id.tab_mybooks_available);
        TabItem tabPending = view.findViewById(R.id.tab_mybooks_pending);
        TabItem tabLending = view.findViewById(R.id.tab_mybooks_lending);

        TabLayout tabLayout = view.findViewById(R.id.mybooks_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.mybooks_viewPager);

        MyBooksPageAdapter pageAdapter = new MyBooksPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());
        viewPager.setAdapter(pageAdapter);

        // Button for adding a new book
        Button addBooks = view.findViewById(R.id.button_add_book);

        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(),MyBooksAddBook.class);
                startActivityForResult(addIntent,1);
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String isbn_string = data.getStringExtra("ISBN");
        Long isbn = Long.parseLong(isbn_string);
        int bookID = data.getIntExtra("bookID", 0);
        Log.d(TAG, "0 bookID: " + Integer.toString(bookID));

        User current = new User();

        switch (requestCode) {

            // change book status to borrowed
            // change transaction

            case 0:
                Log.d(TAG, "0 bookID: " + Integer.toString(bookID));
                break;


            case 1:
                Log.d(TAG, "1 bookID: " + Integer.toString(bookID));
                current.ownerSignOff(bookID);
                break;

            case 2:
                current.ownerConfirmPickup(0);
                break;
        }

    }

}
