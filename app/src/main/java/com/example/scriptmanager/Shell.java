package com.example.scriptmanager;

import android.os.Environment;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is a wrapper for an underlying shell : e.g. sh
 */
public class Shell {

    static public String externalStorage = null;
    static public String internalStorage = null;
    private List<Process> processes = new ArrayList<Process>();

    /**
     *  Constructor will build the storage required to store scripts.
     */
    public Shell(String internalStorage, String scriptStorage) {
            Shell.externalStorage = scriptStorage;
            Shell.internalStorage = internalStorage;
        }
        /**
         *  We considere at  this step that the new Shell(String,String) has already be called
         */
        public Shell() {

        }

    /**
     *  Run one time
     * @param cmd
     * @return
     */
    public int execCmd(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec(new String[]{"sh","-c",cmd});
            processes.add(p);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    public static String getAbsolutePath(String script) {
        Pattern p = Pattern.compile("^/");
        Matcher m = p.matcher(script);
        if (!m.find()) {
            script = externalStorage + "/" + script;
        }
        return script;
    }
    /**
     * parameter could be the name of the script or an absolute path.
     * @param script
     * @return
     */
    public int execScript(String script) {
        getAbsolutePath(script);
        try {
            Process pr = Runtime.getRuntime().exec(new String[]{"sh","-c",". "+script});
            processes.add(pr);
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    public void terminateAll() {
        for(Process p : processes) {
            p.destroy();
        }
        processes = new ArrayList<Process>();
    }
}
