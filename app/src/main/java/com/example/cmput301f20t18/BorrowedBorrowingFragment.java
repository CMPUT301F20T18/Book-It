package com.example.cmput301f20t18;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A {@link Fragment} subclass that is responsible for creating the list of books to be displayed
 * in Borrowed>Borrowing.
 */
public class BorrowedBorrowingFragment extends Fragment {

<<<<<<< HEAD
    RecyclerView recyclerView;
    List<Book> bookList;
=======
    RecyclerView recyclerView;  // recycler that displays list of books
    List<Book> bookList;        // list of books

    /* Everything below here and above onCreateView() is auto-inserted boilerplate */
>>>>>>> 2d192af805a87c056c8bcb682b924dba02f180a4

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BorrowedBorrowingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BorrowedRequestedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BorrowedBorrowingFragment newInstance(String param1, String param2) {
        BorrowedBorrowingFragment fragment = new BorrowedBorrowingFragment();
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
<<<<<<< HEAD

        View view = inflater.inflate(R.layout.fragment_borrowed_pending, container, false);
=======
        return inflater.inflate(R.layout.fragment_borrowed_pending, container, false);
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
>>>>>>> 2d192af805a87c056c8bcb682b924dba02f180a4

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

<<<<<<< HEAD
=======
        // some hardcoded books
>>>>>>> 2d192af805a87c056c8bcb682b924dba02f180a4
        bookList = new ArrayList<>();
        bookList.add(new Book("The Great Gatsby",9780684801520L, "F. Scott Fitzgerald",
                420, Book.STATUS_BORROWED, null, 1995));
        bookList.add(new Book("To Kill a Mockingbird",9781973907985L, "Harper Lee",
                421, Book.STATUS_BORROWED, null, 1960));
        bookList.add(new Book("Jane Eyre",9780194241762L, "Charlotte Bronte",
                422, Book.STATUS_BORROWED, null, 1979));
        bookList.add(new Book("A Passage to India",9780140180763L, "E. M. Forster",
                423, Book.STATUS_BORROWED, null, 1989));

        Collections.sort(bookList);

        BorrowedRecyclerViewAdapter borrowedAdapter = new BorrowedRecyclerViewAdapter(view.getContext(), bookList);
        recyclerView.setAdapter(borrowedAdapter);

<<<<<<< HEAD
        return view;
=======
>>>>>>> 2d192af805a87c056c8bcb682b924dba02f180a4
    }
}