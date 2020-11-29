package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Custom RecyclerView Adapter for Book objects in Borrowed books.
 *
 * @see FirestoreRecyclerAdapter
 * @see BorrowedRequestedFragment
 * @see BorrowedPendingFragment
 * @see BorrowedBorrowingFragment
 * @author Shuval
 * @author deinum
 */
public class FirestoreBorrowedAdapter extends FirestoreRecyclerAdapter<Book, FirestoreBorrowedAdapter.BookViewHolder> {
    final static String TAG = "FBA_DEBUG_BORROWED";
    final static int FRAG_PICKUP = 2;
    final static int FRAG_RETURN = 1;



    public FirestoreBorrowedAdapter(FirestoreRecyclerOptions options) {
        super(options);
    }

    /**
     * This allows {@link #onCreateViewHolder(ViewGroup, int)} to change the recycler layout based
     * on the book status.
     * @param position Position of Book in list.
     * @return Status of book as an int.
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public int getItemViewType(int position) {
        return getItem(position).getStatus();
    }

    /**
     * Called by RecyclerView to display the Book at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the Book
     *               at the given position in bookList.
     * @param i position of book in list.
     * @param book Book to display.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onBindViewHolder(BookViewHolder holder, int i, Book book) {
        /* TODO: Retrieve cover photo from database and assign it to imageView. */
        //holder.imageView.setImageResource(R.drawable.default_cover);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));

        /* TODO: Implement cancel pick up UI and functionality (for "accepted" books) */

        try {
            /* These two TextViews will be null if the book status is "Available" */
            /* TODO: Retrieve username of borrower and assign it to textViewUsername. */
            String uName = book.getOwner_username();
            holder.textViewUsername.setText(uName);
            holder.textViewUserDescription.setText("owner");

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference collection = db.collection("users");
            collection.whereEqualTo("username", uName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        User borrower = task.getResult().toObjects(User.class).get(0);
                        String photoString = borrower.getProfile_picture();
                        if(photoString != null && !photoString.equals("")) {
                            Bitmap bm = photoAdapter.stringToBitmap(photoString);
                            Bitmap photo = photoAdapter.makeCircularImage(bm, holder.buttonUser.getHeight());
                            holder.buttonUser.setImageBitmap(photo);
                            Log.d(TAG, "Picture attached");
                        }
                    }

                    else {
                        Log.d(TAG, "Error Querying for borrower information");
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (book.hasPhotos()) {
            Bitmap bm = book.retrieveCover();
            Bitmap cover = photoAdapter.scaleBitmap(bm, holder.imageView.getLayoutParams().width, holder.imageView.getLayoutParams().height);
            holder.imageView.setImageBitmap(cover);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent slider = new Intent(view.getContext(), ImageSliderActivity.class);
                    slider.putExtra("ID", book.getId());
                    Activity activity = (Activity) view.getContext();

                    activity.startActivity(slider);

                }
            });
        }

        // This is used to open up a user's profile when clicking on their profile photo
        holder.buttonUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), CheckProfileActivity.class);
                intent.putExtra("USERNAME", book.getOwner_username());
                view.getContext().startActivity(intent);
            }
        });

        try {
            // This is used to view the pick up location when clicking the map button
            holder.buttonMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), ShowMapLocationActivity.class);
                    intent.putExtra("bookID", book.getId());
                    v.getContext().startActivity(intent);
                }
            });
        }
        catch (Exception e) {
            Log.d(TAG, e.toString());
        }



                /* holder will be updated differently depending on Book status. */
        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE:
                /* Books in Borrowed section should never have status "available" */
                Log.e(TAG, "onBindViewHolder: \"Available\" status not allowed in borrowed section.");
                break;

            case Book.STATUS_REQUESTED:
                /* User clicks the "Cancel request" button */
                holder.buttonCancelRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new android.app.AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                                .setTitle("Cancel request")
                                .setMessage("Are you sure you want to cancel your request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // find the transaction associated with this book for the current user
                                        User current = new User();
                                        Log.d(TAG, "onClick: " + book.getId());
                                        current.borrowerCancelRequest(book.getId());

                                    }
                                })
                                .setNeutralButton("No", null)
                                .show();
                    }
                });
                break;

            case Book.STATUS_ACCEPTED:
                /* User clicks the "Confirm pick up" button */
                holder.buttonConfirmPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, " bookID: " + book.getId());
                        Log.d(TAG, " book ISBN:  " + book.getISBN());

                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        intent.putExtra("bookID", book.getId());
                        intent.putExtra("type", 1);
                        intent.putExtra("eISBN", book.getISBN());
                        Activity main = (Activity) v.getContext();
                        main.startActivityForResult(intent, FRAG_PICKUP);
                    }
                });

                /* User clicks the 3 dots "more" button */
                holder.buttonMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CustomBottomSheetDialog bottomSheet =
                                new CustomBottomSheetDialog(false, book.getStatus(), book.getId());
                        bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(),
                                "customBottomSheet");
                    }
                });
                break;

            case Book.STATUS_BORROWED:

                /* User clicks the "Confirm return" button */
                holder.buttonConfirmReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, " bookID: " + book.getId());
                        Log.d(TAG, " book ISBN:  " + book.getISBN());

                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        intent.putExtra("bookID", book.getId());
                        intent.putExtra("type", 1);
                        intent.putExtra("eISBN", book.getISBN());
                        Activity main = (Activity) v.getContext();
                        main.startActivityForResult(intent, FRAG_RETURN);
                    }
                });
                break;
        }
    }

    /**
     * Called when RecyclerView needs a new {@link FirestoreBookAdapter.BookViewHolder} of the
     * given type to represent a Book.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position.
     * @param viewType The view type of the new View, based on the book status.
     * @return A new BookViewHolder that holds a View of the given view type.
     * @see FirestoreBookAdapter.BookViewHolder
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(FirestoreBorrowedAdapter.BookViewHolder, int, Book)
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "viewType: : " + Integer.toString(viewType));

        switch (viewType) {
            case Book.STATUS_AVAILABLE:
                return new BookViewHolder(inflater.inflate(R.layout.card_no_requests, null));
            case Book.STATUS_REQUESTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_borrowed_requested, null));
            case Book.STATUS_ACCEPTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_pending, null));
            case Book.STATUS_BORROWED:
                return new BookViewHolder(inflater.inflate(R.layout.card_lending, null));
            default: Log.d(TAG, "Error: Book status not found");
        }

        return null;
    }

    /**
     * Caches Views from layout file.
     * @see #onBindViewHolder(FirestoreBorrowedAdapter.BookViewHolder, int, Book)
     */
    public class BookViewHolder extends RecyclerView.ViewHolder {
        /* Not all layout files will have all of these views, so some may be null.
         * The switch in onBindViewHolder() ensures that this is not an issue. */

        ImageButton imageView;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;
        TextView textViewISBN;

        TextView textViewUsername;
        TextView textViewUserDescription;



        Button profilePic;

        Button buttonCancelRequest;
        Button buttonConfirmPickUp;
        Button buttonMap;
        ImageButton buttonUser;
        Button buttonConfirmReturn;
        Button buttonMore;

        /**
         * Class constructor.
         *
         * @param itemView Used to retrieve Views from layout file.
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);

            textViewTitle = itemView.findViewById(R.id.text_book_title);
            textViewAuthor = itemView.findViewById(R.id.text_book_author);
            textViewYear = itemView.findViewById(R.id.text_book_year);
            textViewISBN = itemView.findViewById(R.id.text_book_isbn);



            textViewUsername = itemView.findViewById(R.id.text_username);
            textViewUserDescription = itemView.findViewById(R.id.text_user_description);

            buttonCancelRequest = itemView.findViewById(R.id.button_cancel_request);
            buttonConfirmPickUp = itemView.findViewById(R.id.button_confirm_pick_up);
            buttonMap = itemView.findViewById(R.id.button_mybooks_map);
            buttonUser = itemView.findViewById(R.id.button_mybooks_user);
            buttonConfirmReturn = itemView.findViewById(R.id.button_confirm_return);
            buttonMore = itemView.findViewById(R.id.button_book_more);
        }

    }

}
