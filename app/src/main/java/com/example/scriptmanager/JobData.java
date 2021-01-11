package com.example.scriptmanager;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
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
    // set The date
    int sched[] = {
            EACH_TIME,        // minutes
            EACH_TIME,        // hours
            EACH_TIME,        // day of month
            EACH_TIME,        // month
            EACH_TIME };      // year

    //
    // Not stored
    public boolean isDateSet = false;
    public String name_in_path = "";

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
            int index2 = state_file.lastIndexOf(Shell.SUFFIX_STATE);
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
            // get The date
            boolean isTimeSet = true;
            for(int ii = 0; ii < 5 ; ii += 1) {
                int j = isr.read();
                sched[ii]=j;
            }
            isDateSet = isTimeSet;

            isr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * Write the state of user interface
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
            Logger.debug("isStarted="+isStarted);
            osw.write(((isSchedulded&isStarted)?1:0));
            for(int i = 0; i < 5 ; i += 1){
                osw.write(sched[i]);
            }
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
