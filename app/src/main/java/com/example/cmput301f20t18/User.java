package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;



/**
 * User represents any user in our system
 * and contains both functionality for owners and borrowers, but not both have to be used
 */
public class User {
    private static Library library = new Library();
    private String username;
    private String DB_id;
    private Bitmap profilePicture;

    public User(String username, int appID, String DB_id, String email, String address) {
        //String id = auth.getUid();
        FirebaseDatabase user_db = FirebaseDatabase.getInstance();
        DatabaseReference ref = user_db.getReference();
    }
    /**
     * Implements the functionality for owner behaviors
     */
    public class Owner {
        private ArrayList<Transaction> owner_transactions;
        private ArrayList<Integer> owner_books;


        /**
         *
         * @param type The status of the book to be found
         * @return list of bookIDs matching the requested status
         */
        public ArrayList<Integer> getBooks(String type) {
            ArrayList<Integer> filtered = new ArrayList<Integer>();
            for (int i = 0; i < owner_books.size(); i++) {
                if (library.getBook(owner_books.get(i)).getStatus() == type) {
                    filtered.add(owner_books.get(i));
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
                if (owner_books.get(i) == bookID) {
                    owner_books.remove(i);
                    // TODO: Also delete the book from the book DB
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
            DatabaseReference mRef = db.getReference("Book");

            // TODO: get unique ID number for each book


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
    }


    /**
     * Borrower contains the implementation for any borrowing activities
     */
    public class Borrower {
        private ArrayList<Transaction> borrower_transactions;
        private ArrayList<Book> borrower_books;

        /**
         * Request a book from a book owner
         * Add the request to borrower transaction list
         * @param bookID is the id of the book you wish to borrow
         * @return returns a transaction object, containing the borrower request
         */
//        public Transaction requestBook(int bookID) {
//            Book requested = bookLibrary.getBookByID(bookID);
//            User owner = requested.getOwner();
//            Transaction request = new RequestTransaction(owner, this, bookID);
//            borrower_transactions.add(request);
//            return request;
//        }


        /**
         * Pick-up the book, scanning it to mark it as borrowed
         * @param bookID the ID of the book being picked up
         */
//        public void pickupBook(int bookID) {
//            for (int i = 0; i < borrower_transactions.size(); i++) {
//                if (borrower_transactions.get(i).getBookID() == bookID) {
//                    Transaction a_trans = borrower_transactions.get(i);
//                    // scan the book as user??
//                    if (a_trans instanceof ExchangeTransaction) {
//                        ((ExchangeTransaction) a_trans).scanned(super);
//                    }
//                }
//            }
//        }


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
//        public ArrayList<Integer> bookSearch(String term) {
//
//        }

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






    }


    /**
     * Each user contains a contact object
     * Representing their contact information and ideal pickup location
     */
    private class Contact {
        private int phone;
        private String pickup_location;
        private String address;
        private String email;

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

    public String getUsername(){
        return username;
    }
}
