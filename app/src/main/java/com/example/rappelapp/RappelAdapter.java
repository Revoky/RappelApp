package com.example.rappelapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rappelapp.R;
import com.example.rappelapp.Rappel;

import java.util.List;

public class RappelAdapter extends RecyclerView.Adapter<RappelAdapter.RappelViewHolder> {
    private List<Rappel> rappels;

    public RappelAdapter(List<Rappel> rappels) {
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
        holder.titreTextView.setText(rappel.titre);
        holder.descriptionTextView.setText(rappel.description);
    }

    @Override
    public int getItemCount() {
        return rappels.size();
    }

    static class RappelViewHolder extends RecyclerView.ViewHolder {
        TextView titreTextView;
        TextView descriptionTextView;

        RappelViewHolder(View itemView) {
            super(itemView);
            titreTextView = itemView.findViewById(R.id.titreTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}
