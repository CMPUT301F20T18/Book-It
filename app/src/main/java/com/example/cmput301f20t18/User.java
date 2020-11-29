 package com.example.cmput301f20t18;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableReference;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firestore.v1.Write;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * A user within our system, contains the methods / variables for any transaction between users
 * @see Transaction
 * @author deinum
 */
public class User {

    // user info
    private String username;
    private int appID;
    private String dbID;
    private UserLocation address;
    private String phone;
    private String email;
    private String profile_picture;
    private String instanceToken;

    // debugging
    private final String TAG = "USER_DEBUG";

    // database info
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    CollectionReference userRef = DB.collection("users");
    CollectionReference transRef = DB.collection("transactions");
    CollectionReference bookRef = DB.collection("books");




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
    public User(String username, int appID, String DB_id, String email, UserLocation address, String phone) {
        this.username = username;
        this.appID = appID;
        this.dbID = DB_id;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.profile_picture = "";
        this.instanceToken = null;
    }


    /**
     * Adds a new book to the owners owned list
     * @param isbn The new books ISBN
     * @param title The title of the new book
     * @param author The author of the new book
     * @param year The year the new book was released
     * @param photos The photos of the book in String form
     */
    public void ownerNewBook(Long isbn, String title, String author, int year, ArrayList<String> photos) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRef = db.getReference().child("max_book_id");


        Log.d(TAG, "onClick: Title " + title);
        Log.d(TAG, "onClick: Author " + author);
        Log.d(TAG, "onClick: ISBN " + isbn );
        Log.d(TAG, "onClick: Year " + year);


        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer val = snapshot.getValue(Integer.class);
                userRef.document(auth.getUid()).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        User current = Objects.requireNonNull(task.getResult()).toObject(User.class);
                        Log.d(TAG, "user dbID " + current.getDbID());
                        Log.d(TAG, "Number of Photos " + photos.size());
                        Book book = new Book(title, isbn, author, val, Book.STATUS_AVAILABLE, year, auth.getUid(), current.getUsername(), photos);
                        bookRef.document(Integer.toString(val)).set(book);
                    }

                    else {
                        Log.d(TAG, "ownerNewBook - Error for users");
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
     * Accept a request for a book
     * @param t_id The transaction ID of the transaction to accept
     */
    public void ownerAcceptRequest(int t_id, UserLocation location, int bookID) {

        Log.d(TAG, "ownerAcceptRequest: t_id " + t_id);
        transRef.whereEqualTo("id", t_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // t_id is unique

                // update the transaction
                transRef.document(Integer.toString(t_id)).update("status", Transaction.STATUS_ACCEPTED);


                // update the user book
                userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(transaction.getBookID())).update("status", Book.STATUS_ACCEPTED);

                // update the global book
                bookRef.document(Integer.toString(transaction.getBookID())).update("status", Book.STATUS_ACCEPTED);
                bookRef.document(Integer.toString(transaction.getBookID())).update("borrower_username", transaction.getBorrower_username());


                // delete any other requests for the same book
                transRef.whereEqualTo("bookID", transaction.getBookID()).whereEqualTo("status",Book.STATUS_REQUESTED).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        List <Transaction> list = task1.getResult().toObjects(Transaction.class);
                        for (int i = 0 ; i < list.size() ; i++) {
                            transRef.document(Integer.toString(list.get(i).getID())).delete();
                            userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(transaction.getBookID())).delete();
                        }


                        bookRef.document(Integer.toString(transaction.getBookID())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                                if (task1.isSuccessful()) {
                                    Book book = task1.getResult().toObject(Book.class);

                                    User current = new User();
                                    current.ownerSetPickupLocation(location, bookID);

                                    // send a notification
                                    Notification notification = new Notification(transaction.getOwner_username(), transaction.getBorrower_username(), book.getTitle(), Notification.OWNER_ACCEPT_REQUEST );
                                    notification.prepareMessage();
                                    notification.sendNotification();
                                }
                            }
                        });
                    }

                    else {
                        Log.d(TAG, "ownerAcceptRequest - Error querying for other transactions!");
                    }
                });

            }

            else {
                Log.d(TAG, "ownerAcceptRequest - Error querying for specified transaction!");
            }

        });
    }






    /**
     * Let owner confirm pickup on a book
     * @param bookID The bookID of the book picked up
     */
    public void ownerConfirmPickup(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_ACCEPTED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // should be unique, and cannot be null

                // borrower has scanned
                if (transaction.getBorrowerFlag() == 1) {

                    // change the status of the transaction to borrowed
                    transRef.document(Integer.toString(transaction.getID())).update("status", Transaction.STATUS_BORROWED);


                    // change the status of the book to borrowed
                    bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_BORROWED);
                    userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(transaction.getBookID())).update("status", Transaction.STATUS_BORROWED);

                }

                // change the value of the ownerFlag
                transRef.document(Integer.toString(transaction.getID())).update("ownerFlag", 1);


            }

            else {
                Log.d(TAG, "OwnerPickup - Error Querying ");
            }
        });
    }



    /**
     * Deny the request for a book and notify the borrower that the request was declined
     * @param t_id The request transaction to decline
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ownerDenyRequest(int t_id) {
        transRef.whereEqualTo("id", t_id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique and not null
                int bookID = transaction.getBookID();

                // delete the transaction
                transRef.document(Integer.toString(t_id)).delete();

                // delete the requested book for user
                userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(bookID)).delete();

                // TODO: Implement Notification

                // check if that was the last request for the book
                transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_REQUESTED).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        // this was the last request
                        if (task1.getResult().toObjects(Transaction.class).size() == 0) {
                            bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_AVAILABLE);
                        }
                    }
                    else {
                        Log.d(TAG, "ownerDenyRequest - Error Finding rest of transactions");
                    }
                });
            }
            else {
                Log.d(TAG, "ownerDenyRequest - Error finding specific transaction");
            }
        });
    }






    /**
     * Let owner signOff on a book, marking the book available
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerSignOff(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_BORROWED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique and non null

                // borrower has scanned
                if (transaction.getBorrowerFlag() == 2) {

                    // delete the transaction
                    transRef.document(Integer.toString(transaction.getID())).delete();


                    // change the status of the book to borrowed
                    bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_AVAILABLE);
                    bookRef.document(Integer.toString(bookID)).update("pickup_location", null);
                    bookRef.document(Integer.toString(bookID)).update("borrower_username", null);


                    // delete the request for the book
                    userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(transaction.getBookID())).delete();

                }
                else {
                    // change the value of the ownerFlag
                    transRef.document(Integer.toString(transaction.getID())).update("ownerFlag", 2);
                }



            }

            else {
                Log.d(TAG, "OwnerSignOff - Error Querying for user transaction");
            }
        });
    }


    /**
     * Allow an owner to edit an existing book
     * @param title The updated title
     * @param author The updated author
     * @param ISBN  The updated ISBN
     * @param bookID The ID of the book to update
     * @param year The updated year
     */
    void ownerEditBook(String title, String author, long ISBN, int bookID, int year, ArrayList<String> photos){

        Log.d(TAG, "onClick: Title " + title);
        Log.d(TAG, "onClick: Author " + author);
        Log.d(TAG, "onClick: ISBN " + ISBN );
        Log.d(TAG, "onClick: Year " + year);
        Log.d(TAG, "onClick: bookID " + bookID);

        // update global book
        bookRef.document(Integer.toString(bookID)).update("title", title, "author", author, "isbn", ISBN, "year", year, "photos", photos);
    }




    /**
     * Allows an owner to cancel a pickup
     * @param bookID The ID of the book being picked up
     */
    void ownerCancelPickup(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_ACCEPTED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique and not null

                // delete the global transaction
                transRef.document(Integer.toString(transaction.getID())).delete();

                // update the global book reference
                bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_AVAILABLE);
                bookRef.document(Integer.toString(bookID)).update("pickup_location", null);
                bookRef.document(Integer.toString(bookID)).update("borrower_username", null);


                // delete the borrower book reference
                userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(bookID)).delete();
            }

            else {
                Log.d(TAG, "ownerCancelPickup - Error finding specific transaction");
            }
        });
    }



    /**
     * Deletes a book from an Owners collection
     * Additionally it removes any Transactions associated with that book
     * @param bookID The ID of the book to delete
     */
    public void ownerDeleteBook(int bookID) {
        transRef.whereEqualTo("bookID", bookID).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                List<Transaction> transactions = task.getResult().toObjects(Transaction.class);

                // delete the book
                bookRef.document(Integer.toString(bookID)).delete();
                WriteBatch batch = DB.batch();

                for (int i = 0 ; i < transactions.size() ; i++) {

                    // remove this book from requested for each requester
                    batch.delete(userRef.document(transactions.get(i).getBorrower_dbID()).collection("requested_books").document(Integer.toString(bookID)));


                    // delete each transaction associated with this book
                    batch.delete(transRef.document(Integer.toString(transactions.get(i).getID())));
                }

                batch.commit();
            }

            else {
                Log.d(TAG, "ownerDeleteBook - Error getting transaction list");
            }
        });

    }

    /**
     * Adds a location to the owners pickup location list
     * @param location The new location being added
     */
    public void ownerAddLocation(UserLocation location) {
        userRef.document(auth.getUid()).collection("pickup_locations").document(location.getTitle().replace(' ', '_')).set(location);
    }


    /**
     * Edits the owners profile with new information
     * @param username The new username for the user
     * @param coverPhoto The new coverPhoto for the user
     * @param phone The new phone number for the user
     */
    public void ownerEditProfile(String username, String coverPhoto, String phone) {



        userRef.document(auth.getUid()).update("profile_picture", coverPhoto);



        if (!phone.equals("")) {
            userRef.document(auth.getUid()).update("phone", phone);
        }

        if (!username.equals("")) {
            // check if the username is already taken
            FirebaseDatabase RTDB = FirebaseDatabase.getInstance();
            RTDB.getReference("username_list").child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        // username exists, cannot get that username
                        Log.d(TAG, "ownerChangeUsername - Username already taken");
                    }
                    else {
                        userRef.document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    User current = task.getResult().toObject(User.class);

                                    // update the transactions with the new user
                                    WriteBatch batch = DB.batch();
                                    transRef.whereEqualTo("borrower_username", current.username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                             if (task.isSuccessful()) {
                                                 List<Transaction> list = task.getResult().toObjects(Transaction.class);
                                                 for (int i = 0 ; i < list.size() ; i++) {
                                                     batch.update(transRef.document(Integer.toString(list.get(i).getID())), "borrower_username", username);
                                                     batch.update(bookRef.document(Integer.toString(list.get(i).getBookID())), "borrower_username", username);
                                                 }

                                                 transRef.whereEqualTo("owner_username", current.username).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                     @Override
                                                     public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                                                         if (task1.isSuccessful()) {
                                                             List<Transaction> list = task1.getResult().toObjects(Transaction.class);
                                                             for (int i = 0; i < list.size(); i++) {
                                                                 batch.update(transRef.document(Integer.toString(list.get(i).getID())), "owner_username", username);
                                                                 batch.update(bookRef.document(Integer.toString(list.get(i).getBookID())), "borrower_username", username);
                                                                 String string = list.get(i).getBorrower_dbID();
                                                                 batch.update(userRef.document(string).collection("requested_books").document(Integer.toString(list.get(i).getBookID())), "owner_username", username);
                                                             }


                                                             batch.commit();

                                                             // delete old username from list and add the new one
                                                             RTDB.getReference().child("username_list").child(current.username).removeValue();
                                                             RTDB.getReference().child("username_list").child(username).setValue(username);


                                                             // update username in firestore
                                                             userRef.document(auth.getUid()).update("username", username);



                                                         } else {
                                                             Log.d(TAG, "EditProfile 2 Error");
                                                         }
                                                     }

                                                 });
                                             }
                                             else {
                                                 Log.d(TAG, "EditProfile 1 Error");
                                             }
                                        }
                                    });
                                }
                                else {
                                    Log.d(TAG, "ownerEditProfile - Error finding current user");
                                }
                            }
                        });
                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }


    /**
     * Set the pickup location for a book
     * @param location The location to assign to the transaction
     * @param bookID The book ID of the book for the transaction
     */
    public void ownerSetPickupLocation(UserLocation location, int bookID) {
        Log.d(TAG, "ownerSetPickupLocation: bookID:" + bookID);
        // find the transaction associated with this book
        transRef.whereEqualTo("bookID", bookID).whereGreaterThanOrEqualTo("status", Transaction.STATUS_ACCEPTED).whereLessThanOrEqualTo("status", Transaction.STATUS_BORROWED ).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique


                    Log.d(TAG,"setPickupLocation - Location title: " + location.getTitle());
                    transRef.document(Integer.toString(transaction.getID())).update("location", location);


                    bookRef.document(Integer.toString(transaction.getBookID())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task1) {
                            if (task1.isSuccessful()) {
                                Book book = task1.getResult().toObject(Book.class);

                                Notification notification = new Notification(transaction.getOwner_username(), transaction.getBorrower_username(), book.getTitle(), Notification.CHANGE_LOCATION);
                                notification.prepareMessage();
                                notification.sendNotification();
                            }
                        }
                    });


                }

                else {
                    Log.d(TAG, "ownerSetPickupLocation - Error finding transaction");
                }
            }
        });
    }


    /**
     * Delete a location from the users pickup location list
     * @param location The location to remove
     */
    public void ownerDeleteLocation(UserLocation location) {
        userRef.document(auth.getUid()).collection("pickup_locations").whereEqualTo("title", location.getTitle()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    UserLocation spot = task.getResult().toObjects(UserLocation.class).get(0);
                    userRef.document(auth.getUid()).collection("pickup_locations").document(location.getTitle().replace(' ', '_')).delete();
                }

                else {
                    Log.d(TAG, "DeleteLocation - error finding pickup location");
                }
            }
        });
    }





    /**
     * Allows a borrower to cancel a book pickup
     * @param bookID The ID of the book being picked up
     */
    void borrowerCancelPickup(int bookID) {

        // get the transaction
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_ACCEPTED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0);

                // delete the global transaction
                transRef.document(Integer.toString(transaction.getID())).delete();

                // update the global book reference
                bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_AVAILABLE);
                bookRef.document(Integer.toString(bookID)).update("pickup_location", null);
                bookRef.document(Integer.toString(bookID)).update("borrower_username", null);



                // delete the borrower book reference
                userRef.document(transaction.getBorrower_dbID()).collection("requested_books").document(Integer.toString(bookID)).delete();
            }
            else {
                Log.d(TAG, "borrowerCancelPickup - Error Querying for specific transaction");
            }
        });
    }













    /**
     * Request a book from a book owner
     * Add the request to borrower transaction list
     * @param bookID is the id of the book you wish to borrow
     */
    public synchronized void borrowerRequestBook(int bookID) {
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRef = db.getReference().child("max_transaction_id");

        // debug info
        Log.d(TAG, "borrowerRequestBook: bookID " + bookID );

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // get the current Transaction ID
                    int val = snapshot.getValue(Integer.class);

                    // get the book
                    bookRef.whereEqualTo("id", bookID).get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Book book = task.getResult().toObjects(Book.class).get(0); // bookID is a unique key, cannot be null

                            // check if the borrower has requested this book already
                            transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_REQUESTED).whereEqualTo("borrower_dbID", auth.getUid()).get().addOnCompleteListener(task12 -> {
                                if (task12.isSuccessful()) {

                                    // borrower hasn't requested this book already
                                    if (task12.getResult().toObjects(Transaction.class).size() == 0) {


                                        // get the user information
                                        userRef.document(auth.getUid()).get().addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                User borrower = task1.getResult().toObject(User.class);

                                                // can't borrow your own book
                                                if (book.getOwner_dbID() == auth.getUid()) {
                                                    Log.d(TAG, "onDataChange: Cant request your own book!");
                                                    return;
                                                }

                                                // create a new transaction
                                                Transaction request = new Transaction(val, bookID, borrower.getUsername(), book.getOwner_username(), auth.getUid(), book.getOwner_dbID());

                                                // add the transaction to the global trans file
                                                transRef.document(Integer.toString(val)).set(request);

                                                // add the book to the borrowers requested list
                                                book.setStatus(Book.STATUS_REQUESTED);
                                                userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(bookID)).set(book);
                                                bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_REQUESTED);


                                                // notify the user
                                                Notification notification = new Notification(request.getBorrower_username(), request.getOwner_username(), book.getTitle(), Notification.BORROW_REQUEST_BOOK);
                                                notification.prepareMessage();
                                                notification.sendNotification();


                                            }

                                            else {
                                                Log.d(TAG, "borrowerRequestBook - Error finding borrower info");
                                            }
                                        });
                                    }

                                    // borrower has requested, don't let them request again
                                    else {
                                        Log.d(TAG, "Error: Borrower has already requested book: " + bookID);
                                    }
                                }

                                // error querying
                                else {
                                    Log.d(TAG, "borrowerRequestBook - Couldn't check if borrower already requested");
                                }
                            });
                        }

                        else {
                            Log.d(TAG, "borrowerRequestBook - Couldn't query for specific book!");
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
     * Allows a borrower to return a book by scanning
     * @param bookID The ID of the book the borrower is returning
     */
    public void borrowerDropOffBook(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_BORROWED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique and non null

                if (transaction.getOwnerFlag() == 2) {


                    // update the book status in the DB
                    bookRef.document(Integer.toString(transaction.getBookID())).update("status", Book.STATUS_AVAILABLE);
                    bookRef.document(Integer.toString(bookID)).update("pickup_location", null);

                    userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(transaction.getBookID())).delete();

                    // delete the global transaction
                    transRef.document(Integer.toString(transaction.getID())).delete();

                }
                else {
                    // update the borrower flag
                    transRef.document(Integer.toString(transaction.getID())).update("borrowerFlag", 2);
                }
            }

            // error querying
            else {
                Log.d(TAG, "borrowerDropOffBook - Error querying for specific transaction!");
            }
        });
    }


    /**
     * Allows borrowers to pickup an accepted book
     * @param bookID The ID of the book the borrower is picking
     */
    public void borrowerPickupBook(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_ACCEPTED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // must be unique and non null

                if (transaction.getOwnerFlag() == 1) {


                    // update the book status in the DB
                    bookRef.document(Integer.toString(transaction.getBookID())).update("status", Book.STATUS_BORROWED);
                    userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(transaction.getBookID())).update("status", Book.STATUS_BORROWED);

                    // update the global transaction
                    transRef.document(Integer.toString(transaction.getID())).update("status", Book.STATUS_BORROWED);
                }

                // update the borrower flag
                transRef.document(Integer.toString(transaction.getID())).update("borrowerFlag", 1);
            }

            // error querying
            else {
                Log.d(TAG, "borrowerDropOffBook - Error querying for specific transaction!");
            }
        });
    }


    /**
     * Allows a borrower to cancel a request
     * @param bookID The book ID of the book they no longer want to request
     */
    public void borrowerCancelRequest(int bookID) {
        transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_REQUESTED).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Transaction transaction = task.getResult().toObjects(Transaction.class).get(0); // request is unique and non null

                // delete the transaction from the global transaction file
                transRef.document(Integer.toString(transaction.getID())).delete();

                // set pickup location to null
                bookRef.document(Integer.toString(bookID)).update("pickup_location", null);

                // remove the book from requested list
                userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(transaction.getBookID())).delete();

                // check if that was the last request for the book
                transRef.whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_REQUESTED).get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {

                        // this was the last request
                        if (task1.getResult().toObjects(Transaction.class).size() == 0) {
                            bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_AVAILABLE);
                        }
                    }
                    else {
                        Log.d(TAG, "ownerDenyRequest - Error Finding rest of transactions");
                    }
                });
            }

            else {
                Log.d(TAG, "borrowerCancelRequest - Error getting transaction");
            }
        });

    }


    /**
     * Delete all the notifications for the current user
     * Done recursively through a cloud function
     */
    public void userDeleteNotifications() {
            Map<String, Object> data = new HashMap<>();
            data.put("path", "/users/" + auth.getUid() + "/notifications");

            HttpsCallableReference deleteFn =
                    FirebaseFunctions.getInstance().getHttpsCallable("recursiveDelete");
            deleteFn.call(data)
                    .addOnSuccessListener(new OnSuccessListener<HttpsCallableResult>() {
                        @Override
                        public void onSuccess(HttpsCallableResult httpsCallableResult) {
                            Log.d(TAG, "userDeleteNotification - Success");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "userDeleteNotification - Failure");
                        }
                    });
    }

    /**
     * Send a password reset email to the current user
     */
    public void passwordReset() {
        userRef.document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User current = task.getResult().toObject(User.class);
                    auth.sendPasswordResetEmail(current.getEmail());
                    Log.d(TAG, "Sent email to: " + current.getEmail());
                }

                else {
                    Log.d(TAG, "Change password - Error finding user");
                }
            }
        });
    }


    /**
     * Change the users current address
     * @param location The new location for the user
     */
    public void userChangeAddress(UserLocation location) {
        userRef.document(auth.getUid()).update("address", location);
    }


    /**
     * Delete a user from our system
     * https://medium.com/firebase-tips-tricks/how-to-delete-users-from-firebase-the-right-way-b4c348b2f75f
     * @param context The current context of the application
     */
    public void userDelete(Context context) {
        WriteBatch ownerDelete = DB.batch();
        userRef.document(auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    User current = task.getResult().toObject(User.class);
                    FirebaseDatabase RTDB = FirebaseDatabase.getInstance();



                    // delete all owner transactions associated with this user
                    transRef.whereEqualTo("owner_dbID", auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Transaction> list = task.getResult().toObjects(Transaction.class);


                                // delete owner transactions
                                for (int i = 0 ; i < list.size() ; i++) {
                                    ownerDelete.delete(userRef.document(list.get(i).getBorrower_dbID()).collection("requested_books").document(Integer.toString(list.get(i).getBookID())));
                                    ownerDelete.delete(transRef.document(Integer.toString(list.get(i).getID())));
                                }


                                // delete the global books
                                bookRef.whereEqualTo("owner_dbID", auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            WriteBatch bookDelete = DB.batch();
                                            List<Book> bookList = task.getResult().toObjects(Book.class);
                                            for (int i = 0 ; i < bookList.size() ; i++) {
                                                ownerDelete.delete(bookRef.document(Integer.toString(bookList.get(i).getId())));
                                            }


                                            // delete the borrower transactions
                                            transRef.whereEqualTo("borrower_dbID", auth.getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        List<Transaction> requests = task.getResult().toObjects(Transaction.class);
                                                        for (int i = 0 ; i < requests.size() ; i++) {
                                                            Log.d(TAG, "Borrowed transaction: " + requests.get(i).getID() + "| status: " + requests.get(i).getStatus());

                                                            if ((requests.get(i).getStatus() != Transaction.STATUS_REQUESTED)) {
                                                                Log.d(TAG, "Borrower transaction: " + requests.get(i).getID());
                                                                ownerDelete.update(bookRef.document(Integer.toString(requests.get(i).getBookID())), "status", Book.STATUS_AVAILABLE);
                                                            }
                                                            ownerDelete.delete(transRef.document(Integer.toString(requests.get(i).getID())));
                                                        }


                                                        // commit all our batches
                                                        ownerDelete.commit();
                                                    }
                                                    else {
                                                        Log.d(TAG, "Error finding transactions associated with the user");
                                                    }
                                                }
                                            });


                                        }
                                        else {
                                            Log.d(TAG, "Delete user - Error finding users books");
                                        }
                                    }
                                });
                            }

                            else {
                                Log.d(TAG, "Error finding all the users transactions!");
                            }
                        }
                    });





                    // free up the current users username
                    // no listener needed, not depending on result
                    RTDB.getReference().child("username_list").child(current.getUsername()).removeValue();

                    // delete all the user info from firestore
                    userRef.document(auth.getUid()).delete();

                    // delete authentication info
                    FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                                // send user to the login screen
                                // clear all previous activities
                                Intent intent = new Intent(context, Login.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            else {
                                Log.d(TAG, "Delete User - Error deleting authentication info!");
                            }
                        }
                    });
                }

                else {
                    Log.d(TAG, "onComplete: Delete account- Error finding user");
                }
            }
        });
    }




    /**
     * Ge the current users username
     * @return String representation of the users username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the owners username
     * Do not use this, as this can allow users to have duplicate usernames
     * @param username The new username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the users appID
     * @return Integer representing the users app ID
     */
    public int getAppID() {
        return appID;
    }


    /**
     * Set the users appID
     * Do not use this, it is used by firestore during de-serialization
     * @param appID The new appID
     */
    public void setAppID(int appID) {
        this.appID = appID;
    }

    /**
     * Get the current users Database ID
     * @return String representation of the user database ID
     */
    public String getDbID() {
        return dbID;
    }

    /**
     * Set the users Database ID
     * Do not use this, it is used by firestore during de-serialization
     * @param dbID The new database ID for the user
     */
    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    /**
     * Get the current address for the user
     * @return Userlocation object, containing the Latitude, Longitude, and title of the address
     */
    public UserLocation getAddress() {
        return address;
    }

    /**
     * Set the new address for the user
     * @param address new UserLocation object specifying the users new address
     */
    public void setAddress(UserLocation address) {
        this.address = address;
    }


    /**
     * Get the current users phone number
     * @return String representation of the users current phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the users phone number
     * @param phone The new phone number for the current user
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Get the current users email
     * @return String representation of the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the users email
     * @param email The new email for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the profile picture for the user
     * @return String represention of the users profile picture
     */
    public String getProfile_picture() {
        return profile_picture;
    }

    /**
     * Set the users profile picture
     * @param profile_picture The new string representation of the profile picture
     */
    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    /**
     * Get the users current instance token
     * @return String Representation of the users current instance token
     */
    public String getInstanceToken() {
        return instanceToken;
    }

    /**
     * Set the users instance token
     * @param instanceToken The new instance token for the user
     */
    public void setInstanceToken(String instanceToken) {
        this.instanceToken = instanceToken;
    }
}
