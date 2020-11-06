package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
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
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Custom RecyclerView Adapter for Book objects
 */
public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private final String TAG = "BookAdapter";
    private Context context;
    private List<Book> bookList;

    public BookAdapter(Context context, List<Book> bookList) {
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
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

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

                // User clicks the "Confirm pick up" button
                holder.buttonConfirmPickUp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(v.getContext(), Scanner.class);
                        Activity origin = (Activity) (v.getContext());
                        origin.startActivityForResult(intent, 0);


                        

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

                // User clicks the "Confirm return" button
                holder.buttonConfirmReturn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
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
        TextView textViewStatus;

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

            buttonViewRequests = itemView.findViewById(R.id.button_view_requests);
            buttonConfirmPickUp = itemView.findViewById(R.id.button_confirm_pick_up);
            buttonMap = itemView.findViewById(R.id.button_mybooks_map);
            buttonUser = itemView.findViewById(R.id.button_mybooks_user);
            buttonConfirmReturn = itemView.findViewById(R.id.button_confirm_return);

        }

    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 0:
                int isbn = data.getIntExtra("isbn", 0);

                // Change the status of the book the borrower scanned
                for (int i = 0; i < bookList.size(); i++) {
                    if (bookList.get(i).getISBN() == isbn) {
                        bookList.get(i).setStatus(Book.STATUS_BORROWED);

                        // update the same book status in the DB
                        FirebaseFirestore DB = FirebaseFirestore.getInstance();
                        CollectionReference books = DB.collection("system").document("System").collection("books");
                        books.document(Integer.toString(bookList.get(i).getId())).update("status", Book.STATUS_BORROWED);
                    }
                }

            case 1:

        }

    }
}


