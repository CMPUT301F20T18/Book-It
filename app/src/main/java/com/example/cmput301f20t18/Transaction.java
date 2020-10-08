package com.example.cmput301f20t18;

public abstract class Transaction {
    private String status;
    private Integer transactionID;
    private Integer bookID;
    //Variables bookOwner and bookBorrower will be users.
    //Currently strings
    private String bookOwner;
    private String bookBorrower;
    //private User bookOwner;
    //private User bookBorrower;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Integer transactionID) {
        this.transactionID = transactionID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }
}
