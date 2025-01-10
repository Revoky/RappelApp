package com.example.rappelapp;

import android.content.Context;
import android.icu.text.SimpleDateFormat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

public class FileManager {
    private static final String FILE_NAME = "alarm_logs.txt";

    public static void writeToFile(Context context, String fileName, String data) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String readFromFile(Context context, String fileName) {
        String data = "";
        try {
            FileInputStream fis = context.openFileInput(fileName);
            int character;
            StringBuilder stringBuilder = new StringBuilder();

            while ((character = fis.read()) != -1) {
                stringBuilder.append((char) character);
            }

            data = stringBuilder.toString();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;
    }

    public void saveRappelsToFile(Context context, List<Rappel> rappels) {
        StringBuilder data = new StringBuilder();

        for (Rappel rappel : rappels) {
            data.append("Titre: ").append(rappel.getTitre()).append("\n");
            data.append("Description: ").append(rappel.getDescription()).append("\n");
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            String heureStr = format.format(new Date(rappel.getHeure()));
            data.append("Heure: ").append(heureStr).append("\n\n");

        }

        FileManager.writeToFile(context, "rappels.txt", data.toString());
    }
}