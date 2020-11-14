package com.example.cmput301f20t18;

import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.LogDescriptor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


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
                            User current = Objects.requireNonNull(task.getResult()).toObject(User.class);
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
     * @param t_id The request transaction ID
     */
    public void ownerAcceptRequest(int t_id) {
        DocumentReference trans = userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").document(Integer.toString(t_id));
        trans.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                // get the accepted transaction
                if (task.isSuccessful()) {
                    Transaction req = Objects.requireNonNull(task.getResult()).toObject(Transaction.class);
                    assert req != null;
                    int bookID = req.getBookID();
                    String book_borrower = req.getBookBorrower();
                    Log.d(TAG, "onComplete: BookID =" + Integer.toString(bookID));

                    // change the status of that transaction to accepted
                    userRef.document(auth.getUid()).collection("owner_transactions").document(Integer.toString(t_id)).update("status", Transaction.STATUS_ACCEPTED);


                    // change the status of the book to accepted
                    userRef.document(auth.getUid()).collection("books_owned").document(Integer.toString(bookID)).update("status", Transaction.STATUS_ACCEPTED);
                    bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_ACCEPTED);

                    // decline all other request with the same bookID
                    Query trans_query = userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_REQUESTED);
                    trans_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<Transaction> req_list= Objects.requireNonNull(task.getResult()).toObjects(Transaction.class);


                                // change the status of the borrower transaction
                                Query borrower = userRef.whereEqualTo("username", book_borrower);
                                borrower.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            List<User> borrower_list = task.getResult().toObjects(User.class);
                                            String borrower_dbID = borrower_list.get(0).getDbID();
                                            userRef.document(borrower_dbID).collection("borrower_transactions").document(Integer.toString(t_id)).update("status", Transaction.STATUS_ACCEPTED);
                                            userRef.document(borrower_dbID).collection("requested_books").document(Integer.toString(bookID)).update("status", Book.STATUS_ACCEPTED);
                                            transRef.document(Integer.toString(t_id)).update("status", Transaction.STATUS_ACCEPTED);

                                            // change the other requests to declines
                                            for (int i = 0 ; i < req_list.size() ; i++) {
                                                Log.d(TAG, "onComplete: TransactionID: " + req_list.get(i).getID());
                                                userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").document(Integer.toString(req_list.get(i).getID())).delete();
                                                userRef.document(borrower_dbID).collection("borrower_transactions").document(Integer.toString(req_list.get(i).getID())).delete();
                                            }

                                        }
                                    }
                                });
                            }
                        }
                    });
                }

            }
        });
    }




    /**
     * Let owner confirm pickup on a book, marking the book borrowed
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerConfirmPickup(int bookID) {
        DocumentReference book_query = bookRef.document(Integer.toString(bookID));
        Query borrower_trans_query = userRef.document(auth.getUid()).collection("owner_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_ACCEPTED);

        Task<DocumentSnapshot> book_task = book_query.get();
        Task<QuerySnapshot> trans_task = borrower_trans_query.get();

        Tasks.whenAllComplete(book_task, trans_task).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                Book book = book_task.getResult().toObject(Book.class);
                List<Transaction> trans_list = trans_task.getResult().toObjects(Transaction.class);
                Transaction accepted = trans_list.get(0); // only 1 request per book ID should be able to be accepted




                // update all the files
                userRef.whereEqualTo("username", accepted.getBookBorrower()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<User> borrow_list = task.getResult().toObjects(User.class);
                            User borrower = borrow_list.get(0);

                            Log.d(TAG, "ownerConfirmPickup - borrowerFlag: " + accepted.getBorrowerFlag());

                            if (accepted.getBorrowerFlag() == 1) {

                                // change the status of that transaction to borrowed
                                userRef.document(auth.getUid()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Transaction.STATUS_BORROWED);


                                // change the status of the book to borrowed
                                userRef.document(auth.getUid()).collection("books_owned").document(Integer.toString(bookID)).update("status", Transaction.STATUS_BORROWED);
                                bookRef.document(Integer.toString(bookID)).update("status", Book.STATUS_BORROWED);

                                userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 1);
                                userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 1);
                                transRef.document(Integer.toString(accepted.getID())).update("ownerFlag", 1);



                                userRef.document(borrower.getDbID()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("status", Transaction.STATUS_BORROWED);
                                userRef.document(borrower.getDbID()).collection("books_requested").document(Integer.toString(accepted.getBookID())).update("status", Transaction.STATUS_BORROWED);
                                transRef.document(Integer.toString(accepted.getID())).update("status", Transaction.STATUS_BORROWED);
                            }


                            else {
                                userRef.document(auth.getUid()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 1);
                                userRef.document(borrower.getDbID()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 1);
                                transRef.document(Integer.toString(accepted.getID())).update("ownerFlag", 1);
                            }

                        }

                    }
                });


            }
        });
    }



    /**
     * Deny the request for a book and notify the borrower that the request was declined
     * @param t_id The request transaction to decline
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ownerDenyRequest(int t_id) {

        // decline request for owner
        userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").document(Integer.toString(t_id)).update("status", Transaction.STATUS_DECLINED);


        // decline the request for the borrower
        Query transaction = transRef.whereEqualTo("id", t_id);
        transaction.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Transaction> req_list = task.getResult().toObjects(Transaction.class);
                    Transaction req = req_list.get(0);

                    int bookID = req.getBookID();
                    String borrower = req.getBookBorrower();
                    Query user = userRef.whereEqualTo("username", borrower);
                    user.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task1) {
                            if (task1.isSuccessful()) {
                                List<User> user_list = task.getResult().toObjects(User.class);
                                User borrow = user_list.get(0);

                                userRef.document(borrow.getDbID()).collection("borrow_transactions").document(Integer.toString(t_id)).update("status", Transaction.STATUS_DECLINED);
                                userRef.document(borrow.getDbID()).collection("books_requested").document(Integer.toString(bookID)).update("status", Transaction.STATUS_DECLINED);
                                transRef.document(Integer.toString(t_id)).update("status", Transaction.STATUS_DECLINED);
                            }
                        }
                    });
                }
            }
        });
    }




    /**
     * Let owner signOff on a book, marking the book available
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerSignOff(int bookID) {
        DocumentReference book_query = bookRef.document(Integer.toString(bookID));
        Query borrower_trans_query = userRef.document(auth.getUid()).collection("owner_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_BORROWED);

        Task<DocumentSnapshot> book_task = book_query.get();
        Task<QuerySnapshot> trans_task = borrower_trans_query.get();

        Tasks.whenAllComplete(book_task, trans_task).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                Book book = book_task.getResult().toObject(Book.class);
                List<Transaction> trans_list = trans_task.getResult().toObjects(Transaction.class);
                if (trans_list.size() > 0) {
                    Transaction accepted = trans_list.get(0); // only 1 request per book ID should be able to be accepted

                    Log.d(TAG, "bookID: " + Integer.toString(book.getId()));
                    Log.d(TAG, "transID: " + Integer.toString(accepted.getID()));


                    // update the borrower
                    userRef.whereEqualTo("username", accepted.getBookBorrower()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<User> borrow_list = task.getResult().toObjects(User.class);
                                User borrower = borrow_list.get(0);

                                // borrower has already signed off
                                if (accepted.getBorrowerFlag() == 2) {

                                    // update the book status in the DB
                                    bookRef.document(Integer.toString(book.getId())).update("status", Book.STATUS_AVAILABLE);
                                    userRef.document(auth.getUid()).collection("books_requested").document(Integer.toString(accepted.getBookID())).delete();
                                    userRef.document(accepted.getBookOwner().getDbID()).collection("books_owned").document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_AVAILABLE);



                                    // delete the borrower transaction
                                    userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).delete();


                                    // delete the owner transaction
                                    userRef.document(book.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).delete();


                                    // delete the global transaction
                                    transRef.document(Integer.toString(accepted.getID())).delete();

                                }

                                else {
                                    userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 2);
                                    userRef.document(book.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("ownerFlag", 2);
                                    transRef.document(Integer.toString(accepted.getID())).update("ownerFlag", 2);

                                }

                            }

                        }
                    });

                }

            }
        });
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

                            // determine if the borrower has already requested the book
                            userRef.document(requested.getOwner().getDbID()).collection("owner_transactions").whereEqualTo("bookID", bookID).whereEqualTo("bookBorrower", current.getUsername()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<Transaction> list = task.getResult().toObjects(Transaction.class);
                                        if (list.size() == 0) {
                                            Transaction new_request = new Transaction(requested.getOwner(), current.getUsername(), bookID, val, Book.STATUS_REQUESTED);

                                            // update the user transactions
                                            userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(new_request.getID())).set(new_request);
                                            userRef.document(requested.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(new_request.getID())).set(new_request);
                                            transRef.document(Integer.toString(val)).set(new_request);

                                            // update the user book references
                                            userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(new_request.getBookID())).set(requested);
                                            userRef.document(requested.getOwner().getDbID()).collection("books_owned").document(Integer.toString(requested.getId())).update("status", Book.STATUS_REQUESTED);
                                        }
                                        else {
                                            Log.d(TAG, "Borrower " + current.getUsername() + " has already requested book " + requested.getId());
                                        }
                                    }
                                }
                            });
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



    public void borrowerDropOffBook(int bookID) {


        Query trans = userRef.document(auth.getUid()).collection("borrower_transactions").whereEqualTo("bookID", bookID);
        Task<QuerySnapshot> trans_task = trans.get();
        trans_task.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Transaction accepted = task.getResult().toObjects(Transaction.class).get(0);

                    if (accepted.getOwnerFlag() == 2) {

                        // update the book status in the DB
                        bookRef.document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_AVAILABLE);
                        userRef.document(auth.getUid()).collection("books_requested").document(Integer.toString(accepted.getBookID())).delete();
                        userRef.document(accepted.getBookOwner().getDbID()).collection("books_owned").document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_AVAILABLE);



                        // update the borrower transaction
                        userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).delete();


                        // update the owner transaction
                        userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).delete();


                        // update the global transaction
                        transRef.document(Integer.toString(accepted.getID())).delete();
                    }

                    else {
                        // update the borrower flag
                        userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                        userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                        transRef.document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                    }
                }

            }
        });
            }


    public void borrowerPickupBook(int bookID) {


        Query trans = userRef.document(auth.getUid()).collection("borrower_transactions").whereEqualTo("bookID", bookID);
        Task<QuerySnapshot> book_list = trans.get();
        book_list.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<Transaction> accepted_list = task.getResult().toObjects(Transaction.class);
                    Transaction accepted = accepted_list.get(0);

                    if (accepted.getOwnerFlag() == 1) {


                        // update the book status in the DB
                        bookRef.document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_BORROWED);
                        userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_BORROWED);
                        userRef.document(accepted.getBookOwner().getDbID()).collection("books_owned").document(Integer.toString(accepted.getBookID())).update("status", Book.STATUS_BORROWED);



                        // update the borrower transaction
                        userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_BORROWED);


                        // update the owner transaction
                        userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_BORROWED);


                        // update the global transaction
                        transRef.document(Integer.toString(accepted.getID())).update("status", Book.STATUS_BORROWED);

                        // update the borrower flag
                        userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 1);
                        userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 1);
                        transRef.document(Integer.toString(accepted.getID())).update("borrowerFlag", 1);



                    }

                    else {
                        // update the borrower flag
                        userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                        userRef.document(accepted.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                        transRef.document(Integer.toString(accepted.getID())).update("borrowerFlag", 2);
                    }
                }
            }
        });
    }



    public void borrowerCancelRequest(int t_id) {
        // get the book Owner
        transRef.whereEqualTo("id", t_id).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "t_id: " + t_id);
                    List<Transaction> trans_list = task.getResult().toObjects(Transaction.class);
                    Transaction request = trans_list.get(0);

                    // delete the request for the borrower
                    userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(t_id)).delete();

                    // delete the global transaction
                    transRef.document(Integer.toString(t_id)).delete();

                    // delete the request for the owner
                    userRef.document(request.getBookOwner().getDbID()).collection("owner_transactions").document(Integer.toString(t_id)).delete();





                    // delete the borrower book reference
                    userRef.document(auth.getUid()).collection("requested_books").document(Integer.toString(request.getBookID())).delete();

                    // determine if this was the last request for the book
                    userRef.document(request.getBookOwner().getDbID()).collection("owner_transactions").whereEqualTo("bookID", request.getBookID()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                if (task.getResult().toObjects(Transaction.class).size() == 0) {
                                    // that was the last request for this book
                                    // change its status to available
                                    userRef.document(request.getBookOwner().getDbID()).collection("books_owned").document(Integer.toString(request.getBookID())).update("status", Book.STATUS_AVAILABLE);
                                }
                            }

                        }
                    });
                }
            }
        });
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
