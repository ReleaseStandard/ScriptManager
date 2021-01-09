package com.example.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmReceiver extends BroadcastReceiver {
    public static int REQUEST_CODE = 0; // 2^32 Alarms avant d'avoir des problemes (ex: besoin de redémarrer l'application)
    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar next = new GregorianCalendar();
        Shell shell = new Shell();
        String script = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        Log.v("scriptmanager","Job execution : " + script);

        shell.execScript(script);

        Log.v("scriptmanager","> " + script + " A");
        if ( JobFragment.isRepeated(sched)) {
            Log.v("scriptmanager","> " + script + " B");
            shell.scheduleJob(context,script,sched);
        }
    }
}
