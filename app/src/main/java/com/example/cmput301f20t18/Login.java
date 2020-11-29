package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

/**
 * Allows the users to sign in / opt to register for the application
 * @author Jacob Deinum
 * UI contributions
 * @author Johnathon Gil
 * @see Register
 * @see User
 * */

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button login;
    TextView register;
    EditText login_user;
    EditText login_password;
    TextView usernameT;
    TextView passwordT;
    ImageView loginArt;
    TextView signIn;
    ImageView projectLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // register parameters
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login);
        register = (TextView) findViewById(R.id.sign_up);
        login_user = (EditText) findViewById(R.id.username);
        login_password = (EditText) findViewById(R.id.password);

        // user clicks on login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login_user.setText("phlafoo@gmail.com");
                login_password.setText("123456");

                // get entered info
                String username = login_user.getText().toString();
                String password = login_password.getText().toString();

                // ensure input not empty
                if (username.matches("") || password.matches("")) {
                    new AlertDialog.Builder(Login.this, R.style.CustomDialogTheme)
                            .setTitle("Please fill out both fields")
                            .setMessage("")
                            .setPositiveButton("OK", null)
                            .show();
                    return;
                }

                // attempt authentication
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    // start new activity with current user
                                    Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                                    finish();
                                    startActivityForResult(intent, 0);
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    FirebaseAuthException e = (FirebaseAuthException) task.getException();
                                    new AlertDialog.Builder(Login.this, R.style.CustomDialogTheme)
                                            .setTitle("Sign in failed")
                                            .setMessage("Incorrect email or password!")
                                            .setPositiveButton("OK", null)
                                            .show();
                                }
                            }
                        });

            }
        });

        // user wants to register, redirect them to register activity
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });
        register.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                register.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        register.setBackgroundColor(getResources().getColor(R.color.colorGray2));
    }

    @Override
    public void onBackPressed()
    {

    }

}


