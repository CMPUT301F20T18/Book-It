package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_search);

        EditText searchEditText;
        Spinner searchSpinner;
        Button searchButton;
        ListView searchResultList;

        //Temporary Create library to be replaced by user lib access
        Library lib = new Library();

        //Sets up spinner Currently broken. Assigning adapter to spinner causes a crash and debugger
        //complains about source not matching bytecode
        searchSpinner = findViewById(R.id.search_spinner);
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.search_spinner_array, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //searchSpinner.setAdapter(spinnerAdapter);

        searchEditText = findViewById(R.id.search_edit_text);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

    }
}