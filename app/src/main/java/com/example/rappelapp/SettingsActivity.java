package com.example.rappelapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rappelapp.PreferencesManager;

public class SettingsActivity extends AppCompatActivity {
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        preferencesManager = new PreferencesManager(this);

        findViewById(R.id.btnSaveTone).setOnClickListener(v -> {
            preferencesManager.setAlarmTone("Default Tone");
            Toast.makeText(this, "Tonalité sauvegardée", Toast.LENGTH_SHORT).show();
        });
    }
}
