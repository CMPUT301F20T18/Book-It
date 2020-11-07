package com.example.cmput301f20t18;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Hashtable;
import java.util.List;

/**
 * TransactionLibrary is to be used as an interface between
 * the program and the database allowing us reduce queries
 */
public class TransactionLibrary {
    private Hashtable<Integer, Transaction> transactionLibrary;

    /**
     * Constructs TransactionLibrary
     */
    public TransactionLibrary() {
        this.transactionLibrary = new Hashtable<Integer, Transaction>();
        initTransactionLibrary();
    }

    /**
     * Initializes TransactionLibrary such that it has all the data
     * locally
     */
    private void initTransactionLibrary(){
        List<Transaction> transactions = getDataFromDB();
        for (int i=0; i < transactions.size(); i++){
            Transaction transaction = transactions.get(i);
            this.transactionLibrary.put(transaction.getID(), transaction);
        }

    }

    /**
     * Grabs all transactions from database
     * @return The listener holding the data
     */
    private List<Transaction> getDataFromDB(){
        TransactionLibOnCompleteListener listener = new TransactionLibOnCompleteListener();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Task queryAttempt = db.collection("transactions")
                .get()
                .addOnCompleteListener(listener);
        return listener.returnData();
    }

    /**
     * To be called on the creation of a new transaction
     * adds the new transaction to local maps and database
     * @param transaction the transaction to be added
     */
    public void addTransaction(Transaction transaction){
        addLocal(transaction);
        addDB(transaction);

    }

    /**
     * Adds the transaction to the local HashMaps to avoid a query
     * @param transaction transaction to be added
     */
    /*
     * Note this function might not actually be that useful. I think
     * we might want to just add it to the DB and perform a quick update
     * and I think firebase actually automatically updates the db
     * Have to read up on the documentation but yeah one of these functions might
     * not be necessary given depending on if my memory of the lab is correct.
     */

    private void addLocal(Transaction transaction){
        this.transactionLibrary.put(transaction.getID(), transaction);
    }

    /**
     * Function called within add transaction
     * Used to add a new transaction to the database
     * @param transaction the transaction being added to the database
     */
    //TODO: Add success and failure listener for debugging purposes
    private void addDB(Transaction transaction){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("transactions")
                .document(Integer.toString(transaction.getID()))
                .set(transaction);
    }

    /**
     * This function allows for one to use the ID of
     * a transaction to get the transaction object
     * @param ID The ID of the transaction to be retrieved
     * @return The transaction object corresponding to ID
     */
    public Transaction getTransactionById(Integer ID){
        return this.transactionLibrary.get(ID);
    }

    /**
     * Custom listener for retrieving data from the database
     */
    private class TransactionLibOnCompleteListener implements OnCompleteListener {
        private List<Transaction> items;

        /**
         * Called on completion of task
         * Takes data from task and stores it locally
         * @param task The task being completed
         */
        @Override
        public void onComplete(@NonNull Task task) {
            if (task.isSuccessful()){
                QuerySnapshot queryResult = (QuerySnapshot) task.getResult();
                this.items = queryResult.toObjects(Transaction.class);
            }
            else{
                throw new RuntimeException("Database query failed");
            }
        }

        /**
         * Returns data retrieved from the database
         * @return A list of transactions that were retrieved
         */
        public List<Transaction> returnData(){
            return this.items;
        }
    }

}
