package com.example.scriptapp;

import android.os.Environment;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * This class is a wrapper for an underlying shell : e.g. sh
 */
public class Shell {

    boolean stoIsAvailable = false;
    boolean stoIsWritable = false;
    boolean stoIsReadable = false;
    private String baseDir = Environment
            .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
            .getAbsolutePath() + "@string/app_name";
    private String scriptsDir = baseDir + "/scripts/" ;

    /**
     *  Constructor will build the storage required to store scripts.
     */
    public Shell() {
        // prepare the storage for scripts
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            // Operation possible - Read and Write
            stoIsAvailable = true;
            stoIsWritable = true;
            stoIsReadable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Operation possible - Read Only
            stoIsAvailable = true;
            stoIsWritable = false;
            stoIsReadable = true;
        } else {
            // SD card not available
            stoIsAvailable = false;
            stoIsWritable = false;
            stoIsReadable = false;
        }

        // Prepare application's folder
        if ( stoIsWritable ) {
            this.execCmd(
                    "mkdir -p " + baseDir + " " + scriptsDir + ";"
            );
        }
    }

    /**
     *  Run one time
     * @param cmd
     * @return
     */
    public int execCmd(String cmd) {
        try {
            Runtime.getRuntime().exec(new String[]{"sh","-c",cmd});
        } catch (IOException e) {
            return 1;
        }
        return 0;
    }

    /**
     * parameter could be the name of the script or an absolute path.
     * @param script
     * @return
     */
    public int execScript(String script) {
        Pattern p = Pattern.compile("^/");
        Matcher m = p.matcher(script);
        if (!m.find()) {
            script = scriptsDir + script;
        }
        return this.execCmd("< " + script);
    }
}
