package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.releasestandard.scriptmanager.controller.JobData;
import com.releasestandard.scriptmanager.model.Shell;
import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.model.TimeManager;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Random;

/*
 * Class that handle the return of alarm and start the task.
 */
// This receiver is not exported so we dont have to secure it.
public class AlarmReceiver extends BroadcastReceiver {

    public static int REQUEST_CODE = (new Random()).nextInt();

    /**
     * compat 8
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        String scriptname = intent.getStringExtra("script");
        int [] sched = intent.getIntArrayExtra("sched");

        Logger.debug("AlarmReceiver : scriptname="+scriptname+",sched="+ TimeManager.sched2str(sched));
        StorageManager sm = new StorageManager(context, scriptname);
        JobData jd = new JobData();
        sm.dump();
        InputStreamReader isr = StorageManager.getISR(context,sm.getStateFileNameInPath());
        if ( isr == null ) {
            Logger.debug("AlarmReceiver : isr is null");
        }
        Logger.debug(sm.getStateFileNameInPath());

        jd.readState(isr);
        jd.dump("\t");
        Shell s = new Shell(sm);
        Integer j = s.execScript(scriptname);
        if ( j != -1 ) {
            jd.processes.add(j);
        }
        // add listener (will be nulled at job stop) //
        jd.listeners.add(JavaEventsReceiver.listeners.size()-1);

        if ( jd.isSchedulded && TimeManager.isRepeated(sched)) {
            Integer i =s.scheduleScript(context,scriptname,sched);
            if ( i != -1) {
                jd.intents.add(i);
            }
        }
        try {
            isr.close();
        } catch (IOException e) {
            e.printStackTrace(Logger.getTraceStream());
        }

        OutputStreamWriter osw = StorageManager.getOSW(context,sm.getStateFileNameInPath());
        jd.writeState(osw);
        try {
            osw.flush();
            osw.close();
        } catch (IOException e) {
            e.printStackTrace(Logger.getTraceStream());
        }
    }

}
