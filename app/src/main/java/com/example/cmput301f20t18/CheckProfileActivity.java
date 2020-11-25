package com.example.cmput301f20t18;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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


    /**
     * Creates the instance of view and run it
     * @param savedInstanceState
     */

    // TODO: Update and add references of the user from the database to the class

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
        String imageString = currentActivity.getStringExtra("PICTURE");
        Bitmap photo = photoAdapter.scaleBitmap(photoAdapter.stringToBitmap(imageString),
                profilePic.getLayoutParams().width,
                profilePic.getLayoutParams().height);

        username.setText(currentActivity.getStringExtra("USERNAME"));
        phoneNum.setText(currentActivity.getStringExtra("PHONE"));
        email.setText(currentActivity.getStringExtra("EMAIL"));
        profilePic.setImageBitmap(photo);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}