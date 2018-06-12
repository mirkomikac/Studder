package com.studder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.studder.database.schema.UserTable;

import java.text.AttributedCharacterIterator;
import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    private static final String TAG = "SettingsActivityLOG";

    private static Context context;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            //take value and map it to property
            Log.i(TAG, value.toString());
            JsonObject user = new JsonObject();
            //when you get username from sharedpref, put it in so server can update...
            SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();

            String username = sp.getString(UserTable.Cols.USERNAME, "Unknown value");
            user.addProperty("username", username);
            if(preference instanceof EditTextPreference){
                Log.i(TAG, preference.getKey());
                switch (preference.getKey()){
                    case UserTable.Cols.NAME: {
                        user.addProperty("name", stringValue);
                        editor.putString(UserTable.Cols.NAME, stringValue);
                        break;
                    }
                    case UserTable.Cols.SURNAME: {
                        user.addProperty("surname", stringValue);
                        editor.putString(UserTable.Cols.SURNAME, stringValue);
                        break;
                    }
                    case UserTable.Cols.CITY: {
                        user.addProperty("city", stringValue);
                        editor.putString(UserTable.Cols.CITY,  stringValue);
                        break;
                    }
                    case UserTable.Cols.RADIUS: {
                        user.addProperty("radius", stringValue);
                        editor.putInt(UserTable.Cols.RADIUS, Integer.parseInt(stringValue));
                        break;
                    }
            }
            } else if(preference instanceof ListPreference){
                Log.i(TAG, preference.getKey());
                switch (preference.getKey()){
                    case UserTable.Cols.USER_GENDER : {
                        user.addProperty("userGender", stringValue);
                        editor.putString(UserTable.Cols.USER_GENDER, stringValue);
                        break;
                    }
                    case UserTable.Cols.SWIPE_THROW: {
                        user.addProperty("swipeThrow", stringValue);
                        editor.putString(UserTable.Cols.SWIPE_THROW, stringValue);
                        break;
                    }
                }
            }

            editor.apply();


            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            String ipConfig = context.getResources().getString(R.string.ipconfig);
            Ion.with(context)
                    .load("POST","http://"+ipConfig+"/users/update")
                    //.load("http://10.0.2.2:8080/users/update")
                    .setJsonObjectBody(user)
                    .asJsonObject()
                    .withResponse()
                    .setCallback(new FutureCallback<Response<JsonObject>>() {
                        @Override
                        public void onCompleted(Exception e, Response<JsonObject> result) {
                            if(result.getHeaders().code() == 200){
                                Log.i(TAG, "updated");
                            } else{
                                Log.e(TAG, "server response != 200");
                            }
                        }
                    });

            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        try{
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    context.getSharedPreferences("USER_INFO", MODE_PRIVATE)
                            .getString(preference.getKey(), ""));
        } catch(Exception e){
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    context.getSharedPreferences("USER_INFO", MODE_PRIVATE)
                            .getInt(preference.getKey(), -1));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        context = getApplicationContext();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (!super.onMenuItemSelected(featureId, item)) {
                NavUtils.navigateUpFromSameTask(this);
            }
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || MatchingPreferenceFragment.class.getName().equals(fragmentName)
                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || NotificationPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);
            String name = sp.getString(UserTable.Cols.NAME, "Unknown value");
            String surname = sp.getString(UserTable.Cols.SURNAME, "Unknown value");
            String userGender = sp.getString(UserTable.Cols.USER_GENDER, "Unknown value");
            String city = sp.getString(UserTable.Cols.CITY, "Unknown value");
            if(findPreference(UserTable.Cols.NAME) instanceof EditTextPreference) {
                EditTextPreference namePref = (EditTextPreference) findPreference(UserTable.Cols.NAME);
                namePref.setSummary(name);
                namePref.setText(name);
            }
            if(findPreference(UserTable.Cols.SURNAME) instanceof EditTextPreference) {
                EditTextPreference surnamePref = (EditTextPreference) findPreference(UserTable.Cols.SURNAME);
                surnamePref.setSummary(surname);
                surnamePref.setText(surname);
            }

            //AGE doesn't exist in preferences

            if(findPreference(UserTable.Cols.USER_GENDER) instanceof ListPreference) {
                ListPreference sexListPref = (ListPreference) findPreference(UserTable.Cols.USER_GENDER);
                //FEMAIL VS FEMALE.....

                if(userGender.equals("MALE")){
                    sexListPref.setValueIndex(0);
                    sexListPref.setSummary("MALE");
                }
                else if(userGender.equals("FEMAIL")){
                    sexListPref.setValueIndex(1);
                    sexListPref.setSummary("FEMALE");
                }
            }
            if(findPreference(UserTable.Cols.CITY) instanceof EditTextPreference){
                EditTextPreference cityPref = (EditTextPreference) findPreference(UserTable.Cols.CITY);
                cityPref.setSummary(city);
                cityPref.setText(city);
            }

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.NAME));
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.SURNAME));
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.USER_GENDER));
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.CITY));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //This fragment shows matching preferences only
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class MatchingPreferenceFragment extends PreferenceFragment{

        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            SharedPreferences sp = context.getSharedPreferences("USER_INFO", MODE_PRIVATE);



            Integer locationRadius = sp.getInt(UserTable.Cols.RADIUS, -1);
            String interestedIn = sp.getString(UserTable.Cols.SWIPE_THROW, "Unknown value");

            if(findPreference(UserTable.Cols.RADIUS) instanceof EditTextPreference){
                EditTextPreference radiusPref = (EditTextPreference) findPreference(UserTable.Cols.RADIUS);
                radiusPref.setSummary(locationRadius.toString());
                radiusPref.setText(locationRadius.toString());
            }

            if(findPreference(UserTable.Cols.SWIPE_THROW) instanceof  ListPreference){
                ListPreference interestedInPref = (ListPreference) findPreference(UserTable.Cols.SWIPE_THROW);
                if(interestedIn.equals("MALE")){
                    interestedInPref.setSummary("MALE");
                    interestedInPref.setValueIndex(0);
                }
                else if(interestedIn.equals("FEMAIL"))
                {
                    interestedInPref.setValueIndex(1);
                    interestedInPref.setSummary("FEMALE");
                }
            }

            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_matching);
            setHasOptionsMenu(true);

            //bind summaries to their values
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.RADIUS));
            bindPreferenceSummaryToValue(findPreference(UserTable.Cols.SWIPE_THROW));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_notification);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_data_sync);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("sync_frequency"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
