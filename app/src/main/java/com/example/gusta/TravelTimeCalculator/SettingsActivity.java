package com.example.gusta.TravelTimeCalculator;

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
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.support.v4.app.NavUtils;

//import com.example.gusta.traveltimecalculator.R;

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

    /**
     * Copies all parameters to mapsActivity
     * comparisonMode
     * currency
     * costEmissions
     * costTime
     * bicycleStartStopTime
     * drivingStartStopTime
     * drivingCostkm
     * drivingEmissions
     * transitCost
     * transitEmissions
     *
     */
    private static void updateSettingsToMain(){
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(MapsActivity.getAppContext());

        int comparisonMode=-1;
        TravelTimeHandler.settingComparisonMode = sharedPreferences.getInt("general_compare_mode", comparisonMode);
        String currency="";
        TravelTimeHandler.settingCurrency = sharedPreferences.getString("general_currency", currency);
        int costEmissions = -1;
        TravelTimeHandler.settingCostEmissions = sharedPreferences.getInt("general_cost_emission", costEmissions);
        int costTime = -1;
        TravelTimeHandler.settingCostTime = sharedPreferences.getInt("general_cost_time", costTime);
        int bicycleStartStopTime = -1;
        TravelTimeHandler.settingBicyclingStartStopTime = sharedPreferences.getInt("travel_mode_bicycling_start_stop", bicycleStartStopTime);
        int drivingStartStopTime = -1;
        TravelTimeHandler.settingDrivingStartStopTime = sharedPreferences.getInt("travel_mode_driving_start_stop", drivingStartStopTime);
        int drivingCostkm = -1;
        TravelTimeHandler.settingDrivingCostkm = sharedPreferences.getInt("travel_mode_driving_cost", drivingCostkm);
        int drivingEmissions = -1;
        TravelTimeHandler.settingDrivingEmission = sharedPreferences.getInt("travel_mode_driving_emissions", drivingEmissions);
        int transitCost = -1;
        TravelTimeHandler.settingTransitCost = sharedPreferences.getInt("travel_mode_transit_cost", transitCost);
        int transitEmissions = -1;
        TravelTimeHandler.settingTransitEmissions = sharedPreferences.getInt("travel_mode_transit_emissions", transitEmissions);

    }

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            Log.d("GustafTag", "Preference Changed! preference: " + preference.toString() );
            if (preference instanceof ListPreference) {
                Log.d("GustafTag", "Entering ListPreference with " + stringValue );
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);
                Log.d("GustafTag", "stringValue: "+ stringValue);
                if(stringValue == "Cost" || stringValue == "Distancce" || stringValue =="Duration") {
                    Log.d("GustafTag", "update with stringValue: ");
                    updateSettingsToMain();

                }
            } else if (preference instanceof EditTextPreference) {
                Log.d("GustafTag", "Entering EditTextPreference with " + stringValue );
                preference.setSummary(stringValue);

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                Log.d("GustafTag", "Set summary to: "+stringValue);
                preference.setSummary(stringValue);
            }
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
        if(preference != null){
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

            // Trigger the listener immediately with the preference's
            // current value.
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
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
                || TravelModePreferenceFragment.class.getName().equals(fragmentName);
    }
 //TODO Update the description of the settings to the current value
    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("example_text"));
            bindPreferenceSummaryToValue(findPreference("example_list"));
            bindPreferenceSummaryToValue(findPreference("general_compare_mode"));

            bindPreferenceSummaryToValue(findPreference("general_currency"));
            bindPreferenceSummaryToValue(findPreference("general_cost_emission"));
            bindPreferenceSummaryToValue(findPreference("general_cost_time"));

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
    public static class TravelModePreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_travelmode);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference("notifications_new_message_ringtone"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_bicycle_start_stop"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_driving_start_stop"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_driving_cost"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_driving_emissions"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_transit_cost"));
            bindPreferenceSummaryToValue(findPreference("travel_mode_transit_emissions"));
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
