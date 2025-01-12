package com.example.rappelapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class EditRappelActivity extends AppCompatActivity {

    private EditText editTextTitre, editTextDescription, editTextHeure;
    private Button btnSaveRappel;
    private String selectedToneUri;
    private PreferencesManager preferencesManager;
    private long rappelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rappel);

        editTextTitre = findViewById(R.id.editTextTitre);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextHeure = findViewById(R.id.editTextHeure);
        btnSaveRappel = findViewById(R.id.btnSaveRappel);
        Button btnSelectTone = findViewById(R.id.btnSelectTone);

        preferencesManager = new PreferencesManager(this);

        Intent intent = getIntent();
        rappelId = intent.getLongExtra("rappel_id", -1);
        String titre = intent.getStringExtra("titre");
        String description = intent.getStringExtra("description");
        String heure = intent.getStringExtra("heure");
        selectedToneUri = intent.getStringExtra("sonnerie");

        editTextTitre.setText(titre);
        editTextDescription.setText(description);
        editTextHeure.setText(heure);

        btnSelectTone.setOnClickListener(v -> {
            Intent toneIntent = new Intent(EditRappelActivity.this, TonesActivity.class);
            startActivityForResult(toneIntent, 1);
        });

        btnSaveRappel.setOnClickListener(v -> {
            String titreModifie = editTextTitre.getText().toString();
            String descriptionModifie = editTextDescription.getText().toString();
            String heureStr = editTextHeure.getText().toString();

            if (titreModifie.isEmpty() || descriptionModifie.isEmpty() || heureStr.isEmpty() || selectedToneUri == null) {
                Toast.makeText(EditRappelActivity.this, "Veuillez remplir tous les champs et choisir une sonnerie", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date date = format.parse(heureStr);

                long currentTime = System.currentTimeMillis();
                long calculatedHeure = date != null ? date.getTime() : currentTime;

                if (date != null) {
                    Date today = new Date(currentTime);
                    Date fullDate = new Date(today.getYear(), today.getMonth(), today.getDate(),
                            date.getHours(), date.getMinutes());
                    calculatedHeure = fullDate.getTime();

                    if (calculatedHeure < currentTime) {
                        calculatedHeure += 24 * 60 * 60 * 1000;
                    }
                }

                final long heureFinal = calculatedHeure;
                Rappel rappelModifie = new Rappel(titreModifie, descriptionModifie, heureFinal, true, selectedToneUri);
                rappelModifie.setId(rappelId);

                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(EditRappelActivity.this);
                    db.rappelDao().update(rappelModifie);

                    scheduleAlarm(rappelModifie);

                    runOnUiThread(() -> {
                        Toast.makeText(EditRappelActivity.this, "Rappel modifié", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditRappelActivity.this, MainActivity.class));
                        finish();
                    });
                }).start();

            } catch (Exception e) {
                Toast.makeText(EditRappelActivity.this, "Format de l'heure incorrect", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void scheduleAlarm(Rappel rappel) {
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        alarmIntent.putExtra("titre", rappel.getTitre());
        alarmIntent.putExtra("sonnerieUri", rappel.getSonnerieUri());

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this,
                (int) rappel.getHeure(),
                alarmIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    rappel.getHeure(),
                    pendingIntent
            );
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            selectedToneUri = data.getStringExtra("selectedToneUri");
            preferencesManager.saveAlarmTone(selectedToneUri);
        }
    }
}
