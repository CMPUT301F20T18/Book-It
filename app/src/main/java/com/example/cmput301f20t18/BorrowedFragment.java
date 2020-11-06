package com.example.cmput301f20t18;

import android.os.Bundle;
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
public class BorrowedFragment extends Fragment {

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
//        TabItem tabRequested = view.findViewById(R.id.tab_borrowed_requested);
//        TabItem tabPending = view.findViewById(R.id.tab_borrowed_pending);
//        TabItem tabBorrowing = view.findViewById(R.id.tab_borrowed_borrowing);
        
        TabLayout tabLayout = view.findViewById(R.id.borrowed_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.borrowed_viewPager);

        BorrowedPageAdapter pageAdapter = new BorrowedPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPager.setAdapter(pageAdapter);

    }
}
