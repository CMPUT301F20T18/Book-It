package com.example.cmput301f20t18;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.fragment.app.DialogFragment;


import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ChangePasswordDialog extends DialogFragment {

    Button cancel, confirm;
    EditText newPassword, confirmPassword;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.change_password_dialog, container, false);

            newPassword = view.findViewById(R.id.new_password);
            confirmPassword = view.findViewById(R.id.confirm_password);

            cancel = view.findViewById(R.id.cancel_change);
            confirm = view.findViewById(R.id.confirm_change);

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // do required confirmation
                }
            });

            return view;
        }

}



