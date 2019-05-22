package com.zht.launchstarter.utils;

import android.util.Log;

public class DispatchLog {
    private static boolean sDebug = true;
    public static void i(String msg){
        if(!sDebug){
            return;
        }
        Log.i("task",msg);
    }
    public static boolean isDebug(){
        return sDebug;
    }

    public static void setDebug(boolean sDebug) {
        DispatchLog.sDebug = sDebug;
    }
}
