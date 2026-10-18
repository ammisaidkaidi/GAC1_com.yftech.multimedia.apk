package com.media.common.util;

import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.security.keystore.KeyProperties;
import com.yftech.vehicle.internal.adapter.ICarConfig;
import com.yftech.vehicle.sdk.CarConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

/* JADX INFO: loaded from: classes3.dex */
public class SystemUtil {
    private static long lastClickTime;

    public static String bytesToHexString(byte[] bArr) {
        StringBuilder sb = new StringBuilder("");
        if (bArr == null || bArr.length <= 0) {
            return null;
        }
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() < 2) {
                sb.append(0);
            }
            sb.append(hexString);
        }
        return sb.toString();
    }

    public static String getFileMD5(String str) throws Throwable {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2 = null;
        if (str == null || str.trim().equals("")) {
            return null;
        }
        File file = new File(str);
        if (!file.isFile()) {
            return null;
        }
        byte[] bArr = new byte[1024];
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(KeyProperties.DIGEST_MD5);
            fileInputStream = new FileInputStream(file);
            while (true) {
                try {
                    try {
                        int i = fileInputStream.read(bArr, 0, 1024);
                        if (i == -1) {
                            break;
                        }
                        messageDigest.update(bArr, 0, i);
                    } catch (Exception e) {
                        e = e;
                        e.printStackTrace();
                        if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        return null;
                    }
                } catch (Throwable th) {
                    th = th;
                    fileInputStream2 = fileInputStream;
                }
                th = th;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (IOException e3) {
                        e3.printStackTrace();
                    }
                }
                throw th;
            }
            fileInputStream.close();
            try {
                fileInputStream.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            return bytesToHexString(messageDigest.digest());
        } catch (Exception e5) {
            e = e5;
            fileInputStream = null;
        } catch (Throwable th2) {
            th = th2;
        }
    }

    private static String getFileRoot(Context context) {
        File externalFilesDir;
        return (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || (externalFilesDir = context.getExternalFilesDir(null)) == null) ? context.getFilesDir().getAbsolutePath() : externalFilesDir.getAbsolutePath();
    }

    public static String getWallPaperOutputPath(Context context) {
        File file = new File(AppConstants.WallPaperOutputDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, AppConstants.WallPaperOutputName);
        if (!file2.exists()) {
            try {
                file2.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file2.getAbsolutePath();
    }

    public static boolean isA88Config() {
        return ICarConfig.VehiclePlatform.A88 == CarConfig.getVehiclePlatform();
    }

    public static boolean isFastDoubleClick() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Math.abs(jCurrentTimeMillis - lastClickTime) <= 800) {
            return true;
        }
        lastClickTime = jCurrentTimeMillis;
        return false;
    }

    public static void setSysFullScreen(Activity activity, boolean z) {
        if (activity.getWindow() == null) {
            LogUtil.i("cur window is null isFullScreen=" + z);
            return;
        }
        int windowVisibility = activity.getWindow().getDecorView().getWindowVisibility();
        activity.getWindow().getDecorView().setSystemUiVisibility(!z ? windowVisibility & (-3) & (-513) & (-5) & (-16385) : windowVisibility | 2 | 512 | 256 | 2048 | 4096 | 4 | 16384);
        LogUtil.d("setSysFullScreen called isFullScreen=" + z);
    }
}
