package com.example.rappelapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rappels")
public class Rappel {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String titre;
    public String description;
    public long heure;
    public boolean actif;

    public Rappel(String titre, String description, long heure, boolean actif) {
        this.titre = titre;
        this.description = description;
        this.heure = heure;
        this.actif = actif;
    }
}
