package com.media.common.event;

/* JADX INFO: loaded from: classes3.dex */
public class MediaDataCallbackForScanStatusChangedEvent {
    public boolean isScanner;
    public int usbIndex;

    public MediaDataCallbackForScanStatusChangedEvent(int i, boolean z) {
        this.usbIndex = i;
        this.isScanner = z;
    }
}
