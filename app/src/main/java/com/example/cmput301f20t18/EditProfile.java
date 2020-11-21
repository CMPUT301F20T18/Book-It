package com.example.cmput301f20t18;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class EditProfile extends AppCompatActivity {

    private static final int RESULT_LOAD_IMAGE = 1;
    private static final int RESULT_PROFILE_EDITED = 1;
    private EditText usernameInput, phoneNumInput, emailInput;
    private TextView changePass;
    private Button changePhoto, deletePhoto, deleteAccount, editDone, myProfileReturn;
    private ImageView profilePic;
    private Bitmap photo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        Bundle extras = getIntent().getExtras();

        usernameInput = findViewById(R.id.username_input);
        usernameInput.setText((String) extras.get("username"));

        phoneNumInput = findViewById(R.id.phone_input);
        phoneNumInput.setText((String) extras.get("phone"));

        emailInput = findViewById(R.id.email_input);
        emailInput.setText((String) extras.get("email"));

        profilePic  = findViewById(R.id.profile_pic);

        changePass = findViewById(R.id.password_change);

        changePhoto = findViewById(R.id.change_photo);
        deletePhoto = findViewById(R.id.delete_photo);
        deleteAccount = findViewById(R.id.delete_account);
        myProfileReturn = findViewById(R.id.return_to_my_profile);
        editDone = findViewById(R.id.done_edit_profile);


        myProfileReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Add Intent return

                Intent i = getIntent();
                i.putExtra("photo", photo);
                i.putExtra("username", usernameInput.getText().toString());
                i.putExtra("email", emailInput.getText().toString());
                i.putExtra("phone", phoneNumInput.getText().toString());
                setResult(RESULT_OK, i);
                finish();

            }
        });

        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                profilePic.setImageDrawable(getDrawable(R.drawable.default_profile));

            }
        });

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPicture = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPicture, RESULT_LOAD_IMAGE);
            }
        });



    }

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
                    photo = outMap;
                    profilePic.setImageBitmap(circleImage);
                }
        }
    }


}