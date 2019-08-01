package com.adhityakuncoro.wordpress.squidscan.ui;

import android.os.Bundle;
import android.preference.PreferenceActivity;


public class SettingsActivity extends PreferenceActivity {

    private final static String TAG = "SettingsAcitivity";

    public SettingsActivity() {}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingInisialisasi()).commit();
    }

}
