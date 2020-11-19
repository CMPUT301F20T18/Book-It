package com.example.cmput301f20t18;

import android.location.Address;

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
 *
 * Note:
 */
public class Transaction {
    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_BORROWED = 3;


    public static final int CONFIRMED_LOCATION = 4;



    public static final String TAG = "TRANS_DEBUG";


    // Transaction information
    private Integer ID;
    private int ownerFlag;
    private int borrowerFlag;
    private int status;


    // book info
    private Integer bookID;
    private String cover_photo;


    // users info
    private String borrower_username;
    private String owner_username;
    private String borrower_dbID;
    private String owner_dbID;


    public Transaction(Integer ID, Integer bookID, String cover_photo, String borrower_username, String owner_username, String borrower_dbID, String owner_dbID) {
        this.ID = ID;
        this.bookID = bookID;
        this.cover_photo = cover_photo;
        this.borrower_username = borrower_username;
        this.owner_username = owner_username;
        this.borrower_dbID = borrower_dbID;
        this.owner_dbID = owner_dbID;
        this.borrowerFlag = 0;
        this.ownerFlag = 0;
        this.status = Transaction.STATUS_REQUESTED;
    }

    /**
     * Empty constructor used for serialization within firestore
     */
    public Transaction() {

    }


    // getters and setters start here



    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    public String getCover_photo() {
        return cover_photo;
    }

    public void setCover_photo(String cover_photo) {
        this.cover_photo = cover_photo;
    }

    public String getBorrower_username() {
        return borrower_username;
    }

    public void setBorrower_username(String borrower_username) {
        this.borrower_username = borrower_username;
    }

    public String getOwner_username() {
        return owner_username;
    }

    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }

    public String getBorrower_dbID() {
        return borrower_dbID;
    }

    public void setBorrower_dbID(String borrower_dbID) {
        this.borrower_dbID = borrower_dbID;
    }

    public String getOwner_dbID() {
        return owner_dbID;
    }

    public void setOwner_dbID(String owner_dbID) {
        this.owner_dbID = owner_dbID;
    }
}
