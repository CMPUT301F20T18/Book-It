package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

/**
 * Custom RecyclerView Adapter for Book objects in My Books.
 * Reference: https://www.simplifiedcoding.net/android-recyclerview-cardview-tutorial/
 *
 * @see FirestoreRecyclerAdapter
 * @see MyBooksAvailableFragment
 * @see MyBooksPendingFragment
 * @see MyBooksLendingFragment
 * @author deinum
 * @author shuval
 */
public class FirestoreMyBooksAdapter
        extends FirestoreRecyclerAdapter<Book, FirestoreMyBooksAdapter.BookViewHolder>
         {
    final static String TAG = "FBA_DEBUG_OWNED";
    final static int VIEW_REQUESTS = 3;
    final static int FRAG_PENDING = 2;
    final static int FRAG_LENDING = 1;
    final static int FRAG_IMAGE = 4;

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See
     * {@link FirestoreRecyclerOptions} for configuration options.
     *
     * @param options options to configure
     */
    public FirestoreMyBooksAdapter(FirestoreRecyclerOptions options, Context context) {
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
        return getItem(position).getStatus(); // Book status determines which layout file to use
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

        if(book != null) {
            if (book.hasPhotos()) {
                Bitmap bitmap = book.retrieveCover();
                Bitmap photo = photoAdapter.scaleBitmap(bitmap, (float) holder.imageView.getLayoutParams().width, (float) holder.imageView.getLayoutParams().height);
                holder.imageView.setImageBitmap(photo);
                holder.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent slider = new Intent(view.getContext(), ImageSliderActivity.class);
                        slider.putExtra("ID", book.getId());
                        Activity activity = (Activity) view.getContext();

                        activity.startActivity(slider);

                        //Start slider activity here, photos is the list of Bitmaps needed
                    }
                });
            }
            else{
                holder.imageView.setImageDrawable(context.getDrawable(R.drawable.default_cover));
            }
        }

        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));


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

        // buttonUser will be null for "requested" or "available" books
        if (holder.buttonUser != null) {
            try{
                String uName = book.getBorrower_username();
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collection = db.collection("users");
                collection.whereEqualTo("username", uName).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List borrowers = task.getResult().toObjects(User.class);
                            if(borrowers.size()>0) {
                                User borrower = (User) borrowers.get(0);
                                String photoString = borrower.getProfile_picture();
                                if (!photoString.equals("")) {
                                    Bitmap bm = photoAdapter.stringToBitmap(photoString);
                                    Bitmap photo = photoAdapter.makeCircularImage(bm, holder.buttonUser.getHeight());
                                    holder.buttonUser.setImageBitmap(photo);
                                    Log.d(TAG, "Picture attached");
                                }
                            }
                        }

                        else {
                            Log.d(TAG, "Error Querying for borrower information");
                        }
                    }
                });
            } catch (Exception ignore) {}
        }
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
                String uName = book.getBorrower_username();
                if (uName != null) {
                    holder.textViewUsername.setText(book.getBorrower_username());
                    Log.d(TAG, "Requester is " + uName);

                }
                holder.textViewUserDescription.setText(R.string.picking_up);

                /* User clicks the "Confirm pick up" button */
                holder.buttonConfirmPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, " bookID: " + book.getId());
                        Log.d(TAG, " book ISBN:  " + book.getISBN());

                        Intent intent = new Intent(v.getContext(), ScannerActivity.class);
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
                holder.textViewUsername.setText(book.getBorrower_username());
                holder.textViewUserDescription.setText(R.string.borrowing);

                /* User clicks the "Confirm return" button */
                holder.buttonConfirmReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d(TAG, " bookID: " + book.getId());
                        Log.d(TAG, " book ISBN:  " + book.getISBN());

                        Intent intent = new Intent(v.getContext(), ScannerActivity.class);
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
     * Called when RecyclerView needs a new {@link FirestoreMyBooksAdapter.BookViewHolder} of the
     * given type to represent a Book.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an
     *               adapter position.
     * @param viewType The view type of the new View, based on the book status.
     * @return A new BookViewHolder that holds a View of the given view type.
     * @see FirestoreMyBooksAdapter.BookViewHolder
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(FirestoreMyBooksAdapter.BookViewHolder, int, Book)
     */
    @NonNull
    @Override
    public BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        Log.d(TAG, "viewType: : " + viewType);

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
                return new BookViewHolder(inflater.inflate(R.layout.card_lending, null));
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

        ImageButton imageView;
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
        }

    }



}

