package com.example.cmput301f20t18;

import android.content.Intent;

/**
 * Passes information from main activity to particular activities to handle results
 * @author deinum
 */
public interface fragmentListener {
     void onActivityResult(int requestCode, int resultCode, Intent data);
}
