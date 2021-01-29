package com.releasestandard.scriptmanager;

import android.content.Context;

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
    public Integer processes_sz = 0;
    public List<Integer> processes = new ArrayList<Integer>();
    public List<Integer> intents = new ArrayList<>();
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
                init + " processes_sz=" + processes.size()+ "\n" +
                init + " intent_sz=" + intents.size() + "\n" +
                init + " name_in_path=" + name_in_path + "\n" +
                init + "}\n"
        ;
    }
    public int[] readIntArray(InputStreamReader isr) throws IOException { return readIntArray(isr,isr.read()); }
    public int[] readIntArray(InputStreamReader isr, Integer i) throws IOException {
        int[] tab = new int[i];
        for(int ii = 0; ii < i ; ii += 1) {
            int j = isr.read();
            // since any of secondes, minutes, hours, day, month year will go to much high we stop here
            short jj = (short)j;
            tab[ii]=jj;
        }
        return tab;
    }
    public List<Integer> readIntegerArray(InputStreamReader isr) throws IOException { return readIntegerArray(isr,isr.read()); }
    public List<Integer> readIntegerArray(InputStreamReader isr, Integer i) throws IOException {
        List<Integer> tab = new ArrayList<Integer>();
        for(int ii = 0; ii < i ; ii += 1) {
            int j = isr.read();
            // since any of secondes, minutes, hours, day, month year will go to much high we stop here
            short jj = (short)j;
            Logger.debug("readIntegerArray,j="+j+",jj="+jj);
            tab.add((int) jj);
        }
        return tab;
    }
    /**
     *  Beware this method is used at boot time to set alarms, Object like Matcher, File
     *  could cause crashes.
     * @param context
     * @param state_file
     */
    public void readFromInternalStorage(Context context, String state_file) { readFromInternalStorage(context,state_file,false); }
    public void readFromInternalStorage(Context context, String state_file, boolean ignore_intents_processes) {
        Logger.debug("readFromInternalStorage,state_file="+state_file);
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
            sched = readIntArray(isr,5);
            if ( ! ignore_intents_processes ) {
                processes = readIntegerArray(isr);
                intents = readIntegerArray(isr);
            }
            isr.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeIntArray(OutputStreamWriter osw, int tab[], int sz) throws IOException {
        for(int i = 0; i < sz ; i += 1){
            osw.write(tab[i]);
        }
    }
    public void writeIntegerArray(OutputStreamWriter osw, List<Integer> tab) throws IOException {
        osw.write(tab.size());
        for(int i = 0; i < tab.size() ; i += 1){
            Integer ii = tab.get(i);
            Logger.debug("writeIntegerArray,ii="+ii);
            osw.write(ii);
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
            writeIntArray(osw,sched,5);
            // index of (intent, process) in array (intents, processes)
            Logger.debug("write processes");
            writeIntegerArray(osw,processes);
            Logger.debug("write intents");
            writeIntegerArray(osw,intents);
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
