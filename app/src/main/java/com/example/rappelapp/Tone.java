package com.example.rappelapp;

import android.net.Uri;

public class Tone {
    private final String name;
    private final Uri uri;

    public Tone(String name, Uri uri) {
        this.name = name;
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public Uri getUri() {
        return uri;
    }
}
