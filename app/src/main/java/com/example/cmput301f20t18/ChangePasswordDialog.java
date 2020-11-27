package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;


import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class ChangePasswordDialog extends DialogFragment {

    Button cancel, request;
    EditText newPassword, confirmPassword;

    // database info
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore DB = FirebaseFirestore.getInstance();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.change_password_dialog, container, false);


            request = view.findViewById(R.id.confirm_change);
            cancel = view.findViewById(R.id.cancel_change);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            request.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User current = new User();
                    current.passwordReset();
                    dismiss();

                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                    alertDialog.setTitle("Password Reset");
                    alertDialog.setMessage("Your password reset email has been sent to the email linked to this account");
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    alertDialog.show();
                }
            });

            return view;
        }

}



