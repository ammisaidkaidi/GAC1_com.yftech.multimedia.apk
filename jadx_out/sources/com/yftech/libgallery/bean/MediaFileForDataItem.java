package com.yftech.libgallery.bean;

import com.media.common.entity.QuickMultipleEntity;
import com.yftech.car.media.data.entity.MediaFile;

/* JADX INFO: loaded from: classes.dex */
public class MediaFileForDataItem extends QuickMultipleEntity {
    private String album;
    private String artist;
    private int currentTime;
    private String data;
    private long duration;
    private String fileName;
    private String folder = "null";
    private String genre;
    private long lastModified;
    private long size;
    private String title;

    public MediaFileForDataItem() {
    }

    public MediaFileForDataItem(MediaFile mediaFile) {
        setAlbum(mediaFile.getAlbum());
        setArtist(mediaFile.getArtist());
        setCurrentTime(mediaFile.getCurrentTime());
        setData(mediaFile.getData());
        setDuration(mediaFile.getDuration());
        setFileName(mediaFile.getFileName());
        setFolder(mediaFile.getFolder());
        setGenre(mediaFile.getGenre());
        setFileName(mediaFile.getFileName());
        setSize(mediaFile.getSize());
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

    @Override // com.chad.library.adapter.base.entity.MultiItemEntity
    public int getItemType() {
        return 1;
    }

    public long getLastModified() {
        return this.lastModified;
    }

    public long getSize() {
        return this.size;
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

    public void setLastModified(long j) {
        this.lastModified = j;
    }

    public void setSize(long j) {
        this.size = j;
    }

    public void setTitle(String str) {
        this.title = str;
    }
}
