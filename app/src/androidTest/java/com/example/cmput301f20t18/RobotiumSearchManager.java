package com.example.cmput301f20t18;

import android.view.View;
import android.widget.EditText;

import com.robotium.solo.Solo;

public class RobotiumSearchManager {
    public static Boolean searchUser(Solo solo, String USERNAME, String searchKey){
        EditText searchEditText = (EditText)solo.getView(R.id.search_edit_text);
        View searchButton = solo.getView(R.id.search_button);

        solo.enterText(searchEditText, searchKey);
        solo.clickOnView(searchButton);
        solo.clearEditText(searchEditText);

        return solo.waitForText(USERNAME, 1, 2000);
    }
    public static Boolean searchBook(Solo solo, String bookTitle, String searchKey){
        EditText searchEditText = (EditText)solo.getView(R.id.search_edit_text);
        View searchButton = solo.getView(R.id.search_button);

        solo.enterText(searchEditText, searchKey);
        solo.clickOnView(searchButton);
        solo.clearEditText(searchEditText);

        return solo.waitForText(bookTitle, 1, 2000);
    }
}
