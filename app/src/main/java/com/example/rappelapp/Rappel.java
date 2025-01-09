package com.example.rappelapp;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "rappels")
public class Rappel {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String titre;
    private String description;
    private long heure;
    private boolean actif;
    private String sonnerieUri;

    public Rappel(String titre, String description, long heure, boolean actif, String sonnerieUri) {
        this.titre = titre;
        this.description = description;
        this.heure = heure;
        this.actif = actif;
        this.sonnerieUri = sonnerieUri;
    }

    public String getSonnerieUri() {
        return sonnerieUri;
    }

    public void setSonnerieUri(String sonnerieUri) {
        this.sonnerieUri = sonnerieUri;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getHeure() {
        return heure;
    }

    public void setHeure(long heure) {
        this.heure = heure;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }
}