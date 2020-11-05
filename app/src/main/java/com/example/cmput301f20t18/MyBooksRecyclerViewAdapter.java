package com.example.cmput301f20t18;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Custom RecyclerView Adapter for Book objects in My Books
 */
public class MyBooksRecyclerViewAdapter extends RecyclerView.Adapter<MyBooksRecyclerViewAdapter.BookViewHolder> {

    private final String TAG = "MyBooksRecViewAdapter";
    private Context context;
    private List<Book> bookList;    // Books to display

    /**
     * Constructor receives context and list of books to display
     * @param context Context to inflate
     * @param bookList List of books to display
     */
    public MyBooksRecyclerViewAdapter(Context context, List<Book> bookList) {
        this.context = context;
        this.bookList = bookList;
    }

    @Override
    public int getItemViewType(int position) {

        Book book = bookList.get(position);

        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE: return 0;
            case Book.STATUS_REQUESTED: return 1;
            case Book.STATUS_ACCEPTED: return 2;
            case Book.STATUS_BORROWED: return 3;
        }
        return -1;
    }

    @NonNull
    @Override
    public MyBooksRecyclerViewAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);

        switch (viewType) {
            case Book.STATUS_AVAILABLE:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_no_requests, null));
            case Book.STATUS_REQUESTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_requested, null));
            case Book.STATUS_ACCEPTED:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_pending, null));
            case Book.STATUS_BORROWED:
                return new BookViewHolder(inflater.inflate(R.layout.card_mybooks_lending, null));
            default: Log.d(TAG, "Error: Book status not found");
        }

        View view = inflater.inflate(R.layout.card_mybooks_no_requests, null);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Book book = bookList.get(position);

        //holder.imageView.setImageResource(R.drawable.default_cover);
        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));

        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE:
                //todo
                break;
            case Book.STATUS_REQUESTED:

                // User clicks the "View requests" button
                holder.buttonViewRequests.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ViewRequestsActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;
            case Book.STATUS_ACCEPTED:
                holder.textViewUsername.setText("Username");
                holder.textViewUserDescription.setText("picking up");

                // User clicks the "Confirm pick up" button
                holder.buttonConfirmPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // The book isbn needs to be passed to Scanner
                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        v.getContext().startActivity(intent);
                        //TODO: upon successful scan, book status should be changed to "borrowed"
                        // and should be updated in firestore
                    }
                });

                // User clicks on profile photo
                holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "*opens user profile* OwO", Toast.LENGTH_SHORT).show();
                        // TODO: open the users profile in a new activity
                    }
                });

                // User clicks on map button
                holder.buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;
            case Book.STATUS_BORROWED:
                holder.textViewUsername.setText("Username");
                holder.textViewUserDescription.setText("borrowing");

                // User clicks the "Confirm return" button
                holder.buttonConfirmReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // The book isbn needs to be passed to Scanner
                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        v.getContext().startActivity(intent);
                        //TODO: upon successful scan, book status should be changed to "available"
                        // and should be updated in firestore
                    }
                });

                // User clicks on profile photo
                holder.buttonUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "*opens user profile* OwO", Toast.LENGTH_SHORT).show();
                        // TODO: open the users profile in a new activity
                    }
                });

                // User clicks on map button
                holder.buttonMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), ChooseLocationActivity.class);
                        v.getContext().startActivity(intent);
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public static class BookViewHolder extends RecyclerView.ViewHolder {

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
