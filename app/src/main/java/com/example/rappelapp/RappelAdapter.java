package com.example.rappelapp;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RappelAdapter extends RecyclerView.Adapter<RappelAdapter.RappelViewHolder> {
    private List<Rappel> rappels;
    private Context context;

    public RappelAdapter(Context context, List<Rappel> rappels) {
        this.context = context;
        this.rappels = rappels;
    }

    @NonNull
    @Override
    public RappelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rappel, parent, false);
        return new RappelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RappelViewHolder holder, int position) {
        Rappel rappel = rappels.get(position);
        holder.titreTextView.setText(rappel.getTitre());
        holder.descriptionTextView.setText(rappel.getDescription());

        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        String heureStr = format.format(new Date(rappel.getHeure()));
        holder.heureTextView.setText(heureStr);

        String sonnerieUriString = rappel.getSonnerieUri();
        if (sonnerieUriString != null) {
            Uri sonnerieUri = Uri.parse(sonnerieUriString);
            String sonnerieName = getRingtoneTitle(sonnerieUri);
            holder.sonnerieTextView.setText("Sonnerie : " + sonnerieName);
        }

        holder.btnEditRappel.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditRappelActivity.class);

            intent.putExtra("rappel_id", rappel.getId());
            intent.putExtra("titre", rappel.getTitre());
            intent.putExtra("description", rappel.getDescription());
            intent.putExtra("heure", new SimpleDateFormat("HH:mm").format(new Date(rappel.getHeure())));
            intent.putExtra("sonnerie", rappel.getSonnerieUri());

            context.startActivity(intent);
        });

        holder.btnDelete.setOnClickListener(v -> {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                db.rappelDao().delete(rappels.get(position));
                rappels.remove(position);

                ((MainActivity) context).runOnUiThread(() -> notifyItemRemoved(position));
            }).start();
        });
    }


    @Override
    public int getItemCount() {
        return rappels.size();
    }

    public Rappel getRappelAt(int position) {
        return rappels.get(position);
    }

    public void removeRappel(int position) {
        rappels.remove(position);
        notifyItemRemoved(position);
    }

    private String getRingtoneTitle(Uri uri) {
        Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        return ringtone.getTitle(context);
    }

    public void addRappels(List<Rappel> newRappels) {
        int startPosition = rappels.size();
        rappels.addAll(newRappels);
        notifyItemRangeInserted(startPosition, newRappels.size());
    }

    static class RappelViewHolder extends RecyclerView.ViewHolder {
        TextView titreTextView;
        TextView descriptionTextView;
        TextView heureTextView;
        TextView sonnerieTextView;
        Button btnEditRappel;
        Button btnDelete;

        RappelViewHolder(View itemView) {
            super(itemView);
            titreTextView = itemView.findViewById(R.id.titreTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            heureTextView = itemView.findViewById(R.id.heureTextView);
            sonnerieTextView = itemView.findViewById(R.id.sonnerieTextView);
            btnEditRappel = itemView.findViewById(R.id.btnEditRappel);  // Initialiser btnEditRappel
            btnDelete = itemView.findViewById(R.id.btnDeleteRappel);
        }
    }


    public void clearRappels() {
        rappels.clear();
        notifyDataSetChanged();
    }
}
