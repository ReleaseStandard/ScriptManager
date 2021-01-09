package com.example.scriptmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.EditorInfo;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobFragment extends Fragment {

    private static final String sname = "test";
    public static Integer fragmentCount = 0;

    // is this fragment selected user
    public boolean isSelected = false;
    public String name;
    public String path;
    public String log_path;
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

    private String msname;

    public JobFragment() {
        Calendar rn = Calendar.getInstance();
        sched[0] = rn.get(Calendar.MINUTE);
        sched[1] = rn.get(Calendar.HOUR);
        sched[2] = rn.get(Calendar.DAY_OF_MONTH);
        sched[3] = rn.get(Calendar.MONTH);
        sched[4] = rn.get(Calendar.YEAR);
        rn.set(Calendar.SECOND,0);
        rn.set(Calendar.MILLISECOND,0);

        Integer i = fragmentCount++;
        name = "Script n°" + i.toString();
        path = "script_" + i.toString() + ".sh";
        log_path = shell.getLogPath(path);

        shell.execCmd("> " + log_path);
    }
    public String getAbsolutePath() {
        return Shell.getAbsolutePath(path);
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
    public static int[] str2sched(String s) {
        int sched[] = {EACH_TIME,EACH_TIME,EACH_TIME,EACH_TIME,EACH_TIME};
        int i = 0;
        String [] parts = s.split(" ");
        if ( parts.length == sched.length ) {
            for (String ss : parts) {
                if (ss.equals(new String("*"))) {
                    sched[i] = EACH_TIME;
                } else {
                    sched[i] = (int) Integer.parseInt(ss);
                }
                i = i + 1;
            }
        }
        return sched;
    }
    public static String sched2str(int [] s) {
        String res = "";
        for (int i : s) {
            if ( i < 0 ) {
                res += "* ";
            }
            else {
                Integer ii = new Integer(i);
                res += ii + " ";
            }
        }
        return res;
    }

    public static boolean isRepeated(int [] sched) {
        for ( int i = 0 ; i < 5 ; i = i +1 ) {
            if ( sched[i] == EACH_TIME ) {
                return true;
            }
        }
        return false;
    }
    public static Calendar nextSched(int [] sched) {
        Calendar c = new GregorianCalendar();
        int parts[]={ Calendar.MINUTE,Calendar.HOUR,
                Calendar.DAY_OF_MONTH,Calendar.MONTH,
                Calendar.YEAR};
        c.set(Calendar.MILLISECOND,0);
        c.set(Calendar.SECOND,0);

        // if we count in
        boolean areWeInEachThing = false;
        for ( int i = 0; i < 5 ; i = i + 1 ) {
            if ( sched[i] == EACH_TIME ) {
                // we need to apply this only on the first each
                if ( areWeInEachThing == false ) {
                    c.set(parts[i], c.get(parts[i]) + 1);
                    areWeInEachThing = true;
                }
                else {
                    c.set(parts[i], c.get(parts[i]));
                }
            }
            else {
                c.set(parts[i], sched[i]);
            }
        }
        return c;
    }


    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("scriptmanager","JogFragment:onViewCreated");
    }
    public void showScheduleJob(View v) {

        int minute = sched[0];
        int hour = sched[1];
        int day = sched[2];
        int month = sched[3];
        int year = sched[4];


        ViewGroup vpg = (ViewGroup)v.getParent();
        TextView tv = vpg.findViewById(R.id.editTextTextPersonName2);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),R.style.TimePickerTheme,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        sched[0] = minute;
                                        sched[1] = hourOfDay;
                                        sched[2] = dayOfMonth;
                                        sched[3] = monthOfYear;
                                        sched[4] = year;
                                        tv.setText(sched2str(sched));
                                    }
                                }, year, month, day);
                        datePickerDialog.show();
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }

    /*
     * Check if the guy has modified the time when to launch the script.
     */
    public boolean isDateSet() {
        EditText et = getView().findViewById(R.id.editTextTextPersonName2);
        String s = et.getText().toString();
        return ! s.equals(new String("")) ;
    }
    public int[] getDate() {
        EditText et = getView().findViewById(R.id.editTextTextPersonName2);
        return str2sched(et.getText().toString());
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("scriptmanager","JogFragment:onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.v("scriptmanager","JogFragment:ViewStateRestored");
        restoreView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            msname = getArguments().getString(sname);
        }
        Log.v("scriptmanager","JogFragment:onCreate");
    }

    public void callUnselectAll() {
        MainActivity main = (MainActivity) getActivity();
        main.jobs_view.unselectAllFragments();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.job_fragment, container, false);
        TextView tv = v.findViewById(R.id.job_fragment_textView);
        tv.setText(name);
        TextView tv2 = v.findViewById(R.id.textView3);
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

        View button = v.findViewById(R.id.floatingActionButton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                EditText et = v.findViewById(R.id.editTextTextPersonName2);
                sched = getDate();
                // update image
                if ( started == null || (started != null && stopped != null)  ) {
                    startJob();
                }
                else {
                    if ( isStarted() ) {
                        stopJob();
                    }
                }
            }
        });

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

    public boolean isStarted() {
        return (started != null && stopped == null);
    }

    public void restoreView() {
        if( isStarted() ) {
            // custom date?
            if ( isDateSet() ) {
                setViewWaitStartJob();
            }
            else {
                setViewStartJob();
            }
        }
        else {
            setViewStopJob();
        }
    }
    public void setViewStartJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.floatingActionButton2);
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_media_pause)
        );
    }
    public void setViewWaitStartJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.floatingActionButton2);
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_menu_recent_history)
        );
    }
    public void setViewStopJob() {
        FloatingActionButton fab = this.getView().findViewById(R.id.floatingActionButton2);
        fab.setImageDrawable(
                getResources().getDrawable(android.R.drawable.ic_media_play)
        );
    }

    public void startJob() {
        MainActivity main = (MainActivity)getActivity();
        int i = main.jobs_view.getNumberStarted();
        if ( i == 0) {
            main.ow_menu.enterRunningMode();
        }

        if ( isDateSet() ) {
            setViewWaitStartJob();
            shell.scheduleJob(main,path,getDate());
        }
        else {
            setViewStartJob();
            shell.execScript(path);
        }

        stopped = null;
        started = new Date();
        if( isSelected ) {
            main.ow_menu.callbackSelectAndRunning(main);
        }
    }
    public void setName(String name) {
        this.name = name;
        TextView tv = getView().findViewById(R.id.job_fragment_textView);
        tv.setText(name);
    }
    public void stopJob() {
        setViewStopJob();
        shell.terminateAll();
        stopped = new Date();
        MainActivity main = (MainActivity)getActivity();
        int i = main.jobs_view.getNumberStarted();
        if ( i == 0 ) {
            main.ow_menu.leaveRunningMode();
        }
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
}