package com.example.cmput301f20t18;

//Transaction class acts as a general blueprint for
//RequestTransaction, BorrowTransaction, and ReturnTransaction.

//Transactions have two Users involved the owner and the borrower.

//Transactions contain two ID's which uniquely identify the transaction
//and the book that is part of the transaction as well as a static
//transactionID variable which is how unique ID's are given to any given transaction.

//Transactions also have a status which detail what stage the Transaction is currently in.
public abstract class Transaction {
    private User bookOwner;
    private User bookBorrower;
    private Integer ID;
    private Integer bookID;
    private String status;


    private static Integer transactionID = 0;

    //For use in creating a brand new transaction
    public Transaction(User bookOwner, User bookBorrower, Integer bookID) {
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.ID = transactionID;
        this.status = "request";

        Transaction.transactionID++;
    }
    //For use in changing the status of a transaction
    public Transaction(User bookOwner, User bookBorrower, Integer bookID, Integer ID, String status){
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.ID = ID;
        this.status = status;
    }

    //Used to update status of a transaction
    public Transaction changeStatus(String status) {
        if (status.equals("request")){
            return new RequestTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        }
        else if (status.equals("exchange")){
            return new ExchangeTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        }
        else if (status.equals("borrow")){
            return new BorrowTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        }
        else if (status.equals("declined")){
            return new DeclinedTransaction(this.bookOwner, this.bookBorrower, this.bookID, this.ID, status);
        }
        return this;
    }

    public String getStatus() {
        return status;
    }

    public Integer getID() {
        return ID;
    }

    public Integer getBookID() {
        return bookID;
    }

    public User getBookOwner(){
        return bookOwner;
    }

    public User getBookBorrower(){
        return bookBorrower;
    }
}
