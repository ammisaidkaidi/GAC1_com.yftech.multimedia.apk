package com.yftech.libmusic.bean;

import android.text.format.DateFormat;
import com.yftech.car.media.data.entity.MediaFile;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MusicListItem {
    private String album;
    private String artist;
    private List<MusicListItem> data;
    private long duration;
    private String filePath;
    private String foloder;
    private boolean isDir;
    private String suffix;
    private byte[] thumbData;
    private String title;

    public MusicListItem() {
    }

    public MusicListItem(MediaFile mediaFile) {
        this.isDir = false;
        this.foloder = mediaFile.getFolder();
        this.duration = mediaFile.getDuration();
        this.title = mediaFile.getTitle();
        this.artist = mediaFile.getArtist();
        this.album = mediaFile.getAlbum();
        this.filePath = mediaFile.getData();
    }

    public String getAlbum() {
        return this.album;
    }

    public String getArtist() {
        return this.artist;
    }

    public List<MusicListItem> getData() {
        return this.data;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public String getFoloder() {
        return this.foloder;
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

    public boolean isDir() {
        return this.isDir;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public void setData(List<MusicListItem> list) {
        this.data = list;
    }

    public void setDir(boolean z) {
        this.isDir = z;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public void setFilePath(String str) {
        this.filePath = str;
    }

    public void setFoloder(String str) {
        this.foloder = str;
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

    public String toString() {
        return "MusicListItem{isDir=" + this.isDir + ", foloder='" + this.foloder + DateFormat.QUOTE + ", thumbData=" + Arrays.toString(this.thumbData) + ", duration=" + this.duration + ", title='" + this.title + DateFormat.QUOTE + ", artist='" + this.artist + DateFormat.QUOTE + ", album='" + this.album + DateFormat.QUOTE + ", suffix='" + this.suffix + DateFormat.QUOTE + ", filePath='" + this.filePath + DateFormat.QUOTE + ", data=" + this.data + '}';
    }
}
