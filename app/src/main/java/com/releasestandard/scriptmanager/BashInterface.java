package com.releasestandard.scriptmanager;

import java.util.HashMap;

/**
 * By convention, we use prefix _scriptmanager_ for our internal bash variables.
 */
public class BashInterface {

    // these files are used only for this classe so we don't use the StorageManager //
    private static String SUFFIX_PID = ".pid";
    private static String SUFFIX_ARG = ".arg";
    private static String SUFFIX_CALLED = ".status";
    private String pidFile = null;
    private String functNameFile = null;
    private String arg0 = null;
    private String arg1 = null;
    private Boolean isWrappScriptCalled = false;
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

    public BashInterface(StorageManager sm) {
        pidFile = sm.getInternalAbsolutePath("dummy.pid");
    }

    /**
     *  This is a wrapper allow a script to handle events from android.
     * @return
     */
    public  String wrappScript(String in,String out)  {
        Logger.debug("Transform "+in+" > "+out);
        pidFile = out + SUFFIX_PID;
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
                "\tsleep 100 &\n" +
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

        isWrappScriptCalled = true;
        return out;
    }
    /**
     * React to events
     */
    public void triggerRecvMsg(String from, String body) {
        if ( ! isWrappScriptCalled ) {
            Logger.debug("ERROR : you need to call wrappScript first !");
        }
        Logger.debug("triggerRecvMsg,from="+from+",body="+body);
        String cmd = "" +
                "" +
                "echo \"" + API.get("smsReceived") + "\" > " + functNameFile + " && " +
                "echo \"" + from + "\" > " + arg0 + " && " +
                "echo \"" + body + "\" > " + arg1 + " && " +
                "" +
                "kill -s " + signal + " $(cat " + pidFile + ");";
        Logger.debug(cmd);
        Shell._execCmd(cmd);
    }
}
