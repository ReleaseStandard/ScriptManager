package com.example.scriptmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobFragment extends Fragment {

    // ?
    private static final String sname = "test";
    private String msname;

    public static Integer fragmentCount = 0;

    // is this fragment selected user
    public boolean isSelected = false;
    public JobData jd = new JobData();
    public String path;                     // script path
    public String log_path;              // log path
    public String state_file;             // state path
    public Date started = null;
    public Date stopped = null;
    public final static Integer EACH_TIME = -1;
    int sched[] = {
                    EACH_TIME,        // minutes
                    EACH_TIME,        // hours
                    EACH_TIME,        // day of month
                    EACH_TIME,        // month
                    EACH_TIME };      // year
    // données du modèle

    private View view = null;
    Shell shell = new Shell();

    public void dump() {
            Logger.log("JobFragment {\n fragmentCount="+fragmentCount+"\n name="+jd.name+"\n path="+path+
                    "\n log_path="+log_path+"\n state_file="+state_file+"\n id="+jd.id+"\n isSchedulded="+ jd.isSchedulded +"\n isStarted="+ jd.isStarted + "\n}");
    }

    public JobFragment(String statefile) {
        newInstanceStuff();
        this.state_file = statefile;
    }
    public JobFragment() {
        newInstanceStuff();
    }
    public void newInstanceStuff() {
        Calendar rn = Calendar.getInstance();
        sched[0] = rn.get(Calendar.MINUTE);
        sched[1] = rn.get(Calendar.HOUR);
        sched[2] = rn.get(Calendar.DAY_OF_MONTH);
        sched[3] = rn.get(Calendar.MONTH);
        sched[4] = rn.get(Calendar.YEAR);
        rn.set(Calendar.SECOND,0);
        rn.set(Calendar.MILLISECOND,0);

        jd.id = fragmentCount++;
        jd.name = "Script n°" + jd.id.toString();
        path = "script_" + jd.id.toString() + Shell.SUFFIX_SCRIPT;
        state_file = "script_" + jd.id.toString() + Shell.SUFFIX_STATE;
        log_path = shell.getLogPath(path);

        shell.execCmd("> " + log_path);
    }
    public String getAbsolutePath() {
        return Shell.getAbsolutePath(path);
    }
    public String getLogPath() {
        return Shell.getLogPath(path);
    }
    public String getStatePath() {
        return Shell.internalStorage + "/" + state_file;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sname SName.
     * @return A new instance of fragment fragment_first_part.
     */
    public static JobFragment newInstance(String sname) {
        JobFragment fragment = new JobFragment();
        Bundle args = new Bundle();
        args.putString(JobFragment.sname, sname);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        writeState();
        Logger.debug("JogFragment:onViewCreated");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.debug("JogFragment:onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        restoreView();
        Logger.debug("JogFragment:ViewStateRestored");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            msname = getArguments().getString(sname);
        }
        Logger.debug("JogFragment:onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug("onCreateView from JobFragment");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.job_fragment, container, false);
        TextView tv = v.findViewById(R.id.job_fragment_textView);
        tv.setText(jd.name);
        TextView tv2 = v.findViewById(R.id.textView3);
        tv2.setText(path);

        // update the date view
        // and schedule icon
        if ( jd.isDateSet ) {
            TextView dateView = v.findViewById(R.id.editTextTextPersonName2);
            dateView.setText(TimeManager.sched2str(sched));
        }

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                selectView(arg0);
                return true;
            }});
        v.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)  {
                MainActivity main = (MainActivity)getActivity();
                if ( main.jobs_view.getNumberSelected() > 0 ) {
                    if (isSelected) {
                        unselectView(v);
                    } else {
                        selectView(v);
                    }
                }
            }
        });

        View playpause_button = v.findViewById(R.id.floatingActionButton2);
        playpause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                EditText et = v.findViewById(R.id.editTextTextPersonName2);
                sched = getDate();
                // update image
                if ( !isStarted()  ) {
                    startJob();
                }
                else {
                    stopJob();
                }
            }
        });
        if ( jd.isSchedulded ) {
            setViewWaitStartJob(playpause_button);
        }
        // set up event s for the date & time picker
        View vv = v.findViewById(R.id.job_fragment_time_picker_button);
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleJob(v);
            }
        });

        return v;
    }

    public void remove() {
        Logger.debug("JobFragments : remove");
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.remove(this);
        ft.commit();

        // remove the script file
        ArrayList<String> files = new ArrayList<String>();
        files.add(getAbsolutePath()); //script file remove
        files.add(getLogPath());           // remove log file
        files.add(getStatePath());        // remove state file
        for ( String s : files ) {
            Logger.log(s);
            File f = new File(s);
            if (f.exists()) {
                f.delete();
            }
        }
    }

    // Model //
    public boolean isStarted() {
        return jd.isStarted;
    }
    //    //

    // Controller //
    /*
     * Check if the guy has modified the time when to launch the script.
     */
    public void showScheduleJob(View v) {

        TimeManager tm = new TimeManager(){
            @Override
            public void onPicked(int minute,int hourOfDay,int dayOfMonth,int monthOfYear,int year) {
                sched[0] = minute;
                sched[1] = hourOfDay;
                sched[2] = dayOfMonth;
                sched[3] = monthOfYear;
                sched[4] = year;
                writeState();
            }
        };
        tm.show(v,sched);
    }
    public boolean isDateSet() {
        EditText et = getView().findViewById(R.id.editTextTextPersonName2);
        String s = et.getText().toString();
        this.jd.isDateSet =  ! s.equals(new String("")) && s != null ;
        return  this.jd.isDateSet;
    }
    public void setName(String name) {
        this.jd.name = name;
        TextView tv = getView().findViewById(R.id.job_fragment_textView);
        tv.setText(name);
    }
    public void stopJob() {
        jd.isSchedulded = false;
        jd.isStarted = false;
        setViewStopJob();
        shell.terminateAll();
        stopped = new Date();
        MainActivity main = (MainActivity)getActivity();
        int i = main.jobs_view.getNumberStarted();
        if ( i == 0 ) {
            main.ow_menu.leaveRunningMode();
        }
        writeState();
    }
    public void startJob() {
        MainActivity main = (MainActivity)getActivity();
        int i = main.jobs_view.getNumberStarted();
        if ( i == 0) {
            main.ow_menu.enterRunningMode();
        }

        stopped = null;
        started = new Date();
        jd.isStarted = true;

        if ( isDateSet() ) {
            jd.isSchedulded = true;
            setViewWaitStartJob();
            shell.scheduleJob(main,path,getDate());
            writeState();
        }
        else {
            setViewStartJob();
            shell.execScript(path);
        }

        if( isSelected ) {
            main.ow_menu.callbackSelectAndRunning(main);
        }
    }
    public int[] getDate() {
        EditText et = getView().findViewById(R.id.editTextTextPersonName2);
        return TimeManager.str2sched(et.getText().toString());
    }
    // //

    // View //
    public void setViewStartJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.floatingActionButton2);
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_media_pause)
        );
    }
    public void setViewWaitStartJob(View v) {
        FloatingActionButton fab = (FloatingActionButton)v;
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_menu_recent_history)
        );
    }
    public void setViewWaitStartJob() {
        setViewWaitStartJob(this.getView().findViewById(R.id.floatingActionButton2));
    }
    public void setViewStopJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.floatingActionButton2);
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_media_play)
        );
    }
    public void unselectView() {
        this.isSelected = false;
        if ( this.view != null) {
            MainActivity main = (MainActivity) getActivity();
            int color =main.getColorFromId(main, R.attr.colorPrimary);
            view.setBackgroundColor(color);
            if( main.jobs_view.getNumberSelected()  <= 0) {
                main.ow_menu.leaveSelectMode();
            }
            if( main.jobs_view.getNumberSelected() == 1) {
                main.ow_menu.enterOneOnlySelectMode();
            }
        }
    }
    public void unselectView(View v) {
        if ( v != null ) {
            this.view = v;
        }
        this.unselectView();
    }
    public void selectView() {
        if ( this.view != null ) {
            this.selectView(this.view);
        }
    }
    public void selectView(View v) {
        this.isSelected = true;
        // The selection action
        // Hightlight the ActionBar
        MainActivity main = (MainActivity) getActivity();
        ActionBar ab = main.getSupportActionBar();
        int color =main.getColorFromId(main, android.R.attr.colorLongPressedHighlight);
        ab.setBackgroundDrawable(new ColorDrawable(color));
        v.setBackgroundColor(color);
        if( main.jobs_view.getNumberSelected()  == 1) {
            main.ow_menu.enterSelectMode();
        }
        if( main.jobs_view.getNumberSelected()  > 1) {
            main.ow_menu.leaveOneOnlySelectMode();
        }
        if ( v != null ) {
            this.view = v;
        }
        main.ow_menu.callbackSelectAndRunning(main);
    }
    public void restoreView() {
        if (jd.isSchedulded) {
            setViewWaitStartJob();
        }
        else {
            if (isStarted()) {
                setViewStartJob();
            } else {
                setViewStopJob();
            }
        }
    }
    //    //


    /*******************************************************************************
     *                                                          Model = filesystem                                                                                        *
     *******************************************************************************/
    /**
     * Write the state of user interface
     */
    public void writeState() {
        OutputStreamWriter osw = null;
        try {
            // private storage
            osw = new OutputStreamWriter(getContext().openFileOutput(state_file, Context.MODE_PRIVATE));
            // id of the script
            osw.write(jd.id.intValue());
            // size of the string
            osw.write(jd.name.length());
            // name of the script
            osw.write(jd.name);
            // boolean for the (if it is schedulded)
            osw.write((jd.isSchedulded?1:0));
            // boolean for the (if it is started)
            Logger.debug("isStarted="+jd.isStarted);
            osw.write(((jd.isSchedulded&jd.isStarted)?1:0));
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

    /*
     * Read the state at start
     *   => pas d'update dans l'ui, c'est le probleme
     */
    public void readState(Context context, String path_name) {
        Logger.debug("readState from JobFragment");

        jd.readFromInternalStorage(context,state_file);
        // Adjust pathname & logpath
        //path_name
        String script_name_path = path_name + Shell.SUFFIX_SCRIPT;
        path = script_name_path;
        String log_name_path = path_name + Shell.SUFFIX_LOG;
        log_path = Shell.getAbsolutePath(log_name_path);
    }
}