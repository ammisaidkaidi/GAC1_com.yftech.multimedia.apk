package com.yftech.libgallery.bean;

import com.media.common.entity.QuickMultipleEntity;

/* JADX INFO: loaded from: classes.dex */
public class MediaFileForDateItem extends QuickMultipleEntity {
    private String date;

    public MediaFileForDateItem() {
    }

    public MediaFileForDateItem(String str) {
        this.date = str;
    }

    public String getDate() {
        return this.date;
    }

    @Override // com.chad.library.adapter.base.entity.MultiItemEntity
    public int getItemType() {
        return 0;
    }

    public void setDate(String str) {
        this.date = str;
    }
}
