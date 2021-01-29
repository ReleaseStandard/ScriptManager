package com.releasestandard.scriptmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.releasestandard.scriptmanager.model.StorageManager;
import com.releasestandard.scriptmanager.tools.Logger;

import java.util.ArrayList;
import java.util.List;

public class JobsView extends Fragment {

    // list of Jobs
    public List<JobView> fragments = new ArrayList<JobView>();
    public List<SavedState> fragments_ss = new ArrayList<SavedState>();

    public StorageManager ptr_sm = null;

    public JobsView() {
        // Required empty public constructor
    }
    public JobsView(StorageManager ptr_sm) {
        this.ptr_sm = ptr_sm;
    }

    public static SettingsView newInstance(String param1, String param2) {
        SettingsView fragment = new SettingsView();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if ( savedInstanceState == null ) {
           Logger.debug( "savedInstanceState is null");
        }
        else{
            Logger.debug("savedInstanceState is not null");
        }
        super.onCreate(savedInstanceState);
    }
    public ArrayList<JobView>getSelecteds() {
        ArrayList<JobView> list = new ArrayList<>();
        for(JobView js : fragments) {
            if  (js.isSelected) {
                list.add(js);
            }
        }
        return list;
    }
    public JobView getSelected() {
        for(JobView js : fragments) {
            if(js.isSelected) {
                return js;
            }
        }
        return null;
    }
    // here we need registerJobs
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        Integer i = new Integer(fragments.size());
        Logger.debug("size in fragments : "+i);
        if ( savedInstanceState == null ) {
            Logger.debug("ViewJobsFragment:saveInstanceNull");
        }
        View v =  inflater.inflate(R.layout.jobs_view, container, false);
        Logger.debug("ViewJobsFragment:onCreateView");
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.debug("ViewJobsFragment:onViewCreated");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Logger.debug("ViewJobsFragment:onSaveInstanceState");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Integer in = new Integer(getFragmentManager().getFragments().size());
        Logger.debug("ViewJobsFragment:nb of frags "+in);
        Logger.debug("ViewJobsFragment:onViewStateRestored");
        restoreFragments();
    }

    public void restoreFragments() {
        // we need to restore the view for all childs
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        for(JobView jf : fragments) {
            ft.add(R.id.view_jobs_linearlayout, jf);
        }
        ft.commit();
    }

    // Tools
    public void stopAllFragments() {
        stopAllFragments(false);
    }

    public void stopAllFragments(boolean onlySelected) {
        for (JobView jf : fragments) {
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
        for (JobView jf : fragments) {
            jf.unselectView();
        }
        if ( getNumberSelected() == 0) {
            MainActivity main = (MainActivity) getActivity();
            main.ow_menu.leaveSelectMode();
        }
    }

    public int getNumberSelected() {
        int count = 0;
        for (JobView jf : fragments) {
            if ( jf.isSelected ) {
                count += 1;
            }
        }
        return count;
    }

    public int getNumberStartedAndSelected() {
        int count = 0;
        for (JobView jf : fragments) {
            if ( jf.isStarted() && jf.isSelected) {
                count += 1;
            }
        }
        return count;
    }

    public int getNumberStarted() {
        int count = 0;
        for (JobView jf : fragments) {
            if ( jf.isStarted()) {
                count += 1;
            }
        }
        return count;
    }


    public JobView addNewJob(){
        return addNewJob(null);
    }
    public JobView addNewJob(String scriptname) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        JobView f = null;
        if ( scriptname == null ) {
            f = new JobView(ptr_sm);
        }
        else {
            // don't create the files please
            f = new JobView(ptr_sm, scriptname);
        }
        ft.add(R.id.view_jobs_linearlayout, f);
        ft.commit();

        fragments.add(f);
        return f;
    }

    public void writeState() {
        for ( JobView jf : fragments) {
            jf.writeState();
        }
    }

    public void readState() {
        for(String scriptname : this.ptr_sm.getScriptsFromFilesystem()) {
            JobView jf = addNewJob(scriptname);
            jf.readState(getActivity(),scriptname);
            jf.dump();
        }
        if ( fragments.size() > 0) {
            JobView.fragmentCount = fragments.get(fragments.size() - 1).jd.id + 1;
        }
    }

}