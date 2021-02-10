package com.releasestandard.scriptmanager.model;

import android.util.Log;

import com.releasestandard.scriptmanager.R;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * By convention, we use prefix _scriptmanager_ for our internal bash variables.
 */
public class KornShellInterface {

    // these files are used only for this classe so we don't use the StorageManager //
    private static String SUFFIX_PID = ".pid";
    private String pidFile = null;
    private String evts_path;
    private String signal = "USR1";
    private String events_path;

    // public API definition
    public HashMap<Integer,String> API = new HashMap<Integer, String>() {{

        put(R.string.ioctlSmsReceived,"smsReceived"); // $1 : from $2 : body

    }};


    /**
     * compat 1
     */
    public void dump() { Logger.debug(dump("")); }
    public String dump(String off) {
            String noff = off + "\t";
            return  Logger.SZERO + off + "KornShellInterface {\n"+
                    Logger.SZERO + noff + "signal="+signal+"\n" +
                    Logger.SZERO + off + "}\n"
                    ;
    }

    public KornShellInterface(StorageManager sm) {
    }

    /**
     *  This is a wrapper allow a script to handle events from android.
     * compat 23
     *  (printf)
     * @return
     */
    public  String wrappScript(String in,String out)  {
        Logger.debug("Transform "+in+" > "+out);
        pidFile = out + SUFFIX_PID;
        evts_path = StorageManager.getEventsAbsolutePath(StorageManager.getTerminalPart(in));
        KSHEvent kse = new KSHEvent(evts_path);
        kse.prereq();

        String header = "" +
                 "       _scriptmanager_pidf=\"" + pidFile + "\" ;     \n" +
                "        _scriptmanager_SIG=\""+signal+"\" ;         \n" +
                "\n" +
                kse.packLib() + "\n" +
                "events_interface () { \n" +
                        kse.recvJava2ksh("    ") + "\n" +
                "}\n" +
                "trap \"events_interface\" $_scriptmanager_SIG; \n" +
                "\n" +
                "echo \"$$\" > " + pidFile + " ; \n" +
                "# \n" +
                "# user part\n" +
                "# \n" +
                "\n" +
                "\n";

        String footer = "" +
                "\n" +
                "\n" +
                "#\n" +
                "#\n" +
                "#\n" +
                "while true ; do\n" +
                "\tsleep 2 &\n" +
                "\twait $!\n" +
                "done\n";


        String cmd = "" +
                "{ printf '" + header + "' > "   + out + ";" +
                "cat '"      + in   + "' >> " + out + ";" +
                "printf '" + footer   + "' >> " + out + "; }";


        try {
            Shell._execCmd(cmd).waitFor();
        } catch (InterruptedException e) {
            Logger.debug("Wrapping has failed");
            e.printStackTrace(Logger.getTraceStream());
        }

        return out;
    }

    /**
     * Trigger a script callback method
     * compat 14
     * @param methodToCall
     * @param args
     */
    public void triggerCallback(String methodToCall,String... args) {
        String cmd ="";
        Method method = null;
        try {
            method = KornShellInterface.class.getDeclaredMethod(methodToCall, String.class, String.class);
            cmd += method.invoke(this, args[0], args[1]);
            cmd += " && ";
        } catch (NoSuchMethodException e) {
            e.printStackTrace(Logger.getTraceStream());
        } catch (IllegalAccessException e) {
            e.printStackTrace(Logger.getTraceStream());
        } catch (InvocationTargetException e) {
            e.printStackTrace(Logger.getTraceStream());
        }
        cmd  += "kill -s " + signal + " $(cat " + pidFile + ");";

        Logger.debug(cmd);
        Shell._execCmd(cmd);

    }
        /**
         * React to events
         * compat 14
         * @return
         */
    public String triggerRecvMsg(String from, String body) {

        Logger.debug("from="+from+",body="+body);
        KSHEvent kshe = new KSHEvent(evts_path,
          API.get(R.string.ioctlSmsReceived),
          new String[]{from,body});

        return kshe.prereq() + " && " + kshe.sendJava2ksh();
    }

    /**
     * Offer a way to attach process to root (by cmd wrapping) (don't be killed by application when user close it)
     * @return
     */
    public static String attachToRoot(String cmd) {
        // Working example to attach to PPID 1 but get killed anyway :/
        // return "" + cmd + " &";
        return cmd;
    }

    /**
     * compat 14
     * @param cmd
     * @param log
     * @return
     */
    public static String outputToLog(String cmd, String log) {
        return "&>> " + log + "  " + cmd;
    }

    /**
     * compat 1
     * @param cmd
     * @return
     */
    public static String[] packIn(String cmd) {
        return new String[]{"sh", "-c", cmd};
    }

}
