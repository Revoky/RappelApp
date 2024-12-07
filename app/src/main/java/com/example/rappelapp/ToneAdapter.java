package com.example.rappelapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ToneAdapter extends RecyclerView.Adapter<ToneAdapter.ToneViewHolder> {
    private final Context context;
    private final List<Tone> toneList;
    private Uri selectedToneUri;
    private MediaPlayer mediaPlayer;

    public ToneAdapter(Context context, List<Tone> toneList) {
        this.context = context;
        this.toneList = toneList;
    }

    public Uri getSelectedToneUri() {
        return selectedToneUri;
    }

    @NonNull
    @Override
    public ToneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_tone, parent, false);
        return new ToneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToneViewHolder holder, int position) {
        Tone tone = toneList.get(position);
        holder.txtToneName.setText(tone.getName());

        // Ajouter un log pour suivre la sélection de la tonalité
        Log.d("ToneAdapter", "Binding tone: " + tone.getName() + " at position: " + position);

        holder.btnPlayTone.setOnClickListener(v -> playTone(tone.getUri()));

        holder.chkSelectTone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Log lorsque l'état de la case à cocher change
            Log.d("ToneAdapter", "Checkbox changed: " + isChecked + " for tone: " + tone.getName());

            if (isChecked) {
                // Sauvegarder l'URI de la tonalité sélectionnée
                selectedToneUri = tone.getUri();

                // Retarder la mise à jour après que la mise en page soit terminée
                holder.itemView.post(() -> {
                    // Ne pas appeler notifyDataSetChanged() pour éviter les problèmes de mise en page
                    // Utiliser notifyItemChanged() pour mettre à jour seulement l'élément
                    notifyItemChanged(position);
                });
            } else {
                // Si la case est décochée, supprimer la sélection
                selectedToneUri = null;
            }
        });

        // Vérifier si cette tonalité est déjà sélectionnée et mettre à jour l'UI
        holder.chkSelectTone.setChecked(selectedToneUri != null && selectedToneUri.equals(tone.getUri()));
    }

    @Override
    public int getItemCount() {
        return toneList.size();
    }

    private void playTone(Uri toneUri) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(context, toneUri);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release();
                mediaPlayer = null;
            });
        }
    }

    static class ToneViewHolder extends RecyclerView.ViewHolder {
        TextView txtToneName;
        Button btnPlayTone;
        CheckBox chkSelectTone;

        public ToneViewHolder(@NonNull View itemView) {
            super(itemView);
            txtToneName = itemView.findViewById(R.id.txtToneName);
            btnPlayTone = itemView.findViewById(R.id.btnPlayTone);
            chkSelectTone = itemView.findViewById(R.id.chkSelectTone);
        }
    }

    public void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}