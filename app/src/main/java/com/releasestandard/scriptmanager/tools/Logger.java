package com.releasestandard.scriptmanager.tools;

import android.util.Log;

/**
 * Show messages on stdout (logcat)
 * compat 1
 */
public class Logger {

    public static boolean DEBUG = true;
    private static final String appname = "scriptmanager";

    public static void debug(String msg) {
        if ( DEBUG ) {
            Log.v(appname, msg);
        }
    }
    public static void log(String msg) {
        Log.v(appname,msg);
    }
    public static  void unsupported(Integer min) {
        unsupported(min,-1);
    }
    public static void unsupported(Integer min, Integer max) {
        if ( max < 0) {
            debug("API < " + min.toString() + " are not supported");
        } else {
            debug("API < " + min.toString() + " or API > " + max.toString() + " are not supported");
        }
    }
}
