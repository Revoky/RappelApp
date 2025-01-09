package com.example.rappelapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            new Thread(() -> {
                AppDatabase db = AppDatabase.getInstance(context);
                List<Rappel> rappels = db.rappelDao().getAllRappels();

                for (Rappel rappel : rappels) {
                    if (rappel.isActif()) {
                        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
                        alarmIntent.putExtra("titre", rappel.getTitre());
                        alarmIntent.putExtra("sonnerieUri", rappel.getSonnerieUri());

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context,
                                (int) rappel.getHeure(),
                                alarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        if (alarmManager != null) {
                            alarmManager.setExact(
                                    AlarmManager.RTC_WAKEUP,
                                    rappel.getHeure(),
                                    pendingIntent
                            );
                        }
                    }
                }
            }).start();
        }
    }
}
