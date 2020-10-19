package com.example.cmput301f20t18;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.annotation.TransitionRes;

import java.util.ArrayList;

public class User {
    private String username;
    private Bitmap profilePicture;


    // implements the owner functionality for a user
    private class Owner {
        private ArrayList<Transaction> owner_transactions;
        private ArrayList<Integer> owner_books;


        /**
         *
         * @param type The status of the book to be found
         * @return list of bookIDs matching the requested status
         */
        public ArrayList<Integer> getBooks(String type) {
            ArrayList<Integer> filtered = new ArrayList<Integer>();
            for (int i = 0; i < Books.size(); i++) {
                if (Library.getBook(Books.get(i)).getStatus() == type) {
                    filtered.add(Books.get(i));
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
                if (owner_books.get(i).getbookID() == bookID) {
                    owner_books.remove(i);
                    // TO DO: Also delete the book from the book DB
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
            Book newBook = Library.addBook(isbn, title, author);
            owner_books.add(newBook.getbookID());
        }




        /**
         * accept the request for a book and notify the user it has been accepted
         * remove all other request for books with the same book ID
         * @param request The request transaction to accept
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void acceptRequest(RequestTransaction request) {
            RequestTransaction denied;
            request.accept();
            // deny all other requests with the same bookID?
            int bookID = request.getBookID();
            for (int i = 0; i < owner_transactions.size(); i++) {
                if (owner_transactions.get(i).getBookID() == bookID) {
                    // TODO: Downcast here to gain access to requestTransaction methods
                    Transaction current = owner_transactions.get(i);

                    
                    denied = owner_transactions.get(i);
                    owner_transactions.remove(i);
                    denied.changeStatus("declined");
                    // TODO
                    // remove transactions from transaction DB with same bookID and requested status

                }
            }
        }



        /**
         * Deny the request for a book and notify the borrower that the request was declined
         * @param t_id The request transaction to decline
         */
        @RequiresApi(api = Build.VERSION_CODES.N)
        public void denyRequest(int t_id) {

            // TODO: Implement notification for borrower
        }


        /**
         * Let owner signOff on a book, marking the book either as Borrowed or available
         * @param bookID The bookID of the book being signed off on
         */
        public void signOff(int bookID) {
            for (int i = 0; i < owner_transactions.size(); i++) {
                if (owner_transactions.get(i).getbookID() == bookID) {
                    owner_transactions.get(i).ownerScan();
                }
            }
        }
    }




    // each users borrower interface
    private class Borrower {
        private ArrayList<Transaction> borrower_transactions;

        public int requestBook(int bookid) {
            // Transaction newRequest = new Transaction()
        }

        public int pickupBook(int bookid) {

        }


    }


    // contact for each user
    private class Contact {
        private int phone;
        private String address;
        private String email;


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
}
