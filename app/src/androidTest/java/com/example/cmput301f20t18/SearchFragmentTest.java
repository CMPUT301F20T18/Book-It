package com.example.cmput301f20t18;


import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class SearchFragmentTest {
    private Solo solo;

    private static final String EMAIL = "OtherBot@OtherBot.botnet";
    private static final String PASSWORD = "BotPass";
    private static final String USERNAME = RobotiumLoginManager.owner.getUsername();

    private static final String TITLE = RobotiumUserBookManager.DEFAULT_BOOK_TITLE;
    private static final String AUTHOR = RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR;
    private static final String YEAR = RobotiumUserBookManager.DEFAULT_BOOK_YEAR;
    private static final String ISBN = RobotiumUserBookManager.DEFAULT_BOOK_ISBN;


    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        RobotiumLoginManager.loginOwner(solo);
        RobotiumUserBookManager.add(solo);
    }
    @After
    public void cleanUp(){
        RobotiumUserBookManager.deleteAll(solo);
    }
    @Test
    public void searchBookOwnedExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, TITLE));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, AUTHOR));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, YEAR));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, ISBN));
        assertTrue(solo.waitForText("Owned By You", 1, 2000));
    }
    @Test
    public void searchBookOwnedImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        for (int i=1; i<TITLE.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, TITLE.substring(0,i)));
            assertTrue(solo.waitForText("Owned By You", 1, 2000));
        }

        for (int i=1; i<AUTHOR.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, AUTHOR.substring(0,i)));
            assertTrue(solo.waitForText("Owned By You", 1, 2000));
        }
    }
    @Test
    public void searchBookUnownedExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        RobotiumLoginManager.signOut(solo);

        RobotiumLoginManager.loginBorrower(solo);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, TITLE));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, AUTHOR));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, YEAR));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, ISBN));
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        RobotiumLoginManager.signOut(solo);
        solo.assertCurrentActivity("Wrong Activity - NOT LOGIN", Login.class);
        RobotiumLoginManager.loginOwner(solo);
    }
    @Test
    public void searchBookUnownedImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        RobotiumLoginManager.signOut(solo);

        RobotiumLoginManager.loginBorrower(solo);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        for (int i=1; i<TITLE.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, TITLE.substring(0,i)));
            assertTrue(solo.waitForText("Request Book", 1, 2000));
        }

        for (int i=1; i<AUTHOR.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchBook(solo, TITLE, AUTHOR.substring(0,i)));
            assertTrue(solo.waitForText("Request Book", 1, 2000));
        }

        RobotiumLoginManager.signOut(solo);
        RobotiumLoginManager.loginOwner(solo);
    }
    @Test
    public void searchBookNonexistent(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));

        assertFalse(RobotiumSearchManager.searchBook(solo, TITLE, "Hello"));
        assertFalse(RobotiumSearchManager.searchBook(solo, TITLE, "1"));

    }
    @Test
    public void requestBookFromSearch(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        RobotiumLoginManager.signOut(solo);

        RobotiumLoginManager.loginBorrower(solo);

        //Test Making request and prompt on cancelling request within search
        assertTrue(RobotiumTransactionManager.requestBook(solo, TITLE,
                TITLE));
        solo.clickOnButton("Cancel Request");
        solo.clickOnButton("No");
        assertTrue(solo.waitForText("Cancel Request", 1, 2000));
        solo.clickOnButton("Cancel Request");
        solo.clickOnButton("Yes");
        assertTrue(solo.waitForText("Request Book", 1, 2000));

        RobotiumLoginManager.signOut(solo);
        RobotiumLoginManager.loginOwner(solo);
    }


    @Test
    public void searchUserSelfExplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        assertTrue(RobotiumSearchManager.searchUser(solo,
                USERNAME, USERNAME));
    }
    @Test
    public void searchUserSelfImplicit(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        for (int i=1; i<USERNAME.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchUser(solo, USERNAME,
                    USERNAME.substring(0,i)));
        }
    }

    @Test
    public void searchUserOtherExplicit(){
        RobotiumLoginManager.signOut(solo);
        RobotiumLoginManager.loginBorrower(solo);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        assertTrue(RobotiumSearchManager.searchUser(solo, USERNAME, USERNAME));
    }
    @Test
    public void searchUserOtherImplicit(){
        RobotiumLoginManager.signOut(solo);
        RobotiumLoginManager.loginBorrower(solo);
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");
        for (int i=1; i<USERNAME.length(); i+=5){
            assertTrue(RobotiumSearchManager.searchUser(solo, USERNAME, USERNAME.substring(0,i)));
        }
    }

}
