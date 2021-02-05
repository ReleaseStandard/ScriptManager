package com.releasestandard.scriptmanager.tools;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import androidx.preference.PreferenceFragmentCompat;

import com.releasestandard.scriptmanager.MainActivity;

import java.io.File;

/**
 * Handle API differences in Android.
 */
public class CompatAPI {

    /**
     * Open a file or directory
     * compat 1
     */
    public static boolean openDocument(MainActivity main) { return openDocument(main,null); }
    public static boolean openDocument(MainActivity main, String selectedUri) {
            Intent intent;
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("text/*");
            if ( Build.VERSION.SDK_INT >= 26 && selectedUri != null ) {
                intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, selectedUri);
            }
            main.startActivityForResult(intent, main.ACTIVITY_REQUEST_CODE_IMPORT);
            return true;
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

    /**
     * Modify settings when features are not avaliable.
     */
    public static boolean modifySettings(PreferenceFragmentCompat settings) {
        Resources r = settings.getContext().getResources();
        return true;
    }
    /**
     * compat 1
     *  Return a path to external storage or null, if unmounted or doesn't exists.
     */
    public static String getExternalStorage(Context ctx) {
        Logger.debug("getExternalStorage decision:");
        String state = Environment.getExternalStorageState();
        String bad_states[] = new String[]{Environment.MEDIA_REMOVED, Environment.MEDIA_UNMOUNTED, Environment.MEDIA_NOFS, Environment.MEDIA_MOUNTED_READ_ONLY,
                Environment.MEDIA_BAD_REMOVAL, Environment.MEDIA_UNMOUNTABLE};
        for(String bs : bad_states) {
            if( state.equals(bs) ) {
                Logger.debug("external storage not valid (" + bs + ")");
                return null;
            }
        }

        if (Build.VERSION.SDK_INT < 8) {
            String sf = Environment.getExternalStorageDirectory() + "/" + Logger.appname;
            File f = new File(sf);
            if ( f != null ) {
                f.mkdir();
                if (f.exists()) {
                    Logger.debug("directory created in external storage");
                    return f.getAbsolutePath();
                }
            }
            Logger.debug("something has failed");
            return null;
        }
        else {
            File f = ctx.getExternalFilesDir(null);
            Logger.debug("success");
            return f.getAbsolutePath();
        }
    }
}
