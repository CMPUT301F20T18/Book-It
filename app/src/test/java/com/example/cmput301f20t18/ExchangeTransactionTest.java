package com.example.cmput301f20t18;

import com.google.protobuf.StringValueOrBuilder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

//NOT DONE
public class ExchangeTransactionTest {
    private ExchangeTransaction mockExchangeTransaction(){
        return new RequestTransaction("Owner", "Borrower", 10).accept();
    }

    @Test
    void testScanned(){
        ExchangeTransaction transaction = mockExchangeTransaction();
        String bookOwner = transaction.getBookOwner();
        String bookBorrower = transaction.getBookBorrower();

        assertFalse(transaction.getBookOwnerScanned());
        assertFalse(transaction.getBookBorrowerScanned());

        transaction.scanned(bookOwner);
        assertTrue(transaction.getBookOwnerScanned());
        assertFalse(transaction.getBookBorrowerScanned());

        transaction.scanned(bookBorrower);
        assertTrue(transaction.getBookOwnerScanned());
        assertTrue(transaction.getBookBorrowerScanned());
    }

    @Test
    void testHandOffReady(){
        ExchangeTransaction transaction = mockExchangeTransaction();
        String bookOwner = transaction.getBookOwner();
        String bookBorrower = transaction.getBookBorrower();

        assertFalse(transaction.handOffReady());

        transaction.scanned(bookOwner);
        assertFalse(transaction.handOffReady());

        transaction.scanned(bookBorrower);
        assertTrue(transaction.handOffReady());
    }

    @Test
    void testHandOff(){
        ExchangeTransaction exchangeTransaction = mockExchangeTransaction();
        String bookOwner = exchangeTransaction.getBookOwner();
        String bookBorrower = exchangeTransaction.getBookBorrower();
        Integer ID = exchangeTransaction.getID();
        Integer bookID = exchangeTransaction.getBookID();

        exchangeTransaction.scanned(bookOwner);
        exchangeTransaction.scanned(bookBorrower);

        BorrowTransaction borrowTransaction = exchangeTransaction.handOff();
        assertEquals(bookOwner, borrowTransaction.getBookOwner());
        assertEquals(bookBorrower, borrowTransaction.getBookBorrower());
        assertEquals(ID, borrowTransaction.getID());
        assertEquals(bookID, borrowTransaction.getBookID());
        assertEquals("borrow", borrowTransaction.getStatus());
    }
}
