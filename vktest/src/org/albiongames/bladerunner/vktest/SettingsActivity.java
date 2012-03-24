package org.albiongames.bladerunner.vktest;

/*
 * (c) Vladimir Lebedev-Schmidthof <dair@dair.spb.ru>
 * Saint Petersburg, Russia
 * Specially for Albion Games (http://albiongames.org)
 * 
 * 
 * License text:
 * 
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *                     Version 2, December 2004
 * 
 *  Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>
 * 
 *  Everyone is permitted to copy and distribute verbatim or modified
 *  copies of this license document, and changing it is allowed as long
 *  as the name is changed.
 * 
 *             DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION
 * 
 *   0. You just DO WHAT THE FUCK YOU WANT TO. 
 * 
 * http://sam.zoy.org/wtfpl/
 */


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