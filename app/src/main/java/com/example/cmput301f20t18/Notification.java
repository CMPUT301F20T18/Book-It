package com.example.cmput301f20t18;

import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;


/**
 * Represents a notification for a user
 * @see User
 * @author deinum
 * https://www.androidauthority.com/android-push-notifications-with-firebase-cloud-messaging-925075/
 */
public class Notification {


    public static final int BORROW_REQUEST_BOOK = 1;
    public static final int CANCEL_PICKUP = 2;
    public static final int OWNER_ACCEPT_REQUEST = 3;
    public static final int OWNER_DELETE = 5;
    public static final int CHANGE_LOCATION = 6;

    static final String TAG = "NOTIF_DEBUG";

    private final String sourceUsername;
    private final String target_dbID;
    private final String bookTitle;
    private final int type;
    private String message;


    // database info
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore DB = FirebaseFirestore.getInstance();
    FirebaseDatabase RTDB = FirebaseDatabase.getInstance();
    CollectionReference userRef = DB.collection("users");


    /**
     * Contructor for a notification
     * @param sourceUsername The user whose action sends a notification
     * @param target_dbID The user who the notification is being sent to
     * @param bookTitle The title of the book used within the notification
     * @param type The type of notification being sent
     */
    public Notification(String sourceUsername, String target_dbID, String bookTitle, int type) {
        this.sourceUsername = sourceUsername;
        this.target_dbID = target_dbID;
        this.bookTitle = bookTitle;
        this.type = type;
        this.message = "";
    }


    /**
     * Prepare the message being sent to the user, depending on the type of notification being sent
     */
    public void prepareMessage() {
        switch (this.type) {


            case BORROW_REQUEST_BOOK:
                this.message = String.format("%s has requested to borrow %s!", this.sourceUsername, this.bookTitle);
                break;

            case CANCEL_PICKUP:
                this.message = String.format("%s has cancelled the pickup for %s!", this.sourceUsername, this.bookTitle);
                break;


            case OWNER_ACCEPT_REQUEST:
                this.message = String.format("%s has accepted your request for %s!", this.sourceUsername, this.bookTitle);
                break;


            case OWNER_DELETE:
                this.message = String.format("%s has deleted %s from his collection, the book is now yours!", this.sourceUsername, this.bookTitle);
                break;


            case CHANGE_LOCATION:
                this.message = String.format("%s has set the location for %s", this.sourceUsername, this.bookTitle);
            default:
                Log.d(TAG, "prepareMessage: Invalid Request Type!");
        }
    }


    /**
     * Initiate the sending of the Notification by writing it to the database
     * https://www.javatpoint.com/java-get-current-date
     */
    public void sendNotification() {

        // find the instance token of the targeted user
        userRef.whereEqualTo("dbID", target_dbID).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    User target = task.getResult().toObjects(User.class).get(0); // username is unique and non null


                    // find the current date
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();


                    // create the ID for the notification
                    String ID = target.getUsername() + "-" + now.toString();
                    ID = ID.replace('.', ':');
                    Log.d(TAG, "ID: " + ID);

                    // create the notification map
                    Map<Object, Object> notification = new HashMap<>();
                    notification.put("target", target.getInstanceToken());
                    notification.put("message", Notification.this.message);

                    // write our notification to the DB
                    RTDB.getReference().child("Notifications").child(ID.replace('.', ':')).setValue(notification);


                    UserNotification notif = new UserNotification(ID.replace('.', ':'), message);

                    // write our notification to RTDB
                    userRef.document(target.getDbID()).collection("notifications").document(ID.replace('.', ':')).set(notif);

                }

                else {
                    Log.d(TAG, "sendNotification - Error Finding User!");
                }
            }
        });
    }
}
