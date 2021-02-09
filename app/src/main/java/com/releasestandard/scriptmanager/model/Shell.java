package com.releasestandard.scriptmanager.model;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.releasestandard.scriptmanager.AlarmReceiver;
import com.releasestandard.scriptmanager.JavaEventsReceiver;
import com.releasestandard.scriptmanager.tools.CompatAPI;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/*
 * This class use {KornShellInterface,StorageManager} to provide interface with Java.
 * all JobView got a Shell associated with it.
 */
public class Shell {

    // The object is used by recursive alarms, so many processes can be setup
    private static List<Process> processes = new ArrayList<Process>();
    private static List<PendingIntent> intents = new ArrayList<PendingIntent>();

    public StorageManager sm = null;
    public KornShellInterface bi = null;

    public boolean eventReceiverRegistered = false;

    /**
     *  This is the shell for a given JobView (one line on screen).
     */
    public Shell(StorageManager sm) {
            this.sm = new StorageManager(sm);
            this.bi = new KornShellInterface(sm);
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
        if ( !eventReceiverRegistered ) {
            Logger.debug("[[eventReceiverRegistered]]");
            JavaEventsReceiver.listeners.add(this);
            eventReceiverRegistered = true;
        }

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

    /**
     * Execute script & cmd and return a Process
     * compat 1
     * @param script
     * @return
     */
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

    /**
     * Clear logs
     * compat 14
     * @throws IOException
     */
    public void clearLog() throws IOException { clearLog(this.sm.script_name); }
    public void clearLog(String logpath) throws IOException {
        Shell._execCmd("> "+logpath);
    }

    /**
     * compat 1
     * @param context
     * @param scriptname
     * @param sched
     * @return
     */
    public Integer scheduleScript(Context context, String scriptname, int sched[]) { return scheduleScript(context, scriptname, sched, false);}
    public Integer scheduleScript(Context context, String scriptname, int sched[], boolean immediate) {
        if ( ! intents.add(_scheduleScript(context,scriptname, sched,immediate)) ) {
            return -1;
        }
        return intents.size()-1;
    }
    /**
     * Schedule a script for execution.
     * compat 1
     */
    public static PendingIntent _scheduleScript(Context context, String scriptname, int sched[])  { return _scheduleScript(context,scriptname,sched,false);}
    public static PendingIntent _scheduleScript(Context context, String scriptname, int sched[], boolean immediate)  {
        // need to get the time here
        Calendar next = null;
        if (immediate) {
            next = TimeManager.getImmediate();
        }
        else {
            next = TimeManager.nextSched(sched);
        }

        Logger.log("[" + (new Integer(AlarmReceiver.REQUEST_CODE + 1)) + "] Script " + scriptname + " scheduled for " + next.getTime().toString());
        long t = next.getTimeInMillis();

        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("script",scriptname);
        intent.putExtra("sched",sched);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(context, AlarmReceiver.REQUEST_CODE++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        CompatAPI.setAlarmIntentTime(context,t,alarmIntent);

        return alarmIntent;
    }

    /**
     * compat 1
     */
    public boolean terminateProcess(Integer i) {
        if ( i < 0 || i >= processes.size()) {
            Logger.debug("terminateProcess : invalid index");
            return false;
        }
        processes.get(i).destroy();
        return true;
    }

    /**
     * compat 1
     * @param i
     * @return
     */
    public boolean terminateIntent(Integer i) {
        if ( i < 0 || i >= intents.size()) {
            Logger.debug("terminateProcess : invalid index");
            return false;
        }
        intents.get(i).cancel();
        return true;
    }

    /**
     * compat 1
     */
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
