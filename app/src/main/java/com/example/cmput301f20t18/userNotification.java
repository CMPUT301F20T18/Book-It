package com.example.cmput301f20t18;


/**
 * Represents an in app notification
 * Not to be confused with a general Notification
 * @see Notification
 * @author deinum
 */
public class userNotification {
    private String id;
    private String message;

    /**
     * Default constructor for an in app Notification
     * @param id The notification ID
     * @param message The notification message
     */
    public userNotification(String id, String message) {
        this.id = id;
        this.message = message;
    }

    /**
     * Empty constructor for firestore
     */
    public userNotification() {
    }

    /**
     * Get the notification- ID
     * @return String representation of the notification ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ID for a notification
     * @param id The new ID for the notification
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the notification message
     * @return String representing the notifications message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the notification message
     * @param message The new message for the notification
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
