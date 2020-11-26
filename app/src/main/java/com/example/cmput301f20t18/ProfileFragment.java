package com.example.cmput301f20t18;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

import static android.app.Activity.RESULT_OK;

/**
 * This is a class that displays a user's profile information such as username, email, address, and
 * phone number.
 * @author Johnathon Gil
 * @author Chase Warwick (class UserQueryTaskCompleteListener and function updateUserInfo)
 * @author Sean Butler
 */

public class ProfileFragment extends Fragment {

    TextView username, phoneNum, email, editAccount;
    Button signOut;
    ImageView profilePic;
    String photoString, address;
    //Bitmap userPhoto;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int RESULT_PROFILE_EDITED = 1;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBooksAvailableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        editAccount = (TextView) view.findViewById(R.id.edit_profile);

        signOut = (Button) view.findViewById(R.id.sign_out_button);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // clear all previous activities
                Intent intent = new Intent(getContext(), Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                // sign user out
                FirebaseAuth auth = FirebaseAuth.getInstance();
                auth.signOut();

                // send user back to login
                startActivity(intent);
            }
        });

        final String editAccountText = "Edit Account";

        SpannableString  editProf = new SpannableString(editAccountText);

        ClickableSpan redirect = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                Intent editIntent = new Intent(getContext(),EditProfile.class);
                editIntent.putExtra("username", username.getText().toString());
                editIntent.putExtra("address", address);
                editIntent.putExtra("phone", phoneNum.getText().toString());
                editIntent.putExtra("photo", photoString);

                startActivityForResult(editIntent, RESULT_PROFILE_EDITED);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getResources().getColor(R.color.doveGray));
            }
        };

        editProf.setSpan(redirect,0,12, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        editAccount.setText(editProf);
        editAccount.setMovementMethod(LinkMovementMethod.getInstance());

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Task<DocumentSnapshot> currentUser = FirebaseFirestore.getInstance()
                .collection("users")
                .document(auth.getUid())
                .get()
                .addOnCompleteListener(new UserQueryTaskCompleteListener(view));

        return view;
    }

    /**
     * updateUserData is called after the query to the db for user information is complete
     * and updates the fields filling them with the data recieved
     *
     * @author Chase Warwick
     * @param user The user currently using the app!
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateUserData(User user, View view) {
        username = (TextView) view.findViewById(R.id.my_user_name);
        phoneNum = (TextView) view.findViewById(R.id.phone_num);
        email = (TextView) view.findViewById(R.id.email);
        profilePic = (ImageView) view.findViewById(R.id.profile_pic);

        username.setText(user.getUsername());
        phoneNum.setText(user.getPhone());
        email.setText(user.getEmail());
        photoString = user.getProfile_picture();
        address = user.getAddress();
        if (photoString!= "") {
            Bitmap bitmap;
            try {
               bitmap = photoAdapter.stringToBitmap(photoString);
                profilePic.setImageBitmap(photoAdapter.makeCircularImage(bitmap, profilePic.getHeight()));
            } catch (Exception e) {
                e.printStackTrace();
                photoString = "";
            }

        }


    }

    /**
     * UserQueryTaskCompleteListener waits for the task of getting user snapshot to complete
     * before calling a function to update user information
     * @author Chase Warwick
     * TODO: Add profile picture functionality (Should be easy just need db to have it)
     */
    private class UserQueryTaskCompleteListener implements OnCompleteListener{
        private View view;

        UserQueryTaskCompleteListener(View view){
            this.view = view;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                DocumentSnapshot UserDocument = (DocumentSnapshot) task.getResult();
                User user = UserDocument.toObject(User.class);
                updateUserData(user, view);
            }
        }
    }

    /**
     * onActivityResult triggers after a user edits their profile information
     * @param requestCode represents the type of activity that the result is from
     * @param resultCode represents the result of the activity
     * @param data the data from the result of the activity
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_PROFILE_EDITED:
                if (resultCode == RESULT_OK) {
                    Bundle newInfo = data.getExtras();
                    photoString= (String) newInfo.get("photo");
                    if(photoString != "") {
                        Bitmap userPhoto = photoAdapter.stringToBitmap(photoString);
                        Bitmap outMap = photoAdapter.scaleBitmap(userPhoto, (float) profilePic.getWidth(), (float) profilePic.getHeight());
                        Bitmap circleImage = photoAdapter.makeCircularImage(outMap, outMap.getHeight());
                        profilePic.setImageBitmap(circleImage);
                    }

                    username.setText((String)newInfo.get("username"));
                    phoneNum.setText((String)newInfo.get("phone"));
                    address = (String)newInfo.get("address");


                    User current = new User();

                    current.ownerEditProfile((String) newInfo.get("username"), address , photoString, (String)newInfo.get("phone"));


                }
        }
    }
}
