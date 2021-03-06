package com.releasestandard.scriptmanager.controller;

import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.model.TimeManager;
import com.releasestandard.scriptmanager.tools.Logger;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 *  Classe used to parse offline data, extract information and do the thing.
 */
public class JobData {

    public final static Integer EACH_TIME = -1;

    // id of the script
     public Integer id = 0;
    // name of the script
    public String name = "";
    // boolean for the (if it is schedulded)
    public Boolean isSchedulded = false;
    // boolean for the (if it is started)
    public Boolean isStarted = false;
    // is the date graphically set
    public boolean isDateSet = false;
    // set The date
    public int sched[] = {
            EACH_TIME,        // minutes
            EACH_TIME,        // hours
            EACH_TIME,        // day of month
            EACH_TIME,        // month
            EACH_TIME };      // year
    // index in array (intents for schedulded and processes for started)
    public List<Integer> processes = new ArrayList<Integer>();
    public List<Integer> intents = new ArrayList<>();
    public List<Integer> listeners = new ArrayList<>();

    //
    // Not stored
    public String name_in_path = "";



    /**
     *  Beware this method is used at boot time to set alarms, Object like Matcher, File
     *  could cause crashes.
     *  compat 1
     */
    public void readState(InputStreamReader isr) { readState(isr,false); }
    public void readState(InputStreamReader isr, boolean ignore_intents_processes) {
        if ( isr == null ) { return; }
        try {
            // id of the script
            int id = isr.read();
            this.id = id;
            // name of the script
            int script_name_size = isr.read();
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
            sched = StorageManager.readIntArray(isr);
            if ( ! ignore_intents_processes ) {
                Logger.debug("read processes");
                processes = StorageManager.readIntegerArray( isr);
                dump();
                Logger.debug("read intents");
                intents = StorageManager.readIntegerArray( isr);
                listeners = StorageManager.readIntegerArray( isr );
            }
            Logger.debug("after read from internal storage");
            dump();
        } catch (FileNotFoundException e) {
            e.printStackTrace(Logger.getTraceStream());
        } catch (IOException e) {
            e.printStackTrace(Logger.getTraceStream());
        }
    }

    /**
     * Write the state of user interface
     *  WARNING state_file is just the terminal part of the path
     *  compat 1
     */
    public void writeState(OutputStreamWriter osw) {
        if ( osw == null ) { return; }
        try {
            // id of the script
            osw.write(id.intValue());
            // size of the string
            osw.write(name.length());
            // name of the script
            osw.write(name);
            // boolean for the (if it is schedulded)
            osw.write((isSchedulded?1:0));
            // boolean for the (if it is started)
            osw.write((isStarted?1:0));
            // boolean for the (if it is date set or not)
            osw.write((isDateSet?1:0));
            StorageManager.writeIntArray(osw,sched,5);
            // index of (intent, process) in array (intents, processes)
            Logger.debug("write processes");
            StorageManager.writeIntegerArray(osw,processes);
            Logger.debug("write intents");
            StorageManager.writeIntegerArray(osw,intents);
            StorageManager.writeIntegerArray(osw,listeners);
            osw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace(Logger.getTraceStream());
        } catch (IOException e) {
            e.printStackTrace(Logger.getTraceStream());
        }
    }

    /**
     * compat 1
     */
    public void dump() {
        Logger.debug(dump(""));
    }
    public String dump(String init) {
        String sched_as_ints = "";
        for ( int i : sched) {
            sched_as_ints = sched_as_ints + (new Integer(i)) + " ";
        }
        return
                Logger.SZERO + init + "JobData {\n" +
                Logger.SZERO +         init + " id=" + id + "\n" +
                Logger.SZERO +         init + " name=" + name + "\n" +
                Logger.SZERO +         init + " isSchedulded=" + isSchedulded + "\n" +
                Logger.SZERO +         init + " isStarted=" + isStarted + "\n" +
                Logger.SZERO +         init + " isDateSet=" + isDateSet + "\n" +
                Logger.SZERO +         init + " sched=" + TimeManager.sched2str(sched) + "\n" +
                Logger.SZERO +         init + "  (" + sched_as_ints + ")\n" +
                Logger.SZERO +         init + " processes_sz=" + processes.size()+ "\n" +
                Logger.SZERO +         init + " intent_sz=" + intents.size() + "\n" +
                Logger.SZERO +         init + " name_in_path=" + name_in_path + "\n" +
                Logger.SZERO +         init + "}\n"
                ;
    }
}
