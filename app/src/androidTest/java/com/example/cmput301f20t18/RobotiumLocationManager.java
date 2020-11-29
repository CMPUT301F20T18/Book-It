package com.example.cmput301f20t18;

import com.robotium.solo.Solo;

public class RobotiumLocationManager {
    public static void deleteAll(Solo solo){
        solo.assertCurrentActivity("Wrong Activity - NOT CHOOSELOCATIONACTIVITY",
                ChooseLocationActivity.class);
        while(solo.waitForText("Select", 1, 2000)){
            solo.clickOnView(solo.getView(R.id.button_delete_location));
        }
    }
}
