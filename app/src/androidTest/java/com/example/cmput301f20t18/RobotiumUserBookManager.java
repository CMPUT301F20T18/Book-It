package com.example.cmput301f20t18;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
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

        ViewGroup tabs = (ViewGroup) solo.getView(R.id.mybooks_tab_layout);
        Log.d("ROBO", tabs.toString());
        for (int i=0; i < tabs.getChildCount(); i++) {
            //deleteAllInTab(solo, (ViewGroup) tabs.getChildAt(i));
        }
            //deleteAllInTab(solo, (ViewGroup) tabs.getChildAt(i));

//            solo.sleep(2000);
            while (solo.searchText("ISBN", 1)) {
                View view = solo.getView(R.id.button_book_more);
                Log.d("ROBO", view.getParent().getParent().getParent().getParent().getParent().getParent().getParent().toString());
                solo.clickOnView(solo.getView(R.id.button_book_more));
                solo.clickOnButton("Delete");
                solo.clickOnButton("Delete");
            }
        //}
    }

    private static void deleteAllInTab(Solo solo, ViewGroup tab) {
        Log.d("ROBO", tab.toString());
        for (int i=0; i < tab.getChildCount(); i++){
            deleteBook(solo, (ViewGroup) tab.getChildAt(i));
        }
    }

    private static void deleteBook(Solo solo, ViewGroup book) {
        Log.d("ROBO", book.toString());
        for (int i=0; i < book.getChildCount(); i++){
            Log.d("ROBO", book.getChildAt(i).toString());
        }
    }
}
