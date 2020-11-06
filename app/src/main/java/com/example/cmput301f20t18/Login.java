package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Allows the users to login into the app
 * @author Jacob Deinum
 * UI contributions
 * @author Johnathon Gil
 */
public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button login;
    Button register;
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
        register = (Button) findViewById(R.id.Register);
        login_user = (EditText) findViewById(R.id.username);
        login_password = (EditText) findViewById(R.id.password);
        usernameT = (TextView) findViewById(R.id.textView_username);
        passwordT = (TextView) findViewById(R.id.textView_password);
        loginArt = (ImageView) findViewById(R.id.login_image);
        signIn = (TextView) findViewById(R.id.sign_in);
        projectLogo = (ImageView) findViewById(R.id.login_image);

        // user clicks on login button
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // get entered info
                String username = login_user.getText().toString();
                String password = login_password.getText().toString();

                // ensure input not empty
                if (username.matches("") || password.matches("")) {
                    Toast.makeText(Login.this, "Please fill all fields", Toast.LENGTH_LONG).show();
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
                                    startActivityForResult(intent, 0);
                                }
                                else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(Login.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

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



    }

// TODO: IMPLEMENT ONSTART

//    @Override
//    protected void onStart() {
//        super.onStart();
//        FirebaseUser current_user = mAuth.getCurrentUser();
//
//        // start the Homescreen, get all the users books later
//        Intent intent = new Intent(getBaseContext(), HomeScreen.class);
//        startActivityForResult(intent, 0);
//    }
}