package com.example.cmput301f20t18;


import android.app.Activity;
import android.view.View;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchFragmentTest {
    private Solo solo;

    public static final String EMAIL = "OtherBot@OtherBot.botnet";
    public static final String PASSWORD = "BotPass";

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        LoginActivityTest.Login(solo);
    }
    @Test
    public void start(){
        Activity activity = rule.getActivity();
    }
    @Test
    public void searchBookOwnedExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_TITLE));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_YEAR));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_ISBN));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));


        MyBooksAvailableFragmentTest.deleteAll(solo);
    }
    @Test
    public void searchBookOwnedImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        solo.clickOnView(solo.getView(R.id.tab_search));

        for (int i=1; i<MyBooksAddBookTest.DEFAULT_BOOK_TITLE.length(); i+=5){
            assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                    MyBooksAddBookTest.DEFAULT_BOOK_TITLE.substring(0,i)));
            assertTrue(solo.waitForText("Owned By You", 1, 2000));
        }

        for (int i=1; i<MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR.length(); i+=5){
            assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                    MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR.substring(0,i)));
            assertTrue(solo.waitForText("Owned By You", 1, 2000));
        }
        MyBooksAvailableFragmentTest.deleteAll(solo);
    }
    @Test
    public void searchBookUnownedExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        ProfileFragmentTest.signOut(solo);

        LoginActivityTest.Login(solo, EMAIL, PASSWORD);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_TITLE));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_YEAR));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_ISBN));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.Login(solo);
        MyBooksAvailableFragmentTest.deleteAll(solo);
    }
    @Test
    public void searchBookUnownedImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        ProfileFragmentTest.signOut(solo);

        LoginActivityTest.Login(solo, EMAIL, PASSWORD);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        for (int i=1; i<MyBooksAddBookTest.DEFAULT_BOOK_TITLE.length(); i+=5){
            assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                    MyBooksAddBookTest.DEFAULT_BOOK_TITLE.substring(0,i)));
            assertTrue(solo.waitForText("Request Book", 1, 2000));
        }

        for (int i=1; i<MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR.length(); i+=5){
            assertTrue(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                    MyBooksAddBookTest.DEFAULT_BOOK_AUTHOR.substring(0,i)));
            assertTrue(solo.waitForText("Request Book", 1, 2000));
        }

        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.Login(solo);
        MyBooksAvailableFragmentTest.deleteAll(solo);
    }
    @Test
    public void searchBookNonexistent(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertFalse(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE, "Hello"));
        assertFalse(searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE, "1"));

        MyBooksAvailableFragmentTest.deleteAll(solo);
    }
    @Test
    public void requestBookFromSearch(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        MyBooksAddBookTest.addBook(solo);
        ProfileFragmentTest.signOut(solo);

        LoginActivityTest.Login(solo, EMAIL, PASSWORD);

        //Test Making request and prompt on cancelling request within search
        assertTrue(requestBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE,
                MyBooksAddBookTest.DEFAULT_BOOK_TITLE));
        solo.clickOnButton("Cancel Request");
        solo.clickOnButton("No");
        assertTrue(solo.waitForText("Cancel Request", 1, 2000));
        solo.clickOnButton("Cancel Request");
        solo.clickOnButton("Yes");
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.Login(solo);
        MyBooksAvailableFragmentTest.deleteAll(solo);
    }


    @Test
    public void searchUserSelfExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        assertTrue(searchUser(RegisterActivityTest.DEFAULT_USERNAME, RegisterActivityTest.DEFAULT_USERNAME));
    }
    @Test
    public void searchUserSelfImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        for (int i=1; i<RegisterActivityTest.DEFAULT_USERNAME.length(); i+=5){
            assertTrue(searchUser(RegisterActivityTest.DEFAULT_USERNAME,
                    RegisterActivityTest.DEFAULT_USERNAME.substring(0,i)));
        }
    }

    @Test
    public void searchUserOtherExplicit(){
        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.Login(solo, EMAIL, PASSWORD);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        assertTrue(searchUser(RegisterActivityTest.DEFAULT_USERNAME,
                RegisterActivityTest.DEFAULT_USERNAME));
    }
    @Test
    public void searchUserOtherImplicit(){
        ProfileFragmentTest.signOut(solo);
        LoginActivityTest.Login(solo, EMAIL, PASSWORD);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");
        for (int i=1; i<RegisterActivityTest.DEFAULT_USERNAME.length(); i+=5){
            assertTrue(searchUser(RegisterActivityTest.DEFAULT_USERNAME,
                    RegisterActivityTest.DEFAULT_USERNAME.substring(0,i)));
        }
    }

    private Boolean searchBook(String bookTitle, String searchKey){
        EditText searchEditText = (EditText)solo.getView(R.id.search_edit_text);
        View searchButton = solo.getView(R.id.search_button);

        solo.enterText(searchEditText, searchKey);
        solo.clickOnView(searchButton);
        solo.clearEditText(searchEditText);

        return solo.waitForText(bookTitle, 1, 2000);
    }

    private Boolean requestBook(String bookTitle, String searchKey){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        searchBook(MyBooksAddBookTest.DEFAULT_BOOK_TITLE, MyBooksAddBookTest.DEFAULT_BOOK_TITLE);
        solo.clickOnButton("Request Book");
        return solo.waitForText("Cancel Request");
    }

    private Boolean searchUser(String username, String searchKey){
        EditText searchEditText = (EditText)solo.getView(R.id.search_edit_text);
        View searchButton = solo.getView(R.id.search_button);

        solo.enterText(searchEditText, searchKey);
        solo.clickOnView(searchButton);
        solo.clearEditText(searchEditText);

        return solo.waitForText(username, 1, 2000);
    }

}
