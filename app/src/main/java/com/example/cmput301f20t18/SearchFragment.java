package com.example.cmput301f20t18;

import android.os.Build;
import android.os.Bundle;
import android.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * SearchFragment is a fragment which handles user searching for other users and books
 * Functionality
 * @author Chase Warwick
 * UI contrabutions
 * @author Johnathon Gil
 */
//TODO: Add a listview to the UI and an adapter which can display search results for both
//      User and Book (Specifically either one that can take in an array set or if that is not
//      possible one that takes in a list and let Chase know to update searchFrag as appropriate)
@RequiresApi(api = Build.VERSION_CODES.M)
public class SearchFragment extends Fragment {

    private final String TAG = "SEARCH_FRAG";                                       //Tag for Log
    final ArrayList<Book> bookDataList = new ArrayList();
    final ArrayList<User> userDataList = new ArrayList();

    /**
     *  onCreateView is called on creation
     *  It initializes various UI elements (Button, TabLayout, ViewPager, EditText, etc.)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return A View object
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button searchButton;

        TabLayout tabLayout = view.findViewById(R.id.search_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.search_viewPager);

        SearchPageAdapter pageAdapter = new SearchPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        //viewPager.setAdapter(pageAdapter);                    //Chase commented this out because it results in a crash

        //Set up spinner
        SpinnerOnClickListener spinnerListener = new SpinnerOnClickListener();

        Spinner searchSpinner = view.findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.search_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(spinnerAdapter);
        searchSpinner.setOnItemSelectedListener(spinnerListener);

        //set up edit text
        final EditText searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener(
                new SearchEditTextOnActionListener(searchEditText, spinnerListener));

        //Set up search button
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(
                new SearchButtonOnClickListener(searchEditText, spinnerListener));
        return view;
    }

    /**
     * Search checks to ensure search field is populated
     * Search then calls a method depending on the selected option
     *
     * @param searchWord     String object containing user entered search key
     * @param selectedOption String object containing current option selected
     *                       in spinner
     */
    private void search(String searchWord, String selectedOption) {
        if (searchWord != "") {
            if (selectedOption.equals("Books")) {
                searchBooks(searchWord);
            } else {
                searchUsers(searchWord);
            }
        }
    }

    /**
     * searchBooks is called from Search and calls various methods which query the database for
     * books
     *
     * @param searchKey String object containing user entered search key
     */
    //TODO Populate adapter with query results
    //TODO add parameter String[] searchField which determines what fields to check
    private void searchBooks(String searchKey) {
        bookDataList.clear();
        final QueryBookListener listener = new QueryBookListener();

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference collection = db.collection("books");

        BookQueryHandler.searchByTitle(collection, listener, searchKey);
        BookQueryHandler.searchByAuthor(collection, listener, searchKey);
        BookQueryHandler.searchByISBN(collection, listener, searchKey);
        BookQueryHandler.searchByYear(collection, listener, searchKey);
    }

    /**
     * searchUsers is called from Search and calls various methods which query the database for
     * users
     * @param searchKey
     */
    private void searchUsers(String searchKey){
        userDataList.clear();
        final QueryUserListener listener = new QueryUserListener();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        final CollectionReference userCollection = db.collection("users");

        UserQueryHandler.searchByUsername(userCollection, listener, searchKey);
    }

    /**
     * An OnClickListener for SearchFragments spinner
     */
    private class SpinnerOnClickListener implements AdapterView.OnItemSelectedListener {
        String searchOption = "Books";

        /**
         * Called when an item is selected. Sets searchOption to
         * the current selection of spinner
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setSearchOption(parent.getItemAtPosition(position).toString());
        }

        /**
         * Called when nothing is selected and currently does nothing
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do Nothing
        }

        /**
         * setSearchOption is called from onItemSelected
         * Changes searchOption to the value selected
         * @param selected
         */
        private void setSearchOption(String selected){
            this.searchOption = selected;
        }

        /**
         * getSearchOption gets the currently selected item of
         * the spinner
         * @return The current selected item in listener
         */
        public String getSearchOption() {
            return searchOption;
        }
    }

    /**
     * An onClickListener for SearchFragments search button
     */
    private class SearchButtonOnClickListener implements View.OnClickListener {
        private EditText searchEditText;
        private SpinnerOnClickListener spinnerListener;

        /**
         * Creates an instance of the listener
         * @param searchEditText A EditText object which contains the user entered search key
         * @param spinnerListener A SpinnerOnClickListener with the ability to provide the currently
         *                        selected option
         */
        SearchButtonOnClickListener(EditText searchEditText, SpinnerOnClickListener spinnerListener) {
            this.searchEditText = searchEditText;
            this.spinnerListener = spinnerListener;
        }

        /**
         * onClick is called when the button is clicked
         * It calls a method which conducts a search of the database
         * @param v A View object
         */
        @Override
        public void onClick(View v) {
            String searchWord = searchEditText.getText().toString();
            String selectedOption = spinnerListener.getSearchOption();
            search(searchWord, selectedOption);
        }
    }

    private class SearchEditTextOnActionListener implements TextView.OnEditorActionListener{
        private EditText searchEditText;
        private SpinnerOnClickListener spinnerListener;

        /**
         * Creates an instance of the listener
         * @param searchEditText EditText for the search bar
         * @param spinnerListener SpinnerOnClickListener for getting selected search option
         */
        SearchEditTextOnActionListener(EditText searchEditText,
                                       SpinnerOnClickListener spinnerListener){
            this.searchEditText = searchEditText;
            this.spinnerListener = spinnerListener;
        }

        /**
         * Called on doing an action within the editor
         * Checks if the action was a search button click and if so calls search function
         * @param v TextView object
         * @param actionId int representing action taken
         * @param event KeyEvent
         * @return Boolean demonstrating whether the action was handled or not
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                String searchWord = searchEditText.getText().toString();
                String selectedOption = spinnerListener.getSearchOption();
                search(searchWord, selectedOption);
                handled = true;
            }
            return handled;
        }
    }

    /**
     * QueryHandler handles search queries by the user
     */
    private static class QueryHandler{
        private static String TAG = "SEARCH_FRAG";      //Tag for Log

        /**
         * queryByString handles queries for words and characters by constructing a query and
         * setting the SnapshotListener (provided by the call) to said query. The query will return
         * any entry in the database which is similar to the string provided
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param field A String object containing the field being queried
         * @param searchKey A String object containing the key to be searched for
         */
        static void queryByString(CollectionReference collection,
                                   EventListener<QuerySnapshot> listener,
                                   String field, String searchKey){
            Log.d(TAG, "Field: " + field);

            final String specialChar = "\uf8ff";

            Query queryLower = collection
                    .orderBy(field)
                    .startAt(searchKey.toLowerCase())
                    .endAt(searchKey.toLowerCase() + specialChar);
            queryLower.addSnapshotListener(listener);

            Query queryUpper = collection
                    .orderBy(field)
                    .startAt(searchKey.toUpperCase())
                    .endAt(searchKey.toUpperCase() + specialChar);
            queryUpper.addSnapshotListener(listener);
        }

        /**
         * queryByNumberEqual handles queries for numbers by constructing a query and
         * setting the SnapshotListener (provided by the call) to said query. The query will return
         * any entry in the database which is equivalent in value to the key
         * In the case where the key is not parsable into a string an error will be caught
         * and logged
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param field A String object containing the field being queried
         * @param searchKeyString A String object containing the key to be searched for
         */
        static void queryByNumberEqual(CollectionReference collection,
                                       EventListener<QuerySnapshot> listener,
                                       String field, String searchKeyString){
            Log.d(TAG, "Field: " + field);
            try{
                Long searchKey = Long.parseLong(searchKeyString);
                Query query = collection
                        .whereEqualTo(field, searchKey);
                query.addSnapshotListener(listener);
            }
            catch (NumberFormatException numberFormatException){
                Log.e(TAG, "String entered is not able to be parsed as a number");
            }
        }
    }

    /**
     * BookQueryHandler handles search queries by the user for books by making calls to
     * QueryHandler
     */
    private static class BookQueryHandler{

        /**
         * Searches the database for any books whose title is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param searchKey A String object containing the key to be searched for
         */
        static void searchByTitle(CollectionReference collection, QueryBookListener listener,
                                  String searchKey){
            final String field = "title";

            QueryHandler.queryByString(collection, listener, field, searchKey);
        }

        /**
         * Searches the database for any books whose author is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param searchKey A String object containing the key to be searched for
         */
        static void searchByAuthor(CollectionReference collection, QueryBookListener listener,
                                   String searchKey){
            final String field = "author";

            QueryHandler.queryByString(collection, listener, field, searchKey);
        }
        //TODO After restructuring of DB, make this work
        static void searchByOwnerUsername(CollectionReference collection,
                                          QueryBookListener listener, String searchKey){
            final String field = "owner";
        }

        /**
         * Searches the database for any books whose ISBN is equivalent to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param searchKey A String object containing the key to be searched for
         */
        static void searchByISBN(CollectionReference collection, QueryBookListener listener,
                                 String searchKey){
            final String field = "isbn";

            QueryHandler.queryByNumberEqual(collection, listener, field, searchKey);
        }

        /**
         * Searches the database for any books whose year published is equivalent to the key provided
         * by the user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param searchKey A String object containing the key to be searched for
         */
        static void searchByYear(CollectionReference collection, QueryBookListener listener,
                                 String searchKey){
            final String field = "year";

            QueryHandler.queryByNumberEqual(collection, listener, field, searchKey);
        }
    }

    /**
     * UserQueryHandler handles search queries by the user for users by making calls to
     * QueryHandler
     */
    private static class UserQueryHandler{

        /**
         * Searches the database for any users whose username is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener A EventListener for QuerySnapshots which manipulates data returned by
         *                 the query
         * @param searchKey A String object containing the key to be searched for
         */
        static void searchByUsername(CollectionReference collection, QueryUserListener listener, String searchKey){
            final String field = "username";

            QueryHandler.queryByString(collection, listener, field, searchKey);
        }
    }

    /**
     * QueryBookListener is an EventListener which handles data gotten from a query to the
     * books collection
     */
    class QueryBookListener implements EventListener<QuerySnapshot> {
        private String TAG = "SEARCH_FRAG";     //Tag for Log

        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            for (QueryDocumentSnapshot snapshot : querySnapshot) {
                boolean add = true;
                Book book = snapshot.toObject(Book.class);
                Log.d(TAG, "Current Book: " + book.getTitle());
                for (Book bookContained : bookDataList){
                    if (book.getId() == bookContained.getId()){
                        add = false;
                    }
                }
                if (add) {
                    bookDataList.add(book);
                }
            }
        }
    }

    /**
     * QueryUserListener is an EventListener which handles data gotten from a query to the
     * users collection
     */
    class QueryUserListener implements EventListener<QuerySnapshot>{
        private String TAG = "SEARCH_FRAG";

        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            for (QueryDocumentSnapshot snapshot : querySnapshot){
                boolean add = true;
                User user = snapshot.toObject(User.class);
                Log.d(TAG, "Current User: " + user.getUsername());
                for (User userContained : userDataList){
                    if (user.getDbID().equals(userContained.getDbID())){
                        add = false;
                    }
                }
                if (add){
                    userDataList.add(user);
                }
            }
        }
    }
}
