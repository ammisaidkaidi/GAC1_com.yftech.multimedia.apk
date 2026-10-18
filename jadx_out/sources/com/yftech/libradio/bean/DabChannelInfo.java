package com.yftech.libradio.bean;

import com.yftech.car.media.radio.dab.entity.DabLabel;
import com.yftech.car.media.radio.dab.entity.DabProperties;

/* JADX INFO: loaded from: classes4.dex */
public class DabChannelInfo {
    private boolean caApplied;
    private int caId;
    private int dynPty;
    private int ensembleId;
    private DabLabel ensembleLabel;
    private int ensembleTab;
    private long frequency;
    private byte[] imageData;
    private int internTableIdDpty;
    private int internTableIdSpty;
    private boolean isEpg;
    private byte pdFlag;
    private int propertiesTab;
    private DabLabel propertieslabel;
    private long serviceId;
    private int statPty;

    public DabChannelInfo(long j, int i, DabLabel dabLabel, DabProperties dabProperties, int i2) {
        this.frequency = j;
        this.ensembleId = i;
        this.ensembleLabel = dabLabel;
        this.ensembleTab = i2;
        this.pdFlag = dabProperties.getPdFlag();
        this.serviceId = dabProperties.getServiceId();
        this.propertieslabel = dabProperties.getLabel();
        this.caId = dabProperties.getCaId();
        this.caApplied = dabProperties.isCaApplied();
        this.internTableIdSpty = dabProperties.getInternTableIdSpty();
        this.statPty = dabProperties.getStatPty();
        this.internTableIdDpty = dabProperties.getInternTableIdDpty();
        this.dynPty = dabProperties.getDynPty();
        this.propertiesTab = dabProperties.getTab();
    }

    public DabChannelInfo(DabChannelInfo dabChannelInfo) {
        if (dabChannelInfo != null) {
            this.frequency = dabChannelInfo.getFrequency();
            this.ensembleId = dabChannelInfo.getEnsembleId();
            this.ensembleLabel = dabChannelInfo.getEnsembleLabel();
            this.ensembleTab = dabChannelInfo.getEnsembleTab();
            this.pdFlag = dabChannelInfo.getPdFlag();
            this.serviceId = dabChannelInfo.getServiceId();
            this.propertieslabel = dabChannelInfo.getPropertieslabel();
            this.caId = dabChannelInfo.getCaId();
            this.caApplied = dabChannelInfo.isCaApplied();
            this.internTableIdSpty = dabChannelInfo.getInternTableIdSpty();
            this.statPty = dabChannelInfo.getStatPty();
            this.internTableIdDpty = dabChannelInfo.getInternTableIdDpty();
            this.dynPty = dabChannelInfo.getDynPty();
            this.propertiesTab = dabChannelInfo.getPropertiesTab();
        }
    }

    public int getCaId() {
        return this.caId;
    }

    public int getDynPty() {
        return this.dynPty;
    }

    public int getEnsembleId() {
        return this.ensembleId;
    }

    public DabLabel getEnsembleLabel() {
        return this.ensembleLabel;
    }

    public int getEnsembleTab() {
        return this.ensembleTab;
    }

    public long getFrequency() {
        return this.frequency;
    }

    public byte[] getImageData() {
        return this.imageData;
    }

    public int getInternTableIdDpty() {
        return this.internTableIdDpty;
    }

    public int getInternTableIdSpty() {
        return this.internTableIdSpty;
    }

    public byte getPdFlag() {
        return this.pdFlag;
    }

    public int getPropertiesTab() {
        return this.propertiesTab;
    }

    public DabLabel getPropertieslabel() {
        return this.propertieslabel;
    }

    public long getServiceId() {
        return this.serviceId;
    }

    public int getStatPty() {
        return this.statPty;
    }

    public boolean isCaApplied() {
        return this.caApplied;
    }

    public boolean isEpg() {
        return this.isEpg;
    }

    public void setEpg(boolean z) {
        this.isEpg = z;
    }

    public void setImageData(byte[] bArr) {
        this.imageData = bArr;
    }
}
