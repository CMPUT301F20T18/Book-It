package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


/**
 * User represents any user in our system
 * and contains both functionality for owners and borrowers, but not both have to be used
 */
public class User {
    private String username;
    private int appID;
    private String dbID;
    private String address;
    private String pickup;
    private String phone;
    private String email;
    private String profile_picture;

    // debugging
    private final String TAG = "USER_DEBUG";

    // database info
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference userRef = DB.collection("system").document("System").collection("users");
    CollectionReference transRef = DB.collection("system").document("System").collection("transactions");
    CollectionReference bookRef = DB.collection("system").document("System").collection("books");




    /**
     * empty constructor used for firestore
     */
    public User() {

    }


    /**
     * Default constructor for a user, used during registration
     *
     * @param username THe username of the user
     * @param appID The appID of the user
     * @param DB_id The authentication token used by the user
     * @param email The email used by the user
     * @param address The users address
     */
    public User(String username, int appID, String DB_id, String email, String address) {
        this.username = username;
        this.appID = appID;
        this.dbID = DB_id;
        this.email = email;
        this.address = address;
        this.pickup = address;
        this.phone = null;
        this.profile_picture = null;
    }




    /**
     * Deletes the book with bookID from the owners collection
     * Deletes the same book from the DB
     * @param bookID The id of the book to delete
     */
    public void ownerDelBook(int bookID) {
        // delete the book from the DB
        bookRef.document(Integer.toString(bookID)).collection("owned_books").document(Integer.toString(bookID)).delete();
    }




    /**
     * Adds a new book to the owners list of books
     * Adds the same book to the library DB
     * @param isbn   ISBN of the book to be added
     * @param title  Title of the book to be added
     * @param author Author of the book to be added
     */
    public void ownerNewBook(Long isbn, String title, String author, int year) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRef = db.getReference().child("max_book_id");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer val = snapshot.getValue(Integer.class);
                userRef.document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            User current = task.getResult().toObject(User.class);
                            Book book = new Book(title, isbn, author, val, Book.STATUS_AVAILABLE,current, year);
                            bookRef.document(Integer.toString(val)).set(book);
                            userRef.document(auth.getUid()).collection("books_owned").document(Integer.toString(val)).set(book);
                        }
                    }
                });
                mRef.setValue(val + 1);
            }

            // book ID couldn't be found
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Error finding bookID");
            }
        });
    }


    /**
     * accept the request for a book and notify the user it has been accepted
     * remove all other request for books with the same book ID
     * @param request The request transaction to accept
     */
    public void ownerAcceptRequest(Transaction request) {




    }


    /**
     * Deny the request for a book and notify the borrower that the request was declined
     *
     * @param t_id The request transaction to decline
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ownerDenyRequest(int t_id) {

    }


    /**
     * Let owner signOff on a book, marking the book available
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerSignOff(int bookID) {

    }



    /**
     * Request a book from a book owner
     * Add the request to borrower transaction list
     *
     * @param bookID is the id of the book you wish to borrow
     * @return returns a transaction object, containing the borrower request
     */
    public void borrowerRequestBook(int bookID) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRef = db.getReference().child("max_transaction_id");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int val = snapshot.getValue(Integer.class);

                    // retrieve the book, owner, and borrower information from the DB
                    Query book = bookRef.whereEqualTo("id", bookID);
                    Task<DocumentSnapshot> user = userRef.document(auth.getUid()).get();
                    Task<QuerySnapshot> current_book = book.get();


                    Task<List<Task<?>>> combined = Tasks.whenAllComplete(user, current_book).addOnCompleteListener(new OnCompleteListener<List<Task<?>>>() {
                        @Override
                        public void onComplete(@NonNull Task<List<Task<?>>> task) {
                            User current = user.getResult().toObject(User.class);
                            List<Book> requested_list = current_book.getResult().toObjects(Book.class);
                            Book requested = requested_list.get(0);

                            RequestTransaction new_request = new RequestTransaction(requested.getOwner(), current.getUsername(), bookID, val, Book.STATUS_REQUESTED);

                            // update the user transactions
                            userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(new_request.getID())).set(new_request);
                            userRef.document(requested.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(new_request.getID())).set(new_request);
                        }
                    });
                    mRef.setValue(val + 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "onCancelled: Error finding transaction ID");
            }
        });
    }


    /**
     * Pick-up the book, scanning it to mark it as borrowed
     * @param bookID the ID of the book being picked up
     */
    public void borrowerPickupBook(int bookID) {

        // change the status of the book to borrowed
        Query book_query = bookRef.whereEqualTo("ID", bookID);

    }


    /**
     * Drop off the book to the owner, scanning it to mark it as returned
     * @param bookID The ID of the book being dropped off
     */
    public void borrowerDropOffBook(int bookID) {
        // change the status of the book to available



    }


    /**
     * TODO
     * Let the borrower search for books where the description contains a term
     * @param term The term to filter for
     * @return A list of integers representing book IDs of the books that matched
     */
    public ArrayList<Integer> bookSearch(String term) {
        return null;
    }



    /**
     * adds a transaction to a borrowers transaction list
     * @param transaction the transaction id
     */
    public void borrowerTransactionAdd(Transaction transaction) {



    }

    /**
     * deletes a transaction from a owner transaction list
     * @param t_id The transaction id of the transaction to remove
     */
    public void ownerTransactionDelete(int t_id) {

    }




    /**
     * deletes a transaction from a borrower transaction list
     * @param t_id The transaction id of the transaction to remove
     */
    public void borrowerTransactionDelete(int t_id) {


    }



    // setters and getters start here




    public String getUsername() {
        return username;
    }




    public void setUsername(String username) {
        this.username = username;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public String getDbID() {
        return dbID;
    }

    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPickup() {
        return pickup;
    }

    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }
}
