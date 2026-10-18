package com.yftech.libmusic.view;

import com.yftech.libmusic.bean.LyricObject;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IMusicLyricFragmentView extends IMvpView {
    void displayMusicLyricPath(List<LyricObject> list);
}
