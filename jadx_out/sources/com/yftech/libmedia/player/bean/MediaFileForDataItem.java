package com.yftech.libmedia.player.bean;

import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.media.common.entity.MediaFileInfo;
import com.media.common.entity.QuickMultipleEntity;

/* JADX INFO: loaded from: classes4.dex */
public class MediaFileForDataItem extends QuickMultipleEntity {
    private String album;
    private String artist;
    private int currentTime;
    private String data;
    private long duration;
    private String fileName;
    private String folder = "null";
    private String genre;
    private long id;
    private long lastModified;
    private long size;
    private byte[] thumbData;
    private String title;

    public MediaFileForDataItem() {
    }

    public MediaFileForDataItem(MediaFileInfo mediaFileInfo) {
        setAlbum(mediaFileInfo.getAlbum());
        setArtist(mediaFileInfo.getArtist());
        setCurrentTime(mediaFileInfo.getCurrentTime());
        setData(mediaFileInfo.getData());
        setDuration(mediaFileInfo.getDuration());
        setFileName(mediaFileInfo.getFileName());
        setFolder(mediaFileInfo.getFolder());
        setGenre(mediaFileInfo.getGenre());
        setFileName(mediaFileInfo.getFileName());
        setSize(mediaFileInfo.getSize());
        setId(mediaFileInfo.getId());
        setLastModified(mediaFileInfo.getLastModified());
        setThumbData(mediaFileInfo.getThumbData());
    }

    public boolean equals(@Nullable Object obj) {
        if (obj instanceof MediaFileForDataItem) {
            return !TextUtils.isEmpty(getData()) && getData().equals(((MediaFileForDataItem) obj).getData());
        }
        return false;
    }

    public String getAlbum() {
        return this.album;
    }

    public String getArtist() {
        return this.artist;
    }

    public int getCurrentTime() {
        return this.currentTime;
    }

    public String getData() {
        return this.data;
    }

    public long getDuration() {
        return this.duration;
    }

    public String getFileName() {
        return this.fileName;
    }

    public String getFolder() {
        return this.folder;
    }

    public String getGenre() {
        return this.genre;
    }

    public long getId() {
        return this.id;
    }

    @Override // com.chad.library.adapter.base.entity.MultiItemEntity
    public int getItemType() {
        return 3;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public long getSize() {
        return this.size;
    }

    public byte[] getThumbData() {
        return this.thumbData;
    }

    public String getTitle() {
        return this.title;
    }

    public void setAlbum(String str) {
        this.album = str;
    }

    public void setArtist(String str) {
        this.artist = str;
    }

    public void setCurrentTime(int i) {
        this.currentTime = i;
    }

    public void setData(String str) {
        this.data = str;
    }

    public void setDuration(long j) {
        this.duration = j;
    }

    public void setFileName(String str) {
        this.fileName = str;
    }

    public void setFolder(String str) {
        this.folder = str;
    }

    public void setGenre(String str) {
        this.genre = str;
    }

    public void setId(long j) {
        this.id = j;
    }

    public void setLastModified(long j) {
        this.lastModified = j;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public void setThumbData(byte[] bArr) {
        this.thumbData = bArr;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
