package com.example.rappelapp;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        TextView titreTextView = findViewById(R.id.titreTextView);
        Button stopButton = findViewById(R.id.btnStopAlarm);

        String titre = getIntent().getStringExtra("titre");
        String sonnerieUri = getIntent().getStringExtra("sonnerieUri");

        titreTextView.setText(titre);

        if (sonnerieUri != null) {
            mediaPlayer = MediaPlayer.create(this, Uri.parse(sonnerieUri));
            if (mediaPlayer != null) {
                mediaPlayer.setLooping(true);
                mediaPlayer.start();
            }
        }

        stopButton.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
