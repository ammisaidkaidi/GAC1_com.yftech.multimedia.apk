package com.example.ffmpeg.bean;

/* JADX INFO: loaded from: classes3.dex */
public class TimeInfoBean {
    private int currentTime;
    private int totalTime;

    public int getCurrentTime() {
        return this.currentTime;
    }

    public int getTotalTime() {
        return this.totalTime;
    }

    public void setCurrentTime(int i) {
        this.currentTime = i;
    }

    public void setTotalTime(int i) {
        this.totalTime = i;
    }

    public String toString() {
        return "WlTimeInfoBean{currentTime=" + this.currentTime + ", totalTime=" + this.totalTime + '}';
    }
}
