package com.releasestandard.scriptmanager.tools;

import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Show messages on stdout (logcat)
 * compat 1
 */
public class Logger {

    public static boolean DEBUG = true;
    private static final String appname = "scriptmanager";
    private static String RED = "31";
    private static String GREEN = "32";
    private static String YELLOW = "33";
    public static void debug(String msg) { debug(msg,GREEN); }
    public static void debug(String msg, String color) {
        if ( DEBUG ) {
            Log.v("\033["+ color + "m" + appname+"\033[0m", msg);
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
            debug("API < " + min.toString() + " are not supported",RED);
        } else {
            debug("API < " + min.toString() + " or API > " + max.toString() + " are not supported",RED);
        }
    }
    public static PrintStream getTraceStream() {
        if ( DEBUG ) {
            return System.out;
        } else {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            PrintStream os = new PrintStream(buffer);
            return os;
        }
    }
}
