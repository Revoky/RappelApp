package com.example.rappelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rappelapp.RappelAdapter;
import com.example.rappelapp.AppDatabase;
import com.example.rappelapp.Rappel;
import com.example.rappelapp.PreferencesManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RappelAdapter adapter;
    private ExecutorService executorService;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        preferencesManager = new PreferencesManager(this);  // Initialisation

        TextView alarmToneText = findViewById(R.id.alarm_tone_text);
        String alarmTone = preferencesManager.getAlarmTone();
        alarmToneText.setText("Sonnerie actuelle : " + alarmTone);

        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(MainActivity.this);
                List<Rappel> rappels = db.rappelDao().getAllRappels();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter = new RappelAdapter(rappels);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous vraiment quitter l'application ?")
                .setPositiveButton("Oui", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Non", null)
                .show();
    }

    executorService.execute(new Runnable() {
        @Override
        public void run() {
            AppDatabase db = AppDatabase.getInstance(MainActivity.this);
            // Ajout temporaire de rappels pour tester
            Rappel rappel1 = new Rappel("Rappel 1", "Description du rappel 1", System.currentTimeMillis(), true);
            Rappel rappel2 = new Rappel("Rappel 2", "Description du rappel 2", System.currentTimeMillis() + 3600000, false);
            db.rappelDao().insert(rappel1);
            db.rappelDao().insert(rappel2);

            List<Rappel> rappels = db.rappelDao().getAllRappels();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter = new RappelAdapter(rappels);
                    recyclerView.setAdapter(adapter);
                }
            });
        }
    });
}
