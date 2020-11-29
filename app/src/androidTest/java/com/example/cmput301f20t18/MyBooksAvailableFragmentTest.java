package com.example.cmput301f20t18;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MyBooksAvailableFragmentTest {
    private Solo solo;

    private static final String EMAIL = "OtherBot@OtherBot.botnet";
    private static final String PASSWORD = "BotPass";

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        LoginActivityTest.login(solo);
        MyBooksAddBookTest.addBook(solo);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
    }
    @After
    public void deleteBooks(){
        deleteAll(solo);
    }
    @Test
    public void start(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
    }
    @Test
    public void bookAdded(){
        assertTrue(solo.waitForText(MyBooksAddBookTest.DEFAULT_BOOK_TITLE, 1, 2000));
        assertTrue(solo.waitForText(MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR, 1, 2000));
        assertTrue(solo.waitForText(MyBooksAddBookTest.DEFAULT_BOOK_YEAR, 1, 2000));
        assertTrue(solo.waitForText(MyBooksAddBookTest.DEFAULT_BOOK_ISBN, 1, 2000));
        assertTrue(solo.waitForText("No requests", 1, 2000));
    }
    @Test
    public void bookRequested(){
        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.login(solo, EMAIL, PASSWORD);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

    }
    @Test
    public void acceptRequest(){

    }



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
