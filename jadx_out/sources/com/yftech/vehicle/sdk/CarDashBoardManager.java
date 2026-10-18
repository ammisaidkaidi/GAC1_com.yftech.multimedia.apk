package com.yftech.vehicle.sdk;

import android.drm.DrmManagerClient;
import android.os.SystemProperties;
import android.util.Log;
import com.yftech.vehicle.internal.BaseApiManager;
import com.yftech.vehicle.internal.SignalReceiver;
import com.yftech.vehicle.internal.adapter.ICarDashBoard;
import com.yftech.vehicle.internal.adapter.IMpuVersion;
import com.yftech.vehicle.internal.project.Ihu12DashBoard;
import com.yftech.vehicle.internal.utils.StringFormart;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;

/* JADX INFO: loaded from: classes4.dex */
public class CarDashBoardManager extends BaseApiManager implements ICarDashBoard {
    private static final String TAG = "CarDashBoardManager";
    private static CarDashBoardManager sMe;
    private ICarDashBoard.DashBoardType mDashBoardType;

    public static abstract class CarDashBoardListener {
        @SignalReceiver(signalId = 9947)
        public void onDashBoardPhoneActionMsgChanged(ICarDashBoard.PhoneAction phoneAction) {
        }

        @SignalReceiver(signalId = ICarDashBoard.DASH_BOARD_SETTING_ITEM_UNIT)
        public void onDashBoardSettingItemInfoChanged(ICarDashBoard.SetItemUnitInfo setItemUnitInfo) {
        }

        @SignalReceiver(signalId = ICarDashBoard.DASH_BOARD_SETTING_ITEM_LANGUAGE)
        public void onDashBoardSettingItemLanguageChanged(int i) {
        }

        @SignalReceiver(signalId = 9978)
        public void onDashBoardSourceChanged(int i) {
        }

        @SignalReceiver(signalId = 9945)
        public void onDashBoardTimeFormatChanged(ICarDashBoard.TimeFormatMode timeFormatMode) {
        }
    }

    private CarDashBoardManager(ICarDashBoard.DashBoardType dashBoardType) {
        this.mDashBoardType = ICarDashBoard.DashBoardType.MCU_CA1105;
        this.mDashBoardType = dashBoardType;
    }

    public static synchronized CarDashBoardManager get() {
        try {
            String str = SystemProperties.get(IMpuVersion.PROP_KEY_CAR_KIT_PRODUCT_NAME);
            if (sMe == null) {
                if (str == null || !(str.startsWith("CBIHU") || str.startsWith("CB_IHU"))) {
                    sMe = new CarDashBoardManager(ICarDashBoard.DashBoardType.MCU_CA1105);
                } else {
                    sMe = new CarDashBoardManager(ICarDashBoard.DashBoardType.MCU_IHU12);
                }
            }
        } catch (Exception unused) {
        }
        return sMe;
    }

    public int getDashBoardLanguage() {
        return getSignalValue(ICarDashBoard.DASH_BOARD_SETTING_ITEM_LANGUAGE).intValue();
    }

    public int getDashBoardSource() {
        return getSignalValue(9978).intValue();
    }

    public ICarDashBoard.SetItemUnitInfo getSetItemUnitInfo() {
        return ICarDashBoard.SetItemUnitInfo.valueOf(getSignalDataValue(ICarDashBoard.DASH_BOARD_SETTING_ITEM_UNIT));
    }

    public void registerListener(CarDashBoardListener carDashBoardListener) {
        registerReceiver(carDashBoardListener);
    }

    public boolean setDashBoardAudioSourceEnableInfo(ICarDashBoard.AudioSourceEnableInfo audioSourceEnableInfo) {
        try {
            if (audioSourceEnableInfo != null) {
                return setSignalValue(9948, audioSourceEnableInfo.getDataArray());
            }
            Log.e(TAG, "setDashBoardAudioSourceEnableInfo()  info is null ");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "setDashBoardAudioSourceEnableInfo() Exception ", e);
            return false;
        }
    }

    public boolean setDashBoardFMFreqInfo(int i) {
        if (i == 50) {
            return setSignalValue(533, 1);
        }
        if (i == 100) {
            return setSignalValue(533, 0);
        }
        Log.i(TAG, "setDashBoardFMFreqInfo: illegality freq");
        return false;
    }

    public boolean setDashBoardItemUnit(ICarDashBoard.SetItemUnitInfo setItemUnitInfo) {
        if (setItemUnitInfo == null) {
            return false;
        }
        try {
            byte[] dashBoardItemUnitByteArray = ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).getDashBoardItemUnitByteArray(setItemUnitInfo);
            Log.d(TAG, StringFormart.byte2hex(dashBoardItemUnitByteArray));
            if (dashBoardItemUnitByteArray != null) {
                return setSignalValue(ICarDashBoard.DASH_BOARD_SETTING_ITEM_UNIT, dashBoardItemUnitByteArray);
            }
            Log.w(TAG, "setDashBoardItem() format data error ! ");
            return false;
        } catch (Exception e) {
            Log.e(TAG, "setDashBoardItemUnit()  Exception ", e);
            return false;
        }
    }

    public boolean setDashBoardLanguage(int i) {
        try {
            if (i == 0) {
                Log.w(TAG, "setDashBoardLanguage() language ==0 unknown ! ");
                return false;
            }
            byte[] dashBoardLanguageByteArray = ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).getDashBoardLanguageByteArray(i);
            Log.d(TAG, StringFormart.byte2hex(dashBoardLanguageByteArray));
            return setSignalValue(ICarDashBoard.DASH_BOARD_SETTING_ITEM_LANGUAGE, dashBoardLanguageByteArray);
        } catch (Exception e) {
            Log.e(TAG, "setDashBoardLanguage()  Exception ", e);
            return false;
        }
    }

    public boolean setDashBoardMsgNotify(ICarDashBoard.AbsDashBoardMsg absDashBoardMsg) {
        byte[] dashBoardMsgDataByteArray = ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).getDashBoardMsgDataByteArray(absDashBoardMsg);
        if (dashBoardMsgDataByteArray == null) {
            Log.w(TAG, "setDashBoardMsgNotify() byteArr is null");
            return false;
        }
        Log.d(TAG, "setDashBoardMsgNotify-> [" + StringFormart.byte2hex(dashBoardMsgDataByteArray) + " ]");
        return setSignalValue(9961, dashBoardMsgDataByteArray);
    }

    public boolean setDashBoardSyncSystemTime(Calendar calendar, boolean z) {
        byte b = z ? (byte) 1 : (byte) 0;
        int i = (byte) calendar.get(z ? 11 : 10);
        Log.i(TAG, "setDashBoardSyncSystemTime() isHour24Val=" + ((int) b) + ", hourVal =" + i);
        if (!z) {
            boolean z2 = calendar.get(9) == 0;
            Log.i(TAG, "setDashBoardSyncSystemTime() isAm=" + z2 + ", am_pm=" + calendar.get(9));
            if (i == 0) {
                i = z2 ? 0 : 12;
            } else if (!z2) {
                i += 12;
            }
        }
        return setSignalValue(9946, new byte[]{(byte) (calendar.get(1) + DrmManagerClient.ERROR_UNKNOWN), (byte) (calendar.get(2) + 1), (byte) calendar.get(5), (byte) i, (byte) calendar.get(12), (byte) calendar.get(13), b});
    }

    public boolean setSendBtInfoToDashBoardForTboxCall(final ICarDashBoard.BtInfo btInfo) {
        if (btInfo == null) {
            Log.w(TAG, "MCU_IHU12 setSendBtInfoToDashBoardForTboxCall -ECall() byteArr is null");
            return false;
        }
        byte[] mediaInfo = Ihu12DashBoard.getMediaInfo((byte) 3, btInfo);
        StringBuilder sb = new StringBuilder();
        sb.append("MCU_IHU12 setSendBtInfoToDashBoardForTboxCall->INFO_TYPE_PHONE_INFO= ");
        sb.append(btInfo != null ? btInfo.getDashBoardSource() : -1);
        sb.append(",[");
        sb.append(StringFormart.byte2hex(mediaInfo));
        sb.append(" ]");
        Log.d(TAG, sb.toString());
        setSignalValue(9939, mediaInfo);
        if (btInfo == null || btInfo.mBtStatus != 5) {
            return true;
        }
        btInfo.mBtStatus = (byte) -1;
        new Thread(new Runnable() { // from class: com.yftech.vehicle.sdk.CarDashBoardManager.2
            @Override // java.lang.Runnable
            public void run() {
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException unused) {
                }
                byte[] mediaInfo2 = Ihu12DashBoard.getMediaInfo((byte) 3, btInfo);
                StringBuilder sb2 = new StringBuilder();
                sb2.append("MCU_IHU12 setSendDashBoardInfo->STATUS_ENDING_CALL= ");
                sb2.append(btInfo != null ? btInfo.getDashBoardSource() : -1);
                sb2.append(",[");
                sb2.append(StringFormart.byte2hex(mediaInfo2));
                sb2.append(" ]");
                Log.d(CarDashBoardManager.TAG, sb2.toString());
                CarDashBoardManager.this.setSignalValue(9939, mediaInfo2);
            }
        }).start();
        return true;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:56:0x0189 A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:58:0x0191 A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:60:0x0199 A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:63:0x01cc A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:65:0x01de A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    /* JADX WARN: Code duplicated, block: B:66:0x01e3  */
    /* JADX WARN: Code duplicated, block: B:69:0x0215 A[Catch: Exception -> 0x0280, TryCatch #0 {Exception -> 0x0280, blocks: (B:3:0x0001, B:6:0x000e, B:8:0x0016, B:10:0x0028, B:12:0x002e, B:14:0x0057, B:15:0x005a, B:17:0x005f, B:19:0x0063, B:21:0x006b, B:23:0x0081, B:25:0x0087, B:27:0x00ac, B:29:0x00be, B:31:0x00c4, B:33:0x00e4, B:35:0x00e8, B:37:0x00f2, B:38:0x00f6, B:40:0x0109, B:42:0x010f, B:44:0x012f, B:46:0x0134, B:48:0x0147, B:50:0x014d, B:52:0x016d, B:54:0x0171, B:55:0x0180, B:56:0x0189, B:58:0x0191, B:61:0x01a4, B:63:0x01cc, B:65:0x01de, B:67:0x01e4, B:69:0x0215, B:70:0x0219, B:60:0x0199, B:72:0x0238, B:74:0x0244, B:76:0x024c, B:78:0x025a, B:79:0x025e), top: B:84:0x0001 }] */
    public boolean setSendDashBoardInfo(final ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        boolean zIsMediaChange;
        int dashBoardSource;
        try {
            if (!ICarDashBoard.DashBoardType.MCU_IHU12.equals(this.mDashBoardType)) {
                byte[] dashBoardInfoDataByteArray = ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).getDashBoardInfoDataByteArray(absDashBoardInfo);
                if (dashBoardInfoDataByteArray == null) {
                    Log.w(TAG, "setSendDashBoardInfo() byteArr is null");
                    return false;
                }
                StringBuilder sb = new StringBuilder();
                sb.append("setSendDashBoardInfo->source= ");
                sb.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                sb.append(",[");
                sb.append(StringFormart.byte2hex(dashBoardInfoDataByteArray));
                sb.append(" ]");
                Log.d(TAG, sb.toString());
                return setSignalValue(9986, dashBoardInfoDataByteArray);
            }
            if (absDashBoardInfo == null) {
                Log.w(TAG, "MCU_IHU12 setSendDashBoardInfo() byteArr is null");
                return false;
            }
            byte[] dashBoardInfoDataByteArrayForIhu12 = Ihu12DashBoard.getDashBoardInfoDataByteArrayForIhu12(absDashBoardInfo);
            StringBuilder sb2 = new StringBuilder();
            sb2.append("MCU_IHU12 setSendDashBoardInfo->DashBoardInfo= ");
            sb2.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
            sb2.append(",[");
            sb2.append(StringFormart.byte2hex(dashBoardInfoDataByteArrayForIhu12));
            sb2.append(" ]");
            Log.d(TAG, sb2.toString());
            setSignalValue(9939, dashBoardInfoDataByteArrayForIhu12);
            int dashBoardSource2 = absDashBoardInfo.getDashBoardSource();
            if (dashBoardSource2 != 9) {
                switch (dashBoardSource2) {
                    case 2:
                    case 3:
                    case 5:
                        if (absDashBoardInfo.getDashBoardSource() != 12 || absDashBoardInfo.getDashBoardSource() == 13) {
                            ICarDashBoard.MediaInfo mediaInfo = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                            mediaInfo.title = "";
                            mediaInfo.artist = "";
                        }
                        zIsMediaChange = Ihu12DashBoard.isMediaChange(absDashBoardInfo);
                        Log.d(TAG, "MCU_IHU12 setSendDashBoardInfo->isMediaChange-0 = " + zIsMediaChange + ",getDashBoardSource=" + absDashBoardInfo.getDashBoardSource());
                        if (zIsMediaChange) {
                            byte[] mediaInfo2 = Ihu12DashBoard.getMediaInfo((byte) 1, absDashBoardInfo);
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_TITLE= ");
                            if (absDashBoardInfo != null) {
                                dashBoardSource = absDashBoardInfo.getDashBoardSource();
                            } else {
                                dashBoardSource = -1;
                            }
                            sb3.append(dashBoardSource);
                            sb3.append(",[");
                            sb3.append(StringFormart.byte2hex(mediaInfo2));
                            sb3.append(" ]");
                            Log.d(TAG, sb3.toString());
                            setSignalValue(9939, mediaInfo2);
                            byte[] mediaInfo3 = Ihu12DashBoard.getMediaInfo((byte) 2, absDashBoardInfo);
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_ARTIST= ");
                            sb4.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                            sb4.append(",[");
                            sb4.append(StringFormart.byte2hex(mediaInfo3));
                            sb4.append(" ]");
                            Log.d(TAG, sb4.toString());
                            setSignalValue(9939, mediaInfo3);
                        }
                        break;
                    case 4:
                        if (!(absDashBoardInfo instanceof ICarDashBoard.BtInfo)) {
                            Log.w(TAG, "getMediaInfo() - PHONE_NUMBER info is not BtInfo");
                            return false;
                        }
                        final ICarDashBoard.BtInfo btInfo = (ICarDashBoard.BtInfo) absDashBoardInfo;
                        byte[] mediaInfo4 = Ihu12DashBoard.getMediaInfo((byte) 3, absDashBoardInfo);
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_PHONE_INFO= ");
                        sb5.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                        sb5.append(",[");
                        sb5.append(StringFormart.byte2hex(mediaInfo4));
                        sb5.append(" ]");
                        Log.d(TAG, sb5.toString());
                        setSignalValue(9939, mediaInfo4);
                        if (Ihu12DashBoard.isBtInfoChange(absDashBoardInfo)) {
                            byte[] mediaInfo5 = Ihu12DashBoard.getMediaInfo((byte) 5, absDashBoardInfo);
                            StringBuilder sb6 = new StringBuilder();
                            sb6.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_PHONE_NAME= ");
                            sb6.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                            sb6.append(",[");
                            sb6.append(StringFormart.byte2hex(mediaInfo5));
                            sb6.append(" ]");
                            Log.d(TAG, sb6.toString());
                            setSignalValue(9939, mediaInfo5);
                            if (btInfo != null && btInfo.mBtName != null && !btInfo.mBtName.equals("")) {
                                btInfo.mBtCallNumber = "";
                            }
                            byte[] mediaInfo6 = Ihu12DashBoard.getMediaInfo((byte) 4, btInfo);
                            StringBuilder sb7 = new StringBuilder();
                            sb7.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_PHONE_NUMBER= ");
                            sb7.append(btInfo != null ? btInfo.getDashBoardSource() : -1);
                            sb7.append(",[");
                            sb7.append(StringFormart.byte2hex(mediaInfo6));
                            sb7.append(" ]");
                            Log.d(TAG, sb7.toString());
                            setSignalValue(9939, mediaInfo6);
                        }
                        if (btInfo != null && btInfo.mBtStatus == 7) {
                            byte[] mediaInfo7 = Ihu12DashBoard.getMediaInfo((byte) 6, absDashBoardInfo);
                            StringBuilder sb8 = new StringBuilder();
                            sb8.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_PHONE_TIMES= ");
                            sb8.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                            sb8.append(",[");
                            sb8.append(StringFormart.byte2hex(mediaInfo7));
                            sb8.append(" ]");
                            Log.d(TAG, sb8.toString());
                            setSignalValue(9939, mediaInfo7);
                        }
                        if (btInfo != null && btInfo.mBtStatus == 5) {
                            btInfo.mBtStatus = (byte) -1;
                            new Thread(new Runnable() { // from class: com.yftech.vehicle.sdk.CarDashBoardManager.1
                                @Override // java.lang.Runnable
                                public void run() {
                                    try {
                                        Thread.sleep(500L);
                                    } catch (InterruptedException unused) {
                                    }
                                    byte[] mediaInfo8 = Ihu12DashBoard.getMediaInfo((byte) 3, btInfo);
                                    StringBuilder sb9 = new StringBuilder();
                                    sb9.append("MCU_IHU12 setSendDashBoardInfo->STATUS_ENDING_CALL= ");
                                    sb9.append(absDashBoardInfo != null ? btInfo.getDashBoardSource() : -1);
                                    sb9.append(",[");
                                    sb9.append(StringFormart.byte2hex(mediaInfo8));
                                    sb9.append(" ]");
                                    Log.d(CarDashBoardManager.TAG, sb9.toString());
                                    CarDashBoardManager.this.setSignalValue(9939, mediaInfo8);
                                }
                            }).start();
                        }
                        Ihu12DashBoard.setCurrentSource(absDashBoardInfo.getDashBoardSource());
                        break;
                        break;
                    default:
                        switch (dashBoardSource2) {
                            case 12:
                            case 13:
                            case 14:
                                if (absDashBoardInfo.getDashBoardSource() != 12) {
                                    ICarDashBoard.MediaInfo mediaInfo8 = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                                    mediaInfo8.title = "";
                                    mediaInfo8.artist = "";
                                } else {
                                    ICarDashBoard.MediaInfo mediaInfo9 = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                                    mediaInfo9.title = "";
                                    mediaInfo9.artist = "";
                                }
                                zIsMediaChange = Ihu12DashBoard.isMediaChange(absDashBoardInfo);
                                Log.d(TAG, "MCU_IHU12 setSendDashBoardInfo->isMediaChange-0 = " + zIsMediaChange + ",getDashBoardSource=" + absDashBoardInfo.getDashBoardSource());
                                if (zIsMediaChange) {
                                    byte[] mediaInfo10 = Ihu12DashBoard.getMediaInfo((byte) 1, absDashBoardInfo);
                                    StringBuilder sb9 = new StringBuilder();
                                    sb9.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_TITLE= ");
                                    if (absDashBoardInfo != null) {
                                        dashBoardSource = absDashBoardInfo.getDashBoardSource();
                                    } else {
                                        dashBoardSource = -1;
                                    }
                                    sb9.append(dashBoardSource);
                                    sb9.append(",[");
                                    sb9.append(StringFormart.byte2hex(mediaInfo10));
                                    sb9.append(" ]");
                                    Log.d(TAG, sb9.toString());
                                    setSignalValue(9939, mediaInfo10);
                                    byte[] mediaInfo11 = Ihu12DashBoard.getMediaInfo((byte) 2, absDashBoardInfo);
                                    StringBuilder sb10 = new StringBuilder();
                                    sb10.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_ARTIST= ");
                                    sb10.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                                    sb10.append(",[");
                                    sb10.append(StringFormart.byte2hex(mediaInfo11));
                                    sb10.append(" ]");
                                    Log.d(TAG, sb10.toString());
                                    setSignalValue(9939, mediaInfo11);
                                }
                                break;
                            default:
                                break;
                        }
                        break;
                }
            } else {
                if (absDashBoardInfo.getDashBoardSource() != 12) {
                    ICarDashBoard.MediaInfo mediaInfo12 = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                    mediaInfo12.title = "";
                    mediaInfo12.artist = "";
                } else {
                    ICarDashBoard.MediaInfo mediaInfo13 = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                    mediaInfo13.title = "";
                    mediaInfo13.artist = "";
                }
                zIsMediaChange = Ihu12DashBoard.isMediaChange(absDashBoardInfo);
                Log.d(TAG, "MCU_IHU12 setSendDashBoardInfo->isMediaChange-0 = " + zIsMediaChange + ",getDashBoardSource=" + absDashBoardInfo.getDashBoardSource());
                if (zIsMediaChange) {
                    byte[] mediaInfo14 = Ihu12DashBoard.getMediaInfo((byte) 1, absDashBoardInfo);
                    StringBuilder sb11 = new StringBuilder();
                    sb11.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_TITLE= ");
                    if (absDashBoardInfo != null) {
                        dashBoardSource = absDashBoardInfo.getDashBoardSource();
                    } else {
                        dashBoardSource = -1;
                    }
                    sb11.append(dashBoardSource);
                    sb11.append(",[");
                    sb11.append(StringFormart.byte2hex(mediaInfo14));
                    sb11.append(" ]");
                    Log.d(TAG, sb11.toString());
                    setSignalValue(9939, mediaInfo14);
                    byte[] mediaInfo15 = Ihu12DashBoard.getMediaInfo((byte) 2, absDashBoardInfo);
                    StringBuilder sb12 = new StringBuilder();
                    sb12.append("MCU_IHU12 setSendDashBoardInfo->INFO_TYPE_SONG_ARTIST= ");
                    sb12.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                    sb12.append(",[");
                    sb12.append(StringFormart.byte2hex(mediaInfo15));
                    sb12.append(" ]");
                    Log.d(TAG, sb12.toString());
                    setSignalValue(9939, mediaInfo15);
                }
            }
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public boolean setSendDashBoardMusicArtImage(byte[] bArr) {
        if (bArr != null) {
            return setSignalValue(9985, bArr);
        }
        Log.w(TAG, "setSendDashBoardMusicArtImage() imageBytes is null");
        return false;
    }

    public boolean setSendDashBoardNaviGuideBaseInfoForIhu12(Ihu12DashBoard.NaviGuideBaseInfo naviGuideBaseInfo) {
        if (naviGuideBaseInfo == null) {
            Log.w(TAG, "setSendDashBoardNaviGuideInfoForIhu12() info is null");
            return false;
        }
        try {
            setSignalValue(9939, Ihu12DashBoard.getNaviGuideBaseInfoDataProtocol((byte) 7, naviGuideBaseInfo));
            setSignalValue(9939, Ihu12DashBoard.getNaviGuideBaseInfoDataProtocol((byte) 8, naviGuideBaseInfo));
            Ihu12DashBoard.getNaviGuideBaseInfoDataProtocol((byte) 9, naviGuideBaseInfo);
            setSignalValue(9939, Ihu12DashBoard.getNaviGuideBaseInfoDataProtocol((byte) 10, naviGuideBaseInfo));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setSendDashBoardNaviGuideInfo()  Exception ", e);
            return false;
        }
    }

    public boolean setSendDashBoardNaviGuideInfo(int i, int i2, String str) {
        if (str == null) {
            Log.w(TAG, "setSendDashBoardNaviGuideInfo() nextRoadName is null");
            return false;
        }
        try {
            int length = str.getBytes(StandardCharsets.UTF_8).length;
            byte[] bArr = new byte[length + 2];
            bArr[0] = (byte) (i & 255);
            bArr[1] = (byte) (i2 & 255);
            System.arraycopy(str.getBytes(StandardCharsets.UTF_8), 0, bArr, 2, length);
            return setSignalValue(9984, bArr);
        } catch (Exception e) {
            Log.e(TAG, "setSendDashBoardNaviGuideInfo()  Exception ", e);
            return false;
        }
    }

    public boolean setSendDashBoardNaviGuideUpdateInfoForIhu12(Ihu12DashBoard.NaviUpdateData naviUpdateData) {
        if (naviUpdateData == null) {
            Log.w(TAG, "setSendDashBoardNaviGuideUpdateInfoForIhu12() info is null");
            return false;
        }
        try {
            setSignalValue(9939, Ihu12DashBoard.getNaviGuideUpdateDataProtocol(naviUpdateData));
            return true;
        } catch (Exception e) {
            Log.e(TAG, "setSendDashBoardNaviGuideUpdateInfoForIhu12()  Exception ", e);
            return false;
        }
    }

    public boolean setSendDashBoardSource(int i, ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        try {
            if (!ICarDashBoard.DashBoardType.MCU_IHU12.equals(this.mDashBoardType)) {
                byte[] dashBoardSourceDataByteArray = ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).getDashBoardSourceDataByteArray(i, absDashBoardInfo);
                if (dashBoardSourceDataByteArray != null && dashBoardSourceDataByteArray.length > 1) {
                    Log.d(TAG, "setSendDashBoardSource->source=" + i + ",[" + StringFormart.byte2hex(dashBoardSourceDataByteArray) + " ]");
                    return ICarDashBoard.DashBoardDataFormater.get(this.mDashBoardType).isSendMediaInfoOnly() ? setSignalValue(9986, dashBoardSourceDataByteArray) : setSignalValue(9986, dashBoardSourceDataByteArray);
                }
                Log.w(TAG, "setSendDashBoardSource() byteArr is null info=" + absDashBoardInfo);
                return false;
            }
            Ihu12DashBoard.setCurrentSource(i);
            byte[] dashBoardSourceDataByteArray2 = Ihu12DashBoard.getDashBoardSourceDataByteArray(i, absDashBoardInfo);
            if (dashBoardSourceDataByteArray2 == null) {
                Log.d(TAG, "MCU_IHU12  setSendDashBoardSource->source= " + i + ",byteArr=null return!");
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("MCU_IHU12  setSendDashBoardSource->source= ");
                sb.append(absDashBoardInfo != null ? absDashBoardInfo.getDashBoardSource() : -1);
                sb.append(",[");
                sb.append(StringFormart.byte2hex(dashBoardSourceDataByteArray2));
                sb.append(" ]");
                Log.d(TAG, sb.toString());
                setSignalValue(9939, dashBoardSourceDataByteArray2);
            }
            switch (i) {
                case 12:
                case 13:
                case 14:
                    ICarDashBoard.MediaInfo mediaInfo = new ICarDashBoard.MediaInfo(i);
                    mediaInfo.title = "";
                    mediaInfo.artist = "";
                    setSendDashBoardInfo(mediaInfo);
                    return true;
                default:
                    return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public void unregisterListener(CarDashBoardListener carDashBoardListener) {
        unregisterReceiver(carDashBoardListener);
    }
}
