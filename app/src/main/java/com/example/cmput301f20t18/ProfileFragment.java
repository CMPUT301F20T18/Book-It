package com.example.cmput301f20t18;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static android.app.Activity.RESULT_OK;

/**
 * This is a class that displays a user's profile information such as username, email, address, and
 * phone number.
 * @author Johnathon Gil
 * @author Chase Warwick (class UserQueryTaskCompleteListener and function updateUserInfo)
 * @author Sean Butler
 * @author deinum
 */

public class ProfileFragment extends Fragment {

    TextView username, textAddress, phoneNum, email, editAccount, noResultsTextView;
    Button signOut, clearNotifications;
    ImageView profilePic;
    String photoString, address;

    RecyclerView recyclerView;
    Query query;
    FirestoreNotificationAdapter adapter;

    // DB info
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    CollectionReference userRef = DB.collection("users");

    private static final int RESULT_PROFILE_EDITED = 1;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        editAccount = (TextView) view.findViewById(R.id.edit_profile);
        recyclerView = view.findViewById(R.id.NotifRecyclerView);
        setUp();

        noResultsTextView = view.findViewById(R.id.no_results);
        noResultsTextView.setText(R.string.notifications_empty);

        // display message if list of books is empty
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                noResultsTextView.setText("");
            }
            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                if (adapter.getItemCount() == 0) {
                    noResultsTextView.setText(R.string.notifications_empty);
                }
            }
        });

        signOut = (Button) view.findViewById(R.id.sign_out_button);

        // https://stackoverflow.com/questions/6330260/finish-all-previous-activities
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

        clearNotifications = view.findViewById(R.id.button_clear_notifications);
        clearNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User current = new User();
                recyclerView.setAdapter(null);
                current.userDeleteNotifications();
                setUp();
            }
        });

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
        textAddress = (TextView) view.findViewById(R.id.address);
        phoneNum = (TextView) view.findViewById(R.id.phone_num);
        email = (TextView) view.findViewById(R.id.email);
        profilePic = (ImageView) view.findViewById(R.id.profile_pic);

        username.setText(user.getUsername());
        textAddress.setText(user.getAddress().getTitle());
        phoneNum.setText(user.getPhone());
        email.setText(user.getEmail());
        photoString = user.getProfile_picture();
        address = user.getAddress().getTitle();
        if (!photoString.equals("")) {
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

                editAccount.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent editIntent = new Intent(getContext(), EditProfileActivity.class);
                        editIntent.putExtra("username", username.getText().toString());
                        editIntent.putExtra("address", address);
                        editIntent.putExtra("phone", phoneNum.getText().toString());
                        editIntent.putExtra("photo", photoString);
                        editIntent.putExtra("email", email.getText().toString());

                        startActivityForResult(editIntent, RESULT_PROFILE_EDITED);

                    }
                });
                editAccount.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        editAccount.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                        return false;
                    }
                });
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
                    if(!photoString.equals("")) {
                        Bitmap userPhoto = photoAdapter.stringToBitmap(photoString);
                        Bitmap outMap = photoAdapter.scaleBitmap(userPhoto, (float) profilePic.getWidth(), (float) profilePic.getHeight());
                        Bitmap circleImage = photoAdapter.makeCircularImage(outMap, outMap.getHeight());
                        profilePic.setImageBitmap(circleImage);
                    }

                    username.setText((String)newInfo.get("username"));
                    textAddress.setText((String)newInfo.get("address"));
                    phoneNum.setText((String)newInfo.get("phone"));
                    address = (String)newInfo.get("address");


                    User current = new User();
                    current.ownerEditProfile((String) newInfo.get("username"), photoString, (String)newInfo.get("phone"));
                    if(photoString.equals("")){

                        profilePic
                                .setImageDrawable(getContext()
                                        .getDrawable(R.drawable.default_profile));
                    }


                }
        }
    }

    public void setUp() {
        query = userRef.document(auth.getUid()).collection("notifications");
        FirestoreRecyclerOptions<UserNotification> options = new FirestoreRecyclerOptions.Builder<UserNotification>()
                .setQuery(query, UserNotification.class)
                .build();

        adapter = new FirestoreNotificationAdapter(options);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
    }


    // tell our adapter to start listening as soon as the fragment begins
    @Override
    public void onStart() {
        super.onStart();
        if (adapter != null) {
            adapter.startListening();
        }
        editAccount.setBackgroundColor(getResources().getColor(R.color.colorGray2));
    }
    // tell our adapter to stop listening as soon as the fragment ends
    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }



}
