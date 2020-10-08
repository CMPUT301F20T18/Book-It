package com.example.cmput301f20t18;

public class Book {
    private String title;
    private long ISBN;
    private long id;
    private String photo;
    private String status;

    // no path to photo
    public Book(String title, long ISBN) {
        this.title = title;
        this.ISBN = ISBN;
    }

    // book receives path to photo
    public Book(String title, long ISBN, String photo) {
        this.title = title;
        this.ISBN = ISBN;
        this.photo = photo;
    }



}

