package com.example.cmput301f20t18;

import android.app.Activity;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class RegisterActivityTest {
    private Solo solo;
    @Rule
    public ActivityTestRule<Register> rule =
            new ActivityTestRule<>(Register.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }
    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }
    @Test
    public void EnterAccountInformation(){
        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);

        solo.enterText((EditText) solo.getView(R.id.username), "RegisterBot");
        solo.enterText((EditText) solo.getView(R.id.password), "RoboPass");
        solo.enterText((EditText) solo.getView(R.id.email), "RegisterBot@RegisterBot.bot");
        solo.enterText((EditText) solo.getView(R.id.phone), "9999999999");

        solo.clickOnButton("Choose an Address");
        solo.assertCurrentActivity("Wrong Activity (Should be SelectLocationActivity)",
                SelectLocationActivity.class);
        solo.clickOnScreen(100,100);
        solo.clickOnView(solo.getView(R.id.confirm_location_selected_button));

        solo.assertCurrentActivity("Wrong Activity - NOT REGISTER", Register.class);
        solo.clickOnButton("Sign Up");
    }
}
