package com.releasestandard.scriptmanager;

import android.util.Log;

/*
 * Show messages on stdout (logcat)
 */
public class Logger {

    public static boolean DEBUG = false;
    private static final String appname = "scriptmanager";

    public static void debug(String msg) {
        if ( DEBUG ) {
            Log.v(appname, msg);
        }
    }
    public static void log(String msg) {
        Log.v(appname,msg);
    }
}
