package com.releasestandard.scriptmanager;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.releasestandard.scriptmanager.model.Shell;
import com.releasestandard.scriptmanager.tools.Logger;

import java.util.ArrayList;

public class JavaEventsReceiver extends BroadcastReceiver {

    private SharedPreferences preferences;
    public static ArrayList<Shell> listeners = new ArrayList<>();

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * compat 19
         */
        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String msg_from;
            Resources r = context.getResources();
            if (bundle != null){
                try{
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    msgs = new SmsMessage[pdus.length];
                    for(int i=0; i<msgs.length; i++){
                        if (Build.VERSION.SDK_INT >= 23) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], bundle.getString("format"));
                        } else {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        }
                        msg_from = msgs[i].getOriginatingAddress();
                        String msgBody = msgs[i].getMessageBody();
                        for (Shell s : listeners) {
                            if ( s == null ) { continue ; } // listener has been disabled
                            s.bi.triggerCallback(r.getString(R.string.callbackSmsReceived),msg_from,msgBody);
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace(Logger.getTraceStream());
                }
            }
        }
    }
}