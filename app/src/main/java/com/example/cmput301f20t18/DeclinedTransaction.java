package com.example.cmput301f20t18;

public class DeclinedTransaction extends Transaction{
    public DeclinedTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
    }
}
