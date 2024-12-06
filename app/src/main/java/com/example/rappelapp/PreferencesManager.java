package com.example.rappelapp;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {
    private static final String PREF_NAME = "AlarmAppPreferences";
    private static final String KEY_ALARM_TONE = "alarm_tone";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void setAlarmTone(String tone) {
        editor.putString(KEY_ALARM_TONE, tone);
        editor.apply();
    }

    public String getAlarmTone() {
        return sharedPreferences.getString(KEY_ALARM_TONE, "default_tone");
    }
}
