package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {

    private EditText usernameInput, phoneNumInput, emailInput;
    private TextView changePass;
    private Button changePhoto, deletePhoto, deleteAccount, editDone, myProfileReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        usernameInput = findViewById(R.id.username_input);
        phoneNumInput = findViewById(R.id.phone_input);
        emailInput = findViewById(R.id.email_input);

        changePass = findViewById(R.id.password_change);

        changePhoto = findViewById(R.id.change_photo);
        deletePhoto = findViewById(R.id.delete_photo);
        deleteAccount = findViewById(R.id.delete_account);
        myProfileReturn = findViewById(R.id.return_to_my_profile);

        myProfileReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}