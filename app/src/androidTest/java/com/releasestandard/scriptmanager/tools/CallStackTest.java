package com.releasestandard.scriptmanager.tools;
import junit.framework.TestCase;
import org.junit.Test;

public class CallStackTest extends TestCase {

    @Test
    public void testGetLastCaller() {
        String n = CallStack.getLastCaller();
        assertEquals(n, "testGetLastCaller");
    }

    public void testTestGetLastCaller() {

    }

}
