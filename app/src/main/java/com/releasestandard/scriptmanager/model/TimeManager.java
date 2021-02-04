package com.releasestandard.scriptmanager.model;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handle time conversions between different formats.
 * compat 1
 */
public class TimeManager {

    public final static Integer EACH_TIME = -1;

    public static int[] packIn(int minute, int hourOfDay, int dayOfMonth, int monthOfYear, int year) { int sched[]={minute,hourOfDay,dayOfMonth,monthOfYear,year} ; return sched ; }
    public static int[] str2sched(String s) {
        int sched[] = {EACH_TIME, EACH_TIME, EACH_TIME, EACH_TIME, EACH_TIME};
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

    public static Calendar getImmediate() {
        Calendar c = new GregorianCalendar();
        int off = 100;
        c.setTimeInMillis(c.getTimeInMillis() + off);
        return c;
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
