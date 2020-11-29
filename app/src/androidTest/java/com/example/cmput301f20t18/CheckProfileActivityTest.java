package com.example.cmput301f20t18;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CheckProfileActivityTest {
    private Solo solo;

    public static final String EMAIL = "OtherBot@OtherBot.botnet";
    public static final String PASSWORD = "BotPass";

    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        RobotiumLoginManager.loginBorrower(solo);

        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
        solo.clickOnView(solo.getView(R.id.tab_search));
        solo.clickOnView(solo.getView(R.id.search_spinner));
        solo.clickOnText("Users");

        String username = RobotiumLoginManager.owner.getUsername();
        RobotiumSearchManager.searchUser(solo, username, username);

        solo.clickOnButton("View Profile");
    }
    @Test
    public void start(){
        solo.assertCurrentActivity("Wrong Activity - NOT CHECKPROFILEACTIVITY",
                CheckProfileActivity.class);
    }
    @Test
    public void checkData(){
        RobotiumUser owner = RobotiumLoginManager.owner;
        solo.assertCurrentActivity("Wrong Activity - NOT CHECKPROFILEACTIVITY",
                CheckProfileActivity.class);
        assertTrue(solo.waitForText(owner.getUsername(), 1, 2000));
        assertTrue(solo.waitForText(owner.getPhoneNum(), 1, 2000));
        assertTrue(solo.waitForText(owner.getEmail(), 1, 2000));
    }
}
