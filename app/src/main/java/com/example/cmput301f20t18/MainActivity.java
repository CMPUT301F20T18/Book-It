package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //get permission for storing and reading data to system

        // start the main Activity
        Intent loginIntent = new Intent(MainActivity.this, Login.class);
        startActivity(loginIntent);
    }

    @Override
    public void onBackPressed() {
        // this prevents user from going back
    }
}


