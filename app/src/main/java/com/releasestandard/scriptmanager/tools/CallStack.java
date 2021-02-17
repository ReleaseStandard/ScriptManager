package com.releasestandard.scriptmanager.tools;

import android.util.Log;

import java.util.Map;
import java.util.Set;

public class CallStack {


    public static String getLastCaller() {
        return getLastCaller(Thread.getAllStackTraces().entrySet());
    }

    public static String getLastCaller(Integer off) {
        return getLastCaller(Thread.getAllStackTraces().entrySet(), off);
    }
    public static String getLastCaller(Set<Map.Entry<Thread, StackTraceElement[]>> set) {
        return getLastCaller(set, 3);
    }

    private static String getLastCaller(Set<Map.Entry<Thread, StackTraceElement[]>> set, Integer offset) {
        for (Map.Entry<Thread, StackTraceElement[]> entry : set) {
            Integer n = offset;
            if (n < 0 || !entry.getKey().getName().equals("main")) {
                continue;
            }
            StackTraceElement[] es = entry.getValue();
            for( int a = 0; a < es.length; a = a + 1) {
                StackTraceElement ste = es[a];
                String s;
                if(ste.getClassName().lastIndexOf(Logger.packageid) != -1 &&
                        ste.getClassName().lastIndexOf(Logger.packageid + ".tools.Logger") == -1 &&
                        ste.getClassName().lastIndexOf(Logger.packageid + ".tools.CallStack") == -1 ) {
                    return clearName(ste.getClassName()) + "#" + clearName(ste.getMethodName());
                }
            }
        }
        return null;
    }

    private static String clearName(String name) {
        Integer i = name.lastIndexOf(".") + 1;
        if (i == -1) {
            return name;
        }
        return name.substring(i);
    }
}
