package com.zht.launchstarter.utils;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class DisPatcherExecutor {
    private static ThreadPoolExecutor sCPUThreadPoolExecutor;
    private static ExecutorService sIOThreadPoolExecutor;

    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2,Math.min(CPU_COUNT-1,5));
    private static final int MAXIMUM_POOL_SIZE = CORE_POOL_SIZE;
    private static final int KEEP_ALIVE_SECONDS =5;
    private static final BlockingQueue<Runnable> sPoolWorkQuene = new LinkedBlockingQueue<>();
    private static final DefaultThreadFactory sThreadFactory = new DefaultThreadFactory();
    private static final RejectedExecutionHandler sHandler = new RejectedExecutionHandler() {// 一般不会到这里
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            Executors.newCachedThreadPool().execute(r);
        }
    };

    /**
     * 获取CPU线程池
     */

    public static ThreadPoolExecutor getCPUThreadPoolExecutor(){
        return sCPUThreadPoolExecutor;
    }

    public static ExecutorService getIOExecutor(){
        return sIOThreadPoolExecutor;
    }
    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        DefaultThreadFactory(){
            SecurityManager s  = System.getSecurityManager();
            group = (s !=null)?s.getThreadGroup():Thread.currentThread().getThreadGroup();
            namePrefix = "TaskDispatcherPool-" +
                    poolNumber.getAndIncrement() +
                    "-Thread-";
        }

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread  t =new Thread(group,r,namePrefix+threadNumber.getAndIncrement(),0);
            if(t.isDaemon())
                t.setDaemon(false);
            if(t.getPriority()!=Thread.NORM_PRIORITY)
                t.setPriority(Thread.NORM_PRIORITY);
            return t;
        }

        static {
            sCPUThreadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
                    MAXIMUM_POOL_SIZE,
                    KEEP_ALIVE_SECONDS,
                    TimeUnit.SECONDS,sPoolWorkQuene,sThreadFactory,sHandler);
            sCPUThreadPoolExecutor.allowCoreThreadTimeOut(true);
            sIOThreadPoolExecutor = Executors.newCachedThreadPool(sThreadFactory);
        }
    }
}
