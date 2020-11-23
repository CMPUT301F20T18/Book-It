package com.example.cmput301f20t18;

import android.content.Intent;
import android.graphics.Bitmap;
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
 * This is a class that creates options for the use of the ISBN
 * The class is still under development so the elements that appear on screen are mostly visual
 * with the exception of cancel
 * @author Johnathon Gil
 * @author Chase Warwick (class UserQueryTaskCompleteListener and function updateUserInfo)
 * @author Sean Butler
 */

public class ProfileFragment extends Fragment {

    TextView username, phoneNum, email, editAccount;
    Button signOut;
    ImageView profilePic;
    Bitmap userPhoto;

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

        final String editAccountText = "Edit Account";

        SpannableString  editProf = new SpannableString(editAccountText);

        ClickableSpan redirect = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

                Intent editIntent = new Intent(getContext(),EditProfile.class);
                editIntent.putExtra("username", username.getText().toString());
                editIntent.putExtra("email", email.getText().toString());
                editIntent.putExtra("phone", phoneNum.getText().toString());

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
    private void updateUserData(User user, View view) {
        username = (TextView) view.findViewById(R.id.my_user_name);
        phoneNum = (TextView) view.findViewById(R.id.phone_num);
        email = (TextView) view.findViewById(R.id.email);
        profilePic = (ImageView) view.findViewById(R.id.profile_pic);

        username.setText(user.getUsername());
        phoneNum.setText(user.getPhone());
        email.setText(user.getEmail());

    }

    /**
     * UserQueryTaskCompleteListener waits for the task of getting user snapshot to complete
     * before calling a function to update user information
     * @author Chase Warwick
     * TODO: Currently crashes if user repeatedly clicks profile button (not sure what that's about)
     * TODO: Add profile picture functionality (Should be easy just need db to have it)
     */
    private class UserQueryTaskCompleteListener implements OnCompleteListener{
        private View view;

        UserQueryTaskCompleteListener(View view){
            this.view = view;
        }

        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                DocumentSnapshot UserDocument = (DocumentSnapshot) task.getResult();
                User user = UserDocument.toObject(User.class);
                updateUserData(user, view);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RESULT_PROFILE_EDITED:
                if (resultCode == RESULT_OK) {
                    Bundle newInfo = data.getExtras();
                    userPhoto = (Bitmap) newInfo.get("photo");
                    if(userPhoto != null) {
                        Bitmap outMap = photoAdapter.scaleBitmap(userPhoto, (float) profilePic.getWidth(), (float) profilePic.getHeight());
                        Bitmap circleImage = photoAdapter.makeCircularImage(outMap, outMap.getHeight());
                        profilePic.setImageBitmap(circleImage);
                    }

                    username.setText((String)newInfo.get("username"));
                    phoneNum.setText((String)newInfo.get("phone"));
                    email.setText((String)newInfo.get("email"));

                    User current = new User();
                    current.ownerEditProfile((String) newInfo.get("username"), (String)newInfo.get("phone"), "default address");
                }
        }
    }
}
