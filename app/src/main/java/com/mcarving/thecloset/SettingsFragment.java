package com.mcarving.thecloset;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

import com.mcarving.thecloset.data.MyPreferences;
import com.mcarving.thecloset.retrofitWeather.WeatherInfoService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// Fragment to read/edit zipcode inforamtion,
// sets up listener to start WeatherInfoService if zipcoe is changed
public class SettingsFragment extends PreferenceFragmentCompat implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();

        preference.setSummary(stringValue);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);

            String value = sharedPreferences.getString(p.getKey(), "");
            setPreferenceSummary(p, value);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();

        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_zipcode_key))) {

            // value contains only digits and it has only 5 digits as zipcode
            String value = sharedPreferences.getString(
                    getString(R.string.pref_zipcode_key), "");

            String regex = "^[0-9]{5}";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(value);
            if (!matcher.matches()) {
                Utils.showToast(getActivity(), "Invalid ZIP code! please try again.");
            } else {
                //get the new zipcode, updage the weather information string
                //Utils.showToast(getActivity(), "onSharedPreferenceChanged");
                MyPreferences.setCurrentDate(getActivity(), null);
                WeatherInfoService.startWeatherInfoService(getActivity().getApplicationContext());
            }
        }

        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }

    }
}
