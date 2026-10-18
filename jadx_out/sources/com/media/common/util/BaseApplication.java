package com.media.common.util;

import android.app.Application;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* JADX INFO: loaded from: classes3.dex */
public class BaseApplication extends Application {
    public static BaseApplication instance;
    public ExecutorService dataThreadPool;
    public ExecutorService fixedThreadPool;

    public static BaseApplication getInstance() {
        return instance;
    }

    public ExecutorService getDataThreadPool() {
        return this.dataThreadPool;
    }

    public ExecutorService getFixedThreadPool() {
        return this.fixedThreadPool;
    }

    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        instance = this;
        this.fixedThreadPool = Executors.newFixedThreadPool(1);
        this.dataThreadPool = Executors.newFixedThreadPool(1);
        ToastUtils.init(getApplicationContext());
    }
}
