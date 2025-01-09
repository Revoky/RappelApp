package com.example.rappelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

        preferencesManager = new PreferencesManager(this);

        executorService = Executors.newSingleThreadExecutor();

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                AppDatabase db = AppDatabase.getInstance(MainActivity.this);
                List<Rappel> rappels = db.rappelDao().getAllRappels();

                runOnUiThread(() -> {
                    adapter = new RappelAdapter(MainActivity.this, rappels);
                    recyclerView.setAdapter(adapter);

                    ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                        @Override
                        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                            return false;
                        }

                        @Override
                        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                            int position = viewHolder.getAdapterPosition();
                            Rappel rappel = adapter.getRappelAt(position);
                            AppDatabase db = AppDatabase.getInstance(MainActivity.this);
                            new Thread(() -> {
                                db.rappelDao().delete(rappel);
                                runOnUiThread(() -> {
                                    adapter.removeRappel(position);
                                    Toast.makeText(MainActivity.this, "Rappel supprimé", Toast.LENGTH_SHORT).show();
                                });
                            }).start();
                        }
                    });
                    itemTouchHelper.attachToRecyclerView(recyclerView);
                });
            }
        });

        findViewById(R.id.btnSettings).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
        });

        findViewById(R.id.btnAbout).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        });

        findViewById(R.id.btnAddRappel).setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, AddRappelActivity.class));
        });

        findViewById(R.id.btnDeleteAllRappels).setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(MainActivity.this);
                List<Rappel> rappels = db.rappelDao().getAllRappels();
                FileManager fileManager = new FileManager();
                fileManager.saveRappelsToFile(MainActivity.this, rappels);

                db.rappelDao().deleteAll();

                runOnUiThread(() -> {
                    adapter.clearRappels();
                    Toast.makeText(MainActivity.this, "Tous les rappels ont été supprimés et sauvegardés", Toast.LENGTH_SHORT).show();
                });
            }).start();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Récupérer la tonalité de l'alarme sauvegardée
        String alarmToneUri = preferencesManager.getAlarmTone();

        if (alarmToneUri != null) {
            Uri uri = Uri.parse(alarmToneUri);
            String toneName = uri.getLastPathSegment();

            if (toneName != null && toneName.contains(".")) {
                toneName = toneName.substring(0, toneName.lastIndexOf('.'));
            }

            // Afficher la tonalité sélectionnée
            TextView alarmToneText = findViewById(R.id.alarm_tone_text);
            alarmToneText.setText("Sonnerie actuelle : " + toneName);
        } else {
            TextView alarmToneText = findViewById(R.id.alarm_tone_text);
            alarmToneText.setText("Aucune sonnerie sélectionnée");
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous vraiment quitter l'application ?")
                .setPositiveButton("Oui", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Non", null)
                .show();
    }
}