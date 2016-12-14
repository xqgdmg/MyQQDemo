package com.test.MyQQ.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ThinkPad on 2016/8/12.
 */
public class ThreadUtil {
    //创建线程池  线程池中的最大线程数是手机核心数加1
    private static final ExecutorService exector = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()+1);
    public static void runOnNewThread(Runnable runnable){
        exector.execute(runnable);
    }
}
