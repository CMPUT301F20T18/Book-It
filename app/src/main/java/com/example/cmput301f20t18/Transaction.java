package com.example.cmput301f20t18;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Map;

/**
 * A Transaction object represents various
 * interactions that two people can have within the app
 * requesting books, declining requests, transferring of books
 * between users and returning the book to it's original owner
 */
public abstract class Transaction {


    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_BORROWED = 3;

    public static final String TAG = "TRANS_DEBUG";



    private User bookOwner;
    private String bookBorrower;
    private Integer ID;
    private Integer bookID;
    private int status;

    //private static TransactionLibrary transactionLib = new TransactionLibrary();

    /**
     * This is used to create a new object of type transaction
     *
     * @param bookOwner    The user who owns the book
     * @param bookBorrower The user who is borrowing the book
     * @param bookID       The id of the book which is being borrowed
     */
    public Transaction(User bookOwner, String bookBorrower, Integer bookID) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TransactionOnCompleteListener listener = new TransactionOnCompleteListener();

        Task transactionMax = db.collection("MAX")
                .document("transaction")
                .get()
                .addOnCompleteListener(listener);
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.status = Book.STATUS_REQUESTED;
        this.ID = listener.returnData();

        //this.transactionLib.addTransaction(this);
    }

    /**
     * This is the constructor used to change the status of
     * a transaction
     *
     * @param bookOwner    The user who owns the book
     * @param bookBorrower The user who is borrowing the book
     * @param bookID       The id of the book being borrowed
     * @param ID           The id of the transaction
     *                     (assigned on creation)
     * @param status       The current state of the book
     *                     (request, exchange, borrow, declined)
     */
    //For use in changing the status of a transaction
    public Transaction(User bookOwner, String bookBorrower, Integer bookID, Integer ID, int status) {
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.ID = ID;
        this.status = status;
    }

    /**
     * This is used to change the status of a transaction
     *
     * @param status The status that transaction should become
     * @return the type of transaction specified by status
     */
    public Transaction changeStatus(String status) {
        if (status.equals("exchange")) {
            return new ExchangeTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        } else if (status.equals("borrow")) {
            return new BorrowTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        } else if (status.equals("declined")) {
            return new DeclinedTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        }
        return this;
    }

    /**
     * Used to get the status of a transaction
     *
     * @return status of transaction
     */
    public int getStatus() {
        return status;
    }

    /**
     * Used to get the ID of a transaction
     *
     * @return ID of transaction
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Used to get the ID of the
     * book being borrowed
     *
     * @return ID of Book
     */
    public Integer getBookID() {
        return bookID;
    }

    /**
     * Used to get the User who owns the book
     *
     * @return User who owns the book
     */
    public User getBookOwner() {
        return bookOwner;
    }

    /**
     * Used to get the User who is borrowing
     * or would like to borrow the book
     *
     * @return User who is borrowing the book
     */
    public String getBookBorrower() {
        return bookBorrower;
    }

    private class TransactionOnCompleteListener implements OnCompleteListener {
        Integer ID;


        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()) {
                DocumentSnapshot queryResult = (DocumentSnapshot) task.getResult();
                Map<String, Object> data = queryResult.getData();
                this.ID = (Integer) data.get("transaction");
                this.updateDB();
            } else {
                throw new RuntimeException("Database query failed");
            }
        }

        public void updateDB(){
            FirebaseFirestore.getInstance()
                    .collection("MAX")
                    .document("transaction")
                    .set(this.ID+1);
        }

        public Integer returnData() {
            return this.ID;
        }
    }
}