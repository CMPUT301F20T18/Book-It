package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*This is the opening screen of the app*/
        Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
        startActivity(intent);
    }
}