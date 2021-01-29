package com.releasestandard.scriptmanager.model;

import com.releasestandard.scriptmanager.tools.Logger;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * By convention, we use prefix _scriptmanager_ for our internal bash variables.
 */
public class KornShellInterface {

    // these files are used only for this classe so we don't use the StorageManager //
    private static String SUFFIX_PID = ".pid";
    private static String SUFFIX_ARG = ".arg";
    private static String SUFFIX_CALLED = ".status";
    private static String SUFFIX_LOCK = ".lock";
    private String pidFile = null;
    private String lockFile = null;
    private String functNameFile = null;
    private String arg0 = null;
    private String arg1 = null;
    private String signal = "USR1";

    // API definition
    // event -> function name associated
    public HashMap<String,String> API = new HashMap<String, String>() {{

        put("smsReceived","smsReceived"); // $1 : from $2 : body

    }};

    public void dump() { Logger.debug(dump("")); }
    public String dump(String off) {
            String noff = off + "\t";
            return off + "BashInterface {\n"+
                    noff + "signal="+signal+"\n" +
                    off + "}\n"
                    ;
    }

    public KornShellInterface(StorageManager sm) {
        pidFile = sm.getInternalAbsolutePath("dummy.pid");
    }

    /**
     *  This is a wrapper allow a script to handle events from android.
     * @return
     */
    public  String wrappScript(String in,String out)  {
        Logger.debug("Transform "+in+" > "+out);
        pidFile = out + SUFFIX_PID;
        lockFile = out + SUFFIX_LOCK;
        arg0 = out + SUFFIX_ARG + "0";
        arg1 = out + SUFFIX_ARG + "1";
        functNameFile = out + SUFFIX_CALLED;

        String header = "" +
                 "       _scriptmanager_pidf=\"" + pidFile + "\" ;     \n" +
                "        _scriptmanager_SIG=\""+signal+"\" ;         \n" +
                "\n" +
                "events_interface () { \n" +
                "         arg0=\"$(cat "+arg0+")\"      ;             \n" +
                "         arg1=\"$(cat "+arg1+")\" ;                  \n" +
                "         functname=\"$(cat "+ functNameFile +")\" ;     \n" +
                "         type $functname &> /dev/null && " + // if user has defined a callback
                "          $functname \"$arg0\" \"$arg1\" ;                  \n" +
                "          rm -f \"" + lockFile + "\"; \n" +                                      // action is done, we can remove the lock
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
            e.printStackTrace();
        }

        return out;
    }
    public void triggerCallback(String methodToCall,String... args) {
        String cmd ="";
        Method method = null;
        try {
            method = KornShellInterface.class.getDeclaredMethod(methodToCall,String.class,String.class);
            cmd += method.invoke(this, args[0], args[1]);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        cmd  += "kill -s " + signal + " $(cat " + pidFile + ");";

        // we have to wait for the lock file before run command
        File file = new File(lockFile);
        while(file.exists()) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Shell._execCmd("touch \""+lockFile+"\"");
        Logger.debug(cmd);
        Shell._execCmd(cmd);

    }
        /**
     * React to events
         * @return
         */
    public String triggerRecvMsg(String from, String body) {
        Logger.debug("triggerRecvMsg,from="+from+",body="+body);

                return "" +
                "echo \"" + API.get("smsReceived") + "\" > " + functNameFile + " && " +
                "echo \"" + from + "\" > " + arg0 + " && " +
                "echo \"" + body + "\" > " + arg1 + " && " +
                "";
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
    public static String outputToLog(String cmd, String log) {
        return "&>> " + log + "  " + cmd;
    }
    public static String[] packIn(String cmd) {
        return new String[]{"sh", "-c", cmd};
    }
}
