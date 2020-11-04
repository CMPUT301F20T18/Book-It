package com.example.cmput301f20t18;

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

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
                break; //TODO
            default: Log.d(TAG, "Error: Book status not found");
        }

        View view = inflater.inflate(R.layout.card_mybooks_no_requests, null);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookViewHolder holder, int position) {

        Book book = bookList.get(position);

        holder.imageView.setImageResource(R.drawable.default_cover);
        holder.textViewTitle.setText(book.getTitle());
        holder.textViewAuthor.setText(book.getAuthor());
        holder.textViewYear.setText(String.valueOf(book.getYear()));
        holder.textViewISBN.setText(String.valueOf(book.getISBN()));

        int status = book.getStatus();
        switch (status) {
            case Book.STATUS_AVAILABLE:
                holder.textViewStatus.setText("Available");
                break;
            case Book.STATUS_REQUESTED:
                holder.textViewStatus.setText("Requested");
                holder.button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog dialog = (new AlertDialog.Builder(v.getContext())
                                .setTitle("View Requests")
                                .setMessage("todo")
                                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //TODO
                                    }
                                })
                                .setNegativeButton("back", null)).show();

                        Button buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        buttonPositive.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
                        Button buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        buttonNegative.setTextColor(ContextCompat.getColor(v.getContext(), R.color.colorPrimaryDark));
                    }
                });
                break;
            case Book.STATUS_ACCEPTED:
                holder.textViewStatus.setText("Accepted");
                break;
            case Book.STATUS_BORROWED:
                holder.textViewStatus.setText("Borrowed");
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

        Button button;

        public BookViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            textViewTitle = itemView.findViewById(R.id.text_book_title);
            textViewAuthor = itemView.findViewById(R.id.text_book_author);
            textViewYear = itemView.findViewById(R.id.text_book_year);
            textViewISBN = itemView.findViewById(R.id.text_book_isbn);
            button = itemView.findViewById(R.id.button_view_requests);

            // temporary
            textViewStatus = itemView.findViewById(R.id.text_book_status);

        }
    }
}
