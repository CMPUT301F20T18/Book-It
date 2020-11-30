package com.example.cmput301f20t18;

import android.widget.EditText;

import com.robotium.solo.Solo;

import static junit.framework.TestCase.assertTrue;

public class RobotiumTransactionManager {

    public static Boolean requestBook(Solo solo, String bookTitle, String searchKey){
        final String TITLE = RobotiumUserBookManager.DEFAULT_BOOK_TITLE;
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        RobotiumSearchManager.searchBook(solo, TITLE, TITLE);
        solo.clickOnButton("Request Book");
        return solo.waitForText("Cancel Request");
    }


    public static void acceptRequestBook (Solo solo, String DEMO_ISBN){

        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_scan));
        solo.assertCurrentActivity("Wrong Activity - NOT SCANNER",
                Scanner.class);
        solo.clickOnButton("Manual");
        solo.enterText((EditText)solo.getView(R.id.manual_isbn_input), DEMO_ISBN);
        solo.clickOnButton("GO");
        solo.assertCurrentActivity("Wrong Activity - NOT POST SCAN ACTIVITY",
                PostScanActivity.class);
        assertTrue(solo.waitForText(DEMO_ISBN, 1, 2000));
        solo.clickOnButton("Confirm pick up");
        solo.assertCurrentActivity("Wrong Activity - NOT SCANNER",
                Scanner.class);
        solo.clickOnButton("Manual");
        solo.enterText((EditText)solo.getView(R.id.manual_isbn_input), DEMO_ISBN);
        solo.clickOnButton("GO");
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

    }
}
