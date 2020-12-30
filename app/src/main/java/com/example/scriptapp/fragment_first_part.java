package com.example.scriptapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link fragment_first_part#newInstance} factory method to
 * create an instance of this fragment.
 */
public class fragment_first_part extends Fragment {

    private static final String sname = "test";

    private String msname;

    public fragment_first_part() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param sname SName.
     * @return A new instance of fragment fragment_first_part.
     */
    public static fragment_first_part newInstance(String sname) {
        fragment_first_part fragment = new fragment_first_part();
        Bundle args = new Bundle();
        args.putString(fragment_first_part.sname, sname);
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
        return inflater.inflate(R.layout.fragment_first_part, container, false);
    }
}