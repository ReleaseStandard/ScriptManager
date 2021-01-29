package com.releasestandard.scriptmanager.model;

import com.releasestandard.scriptmanager.tools.Logger;
import com.releasestandard.scriptmanager.R;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage where the data is stored on phone (different places, suffixes, etc).
 */
public class StorageManager {

    public String externalStorage = "/sdcard/Android/data/" + R.string.app_packageid + "/files/";
    public String internalStorage = "/data/data/" + R.string.app_packageid + "/files/";

    public static String SUFFIX_LOG = ".log.txt";
    public static String SUFFIX_SCRIPT = ".txt";
    public static String SUFFIX_STATE = ".xml";
    public static String SUFFIX_OUTPUT= ".out";

    /**
     * In reality that's the script name (not path part inside).
     */
    public String script_name = "";

    public void dump() { Logger.debug(dump("")); }
    public String dump(String off) {
        String noff = off + "\t";
        return "" +
                    off + "StorageManager {\n" +
                    noff + "externalStorage="+externalStorage+"\n" +
                    noff + "internalStorage="+internalStorage+"\n"+
                    noff + "SUFFIX_LOG=" + SUFFIX_LOG + "\n" +
                    noff + "SUFFIX_SCRIPT=" + SUFFIX_SCRIPT + "\n" +
                    noff + "SUFFIX_STATE=" + SUFFIX_STATE + "\n" +
                    noff + "SUFFIX_OUTPUT=" + SUFFIX_OUTPUT + "\n" +
                    noff + "script_name=" + script_name + "\n" +
                    off + "}\n"
                ;
    }

    public StorageManager(StorageManager sm) {
        this.externalStorage = sm.externalStorage;
        this.internalStorage = sm.internalStorage;
        this.script_name = sm.script_name;
    }
    public StorageManager(String externalStorage, String internalStorage, String script) {
        this.externalStorage = externalStorage;
        this.internalStorage = internalStorage;
        this.script_name = script;
    }

    public StorageManager(String internalStorage, String scriptStorage) {
        this.externalStorage = scriptStorage;
        this.internalStorage = internalStorage;
    }
    public StorageManager() {

    }
    public String getScriptName() {
        return this.script_name;
    }
    public void setScriptName(String script) {
        this.script_name = script;
    }


    /**
     * Input    : script name or abs path
     * Output : log path
     */
    private String getLogPath() { return getLogPath(this.script_name);  }
    private String getLogPath(String script_name) { return addSuffixeIfNeeded(script_name , SUFFIX_LOG); }
    public String getLogAbsolutePath() { return getLogAbsolutePath(this.script_name);  }
    public String getLogAbsolutePath(String script_name) { return getExternalAbsolutePath(getLogPath(script_name)); }
    private String getStateFilePath() { return getStateFilePath(this.script_name) ; }
    private String getStateFilePath(String script_name) { return addSuffixeIfNeeded(script_name , SUFFIX_STATE); }
    public String getStateFileAbsolutePath() { return getStateFileAbsolutePath(this.script_name) ; }
    public String getStateFileAbsolutePath(String scriptname) { return getInternalAbsolutePath(getStateFilePath(scriptname)); }
    private String getScriptPath() { return getScriptPath(this.script_name);}
    private String getScriptPath(String script_name) { return addSuffixeIfNeeded(script_name,SUFFIX_SCRIPT);}
    public String getScriptAbsolutePath() { return this.getScriptAbsolutePath(this.script_name); }
    public String getScriptAbsolutePath(String scriptname) { return this.getExternalAbsolutePath(getScriptPath(scriptname)); }
    private String getOutputPath() { return getOutputPath(this.script_name);  }
    private String getOutputPath(String scriptname) { return addSuffixeIfNeeded(scriptname,SUFFIX_OUTPUT); }
    public String getOutputAbsolutePath() { return this.getOutputAbsolutePath(this.script_name); }
    public String getOutputAbsolutePath(String scriptname) { return this.getExternalAbsolutePath(getOutputPath(scriptname)); }
    public String getStateFileNameInPath() { return this.getStateFileNameInPath(this.script_name); }
    public String getStateFileNameInPath(String script_name) { return script_name + SUFFIX_STATE ; }
    /**
     * Input  : script name or abs path
     *  Output : script abs path to external storage
     */
    private String getExternalAbsolutePath() { return getExternalAbsolutePath(this.script_name);  }
    public String getExternalAbsolutePath(String name) { return getResolvedPath(name,this.externalStorage); }
    private String getInternalAbsolutePath() { return getInternalAbsolutePath(this.script_name);  }
    public String getInternalAbsolutePath(String name) { return getResolvedPath(name,this.internalStorage); }
    private String getResolvedPath(String name, String path) {
        Pattern p = Pattern.compile("^/");
        Matcher m = p.matcher(name);
        if (!m.find()) {
            name = path + "/" + name;
        }
        return name;
    }
    private String addSuffixeIfNeeded(String string, String suf) {
        Pattern p = Pattern.compile(suf+"$");
        Matcher m = p.matcher(script_name);
        if (!m.find()) {
            return string + suf;
        }
        return string;
    }
    /**
     * Output : script names (usables by get*AbsolutePath)
     */
    public ArrayList<String> getScriptsFromFilesystem() {
        ArrayList<String>l = new ArrayList();
        File directory = new File(this.internalStorage);
        File[] files = directory.listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            Logger.debug(file.getAbsolutePath());
            String n = file.getName();
            Pattern p = Pattern.compile("([^/]+)" + SUFFIX_STATE + "$");
            Matcher m = p.matcher(n);
            if (m.matches()) {
                l.add(m.group(1));
            }
        }
        return l;
    }
    public static String getTerminalPart(String pathname) {
        String [] tab = pathname.split("/");
        if ( tab.length <= 0) {
            return null;
        }
        return tab[tab.length-1];
    }

    // This function will be executed when no access to Regex object
    public static boolean isStateFile(String fname) {
        int i = fname.lastIndexOf(SUFFIX_STATE);
        if ( i < 0 ) {
            return false;
        }
        return (SUFFIX_STATE.length() + i ) == fname.length();
    }
}
