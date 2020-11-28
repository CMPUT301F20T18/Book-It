package com.example.cmput301f20t18;

import android.app.Activity;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RegisterActivityTest {
    public static final String DEFAULT_USERNAME = "RegisterBot";
    public static final String DEFAULT_PASSWORD = "BotPass";
    public static final String DEFAULT_EMAIL = "RegisterBot@RegisterBot.botnet";
    public static final String DEFAULT_PHONE = "9999999999";

    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        solo.clickOnView((TextView) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);

    }
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }
    @Test
    public void enterAccountInformation(){
        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);

        solo.enterText((EditText) solo.getView(R.id.username), DEFAULT_USERNAME);
        solo.enterText((EditText) solo.getView(R.id.password), DEFAULT_PASSWORD);
        solo.enterText((EditText) solo.getView(R.id.email), DEFAULT_EMAIL);
        solo.enterText((EditText) solo.getView(R.id.phone), DEFAULT_PASSWORD);

        solo.clickOnButton("Choose an Address");
        solo.assertCurrentActivity("Wrong Activity (Should be SelectLocationActivity)",
                SelectLocationActivity.class);
        solo.clickOnScreen(100,100);
        solo.clickOnView(solo.getView(R.id.confirm_location_selected_button));

        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);
        solo.clickOnButton("Sign Up");
    }
    @Test
    public void switchToLogin(){
        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);
        solo.clickOnView(solo.getView(R.id.redirect_sign_in));
        solo.assertCurrentActivity("Wrong Activity - NOT LOGIN", Login.class);
    }
}
