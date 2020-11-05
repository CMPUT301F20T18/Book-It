package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

<<<<<<< HEAD
        /*This is the opening screen of the app*/
        Intent intent = new Intent(SplashScreen.this, HomeScreen.class);
=======
        //skip to MainActivity so I don't have to log in every time
        Intent intent = new Intent(SplashScreen.this, MainActivity.class);
>>>>>>> c0363d55943d827830aa57f39cd57d30cbbe7530
        startActivity(intent);

        /*This is the opening screen of the app*/
        /*Intent intent = new Intent(SplashScreen.this, Login.class);
        startActivity(intent);*/
    }
}