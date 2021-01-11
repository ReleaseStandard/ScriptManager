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
        Shell shell = new Shell();
        String script = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        shell.execScript(script);

        if ( TimeManager.isRepeated(sched)) {
            shell.scheduleJob(context,script,sched);
        }
    }
}
