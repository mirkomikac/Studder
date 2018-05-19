package com.studder.sharedpreferconfiguration;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SaveSharedPreferences {

    static SharedPreferences getPreferences(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public static void setLoggedIn(Context context, boolean loggedIn){
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(PreferencesUtility.LOGGED_IN_PREF, loggedIn);
        editor.apply();
    }

    public static boolean getLoggedIn(Context context){
        return getPreferences(context).getBoolean(PreferencesUtility.LOGGED_IN_PREF, false);
    }

}
