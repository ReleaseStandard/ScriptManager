package com.example.scriptmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobFragment extends Fragment {

    private static final String sname = "test";

    // is this fragment selected user
    public boolean isSelected = false;
    public String name = "das ist ein test";
    public Date started = null;
    public Date stopped = null;
    private View view = null;
    Shell shell = new Shell();

    private String msname;

    public JobFragment() {

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
        Log.v("scriptmanager","JogFragment:onViewCreated");
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

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                //callUnselectAll();
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
        return v;
    }

    public boolean isStarted() {
        return (started != null && stopped == null);
    }

    public void restoreView() {
        if( isStarted() ) {
            setViewStartJob();
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
        setViewStartJob();
        shell.execScript("test.sh");
        stopped = null;
        started = new Date();
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
    }
}