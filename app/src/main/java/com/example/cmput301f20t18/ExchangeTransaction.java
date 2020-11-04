package com.example.cmput301f20t18;

import com.google.android.gms.maps.model.LatLng;

/**
 * A ExchangeTransaction represents a transaction
 * which allows users to hand the book between one another
 * and is the result of the book owner accepting a request
 * or the book borrower being done with the book and wanting
 * to return it
 */
public class ExchangeTransaction extends Transaction{
    private Boolean bookOwnerScanned;
    private Boolean bookBorrowerScanned;
    private LatLng location;

    /**
     * ExchangeTransaction Constructor
     * @param bookOwner The user who owns the book
     * @param bookBorrower The user who is borrowing the book
     * @param bookId The ID of the book being borrowed
     * @param ID The ID of the transaction
     * @param status The state the transaction is in (exchange)
     */
    public ExchangeTransaction(String bookOwner, String bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
        this.bookOwnerScanned = false;
        this.bookBorrowerScanned = false;
    }

    /**
     * Used to set the state of bookOwnerScanned and bookBorrowerScanned
     * to be called when a user scans the book during a hand off
     * @param userWhoScanned The user who scanned the book
     */
    public void scanned(String userWhoScanned){
        if (userWhoScanned.equals(this.getBookOwner())){
            this.bookOwnerScanned = true;
        }
        else if (userWhoScanned.equals(this.getBookBorrower())){
            this.bookBorrowerScanned = true;
        }
    }

    /**
     * Used to check if a hand off has taken place
     * @return returns a boolean representing whether both
     *         users have acknowledged the book changing hands
     */
    public Boolean handOffReady(){
        return this.bookOwnerScanned && this.bookBorrowerScanned;
    }

    /**
     * Called after the owner of the book
     * has given the book to the borrower
     * authorised by both users scanning
     * the book
     * @return BorrowTransaction with same ID, bookOwner, bookBorrower and bookID
     */
    public BorrowTransaction handOff(){
        return (BorrowTransaction) this.changeStatus("borrow");
    }

    /**
     * Gets the location where the hand off is taking
     * place
     * @return a LatLng object representing the location of the
     *         hand off
     */
    public LatLng getLocation() {
        return location;
    }

    /**
     * To be called from the SelectLocationActivity to set
     * the location to the location chosen on the map
     * @param location the location chosen on the map
     *                 where the hand off will take place
     */
    public void setLocation(LatLng location) {
        this.location = location;
    }

    public Boolean getBookOwnerScanned() {
        return bookOwnerScanned;
    }

    public Boolean getBookBorrowerScanned() {
        return bookBorrowerScanned;
    }
}
