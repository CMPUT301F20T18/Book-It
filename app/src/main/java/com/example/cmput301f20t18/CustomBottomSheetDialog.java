package com.example.cmput301f20t18;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * Dialog that appears at the bottom of the screen when hitting the 3 dots or "more" button on a
 * book card.
 *
 * @author shuval
 * @see HomeScreen
 * @see PostScanActivity
 *
 * reference: https://www.youtube.com/watch?v=IfpRL2K1hJk
 */
public class CustomBottomSheetDialog  extends BottomSheetDialogFragment {

    private static final String TAG = "BottomSheetDialog";
    public static final int CANCEL_BUTTON = 0;
    public static final int EDIT_BUTTON = 1;
    public static final int DELETE_BUTTON = 2;

    private final boolean owner;
    private final int status;
    private final int bookID;

    private BottomSheetListener mListener;

    /**
     * Class constructor.
     *
     * @param owner Whether or not the book in question is owned by the user.
     * @param status The book's status.
     * @param bookID The book's ID.
     */
    public CustomBottomSheetDialog(boolean owner, int status, int bookID) {
        this.owner = owner;
        this.status = status;
        this.bookID = bookID;
    }

    /**
     * Instantiates view.
     *
     * @param inflater Used to inflate view
     * @param container Parent view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view;  // View to be returned

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

        buttonCancel = view.findViewById(R.id.button_cancel_pick_up);
        if (buttonCancel != null) {
            // owner/borrower wants to cancel the pickup
            buttonCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onButtonClick(CANCEL_BUTTON, status, bookID, owner);
                    dismiss();
                }
            });
        }

        if (owner) {
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
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    /**
     * Allows this class to interface with the calling class
     */
    public interface BottomSheetListener {
        /**
         * Allows information to be passed back to calling class.
         * @param button Button ID (see constants in {@link CustomBottomSheetDialog})
         * @param status Book status
         * @param bookID Book ID
         * @param owner Whether or not the book is owned by the user
         */
        void onButtonClick(int button, int status, int bookID, boolean owner);
    }

    /**
     * Performs action depending on which button was pressed. This method is necessary to receive
     * context from the activity.
     *
     * @param button Button ID (see constants in {@link CustomBottomSheetDialog})
     * @param status Book status
     * @param bookID Book ID
     * @param owner Whether or not the book is owned by the user
     * @param context Context of the instantiating Activity
     * @see HomeScreen
     * @see PostScanActivity
     */
    public static void buttonAction(int button, int status, int bookID, boolean owner, Context context) {
        User current = new User();
        switch (button) {
            case CustomBottomSheetDialog.CANCEL_BUTTON:

                if (owner) {
                    // Owner wants to cancel pick up
                    new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                            .setTitle("Cancel pick up")
                            .setMessage("Are you sure you want to cancel this pick up?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    current.ownerCancelPickup(bookID);
                                }
                            })
                            .setNeutralButton("Back", null)
                            .show();
                } else {
                    // Borrower wants to cancel pick up
                    new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                            .setTitle("Cancel pick up")
                            .setMessage("Are you sure you want to cancel this pick up?")
                            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    current.borrowerCancelPickup(bookID);
                                }
                            })
                            .setNeutralButton("Back", null)
                            .show();
                }
                break;


            case CustomBottomSheetDialog.EDIT_BUTTON:
                Intent intent = new Intent(context, AddBookActivity.class);
                intent.putExtra("bookID", bookID);
                intent.putExtra("type", AddBookActivity.EDIT_BOOK);
                ((Activity)context).startActivityForResult(intent, 5);
                break;

            case CustomBottomSheetDialog.DELETE_BUTTON:
                String alertMessage = "";

                switch (status) { // Different warning message depending on book status
                    case Book.STATUS_AVAILABLE:
                        alertMessage = "Are you sure you want to delete this book?";
                        break;

                    case Book.STATUS_REQUESTED:
                        alertMessage = "Deleting this book will decline all requests.\n" +
                                "Are you sure you want to delete this book?";
                        break;

                    case Book.STATUS_ACCEPTED:
                        alertMessage = "Deleting this book will cancel the pick up.\n" +
                                "Are you sure you want to delete this book?";
                        break;
                    case Book.STATUS_BORROWED:
                        alertMessage = "This book is currently being borrowed.\n" +
                                "Are you sure you want to delete this book?";
                        break;
                }
                new AlertDialog.Builder(context, R.style.CustomDialogTheme)
                        .setTitle("Delete book")
                        .setMessage(alertMessage)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                current.ownerDeleteBook(bookID);
                            }
                        })
                        .setNeutralButton("Cancel", null)
                        .show();
                break;
            default:
                // Should never reach this point
                Log.e(TAG, "onButtonClick: Invalid button ID");
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // Ensures that this dialog is only instantiated from classes that implement BottomSheetListener
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }
}
