package com.zht.launchstarter;

import android.content.Context;

import java.util.concurrent.CountDownLatch;

public class TaskDispatcher {
    private static Context sContext;
    private static boolean sIsMainProcess;


    public static Context getContext() {
        return sContext;
    }

    public static boolean isMainProcess() {
        return sIsMainProcess;
    }
}
