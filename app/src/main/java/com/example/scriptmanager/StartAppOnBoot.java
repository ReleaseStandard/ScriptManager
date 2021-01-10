package com.example.scriptmanager;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class StartAppOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // Here we need to start all Jobs that has been started

            ArrayList<String> file_jobs_names = Shell.getJobsFromFilesystem();
            for(String fjn : file_jobs_names) {
                    JobData jd = new JobData();
                    jd.readFromInternalStorage(context,fjn);
                    if ( jd.isSchedulded && jd.isStarted) {
                        String path = Shell.getAbsolutePath(jd.name + Shell.SUFFIX_SCRIPT);
                        PendingIntent pi = Shell._scheduleJob(context,path,jd.sched);

                        // write the pending intent
                    }
            }
        }

    }
}