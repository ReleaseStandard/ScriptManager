package com.releasestandard.scriptmanager;

import java.util.HashMap;

/**
 * By convention, we use prefix _scriptmanager_ for our internal bash variables.
 */
public class BashInterface {

    // these files are used only for this classe so we don't use the StorageManager //
    private static String SUFFIX_PID = ".pid";
    private String pidFile = null;
    private HashMap<String,String> events =  new HashMap<String, String>() {{
        put("msg_recv", "USR1");
        put("msg_send", "USR2");
    }};

    public void dump() { Logger.debug(dump("")); }
    public String dump(String off) {
            String noff = off + "\t";
            return off + "BashInterface {\n"+
                    noff + "events=" + events.size() + "\n" +
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


        String header = "" +
                 "       _scriptmanager_pidf=\"" + pidFile + "\" ;     \n" +
                "        _scriptmanager_is_trap_set=false ;              \n" +
                "        _scriptmanager_SIG_msg_recv=\""+events.get("msg_recv")+"\" ;         \n" +
                "\n" +
                "handle_msg_recv () { \n" +
                "         _scriptmanager_is_trap_set=true ; \n" +
                "         msg=\"read from disk\"      ;             \n" +
                "         tel=\"also read from disk\" ;            \n" +
                "         trap \"$1 \\\"$msg\\\" \\\"$tel\\\"\" $_scriptmanager_SIG_msg_recv ;  \n" +
                "}\n" +
                "\n" +
                "echo \"$$\" > " + pidFile + " ; \n";

        String footer = "" +
                "while $_scriptmanager_is_trap_set ; do\n" +
                "\tsleep 100 &\n" +
                "\techo \"$!\"\n" +
                "\twait $!\n" +
                "done\n";


        String cmd = "" +
                "{ printf '" + header + "' > "   + out + ";" +
                "cat '"      + in   + "' >> " + out + ";" +
                "printf '" + footer   + "' >> " + out + "; }";


      Shell._execCmd(cmd);

        return out;
    }
    /**
     * React to events
     */
    public void triggerRecvMsg(String from, String body) {
        Logger.debug("triggerRecvMsg,from="+from+",body="+body);
        String cmd = "kill -s " + events.get("msg_recv") + " $(cat " + pidFile + ")";
        Logger.debug(cmd);
        Shell._execScript(cmd);
    }
}