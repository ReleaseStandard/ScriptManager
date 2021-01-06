package com.example.scriptmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.scriptmanager.R;

import java.util.ArrayList;
import java.util.List;

public class ViewJobsFragment extends Fragment {

    // list of Jobs
    public List<JobFragment> fragments = new ArrayList<JobFragment>();

    public ViewJobsFragment() {
        // Required empty public constructor
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // here we need registerJobs
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        if ( savedInstanceState == null ) {
            Log.v("scriptmanager","ViewJobsFragment:saveInstanceNull");
        }
        View v =  inflater.inflate(R.layout.view_jobs_fragment, container, false);
        Log.v("scriptmanager","ViewJobsFragment:onCreateView");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("scriptmanager","ViewJobsFragment:onViewCreated");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("scriptmanager","ViewJobsFragment:onSaveInstanceState");
    }




    // Tools
    public void stopAllFragments() {
        stopAllFragments(false);
    }
    public void stopAllFragments(boolean onlySelected) {
        for (JobFragment jf : fragments) {
            if( onlySelected)  {
                if(jf.isSelected) {
                    jf.stopJob();
                }
            }
            else
            {
                jf.stopJob();
            }
        }
    }

    public void unselectAllFragments() {
        for (JobFragment jf : fragments) {
            jf.unselectView();
        }
    }

    public int getNumberSelected() {
        int count = 0;
        for (JobFragment jf : fragments) {
            if ( jf.isSelected ) {
                count += 1;
            }
        }
        return count;
    }
}