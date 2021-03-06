package com.example.cmput301f20t18;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
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
 * Homescreen also manages fragments, and provides a mean for two fragments to interact.
 *
 * reference for BottomNavigationView: https://www.youtube.com/watch?v=tPV8xA7m-iw
 * @see User
 * @see Book
 * @author shuval
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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (getIntent() != null) {
            String ISBN = getIntent().getStringExtra("ISBN");
            if (ISBN != null) {
                // Have to switch to search frag and then replace it with itself to pass intent
                bottomNav.setSelectedItemId(R.id.tab_search);

                Bundle bundle = new Bundle();
                bundle.putString("ISBN", ISBN);
                Fragment fragment = new SearchFragment();
                fragment.setArguments(bundle);

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .commit();
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
        // Prevents user from going back
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
                            Intent intent = new Intent(HomeScreen.this, ScannerActivity.class);
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


    // User clicks on button in BottomSheetDialog
    @Override
    public void onButtonClick(int button, int status, int bookID, boolean owner) {
        CustomBottomSheetDialog.buttonAction(button, status, bookID, owner, HomeScreen.this);
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
