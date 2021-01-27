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
        String scriptname = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        StorageManager sm = new StorageManager(context.getFilesDir().getAbsolutePath(), context.getExternalFilesDir(null).getAbsolutePath());
        Shell s = new Shell(sm);
        s.execScript(scriptname);

        if ( TimeManager.isRepeated(sched)) {
            s.scheduleJob(context,scriptname,sched);
        }
    }
}
