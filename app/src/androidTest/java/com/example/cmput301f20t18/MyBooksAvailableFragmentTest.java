package com.example.cmput301f20t18;

import com.robotium.solo.Solo;

public class MyBooksAvailableFragmentTest {



    public static void deleteAll(Solo solo){
        solo.assertCurrentActivity("Wrong Activity - HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        while (solo.searchText("ISBN", 1)){
            solo.clickOnView(solo.getView(R.id.button_book_more));
            solo.clickOnButton("Delete");
            solo.clickOnButton("Delete");
        }

    }
}
