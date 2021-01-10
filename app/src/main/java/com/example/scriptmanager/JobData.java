package com.example.scriptmanager;

import android.content.Context;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

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
    // Not stocked
    public boolean isDateSet = false;


    public void readFromInternalStorage(Context context, String state_file) {
        InputStreamReader isr;
        try {
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
                if ( j == -1 ) {
                    // the date has not be writted
                    isTimeSet = false;
                    break;
                }
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
}
