package com.hb712.gleak_android.util;


import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/19 14:58
 */
public class ThreadPoolUtil {
    private static final int CORE_POOL_SIZE = 4;
    private static final int MAX_POOL_SIZE = 100;
    private static final int KEEP_ALIVE_TIME = 60;

    private static final int DEVICE_PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();

    private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);
    private static final ThreadPoolExecutor ioThreadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);

    public static void ioCommonExecute(Runnable runnable) {
        ioThreadPool.execute(runnable);
    }

    private static final ThreadPoolExecutor computeThreadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEVICE_PROCESSORS_COUNT);

    public static void computeCommonExecute(Runnable runnable) {
        computeThreadPool.execute(runnable);
    }
}
