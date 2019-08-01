package com.adhityakuncoro.wordpress.squidscan.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.adhityakuncoro.wordpress.squidscan.R;

public class SettingInisialisasi extends PreferenceFragment {

    private final static String beep = "beep";
    private final static String frontCamera = "frontCamera";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }

}
