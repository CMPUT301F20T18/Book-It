package com.example.cmput301f20t18;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class MyBooksAddBookTest {

    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
    }
    /**
     * Gets the Activity
     * @throws Exception
     */
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void checkAddBook(){
// Asserts that the current activity is the MainActivity. Otherwise, show “Wrong Activity
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("Add");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRMATION");
        solo.clearEditText((EditText) solo.getView(R.id.editText_name)); //Clear the EditText
/* True if there is a text: Edmonton on the screen, wait at least 2 seconds and find
minimum one match. */
        assertTrue(solo.waitForText("Edmonton", 1, 2000));
        solo.clickOnButton("CLEAR ALL"); //Select CLEAR ALL
//True if there is no text: Edmonton on the screen
        assertFalse(solo.searchText("Edmonton"));
    }

    /**
     * Check item taken from the listview
     */
    @Test
    public void checkCiyListItem(){
        solo.assertCurrentActivity("Wrong Activity", MainActivity.class);
        solo.clickOnButton("ADD CITY");
        solo.enterText((EditText) solo.getView(R.id.editText_name), "Edmonton");
        solo.clickOnButton("CONFIRMATION");
        solo.waitForText("Edmonton", 1, 2000);
// Get MainActivity to access its variables and methods.
        MainActivity activity = (MainActivity) solo.getCurrentActivity();
        final ListView cityList = activity.cityList; // Get the listview
        String city = (String) cityList.getItemAtPosition(0); // Get item from first position
        assertEquals("Edmonton", city);
    }

    /**
     * Closes the activity after each test
     * @throws Exception
     */
    @After
    public void tearDown() throws Exception{
        solo.finishOpenedActivities();
    }
}
