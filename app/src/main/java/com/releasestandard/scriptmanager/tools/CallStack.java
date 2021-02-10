package com.releasestandard.scriptmanager.tools;

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
            System.out.println(entry.getKey().getName() + ":");
            Integer n = offset;
            if (n < 0 || !entry.getKey().getName().equals("main")) {
                continue;
            }
            System.out.println("n=" + n);
            for (StackTraceElement element : entry.getValue())
                System.out.println("\t" + element);
            return clearCallerName(entry.getValue()[n].toString());
        }
        return null;
    }

    /**
     * clear the name give by trace.
     * @param name
     * @return
     */
    private static String clearCallerName(String name) {
        Integer i = name.lastIndexOf("(");
        Integer beg = name.lastIndexOf(Logger.appname) + Logger.appname.length() + 1;
        if ( i < 0 ) {
            return name.substring(beg);
        }
        return name.substring(beg,i);
    }
}
