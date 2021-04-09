package com.luckyxmobile.correction.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadUtils {

    private Handler handler;
    private ExecutorService service;

    private ThreadUtils() {
        service = Executors.newCachedThreadPool();
        handler = new Handler(Looper.getMainLooper());
    }

    private static final ThreadUtils threadUtils = new ThreadUtils();

    public static ThreadUtils getInstance() {
        return threadUtils;
    }

    public Handler getHandler() {
        return handler;
    }

    public ExecutorService getService() {
        return service;
    }
}
