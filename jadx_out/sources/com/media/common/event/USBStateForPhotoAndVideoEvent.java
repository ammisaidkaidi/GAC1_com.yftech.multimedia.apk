package com.media.common.event;

import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes3.dex */
public class USBStateForPhotoAndVideoEvent {
    public boolean isUsbExists;
    public int usbIndex;

    public USBStateForPhotoAndVideoEvent(int i, boolean z) {
        this.usbIndex = i;
        this.isUsbExists = z;
    }

    @NonNull
    public String toString() {
        return "usbIndex=" + this.usbIndex + ", isUsbExists=" + this.isUsbExists;
    }
}
