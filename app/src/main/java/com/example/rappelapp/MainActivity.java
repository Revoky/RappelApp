package com.example.rappelapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Voulez-vous vraiment quitter l'application ?")
                .setPositiveButton("Oui", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Non", null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull android.view.MenuItem item) {
        if (item.getItemId() == R.id.menu_add_rappel) {
            startActivity(new Intent(MainActivity.this, AddRappelActivity.class));
            return true;
        }
        else if (item.getItemId() == R.id.menu_delete_all_rappels) {
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
            return true;
        }
        else if (item.getItemId() == R.id.menu_about) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}