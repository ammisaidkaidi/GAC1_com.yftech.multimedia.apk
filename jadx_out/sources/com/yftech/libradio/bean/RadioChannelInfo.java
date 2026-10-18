package com.yftech.libradio.bean;

import com.yftech.car.media.radio.entity.DataOfRadio;

/* JADX INFO: loaded from: classes4.dex */
public class RadioChannelInfo extends DataOfRadio {
    public boolean isValid;

    public RadioChannelInfo() {
    }

    public RadioChannelInfo(DataOfRadio dataOfRadio) {
        setFreq(dataOfRadio.getFreq());
        setName(dataOfRadio.getName());
        setType(dataOfRadio.getType());
    }
}
