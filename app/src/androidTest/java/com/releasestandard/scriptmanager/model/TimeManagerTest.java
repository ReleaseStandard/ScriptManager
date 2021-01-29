package com.releasestandard.scriptmanager.model;

import com.releasestandard.scriptmanager.JobView;
import com.releasestandard.scriptmanager.tools.Logger;

import junit.framework.TestCase;

import java.util.Random;

import static com.releasestandard.scriptmanager.model.TimeManager.isRepeated;
import static com.releasestandard.scriptmanager.model.TimeManager.sched2str;
import static com.releasestandard.scriptmanager.model.TimeManager.str2sched;
import static com.releasestandard.scriptmanager.model.TimeManager.validDate;

public class TimeManagerTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();
    }

    public void testStr2sched() {
        {
            String s1 = "1 2 3 4 5";
            int[] expected = new int[]{1, 2, 3, 4, 5};
            int[] sched = str2sched(s1);
            assertEquals(sched, expected);
        }
        {
            String s = "* 2 3 4 5";
            int[] expected = new int[]{JobView.EACH_TIME, 2, 3, 4, 5};
            int[] sched = str2sched(s);
            assertEquals(sched, expected);
        }
    }

    public void testSched2str() {
        {
            String s1 = "1 2 3 4 5";
            int[] sched = new int[]{1, 2, 3, 4, 5};
            String expected = sched2str(sched);
            assertEquals(expected,s1);
        }
        {
            String s1 = "* 2 3 4 5";
            int[] sched = new int[]{JobView.EACH_TIME, 2, 3, 4, 5};
            String expected = sched2str(sched);
            assertEquals(expected,s1);
        }
    }

    public void testSymetry() {
        Random r = new Random();
        for ( int i = 0; i < 10 ; i = i + 1) {
            int [] sched = new int[5];
            for ( int j = 0; j < 5 ; j = j + 1) {
                int x = r.nextInt();
                if ( x < 0 ) { x = x * -1; }
                sched[j] = x;
            }
            String sched_ = sched2str(sched);
            Logger.debug("sched_=" + sched_);
            assertEquals(str2sched(sched_),sched);
        }
    }

    public void testIsRepeated() {
        String s1 = "* 2 3 4 5";
        assertEquals(isRepeated(str2sched(s1)),true);
        String s2 = "* * * * *";
        assertEquals(isRepeated(str2sched(s2)),true);
        String s3 = "1 2 3 4 5";
        assertEquals(isRepeated(str2sched(s3)),false);
    }

    public void testNextSched() {
    }
    public void testValidDate() {
        String [] valid = new String[] {
                "* 1 2 3 4",
                "1 2 3 4 5",
                "* * * * *",
                "0 1 2 3 4",
                "0 * 1 2 3"
        };
        for(String s : valid) {
            String ss = sched2str(str2sched(s));
            assertEquals(validDate(ss),true);
        }
    }
}