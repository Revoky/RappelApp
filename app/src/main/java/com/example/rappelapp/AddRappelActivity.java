package com.example.rappelapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddRappelActivity extends AppCompatActivity {

    private EditText editTextTitre, editTextDescription, editTextHeure;
    private Button btnSaveRappel;
    private String selectedToneUri;
    private PreferencesManager preferencesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rappel);

        editTextTitre = findViewById(R.id.editTextTitre);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextHeure = findViewById(R.id.editTextHeure);
        btnSaveRappel = findViewById(R.id.btnSaveRappel);
        Button btnSelectTone = findViewById(R.id.btnSelectTone);

        preferencesManager = new PreferencesManager(this);

        editTextHeure.addTextChangedListener(new TextWatcher() {
            private boolean isEditing = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Rien à faire ici
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Rien à faire ici
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (isEditing) return;
                isEditing = true;

                String input = s.toString();
                input = input.replace(":", "");
                if (input.length() >= 2) {
                    String hours = input.substring(0, 2);
                    String minutes = input.length() > 2 ? input.substring(2) : "";

                    boolean isValid = true;

                    try {
                        int hoursInt = Integer.parseInt(hours);
                        if (hoursInt < 0 || hoursInt > 23) {
                            isValid = false;
                            Toast.makeText(editTextHeure.getContext(), "Les heures doivent être entre 00 et 23", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        isValid = false;
                        Toast.makeText(editTextHeure.getContext(), "Heures invalides", Toast.LENGTH_SHORT).show();
                    }

                    if (!minutes.isEmpty()) {
                        try {
                            int minutesInt = Integer.parseInt(minutes);
                            if (minutesInt < 0 || minutesInt > 59) {
                                isValid = false;
                                Toast.makeText(editTextHeure.getContext(), "Les minutes doivent être entre 00 et 59", Toast.LENGTH_SHORT).show();
                            }
                        } catch (NumberFormatException e) {
                            isValid = false;
                            Toast.makeText(editTextHeure.getContext(), "Minutes invalides", Toast.LENGTH_SHORT).show();
                        }
                    }

                    if (!isValid) {
                        s.clear();
                    } else {
                        String formatted = hours + (minutes.isEmpty() ? "" : ":" + minutes);
                        editTextHeure.setText(formatted);
                        editTextHeure.setSelection(formatted.length());
                    }
                }
                isEditing = false;
            }
        });

        btnSelectTone.setOnClickListener(v -> {
            Intent intent = new Intent(AddRappelActivity.this, SettingsActivity.class);
            startActivityForResult(intent, 1);
        });

        btnSaveRappel.setOnClickListener(v -> {
            String titre = editTextTitre.getText().toString();
            String description = editTextDescription.getText().toString();
            String heureStr = editTextHeure.getText().toString();

            if (titre.isEmpty() || description.isEmpty() || heureStr.isEmpty() || selectedToneUri == null) {
                Toast.makeText(AddRappelActivity.this, "Veuillez remplir tous les champs et choisir une sonnerie", Toast.LENGTH_SHORT).show();
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
                Rappel rappel = new Rappel(titre, description, heureFinal, true, selectedToneUri);

                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(AddRappelActivity.this);
                    db.rappelDao().insert(rappel);

                    Intent alarmIntent = new Intent(AddRappelActivity.this, AlarmReceiver.class);
                    alarmIntent.putExtra("titre", titre);
                    alarmIntent.putExtra("sonnerieUri", selectedToneUri);

                    PendingIntent pendingIntent = PendingIntent.getBroadcast(
                            AddRappelActivity.this,
                            (int) heureFinal,
                            alarmIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                    );

                    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                    if (alarmManager != null) {
                        alarmManager.setExact(
                                AlarmManager.RTC_WAKEUP,
                                heureFinal,
                                pendingIntent
                        );
                    }

                    runOnUiThread(() -> {
                        Toast.makeText(AddRappelActivity.this, "Rappel ajouté", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(AddRappelActivity.this, MainActivity.class));
                        finish();
                    });
                }).start();

            } catch (Exception e) {
                Toast.makeText(AddRappelActivity.this, "Format de l'heure incorrect", Toast.LENGTH_SHORT).show();
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
