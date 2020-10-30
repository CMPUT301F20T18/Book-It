package com.example.cmput301f20t18;


import java.util.ArrayList;
import java.util.Hashtable;




/**
 * An object that represents a library full of books
 */

public class Library {

    private Hashtable<Integer, Book> books;
    private Hashtable<String, ArrayList<Integer>> authorLookUp;
    private Hashtable<String, ArrayList<Integer>> titleLookUp;
    private Hashtable<Integer, ArrayList<Integer>> isbnLookUp;

    /**
     * The library of books
     */

    Library() {
        this.books = new Hashtable<Integer, Book>();
        this.authorLookUp = new Hashtable<>();
        this.titleLookUp = new Hashtable<>();
        this.isbnLookUp = new Hashtable<>();

    }

    /**
     * returns a book object based on the id int passed in
     * @param id an integer representing the id of the book to be returned
     * @return The book object
     */

    public static Book getBook(int id){
        return this.books.get(id);
    }

    /**
     * Adds a book object to the library, and updates the various searching HashTables
     * @param book the Book object to be added to library
     */

    public void addBook(Book book){
        this.books.put(book.getId(), book);
        if ( this.authorLookUp.keySet().contains(book.getAuthor().toUpperCase())){
            this.authorLookUp.get(book.getAuthor().toUpperCase()).add(book.getId());
        }
        else {
            ArrayList tempAuthor = new ArrayList();
            tempAuthor.add(book.getId());
            this.authorLookUp.put(book.getAuthor().toUpperCase(), tempAuthor);
        }
        if ( this.titleLookUp.keySet().contains(book.getTitle().toUpperCase())){
            this.titleLookUp.get(book.getTitle().toUpperCase()).add(book.getId());
        }
        else {
            ArrayList tempTitle = new ArrayList();
            tempTitle.add(book.getId());
            this.titleLookUp.put(book.getTitle().toUpperCase(), tempTitle);
        }
        if ( this.isbnLookUp.keySet().contains(book.getISBN())){
            this.isbnLookUp.get(book.getISBN()).add(book.getId());
        }
        else {
            ArrayList tempIsbn = new ArrayList();
            tempIsbn.add(book.getId());
            this.isbnLookUp.put(book.getISBN(), tempIsbn);
        }
    }

    /**
     * Deletes a book object based on the id int passed in
     * @param id an integer representing the id of the book to be returned
     */

    public void delBook(int id){
        this.books.remove(id);
    }

    /**
     * Checks whether a book with the corresponding id is in the library
     * @param id an integer representing the id of the book to be returned
     * @return A boolean representing whether the book is in the library
     */

    public boolean hasBook(int id){
        return this.books.containsKey(id);
    }

    /**
     * Checks the number of books currently in the library
     * @return An int representing the number of books in the library
     */

    public int numBooks(){
        return this.books.keySet().size();
    }
}