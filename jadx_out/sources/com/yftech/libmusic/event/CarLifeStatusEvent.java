package com.yftech.libmusic.event;

import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes4.dex */
public class CarLifeStatusEvent {
    public boolean mIsCarLifeConnect;

    public CarLifeStatusEvent(boolean z) {
        this.mIsCarLifeConnect = z;
    }

    @NonNull
    public String toString() {
        return "mIsCarLifeConnect=" + this.mIsCarLifeConnect;
    }
}
