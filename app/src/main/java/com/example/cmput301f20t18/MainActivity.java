package com.example.cmput301f20t18;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private int permissionStorageWriteCode = 100;
    private int permissionStorageReadCode = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get permission for storing and reading data to system
        checkPermissionExternalData();

        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);

        /* Testing bottom navigation menu */

//        Intent homeScreenIntent = new Intent(MainActivity.this, HomeScreen.class);
//        startActivity(homeScreenIntent);

        //Intent intent = new Intent(MainActivity.this, SelectLocationActivity.class);
        //startActivity(intent);
    }

    private void checkPermissionExternalData() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    permissionStorageWriteCode);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},
                    permissionStorageReadCode);
        }
    }
}