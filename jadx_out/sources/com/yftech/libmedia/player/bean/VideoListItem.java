package com.yftech.libmedia.player.bean;

/* JADX INFO: loaded from: classes4.dex */
public class VideoListItem extends MediaFileItem {
    private long duration;
    private long lastModify;
    private String suffix;
    private byte[] thumbData;
    private String title;

    public VideoListItem() {
        super(1);
    }

    public long getDuration() {
        return this.duration;
    }

    public long getLastModify() {
        return this.lastModify;
    }

    public String getSuffix() {
        return this.suffix;
    }

    public byte[] getThumbData() {
        return this.thumbData;
    }

    public String getTitle() {
        return this.title;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public void setLastModify(long j) {
        this.lastModify = j;
    }

    public void setSuffix(String str) {
        this.suffix = str;
    }

    public void setThumbData(byte[] bArr) {
        this.thumbData = bArr;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
