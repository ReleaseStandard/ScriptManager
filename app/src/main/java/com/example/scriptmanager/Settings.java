package com.example.scriptmanager;

/**
 * This object is used to store settings on the application.
 */

import java.util.HashMap;

public class Settings extends HashMap<String,String> {
    public void put(HashMap <String,String>settings) {
        for (String k : keySet())  {
            put(k,get(k));
        }
    }
}
