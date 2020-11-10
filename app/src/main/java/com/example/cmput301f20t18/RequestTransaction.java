package com.example.cmput301f20t18;

/**
 * A RequestTransaction represents a transaction
 * wherein the borrower has requested to borrow
 * a book from the book owner
 * This is where a Transaction starts it's life cycle
 */
public class RequestTransaction extends Transaction{
    /**
     * This is the constructor for a RequestTransaction
     * which can be called by developers when a transaction
     * is created
     * @param bookOwner The User who owns the book
     * @param bookBorrower The User who has requested the book
     * @param bookID The ID of the book being requested
     * @param status
     */
    public RequestTransaction(User bookOwner, String bookBorrower, Integer bookID, Integer ID, int status) {
        super(bookOwner, bookBorrower, bookID, ID, status);
    }

    /**
     * Called when the book owner accepts the request
     * to borrow their book
     * @return ExchangeTransaction with same ID, bookOwner, bookBorrower and bookID
     */
    public ExchangeTransaction accept(){
       return (ExchangeTransaction) this.changeStatus("exchange");
    }

    /**
     * Called when the book owner declines the request
     * to borrow their book
     * @return DeclinedTransaction with same ID, bookOwner, bookBorrower and bookID
     */
    public DeclinedTransaction decline(){
        return (DeclinedTransaction) this.changeStatus("declined");
    }
}
