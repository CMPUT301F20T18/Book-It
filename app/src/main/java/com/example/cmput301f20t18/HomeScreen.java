package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class HomeScreen extends AppCompatActivity {
    User user;
    List<Book> ownedBooks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();


    }
}