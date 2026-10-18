package com.yftech.libradio.view;

import com.yftech.car.media.radio.entity.DataOfRadio;
import com.yftech.libmvpmodule.mvp.IMvpView;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface IRadioSearchChannelDialogView extends IMvpView {
    void displaySearchList(int i, List<DataOfRadio> list);
}
