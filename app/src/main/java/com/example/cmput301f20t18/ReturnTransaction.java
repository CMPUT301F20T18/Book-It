package com.example.cmput301f20t18;

public class ReturnTransaction extends Transaction {
    private Boolean BookOwnerScanned;
    private Boolean BookBorrowerScanned;


    public Boolean getBookOwnerScanned() {
        return BookOwnerScanned;
    }

    public void setBookOwnerScanned(Boolean bookOwnerScanned) {
        BookOwnerScanned = bookOwnerScanned;
    }

    public Boolean getBookBorrowerScanned() {
        return BookBorrowerScanned;
    }

    public void setBookBorrowerScanned(Boolean bookBorrowerScanned) {
        BookBorrowerScanned = bookBorrowerScanned;
    }
}
