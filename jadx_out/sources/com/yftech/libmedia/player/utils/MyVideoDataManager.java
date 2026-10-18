package com.yftech.libmedia.player.utils;

import com.yftech.car.media.data.entity.MediaFile;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class MyVideoDataManager {
    private static volatile MyVideoDataManager instance;
    private List<MediaFile> usb1Videos = new ArrayList();
    private List<MediaFile> usb2Videos = new ArrayList();

    private MyVideoDataManager() {
    }

    public static MyVideoDataManager getInstance() {
        if (instance == null) {
            synchronized (MyVideoDataManager.class) {
                if (instance == null) {
                    instance = new MyVideoDataManager();
                }
            }
        }
        return instance;
    }

    public List<MediaFile> getData(int i) {
        if (i == 0) {
            return this.usb1Videos;
        }
        if (i == 1) {
            return this.usb2Videos;
        }
        return null;
    }

    public void resetData(int i) {
        if (i == 0) {
            this.usb1Videos.clear();
        } else if (i == 1) {
            this.usb2Videos.clear();
        }
    }

    public void setNewData(int i, MediaFile mediaFile) {
        if (i == 0) {
            this.usb1Videos.add(mediaFile);
        } else if (i == 1) {
            this.usb2Videos.add(mediaFile);
        }
    }

    public void setNewData(int i, List<MediaFile> list) {
        if (i == 0) {
            this.usb1Videos.clear();
            this.usb2Videos.addAll(list);
        } else if (i == 1) {
            this.usb2Videos.clear();
            this.usb2Videos.addAll(list);
        }
    }
}
