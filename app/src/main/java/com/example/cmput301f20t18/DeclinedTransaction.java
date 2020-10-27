package com.example.cmput301f20t18;

/**
 * A DeclinedTransaction represents a transaction
 * wherein the request to borrow the book was declined
 * by the user who owns the book
 */
public class DeclinedTransaction extends Transaction{
    /**
     * DeclinedTransaction Constructor called by RequestTransaction
     * @param bookOwner The user who owns the book
     * @param bookBorrower The user who requested the book
     * @param bookId The ID of the book that was requested
     * @param ID The ID of the transaction
     * @param status The state of the transaction (declined)
     */
    public DeclinedTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
    }
}
