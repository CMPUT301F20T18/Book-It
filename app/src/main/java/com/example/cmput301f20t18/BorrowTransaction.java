package com.example.cmput301f20t18;

//Transaction wherein the borrower is now in the possession of the book
public class BorrowTransaction extends Transaction{
    public BorrowTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
    }

    public Transaction finish(){
        return this.changeStatus("exchange");
    }
}
