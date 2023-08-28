package com.najeebi.reminders;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class ReminderNotificationReceiver extends BroadcastReceiver {
    private String Title;
    private int alarmId;

    @Override
    public void onReceive(Context context, Intent intent) {

        //get title and id from previous activity
        Title = intent.getStringExtra("TITLE");
        alarmId = intent.getIntExtra("NUM", -1);
        //send a notification when the time is right
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"Notification_reminder")
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(Title)
                .setContentText("Times up!!!")
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(alarmId,builder.build());

    }
}
