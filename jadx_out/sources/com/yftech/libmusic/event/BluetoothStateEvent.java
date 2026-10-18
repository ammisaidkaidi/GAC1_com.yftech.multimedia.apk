package com.yftech.libmusic.event;

import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes4.dex */
public class BluetoothStateEvent {
    public String deviceName;
    public int state;

    public BluetoothStateEvent(int i, String str) {
        this.state = i;
        this.deviceName = str;
    }

    @NonNull
    public String toString() {
        return "state=" + this.state + ", deviceName=" + this.deviceName;
    }
}
