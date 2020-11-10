package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**This class is used to check another users profile
 * This is a class still under development
 * @author Johnathon
 *
 */

public class CheckProfileActivity extends AppCompatActivity {

    TextView username, phoneNum, email;
    ImageView profilePic;


    /**
     * Creates the instance of view and run it
     * @param savedInstanceState
     */

    // TODO: Update and add references of the user from the database to the class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_profile);

        username = findViewById(R.id.user_name);
        phoneNum = findViewById(R.id.phone_num_user);
        email = findViewById(R.id.email_user);

        profilePic = findViewById(R.id.profile_pic_user);

        username.setText("MysticWolf");
        phoneNum.setText("780 933 8641");
        email.setText("jggil@ualberta.ca");


        profilePic.setImageResource(R.drawable.user_pic);


    }
}