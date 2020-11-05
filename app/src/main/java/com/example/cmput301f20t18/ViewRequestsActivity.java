package com.example.cmput301f20t18;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class ViewRequestsActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_requests);

        recyclerView = findViewById(R.id.request_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Toolbar toolbar = findViewById(R.id.mybooks_toolbar);
        toolbar.setTitle(getResources().getText(R.string.view_requests_header));

        userList = new ArrayList<User>();
        userList.add(new User("phlafoo", 12345, "dbID1", "phlafoo@gmail.com", "Abbey Road"));
        userList.add(new User("MysticWolf", 23456, "dbID2", "mystic@gmail.com", "Drury Lane"));
        userList.add(new User("Solomon", 34567, "dbID3", "sol@gmail.com", "Sesame Street"));

        RequestsAdapter requestsAdapter = new RequestsAdapter(this, userList);
        recyclerView.setAdapter(requestsAdapter);

    }
}