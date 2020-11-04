package com.example.cmput301f20t18;

import android.graphics.Bitmap;

import java.util.Comparator;

/**
 * A book object represents a book within our library
 */
public class Book {

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_BORROWED = 3;

    private String title;
    private long isbn;
    private String author;
    private int id;
    private Bitmap photo;
    private int status;
    private User owner;
    private int year;

    //private static Library library = new Library();

    /**
     * A book in our system
     * @param title The title of the book
     * @param isbn The ISBN of the book
     * @param author The Author of the book
     * @param id The unique Book ID within our library
     * @param status The status of the book within our library
     */
    public Book(String title, long isbn, String author, int id, int status, User owner, int year) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.id = id;
        this.status = status;
        this.owner = owner;
        this.year = year;

    }

    /**
     * Empty constructor for firebase
     */
    public Book() {

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
    public long getISBN() {
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
    public int getStatus() {
        return status;
    }

    /**
     * set the status of the current book
     * @param status The new status for the book
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * Set the library of the book
     * @param newLibrary The library to associate the book to
     */
    public static void setLibrary(Library newLibrary){
        //library = newLibrary;
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

    /**
     * Set the year of a book
     * @param year The new year of the book
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Get the year of the book
     * @return the year of the book
     */
    public int getYear() { return year; }

    /**
     * Used for sorting books when in the MyBooks>Available tab.
     * Books are sorted by status then alphabetically by title.
     * This is reliable only for books of status "available" or "requested".
     *
     * @return -1 if o1<o2, 0 if o1==o2, 1 if o1>o2
     */
    static Comparator<Book> getMyBooksAvailableComparator() {
        return new Comparator<Book>() {
            @Override
            public int compare(Book o1, Book o2) {
                if (o1.getStatus() == o2.getStatus()) {
                    return o1.getTitle().compareToIgnoreCase(o2.getTitle()); // alphabetically
                }
                else if (o1.getStatus() == STATUS_AVAILABLE) { // available > requested
                    return 1;
                }
                else {
                    return -1;
                }
            }
        };
    }

}
