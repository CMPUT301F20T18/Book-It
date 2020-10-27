package com.example.cmput301f20t18;

/**
 * A book object represents a book within our library
 */
public class Book {
    private String title;
    private int isbn;
    private String author;
    private int id;
    //TODO: private ??? photo;
    private String status;
    private static Library library;
    private User owner;

    /**
     * A book in our system
     * @param title The title of the book
     * @param isbn The ISBN of the book
     * @param author The Author of the book
     * @param id The unique Book ID within our library
     * @param status The status of the book within our library
     */
    public Book(String title, int isbn, String author, int id, String status, User owner) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.id = id;
        this.status = status;
        this.owner = owner;

    }

    /**
     * returns the title of the book object
     * @return The title of the book object
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set the title of the book object
     * @param title The title you want the book to have
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the ISBN of the book object
     * @return The ISBN of the book, as an integer
     */
    public int getISBN() {
        return isbn;
    }

    /**
     * returns the author of the book object
     * @return String representation of the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * sets the author of the current book object
     * @param author The new author for the book
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * returns the unique bookID within our library
     * @return the unique ID of the book as an int
     */
    public int getId() {
        return id;
    }


    /**
     * get the status of the current book object
     * @return The status of the book
     */
    public String getStatus() {
        return status;
    }

    /**
     * set the status of the current book
     * @param status The new status for the book
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Set the library of the book
     * @param newLibrary The library to associate the book to
     */
    public static void setLibrary(Library newLibrary){
        library = newLibrary;
    }

    /**
     * get the owner of the book object
     * @return User object representing the owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set the owner of a book
     * @param owner The new owner of the book
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }
}
