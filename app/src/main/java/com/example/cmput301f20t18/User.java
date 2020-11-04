package com.example.cmput301f20t18;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


/**
 * User represents any user in our system
 * and contains both functionality for owners and borrowers, but not both have to be used
 */
public class User {
    private String username;
    private int appID;
    private String dbID;
    private String profilePicture;
    private String email;
    private String address;

    Borrower borrower;
    Owner owner;
    Contact contact;
    private Library lib;


    /**
     * empty constructor used for firebase deserializing
     */
    public User(){

    }


    /**
     * Constructor for initializing user
     * First when a used first registers
     * @param username Username the user picked
     * @param appID The users id within our app
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
        this.profilePicture =  "picture";
        borrower = new Borrower(new ArrayList<Transaction>(), new ArrayList<Integer>(), new ArrayList<Book>());
        owner = new Owner(new ArrayList<Transaction>(), new ArrayList<Integer>(), new ArrayList<Book>());
        contact = new Contact();
    }





    /**
     * Implements the functionality for owner behaviors
     */
    public class Owner {
        private ArrayList<Transaction> owner_transactions;
        private ArrayList<Integer> owner_book_id;
        private ArrayList<Book> owner_books;

        /**
         * empty constructor for de-serializing
         */
        private Owner(){

        }

        /**
         * Default constructor used for
         * @param transactions list of transactions for the Owner
         * @param book_id list of transaction for the owner
         * @param books list of books for the owner
         */
        private Owner(ArrayList<Transaction> transactions, ArrayList<Integer> book_id, ArrayList<Book> books) {
            this.owner_transactions = transactions;
            this.owner_book_id = book_id;
            this.owner_books = books;
        }





        /**
         *
         * @param type The status of the book to be found
         * @return list of bookIDs matching the requested status
         */
        public ArrayList<Book> getBooks(int type) {
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

        public void delBook(int bookID) {
            for (int i = 0 ; i < owner_books.size() ; i++) {
                if (owner_books.get(i).getId() == bookID) {
                    owner_books.remove(i);

                    return;
                }
            }
        }


        /**
         * Adds a new book to the owners list of books
         * Adds the same book to the library DB
         * @param isbn ISBN of the book to be added
         * @param title Title of the book to be added
         * @param author Author of the book to be added
         */
        public void newBook(int isbn, String title, String author) {

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


                    // add the book to the DB
                    FirebaseFirestore DB = FirebaseFirestore.getInstance();
                    CollectionReference book_ref = DB.collection("system").document("System").collection("books");
                    book_ref.document(Integer.toString(val)).set(book);
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
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void acceptRequest(RequestTransaction request) {

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
         * @param t_id The request transaction to decline
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void denyRequest(int t_id) {
            for (int i = 0 ; i < owner_transactions.size() ; i++) {
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
        public void signOff(int bookID) {
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
         * Materialize books for owners
         */
        public void init() {
            this.owner_books = lib.getBooks(this.owner_book_id);
        }
    }


    /**
     * Borrower contains the implementation for any borrowing activities
     */
    public class Borrower {
        private ArrayList<Transaction> borrower_transactions;
        private ArrayList<Integer> borrower_book_id;
        private ArrayList<Book> borrower_books;

        /**
         * Empty constructor for firebase
         */
        private Borrower() {

        }

        /**
         * constructor for initializing the borrower object
         * @param borrower_transactions list of the borrowers transactions
         * @param borrower_book_id list of the book IDs this User is borrowing
         * @param borrower_books list of the books this user is borrowing
         */
        public Borrower(ArrayList<Transaction> borrower_transactions, ArrayList<Integer> borrower_book_id, ArrayList<Book> borrower_books) {
            this.borrower_transactions = borrower_transactions;
            this.borrower_book_id = borrower_book_id;
            this.borrower_books = borrower_books;
        }

        /**
         * Request a book from a book owner
         * Add the request to borrower transaction list
         * @param bookID is the id of the book you wish to borrow
         * @return returns a transaction object, containing the borrower request
         */
        public Transaction requestBook(int bookID) {
            Book requested = lib.getBook(bookID);
            User owner = requested.getOwner();
            Transaction request = new RequestTransaction(requested.getOwner().getUsername(), User.this.getUsername(), bookID);
            borrower_transactions.add(request);
            return request;
        }


        /**
         * Pick-up the book, scanning it to mark it as borrowed
         * @param bookID the ID of the book being picked up
         */
        public void pickupBook(int bookID) {
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
        public void dropOffBook(int bookID) {
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
        public void transactionAdd(Transaction transaction) {

        }

        /**
         * deletes a transaction from a borrower transaction list
         * @param t_id The transaction id of the transaction to remove
         */
        public void transactionDelete(int t_id) {

        }


        /**
         * Returns a list of borrowed books for the borrower
         * @return ArrayList of Books
         */
        public ArrayList<Book> getBooks() {
            return this.borrower_books;
        }


        /**
         * materialize borrower books from bookID list
         */
        public void init() {
            this.borrower_books = lib.getBooks(this.borrower_book_id);
        }

    }


    /**
     * Each user contains a contact object
     * Representing their contact information and ideal pickup location
     */
    public class Contact {

        private int phone;
        private String pickup_location;
        private String address;
        private String email;

        /**
         * empty constructor for firebase
         */
        private Contact() {

        }


        public String getPickup_location() {
            return pickup_location;
        }

        public void setPickup_location(String pickup_location) {
            this.pickup_location = pickup_location;
        }

        public int getPhone() {
            return phone;
        }

        public void setPhone(int phone) {
            this.phone = phone;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }

    /**
     * Returns the given users username
     * @return String oof the users username
     */
    public String getUsername(){
        return username;
    }


    /**
     * Returns the borrowers books
     * @return ArrayList of Books
     */
    public ArrayList<Book> getBorrowedBooks() {
        return this.borrower.getBooks();
    }


    /**
     * Materializes all of the users books from their bookID list
     * Used to speed up application and reduce queries to DB
     */
    public void initUserBooks() {
        this.borrower.init();
        this.owner.init();
    }

    /**
     * Give library access to the user
     * @param lib The library
     */
    public void setLib(Library lib) {
        this.lib = lib;
    }

    /**
     * Gets the DB ID for a user
     * @return String of the DB ID
     */
    public String getDbID() {
        return this.dbID;
    }


}
