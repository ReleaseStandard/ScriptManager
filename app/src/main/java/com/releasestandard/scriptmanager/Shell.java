package com.releasestandard.scriptmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/*
 * This class is a wrapper for an underlying shell : e.g. sh
 */
public class Shell {

    private List<Process> processes = new ArrayList<Process>();
    private List<PendingIntent> intents = new ArrayList<PendingIntent>();


    public StorageManager sm = null;
    public BashInterface bi = null;

    /**
     *  Constructor will build the storage required to store scripts.
     */
    public Shell(StorageManager sm) {
            this.sm = new StorageManager(sm);
            this.bi = new BashInterface(sm);
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

    /**
     * parameter could be the name of the script or an absolute path.
     * @param scriptname
     * @return
     */
    public int execScript(String scriptname) {
        sm.setScriptName(scriptname);
        EventsReceiver.listeners.add(this);
        String script_path = sm.getScriptAbsolutePath();
        String output = sm.getOutputAbsolutePath();

        output = bi.wrappScript(script_path,output);

        Logger.log("Job execution : " + output + "\n   log=" + sm.getLogAbsolutePath());
        Process p = _execScript(output,sm.getLogAbsolutePath());
        if ( p != null ) {
            processes.add(p);
            return 0;
        }
        return 1;
    }
    public static Process _execScript(String cmd) {
        return _execScript(cmd,"/dev/null");
    }
    public static Process _execScript(String cmd, String log) {
        try {
            return Runtime.getRuntime().exec(new String[]{"sh","-c",". " + cmd + " >> " + log+ " 2>&1"});

        } catch (IOException e) {
            return null;
        }
    }

    public void clearLog() throws IOException { clearLog(this.sm.script_name); }
    public void clearLog(String script) throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh","-c","> "+ sm.getLogAbsolutePath(script)});
    }

    public void scheduleJob(Context context, String script, int sched[]) {
        intents.add(_scheduleJob(context,script, sched));
    }

    public static PendingIntent _scheduleJob(Context context, String script, int sched[])  {
        // need to get the time here
        Calendar next = TimeManager.nextSched(sched);

        Logger.log("[" + (new Integer(AlarmReceiver.REQUEST_CODE + 1)) + "] Job " + script + " scheduled for " + next.getTime().toString());
        long t = next.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("script",script);
        intent.putExtra("sched",sched);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
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
        return alarmIntent;
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
