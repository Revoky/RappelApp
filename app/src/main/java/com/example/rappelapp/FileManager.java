package com.example.rappelapp;

import android.content.Context;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
            data.append("Heure: ").append(rappel.getHeure()).append("\n\n");
        }

        FileManager.writeToFile(context, "rappels.txt", data.toString());
    }
}