package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

/**
 * This is a splash screen activity
 * @author Johnathon Gil
 */

public class SplashScreen extends AppCompatActivity {

    /**
     * This creates the instance of the splash screen and goes to the main activity screen
     * @param savedInstanceState
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //skip to MainActivity so I don't have to log in every time
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
        startActivity(intent);

        /*This is the opening screen of the app*/
        /*Intent intent = new Intent(SplashScreen.this, Login.class);
        startActivity(intent);*/
    }
}