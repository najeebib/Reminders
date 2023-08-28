package com.najeebi.reminders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderAlarmReceiver extends BroadcastReceiver {
    private String Title,Date;

    @Override
    public void onReceive(Context context, Intent intent) {
        Title = intent.getStringExtra("TITLE");
        Date = intent.getStringExtra("Date");
        //go to the alarm activity
        Intent i = new Intent(context, AlarmActivity.class);
        i.putExtra("TITLE", Title);
        i.putExtra("Date",Date);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);


    }
}
