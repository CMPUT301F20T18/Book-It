package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private FirebaseAuth mAuth;
    Button login;
    Button register;
    EditText login_user;
    EditText login_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        login = (Button) findViewById(R.id.login);
        register = (Button) findViewById(R.id.Register);
        login_user = (EditText) findViewById(R.id.username);
        login_password = (EditText) findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = login_user.getText().toString();
                String password = login_password.getText().toString();

                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

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

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivityForResult(intent, RESULT_OK);
            }
        });



    }


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