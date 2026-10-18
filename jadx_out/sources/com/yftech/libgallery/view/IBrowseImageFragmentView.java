package com.yftech.libgallery.view;

import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IBrowseImageFragmentView extends IMvpView {
    void displayImageList(int i, List<MediaFile> list);
}
