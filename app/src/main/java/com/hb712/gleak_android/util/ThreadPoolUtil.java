package com.hb712.gleak_android.util;


import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
    private static final ThreadPoolExecutor IO_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue);

    private static final ThreadPoolExecutor COMPUTE_THREAD_POOL_EXECUTOR = (ThreadPoolExecutor) Executors.newFixedThreadPool(DEVICE_PROCESSORS_COUNT);

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(CORE_POOL_SIZE);

    /**
     * IO 密集型线程创建
     *
     * @param runnable 线程执行体
     * @throws NullPointerException 当 runnable 为 null 时，抛出
     * @see ThreadPoolExecutor
     */
    public static void ioCommonExecute(Runnable runnable) throws NullPointerException {
        IO_THREAD_POOL_EXECUTOR.execute(runnable);
    }

    /**
     * 计算密集型线程创建
     *
     * @param runnable 线程执行体
     * @throws NullPointerException 当 runnable 为 null 时，抛出
     * @see ThreadPoolExecutor
     */
    public static void computeCommonExecute(Runnable runnable) throws NullPointerException {
        COMPUTE_THREAD_POOL_EXECUTOR.execute(runnable);
    }

    /**
     * 定时任务型线程创建
     *
     * @param runnable 线程执行体
     * @param delay    延迟时间
     * @param unit     延迟时间单位
     * @throws NullPointerException 当 runnable 为 null 时，抛出
     * @see ScheduledExecutorService
     */
    public static void scheduledCommonExecute(Runnable runnable, long delay, TimeUnit unit) throws NullPointerException {
        SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, delay, unit);
    }
}
