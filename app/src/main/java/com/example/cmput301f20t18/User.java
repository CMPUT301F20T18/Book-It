//package com.example.cmput301f20t18;
//
//import android.content.Context;
//import android.os.Build;
//
//import androidx.annotation.RequiresApi;
//import androidx.annotation.TransitionRes;
//
//import java.util.ArrayList;
//
//public class User {
//    private String username;
//
//
//
//    private class Owner {
//        private ArrayList<Transactions> Transactions;
//        private ArrayList<Integer> Books;
//
//
//        // filters an owners book by status and returns a
//        public ArrayList<Integer> getBooks(String type) {
//            ArrayList<Integer> filtered = new ArrayList<Integer>();
//            for (int i = 0 ; i < Books.size() ; i++) {
//                if (Library.getBook(Books.get(i)).getStatus() == type) {
//                    filtered.add(Books.get(i));
//                }
//            }
//            return filtered;
//        }
//
//
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        public void delBook(int bookid) {
//            Books.removeIf(s -> s.getID() == bookid);
//        }
//
//        // add a new book to the lib, we check in the library whether or not the book already exists
//        public void newBook(int isbn, String title, String author) {
//            int new_id = Library.addBook(isbn, title, author);
//            Books.add(new_id);
//        }
//
//
//        // accept the request for a book and notify the user it has been accepted
//        // remove all other request for books with the same book ID
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        public void acceptRequest(int t_id) {
//            int bookID = 0;
//            for (int i = 0 ; i < Transactions.size() ; i++) {
//                if (Transactions.get(i).gettransactionID() == t_id) {
//                    bookID = Transactions.get(i).getbookID();
//                    break;
//                }
//            }
//            if (bookID == 0) return;
//            final int finalBookID = bookID;
//            for (int j = 0 ; j < Transactions.size() ; j++) {
//                Transactions.removeIf(s -> s.getbookID() == finalBookID);
//
//            }
//
//        }
//
//        // deny the request and notfiy the user that the request has been rejected
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        public void denyRequest(int t_id) {
//            Transactions.removeIf(s -> s.getID() == t_id);
//            // TO DO
//            // Implement notification for borrower
//        }
//
//
//        // owners scans book to mark it as being borrowed
//        public void signOff(int bookID) {
//            for (int i = 0 ; i < Transactions.size() ; i++) {
//                if (Transactions.get(i).getbookID() == bookID) {
//                    Transactions.get(i).ownerScan();
//                }
//            }
//        }
//
//
//    }
//
//
//
//
//
//    // each users borrower interface
//    private class Borrower {
//        private ArrayList<Integer> Transactions;
//
//        public int requestBook(int bookid) {
//            // Transaction newRequest = new Transaction()
//        }
//
//        public int pickupBook(int bookid) {
//
//        }
//
//
//
//    }
//
//
//    // contact for each user
//    private class Contact {
//        private int phone;
//        private String address;
//        private String email;
//
//
//        public int getPhone() {
//            return phone;
//        }
//
//        public void setPhone(int phone) {
//            this.phone = phone;
//        }
//
//        public String getAddress() {
//            return address;
//        }
//
//        public void setAddress(String address) {
//            this.address = address;
//        }
//
//        public String getEmail() {
//            return email;
//        }
//
//        public void setEmail(String email) {
//            this.email = email;
//        }
//    }
//
//
//}
