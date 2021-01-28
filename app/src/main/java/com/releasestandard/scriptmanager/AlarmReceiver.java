package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.sql.Time;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;

/*
 * Class that handle the return of alarm and start the task.
 */
// This receiver is not exported so we dont have to secure it.
public class AlarmReceiver extends BroadcastReceiver {

    public static int REQUEST_CODE = (new Random()).nextInt();

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar next = new GregorianCalendar();
        String scriptname = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        Logger.debug("AlarmReceiver : scriptname="+scriptname+",sched="+ TimeManager.sched2str(sched));
        StorageManager sm = new StorageManager(context.getFilesDir().getAbsolutePath(), context.getExternalFilesDir(null).getAbsolutePath());
        Shell s = new Shell(sm);
        s.execScript(scriptname);

        if ( TimeManager.isRepeated(sched)) {
            s.scheduleJob(context,scriptname,sched);
        }
    }
}
