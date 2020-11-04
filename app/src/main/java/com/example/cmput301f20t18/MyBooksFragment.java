package com.example.cmput301f20t18;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MyBooksFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_mybooks, container, false);

        Toolbar toolbar = view.findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.mybooks_header));
        FloatingActionButton addBooks = view.findViewById(R.id.mybooks_add);
        TabLayout tabLayout = view.findViewById(R.id.mybooks_tab_layout);
        TabItem tabAvailable = view.findViewById(R.id.tab_mybooks_available);
        TabItem tabPending = view.findViewById(R.id.tab_mybooks_pending);
        TabItem tabLending = view.findViewById(R.id.tab_mybooks_lending);
        ViewPager viewPager = view.findViewById(R.id.mybooks_viewPager);

        MyBooksPageAdapter pageAdapter = new MyBooksPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPager.setAdapter(pageAdapter);

        addBooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(getContext(),MyBooksAddBook.class);
                startActivityForResult(addIntent,1);
            }
        });

        return view;
    }

}
