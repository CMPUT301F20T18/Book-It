package com.example.cmput301f20t18;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private Button Login;
    private Button Register;
    private static final int SCANNER_RETURN_CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Login = findViewById(R.id.login_button);
        Register = findViewById(R.id.register_button);


        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), Scanner.class);
                startActivityForResult(intent, SCANNER_RETURN_CODE);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Register.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ensure it is our scanner sending back the info
        if (requestCode == SCANNER_RETURN_CODE) {
            if (resultCode == RESULT_OK) {
                assert data != null;
                String isbn_string = data.getStringExtra("key");
                Toast.makeText(this, "ISBN: " + isbn_string, Toast.LENGTH_SHORT).show();
            }


        }




    }
}