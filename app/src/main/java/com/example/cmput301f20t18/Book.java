package com.example.cmput301f20t18;

import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;


/**
 * A book object represents a book within our system
 * Books are owned by users, and can be transferred to others
 * @see User
 * @author deinum
 * @author Sean Butler
 */
public class Book{

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
    private String borrower_username;


    /**
     * A book in our system
     * @param title The title of the book
     * @param isbn The ISBN of the book
     * @param author The Author of the book
     * @param id The unique Book ID within our library
     * @param status The status of the book within our library
     */
    public Book(String title, long isbn, String author, int id, int status, int year, String owner_dbID, String owner_username, ArrayList<String> photos) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.id = id;
        this.status = status;
        this.owner_username = owner_username;
        this.owner_dbID = owner_dbID;
        this.year = year;
        this.photos = photos;
        this.borrower_username = null;

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
     * Set book ID to a new ID
     * @param id the new ID for a book
     */
    public void setId(int id) {
        this.id = id;
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
     * Get the year of the book
     * @return the year of the book
     */
    public int getYear() { return year; }

    /**
     * Set the year of a book
     * @param year The new year of the book
     */
    public void setYear(int year) { this.year = year; }

    /**
     * Returns the ISBN of the book object
     * @return The ISBN of the book, as an integer
     */
    public long getISBN() {
        return isbn;
    }

    /**
     * Set the isbn for a book
     * @param isbn The new isbn for the book
     */
    public void setIsbn(long isbn) {
        this.isbn = isbn;
    }

    /**
     * DO NOT USE
     * FOR USE BY FIREBASE FIRESTORE FOR EASY CONVERSION TO BOOK OBJECT
     * @return ArrayList of String objects representing encoded photos
     */
    public ArrayList<String> getPhotos(){
        return this.photos;
    }

    /**
     * DO NOT USE
     * FOR USE BY FIREBASE FIRESTORE FOR EASY CONVERSION TO BOOK OBJECT
     * @param photos An ArrayList of String objects representing encoded photos
     */
    public void setPhotos(ArrayList<String> photos){
        this.photos = photos;
    }

    /**
     * Gets the borrowers username
     * @return String object representing borrowers username
     */
    public String getBorrower_username() {
        return borrower_username;
    }

    /**
     * Sets the borrowers username
     * @param borrower_username A String object representing the new borrower's username
     */
    public void setBorrower_username(String borrower_username) {
        this.borrower_username = borrower_username;
    }

    /**
     * get the owner of the book object
     * @return User object representing the owner
     */
    public String getOwner_username() {
        return owner_username;
    }


    /**
     * get the owner Database ID
     * @return String representation of the Database ID
     */
    public String getOwner_dbID() {
        return owner_dbID;
    }

//  May delete if no one needs this
//    /**
//     * Adds a photo to the arrayList of photos the book has, the first one is the cover
//     * @param photoByte The byte representation of a book
//     */
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    public void addPhoto(byte[] photoByte) {
//
//        String photo = Base64
//                .getEncoder()
//                .encodeToString(photoByte);
//        this.photos.add(photo);
//    }

//    /**
//     * Removes a photo to the arrayList of photos the book has
//     * @param i is the index of the book to be removed
//     * @exception IndexOutOfBoundsException is thrown if the given index i is out of range
//     */
//
//    public void removePhoto(int i) {
//        this.photos.remove(i);
//    }

//    /**
//     * Changes which string is at position 0 of the ArrayList this.photos which represents the
//     * cover picture
//     * @param i is the index of the book that is to be the new cover
//     * @exception IndexOutOfBoundsException is thrown if the given index i is out of range
//     */
//
//    public void choseCover(int i) {
//        String cover = this.photos.remove(i);
//        ArrayList<String> newCover = new ArrayList<>();
//        newCover.add(cover);
//        newCover.addAll(this.photos);
//        this.photos = newCover;
//    }

    /**
     * Check if a book has images
     * @return boolean representing
     */
    public boolean hasPhotos(){
        return !photos.isEmpty();
    }


    /**
     * Retrieve the cover photo of the book
     * @ Bitmap Representation of the photo
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Bitmap retrieveCover() {
        Bitmap cover = null;


        if (this.photos.size() > 0) {
            String coverString = this.photos.get(0);
            cover = photoAdapter.stringToBitmap(coverString);
        }
        return cover;
    }


    /**
     * Retrieve all the photos for the book
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public ArrayList<Bitmap> retrievePhotos() {
        ArrayList<Bitmap> photoList = new ArrayList<>();
        for(String photo: photos){
            photoList.add(photoAdapter.stringToBitmap(photo));
        }
        return photoList;
    }
}
