package com.releasestandard.scriptmanager.tools;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

/**
 * Show messages on stdout (logcat)
 * compat 1
 */
public class Logger {

    public static boolean DEBUG = true;
    public static final String appname = "scriptmanager";
    private static String RED = "31";
    private static String GREEN = "32";
    private static String YELLOW = "33";
    public static String SRED = "\033[" + RED + "m";
    public static String SGREEN = "\033[" + GREEN + "m";
    public static String SYELLOW = "\033[" + YELLOW + "m";
    public static String SZERO = "\033[0m";
    public static void debug(String msg) { debug(msg,SGREEN); }
    public static void debug(String msg, String color) {
        if ( DEBUG ) {
            String tag =  color + appname+"/" + CallStack.getLastCaller(6) ;
            Log.v(tag, SZERO + msg);
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
            debug("API < " + min.toString() + " are not supported",SRED);
        } else {
            debug("API < " + min.toString() + " or API > " + max.toString() + " are not supported",SRED);
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
