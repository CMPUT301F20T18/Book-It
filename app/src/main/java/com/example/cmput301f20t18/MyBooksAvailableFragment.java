package com.example.cmput301f20t18;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Fragment} subclass that is responsible for creating the list of books to be displayed
 * in My Books>Available.
 */
public class MyBooksAvailableFragment extends Fragment {

    RecyclerView recyclerView;
    List<Book> bookList;

    /* Everything below here and above onCreateView() is auto-inserted boilerplate */

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public MyBooksAvailableFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyBooksAvailableFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyBooksAvailableFragment newInstance(String param1, String param2) {
        MyBooksAvailableFragment fragment = new MyBooksAvailableFragment();
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

    /**
     * Instantiates view. The documentation recommends only inflating the layout here and doing
     * everything else in {@link #onViewCreated(View, Bundle)}.
     *
     * @param inflater Used to inflate view
     * @param container Parent view
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_books_available, container, false);
    }

    /**
     * Populates bookList and sets up adapter to display the list.
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has
     * returned, but before any saved state has been restored in to the view.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        // hardcoded some books
        bookList = new ArrayList<>();
        bookList.add(new Book("The Great Gatsby", 9780684801520L, "F. Scott Fitzgerald",
                420, Book.STATUS_REQUESTED, null, 1995));
        bookList.add(new Book("To Kill a Mockingbird", 9781973907985L, "Harper Lee",
                421, Book.STATUS_AVAILABLE, null, 1960));
        bookList.add(new Book("Jane Eyre", 9780194241762L, "Charlotte Bronte",
                422, Book.STATUS_REQUESTED, null, 1979));
        bookList.add(new Book("A Passage to India", 9780140180763L, "E. M. Forster",
                423, Book.STATUS_AVAILABLE, null, 1989));

        Collections.sort(bookList);

        MyBooksRecyclerViewAdapter myBooksRecyclerViewAdapter = new MyBooksRecyclerViewAdapter(view.getContext(), bookList, this);
        recyclerView.setAdapter(myBooksRecyclerViewAdapter);

    }
}