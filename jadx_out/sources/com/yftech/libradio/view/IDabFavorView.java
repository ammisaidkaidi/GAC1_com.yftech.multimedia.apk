package com.yftech.libradio.view;

import com.yftech.libmvpmodule.mvp.IMvpView;
import com.yftech.libradio.bean.DabChannelInfo;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IDabFavorView extends IMvpView {
    void displayFavorList(int i, List<DabChannelInfo> list);
}
