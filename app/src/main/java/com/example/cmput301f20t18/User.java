package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


/**
 * User represents any user in our system
 * and contains both functionality for owners and borrowers, but not both have to be used
 */
public class User {
    private String username;
    private int appID;
    private String dbID;
    private String address;
    private String pickup;
    private String phone;
    private String email;

    private Library lib;

    private ArrayList<Integer> owner_book_id;
    private ArrayList<Transaction> owner_transactions;
    private ArrayList<Book> owner_books;

    private ArrayList<Integer> borrower_book_id;
    private ArrayList<Transaction> borrower_transactions;
    private ArrayList<Book> borrower_books;


    /**
     * empty constructor used for firestore
     */
    public User() {

    }


    /**
     * Default constructor for a user, used during registration
     *
     * @param username The username for the user
     * @param appID The assigned app ID
     * @param DB_id The users authentication token
     * @param email The users email address
     * @param address The users address
     */
    public User(String username, int appID, String DB_id, String email, String address) {
        this.username = username;
        this.appID = appID;
        this.dbID = DB_id;
        this.email = email;
        this.address = address;
        this.pickup = address;

        lib = null;

        owner_book_id = new ArrayList<Integer>();
        owner_books = new ArrayList<Book>();
        owner_transactions = new ArrayList<Transaction>();

        borrower_book_id = new ArrayList<Integer>();
        borrower_books = new ArrayList<Book>();
        borrower_transactions = new ArrayList<Transaction>();

    }


    /**
     * Returns books for users of a particular type
     * @param type The status of the book to be found
     * @return list of bookIDs matching the requested status
     */
    public ArrayList<Book> ownerGetBooks(int type) {
        owner_books = lib.getBooks(owner_book_id);
        ArrayList<Book> filtered = new ArrayList<Book>();
        for (Book b : owner_books) {
            if (b.getStatus() == type) {
                filtered.add(b);
            }
        }


        return filtered;
    }

    /**
     * Deletes the book with bookID from the owners collection
     * @param bookID The id of the book to delete
     */
    public void ownerDelBook(int bookID) {
        for (int i = 0; i < owner_books.size(); i++) {
            if (owner_books.get(i).getId() == bookID) {
                owner_books.remove(i);

                return;
            }
        }
    }


    /**
     * Adds a new book to the owners list of books
     * Adds the same book to the library DB
     *
     * @param isbn   ISBN of the book to be added
     * @param title  Title of the book to be added
     * @param author Author of the book to be added
     */
    public void ownerNewBook(int isbn, String title, String author) {

        FirebaseDatabase db = FirebaseDatabase.getInstance();
        DatabaseReference mRef = db.getReference().child("max_book_id");
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer val = snapshot.getValue(Integer.class);
                Book book = new Book(title, isbn, author, val, 0, User.this, 1984);
                lib.addBook(book);
                owner_book_id.add(val);
                mRef.setValue(val + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }


    /**
     * accept the request for a book and notify the user it has been accepted
     * remove all other request for books with the same book ID
     * @param request The request transaction to accept
     */
    public void ownerAcceptRequest(RequestTransaction request) {

        // accept the request and replace the Request transaction with
        // an Exchange transaction
        Transaction accepted = request.accept();
        if (accepted instanceof ExchangeTransaction) {
            ExchangeTransaction new_ex = (ExchangeTransaction) accepted;
            owner_transactions.remove(request);
            owner_transactions.add(new_ex);
        }


        // deny all other requests with the same bookID?
        int bookID = request.getBookID();
        for (int i = 0; i < owner_transactions.size(); i++) {

            // transaction deals with same book and is requested
            if (owner_transactions.get(i).getBookID() == bookID && owner_transactions.get(i).getStatus() == "requested") {
                owner_transactions.remove(i);

                // TODO
                // remove transactions from transaction DB with same bookID and requested status
                return;
            }
        }
    }


    /**
     * Deny the request for a book and notify the borrower that the request was declined
     *
     * @param t_id The request transaction to decline
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void ownerDenyRequest(int t_id) {
        for (int i = 0; i < owner_transactions.size(); i++) {
            if (owner_transactions.get(i).getID() == t_id) {
                owner_transactions.remove(i);
                // TODO: Implement notification for borrower
            }
        }
    }


    /**
     * Let owner signOff on a book, marking the book either as Borrowed or available
     * @param bookID The bookID of the book being signed off on
     */
    public void ownerSignOff(int bookID) {
        for (int i = 0; i < owner_transactions.size(); i++) {
            if (owner_transactions.get(i).getBookID() == bookID) {
                Transaction a_trans = owner_transactions.get(i);
                // scan the book as user??
                if (a_trans instanceof ExchangeTransaction) {
                    // TODO: Find way to include user
                    //((ExchangeTransaction) a_trans).scanned();
                }
            }
        }
    }



    /**
     * Request a book from a book owner
     * Add the request to borrower transaction list
     *
     * @param bookID is the id of the book you wish to borrow
     * @return returns a transaction object, containing the borrower request
     */
    public Transaction borrowerRequestBook(int bookID) {
        Book requested = lib.getBook(bookID);
        User owner = requested.getOwner();
        Transaction request = new RequestTransaction(requested.getOwner().getUsername(), User.this.getUsername(), bookID);
        borrower_transactions.add(request);
        return request;
    }


    /**
     * Pick-up the book, scanning it to mark it as borrowed
     *
     * @param bookID the ID of the book being picked up
     */
    public void borrowerPickupBook(int bookID) {
        for (int i = 0; i < borrower_transactions.size(); i++) {
            if (borrower_transactions.get(i).getBookID() == bookID) {
                Transaction a_trans = borrower_transactions.get(i);
                // scan the book as user??
                if (a_trans instanceof ExchangeTransaction) {
                    ((ExchangeTransaction) a_trans).scanned(User.this.getUsername());
                }
            }
        }
    }


    /**
     * Drop off the book to the owner, scanning it to mark it as returned
     * @param bookID The ID of the book being dropped off
     */
    public void borrowerDropOffBook(int bookID) {
        for (int i = 0; i < borrower_transactions.size(); i++) {
            if (borrower_transactions.get(i).getBookID() == bookID) {
                Transaction a_trans = borrower_transactions.get(i);
                // scan the book as user??
                if (a_trans instanceof BorrowTransaction) {
                    ((BorrowTransaction) a_trans).finish();
                }
            }
        }

    }

    /**
     * Let the borrower search for books where the description contains a term
     * TODO: IMPLEMENT
     * @param term The term to filter for
     * @return A list of integers representing book IDs of the books that matched
     */
    public ArrayList<Integer> bookSearch(String term) {
        return null;
    }



    /**
     * adds a transaction to a borrowers transaction list
     * @param transaction the transaction id
     */
    public void borrowerTransactionAdd(Transaction transaction) {
        this.borrower_transactions.add(transaction);
    }


    /**
     * deletes a transaction from a borrower transaction list
     * @param t_id The transaction id of the transaction to remove
     */
    public void transactionDelete(int t_id, char type) {
        ArrayList<Transaction> transactions = null;
        if (type == 'o') {
            transactions = this.owner_transactions;
        }
        else {
            transactions = borrower_transactions;
        }

        for (int i = 0; i < transactions.size() ; i++) {
            if (transactions.get(i).getID() == t_id) {
                transactions.remove(transactions.get(i));
            }
        }

    }



    /**
     * Materializes all of the users books from their bookID list
     * Used to speed up application and reduce queries to DB
     */
    public void initUserBooks() {
        this.borrower_books = lib.getBooks(borrower_book_id);
        this.owner_books = lib.getBooks(owner_book_id);
    }




    // setters and getters start here


    /**
     * Returns the users username
     * @return String version of the users username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the users username
     * @param username The new username for the user
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Return the users appID
     * @return integer representation of the users app ID
     */
    public int getAppID() {
        return appID;
    }

    /**
     * Set the users appID
     * Note: Do not use this, Firestore requires it for de-serialization
     * @param appID The new appID
     */
    public void setAppID(int appID) {
        this.appID = appID;
    }

    /**
     * Returns the Auth ID for the user
     * Also used as a document ID within Firestore
     * @return
     */
    public String getDbID() {
        return dbID;
    }

    /**
     * Set the DB ID
     * Note: DO NOT USE! Firestore requires this for de-serialization however
     * its use will result in the app breaking.
     * @param dbID
     */
    public void setDbID(String dbID) {
        this.dbID = dbID;
    }

    /**
     * Get the users address
     * @return String representation of the users address
     */
    public String getAddress() {
        return address;
    }


    /**
     * Sets the current users address
     * @param address The new address for the user
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Get the preferred pickup location for the user
     * @return String representation of the user preferred pickup location
     */
    public String getPickup() {
        return pickup;
    }


    /**
     * Sets the users pickup location
     * @param pickup
     */
    public void setPickup(String pickup) {
        this.pickup = pickup;
    }

    /**
     * Get the users phone number
     * @return String representation of the users phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the users phone number
     * @param phone The new phone number for the user
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }


    /**
     * Get the users current email
     * @return String representation of the users email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the users email
     * @param email The new email for the user
     */
    public void setEmail(String email) {
        this.email = email;
    }


    /**
     * Returns the current library used for the user
     * @return the Library object associated with the user
     */
    public Library getLib() {
        return lib;
    }

    /**
     * Give library access to the user
     * @param lib The library
     */
    public void setLib(Library lib) {
        this.lib = lib;
    }


    /**
     * Returns the list of the transactions associated with books they own
     * @return ArrayList of Transactions associated with books a user owns
     */
    public ArrayList<Transaction> getOwner_transactions() {
        return owner_transactions;
    }

    /**
     * Sets the users owner transactions
     * @param owner_transactions The new Arraylist of transactions for the user
     */
    public void setOwner_transactions(ArrayList<Transaction> owner_transactions) {
        this.owner_transactions = owner_transactions;
    }

    /**
     * Returns the IDs of the books the user owns
     * @return ArrayList of Integers, representing the bookID of a book they own
     */
    public ArrayList<Integer> getOwner_book_id() {
        return owner_book_id;
    }

    /**
     * Sets the list of the owners book ID
     * @param owner_book_id ArrayList of bookIDs for the books an owner uses
     */
    public void setOwner_book_id(ArrayList<Integer> owner_book_id) {
        this.owner_book_id = owner_book_id;
    }

    /**
     * returns the list of book the User owns
     * @return ArrayList of books that the user owns
     */
    public ArrayList<Book> getOwner_books() {
        return owner_books;
    }

    /**
     * Sets the list of user Books
     * @param owner_books the new list of books owned by the user
     */
    public void setOwner_books(ArrayList<Book> owner_books) {
        this.owner_books = owner_books;
    }

    /**
     * Get the list of books owned by the user
     * @return ArrayList of books owned by the user
     */
    public ArrayList<Book> borrowerGetBooks() {
        return this.borrower_books;
    }


    /**
     * Gets the list of transactions associated with books they borrow/request
     * @return ArrayList of Transactions for books the user borrows
     */
    public ArrayList<Transaction> getBorrower_transactions() {
        return borrower_transactions;
    }

    /**
     * Sets the Users borrower transactions
     * @param borrower_transactions The new list of transactions to set
     */
    public void setBorrower_transactions(ArrayList<Transaction> borrower_transactions) {
        this.borrower_transactions = borrower_transactions;
    }

    /**
     * Get the book IDs of all books the user has borrowed
     * @return ArrayList of Integers representing the bookIDs of books the user has borrowed
     */
    public ArrayList<Integer> getBorrower_book_id() {
        return borrower_book_id;
    }

    /**
     * Sets the list of book IDs for books borrowed by the user.
     * @param borrower_book_id ArrayList of book IDs to assign to the user
     */
    public void setBorrower_book_id(ArrayList<Integer> borrower_book_id) {
        this.borrower_book_id = borrower_book_id;
    }

    /**
     * Returns the Books borrowed by the user
     * @return ArrayList of Books borrowed by the user
     */
    public ArrayList<Book> getBorrower_books() {
        return borrower_books;
    }

    /**
     * Sets the users list of borrowed books
     * @param borrower_books The new list of Books the user has borrowed
     */
    public void setBorrower_books(ArrayList<Book> borrower_books) {
        this.borrower_books = borrower_books;
    }
}
