package com.releasestandard.scriptmanager.model;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.releasestandard.scriptmanager.AlarmReceiver;
import com.releasestandard.scriptmanager.SmsReceiver;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * This class is a wrapper for an underlying shell : e.g. sh
 */

public class Shell {

    private static List<Process> processes = new ArrayList<Process>();
    private static List<PendingIntent> intents = new ArrayList<PendingIntent>();

    public StorageManager sm = null;
    public KornShellInterface bi = null;

    /**
     *  This is the shell for a given JobView (one line on screen).
     */
    public Shell(StorageManager sm) {
            this.sm = new StorageManager(sm);
            this.bi = new KornShellInterface(sm);
            SmsReceiver.listeners.add(this);
    }

    /**
     * parameter could be the name of the script or an absolute path.
     * @param scriptname scriptname (last part of pathname)
     * @return index in the array of processes
     */
    public Integer execScript(String scriptname) {
        sm.setScriptName(scriptname);
        Logger.debug("<======== State before execScript =======>");
        sm.dump();
        Logger.debug("<=========                                      =========>");
        String output = sm.getOutputAbsolutePath();
        bi.wrappScript( sm.getScriptAbsolutePath(),output);

        Logger.log("Job execution : " + output + "\n   log=" + sm.getLogAbsolutePath());
        Process p = _execScript(output,sm.getLogAbsolutePath());
        if ( p != null ) {
            Logger.debug("Shell: Process has started");
            if ( ! processes.add(p) ) {
                return -1;
            }
            return processes.size()-1;
        }
        Logger.debug("Shell: Process failed to start");
        return -1;
    }
    public static Process _execScript(String script) {
        return _execScript(script,null);
    }
    public static Process _execScript(String script, String log) { return _execCmd(". "+script,log,true); }
    public static Process _execCmd(String cmd) {
        return _execCmd(cmd,null);
    }
    public static Process _execCmd(String cmd,String log) {
        return _execCmd(cmd,log,false);
    }
    public static Process _execCmd(String cmd, String log,boolean attachToRoot) {
        try {
            String real_cmd = cmd;
            if ( log != null) {
                if ( attachToRoot ) {
                    real_cmd=KornShellInterface.attachToRoot(
                                            KornShellInterface.outputToLog(cmd, log));
                } else {
                    real_cmd=KornShellInterface.outputToLog(cmd, log);
                }
            } else {
                if ( attachToRoot ) {
                    real_cmd=KornShellInterface.attachToRoot(cmd);
                }
            }
            Logger.debug("real_cmd="+real_cmd);
            return Runtime.getRuntime().exec(KornShellInterface.packIn(real_cmd));
        } catch (IOException e) {
            return null;
        }
    }
    public void clearLog() throws IOException { clearLog(this.sm.script_name); }
    public void clearLog(String logpath) throws IOException {
        Shell._execCmd("> "+logpath);
    }

    public Integer scheduleScript(Context context, String scriptname, int sched[]) {
        if ( ! intents.add(_scheduleScript(context,scriptname, sched)) ) {
            return -1;
        }
        return intents.size()-1;
    }

    public static PendingIntent _scheduleScript(Context context, String scriptname, int sched[])  {
        // need to get the time here
        Calendar next = TimeManager.nextSched(sched);

        Logger.log("[" + (new Integer(AlarmReceiver.REQUEST_CODE + 1)) + "] Script " + scriptname + " scheduled for " + next.getTime().toString());
        long t = next.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("script",scriptname);
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

    public boolean terminateProcess(Integer i) {
        if ( i < 0 || i >= processes.size()) {
            Logger.debug("terminateProcess : invalid index");
            return false;
        }
        processes.get(i).destroy();
        return true;
    }
    public boolean terminateIntent(Integer i) {
        if ( i < 0 || i >= intents.size()) {
            Logger.debug("terminateProcess : invalid index");
            return false;
        }
        intents.get(i).cancel();
        return true;
    }

    public void dump() { Logger.debug(dump("")); }
    public String dump(String offset) {
        return "" +
                offset + "Shell { \n" +
                offset + "\tprocesses=" + processes.size() + "\n" +
                offset + "\tintents=" + intents.size() + "\n" +
                bi.dump(offset + "\t" ) +
                sm.dump(offset + "\t") +
                offset + "}\n";
    }
}
