package com.zht.launchstarter.task;

import android.os.Looper;
import android.os.Process;
import android.support.v4.os.TraceCompat;

import com.zht.launchstarter.TaskDispatcher;
import com.zht.launchstarter.stat.TaskStat;
import com.zht.launchstarter.utils.DispatchLog;

public class DispatchRunnable implements Runnable {
    private Task mTask;
    private TaskDispatcher mTaskDispatcher;

    public DispatchRunnable(Task mTask) {
        this.mTask = mTask;
    }

    public DispatchRunnable(Task mTask, TaskDispatcher mTaskDispatcher) {
        this.mTask = mTask;
        this.mTaskDispatcher = mTaskDispatcher;
    }

    @Override
    public void run() {
        TraceCompat.beginSection(mTask.getClass().getCanonicalName());
        DispatchLog.i(mTask.getClass().getSimpleName()
                + " begin run" + "  Situation  " + TaskStat.getCurrentSituation());
        Process.setThreadPriority(mTask.priority());
        long startTime = System.currentTimeMillis();
        mTask.setWating(true);
        mTask.waitToSatisfy();
        long waitTime = System.currentTimeMillis() - startTime;
        startTime = System.currentTimeMillis();
        mTask.setRunning(true);
        mTask.run();
        //执行Task的尾部任务
        Runnable tailRunnable = mTask.getTailRunnable();
        if(tailRunnable != null){
            tailRunnable.run();
        }
        if(!mTask.needCall()||!mTask.runOnMainThread()){
            printTaskLog(startTime,waitTime);
        }
        TaskStat.markTaskDone();
        mTask.setFinished(true);
        if(mTaskDispatcher!=null){

        }
        DispatchLog.i(mTask.getClass().getSimpleName() + " finish");
    }
    private void printTaskLog(long startTime, long waitTime) {
        long runTime = System.currentTimeMillis() - startTime;
        if (DispatchLog.isDebug()) {
            DispatchLog.i(mTask.getClass().getSimpleName() + "  wait " + waitTime + "    run "
                    + runTime + "   isMain " + (Looper.getMainLooper() == Looper.myLooper())
                    + "  needWait " + (mTask.needWait() || (Looper.getMainLooper() == Looper.myLooper()))
                    + "  ThreadId " + Thread.currentThread().getId()
                    + "  ThreadName " + Thread.currentThread().getName()
                    + "  Situation  " + TaskStat.getCurrentSituation()
            );
        }
    }
}
