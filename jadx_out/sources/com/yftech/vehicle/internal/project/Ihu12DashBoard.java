package com.yftech.vehicle.internal.project;

import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiScanner;
import android.telecom.Logging.Session;
import android.util.Log;
import com.yftech.vehicle.internal.adapter.ICarDashBoard;
import java.nio.charset.StandardCharsets;

/* JADX INFO: loaded from: classes4.dex */
public class Ihu12DashBoard {
    public static final byte INFO_TYPE_NAVI_BASE_INFO = 7;
    public static final byte INFO_TYPE_NAVI_GUIDE_COMPASS = 8;
    public static final byte INFO_TYPE_NAVI_GUIDE_CURR_LOAD_NAME = 9;
    public static final byte INFO_TYPE_NAVI_GUIDE_NEXT_LOAD_NAME = 10;
    public static final byte INFO_TYPE_NAVI_GUIDE_NEXT_PATH_CALCULATION = 11;
    public static final byte INFO_TYPE_PHONE_INFO = 3;
    public static final byte INFO_TYPE_PHONE_NAME = 5;
    public static final byte INFO_TYPE_PHONE_NUMBER = 4;
    public static final byte INFO_TYPE_PHONE_TIMES = 6;
    public static final byte INFO_TYPE_SONG_ARTIST = 2;
    public static final byte INFO_TYPE_SONG_TITLE = 1;
    private static final String TAG = "Ihu12DashBoard";
    static ICarDashBoard.AbsDashBoardInfo mAbsInfo;
    private static byte[] mDashBoardInfoDataByteArrayForIhu12 = new byte[18];
    private static int mLastSource;

    public enum CaculationState {
        IN_CACULATION(0),
        CACULATION_DONE(1),
        NOT_IN_NAVI(2),
        __UNKNOWN__(-1);

        public final int value;

        CaculationState(int i) {
            this.value = i;
        }

        public static CaculationState valueOf(int i) {
            if (i == IN_CACULATION.value) {
                return IN_CACULATION;
            }
            if (i == CACULATION_DONE.value) {
                return CACULATION_DONE;
            }
            return i == NOT_IN_NAVI.value ? NOT_IN_NAVI : __UNKNOWN__;
        }
    }

    public enum CompassGuide {
        N(1),
        NE(2),
        E(3),
        SE(4),
        S(5),
        SW(6),
        W(7),
        NW(8),
        __UNKNOWN__(-1);

        public final int value;

        CompassGuide(int i) {
            this.value = i;
        }

        public static CompassGuide valueOf(int i) {
            if (i == N.ordinal()) {
                return N;
            }
            if (i == NE.ordinal()) {
                return NE;
            }
            if (i == E.ordinal()) {
                return E;
            }
            if (i == SE.ordinal()) {
                return SE;
            }
            if (i == S.ordinal()) {
                return S;
            }
            if (i == SW.ordinal()) {
                return SW;
            }
            if (i == W.ordinal()) {
                return W;
            }
            return i == NW.ordinal() ? NW : __UNKNOWN__;
        }
    }

    public enum GpsSignal {
        NO_SIGNAL(0),
        HAVE_SIGNAL(1),
        __UNKNOWN__(-1);

        public final int value;

        GpsSignal(int i) {
            this.value = i;
        }

        public static GpsSignal valueOf(int i) {
            if (i == NO_SIGNAL.value) {
                return NO_SIGNAL;
            }
            return i == HAVE_SIGNAL.value ? HAVE_SIGNAL : __UNKNOWN__;
        }
    }

    public enum NaviDistUnit {
        METER(1),
        KILOMETER(2),
        MILE(3),
        __UNKNOWN__(-1);

        public final int value;

        NaviDistUnit(int i) {
            this.value = i;
        }

        public static NaviDistUnit valueOf(int i) {
            if (i == METER.value) {
                return METER;
            }
            if (i == KILOMETER.value) {
                return KILOMETER;
            }
            return i == MILE.value ? MILE : __UNKNOWN__;
        }
    }

    public static class NaviGuideBaseInfo {
        public int azimuthInfo;
        public CaculationState caculationState;
        public CompassGuide compass;
        public GpsSignal gpsSignal;
        public int naviIcon_RC;
        public int naviIcon_TBT;
        public int naviIcon_WS;
        public NaviState naviState;
        public String nextRoadName;
        public String roadName;
        public SeaLevel seaLevel;

        public NaviGuideBaseInfo(SeaLevel seaLevel, GpsSignal gpsSignal, int i, NaviState naviState, int i2, int i3, int i4, CompassGuide compassGuide, String str, String str2, CaculationState caculationState) {
            this.seaLevel = SeaLevel.__UNKNOWN__;
            this.gpsSignal = GpsSignal.__UNKNOWN__;
            this.naviState = NaviState.__UNKNOWN__;
            this.compass = CompassGuide.__UNKNOWN__;
            this.roadName = "";
            this.nextRoadName = "";
            this.caculationState = CaculationState.__UNKNOWN__;
            this.seaLevel = seaLevel;
            this.gpsSignal = gpsSignal;
            this.azimuthInfo = i;
            this.naviState = naviState;
            this.naviIcon_TBT = i2;
            this.naviIcon_RC = i3;
            this.naviIcon_WS = i4;
            this.compass = compassGuide;
            this.roadName = str;
            this.nextRoadName = str2;
            this.caculationState = caculationState;
        }
    }

    public enum NaviState {
        NOT_NAVIGATED(0),
        NAV_PLANNING(1),
        NAV_START(2),
        NAVIGATING(3),
        NAV_OVER(4),
        __UNKNOWN__(-1);

        public final int value;

        NaviState(int i) {
            this.value = i;
        }

        public static NaviState valueOf(int i) {
            if (i == NOT_NAVIGATED.value) {
                return NOT_NAVIGATED;
            }
            if (i == NAV_PLANNING.value) {
                return NAV_PLANNING;
            }
            if (i == NAV_START.value) {
                return NAV_START;
            }
            if (i == NAVIGATING.value) {
                return NAVIGATING;
            }
            return i == NAV_OVER.value ? NAV_OVER : __UNKNOWN__;
        }
    }

    public static class NaviUpdateData {
        public int NaviDestDistInfo;
        public int NaviDist;
        public int NaviDistBar;
        public int NaviHour;
        public int NaviMinitue;
        public NaviDistUnit naviDistUnit;
        public NaviDistUnit naviSumDistUnit;

        public NaviUpdateData(NaviDistUnit naviDistUnit, int i, int i2, NaviDistUnit naviDistUnit2, int i3, int i4, int i5) {
            this.naviDistUnit = NaviDistUnit.__UNKNOWN__;
            this.naviSumDistUnit = NaviDistUnit.__UNKNOWN__;
            this.naviDistUnit = naviDistUnit;
            this.NaviDist = i;
            this.NaviDistBar = i2;
            this.naviSumDistUnit = naviDistUnit2;
            this.NaviMinitue = i3;
            this.NaviHour = i4;
            this.NaviDestDistInfo = i5;
        }
    }

    public enum SeaLevel {
        BELOW_SEA(0),
        ABOVE_SEA(1),
        __UNKNOWN__(-1);

        public final int value;

        SeaLevel(int i) {
            this.value = i;
        }

        public static SeaLevel valueOf(int i) {
            if (i == BELOW_SEA.value) {
                return BELOW_SEA;
            }
            return i == ABOVE_SEA.value ? ABOVE_SEA : __UNKNOWN__;
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:13:0x003d  */
    /* JADX WARN: Code duplicated, block: B:44:0x00ae A[FALL_THROUGH] */
    /* JADX WARN: Code duplicated, block: B:46:0x00b2  */
    /* JADX WARN: Code duplicated, block: B:48:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:50:0x00c0  */
    /* JADX WARN: Code duplicated, block: B:51:0x00c3  */
    /* JADX WARN: Code duplicated, block: B:53:0x00c7  */
    /*  JADX ERROR: UnsupportedOperationException in pass: RegionMakerVisitor
        java.lang.UnsupportedOperationException
        	at java.base/java.util.Collections$UnmodifiableCollection.add(Collections.java:1092)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker$1.leaveRegion(SwitchRegionMaker.java:419)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:31)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaksForCase(SwitchRegionMaker.java:399)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.insertBreaks(SwitchRegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.leaveRegion(PostProcessRegions.java:31)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverseInternal(DepthRegionTraversal.java:91)
        	at jadx.core.dex.visitors.regions.DepthRegionTraversal.traverse(DepthRegionTraversal.java:27)
        	at jadx.core.dex.visitors.regions.PostProcessRegions.process(PostProcessRegions.java:21)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:31)
        */
    public static byte[] getDashBoardInfoDataByteArrayForIhu12(com.yftech.vehicle.internal.adapter.ICarDashBoard.AbsDashBoardInfo r13) {
        /*
            Method dump skipped, instruction units count: 436
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.yftech.vehicle.internal.project.Ihu12DashBoard.getDashBoardInfoDataByteArrayForIhu12(com.yftech.vehicle.internal.adapter.ICarDashBoard$AbsDashBoardInfo):byte[]");
    }

    public static byte[] getDashBoardSourceDataByteArray(int i, ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        byte[] bArr = new byte[18];
        bArr[0] = -124;
        bArr[1] = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
        bArr[2] = 1;
        bArr[3] = 0;
        switch (i) {
            case -1:
            case 0:
                bArr[3] = 0;
                break;
            default:
                bArr = null;
                break;
        }
        if (absDashBoardInfo == null) {
            Log.d(TAG, "MCU_IHU12  getDashBoardSourceDataByteArray->source=" + i + ",info is null continue");
        }
        return bArr;
    }

    public static byte[] getMediaInfo(byte b, ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        String str;
        String str2;
        String str3;
        String str4;
        byte[] bArr = new byte[8];
        switch (b) {
            case 1:
                if (!(absDashBoardInfo instanceof ICarDashBoard.MediaInfo)) {
                    Log.w(TAG, "getMediaInfo() - SONG_TITLE info is not MediaInfo");
                    return bArr;
                }
                ICarDashBoard.MediaInfo mediaInfo = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                if (mediaInfo.title == null) {
                    str = "";
                } else {
                    str = mediaInfo.title + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length = str != null ? str.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str.length() > 27) {
                    str = str.substring(0, 24) + Session.TRUNCATE_STRING;
                    length = str != null ? str.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr2 = new byte[length + 5];
                bArr2[0] = -124;
                bArr2[1] = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
                bArr2[2] = 2;
                bArr2[3] = (byte) length;
                bArr2[4] = 0;
                if (length == 0) {
                    return bArr2;
                }
                System.arraycopy(str.getBytes(StandardCharsets.UTF_16BE), 0, bArr2, 5, length);
                return bArr2;
            case 2:
                if (!(absDashBoardInfo instanceof ICarDashBoard.MediaInfo)) {
                    Log.w(TAG, "getMediaInfo() - SONG_ARTIST info is not MediaInfo");
                    return bArr;
                }
                ICarDashBoard.MediaInfo mediaInfo2 = (ICarDashBoard.MediaInfo) absDashBoardInfo;
                if (mediaInfo2.artist == null) {
                    str2 = "";
                } else {
                    str2 = mediaInfo2.artist + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length2 = str2 != null ? str2.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str2.length() > 27) {
                    str2 = str2.substring(0, 24) + Session.TRUNCATE_STRING;
                    length2 = str2 != null ? str2.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr3 = new byte[length2 + 5];
                bArr3[0] = -124;
                bArr3[1] = WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK;
                bArr3[2] = 3;
                bArr3[3] = (byte) length2;
                bArr3[4] = 0;
                if (length2 == 0) {
                    return bArr3;
                }
                System.arraycopy(str2.getBytes(StandardCharsets.UTF_16BE), 0, bArr3, 5, length2);
                return bArr3;
            case 3:
                if (!(absDashBoardInfo instanceof ICarDashBoard.BtInfo)) {
                    Log.w(TAG, "getMediaInfo() - PHONE_INFO info is not BtInfo");
                    return bArr;
                }
                ICarDashBoard.BtInfo btInfo = (ICarDashBoard.BtInfo) absDashBoardInfo;
                byte[] bArr4 = new byte[8];
                bArr4[0] = -124;
                bArr4[1] = 17;
                bArr4[2] = 1;
                if (btInfo.mBtStatus != 0) {
                    bArr4[3] = 1;
                } else {
                    bArr4[3] = 0;
                }
                bArr4[4] = 0;
                bArr4[5] = 0;
                byte b2 = btInfo.mBtStatus;
                if (b2 != 0) {
                    switch (b2) {
                        case 2:
                            bArr4[6] = 1;
                            break;
                        case 3:
                            bArr4[6] = 2;
                            break;
                        case 4:
                        case 7:
                            bArr4[6] = 3;
                            break;
                        case 5:
                            bArr4[6] = 4;
                            break;
                        case 6:
                            bArr4[6] = 5;
                            break;
                        default:
                            bArr4[6] = 0;
                            break;
                    }
                } else {
                    bArr4[6] = 0;
                }
                bArr4[7] = 1;
                if (btInfo.mPhoneSourceType == 4) {
                    bArr4[7] = 2;
                }
                if (btInfo.mPhoneSourceType != 5) {
                    return bArr4;
                }
                bArr4[7] = 3;
                return bArr4;
            case 4:
                if (!(absDashBoardInfo instanceof ICarDashBoard.BtInfo)) {
                    Log.w(TAG, "getMediaInfo() - PHONE_NUMBER info is not BtInfo");
                    return bArr;
                }
                ICarDashBoard.BtInfo btInfo2 = (ICarDashBoard.BtInfo) absDashBoardInfo;
                if (btInfo2.mBtCallNumber == null) {
                    str3 = "";
                } else {
                    str3 = btInfo2.mBtCallNumber + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length3 = str3 != null ? str3.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str3.length() > 27) {
                    str3 = str3.substring(0, 24) + Session.TRUNCATE_STRING;
                    length3 = str3 != null ? str3.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr5 = new byte[length3 + 5];
                bArr5[0] = -124;
                bArr5[1] = 17;
                bArr5[2] = 2;
                bArr5[3] = (byte) length3;
                bArr5[4] = 0;
                if (length3 == 0) {
                    return bArr5;
                }
                System.arraycopy(str3.getBytes(StandardCharsets.UTF_16BE), 0, bArr5, 5, length3);
                return bArr5;
            case 5:
                if (!(absDashBoardInfo instanceof ICarDashBoard.BtInfo)) {
                    Log.w(TAG, "getMediaInfo() - PHONE_NAME info is not BtInfo");
                    return bArr;
                }
                ICarDashBoard.BtInfo btInfo3 = (ICarDashBoard.BtInfo) absDashBoardInfo;
                if (btInfo3.mBtName == null) {
                    str4 = "";
                } else {
                    str4 = btInfo3.mBtName + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length4 = str4 != null ? str4.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str4.length() > 27) {
                    str4 = str4.substring(0, 24) + Session.TRUNCATE_STRING;
                    length4 = str4 != null ? str4.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr6 = new byte[length4 + 5];
                bArr6[0] = -124;
                bArr6[1] = 17;
                bArr6[2] = 3;
                bArr6[3] = (byte) length4;
                bArr6[4] = 0;
                if (length4 == 0) {
                    return bArr6;
                }
                System.arraycopy(str4.getBytes(StandardCharsets.UTF_16BE), 0, bArr6, 5, length4);
                return bArr6;
            case 6:
                if (!(absDashBoardInfo instanceof ICarDashBoard.BtInfo)) {
                    Log.w(TAG, "getMediaInfo() - PHONE_TIMES info is not btInfo");
                    return bArr;
                }
                ICarDashBoard.BtInfo btInfo4 = (ICarDashBoard.BtInfo) absDashBoardInfo;
                int i = (btInfo4.talkTimes % 86400000) / 3600000;
                int i2 = (btInfo4.talkTimes % 3600000) / 60000;
                int i3 = (btInfo4.talkTimes % 60000) / 1000;
                Log.w(TAG, "INFO_TYPE_PHONE_TIMES: " + i + " currHours " + i2 + " currMinutes " + i3 + " currSeconds ");
                return new byte[]{-124, 17, 4, (byte) ((i >> 8) & 255), (byte) (i & 255), (byte) i2, (byte) i3, 0};
            default:
                return bArr;
        }
    }

    public static byte[] getNaviGuideBaseInfoDataProtocol(byte b, NaviGuideBaseInfo naviGuideBaseInfo) {
        String str;
        String str2;
        byte[] bArr = new byte[8];
        if (naviGuideBaseInfo == null) {
            Log.w(TAG, "getNaviGuideInfoDataProtocol() - PHONE_NUMBER info is null");
            return bArr;
        }
        switch (b) {
            case 7:
                return new byte[]{-124, 18, 1, (byte) naviGuideBaseInfo.seaLevel.ordinal(), (byte) naviGuideBaseInfo.gpsSignal.ordinal(), (byte) ((naviGuideBaseInfo.azimuthInfo >> 8) & 255), (byte) (naviGuideBaseInfo.azimuthInfo & 255), (byte) naviGuideBaseInfo.naviState.value, (byte) naviGuideBaseInfo.naviIcon_TBT, (byte) naviGuideBaseInfo.naviIcon_RC, (byte) naviGuideBaseInfo.naviIcon_WS};
            case 8:
                return new byte[]{-124, 18, 2, (byte) naviGuideBaseInfo.compass.ordinal(), 0, 0, 0, 0};
            case 9:
                if (!(naviGuideBaseInfo instanceof NaviGuideBaseInfo)) {
                    Log.w(TAG, "getNaviGuideBaseInfoDataProtocol() - INFO_TYPE_NAVI_GUIDE_CURR_LOAD_NAME info is not NaviGuideBaseInfo");
                    return bArr;
                }
                if (naviGuideBaseInfo.roadName == null || "".equals(naviGuideBaseInfo.roadName)) {
                    Log.w(TAG, "getNaviGuideInfoDataProtocol() - info.roadName is null");
                    return bArr;
                }
                if (naviGuideBaseInfo.roadName == null) {
                    str = "";
                } else {
                    str = naviGuideBaseInfo.roadName + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length = str != null ? str.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str.length() > 27) {
                    str = str.substring(0, 24) + Session.TRUNCATE_STRING;
                    length = str != null ? str.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr2 = new byte[length + 5];
                bArr2[0] = -124;
                bArr2[1] = 18;
                bArr2[2] = 3;
                bArr2[3] = (byte) length;
                bArr2[4] = 0;
                if (length == 0) {
                    return bArr2;
                }
                System.arraycopy(str.getBytes(StandardCharsets.UTF_16BE), 0, bArr2, 5, length);
                return bArr2;
            case 10:
                if (!(naviGuideBaseInfo instanceof NaviGuideBaseInfo)) {
                    Log.w(TAG, "getNaviGuideBaseInfoDataProtocol() - INFO_TYPE_NAVI_GUIDE_CURR_LOAD_NAME info is not NaviGuideBaseInfo");
                    return bArr;
                }
                if (naviGuideBaseInfo.nextRoadName == null || "".equals(naviGuideBaseInfo.nextRoadName)) {
                    Log.w(TAG, "getNaviGuideInfoDataProtocol() - info.nextRoadName is null");
                    return bArr;
                }
                if (naviGuideBaseInfo.nextRoadName == null) {
                    str2 = "";
                } else {
                    str2 = naviGuideBaseInfo.nextRoadName + WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER;
                }
                int length2 = str2 != null ? str2.getBytes(StandardCharsets.UTF_16BE).length : 0;
                if (str2.length() > 27) {
                    str2 = str2.substring(0, 24) + Session.TRUNCATE_STRING;
                    length2 = str2 != null ? str2.getBytes(StandardCharsets.UTF_16BE).length : 0;
                }
                byte[] bArr3 = new byte[length2 + 5];
                bArr3[0] = -124;
                bArr3[1] = 18;
                bArr3[2] = 4;
                bArr3[3] = (byte) length2;
                bArr3[4] = 0;
                if (length2 == 0) {
                    return bArr3;
                }
                System.arraycopy(str2.getBytes(StandardCharsets.UTF_16BE), 0, bArr3, 5, length2);
                return bArr3;
            case 11:
                if (naviGuideBaseInfo.caculationState != null) {
                    return new byte[]{-124, 18, 5, (byte) naviGuideBaseInfo.caculationState.ordinal(), 0, 0, 0, 0};
                }
                Log.w(TAG, "getNaviGuideInfoDataProtocol() - info.caculationState is null");
                return bArr;
            default:
                return bArr;
        }
    }

    public static byte[] getNaviGuideUpdateDataProtocol(NaviUpdateData naviUpdateData) {
        byte[] bArr = new byte[8];
        if (naviUpdateData != null) {
            return new byte[]{-124, 18, 6, (byte) naviUpdateData.naviDistUnit.ordinal(), (byte) ((naviUpdateData.NaviDist >> 16) & 255), (byte) ((naviUpdateData.NaviDist >> 8) & 255), (byte) (naviUpdateData.NaviDist & 255), (byte) ((naviUpdateData.NaviDistBar >> 16) & 255), (byte) ((naviUpdateData.NaviDistBar >> 8) & 255), (byte) (naviUpdateData.NaviDistBar & 255), (byte) naviUpdateData.naviSumDistUnit.ordinal(), (byte) naviUpdateData.NaviMinitue, (byte) ((naviUpdateData.NaviHour >> 8) & 255), (byte) (naviUpdateData.NaviHour & 255), (byte) ((naviUpdateData.NaviDestDistInfo >> 24) & 255), (byte) ((naviUpdateData.NaviDestDistInfo >> 16) & 255), (byte) ((naviUpdateData.NaviDestDistInfo >> 8) & 255), (byte) (naviUpdateData.NaviDestDistInfo & 255)};
        }
        Log.w(TAG, "getNaviGuideUpdateDataProtocol() - updateData is null");
        return bArr;
    }

    public static boolean isBtInfoChange(ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        if (absDashBoardInfo == null) {
            Log.w(TAG, "isBtInfoChange()  BtInfo is null)");
            return false;
        }
        ICarDashBoard.BtInfo btInfo = (ICarDashBoard.BtInfo) absDashBoardInfo;
        if (btInfo.mBtCallNumber == null) {
            Log.w(TAG, "isBtInfoChange()  BtInfo.mBtCallNumber is null)");
            return false;
        }
        boolean z = true;
        if (mAbsInfo == null || !(mAbsInfo instanceof ICarDashBoard.BtInfo)) {
            mAbsInfo = new ICarDashBoard.BtInfo();
        } else {
            ICarDashBoard.BtInfo btInfo2 = (ICarDashBoard.BtInfo) mAbsInfo;
            if (btInfo2 == null || !btInfo2.isInfoDiff(btInfo)) {
                z = false;
            } else {
                btInfo2.setInfoDiff(btInfo);
            }
        }
        Log.d(TAG, "isBtInfoChange() isBtInfoChange =" + z);
        return z;
    }

    public static boolean isCurrentSource(int i) {
        return mLastSource != i;
    }

    public static boolean isMediaChange(ICarDashBoard.AbsDashBoardInfo absDashBoardInfo) {
        ICarDashBoard.MediaInfo mediaInfo = (ICarDashBoard.MediaInfo) absDashBoardInfo;
        if (mediaInfo.title == null || mediaInfo.artist == null) {
            Log.w(TAG, "isMediaChange() USB MediaInfo.title is null or AbsDashBoardInfo is not MediaInfo");
            return false;
        }
        boolean z = true;
        if (mAbsInfo == null || !(mAbsInfo instanceof ICarDashBoard.MediaInfo)) {
            mAbsInfo = new ICarDashBoard.MediaInfo(absDashBoardInfo.getDashBoardSource());
            ((ICarDashBoard.MediaInfo) mAbsInfo).setInfoDiff(mediaInfo);
        } else if (mLastSource != mAbsInfo.getDashBoardSource()) {
            mAbsInfo.setDashBoardSource(mediaInfo.getDashBoardSource());
        } else {
            ICarDashBoard.MediaInfo mediaInfo2 = (ICarDashBoard.MediaInfo) mAbsInfo;
            if (mediaInfo2 == null || !mediaInfo2.isInfoDiff(mediaInfo)) {
                z = false;
            } else {
                mediaInfo2.setInfoDiff(mediaInfo);
            }
        }
        Log.d(TAG, "isMediaChange()-3 mLastSource=" + mLastSource + " mAbsInfo.getDashBoardSource()=" + mAbsInfo.getDashBoardSource() + ", isMediaUpdate =" + z);
        return z;
    }

    public static void setCurrentSource(int i) {
        mLastSource = i;
    }
}
