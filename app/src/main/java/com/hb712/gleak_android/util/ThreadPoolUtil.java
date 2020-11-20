package com.hb712.gleak_android.util;

import android.support.annotation.NonNull;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
    //阻塞队列。当核心线程都被占用，且阻塞队列已满的情况下，才会开启额外线程。
    private static final BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10);

    //线程工厂
    private static final ThreadFactory threadFactory = new ThreadFactory() {
        private final AtomicInteger integer = new AtomicInteger();
        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, "myThreadPool_" + integer.getAndIncrement());
        }
    };

    //线程池
    private static final ThreadPoolExecutor threadPool;

    static {
        //线程池核心线程数
        int CORE_POOL_SIZE = 5;
        //线程池最大线程数
        int MAX_POOL_SIZE = 100;
        //额外线程空状态生存时间
        int KEEP_ALIVE_TIME = 10000;
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, workQueue, threadFactory);
    }

    /**
     * 从线程池中抽取线程，执行指定的Runnable对象
     *
     * @param runnable 线程
     */
    public static void execute(Runnable runnable) {
        threadPool.execute(runnable);
    }
}
