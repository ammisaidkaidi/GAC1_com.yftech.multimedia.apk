package com.yftech.libmusic.bean;

import android.net.wifi.WifiEnterpriseConfig;

/* JADX INFO: loaded from: classes4.dex */
public class LyricObject {
    public int begintime;
    public int endtime;
    public String lrc;
    public int timeline;

    public LyricObject() {
        this.lrc = WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
    }

    public LyricObject(String str) {
        this.lrc = str;
    }
}
