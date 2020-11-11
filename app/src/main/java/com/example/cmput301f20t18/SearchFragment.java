package com.example.cmput301f20t18;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.firestore.core.QueryListener;


public class SearchFragment extends Fragment {

    private final String TAG = "SEARCH_FRAG";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        Button searchButton;
        final ListView searchResultList;

        TabLayout tabLayout = view.findViewById(R.id.search_tab_layout);
        ViewPager viewPager = view.findViewById(R.id.search_viewPager);

        SearchPageAdapter pageAdapter = new SearchPageAdapter(getChildFragmentManager(), tabLayout.getTabCount(), getContext());

        viewPager.setAdapter(pageAdapter);

        //set up edit text
        final EditText searchEditText = view.findViewById(R.id.search_edit_text);

        //Set up spinner
        SpinnerOnClickListener spinnerListener = new SpinnerOnClickListener();

        Spinner searchSpinner = view.findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.search_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchSpinner.setAdapter(spinnerAdapter);
        searchSpinner.setOnItemSelectedListener(spinnerListener);

        //Set up search button
        searchButton = view.findViewById(R.id.search_button);
        searchButton.setOnClickListener(new SearchButtonOnClickListener(searchEditText, spinnerListener));

        return view;
    }

    /**
     * Search checks to ensure search field is populated
     * Search then calls a method depending on the selectedOption
     *
     * @param searchWord     String object containing user entered search key
     * @param selectedOption String object containing current option selected
     *                       in spinner
     */
    //TODO Possibly add options for which fields are queried
    //TODO User search has not been touched as of yet (will have similar functionality to bookSearch)
    private void search(String searchWord, String selectedOption) {
        if (searchWord != "") {
            //Currently debug text. Will in the future instantiate the proper object
            //In order to conduct a search
            if (selectedOption.equals("Books")) {
                searchBooks(searchWord, new String[]{"title"});         //Initialize field here for testing purpose. Will change later
            } else {
                Log.d("Search", "Users are cool");
                //SearchUsers(searchWord);
            }
        }
    }

    /**
     * searchBooks is called from Search and queries the database for books
     *
     * @param searchWord String object containing user entered search key
     */
    //TODO Populate adapter with query results
    //TODO add parameter String[] searchField which determines what fields to check
    private void searchBooks(String searchKey, String[] searchFields) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        QueryListener listener = new QueryListener();

        final CollectionReference bookCollection = db.collection("system")
                .document("System")
                .collection("books");

        searchBooksByTitle(bookCollection, searchKey);

    }

    private void searchBooksByTitle(CollectionReference bookCollection, String searchKey){
        final String field = "title";
        final String specialChar = "\uf8ff";

        QueryListener listener = new QueryListener();
        Query queryLower = bookCollection
                .orderBy(field)
                .startAt(searchKey.toLowerCase())
                .endAt(searchKey.toLowerCase() + specialChar);
        queryLower.addSnapshotListener(listener);

        Query queryUpper = bookCollection
                .orderBy(field)
                .startAt(searchKey.toUpperCase())
                .endAt(searchKey.toUpperCase() + specialChar);
        queryUpper.addSnapshotListener(listener);
    }

    /**
     * An OnClickListener with the ability to return data to the fragment
     */
    private class SpinnerOnClickListener implements AdapterView.OnItemSelectedListener {
        String searchOption = "Books";

        /**
         * Called when an item is selected. Sets searchOption to
         * the current selection of spinner
         */
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            searchOption = parent.getItemAtPosition(position).toString();
        }

        /**
         * Called when nothing is selected and currently does nothing
         */
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            //Do Nothing
        }

        /**
         * Grabs data from the listener
         *
         * @return The current selected item in listener
         */
        public String getSelected() {
            return searchOption;
        }
    }

    private class SearchButtonOnClickListener implements View.OnClickListener {
        private EditText searchEditText;
        private SpinnerOnClickListener spinnerListener;

        SearchButtonOnClickListener(EditText searchEditText, SpinnerOnClickListener spinnerListener) {
            this.searchEditText = searchEditText;
            this.spinnerListener = spinnerListener;
        }

        @Override
        public void onClick(View v) {
            String searchWord = searchEditText.getText().toString();
            String selectedOption = spinnerListener.getSelected();
            search(searchWord, selectedOption);
        }
    }

    class QueryListener implements EventListener<QuerySnapshot> {
        private String TAG = "QUERY";

        @Override
        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
            Log.d(TAG, "Querying!");
            for (QueryDocumentSnapshot snapshot : querySnapshot) {
                Book book = snapshot.toObject(Book.class);
                Log.d(TAG, "Current Book: " + book.getTitle());
            }
        }
    }
}
