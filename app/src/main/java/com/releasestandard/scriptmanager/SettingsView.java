package com.releasestandard.scriptmanager;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.releasestandard.scriptmanager.tools.Logger;

public class SettingsView extends PreferenceFragmentCompat {

    private SharedPreferences.OnSharedPreferenceChangeListener listener = null;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings_fragment, rootKey);
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                applySettings();
            }
        };

        sp.registerOnSharedPreferenceChangeListener(listener);
    }

    public void applySettings() {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(getActivity());

        Logger.DEBUG = sp.getBoolean("preferences_developper_debug_mode",false);
        Logger.debug("debug_mode = "+(Logger.DEBUG?"true":"false"));
    }
}
