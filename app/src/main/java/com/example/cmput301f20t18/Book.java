package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.FileUtils;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;


/**
 * A book object represents a book within our library
 * Books are stored within our library
 */
public class Book implements Comparable<Book> {

    public static final int STATUS_AVAILABLE = 0;
    public static final int STATUS_REQUESTED = 1;
    public static final int STATUS_ACCEPTED = 2;
    public static final int STATUS_BORROWED = 3;

    private String title;
    private long isbn;
    private String author;
    private int id;
    private ArrayList<String> photos;
    private int status;
    private String owner_username;
    private String owner_dbID;
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
    public Book(String title, long isbn, String author, int id, int status, String owner, int year, String owner_dbID, String owner_username) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.id = id;
        this.status = status;
        this.owner_username = owner_username;
        this.owner_dbID = owner_dbID;
        this.year = year;
        this.photos = new ArrayList<String>();

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
     * get the owner of the book object
     * @return User object representing the owner
     */
    public String getOwner() {
        return owner_username;
    }

    /**
     * Set the owner of a book
     * @param owner The new owner of the book
     */
    public void setOwner(String owner) {
        this.owner_username = owner;
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
     * Used for sorting books.
     * Books in My Books>Available are sorted by status and then alphabetically by title.
     * Anywhere else books are sorted alphabetically by title.
     *
     * @return -1 if this<o, 0 if this==o, 1 if this>o
     */
    @Override
    public int compareTo(Book o) {
        if (this.getStatus() == o.getStatus()) {
            return this.getTitle().compareToIgnoreCase(o.getTitle()); // alphabetically
        }
        else if (this.getStatus() == STATUS_AVAILABLE) { // available > requested
            return 1;
        }
        else {
            return -1;
        }
    }

    /**
     * Set the isbn for a book
     * @param isbn The new isbn for the book
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }


    /**
     * Set book ID to a new ID
     * @param id the new ID for a book
     */
    public void setId(int id) {
        this.id = id;
    }
    /* Returns the cover picture of a book
     * @return the byte[] represntation of a cover photo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<byte[]> retrievePhotos() {
        ArrayList<byte[]> outPhotos = new ArrayList<byte[]>();
        for(String photo: this.photos){
            outPhotos.add(Base64.getDecoder().decode(photo));
        }
        return outPhotos;
    }


    /**
     * Adds a photo to the arrayList of photos the book has, the first one is the cover
     * @param photoByte The byte representation of a book
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void addPhoto(byte[] photoByte) {

        String photo = Base64
                .getEncoder()
                .encodeToString(photoByte);
        this.photos.add(photo);
    }

    /**
     * Removes a photo to the arrayList of photos the book has
     * @param i is the index of the book to be removed
     * @exception IndexOutOfBoundsException is thrown if the given index i is out of range
     */

    public void removePhoto(int i) {
        this.photos.remove(i);
    }

    /**
     * Changes which string is at position 0 of the ArrayList this.photos which represents the
     * cover picture
     * @param i is the index of the book that is to be the new cover
     * @exception IndexOutOfBoundsException is thrown if the given index i is out of range
     */

    public void setCover(int i) {
        String cover = this.photos.remove(i);
        ArrayList<String> newCover = new ArrayList<>();
        newCover.add(cover);
        newCover.addAll(this.photos);
        this.photos = newCover;
    }


    public void setPhotos(ArrayList<String> photos) {
        this.photos = photos;
    }


    public String getCover() {
        return this.photos.get(0);
    }


    public ArrayList<String> getPhotos() {
        return photos;
    }
}
