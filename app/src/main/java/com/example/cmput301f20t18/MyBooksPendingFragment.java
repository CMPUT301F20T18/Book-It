package com.example.cmput301f20t18;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyBooksPendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyBooksPendingFragment extends Fragment {

    RecyclerView recyclerView;
    List<Book> bookList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyBooksPendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBooksPendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBooksPendingFragment newInstance(String param1, String param2) {
        MyBooksPendingFragment fragment = new MyBooksPendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_books_pending, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        bookList = new ArrayList<>();
        bookList.add(new Book("The Great Gatsby",9780684801520L, "F. Scott Fitzgerald",
                420, Book.STATUS_ACCEPTED, null, 1995));
        bookList.add(new Book("To Kill a Mockingbird",9781973907985L, "Harper Lee",
                421, Book.STATUS_ACCEPTED, null, 1960));
        bookList.add(new Book("Jane Eyre",9780194241762L, "Charlotte Bronte",
                422, Book.STATUS_ACCEPTED, null, 1979));
        bookList.add(new Book("A Passage to India",9780140180763L, "E. M. Forster",
                423, Book.STATUS_ACCEPTED, null, 1989));

        Collections.sort(bookList);

        MyBooksRecyclerViewAdapter myBooksRecyclerViewAdapter = new MyBooksRecyclerViewAdapter(view.getContext(), bookList, this);
        recyclerView.setAdapter(myBooksRecyclerViewAdapter);

        return view;
    }

    /**
     * Handle users scanning books to return / borrow
     * @param requestCode The request code for the calling activity
     * @param resultCode The result code from the called activity
     * @param data The data embedded in the intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String isbn_string = data.getStringExtra("ISBN");
        Long isbn = Long.parseLong(isbn_string);
        Log.d("FRAGMENT", "Got here!");
        switch (resultCode) {
            // borrower scans book to confirm pickup
            case 0:

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

                // owner scans to confirm return
            case 1:

                // Change the status of the book the owner scanned
                for (int i = 0; i < bookList.size(); i++) {
                    if (bookList.get(i).getISBN() == isbn) {
                        bookList.get(i).setStatus(Book.STATUS_AVAILABLE);

                        // update the same book status in the DB
                        FirebaseFirestore DB = FirebaseFirestore.getInstance();
                        CollectionReference books = DB.collection("system").document("System").collection("books");
                        books.document(Integer.toString(bookList.get(i).getId())).update("status", Book.STATUS_AVAILABLE);
                    }
                }
        }

    }
}