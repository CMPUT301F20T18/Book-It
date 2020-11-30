package com.example.cmput301f20t18;

/**
 * A User controlled by robotium
 */
public class RobotiumUser {
    private String username;
    private String email;
    private String password;
    private String phoneNum;
    private String address;

    public RobotiumUser(String username, String email, String address) {
        this.username = username;
        this.email = email;
        this.password = "BotPass";
        this.phoneNum = "1234567890";
        this.address = address;
    }


    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNum() {
        return phoneNum;
    }
}
