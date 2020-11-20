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
 * @author warwick
 * @author deinum
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


    // users info
    private String borrower_username;
    private String owner_username;
    private String borrower_dbID;
    private String owner_dbID;


    /**
     * Constructor for creating a transaction object
     * @param ID The ID # of a transaction
     * @param bookID The book ID of the book the transaction deals with
     * @param borrower_username The borrowers username
     * @param owner_username The book owners username
     * @param borrower_dbID The borrowers database ID
     * @param owner_dbID The book owners database ID
     */
    public Transaction(Integer ID, Integer bookID, String borrower_username, String owner_username, String borrower_dbID, String owner_dbID) {
        this.ID = ID;
        this.bookID = bookID;
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


    /**
     * get the ID number of the transaction
     * @return Integer representing the ID number of the transaction
     */
    public Integer getID() {
        return ID;
    }

    /**
     * set the transaction ID number
     * used by firestore during serialization
     * DO NOT USE THIS
     * @param ID The new ID of the transaction
     */
    public void setID(Integer ID) {
        this.ID = ID;
    }


    /**
     * Returns the value of the owner flag for the transaction
     * @return Integer value of the owners flag
     */
    public int getOwnerFlag() {
        return ownerFlag;
    }

    /**
     * Set the owner flag for a transaction
     * @param ownerFlag The value of the owner flag
     */
    public void setOwnerFlag(int ownerFlag) {
        this.ownerFlag = ownerFlag;
    }

    /**
     * Get the value of the borrower flag for the transaction
     * @return Integer value of the borrower flag
     */
    public int getBorrowerFlag() {
        return borrowerFlag;
    }

    /**
     * Set the borrower flag for the transaction
     * @param borrowerFlag The new value for the borrower flag
     */
    public void setBorrowerFlag(int borrowerFlag) {
        this.borrowerFlag = borrowerFlag;
    }

    /**
     * Get the current status of the transaction
     * @return Integer value of the current status
     */
    public int getStatus() {
        return status;
    }

    /**
     * Set the status for a transaction
     * @param status The new status for the transaction
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Get the book ID for a transaction
     * @return Integer representing the book ID of the transaction
     */
    public Integer getBookID() {
        return bookID;
    }

    /**
     * Set the bookID for a transaction
     * @param bookID The new bookID
     */
    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }

    /**
     * Get the borrowers username
     * @return String representing the borrowers username for a transaction
     */
    public String getBorrower_username() {
        return borrower_username;
    }

    /**
     * Set the borrowers username
     * used by firestore to serialize objects
     * DO NOT USE
     * @param borrower_username The new borrower username
     */
    public void setBorrower_username(String borrower_username) {
        this.borrower_username = borrower_username;
    }

    /**
     * Get the book owners username
     * @return String representing the book owners username
     */
    public String getOwner_username() {
        return owner_username;
    }

    /**
     * Set the owners username for a transaction
     * used by firstore during serialization
     * DO NOT USE
     * @param owner_username New username for the owner
     */
    public void setOwner_username(String owner_username) {
        this.owner_username = owner_username;
    }

    /**
     * Get the borrower database ID
     * @return String representing the database ID for the borrower
     */
    public String getBorrower_dbID() {
        return borrower_dbID;
    }

    /**
     * Set the borrower database ID for a transaction
     * used by firestore during serialization
     * DO NOT USE
     * @param borrower_dbID New database ID for the borrower
     */
    public void setBorrower_dbID(String borrower_dbID) {
        this.borrower_dbID = borrower_dbID;
    }

    /**
     * Get the owner database ID
     * @return String representing the owners database ID
     */
    public String getOwner_dbID() {
        return owner_dbID;
    }

    /**
     * Set the owners database ID for a transaction
     * required by firestore during object serialization
     * DO NOT USE
     * @param owner_dbID The new owner database ID for the transaction
     */
    public void setOwner_dbID(String owner_dbID) {
        this.owner_dbID = owner_dbID;
    }
}
