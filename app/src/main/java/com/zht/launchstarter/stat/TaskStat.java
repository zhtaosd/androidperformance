package com.zht.launchstarter.stat;

import com.zht.launchstarter.utils.DispatchLog;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskStat {
    private static volatile String sCurrentSituation="";
    private static List<TaskStatBean> sBeans = new ArrayList<>();
    private static AtomicInteger sTaskDoneCount = new AtomicInteger();
    private static boolean sOPenLaunchStat = false;

    public static String getCurrentSituation() {
        return sCurrentSituation;
    }

    public static void setCurrentSituation(String currentSituation) {
       if(!sOPenLaunchStat){
           return;
       }
        DispatchLog.i("currentSituation   " + currentSituation);
       sCurrentSituation = currentSituation;
       setLaunchStat();
    }

    private static void setLaunchStat() {
        TaskStatBean bean = new TaskStatBean();
        bean.setSituation(sCurrentSituation);
        bean.setCount(sTaskDoneCount.get());
        sBeans.add(bean);
        sTaskDoneCount = new AtomicInteger();
    }

    public static void markTaskDone() {
        sTaskDoneCount.getAndIncrement();
    }
}
