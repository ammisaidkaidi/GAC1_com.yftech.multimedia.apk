package com.yftech.car.widget.media.utils;

import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes3.dex */
public class FileUtil {
    private static final long GB = 1073741824;
    private static final int KB = 1024;
    private static final int MB = 1048576;
    private static final String TAG = "FileUtil";
    private static final long TB = 1099511627776L;

    private static long directoryFirst(File file, long j) {
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                j += file2.length();
                if (file2.isDirectory()) {
                    directoryFirst(file2, j);
                }
            }
        }
        return j;
    }

    private static String execRootCmd(String str) {
        String line = "";
        try {
            InputStream inputStream = Runtime.getRuntime().exec(str).getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            while (bufferedReader.read() > 0) {
                line = bufferedReader.readLine();
            }
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (IOException e) {
            Log.d(TAG, "exception " + e.toString());
            e.printStackTrace();
        }
        return line;
    }

    public static String fileSize(long j, int i) {
        if (j < 1024) {
            return j + "B";
        }
        if (j < 1048576) {
            if (i <= 0) {
                return (j / 1024) + "KB";
            }
            return getSizeValues(j / 1024.0d, i) + "KB";
        }
        if (j < 1073741824) {
            if (i <= 0) {
                return (j / 1048576) + "MB";
            }
            return getSizeValues(j / 1048576.0d, i) + "MB";
        }
        if (j < 1099511627776L) {
            if (i <= 0) {
                return (j / 1073741824) + "GB";
            }
            return getSizeValues(j / 1.073741824E9d, i) + "GB";
        }
        if (i <= 0) {
            return (j / 1099511627776L) + "TB";
        }
        return getSizeValues(j / 1.099511627776E12d, i) + "TB";
    }

    public static String folderFileSize(String str, int i) {
        return fileSize(getFolderFileSize(str), i);
    }

    public static long getFolderFileSize(String str) {
        if (str == null) {
            return 0L;
        }
        File file = new File(str);
        if (file.exists()) {
            return file.isDirectory() ? directoryFirst(file, file.length()) : file.length();
        }
        return 0L;
    }

    private static double getSizeValues(double d, int i) {
        switch (i) {
            case 1:
                return ((double) ((int) (d * 10.0d))) / 10.0d;
            case 2:
                return ((double) ((int) (d * 100.0d))) / 100.0d;
            case 3:
                return ((double) ((int) (d * 1000.0d))) / 1000.0d;
            case 4:
                return ((double) ((int) (d * 10000.0d))) / 10000.0d;
            case 5:
                return ((double) ((int) (d * 100000.0d))) / 100000.0d;
            default:
                return d;
        }
    }
}
