package com.example.rappelapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "AlarmPreferences";
    private static final String ALARM_TONE_KEY = "alarm_tone";

    private final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void setAlarmTone(String uri) {
        sharedPreferences.edit().putString(ALARM_TONE_KEY, uri).apply();
    }

    public String getAlarmTone() {
        return sharedPreferences.getString(ALARM_TONE_KEY, null);
    }
}
