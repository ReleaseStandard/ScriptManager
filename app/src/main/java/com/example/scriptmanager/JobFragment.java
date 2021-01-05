package com.example.scriptmanager;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.scriptmanager.R;

import java.io.IOException;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link JobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class JobFragment extends Fragment {

    private static final String sname = "test";

    private String msname;

    public JobFragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            msname = getArguments().getString(sname);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.job_fragment, container, false);

        v.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View arg0) {
                // need to change the background here
                //getActivity().setVisible();

                // Hightlight the ActionBar
                MainActivity main = (MainActivity) getActivity();
                ActionBar ab = main.getSupportActionBar();
                ab.setBackgroundDrawable(new ColorDrawable(android.R.attr.colorLongPressedHighlight));
                return true;
            }});

        View button = v.findViewById(R.id.floatingActionButton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)  {
                // launch the script here //
                Shell s = new Shell();
                s.execScript("test.sh");

                // Hightlight the ActionBar
                MainActivity main = (MainActivity) getActivity();
                ActionBar ab = main.getSupportActionBar();
                // => produce error //ab.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.attr.colorPrimaryVariant)));


            }
        });
        return v;
    }


}