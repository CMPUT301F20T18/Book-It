package com.example.cmput301f20t18;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CustomBottomSheetDialog  extends BottomSheetDialogFragment {

    public static final int CANCEL_BUTTON = 0;
    public static final int EDIT_BUTTON = 1;
    public static final int DELETE_BUTTON = 2;

    private BottomSheetListener mListener;
    private boolean owner;
    private int status;
    private int bookID;

    public CustomBottomSheetDialog(boolean owner, int status, int bookID) {
        this.owner = owner;
        this.status = status;
        this.bookID = bookID;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;

        Button buttonCancel;
        Button buttonEdit;
        Button buttonDelete;

        if (status == Book.STATUS_ACCEPTED) {
            if (owner) {
                view = inflater.inflate(R.layout.bottom_sheet_mybooks_with_cancel, container, false);
            } else {
                // This is the only bottom sheet in the Borrowed section
                view = inflater.inflate(R.layout.bottom_sheet_borrowed, container, false);
            }
        } else { // User owns the book
            view = inflater.inflate(R.layout.bottom_sheet_mybooks_no_cancel, container, false);
        }

        try {
            buttonCancel = view.findViewById(R.id.button_cancel_pick_up);

            // owner wants to cancel the pickup
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClick(CANCEL_BUTTON, status, bookID, owner);
                    dismiss();
                }
            });
        } catch (Exception ignored) {}

        try {
            buttonEdit = view.findViewById(R.id.button_edit_book);
            buttonDelete = view.findViewById(R.id.button_delete_book);

            // owner wants to edit the book
            buttonEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClick(EDIT_BUTTON, status, bookID, owner);
                    dismiss();
                }
            });


            // deletes the book from the owners collection
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClick(DELETE_BUTTON, status, bookID, owner);
                    dismiss();
                }
            });
        } catch (Exception ignored) {}

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public interface BottomSheetListener {
        void onButtonClick(int button, int status, int bookID, boolean owner);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }
}
