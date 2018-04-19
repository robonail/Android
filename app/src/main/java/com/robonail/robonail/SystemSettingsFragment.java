package com.robonail.robonail;

import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.RingtonePreference;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Michael on 7/9/2017.
 */

public class SystemSettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String Tag = "SystemSettingsFragment";

    View myView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.settings_preferences,container,false);
        //return super.onCreateView(inflater, container, savedInstanceState);

        //Intent intent = new Intent(getActivity(),SettingsActivity.class);
        //startActivity(intent);

        return myView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);
    }
    @Override
    public void onResume() {
        super.onResume();

        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
    {
        String val;

        //crazy code block because I don't know what datatype the perference is
        try{
            val = sharedPreferences.getString(key,null);
        }
        catch(ClassCastException exS){
            try {
                val = String.valueOf(sharedPreferences.getBoolean(key, false));
            }
            catch(ClassCastException exB){
                try {
                    val = String.valueOf(sharedPreferences.getInt(key, 0));
                }
                catch(ClassCastException exI) {
                    try {
                        val = String.valueOf(sharedPreferences.getFloat(key, 0.0f));
                    } catch (ClassCastException exF) {
                        val = "";
                    }
                }
            }
        }
        Log.i(Tag,"Preference changed:"+key+":"+val);
        //Handler handler = new Handler();
        String myURL = RobonailApplication.cmdURL //"http://192.168.4.100:80/cmd"
                + "?c=settings&v="+key+":"+val+";";

        new RetrieveHttp().execute(myURL);
    }

    /*
    @Override
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

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

            } else {*/
                // For all other preferences, set the summary to the value's
                // simple string representation.
                /*preference.setSummary(stringValue);
            //}
            return true;
        }
    };*/
}
