package com.william_l.wemore.Api;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by william on 2016/4/11.
 */
public class MyThreadPool {
    static ExecutorService cachedExecutor = null;

    public static ExecutorService getInstance() {

        if (null == cachedExecutor) {
            cachedExecutor = Executors.newCachedThreadPool();
            return cachedExecutor;
        } else {
            return cachedExecutor;
        }

    }

}
