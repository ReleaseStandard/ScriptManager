package com.example.scriptapp;

/**
 * This object is used to store settings on the application.
 */

import java.util.HashMap;

public class Settings {
    private HashMap<String,String> settings;
    public String get(String k) {
        return settings.get(k);
    }
    public void set(String k, String v) {
        this.settings.put(k,v);
    }
    public void set(HashMap <String,String>settings) {
        for (String k : settings.keySet())  {
            this.set(k,settings.get(k));
        }
    }
}
