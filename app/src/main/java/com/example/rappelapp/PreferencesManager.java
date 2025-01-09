package com.example.rappelapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "RappelAppPreferences";
    private static final String KEY_ALARM_TONE = "alarm_tone";

    private SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveAlarmTone(String toneUri) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ALARM_TONE, toneUri);
        editor.apply();
    }

    public String getAlarmTone() {
        return sharedPreferences.getString(KEY_ALARM_TONE, null);
    }
}
