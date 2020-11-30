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

public class LoginActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    @Test
    public void start(){
        Activity activity = rule.getActivity();
    }
    @Test
    public void login(){
        RobotiumUser owner = RobotiumLoginManager.owner;

        solo.assertCurrentActivity("Wrong Activity - NOT LOGIN", Login.class);
        solo.enterText((EditText)solo.getView(R.id.username), owner.getEmail());
        solo.enterText((EditText)solo.getView(R.id.password), owner.getPassword());

        solo.clickOnButton("Sign in");

        solo.assertCurrentActivity("Wrong Activity - NOT HOMESCREEN", HomeScreen.class);
    }
    @Test
    public void switchToRegister(){
        solo.assertCurrentActivity("Wrong Activity - NOT LOGIN", Login.class);
        solo.clickOnView((TextView) solo.getView(R.id.sign_up));
        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);
    }
}
