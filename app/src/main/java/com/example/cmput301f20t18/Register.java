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
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;
    EditText address;
    Button register;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        email = (EditText) findViewById(R.id.email);
        address = (EditText) findViewById(R.id.address);
        register = (Button) findViewById(R.id.registerButton);
        mAuth = FirebaseAuth.getInstance();

        register.setOnClickListener(new View.OnClickListener() {
            // TODO: Add input verification
            @Override
            public void onClick(View view) {
                String new_username = username.getText().toString();
                String new_password = password.getText().toString();
                String new_email= email.getText().toString();
                String new_address = address.getText().toString();

                mAuth.createUserWithEmailAndPassword(new_email, new_password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    user.updateEmail(new_email);
                                    // TODO: Set users address and username

                                    // start new activity with current user
                                    Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                                    startActivityForResult(intent, 0);
                                }
                                else {
                                    FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                    Toast.makeText(Register.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                                    return;

                                }

                            }
                        });

            }
        });

    }

}