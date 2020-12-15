package com.hb712.gleak_android.util;


import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hiYuzu
 * @version V1.0
 * @date 2020/11/19 14:58
 */
public class ThreadPoolUtil {

    private ThreadPoolUtil() {
    }

    private static class SingletonHolder {
        private static final ThreadPoolUtil INSTANCE = new ThreadPoolUtil();
    }

    public static ThreadPoolUtil getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private final int CORE_POOL_SIZE = 4;
    private final int MAX_POOL_SIZE = 100;
    private final int KEEP_ALIVE_TIME = 60;

    private final int DEVICE_PROCESSORS_COUNT = Runtime.getRuntime().availableProcessors();

    private final ThreadFactory ioThreadFactory = new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "IOThread-" + threadNumber.getAndIncrement());
        }
    };

    private final ThreadFactory comThreadFactory = new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ComThread-" + threadNumber.getAndIncrement());
        }
    };

    private final ThreadFactory scheduleThreadFactory  = new ThreadFactory() {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "ScheduleThread-" + threadNumber.getAndIncrement());
        }
    };

    private final ThreadPoolExecutor IO_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10), ioThreadFactory);

    private final ThreadPoolExecutor COMPUTE_THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(DEVICE_PROCESSORS_COUNT, DEVICE_PROCESSORS_COUNT, 0, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(100), comThreadFactory);

    private final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = new ScheduledThreadPoolExecutor(CORE_POOL_SIZE, scheduleThreadFactory);

    /**
     * IO 密集型线程创建
     *
     * @param runnable 线程执行体
     * @throws NullPointerException 当 runnable 为 null 时，抛出
     * @see ThreadPoolExecutor
     */
    public void ioCommonExecute(Runnable runnable) throws NullPointerException {
        IO_THREAD_POOL_EXECUTOR.execute(runnable);
    }

    /**
     * 计算密集型线程创建
     *
     * @param runnable 线程执行体
     * @throws NullPointerException 当 runnable 为 null 时，抛出
     * @see ThreadPoolExecutor
     */
    public void computeCommonExecute(Runnable runnable) throws NullPointerException {
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
    public void scheduledCommonExecute(Runnable runnable, long delay, TimeUnit unit) throws NullPointerException {
        SCHEDULED_EXECUTOR_SERVICE.schedule(runnable, delay, unit);
    }

}
