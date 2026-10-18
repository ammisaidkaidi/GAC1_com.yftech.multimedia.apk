package com.yftech.libmvpmodule.utils;

import android.app.Activity;
import android.util.Log;

/* JADX INFO: loaded from: classes4.dex */
public class SystemUtil {
    private static final String TAG = "SystemUtil";

    public static void setSysFullScreen(Activity activity, boolean z) {
        if (activity.getWindow() == null) {
            Log.i(TAG, "cur window is null isFullScreen=" + z);
            return;
        }
        int windowVisibility = activity.getWindow().getDecorView().getWindowVisibility();
        activity.getWindow().getDecorView().setSystemUiVisibility(!z ? windowVisibility & (-3) & (-513) & (-5) & (-16385) : windowVisibility | 2 | 512 | 256 | 2048 | 4096 | 4 | 16384);
        Log.d(TAG, "setSysFullScreen called isFullScreen=" + z);
    }
}
