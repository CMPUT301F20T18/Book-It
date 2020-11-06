package com.example.cmput301f20t18;


import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * Library is a storage and retrieval
 * system for books within our system as well as
 * a interface for the application to interact with
 * the books collection of the database
 */
public class Library {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference bookCollection = db
            .collection("system")
            .document("system")
            .collection("book");

    private Hashtable<Integer, Book> bookLibrary = new Hashtable<>();


    /**
     * Serves to construct and initialize the Library
     * such that it is ready for future calls
     */
    Library() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        bookCollection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                @Nullable FirebaseFirestoreException error) {

                bookLibrary.clear();
                for (QueryDocumentSnapshot bookDocument : queryDocumentSnapshots){
                    String bookID = bookDocument.getId();
                    Log.d("LIB_DB", "Current Book ID: " + bookID);
                    Book book = bookDocument.toObject(Book.class);
                    bookLibrary.put(Integer.parseInt(bookID), book);

                }
            }
        });
    }

    private void fillBookLibrary(List<Book> books) {
        Hashtable<Integer, Book> bookTable = new Hashtable<Integer, Book>();
        for (Book book : books) {
            bookTable.put(book.getId(), book);
        }
        this.bookLibrary = bookTable;
    }

    /**
     * Adds a book to the book collection
     * within the database
     *
     * @param book Book object to be added
     */
    public void addBook(Book book) {
        String bookID = Integer.toString(book.getId());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("system")
                .document("system")
                .collection("book")
                .document(Integer.toString(book.getId()))
                .set(book)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("LIB_DB", "Book with ID: " + bookID
                                    + "was successfully added");
                        }
                        else{
                            Log.d("LIB_DB", "Book with ID: " + bookID
                                    + "was not added successfully");
                        }
                    }
                });
    }

    /**
     * Removes the book from the
     * books collection of the database
     *
     * @param book Book object to be deleted
     */
    public void deleteBook(Book book) {
        String bookID = Integer.toString(book.getId());

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("system")
                .document("system")
                .collection("book")
                .document(bookID)
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Log.d("LIB_DB", "Book with ID: " + bookID
                                    + "was successfully deleted");
                        }
                        else{
                            Log.d("LIB_DB", "Book with ID: " + bookID
                                    + "was not deleted successfully");
                        }
                    }
                });
    }

    /**
     * Grabs a Book object from Library's
     * bookLibrary given the Book objects
     * ID
     *
     * @param ID Integer representing the ID
     *           of the Book object
     * @return Book object that corresponds to
     * the ID given
     */
    public Book getBook(Integer ID) {
        return bookLibrary.get(ID);
    }

    /**
     * Grabs a List of Book objects from Library's
     * bookLibrary given the Book object
     * IDs
     *
     * @param IDs Integer List representing the IDs
     *            of the Book objects
     * @return Book object that corresponds to
     * the ID given
     */
    public ArrayList<Book> getBooks(List<Integer> IDs) {
        ArrayList<Book> books = new ArrayList<>();
        for (int i = 0; i < IDs.size(); i++) {
            books.add(bookLibrary.get(IDs.get(i)));
        }
        return books;
    }

    /**
     * Return a list of books for based on a search performed locally
     *
     * @param field represents the fields that can be searched
     * @param query represents the information that we want a mathcing book for
     * @return an ArrayList of books that match the search
     */
    public ArrayList<Book> searchBookLocal(int field, String query) {
        ArrayList<Book> outBooks = new ArrayList<>();
        for (int key : this.bookLibrary.keySet()) {
            switch (field) {
                case 0:
                    if (this.bookLibrary.get(key).getAuthor().toUpperCase() == query.toUpperCase()) {
                        outBooks.add(this.bookLibrary.get(key));
                    }
                    break;
                case 1:
                    if (this.bookLibrary.get(key).getTitle().toUpperCase() == query.toUpperCase()) {
                        outBooks.add(this.bookLibrary.get(key));
                    }
                    break;
                case 2:
                    if (this.bookLibrary.get(key).getISBN() == Long.parseLong(query)) {
                        outBooks.add(this.bookLibrary.get(key));
                    }
                    break;
            }
        }
        return outBooks;
    }
    //ToDo
    /*
    public ArrayList<Book> searchBookDB(String field, String query){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference searchRef = db.collection("books");
        Query searchQuery = searchRef.whereEqualTo(field, query);
    }
   */
}