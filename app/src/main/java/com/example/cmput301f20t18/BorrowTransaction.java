package com.example.cmput301f20t18;

/**
 * A BorrowTransaction object is a type of transaction
 * which handles the time at which the bookBorrower is
 * in possession of the book
 */
public class BorrowTransaction extends Transaction{
    /**
     * BorrowTransaction constructor called by ExchangeTransaction
     *
     * @param bookOwner The owner of the book
     * @param bookBorrower The borrower of the book
     * @param bookId The id of the book being borrowed
     * @param ID The id of the transaction
     * @param status The state of the transaction (borrow)
     */
    public BorrowTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
    }

    /**
     * Called when the borrower is ready to return the book
     * to the bookOwner
     * @return ExchangeTransaction with same ID, bookOwner, bookBorrower and bookID
     */
    public ExchangeTransaction finish(){
        return (ExchangeTransaction) this.changeStatus("exchange");
    }
}
