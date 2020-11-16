package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
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

import static com.example.cmput301f20t18.FirestoreBookAdapter.VIEW_REQUESTS;

/**
 * Homescreen is the first object a user sees upon signing in, and will contain all the books
 * borrowed by the user.
 * Homescreen also manages fragments, and provides a mean for two fragments to interact
 * @see User
 * @see Book
 */

public class HomeScreen extends AppCompatActivity implements CustomBottomSheetDialog.BottomSheetListener{
    private int permissionStorageWriteCode = 100;
    private int permissionStorageReadCode = 101;


    Fragment selectedFragment;
    final String TAG = "HOMESCREEN_DEBUG";


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);



        User current = new User();
        // current.borrowerRequestBook(108);



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
                @RequiresApi(api = Build.VERSION_CODES.M) // This is because of SearchFragment
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


    @Override
    public void onButtonClick(int button, int status) {
        AlertDialog dialog;
        switch (button) {
            case CustomBottomSheetDialog.CANCEL_BUTTON:
                dialog = new AlertDialog.Builder(HomeScreen.this)
                        .setTitle("Cancel pick up")
                        .setMessage("Are you sure you want to cancel this pick up?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: cancel pick up (owner or borrower can do this).
                            }
                        })
                        .setNegativeButton("Back", null)
                        .show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
                break;

            case CustomBottomSheetDialog.EDIT_BUTTON:
                // TODO: Make activity for editing book details.
                Toast.makeText(getApplicationContext(), "edit clicked", Toast.LENGTH_SHORT).show();
                break;

            case CustomBottomSheetDialog.DELETE_BUTTON:
                String alertMessage = "";
                switch (status) {
                    case Book.STATUS_AVAILABLE:
                        alertMessage = "Are you sure you want to delete this book?";
                        break;

                    case Book.STATUS_REQUESTED:
                        alertMessage = "Deleting this book will decline all requests.\n" +
                                "Are you sure you want to delete this book?";
                        break;

                    case Book.STATUS_ACCEPTED:
                        alertMessage = "Deleting this book will cancel the pick up.\n" +
                                "Are you sure you want to delete this book?";
                        break;
                    case Book.STATUS_BORROWED:
                        alertMessage = "This book is currently being borrowed.\n" +
                                "Are you sure you want to delete this book?";
                        break;
                }
                dialog = new AlertDialog.Builder(HomeScreen.this)
                        .setTitle("Delete book")
                        .setMessage(alertMessage)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO: delete book
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources()
                        .getColor(R.color.colorPrimaryDark));
                break;
            default:
                Log.e(TAG, "onButtonClick: Invalid button ID");
        }
    }

    // handles activity results by sending the result where it needs to go
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                Log.d(TAG, "Got to activity Result!");
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
