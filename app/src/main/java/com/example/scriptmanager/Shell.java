package com.example.scriptmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

    public static String SUFFIX_LOG = ".log.txt";
    public static String SUFFIX_SCRIPT = ".txt";
    public static String SUFFIX_STATE = ".xml";

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

    public static String getLogPath(String script) {
        script = getAbsolutePath(script);
        return script + SUFFIX_LOG;
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
        Logger.log("Job execution : " + script + "\n   log=" + getLogPath(script));
        try {
            Process pr = Runtime.getRuntime().exec(new String[]{"sh","-c",". " + script + " >> " + getLogPath(script)});
            processes.add(pr);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    public void clearLog(String script) throws IOException {
        Runtime.getRuntime().exec(new String[]{"sh","-c","> "+getLogPath(script)});
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
    public static ArrayList<String> getJobsFromFilesystem() {
        ArrayList<String>l = new ArrayList();
        File directory = new File(Shell.internalStorage);
        File[] files = directory.listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Logger.debug(file.getAbsolutePath());
            String n = file.getName();
            Pattern p = Pattern.compile("([^/]+)" + Shell.SUFFIX_STATE + "$");
            Matcher m = p.matcher(n);
            if (m.matches()) {
                l.add(m.group(1));
            }
        }
        return l;
    }
}
