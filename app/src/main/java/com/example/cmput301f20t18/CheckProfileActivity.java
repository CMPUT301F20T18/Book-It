package com.example.cmput301f20t18;

import androidx.annotation.RequiresApi;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/**This class is used to check another users profile
 * This is a class still under development
 * @author Johnathon
 * @author Chase Warwick (populated the fields with user data instead of sample data & implemented
 *                        back button)
 *
 */

public class CheckProfileActivity extends AppCompatActivity {

    TextView username, phoneNum, email;
    Button backButton;
    ImageView profilePic;

    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference userRef = DB.collection("users");

    final static String TAG = "CPA_DEBUG";


    /**
     * Creates the instance of view and run it
     * @param savedInstanceState
     */


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_profile);

        Intent currentActivity = getIntent();


        username = findViewById(R.id.user_name);
        phoneNum = findViewById(R.id.phone_num_user);
        email = findViewById(R.id.email_user);

        backButton = findViewById(R.id.back_profile);
        profilePic = findViewById(R.id.profile_pic_user);


        // get the borrowers information
        String borrower_username = getIntent().getStringExtra("USERNAME");
        userRef.whereEqualTo("username", borrower_username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    User borrower = task.getResult().toObjects(User.class).get(0); // username is unique and non null
                    username.setText(borrower.getUsername());
                    phoneNum.setText(borrower.getPhone());
                    email.setText(borrower.getEmail());
                    String photoString = borrower.getProfile_picture();

                    if(photoString!="" && photoString != null) {
                        Bitmap photo = photoAdapter.scaleBitmap(photoAdapter.stringToBitmap(photoString),
                                profilePic.getWidth(),
                                profilePic.getHeight());
                        Bitmap profile = photoAdapter.makeCircularImage(photo, photo.getHeight());
                        profilePic.setImageBitmap(profile);
                    }
                }

                else {
                    Log.d(TAG, "Error Querying for borrower information");
                }
            }
        });






        username.setText(currentActivity.getStringExtra("USERNAME"));
        phoneNum.setText(currentActivity.getStringExtra("PHONE"));
        email.setText(currentActivity.getStringExtra("EMAIL"));


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}