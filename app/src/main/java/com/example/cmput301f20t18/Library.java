package com.example.cmput301f20t18;


import java.util.Hashtable;



public class Library {
    private Hashtable<Integer, Book> books;


    Library(){
        this.books = new Hashtable<Integer, Book>();
    }


    public Book getBook(int id){
        return this.books.get(id);
    }

    public void delBook(int id){
        this.books.remove(id);
    }

    public boolean hasBook(int id){
        return this.books.containsKey(id);
    }

    public int numBooks(){
        return this.books.keySet().size();
    }
}
