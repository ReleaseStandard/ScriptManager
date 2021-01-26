package com.releasestandard.scriptmanager;

import java.util.HashMap;

public class BashInterface {

    private String pidFile = null;
    private String salt = salt = "scriptmanager_internal_dfjskhqipfhauzihuifeazipuihefuihiaez_";
    private HashMap<String,String> events =  new HashMap<String, String>() {{
        put("msg_recv", "USR1");
        put("msg_send", "USR2");
    }};

    public BashInterface(StorageManager sm) {
        pidFile = sm.getInternalAbsolutePath("dummy.pid");
    }

    /**
     *  This is a wrapper allow a script to handle events from android.
     * @return
     */
    public  String wrappScript(String in,String out)  {
        Logger.debug("Transform "+in+" > "+out);
        pidFile = out + ".pid";
        String header = "" +
                salt + "pidf=\"" + pidFile + "\";\n" +
                salt + "is_trap_set=false;\n" +
                salt + "SIG_msg_recv="+events.get("msg_recv")+";\n" +
                "\n" +
                "handle_msg_recv() \n" +
                "{\n" +
                "\t" + salt + "is_trap_set=true;\n" +
                "\tmsg=\"read from disk\";\n" +
                "\ttel=\"also read from disk\";\n" +
                "\ttrap \"$1 \\\"$msg\\\" \\\"$tel\\\"\" $" + salt + "SIG_msg_recv;\n" +
                "}\n" +
                "\n" +
                "echo \"$$\" > " + pidFile + ";\n";

        String footer = "" +
                "while $" + salt + "is_trap_set ; do\n" +
                "\tsleep 100 &\n" +
                "\techo \"$!\"\n" +
                "\twait $!\n" +
                "done\n";


        Shell._execScript(
                "printf '" + header + "' > "   + out +
                        " ; cat '"      + in   + "' >> " + out +
                        " ; printf '" + footer   + "' >> " + out + ";"
        );

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
