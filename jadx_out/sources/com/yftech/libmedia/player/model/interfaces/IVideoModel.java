package com.yftech.libmedia.player.model.interfaces;

import com.media.common.entity.QuickMultipleEntity;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmvpmodule.BasePresenter;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IVideoModel {

    public interface OnVideoLoadListener {
        void onComplete(List<QuickMultipleEntity> list);
    }

    public interface OnVideoPlayListLoadListener {
        void onComplete(int i, List<MediaFile> list);
    }

    void getVideo(int i, BasePresenter<? extends IMvpView> basePresenter, OnVideoLoadListener onVideoLoadListener);

    void getVideoPlayList(int i, String str, BasePresenter<? extends IMvpView> basePresenter, OnVideoPlayListLoadListener onVideoPlayListLoadListener);
}
