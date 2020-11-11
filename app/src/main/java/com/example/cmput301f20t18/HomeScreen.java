package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Homescreen is the first object a user sees upon signing in, and will contain all the books
 * borrowed by the user.
 * Homescreen also manages fragments, and provides a mean for two fragments to interact
 * @see User
 * @see Book
 */
public class HomeScreen extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseFirestore DB;
    CollectionReference system;
    CollectionReference users;
    CollectionReference books;
    DocumentReference current_user;
    Library lib;
    Fragment selectedFragment;
    final String TAG = "HOMESCREEN_DEBUG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);



        User current = new User();
        //current.borrowerRequestBook(77);
        current.ownerAcceptRequest(10);


        //* Bottom navigation menu *//*
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemBackgroundResource(R.drawable.tab_background);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // start at My Books by default
        bottomNav.setSelectedItemId(R.id.tab_mybooks);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MyBooksFragment()).commit();


    }

    // Not in onCreate() to avoid clutter but idk
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.tab_borrowed:
                            selectedFragment = new BorrowedFragment();
                            break;
                        case R.id.tab_search:
                            selectedFragment = new SearchFragment();
                            break;
                        case R.id.tab_scan:
                            Intent intent = new Intent(HomeScreen.this, Scanner.class);
                            intent.putExtra("type", 0);
                            startActivityForResult(intent, RESULT_OK);
                            break;
                        case R.id.tab_mybooks:
                            selectedFragment = new MyBooksFragment();
                            break;
                        case R.id.tab_profile:
                            selectedFragment = new ProfileFragment();
                            break;
                    }

                    if (selectedFragment == null) {
                        return false;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment).commit();

                    return true;
                }
            };


    // handles activity results by sending the result where it needs to go
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case -1:
                Log.d(TAG, "Got to activity Result!");
                selectedFragment.onActivityResult(requestCode, resultCode, data);

        }
    }
}
