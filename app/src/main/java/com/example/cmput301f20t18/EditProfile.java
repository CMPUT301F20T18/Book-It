package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * This is a class used to edit a user's profile information.
 * @author Sean Butler
 */

public class EditProfile extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_PROFILE_EDITED = 1;
    private EditText usernameInput, phoneNumInput;
    private Button addressInput;
    private TextView changePass, textAddress;
    private Button changePhoto, deletePhoto, deleteAccount, editDone, myProfileReturn;
    private ImageView profilePic;
    private String photo, address;
    private UserLocation location;
    private String email;

    private final static String TAG = "EP_DEBUG";


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Bundle extras = getIntent().getExtras();

        usernameInput = findViewById(R.id.username_input);
        usernameInput.setText((String) extras.get("username"));

        phoneNumInput = findViewById(R.id.phone_input);
        phoneNumInput.setText((String) extras.get("phone"));

        address = (String) extras.get("address");
        textAddress = findViewById(R.id.text_address);
        addressInput = findViewById(R.id.edit_address_button);

        profilePic  = findViewById(R.id.profile_pic);
        photo = (String) extras.get("photo");

        changePass = findViewById(R.id.password_change);

        changePhoto = findViewById(R.id.change_photo);
        deletePhoto = findViewById(R.id.delete_photo);
        deleteAccount = findViewById(R.id.delete_account);
        myProfileReturn = findViewById(R.id.return_to_my_profile);
        editDone = findViewById(R.id.done_edit_profile);


        // Button to return to profile page, saving no changes
        myProfileReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Button to save changes made to the profile
        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Add Intent return

                Intent i = getIntent();
                i.putExtra("photo", photo);
                i.putExtra("username", usernameInput.getText().toString());
                i.putExtra("phone", phoneNumInput.getText().toString());
                i.putExtra("address", textAddress.getText().toString());
                setResult(RESULT_OK, i);
                finish();

            }
        });

        // Button to delete the user's profile picture
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePic.setImageDrawable(getDrawable(R.drawable.default_profile));

            }
        });

        // Button to have the user upload a profile picture.
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPicture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPicture, RESULT_LOAD_IMAGE);
            }
        });

        // User wants to change password
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User current = new User();
                /*ChangePasswordDialog changePassword = new ChangePasswordDialog();
                changePassword.show(getSupportFragmentManager(), "dialog");
                changePass.setBackgroundColor(getResources().getColor(R.color.colorGray2));*/
                AlertDialog dialog = new AlertDialog.Builder(EditProfile.this, R.style.CustomDialogTheme)
                        .setTitle("Reset password")
                        .setMessage("Send password reset link to\n" + extras.get("email") + "?")
                        .setPositiveButton("Send", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.passwordReset();

                                new AlertDialog.Builder(EditProfile.this, R.style.CustomDialogTheme)
                                        .setMessage("We have emailed your password reset link!")
                                        .setPositiveButton("OK",null)
                                        .show();
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();

                TextView textView = (TextView)  dialog.findViewById(android.R.id.message);
                Typeface face= Typeface.createFromAsset(getAssets(),"poppins.tff");
                textView.setTypeface(face);
                dialog.show();
            }
        });
        changePass.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                changePass.setBackgroundColor(getResources().getColor(R.color.colorGray1));
                return false;
            }
        });

        textAddress.setText(address);

        EditProfile.AddressOnClickListener listener = new EditProfile.AddressOnClickListener(this);
        addressInput.setOnClickListener(listener);

    }

    /**
     * Used to load and resize the user's current profile picture on the edit screen
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!photo.equals("")){
            Bitmap userPhoto = photoAdapter.stringToBitmap(photo);
            float w;
            float h;
            w = profilePic.getHeight();
            h = profilePic.getWidth();
            Log.d("TAG", "onCreate: "+ w);
            Log.d("TAG", "onCreate: "+ h);
            Bitmap outMap = photoAdapter.scaleBitmap(userPhoto, w, h);
            Bitmap circleImage = photoAdapter.makeCircularImage(outMap, outMap.getHeight());
            profilePic.setImageBitmap(circleImage);

        }
    }

    /**
     * onActivityResult triggers after a user uploads a profile picture
     * @param requestCode represents the type of activity that the result is from
     * @param resultCode represents the result of the activity
     * @param data the data from the result of the activity
     */

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case RESULT_LOAD_IMAGE:
                if(resultCode == RESULT_OK){
                    Uri image = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(image, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int colIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(colIndex);
                    cursor.close();
                    Bitmap bitmap = BitmapFactory.decodeFile(picturePath);

                    Bitmap outMap = photoAdapter.scaleBitmap(bitmap, (float) profilePic.getWidth(), (float) profilePic.getHeight());
                    Bitmap circleImage = photoAdapter.makeCircularImage(outMap, outMap.getHeight());
                    photo = photoAdapter.bitmapToString(outMap);
                    profilePic.setImageBitmap(circleImage);
                }
                break;

            default:
                if (resultCode == RESULT_OK) {
                    String title = data.getStringExtra("OUTPUT_TITLE");
                    double longitude = data.getDoubleExtra("OUTPUT_LATITUDE", 0);
                    double latitude = data.getDoubleExtra("OUTPUT_LONGITUDE", 0);


                    location = new UserLocation(title, latitude, longitude);
                    Log.d(TAG, "CHECK 2: location: " + location.getTitle());

                    User current = new User();
                    current.userChangeAddress(location);
                    textAddress.setText(title);
                    break;
                }

        }

    }

    private class AddressOnClickListener implements View.OnClickListener{
        private Context parentContext;
        private final int SELECT_LOCATION_REQUEST_CODE = 0;

        AddressOnClickListener(Context parentContext){
            this.parentContext = parentContext;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(parentContext, SelectLocationActivity.class);
            startActivityForResult(intent, SELECT_LOCATION_REQUEST_CODE);
        }
    }
}