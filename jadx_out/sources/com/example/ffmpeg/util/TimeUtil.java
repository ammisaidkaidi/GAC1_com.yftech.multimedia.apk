package com.example.ffmpeg.util;

import android.provider.SettingsStringUtil;
import android.telephony.NetworkScanRequest;

/* JADX INFO: loaded from: classes3.dex */
public class TimeUtil {
    public static String secdsToDateFormat(int i, int i2) {
        long j = i / NetworkScanRequest.MAX_SEARCH_MAX_SEC;
        long j2 = (i % NetworkScanRequest.MAX_SEARCH_MAX_SEC) / 60;
        long j3 = i % 60;
        String str = "00";
        if (j > 0) {
            if (j < 10) {
                str = "0" + j;
            } else {
                str = j + "";
            }
        }
        String str2 = "00";
        if (j2 > 0) {
            if (j2 < 10) {
                str2 = "0" + j2;
            } else {
                str2 = j2 + "";
            }
        }
        String str3 = "00";
        if (j3 > 0) {
            if (j3 < 10) {
                str3 = "0" + j3;
            } else {
                str3 = j3 + "";
            }
        }
        if (i2 < 3600) {
            return str2 + SettingsStringUtil.DELIMITER + str3;
        }
        return str + SettingsStringUtil.DELIMITER + str2 + SettingsStringUtil.DELIMITER + str3;
    }
}
