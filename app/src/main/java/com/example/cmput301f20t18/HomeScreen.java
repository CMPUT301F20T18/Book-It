package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.WindowManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * Homescreen is the first object a user sees upon signing in, and will contain all the books
 * borrowed by the user.
 * Homescreen also manages fragments, and provides a mean for two fragments to interact
 * @see User
 * @see Book
 * @author Shuval
 * @author deinum
 */

public class HomeScreen extends AppCompatActivity implements CustomBottomSheetDialog.BottomSheetListener{
    private int permissionStorageWriteCode = 100;
    private int permissionStorageReadCode = 101;
    private int permissionInternet = 102;
    private BottomNavigationView bottomNav;


    Fragment selectedFragment;
    final String TAG = "HOMESCREEN_DEBUG";

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    // this is for when a user clicks "search for available copies" in postscan
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent() != null) {
            String ISBN = getIntent().getStringExtra("ISBN");
            if (ISBN != null) {
                // Have to switch to search frag and then replace it with itself to pass intent
                bottomNav.setSelectedItemId(R.id.tab_search);

                Bundle bundle = new Bundle();
                bundle.putString("ISBN", ISBN);
                Fragment fragment = new SearchFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment).commit();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // update the users instanceToken
        FirebaseFirestore DB = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if (task.isSuccessful()) {
                    String token = task.getResult();
                    DB.collection("users").document(auth.getUid()).update("instanceToken", token);


                }
            }
        });

        //* Bottom navigation menu *//*
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setItemIconTintList(null);
        bottomNav.setItemBackgroundResource(R.drawable.tab_background);
        bottomNav.setOnNavigationItemSelectedListener(navListener);


        // start at My Books by default
        bottomNav.setSelectedItemId(R.id.tab_mybooks);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                new MyBooksFragment()).commit();

        checkPermissionInternet();
        checkPermissionExternalData();


    }

    @Override
    public void onBackPressed()
    {

    }

    // Not in onCreate() to avoid clutter but idk
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @RequiresApi(api = Build.VERSION_CODES.M) // This is because of SearchFragment
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    selectedFragment = null;
                    String FragTag = "";

                    switch (item.getItemId()) {
                        case R.id.tab_borrowed:
                            selectedFragment = new BorrowedFragment();
                            FragTag = "BORROWED";
                            break;
                        case R.id.tab_search:
                            selectedFragment = new SearchFragment();
                            FragTag = "SEARCH";
                            break;
                        case R.id.tab_scan:
                            Intent intent = new Intent(HomeScreen.this, Scanner.class);
                            intent.putExtra("type", 0);
                            startActivityForResult(intent, RESULT_OK);
                            break;
                        case R.id.tab_mybooks:
                            selectedFragment = new MyBooksFragment();
                            FragTag = "MYBOOKS";
                            break;
                        case R.id.tab_profile:
                            selectedFragment = new ProfileFragment();
                            FragTag = "PROFILE";
                            break;
                    }

                    Fragment currentFragment = getSupportFragmentManager().findFragmentByTag(FragTag);

                    if (selectedFragment == null ||
                            (currentFragment != null && currentFragment.isResumed())) {
                        return false;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                            selectedFragment,FragTag).commit();

                    return true;
                }
            };


    @Override
    public void onButtonClick(int button, int status, int bookID, boolean owner) {
        AlertDialog dialog;
        User current = new User();
        switch (button) {
            case CustomBottomSheetDialog.CANCEL_BUTTON:

                if (owner) {
                    dialog = new AlertDialog.Builder(HomeScreen.this, R.style.CustomDialogTheme)
                            .setTitle("Cancel pick up")
                            .setMessage("Are you sure you want to cancel this pick up?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    current.ownerCancelPickup(bookID);
                                }
                            })
                            .setNeutralButton("Back", null)
                            .show();
                }
                else {
                    dialog = new AlertDialog.Builder(HomeScreen.this, R.style.CustomDialogTheme)
                            .setTitle("Cancel pick up")
                            .setMessage("Are you sure you want to cancel this pick up?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    current.borrowerCancelPickup(bookID);
                                }
                            })
                            .setNeutralButton("Back", null)
                            .show();

                }
                break;


            case CustomBottomSheetDialog.EDIT_BUTTON:
                Intent intent = new Intent(getApplicationContext(), AddBookActivity.class);
                intent.putExtra("bookID", bookID);
                intent.putExtra("type", AddBookActivity.EDIT_BOOK);
                startActivityForResult(intent, 5);
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
                dialog = new AlertDialog.Builder(HomeScreen.this, R.style.CustomDialogTheme)
                        .setTitle("Delete book")
                        .setMessage(alertMessage)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.ownerDeleteBook(bookID);
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
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
            Log.d(TAG, "checkPermissionExternalData: Failed");
            ActivityCompat.requestPermissions(HomeScreen.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    permissionStorageReadCode);
        }
    }

    private void checkPermissionInternet(){
        if (ContextCompat.checkSelfPermission(HomeScreen.this,
                Manifest.permission.INTERNET) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(HomeScreen.this,
                    new String[] {Manifest.permission.INTERNET}, permissionInternet);
        }
    }
}
