package com.yftech.multimedia.view;

import android.graphics.Bitmap;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmvpmodule.mvp.IMvpView;

/* JADX INFO: loaded from: classes4.dex */
public interface ISubjectFragmentView extends IMvpView {
    void displayPhotoFileExist(int i);

    void displayPhotoFileFrame(MediaFile mediaFile);

    void displayPhotoFileSize(int i);

    void displayVideoFileExist(int i);

    void displayVideoFileFrame(Bitmap bitmap);

    void displayVideoFileSize(int i);
}
