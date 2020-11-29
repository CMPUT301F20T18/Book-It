package com.example.cmput301f20t18;

import android.widget.EditText;

import com.robotium.solo.Solo;

/**
 * Handles adding and deleting books from a robotium controlled user
 */
public class RobotiumUserBookManager {
    public static final String DEFAULT_BOOK_TITLE = "Robots Can't Read!";
    public static final String DEFAULT_BOOK_AUTHOR = "AuthorBot9000";
    public static final String DEFAULT_BOOK_YEAR = "2020";
    public static final String DEFAULT_BOOK_ISBN = "1001001101110";

    public static void add(Solo solo){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        solo.clickOnButton("Add book");
        solo.enterText((EditText)solo.getView(R.id.title_input), DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input), DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input), DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input), DEFAULT_BOOK_ISBN);
        solo.clickOnButton("Done");
    }
    public static void deleteAll(Solo solo){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        while (solo.searchText("ISBN", 1)) {
            solo.clickOnView(solo.getView(R.id.button_book_more));
            solo.clickOnButton("Delete");
            solo.clickOnButton("Delete");
        }
    }
}
