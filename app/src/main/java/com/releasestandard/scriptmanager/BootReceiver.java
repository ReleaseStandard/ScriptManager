package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.releasestandard.scriptmanager.controller.JobData;
import com.releasestandard.scriptmanager.model.Shell;
import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class BootReceiver extends BroadcastReceiver {
    /**
     * compat 8
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        // This is a privileged intent, we don't have to secure it.
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Logger.debug("[StartAppOnBoot] : onReceive boot signal received, starting the service");

            for ( String f : context.fileList())  {
                if ( ! StorageManager.isStateFile(f) ) {
                    continue;
                }
                Logger.debug("statefile found : " + f);
                JobData jd = new JobData();
                InputStreamReader isr = StorageManager.getISR(context,f);
                jd.readState(isr, true);
                jd.name_in_path = StorageManager.removeSuffix(StorageManager.getTerminalPart(f));
                jd.dump();
                if (jd.isStarted) {
                    Shell shell = new Shell(new StorageManager(context, jd.name_in_path));
                    Logger.debug(jd.name_in_path + " is null");
                    Integer i = shell.scheduleScript(context, jd.name_in_path, jd.sched,!jd.isSchedulded);
                    if ( i != -1 ) {
                        jd.intents.add(i);
                    }

                }
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace(Logger.getTraceStream());
                }
                OutputStreamWriter osw = StorageManager.getOSW(context,f);
                jd.writeState(osw);
                try {
                    osw.flush();
                    osw.close();
                } catch (IOException e) {
                    e.printStackTrace(Logger.getTraceStream());
                }
            }

        }
    }
}