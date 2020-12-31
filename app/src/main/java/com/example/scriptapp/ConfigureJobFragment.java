package com.example.scriptapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfigureJobFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfigureJobFragment extends Fragment {

    private static final String sname = "test";

    private String msname;

    public ConfigureJobFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sname SName.
     * @return A new instance of fragment fragment_first_part.
     */
    public static ConfigureJobFragment newInstance(String sname) {
        ConfigureJobFragment fragment = new ConfigureJobFragment();
        Bundle args = new Bundle();
        args.putString(ConfigureJobFragment.sname, sname);
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
        return inflater.inflate(R.layout.configure_job_fragment, container, false);
    }


}