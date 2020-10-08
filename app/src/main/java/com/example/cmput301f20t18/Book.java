package com.example.cmput301f20t18;

public class Book {
    private String title;
    private int isbn;
    private String author;
    private int id;
    //private ??? photo;
    private String status;
    private static Library library;

    public Book(String title, int isbn, String author, int id, String status) {
        this.title = title;
        this.isbn = isbn;
        this.author = author;
        this.id = id;
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIsbn() {
        return isbn;
    }

    public void setIsbn(int isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static void setLibrary(Library newLibrary){
        library = newLibrary;
    }
}
