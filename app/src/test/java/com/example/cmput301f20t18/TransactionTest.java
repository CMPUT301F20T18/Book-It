package com.example.cmput301f20t18;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransactionTest {
    private int defaultID = 0;
    private int defaultBookID = 1;
    private String defaultBorrowerUsername = "BORROWER_USERNAME";
    private String defaultOwnerUsername = "OWNER_USERNAME";
    private String defaultBorrowerDBID = "BORROWER_DB_ID";
    private String defaultOwnerDBID = "OWNER_DB_ID";

    private Transaction mockTransaction(){
        return new Transaction(defaultID, defaultBookID, defaultBorrowerUsername,
                defaultOwnerUsername, defaultBorrowerDBID, defaultOwnerDBID);
    }

    @Test
    void testGetID(){
        Transaction transaction = mockTransaction();
        assertEquals((int)defaultID, (int)transaction.getID());
    }
    @Test
    void testSetID(){
        Transaction transaction = mockTransaction();
        int ID = 10;
        transaction.setID(ID);
        assertEquals((int)ID, (int)transaction.getID());
    }
    @Test
    void testGetOwnerFlag(){
        Transaction transaction = mockTransaction();
        assertEquals(Transaction.NO_SCAN, transaction.getOwnerFlag());
    }
    @Test
    void testSetOwnerFlag(){
        Transaction transaction = mockTransaction();
        int ownerFlag = Transaction.FIRST_SCAN;
        transaction.setOwnerFlag(ownerFlag);
        assertEquals(ownerFlag, transaction.getOwnerFlag());
    }
    @Test
    void testGetBorrowerFlag(){
        Transaction transaction = mockTransaction();
        assertEquals(Transaction.NO_SCAN, transaction.getBorrowerFlag());
    }
    @Test
    void testSetBorrowerFlag(){
        Transaction transaction = mockTransaction();
        int ownerFlag = Transaction.SECOND_SCAN;
        transaction.setOwnerFlag(ownerFlag);
        assertEquals(ownerFlag, transaction.getOwnerFlag());
    }
    @Test
    void testGetStatus(){
        Transaction transaction = mockTransaction();
        assertEquals(Transaction.STATUS_REQUESTED, transaction.getStatus());
    }
    @Test
    void testSetStatus(){
        Transaction transaction = mockTransaction();
        int status = Transaction.STATUS_BORROWED;
        transaction.setStatus(status);
        assertEquals(status, transaction.getStatus());
    }
    @Test
    void testGetBookID(){
        Transaction transaction = mockTransaction();
        assertEquals((int)defaultBookID, (int)transaction.getBookID());
    }
    @Test
    void testSetBookID(){
        Transaction transaction = mockTransaction();
        int bookID = -1;
        transaction.setBookID(bookID);
        assertEquals((int)bookID, (int)transaction.getBookID());
    }
    @Test
    void testGetBorrower_Username(){
        Transaction transaction = mockTransaction();
        assertTrue(transaction.getBorrower_username().equals(defaultBorrowerUsername));
    }
    @Test
    void testSetBorrower_Username(){
        Transaction transaction = mockTransaction();
        String borrowerUsername = "HELLO_WORLD";
        transaction.setBorrower_username(borrowerUsername);
        assertTrue(transaction.getBorrower_username().equals(borrowerUsername));
    }
    @Test
    void testGetOwner_Username(){
        Transaction transaction = mockTransaction();
        assertTrue(transaction.getOwner_username().equals(defaultOwnerUsername));
    }
    @Test
    void testSetOwner_Username(){
        Transaction transaction = mockTransaction();
        String ownerUsername = "HELLO_WORLD";
        transaction.setOwner_username(ownerUsername);
        assertTrue(transaction.getOwner_username().equals(ownerUsername));
    }
    @Test
    void testGetBorrower_DBID(){
        Transaction transaction = mockTransaction();
        assertTrue(transaction.getBorrower_dbID().equals(defaultBorrowerDBID));
    }
    @Test
    void testSetBorrower_DBID(){
        Transaction transaction = mockTransaction();
        String borrowerDBID = "HELLO_WORLD";
        transaction.setBorrower_dbID(borrowerDBID);
        assertTrue(transaction.getBorrower_dbID().equals(borrowerDBID));
    }
    @Test
    void testGetOwner_DBID(){
        Transaction transaction = mockTransaction();
        assertTrue(transaction.getOwner_dbID().equals(defaultOwnerDBID));
    }
    @Test
    void testSetOwner_DBID(){
        Transaction transaction = mockTransaction();
        String ownerDBID = "HELLO_WORLD";
        transaction.setOwner_dbID(ownerDBID);
        assertTrue(transaction.getOwner_dbID().equals(ownerDBID));
    }
    @Test
    void testGetLocation(){
        Transaction transaction = mockTransaction();
        assertEquals(null, transaction.getLocation());
    }
    @Test
    void testSetLocation(){
        Transaction transaction = mockTransaction();
        String title = "DEFAULT";
        double lat = 1.0;
        double lng = 0.0;
        UserLocation location = new UserLocation(title, lat, lng);
        transaction.setLocation(location);
        assertTrue(transaction.getLocation().getTitle().equals(title));
        assertEquals((double)lat, (double)transaction.getLocation().getLatitude(), 0.05);
        assertEquals((double)lng, (double)transaction.getLocation().getLongitude(), 0.05);
    }

}
