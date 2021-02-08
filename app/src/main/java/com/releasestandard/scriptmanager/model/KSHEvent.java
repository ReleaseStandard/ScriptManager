package com.releasestandard.scriptmanager.model;

import android.util.Log;

import com.releasestandard.scriptmanager.tools.Logger;

import java.util.Random;

/**
 * How do we handle events from and to java.
 * hint: fs
 */
public class KSHEvent {

    private static Integer id = (new Random().nextInt());
    private Integer _id;   // id of the event
    public  String type;   // type of event eg SmsReceived
    public  String[] args; // values of arg0 arg1 eg +33600000000
    public  String base;   // base rep for output

    public KSHEvent(String base) {
        this.base = base + "/";
        this._id=this.id;
        this.id+=1;
    }
    public KSHEvent(String base, String type) {
        this.base = base + "/";
        this.type = type;
        this._id=this.id;
        this.id+=1;
    }
    public KSHEvent(String base, String type, String []args) {
        this.type = type;
        this.base = base + "/";
        this.args = new String[args.length];
        this._id=this.id;
        this.id += 1;
        for (int i = 0; i < args.length ; i = i + 1 ) { this.args[i] = args[i]; }
    }

    /**
     * Send the event from java to ksh
     * @return
     */
    public String sendJava2ksh() { return sendJava2ksh(args); }
    public String sendJava2ksh(String[] args) {
        Logger.debug("sendJava2ksh::args");
        for ( String arg : args ) { Logger.debug("sendJava2ksh::args -> " + arg); }
        String res = "echo \""+ type +"\" > " + getFunctNameFile() + " ";
        Integer i = 0;
        for ( String arg : args ) {
            String argfile = getArgFile(i);
            res += " && echo \"" + arg + "\" > " + argfile + " ";
            i += 1;
        }
        res += "";
        return res;
    }

    /**
     * Recv events (ALL events that are in the events directory) from java to ksh
     * @return
     */
    public String recvJava2ksh() { return recvJava2ksh("");}
    public String recvJava2ksh(String off) {
        String res = "" +
                off + "for event in $(ls " + this.base + ") ; do\n" +
                off + "     eventp=" + this.base + "/$event/;\n" +
                off + "     functname=$(cat \"" + getFunctNameFile(this.base + "/$event/") + "\" );\n" +
                off + "     ARGS=($(ls $eventp/arg*));\n" +
                off + "     i=0;\n" +
                off + "     while [ \"$i\" -lt \"${#ARGS[@]}\" ] ; do \n" +
                off + "          ARGS[$i]=\"$(cat ${ARGS[$i]})\";\n" +
                off + "          let i=i+1;\n" +
                off + "     done\n" +
                off + "     type $functname &> /dev/null && " +
                off + "         $functname \"${ARGS[@]}\" \n" +
                off + "     clearEvent $event\n" +
                off + "done\n" +
                off + "" ;

        Logger.debug("recvJava2ksh::res="+res);
        return res;
    }

    /**
     * Send event from ksh to java
     * @return
     */
    public String sendKsh2java() {
        Logger.debug("sendKsh2java not impemented");
        return "";
    }

    /**
     * Receive event from ksh to java
     * @return
     */
    public boolean recvKsh2java() {
        Logger.debug("recvKsh2java not impemented");
        return false;
    }
    private String getFunctNameFile() { return getFunctNameFile(null); }
    private String getFunctNameFile(String path) {
        String name = "/functnamefile";
        if (path == null) {
            return base + "/" + _id + name;
        }
        return path + name;
    }
    private String getArgFile(Integer i) {
        return base + "/" + _id + "/arg" + i;
    }
    public String[] getArgFiles() {
        String [] args = new String[this.args.length];
        for(int i = 0; i < this.args.length; i = i +1) {
            args[i] = getArgFile(i);
        }
        return args;
    }

    /**
     * clear the event from ksh.
     */
    public String clearEvent() { return clearEvent(""); }
    public String clearEvent(String off) {
        return  off + "function clearEvent() {\n" +
                off + "    id=$1;\n" +
                off + "    path=" + this.base + "/$id/;\n" +
                off + "    if [ -e \"$path\" ] ; then \n" +
                off + "         rm -rf \"$path\";\n" +
                off + "    fi\n" +
                off + "}\n";
    }

    /**
     * clear the event from java.
     */
    public void clear() {
        Logger.debug("clear not implemented : (function(){}) not allowed for direct cmd execution");
        //Shell._execCmd(clearEvent() + " \n clearEvent " + this._id + ";\n");
    }

    /**
     * Check prerequisites (folder creation, ...) from ksh
     * @return
     */
    public String checkPrereq() { return checkPrereq(""); }
    public String checkPrereq(String off) {
        return  off + "function checkPrereq() {\n" +
                off + "    id=$1\n" +
                off + "    mkdir -p \""+this.base+"/$id\"\n" +
                off + "}\n";
    }
    /**
     * check prerequisite from java.
     */
    public String prereq() {
        Logger.debug("prereq : " + this._id);
        String cmd= "mkdir -p \""+this.base+"/" + this._id + "\"";
        return cmd;
    }

    /**
     * Package function in a lib.
     * @return
     */
    public String packLib() { return packLib(""); }
    public String packLib(String off) {
        String res = "" +
                clearEvent(off) + "\n" +
                checkPrereq(off) + "\n";
        return res;
    }
}
