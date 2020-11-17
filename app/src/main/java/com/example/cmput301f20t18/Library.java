package com.example.cmput301f20t18;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.Integer.parseInt;

/**
 * Library is a storage and retrieval
 * system for books within our system as well as
 * a interface for the application to interact with
 * the books collection of the database
 */
public class Library {
    FirebaseAuth auth;
    FirebaseFirestore DB;
    CollectionReference users;
    CollectionReference books;
    DocumentReference c_user;
    private static final String TAG = "LIB_DEBUG";

    private static Library mInstance;
    private HashMap<Integer, Book> bookMap;
    public ArrayList<Book> bookList;
    private ArrayList<Transaction> transactionList;
    private ArrayList<User> userList;
    private User current_user;


    private interface FirestoreCallback {
        void onCallback(List<Book> books, List<User> users, User current);
    }

    private void readData(FirestoreCallback callback) {
        Task<QuerySnapshot> user_q = users.get();
        Task<QuerySnapshot> books_q = books.get();
        Task<DocumentSnapshot> c_user_q = c_user.get();


        // initialize our lists
        Tasks.whenAllSuccess(user_q, books_q, c_user_q).addOnCompleteListener(new OnCompleteListener<List<Object>>() {
            @Override
            public void onComplete(@NonNull Task<List<Object>> task) {
                List<Book> temp1 = Objects.requireNonNull(books_q.getResult()).toObjects(Book.class);
                List<User> temp2 = Objects.requireNonNull(user_q.getResult()).toObjects(User.class);
                User temp3 = Objects.requireNonNull(c_user_q.getResult()).toObject(User.class);

                Log.d(TAG, "onComplete: " + Integer.toString(temp1.size()));
                Log.d(TAG, "onComplete: " + Integer.toString(temp2.size()));

                callback.onCallback(temp1, temp2, temp3);

            }
        });
    }


    private Library(Context context) {
        bookList = new ArrayList<Book>();
        userList = new ArrayList<User>();

        // retrieve info from the DB
        auth = FirebaseAuth.getInstance();
        DB = FirebaseFirestore.getInstance();
        users = DB.collection("system").document("System").collection("users");
        books = DB.collection("system").document("System").collection("books");
        c_user = DB.collection("system").document("System").collection("users").document(auth.getUid());

        readData(new FirestoreCallback() {
            @Override
            public void onCallback(List<Book> books, List<User> users, User current) {
                bookList.addAll(books);
                userList.addAll(users);
                current_user = current;

            }
        });

    }


    private void initLib(List<Book> book_l, List<User>user_l, User user_c) {
        Log.d(TAG, "onComplete: " + Integer.toString(book_l.size()));
        Log.d(TAG, "onComplete: " + Integer.toString(user_l.size()));


        // add books to our library
        for (int i = 0 ; i < book_l.size() ; i++) {
            bookList.add(book_l.get(i));
        }

        // add users to our library
        for (int i = 0 ; i < user_l.size() ; i++) {
            userList.add(user_l.get(i));
            if (user_l.get(i).getDbID() == auth.getUid()) {
                current_user = userList.get(i);
            }
        }


        Log.d(TAG, "onCreate: " + this.bookList.get(0).getAuthor());

    }

    // return the instance of our library
    public static synchronized Library getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new Library(context);
        }
        return mInstance;
    }


    public User getCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(User current_user) {
        this.current_user = current_user;
    }
}

