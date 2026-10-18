package com.yftech.multimedia.model.interfaces;

import android.graphics.Bitmap;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmvpmodule.BasePresenter;
import com.yftech.libmvpmodule.mvp.IMvpView;

/* JADX INFO: loaded from: classes4.dex */
public interface ISubjectModel {

    public interface OnFileExistLoadListener {
        void onComplete(int i);
    }

    public interface OnFileSizeLoadListener {
        void onComplete(int i);
    }

    public interface OnPhotoFrameLoadListener {
        void onComplete(MediaFile mediaFile);
    }

    public interface OnVideoFrameLoadListener {
        void onComplete(Bitmap bitmap);
    }

    void getPhotoFileExist(int i, BasePresenter<? extends IMvpView> basePresenter, OnFileExistLoadListener onFileExistLoadListener);

    void getPhotoFileFrame(int i, BasePresenter<? extends IMvpView> basePresenter, OnPhotoFrameLoadListener onPhotoFrameLoadListener);

    void getPhotoFileSize(int i, BasePresenter<? extends IMvpView> basePresenter, OnFileSizeLoadListener onFileSizeLoadListener);

    void getVideoFileExist(int i, BasePresenter<? extends IMvpView> basePresenter, OnFileExistLoadListener onFileExistLoadListener);

    void getVideoFileFrame(int i, BasePresenter<? extends IMvpView> basePresenter, OnVideoFrameLoadListener onVideoFrameLoadListener);

    void getVideoFileSize(int i, BasePresenter<? extends IMvpView> basePresenter, OnFileSizeLoadListener onFileSizeLoadListener);
}
