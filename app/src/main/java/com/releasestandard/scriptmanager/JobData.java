package com.releasestandard.scriptmanager;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *  Classe used to parse offline data, extract information and do the thing.
 */
public class JobData {

    public final static Integer EACH_TIME = -1;

    // id of the script
     public Integer id = 0;
    // size of the string
    public Integer name_length = 0;
    // name of the script
    public String name = "";
    // boolean for the (if it is schedulded)
    public Boolean isSchedulded = false;
    // boolean for the (if it is started)
    public Boolean isStarted = false;
    // is the date graphically set
    public boolean isDateSet = false;
    // set The date
    int sched[] = {
            EACH_TIME,        // minutes
            EACH_TIME,        // hours
            EACH_TIME,        // day of month
            EACH_TIME,        // month
            EACH_TIME };      // year
    // index in array (intents for schedulded and processes for started)
    public Integer index_in_array = -1;
    //
    // Not stored
    public String name_in_path = "";


    public void dump() {
        Logger.debug(dump(""));
    }
    public String dump(String init) {
        String sched_as_ints = "";
        for ( int i : sched) {
            sched_as_ints = sched_as_ints + (new Integer(i)) + " ";
        }
        return
                init + "JobData {\n" +
                init + " id=" + id + "\n" +
                init + " name=" + name + "\n" +
                init + " isSchedulded=" + isSchedulded + "\n" +
                init + " isStarted=" + isStarted + "\n" +
                init + " isDateSet=" + isDateSet + "\n" +
                init + " sched=" + TimeManager.sched2str(sched) + "\n" +
                init + "  (" + sched_as_ints + ")\n" +
                init + " index_in_array=" + index_in_array + "\n" +
                init + " name_in_path=" + name_in_path + "\n" +
                init + "}\n"
        ;
    }
    /**
     *  Beware this method is used at boot time to set alarms, Object like Matcher, File
     *  could cause crashes.
     * @param context
     * @param state_file
     */
    public void readFromInternalStorage(Context context, String state_file) {
        InputStreamReader isr;
        try {
            int index1 = state_file.lastIndexOf('/');
            if ( index1 == -1) {
                index1=0;
            }
            int index2 = state_file.lastIndexOf(StorageManager.SUFFIX_STATE);
            name_in_path = state_file.substring(index1,index2);
            isr = new InputStreamReader(context.openFileInput(state_file));
            // id of the script
            int id = isr.read();
            this.id = id;
            // size of the string
            int script_name_size = isr.read();
            // name of the script
            char [] script_name = new char[script_name_size];
            isr.read(script_name);
            name = new String(script_name);
            // boolean for the (if it is schedulded)
            isSchedulded = (isr.read() == 0)?false:true;
            // boolean for the (if it is started)
            isStarted = (isr.read() == 0)?false:true;
            // boolean for the (if it is date set or not)
            isDateSet = (isr.read() == 0)?false:true;
            // get The date
            for(int ii = 0; ii < 5 ; ii += 1) {
                int j = isr.read();
                // since any of secondes, minutes, hours, day, month year will go to much high we stop here
                short jj = (short)j;
                sched[ii]=jj;
            }
            index_in_array = isr.read();
            isr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Write the state of user interface
     *  WARNING state_file is just the terminal part of the path
     */
    public void writeState(Context context, String state_file) {
        OutputStreamWriter osw = null;
        try {
            // private storage
            osw = new OutputStreamWriter(context.openFileOutput(state_file, Context.MODE_PRIVATE));
            // id of the script
            osw.write(id.intValue());
            // size of the string
            osw.write(name.length());
            // name of the script
            osw.write(name);
            // boolean for the (if it is schedulded)
            osw.write((isSchedulded?1:0));
            // boolean for the (if it is started)
            osw.write(((isSchedulded&isStarted)?1:0));
            // boolean for the (if it is date set or not)
            osw.write((isDateSet?1:0));
            for(int i = 0; i < 5 ; i += 1){
                osw.write(sched[i]);
            }
            // index of (intent, process) in array (intents, processes)
            osw.write(index_in_array);
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
