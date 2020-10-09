package com.example.cmput301f20t18;

//All transactions start as RequestTransactions which handle
//the initial interaction between a book owner and a book borrower
//Can be accepted or declined
//If accepted it becomes an ExchangeTransaction and if declined the transaction is deleted
public class RequestTransaction extends Transaction{
    //For creating a brand new Transaction
    public RequestTransaction(User bookOwner, User bookBorrower, Integer bookID) {
        super(bookOwner, bookBorrower, bookID);
    }
    //For changing status of any transaction back to a RequestTransaction
    public RequestTransaction(User bookOwner, User bookBorrower, Integer bookId, Integer ID, String status) {
        super(bookOwner, bookBorrower, bookId, ID, status);
    }

    public Transaction accept(){
       return this.changeStatus("exchange");
    }
}
