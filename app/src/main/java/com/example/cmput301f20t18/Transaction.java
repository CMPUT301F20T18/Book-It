package com.example.cmput301f20t18;

public abstract class Transaction {
    private String status;
    private static Integer transactionID;
    private Integer ID;
    private Integer bookID;
    //Variables bookOwner and bookBorrower will be users.
    private String bookOwner;
    private String bookBorrower;

    public Transaction(String status, Integer bookID) {
        this.ID = transactionID;
        Transaction.transactionID++;
        this.status = status;
        this.bookID = bookID;
    }
    //private User bookOwner;
    //private User bookBorrower;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getID() {
        return transactionID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public void setBookID(Integer bookID) {
        this.bookID = bookID;
    }
}
