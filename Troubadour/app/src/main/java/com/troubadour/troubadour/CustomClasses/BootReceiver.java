package com.troubadour.troubadour.CustomClasses;

import android.app.*;
import android.content.*;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        InitAlarm alarm = new InitAlarm(context);
        alarm.init();
        Log.d("Boot Received", "TROUBADOOBADO");
    }
}

