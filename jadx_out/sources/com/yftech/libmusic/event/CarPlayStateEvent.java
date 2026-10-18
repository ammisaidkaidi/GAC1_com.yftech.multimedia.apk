package com.yftech.libmusic.event;

import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes4.dex */
public class CarPlayStateEvent {
    public int status;

    public CarPlayStateEvent(int i) {
        this.status = i;
    }

    @NonNull
    public String toString() {
        return "status=" + this.status;
    }
}
