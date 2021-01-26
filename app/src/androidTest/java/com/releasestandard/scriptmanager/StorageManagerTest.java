package com.releasestandard.scriptmanager;

import junit.framework.TestCase;

import java.lang.reflect.Field;

public class StorageManagerTest extends TestCase {

    Field script_name;

    public void setUp() throws Exception {
        super.setUp();

        script_name = StorageManager.class.
                getDeclaredField("script_name");

        script_name.setAccessible(true);
    }

    public void testSetScriptName() {
        String scriptname = "test";
        StorageManager sm = new StorageManager();
        sm.setScriptName(scriptname);
        assertNull(sm.getLogAbsolutePath());
        assertTrue(script_name.equals(scriptname));
    }

    public void testGetLogAbsolutePath() {
    }

    public void testTestGetLogAbsolutePath() {
    }

    public void testGetStateFileAbsolutePath() {
    }

    public void testTestGetStateFileAbsolutePath() {
    }

    public void testGetScriptAbsolutePath() {
    }

    public void testTestGetScriptAbsolutePath() {
    }

    public void testGetOutputAbsolutePath() {
    }

    public void testTestGetOutputAbsolutePath() {
    }

    public void testGetExternalAbsolutePath() {
    }

    public void testGetInternalAbsolutePath() {
    }

    public void testGetScriptsFromFilesystem() {
    }
}