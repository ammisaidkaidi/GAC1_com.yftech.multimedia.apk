package com.facebook.common.time;

import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
public interface Clock {
    public static final long MAX_TIME = Long.MAX_VALUE;

    long now();
}
