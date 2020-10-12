package com.example.cmput301f20t18;

import com.google.android.gms.maps.model.LatLng;

//Facilitates the handing off of the book from one person to the other
//Providing a location where the hand off takes place and a check for whether
//both users have interacted and exchanged the book in the form of 2 booleans
public class ExchangeTransaction extends Transaction{
    private Boolean bookOwnerScanned;
    private Boolean bookBorrowerScanned;
    private LatLng location;

    public ExchangeTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
        this.bookOwnerScanned = false;
        this.bookBorrowerScanned = false;
    }

    public void scanned(User userWhoScanned){
        if (userWhoScanned == this.bookOwnerScanned){
            this.bookOwnerScanned = true;
        }
        else if (userWhoScanned == this.bookBorrowerScanned){
            this.bookBorrowerScanned = true;
        }
    }

    public Boolean getBookOwnerScanned() {
        return bookOwnerScanned;
    }

    public Boolean getBookBorrowerScanned() {
        return bookBorrowerScanned;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }
}
