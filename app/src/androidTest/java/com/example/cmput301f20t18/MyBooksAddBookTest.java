package com.example.cmput301f20t18;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

//NOT DONE
public class MyBooksAddBookTest {
    private Solo solo;

    public static final String DEFAULT_BOOK_TITLE = "Robots Can't Read!";
    public static final String DEFAULT_BOOK_AUTHOR = "AuthorBot9000";
    public static final String DEFAULT_BOOK_YEAR = "2020";
    public static final String DEFAULT_BOOK_ISBN = "1001001101110";

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        LoginActivityTest.Login(solo);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        solo.clickOnButton("Add book");
    }
    @Test
    public void start(){
        Activity activity = rule.getActivity();
    }
    @Test
    public void addBookCancel(){
        solo.assertCurrentActivity("Wrong Activity - NOT MYBOOKSADDBOOKS",
                MyBooksAddBook.class);

        solo.enterText((EditText)solo.getView(R.id.title_input), DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input), DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input), DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input), DEFAULT_BOOK_ISBN);
        solo.clickOnView(solo.getView(R.id.return_to_my_books));
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        assertFalse(solo.waitForText(DEFAULT_BOOK_TITLE, 1, 2000));
        assertFalse(solo.waitForText(DEFAULT_BOOK_AUTHOR, 1, 2000));
        assertFalse(solo.waitForText(DEFAULT_BOOK_YEAR, 1, 2000));
        assertFalse(solo.waitForText(DEFAULT_BOOK_ISBN, 1, 2000));
    }
    @Test
    public void addBookDone(){
        solo.assertCurrentActivity("Wrong Activity - NOT MYBOOKSADDBOOKS",
                MyBooksAddBook.class);

        solo.enterText((EditText)solo.getView(R.id.title_input), DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input), DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input), DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input), DEFAULT_BOOK_ISBN);
        solo.clickOnButton("Done");
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        assertTrue(solo.waitForText(DEFAULT_BOOK_TITLE, 1, 2000));
        assertTrue(solo.waitForText(DEFAULT_BOOK_AUTHOR, 1, 2000));
        assertTrue(solo.waitForText(DEFAULT_BOOK_YEAR, 1, 2000));
        assertTrue(solo.waitForText(DEFAULT_BOOK_ISBN, 1, 2000));

        MyBooksAvailableFragmentTest.deleteAll(solo);
    }

    public static void addBook(Solo solo){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        solo.clickOnButton("Add book");
        solo.enterText((EditText)solo.getView(R.id.title_input), DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input), DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input), DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input), DEFAULT_BOOK_ISBN);
        solo.clickOnButton("Done");
    }
}
