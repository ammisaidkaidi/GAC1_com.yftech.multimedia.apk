package com.yftech.car.widget.media.music.lyrics;

import android.text.format.DateFormat;
import java.util.Arrays;

/* JADX INFO: loaded from: classes3.dex */
public class KrcPlayEntity {
    private String lyrics;
    private int[] playTime;

    public String getLyrics() {
        return this.lyrics;
    }

    public int[] getPlayTime() {
        return this.playTime;
    }

    public void setLyrics(String str) {
        this.lyrics = str;
    }

    public void setPlayTime(int[] iArr) {
        this.playTime = iArr;
    }

    public String toString() {
        return "KrcPlayEntity{playTime=" + Arrays.toString(this.playTime) + ", lyrics='" + this.lyrics + DateFormat.QUOTE + '}';
    }
}
