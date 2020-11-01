package com.example.cmput301f20t18;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class RequestTransactionTest {
    private RequestTransaction mockRequestTransaction(){
        return new RequestTransaction("Owner", "Borrower", 10);
    }

    @Test
    void testAcceptRequest(){
        RequestTransaction requestTransaction = mockRequestTransaction();
        String owner = requestTransaction.getBookOwner();
        String borrower = requestTransaction.getBookBorrower();
        Integer ID = requestTransaction.getID();
        Integer bookID = requestTransaction.getBookID();
        ExchangeTransaction exchangeTransaction = requestTransaction.accept();
        assertEquals(owner, exchangeTransaction.getBookOwner());
        assertEquals(borrower, exchangeTransaction.getBookBorrower());
        assertEquals(ID, exchangeTransaction.getID());
        assertEquals(bookID, exchangeTransaction.getBookID());
        assertEquals("exchange", exchangeTransaction.getStatus());
    }

    @Test
    void testDeclineRequest(){
        RequestTransaction requestTransaction = mockRequestTransaction();
        String owner = requestTransaction.getBookOwner();
        String borrower = requestTransaction.getBookBorrower();
        Integer ID = requestTransaction.getID();
        Integer bookID = requestTransaction.getBookID();
        DeclinedTransaction declinedTransaction = requestTransaction.decline();
        assertEquals(owner, declinedTransaction.getBookOwner());
        assertEquals(borrower, declinedTransaction.getBookBorrower());
        assertEquals(ID, declinedTransaction.getID());
        assertEquals(bookID, declinedTransaction.getBookID());
        assertEquals("declined", declinedTransaction.getStatus());
    }
}
