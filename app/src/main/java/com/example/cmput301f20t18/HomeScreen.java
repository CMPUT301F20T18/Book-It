package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
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
 * This class probably needs to be divided up as it has many responsibilities.
 */
public class HomeScreen extends AppCompatActivity {
    private int permissionStorageWriteCode = 100;
    private int permissionStorageReadCode = 101;

    FirebaseAuth auth;
    FirebaseFirestore DB;
    CollectionReference system;
    CollectionReference users;
    CollectionReference books;
    DocumentReference current_user;
    Library lib;
    Fragment selectedFragment;
    final String TAG = "HOMESCREEN";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        DB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // get all of our collections
        system = DB.collection("system");
        users = DB.collection("system").document("System").collection("users");
        books = DB.collection("system").document("System").collection("books");
        current_user = DB.collection("system").document("System").collection("users").document(auth.getUid());

        Task<QuerySnapshot> retrieve_users = users.get();
        Task<QuerySnapshot> retrieve_books = books.get();
        Task<DocumentSnapshot> retrieve_current_user = current_user.get();

        // successfully got all books and users
        Task<List<Task<?>>> combined = Tasks.whenAllComplete(retrieve_books, retrieve_users, retrieve_current_user).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                User current = Objects.requireNonNull(retrieve_current_user.getResult()).toObject(User.class);
                assert (current != null);
                Log.d(TAG, "onSuccess: " + current.getUsername());
            }


        });

        //* Bottom navigation menu *//*
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemBackgroundResource(R.drawable.tab_background);

        bottomNav.setOnNavigationItemSelectedListener(navListener);

        // start at My Books by default
        bottomNav.setSelectedItemId(R.id.tab_mybooks);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MyBooksFragment()).commit();

        checkPermissionExternalData();


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
        switch (requestCode) {
            case 1:
                selectedFragment.onActivityResult(requestCode, resultCode, data);

        }


    }

    private void checkPermissionExternalData() {
        if (ContextCompat.checkSelfPermission(HomeScreen.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(HomeScreen.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionStorageWriteCode);
        }
        if (ContextCompat.checkSelfPermission(HomeScreen.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(HomeScreen.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    permissionStorageReadCode);
        }
    }
}
