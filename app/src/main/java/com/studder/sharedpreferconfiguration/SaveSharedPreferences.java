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

    public static void setFirstTimeLogin(Context context, boolean firstTimeLogin)
    {
        SharedPreferences.Editor editor = getPreferences(context).edit();
        editor.putBoolean(PreferencesUtility.FIRST_TIME_LOGIN_PREF, firstTimeLogin);
        editor.apply();
    }

    public static boolean getFirstTimeLogin(Context context)
    {
        return getPreferences(context).getBoolean(PreferencesUtility.FIRST_TIME_LOGIN_PREF, true);
    }

}
