package com.example.instadam.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.instadam.R;

import java.util.Random;


public class AlarmReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "channel_01";

    /**
     * On receive of the pendingIntent send by the AlarmManager daily, we create a new notification
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        sendNotification(context);
    }

    /**
     * Create a notification channel, build a notification and send it.
     */
    private void sendNotification(Context context) {
        createNotificationChannel(context);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("InstaDAM")
                .setContentText("Venez découvrir les dernières photos autour de vous !")
                .setTicker("Nouvelle notification !")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        Random r = new Random();
        notificationManager.notify(r.nextInt(), builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name_instaDAM";
            String description = "channel_description_instaDAM";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}