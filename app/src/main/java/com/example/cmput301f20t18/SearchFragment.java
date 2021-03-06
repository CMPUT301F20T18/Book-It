package com.example.cmput301f20t18;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * SearchFragment is a fragment which handles user searching for other users and books
 * Functionality
 * @author Chase Warwick
 * UI contrabutions
 * @author Johnathon Gil
 * @author shuval
 */
@RequiresApi(api = Build.VERSION_CODES.M)
public class SearchFragment extends Fragment {
    private final String TAG = "SEARCH_FRAG";                                       //Tag for Log

    final ArrayList<Book> bookDataList = new ArrayList();
    final ArrayList<User> userDataList = new ArrayList();

    SearchFragBookAdapter bookAdapter;
    SearchFragUserAdapter userAdapter;

    TextView noResultsTextView;
    EditText searchEditText;
    Button searchButton;
    ListView SearchResultList;

    /**
     * onCreateView is called on creation
     * It initializes various UI elements (Button, TabLayout, ViewPager, EditText, etc.)
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return A View object
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        bookAdapter = new SearchFragBookAdapter(this.getContext(), bookDataList);
        userAdapter = new SearchFragUserAdapter(this.getContext(), userDataList);

        //Set up spinner
        SpinnerOnClickListener spinnerListener = new SpinnerOnClickListener();

        Spinner searchSpinner = view.findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.search_spinner_array, R.layout.custom_spinner_item);
        spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item);
        searchSpinner.setAdapter(spinnerAdapter);
        searchSpinner.setOnItemSelectedListener(spinnerListener);

        noResultsTextView = view.findViewById(R.id.no_results);

        //set up edit text
        searchEditText = view.findViewById(R.id.search_edit_text);
        searchEditText.setOnEditorActionListener(
                new SearchEditTextOnActionListener(searchEditText, spinnerListener));

        //Set up search button
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(
                new SearchButtonOnClickListener(searchEditText, spinnerListener));


        SearchResultList = view.findViewById(R.id.search_result_list);

        if (getArguments() != null) {
            String ISBN = getArguments().getString("ISBN");
            if (ISBN != null) {
                searchEditText.setText(ISBN);
                searchButton.performClick();
            }
            noResultsTextView.setText(""); // this has to be here for some reason
        }
        return view;
    }

    // this is for when a user clicks "search for available copies" in postscan
    @Override
    public void onStart() {
        super.onStart();
        /*if (getArguments() != null) {
            String ISBN = getArguments().getString("ISBN");
            if (ISBN != null) {
                searchEditText.setText(ISBN);
                searchButton.performClick();
            }
            noResultsTextView.setText(""); // this has to be here for some reason
        }*/
    }

    @Override
    public void onResume() {
        super.onResume();
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
        if (!searchWord.equals("")) {
            if (selectedOption.equals("Books")) {
                SearchResultList.setAdapter(bookAdapter);
                searchBooks(searchWord, bookAdapter, true);
            }
            else {
                SearchResultList.setAdapter(userAdapter);
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
    private void searchBooks(String searchKey, SearchFragBookAdapter adapter, boolean allBooks) {
        bookDataList.clear();
        final QueryBookListener listener = new QueryBookListener(allBooks);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        final CollectionReference collection = db.collection("books");

        BookQueryHandler.searchByTitle(collection, listener, searchKey);
        BookQueryHandler.searchByAuthor(collection, listener, searchKey);
        BookQueryHandler.searchByISBN(collection, listener, searchKey);
        BookQueryHandler.searchByYear(collection, listener, searchKey);
        adapter.notifyDataSetChanged();
    }

    /**
     * searchUsers is called from Search and calls various methods which query the database for
     * users
     *
     * @param searchKey
     */
    private void searchUsers(String searchKey) {
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
            String option = parent.getItemAtPosition(position).toString(); // get selected option

            // change the EditText hint and clear the list when changing options
            if (option.equals("Books")) {
                Log.d(TAG, "onItemSelected: Books selected");
                searchEditText.setHint(R.string.search_book);
                userAdapter.clear();
            } else {
                Log.d(TAG, "onItemSelected: Users selected");
                searchEditText.setHint("Enter a username");
                bookAdapter.clear();
            }

            noResultsTextView.setText(R.string.no_results);
            setSearchOption(option);
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
         *
         * @param selected
         */
        private void setSearchOption(String selected) {
            this.searchOption = selected;
        }

        /**
         * getSearchOption gets the currently selected item of
         * the spinner
         *
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
         *
         * @param searchEditText  A EditText object which contains the user entered search key
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
         *
         * @param v A View object
         */
        @Override
        public void onClick(View v) {
            String searchWord = searchEditText.getText().toString();
            String selectedOption = spinnerListener.getSearchOption();
            InputMethodManager imm = (InputMethodManager) v.getContext()
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            search(searchWord, selectedOption);
            search(searchWord, selectedOption);
        }
    }

    private class SearchEditTextOnActionListener implements TextView.OnEditorActionListener {
        private EditText searchEditText;
        private SpinnerOnClickListener spinnerListener;

        /**
         * Creates an instance of the listener
         *
         * @param searchEditText  EditText for the search bar
         * @param spinnerListener SpinnerOnClickListener for getting selected search option
         */
        SearchEditTextOnActionListener(EditText searchEditText,
                                       SpinnerOnClickListener spinnerListener) {
            this.searchEditText = searchEditText;
            this.spinnerListener = spinnerListener;
        }

        /**
         * Called on doing an action within the editor
         * Checks if the action was a search button click and if so calls search function
         *
         * @param v        TextView object
         * @param actionId int representing action taken
         * @param event    KeyEvent
         * @return Boolean demonstrating whether the action was handled or not
         */
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String searchWord = searchEditText.getText().toString();
                String selectedOption = spinnerListener.getSearchOption();
                InputMethodManager imm = (InputMethodManager) v.getContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                search(searchWord, selectedOption);
                handled = true;
            }
            return handled;
        }
    }

    /**
     * QueryHandler handles search queries by the user
     */
    private static class QueryHandler {
        private static String TAG = "SEARCH_FRAG";      //Tag for Log

        /**
         * queryByString handles queries for words and characters by constructing a query and
         * setting the SnapshotListener (provided by the call) to said query. The query will return
         * any entry in the database which is similar to the string provided
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param field      A String object containing the field being queried
         * @param searchKey  A String object containing the key to be searched for
         */
        static void queryByString(CollectionReference collection,
                                  EventListener<QuerySnapshot> listener,
                                  String field, String searchKey) {
            Log.d(TAG, "Field: " + field);

            final String specialChar = "\uf8ff";

            Query queryLower = collection
                    .orderBy(field)
                    .startAt(searchKey)
                    .endAt(searchKey + specialChar);
            queryLower.addSnapshotListener(listener);
        }

        /**
         * queryByNumberEqual handles queries for numbers by constructing a query and
         * setting the SnapshotListener (provided by the call) to said query. The query will return
         * any entry in the database which is equivalent in value to the key
         * In the case where the key is not parsable into a string an error will be caught
         * and logged
         *
         * @param collection      A CollectionReference object referring to the collection being queried
         * @param listener        A EventListener for QuerySnapshots which manipulates data returned by
         *                        the query
         * @param field           A String object containing the field being queried
         * @param searchKeyString A String object containing the key to be searched for
         */
        static void queryByNumberEqual(CollectionReference collection,
                                       EventListener<QuerySnapshot> listener,
                                       String field, String searchKeyString) {
            Log.d(TAG, "Field: " + field);
            try {
                Long searchKey = Long.parseLong(searchKeyString);
                Query query = collection
                        .whereEqualTo(field, searchKey);
                query.addSnapshotListener(listener);
            } catch (NumberFormatException numberFormatException) {
                Log.e(TAG, "String entered is not able to be parsed as a number");
            }
        }
    }

    /**
     * BookQueryHandler handles search queries by the user for books by making calls to
     * QueryHandler
     */
    private static class BookQueryHandler {

        /**
         * Searches the database for any books whose title is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param searchKey  A String object containing the key to be searched for
         */
        static void searchByTitle(CollectionReference collection, QueryBookListener listener,
                                  String searchKey) {
            final String field = "title";

            QueryHandler.queryByString(collection, listener, field, searchKey);
        }

        /**
         * Searches the database for any books whose author is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param searchKey  A String object containing the key to be searched for
         */
        static void searchByAuthor(CollectionReference collection, QueryBookListener listener,
                                   String searchKey) {
            final String field = "author";

            QueryHandler.queryByString(collection, listener, field, searchKey);
        }

        //TODO After restructuring of DB, make this work
        static void searchByOwnerUsername(CollectionReference collection,
                                          QueryBookListener listener, String searchKey) {
            final String field = "owner";
        }

        /**
         * Searches the database for any books whose ISBN is equivalent to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param searchKey  A String object containing the key to be searched for
         */
        static void searchByISBN(CollectionReference collection, QueryBookListener listener,
                                 String searchKey) {
            final String field = "isbn";

            QueryHandler.queryByNumberEqual(collection, listener, field, searchKey);
        }

        /**
         * Searches the database for any books whose year published is equivalent to the key provided
         * by the user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param searchKey  A String object containing the key to be searched for
         */
        static void searchByYear(CollectionReference collection, QueryBookListener listener,
                                 String searchKey) {
            final String field = "year";

            QueryHandler.queryByNumberEqual(collection, listener, field, searchKey);
        }
    }

    /**
     * UserQueryHandler handles search queries by the user for users by making calls to
     * QueryHandler
     */
    private static class UserQueryHandler {

        /**
         * Searches the database for any users whose username is similar to the key provided by the
         * user
         *
         * @param collection A CollectionReference object referring to the collection being queried
         * @param listener   A EventListener for QuerySnapshots which manipulates data returned by
         *                   the query
         * @param searchKey  A String object containing the key to be searched for
         */
        static void searchByUsername(CollectionReference collection, QueryUserListener listener, String searchKey) {
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
        private boolean allBooks;

        public QueryBookListener(boolean allBooks){
            this.allBooks = allBooks;
        }

        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {

            for (QueryDocumentSnapshot snapshot : querySnapshot) {
                boolean add = true;
                Book book = snapshot.toObject(Book.class);
                Log.d(TAG, "Current Book: " + book.getTitle());

                int status = book.getStatus();
                if (status != Book.STATUS_ACCEPTED && status != Book.STATUS_BORROWED) {

                    int bookID = book.getId();

                    for (Book bookContained : bookDataList) {
                        if (bookID == bookContained.getId()) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        bookDataList.add(book);
                        bookAdapter.notifyDataSetChanged();
                    }
                }
            }
            if (bookDataList.size() == 0) {
                noResultsTextView.setText(getResources().getString(R.string.no_results));
            } else {
                noResultsTextView.setText("");
            }
        }
    }

    /**
     * QueryUserListener is an EventListener which handles data gotten from a query to the
     * users collection
     */
    class QueryUserListener implements EventListener<QuerySnapshot> {
        private String TAG = "SEARCH_FRAG";

        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot,
                            @Nullable FirebaseFirestoreException error) {
            for (QueryDocumentSnapshot snapshot : querySnapshot) {
                boolean add = true;
                User user = snapshot.toObject(User.class);
                Log.d(TAG, "Current User: " + user.getUsername());
                for (User userContained : userDataList) {
                    if (user.getDbID().equals(userContained.getDbID())) {
                        add = false;
                    }
                }
                if (add) {
                    userDataList.add(user);
                    userAdapter.notifyDataSetChanged();
                }
            }
            if (userDataList.size() == 0) {
                noResultsTextView.setText(getResources().getString(R.string.no_results));
            } else {
                noResultsTextView.setText("");
            }
        }
    }

    /**
     * SearchFragBookAdapter is an Adapter object used for displaying book data retrieved from the
     * database to the user
     */
    class SearchFragBookAdapter extends ArrayAdapter<Book> {
        private ArrayList<Book> books;
        private Context context;
        private ArrayList<Transaction> transactionDataList = new ArrayList<>();

        /**
         * Constructs an instance of SearchFragBookAdapter
         * @param context
         * @param books ArrayList of Book objects retrieved from the database
         */
        public SearchFragBookAdapter(Context context, ArrayList<Book> books) {
            super(context, 0, books);
            this.books = books;
            this.context = context;

            String user = FirebaseAuth.getInstance().getUid();
            Query transactionQuery = FirebaseFirestore.getInstance()
                    .collection("transactions")
                    .whereEqualTo("borrower_dbID", FirebaseAuth.getInstance().getUid());
            transactionQuery.addSnapshotListener(new QueryTransactionListener(this));
        }

        /**
         * Gets the view depending on a variety of factors and sets the values to display each Book
         * object
         * @param position An int object representing a pointer to the current index of books
         * @param convertView A View object
         * @param parent A ViewGroup object
         * @return The correct view for the book
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view;

            Book book = books.get(position);

            boolean requested = false;
            int bookID = book.getId();

            boolean userOwned = book.getOwner_dbID().equals(FirebaseAuth.getInstance().getUid());

            for (Transaction transaction : transactionDataList) {
                if (transaction.getBookID() == bookID) {
                    requested = true;
                    break;
                }
            }

            if (userOwned) {
                view = LayoutInflater.from(context).inflate(R.layout.card_book_search_owned,
                        parent, false);
            } else if (requested) {
                view = LayoutInflater.from(context).inflate(R.layout.card_borrowed_requested,
                        parent, false);
            } else {
                view = LayoutInflater.from(context).inflate(R.layout.card_book_search_request,
                        parent, false);
                Button requestBook = view.findViewById(R.id.button_request);
                requestBook.setOnClickListener(new RequestBookButtonListener(book));
            }

            TextView owner = view.findViewById(R.id.text_username);
            TextView description = view.findViewById(R.id.text_user_description);
            Button buttonCancelRequest = view.findViewById(R.id.button_cancel_request);
            ImageButton buttonProfile = view.findViewById(R.id.button_mybooks_user);

            // set the user details
            if (owner != null && description != null) {
                owner.setText(book.getOwner_username());
                description.setText(R.string.owner);
            }

            // Books that the user has requested will have a cancel request button
            if (buttonCancelRequest != null) {
                buttonCancelRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new android.app.AlertDialog.Builder(v.getContext(), R.style.CustomDialogTheme)
                                .setTitle("Cancel request")
                                .setMessage("Are you sure you want to cancel your request?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // find the transaction associated with this book for the current user
                                        User current = new User();
                                        Log.d(TAG, "onClick: " + book.getId());
                                        current.borrowerCancelRequest(book.getId());

                                    }
                                })
                                .setNeutralButton("No", null)
                                .show();
                    }
                });
            }

            // Not all cards will have a profile pic button
            if (buttonProfile != null) {
                buttonProfile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(view.getContext(), CheckProfileActivity.class);
                        intent.putExtra("USERNAME", book.getOwner_username());
                        view.getContext().startActivity(intent);
                    }
                });

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference collection = db.collection("users");
                collection.whereEqualTo("username", book.getOwner_username()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List borrowers = task.getResult().toObjects(User.class);
                            if(borrowers.size()>0) {
                                User borrower = (User) borrowers.get(0);
                                String photoString = borrower.getProfile_picture();
                                if (photoString != null && !photoString.equals("")) {
                                    Bitmap bm = photoAdapter.stringToBitmap(photoString);
                                    Bitmap photo = photoAdapter.makeCircularImage(bm, buttonProfile.getHeight());
                                    buttonProfile.setImageBitmap(photo);
                                    Log.d(TAG, "Picture attached");
                                }
                            }
                        } else {
                            Log.d(TAG, "Error Querying for borrower information");
                        }
                    }
                });
            }

            TextView bookTitle = view.findViewById(R.id.text_book_title);
            TextView bookAuthor = view.findViewById(R.id.text_book_author);
            TextView bookISBN = view.findViewById(R.id.text_book_isbn);
            TextView bookYear = view.findViewById(R.id.text_book_year);

            if (book.hasPhotos()) {
                ImageButton bookImageView = view.findViewById(R.id.image_view);
                Bitmap bookCoverImage = photoAdapter.scaleBitmap(book.retrieveCover(),
                        bookImageView.getLayoutParams().width, bookImageView.getLayoutParams().height);
                bookImageView.setImageBitmap(bookCoverImage);

                bookImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent slider = new Intent(view.getContext(), ImageSliderActivity.class);
                        slider.putExtra("ID", book.getId());
                        Activity activity = (Activity) view.getContext();

                        activity.startActivity(slider);

                    }
                });
            }

            bookTitle.setText(book.getTitle());
            bookAuthor.setText(book.getAuthor());
            bookISBN.setText(Long.toString(book.getISBN()));
            bookYear.setText(Integer.toString(book.getYear()));

            return view;
        }

        /**
         * RequestBookButtonListener is an OnClickListener for the request button
         */
        private class RequestBookButtonListener implements View.OnClickListener {
            private Book book;

            /**
             * Constructs an instance of RequestBookButtonListener
             * @param book
             */
            public RequestBookButtonListener(Book book) {
                this.book = book;
            }

            /**
             * onClick is called when the button is clicked and it
             * logs data for debugging
             * tells user to create a request for the book clicked
             * @param v A View object
             */
            @Override
            public void onClick(View v) {
                book.setStatus(Book.STATUS_REQUESTED);
                bookAdapter.notifyDataSetChanged();

                User current = new User();
                Log.d(TAG, "User is attempting to request " + book.getTitle()
                        + " from user " + book.getOwner_username());
                current.borrowerRequestBook(book.getId());
            }
        }

        /**
         * QueryTransactionListener is an EventListener for QuerySnapshot events and is used to
         * determine which books the user has previously requested. It also notifies the adapter
         * to update onDataChange so that the adapter responds to any changes in the database
         */
        private class QueryTransactionListener implements EventListener<QuerySnapshot> {
            final String TAG = "SEARCH_FRAG";
            final SearchFragBookAdapter adapter;

            /**
             * Constructs an instance of QueryTransactionListener
             * @param adapter A SearchFragBookAdapter responsible for displaying data to the user
             */
            public QueryTransactionListener(SearchFragBookAdapter adapter){
                this.adapter = adapter;
            }

            /**
             * onEvent is called when a QuerySnapshot event occurs and clears the
             * transactionDataList before adding the updated information to it then notifies the
             * adapter of changes
             * @param querySnapshot A QuerySnapshot object carrying data from the database
             * @param error A FirebaseFirestoreException object representing errors that occured
             */
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot,
                                @Nullable FirebaseFirestoreException error) {
                transactionDataList.clear();
                for (QueryDocumentSnapshot snapshot : querySnapshot) {
                    Transaction transaction = snapshot.toObject(Transaction.class);
                    Log.d(TAG, "Current Transaction: " + transaction.getID());
                    transactionDataList.add(transaction);
                }
                adapter.notifyDataSetChanged();
            }
        }
    }


    /**
     * SearchFragUserAdapter is an Adapter object used for displaying user Data retrieved from the
     * database to the user
     */
    class SearchFragUserAdapter extends ArrayAdapter<User> {
        private ArrayList<User> users;
        private Context context;

        /**
         * Constructs an instance of SearchFragUserAdapter
         * @param context
         * @param users ArrayList of User objects retrieved from the database
         */
        public SearchFragUserAdapter(Context context, ArrayList<User> users) {
            super(context, 0, users);
            this.users = users;
            this.context = context;
        }

        /**
         * Gets the view depending on a variety of factors and sets the values to display each Book
         * object
         * @param position An int object representing a pointer to the current index of books
         * @param convertView A View object
         * @param parent A ViewGroup object
         * @return The correct view for the book
         */
        @RequiresApi(api = Build.VERSION_CODES.O)
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                view = LayoutInflater.from(context).inflate(R.layout.card_user_search, parent, false);
            }

            User user = users.get(position);

            TextView userName = view.findViewById(R.id.text_username);
            CardView card = view.findViewById(R.id.profile_card);
            ImageView profilePicView = view.findViewById(R.id.profile_view);

            //Set up profile pic

            String profilePictureSting = user.getProfile_picture();
            if (profilePictureSting != null && !profilePictureSting.equals("")){

                String profilePicString = user.getProfile_picture();

                /*
                Bitmap bm = photoAdapter.scaleBitmap(
                        photoAdapter.stringToBitmap(profilePictureSting),
                        profilePicView.getLayoutParams().width,
                        profilePicView.getLayoutParams().height);
                Bitmap profilePicture = photoAdapter.makeCircularImage(bm, bm.getHeight());
                */

                profilePicView.setImageBitmap(photoAdapter.stringToBitmap(profilePicString));
            }


            userName.setText(user.getUsername());
            card.setOnClickListener(new ViewProfileButtonListener(user));

            return view;
        }
        


        /**
         * ViewProfileButtonListener is an OnClickListener for the request button
         */
        public class ViewProfileButtonListener implements View.OnClickListener {
            private User user;

            /**
             * Constructs an instance of ViewProfileUserAdapter
             * @param user User object that was clicked
             */
            public ViewProfileButtonListener(User user) {
                this.user = user;
            }

            /**
             * onClick is called when the button is clicked and it passes data to
             * CheckProfileActivity's intent before starting the activity
             * @param v A View object
             */
            @Override
            public void onClick(View v) {
                Intent viewProfileIntent = new Intent(v.getContext(), CheckProfileActivity.class);
                viewProfileIntent.putExtra("USERNAME", user.getUsername());
                viewProfileIntent.putExtra("PHONE", user.getPhone());
                viewProfileIntent.putExtra("EMAIL", user.getEmail());
                viewProfileIntent.putExtra("PICTURE", user.getProfile_picture());

                startActivity(viewProfileIntent);
            }
        }
    }
}


