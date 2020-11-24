package com.example.cmput301f20t18;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import static com.example.cmput301f20t18.photoAdapter.stringToByte;

/**
 * Custom RecyclerView Adapter for Book objects in My Books.
 * @see FirestoreRecyclerAdapter
 * @see MyBooksAvailableFragment
 * @see MyBooksPendingFragment
 * @see MyBooksLendingFragment
 * @author deinum
 */
public class FirestoreBookAdapter
        extends FirestoreRecyclerAdapter<Book, FirestoreBookAdapter.BookViewHolder>
         {
    final static String TAG = "FBA_DEBUG_OWNED";
    final static int VIEW_REQUESTS = 3;
    final static int FRAG_PENDING = 2;
    final static int FRAG_LENDING = 1;

    private Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public FirestoreBookAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
    }


    /**
     * This allows {@link #onCreateViewHolder(ViewGroup, int)} to change the recycler layout based
     * on the book status.
     *
     * @param position Position of Book in list.
     * @return Status of book as an int.
     * @see #onCreateViewHolder(ViewGroup, int)
     */
    @Override
    public int getItemViewType(int position) {
        Log.d(TAG, "getItemViewType: Position of " + position);
        Book book = getItem(position);
        int status = book.getStatus();
        return status;
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
        if (book.hasPhotos()) {
            Bitmap bitmap = book.retrieveCover();
            Bitmap photo = photoAdapter.scaleBitmap(bitmap, (float) holder.imageView.getLayoutParams().width, (float) holder.imageView.getLayoutParams().height);
            holder.imageView.setImageBitmap(photo);
        }

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));

        if (book.getBorrower_username() != null) {
            holder.textViewUsername.setText(book.getBorrower_username());
        }

        // This is used to open up a user's profile when clicking on their profile photo
        View.OnClickListener openProfileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CheckProfileActivity.class);
                intent.putExtra("USERNAME", book.getBorrower_username());
                v.getContext().startActivity(intent);
            }
        };

        // This is used to open the location selection activity when clicking on the map icon
        View.OnClickListener openMapListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Owner can select a new location if they so please. */
                Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                intent.putExtra("bookID", book.getId());
                v.getContext().startActivity(intent);
            }
        };

        /* User clicks the 3 dots "more" button */
        holder.buttonMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomBottomSheetDialog bottomSheet =
                        new CustomBottomSheetDialog(true, book.getStatus(), book.getId());
                bottomSheet.show(((AppCompatActivity) v.getContext()).getSupportFragmentManager(),
                        "customBottomSheet");
            }
        });

        /* holder will be updated differently depending on Book status. */
        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE:
                break;

            case Book.STATUS_REQUESTED:
                /* User clicks the "View requests" button */
                holder.buttonViewRequests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ViewRequestsActivity.class);
                        intent.putExtra("bookID", book.getId());
                        Activity main = (Activity) v.getContext();
                        main.startActivity(intent);
                    }
                });
                break;

            case Book.STATUS_ACCEPTED:
                /* TODO: Retrieve username of borrower and assign it to textViewUsername. */

                holder.textViewUserDescription.setText(R.string.picking_up);

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
                        main.startActivityForResult(intent, FRAG_PENDING);                    }
                });

                holder.buttonUser.setOnClickListener(openProfileListener);
                holder.buttonMap.setOnClickListener(openMapListener);
                break;

            case Book.STATUS_BORROWED:
                /* TODO: Retrieve username of borrower and assign it to textViewUsername. */
                holder.textViewUsername.setText(book.getOwner_username());
                holder.textViewUserDescription.setText(R.string.borrowing);

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
                        main.startActivityForResult(intent, FRAG_LENDING);

                    }
                });

                holder.buttonUser.setOnClickListener(openProfileListener);
                holder.buttonMap.setOnClickListener(openMapListener);
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
     * @see #onBindViewHolder(FirestoreBookAdapter.BookViewHolder, int, Book)
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "viewType: : " + Integer.toString(viewType));

        /* viewType holds the status of the Book. The switch assigns the appropriate layout file. */
        switch (viewType) {
            case Book.STATUS_AVAILABLE:
                Log.d(TAG, "onCreateViewHolder: Got to available!!");
                return new BookViewHolder(inflater.inflate(R.layout.card_no_requests, null));
            case Book.STATUS_REQUESTED:
                Log.d(TAG, "onCreateViewHolder: Got to requested!");
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_requested, null));
            case Book.STATUS_ACCEPTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_pending, null));
            case Book.STATUS_BORROWED:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_lending, null));
            default: Log.d(TAG, "Error: Book status not found");
        }

        return null;

    }



    /**
     * Caches Views from layout file.
     * @see #onBindViewHolder(BookViewHolder, int, Book)
     */
    public class BookViewHolder extends RecyclerView.ViewHolder {
         /* Not all layout files will have all of these views, so some may be null.
          * The switch in onBindViewHolder() ensures that this is not an issue. */

        ImageView imageView;
        TextView textViewTitle;
        TextView textViewAuthor;
        TextView textViewYear;
        TextView textViewISBN;

        TextView textViewUsername;
        TextView textViewUserDescription;

        Button buttonViewRequests;
        Button buttonCancelRequest;
        Button buttonConfirmPickUp;
        Button buttonMap;
        Button buttonUser;
        Button buttonConfirmReturn;
        Button buttonMore;

        /**
         * Class constructor.
         *
         * @param itemView Used to retrieve Views from layout file.
         */
        public BookViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "onCreateBookViewHolder: Begin!!");
            imageView = itemView.findViewById(R.id.image_view);

            textViewTitle = itemView.findViewById(R.id.text_book_title);
            textViewAuthor = itemView.findViewById(R.id.text_book_author);
            textViewYear = itemView.findViewById(R.id.text_book_year);
            textViewISBN = itemView.findViewById(R.id.text_book_isbn);

            textViewUsername = itemView.findViewById(R.id.text_username);
            textViewUserDescription = itemView.findViewById(R.id.text_user_description);

            buttonViewRequests = itemView.findViewById(R.id.button_view_requests);
            buttonCancelRequest = itemView.findViewById(R.id.button_cancel_request);
            buttonConfirmPickUp = itemView.findViewById(R.id.button_confirm_pick_up);
            buttonMap = itemView.findViewById(R.id.button_mybooks_map);
            buttonUser = itemView.findViewById(R.id.button_mybooks_user);
            buttonConfirmReturn = itemView.findViewById(R.id.button_confirm_return);
            buttonMore = itemView.findViewById(R.id.button_book_more);

            Log.d(TAG, "onCreateBookViewHolder: End!!");
        }

    }

}

