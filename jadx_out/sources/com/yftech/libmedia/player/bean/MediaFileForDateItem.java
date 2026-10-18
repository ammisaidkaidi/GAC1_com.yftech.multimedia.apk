package com.yftech.libmedia.player.bean;

import com.media.common.entity.QuickMultipleEntity;

/* JADX INFO: loaded from: classes4.dex */
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
        return 2;
    }

    public void setDate(String str) {
        this.date = str;
    }
}
