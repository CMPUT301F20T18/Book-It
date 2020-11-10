package com.example.cmput301f20t18;

import android.os.Build;
import android.transition.Transition;
import android.util.Log;

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
                    RequestTransaction req = Objects.requireNonNull(task.getResult()).toObject(RequestTransaction.class);
                    assert req != null;
                    int bookID = req.getBookID();

                    // decline all other request with the same bookID
                    Query trans_query = userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Transaction.STATUS_REQUESTED);
                    trans_query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                List<RequestTransaction> req_list= Objects.requireNonNull(task.getResult()).toObjects(RequestTransaction.class);
                                Log.d(TAG, "onComplete: " + Integer.toString(req_list.size()));
                                Log.d(TAG, "onComplete: " + Integer.toString());
                                for (int i = 0 ; i < req_list.size() ; i++) {
                                    userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").document(Integer.toString(req_list.get(i).getID())).update("status", Transaction.STATUS_DECLINED);
                                }
                            }
                        }
                    });
                }

            }
        });
    }


    /**
     * Deny the request for a book and notify the borrower that the request was declined
     * @param t_id The request transaction to decline
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ownerDenyRequest(int t_id) {
        userRef.document(Objects.requireNonNull(auth.getUid())).collection("owner_transactions").document(Integer.toString(t_id)).update("status", Transaction.STATUS_ACCEPTED);
    }


    /**
     * Let owner signOff on a book, marking the book available
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerSignOff(int bookID) {
        Query book_query = bookRef.whereEqualTo("ID", bookID).whereEqualTo("status", Book.STATUS_BORROWED);
        Query borrower_trans_query = userRef.document(auth.getUid()).collection("borrower_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_BORROWED);

        Task<QuerySnapshot> book_task = book_query.get();
        Task<QuerySnapshot> trans_task = borrower_trans_query.get();

        Tasks.whenAllComplete(book_task, trans_task).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                List<Book> book_list = book_task.getResult().toObjects(Book.class);
                List<Transaction> trans_list = trans_task.getResult().toObjects(Transaction.class);
                Book found = book_list.get(0); // bookID unique, only returns 1 book
                Transaction accepted = trans_list.get(0); // only 1 request per book ID should be able to be accepted

                // update the book status in the DB
                bookRef.document(Integer.toString(found.getId())).update("status", Book.STATUS_AVAILABLE);

                // update the borrower transaction
                userRef.document(auth.getUid()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Transaction.STATUS_RETURNED);

                // update the owner transaction
                userRef.document(found.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Transaction.STATUS_RETURNED);


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
     * Change book status in DB
     * Change borrower transaction status
     * Change owner transaction status
     * @param bookID the ID of the book being picked up
     */
    public void borrowerPickupBook(int bookID) {

        Query book_query = bookRef.whereEqualTo("ID", bookID).whereEqualTo("status", Book.STATUS_ACCEPTED);
        Query borrower_trans_query = userRef.document(auth.getUid()).collection("borrower_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_ACCEPTED);

        Task<QuerySnapshot> book_task = book_query.get();
        Task<QuerySnapshot> trans_task = borrower_trans_query.get();

        Tasks.whenAllComplete(book_task, trans_task).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                List<Book> book_list = book_task.getResult().toObjects(Book.class);
                List<Transaction> trans_list = trans_task.getResult().toObjects(Transaction.class);
                Book found = book_list.get(0); // bookID unique, only returns 1 book
                Transaction accepted = trans_list.get(0); // only 1 request per book ID should be able to be accepted

                // update the book status in the DB
                bookRef.document(Integer.toString(found.getId())).update("status", Book.STATUS_BORROWED);

                // update the borrower transaction
                userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_BORROWED);

                // update the owner transaction
                userRef.document(found.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_BORROWED);
            }
        });


    }


    /**
     * Drop off the book to the owner, scanning it to mark it as returned
     * Change book status in DB
     * Change borrower transaction status
     * Change owner transaction status
     * @param bookID The ID of the book being dropped off
     */
    public void borrowerDropOffBook(int bookID) {
        Query book_query = bookRef.whereEqualTo("ID", bookID).whereEqualTo("status", Book.STATUS_BORROWED);
        Query borrower_trans_query = userRef.document(auth.getUid()).collection("borrower_transactions").whereEqualTo("bookID", bookID).whereEqualTo("status", Book.STATUS_BORROWED);

        Task<QuerySnapshot> book_task = book_query.get();
        Task<QuerySnapshot> trans_task = borrower_trans_query.get();

        Tasks.whenAllComplete(book_task, trans_task).addOnSuccessListener(new OnSuccessListener<List<Task<?>>>() {
            @Override
            public void onSuccess(List<Task<?>> tasks) {
                List<Book> book_list = book_task.getResult().toObjects(Book.class);
                List<Transaction> trans_list = trans_task.getResult().toObjects(Transaction.class);
                Book found = book_list.get(0); // bookID unique, only returns 1 book
                Transaction accepted = trans_list.get(0); // only 1 request per book ID should be able to be accepted

                // update the book status in the DB
                bookRef.document(Integer.toString(found.getId())).update("status", Book.STATUS_AVAILABLE);

                // update the borrower transaction
                userRef.document(auth.getUid()).collection("borrower_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_AVAILABLE);

                // update the owner transaction
                userRef.document(found.getOwner().getDbID()).collection("owner_transactions").document(Integer.toString(accepted.getID())).update("status", Book.STATUS_AVAILABLE);
            }
        });
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
