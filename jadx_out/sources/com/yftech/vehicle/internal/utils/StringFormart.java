package com.yftech.vehicle.internal.utils;

import android.net.wifi.WifiEnterpriseConfig;

/* JADX INFO: loaded from: classes4.dex */
public class StringFormart {
    public static String byte2hex(byte[] bArr) {
        if (bArr == null) {
            return "byte[] null";
        }
        String str = "";
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                hexString = "0" + hexString;
            }
            str = str + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + hexString;
        }
        return str;
    }
}
