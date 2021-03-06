package com.releasestandard.scriptmanager.model;

import android.content.Context;
import android.util.Log;

import com.releasestandard.scriptmanager.controller.OverflowMenu;
import com.releasestandard.scriptmanager.tools.CompatAPI;
import com.releasestandard.scriptmanager.tools.Logger;
import com.releasestandard.scriptmanager.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Manage where the data is stored on phone (different places, suffixes, etc).
 */
public class StorageManager {

    public static String externalStorage = "/sdcard/Android/data/" + R.string.app_packageid + "/files/";
    public static String internalStorage = "/data/data/" + R.string.app_packageid + "/files/";

    public static String SUFFIX_LOG = ".log.txt";
    public static String SUFFIX_SCRIPT = ".txt";
    public static String SUFFIX_STATE = ".xml";
    public static String SUFFIX_OUTPUT= ".out";


    public String script_name = "";
    /**
      * compat 1
     */
    public static void writeIntArray(OutputStreamWriter osw, int tab[], int sz) throws IOException {
        Logger.debug("sz="+(new Integer(sz)).toString());
        osw.write(sz);
        for(int i = 0; i < sz ; i += 1){
            osw.write(tab[i]);
        }
    }

    /**
     * compat 1
     * @param osw
     * @param tab
     * @throws IOException
     */
    public static void writeIntegerArray(OutputStreamWriter osw, List<Integer> tab) throws IOException {
        int[] tabi = new int[tab.size()];
        for (int i = 0; i < tab.size(); i = i +1 ) {
            tabi[i]=tab.get(i);
        }
        writeIntArray(osw,tabi,tab.size());
    }
    /**
     * Read an int array from input stream.
     * compat 1
     * @param isr input stream
     * @return array readed
     * @throws IOException
     */
    public static List<Integer> readIntegerArray(InputStreamReader isr) throws IOException {
        List<Integer> tab = new ArrayList<Integer>();
        for( int j : readIntArray(isr) ) { tab.add(j); }
        return tab;
    }
    public static int[] readIntArray(InputStreamReader isr) throws IOException {
        int j = isr.read();
        short jj = (short)j;
        return readIntArray(isr,new Integer(jj));
    }
    private static int[] readIntArray(InputStreamReader isr, Integer i) throws IOException {
        if ( i < 0 ) { return null; }
        int[] tab = new int[i];
        for(int ii = 0; ii < i ; ii += 1) {
            int j = isr.read();
            // since any of secondes, minutes, hours, day, month year will go to much high we stop here
            short jj = (short)j;
            tab[ii]=jj;
        }
        return tab;
    }

    /**
     * compat 1
     * @param sm
     */
    public StorageManager(StorageManager sm) {
        setInstance(sm);
    }
    public  StorageManager(Context ctx, String script_name) { StorageManager sm = newInstance(ctx,script_name); setInstance(sm);}
    public  StorageManager(Context ctx) { StorageManager sm = newInstance(ctx); setInstance(sm);}
    public static StorageManager newInstance(Context ctx) { return newInstance(ctx,null); }
    public static StorageManager newInstance(Context ctx,String script_name) {
        StorageManager sm = new StorageManager();
        if ( script_name != null ) {
            sm.script_name = script_name;
        }
        sm.internalStorage = ctx.getFilesDir().getAbsolutePath();
        sm.externalStorage = CompatAPI.getExternalStorage(ctx);
        if ( sm.externalStorage == null ) {
            sm.externalStorage = sm.internalStorage;
            OverflowMenu.gotoMode(OverflowMenu.MODE_NO_EXT);
        }
        return sm;
    }
    public void setInstance(StorageManager sm) {
        this.externalStorage = sm.externalStorage;
        this.internalStorage = sm.internalStorage;
        this.script_name = sm.script_name;
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
     *  compat
     */
    private String getLogPath() { return getLogPath(this.script_name);  }
    private String getLogPath(String script_name) { return addSuffixeIfNeeded(script_name , SUFFIX_LOG); }
    public String getLogAbsolutePath() { return getLogAbsolutePath(this.script_name);  }
    public String getLogAbsolutePath(String script_name) { return getExternalAbsolutePath(getLogPath(script_name)); }
    private String getScriptPath() { return getScriptPath(this.script_name);}
    private String getScriptPath(String script_name) { return addSuffixeIfNeeded(script_name,SUFFIX_SCRIPT);}
    public String getScriptAbsolutePath() { return this.getScriptAbsolutePath(this.script_name); }
    public String getScriptAbsolutePath(String scriptname) { return getExternalAbsolutePath(getScriptPath(scriptname)); }
    private String getOutputPath() { return getOutputPath(this.script_name);  }
    private String getOutputPath(String scriptname) { return addSuffixeIfNeeded(scriptname,SUFFIX_OUTPUT); }
    public String getOutputAbsolutePath() { return this.getOutputAbsolutePath(this.script_name); }
    public String getOutputAbsolutePath(String scriptname) { return getInternalAbsolutePath(getOutputPath(scriptname)); }
    public String getStateFileNameInPath() { return this.getStateFileNameInPath(this.script_name); }
    public String getStateFileNameInPath(String script_name) { return script_name + SUFFIX_STATE ; }
    public static String getEventsAbsolutePath(String script_name) {
        String name_wo_suf = removeSuffix(script_name);
        return internalStorage + "/" + name_wo_suf + "/events/";
    }
    public static String getEventsRelativePath(String script_name) {
            String name_wo_suf = removeSuffix(script_name);
            return name_wo_suf + "/events/";
        }
    public static String getEventRelativePath(String script_name, String eid) {
        return getEventsRelativePath(script_name) + "/"+eid+"/";
    }
    public static Boolean isArgFile(String path) {
        return getTerminalPart(path).lastIndexOf("arg") != -1;
    }

    /**
     * Remove any suffix from script_name.
     * @return
     */
    public String removeSuffix() { return removeSuffix(this.script_name); }
    public static String removeSuffix(String script_name) {
        String [] suffixes = new String[]{
                StorageManager.SUFFIX_LOG,
                StorageManager.SUFFIX_SCRIPT,
                StorageManager.SUFFIX_OUTPUT,
                StorageManager.SUFFIX_STATE
        };
        for(String suffixe : suffixes) {
            int i = script_name.lastIndexOf(suffixe);
            if (i < 0) {
                continue;
            }
            String name_wo_suf = script_name.substring(0, i);
            return name_wo_suf;
        }
        return script_name;
    }

    /**
     * Input  : script name or abs path
     *  Output : script abs path to external storage
     *  compat 1
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
    /**
    *  Check if a filename has a suffix and add it if needed.
     *  compat 1
     */
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
     * compat 1
     */
    public ArrayList<String> getScriptsFromFilesystem(Context c) {
        ArrayList <String> l = new ArrayList<>();
        File directory = c.getFilesDir();
        File[] files = directory.listFiles();
        Arrays.sort(files);
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            String n = file.getName();
            Pattern p = Pattern.compile("([^/]+)" + SUFFIX_STATE + "$");
            Matcher m = p.matcher(n);
            if (m.matches()) {
                l.add(m.group(1));
            }
        }
        return l;
    }

    /**
     * Extract filename from file path.
     * compat 1
     * @param pathname
     * @return
     */
    public static String getTerminalPart(String pathname) {
        String [] tab = pathname.split("/");
        if ( tab.length <= 0) {
            return null;
        }
        return tab[tab.length-1];
    }

    // This function will be executed when no access to Regex object

    /**
     * Check if a file is a "state file" of a given job.
     * compat 1
     * @param fname
     * @return
     */
    public static boolean isStateFile(String fname) {
        int i = fname.lastIndexOf(SUFFIX_STATE);
        if ( i < 0 ) {
            return false;
        }
        return (SUFFIX_STATE.length() + i ) == fname.length();
    }

    /**
     * compat 1
     */
    public static OutputStreamWriter getOSW(Context ctx, String name) {
        try {
            return new OutputStreamWriter(ctx.openFileOutput(name,Context.MODE_PRIVATE));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * compat 1
     */
    public static InputStreamReader getISR(Context ctx, String name) {
        try {
            FileInputStream fis = ctx.openFileInput(name);
            InputStreamReader isr = new InputStreamReader(fis);
            return isr;
        } catch (FileNotFoundException e) {
            return null;
        }
    }
    /**
     * better implementation
     * @param ctx
     * @param path
     * @return
     */
    public static InputStreamReader newGetISR(Context ctx, String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace(Logger.getTraceStream());
            return null;
        }
        InputStreamReader isr = new InputStreamReader(fis);
        return isr;
    }
    public static String fileAsText(Context ctx, String path) {
        String res = "";
        InputStreamReader isr = newGetISR(ctx,path);
        BufferedReader buffr = new BufferedReader(isr);
        while (true) {
            String l = null;
            try {
                l = buffr.readLine();
            } catch (IOException e) {
                e.printStackTrace(Logger.getTraceStream());
                break;
            }
            if ( l == null ) {
                break;
            }
            res += l;
        }
        return res;
    }

    /**
     * compat 1
     */
    public void dump() { Logger.debug(dump("")); }
    public String dump(String off) {
        String noff = off + "\t";
        return "" +
                Logger.SZERO + off + "StorageManager {\n" +
                Logger.SZERO + noff + "externalStorage="+externalStorage+"\n" +
                Logger.SZERO + noff + "internalStorage="+internalStorage+"\n"+
                Logger.SZERO + noff + "SUFFIX_LOG=" + SUFFIX_LOG + "\n" +
                Logger.SZERO + noff + "SUFFIX_SCRIPT=" + SUFFIX_SCRIPT + "\n" +
                Logger.SZERO + noff + "SUFFIX_STATE=" + SUFFIX_STATE + "\n" +
                Logger.SZERO + noff + "SUFFIX_OUTPUT=" + SUFFIX_OUTPUT + "\n" +
                Logger.SZERO + noff + "script_name=" + script_name + "\n" +
                Logger.SZERO + off + "}\n"
                ;
    }
}
