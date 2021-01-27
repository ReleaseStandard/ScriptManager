package com.releasestandard.scriptmanager;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import java.util.ArrayList;

public class StartAppOnBoot extends BroadcastReceiver {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            Logger.debug("[StartAppOnBoot] : onReceive boot signal received, starting the service");

            for ( String f : context.fileList())  {
                JobData jd = new JobData();
                jd.readFromInternalStorage(context, f);
                if (jd.isSchedulded && jd.isStarted) {
                    Shell shell = new Shell(
                            new StorageManager(
                                context.getApplicationContext().getFilesDir().getAbsolutePath(),
                                context.getApplicationContext().getExternalFilesDir(null).getAbsolutePath(),
                                jd.name_in_path));


                    shell.scheduleJob(context, jd.name_in_path, jd.sched);
                }
            }

        }
    }
}