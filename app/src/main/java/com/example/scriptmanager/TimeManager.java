package com.releasestandard.scriptmanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.service.autofill.FieldClassification;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimeManager {

    public TimeManager() {

    }

    /*
     * handler called at the end of time picking.
     */
    public void onPicked(int minute,int hourOfDay,int dayOfMonth,int monthOfYear,int year) {

    }

    public void show(View v, int []sched) {
        show(v,sched[0],sched[1],sched[2],sched[3],sched[4]);
    }
    public void show(View v, int minute, int hourOfDay, int dayOfMonth, int monthOfYear, int year) {
        ViewGroup vpg = (ViewGroup)v.getParent();
        TextView tv = vpg.findViewById(R.id.job_date_input);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(v.getContext(),R.style.TimePickerTheme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {
                        DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),R.style.TimePickerTheme,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        int temp_sched[] = {minute,hourOfDay,dayOfMonth,monthOfYear,year};
                                        tv.setText(TimeManager.sched2str(temp_sched));
                                        onPicked(minute,hourOfDay,dayOfMonth,monthOfYear,year);
                                    }
                                }, year, monthOfYear, dayOfMonth);
                        datePickerDialog.show();
                    }
                }, hourOfDay, minute, false);
        timePickerDialog.show();
    }

    public static int[] str2sched(String s) {
        int sched[] = {JobFragment.EACH_TIME, JobFragment.EACH_TIME, JobFragment.EACH_TIME, JobFragment.EACH_TIME, JobFragment.EACH_TIME};
        int i = 0;
        String [] parts = s.split(" ");
        if ( parts.length == sched.length ) {
            for (String ss : parts) {
                if (ss.equals(new String("*"))) {
                    sched[i] = JobFragment.EACH_TIME;
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
            if ( sched[i] == JobFragment.EACH_TIME ) {
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
            if ( sched[i] == JobFragment.EACH_TIME ) {
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


    public static boolean validDate(String s) {
        String family = "0-9\\*";
        Pattern p = Pattern.compile(
                "^[ ]*["+family+
                "]+[ ]+["+family+"]+[ ]+["+family+
                "]+[ ]+["+family+"]+[ ]+["+family+"]+[ ]*$");
        Matcher m = p.matcher(s);
        return m.matches();
    }
}
