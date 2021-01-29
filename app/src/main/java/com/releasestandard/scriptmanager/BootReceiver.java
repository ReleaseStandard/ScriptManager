package com.releasestandard.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.releasestandard.scriptmanager.controller.JobData;
import com.releasestandard.scriptmanager.model.Shell;
import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.tools.Logger;

public class BootReceiver extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                jd.readFromInternalStorage(context, f, true);
                if (jd.isStarted) {
                    Shell shell = new Shell(
                            new StorageManager(
                                context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath(),
                                context.getApplicationContext().getFilesDir().getAbsolutePath(),
                                jd.name_in_path));


                    Integer i = shell.scheduleScript(context, jd.name_in_path, jd.sched,!jd.isSchedulded);
                    if ( i != -1 ) {
                        jd.intents.add(i);
                    }

                }
                jd.writeState(context,f);
            }

        }
    }
}