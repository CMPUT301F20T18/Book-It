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

public class BorrowedFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_borrowed, container, false);

        Toolbar toolbar = view.findViewById(R.id.borrowed_toolbar);
        toolbar.setTitle(getResources().getText(R.string.borrowed_header));
        TabLayout tabLayout = view.findViewById(R.id.borrowed_tab_layout);
        TabItem tabRequested = view.findViewById(R.id.tab_borrowed_requested);
        TabItem tabPending = view.findViewById(R.id.tab_borrowed_pending);
        TabItem tabBorrowing = view.findViewById(R.id.tab_borrowed_borrowing);
        ViewPager viewPager = view.findViewById(R.id.borrowed_viewPager);

        BorrowedPageAdapter pageAdapter = new BorrowedPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPager.setAdapter(pageAdapter);

        return view;
    }

}
