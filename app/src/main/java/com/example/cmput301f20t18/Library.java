package com.example.cmput301f20t18;


import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

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
    private Hashtable<Integer, Book> bookLibrary;

    /**
     * Serves to construct and initialize the Library
     * such that it is ready for future calls
     */
    Library(){
        updateBookLibrary();
    }

    /**
     * Parses the list returned by getDataFromDB
     * and stores it in a map which is then assigned
     * to Libraries bookLibrary
     */
    public void updateBookLibrary(){
        this.bookLibrary = getDataFromDB();
    }

    /**
     * Pulls data from the database and stores it in a list
     * for future parsing
     * @return returns a list of books constructed from the
     * data in the database
     */
    //TODO: Test this implementation of pulling data to see if it is effective
    private Hashtable<Integer, Book> getDataFromDB(){
        Hashtable<Integer, Book> bookMap = new Map<Integer, Book>;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        QuerySnapshot documents = db.collectionGroup("books")
                .get()
                .getResult();
        for (DocumentSnapshot document:documents){
            bookMap.put(parseInt(document.getId()), document.toObject(Book.class));
        }
        return bookMap;
    }

    /**
     * Adds a new book both locally and remotely
     * @param book Book object to be added
     */
    public void addBook(Book book){
        addLocal(book);
        addDB(book);
    }

    /**
     * Adds a book to the local library
     * @param book Book object to be added
     */
    private void addLocal(Book book){
        this.bookLibrary.put(book.getId(), book);
    }

    /**
     * Adds a book to the book collection
     * within the database
     * @param book Book object to be added
     */
    private void addDB(Book book){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("system")
                .document("system")
                .collection("book")
                .document(Integer.toString(book.getId()))
                .set(book);
    }

    /**
     * Deletes a book both locally and remotely
     * @param book Book object to be deleted
     */
    public void deleteBook(Book book){
        deleteLocal(book);
        deleteDB(book);
    }

    /**
     * Removes the book from Libraries
     * bookLibrary
     * @param book Book object to be deleted
     */
    private void deleteLocal(Book book){
        this.bookLibrary.remove(book);
    }

    /**
     * Removes the book from the
     * books collection of the database
     * @param book Book object to be deleted
     */
    private void deleteDB(Book book){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("system")
                .document("system")
                .collection("book")
                .document(Integer.toString(book.getId()))
                .delete();
    }

    /**
     * Grabs a Book object from Library's
     * bookLibrary given the Book objects
     * ID
     * @param ID Integer representing the ID
     *           of the Book object
     * @return Book object that corresponds to
     *         the ID given
     */
    public Book getBook(Integer ID){
        return bookLibrary.get(ID);
    }

    public List<Book> getBooks(List<Integer> IDs){
        List<Book> books = new List<Book>;
        for (int i = 0; i < books.size(); i++){
            books.add(bookLibrary.get(IDs.get(i)));
        }
        return books;
    }

    /**
     * A complete listener that allows for the retrieval
     * of data onComplete
     */
    private class BookLibOnCompleteListener implements OnCompleteListener{
        private List<Book> items;

        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                QuerySnapshot queryResult = (QuerySnapshot) task.getResult();
                this.items = queryResult.toObjects(Book.class);
            }
            else{
                throw new RuntimeException("Database query failed");
            }
        }

        public List<Book> returnData(){
            return this.items;
        }
    }
}
