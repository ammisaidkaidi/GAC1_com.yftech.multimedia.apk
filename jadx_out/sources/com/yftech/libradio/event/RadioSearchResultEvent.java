package com.yftech.libradio.event;

import com.yftech.car.media.radio.entity.DataOfRadio;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class RadioSearchResultEvent {
    public boolean result;
    public List<DataOfRadio> resultList;

    public RadioSearchResultEvent(boolean z, List<DataOfRadio> list) {
        this.result = z;
        this.resultList = list;
    }
}
