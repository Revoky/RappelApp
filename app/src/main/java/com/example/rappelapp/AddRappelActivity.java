package com.example.rappelapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddRappelActivity extends AppCompatActivity {

    private EditText editTextTitre, editTextDescription, editTextHeure;
    private Button btnSaveRappel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_rappel);

        editTextTitre = findViewById(R.id.editTextTitre);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextHeure = findViewById(R.id.editTextHeure);
        btnSaveRappel = findViewById(R.id.btnSaveRappel);

        btnSaveRappel.setOnClickListener(v -> {
            String titre = editTextTitre.getText().toString();
            String description = editTextDescription.getText().toString();
            String heureStr = editTextHeure.getText().toString();

            if (titre.isEmpty() || description.isEmpty() || heureStr.isEmpty()) {
                Toast.makeText(AddRappelActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date date = format.parse(heureStr);
                long heure = date != null ? date.getTime() : System.currentTimeMillis();

                Rappel rappel = new Rappel(titre, description, heure, true);

                new Thread(() -> {
                    AppDatabase db = AppDatabase.getInstance(AddRappelActivity.this);
                    db.rappelDao().insert(rappel);

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
}
