package com.example.cmput301f20t18;

import android.app.Activity;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class ChooseLocationTest {

    private static String DEMO_LOCATION = "";
    private Solo solo;
    @Rule
    public ActivityTestRule<Login> rule =
            new ActivityTestRule<>(Login.class, true, true);
    @Before
    public void setUp() throws Exception{
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        RobotiumLoginManager.loginOwner(solo);
        solo.clickOnView(solo.getView(R.id.tab_scan));

    }

    @Test
    public void start(){
        Activity activity = rule.getActivity();
    }

    @Test
    public void chooseLocationEditBooks () {
    }
}
