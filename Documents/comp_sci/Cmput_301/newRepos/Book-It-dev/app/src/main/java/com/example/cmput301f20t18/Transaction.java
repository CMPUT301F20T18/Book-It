package com.example.cmput301f20t18;

/**
 * A Transaction object represents various
 * interactions that two people can have within the app
 * requesting books, declining requests, transferring of books
 * between users and returning the book to it's original owner
 */
public abstract class Transaction {
    private User bookOwner;
    private User bookBorrower;
    private Integer ID;
    private Integer bookID;
    private String status;


    private static Integer transactionID = 0;

    /**
     * This is used to create a new object of type transaction
     * @param bookOwner The user who owns the book
     * @param bookBorrower The user who is borrowing the book
     * @param bookID The id of the book which is being borrowed
     */
    public Transaction(User bookOwner, User bookBorrower, Integer bookID) {
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.ID = transactionID;
        this.status = "request";

        Transaction.transactionID++;
    }

    /**
     * This is the constructor used to change the status of
     * a transaction
     * @param bookOwner The user who owns the book
     * @param bookBorrower The user who is borrowing the book
     * @param bookID The id of the book being borrowed
     * @param ID The id of the transaction
     *           (assigned on creation)
     * @param status The current state of the book
     *               (request, exchange, borrow, declined)
     */
    //For use in changing the status of a transaction
    public Transaction(User bookOwner, User bookBorrower, Integer bookID, Integer ID, String status){
        this.bookOwner = bookOwner;
        this.bookBorrower = bookBorrower;
        this.bookID = bookID;
        this.ID = ID;
        this.status = status;
    }

    /**
     * This is used to change the status of a transaction
     * @param status The status that transaction should become
     * @return the type of transaction specified by status
     */
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

    /**
     * Used to get the status of a transaction
     * @return status of transaction
     */
    public String getStatus() {
        return status;
    }

    /**
     * Used to get the ID of a transaction
     * @return ID of transaction
     */
    public Integer getID() {
        return ID;
    }

    /**
     * Used to get the ID of the
     * book being borrowed
     * @return ID of Book
     */
    public Integer getBookID() {
        return bookID;
    }

    /**
     * Used to get the User who owns the book
     * @return User who owns the book
     */
    public User getBookOwner(){
        return bookOwner;
    }

    /**
     * Used to get the User who is borrowing
     * or would like to borrow the book
     * @return User who is borrowing the book
     */
    public User getBookBorrower(){
        return bookBorrower;
    }
}
