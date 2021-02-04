package com.releasestandard.scriptmanager.controller;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.releasestandard.scriptmanager.R;

public class TimeManagerView {

    public TimeManagerView() {

    }

    /*
     * handler called at the end of time picking.
     */
    public void onPicked(int minute,int hourOfDay,int dayOfMonth,int monthOfYear,int year) {

    }

    /**
     * compat 1
     * @param v
     * @param sched
     */
    public void show(View v, int []sched) {
        show(v,sched[0],sched[1],sched[2],sched[3],sched[4]);
    }
    public void show(View v, int minute, int hourOfDay, int dayOfMonth, int monthOfYear, int year) {
        ViewGroup vpg = (ViewGroup)v.getParent();
        TextView tv = vpg.findViewById(R.id.job_date_input);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),R.style.AppTheme_TimePicker,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),R.style.AppTheme_DatePicker,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        int temp_sched[] = {minute,hourOfDay,dayOfMonth,monthOfYear,year};
                                        tv.setText(com.releasestandard.scriptmanager.model.TimeManager.sched2str(temp_sched));
                                        onPicked(minute,hourOfDay,dayOfMonth,monthOfYear,year);
                                    }
                                }, year, monthOfYear, dayOfMonth);
                        datePickerDialog.show();
                    }
                }, hourOfDay, minute, false);
        timePickerDialog.show();
    }
}
