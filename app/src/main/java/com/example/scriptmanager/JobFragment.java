package com.example.scriptmanager;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class JobFragment extends Fragment {

    public static String WRONG_DATE_FORMAT = "Wrong date format";
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
    // données du modèle

    private View view = null;
    Shell shell = new Shell();

    public void dump() {
        Logger.debug(dump(""));
    }
    public String dump(String init) {
            return
                    init + "JobFragment {\n"+
                    init + " fragmentCount="+fragmentCount+"\n"+
                    init + " path="+path+ "\n"+
                    init + " log_path="+log_path+"\n"+
                    init + " state_file="+state_file + "\n" +
                    jd.dump(init+" ") +
                    "}\n";
    }

    public JobFragment(String statefile) {
        initializeInstance();
        this.state_file = statefile;
    }
    public JobFragment() {
        initializeInstance();
    }

    /**
     * Used to initialize the JobFragment
     */
    public void initializeInstance() {
        Calendar rn = Calendar.getInstance();
        jd.sched[0] = rn.get(Calendar.MINUTE);
        jd.sched[1] = rn.get(Calendar.HOUR);
        jd.sched[2] = rn.get(Calendar.DAY_OF_MONTH);
        jd.sched[3] = rn.get(Calendar.MONTH);
        jd.sched[4] = rn.get(Calendar.YEAR);
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


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        writeState();
        // update the date view
        // and schedule icon
        TextView dateView = view.findViewById(R.id.job_date_input);
        if ( jd.isDateSet ) {
            dateView.setText(TimeManager.sched2str(jd.sched));
        }
        long delay = 500; // 1 seconds after user stops typing
        long last_text_edit = 0;
        Handler handler = new Handler();
        Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - delay/2)) {
                    int [] s = getDateFromView();
                    if ( s != null ) {
                        jd.sched = s;
                        setDate();
                        writeState();
                    }
                }
            }
        };

        dateView.addTextChangedListener(new TextWatcher() {
            private Editable s;

            @Override
            public void beforeTextChanged (CharSequence s,int start, int count,
                                           int after){
            }
            @Override
            public void onTextChanged ( final CharSequence s, int start, int before,
                                        int count){
                //You need to remove this to run only once
                handler.removeCallbacks(input_finish_checker);

            }
            @Override
            public void afterTextChanged ( final Editable s){
                //avoid triggering event when text is empty
                if (s.length() > 0) {
                    handler.postDelayed(input_finish_checker, delay);
                } else {

                }
            }
        });

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

        Logger.debug("JogFragment:onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.debug("onCreateView from JobFragment");
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.job_fragment, container, false);
        TextView tv = v.findViewById(R.id.job_title);
        tv.setText(jd.name);
        TextView tv2 = v.findViewById(R.id.job_filename);
        tv2.setText(path);

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

        View playpause_button = v.findViewById(R.id.job_trigger_button);
        playpause_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                EditText et = v.findViewById(R.id.job_date_input);
                int [] s = getDateFromView();
                if ( s != null ) {
                    jd.sched = s;
                    // update image
                    if (!isStarted()) {
                        startJob();
                    } else {
                        stopJob();
                    }
                }
                else {
                    showErrorView(WRONG_DATE_FORMAT);
                }
            }
        });
        if ( jd.isSchedulded ) {
            setViewWaitStartJob(playpause_button);
        }
        // set up event s for the date & time picker
        View vv = v.findViewById(R.id.job_date_picker_button);
        vv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScheduleJob(v);
            }
        });

        return v;
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
                jd.sched[0] = minute;
                jd.sched[1] = hourOfDay;
                jd.sched[2] = dayOfMonth;
                jd.sched[3] = monthOfYear;
                jd.sched[4] = year;
                setDate();
                writeState();
            }
        };
        tm.show(v,jd.sched);
    }
    public boolean setDate() {
        jd.isDateSet = true;
        return jd.isDateSet;
    }
    public boolean isDateSet() {
        EditText et = getView().findViewById(R.id.job_date_input);
        String s = et.getText().toString();
        this.jd.isDateSet =  ! s.equals(new String("")) && s != null ;
        return  this.jd.isDateSet;
    }
    public void setName(String name) {
        this.jd.name = name;
        TextView tv = getView().findViewById(R.id.job_title);
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
            int [] s = getDateFromView();
            if ( s!=null) {
                jd.isSchedulded = true;
                setViewWaitStartJob();
                shell.scheduleJob(main, path,s );
                writeState();
            }
        }
        else {
            setViewStartJob();
            shell.execScript(path);
        }

        if( isSelected ) {
            main.ow_menu.callbackSelectAndRunning(main);
        }
    }
    public int[] getDateFromView() {
        EditText et = getView().findViewById(R.id.job_date_input);
        String date_input = et.getText().toString();
        if (TimeManager.validDate(date_input)) {
            return TimeManager.str2sched(date_input);
        }
        return null;
    }
    // //

    // View //
    public void removeViewJob() {
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
    public void setViewStartJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.job_trigger_button);
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
        setViewWaitStartJob(this.getView().findViewById(R.id.job_trigger_button));
    }
    public void setViewStopJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.job_trigger_button);
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
    public void showErrorView(String text) {
        Context context = getContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();

    }
    //    //


    /*******************************************************************************
     *                                                          Model = filesystem                                                                                        *
     *******************************************************************************/
    /**
     * Write the state of user interface
     */
    public void writeState() {
        jd.dump();
        jd.writeState(getContext(),state_file);
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