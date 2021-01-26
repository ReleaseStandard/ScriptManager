package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/*
 * Class that handle the return of alarm and start the task.
 */
public class AlarmReceiver extends BroadcastReceiver {
    public static Random r = new Random();
    public static int REQUEST_CODE = r.nextInt();

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
