package com.namlongsolutions.medicare.fragments;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.namlongsolutions.medicare.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class SettingActivityFragment extends PreferenceFragment {

    public SettingActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
