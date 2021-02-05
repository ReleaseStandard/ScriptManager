package com.releasestandard.scriptmanager;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;

import com.releasestandard.scriptmanager.tools.CompatAPI;
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
                applySettings(prefs);
            }
        };

        // load current values into view
        sp.registerOnSharedPreferenceChangeListener(listener);
        CompatAPI.modifySettings(this);
        hideUnusedGroups();
    }

    private int getPreferenceCountShown(PreferenceGroup pg) {
        int c = 0;
        for( int i = 0; i < pg.getPreferenceCount(); i = i +1) {
            c += pg.getPreference(i).isShown()?1:0;
        }
        return c;
    }

    /**
     * Works only if at least one setting is defined in xml file
     *  (eg: used to hide when all settings of  given group are not usable)
     */
    private void hideUnusedGroups() {
        SharedPreferences sp =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        for(String k : sp.getAll().keySet()) {
            Preference pref = findPreference(k);
            if ( pref == null ) {
                continue;
            }
            PreferenceGroup pg = pref.getParent();
            if ( pg != null ) {
                if (getPreferenceCountShown(pg) == 0) {
                    pg.setVisible(false);
                }
            }
        }
    }

    public void applySettings() {
        SharedPreferences sp =
        PreferenceManager.getDefaultSharedPreferences(getActivity());
        applySettings(sp);
    }
    public void applySettings(SharedPreferences sp) {
        Resources r = getActivity().getResources();
        Boolean preferences_developper_debug_mode = sp.getBoolean(r .getString(R.string.preferences_developper_debug_mode), r .getBoolean(R.bool.preferences_developper_debug_mode));
        Logger.DEBUG = preferences_developper_debug_mode;
        Logger.debug(r .getString(R.string.preferences_developper_debug_mode) + "="+preferences_developper_debug_mode);

    }
}
