package com.example.scriptmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class StartAppOnBoot extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            // Here we need to start all Jobs that has been started

            Shell s = new Shell();
            s.execCmd("echo boot_time_done > /data/data/com.example.scriptmanager/done.txt");
        }

    }
}