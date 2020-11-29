package com.example.cmput301f20t18;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class EditProfileActivityTest {
    private final String NEW_USERNAME = "NOT_A_BOT!";
    private final String NEW_PHONE = "1234567890";

    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        RobotiumLoginManager.loginOwner(solo);
        solo.clickOnView(solo.getView(R.id.tab_profile));
        solo.clickOnText("Edit Account");
    }
    @Test
    public void start(){
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);
    }
    @Test
    public void changeDataCancel() {
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);

        EditText usernameInput = (EditText) solo.getView(R.id.username_input);
        EditText phoneInput = (EditText) solo.getView(R.id.phone_input);
        solo.clearEditText(usernameInput);
        solo.clearEditText(phoneInput);
        solo.enterText(usernameInput, NEW_USERNAME);
        solo.enterText(phoneInput, NEW_PHONE);

        solo.clickOnView(solo.getView(R.id.return_to_my_profile));

        assertTrue(solo.waitForText(RobotiumLoginManager.owner.getUsername()));
        assertTrue(solo.waitForText(RobotiumLoginManager.owner.getPhoneNum()));
    }
    @Test
    public void changeUsername(){
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);
        EditText usernameInput = (EditText) solo.getView(R.id.username_input);

        solo.clearEditText(usernameInput);
        solo.enterText(usernameInput, NEW_USERNAME);
        solo.clickOnButton("Done");
        assertTrue(solo.waitForText(NEW_USERNAME));

        solo.clickOnText("Edit Account");
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);

        usernameInput = (EditText) solo.getView(R.id.username_input);
        solo.clearEditText(usernameInput);
        solo.enterText(usernameInput, RobotiumLoginManager.owner.getUsername());
        solo.clickOnButton("Done");
        assertTrue(solo.waitForText(RobotiumLoginManager.owner.getUsername()));
    }
    @Test
    public void changePhone(){
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);

        EditText phoneInput = (EditText) solo.getView(R.id.phone_input);
        solo.clearEditText(phoneInput);
        solo.enterText(phoneInput, NEW_PHONE);
        solo.clickOnButton("Done");
        assertTrue(solo.waitForText(NEW_PHONE));

        solo.clickOnText("Edit Account");
        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfileActivity.class);

        phoneInput = (EditText) solo.getView(R.id.phone_input);
        solo.clearEditText(phoneInput);
        solo.enterText(phoneInput, RobotiumLoginManager.owner.getPhoneNum());
        solo.clickOnButton("Done");
        assertTrue(solo.waitForText(RobotiumLoginManager.owner.getPhoneNum()));
    }
//    @Test
//    public void changeAddress(){
//        solo.assertCurrentActivity("Wrong Activity - NOT EDITPROFILE", EditProfile.class);
//
//        solo.clickOnButton("Select New Address");
//        solo.assertCurrentActivity("Wrong Activity - NOT SELECTLOCATIONACTIVITY",
//                SelectLocationActivity.class);
//    }

}
