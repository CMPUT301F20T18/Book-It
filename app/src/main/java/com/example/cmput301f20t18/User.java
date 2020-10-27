package com.example.cmput301f20t18;

public class User {
    private String username;

    public User(String user) {
        this.username = user;
    }

    public Boolean equals(User user){
        return this.username.equals(user.username);
    }

    public String getUsername(){
        return username;
    }
}
