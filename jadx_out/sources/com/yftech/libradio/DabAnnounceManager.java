package com.yftech.libradio;

/* JADX INFO: loaded from: classes4.dex */
public class DabAnnounceManager {
    private static final DabAnnounceManager instance = new DabAnnounceManager();
    private int annotype;
    private boolean isAnnouncePlaying;

    public static DabAnnounceManager getInstance() {
        return instance;
    }

    public int getAnnotype() {
        return this.annotype;
    }

    public boolean isAnnouncePlaying() {
        return this.isAnnouncePlaying;
    }

    public void setAnnotype(int i) {
        this.annotype = i;
    }

    public void setAnnouncePlaying(boolean z) {
        this.isAnnouncePlaying = z;
    }
}
