package com.troubadour.troubadour.CustomClasses;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class InitAlarm {
    Context context;

    public InitAlarm(Context context) {
        this.context = context;
    }

    public void init() {
        boolean alarmActive = (PendingIntent.getBroadcast(context, 0,
                new Intent(context, AlarmReceiver.class),
                PendingIntent.FLAG_NO_CREATE) != null);

        if (!alarmActive) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 15, pendingIntent);
        }
    }
}
