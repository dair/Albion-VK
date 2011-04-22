package org.albiongames.bladerunner.vktest;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by IntelliJ IDEA.
 * User: dair
 * Date: 4/22/11
 * Time: 7:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class SettingsActivity extends PreferenceActivity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}