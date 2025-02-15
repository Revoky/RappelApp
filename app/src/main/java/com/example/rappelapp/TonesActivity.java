package com.example.rappelapp;

import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TonesActivity extends AppCompatActivity {
    private ToneAdapter toneAdapter;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tones);

        preferencesManager = new PreferencesManager(this);

        RecyclerView recyclerTones = findViewById(R.id.recyclerTones);
        recyclerTones.setLayoutManager(new LinearLayoutManager(this));

        List<Tone> toneList = loadTones();
        toneAdapter = new ToneAdapter(this, toneList);
        recyclerTones.setAdapter(toneAdapter);

        Button btnSaveTone = findViewById(R.id.btnSaveTone);
        btnSaveTone.setOnClickListener(v -> {
            Uri selectedTone = toneAdapter.getSelectedToneUri();
            if (selectedTone != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("selectedToneUri", selectedTone.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Veuillez sélectionner une tonalité", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private List<Tone> loadTones() {
        List<Tone> tones = new ArrayList<>();
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);

        for (int i = 0; i < ringtoneManager.getCursor().getCount(); i++) {
            Uri uri = ringtoneManager.getRingtoneUri(i);
            String title = ringtoneManager.getRingtone(i).getTitle(this);
            tones.add(new Tone(title, uri));
        }
        return tones;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (toneAdapter != null) {
            toneAdapter.releaseMediaPlayer();
        }
    }
}