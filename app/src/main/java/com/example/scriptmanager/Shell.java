package com.example.scriptmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is a wrapper for an underlying shell : e.g. sh
 */
public class Shell {

    static public String externalStorage = null;
    static public String internalStorage = null;
    private List<Process> processes = new ArrayList<Process>();
    private List<PendingIntent> intents = new ArrayList<PendingIntent>();

    /**
     *  Constructor will build the storage required to store scripts.
     */
    public Shell(String internalStorage, String scriptStorage) {
            Shell.externalStorage = scriptStorage;
            Shell.internalStorage = internalStorage;
        }
        /**
         *  We considere at  this step that the new Shell(String,String) has already be called
         */
        public Shell() {

        }

    /**
     *  Run one time
     * @param cmd
     * @return
     */
    public int execCmd(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh","-c",cmd});
            processes.add(p);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    public static String getAbsolutePath(String script) {
        Pattern p = Pattern.compile("^/");
        Matcher m = p.matcher(script);
        if (!m.find()) {
            script = externalStorage + "/" + script;
        }
        return script;
    }
    /**
     * parameter could be the name of the script or an absolute path.
     * @param script
     * @return
     */
    public int execScript(String script) {
        script = getAbsolutePath(script);
        try {
            Process pr = Runtime.getRuntime().exec(new String[]{"sh","-c",". "+script});
            processes.add(pr);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    public void scheduleJob(Context context, String script, int sched[]) {

        // need to get the time here
        Calendar next = JobFragment.nextSched(sched);

        Log.v("scriptmanager","Job " + script + " scheduled for " + next.getTime().toString());
        long t = next.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("script",script);
        intent.putExtra("sched",sched);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT < 23) {
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, t, alarmIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, t, alarmIntent);
            }
        } else {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, t, alarmIntent);
            }
        intents.add(alarmIntent);
    }

    public void terminateAll() {
        for(Process p : processes) {
            p.destroy();
        }
        processes.clear();
        for(PendingIntent p : intents) {
            p.cancel();
        }
        intents.clear();
    }
}
