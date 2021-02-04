package com.releasestandard.scriptmanager.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.DocumentsContract;

import androidx.core.app.AlarmManagerCompat;

import com.releasestandard.scriptmanager.MainActivity;
import com.releasestandard.scriptmanager.tools.Logger;

/**
 * Handle API differences in Android.
 */
public class CompatAPI {

    /**
     * Open a file or directory
     * compat 19
     */
    public static boolean openDocument(MainActivity main) { return openDocument(main,null); }
    public static boolean openDocument(MainActivity main, String selectedUri) {
        if (Build.VERSION.SDK_INT >= 19) {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("text/*");
            if ( selectedUri != null ) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, selectedUri);
            }
            main.startActivityForResult(intent, main.ACTIVITY_REQUEST_CODE_IMPORT);
            return true;
        } else {  Logger.unsupported(19); }
        return false;
    }

    /**
     * Handle alarm set time.
     * compat 1
     */
    public static boolean setAlarmIntentTime(Context context, long t, PendingIntent alarmIntent) {
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
        return true;
    }
}
