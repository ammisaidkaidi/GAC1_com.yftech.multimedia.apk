package com.yftech.libmusic.view;

import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.libmusic.bean.MusicListItem;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IMusicListActivityView extends IMvpView {
    void displayMusicList(MediaFile mediaFile, List<MusicListItem> list);
}
