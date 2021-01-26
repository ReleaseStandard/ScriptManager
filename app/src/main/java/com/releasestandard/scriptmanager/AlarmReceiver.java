package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;

/*
 * Class that handle the return of alarm and start the task.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static int REQUEST_CODE = 0; // 2^32 Alarms avant d'avoir des problemes (ex: besoin de redémarrer l'application)
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar next = new GregorianCalendar();
        String script = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        Shell._execScript(script);

        if ( TimeManager.isRepeated(sched)) {
            Shell._scheduleJob(context,script,sched);
        }
    }
}
