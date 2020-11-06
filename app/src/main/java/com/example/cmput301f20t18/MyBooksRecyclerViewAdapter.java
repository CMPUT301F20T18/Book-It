package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.hotspot2.omadm.PpsMoParser;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Custom RecyclerView Adapter for Book objects in My Books.
 *
 * @see MyBooksAvailableFragment
 * @see MyBooksPendingFragment
 * @see MyBooksLendingFragment
 */
public class MyBooksRecyclerViewAdapter extends
        RecyclerView.Adapter<MyBooksRecyclerViewAdapter.BookViewHolder> {

    private final String TAG = "MyBooksRecViewAdapter";
    private Context context;
    private List<Book> bookList;    // Books to display
    private Fragment fragment;

    /**
     * Class Constructor.
     *
     * @param context Context to inflate from.
     * @param bookList List of books to display.
     */
    public MyBooksRecyclerViewAdapter(Context context, List<Book> bookList, Fragment fragment) {
        this.context = context;
        this.bookList = bookList;
        this.fragment = fragment;
    }

    /**
     * This allows {@link #onCreateViewHolder(ViewGroup, int)} to change the recycler layout based
     * on the book status.
     *
     * @param position Index of Book in bookList.
     * @return Status of book as an int.
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public int getItemViewType(int position) {

        int status = bookList.get(position).getStatus();    // retrieve book status
        switch (status) {
            case Book.STATUS_AVAILABLE: return 0;
            case Book.STATUS_REQUESTED: return 1;
            case Book.STATUS_ACCEPTED: return 2;
            case Book.STATUS_BORROWED: return 3;
        }

        // should never reach this point
        Log.e(TAG, "getItemViewType: Invalid book status");
        return 0;
    }

    /**
     * Called when RecyclerView needs a new {@link MyBooksRecyclerViewAdapter.BookViewHolder} of the
     * given type to represent a Book.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position.
     * @param viewType The view type of the new View, based on the book status.
     * @return A new BookViewHolder that holds a View of the given view type.
     * @see MyBooksRecyclerViewAdapter.BookViewHolder
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(BookViewHolder, int)
     */
    @NonNull
    @Override
    public MyBooksRecyclerViewAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        /* viewType holds the status of the Book. The switch assigns the appropriate layout file. */
        switch (viewType) {
            case Book.STATUS_AVAILABLE:
                return new BookViewHolder(inflater.inflate(R.layout.card_no_requests, null));

            case Book.STATUS_REQUESTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_requested, null));

            case Book.STATUS_ACCEPTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_pending, null));

            case Book.STATUS_BORROWED:
                return new BookViewHolder(inflater.inflate(R.layout.card_lending, null));

            default: // should never reach this point
                Log.e(TAG, "onCreateViewHolder: Invalid viewType value");
                return new BookViewHolder(inflater.inflate(R.layout.card_no_requests, null));
        }
    }

    /**
     * Called by RecyclerView to display the Book at the specified position.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the Book
     *               at the given position in bookList.
     * @param position Index of the book in bookList.
     */
    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Book book = bookList.get(position);

        /* TODO: Retrieve cover photo from database and assign it to imageView. */
        //holder.imageView.setImageResource(R.drawable.default_cover);

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));

        /* TODO: Implement delete/edit UI and functionality. */
        /* TODO: Implement cancel pick up UI and functionality (for "accepted" books) */

        /* holder will be updated differently depending on Book status. */
        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE:
                /* Nothing should happen here? Will delete later. */
                break;

            case Book.STATUS_REQUESTED:
                /* User clicks the "View requests" button */
                holder.buttonViewRequests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* TODO: Get requests from database and pass to activity. */
                        Intent intent = new Intent(v.getContext(), ViewRequestsActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;

            case Book.STATUS_ACCEPTED:
                /* TODO: Retrieve username of borrower and assign it to textViewUsername. */
                holder.textViewUsername.setText("Username");

                holder.textViewUserDescription.setText(R.string.picking_up);

                /* User clicks the "Confirm pick up" button */
                holder.buttonConfirmPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* TODO: Scanner needs to know that an owner is trying to confirm pick up. */
                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        intent.putExtra("type", 1);
                        fragment.startActivityForResult(intent, 0);



                    }
                });

                /* User clicks on profile photo */
                holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* TODO: open the borrower's profile in a new activity. */
                        Toast.makeText(context, "*opens user profile* OwO", Toast.LENGTH_SHORT).show();
                    }
                });

                /* User clicks on map button */
                holder.buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Owner can select a new location if they so please. */
                        Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;

            case Book.STATUS_BORROWED:
                /* TODO: Retrieve username of borrower and assign it to textViewUsername. */
                holder.textViewUsername.setText("Username");

                holder.textViewUserDescription.setText(R.string.borrowing);

                /* User clicks the "Confirm return" button */
                holder.buttonConfirmReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* TODO: Scanner needs to know that an owner is trying to confirm return. */
                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        Activity origin = (Activity) (v.getContext());
                        origin.startActivityForResult(intent, 1);


                    }
                });

                /* User clicks on profile photo */
                holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* TODO: open the borrower's profile in a new activity. */
                        Toast.makeText(context, "*opens user profile* OwO", Toast.LENGTH_SHORT).show();
                    }
                });

                /* User clicks on map button */
                holder.buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        /* Owner can select a new location if they so please. */
                        Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;
        }
    }

    /**
     * Returns the number of Books in bookList
     *
     * @return number of Books in bookList
     */
    @Override
    public int getItemCount() {
        return bookList.size();
    }

    /**
     * Caches Views from layout file.
     * @see #onBindViewHolder(BookViewHolder, int)
     */
    public static class BookViewHolder extends RecyclerView.ViewHolder {

        /* Not all layout files will have all of these views, so some may be null.
        *  The switch in onBindViewHolder() ensures that this is not an issue. */

        ImageView imageView;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;
        TextView textViewISBN;

        TextView textViewUsername;
        TextView textViewUserDescription;

        Button buttonViewRequests;
        Button buttonConfirmPickUp;
        Button buttonMap;
        Button buttonUser;
        Button buttonConfirmReturn;

        /**
         * Class constructor.
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

            buttonViewRequests = itemView.findViewById(R.id.button_view_requests);
            buttonConfirmPickUp = itemView.findViewById(R.id.button_confirm_pick_up);
            buttonMap = itemView.findViewById(R.id.button_mybooks_map);
            buttonUser = itemView.findViewById(R.id.button_mybooks_user);
            buttonConfirmReturn = itemView.findViewById(R.id.button_confirm_return);

        }

    }

}


