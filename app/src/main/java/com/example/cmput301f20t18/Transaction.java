package com.example.cmput301f20t18;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

/**
 * A Transaction object represents various
 * interactions that two people can have within the app
 * requesting books, declining requests, transferring of books
 * between users and returning the book to it's original owner
 */
public class Transaction {
    private String bookOwner;
    private String bookBorrower;
    private Integer ID;
    private Integer bookID;
    private String status;


    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_BORROWED = 3;
    public static final int STATUS_RETURNED = 4;
    public static final int STATUS_DECLINED = 5;

    public static final String TAG = "TRANS_DEBUG";


    private User bookOwner;
    private String bookBorrower;
    private Integer ID;
    private Integer bookID;
    private int status;
    private int ownerFlag;
    private int borrowerFlag;
    private String cover_photo;

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
        this.ownerFlag = 0;
        this.borrowerFlag = 0;

    }

    /**
     * Empty constructor used for serialization within firestore
     */
    public Transaction() {

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

    public void setBookOwner(User bookOwner) {
        this.bookOwner = bookOwner;
    }

    public void setBookBorrower(String bookBorrower) {
        this.bookBorrower = bookBorrower;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getOwnerFlag() {
        return ownerFlag;
    }

    public void setOwnerFlag(int ownerFlag) {
        this.ownerFlag = ownerFlag;
    }

    public int getBorrowerFlag() {
        return borrowerFlag;
    }

    public void setBorrowerFlag(int borrowerFlag) {
        this.borrowerFlag = borrowerFlag;
    }


}
