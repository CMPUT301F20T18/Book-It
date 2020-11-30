package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;


/**
 * The solve purpose of this activity is to initialize the login screen
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // start the main Activity
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);
    }

    @Override
    public void onBackPressed() {
        // this prevents user from going back
    }
}


