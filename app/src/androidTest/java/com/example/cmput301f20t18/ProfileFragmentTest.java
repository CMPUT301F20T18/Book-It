package com.example.cmput301f20t18;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class ProfileFragmentTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        LoginActivityTest.Login(solo);
        solo.clickOnView(solo.getView(R.id.tab_profile));
    }
    @Test
    public void checkUserData(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);

        assertTrue(solo.waitForText(RegisterActivityTest.DEFAULT_USERNAME, 1, 2000));
        assertTrue(solo.waitForText(RegisterActivityTest.DEFAULT_PHONE, 1, 2000));
        assertTrue(solo.waitForText(RegisterActivityTest.DEFAULT_EMAIL, 1, 2000));
    }
    @Test
    public void signOut(){
        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnButton("Sign Out");
        solo.assertCurrentActivity("Wrong Activity - NOT LOGIN", Login.class);
    }
}
