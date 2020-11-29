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


public class AddBookActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        RobotiumLoginManager.loginOwner(solo);
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
                AddBookActivity.class);

        solo.enterText((EditText)solo.getView(R.id.title_input),
                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input),
                RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input),
                RobotiumUserBookManager.DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input),
                RobotiumUserBookManager.DEFAULT_BOOK_ISBN);
        solo.clickOnView(solo.getView(R.id.return_to_my_books));
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        assertFalse(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
                1, 2000));
        assertFalse(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR,
                1, 2000));
        assertFalse(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_YEAR,
                1, 2000));
        assertFalse(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_ISBN,
                1, 2000));
    }
    @Test
    public void addBookDone(){
        solo.assertCurrentActivity("Wrong Activity - NOT MYBOOKSADDBOOKS",
                AddBookActivity.class);

        solo.enterText((EditText)solo.getView(R.id.title_input),
                RobotiumUserBookManager.DEFAULT_BOOK_TITLE);
        solo.enterText((EditText)solo.getView(R.id.author_input),
                RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR);
        solo.enterText((EditText)solo.getView(R.id.year_input),
                RobotiumUserBookManager.DEFAULT_BOOK_YEAR);
        solo.enterText((EditText)solo.getView(R.id.isbn_input),
                RobotiumUserBookManager.DEFAULT_BOOK_ISBN);
        solo.clickOnButton("Done");
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        solo.clickOnView(solo.getView(R.id.tab_mybooks));
        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_TITLE,
                1, 2000));
        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_AUTHOR,
                1, 2000));
        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_YEAR,
                1, 2000));
        assertTrue(solo.waitForText(RobotiumUserBookManager.DEFAULT_BOOK_ISBN,
                1, 2000));
        RobotiumUserBookManager.deleteAll(solo);
    }
}
