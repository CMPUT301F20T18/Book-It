package com.example.cmput301f20t18;

import java.util.Hashtable;

public class TransactionLibrary {
    private Hashtable<Integer, Transaction> transactionLibrary;
    private Hashtable<String, Transaction> statusLibrary;
    private Hashtable<String, Transaction> ownerLibrary;
    private Hashtable<String, Transaction> borrowerLibrary;

    public TransactionLibrary() {
        this.transactionLibrary = new Hashtable<Integer, Transaction>();
        this.statusLibrary = new Hashtable<String, Transaction>();
        this.ownerLibrary = new Hashtable<String, Transaction>();
        this.borrowerLibrary = new Hashtable<String, Transaction>();
    }

    /*
     *  Not done must implement DB adding to this
     */
    public void addTransaction(Transaction transaction){
        User bookOwner= transaction.getBookOwner();
        User bookBorrower = transaction.getBookBorrower();
        this.transactionLibrary.put(transaction.getID(), transaction);
        this.statusLibrary.put(transaction.getStatus(),transaction);
        this.ownerLibrary.put(bookOwner.getUsername(), transaction);
        this.borrowerLibrary.put(bookBorrower.getUsername(), transaction);
    }

    /*
     *
     */
    public Transaction getTransactionById(Integer ID){
        return this.transactionLibrary.get(ID);
    }
}
