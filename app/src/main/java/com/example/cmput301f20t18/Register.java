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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.core.FirestoreClient;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {

    EditText username;
    EditText password;
    EditText email;
    EditText address;
    Button register;
    FirebaseAuth mAuth;
    FirebaseFirestore DB;
    CollectionReference users;

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



        DB = FirebaseFirestore.getInstance();
        users = DB.collection("users");

        register.setOnClickListener(new View.OnClickListener() {
            // TODO: Add input verification
            @Override


            public void onClick(View view) {

                String new_username = username.getText().toString();
                String new_password = password.getText().toString();
                String new_email= email.getText().toString();
                String new_address = address.getText().toString();

                if (new_username.matches("") || new_email.matches("") || new_password.matches("") || new_address.matches("")) {
                    Toast.makeText(Register.this, "Please fill all fields", Toast.LENGTH_LONG).show();
                    return;
                }




                // ensure the username is unique
                FirebaseDatabase db = FirebaseDatabase.getInstance();
                DatabaseReference Baseref = db.getReference();
                DatabaseReference username_ref = db.getReference().child("username_list");

                username_ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(new_username)) {
                            Toast.makeText(Register.this, "Username has been taken, please enter a different username", Toast.LENGTH_LONG).show();
                        }

                        else {
                            mAuth.createUserWithEmailAndPassword(new_email, new_password)
                                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {

                                                // Sign in success, update UI with the signed-in user's information
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                username_ref.child(new_username).setValue(new_username);



                                                // set user ID
                                                Baseref.child("max_user_id").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        Long val = snapshot.getValue(Long.class);
                                                        // add the user to the collection
                                                        User person = new User(new_username, val, mAuth.getUid(), new_email, new_address);
                                                        users.document(mAuth.getUid()).set(person);



                                                        // update the ID
                                                        Baseref.child("max_user_id").setValue(val + 1);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                        Log.e("USERID", "Error updating max user id");
                                                    }
                                                });



                                                // start new activity with current user
                                                Intent intent = new Intent(getBaseContext(), HomeScreen.class);
                                                startActivityForResult(intent, 0);
                                            }
                                            else {
                                                FirebaseAuthException e = (FirebaseAuthException)task.getException();
                                                Toast.makeText(Register.this, "Failed Registration: "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                                                return;

                                            }

                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("USERID", "Error looking up username!");

                    }
                });

            }
        });

    }

}