package android.os;

import android.app.backup.FullBackup;
import android.app.job.JobParameters;
import android.app.slice.Slice;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.hardware.contexthub.V1_0.HostEndPoint;
import android.location.LocationManager;
import android.media.TtmlUtils;
import android.net.TrafficStats;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiScanner;
import android.provider.SettingsStringUtil;
import android.provider.Telephony;
import android.telephony.SignalStrength;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.LongSparseArray;
import android.util.MutableBoolean;
import android.util.Pair;
import android.util.Printer;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import androidx.exifinterface.media.ExifInterface;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.os.BatterySipper;
import com.android.internal.os.BatteryStatsHelper;
import com.android.internal.telephony.PhoneConstants;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BatteryStats implements Parcelable {
    private static final String AGGREGATED_WAKELOCK_DATA = "awl";
    public static final int AGGREGATED_WAKE_TYPE_PARTIAL = 20;
    private static final String APK_DATA = "apk";
    private static final String AUDIO_DATA = "aud";
    public static final int AUDIO_TURNED_ON = 15;
    private static final String BATTERY_DATA = "bt";
    private static final String BATTERY_DISCHARGE_DATA = "dc";
    private static final String BATTERY_LEVEL_DATA = "lv";
    private static final int BATTERY_STATS_CHECKIN_VERSION = 9;
    private static final String BLUETOOTH_CONTROLLER_DATA = "ble";
    private static final String BLUETOOTH_MISC_DATA = "blem";
    public static final int BLUETOOTH_SCAN_ON = 19;
    public static final int BLUETOOTH_UNOPTIMIZED_SCAN_ON = 21;
    private static final long BYTES_PER_GB = 1073741824;
    private static final long BYTES_PER_KB = 1024;
    private static final long BYTES_PER_MB = 1048576;
    private static final String CAMERA_DATA = "cam";
    public static final int CAMERA_TURNED_ON = 17;
    private static final String CELLULAR_CONTROLLER_NAME = "Cellular";
    private static final String CHARGE_STEP_DATA = "csd";
    private static final String CHARGE_TIME_REMAIN_DATA = "ctr";
    static final int CHECKIN_VERSION = 32;
    private static final String CPU_DATA = "cpu";
    private static final String CPU_TIMES_AT_FREQ_DATA = "ctf";
    private static final String DATA_CONNECTION_COUNT_DATA = "dcc";
    public static final int DATA_CONNECTION_NONE = 0;
    public static final int DATA_CONNECTION_OTHER = 20;
    private static final String DATA_CONNECTION_TIME_DATA = "dct";
    public static final int DEVICE_IDLE_MODE_DEEP = 2;
    public static final int DEVICE_IDLE_MODE_LIGHT = 1;
    public static final int DEVICE_IDLE_MODE_OFF = 0;
    private static final String DISCHARGE_STEP_DATA = "dsd";
    private static final String DISCHARGE_TIME_REMAIN_DATA = "dtr";
    public static final int DUMP_CHARGED_ONLY = 2;
    public static final int DUMP_DAILY_ONLY = 4;
    public static final int DUMP_DEVICE_WIFI_ONLY = 64;
    public static final int DUMP_HISTORY_ONLY = 8;
    public static final int DUMP_INCLUDE_HISTORY = 16;
    public static final int DUMP_VERBOSE = 32;
    private static final String FLASHLIGHT_DATA = "fla";
    public static final int FLASHLIGHT_TURNED_ON = 16;
    public static final int FOREGROUND_ACTIVITY = 10;
    public static final int FOREGROUND_SERVICE = 22;
    private static final String FOREGROUND_SERVICE_DATA = "fgs";
    public static final int FULL_WIFI_LOCK = 5;
    private static final String GLOBAL_BLUETOOTH_CONTROLLER_DATA = "gble";
    private static final String GLOBAL_CPU_FREQ_DATA = "gcf";
    private static final String GLOBAL_MODEM_CONTROLLER_DATA = "gmcd";
    private static final String GLOBAL_NETWORK_DATA = "gn";
    private static final String GLOBAL_WIFI_CONTROLLER_DATA = "gwfcd";
    private static final String GLOBAL_WIFI_DATA = "gwfl";
    private static final String HISTORY_DATA = "h";
    private static final String HISTORY_STRING_POOL = "hsp";
    public static final int JOB = 14;
    private static final String JOBS_DEFERRED_DATA = "jbd";
    private static final String JOB_COMPLETION_DATA = "jbc";
    private static final String JOB_DATA = "jb";
    private static final String KERNEL_WAKELOCK_DATA = "kwl";
    private static final boolean LOCAL_LOGV = false;
    public static final int MAX_TRACKED_SCREEN_STATE = 4;
    private static final String MISC_DATA = "m";
    private static final String MODEM_CONTROLLER_DATA = "mcd";
    public static final int NETWORK_BT_RX_DATA = 4;
    public static final int NETWORK_BT_TX_DATA = 5;
    private static final String NETWORK_DATA = "nt";
    public static final int NETWORK_MOBILE_BG_RX_DATA = 6;
    public static final int NETWORK_MOBILE_BG_TX_DATA = 7;
    public static final int NETWORK_MOBILE_RX_DATA = 0;
    public static final int NETWORK_MOBILE_TX_DATA = 1;
    public static final int NETWORK_WIFI_BG_RX_DATA = 8;
    public static final int NETWORK_WIFI_BG_TX_DATA = 9;
    public static final int NETWORK_WIFI_RX_DATA = 2;
    public static final int NETWORK_WIFI_TX_DATA = 3;
    public static final int NUM_DATA_CONNECTION_TYPES = 21;
    public static final int NUM_NETWORK_ACTIVITY_TYPES = 10;
    public static final int NUM_SCREEN_BRIGHTNESS_BINS = 5;
    public static final int NUM_WIFI_SIGNAL_STRENGTH_BINS = 5;
    public static final int NUM_WIFI_STATES = 8;
    public static final int NUM_WIFI_SUPPL_STATES = 13;
    private static final String POWER_USE_ITEM_DATA = "pwi";
    private static final String POWER_USE_SUMMARY_DATA = "pws";
    private static final String PROCESS_DATA = "pr";
    public static final int PROCESS_STATE = 12;
    private static final String RESOURCE_POWER_MANAGER_DATA = "rpm";
    public static final String RESULT_RECEIVER_CONTROLLER_KEY = "controller_activity";
    public static final int SCREEN_BRIGHTNESS_BRIGHT = 4;
    public static final int SCREEN_BRIGHTNESS_DARK = 0;
    private static final String SCREEN_BRIGHTNESS_DATA = "br";
    public static final int SCREEN_BRIGHTNESS_DIM = 1;
    public static final int SCREEN_BRIGHTNESS_LIGHT = 3;
    public static final int SCREEN_BRIGHTNESS_MEDIUM = 2;
    protected static final boolean SCREEN_OFF_RPM_STATS_ENABLED = false;
    public static final int SENSOR = 3;
    private static final String SENSOR_DATA = "sr";
    public static final String SERVICE_NAME = "batterystats";
    private static final String SIGNAL_SCANNING_TIME_DATA = "sst";
    private static final String SIGNAL_STRENGTH_COUNT_DATA = "sgc";
    private static final String SIGNAL_STRENGTH_TIME_DATA = "sgt";
    private static final String STATE_TIME_DATA = "st";
    public static final int STATS_CURRENT = 1;
    public static final int STATS_SINCE_CHARGED = 0;
    public static final int STATS_SINCE_UNPLUGGED = 2;
    public static final long STEP_LEVEL_INITIAL_MODE_MASK = 71776119061217280L;
    public static final int STEP_LEVEL_INITIAL_MODE_SHIFT = 48;
    public static final long STEP_LEVEL_LEVEL_MASK = 280375465082880L;
    public static final int STEP_LEVEL_LEVEL_SHIFT = 40;
    public static final int STEP_LEVEL_MODE_DEVICE_IDLE = 8;
    public static final int STEP_LEVEL_MODE_POWER_SAVE = 4;
    public static final int STEP_LEVEL_MODE_SCREEN_STATE = 3;
    public static final long STEP_LEVEL_MODIFIED_MODE_MASK = -72057594037927936L;
    public static final int STEP_LEVEL_MODIFIED_MODE_SHIFT = 56;
    public static final long STEP_LEVEL_TIME_MASK = 1099511627775L;
    public static final int SYNC = 13;
    private static final String SYNC_DATA = "sy";
    private static final String TAG = "BatteryStats";
    private static final String UID_DATA = "uid";

    @VisibleForTesting
    public static final String UID_TIMES_TYPE_ALL = "A";
    private static final String USER_ACTIVITY_DATA = "ua";
    private static final String VERSION_DATA = "vers";
    private static final String VIBRATOR_DATA = "vib";
    public static final int VIBRATOR_ON = 9;
    private static final String VIDEO_DATA = "vid";
    public static final int VIDEO_TURNED_ON = 8;
    private static final String WAKELOCK_DATA = "wl";
    private static final String WAKEUP_ALARM_DATA = "wua";
    private static final String WAKEUP_REASON_DATA = "wr";
    public static final int WAKE_TYPE_DRAW = 18;
    public static final int WAKE_TYPE_FULL = 1;
    public static final int WAKE_TYPE_PARTIAL = 0;
    public static final int WAKE_TYPE_WINDOW = 2;
    public static final int WIFI_AGGREGATE_MULTICAST_ENABLED = 23;
    public static final int WIFI_BATCHED_SCAN = 11;
    private static final String WIFI_CONTROLLER_DATA = "wfcd";
    private static final String WIFI_CONTROLLER_NAME = "WiFi";
    private static final String WIFI_DATA = "wfl";
    private static final String WIFI_MULTICAST_DATA = "wmc";
    public static final int WIFI_MULTICAST_ENABLED = 7;
    private static final String WIFI_MULTICAST_TOTAL_DATA = "wmct";
    public static final int WIFI_RUNNING = 4;
    public static final int WIFI_SCAN = 6;
    private static final String WIFI_SIGNAL_STRENGTH_COUNT_DATA = "wsgc";
    private static final String WIFI_SIGNAL_STRENGTH_TIME_DATA = "wsgt";
    private static final String WIFI_STATE_COUNT_DATA = "wsc";
    public static final int WIFI_STATE_OFF = 0;
    public static final int WIFI_STATE_OFF_SCANNING = 1;
    public static final int WIFI_STATE_ON_CONNECTED_P2P = 5;
    public static final int WIFI_STATE_ON_CONNECTED_STA = 4;
    public static final int WIFI_STATE_ON_CONNECTED_STA_P2P = 6;
    public static final int WIFI_STATE_ON_DISCONNECTED = 3;
    public static final int WIFI_STATE_ON_NO_NETWORKS = 2;
    public static final int WIFI_STATE_SOFT_AP = 7;
    private static final String WIFI_STATE_TIME_DATA = "wst";
    public static final int WIFI_SUPPL_STATE_ASSOCIATED = 7;
    public static final int WIFI_SUPPL_STATE_ASSOCIATING = 6;
    public static final int WIFI_SUPPL_STATE_AUTHENTICATING = 5;
    public static final int WIFI_SUPPL_STATE_COMPLETED = 10;
    private static final String WIFI_SUPPL_STATE_COUNT_DATA = "wssc";
    public static final int WIFI_SUPPL_STATE_DISCONNECTED = 1;
    public static final int WIFI_SUPPL_STATE_DORMANT = 11;
    public static final int WIFI_SUPPL_STATE_FOUR_WAY_HANDSHAKE = 8;
    public static final int WIFI_SUPPL_STATE_GROUP_HANDSHAKE = 9;
    public static final int WIFI_SUPPL_STATE_INACTIVE = 3;
    public static final int WIFI_SUPPL_STATE_INTERFACE_DISABLED = 2;
    public static final int WIFI_SUPPL_STATE_INVALID = 0;
    public static final int WIFI_SUPPL_STATE_SCANNING = 4;
    private static final String WIFI_SUPPL_STATE_TIME_DATA = "wsst";
    public static final int WIFI_SUPPL_STATE_UNINITIALIZED = 12;
    private final StringBuilder mFormatBuilder = new StringBuilder(32);
    private final Formatter mFormatter = new Formatter(this.mFormatBuilder);
    private static final String[] STAT_NAMES = {"l", FullBackup.CACHE_TREE_TOKEN, "u"};
    public static final long[] JOB_FRESHNESS_BUCKETS = {3600000, 7200000, 14400000, 28800000, Long.MAX_VALUE};
    static final String[] SCREEN_BRIGHTNESS_NAMES = {"dark", "dim", "medium", "light", "bright"};
    static final String[] SCREEN_BRIGHTNESS_SHORT_NAMES = {"0", "1", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"};
    static final String[] DATA_CONNECTION_NAMES = {"none", "gprs", "edge", "umts", "cdma", "evdo_0", "evdo_A", "1xrtt", "hsdpa", "hsupa", "hspa", "iden", "evdo_b", "lte", "ehrpd", "hspap", "gsm", "td_scdma", "iwlan", "lte_ca", "other"};
    static final String[] WIFI_SUPPL_STATE_NAMES = {"invalid", "disconn", "disabled", "inactive", "scanning", "authenticating", "associating", "associated", "4-way-handshake", "group-handshake", "completed", "dormant", "uninit"};
    static final String[] WIFI_SUPPL_STATE_SHORT_NAMES = {"inv", "dsc", "dis", "inact", "scan", "auth", "ascing", "asced", "4-way", WifiConfiguration.GroupCipher.varName, "compl", "dorm", "uninit"};
    public static final BitDescription[] HISTORY_STATE_DESCRIPTIONS = {new BitDescription(Integer.MIN_VALUE, "running", FullBackup.ROOT_TREE_TOKEN), new BitDescription(1073741824, "wake_lock", "w"), new BitDescription(8388608, Context.SENSOR_SERVICE, "s"), new BitDescription(536870912, LocationManager.GPS_PROVIDER, "g"), new BitDescription(268435456, "wifi_full_lock", "Wl"), new BitDescription(134217728, "wifi_scan", "Ws"), new BitDescription(65536, "wifi_multicast", "Wm"), new BitDescription(67108864, "wifi_radio", "Wr"), new BitDescription(33554432, "mobile_radio", "Pr"), new BitDescription(2097152, "phone_scanning", "Psc"), new BitDescription(4194304, "audio", FullBackup.APK_TREE_TOKEN), new BitDescription(1048576, "screen", ExifInterface.LATITUDE_SOUTH), new BitDescription(524288, BatteryManager.EXTRA_PLUGGED, "BP"), new BitDescription(262144, "screen_doze", "Sd"), new BitDescription(HistoryItem.STATE_DATA_CONNECTION_MASK, 9, "data_conn", "Pcn", DATA_CONNECTION_NAMES, DATA_CONNECTION_NAMES), new BitDescription(448, 6, "phone_state", "Pst", new String[]{"in", "out", PhoneConstants.APN_TYPE_EMERGENCY, "off"}, new String[]{"in", "out", "em", "off"}), new BitDescription(56, 3, "phone_signal_strength", "Pss", SignalStrength.SIGNAL_STRENGTH_NAMES, new String[]{"0", "1", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"}), new BitDescription(7, 0, "brightness", "Sb", SCREEN_BRIGHTNESS_NAMES, SCREEN_BRIGHTNESS_SHORT_NAMES)};
    public static final BitDescription[] HISTORY_STATE2_DESCRIPTIONS = {new BitDescription(Integer.MIN_VALUE, "power_save", "ps"), new BitDescription(1073741824, "video", Telephony.BaseMmsColumns.MMS_VERSION), new BitDescription(536870912, "wifi_running", "Ww"), new BitDescription(268435456, "wifi", ExifInterface.LONGITUDE_WEST), new BitDescription(134217728, "flashlight", "fl"), new BitDescription(HistoryItem.STATE2_DEVICE_IDLE_MASK, 25, "device_idle", "di", new String[]{"off", "light", "full", "???"}, new String[]{"off", "light", "full", "???"}), new BitDescription(16777216, "charging", "ch"), new BitDescription(262144, "usb_data", "Ud"), new BitDescription(8388608, "phone_in_call", "Pcl"), new BitDescription(4194304, "bluetooth", "b"), new BitDescription(112, 4, "wifi_signal_strength", "Wss", new String[]{"0", "1", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"}, new String[]{"0", "1", ExifInterface.GPS_MEASUREMENT_2D, ExifInterface.GPS_MEASUREMENT_3D, "4"}), new BitDescription(15, 0, "wifi_suppl", "Wsp", WIFI_SUPPL_STATE_NAMES, WIFI_SUPPL_STATE_SHORT_NAMES), new BitDescription(2097152, Context.CAMERA_SERVICE, "ca"), new BitDescription(1048576, "ble_scan", "bles"), new BitDescription(524288, "cellular_high_tx_power", "Chtp"), new BitDescription(128, 7, "gps_signal_quality", "Gss", new String[]{"poor", "good"}, new String[]{"poor", "good"})};
    private static final String FOREGROUND_ACTIVITY_DATA = "fg";
    public static final String[] HISTORY_EVENT_NAMES = {"null", "proc", FOREGROUND_ACTIVITY_DATA, "top", "sync", "wake_lock_in", "job", "user", "userfg", "conn", "active", "pkginst", "pkgunin", "alarm", Context.STATS_MANAGER, "pkginactive", "pkgactive", "tmpwhitelist", "screenwake", "wakeupap", "longwake", "est_capacity"};
    public static final String[] HISTORY_EVENT_CHECKIN_NAMES = {"Enl", "Epr", "Efg", "Etp", "Esy", "Ewl", "Ejb", "Eur", "Euf", "Ecn", "Eac", "Epi", "Epu", "Eal", "Est", "Eai", "Eaa", "Etw", "Esw", "Ewa", "Elw", "Eec"};
    private static final IntToString sUidToString = new IntToString() { // from class: android.os.-$$Lambda$IyvVQC-0mKtsfXbnO0kDL64hrk0
        @Override // android.os.BatteryStats.IntToString
        public final String applyAsString(int i) {
            return UserHandle.formatUid(i);
        }
    };
    private static final IntToString sIntToString = new IntToString() { // from class: android.os.-$$Lambda$q1UvBdLgHRZVzc68BxdksTmbuCw
        @Override // android.os.BatteryStats.IntToString
        public final String applyAsString(int i) {
            return Integer.toString(i);
        }
    };
    public static final IntToString[] HISTORY_EVENT_INT_FORMATTERS = {sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sUidToString, sIntToString};
    static final String[] WIFI_STATE_NAMES = {"off", "scanning", "no_net", "disconn", "sta", "p2p", "sta_p2p", "soft_ap"};
    public static final int[] STEP_LEVEL_MODES_OF_INTEREST = {7, 15, 11, 7, 7, 7, 7, 7, 15, 11};
    public static final int[] STEP_LEVEL_MODE_VALUES = {0, 4, 8, 1, 5, 2, 6, 3, 7, 11};
    public static final String[] STEP_LEVEL_MODE_LABELS = {"screen off", "screen off power save", "screen off device idle", "screen on", "screen on power save", "screen doze", "screen doze power save", "screen doze-suspend", "screen doze-suspend power save", "screen doze-suspend device idle"};

    public static final class BitDescription {
        public final int mask;
        public final String name;
        public final int shift;
        public final String shortName;
        public final String[] shortValues;
        public final String[] values;

        public BitDescription(int i, int i2, String str, String str2, String[] strArr, String[] strArr2) {
            this.mask = i;
            this.shift = i2;
            this.name = str;
            this.shortName = str2;
            this.values = strArr;
            this.shortValues = strArr2;
        }

        public BitDescription(int i, String str, String str2) {
            this.mask = i;
            this.shift = -1;
            this.name = str;
            this.shortName = str2;
            this.values = null;
            this.shortValues = null;
        }
    }

    public static abstract class ControllerActivityCounter {
        public abstract LongCounter getIdleTimeCounter();

        public abstract LongCounter getPowerCounter();

        public abstract LongCounter getRxTimeCounter();

        public abstract LongCounter getScanTimeCounter();

        public abstract LongCounter getSleepTimeCounter();

        public abstract LongCounter[] getTxTimeCounters();
    }

    public static abstract class Counter {
        public abstract int getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static final class DailyItem {
        public LevelStepTracker mChargeSteps;
        public LevelStepTracker mDischargeSteps;
        public long mEndTime;
        public ArrayList<PackageChange> mPackageChanges;
        public long mStartTime;
    }

    public static final class HistoryEventTracker {
        private final HashMap<String, SparseIntArray>[] mActiveEvents = new HashMap[22];

        public HashMap<String, SparseIntArray> getStateForEvent(int i) {
            return this.mActiveEvents[i];
        }

        public void removeEvents(int i) {
            this.mActiveEvents[i & HistoryItem.EVENT_TYPE_MASK] = null;
        }

        public boolean updateState(int i, String str, int i2, int i3) {
            SparseIntArray sparseIntArray;
            int iIndexOfKey;
            if ((32768 & i) == 0) {
                if ((i & 16384) == 0) {
                    return true;
                }
                HashMap<String, SparseIntArray> map = this.mActiveEvents[i & HistoryItem.EVENT_TYPE_MASK];
                if (map == null || (sparseIntArray = map.get(str)) == null || (iIndexOfKey = sparseIntArray.indexOfKey(i2)) < 0) {
                    return false;
                }
                sparseIntArray.removeAt(iIndexOfKey);
                if (sparseIntArray.size() > 0) {
                    return true;
                }
                map.remove(str);
                return true;
            }
            int i4 = i & HistoryItem.EVENT_TYPE_MASK;
            HashMap<String, SparseIntArray> map2 = this.mActiveEvents[i4];
            if (map2 == null) {
                map2 = new HashMap<>();
                this.mActiveEvents[i4] = map2;
            }
            SparseIntArray sparseIntArray2 = map2.get(str);
            if (sparseIntArray2 == null) {
                sparseIntArray2 = new SparseIntArray();
                map2.put(str, sparseIntArray2);
            }
            if (sparseIntArray2.indexOfKey(i2) >= 0) {
                return false;
            }
            sparseIntArray2.put(i2, i3);
            return true;
        }
    }

    public static final class HistoryItem implements Parcelable {
        public static final byte CMD_CURRENT_TIME = 5;
        public static final byte CMD_NULL = -1;
        public static final byte CMD_OVERFLOW = 6;
        public static final byte CMD_RESET = 7;
        public static final byte CMD_SHUTDOWN = 8;
        public static final byte CMD_START = 4;
        public static final byte CMD_UPDATE = 0;
        public static final int EVENT_ACTIVE = 10;
        public static final int EVENT_ALARM = 13;
        public static final int EVENT_ALARM_FINISH = 16397;
        public static final int EVENT_ALARM_START = 32781;
        public static final int EVENT_COLLECT_EXTERNAL_STATS = 14;
        public static final int EVENT_CONNECTIVITY_CHANGED = 9;
        public static final int EVENT_COUNT = 22;
        public static final int EVENT_FLAG_FINISH = 16384;
        public static final int EVENT_FLAG_START = 32768;
        public static final int EVENT_FOREGROUND = 2;
        public static final int EVENT_FOREGROUND_FINISH = 16386;
        public static final int EVENT_FOREGROUND_START = 32770;
        public static final int EVENT_JOB = 6;
        public static final int EVENT_JOB_FINISH = 16390;
        public static final int EVENT_JOB_START = 32774;
        public static final int EVENT_LONG_WAKE_LOCK = 20;
        public static final int EVENT_LONG_WAKE_LOCK_FINISH = 16404;
        public static final int EVENT_LONG_WAKE_LOCK_START = 32788;
        public static final int EVENT_NONE = 0;
        public static final int EVENT_PACKAGE_ACTIVE = 16;
        public static final int EVENT_PACKAGE_INACTIVE = 15;
        public static final int EVENT_PACKAGE_INSTALLED = 11;
        public static final int EVENT_PACKAGE_UNINSTALLED = 12;
        public static final int EVENT_PROC = 1;
        public static final int EVENT_PROC_FINISH = 16385;
        public static final int EVENT_PROC_START = 32769;
        public static final int EVENT_SCREEN_WAKE_UP = 18;
        public static final int EVENT_SYNC = 4;
        public static final int EVENT_SYNC_FINISH = 16388;
        public static final int EVENT_SYNC_START = 32772;
        public static final int EVENT_TEMP_WHITELIST = 17;
        public static final int EVENT_TEMP_WHITELIST_FINISH = 16401;
        public static final int EVENT_TEMP_WHITELIST_START = 32785;
        public static final int EVENT_TOP = 3;
        public static final int EVENT_TOP_FINISH = 16387;
        public static final int EVENT_TOP_START = 32771;
        public static final int EVENT_TYPE_MASK = -49153;
        public static final int EVENT_USER_FOREGROUND = 8;
        public static final int EVENT_USER_FOREGROUND_FINISH = 16392;
        public static final int EVENT_USER_FOREGROUND_START = 32776;
        public static final int EVENT_USER_RUNNING = 7;
        public static final int EVENT_USER_RUNNING_FINISH = 16391;
        public static final int EVENT_USER_RUNNING_START = 32775;
        public static final int EVENT_WAKEUP_AP = 19;
        public static final int EVENT_WAKE_LOCK = 5;
        public static final int EVENT_WAKE_LOCK_FINISH = 16389;
        public static final int EVENT_WAKE_LOCK_START = 32773;
        public static final int MOST_INTERESTING_STATES = 1835008;
        public static final int MOST_INTERESTING_STATES2 = -1749024768;
        public static final int SETTLE_TO_ZERO_STATES = -1900544;
        public static final int SETTLE_TO_ZERO_STATES2 = 1748959232;
        public static final int STATE2_BLUETOOTH_ON_FLAG = 4194304;
        public static final int STATE2_BLUETOOTH_SCAN_FLAG = 1048576;
        public static final int STATE2_CAMERA_FLAG = 2097152;
        public static final int STATE2_CELLULAR_HIGH_TX_POWER_FLAG = 524288;
        public static final int STATE2_CHARGING_FLAG = 16777216;
        public static final int STATE2_DEVICE_IDLE_MASK = 100663296;
        public static final int STATE2_DEVICE_IDLE_SHIFT = 25;
        public static final int STATE2_FLASHLIGHT_FLAG = 134217728;
        public static final int STATE2_GPS_SIGNAL_QUALITY_MASK = 128;
        public static final int STATE2_GPS_SIGNAL_QUALITY_SHIFT = 7;
        public static final int STATE2_PHONE_IN_CALL_FLAG = 8388608;
        public static final int STATE2_POWER_SAVE_FLAG = Integer.MIN_VALUE;
        public static final int STATE2_USB_DATA_LINK_FLAG = 262144;
        public static final int STATE2_VIDEO_ON_FLAG = 1073741824;
        public static final int STATE2_WIFI_ON_FLAG = 268435456;
        public static final int STATE2_WIFI_RUNNING_FLAG = 536870912;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_MASK = 112;
        public static final int STATE2_WIFI_SIGNAL_STRENGTH_SHIFT = 4;
        public static final int STATE2_WIFI_SUPPL_STATE_MASK = 15;
        public static final int STATE2_WIFI_SUPPL_STATE_SHIFT = 0;
        public static final int STATE_AUDIO_ON_FLAG = 4194304;
        public static final int STATE_BATTERY_PLUGGED_FLAG = 524288;
        public static final int STATE_BRIGHTNESS_MASK = 7;
        public static final int STATE_BRIGHTNESS_SHIFT = 0;
        public static final int STATE_CPU_RUNNING_FLAG = Integer.MIN_VALUE;
        public static final int STATE_DATA_CONNECTION_MASK = 15872;
        public static final int STATE_DATA_CONNECTION_SHIFT = 9;
        public static final int STATE_GPS_ON_FLAG = 536870912;
        public static final int STATE_MOBILE_RADIO_ACTIVE_FLAG = 33554432;
        public static final int STATE_PHONE_SCANNING_FLAG = 2097152;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_MASK = 56;
        public static final int STATE_PHONE_SIGNAL_STRENGTH_SHIFT = 3;
        public static final int STATE_PHONE_STATE_MASK = 448;
        public static final int STATE_PHONE_STATE_SHIFT = 6;
        private static final int STATE_RESERVED_0 = 16777216;
        public static final int STATE_SCREEN_DOZE_FLAG = 262144;
        public static final int STATE_SCREEN_ON_FLAG = 1048576;
        public static final int STATE_SENSOR_ON_FLAG = 8388608;
        public static final int STATE_WAKE_LOCK_FLAG = 1073741824;
        public static final int STATE_WIFI_FULL_LOCK_FLAG = 268435456;
        public static final int STATE_WIFI_MULTICAST_ON_FLAG = 65536;
        public static final int STATE_WIFI_RADIO_ACTIVE_FLAG = 67108864;
        public static final int STATE_WIFI_SCAN_FLAG = 134217728;
        public int batteryChargeUAh;
        public byte batteryHealth;
        public byte batteryLevel;
        public byte batteryPlugType;
        public byte batteryStatus;
        public short batteryTemperature;
        public char batteryVoltage;
        public byte cmd;
        public long currentTime;
        public int eventCode;
        public HistoryTag eventTag;
        public final HistoryTag localEventTag;
        public final HistoryTag localWakeReasonTag;
        public final HistoryTag localWakelockTag;
        public HistoryItem next;
        public int numReadInts;
        public int states;
        public int states2;
        public HistoryStepDetails stepDetails;
        public long time;
        public HistoryTag wakeReasonTag;
        public HistoryTag wakelockTag;

        public HistoryItem() {
            this.cmd = (byte) -1;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
        }

        public HistoryItem(long j, Parcel parcel) {
            this.cmd = (byte) -1;
            this.localWakelockTag = new HistoryTag();
            this.localWakeReasonTag = new HistoryTag();
            this.localEventTag = new HistoryTag();
            this.time = j;
            this.numReadInts = 2;
            readFromParcel(parcel);
        }

        private void setToCommon(HistoryItem historyItem) {
            this.batteryLevel = historyItem.batteryLevel;
            this.batteryStatus = historyItem.batteryStatus;
            this.batteryHealth = historyItem.batteryHealth;
            this.batteryPlugType = historyItem.batteryPlugType;
            this.batteryTemperature = historyItem.batteryTemperature;
            this.batteryVoltage = historyItem.batteryVoltage;
            this.batteryChargeUAh = historyItem.batteryChargeUAh;
            this.states = historyItem.states;
            this.states2 = historyItem.states2;
            if (historyItem.wakelockTag != null) {
                this.wakelockTag = this.localWakelockTag;
                this.wakelockTag.setTo(historyItem.wakelockTag);
            } else {
                this.wakelockTag = null;
            }
            if (historyItem.wakeReasonTag != null) {
                this.wakeReasonTag = this.localWakeReasonTag;
                this.wakeReasonTag.setTo(historyItem.wakeReasonTag);
            } else {
                this.wakeReasonTag = null;
            }
            this.eventCode = historyItem.eventCode;
            if (historyItem.eventTag != null) {
                this.eventTag = this.localEventTag;
                this.eventTag.setTo(historyItem.eventTag);
            } else {
                this.eventTag = null;
            }
            this.currentTime = historyItem.currentTime;
        }

        public void clear() {
            this.time = 0L;
            this.cmd = (byte) -1;
            this.batteryLevel = (byte) 0;
            this.batteryStatus = (byte) 0;
            this.batteryHealth = (byte) 0;
            this.batteryPlugType = (byte) 0;
            this.batteryTemperature = (short) 0;
            this.batteryVoltage = (char) 0;
            this.batteryChargeUAh = 0;
            this.states = 0;
            this.states2 = 0;
            this.wakelockTag = null;
            this.wakeReasonTag = null;
            this.eventCode = 0;
            this.eventTag = null;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public boolean isDeltaData() {
            return this.cmd == 0;
        }

        public void readFromParcel(Parcel parcel) {
            int iDataPosition = parcel.dataPosition();
            int i = parcel.readInt();
            this.cmd = (byte) (i & 255);
            this.batteryLevel = (byte) ((i >> 8) & 255);
            this.batteryStatus = (byte) ((i >> 16) & 15);
            this.batteryHealth = (byte) ((i >> 20) & 15);
            this.batteryPlugType = (byte) ((i >> 24) & 15);
            int i2 = parcel.readInt();
            this.batteryTemperature = (short) (i2 & 65535);
            this.batteryVoltage = (char) ((i2 >> 16) & 65535);
            this.batteryChargeUAh = parcel.readInt();
            this.states = parcel.readInt();
            this.states2 = parcel.readInt();
            if ((268435456 & i) != 0) {
                this.wakelockTag = this.localWakelockTag;
                this.wakelockTag.readFromParcel(parcel);
            } else {
                this.wakelockTag = null;
            }
            if ((536870912 & i) != 0) {
                this.wakeReasonTag = this.localWakeReasonTag;
                this.wakeReasonTag.readFromParcel(parcel);
            } else {
                this.wakeReasonTag = null;
            }
            if ((i & 1073741824) != 0) {
                this.eventCode = parcel.readInt();
                this.eventTag = this.localEventTag;
                this.eventTag.readFromParcel(parcel);
            } else {
                this.eventCode = 0;
                this.eventTag = null;
            }
            if (this.cmd == 5 || this.cmd == 7) {
                this.currentTime = parcel.readLong();
            } else {
                this.currentTime = 0L;
            }
            this.numReadInts += (parcel.dataPosition() - iDataPosition) / 4;
        }

        public boolean same(HistoryItem historyItem) {
            if (!sameNonEvent(historyItem) || this.eventCode != historyItem.eventCode) {
                return false;
            }
            if (this.wakelockTag != historyItem.wakelockTag && (this.wakelockTag == null || historyItem.wakelockTag == null || !this.wakelockTag.equals(historyItem.wakelockTag))) {
                return false;
            }
            if (this.wakeReasonTag != historyItem.wakeReasonTag && (this.wakeReasonTag == null || historyItem.wakeReasonTag == null || !this.wakeReasonTag.equals(historyItem.wakeReasonTag))) {
                return false;
            }
            if (this.eventTag != historyItem.eventTag) {
                return (this.eventTag == null || historyItem.eventTag == null || !this.eventTag.equals(historyItem.eventTag)) ? false : true;
            }
            return true;
        }

        public boolean sameNonEvent(HistoryItem historyItem) {
            return this.batteryLevel == historyItem.batteryLevel && this.batteryStatus == historyItem.batteryStatus && this.batteryHealth == historyItem.batteryHealth && this.batteryPlugType == historyItem.batteryPlugType && this.batteryTemperature == historyItem.batteryTemperature && this.batteryVoltage == historyItem.batteryVoltage && this.batteryChargeUAh == historyItem.batteryChargeUAh && this.states == historyItem.states && this.states2 == historyItem.states2 && this.currentTime == historyItem.currentTime;
        }

        public void setTo(long j, byte b, HistoryItem historyItem) {
            this.time = j;
            this.cmd = b;
            setToCommon(historyItem);
        }

        public void setTo(HistoryItem historyItem) {
            this.time = historyItem.time;
            this.cmd = historyItem.cmd;
            setToCommon(historyItem);
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.time);
            parcel.writeInt((this.cmd & 255) | ((this.batteryLevel << 8) & 65280) | ((this.batteryStatus << WifiScanner.PnoSettings.PnoNetwork.FLAG_SAME_NETWORK) & SurfaceControl.FX_SURFACE_MASK) | ((this.batteryHealth << 20) & 15728640) | ((this.batteryPlugType << 24) & 251658240) | (this.wakelockTag != null ? 268435456 : 0) | (this.wakeReasonTag != null ? 536870912 : 0) | (this.eventCode != 0 ? 1073741824 : 0));
            parcel.writeInt((this.batteryTemperature & HostEndPoint.BROADCAST) | ((this.batteryVoltage << 16) & (-65536)));
            parcel.writeInt(this.batteryChargeUAh);
            parcel.writeInt(this.states);
            parcel.writeInt(this.states2);
            if (this.wakelockTag != null) {
                this.wakelockTag.writeToParcel(parcel, i);
            }
            if (this.wakeReasonTag != null) {
                this.wakeReasonTag.writeToParcel(parcel, i);
            }
            if (this.eventCode != 0) {
                parcel.writeInt(this.eventCode);
                this.eventTag.writeToParcel(parcel, i);
            }
            if (this.cmd == 5 || this.cmd == 7) {
                parcel.writeLong(this.currentTime);
            }
        }
    }

    public static class HistoryPrinter {
        int oldState = 0;
        int oldState2 = 0;
        int oldLevel = -1;
        int oldStatus = -1;
        int oldHealth = -1;
        int oldPlug = -1;
        int oldTemp = -1;
        int oldVolt = -1;
        int oldChargeMAh = -1;
        long lastTime = -1;

        private String printNextItem(HistoryItem historyItem, long j, boolean z, boolean z2) {
            StringBuilder sb = new StringBuilder();
            if (z) {
                sb.append(9);
                sb.append(',');
                sb.append(BatteryStats.HISTORY_DATA);
                sb.append(',');
                if (this.lastTime < 0) {
                    sb.append(historyItem.time - j);
                } else {
                    sb.append(historyItem.time - this.lastTime);
                }
                this.lastTime = historyItem.time;
            } else {
                sb.append("  ");
                TimeUtils.formatDuration(historyItem.time - j, sb, 19);
                sb.append(" (");
                sb.append(historyItem.numReadInts);
                sb.append(") ");
            }
            if (historyItem.cmd == 4) {
                if (z) {
                    sb.append(SettingsStringUtil.DELIMITER);
                }
                sb.append("START\n");
                reset();
            } else if (historyItem.cmd == 5 || historyItem.cmd == 7) {
                if (z) {
                    sb.append(SettingsStringUtil.DELIMITER);
                }
                if (historyItem.cmd == 7) {
                    sb.append("RESET:");
                    reset();
                }
                sb.append("TIME:");
                if (z) {
                    sb.append(historyItem.currentTime);
                    sb.append("\n");
                } else {
                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                    sb.append(DateFormat.format("yyyy-MM-dd-HH-mm-ss", historyItem.currentTime).toString());
                    sb.append("\n");
                }
            } else if (historyItem.cmd == 8) {
                if (z) {
                    sb.append(SettingsStringUtil.DELIMITER);
                }
                sb.append("SHUTDOWN\n");
            } else if (historyItem.cmd == 6) {
                if (z) {
                    sb.append(SettingsStringUtil.DELIMITER);
                }
                sb.append("*OVERFLOW*\n");
            } else {
                if (!z) {
                    if (historyItem.batteryLevel < 10) {
                        sb.append("00");
                    } else if (historyItem.batteryLevel < 100) {
                        sb.append("0");
                    }
                    sb.append(historyItem.batteryLevel);
                    if (z2) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        if (historyItem.states >= 0) {
                            if (historyItem.states < 16) {
                                sb.append("0000000");
                            } else if (historyItem.states < 256) {
                                sb.append("000000");
                            } else if (historyItem.states < 4096) {
                                sb.append("00000");
                            } else if (historyItem.states < 65536) {
                                sb.append("0000");
                            } else if (historyItem.states < 1048576) {
                                sb.append("000");
                            } else if (historyItem.states < 16777216) {
                                sb.append("00");
                            } else if (historyItem.states < 268435456) {
                                sb.append("0");
                            }
                        }
                        sb.append(Integer.toHexString(historyItem.states));
                    }
                } else if (this.oldLevel != historyItem.batteryLevel) {
                    this.oldLevel = historyItem.batteryLevel;
                    sb.append(",Bl=");
                    sb.append(historyItem.batteryLevel);
                }
                if (this.oldStatus != historyItem.batteryStatus) {
                    this.oldStatus = historyItem.batteryStatus;
                    sb.append(z ? ",Bs=" : " status=");
                    switch (this.oldStatus) {
                        case 1:
                            sb.append(z ? "?" : "unknown");
                            break;
                        case 2:
                            sb.append(z ? FullBackup.CACHE_TREE_TOKEN : "charging");
                            break;
                        case 3:
                            sb.append(z ? "d" : "discharging");
                            break;
                        case 4:
                            sb.append(z ? "n" : "not-charging");
                            break;
                        case 5:
                            sb.append(z ? FullBackup.FILES_TREE_TOKEN : "full");
                            break;
                        default:
                            sb.append(this.oldStatus);
                            break;
                    }
                }
                if (this.oldHealth != historyItem.batteryHealth) {
                    this.oldHealth = historyItem.batteryHealth;
                    sb.append(z ? ",Bh=" : " health=");
                    switch (this.oldHealth) {
                        case 1:
                            sb.append(z ? "?" : "unknown");
                            break;
                        case 2:
                            sb.append(z ? "g" : "good");
                            break;
                        case 3:
                            sb.append(z ? BatteryStats.HISTORY_DATA : "overheat");
                            break;
                        case 4:
                            sb.append(z ? "d" : "dead");
                            break;
                        case 5:
                            sb.append(z ? Telephony.BaseMmsColumns.MMS_VERSION : "over-voltage");
                            break;
                        case 6:
                            sb.append(z ? FullBackup.FILES_TREE_TOKEN : "failure");
                            break;
                        case 7:
                            sb.append(z ? FullBackup.CACHE_TREE_TOKEN : "cold");
                            break;
                        default:
                            sb.append(this.oldHealth);
                            break;
                    }
                }
                if (this.oldPlug != historyItem.batteryPlugType) {
                    this.oldPlug = historyItem.batteryPlugType;
                    sb.append(z ? ",Bp=" : " plug=");
                    int i = this.oldPlug;
                    if (i != 4) {
                        switch (i) {
                            case 0:
                                sb.append(z ? "n" : "none");
                                break;
                            case 1:
                                sb.append(z ? FullBackup.APK_TREE_TOKEN : "ac");
                                break;
                            case 2:
                                sb.append(z ? "u" : Context.USB_SERVICE);
                                break;
                            default:
                                sb.append(this.oldPlug);
                                break;
                        }
                    } else {
                        sb.append(z ? "w" : "wireless");
                    }
                }
                if (this.oldTemp != historyItem.batteryTemperature) {
                    this.oldTemp = historyItem.batteryTemperature;
                    sb.append(z ? ",Bt=" : " temp=");
                    sb.append(this.oldTemp);
                }
                if (this.oldVolt != historyItem.batteryVoltage) {
                    this.oldVolt = historyItem.batteryVoltage;
                    sb.append(z ? ",Bv=" : " volt=");
                    sb.append(this.oldVolt);
                }
                int i2 = historyItem.batteryChargeUAh / 1000;
                if (this.oldChargeMAh != i2) {
                    this.oldChargeMAh = i2;
                    sb.append(z ? ",Bcc=" : " charge=");
                    sb.append(this.oldChargeMAh);
                }
                BatteryStats.printBitDescriptions(sb, this.oldState, historyItem.states, historyItem.wakelockTag, BatteryStats.HISTORY_STATE_DESCRIPTIONS, !z);
                BatteryStats.printBitDescriptions(sb, this.oldState2, historyItem.states2, null, BatteryStats.HISTORY_STATE2_DESCRIPTIONS, !z);
                if (historyItem.wakeReasonTag != null) {
                    if (z) {
                        sb.append(",wr=");
                        sb.append(historyItem.wakeReasonTag.poolIdx);
                    } else {
                        sb.append(" wake_reason=");
                        sb.append(historyItem.wakeReasonTag.uid);
                        sb.append(":\"");
                        sb.append(historyItem.wakeReasonTag.string);
                        sb.append("\"");
                    }
                }
                if (historyItem.eventCode != 0) {
                    sb.append(z ? "," : WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                    if ((historyItem.eventCode & 32768) != 0) {
                        sb.append("+");
                    } else if ((historyItem.eventCode & 16384) != 0) {
                        sb.append(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    }
                    String[] strArr = z ? BatteryStats.HISTORY_EVENT_CHECKIN_NAMES : BatteryStats.HISTORY_EVENT_NAMES;
                    int i3 = historyItem.eventCode & HistoryItem.EVENT_TYPE_MASK;
                    if (i3 < 0 || i3 >= strArr.length) {
                        sb.append(z ? "Ev" : "event");
                        sb.append(i3);
                    } else {
                        sb.append(strArr[i3]);
                    }
                    sb.append("=");
                    if (z) {
                        sb.append(historyItem.eventTag.poolIdx);
                    } else {
                        sb.append(BatteryStats.HISTORY_EVENT_INT_FORMATTERS[i3].applyAsString(historyItem.eventTag.uid));
                        sb.append(":\"");
                        sb.append(historyItem.eventTag.string);
                        sb.append("\"");
                    }
                }
                sb.append("\n");
                if (historyItem.stepDetails != null) {
                    if (z) {
                        sb.append(9);
                        sb.append(',');
                        sb.append(BatteryStats.HISTORY_DATA);
                        sb.append(",0,Dcpu=");
                        sb.append(historyItem.stepDetails.userTime);
                        sb.append(SettingsStringUtil.DELIMITER);
                        sb.append(historyItem.stepDetails.systemTime);
                        if (historyItem.stepDetails.appCpuUid1 >= 0) {
                            printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid1, historyItem.stepDetails.appCpuUTime1, historyItem.stepDetails.appCpuSTime1);
                            if (historyItem.stepDetails.appCpuUid2 >= 0) {
                                printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid2, historyItem.stepDetails.appCpuUTime2, historyItem.stepDetails.appCpuSTime2);
                            }
                            if (historyItem.stepDetails.appCpuUid3 >= 0) {
                                printStepCpuUidCheckinDetails(sb, historyItem.stepDetails.appCpuUid3, historyItem.stepDetails.appCpuUTime3, historyItem.stepDetails.appCpuSTime3);
                            }
                        }
                        sb.append("\n");
                        sb.append(9);
                        sb.append(',');
                        sb.append(BatteryStats.HISTORY_DATA);
                        sb.append(",0,Dpst=");
                        sb.append(historyItem.stepDetails.statUserTime);
                        sb.append(',');
                        sb.append(historyItem.stepDetails.statSystemTime);
                        sb.append(',');
                        sb.append(historyItem.stepDetails.statIOWaitTime);
                        sb.append(',');
                        sb.append(historyItem.stepDetails.statIrqTime);
                        sb.append(',');
                        sb.append(historyItem.stepDetails.statSoftIrqTime);
                        sb.append(',');
                        sb.append(historyItem.stepDetails.statIdlTime);
                        sb.append(',');
                        if (historyItem.stepDetails.statPlatformIdleState != null) {
                            sb.append(historyItem.stepDetails.statPlatformIdleState);
                            if (historyItem.stepDetails.statSubsystemPowerState != null) {
                                sb.append(',');
                            }
                        }
                        if (historyItem.stepDetails.statSubsystemPowerState != null) {
                            sb.append(historyItem.stepDetails.statSubsystemPowerState);
                        }
                        sb.append("\n");
                    } else {
                        sb.append("                 Details: cpu=");
                        sb.append(historyItem.stepDetails.userTime);
                        sb.append("u+");
                        sb.append(historyItem.stepDetails.systemTime);
                        sb.append("s");
                        if (historyItem.stepDetails.appCpuUid1 >= 0) {
                            sb.append(" (");
                            printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid1, historyItem.stepDetails.appCpuUTime1, historyItem.stepDetails.appCpuSTime1);
                            if (historyItem.stepDetails.appCpuUid2 >= 0) {
                                sb.append(", ");
                                printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid2, historyItem.stepDetails.appCpuUTime2, historyItem.stepDetails.appCpuSTime2);
                            }
                            if (historyItem.stepDetails.appCpuUid3 >= 0) {
                                sb.append(", ");
                                printStepCpuUidDetails(sb, historyItem.stepDetails.appCpuUid3, historyItem.stepDetails.appCpuUTime3, historyItem.stepDetails.appCpuSTime3);
                            }
                            sb.append(')');
                        }
                        sb.append("\n");
                        sb.append("                          /proc/stat=");
                        sb.append(historyItem.stepDetails.statUserTime);
                        sb.append(" usr, ");
                        sb.append(historyItem.stepDetails.statSystemTime);
                        sb.append(" sys, ");
                        sb.append(historyItem.stepDetails.statIOWaitTime);
                        sb.append(" io, ");
                        sb.append(historyItem.stepDetails.statIrqTime);
                        sb.append(" irq, ");
                        sb.append(historyItem.stepDetails.statSoftIrqTime);
                        sb.append(" sirq, ");
                        sb.append(historyItem.stepDetails.statIdlTime);
                        sb.append(" idle");
                        int i4 = historyItem.stepDetails.statUserTime + historyItem.stepDetails.statSystemTime + historyItem.stepDetails.statIOWaitTime + historyItem.stepDetails.statIrqTime + historyItem.stepDetails.statSoftIrqTime;
                        int i5 = historyItem.stepDetails.statIdlTime + i4;
                        if (i5 > 0) {
                            sb.append(" (");
                            sb.append(String.format("%.1f%%", Float.valueOf((i4 / i5) * 100.0f)));
                            sb.append(" of ");
                            StringBuilder sb2 = new StringBuilder(64);
                            BatteryStats.formatTimeMsNoSpace(sb2, i5 * 10);
                            sb.append((CharSequence) sb2);
                            sb.append(")");
                        }
                        sb.append(", PlatformIdleStat ");
                        sb.append(historyItem.stepDetails.statPlatformIdleState);
                        sb.append("\n");
                        sb.append(", SubsystemPowerState ");
                        sb.append(historyItem.stepDetails.statSubsystemPowerState);
                        sb.append("\n");
                    }
                }
                this.oldState = historyItem.states;
                this.oldState2 = historyItem.states2;
            }
            return sb.toString();
        }

        private void printStepCpuUidCheckinDetails(StringBuilder sb, int i, int i2, int i3) {
            sb.append('/');
            sb.append(i);
            sb.append(SettingsStringUtil.DELIMITER);
            sb.append(i2);
            sb.append(SettingsStringUtil.DELIMITER);
            sb.append(i3);
        }

        private void printStepCpuUidDetails(StringBuilder sb, int i, int i2, int i3) {
            UserHandle.formatUid(sb, i);
            sb.append("=");
            sb.append(i2);
            sb.append("u+");
            sb.append(i3);
            sb.append("s");
        }

        public void printNextItem(ProtoOutputStream protoOutputStream, HistoryItem historyItem, long j, boolean z) {
            String[] strArrSplit = printNextItem(historyItem, j, true, z).split("\n");
            for (String str : strArrSplit) {
                protoOutputStream.write(2237677961222L, str);
            }
        }

        public void printNextItem(PrintWriter printWriter, HistoryItem historyItem, long j, boolean z, boolean z2) {
            printWriter.print(printNextItem(historyItem, j, z, z2));
        }

        void reset() {
            this.oldState2 = 0;
            this.oldState = 0;
            this.oldLevel = -1;
            this.oldStatus = -1;
            this.oldHealth = -1;
            this.oldPlug = -1;
            this.oldTemp = -1;
            this.oldVolt = -1;
            this.oldChargeMAh = -1;
        }
    }

    public static final class HistoryStepDetails {
        public int appCpuSTime1;
        public int appCpuSTime2;
        public int appCpuSTime3;
        public int appCpuUTime1;
        public int appCpuUTime2;
        public int appCpuUTime3;
        public int appCpuUid1;
        public int appCpuUid2;
        public int appCpuUid3;
        public int statIOWaitTime;
        public int statIdlTime;
        public int statIrqTime;
        public String statPlatformIdleState;
        public int statSoftIrqTime;
        public String statSubsystemPowerState;
        public int statSystemTime;
        public int statUserTime;
        public int systemTime;
        public int userTime;

        public HistoryStepDetails() {
            clear();
        }

        public void clear() {
            this.systemTime = 0;
            this.userTime = 0;
            this.appCpuUid3 = -1;
            this.appCpuUid2 = -1;
            this.appCpuUid1 = -1;
            this.appCpuSTime3 = 0;
            this.appCpuUTime3 = 0;
            this.appCpuSTime2 = 0;
            this.appCpuUTime2 = 0;
            this.appCpuSTime1 = 0;
            this.appCpuUTime1 = 0;
        }

        public void readFromParcel(Parcel parcel) {
            this.userTime = parcel.readInt();
            this.systemTime = parcel.readInt();
            this.appCpuUid1 = parcel.readInt();
            this.appCpuUTime1 = parcel.readInt();
            this.appCpuSTime1 = parcel.readInt();
            this.appCpuUid2 = parcel.readInt();
            this.appCpuUTime2 = parcel.readInt();
            this.appCpuSTime2 = parcel.readInt();
            this.appCpuUid3 = parcel.readInt();
            this.appCpuUTime3 = parcel.readInt();
            this.appCpuSTime3 = parcel.readInt();
            this.statUserTime = parcel.readInt();
            this.statSystemTime = parcel.readInt();
            this.statIOWaitTime = parcel.readInt();
            this.statIrqTime = parcel.readInt();
            this.statSoftIrqTime = parcel.readInt();
            this.statIdlTime = parcel.readInt();
            this.statPlatformIdleState = parcel.readString();
            this.statSubsystemPowerState = parcel.readString();
        }

        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.userTime);
            parcel.writeInt(this.systemTime);
            parcel.writeInt(this.appCpuUid1);
            parcel.writeInt(this.appCpuUTime1);
            parcel.writeInt(this.appCpuSTime1);
            parcel.writeInt(this.appCpuUid2);
            parcel.writeInt(this.appCpuUTime2);
            parcel.writeInt(this.appCpuSTime2);
            parcel.writeInt(this.appCpuUid3);
            parcel.writeInt(this.appCpuUTime3);
            parcel.writeInt(this.appCpuSTime3);
            parcel.writeInt(this.statUserTime);
            parcel.writeInt(this.statSystemTime);
            parcel.writeInt(this.statIOWaitTime);
            parcel.writeInt(this.statIrqTime);
            parcel.writeInt(this.statSoftIrqTime);
            parcel.writeInt(this.statIdlTime);
            parcel.writeString(this.statPlatformIdleState);
            parcel.writeString(this.statSubsystemPowerState);
        }
    }

    public static final class HistoryTag {
        public int poolIdx;
        public String string;
        public int uid;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            HistoryTag historyTag = (HistoryTag) obj;
            return this.uid == historyTag.uid && this.string.equals(historyTag.string);
        }

        public int hashCode() {
            return (this.string.hashCode() * 31) + this.uid;
        }

        public void readFromParcel(Parcel parcel) {
            this.string = parcel.readString();
            this.uid = parcel.readInt();
            this.poolIdx = -1;
        }

        public void setTo(HistoryTag historyTag) {
            this.string = historyTag.string;
            this.uid = historyTag.uid;
            this.poolIdx = historyTag.poolIdx;
        }

        public void setTo(String str, int i) {
            this.string = str;
            this.uid = i;
            this.poolIdx = -1;
        }

        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.string);
            parcel.writeInt(this.uid);
        }
    }

    @FunctionalInterface
    public interface IntToString {
        String applyAsString(int i);
    }

    public static final class LevelStepTracker {
        public long mLastStepTime = -1;
        public int mNumStepDurations;
        public final long[] mStepDurations;

        public LevelStepTracker(int i) {
            this.mStepDurations = new long[i];
        }

        public LevelStepTracker(int i, long[] jArr) {
            this.mNumStepDurations = i;
            this.mStepDurations = new long[i];
            System.arraycopy(jArr, 0, this.mStepDurations, 0, i);
        }

        private void appendHex(long j, int i, StringBuilder sb) {
            boolean z = false;
            while (i >= 0) {
                int i2 = (int) ((j >> i) & 15);
                i -= 4;
                if (z || i2 != 0) {
                    z = true;
                    if (i2 < 0 || i2 > 9) {
                        sb.append((char) ((i2 + 97) - 10));
                    } else {
                        sb.append((char) (i2 + 48));
                    }
                }
            }
        }

        public void addLevelSteps(int i, long j, long j2) {
            int length = this.mNumStepDurations;
            long j3 = this.mLastStepTime;
            if (j3 >= 0 && i > 0) {
                long[] jArr = this.mStepDurations;
                long j4 = j2 - j3;
                for (int i2 = 0; i2 < i; i2++) {
                    System.arraycopy(jArr, 0, jArr, 1, jArr.length - 1);
                    long j5 = j4 / ((long) (i - i2));
                    j4 -= j5;
                    long j6 = BatteryStats.STEP_LEVEL_TIME_MASK;
                    if (j5 <= BatteryStats.STEP_LEVEL_TIME_MASK) {
                        j6 = j5;
                    }
                    jArr[0] = j6 | j;
                }
                length += i;
                if (length > jArr.length) {
                    length = jArr.length;
                }
            }
            this.mNumStepDurations = length;
            this.mLastStepTime = j2;
        }

        public void clearTime() {
            this.mLastStepTime = -1L;
        }

        public long computeTimeEstimate(long j, long j2, int[] iArr) {
            long[] jArr = this.mStepDurations;
            int i = this.mNumStepDurations;
            if (i <= 0) {
                return -1L;
            }
            long j3 = 0;
            int i2 = 0;
            for (int i3 = 0; i3 < i; i3++) {
                long j4 = (jArr[i3] & BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK) >> 48;
                if ((((jArr[i3] & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56) & j) == 0 && (j4 & j) == j2) {
                    i2++;
                    j3 += jArr[i3] & BatteryStats.STEP_LEVEL_TIME_MASK;
                }
            }
            if (i2 <= 0) {
                return -1L;
            }
            if (iArr != null) {
                iArr[0] = i2;
            }
            return (j3 / ((long) i2)) * 100;
        }

        public long computeTimePerLevel() {
            long[] jArr = this.mStepDurations;
            int i = this.mNumStepDurations;
            if (i <= 0) {
                return -1L;
            }
            long j = 0;
            for (int i2 = 0; i2 < i; i2++) {
                j += jArr[i2] & BatteryStats.STEP_LEVEL_TIME_MASK;
            }
            return j / ((long) i);
        }

        public void decodeEntryAt(int i, String str) {
            char cCharAt;
            char cCharAt2;
            int length = str.length();
            int i2 = 0;
            long j = 0;
            while (i2 < length && (cCharAt2 = str.charAt(i2)) != '-') {
                i2++;
                switch (cCharAt2) {
                    case 'D':
                        j |= 144115188075855872L;
                        break;
                    case 'F':
                        j |= 0;
                        break;
                    case 'I':
                        j |= 576460752303423488L;
                        break;
                    case 'O':
                        j |= 72057594037927936L;
                        break;
                    case 'P':
                        j |= 288230376151711744L;
                        break;
                    case 'Z':
                        j |= 216172782113783808L;
                        break;
                    case 'd':
                        j |= 562949953421312L;
                        break;
                    case 'f':
                        j |= 0;
                        break;
                    case 'i':
                        j |= 2251799813685248L;
                        break;
                    case 'o':
                        j |= 281474976710656L;
                        break;
                    case 'p':
                        j |= TrafficStats.PB_IN_BYTES;
                        break;
                    case 'z':
                        j |= 844424930131968L;
                        break;
                }
            }
            int i3 = i2 + 1;
            long j2 = 0;
            while (i3 < length && (cCharAt = str.charAt(i3)) != '-') {
                i3++;
                j2 <<= 4;
                if (cCharAt >= '0' && cCharAt <= '9') {
                    j2 += (long) (cCharAt - '0');
                } else if (cCharAt >= 'a' && cCharAt <= 'f') {
                    j2 += (long) ((cCharAt - 'a') + 10);
                } else if (cCharAt >= 'A' && cCharAt <= 'F') {
                    j2 += (long) ((cCharAt - 'A') + 10);
                }
            }
            int i4 = i3 + 1;
            long j3 = j | ((j2 << 40) & BatteryStats.STEP_LEVEL_LEVEL_MASK);
            long j4 = 0;
            while (i4 < length) {
                char cCharAt3 = str.charAt(i4);
                if (cCharAt3 == '-') {
                    this.mStepDurations[i] = (j4 & BatteryStats.STEP_LEVEL_TIME_MASK) | j3;
                }
                i4++;
                j4 <<= 4;
                if (cCharAt3 >= '0' && cCharAt3 <= '9') {
                    j4 += (long) (cCharAt3 - '0');
                } else if (cCharAt3 >= 'a' && cCharAt3 <= 'f') {
                    j4 += (long) ((cCharAt3 - 'a') + 10);
                } else if (cCharAt3 >= 'A' && cCharAt3 <= 'F') {
                    j4 += (long) ((cCharAt3 - 'A') + 10);
                }
            }
            this.mStepDurations[i] = (j4 & BatteryStats.STEP_LEVEL_TIME_MASK) | j3;
        }

        public void encodeEntryAt(int i, StringBuilder sb) {
            long j = this.mStepDurations[i];
            long j2 = BatteryStats.STEP_LEVEL_TIME_MASK & j;
            int i2 = (int) ((BatteryStats.STEP_LEVEL_LEVEL_MASK & j) >> 40);
            int i3 = (int) ((BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK & j) >> 48);
            int i4 = (int) ((j & BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK) >> 56);
            switch ((i3 & 3) + 1) {
                case 1:
                    sb.append('f');
                    break;
                case 2:
                    sb.append('o');
                    break;
                case 3:
                    sb.append(DateFormat.DATE);
                    break;
                case 4:
                    sb.append(DateFormat.TIME_ZONE);
                    break;
            }
            if ((i3 & 4) != 0) {
                sb.append('p');
            }
            if ((i3 & 8) != 0) {
                sb.append('i');
            }
            switch ((i4 & 3) + 1) {
                case 1:
                    sb.append('F');
                    break;
                case 2:
                    sb.append('O');
                    break;
                case 3:
                    sb.append('D');
                    break;
                case 4:
                    sb.append('Z');
                    break;
            }
            if ((i4 & 4) != 0) {
                sb.append('P');
            }
            if ((i4 & 8) != 0) {
                sb.append('I');
            }
            sb.append('-');
            appendHex(i2, 4, sb);
            sb.append('-');
            appendHex(j2, 36, sb);
        }

        public long getDurationAt(int i) {
            return BatteryStats.STEP_LEVEL_TIME_MASK & this.mStepDurations[i];
        }

        public int getInitModeAt(int i) {
            return (int) ((BatteryStats.STEP_LEVEL_INITIAL_MODE_MASK & this.mStepDurations[i]) >> 48);
        }

        public int getLevelAt(int i) {
            return (int) ((BatteryStats.STEP_LEVEL_LEVEL_MASK & this.mStepDurations[i]) >> 40);
        }

        public int getModModeAt(int i) {
            return (int) ((BatteryStats.STEP_LEVEL_MODIFIED_MODE_MASK & this.mStepDurations[i]) >> 56);
        }

        public void init() {
            this.mLastStepTime = -1L;
            this.mNumStepDurations = 0;
        }

        public void readFromParcel(Parcel parcel) {
            int i = parcel.readInt();
            if (i > this.mStepDurations.length) {
                throw new ParcelFormatException("more step durations than available: " + i);
            }
            this.mNumStepDurations = i;
            for (int i2 = 0; i2 < i; i2++) {
                this.mStepDurations[i2] = parcel.readLong();
            }
        }

        public void writeToParcel(Parcel parcel) {
            int i = this.mNumStepDurations;
            parcel.writeInt(i);
            for (int i2 = 0; i2 < i; i2++) {
                parcel.writeLong(this.mStepDurations[i2]);
            }
        }
    }

    public static abstract class LongCounter {
        public abstract long getCountLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static abstract class LongCounterArray {
        public abstract long[] getCountsLocked(int i);

        public abstract void logState(Printer printer, String str);
    }

    public static final class PackageChange {
        public String mPackageName;
        public boolean mUpdate;
        public long mVersionCode;
    }

    public static abstract class Timer {
        public abstract int getCountLocked(int i);

        public long getCurrentDurationMsLocked(long j) {
            return -1L;
        }

        public long getMaxDurationMsLocked(long j) {
            return -1L;
        }

        public Timer getSubTimer() {
            return null;
        }

        public abstract long getTimeSinceMarkLocked(long j);

        public long getTotalDurationMsLocked(long j) {
            return -1L;
        }

        public abstract long getTotalTimeLocked(long j, int i);

        public boolean isRunningLocked() {
            return false;
        }

        public abstract void logState(Printer printer, String str);
    }

    static final class TimerEntry {
        final int mId;
        final String mName;
        final long mTime;
        final Timer mTimer;

        TimerEntry(String str, int i, Timer timer, long j) {
            this.mName = str;
            this.mId = i;
            this.mTimer = timer;
            this.mTime = j;
        }
    }

    public static abstract class Uid {
        public static final int NUM_PROCESS_STATE = 7;
        public static final int NUM_USER_ACTIVITY_TYPES = 4;
        public static final int NUM_WIFI_BATCHED_SCAN_BINS = 5;
        public static final int PROCESS_STATE_BACKGROUND = 3;
        public static final int PROCESS_STATE_CACHED = 6;
        public static final int PROCESS_STATE_FOREGROUND = 2;
        public static final int PROCESS_STATE_FOREGROUND_SERVICE = 1;
        public static final int PROCESS_STATE_HEAVY_WEIGHT = 5;
        public static final int PROCESS_STATE_TOP = 0;
        public static final int PROCESS_STATE_TOP_SLEEPING = 4;
        static final String[] PROCESS_STATE_NAMES = {"Top", "Fg Service", "Foreground", "Background", "Top Sleeping", "Heavy Weight", "Cached"};

        @VisibleForTesting
        public static final String[] UID_PROCESS_TYPES = {ExifInterface.GPS_DIRECTION_TRUE, "FS", "F", "B", "TS", "HW", "C"};
        public static final int[] CRITICAL_PROC_STATES = {0, 1, 2};
        static final String[] USER_ACTIVITY_TYPES = {"other", "button", "touch", Context.ACCESSIBILITY_SERVICE};

        public class Pid {
            public int mWakeNesting;
            public long mWakeStartMs;
            public long mWakeSumMs;

            public Pid() {
            }
        }

        public static abstract class Pkg {

            public static abstract class Serv {
                public abstract int getLaunches(int i);

                public abstract long getStartTime(long j, int i);

                public abstract int getStarts(int i);
            }

            public abstract ArrayMap<String, ? extends Serv> getServiceStats();

            public abstract ArrayMap<String, ? extends Counter> getWakeupAlarmStats();
        }

        public static abstract class Proc {

            public static class ExcessivePower {
                public static final int TYPE_CPU = 2;
                public static final int TYPE_WAKE = 1;
                public long overTime;
                public int type;
                public long usedTime;
            }

            public abstract int countExcessivePowers();

            public abstract ExcessivePower getExcessivePower(int i);

            public abstract long getForegroundTime(int i);

            public abstract int getNumAnrs(int i);

            public abstract int getNumCrashes(int i);

            public abstract int getStarts(int i);

            public abstract long getSystemTime(int i);

            public abstract long getUserTime(int i);

            public abstract boolean isActive();
        }

        public static abstract class Sensor {
            public static final int GPS = -10000;

            public abstract int getHandle();

            public abstract Timer getSensorBackgroundTime();

            public abstract Timer getSensorTime();
        }

        public static abstract class Wakelock {
            public abstract Timer getWakeTime(int i);
        }

        public abstract Timer getAggregatedPartialWakelockTimer();

        public abstract Timer getAudioTurnedOnTimer();

        public abstract ControllerActivityCounter getBluetoothControllerActivity();

        public abstract Timer getBluetoothScanBackgroundTimer();

        public abstract Counter getBluetoothScanResultBgCounter();

        public abstract Counter getBluetoothScanResultCounter();

        public abstract Timer getBluetoothScanTimer();

        public abstract Timer getBluetoothUnoptimizedScanBackgroundTimer();

        public abstract Timer getBluetoothUnoptimizedScanTimer();

        public abstract Timer getCameraTurnedOnTimer();

        public abstract long getCpuActiveTime();

        public abstract long[] getCpuClusterTimes();

        public abstract long[] getCpuFreqTimes(int i);

        public abstract long[] getCpuFreqTimes(int i, int i2);

        public abstract void getDeferredJobsCheckinLineLocked(StringBuilder sb, int i);

        public abstract void getDeferredJobsLineLocked(StringBuilder sb, int i);

        public abstract Timer getFlashlightTurnedOnTimer();

        public abstract Timer getForegroundActivityTimer();

        public abstract Timer getForegroundServiceTimer();

        public abstract long getFullWifiLockTime(long j, int i);

        public abstract ArrayMap<String, SparseIntArray> getJobCompletionStats();

        public abstract ArrayMap<String, ? extends Timer> getJobStats();

        public abstract int getMobileRadioActiveCount(int i);

        public abstract long getMobileRadioActiveTime(int i);

        public abstract long getMobileRadioApWakeupCount(int i);

        public abstract ControllerActivityCounter getModemControllerActivity();

        public abstract Timer getMulticastWakelockStats();

        public abstract long getNetworkActivityBytes(int i, int i2);

        public abstract long getNetworkActivityPackets(int i, int i2);

        public abstract ArrayMap<String, ? extends Pkg> getPackageStats();

        public abstract SparseArray<? extends Pid> getPidStats();

        public abstract long getProcessStateTime(int i, long j, int i2);

        public abstract Timer getProcessStateTimer(int i);

        public abstract ArrayMap<String, ? extends Proc> getProcessStats();

        public abstract long[] getScreenOffCpuFreqTimes(int i);

        public abstract long[] getScreenOffCpuFreqTimes(int i, int i2);

        public abstract SparseArray<? extends Sensor> getSensorStats();

        public abstract ArrayMap<String, ? extends Timer> getSyncStats();

        public abstract long getSystemCpuTimeUs(int i);

        public abstract long getTimeAtCpuSpeed(int i, int i2, int i3);

        public abstract int getUid();

        public abstract int getUserActivityCount(int i, int i2);

        public abstract long getUserCpuTimeUs(int i);

        public abstract Timer getVibratorOnTimer();

        public abstract Timer getVideoTurnedOnTimer();

        public abstract ArrayMap<String, ? extends Wakelock> getWakelockStats();

        public abstract int getWifiBatchedScanCount(int i, int i2);

        public abstract long getWifiBatchedScanTime(int i, long j, int i2);

        public abstract ControllerActivityCounter getWifiControllerActivity();

        public abstract long getWifiMulticastTime(long j, int i);

        public abstract long getWifiRadioApWakeupCount(int i);

        public abstract long getWifiRunningTime(long j, int i);

        public abstract long getWifiScanActualTime(long j);

        public abstract int getWifiScanBackgroundCount(int i);

        public abstract long getWifiScanBackgroundTime(long j);

        public abstract Timer getWifiScanBackgroundTimer();

        public abstract int getWifiScanCount(int i);

        public abstract long getWifiScanTime(long j, int i);

        public abstract Timer getWifiScanTimer();

        public abstract boolean hasNetworkActivity();

        public abstract boolean hasUserActivity();

        public abstract void noteActivityPausedLocked(long j);

        public abstract void noteActivityResumedLocked(long j);

        public abstract void noteFullWifiLockAcquiredLocked(long j);

        public abstract void noteFullWifiLockReleasedLocked(long j);

        public abstract void noteUserActivityLocked(int i);

        public abstract void noteWifiBatchedScanStartedLocked(int i, long j);

        public abstract void noteWifiBatchedScanStoppedLocked(long j);

        public abstract void noteWifiMulticastDisabledLocked(long j);

        public abstract void noteWifiMulticastEnabledLocked(long j);

        public abstract void noteWifiRunningLocked(long j);

        public abstract void noteWifiScanStartedLocked(long j);

        public abstract void noteWifiScanStoppedLocked(long j);

        public abstract void noteWifiStoppedLocked(long j);
    }

    private static long computeWakeLock(Timer timer, long j, int i) {
        if (timer != null) {
            return (timer.getTotalTimeLocked(j, i) + 500) / 1000;
        }
        return 0L;
    }

    private static boolean controllerActivityHasData(ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityCounter == null) {
            return false;
        }
        if (controllerActivityCounter.getIdleTimeCounter().getCountLocked(i) != 0 || controllerActivityCounter.getRxTimeCounter().getCountLocked(i) != 0 || controllerActivityCounter.getPowerCounter().getCountLocked(i) != 0) {
            return true;
        }
        for (LongCounter longCounter : controllerActivityCounter.getTxTimeCounters()) {
            if (longCounter.getCountLocked(i) != 0) {
                return true;
            }
        }
        return false;
    }

    private static final void dumpControllerActivityLine(PrintWriter printWriter, int i, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i2) {
        if (controllerActivityHasData(controllerActivityCounter, i2)) {
            dumpLineHeader(printWriter, i, str, str2);
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getIdleTimeCounter().getCountLocked(i2));
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getRxTimeCounter().getCountLocked(i2));
            printWriter.print(",");
            printWriter.print(controllerActivityCounter.getPowerCounter().getCountLocked(i2) / 3600000);
            for (LongCounter longCounter : controllerActivityCounter.getTxTimeCounters()) {
                printWriter.print(",");
                printWriter.print(longCounter.getCountLocked(i2));
            }
            printWriter.println();
        }
    }

    private static void dumpControllerActivityProto(ProtoOutputStream protoOutputStream, long j, ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityHasData(controllerActivityCounter, i)) {
            long jStart = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, controllerActivityCounter.getIdleTimeCounter().getCountLocked(i));
            protoOutputStream.write(1112396529666L, controllerActivityCounter.getRxTimeCounter().getCountLocked(i));
            protoOutputStream.write(1112396529667L, controllerActivityCounter.getPowerCounter().getCountLocked(i) / 3600000);
            LongCounter[] txTimeCounters = controllerActivityCounter.getTxTimeCounters();
            for (int i2 = 0; i2 < txTimeCounters.length; i2++) {
                LongCounter longCounter = txTimeCounters[i2];
                long jStart2 = protoOutputStream.start(2246267895812L);
                protoOutputStream.write(1120986464257L, i2);
                protoOutputStream.write(1112396529666L, longCounter.getCountLocked(i));
                protoOutputStream.end(jStart2);
            }
            protoOutputStream.end(jStart);
        }
    }

    private void dumpDailyLevelStepSummary(PrintWriter printWriter, String str, String str2, LevelStepTracker levelStepTracker, StringBuilder sb, int[] iArr) {
        if (levelStepTracker == null) {
            return;
        }
        long jComputeTimeEstimate = levelStepTracker.computeTimeEstimate(0L, 0L, iArr);
        if (jComputeTimeEstimate >= 0) {
            printWriter.print(str);
            printWriter.print(str2);
            printWriter.print(" total time: ");
            sb.setLength(0);
            formatTimeMs(sb, jComputeTimeEstimate);
            printWriter.print(sb);
            printWriter.print(" (from ");
            printWriter.print(iArr[0]);
            printWriter.println(" steps)");
        }
        for (int i = 0; i < STEP_LEVEL_MODES_OF_INTEREST.length; i++) {
            long jComputeTimeEstimate2 = levelStepTracker.computeTimeEstimate(STEP_LEVEL_MODES_OF_INTEREST[i], STEP_LEVEL_MODE_VALUES[i], iArr);
            if (jComputeTimeEstimate2 > 0) {
                printWriter.print(str);
                printWriter.print(str2);
                printWriter.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                printWriter.print(STEP_LEVEL_MODE_LABELS[i]);
                printWriter.print(" time: ");
                sb.setLength(0);
                formatTimeMs(sb, jComputeTimeEstimate2);
                printWriter.print(sb);
                printWriter.print(" (from ");
                printWriter.print(iArr[0]);
                printWriter.println(" steps)");
            }
        }
    }

    private void dumpDailyPackageChanges(PrintWriter printWriter, String str, ArrayList<PackageChange> arrayList) {
        if (arrayList == null) {
            return;
        }
        printWriter.print(str);
        printWriter.println("Package changes:");
        for (int i = 0; i < arrayList.size(); i++) {
            PackageChange packageChange = arrayList.get(i);
            if (packageChange.mUpdate) {
                printWriter.print(str);
                printWriter.print("  Update ");
                printWriter.print(packageChange.mPackageName);
                printWriter.print(" vers=");
                printWriter.println(packageChange.mVersionCode);
            } else {
                printWriter.print(str);
                printWriter.print("  Uninstall ");
                printWriter.println(packageChange.mPackageName);
            }
        }
    }

    private static void dumpDurationSteps(ProtoOutputStream protoOutputStream, long j, LevelStepTracker levelStepTracker) {
        int i;
        if (levelStepTracker == null) {
            return;
        }
        int i2 = levelStepTracker.mNumStepDurations;
        for (int i3 = 0; i3 < i2; i3++) {
            long jStart = protoOutputStream.start(j);
            protoOutputStream.write(1112396529665L, levelStepTracker.getDurationAt(i3));
            protoOutputStream.write(1120986464258L, levelStepTracker.getLevelAt(i3));
            long initModeAt = levelStepTracker.getInitModeAt(i3);
            long modModeAt = levelStepTracker.getModModeAt(i3);
            int i4 = 3;
            if ((modModeAt & 3) == 0) {
                switch (((int) (3 & initModeAt)) + 1) {
                    case 1:
                        i = 2;
                        break;
                    case 2:
                        i = 1;
                        break;
                    case 3:
                        i = 3;
                        break;
                    case 4:
                        i = 4;
                        break;
                    default:
                        i = 5;
                        break;
                }
            } else {
                i = 0;
            }
            protoOutputStream.write(1159641169923L, i);
            protoOutputStream.write(1159641169924L, (modModeAt & 4) == 0 ? (4 & initModeAt) == 0 ? 2 : 1 : 0);
            if ((modModeAt & 8) != 0) {
                i4 = 0;
            } else if ((initModeAt & 8) != 0) {
                i4 = 2;
            }
            protoOutputStream.write(1159641169925L, i4);
            protoOutputStream.end(jStart);
        }
    }

    private static boolean dumpDurationSteps(PrintWriter printWriter, String str, String str2, LevelStepTracker levelStepTracker, boolean z) {
        int i;
        int i2;
        char c;
        char c2 = 0;
        if (levelStepTracker == null || (i = levelStepTracker.mNumStepDurations) <= 0) {
            return false;
        }
        if (!z) {
            printWriter.println(str2);
        }
        String[] strArr = new String[5];
        int i3 = 0;
        for (i = levelStepTracker.mNumStepDurations; i3 < i; i = i2) {
            long durationAt = levelStepTracker.getDurationAt(i3);
            int levelAt = levelStepTracker.getLevelAt(i3);
            long initModeAt = levelStepTracker.getInitModeAt(i3);
            long modModeAt = levelStepTracker.getModModeAt(i3);
            if (z) {
                strArr[c2] = Long.toString(durationAt);
                strArr[1] = Integer.toString(levelAt);
                if ((modModeAt & 3) == 0) {
                    i2 = i;
                    switch (((int) (initModeAt & 3)) + 1) {
                        case 1:
                            strArr[2] = "s-";
                            break;
                        case 2:
                            strArr[2] = "s+";
                            break;
                        case 3:
                            strArr[2] = "sd";
                            break;
                        case 4:
                            strArr[2] = "sds";
                            break;
                        default:
                            strArr[2] = "?";
                            break;
                    }
                } else {
                    i2 = i;
                    strArr[2] = "";
                }
                if ((modModeAt & 4) == 0) {
                    strArr[3] = (initModeAt & 4) != 0 ? "p+" : "p-";
                } else {
                    strArr[3] = "";
                }
                if ((modModeAt & 8) == 0) {
                    strArr[4] = (initModeAt & 8) != 0 ? "i+" : "i-";
                } else {
                    strArr[4] = "";
                }
                dumpLine(printWriter, 0, "i", str2, strArr);
                c2 = 0;
            } else {
                i2 = i;
                printWriter.print(str);
                printWriter.print("#");
                printWriter.print(i3);
                printWriter.print(": ");
                TimeUtils.formatDuration(durationAt, printWriter);
                printWriter.print(" to ");
                printWriter.print(levelAt);
                if ((modModeAt & 3) == 0) {
                    printWriter.print(" (");
                    switch (((int) (initModeAt & 3)) + 1) {
                        case 1:
                            printWriter.print("screen-off");
                            break;
                        case 2:
                            printWriter.print("screen-on");
                            break;
                        case 3:
                            printWriter.print("screen-doze");
                            break;
                        case 4:
                            printWriter.print("screen-doze-suspend");
                            break;
                        default:
                            printWriter.print("screen-?");
                            break;
                    }
                    c = 1;
                } else {
                    c = c2;
                }
                if ((modModeAt & 4) == 0) {
                    printWriter.print(c != 0 ? ", " : " (");
                    printWriter.print((initModeAt & 4) != 0 ? "power-save-on" : "power-save-off");
                    c = 1;
                }
                if ((modModeAt & 8) == 0) {
                    printWriter.print(c != 0 ? ", " : " (");
                    printWriter.print((initModeAt & 8) != 0 ? "device-idle-on" : "device-idle-off");
                    c = 1;
                }
                if (c != 0) {
                    printWriter.print(")");
                }
                printWriter.println();
            }
            i3++;
        }
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:36:0x0083  */
    private void dumpHistoryLocked(PrintWriter printWriter, int i, long j, boolean z) {
        HistoryPrinter historyPrinter = new HistoryPrinter();
        HistoryItem historyItem = new HistoryItem();
        long j2 = -1;
        long j3 = -1;
        boolean z2 = false;
        while (getNextHistoryLocked(historyItem)) {
            long j4 = historyItem.time;
            long j5 = j2 < 0 ? j4 : j2;
            if (historyItem.time >= j) {
                if (j < 0 || z2) {
                    j4 = j4;
                } else {
                    if (historyItem.cmd == 5 || historyItem.cmd == 7 || historyItem.cmd == 4 || historyItem.cmd == 8) {
                        historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                        historyItem.cmd = (byte) 0;
                    } else if (historyItem.currentTime != 0) {
                        byte b = historyItem.cmd;
                        historyItem.cmd = (byte) 5;
                        historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                        historyItem.cmd = b;
                    } else {
                        j4 = j4;
                    }
                    z2 = true;
                }
                boolean z3 = z2;
                historyPrinter.printNextItem(printWriter, historyItem, j5, z, (i & 32) != 0);
                z2 = z3;
            } else {
                j4 = j4;
            }
            j2 = j5;
            j3 = j4;
        }
        if (j >= 0) {
            commitCurrentHistoryBatchLocked();
            printWriter.print(z ? "NEXT: " : "  NEXT: ");
            printWriter.println(j3 + 1);
        }
    }

    private static final void dumpLine(PrintWriter printWriter, int i, String str, String str2, Object... objArr) {
        dumpLineHeader(printWriter, i, str, str2);
        for (Object obj : objArr) {
            printWriter.print(',');
            printWriter.print(obj);
        }
        printWriter.println();
    }

    private static final void dumpLineHeader(PrintWriter printWriter, int i, String str, String str2) {
        printWriter.print(9);
        printWriter.print(',');
        printWriter.print(i);
        printWriter.print(',');
        printWriter.print(str);
        printWriter.print(',');
        printWriter.print(str2);
    }

    private void dumpProtoAppsLocked(ProtoOutputStream protoOutputStream, BatteryStatsHelper batteryStatsHelper, List<ApplicationInfo> list) {
        ArrayList<String> arrayList;
        long j;
        Uid uid;
        SparseArray sparseArray;
        long[] cpuFreqTimes;
        ArrayList arrayList2;
        ArrayMap<String, ? extends Uid.Pkg> arrayMap;
        int i;
        long j2;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j3 = jElapsedRealtime * 1000;
        long batteryUptime = getBatteryUptime(jUptimeMillis);
        SparseArray sparseArray2 = new SparseArray();
        if (list != null) {
            for (int i2 = 0; i2 < list.size(); i2++) {
                ApplicationInfo applicationInfo = list.get(i2);
                int appId = UserHandle.getAppId(applicationInfo.uid);
                ArrayList arrayList3 = (ArrayList) sparseArray2.get(appId);
                if (arrayList3 == null) {
                    arrayList3 = new ArrayList();
                    sparseArray2.put(appId, arrayList3);
                }
                arrayList3.add(applicationInfo.packageName);
            }
        }
        SparseArray sparseArray3 = new SparseArray();
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null) {
            for (int i3 = 0; i3 < usageList.size(); i3++) {
                BatterySipper batterySipper = usageList.get(i3);
                if (batterySipper.drainType == BatterySipper.DrainType.APP) {
                    sparseArray3.put(batterySipper.uidObj.getUid(), batterySipper);
                }
            }
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size = uidStats.size();
        int i4 = 0;
        while (i4 < size) {
            int i5 = size;
            long jStart = protoOutputStream.start(2246267895813L);
            Uid uidValueAt = uidStats.valueAt(i4);
            int iKeyAt = uidStats.keyAt(i4);
            SparseArray sparseArray4 = sparseArray3;
            SparseArray<? extends Uid> sparseArray5 = uidStats;
            protoOutputStream.write(1120986464257L, iKeyAt);
            ArrayList arrayList4 = (ArrayList) sparseArray2.get(UserHandle.getAppId(iKeyAt));
            if (arrayList4 == null) {
                arrayList4 = new ArrayList();
            }
            int i6 = i4;
            ArrayMap<String, ? extends Uid.Pkg> packageStats = uidValueAt.getPackageStats();
            int size2 = packageStats.size() - 1;
            while (true) {
                arrayList = arrayList4;
                if (size2 < 0) {
                    break;
                }
                String strKeyAt = packageStats.keyAt(size2);
                ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats = packageStats.valueAt(size2).getServiceStats();
                if (serviceStats.size() == 0) {
                    arrayMap = packageStats;
                    i = size2;
                    j2 = batteryUptime;
                    arrayList2 = arrayList;
                } else {
                    long jStart2 = protoOutputStream.start(2246267895810L);
                    protoOutputStream.write(1138166333441L, strKeyAt);
                    arrayList2 = arrayList;
                    arrayList2.remove(strKeyAt);
                    int size3 = serviceStats.size() - 1;
                    while (size3 >= 0) {
                        Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size3);
                        ArrayMap<String, ? extends Uid.Pkg> arrayMap2 = packageStats;
                        long jRoundUsToMs = roundUsToMs(servValueAt.getStartTime(batteryUptime, 0));
                        int starts = servValueAt.getStarts(0);
                        int launches = servValueAt.getLaunches(0);
                        if (jRoundUsToMs != 0 || starts != 0 || launches != 0) {
                            long jStart3 = protoOutputStream.start(2246267895810L);
                            protoOutputStream.write(1138166333441L, serviceStats.keyAt(size3));
                            protoOutputStream.write(1112396529666L, jRoundUsToMs);
                            protoOutputStream.write(1120986464259L, starts);
                            protoOutputStream.write(1120986464260L, launches);
                            protoOutputStream.end(jStart3);
                        }
                        size3--;
                        packageStats = arrayMap2;
                        batteryUptime = batteryUptime;
                        size2 = size2;
                    }
                    arrayMap = packageStats;
                    i = size2;
                    j2 = batteryUptime;
                    protoOutputStream.end(jStart2);
                }
                size2 = i - 1;
                arrayList4 = arrayList2;
                j3 = j3;
                jElapsedRealtime = jElapsedRealtime;
                packageStats = arrayMap;
                batteryUptime = j2;
            }
            ArrayMap<String, ? extends Uid.Pkg> arrayMap3 = packageStats;
            long j4 = jElapsedRealtime;
            long j5 = j3;
            long j6 = batteryUptime;
            for (String str : arrayList) {
                long jStart4 = protoOutputStream.start(2246267895810L);
                protoOutputStream.write(1138166333441L, str);
                protoOutputStream.end(jStart4);
            }
            if (uidValueAt.getAggregatedPartialWakelockTimer() != null) {
                Timer aggregatedPartialWakelockTimer = uidValueAt.getAggregatedPartialWakelockTimer();
                j = j4;
                long totalDurationMsLocked = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j);
                Timer subTimer = aggregatedPartialWakelockTimer.getSubTimer();
                long totalDurationMsLocked2 = subTimer != null ? subTimer.getTotalDurationMsLocked(j) : 0L;
                long jStart5 = protoOutputStream.start(1146756268056L);
                protoOutputStream.write(1112396529665L, totalDurationMsLocked);
                protoOutputStream.write(1112396529666L, totalDurationMsLocked2);
                protoOutputStream.end(jStart5);
            } else {
                j = j4;
            }
            int i7 = i6;
            int i8 = iKeyAt;
            ArrayMap<String, ? extends Uid.Pkg> arrayMap4 = arrayMap3;
            dumpTimer(protoOutputStream, 1146756268040L, uidValueAt.getAudioTurnedOnTimer(), j5, 0);
            dumpControllerActivityProto(protoOutputStream, 1146756268035L, uidValueAt.getBluetoothControllerActivity(), 0);
            Timer bluetoothScanTimer = uidValueAt.getBluetoothScanTimer();
            if (bluetoothScanTimer != null) {
                long jStart6 = protoOutputStream.start(1146756268038L);
                dumpTimer(protoOutputStream, 1146756268033L, bluetoothScanTimer, j5, 0);
                dumpTimer(protoOutputStream, 1146756268034L, uidValueAt.getBluetoothScanBackgroundTimer(), j5, 0);
                dumpTimer(protoOutputStream, 1146756268035L, uidValueAt.getBluetoothUnoptimizedScanTimer(), j5, 0);
                dumpTimer(protoOutputStream, 1146756268036L, uidValueAt.getBluetoothUnoptimizedScanBackgroundTimer(), j5, 0);
                protoOutputStream.write(1120986464261L, uidValueAt.getBluetoothScanResultCounter() != null ? uidValueAt.getBluetoothScanResultCounter().getCountLocked(0) : 0);
                protoOutputStream.write(1120986464262L, uidValueAt.getBluetoothScanResultBgCounter() != null ? uidValueAt.getBluetoothScanResultBgCounter().getCountLocked(0) : 0);
                protoOutputStream.end(jStart6);
            }
            dumpTimer(protoOutputStream, 1146756268041L, uidValueAt.getCameraTurnedOnTimer(), j5, 0);
            long jStart7 = protoOutputStream.start(1146756268039L);
            Uid uid2 = uidValueAt;
            protoOutputStream.write(1112396529665L, roundUsToMs(uid2.getUserCpuTimeUs(0)));
            protoOutputStream.write(1112396529666L, roundUsToMs(uid2.getSystemCpuTimeUs(0)));
            long[] cpuFreqs = getCpuFreqs();
            if (cpuFreqs != null && (cpuFreqTimes = uid2.getCpuFreqTimes(0)) != null && cpuFreqTimes.length == cpuFreqs.length) {
                long[] screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(0);
                if (screenOffCpuFreqTimes == null) {
                    screenOffCpuFreqTimes = new long[cpuFreqTimes.length];
                }
                int i9 = 0;
                while (i9 < cpuFreqTimes.length) {
                    long jStart8 = protoOutputStream.start(2246267895811L);
                    int i10 = i9 + 1;
                    protoOutputStream.write(1120986464257L, i10);
                    protoOutputStream.write(1112396529666L, cpuFreqTimes[i9]);
                    protoOutputStream.write(1112396529667L, screenOffCpuFreqTimes[i9]);
                    protoOutputStream.end(jStart8);
                    i9 = i10;
                    j = j;
                    i7 = i7;
                    i8 = i8;
                }
            }
            int i11 = i7;
            int i12 = i8;
            long j7 = j;
            int i13 = 0;
            while (i13 < 7) {
                long[] cpuFreqTimes2 = uid2.getCpuFreqTimes(0, i13);
                if (cpuFreqTimes2 == null || cpuFreqTimes2.length != cpuFreqs.length) {
                    uid = uid2;
                    sparseArray = sparseArray2;
                } else {
                    long[] screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(0, i13);
                    if (screenOffCpuFreqTimes2 == null) {
                        screenOffCpuFreqTimes2 = new long[cpuFreqTimes2.length];
                    }
                    long jStart9 = protoOutputStream.start(2246267895812L);
                    protoOutputStream.write(1159641169921L, i13);
                    int i14 = 0;
                    while (i14 < cpuFreqTimes2.length) {
                        long jStart10 = protoOutputStream.start(2246267895810L);
                        int i15 = i14 + 1;
                        protoOutputStream.write(1120986464257L, i15);
                        protoOutputStream.write(1112396529666L, cpuFreqTimes2[i14]);
                        protoOutputStream.write(1112396529667L, screenOffCpuFreqTimes2[i14]);
                        protoOutputStream.end(jStart10);
                        sparseArray2 = sparseArray2;
                        uid2 = uid2;
                        i14 = i15;
                    }
                    uid = uid2;
                    sparseArray = sparseArray2;
                    protoOutputStream.end(jStart9);
                }
                i13++;
                sparseArray2 = sparseArray;
                uid2 = uid;
            }
            Uid uid3 = uid2;
            SparseArray sparseArray6 = sparseArray2;
            long j8 = 2246267895810L;
            protoOutputStream.end(jStart7);
            dumpTimer(protoOutputStream, 1146756268042L, uid3.getFlashlightTurnedOnTimer(), j5, 0);
            dumpTimer(protoOutputStream, 1146756268043L, uid3.getForegroundActivityTimer(), j5, 0);
            dumpTimer(protoOutputStream, 1146756268044L, uid3.getForegroundServiceTimer(), j5, 0);
            ArrayMap<String, SparseIntArray> jobCompletionStats = uid3.getJobCompletionStats();
            int[] iArr = {0, 1, 2, 3, 4};
            int i16 = 0;
            while (i16 < jobCompletionStats.size()) {
                SparseIntArray sparseIntArrayValueAt = jobCompletionStats.valueAt(i16);
                if (sparseIntArrayValueAt != null) {
                    long jStart11 = protoOutputStream.start(2246267895824L);
                    protoOutputStream.write(1138166333441L, jobCompletionStats.keyAt(i16));
                    int length = iArr.length;
                    int i17 = 0;
                    while (i17 < length) {
                        int i18 = iArr[i17];
                        long jStart12 = protoOutputStream.start(j8);
                        protoOutputStream.write(1159641169921L, i18);
                        protoOutputStream.write(1120986464258L, sparseIntArrayValueAt.get(i18, 0));
                        protoOutputStream.end(jStart12);
                        i17++;
                        j8 = 2246267895810L;
                    }
                    protoOutputStream.end(jStart11);
                }
                i16++;
                j8 = 2246267895810L;
            }
            ArrayMap<String, ? extends Timer> jobStats = uid3.getJobStats();
            for (int size4 = jobStats.size() - 1; size4 >= 0; size4--) {
                Timer timerValueAt = jobStats.valueAt(size4);
                Timer subTimer2 = timerValueAt.getSubTimer();
                long jStart13 = protoOutputStream.start(2246267895823L);
                protoOutputStream.write(1138166333441L, jobStats.keyAt(size4));
                dumpTimer(protoOutputStream, 1146756268034L, timerValueAt, j5, 0);
                dumpTimer(protoOutputStream, 1146756268035L, subTimer2, j5, 0);
                protoOutputStream.end(jStart13);
            }
            dumpControllerActivityProto(protoOutputStream, 1146756268036L, uid3.getModemControllerActivity(), 0);
            long jStart14 = protoOutputStream.start(1146756268049L);
            protoOutputStream.write(1112396529665L, uid3.getNetworkActivityBytes(0, 0));
            protoOutputStream.write(1112396529666L, uid3.getNetworkActivityBytes(1, 0));
            protoOutputStream.write(1112396529667L, uid3.getNetworkActivityBytes(2, 0));
            protoOutputStream.write(1112396529668L, uid3.getNetworkActivityBytes(3, 0));
            protoOutputStream.write(1112396529669L, uid3.getNetworkActivityBytes(4, 0));
            protoOutputStream.write(1112396529670L, uid3.getNetworkActivityBytes(5, 0));
            protoOutputStream.write(1112396529671L, uid3.getNetworkActivityPackets(0, 0));
            protoOutputStream.write(1112396529672L, uid3.getNetworkActivityPackets(1, 0));
            protoOutputStream.write(1112396529673L, uid3.getNetworkActivityPackets(2, 0));
            protoOutputStream.write(1112396529674L, uid3.getNetworkActivityPackets(3, 0));
            protoOutputStream.write(1112396529675L, roundUsToMs(uid3.getMobileRadioActiveTime(0)));
            protoOutputStream.write(1120986464268L, uid3.getMobileRadioActiveCount(0));
            protoOutputStream.write(1120986464269L, uid3.getMobileRadioApWakeupCount(0));
            protoOutputStream.write(1120986464270L, uid3.getWifiRadioApWakeupCount(0));
            protoOutputStream.write(1112396529679L, uid3.getNetworkActivityBytes(6, 0));
            protoOutputStream.write(1112396529680L, uid3.getNetworkActivityBytes(7, 0));
            protoOutputStream.write(1112396529681L, uid3.getNetworkActivityBytes(8, 0));
            protoOutputStream.write(1112396529682L, uid3.getNetworkActivityBytes(9, 0));
            protoOutputStream.write(1112396529683L, uid3.getNetworkActivityPackets(6, 0));
            protoOutputStream.write(1112396529684L, uid3.getNetworkActivityPackets(7, 0));
            protoOutputStream.write(1112396529685L, uid3.getNetworkActivityPackets(8, 0));
            protoOutputStream.write(1112396529686L, uid3.getNetworkActivityPackets(9, 0));
            protoOutputStream.end(jStart14);
            SparseArray sparseArray7 = sparseArray4;
            BatterySipper batterySipper2 = (BatterySipper) sparseArray7.get(i12);
            if (batterySipper2 != null) {
                long jStart15 = protoOutputStream.start(1146756268050L);
                protoOutputStream.write(1103806595073L, batterySipper2.totalPowerMah);
                protoOutputStream.write(1133871366146L, batterySipper2.shouldHide);
                protoOutputStream.write(1103806595075L, batterySipper2.screenPowerMah);
                protoOutputStream.write(1103806595076L, batterySipper2.proportionalSmearMah);
                protoOutputStream.end(jStart15);
            }
            ArrayMap<String, ? extends Uid.Proc> processStats = uid3.getProcessStats();
            int size5 = processStats.size() - 1;
            while (size5 >= 0) {
                Uid.Proc procValueAt = processStats.valueAt(size5);
                long jStart16 = protoOutputStream.start(2246267895827L);
                protoOutputStream.write(1138166333441L, processStats.keyAt(size5));
                protoOutputStream.write(1112396529666L, procValueAt.getUserTime(0));
                protoOutputStream.write(1112396529667L, procValueAt.getSystemTime(0));
                protoOutputStream.write(1112396529668L, procValueAt.getForegroundTime(0));
                protoOutputStream.write(1120986464261L, procValueAt.getStarts(0));
                protoOutputStream.write(1120986464262L, procValueAt.getNumAnrs(0));
                protoOutputStream.write(1120986464263L, procValueAt.getNumCrashes(0));
                protoOutputStream.end(jStart16);
                size5--;
                sparseArray7 = sparseArray7;
            }
            SparseArray sparseArray8 = sparseArray7;
            int i19 = 0;
            for (SparseArray<? extends Uid.Sensor> sensorStats = uid3.getSensorStats(); i19 < sensorStats.size(); sensorStats = sensorStats) {
                Uid.Sensor sensorValueAt = sensorStats.valueAt(i19);
                Timer sensorTime = sensorValueAt.getSensorTime();
                if (sensorTime != null) {
                    Timer sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                    int iKeyAt2 = sensorStats.keyAt(i19);
                    long jStart17 = protoOutputStream.start(UidProto.SENSORS);
                    protoOutputStream.write(1120986464257L, iKeyAt2);
                    dumpTimer(protoOutputStream, 1146756268034L, sensorTime, j5, 0);
                    dumpTimer(protoOutputStream, 1146756268035L, sensorBackgroundTime, j5, 0);
                    protoOutputStream.end(jStart17);
                }
                i19++;
            }
            int i20 = 0;
            while (i20 < 7) {
                long j9 = j5;
                long jRoundUsToMs2 = roundUsToMs(uid3.getProcessStateTime(i20, j9, 0));
                if (jRoundUsToMs2 != 0) {
                    long jStart18 = protoOutputStream.start(2246267895828L);
                    protoOutputStream.write(1159641169921L, i20);
                    protoOutputStream.write(1112396529666L, jRoundUsToMs2);
                    protoOutputStream.end(jStart18);
                }
                i20++;
                j5 = j9;
            }
            long j10 = j5;
            ArrayMap<String, ? extends Timer> syncStats = uid3.getSyncStats();
            for (int size6 = syncStats.size() - 1; size6 >= 0; size6--) {
                Timer timerValueAt2 = syncStats.valueAt(size6);
                Timer subTimer3 = timerValueAt2.getSubTimer();
                long jStart19 = protoOutputStream.start(2246267895830L);
                protoOutputStream.write(1138166333441L, syncStats.keyAt(size6));
                dumpTimer(protoOutputStream, 1146756268034L, timerValueAt2, j10, 0);
                dumpTimer(protoOutputStream, 1146756268035L, subTimer3, j10, 0);
                protoOutputStream.end(jStart19);
            }
            if (uid3.hasUserActivity()) {
                for (int i21 = 0; i21 < 4; i21++) {
                    int userActivityCount = uid3.getUserActivityCount(i21, 0);
                    if (userActivityCount != 0) {
                        long jStart20 = protoOutputStream.start(2246267895831L);
                        protoOutputStream.write(1159641169921L, i21);
                        protoOutputStream.write(1120986464258L, userActivityCount);
                        protoOutputStream.end(jStart20);
                    }
                }
            }
            dumpTimer(protoOutputStream, 1146756268045L, uid3.getVibratorOnTimer(), j10, 0);
            dumpTimer(protoOutputStream, 1146756268046L, uid3.getVideoTurnedOnTimer(), j10, 0);
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uid3.getWakelockStats();
            int i22 = 1;
            int size7 = wakelockStats.size() - 1;
            while (size7 >= 0) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size7);
                long jStart21 = protoOutputStream.start(2246267895833L);
                protoOutputStream.write(1138166333441L, wakelockStats.keyAt(size7));
                dumpTimer(protoOutputStream, 1146756268034L, wakelockValueAt.getWakeTime(i22), j10, 0);
                Timer wakeTime = wakelockValueAt.getWakeTime(0);
                if (wakeTime != null) {
                    dumpTimer(protoOutputStream, 1146756268035L, wakeTime, j10, 0);
                    dumpTimer(protoOutputStream, 1146756268036L, wakeTime.getSubTimer(), j10, 0);
                }
                dumpTimer(protoOutputStream, 1146756268037L, wakelockValueAt.getWakeTime(2), j10, 0);
                protoOutputStream.end(jStart21);
                size7--;
                i22 = 1;
            }
            dumpTimer(protoOutputStream, 1146756268060L, uid3.getMulticastWakelockStats(), j10, 0);
            int i23 = 1;
            int size8 = arrayMap4.size() - 1;
            while (size8 >= 0) {
                ArrayMap<String, ? extends Uid.Pkg> arrayMap5 = arrayMap4;
                ArrayMap<String, ? extends Counter> wakeupAlarmStats = arrayMap5.valueAt(size8).getWakeupAlarmStats();
                int size9 = wakeupAlarmStats.size() - i23;
                while (size9 >= 0) {
                    long jStart22 = protoOutputStream.start(2246267895834L);
                    protoOutputStream.write(1138166333441L, wakeupAlarmStats.keyAt(size9));
                    protoOutputStream.write(1120986464258L, wakeupAlarmStats.valueAt(size9).getCountLocked(0));
                    protoOutputStream.end(jStart22);
                    size9--;
                    arrayMap5 = arrayMap5;
                }
                size8--;
                arrayMap4 = arrayMap5;
                i23 = 1;
            }
            dumpControllerActivityProto(protoOutputStream, 1146756268037L, uid3.getWifiControllerActivity(), 0);
            long jStart23 = protoOutputStream.start(1146756268059L);
            protoOutputStream.write(1112396529665L, roundUsToMs(uid3.getFullWifiLockTime(j10, 0)));
            dumpTimer(protoOutputStream, 1146756268035L, uid3.getWifiScanTimer(), j10, 0);
            protoOutputStream.write(1112396529666L, roundUsToMs(uid3.getWifiRunningTime(j10, 0)));
            dumpTimer(protoOutputStream, 1146756268036L, uid3.getWifiScanBackgroundTimer(), j10, 0);
            protoOutputStream.end(jStart23);
            protoOutputStream.end(jStart);
            i4 = i11 + 1;
            j3 = j10;
            size = i5;
            uidStats = sparseArray5;
            batteryUptime = j6;
            jElapsedRealtime = j7;
            sparseArray2 = sparseArray6;
            sparseArray3 = sparseArray8;
        }
    }

    /* JADX WARN: Code duplicated, block: B:47:0x00f0  */
    /* JADX WARN: Code duplicated, block: B:48:0x00f3  */
    private void dumpProtoHistoryLocked(ProtoOutputStream protoOutputStream, int i, long j) {
        HistoryPrinter historyPrinter;
        boolean z;
        boolean z2;
        if (startIteratingHistoryLocked()) {
            protoOutputStream.write(1120986464257L, 32);
            protoOutputStream.write(1112396529666L, getParcelVersion());
            protoOutputStream.write(1138166333443L, getStartPlatformVersion());
            protoOutputStream.write(1138166333444L, getEndPlatformVersion());
            for (int i2 = 0; i2 < getHistoryStringPoolSize(); i2++) {
                try {
                    long jStart = protoOutputStream.start(2246267895813L);
                    protoOutputStream.write(1120986464257L, i2);
                    protoOutputStream.write(1120986464258L, getHistoryTagPoolUid(i2));
                    protoOutputStream.write(1138166333443L, getHistoryTagPoolString(i2));
                    protoOutputStream.end(jStart);
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            HistoryPrinter historyPrinter2 = new HistoryPrinter();
            HistoryItem historyItem = new HistoryItem();
            long j2 = -1;
            long j3 = -1;
            boolean z3 = false;
            while (getNextHistoryLocked(historyItem)) {
                j3 = historyItem.time;
                j2 = j2 < 0 ? j3 : j2;
                if (historyItem.time >= j) {
                    if (j < 0 || z3) {
                        historyPrinter = historyPrinter2;
                    } else {
                        if (historyItem.cmd == 5 || historyItem.cmd == 7 || historyItem.cmd == 4 || historyItem.cmd == 8) {
                            j3 = j3;
                            historyPrinter = historyPrinter2;
                            historyPrinter.printNextItem(protoOutputStream, historyItem, j2, (i & 32) != 0);
                            z = false;
                            historyItem.cmd = (byte) 0;
                            z3 = true;
                        } else {
                            historyPrinter = historyPrinter2;
                            if (historyItem.currentTime != 0) {
                                byte b = historyItem.cmd;
                                historyItem.cmd = (byte) 5;
                                historyPrinter.printNextItem(protoOutputStream, historyItem, j2, (i & 32) != 0);
                                historyItem.cmd = b;
                                z3 = true;
                            }
                        }
                        boolean z4 = z3;
                        if ((i & 32) != 0) {
                            z2 = true;
                        } else {
                            z2 = z;
                        }
                        historyPrinter.printNextItem(protoOutputStream, historyItem, j2, z2);
                        z3 = z4;
                        j3 = j3;
                        historyPrinter2 = historyPrinter;
                    }
                    z = false;
                    boolean z5 = z3;
                    if ((i & 32) != 0) {
                        z2 = true;
                    } else {
                        z2 = z;
                    }
                    historyPrinter.printNextItem(protoOutputStream, historyItem, j2, z2);
                    z3 = z5;
                    j3 = j3;
                    historyPrinter2 = historyPrinter;
                }
            }
            if (j >= 0) {
                commitCurrentHistoryBatchLocked();
                protoOutputStream.write(2237677961222L, "NEXT: " + (j3 + 1));
            }
            finishIteratingHistoryLocked();
        }
    }

    private void dumpProtoSystemLocked(ProtoOutputStream protoOutputStream, BatteryStatsHelper batteryStatsHelper) {
        int i;
        int i2;
        int uid;
        int i3;
        long jStart = protoOutputStream.start(1146756268038L);
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long jStart2 = protoOutputStream.start(1146756268033L);
        protoOutputStream.write(1112396529665L, getStartClockTime());
        protoOutputStream.write(1112396529666L, getStartCount());
        int i4 = 0;
        protoOutputStream.write(1112396529667L, computeRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529668L, computeUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529669L, computeBatteryRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529670L, computeBatteryUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529671L, computeBatteryScreenOffRealtime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529672L, computeBatteryScreenOffUptime(jUptimeMillis, 0) / 1000);
        protoOutputStream.write(1112396529673L, getScreenDozeTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529674L, getEstimatedBatteryCapacity());
        protoOutputStream.write(1112396529675L, getMinLearnedBatteryCapacity());
        protoOutputStream.write(1112396529676L, getMaxLearnedBatteryCapacity());
        protoOutputStream.end(jStart2);
        long jStart3 = protoOutputStream.start(1146756268034L);
        protoOutputStream.write(1120986464257L, getLowDischargeAmountSinceCharge());
        protoOutputStream.write(1120986464258L, getHighDischargeAmountSinceCharge());
        protoOutputStream.write(1120986464259L, getDischargeAmountScreenOnSinceCharge());
        protoOutputStream.write(1120986464260L, getDischargeAmountScreenOffSinceCharge());
        protoOutputStream.write(1120986464261L, getDischargeAmountScreenDozeSinceCharge());
        protoOutputStream.write(1112396529670L, getUahDischarge(0) / 1000);
        protoOutputStream.write(1112396529671L, getUahDischargeScreenOff(0) / 1000);
        protoOutputStream.write(1112396529672L, getUahDischargeScreenDoze(0) / 1000);
        protoOutputStream.write(1112396529673L, getUahDischargeLightDoze(0) / 1000);
        protoOutputStream.write(1112396529674L, getUahDischargeDeepDoze(0) / 1000);
        protoOutputStream.end(jStart3);
        long jComputeChargeTimeRemaining = computeChargeTimeRemaining(jElapsedRealtime);
        long totalTimeLocked = 0;
        if (jComputeChargeTimeRemaining >= 0) {
            protoOutputStream.write(1112396529667L, jComputeChargeTimeRemaining / 1000);
        } else {
            long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(jElapsedRealtime);
            if (jComputeBatteryTimeRemaining >= 0) {
                protoOutputStream.write(1112396529668L, jComputeBatteryTimeRemaining / 1000);
            } else {
                protoOutputStream.write(1112396529668L, -1);
            }
        }
        dumpDurationSteps(protoOutputStream, 2246267895813L, getChargeLevelStepTracker());
        int i5 = 0;
        while (true) {
            if (i5 >= 21) {
                break;
            }
            boolean z = i5 != 0 ? i4 : true;
            int i6 = i5 == 20 ? i4 : i5;
            long jStart4 = protoOutputStream.start(2246267895816L);
            if (z) {
                protoOutputStream.write(1133871366146L, z);
            } else {
                protoOutputStream.write(1159641169921L, i6);
            }
            dumpTimer(protoOutputStream, 1146756268035L, getPhoneDataConnectionTimer(i5), jElapsedRealtime, 0);
            protoOutputStream.end(jStart4);
            i5++;
            i4 = i4;
        }
        int i7 = i4;
        dumpDurationSteps(protoOutputStream, 2246267895814L, getDischargeLevelStepTracker());
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            int length = cpuFreqs.length;
            for (int i8 = i7; i8 < length; i8++) {
                protoOutputStream.write(SystemProto.CPU_FREQUENCY, cpuFreqs[i8]);
            }
        }
        dumpControllerActivityProto(protoOutputStream, 1146756268041L, getBluetoothControllerActivity(), i7);
        dumpControllerActivityProto(protoOutputStream, 1146756268042L, getModemControllerActivity(), i7);
        long jStart5 = protoOutputStream.start(1146756268044L);
        protoOutputStream.write(1112396529665L, getNetworkActivityBytes(i7, i7));
        protoOutputStream.write(1112396529666L, getNetworkActivityBytes(1, i7));
        protoOutputStream.write(1112396529669L, getNetworkActivityPackets(i7, i7));
        protoOutputStream.write(1112396529670L, getNetworkActivityPackets(1, i7));
        protoOutputStream.write(1112396529667L, getNetworkActivityBytes(2, i7));
        protoOutputStream.write(1112396529668L, getNetworkActivityBytes(3, i7));
        protoOutputStream.write(1112396529671L, getNetworkActivityPackets(2, i7));
        protoOutputStream.write(1112396529672L, getNetworkActivityPackets(3, i7));
        protoOutputStream.write(1112396529673L, getNetworkActivityBytes(4, i7));
        protoOutputStream.write(1112396529674L, getNetworkActivityBytes(5, i7));
        protoOutputStream.end(jStart5);
        dumpControllerActivityProto(protoOutputStream, 1146756268043L, getWifiControllerActivity(), i7);
        long jStart6 = protoOutputStream.start(1146756268045L);
        protoOutputStream.write(1112396529665L, getWifiOnTime(jElapsedRealtime, i7) / 1000);
        protoOutputStream.write(1112396529666L, getGlobalWifiRunningTime(jElapsedRealtime, i7) / 1000);
        protoOutputStream.end(jStart6);
        for (Map.Entry<String, ? extends Timer> entry : getKernelWakelockStats().entrySet()) {
            long jStart7 = protoOutputStream.start(2246267895822L);
            protoOutputStream.write(1138166333441L, entry.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, entry.getValue(), jElapsedRealtime, 0);
            protoOutputStream.end(jStart7);
        }
        SparseArray<? extends Uid> uidStats = getUidStats();
        long totalTimeLocked2 = 0;
        for (int i9 = 0; i9 < uidStats.size(); i9++) {
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uidStats.valueAt(i9).getWakelockStats();
            for (int size = wakelockStats.size() - 1; size >= 0; size--) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size);
                Timer wakeTime = wakelockValueAt.getWakeTime(1);
                if (wakeTime != null) {
                    i3 = 0;
                    totalTimeLocked += wakeTime.getTotalTimeLocked(jElapsedRealtime, 0);
                } else {
                    i3 = 0;
                }
                Timer wakeTime2 = wakelockValueAt.getWakeTime(i3);
                if (wakeTime2 != null) {
                    totalTimeLocked2 += wakeTime2.getTotalTimeLocked(jElapsedRealtime, i3);
                }
            }
        }
        long jStart8 = protoOutputStream.start(1146756268047L);
        protoOutputStream.write(1112396529665L, getScreenOnTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529666L, getPhoneOnTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529667L, totalTimeLocked / 1000);
        protoOutputStream.write(1112396529668L, totalTimeLocked2 / 1000);
        protoOutputStream.write(1112396529669L, getMobileRadioActiveTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529670L, getMobileRadioActiveAdjustedTime(0) / 1000);
        protoOutputStream.write(1120986464263L, getMobileRadioActiveCount(0));
        protoOutputStream.write(1120986464264L, getMobileRadioActiveUnknownTime(0) / 1000);
        protoOutputStream.write(1112396529673L, getInteractiveTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1112396529674L, getPowerSaveModeEnabledTime(jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464267L, getNumConnectivityChange(0));
        protoOutputStream.write(1112396529676L, getDeviceIdleModeTime(2, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464269L, getDeviceIdleModeCount(2, 0));
        protoOutputStream.write(1112396529678L, getDeviceIdlingTime(2, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464271L, getDeviceIdlingCount(2, 0));
        protoOutputStream.write(1112396529680L, getLongestDeviceIdleModeTime(2));
        protoOutputStream.write(1112396529681L, getDeviceIdleModeTime(1, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464274L, getDeviceIdleModeCount(1, 0));
        protoOutputStream.write(1112396529683L, getDeviceIdlingTime(1, jElapsedRealtime, 0) / 1000);
        protoOutputStream.write(1120986464276L, getDeviceIdlingCount(1, 0));
        protoOutputStream.write(1112396529685L, getLongestDeviceIdleModeTime(1));
        protoOutputStream.end(jStart8);
        long wifiMulticastWakelockTime = getWifiMulticastWakelockTime(jElapsedRealtime, 0);
        int wifiMulticastWakelockCount = getWifiMulticastWakelockCount(0);
        long jStart9 = protoOutputStream.start(1146756268055L);
        protoOutputStream.write(1112396529665L, wifiMulticastWakelockTime / 1000);
        protoOutputStream.write(1120986464258L, wifiMulticastWakelockCount);
        protoOutputStream.end(jStart9);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null) {
            for (int i10 = 0; i10 < usageList.size(); i10++) {
                BatterySipper batterySipper = usageList.get(i10);
                switch (batterySipper.drainType) {
                    case AMBIENT_DISPLAY:
                        i = 0;
                        i2 = 13;
                        uid = i;
                        long jStart10 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart10);
                        break;
                    case IDLE:
                        uid = 0;
                        i2 = 1;
                        long jStart11 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart11);
                        break;
                    case CELL:
                        i = 0;
                        i2 = 2;
                        uid = i;
                        long jStart12 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart12);
                        break;
                    case PHONE:
                        i = 0;
                        i2 = 3;
                        uid = i;
                        long jStart13 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart13);
                        break;
                    case WIFI:
                        i = 0;
                        i2 = 4;
                        uid = i;
                        long jStart14 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart14);
                        break;
                    case BLUETOOTH:
                        uid = 0;
                        i2 = 5;
                        long jStart15 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart15);
                        break;
                    case SCREEN:
                        i = 0;
                        i2 = 7;
                        uid = i;
                        long jStart16 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart16);
                        break;
                    case FLASHLIGHT:
                        i = 0;
                        i2 = 6;
                        uid = i;
                        long jStart17 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart17);
                        break;
                    case APP:
                        break;
                    case USER:
                        i2 = 8;
                        uid = UserHandle.getUid(batterySipper.userId, 0);
                        long jStart18 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart18);
                        break;
                    case UNACCOUNTED:
                        i2 = 9;
                        uid = 0;
                        long jStart19 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart19);
                        break;
                    case OVERCOUNTED:
                        i2 = 10;
                        uid = 0;
                        long jStart110 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart110);
                        break;
                    case CAMERA:
                        i2 = 11;
                        uid = 0;
                        long jStart111 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart111);
                        break;
                    case MEMORY:
                        i2 = 12;
                        uid = 0;
                        long jStart112 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart112);
                        break;
                    default:
                        uid = 0;
                        i2 = 0;
                        long jStart113 = protoOutputStream.start(SystemProto.POWER_USE_ITEM);
                        protoOutputStream.write(1159641169921L, i2);
                        protoOutputStream.write(1120986464258L, uid);
                        protoOutputStream.write(1103806595075L, batterySipper.totalPowerMah);
                        protoOutputStream.write(1133871366148L, batterySipper.shouldHide);
                        protoOutputStream.write(SystemProto.PowerUseItem.SCREEN_POWER_MAH, batterySipper.screenPowerMah);
                        protoOutputStream.write(SystemProto.PowerUseItem.PROPORTIONAL_SMEAR_MAH, batterySipper.proportionalSmearMah);
                        protoOutputStream.end(jStart113);
                        break;
                }
            }
        }
        long jStart20 = protoOutputStream.start(1146756268050L);
        protoOutputStream.write(1103806595073L, batteryStatsHelper.getPowerProfile().getBatteryCapacity());
        protoOutputStream.write(SystemProto.PowerUseSummary.COMPUTED_POWER_MAH, batteryStatsHelper.getComputedPower());
        protoOutputStream.write(1103806595075L, batteryStatsHelper.getMinDrainedPower());
        protoOutputStream.write(1103806595076L, batteryStatsHelper.getMaxDrainedPower());
        protoOutputStream.end(jStart20);
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        Map<String, ? extends Timer> screenOffRpmStats = getScreenOffRpmStats();
        for (Map.Entry<String, ? extends Timer> entry2 : rpmStats.entrySet()) {
            long jStart21 = protoOutputStream.start(2246267895827L);
            protoOutputStream.write(1138166333441L, entry2.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, entry2.getValue(), jElapsedRealtime, 0);
            dumpTimer(protoOutputStream, 1146756268035L, screenOffRpmStats.get(entry2.getKey()), jElapsedRealtime, 0);
            protoOutputStream.end(jStart21);
        }
        int i11 = 0;
        for (int i12 = 5; i11 < i12; i12 = 5) {
            long jStart22 = protoOutputStream.start(2246267895828L);
            protoOutputStream.write(1159641169921L, i11);
            dumpTimer(protoOutputStream, 1146756268034L, getScreenBrightnessTimer(i11), jElapsedRealtime, 0);
            protoOutputStream.end(jStart22);
            i11++;
        }
        dumpTimer(protoOutputStream, 1146756268053L, getPhoneSignalScanningTimer(), jElapsedRealtime, 0);
        int i13 = 0;
        for (int i14 = 5; i13 < i14; i14 = 5) {
            long jStart23 = protoOutputStream.start(2246267895824L);
            protoOutputStream.write(1159641169921L, i13);
            dumpTimer(protoOutputStream, 1146756268034L, getPhoneSignalStrengthTimer(i13), jElapsedRealtime, 0);
            protoOutputStream.end(jStart23);
            i13++;
        }
        for (Map.Entry<String, ? extends Timer> entry3 : getWakeupReasonStats().entrySet()) {
            long jStart24 = protoOutputStream.start(2246267895830L);
            protoOutputStream.write(1138166333441L, entry3.getKey());
            dumpTimer(protoOutputStream, 1146756268034L, entry3.getValue(), jElapsedRealtime, 0);
            protoOutputStream.end(jStart24);
        }
        for (int i15 = 0; i15 < 5; i15++) {
            long jStart25 = protoOutputStream.start(2246267895832L);
            protoOutputStream.write(1159641169921L, i15);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiSignalStrengthTimer(i15), jElapsedRealtime, 0);
            protoOutputStream.end(jStart25);
        }
        for (int i16 = 0; i16 < 8; i16++) {
            long jStart26 = protoOutputStream.start(2246267895833L);
            protoOutputStream.write(1159641169921L, i16);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiStateTimer(i16), jElapsedRealtime, 0);
            protoOutputStream.end(jStart26);
        }
        for (int i17 = 0; i17 < 13; i17++) {
            long jStart27 = protoOutputStream.start(2246267895834L);
            protoOutputStream.write(1159641169921L, i17);
            dumpTimer(protoOutputStream, 1146756268034L, getWifiSupplStateTimer(i17), jElapsedRealtime, 0);
            protoOutputStream.end(jStart27);
        }
        protoOutputStream.end(jStart);
    }

    private static boolean dumpTimeEstimate(PrintWriter printWriter, String str, String str2, String str3, long j) {
        if (j < 0) {
            return false;
        }
        printWriter.print(str);
        printWriter.print(str2);
        printWriter.print(str3);
        StringBuilder sb = new StringBuilder(64);
        formatTimeMs(sb, j);
        printWriter.print(sb);
        printWriter.println();
        return true;
    }

    private static void dumpTimer(ProtoOutputStream protoOutputStream, long j, Timer timer, long j2, int i) {
        if (timer == null) {
            return;
        }
        long jRoundUsToMs = roundUsToMs(timer.getTotalTimeLocked(j2, i));
        int countLocked = timer.getCountLocked(i);
        long j3 = j2 / 1000;
        long maxDurationMsLocked = timer.getMaxDurationMsLocked(j3);
        long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j3);
        long totalDurationMsLocked = timer.getTotalDurationMsLocked(j3);
        if (jRoundUsToMs == 0 && countLocked == 0 && maxDurationMsLocked == -1 && currentDurationMsLocked == -1 && totalDurationMsLocked == -1) {
            return;
        }
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1112396529665L, jRoundUsToMs);
        protoOutputStream.write(1112396529666L, countLocked);
        if (maxDurationMsLocked != -1) {
            protoOutputStream.write(1112396529667L, maxDurationMsLocked);
        }
        if (currentDurationMsLocked != -1) {
            protoOutputStream.write(1112396529668L, currentDurationMsLocked);
        }
        if (totalDurationMsLocked != -1) {
            protoOutputStream.write(1112396529669L, totalDurationMsLocked);
        }
        protoOutputStream.end(jStart);
    }

    private static final void dumpTimer(PrintWriter printWriter, int i, String str, String str2, Timer timer, long j, int i2) {
        if (timer != null) {
            long jRoundUsToMs = roundUsToMs(timer.getTotalTimeLocked(j, i2));
            int countLocked = timer.getCountLocked(i2);
            if (jRoundUsToMs == 0 && countLocked == 0) {
                return;
            }
            dumpLine(printWriter, i, str, str2, Long.valueOf(jRoundUsToMs), Integer.valueOf(countLocked));
        }
    }

    public static final void formatTimeMs(StringBuilder sb, long j) {
        long j2 = j / 1000;
        formatTimeRaw(sb, j2);
        sb.append(j - (j2 * 1000));
        sb.append("ms ");
    }

    public static final void formatTimeMsNoSpace(StringBuilder sb, long j) {
        long j2 = j / 1000;
        formatTimeRaw(sb, j2);
        sb.append(j - (j2 * 1000));
        sb.append("ms");
    }

    private static final void formatTimeRaw(StringBuilder sb, long j) {
        long j2 = j / 86400;
        if (j2 != 0) {
            sb.append(j2);
            sb.append("d ");
        }
        long j3 = j2 * 60 * 60 * 24;
        long j4 = (j - j3) / 3600;
        if (j4 != 0 || j3 != 0) {
            sb.append(j4);
            sb.append("h ");
        }
        long j5 = j3 + (j4 * 60 * 60);
        long j6 = (j - j5) / 60;
        if (j6 != 0 || j5 != 0) {
            sb.append(j6);
            sb.append("m ");
        }
        long j7 = j5 + (j6 * 60);
        if (j == 0 && j7 == 0) {
            return;
        }
        sb.append(j - j7);
        sb.append("s ");
    }

    public static int mapToInternalProcessState(int i) {
        if (i == 19) {
            return 19;
        }
        if (i == 2) {
            return 0;
        }
        if (i == 3) {
            return 1;
        }
        if (i <= 5) {
            return 2;
        }
        if (i <= 10) {
            return 3;
        }
        if (i <= 11) {
            return 4;
        }
        return i <= 12 ? 5 : 6;
    }

    static void printBitDescriptions(StringBuilder sb, int i, int i2, HistoryTag historyTag, BitDescription[] bitDescriptionArr, boolean z) {
        int i3 = i ^ i2;
        if (i3 == 0) {
            return;
        }
        boolean z2 = false;
        for (BitDescription bitDescription : bitDescriptionArr) {
            if ((bitDescription.mask & i3) != 0) {
                sb.append(z ? WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER : ",");
                if (bitDescription.shift < 0) {
                    sb.append((bitDescription.mask & i2) != 0 ? "+" : NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                    sb.append(z ? bitDescription.name : bitDescription.shortName);
                    if (bitDescription.mask == 1073741824 && historyTag != null) {
                        sb.append("=");
                        if (z) {
                            UserHandle.formatUid(sb, historyTag.uid);
                            sb.append(":\"");
                            sb.append(historyTag.string);
                            sb.append("\"");
                        } else {
                            sb.append(historyTag.poolIdx);
                        }
                        z2 = true;
                    }
                } else {
                    sb.append(z ? bitDescription.name : bitDescription.shortName);
                    sb.append("=");
                    int i4 = (bitDescription.mask & i2) >> bitDescription.shift;
                    if (bitDescription.values == null || i4 < 0 || i4 >= bitDescription.values.length) {
                        sb.append(i4);
                    } else {
                        sb.append(z ? bitDescription.values[i4] : bitDescription.shortValues[i4]);
                    }
                }
            }
        }
        if (z2 || historyTag == null) {
            return;
        }
        sb.append(z ? " wake_lock=" : ",w=");
        if (!z) {
            sb.append(historyTag.poolIdx);
            return;
        }
        UserHandle.formatUid(sb, historyTag.uid);
        sb.append(":\"");
        sb.append(historyTag.string);
        sb.append("\"");
    }

    private final void printControllerActivity(PrintWriter printWriter, StringBuilder sb, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i) {
        long countLocked = controllerActivityCounter.getIdleTimeCounter().getCountLocked(i);
        long countLocked2 = controllerActivityCounter.getRxTimeCounter().getCountLocked(i);
        long countLocked3 = controllerActivityCounter.getPowerCounter().getCountLocked(i);
        long jComputeBatteryRealtime = computeBatteryRealtime(SystemClock.elapsedRealtime() * 1000, i) / 1000;
        LongCounter[] txTimeCounters = controllerActivityCounter.getTxTimeCounters();
        int i2 = 0;
        long countLocked4 = 0;
        for (int length = txTimeCounters.length; i2 < length; length = length) {
            countLocked4 += txTimeCounters[i2].getCountLocked(i);
            i2++;
        }
        if (str2.equals(WIFI_CONTROLLER_NAME)) {
            long countLocked5 = controllerActivityCounter.getScanTimeCounter().getCountLocked(i);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Scan time:  ");
            formatTimeMs(sb, countLocked5);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked5, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
            long j = jComputeBatteryRealtime - ((countLocked + countLocked2) + countLocked4);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Sleep time:  ");
            formatTimeMs(sb, j);
            sb.append("(");
            sb.append(formatRatioLocked(j, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        }
        if (str2.equals(CELLULAR_CONTROLLER_NAME)) {
            long countLocked6 = controllerActivityCounter.getSleepTimeCounter().getCountLocked(i);
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Sleep time:  ");
            formatTimeMs(sb, countLocked6);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked6, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        }
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Idle time:   ");
        formatTimeMs(sb, countLocked);
        sb.append("(");
        sb.append(formatRatioLocked(countLocked, jComputeBatteryRealtime));
        sb.append(")");
        printWriter.println(sb.toString());
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Rx time:     ");
        formatTimeMs(sb, countLocked2);
        sb.append("(");
        sb.append(formatRatioLocked(countLocked2, jComputeBatteryRealtime));
        sb.append(")");
        printWriter.println(sb.toString());
        sb.setLength(0);
        sb.append(str);
        sb.append("     ");
        sb.append(str2);
        sb.append(" Tx time:     ");
        byte b = -1;
        if (str2.hashCode() == -851952246 && str2.equals(CELLULAR_CONTROLLER_NAME)) {
            b = 0;
        }
        String[] strArr = b != 0 ? new String[]{"[0]", "[1]", "[2]", "[3]", "[4]"} : new String[]{"   less than 0dBm: ", "   0dBm to 8dBm: ", "   8dBm to 15dBm: ", "   15dBm to 20dBm: ", "   above 20dBm: "};
        int iMin = Math.min(controllerActivityCounter.getTxTimeCounters().length, strArr.length);
        if (iMin > 1) {
            printWriter.println(sb.toString());
            for (int i3 = 0; i3 < iMin; i3++) {
                long countLocked7 = controllerActivityCounter.getTxTimeCounters()[i3].getCountLocked(i);
                sb.setLength(0);
                sb.append(str);
                sb.append("    ");
                sb.append(strArr[i3]);
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb, countLocked7);
                sb.append("(");
                sb.append(formatRatioLocked(countLocked7, jComputeBatteryRealtime));
                sb.append(")");
                printWriter.println(sb.toString());
            }
        } else {
            long countLocked8 = controllerActivityCounter.getTxTimeCounters()[0].getCountLocked(i);
            formatTimeMs(sb, countLocked8);
            sb.append("(");
            sb.append(formatRatioLocked(countLocked8, jComputeBatteryRealtime));
            sb.append(")");
            printWriter.println(sb.toString());
        }
        if (countLocked3 > 0) {
            sb.setLength(0);
            sb.append(str);
            sb.append("     ");
            sb.append(str2);
            sb.append(" Battery drain: ");
            sb.append(BatteryStatsHelper.makemAh(countLocked3 / 3600000.0d));
            sb.append("mAh");
            printWriter.println(sb.toString());
        }
    }

    private final void printControllerActivityIfInteresting(PrintWriter printWriter, StringBuilder sb, String str, String str2, ControllerActivityCounter controllerActivityCounter, int i) {
        if (controllerActivityHasData(controllerActivityCounter, i)) {
            printControllerActivity(printWriter, sb, str, str2, controllerActivityCounter, i);
        }
    }

    private void printSizeValue(PrintWriter printWriter, long j) {
        float f = j;
        String str = "";
        if (f >= 10240.0f) {
            str = "KB";
            f /= 1024.0f;
        }
        if (f >= 10240.0f) {
            str = "MB";
            f /= 1024.0f;
        }
        if (f >= 10240.0f) {
            str = "GB";
            f /= 1024.0f;
        }
        if (f >= 10240.0f) {
            str = "TB";
            f /= 1024.0f;
        }
        if (f >= 10240.0f) {
            str = "PB";
            f /= 1024.0f;
        }
        printWriter.print((int) f);
        printWriter.print(str);
    }

    private static final boolean printTimer(PrintWriter printWriter, StringBuilder sb, Timer timer, long j, int i, String str, String str2) {
        if (timer != null) {
            long totalTimeLocked = (timer.getTotalTimeLocked(j, i) + 500) / 1000;
            int countLocked = timer.getCountLocked(i);
            if (totalTimeLocked != 0) {
                sb.setLength(0);
                sb.append(str);
                sb.append("    ");
                sb.append(str2);
                sb.append(": ");
                formatTimeMs(sb, totalTimeLocked);
                sb.append("realtime (");
                sb.append(countLocked);
                sb.append(" times)");
                long j2 = j / 1000;
                long maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
                if (maxDurationMsLocked >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMsLocked);
                }
                if (timer.isRunningLocked()) {
                    long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
                    if (currentDurationMsLocked >= 0) {
                        sb.append(" (running for ");
                        sb.append(currentDurationMsLocked);
                        sb.append("ms)");
                    } else {
                        sb.append(" (running)");
                    }
                }
                printWriter.println(sb.toString());
                return true;
            }
        }
        return false;
    }

    private static final String printWakeLock(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        if (timer != null) {
            long jComputeWakeLock = computeWakeLock(timer, j, i);
            int countLocked = timer.getCountLocked(i);
            if (jComputeWakeLock != 0) {
                sb.append(str2);
                formatTimeMs(sb, jComputeWakeLock);
                if (str != null) {
                    sb.append(str);
                    sb.append(' ');
                }
                sb.append('(');
                sb.append(countLocked);
                sb.append(" times)");
                long j2 = j / 1000;
                long maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
                if (maxDurationMsLocked >= 0) {
                    sb.append(" max=");
                    sb.append(maxDurationMsLocked);
                }
                long totalDurationMsLocked = timer.getTotalDurationMsLocked(j2);
                if (totalDurationMsLocked > jComputeWakeLock) {
                    sb.append(" actual=");
                    sb.append(totalDurationMsLocked);
                }
                if (!timer.isRunningLocked()) {
                    return ", ";
                }
                long currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
                if (currentDurationMsLocked < 0) {
                    sb.append(" (running)");
                    return ", ";
                }
                sb.append(" (running for ");
                sb.append(currentDurationMsLocked);
                sb.append("ms)");
                return ", ";
            }
        }
        return str2;
    }

    private static final String printWakeLockCheckin(StringBuilder sb, Timer timer, long j, String str, int i, String str2) {
        int countLocked;
        long totalDurationMsLocked;
        long currentDurationMsLocked;
        long maxDurationMsLocked;
        String str3;
        long totalTimeLocked = 0;
        if (timer != null) {
            totalTimeLocked = timer.getTotalTimeLocked(j, i);
            countLocked = timer.getCountLocked(i);
            long j2 = j / 1000;
            currentDurationMsLocked = timer.getCurrentDurationMsLocked(j2);
            maxDurationMsLocked = timer.getMaxDurationMsLocked(j2);
            totalDurationMsLocked = timer.getTotalDurationMsLocked(j2);
        } else {
            countLocked = 0;
            totalDurationMsLocked = 0;
            currentDurationMsLocked = 0;
            maxDurationMsLocked = 0;
        }
        sb.append(str2);
        sb.append((totalTimeLocked + 500) / 1000);
        sb.append(',');
        if (str != null) {
            str3 = str + ",";
        } else {
            str3 = "";
        }
        sb.append(str3);
        sb.append(countLocked);
        sb.append(',');
        sb.append(currentDurationMsLocked);
        sb.append(',');
        sb.append(maxDurationMsLocked);
        if (str == null) {
            return ",";
        }
        sb.append(',');
        sb.append(totalDurationMsLocked);
        return ",";
    }

    private void printmAh(PrintWriter printWriter, double d) {
        printWriter.print(BatteryStatsHelper.makemAh(d));
    }

    private void printmAh(StringBuilder sb, double d) {
        sb.append(BatteryStatsHelper.makemAh(d));
    }

    private static long roundUsToMs(long j) {
        return (j + 500) / 1000;
    }

    public abstract void commitCurrentHistoryBatchLocked();

    public abstract long computeBatteryRealtime(long j, int i);

    public abstract long computeBatteryScreenOffRealtime(long j, int i);

    public abstract long computeBatteryScreenOffUptime(long j, int i);

    public abstract long computeBatteryTimeRemaining(long j);

    public abstract long computeBatteryUptime(long j, int i);

    public abstract long computeChargeTimeRemaining(long j);

    public abstract long computeRealtime(long j, int i);

    public abstract long computeUptime(long j, int i);

    public final void dumpCheckinLocked(Context context, PrintWriter printWriter, int i, int i2) {
        dumpCheckinLocked(context, printWriter, i, i2, BatteryStatsHelper.checkWifiOnly(context));
    }

    /* JADX WARN: Code duplicated, block: B:227:0x0bb6  */
    /* JADX WARN: Code duplicated, block: B:276:0x0d32  */
    /* JADX WARN: Code duplicated, block: B:278:0x0d4b  */
    /* JADX WARN: Code duplicated, block: B:279:0x0d52  */
    /* JADX WARN: Code duplicated, block: B:281:0x0d58  */
    /* JADX WARN: Code duplicated, block: B:282:0x0d61  */
    /* JADX WARN: Code duplicated, block: B:285:0x0d6a  */
    /* JADX WARN: Code duplicated, block: B:289:0x0dc6  */
    /* JADX WARN: Code duplicated, block: B:291:0x0de1  */
    /* JADX WARN: Code duplicated, block: B:292:0x0dea  */
    /* JADX WARN: Code duplicated, block: B:294:0x0df2  */
    /* JADX WARN: Code duplicated, block: B:295:0x0df7  */
    /* JADX WARN: Code duplicated, block: B:298:0x0dfc  */
    /* JADX WARN: Code duplicated, block: B:299:0x0e42  */
    /* JADX WARN: Code duplicated, block: B:303:0x0e5e  */
    /* JADX WARN: Code duplicated, block: B:305:0x0e66  */
    /* JADX WARN: Code duplicated, block: B:309:0x0ed2  */
    /* JADX WARN: Code duplicated, block: B:312:0x0f1b  */
    /* JADX WARN: Code duplicated, block: B:314:0x0f2b  */
    /* JADX WARN: Code duplicated, block: B:316:0x0f3b  */
    /* JADX WARN: Code duplicated, block: B:318:0x0f45  */
    /* JADX WARN: Code duplicated, block: B:319:0x0f4e  */
    /* JADX WARN: Code duplicated, block: B:322:0x0f59  */
    /* JADX WARN: Code duplicated, block: B:323:0x0f5e  */
    /* JADX WARN: Code duplicated, block: B:325:0x0f96  */
    /* JADX WARN: Code duplicated, block: B:326:0x0f9b  */
    /* JADX WARN: Code duplicated, block: B:331:0x0fe5 A[LOOP:25: B:330:0x0fe3->B:331:0x0fe5, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:334:0x100a  */
    /* JADX WARN: Code duplicated, block: B:341:0x1023  */
    /* JADX WARN: Code duplicated, block: B:343:0x1048  */
    /* JADX WARN: Code duplicated, block: B:345:0x104e  */
    /* JADX WARN: Code duplicated, block: B:347:0x1054  */
    /* JADX WARN: Code duplicated, block: B:350:0x105d  */
    /* JADX WARN: Code duplicated, block: B:352:0x1064  */
    /* JADX WARN: Code duplicated, block: B:353:0x1067  */
    /* JADX WARN: Code duplicated, block: B:357:0x1087  */
    /* JADX WARN: Code duplicated, block: B:360:0x108b A[LOOP:27: B:358:0x1088->B:360:0x108b, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:362:0x10ab  */
    /* JADX WARN: Code duplicated, block: B:365:0x10b1 A[LOOP:36: B:363:0x10ae->B:365:0x10b1, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:367:0x10d6  */
    /* JADX WARN: Code duplicated, block: B:368:0x10db  */
    /* JADX WARN: Code duplicated, block: B:373:0x10e7  */
    /* JADX WARN: Code duplicated, block: B:375:0x10ed  */
    /* JADX WARN: Code duplicated, block: B:397:0x1168  */
    /* JADX WARN: Code duplicated, block: B:400:0x117c  */
    /* JADX WARN: Code duplicated, block: B:411:0x11b4  */
    /* JADX WARN: Code duplicated, block: B:415:0x122a  */
    /* JADX WARN: Code duplicated, block: B:417:0x123c A[LOOP:34: B:416:0x123a->B:417:0x123c, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:420:0x127b  */
    /* JADX WARN: Code duplicated, block: B:426:0x12a7  */
    /* JADX WARN: Code duplicated, block: B:480:0x0dae A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:486:0x0ec4 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:497:0x1164 A[ADDED_TO_REGION, REMOVE, SYNTHETIC] */
    /* JADX WARN: Instruction removed from duplicated block: B:285:0x0d6a, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:298:0x0dfc, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:305:0x0e66, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:360:0x108b, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:411:0x11b4, please report this as an issue */
    public final void dumpCheckinLocked(Context context, PrintWriter printWriter, int i, int i2, boolean z) {
        String str;
        StringBuilder sb;
        char c;
        int i3;
        long j;
        int i4;
        ArrayMap<String, ? extends Timer> syncStats;
        int size;
        long j2;
        ArrayMap<String, ? extends Timer> jobStats;
        int size2;
        long j3;
        long j4;
        int i5;
        ArrayMap<String, SparseIntArray> jobCompletionStats;
        int size3;
        StringBuilder sb2;
        SparseArray<? extends Uid.Sensor> sensorStats;
        int size4;
        int i6;
        StringBuilder sb3;
        long j5;
        long j6;
        int i7;
        Object[] objArr;
        long j7;
        int i8;
        long j8;
        long j9;
        int i9;
        long userCpuTimeUs;
        long systemCpuTimeUs;
        int i10;
        long[] jArr;
        StringBuilder sb4;
        ArrayMap<String, ? extends Uid.Proc> processStats;
        int size5;
        StringBuilder sb5;
        long[] jArr2;
        ArrayMap<String, ? extends Uid.Pkg> packageStats;
        int i11;
        int size6;
        long j10;
        ArrayMap<String, ? extends Counter> wakeupAlarmStats;
        int size7;
        int i12;
        ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats;
        int size8;
        long startTime;
        int starts;
        int launches;
        long userTime;
        long systemTime;
        long foregroundTime;
        int starts2;
        int numCrashes;
        int numAnrs;
        long[] cpuFreqTimes;
        int i13;
        long[] cpuFreqTimes2;
        int i14;
        long[] screenOffCpuFreqTimes;
        int i15;
        int i16;
        String str2;
        Uid.Sensor sensorValueAt;
        int iKeyAt;
        Timer sensorTime;
        long j11;
        long totalTimeLocked;
        Timer sensorBackgroundTime;
        int countLocked;
        long totalDurationMsLocked;
        SparseIntArray sparseIntArrayValueAt;
        long totalTimeLocked2;
        int countLocked2;
        Timer subTimer;
        long j12;
        long totalDurationMsLocked2;
        int countLocked3;
        long totalTimeLocked3;
        int countLocked4;
        Timer subTimer2;
        long j13;
        long totalDurationMsLocked3;
        int countLocked5;
        String str3;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        long j14 = jElapsedRealtime * 1000;
        long batteryUptime = getBatteryUptime(jUptimeMillis);
        long jComputeBatteryUptime = computeBatteryUptime(jUptimeMillis, i);
        long jComputeBatteryRealtime = computeBatteryRealtime(j14, i);
        long jComputeBatteryScreenOffUptime = computeBatteryScreenOffUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffRealtime = computeBatteryScreenOffRealtime(j14, i);
        long jComputeRealtime = computeRealtime(j14, i);
        long jComputeUptime = computeUptime(jUptimeMillis, i);
        long screenOnTime = getScreenOnTime(j14, i);
        long screenDozeTime = getScreenDozeTime(j14, i);
        long interactiveTime = getInteractiveTime(j14, i);
        long powerSaveModeEnabledTime = getPowerSaveModeEnabledTime(j14, i);
        long deviceIdleModeTime = getDeviceIdleModeTime(1, j14, i);
        long deviceIdleModeTime2 = getDeviceIdleModeTime(2, j14, i);
        long deviceIdlingTime = getDeviceIdlingTime(1, j14, i);
        long deviceIdlingTime2 = getDeviceIdlingTime(2, j14, i);
        int numConnectivityChange = getNumConnectivityChange(i);
        long phoneOnTime = getPhoneOnTime(j14, i);
        long uahDischarge = getUahDischarge(i);
        long uahDischargeScreenOff = getUahDischargeScreenOff(i);
        long uahDischargeScreenDoze = getUahDischargeScreenDoze(i);
        long uahDischargeLightDoze = getUahDischargeLightDoze(i);
        long uahDischargeDeepDoze = getUahDischargeDeepDoze(i);
        StringBuilder sb6 = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        long j15 = jElapsedRealtime;
        int size9 = uidStats.size();
        String str4 = STAT_NAMES[i];
        Object[] objArr2 = new Object[12];
        StringBuilder sb7 = sb6;
        objArr2[0] = i == 0 ? Integer.valueOf(getStartCount()) : "N/A";
        objArr2[1] = Long.valueOf(jComputeBatteryRealtime / 1000);
        objArr2[2] = Long.valueOf(jComputeBatteryUptime / 1000);
        objArr2[3] = Long.valueOf(jComputeRealtime / 1000);
        objArr2[4] = Long.valueOf(jComputeUptime / 1000);
        objArr2[5] = Long.valueOf(getStartClockTime());
        objArr2[6] = Long.valueOf(jComputeBatteryScreenOffRealtime / 1000);
        objArr2[7] = Long.valueOf(jComputeBatteryScreenOffUptime / 1000);
        objArr2[8] = Integer.valueOf(getEstimatedBatteryCapacity());
        objArr2[9] = Integer.valueOf(getMinLearnedBatteryCapacity());
        objArr2[10] = Integer.valueOf(getMaxLearnedBatteryCapacity());
        objArr2[11] = Long.valueOf(screenDozeTime / 1000);
        dumpLine(printWriter, 0, str4, BATTERY_DATA, objArr2);
        long totalTimeLocked4 = 0;
        long totalTimeLocked5 = 0;
        for (int i17 = 0; i17 < size9; i17++) {
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats = uidStats.valueAt(i17).getWakelockStats();
            int i18 = 1;
            int size10 = wakelockStats.size() - 1;
            while (size10 >= 0) {
                Uid.Wakelock wakelockValueAt = wakelockStats.valueAt(size10);
                Timer wakeTime = wakelockValueAt.getWakeTime(i18);
                if (wakeTime != null) {
                    totalTimeLocked4 += wakeTime.getTotalTimeLocked(j14, i);
                }
                Timer wakeTime2 = wakelockValueAt.getWakeTime(0);
                if (wakeTime2 != null) {
                    totalTimeLocked5 += wakeTime2.getTotalTimeLocked(j14, i);
                }
                size10--;
                i18 = 1;
            }
        }
        dumpLine(printWriter, 0, str4, GLOBAL_NETWORK_DATA, Long.valueOf(getNetworkActivityBytes(0, i)), Long.valueOf(getNetworkActivityBytes(1, i)), Long.valueOf(getNetworkActivityBytes(2, i)), Long.valueOf(getNetworkActivityBytes(3, i)), Long.valueOf(getNetworkActivityPackets(0, i)), Long.valueOf(getNetworkActivityPackets(1, i)), Long.valueOf(getNetworkActivityPackets(2, i)), Long.valueOf(getNetworkActivityPackets(3, i)), Long.valueOf(getNetworkActivityBytes(4, i)), Long.valueOf(getNetworkActivityBytes(5, i)));
        SparseArray<? extends Uid> sparseArray = uidStats;
        long j16 = batteryUptime;
        long j17 = j14;
        dumpControllerActivityLine(printWriter, 0, str4, GLOBAL_MODEM_CONTROLLER_DATA, getModemControllerActivity(), i);
        String str5 = str4;
        dumpLine(printWriter, 0, str5, GLOBAL_WIFI_DATA, Long.valueOf(getWifiOnTime(j17, i) / 1000), Long.valueOf(getGlobalWifiRunningTime(j17, i) / 1000), 0, 0, 0);
        dumpControllerActivityLine(printWriter, 0, str5, GLOBAL_WIFI_CONTROLLER_DATA, getWifiControllerActivity(), i);
        dumpControllerActivityLine(printWriter, 0, str5, GLOBAL_BLUETOOTH_CONTROLLER_DATA, getBluetoothControllerActivity(), i);
        dumpLine(printWriter, 0, str5, MISC_DATA, Long.valueOf(screenOnTime / 1000), Long.valueOf(phoneOnTime / 1000), Long.valueOf(totalTimeLocked4 / 1000), Long.valueOf(totalTimeLocked5 / 1000), Long.valueOf(getMobileRadioActiveTime(j17, i) / 1000), Long.valueOf(getMobileRadioActiveAdjustedTime(i) / 1000), Long.valueOf(interactiveTime / 1000), Long.valueOf(powerSaveModeEnabledTime / 1000), Integer.valueOf(numConnectivityChange), Long.valueOf(deviceIdleModeTime2 / 1000), Integer.valueOf(getDeviceIdleModeCount(2, i)), Long.valueOf(deviceIdlingTime2 / 1000), Integer.valueOf(getDeviceIdlingCount(2, i)), Integer.valueOf(getMobileRadioActiveCount(i)), Long.valueOf(getMobileRadioActiveUnknownTime(i) / 1000), Long.valueOf(deviceIdleModeTime / 1000), Integer.valueOf(getDeviceIdleModeCount(1, i)), Long.valueOf(deviceIdlingTime / 1000), Integer.valueOf(getDeviceIdlingCount(1, i)), Long.valueOf(getLongestDeviceIdleModeTime(1)), Long.valueOf(getLongestDeviceIdleModeTime(2)));
        Object[] objArr3 = new Object[5];
        for (int i19 = 0; i19 < 5; i19++) {
            objArr3[i19] = Long.valueOf(getScreenBrightnessTime(i19, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, "br", objArr3);
        Object[] objArr4 = new Object[5];
        for (int i20 = 0; i20 < 5; i20++) {
            objArr4[i20] = Long.valueOf(getPhoneSignalStrengthTime(i20, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, SIGNAL_STRENGTH_TIME_DATA, objArr4);
        dumpLine(printWriter, 0, str5, SIGNAL_SCANNING_TIME_DATA, Long.valueOf(getPhoneSignalScanningTime(j17, i) / 1000));
        for (int i21 = 0; i21 < 5; i21++) {
            objArr4[i21] = Integer.valueOf(getPhoneSignalStrengthCount(i21, i));
        }
        dumpLine(printWriter, 0, str5, SIGNAL_STRENGTH_COUNT_DATA, objArr4);
        Object[] objArr5 = new Object[21];
        for (int i22 = 0; i22 < 21; i22++) {
            objArr5[i22] = Long.valueOf(getPhoneDataConnectionTime(i22, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, DATA_CONNECTION_TIME_DATA, objArr5);
        for (int i23 = 0; i23 < 21; i23++) {
            objArr5[i23] = Integer.valueOf(getPhoneDataConnectionCount(i23, i));
        }
        dumpLine(printWriter, 0, str5, DATA_CONNECTION_COUNT_DATA, objArr5);
        Object[] objArr6 = new Object[8];
        for (int i24 = 0; i24 < 8; i24++) {
            objArr6[i24] = Long.valueOf(getWifiStateTime(i24, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, WIFI_STATE_TIME_DATA, objArr6);
        for (int i25 = 0; i25 < 8; i25++) {
            objArr6[i25] = Integer.valueOf(getWifiStateCount(i25, i));
        }
        dumpLine(printWriter, 0, str5, WIFI_STATE_COUNT_DATA, objArr6);
        Object[] objArr7 = new Object[13];
        for (int i26 = 0; i26 < 13; i26++) {
            objArr7[i26] = Long.valueOf(getWifiSupplStateTime(i26, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, WIFI_SUPPL_STATE_TIME_DATA, objArr7);
        for (int i27 = 0; i27 < 13; i27++) {
            objArr7[i27] = Integer.valueOf(getWifiSupplStateCount(i27, i));
        }
        dumpLine(printWriter, 0, str5, WIFI_SUPPL_STATE_COUNT_DATA, objArr7);
        Object[] objArr8 = new Object[5];
        for (int i28 = 0; i28 < 5; i28++) {
            objArr8[i28] = Long.valueOf(getWifiSignalStrengthTime(i28, j17, i) / 1000);
        }
        dumpLine(printWriter, 0, str5, WIFI_SIGNAL_STRENGTH_TIME_DATA, objArr8);
        for (int i29 = 0; i29 < 5; i29++) {
            objArr8[i29] = Integer.valueOf(getWifiSignalStrengthCount(i29, i));
        }
        dumpLine(printWriter, 0, str5, WIFI_SIGNAL_STRENGTH_COUNT_DATA, objArr8);
        dumpLine(printWriter, 0, str5, WIFI_MULTICAST_TOTAL_DATA, Long.valueOf(getWifiMulticastWakelockTime(j17, i) / 1000), Integer.valueOf(getWifiMulticastWakelockCount(i)));
        if (i == 2) {
            dumpLine(printWriter, 0, str5, BATTERY_LEVEL_DATA, Integer.valueOf(getDischargeStartLevel()), Integer.valueOf(getDischargeCurrentLevel()));
        }
        if (i == 2) {
            dumpLine(printWriter, 0, str5, BATTERY_DISCHARGE_DATA, Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeStartLevel() - getDischargeCurrentLevel()), Integer.valueOf(getDischargeAmountScreenOn()), Integer.valueOf(getDischargeAmountScreenOff()), Long.valueOf(uahDischarge / 1000), Long.valueOf(uahDischargeScreenOff / 1000), Integer.valueOf(getDischargeAmountScreenDoze()), Long.valueOf(uahDischargeScreenDoze / 1000), Long.valueOf(uahDischargeLightDoze / 1000), Long.valueOf(uahDischargeDeepDoze / 1000));
        } else {
            dumpLine(printWriter, 0, str5, BATTERY_DISCHARGE_DATA, Integer.valueOf(getLowDischargeAmountSinceCharge()), Integer.valueOf(getHighDischargeAmountSinceCharge()), Integer.valueOf(getDischargeAmountScreenOnSinceCharge()), Integer.valueOf(getDischargeAmountScreenOffSinceCharge()), Long.valueOf(uahDischarge / 1000), Long.valueOf(uahDischargeScreenOff / 1000), Integer.valueOf(getDischargeAmountScreenDozeSinceCharge()), Long.valueOf(uahDischargeScreenDoze / 1000), Long.valueOf(uahDischargeLightDoze / 1000), Long.valueOf(uahDischargeDeepDoze / 1000));
        }
        if (i2 < 0) {
            Map<String, ? extends Timer> kernelWakelockStats = getKernelWakelockStats();
            if (kernelWakelockStats.size() > 0) {
                for (Map.Entry<String, ? extends Timer> entry : kernelWakelockStats.entrySet()) {
                    StringBuilder sb8 = sb7;
                    sb8.setLength(0);
                    String str6 = str5;
                    printWakeLockCheckin(sb8, entry.getValue(), j17, null, i, "");
                    dumpLine(printWriter, 0, str6, KERNEL_WAKELOCK_DATA, "\"" + entry.getKey() + "\"", sb8.toString());
                    str5 = str6;
                    sb7 = sb8;
                }
            }
            str = str5;
            sb = sb7;
            Map<String, ? extends Timer> wakeupReasonStats = getWakeupReasonStats();
            if (wakeupReasonStats.size() > 0) {
                for (Iterator<Map.Entry<String, ? extends Timer>> it = wakeupReasonStats.entrySet().iterator(); it.hasNext(); it = it) {
                    Map.Entry<String, ? extends Timer> next = it.next();
                    dumpLine(printWriter, 0, str, WAKEUP_REASON_DATA, "\"" + next.getKey() + "\"", Long.valueOf((next.getValue().getTotalTimeLocked(j17, i) + 500) / 1000), Integer.valueOf(next.getValue().getCountLocked(i)));
                }
            }
        } else {
            str = str5;
            sb = sb7;
        }
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        Map<String, ? extends Timer> screenOffRpmStats = getScreenOffRpmStats();
        if (rpmStats.size() > 0) {
            Iterator<Map.Entry<String, ? extends Timer>> it2 = rpmStats.entrySet().iterator();
            while (it2.hasNext()) {
                Map.Entry<String, ? extends Timer> next2 = it2.next();
                StringBuilder sb9 = sb;
                sb9.setLength(0);
                Timer value = next2.getValue();
                long totalTimeLocked6 = (value.getTotalTimeLocked(j17, i) + 500) / 1000;
                int countLocked6 = value.getCountLocked(i);
                Timer timer = screenOffRpmStats.get(next2.getKey());
                if (timer != null) {
                    long totalTimeLocked7 = (timer.getTotalTimeLocked(j17, i) + 500) / 1000;
                }
                if (timer != null) {
                    timer.getCountLocked(i);
                }
                dumpLine(printWriter, 0, str, RESOURCE_POWER_MANAGER_DATA, "\"" + next2.getKey() + "\"", Long.valueOf(totalTimeLocked6), Integer.valueOf(countLocked6));
                sb = sb9;
                it2 = it2;
                screenOffRpmStats = screenOffRpmStats;
                j17 = j17;
            }
        }
        long j18 = j17;
        StringBuilder sb10 = sb;
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, z);
        batteryStatsHelper.create(this);
        batteryStatsHelper.refreshStats(i, -1);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList != null && usageList.size() > 0) {
            dumpLine(printWriter, 0, str, POWER_USE_SUMMARY_DATA, BatteryStatsHelper.makemAh(batteryStatsHelper.getPowerProfile().getBatteryCapacity()), BatteryStatsHelper.makemAh(batteryStatsHelper.getComputedPower()), BatteryStatsHelper.makemAh(batteryStatsHelper.getMinDrainedPower()), BatteryStatsHelper.makemAh(batteryStatsHelper.getMaxDrainedPower()));
            int uid = 0;
            for (int i30 = 0; i30 < usageList.size(); i30++) {
                BatterySipper batterySipper = usageList.get(i30);
                switch (batterySipper.drainType) {
                    case AMBIENT_DISPLAY:
                        str3 = "ambi";
                        break;
                    case IDLE:
                        str3 = "idle";
                        break;
                    case CELL:
                        str3 = "cell";
                        break;
                    case PHONE:
                        str3 = "phone";
                        break;
                    case WIFI:
                        str3 = "wifi";
                        break;
                    case BLUETOOTH:
                        str3 = "blue";
                        break;
                    case SCREEN:
                        str3 = "scrn";
                        break;
                    case FLASHLIGHT:
                        str3 = "flashlight";
                        break;
                    case APP:
                        uid = batterySipper.uidObj.getUid();
                        str3 = "uid";
                        break;
                    case USER:
                        uid = UserHandle.getUid(batterySipper.userId, 0);
                        str3 = "user";
                        break;
                    case UNACCOUNTED:
                        str3 = "unacc";
                        break;
                    case OVERCOUNTED:
                        str3 = "over";
                        break;
                    case CAMERA:
                        str3 = Context.CAMERA_SERVICE;
                        break;
                    case MEMORY:
                        str3 = "memory";
                        break;
                    default:
                        str3 = "???";
                        break;
                }
                dumpLine(printWriter, uid, str, POWER_USE_ITEM_DATA, str3, BatteryStatsHelper.makemAh(batterySipper.totalPowerMah), Integer.valueOf(batterySipper.shouldHide ? 1 : 0), BatteryStatsHelper.makemAh(batterySipper.screenPowerMah), BatteryStatsHelper.makemAh(batterySipper.proportionalSmearMah));
            }
        }
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            sb10.setLength(0);
            int i31 = 0;
            while (i31 < cpuFreqs.length) {
                StringBuilder sb11 = new StringBuilder();
                sb11.append(i31 == 0 ? "" : ",");
                sb11.append(cpuFreqs[i31]);
                sb10.append(sb11.toString());
                i31++;
            }
            dumpLine(printWriter, 0, str, GLOBAL_CPU_FREQ_DATA, sb10.toString());
        }
        int i32 = size9;
        int i33 = 0;
        while (i33 < i32) {
            int iKeyAt2 = sparseArray.keyAt(i33);
            if (i2 < 0 || iKeyAt2 == i2) {
                Uid uidValueAt = sparseArray.valueAt(i33);
                long networkActivityBytes = uidValueAt.getNetworkActivityBytes(0, i);
                long networkActivityBytes2 = uidValueAt.getNetworkActivityBytes(1, i);
                long networkActivityBytes3 = uidValueAt.getNetworkActivityBytes(2, i);
                long networkActivityBytes4 = uidValueAt.getNetworkActivityBytes(3, i);
                long networkActivityPackets = uidValueAt.getNetworkActivityPackets(0, i);
                long networkActivityPackets2 = uidValueAt.getNetworkActivityPackets(1, i);
                long mobileRadioActiveTime = uidValueAt.getMobileRadioActiveTime(i);
                int mobileRadioActiveCount = uidValueAt.getMobileRadioActiveCount(i);
                long mobileRadioApWakeupCount = uidValueAt.getMobileRadioApWakeupCount(i);
                long networkActivityPackets3 = uidValueAt.getNetworkActivityPackets(2, i);
                long networkActivityPackets4 = uidValueAt.getNetworkActivityPackets(3, i);
                long wifiRadioApWakeupCount = uidValueAt.getWifiRadioApWakeupCount(i);
                long networkActivityBytes5 = uidValueAt.getNetworkActivityBytes(4, i);
                long networkActivityBytes6 = uidValueAt.getNetworkActivityBytes(5, i);
                long networkActivityBytes7 = uidValueAt.getNetworkActivityBytes(6, i);
                long networkActivityBytes8 = uidValueAt.getNetworkActivityBytes(7, i);
                long networkActivityBytes9 = uidValueAt.getNetworkActivityBytes(8, i);
                long networkActivityBytes10 = uidValueAt.getNetworkActivityBytes(9, i);
                long networkActivityPackets5 = uidValueAt.getNetworkActivityPackets(6, i);
                long networkActivityPackets6 = uidValueAt.getNetworkActivityPackets(7, i);
                long networkActivityPackets7 = uidValueAt.getNetworkActivityPackets(8, i);
                long networkActivityPackets8 = uidValueAt.getNetworkActivityPackets(9, i);
                if (networkActivityBytes > 0 || networkActivityBytes2 > 0 || networkActivityBytes3 > 0 || networkActivityBytes4 > 0 || networkActivityPackets > 0 || networkActivityPackets2 > 0 || networkActivityPackets3 > 0 || networkActivityPackets4 > 0 || mobileRadioActiveTime > 0 || mobileRadioActiveCount > 0 || networkActivityBytes5 > 0 || networkActivityBytes6 > 0 || mobileRadioApWakeupCount > 0 || wifiRadioApWakeupCount > 0 || networkActivityBytes7 > 0 || networkActivityBytes8 > 0 || networkActivityBytes9 > 0 || networkActivityBytes10 > 0 || networkActivityPackets5 > 0 || networkActivityPackets6 > 0 || networkActivityPackets7 > 0 || networkActivityPackets8 > 0) {
                    c = '\n';
                    dumpLine(printWriter, iKeyAt2, str, NETWORK_DATA, Long.valueOf(networkActivityBytes), Long.valueOf(networkActivityBytes2), Long.valueOf(networkActivityBytes3), Long.valueOf(networkActivityBytes4), Long.valueOf(networkActivityPackets), Long.valueOf(networkActivityPackets2), Long.valueOf(networkActivityPackets3), Long.valueOf(networkActivityPackets4), Long.valueOf(mobileRadioActiveTime), Integer.valueOf(mobileRadioActiveCount), Long.valueOf(networkActivityBytes5), Long.valueOf(networkActivityBytes6), Long.valueOf(mobileRadioApWakeupCount), Long.valueOf(wifiRadioApWakeupCount), Long.valueOf(networkActivityBytes7), Long.valueOf(networkActivityBytes8), Long.valueOf(networkActivityBytes9), Long.valueOf(networkActivityBytes10), Long.valueOf(networkActivityPackets5), Long.valueOf(networkActivityPackets6), Long.valueOf(networkActivityPackets7), Long.valueOf(networkActivityPackets8));
                } else {
                    c = '\n';
                }
                int i34 = i;
                Uid uid2 = uidValueAt;
                dumpControllerActivityLine(printWriter, iKeyAt2, str, MODEM_CONTROLLER_DATA, uidValueAt.getModemControllerActivity(), i);
                long j19 = j18;
                long fullWifiLockTime = uid2.getFullWifiLockTime(j19, i34);
                long wifiScanTime = uid2.getWifiScanTime(j19, i34);
                int wifiScanCount = uid2.getWifiScanCount(i34);
                int wifiScanBackgroundCount = uid2.getWifiScanBackgroundCount(i34);
                long wifiScanActualTime = (uid2.getWifiScanActualTime(j19) + 500) / 1000;
                long wifiScanBackgroundTime = (uid2.getWifiScanBackgroundTime(j19) + 500) / 1000;
                long wifiRunningTime = uid2.getWifiRunningTime(j19, i34);
                if (fullWifiLockTime != 0 || wifiScanTime != 0 || wifiScanCount != 0 || wifiScanBackgroundCount != 0 || wifiScanActualTime != 0 || wifiScanBackgroundTime != 0 || wifiRunningTime != 0) {
                    dumpLine(printWriter, iKeyAt2, str, WIFI_DATA, Long.valueOf(fullWifiLockTime), Long.valueOf(wifiScanTime), Long.valueOf(wifiRunningTime), Integer.valueOf(wifiScanCount), 0, 0, 0, Integer.valueOf(wifiScanBackgroundCount), Long.valueOf(wifiScanActualTime), Long.valueOf(wifiScanBackgroundTime));
                }
                StringBuilder sb12 = sb10;
                dumpControllerActivityLine(printWriter, iKeyAt2, str, WIFI_CONTROLLER_DATA, uid2.getWifiControllerActivity(), i);
                Timer bluetoothScanTimer = uid2.getBluetoothScanTimer();
                if (bluetoothScanTimer != null) {
                    long totalTimeLocked8 = (bluetoothScanTimer.getTotalTimeLocked(j19, i34) + 500) / 1000;
                    if (totalTimeLocked8 != 0) {
                        int countLocked7 = bluetoothScanTimer.getCountLocked(i34);
                        Timer bluetoothScanBackgroundTimer = uid2.getBluetoothScanBackgroundTimer();
                        int countLocked8 = bluetoothScanBackgroundTimer != null ? bluetoothScanBackgroundTimer.getCountLocked(i34) : 0;
                        long totalDurationMsLocked4 = bluetoothScanTimer.getTotalDurationMsLocked(j15);
                        long totalDurationMsLocked5 = bluetoothScanBackgroundTimer != null ? bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j15) : 0L;
                        int countLocked9 = uid2.getBluetoothScanResultCounter() != null ? uid2.getBluetoothScanResultCounter().getCountLocked(i34) : 0;
                        int countLocked10 = uid2.getBluetoothScanResultBgCounter() != null ? uid2.getBluetoothScanResultBgCounter().getCountLocked(i34) : 0;
                        Timer bluetoothUnoptimizedScanTimer = uid2.getBluetoothUnoptimizedScanTimer();
                        long totalDurationMsLocked6 = bluetoothUnoptimizedScanTimer != null ? bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j15) : 0L;
                        long maxDurationMsLocked = bluetoothUnoptimizedScanTimer != null ? bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j15) : 0L;
                        Timer bluetoothUnoptimizedScanBackgroundTimer = uid2.getBluetoothUnoptimizedScanBackgroundTimer();
                        dumpLine(printWriter, iKeyAt2, str, BLUETOOTH_MISC_DATA, Long.valueOf(totalTimeLocked8), Integer.valueOf(countLocked7), Integer.valueOf(countLocked8), Long.valueOf(totalDurationMsLocked4), Long.valueOf(totalDurationMsLocked5), Integer.valueOf(countLocked9), Integer.valueOf(countLocked10), Long.valueOf(totalDurationMsLocked6), Long.valueOf(bluetoothUnoptimizedScanBackgroundTimer != null ? bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j15) : 0L), Long.valueOf(maxDurationMsLocked), Long.valueOf(bluetoothUnoptimizedScanBackgroundTimer != null ? bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j15) : 0L));
                    } else {
                        i33 = i33;
                    }
                } else {
                    i33 = i33;
                }
                long j20 = j15;
                dumpControllerActivityLine(printWriter, iKeyAt2, str, BLUETOOTH_CONTROLLER_DATA, uid2.getBluetoothControllerActivity(), i);
                if (uid2.hasUserActivity()) {
                    Object[] objArr9 = new Object[4];
                    int i35 = 0;
                    boolean z2 = false;
                    for (int i36 = 4; i35 < i36; i36 = 4) {
                        int userActivityCount = uid2.getUserActivityCount(i35, i34);
                        objArr9[i35] = Integer.valueOf(userActivityCount);
                        if (userActivityCount != 0) {
                            z2 = true;
                        }
                        i35++;
                    }
                    if (z2) {
                        dumpLine(printWriter, iKeyAt2, str, USER_ACTIVITY_DATA, objArr9);
                    }
                }
                if (uid2.getAggregatedPartialWakelockTimer() != null) {
                    Timer aggregatedPartialWakelockTimer = uid2.getAggregatedPartialWakelockTimer();
                    long totalDurationMsLocked7 = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j20);
                    Timer subTimer3 = aggregatedPartialWakelockTimer.getSubTimer();
                    i3 = 1;
                    dumpLine(printWriter, iKeyAt2, str, AGGREGATED_WAKELOCK_DATA, Long.valueOf(totalDurationMsLocked7), Long.valueOf(subTimer3 != null ? subTimer3.getTotalDurationMsLocked(j20) : 0L));
                } else {
                    i3 = 1;
                }
                ArrayMap<String, ? extends Uid.Wakelock> wakelockStats2 = uid2.getWakelockStats();
                int size11 = wakelockStats2.size() - i3;
                while (size11 >= 0) {
                    Uid.Wakelock wakelockValueAt2 = wakelockStats2.valueAt(size11);
                    StringBuilder sb13 = sb12;
                    sb13.setLength(0);
                    long[] jArr3 = cpuFreqs;
                    long j21 = j20;
                    int i37 = size11;
                    String strPrintWakeLockCheckin = printWakeLockCheckin(sb13, wakelockValueAt2.getWakeTime(i3), j19, FullBackup.FILES_TREE_TOKEN, i, "");
                    Timer wakeTime3 = wakelockValueAt2.getWakeTime(0);
                    printWakeLockCheckin(sb13, wakelockValueAt2.getWakeTime(2), j19, "w", i, printWakeLockCheckin(sb13, wakeTime3 != null ? wakeTime3.getSubTimer() : null, j19, "bp", i, printWakeLockCheckin(sb13, wakeTime3, j19, TtmlUtils.TAG_P, i, strPrintWakeLockCheckin)));
                    if (sb13.length() > 0) {
                        String strKeyAt = wakelockStats2.keyAt(i37);
                        if (strKeyAt.indexOf(44) >= 0) {
                            strKeyAt = strKeyAt.replace(',', '_');
                        }
                        if (strKeyAt.indexOf(10) >= 0) {
                            strKeyAt = strKeyAt.replace('\n', '_');
                        }
                        if (strKeyAt.indexOf(13) >= 0) {
                            strKeyAt = strKeyAt.replace('\r', '_');
                        }
                        dumpLine(printWriter, iKeyAt2, str, WAKELOCK_DATA, strKeyAt, sb13.toString());
                    }
                    size11 = i37 - 1;
                    cpuFreqs = jArr3;
                    sb12 = sb13;
                    j20 = j21;
                    i3 = 1;
                }
                long[] jArr4 = cpuFreqs;
                long j22 = j20;
                StringBuilder sb14 = sb12;
                Timer multicastWakelockStats = uid2.getMulticastWakelockStats();
                if (multicastWakelockStats != null) {
                    j = j19;
                    long totalTimeLocked9 = multicastWakelockStats.getTotalTimeLocked(j, i34) / 1000;
                    int countLocked11 = multicastWakelockStats.getCountLocked(i34);
                    if (totalTimeLocked9 > 0) {
                        i4 = 1;
                        dumpLine(printWriter, iKeyAt2, str, WIFI_MULTICAST_DATA, Long.valueOf(totalTimeLocked9), Integer.valueOf(countLocked11));
                    }
                    syncStats = uid2.getSyncStats();
                    size = syncStats.size() - i4;
                    while (size >= 0) {
                        Timer timerValueAt = syncStats.valueAt(size);
                        totalTimeLocked3 = (timerValueAt.getTotalTimeLocked(j, i34) + 500) / 1000;
                        countLocked4 = timerValueAt.getCountLocked(i34);
                        subTimer2 = timerValueAt.getSubTimer();
                        if (subTimer2 != null) {
                            j13 = j22;
                            totalDurationMsLocked3 = subTimer2.getTotalDurationMsLocked(j13);
                        } else {
                            j13 = j22;
                            totalDurationMsLocked3 = -1;
                        }
                        if (subTimer2 != null) {
                            countLocked5 = subTimer2.getCountLocked(i);
                        } else {
                            countLocked5 = -1;
                        }
                        if (totalTimeLocked3 != 0) {
                            dumpLine(printWriter, iKeyAt2, str, SYNC_DATA, "\"" + syncStats.keyAt(size) + "\"", Long.valueOf(totalTimeLocked3), Integer.valueOf(countLocked4), Long.valueOf(totalDurationMsLocked3), Integer.valueOf(countLocked5));
                        }
                        size--;
                        j22 = j13;
                        i34 = i;
                    }
                    j2 = j22;
                    jobStats = uid2.getJobStats();
                    size2 = jobStats.size() - 1;
                    while (size2 >= 0) {
                        Timer timerValueAt2 = jobStats.valueAt(size2);
                        totalTimeLocked2 = (timerValueAt2.getTotalTimeLocked(j, i) + 500) / 1000;
                        countLocked2 = timerValueAt2.getCountLocked(i);
                        subTimer = timerValueAt2.getSubTimer();
                        if (subTimer != null) {
                            j12 = j2;
                            totalDurationMsLocked2 = subTimer.getTotalDurationMsLocked(j12);
                        } else {
                            j12 = j2;
                            totalDurationMsLocked2 = -1;
                        }
                        if (subTimer != null) {
                            countLocked3 = subTimer.getCountLocked(i);
                        } else {
                            countLocked3 = -1;
                        }
                        if (totalTimeLocked2 != 0) {
                            dumpLine(printWriter, iKeyAt2, str, JOB_DATA, "\"" + jobStats.keyAt(size2) + "\"", Long.valueOf(totalTimeLocked2), Integer.valueOf(countLocked2), Long.valueOf(totalDurationMsLocked2), Integer.valueOf(countLocked3));
                        }
                        size2--;
                        j = j;
                        j2 = j12;
                    }
                    j3 = j;
                    j4 = j2;
                    i5 = i;
                    jobCompletionStats = uid2.getJobCompletionStats();
                    for (size3 = jobCompletionStats.size() - 1; size3 >= 0; size3--) {
                        sparseIntArrayValueAt = jobCompletionStats.valueAt(size3);
                        if (sparseIntArrayValueAt != null) {
                            dumpLine(printWriter, iKeyAt2, str, JOB_COMPLETION_DATA, "\"" + jobCompletionStats.keyAt(size3) + "\"", Integer.valueOf(sparseIntArrayValueAt.get(0, 0)), Integer.valueOf(sparseIntArrayValueAt.get(1, 0)), Integer.valueOf(sparseIntArrayValueAt.get(2, 0)), Integer.valueOf(sparseIntArrayValueAt.get(3, 0)), Integer.valueOf(sparseIntArrayValueAt.get(4, 0)));
                        }
                    }
                    sb2 = sb14;
                    uid2.getDeferredJobsCheckinLineLocked(sb2, i5);
                    if (sb2.length() > 0) {
                        dumpLine(printWriter, iKeyAt2, str, JOBS_DEFERRED_DATA, sb2.toString());
                    }
                    String str7 = str;
                    dumpTimer(printWriter, iKeyAt2, str7, FLASHLIGHT_DATA, uid2.getFlashlightTurnedOnTimer(), j3, i);
                    dumpTimer(printWriter, iKeyAt2, str7, CAMERA_DATA, uid2.getCameraTurnedOnTimer(), j3, i);
                    dumpTimer(printWriter, iKeyAt2, str7, VIDEO_DATA, uid2.getVideoTurnedOnTimer(), j3, i);
                    dumpTimer(printWriter, iKeyAt2, str7, AUDIO_DATA, uid2.getAudioTurnedOnTimer(), j3, i);
                    sensorStats = uid2.getSensorStats();
                    size4 = sensorStats.size();
                    i6 = 0;
                    while (i6 < size4) {
                        sensorValueAt = sensorStats.valueAt(i6);
                        iKeyAt = sensorStats.keyAt(i6);
                        sensorTime = sensorValueAt.getSensorTime();
                        if (sensorTime != null) {
                            j11 = j3;
                            totalTimeLocked = (sensorTime.getTotalTimeLocked(j11, i5) + 500) / 1000;
                            if (totalTimeLocked != 0) {
                                int countLocked12 = sensorTime.getCountLocked(i5);
                                sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                if (sensorBackgroundTime != null) {
                                    countLocked = sensorBackgroundTime.getCountLocked(i5);
                                } else {
                                    countLocked = 0;
                                }
                                long totalDurationMsLocked8 = sensorTime.getTotalDurationMsLocked(j4);
                                if (sensorBackgroundTime != null) {
                                    totalDurationMsLocked = sensorBackgroundTime.getTotalDurationMsLocked(j4);
                                } else {
                                    totalDurationMsLocked = 0;
                                }
                                sensorStats = sensorStats;
                                dumpLine(printWriter, iKeyAt2, str, SENSOR_DATA, Integer.valueOf(iKeyAt), Long.valueOf(totalTimeLocked), Integer.valueOf(countLocked12), Integer.valueOf(countLocked), Long.valueOf(totalDurationMsLocked8), Long.valueOf(totalDurationMsLocked));
                            }
                            i6++;
                            j3 = j11;
                            j4 = j4;
                            sb2 = sb2;
                            sensorStats = sensorStats;
                            i5 = i;
                        } else {
                            j11 = j3;
                        }
                        j4 = j4;
                        i6++;
                        j3 = j11;
                        j4 = j4;
                        sb2 = sb2;
                        sensorStats = sensorStats;
                        i5 = i;
                    }
                    sb3 = sb2;
                    j5 = j3;
                    j6 = j4;
                    String str8 = str;
                    dumpTimer(printWriter, iKeyAt2, str8, VIBRATOR_DATA, uid2.getVibratorOnTimer(), j5, i);
                    dumpTimer(printWriter, iKeyAt2, str8, FOREGROUND_ACTIVITY_DATA, uid2.getForegroundActivityTimer(), j5, i);
                    dumpTimer(printWriter, iKeyAt2, str8, FOREGROUND_SERVICE_DATA, uid2.getForegroundServiceTimer(), j5, i);
                    i7 = 7;
                    objArr = new Object[7];
                    j7 = 0;
                    i8 = 0;
                    while (i8 < i7) {
                        long processStateTime = uid2.getProcessStateTime(i8, j5, i);
                        j7 += processStateTime;
                        objArr[i8] = Long.valueOf((processStateTime + 500) / 1000);
                        i8++;
                        i7 = 7;
                        j6 = j6;
                    }
                    j8 = j6;
                    j9 = j5;
                    i9 = i;
                    if (j7 > 0) {
                        dumpLine(printWriter, iKeyAt2, str, "st", objArr);
                    }
                    userCpuTimeUs = uid2.getUserCpuTimeUs(i9);
                    systemCpuTimeUs = uid2.getSystemCpuTimeUs(i9);
                    if (userCpuTimeUs <= 0 || systemCpuTimeUs > 0) {
                        i10 = 0;
                        dumpLine(printWriter, iKeyAt2, str, CPU_DATA, Long.valueOf(userCpuTimeUs / 1000), Long.valueOf(systemCpuTimeUs / 1000), 0);
                    } else {
                        i10 = 0;
                    }
                    if (jArr4 != null) {
                        cpuFreqTimes = uid2.getCpuFreqTimes(i9);
                        if (cpuFreqTimes != null) {
                            jArr = jArr4;
                            if (cpuFreqTimes.length == jArr.length) {
                                sb4 = sb3;
                                sb4.setLength(i10);
                                i14 = 0;
                                while (i14 < cpuFreqTimes.length) {
                                    StringBuilder sb15 = new StringBuilder();
                                    if (i14 == 0) {
                                        str2 = "";
                                    } else {
                                        str2 = ",";
                                    }
                                    sb15.append(str2);
                                    sb15.append(cpuFreqTimes[i14]);
                                    sb4.append(sb15.toString());
                                    i14++;
                                    j8 = j8;
                                }
                                j8 = j8;
                                screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i9);
                                if (screenOffCpuFreqTimes != null) {
                                    i16 = 0;
                                    while (i16 < screenOffCpuFreqTimes.length) {
                                        sb4.append("," + screenOffCpuFreqTimes[i16]);
                                        i16++;
                                        j9 = j9;
                                    }
                                    j9 = j9;
                                } else {
                                    j9 = j9;
                                    for (i15 = 0; i15 < cpuFreqTimes.length; i15++) {
                                        sb4.append(",0");
                                    }
                                }
                                dumpLine(printWriter, iKeyAt2, str, CPU_TIMES_AT_FREQ_DATA, "A", Integer.valueOf(cpuFreqTimes.length), sb4.toString());
                            }
                            for (i13 = 0; i13 < 7; i13++) {
                                cpuFreqTimes2 = uid2.getCpuFreqTimes(i9, i13);
                                if (cpuFreqTimes2 == null && cpuFreqTimes2.length == jArr.length) {
                                    sb4.setLength(0);
                                    int i38 = 0;
                                    while (i38 < cpuFreqTimes2.length) {
                                        StringBuilder sb16 = new StringBuilder();
                                        sb16.append(i38 == 0 ? "" : ",");
                                        sb16.append(cpuFreqTimes2[i38]);
                                        sb4.append(sb16.toString());
                                        i38++;
                                    }
                                    long[] screenOffCpuFreqTimes2 = uid2.getScreenOffCpuFreqTimes(i9, i13);
                                    if (screenOffCpuFreqTimes2 != null) {
                                        for (long j23 : screenOffCpuFreqTimes2) {
                                            sb4.append("," + j23);
                                        }
                                    } else {
                                        for (int i39 = 0; i39 < cpuFreqTimes2.length; i39++) {
                                            sb4.append(",0");
                                        }
                                    }
                                    dumpLine(printWriter, iKeyAt2, str, CPU_TIMES_AT_FREQ_DATA, Uid.UID_PROCESS_TYPES[i13], Integer.valueOf(cpuFreqTimes2.length), sb4.toString());
                                }
                            }
                        } else {
                            jArr = jArr4;
                        }
                        sb4 = sb3;
                        while (i13 < 7) {
                            cpuFreqTimes2 = uid2.getCpuFreqTimes(i9, i13);
                            if (cpuFreqTimes2 == null) {
                            }
                        }
                    } else {
                        j8 = j8;
                        j9 = j9;
                        jArr = jArr4;
                        sb4 = sb3;
                    }
                    processStats = uid2.getProcessStats();
                    size5 = processStats.size() - 1;
                    while (size5 >= 0) {
                        Uid.Proc procValueAt = processStats.valueAt(size5);
                        userTime = procValueAt.getUserTime(i9);
                        systemTime = procValueAt.getSystemTime(i9);
                        foregroundTime = procValueAt.getForegroundTime(i9);
                        starts2 = procValueAt.getStarts(i9);
                        numCrashes = procValueAt.getNumCrashes(i9);
                        numAnrs = procValueAt.getNumAnrs(i9);
                        if (userTime == 0 || systemTime != 0 || foregroundTime != 0 || starts2 != 0 || numAnrs != 0 || numCrashes != 0) {
                            dumpLine(printWriter, iKeyAt2, str, PROCESS_DATA, "\"" + processStats.keyAt(size5) + "\"", Long.valueOf(userTime), Long.valueOf(systemTime), Long.valueOf(foregroundTime), Integer.valueOf(starts2), Integer.valueOf(numAnrs), Integer.valueOf(numCrashes));
                        }
                        size5--;
                        sb4 = sb4;
                        jArr = jArr;
                        uid2 = uid2;
                        i9 = i;
                    }
                    sb5 = sb4;
                    jArr2 = jArr;
                    packageStats = uid2.getPackageStats();
                    i11 = 1;
                    size6 = packageStats.size() - 1;
                    while (size6 >= 0) {
                        Uid.Pkg pkgValueAt = packageStats.valueAt(size6);
                        wakeupAlarmStats = pkgValueAt.getWakeupAlarmStats();
                        i12 = 0;
                        for (size7 = wakeupAlarmStats.size() - i11; size7 >= 0; size7--) {
                            int countLocked13 = wakeupAlarmStats.valueAt(size7).getCountLocked(i);
                            i12 += countLocked13;
                            dumpLine(printWriter, iKeyAt2, str, WAKEUP_ALARM_DATA, wakeupAlarmStats.keyAt(size7).replace(',', '_'), Integer.valueOf(countLocked13));
                        }
                        serviceStats = pkgValueAt.getServiceStats();
                        size8 = serviceStats.size() - 1;
                        while (size8 >= 0) {
                            Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size8);
                            long j24 = j16;
                            startTime = servValueAt.getStartTime(j24, i);
                            starts = servValueAt.getStarts(i);
                            launches = servValueAt.getLaunches(i);
                            if (startTime == 0 || starts != 0 || launches != 0) {
                                dumpLine(printWriter, iKeyAt2, str, APK_DATA, Integer.valueOf(i12), packageStats.keyAt(size6), serviceStats.keyAt(size8), Long.valueOf(startTime / 1000), Integer.valueOf(starts), Integer.valueOf(launches));
                            }
                            size8--;
                            j16 = j24;
                        }
                        size6--;
                        i11 = 1;
                    }
                    j10 = j16;
                } else {
                    j = j19;
                }
                i4 = 1;
                syncStats = uid2.getSyncStats();
                size = syncStats.size() - i4;
                while (size >= 0) {
                    Timer timerValueAt3 = syncStats.valueAt(size);
                    totalTimeLocked3 = (timerValueAt3.getTotalTimeLocked(j, i34) + 500) / 1000;
                    countLocked4 = timerValueAt3.getCountLocked(i34);
                    subTimer2 = timerValueAt3.getSubTimer();
                    if (subTimer2 != null) {
                        j13 = j22;
                        totalDurationMsLocked3 = subTimer2.getTotalDurationMsLocked(j13);
                    } else {
                        j13 = j22;
                        totalDurationMsLocked3 = -1;
                    }
                    if (subTimer2 != null) {
                        countLocked5 = subTimer2.getCountLocked(i);
                    } else {
                        countLocked5 = -1;
                    }
                    if (totalTimeLocked3 != 0) {
                        dumpLine(printWriter, iKeyAt2, str, SYNC_DATA, "\"" + syncStats.keyAt(size) + "\"", Long.valueOf(totalTimeLocked3), Integer.valueOf(countLocked4), Long.valueOf(totalDurationMsLocked3), Integer.valueOf(countLocked5));
                    }
                    size--;
                    j22 = j13;
                    i34 = i;
                }
                j2 = j22;
                jobStats = uid2.getJobStats();
                size2 = jobStats.size() - 1;
                while (size2 >= 0) {
                    Timer timerValueAt4 = jobStats.valueAt(size2);
                    totalTimeLocked2 = (timerValueAt4.getTotalTimeLocked(j, i) + 500) / 1000;
                    countLocked2 = timerValueAt4.getCountLocked(i);
                    subTimer = timerValueAt4.getSubTimer();
                    if (subTimer != null) {
                        j12 = j2;
                        totalDurationMsLocked2 = subTimer.getTotalDurationMsLocked(j12);
                    } else {
                        j12 = j2;
                        totalDurationMsLocked2 = -1;
                    }
                    if (subTimer != null) {
                        countLocked3 = subTimer.getCountLocked(i);
                    } else {
                        countLocked3 = -1;
                    }
                    if (totalTimeLocked2 != 0) {
                        dumpLine(printWriter, iKeyAt2, str, JOB_DATA, "\"" + jobStats.keyAt(size2) + "\"", Long.valueOf(totalTimeLocked2), Integer.valueOf(countLocked2), Long.valueOf(totalDurationMsLocked2), Integer.valueOf(countLocked3));
                    }
                    size2--;
                    j = j;
                    j2 = j12;
                }
                j3 = j;
                j4 = j2;
                i5 = i;
                jobCompletionStats = uid2.getJobCompletionStats();
                while (size3 >= 0) {
                    sparseIntArrayValueAt = jobCompletionStats.valueAt(size3);
                    if (sparseIntArrayValueAt != null) {
                        dumpLine(printWriter, iKeyAt2, str, JOB_COMPLETION_DATA, "\"" + jobCompletionStats.keyAt(size3) + "\"", Integer.valueOf(sparseIntArrayValueAt.get(0, 0)), Integer.valueOf(sparseIntArrayValueAt.get(1, 0)), Integer.valueOf(sparseIntArrayValueAt.get(2, 0)), Integer.valueOf(sparseIntArrayValueAt.get(3, 0)), Integer.valueOf(sparseIntArrayValueAt.get(4, 0)));
                    }
                }
                sb2 = sb14;
                uid2.getDeferredJobsCheckinLineLocked(sb2, i5);
                if (sb2.length() > 0) {
                    dumpLine(printWriter, iKeyAt2, str, JOBS_DEFERRED_DATA, sb2.toString());
                }
                String str9 = str;
                dumpTimer(printWriter, iKeyAt2, str9, FLASHLIGHT_DATA, uid2.getFlashlightTurnedOnTimer(), j3, i);
                dumpTimer(printWriter, iKeyAt2, str9, CAMERA_DATA, uid2.getCameraTurnedOnTimer(), j3, i);
                dumpTimer(printWriter, iKeyAt2, str9, VIDEO_DATA, uid2.getVideoTurnedOnTimer(), j3, i);
                dumpTimer(printWriter, iKeyAt2, str9, AUDIO_DATA, uid2.getAudioTurnedOnTimer(), j3, i);
                sensorStats = uid2.getSensorStats();
                size4 = sensorStats.size();
                i6 = 0;
                while (i6 < size4) {
                    sensorValueAt = sensorStats.valueAt(i6);
                    iKeyAt = sensorStats.keyAt(i6);
                    sensorTime = sensorValueAt.getSensorTime();
                    if (sensorTime != null) {
                        j11 = j3;
                        totalTimeLocked = (sensorTime.getTotalTimeLocked(j11, i5) + 500) / 1000;
                        if (totalTimeLocked != 0) {
                            int countLocked14 = sensorTime.getCountLocked(i5);
                            sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            if (sensorBackgroundTime != null) {
                                countLocked = sensorBackgroundTime.getCountLocked(i5);
                            } else {
                                countLocked = 0;
                            }
                            long totalDurationMsLocked9 = sensorTime.getTotalDurationMsLocked(j4);
                            if (sensorBackgroundTime != null) {
                                totalDurationMsLocked = sensorBackgroundTime.getTotalDurationMsLocked(j4);
                            } else {
                                totalDurationMsLocked = 0;
                            }
                            sensorStats = sensorStats;
                            dumpLine(printWriter, iKeyAt2, str, SENSOR_DATA, Integer.valueOf(iKeyAt), Long.valueOf(totalTimeLocked), Integer.valueOf(countLocked14), Integer.valueOf(countLocked), Long.valueOf(totalDurationMsLocked9), Long.valueOf(totalDurationMsLocked));
                        }
                        i6++;
                        j3 = j11;
                        j4 = j4;
                        sb2 = sb2;
                        sensorStats = sensorStats;
                        i5 = i;
                    } else {
                        j11 = j3;
                    }
                    j4 = j4;
                    i6++;
                    j3 = j11;
                    j4 = j4;
                    sb2 = sb2;
                    sensorStats = sensorStats;
                    i5 = i;
                }
                sb3 = sb2;
                j5 = j3;
                j6 = j4;
                String str10 = str;
                dumpTimer(printWriter, iKeyAt2, str10, VIBRATOR_DATA, uid2.getVibratorOnTimer(), j5, i);
                dumpTimer(printWriter, iKeyAt2, str10, FOREGROUND_ACTIVITY_DATA, uid2.getForegroundActivityTimer(), j5, i);
                dumpTimer(printWriter, iKeyAt2, str10, FOREGROUND_SERVICE_DATA, uid2.getForegroundServiceTimer(), j5, i);
                i7 = 7;
                objArr = new Object[7];
                j7 = 0;
                i8 = 0;
                while (i8 < i7) {
                    long processStateTime2 = uid2.getProcessStateTime(i8, j5, i);
                    j7 += processStateTime2;
                    objArr[i8] = Long.valueOf((processStateTime2 + 500) / 1000);
                    i8++;
                    i7 = 7;
                    j6 = j6;
                }
                j8 = j6;
                j9 = j5;
                i9 = i;
                if (j7 > 0) {
                    dumpLine(printWriter, iKeyAt2, str, "st", objArr);
                }
                userCpuTimeUs = uid2.getUserCpuTimeUs(i9);
                systemCpuTimeUs = uid2.getSystemCpuTimeUs(i9);
                if (userCpuTimeUs <= 0) {
                    i10 = 0;
                    dumpLine(printWriter, iKeyAt2, str, CPU_DATA, Long.valueOf(userCpuTimeUs / 1000), Long.valueOf(systemCpuTimeUs / 1000), 0);
                } else {
                    i10 = 0;
                    dumpLine(printWriter, iKeyAt2, str, CPU_DATA, Long.valueOf(userCpuTimeUs / 1000), Long.valueOf(systemCpuTimeUs / 1000), 0);
                }
                if (jArr4 != null) {
                    cpuFreqTimes = uid2.getCpuFreqTimes(i9);
                    if (cpuFreqTimes != null) {
                        jArr = jArr4;
                        if (cpuFreqTimes.length == jArr.length) {
                            sb4 = sb3;
                            sb4.setLength(i10);
                            i14 = 0;
                            while (i14 < cpuFreqTimes.length) {
                                StringBuilder sb17 = new StringBuilder();
                                if (i14 == 0) {
                                    str2 = "";
                                } else {
                                    str2 = ",";
                                }
                                sb17.append(str2);
                                sb17.append(cpuFreqTimes[i14]);
                                sb4.append(sb17.toString());
                                i14++;
                                j8 = j8;
                            }
                            j8 = j8;
                            screenOffCpuFreqTimes = uid2.getScreenOffCpuFreqTimes(i9);
                            if (screenOffCpuFreqTimes != null) {
                                i16 = 0;
                                while (i16 < screenOffCpuFreqTimes.length) {
                                    sb4.append("," + screenOffCpuFreqTimes[i16]);
                                    i16++;
                                    j9 = j9;
                                }
                                j9 = j9;
                            } else {
                                j9 = j9;
                                while (i15 < cpuFreqTimes.length) {
                                    sb4.append(",0");
                                }
                            }
                            dumpLine(printWriter, iKeyAt2, str, CPU_TIMES_AT_FREQ_DATA, "A", Integer.valueOf(cpuFreqTimes.length), sb4.toString());
                        }
                        while (i13 < 7) {
                            cpuFreqTimes2 = uid2.getCpuFreqTimes(i9, i13);
                            if (cpuFreqTimes2 == null) {
                            }
                        }
                    } else {
                        jArr = jArr4;
                    }
                    sb4 = sb3;
                    while (i13 < 7) {
                        cpuFreqTimes2 = uid2.getCpuFreqTimes(i9, i13);
                        if (cpuFreqTimes2 == null) {
                        }
                    }
                } else {
                    j8 = j8;
                    j9 = j9;
                    jArr = jArr4;
                    sb4 = sb3;
                }
                processStats = uid2.getProcessStats();
                size5 = processStats.size() - 1;
                while (size5 >= 0) {
                    Uid.Proc procValueAt2 = processStats.valueAt(size5);
                    userTime = procValueAt2.getUserTime(i9);
                    systemTime = procValueAt2.getSystemTime(i9);
                    foregroundTime = procValueAt2.getForegroundTime(i9);
                    starts2 = procValueAt2.getStarts(i9);
                    numCrashes = procValueAt2.getNumCrashes(i9);
                    numAnrs = procValueAt2.getNumAnrs(i9);
                    if (userTime == 0) {
                        dumpLine(printWriter, iKeyAt2, str, PROCESS_DATA, "\"" + processStats.keyAt(size5) + "\"", Long.valueOf(userTime), Long.valueOf(systemTime), Long.valueOf(foregroundTime), Integer.valueOf(starts2), Integer.valueOf(numAnrs), Integer.valueOf(numCrashes));
                    } else {
                        dumpLine(printWriter, iKeyAt2, str, PROCESS_DATA, "\"" + processStats.keyAt(size5) + "\"", Long.valueOf(userTime), Long.valueOf(systemTime), Long.valueOf(foregroundTime), Integer.valueOf(starts2), Integer.valueOf(numAnrs), Integer.valueOf(numCrashes));
                    }
                    size5--;
                    sb4 = sb4;
                    jArr = jArr;
                    uid2 = uid2;
                    i9 = i;
                }
                sb5 = sb4;
                jArr2 = jArr;
                packageStats = uid2.getPackageStats();
                i11 = 1;
                size6 = packageStats.size() - 1;
                while (size6 >= 0) {
                    Uid.Pkg pkgValueAt2 = packageStats.valueAt(size6);
                    wakeupAlarmStats = pkgValueAt2.getWakeupAlarmStats();
                    i12 = 0;
                    while (size7 >= 0) {
                        int countLocked15 = wakeupAlarmStats.valueAt(size7).getCountLocked(i);
                        i12 += countLocked15;
                        dumpLine(printWriter, iKeyAt2, str, WAKEUP_ALARM_DATA, wakeupAlarmStats.keyAt(size7).replace(',', '_'), Integer.valueOf(countLocked15));
                    }
                    serviceStats = pkgValueAt2.getServiceStats();
                    size8 = serviceStats.size() - 1;
                    while (size8 >= 0) {
                        Uid.Pkg.Serv servValueAt2 = serviceStats.valueAt(size8);
                        long j25 = j16;
                        startTime = servValueAt2.getStartTime(j25, i);
                        starts = servValueAt2.getStarts(i);
                        launches = servValueAt2.getLaunches(i);
                        if (startTime == 0) {
                            dumpLine(printWriter, iKeyAt2, str, APK_DATA, Integer.valueOf(i12), packageStats.keyAt(size6), serviceStats.keyAt(size8), Long.valueOf(startTime / 1000), Integer.valueOf(starts), Integer.valueOf(launches));
                        } else {
                            dumpLine(printWriter, iKeyAt2, str, APK_DATA, Integer.valueOf(i12), packageStats.keyAt(size6), serviceStats.keyAt(size8), Long.valueOf(startTime / 1000), Integer.valueOf(starts), Integer.valueOf(launches));
                        }
                        size8--;
                        j16 = j25;
                    }
                    size6--;
                    i11 = 1;
                }
                j10 = j16;
            } else {
                jArr2 = cpuFreqs;
                sb5 = sb10;
                i33 = i33;
                j8 = j15;
                j10 = j16;
                j9 = j18;
            }
            i33++;
            i32 = i32;
            sparseArray = sparseArray;
            j15 = j8;
            j18 = j9;
            sb10 = sb5;
            cpuFreqs = jArr2;
            j16 = j10;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void dumpCheckinLocked(Context context, PrintWriter printWriter, List<ApplicationInfo> list, int i, long j) {
        prepareForDumpLocked();
        dumpLine(printWriter, 0, "i", VERSION_DATA, 32, Integer.valueOf(getParcelVersion()), getStartPlatformVersion(), getEndPlatformVersion());
        getHistoryBaseTime();
        SystemClock.elapsedRealtime();
        if ((i & 24) != 0 && startIteratingHistoryLocked()) {
            for (int i2 = 0; i2 < getHistoryStringPoolSize(); i2++) {
                try {
                    printWriter.print(9);
                    printWriter.print(',');
                    printWriter.print(HISTORY_STRING_POOL);
                    printWriter.print(',');
                    printWriter.print(i2);
                    printWriter.print(",");
                    printWriter.print(getHistoryTagPoolUid(i2));
                    printWriter.print(",\"");
                    printWriter.print(getHistoryTagPoolString(i2).replace("\\", "\\\\").replace("\"", "\\\""));
                    printWriter.print("\"");
                    printWriter.println();
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            dumpHistoryLocked(printWriter, i, j, true);
            finishIteratingHistoryLocked();
        }
        if ((i & 8) != 0) {
            return;
        }
        if (list != null) {
            SparseArray sparseArray = new SparseArray();
            for (int i3 = 0; i3 < list.size(); i3++) {
                ApplicationInfo applicationInfo = list.get(i3);
                Pair pair = (Pair) sparseArray.get(UserHandle.getAppId(applicationInfo.uid));
                if (pair == null) {
                    pair = new Pair(new ArrayList(), new MutableBoolean(false));
                    sparseArray.put(UserHandle.getAppId(applicationInfo.uid), pair);
                }
                ((ArrayList) pair.first).add(applicationInfo.packageName);
            }
            SparseArray<? extends Uid> uidStats = getUidStats();
            int size = uidStats.size();
            String[] strArr = new String[2];
            for (int i4 = 0; i4 < size; i4++) {
                int appId = UserHandle.getAppId(uidStats.keyAt(i4));
                Pair pair2 = (Pair) sparseArray.get(appId);
                if (pair2 != null && !((MutableBoolean) pair2.second).value) {
                    ((MutableBoolean) pair2.second).value = true;
                    for (int i5 = 0; i5 < ((ArrayList) pair2.first).size(); i5++) {
                        strArr[0] = Integer.toString(appId);
                        strArr[1] = (String) ((ArrayList) pair2.first).get(i5);
                        dumpLine(printWriter, 0, "i", "uid", strArr);
                    }
                }
            }
        }
        if ((i & 4) == 0) {
            dumpDurationSteps(printWriter, "", DISCHARGE_STEP_DATA, getDischargeLevelStepTracker(), true);
            String[] strArr2 = new String[1];
            long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (jComputeBatteryTimeRemaining >= 0) {
                strArr2[0] = Long.toString(jComputeBatteryTimeRemaining);
                dumpLine(printWriter, 0, "i", DISCHARGE_TIME_REMAIN_DATA, strArr2);
            }
            dumpDurationSteps(printWriter, "", CHARGE_STEP_DATA, getChargeLevelStepTracker(), true);
            long jComputeChargeTimeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
            if (jComputeChargeTimeRemaining >= 0) {
                strArr2[0] = Long.toString(jComputeChargeTimeRemaining);
                dumpLine(printWriter, 0, "i", CHARGE_TIME_REMAIN_DATA, strArr2);
            }
            dumpCheckinLocked(context, printWriter, 0, -1, (i & 64) != 0);
        }
    }

    public void dumpLocked(Context context, PrintWriter printWriter, int i, int i2, long j) {
        boolean z;
        prepareForDumpLocked();
        boolean z2 = (i & 14) != 0;
        if ((i & 8) != 0 || !z2) {
            long historyTotalSize = getHistoryTotalSize();
            long historyUsedSize = getHistoryUsedSize();
            if (startIteratingHistoryLocked()) {
                try {
                    printWriter.print("Battery History (");
                    printWriter.print((100 * historyUsedSize) / historyTotalSize);
                    printWriter.print("% used, ");
                    printSizeValue(printWriter, historyUsedSize);
                    printWriter.print(" used of ");
                    printSizeValue(printWriter, historyTotalSize);
                    printWriter.print(", ");
                    printWriter.print(getHistoryStringPoolSize());
                    printWriter.print(" strings using ");
                    printSizeValue(printWriter, getHistoryStringPoolBytes());
                    printWriter.println("):");
                    dumpHistoryLocked(printWriter, i, j, false);
                    printWriter.println();
                    finishIteratingHistoryLocked();
                } catch (Throwable th) {
                    finishIteratingHistoryLocked();
                    throw th;
                }
            }
            if (startIteratingOldHistoryLocked()) {
                try {
                    HistoryItem historyItem = new HistoryItem();
                    printWriter.println("Old battery History:");
                    HistoryPrinter historyPrinter = new HistoryPrinter();
                    long j2 = -1;
                    while (getNextOldHistoryLocked(historyItem)) {
                        if (j2 < 0) {
                            j2 = historyItem.time;
                        }
                        long j3 = j2;
                        historyPrinter.printNextItem(printWriter, historyItem, j3, false, (i & 32) != 0);
                        j2 = j3;
                    }
                    printWriter.println();
                    finishIteratingOldHistoryLocked();
                } catch (Throwable th2) {
                    finishIteratingOldHistoryLocked();
                    throw th2;
                }
            }
        }
        if (z2 && (i & 6) == 0) {
            return;
        }
        if (!z2) {
            SparseArray<? extends Uid> uidStats = getUidStats();
            int size = uidStats.size();
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            boolean z3 = false;
            for (int i3 = 0; i3 < size; i3++) {
                SparseArray<? extends Uid.Pid> pidStats = uidStats.valueAt(i3).getPidStats();
                if (pidStats != null) {
                    boolean z4 = z3;
                    for (int i4 = 0; i4 < pidStats.size(); i4++) {
                        Uid.Pid pidValueAt = pidStats.valueAt(i4);
                        if (!z4) {
                            printWriter.println("Per-PID Stats:");
                            z4 = true;
                        }
                        long j4 = pidValueAt.mWakeSumMs + (pidValueAt.mWakeNesting > 0 ? jElapsedRealtime - pidValueAt.mWakeStartMs : 0L);
                        printWriter.print("  PID ");
                        printWriter.print(pidStats.keyAt(i4));
                        printWriter.print(" wake time: ");
                        TimeUtils.formatDuration(j4, printWriter);
                        printWriter.println("");
                    }
                    z3 = z4;
                }
            }
            if (z3) {
                printWriter.println();
            }
        }
        if (!z2 || (i & 2) != 0) {
            if (dumpDurationSteps(printWriter, "  ", "Discharge step durations:", getDischargeLevelStepTracker(), false)) {
                long jComputeBatteryTimeRemaining = computeBatteryTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (jComputeBatteryTimeRemaining >= 0) {
                    printWriter.print("  Estimated discharge time remaining: ");
                    TimeUtils.formatDuration(jComputeBatteryTimeRemaining / 1000, printWriter);
                    printWriter.println();
                }
                LevelStepTracker dischargeLevelStepTracker = getDischargeLevelStepTracker();
                for (int i5 = 0; i5 < STEP_LEVEL_MODES_OF_INTEREST.length; i5++) {
                    dumpTimeEstimate(printWriter, "  Estimated ", STEP_LEVEL_MODE_LABELS[i5], " time: ", dischargeLevelStepTracker.computeTimeEstimate(STEP_LEVEL_MODES_OF_INTEREST[i5], STEP_LEVEL_MODE_VALUES[i5], null));
                }
                printWriter.println();
            }
            if (dumpDurationSteps(printWriter, "  ", "Charge step durations:", getChargeLevelStepTracker(), false)) {
                long jComputeChargeTimeRemaining = computeChargeTimeRemaining(SystemClock.elapsedRealtime() * 1000);
                if (jComputeChargeTimeRemaining >= 0) {
                    printWriter.print("  Estimated charge time remaining: ");
                    TimeUtils.formatDuration(jComputeChargeTimeRemaining / 1000, printWriter);
                    printWriter.println();
                }
                printWriter.println();
            }
        }
        if (z2 && (i & 4) == 0) {
            z = false;
        } else {
            printWriter.println("Daily stats:");
            printWriter.print("  Current start time: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getCurrentDailyStartTime()).toString());
            printWriter.print("  Next min deadline: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMinDailyDeadline()).toString());
            printWriter.print("  Next max deadline: ");
            printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getNextMaxDailyDeadline()).toString());
            StringBuilder sb = new StringBuilder(64);
            int[] iArr = new int[1];
            LevelStepTracker dailyDischargeLevelStepTracker = getDailyDischargeLevelStepTracker();
            LevelStepTracker dailyChargeLevelStepTracker = getDailyChargeLevelStepTracker();
            ArrayList<PackageChange> dailyPackageChanges = getDailyPackageChanges();
            if (dailyDischargeLevelStepTracker.mNumStepDurations > 0 || dailyChargeLevelStepTracker.mNumStepDurations > 0 || dailyPackageChanges != null) {
                if ((i & 4) == 0 && z2) {
                    printWriter.println("  Current daily steps:");
                    dumpDailyLevelStepSummary(printWriter, "    ", "Discharge", dailyDischargeLevelStepTracker, sb, iArr);
                    dumpDailyLevelStepSummary(printWriter, "    ", "Charge", dailyChargeLevelStepTracker, sb, iArr);
                } else {
                    if (dumpDurationSteps(printWriter, "    ", "  Current daily discharge step durations:", dailyDischargeLevelStepTracker, false)) {
                        dumpDailyLevelStepSummary(printWriter, "      ", "Discharge", dailyDischargeLevelStepTracker, sb, iArr);
                    }
                    if (dumpDurationSteps(printWriter, "    ", "  Current daily charge step durations:", dailyChargeLevelStepTracker, false)) {
                        dumpDailyLevelStepSummary(printWriter, "      ", "Charge", dailyChargeLevelStepTracker, sb, iArr);
                    }
                    dumpDailyPackageChanges(printWriter, "    ", dailyPackageChanges);
                }
            }
            int i6 = 0;
            while (true) {
                DailyItem dailyItemLocked = getDailyItemLocked(i6);
                if (dailyItemLocked == null) {
                    break;
                }
                int i7 = i6 + 1;
                int i8 = i & 4;
                if (i8 != 0) {
                    printWriter.println();
                }
                printWriter.print("  Daily from ");
                printWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dailyItemLocked.mStartTime).toString());
                printWriter.print(" to ");
                printWriter.print(DateFormat.format("yyyy-MM-dd-HH-mm-ss", dailyItemLocked.mEndTime).toString());
                printWriter.println(SettingsStringUtil.DELIMITER);
                if (i8 == 0 && z2) {
                    dumpDailyLevelStepSummary(printWriter, "    ", "Discharge", dailyItemLocked.mDischargeSteps, sb, iArr);
                    dumpDailyLevelStepSummary(printWriter, "    ", "Charge", dailyItemLocked.mChargeSteps, sb, iArr);
                } else {
                    if (dumpDurationSteps(printWriter, "      ", "    Discharge step durations:", dailyItemLocked.mDischargeSteps, false)) {
                        dumpDailyLevelStepSummary(printWriter, "        ", "Discharge", dailyItemLocked.mDischargeSteps, sb, iArr);
                    }
                    if (dumpDurationSteps(printWriter, "      ", "    Charge step durations:", dailyItemLocked.mChargeSteps, false)) {
                        dumpDailyLevelStepSummary(printWriter, "        ", "Charge", dailyItemLocked.mChargeSteps, sb, iArr);
                    }
                    dumpDailyPackageChanges(printWriter, "    ", dailyItemLocked.mPackageChanges);
                }
                i6 = i7;
            }
            z = false;
            printWriter.println();
        }
        if (z2 && (i & 2) == 0) {
            return;
        }
        printWriter.println("Statistics since last charge:");
        printWriter.println("  System starts: " + getStartCount() + ", currently on battery: " + getIsOnBattery());
        dumpLocked(context, printWriter, "", 0, i2, (i & 64) != 0 ? true : z);
        printWriter.println();
    }

    public final void dumpLocked(Context context, PrintWriter printWriter, String str, int i, int i2) {
        dumpLocked(context, printWriter, str, i, i2, BatteryStatsHelper.checkWifiOnly(context));
    }

    /* JADX WARN: Code duplicated, block: B:275:0x0f8c  */
    /* JADX WARN: Code duplicated, block: B:278:0x0fa0 A[LOOP:13: B:276:0x0f9a->B:278:0x0fa0, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:282:0x0fee  */
    /* JADX WARN: Code duplicated, block: B:285:0x1009 A[LOOP:14: B:283:0x1003->B:285:0x1009, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:289:0x103d A[LOOP:15: B:287:0x1037->B:289:0x103d, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:381:0x14aa  */
    /* JADX WARN: Code duplicated, block: B:384:0x14ec  */
    /* JADX WARN: Code duplicated, block: B:386:0x14f0  */
    /* JADX WARN: Code duplicated, block: B:389:0x151a  */
    /* JADX WARN: Code duplicated, block: B:391:0x1528  */
    /* JADX WARN: Code duplicated, block: B:393:0x1532  */
    /* JADX WARN: Code duplicated, block: B:394:0x153c  */
    /* JADX WARN: Code duplicated, block: B:397:0x1547  */
    /* JADX WARN: Code duplicated, block: B:398:0x1550  */
    /* JADX WARN: Code duplicated, block: B:401:0x155a  */
    /* JADX WARN: Code duplicated, block: B:402:0x1563  */
    /* JADX WARN: Code duplicated, block: B:405:0x156a  */
    /* JADX WARN: Code duplicated, block: B:406:0x1573  */
    /* JADX WARN: Code duplicated, block: B:409:0x157a  */
    /* JADX WARN: Code duplicated, block: B:410:0x1581  */
    /* JADX WARN: Code duplicated, block: B:412:0x1585  */
    /* JADX WARN: Code duplicated, block: B:413:0x158e  */
    /* JADX WARN: Code duplicated, block: B:416:0x1598  */
    /* JADX WARN: Code duplicated, block: B:417:0x159f  */
    /* JADX WARN: Code duplicated, block: B:419:0x15a3  */
    /* JADX WARN: Code duplicated, block: B:421:0x15ad  */
    /* JADX WARN: Code duplicated, block: B:424:0x15b9  */
    /* JADX WARN: Code duplicated, block: B:426:0x15d7  */
    /* JADX WARN: Code duplicated, block: B:430:0x15ff  */
    /* JADX WARN: Code duplicated, block: B:433:0x160d A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:434:0x160f  */
    /* JADX WARN: Code duplicated, block: B:436:0x162b  */
    /* JADX WARN: Code duplicated, block: B:442:0x1659  */
    /* JADX WARN: Code duplicated, block: B:445:0x1660  */
    /* JADX WARN: Code duplicated, block: B:448:0x1683  */
    /* JADX WARN: Code duplicated, block: B:452:0x1690  */
    /* JADX WARN: Code duplicated, block: B:456:0x16b9  */
    /* JADX WARN: Code duplicated, block: B:458:0x16c9  */
    /* JADX WARN: Code duplicated, block: B:461:0x16d5  */
    /* JADX WARN: Code duplicated, block: B:463:0x16da  */
    /* JADX WARN: Code duplicated, block: B:465:0x16e4 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:466:0x16e6  */
    /* JADX WARN: Code duplicated, block: B:467:0x16f1  */
    /* JADX WARN: Code duplicated, block: B:472:0x1710  */
    /* JADX WARN: Code duplicated, block: B:473:0x1718  */
    /* JADX WARN: Code duplicated, block: B:476:0x1731  */
    /* JADX WARN: Code duplicated, block: B:478:0x1781  */
    /* JADX WARN: Code duplicated, block: B:480:0x1787  */
    /* JADX WARN: Code duplicated, block: B:484:0x1810  */
    /* JADX WARN: Code duplicated, block: B:486:0x1816  */
    /* JADX WARN: Code duplicated, block: B:488:0x1826  */
    /* JADX WARN: Code duplicated, block: B:489:0x182b  */
    /* JADX WARN: Code duplicated, block: B:491:0x1836  */
    /* JADX WARN: Code duplicated, block: B:494:0x1841  */
    /* JADX WARN: Code duplicated, block: B:504:0x185a  */
    /* JADX WARN: Code duplicated, block: B:506:0x1870  */
    /* JADX WARN: Code duplicated, block: B:507:0x187a  */
    /* JADX WARN: Code duplicated, block: B:510:0x187f A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:511:0x1881  */
    /* JADX WARN: Code duplicated, block: B:514:0x1891 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:515:0x1893  */
    /* JADX WARN: Code duplicated, block: B:519:0x18a5 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:520:0x18a7  */
    /* JADX WARN: Code duplicated, block: B:524:0x18b9 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:525:0x18bb  */
    /* JADX WARN: Code duplicated, block: B:529:0x18ce A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:530:0x18d0  */
    /* JADX WARN: Code duplicated, block: B:533:0x18ee  */
    /* JADX WARN: Code duplicated, block: B:536:0x18fb  */
    /* JADX WARN: Code duplicated, block: B:538:0x1909  */
    /* JADX WARN: Code duplicated, block: B:539:0x1933  */
    /* JADX WARN: Code duplicated, block: B:543:0x1943  */
    /* JADX WARN: Code duplicated, block: B:545:0x195b  */
    /* JADX WARN: Code duplicated, block: B:546:0x1960  */
    /* JADX WARN: Code duplicated, block: B:548:0x1964  */
    /* JADX WARN: Code duplicated, block: B:550:0x196c  */
    /* JADX WARN: Code duplicated, block: B:553:0x198e  */
    /* JADX WARN: Code duplicated, block: B:555:0x19a3  */
    /* JADX WARN: Code duplicated, block: B:556:0x19b9  */
    /* JADX WARN: Code duplicated, block: B:560:0x19db  */
    /* JADX WARN: Code duplicated, block: B:562:0x19f3  */
    /* JADX WARN: Code duplicated, block: B:563:0x19fc  */
    /* JADX WARN: Code duplicated, block: B:565:0x1a02  */
    /* JADX WARN: Code duplicated, block: B:567:0x1a0a  */
    /* JADX WARN: Code duplicated, block: B:570:0x1a2c  */
    /* JADX WARN: Code duplicated, block: B:572:0x1a41  */
    /* JADX WARN: Code duplicated, block: B:573:0x1a57  */
    /* JADX WARN: Code duplicated, block: B:577:0x1a7e  */
    /* JADX WARN: Code duplicated, block: B:579:0x1a86  */
    /* JADX WARN: Code duplicated, block: B:582:0x1aa3 A[LOOP:25: B:580:0x1a9d->B:582:0x1aa3, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:587:0x1ad9  */
    /* JADX WARN: Code duplicated, block: B:590:0x1b47  */
    /* JADX WARN: Code duplicated, block: B:592:0x1b65  */
    /* JADX WARN: Code duplicated, block: B:593:0x1b6b  */
    /* JADX WARN: Code duplicated, block: B:596:0x1b79  */
    /* JADX WARN: Code duplicated, block: B:598:0x1b8b  */
    /* JADX WARN: Code duplicated, block: B:600:0x1b9a  */
    /* JADX WARN: Code duplicated, block: B:603:0x1ba8  */
    /* JADX WARN: Code duplicated, block: B:604:0x1bad  */
    /* JADX WARN: Code duplicated, block: B:607:0x1bb3  */
    /* JADX WARN: Code duplicated, block: B:609:0x1bb7  */
    /* JADX WARN: Code duplicated, block: B:612:0x1bd4 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:613:0x1bd6  */
    /* JADX WARN: Code duplicated, block: B:614:0x1bec  */
    /* JADX WARN: Code duplicated, block: B:615:0x1bf2  */
    /* JADX WARN: Code duplicated, block: B:620:0x1c54  */
    /* JADX WARN: Code duplicated, block: B:622:0x1c60  */
    /* JADX WARN: Code duplicated, block: B:626:0x1c98  */
    /* JADX WARN: Code duplicated, block: B:629:0x1cbe  */
    /* JADX WARN: Code duplicated, block: B:631:0x1cc2  */
    /* JADX WARN: Code duplicated, block: B:634:0x1ceb  */
    /* JADX WARN: Code duplicated, block: B:637:0x1cf8 A[LOOP:28: B:635:0x1cf5->B:637:0x1cf8, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:641:0x1d1e  */
    /* JADX WARN: Code duplicated, block: B:644:0x1d2b A[LOOP:29: B:642:0x1d28->B:644:0x1d2b, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:649:0x1d4f  */
    /* JADX WARN: Code duplicated, block: B:651:0x1d55  */
    /* JADX WARN: Code duplicated, block: B:654:0x1d7a A[LOOP:31: B:652:0x1d77->B:654:0x1d7a, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:656:0x1da7  */
    /* JADX WARN: Code duplicated, block: B:659:0x1db1  */
    /* JADX WARN: Code duplicated, block: B:662:0x1dd6 A[LOOP:32: B:660:0x1dd3->B:662:0x1dd6, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:667:0x1e0f  */
    /* JADX WARN: Code duplicated, block: B:669:0x1e35  */
    /* JADX WARN: Code duplicated, block: B:670:0x1e3c  */
    /* JADX WARN: Code duplicated, block: B:673:0x1e41  */
    /* JADX WARN: Code duplicated, block: B:683:0x1e57  */
    /* JADX WARN: Code duplicated, block: B:685:0x1e96 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:687:0x1e9a  */
    /* JADX WARN: Code duplicated, block: B:689:0x1ea9  */
    /* JADX WARN: Code duplicated, block: B:690:0x1eb4  */
    /* JADX WARN: Code duplicated, block: B:692:0x1eb7 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:693:0x1eb9  */
    /* JADX WARN: Code duplicated, block: B:696:0x1eca A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:697:0x1ecc  */
    /* JADX WARN: Code duplicated, block: B:701:0x1ee3  */
    /* JADX WARN: Code duplicated, block: B:703:0x1eeb  */
    /* JADX WARN: Code duplicated, block: B:705:0x1ef8  */
    /* JADX WARN: Code duplicated, block: B:706:0x1efe  */
    /* JADX WARN: Code duplicated, block: B:709:0x1f1e  */
    /* JADX WARN: Code duplicated, block: B:710:0x1f34  */
    /* JADX WARN: Code duplicated, block: B:716:0x1f5c  */
    /* JADX WARN: Code duplicated, block: B:718:0x1f85 A[LOOP:36: B:717:0x1f83->B:718:0x1f85, LOOP_END] */
    /* JADX WARN: Code duplicated, block: B:721:0x1fc2  */
    /* JADX WARN: Code duplicated, block: B:723:0x1fda A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:725:0x1fde  */
    /* JADX WARN: Code duplicated, block: B:729:0x2031  */
    /* JADX WARN: Code duplicated, block: B:733:0x2048  */
    /* JADX WARN: Code duplicated, block: B:778:0x2050 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:781:0x1705 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:785:0x19be A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:789:0x1a5c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:793:0x1acb A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:802:0x1c89 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:807:0x1df6 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:815:0x1f35 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:819:0x2039 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:86:0x05e4  */
    /* JADX WARN: Instruction removed from duplicated block: B:637:0x1cf8, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:644:0x1d2b, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:651:0x1d55, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:654:0x1d7a, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:659:0x1db1, please report this as an issue */
    /* JADX WARN: Instruction removed from duplicated block: B:662:0x1dd6, please report this as an issue */
    public final void dumpLocked(Context context, PrintWriter printWriter, String str, int i, int i2, boolean z) {
        long j;
        long j2;
        int i3;
        int i4;
        int i5;
        StringBuilder sb;
        long j3;
        PrintWriter printWriter2;
        String str2;
        long j4;
        boolean z2;
        int i6;
        long j5;
        long j6;
        long j7;
        long j8;
        StringBuilder sb2;
        String str3;
        long j9;
        long j10;
        long j11;
        Timer bluetoothScanTimer;
        PrintWriter printWriter3;
        boolean z3;
        Uid uid;
        int i7;
        ArrayMap<String, ? extends Uid.Wakelock> wakelockStats;
        int size;
        long j12;
        long j13;
        long j14;
        long jComputeWakeLock;
        int i8;
        long j15;
        long j16;
        boolean z4;
        long j17;
        Uid uid2;
        long j18;
        long j19;
        long j20;
        PrintWriter printWriter4;
        long j21;
        Timer multicastWakelockStats;
        long j22;
        String str4;
        ArrayMap<String, ? extends Timer> syncStats;
        int size2;
        long j23;
        ArrayMap<String, ? extends Timer> jobStats;
        int size3;
        int i9;
        ArrayMap<String, SparseIntArray> jobCompletionStats;
        int size4;
        long j24;
        Uid uid3;
        StringBuilder sb3;
        int i10;
        long j25;
        SparseArray<? extends Uid> sparseArray;
        long j26;
        int i11;
        SparseArray<? extends Uid.Sensor> sensorStats;
        int size5;
        boolean z5;
        int i12;
        long j27;
        long j28;
        boolean zPrintTimer;
        long j29;
        int i13;
        Uid uid4;
        long j30;
        long userCpuTimeUs;
        long systemCpuTimeUs;
        long[] cpuFreqTimes;
        long[] screenOffCpuFreqTimes;
        int i14;
        SparseArray<? extends Uid> sparseArray2;
        long j31;
        ArrayMap<String, ? extends Uid.Proc> processStats;
        int size6;
        boolean z6;
        long j32;
        ArrayMap<String, ? extends Uid.Pkg> packageStats;
        int size7;
        boolean z7;
        long j33;
        int i15;
        ArrayMap<String, ? extends Counter> wakeupAlarmStats;
        int size8;
        boolean z8;
        ArrayMap<String, ? extends Uid.Pkg.Serv> serviceStats;
        int size9;
        long startTime;
        int starts;
        int launches;
        Uid.Proc procValueAt;
        long userTime;
        long systemTime;
        long foregroundTime;
        int starts2;
        int numCrashes;
        int numAnrs;
        int iCountExcessivePowers;
        Uid.Proc proc;
        boolean z9;
        int i16;
        Uid.Proc.ExcessivePower excessivePower;
        long[] cpuFreqTimes2;
        SparseArray<? extends Uid> sparseArray3;
        long j34;
        long[] screenOffCpuFreqTimes2;
        int i17;
        int i18;
        int i19;
        int i20;
        long processStateTime;
        Uid.Sensor sensorValueAt;
        StringBuilder sb4;
        int handle;
        Timer sensorTime;
        long j35;
        long totalTimeLocked;
        int countLocked;
        Timer sensorBackgroundTime;
        int countLocked2;
        long totalDurationMsLocked;
        long totalDurationMsLocked2;
        SparseIntArray sparseIntArrayValueAt;
        int i21;
        long totalTimeLocked2;
        int countLocked3;
        Timer subTimer;
        long j36;
        long totalDurationMsLocked3;
        int countLocked4;
        long totalTimeLocked3;
        int countLocked5;
        Timer subTimer2;
        long totalDurationMsLocked4;
        int countLocked6;
        long totalTimeLocked4;
        int countLocked7;
        long j37;
        long j38;
        long j39;
        boolean z10;
        Timer subTimer3;
        long totalDurationMsLocked5;
        Timer wakeTime;
        Timer subTimer4;
        int i22;
        boolean z11;
        int userActivityCount;
        long totalTimeLocked5;
        int countLocked8;
        Timer bluetoothScanBackgroundTimer;
        int countLocked9;
        long totalDurationMsLocked6;
        long totalDurationMsLocked7;
        int countLocked10;
        int countLocked11;
        Timer bluetoothUnoptimizedScanTimer;
        long totalDurationMsLocked8;
        long maxDurationMsLocked;
        Timer bluetoothUnoptimizedScanBackgroundTimer;
        long totalDurationMsLocked9;
        long maxDurationMsLocked2;
        long j40;
        long j41;
        Map<String, ? extends Timer> wakeupReasonStats;
        ArrayList arrayList;
        int i23;
        int i24;
        BatteryStats batteryStats = this;
        long jUptimeMillis = SystemClock.uptimeMillis() * 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        long j42 = (jElapsedRealtime + 500) / 1000;
        long batteryUptime = batteryStats.getBatteryUptime(jUptimeMillis);
        long jComputeBatteryUptime = batteryStats.computeBatteryUptime(jUptimeMillis, i);
        long jComputeBatteryRealtime = batteryStats.computeBatteryRealtime(jElapsedRealtime, i);
        long jComputeRealtime = batteryStats.computeRealtime(jElapsedRealtime, i);
        long jComputeUptime = batteryStats.computeUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffUptime = batteryStats.computeBatteryScreenOffUptime(jUptimeMillis, i);
        long jComputeBatteryScreenOffRealtime = batteryStats.computeBatteryScreenOffRealtime(jElapsedRealtime, i);
        long jComputeBatteryTimeRemaining = batteryStats.computeBatteryTimeRemaining(jElapsedRealtime);
        long jComputeChargeTimeRemaining = batteryStats.computeChargeTimeRemaining(jElapsedRealtime);
        long screenDozeTime = batteryStats.getScreenDozeTime(jElapsedRealtime, i);
        StringBuilder sb5 = new StringBuilder(128);
        SparseArray<? extends Uid> uidStats = getUidStats();
        int size10 = uidStats.size();
        int estimatedBatteryCapacity = getEstimatedBatteryCapacity();
        SparseArray<? extends Uid> sparseArray4 = uidStats;
        if (estimatedBatteryCapacity > 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Estimated battery capacity: ");
            sb5.append(BatteryStatsHelper.makemAh(estimatedBatteryCapacity));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        int minLearnedBatteryCapacity = getMinLearnedBatteryCapacity();
        if (minLearnedBatteryCapacity > 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Min learned battery capacity: ");
            sb5.append(BatteryStatsHelper.makemAh(minLearnedBatteryCapacity / 1000));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        int maxLearnedBatteryCapacity = getMaxLearnedBatteryCapacity();
        if (maxLearnedBatteryCapacity > 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Max learned battery capacity: ");
            sb5.append(BatteryStatsHelper.makemAh(maxLearnedBatteryCapacity / 1000));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Time on battery: ");
        long j43 = jComputeBatteryRealtime / 1000;
        formatTimeMs(sb5, j43);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(jComputeBatteryRealtime, jComputeRealtime));
        sb5.append(") realtime, ");
        formatTimeMs(sb5, jComputeBatteryUptime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(jComputeBatteryUptime, jComputeBatteryRealtime));
        sb5.append(") uptime");
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Time on battery screen off: ");
        formatTimeMs(sb5, jComputeBatteryScreenOffRealtime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(jComputeBatteryScreenOffRealtime, jComputeBatteryRealtime));
        sb5.append(") realtime, ");
        formatTimeMs(sb5, jComputeBatteryScreenOffUptime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(jComputeBatteryScreenOffUptime, jComputeBatteryRealtime));
        sb5.append(") uptime");
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Time on battery screen doze: ");
        formatTimeMs(sb5, screenDozeTime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(screenDozeTime, jComputeBatteryRealtime));
        sb5.append(")");
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Total run time: ");
        formatTimeMs(sb5, jComputeRealtime / 1000);
        sb5.append("realtime, ");
        formatTimeMs(sb5, jComputeUptime / 1000);
        sb5.append("uptime");
        printWriter.println(sb5.toString());
        if (jComputeBatteryTimeRemaining >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Battery time remaining: ");
            formatTimeMs(sb5, jComputeBatteryTimeRemaining / 1000);
            printWriter.println(sb5.toString());
        }
        if (jComputeChargeTimeRemaining >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Charge time remaining: ");
            formatTimeMs(sb5, jComputeChargeTimeRemaining / 1000);
            printWriter.println(sb5.toString());
        }
        int i25 = i;
        long uahDischarge = batteryStats.getUahDischarge(i25);
        if (uahDischarge >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(uahDischarge / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        long uahDischargeScreenOff = batteryStats.getUahDischargeScreenOff(i25);
        if (uahDischargeScreenOff >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Screen off discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(uahDischargeScreenOff / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        long uahDischargeScreenDoze = batteryStats.getUahDischargeScreenDoze(i25);
        if (uahDischargeScreenDoze >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Screen doze discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(uahDischargeScreenDoze / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        long j44 = uahDischarge - uahDischargeScreenOff;
        if (j44 >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Screen on discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(j44 / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        long uahDischargeLightDoze = batteryStats.getUahDischargeLightDoze(i25);
        if (uahDischargeLightDoze >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Device light doze discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(uahDischargeLightDoze / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        long uahDischargeDeepDoze = batteryStats.getUahDischargeDeepDoze(i25);
        if (uahDischargeDeepDoze >= 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Device deep doze discharge: ");
            sb5.append(BatteryStatsHelper.makemAh(uahDischargeDeepDoze / 1000.0d));
            sb5.append(" mAh");
            printWriter.println(sb5.toString());
        }
        printWriter.print("  Start clock time: ");
        printWriter.println(DateFormat.format("yyyy-MM-dd-HH-mm-ss", getStartClockTime()).toString());
        long j45 = jElapsedRealtime;
        long screenOnTime = batteryStats.getScreenOnTime(j45, i25);
        long interactiveTime = batteryStats.getInteractiveTime(j45, i25);
        long powerSaveModeEnabledTime = batteryStats.getPowerSaveModeEnabledTime(j45, i25);
        long deviceIdleModeTime = batteryStats.getDeviceIdleModeTime(1, j45, i25);
        long deviceIdleModeTime2 = batteryStats.getDeviceIdleModeTime(2, j45, i25);
        long deviceIdlingTime = batteryStats.getDeviceIdlingTime(1, j45, i25);
        long deviceIdlingTime2 = batteryStats.getDeviceIdlingTime(2, j45, i25);
        long phoneOnTime = batteryStats.getPhoneOnTime(j45, i25);
        batteryStats.getGlobalWifiRunningTime(j45, i25);
        batteryStats.getWifiOnTime(j45, i25);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Screen on: ");
        formatTimeMs(sb5, screenOnTime / 1000);
        sb5.append("(");
        long j46 = jComputeBatteryRealtime;
        sb5.append(batteryStats.formatRatioLocked(screenOnTime, j46));
        sb5.append(") ");
        sb5.append(batteryStats.getScreenOnCount(i25));
        sb5.append("x, Interactive: ");
        formatTimeMs(sb5, interactiveTime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(interactiveTime, j46));
        sb5.append(")");
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Screen brightnesses:");
        int i26 = 0;
        boolean z12 = false;
        while (i26 < 5) {
            long j47 = deviceIdlingTime;
            long screenBrightnessTime = batteryStats.getScreenBrightnessTime(i26, j45, i25);
            if (screenBrightnessTime != 0) {
                sb5.append("\n    ");
                sb5.append(str);
                sb5.append(SCREEN_BRIGHTNESS_NAMES[i26]);
                sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb5, screenBrightnessTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(screenBrightnessTime, screenOnTime));
                sb5.append(")");
                z12 = true;
            }
            i26++;
            deviceIdlingTime = j47;
        }
        long j48 = deviceIdlingTime;
        if (!z12) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        if (powerSaveModeEnabledTime != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Power save mode enabled: ");
            formatTimeMs(sb5, powerSaveModeEnabledTime / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(powerSaveModeEnabledTime, j46));
            sb5.append(")");
            printWriter.println(sb5.toString());
        }
        if (j48 != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Device light idling: ");
            formatTimeMs(sb5, j48 / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(j48, j46));
            sb5.append(") ");
            sb5.append(batteryStats.getDeviceIdlingCount(1, i25));
            sb5.append("x");
            printWriter.println(sb5.toString());
        }
        if (deviceIdleModeTime != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Idle mode light time: ");
            formatTimeMs(sb5, deviceIdleModeTime / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(deviceIdleModeTime, j46));
            sb5.append(") ");
            sb5.append(batteryStats.getDeviceIdleModeCount(1, i25));
            sb5.append("x");
            sb5.append(" -- longest ");
            formatTimeMs(sb5, batteryStats.getLongestDeviceIdleModeTime(1));
            printWriter.println(sb5.toString());
        }
        if (deviceIdlingTime2 != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Device full idling: ");
            formatTimeMs(sb5, deviceIdlingTime2 / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(deviceIdlingTime2, j46));
            sb5.append(") ");
            sb5.append(batteryStats.getDeviceIdlingCount(2, i25));
            sb5.append("x");
            printWriter.println(sb5.toString());
        }
        if (deviceIdleModeTime2 != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Idle mode full time: ");
            formatTimeMs(sb5, deviceIdleModeTime2 / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(deviceIdleModeTime2, j46));
            sb5.append(") ");
            sb5.append(batteryStats.getDeviceIdleModeCount(2, i25));
            sb5.append("x");
            sb5.append(" -- longest ");
            formatTimeMs(sb5, batteryStats.getLongestDeviceIdleModeTime(2));
            printWriter.println(sb5.toString());
        }
        if (phoneOnTime != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Active phone call: ");
            formatTimeMs(sb5, phoneOnTime / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(phoneOnTime, j46));
            sb5.append(") ");
            sb5.append(batteryStats.getPhoneOnCount(i25));
            sb5.append("x");
        }
        int numConnectivityChange = batteryStats.getNumConnectivityChange(i25);
        if (numConnectivityChange != 0) {
            printWriter.print(str);
            printWriter.print("  Connectivity changes: ");
            printWriter.println(numConnectivityChange);
        }
        ArrayList arrayList2 = new ArrayList();
        long j49 = 0;
        long j50 = 0;
        int i27 = size10;
        int i28 = 0;
        while (i28 < i27) {
            SparseArray<? extends Uid> sparseArray5 = sparseArray4;
            Uid uidValueAt = sparseArray5.valueAt(i28);
            ArrayMap<String, ? extends Uid.Wakelock> wakelockStats2 = uidValueAt.getWakelockStats();
            int i29 = 1;
            long j51 = j49;
            int size11 = wakelockStats2.size() - 1;
            long j52 = j50;
            long totalTimeLocked6 = j51;
            while (size11 >= 0) {
                Uid.Wakelock wakelockValueAt = wakelockStats2.valueAt(size11);
                int i30 = i27;
                Timer wakeTime2 = wakelockValueAt.getWakeTime(i29);
                if (wakeTime2 != null) {
                    totalTimeLocked6 += wakeTime2.getTotalTimeLocked(j45, i25);
                }
                Timer wakeTime3 = wakelockValueAt.getWakeTime(0);
                if (wakeTime3 != null) {
                    long totalTimeLocked7 = wakeTime3.getTotalTimeLocked(j45, i25);
                    if (totalTimeLocked7 > 0) {
                        if (i2 < 0) {
                            arrayList2.add(new TimerEntry(wakelockStats2.keyAt(size11), uidValueAt.getUid(), wakeTime3, totalTimeLocked7));
                        }
                        j52 += totalTimeLocked7;
                    } else {
                        j46 = j46;
                    }
                } else {
                    j46 = j46;
                }
                size11--;
                i27 = i30;
                j46 = j46;
                i29 = 1;
            }
            i28++;
            j49 = totalTimeLocked6;
            j50 = j52;
            sparseArray4 = sparseArray5;
        }
        int i31 = i27;
        long j53 = j46;
        SparseArray<? extends Uid> sparseArray6 = sparseArray4;
        int i32 = i2;
        long networkActivityBytes = batteryStats.getNetworkActivityBytes(0, i25);
        long networkActivityBytes2 = batteryStats.getNetworkActivityBytes(1, i25);
        long j54 = j49;
        long networkActivityBytes3 = batteryStats.getNetworkActivityBytes(2, i25);
        long networkActivityBytes4 = batteryStats.getNetworkActivityBytes(3, i25);
        long networkActivityPackets = batteryStats.getNetworkActivityPackets(0, i25);
        long networkActivityPackets2 = batteryStats.getNetworkActivityPackets(1, i25);
        long networkActivityPackets3 = batteryStats.getNetworkActivityPackets(2, i25);
        long networkActivityPackets4 = batteryStats.getNetworkActivityPackets(3, i25);
        long networkActivityBytes5 = batteryStats.getNetworkActivityBytes(4, i25);
        long networkActivityBytes6 = batteryStats.getNetworkActivityBytes(5, i25);
        if (j54 != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Total full wakelock time: ");
            formatTimeMsNoSpace(sb5, (j54 + 500) / 1000);
            printWriter.println(sb5.toString());
        }
        if (j50 != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Total partial wakelock time: ");
            formatTimeMsNoSpace(sb5, (j50 + 500) / 1000);
            printWriter.println(sb5.toString());
        }
        long wifiMulticastWakelockTime = batteryStats.getWifiMulticastWakelockTime(j45, i25);
        int wifiMulticastWakelockCount = batteryStats.getWifiMulticastWakelockCount(i25);
        if (wifiMulticastWakelockTime != 0) {
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Total WiFi Multicast wakelock Count: ");
            sb5.append(wifiMulticastWakelockCount);
            printWriter.println(sb5.toString());
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("  Total WiFi Multicast wakelock time: ");
            formatTimeMsNoSpace(sb5, (wifiMulticastWakelockTime + 500) / 1000);
            printWriter.println(sb5.toString());
        }
        printWriter.println("");
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  CONNECTIVITY POWER SUMMARY START");
        printWriter.println(sb5.toString());
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Logging duration for connectivity statistics: ");
        formatTimeMs(sb5, j43);
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Cellular Statistics:");
        printWriter.println(sb5.toString());
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Cellular kernel active time: ");
        long mobileRadioActiveTime = batteryStats.getMobileRadioActiveTime(j45, i25);
        formatTimeMs(sb5, mobileRadioActiveTime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(mobileRadioActiveTime, j53));
        sb5.append(")");
        printWriter.println(sb5.toString());
        printWriter.print("     Cellular data received: ");
        printWriter.println(batteryStats.formatBytesLocked(networkActivityBytes));
        printWriter.print("     Cellular data sent: ");
        printWriter.println(batteryStats.formatBytesLocked(networkActivityBytes2));
        printWriter.print("     Cellular packets received: ");
        printWriter.println(networkActivityPackets);
        printWriter.print("     Cellular packets sent: ");
        printWriter.println(networkActivityPackets2);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Cellular Radio Access Technology:");
        boolean z13 = false;
        for (int i33 = 0; i33 < 21; i33++) {
            long phoneDataConnectionTime = batteryStats.getPhoneDataConnectionTime(i33, j45, i25);
            if (phoneDataConnectionTime != 0) {
                sb5.append("\n       ");
                sb5.append(str);
                sb5.append(DATA_CONNECTION_NAMES[i33]);
                sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb5, phoneDataConnectionTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(phoneDataConnectionTime, j53));
                sb5.append(") ");
                z13 = true;
            }
        }
        if (!z13) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Cellular Rx signal strength (RSRP):");
        String[] strArr = {"very poor (less than -128dBm): ", "poor (-128dBm to -118dBm): ", "moderate (-118dBm to -108dBm): ", "good (-108dBm to -98dBm): ", "great (greater than -98dBm): "};
        int iMin = Math.min(5, strArr.length);
        int i34 = 0;
        boolean z14 = false;
        while (i34 < iMin) {
            boolean z15 = z14;
            long phoneSignalStrengthTime = batteryStats.getPhoneSignalStrengthTime(i34, j45, i25);
            if (phoneSignalStrengthTime == 0) {
                z14 = z15;
            } else {
                sb5.append("\n       ");
                sb5.append(str);
                sb5.append(strArr[i34]);
                sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb5, phoneSignalStrengthTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(phoneSignalStrengthTime, j53));
                sb5.append(") ");
                z14 = true;
            }
            i34++;
            iMin = iMin;
            mobileRadioActiveTime = mobileRadioActiveTime;
        }
        long j55 = mobileRadioActiveTime;
        if (!z14) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        long j56 = batteryUptime;
        SparseArray<? extends Uid> sparseArray7 = sparseArray6;
        int i35 = i31;
        long j57 = j42;
        long j58 = j55;
        printControllerActivity(printWriter, sb5, str, CELLULAR_CONTROLLER_NAME, getModemControllerActivity(), i);
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Wifi Statistics:");
        printWriter.println(sb5.toString());
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Wifi kernel active time: ");
        long wifiActiveTime = batteryStats.getWifiActiveTime(j45, i25);
        formatTimeMs(sb5, wifiActiveTime / 1000);
        sb5.append("(");
        sb5.append(batteryStats.formatRatioLocked(wifiActiveTime, j53));
        sb5.append(")");
        printWriter.println(sb5.toString());
        printWriter.print("     Wifi data received: ");
        printWriter.println(batteryStats.formatBytesLocked(networkActivityBytes3));
        printWriter.print("     Wifi data sent: ");
        printWriter.println(batteryStats.formatBytesLocked(networkActivityBytes4));
        printWriter.print("     Wifi packets received: ");
        printWriter.println(networkActivityPackets3);
        printWriter.print("     Wifi packets sent: ");
        printWriter.println(networkActivityPackets4);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Wifi states:");
        int i36 = 0;
        boolean z16 = false;
        while (i36 < 8) {
            long wifiStateTime = batteryStats.getWifiStateTime(i36, j45, i25);
            if (wifiStateTime != 0) {
                sb5.append("\n       ");
                sb5.append(WIFI_STATE_NAMES[i36]);
                sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb5, wifiStateTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(wifiStateTime, j53));
                sb5.append(") ");
                z16 = true;
            }
            i36++;
            i25 = i;
        }
        if (!z16) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Wifi supplicant states:");
        boolean z17 = false;
        for (int i37 = 0; i37 < 13; i37++) {
            long wifiSupplStateTime = batteryStats.getWifiSupplStateTime(i37, j45, i);
            if (wifiSupplStateTime != 0) {
                sb5.append("\n       ");
                sb5.append(WIFI_SUPPL_STATE_NAMES[i37]);
                sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                formatTimeMs(sb5, wifiSupplStateTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(wifiSupplStateTime, j53));
                sb5.append(") ");
                z17 = true;
            }
        }
        if (!z17) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     Wifi Rx signal strength (RSSI):");
        String[] strArr2 = {"very poor (less than -88.75dBm): ", "poor (-88.75 to -77.5dBm): ", "moderate (-77.5dBm to -66.25dBm): ", "good (-66.25dBm to -55dBm): ", "great (greater than -55dBm): "};
        int iMin2 = Math.min(5, strArr2.length);
        boolean z18 = false;
        for (int i38 = 0; i38 < iMin2; i38++) {
            long wifiSignalStrengthTime = batteryStats.getWifiSignalStrengthTime(i38, j45, i);
            if (wifiSignalStrengthTime != 0) {
                sb5.append("\n    ");
                sb5.append(str);
                sb5.append("     ");
                sb5.append(strArr2[i38]);
                formatTimeMs(sb5, wifiSignalStrengthTime / 1000);
                sb5.append("(");
                sb5.append(batteryStats.formatRatioLocked(wifiSignalStrengthTime, j53));
                sb5.append(") ");
                z18 = true;
            }
        }
        if (!z18) {
            sb5.append(" (no activity)");
        }
        printWriter.println(sb5.toString());
        printControllerActivity(printWriter, sb5, str, WIFI_CONTROLLER_NAME, getWifiControllerActivity(), i);
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  GPS Statistics:");
        printWriter.println(sb5.toString());
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("     GPS signal quality (Top 4 Average CN0):");
        String[] strArr3 = {"poor (less than 20 dBHz): ", "good (greater than 20 dBHz): "};
        int iMin3 = Math.min(2, strArr3.length);
        for (int i39 = 0; i39 < iMin3; i39++) {
            long gpsSignalQualityTime = batteryStats.getGpsSignalQualityTime(i39, j45, i);
            sb5.append("\n    ");
            sb5.append(str);
            sb5.append("  ");
            sb5.append(strArr3[i39]);
            formatTimeMs(sb5, gpsSignalQualityTime / 1000);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(gpsSignalQualityTime, j53));
            sb5.append(") ");
        }
        long j59 = j53;
        printWriter.println(sb5.toString());
        long gpsBatteryDrainMaMs = getGpsBatteryDrainMaMs();
        if (gpsBatteryDrainMaMs > 0) {
            printWriter.print(str);
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("     Battery Drain (mAh): ");
            sb5.append(Double.toString(gpsBatteryDrainMaMs / 3600000.0d));
            printWriter.println(sb5.toString());
        }
        printWriter.print(str);
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  CONNECTIVITY POWER SUMMARY END");
        printWriter.println(sb5.toString());
        printWriter.println("");
        printWriter.print(str);
        printWriter.print("  Bluetooth total received: ");
        printWriter.print(batteryStats.formatBytesLocked(networkActivityBytes5));
        printWriter.print(", sent: ");
        printWriter.println(batteryStats.formatBytesLocked(networkActivityBytes6));
        long bluetoothScanTime = batteryStats.getBluetoothScanTime(j45, i) / 1000;
        sb5.setLength(0);
        sb5.append(str);
        sb5.append("  Bluetooth scan time: ");
        formatTimeMs(sb5, bluetoothScanTime);
        printWriter.println(sb5.toString());
        printControllerActivity(printWriter, sb5, str, "Bluetooth", getBluetoothControllerActivity(), i);
        printWriter.println();
        if (i == 2) {
            if (getIsOnBattery()) {
                printWriter.print(str);
                printWriter.println("  Device is currently unplugged");
                printWriter.print(str);
                printWriter.print("    Discharge cycle start level: ");
                printWriter.println(getDischargeStartLevel());
                printWriter.print(str);
                printWriter.print("    Discharge cycle current level: ");
                printWriter.println(getDischargeCurrentLevel());
            } else {
                printWriter.print(str);
                printWriter.println("  Device is currently plugged into power");
                printWriter.print(str);
                printWriter.print("    Last discharge cycle start level: ");
                printWriter.println(getDischargeStartLevel());
                printWriter.print(str);
                printWriter.print("    Last discharge cycle end level: ");
                printWriter.println(getDischargeCurrentLevel());
            }
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen on: ");
            printWriter.println(getDischargeAmountScreenOn());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen off: ");
            printWriter.println(getDischargeAmountScreenOff());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen doze: ");
            printWriter.println(getDischargeAmountScreenDoze());
            printWriter.println(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
        } else {
            printWriter.print(str);
            printWriter.println("  Device battery use since last full charge");
            printWriter.print(str);
            printWriter.print("    Amount discharged (lower bound): ");
            printWriter.println(getLowDischargeAmountSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged (upper bound): ");
            printWriter.println(getHighDischargeAmountSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen on: ");
            printWriter.println(getDischargeAmountScreenOnSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen off: ");
            printWriter.println(getDischargeAmountScreenOffSinceCharge());
            printWriter.print(str);
            printWriter.print("    Amount discharged while screen doze: ");
            printWriter.println(getDischargeAmountScreenDozeSinceCharge());
            printWriter.println();
        }
        BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, z);
        batteryStatsHelper.create(batteryStats);
        batteryStatsHelper.refreshStats(i, -1);
        List<BatterySipper> usageList = batteryStatsHelper.getUsageList();
        if (usageList == null || usageList.size() <= 0) {
            j = j45;
        } else {
            printWriter.print(str);
            printWriter.println("  Estimated power use (mAh):");
            printWriter.print(str);
            printWriter.print("    Capacity: ");
            batteryStats.printmAh(printWriter, batteryStatsHelper.getPowerProfile().getBatteryCapacity());
            printWriter.print(", Computed drain: ");
            batteryStats.printmAh(printWriter, batteryStatsHelper.getComputedPower());
            printWriter.print(", actual drain: ");
            batteryStats.printmAh(printWriter, batteryStatsHelper.getMinDrainedPower());
            if (batteryStatsHelper.getMinDrainedPower() != batteryStatsHelper.getMaxDrainedPower()) {
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                batteryStats.printmAh(printWriter, batteryStatsHelper.getMaxDrainedPower());
            }
            printWriter.println();
            int i40 = 0;
            while (i40 < usageList.size()) {
                BatterySipper batterySipper = usageList.get(i40);
                printWriter.print(str);
                switch (batterySipper.drainType) {
                    case AMBIENT_DISPLAY:
                        printWriter.print("    Ambient display: ");
                        break;
                    case IDLE:
                        printWriter.print("    Idle: ");
                        break;
                    case CELL:
                        printWriter.print("    Cell standby: ");
                        break;
                    case PHONE:
                        printWriter.print("    Phone calls: ");
                        break;
                    case WIFI:
                        printWriter.print("    Wifi: ");
                        break;
                    case BLUETOOTH:
                        printWriter.print("    Bluetooth: ");
                        break;
                    case SCREEN:
                        printWriter.print("    Screen: ");
                        break;
                    case FLASHLIGHT:
                        printWriter.print("    Flashlight: ");
                        break;
                    case APP:
                        printWriter.print("    Uid ");
                        UserHandle.formatUid(printWriter, batterySipper.uidObj.getUid());
                        printWriter.print(": ");
                        break;
                    case USER:
                        printWriter.print("    User ");
                        printWriter.print(batterySipper.userId);
                        printWriter.print(": ");
                        break;
                    case UNACCOUNTED:
                        printWriter.print("    Unaccounted: ");
                        break;
                    case OVERCOUNTED:
                        printWriter.print("    Over-counted: ");
                        break;
                    case CAMERA:
                        printWriter.print("    Camera: ");
                        break;
                    default:
                        printWriter.print("    ???: ");
                        break;
                }
                batteryStats.printmAh(printWriter, batterySipper.totalPowerMah);
                if (batterySipper.usagePowerMah != batterySipper.totalPowerMah) {
                    printWriter.print(" (");
                    if (batterySipper.usagePowerMah != 0.0d) {
                        printWriter.print(" usage=");
                        batteryStats = this;
                        batteryStats.printmAh(printWriter, batterySipper.usagePowerMah);
                    } else {
                        batteryStats = this;
                    }
                    if (batterySipper.cpuPowerMah != 0.0d) {
                        printWriter.print(" cpu=");
                        batteryStats.printmAh(printWriter, batterySipper.cpuPowerMah);
                    }
                    if (batterySipper.wakeLockPowerMah != 0.0d) {
                        printWriter.print(" wake=");
                        batteryStats.printmAh(printWriter, batterySipper.wakeLockPowerMah);
                    }
                    if (batterySipper.mobileRadioPowerMah != 0.0d) {
                        printWriter.print(" radio=");
                        batteryStats.printmAh(printWriter, batterySipper.mobileRadioPowerMah);
                    }
                    if (batterySipper.wifiPowerMah != 0.0d) {
                        printWriter.print(" wifi=");
                        batteryStats.printmAh(printWriter, batterySipper.wifiPowerMah);
                    }
                    if (batterySipper.bluetoothPowerMah != 0.0d) {
                        printWriter.print(" bt=");
                        batteryStats.printmAh(printWriter, batterySipper.bluetoothPowerMah);
                    }
                    if (batterySipper.gpsPowerMah != 0.0d) {
                        printWriter.print(" gps=");
                        batteryStats.printmAh(printWriter, batterySipper.gpsPowerMah);
                    }
                    if (batterySipper.sensorPowerMah != 0.0d) {
                        printWriter.print(" sensor=");
                        batteryStats.printmAh(printWriter, batterySipper.sensorPowerMah);
                    }
                    if (batterySipper.cameraPowerMah != 0.0d) {
                        printWriter.print(" camera=");
                        batteryStats.printmAh(printWriter, batterySipper.cameraPowerMah);
                    }
                    if (batterySipper.flashlightPowerMah != 0.0d) {
                        printWriter.print(" flash=");
                        batteryStats.printmAh(printWriter, batterySipper.flashlightPowerMah);
                    }
                    printWriter.print(" )");
                } else {
                    batteryStats = this;
                }
                if (batterySipper.totalSmearedPowerMah != batterySipper.totalPowerMah) {
                    printWriter.print(" Including smearing: ");
                    batteryStats.printmAh(printWriter, batterySipper.totalSmearedPowerMah);
                    printWriter.print(" (");
                    if (batterySipper.screenPowerMah != 0.0d) {
                        printWriter.print(" screen=");
                        batteryStats.printmAh(printWriter, batterySipper.screenPowerMah);
                    }
                    if (batterySipper.proportionalSmearMah != 0.0d) {
                        printWriter.print(" proportional=");
                        batteryStats.printmAh(printWriter, batterySipper.proportionalSmearMah);
                    }
                    printWriter.print(" )");
                }
                if (batterySipper.shouldHide) {
                    printWriter.print(" Excluded from smearing");
                }
                printWriter.println();
                i40++;
                j45 = j45;
            }
            j = j45;
            printWriter.println();
        }
        List<BatterySipper> mobilemsppList = batteryStatsHelper.getMobilemsppList();
        if (mobilemsppList != null && mobilemsppList.size() > 0) {
            printWriter.print(str);
            printWriter.println("  Per-app mobile ms per packet:");
            long j60 = 0;
            for (int i41 = 0; i41 < mobilemsppList.size(); i41++) {
                BatterySipper batterySipper2 = mobilemsppList.get(i41);
                sb5.setLength(0);
                sb5.append(str);
                sb5.append("    Uid ");
                UserHandle.formatUid(sb5, batterySipper2.uidObj.getUid());
                sb5.append(": ");
                sb5.append(BatteryStatsHelper.makemAh(batterySipper2.mobilemspp));
                sb5.append(" (");
                sb5.append(batterySipper2.mobileRxPackets + batterySipper2.mobileTxPackets);
                sb5.append(" packets over ");
                formatTimeMsNoSpace(sb5, batterySipper2.mobileActive);
                sb5.append(") ");
                sb5.append(batterySipper2.mobileActiveCount);
                sb5.append("x");
                printWriter.println(sb5.toString());
                j60 += batterySipper2.mobileActive;
            }
            sb5.setLength(0);
            sb5.append(str);
            sb5.append("    TOTAL TIME: ");
            formatTimeMs(sb5, j60);
            sb5.append("(");
            sb5.append(batteryStats.formatRatioLocked(j60, j59));
            sb5.append(")");
            printWriter.println(sb5.toString());
            printWriter.println();
        }
        Comparator<TimerEntry> comparator = new Comparator<TimerEntry>() { // from class: android.os.BatteryStats.1
            @Override // java.util.Comparator
            public int compare(TimerEntry timerEntry, TimerEntry timerEntry2) {
                long j61 = timerEntry.mTime;
                long j62 = timerEntry2.mTime;
                if (j61 < j62) {
                    return 1;
                }
                return j61 > j62 ? -1 : 0;
            }
        };
        if (i32 < 0) {
            Map<String, ? extends Timer> kernelWakelockStats = getKernelWakelockStats();
            if (kernelWakelockStats.size() > 0) {
                ArrayList arrayList3 = new ArrayList();
                for (Map.Entry<String, ? extends Timer> entry : kernelWakelockStats.entrySet()) {
                    Timer value = entry.getValue();
                    long j61 = j;
                    long jComputeWakeLock2 = computeWakeLock(value, j61, i);
                    if (jComputeWakeLock2 > 0) {
                        arrayList3.add(new TimerEntry(entry.getKey(), 0, value, jComputeWakeLock2));
                    }
                    j = j61;
                }
                long j62 = j;
                if (arrayList3.size() > 0) {
                    Collections.sort(arrayList3, comparator);
                    printWriter.print(str);
                    printWriter.println("  All kernel wake locks:");
                    int i42 = 0;
                    while (i42 < arrayList3.size()) {
                        TimerEntry timerEntry = (TimerEntry) arrayList3.get(i42);
                        sb5.setLength(0);
                        sb5.append(str);
                        sb5.append("  Kernel Wake lock ");
                        sb5.append(timerEntry.mName);
                        int i43 = i42;
                        long j63 = j62;
                        if (!printWakeLock(sb5, timerEntry.mTimer, j62, null, i, ": ").equals(": ")) {
                            sb5.append(" realtime");
                            printWriter.println(sb5.toString());
                        }
                        i42 = i43 + 1;
                        j62 = j63;
                    }
                    j2 = j62;
                    i3 = -1;
                    printWriter.println();
                } else {
                    j2 = j62;
                }
                if (arrayList2.size() > 0) {
                    Collections.sort(arrayList2, comparator);
                    printWriter.print(str);
                    printWriter.println("  All partial wake locks:");
                    for (i24 = 0; i24 < arrayList2.size(); i24++) {
                        TimerEntry timerEntry2 = (TimerEntry) arrayList2.get(i24);
                        sb5.setLength(0);
                        sb5.append("  Wake lock ");
                        UserHandle.formatUid(sb5, timerEntry2.mId);
                        sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb5.append(timerEntry2.mName);
                        printWakeLock(sb5, timerEntry2.mTimer, j2, null, i, ": ");
                        sb5.append(" realtime");
                        printWriter.println(sb5.toString());
                    }
                    arrayList2.clear();
                    printWriter.println();
                }
                wakeupReasonStats = getWakeupReasonStats();
                if (wakeupReasonStats.size() > 0) {
                    printWriter.print(str);
                    printWriter.println("  All wakeup reasons:");
                    arrayList = new ArrayList();
                    for (Map.Entry<String, ? extends Timer> entry2 : wakeupReasonStats.entrySet()) {
                        Timer value2 = entry2.getValue();
                        arrayList.add(new TimerEntry(entry2.getKey(), 0, value2, value2.getCountLocked(i)));
                    }
                    Collections.sort(arrayList, comparator);
                    for (i23 = 0; i23 < arrayList.size(); i23++) {
                        TimerEntry timerEntry3 = (TimerEntry) arrayList.get(i23);
                        sb5.setLength(0);
                        sb5.append(str);
                        sb5.append("  Wakeup reason ");
                        sb5.append(timerEntry3.mName);
                        printWakeLock(sb5, timerEntry3.mTimer, j2, null, i, ": ");
                        sb5.append(" realtime");
                        printWriter.println(sb5.toString());
                    }
                    printWriter.println();
                }
            } else {
                j2 = j;
            }
            i3 = -1;
            if (arrayList2.size() > 0) {
                Collections.sort(arrayList2, comparator);
                printWriter.print(str);
                printWriter.println("  All partial wake locks:");
                while (i24 < arrayList2.size()) {
                    TimerEntry timerEntry4 = (TimerEntry) arrayList2.get(i24);
                    sb5.setLength(0);
                    sb5.append("  Wake lock ");
                    UserHandle.formatUid(sb5, timerEntry4.mId);
                    sb5.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                    sb5.append(timerEntry4.mName);
                    printWakeLock(sb5, timerEntry4.mTimer, j2, null, i, ": ");
                    sb5.append(" realtime");
                    printWriter.println(sb5.toString());
                }
                arrayList2.clear();
                printWriter.println();
            }
            wakeupReasonStats = getWakeupReasonStats();
            if (wakeupReasonStats.size() > 0) {
                printWriter.print(str);
                printWriter.println("  All wakeup reasons:");
                arrayList = new ArrayList();
                while (r0.hasNext()) {
                    Timer value3 = entry2.getValue();
                    arrayList.add(new TimerEntry(entry2.getKey(), 0, value3, value3.getCountLocked(i)));
                }
                Collections.sort(arrayList, comparator);
                while (i23 < arrayList.size()) {
                    TimerEntry timerEntry5 = (TimerEntry) arrayList.get(i23);
                    sb5.setLength(0);
                    sb5.append(str);
                    sb5.append("  Wakeup reason ");
                    sb5.append(timerEntry5.mName);
                    printWakeLock(sb5, timerEntry5.mTimer, j2, null, i, ": ");
                    sb5.append(" realtime");
                    printWriter.println(sb5.toString());
                }
                printWriter.println();
            }
        } else {
            j2 = j;
            i3 = -1;
        }
        LongSparseArray<? extends Timer> kernelMemoryStats = getKernelMemoryStats();
        if (kernelMemoryStats.size() > 0) {
            printWriter.println("  Memory Stats");
            for (int i44 = 0; i44 < kernelMemoryStats.size(); i44++) {
                sb5.setLength(0);
                sb5.append("  Bandwidth ");
                sb5.append(kernelMemoryStats.keyAt(i44));
                sb5.append(" Time ");
                sb5.append(kernelMemoryStats.valueAt(i44).getTotalTimeLocked(j2, i));
                printWriter.println(sb5.toString());
            }
            i4 = 0;
            printWriter.println();
        } else {
            i4 = 0;
        }
        Map<String, ? extends Timer> rpmStats = getRpmStats();
        if (rpmStats.size() > 0) {
            printWriter.print(str);
            printWriter.println("  Resource Power Manager Stats");
            if (rpmStats.size() > 0) {
                for (Iterator<Map.Entry<String, ? extends Timer>> it = rpmStats.entrySet().iterator(); it.hasNext(); it = it) {
                    Map.Entry<String, ? extends Timer> next = it.next();
                    StringBuilder sb6 = sb5;
                    printTimer(printWriter, sb6, next.getValue(), j2, i, str, next.getKey());
                    sb5 = sb6;
                    j59 = j59;
                    i4 = 0;
                }
            }
            i5 = i4;
            sb = sb5;
            j3 = j59;
            printWriter.println();
        } else {
            i5 = i4;
            sb = sb5;
            j3 = j59;
        }
        long[] cpuFreqs = getCpuFreqs();
        if (cpuFreqs != null) {
            sb.setLength(i5);
            sb.append("  CPU freqs:");
            for (int i45 = i5; i45 < cpuFreqs.length; i45++) {
                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqs[i45]);
            }
            printWriter2 = printWriter;
            printWriter2.println(sb.toString());
            printWriter.println();
        } else {
            printWriter2 = printWriter;
        }
        int i46 = i5;
        while (i46 < i35) {
            SparseArray<? extends Uid> sparseArray8 = sparseArray7;
            int iKeyAt = sparseArray8.keyAt(i46);
            if (i32 >= 0 && iKeyAt != i32) {
                if (iKeyAt != 1000) {
                    i6 = i35;
                    j32 = j2;
                    sparseArray2 = sparseArray8;
                    i11 = i46;
                    printWriter4 = printWriter2;
                    j33 = j56;
                    j31 = j57;
                    j25 = j58;
                    j11 = j3;
                    i15 = i5;
                }
                i46 = i11 + 1;
                i32 = i2;
                printWriter2 = printWriter4;
                i5 = i15;
                j56 = j33;
                i35 = i6;
                j3 = j11;
                j58 = j25;
                sparseArray7 = sparseArray2;
                j57 = j31;
                j2 = j32;
                batteryStats = this;
            }
            Uid uidValueAt2 = sparseArray8.valueAt(i46);
            printWriter.print(str);
            printWriter2.print("  ");
            UserHandle.formatUid(printWriter2, iKeyAt);
            printWriter2.println(SettingsStringUtil.DELIMITER);
            long networkActivityBytes7 = uidValueAt2.getNetworkActivityBytes(i5, i);
            long networkActivityBytes8 = uidValueAt2.getNetworkActivityBytes(1, i);
            long j64 = j2;
            long networkActivityBytes9 = uidValueAt2.getNetworkActivityBytes(2, i);
            int i47 = i35;
            long networkActivityBytes10 = uidValueAt2.getNetworkActivityBytes(3, i);
            int i48 = i46;
            long networkActivityBytes11 = uidValueAt2.getNetworkActivityBytes(4, i);
            long networkActivityBytes12 = uidValueAt2.getNetworkActivityBytes(5, i);
            long networkActivityPackets5 = uidValueAt2.getNetworkActivityPackets(0, i);
            long networkActivityPackets6 = uidValueAt2.getNetworkActivityPackets(1, i);
            long networkActivityPackets7 = uidValueAt2.getNetworkActivityPackets(2, i);
            long networkActivityPackets8 = uidValueAt2.getNetworkActivityPackets(3, i);
            long mobileRadioActiveTime2 = uidValueAt2.getMobileRadioActiveTime(i);
            int mobileRadioActiveCount = uidValueAt2.getMobileRadioActiveCount(i);
            long fullWifiLockTime = uidValueAt2.getFullWifiLockTime(j64, i);
            long wifiScanTime = uidValueAt2.getWifiScanTime(j64, i);
            int wifiScanCount = uidValueAt2.getWifiScanCount(i);
            int wifiScanBackgroundCount = uidValueAt2.getWifiScanBackgroundCount(i);
            long wifiScanActualTime = uidValueAt2.getWifiScanActualTime(j64);
            long wifiScanBackgroundTime = uidValueAt2.getWifiScanBackgroundTime(j64);
            long wifiRunningTime = uidValueAt2.getWifiRunningTime(j64, i);
            long mobileRadioApWakeupCount = uidValueAt2.getMobileRadioApWakeupCount(i);
            long wifiRadioApWakeupCount = uidValueAt2.getWifiRadioApWakeupCount(i);
            if (networkActivityBytes7 > 0 || networkActivityBytes8 > 0 || networkActivityPackets5 > 0 || networkActivityPackets6 > 0) {
                printWriter.print(str);
                printWriter2.print("    Mobile network: ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes7));
                printWriter2.print(" received, ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes8));
                printWriter2.print(" sent (packets ");
                printWriter2.print(networkActivityPackets5);
                printWriter2.print(" received, ");
                printWriter2.print(networkActivityPackets6);
                printWriter2.println(" sent)");
            }
            if (mobileRadioActiveTime2 > 0 || mobileRadioActiveCount > 0) {
                sb.setLength(0);
                str2 = str;
                sb.append(str2);
                sb.append("    Mobile radio active: ");
                long j65 = mobileRadioActiveTime2 / 1000;
                formatTimeMs(sb, j65);
                sb.append("(");
                j4 = j58;
                sb.append(batteryStats.formatRatioLocked(mobileRadioActiveTime2, j4));
                sb.append(") ");
                sb.append(mobileRadioActiveCount);
                sb.append("x");
                long j66 = networkActivityPackets5 + networkActivityPackets6;
                if (j66 == 0) {
                    j66 = 1;
                }
                sb.append(" @ ");
                sb.append(BatteryStatsHelper.makemAh(j65 / j66));
                sb.append(" mspp");
                printWriter2.println(sb.toString());
            } else {
                j4 = j58;
                str2 = str;
            }
            if (mobileRadioApWakeupCount > 0) {
                z2 = false;
                sb.setLength(0);
                sb.append(str2);
                sb.append("    Mobile radio AP wakeups: ");
                sb.append(mobileRadioApWakeupCount);
                printWriter2.println(sb.toString());
            } else {
                z2 = false;
            }
            i6 = i47;
            Uid uid5 = uidValueAt2;
            long j67 = j4;
            StringBuilder sb7 = sb;
            printControllerActivityIfInteresting(printWriter, sb, str2 + "  ", CELLULAR_CONTROLLER_NAME, uidValueAt2.getModemControllerActivity(), i);
            if (networkActivityBytes9 > 0 || networkActivityBytes10 > 0 || networkActivityPackets7 > 0 || networkActivityPackets8 > 0) {
                printWriter.print(str);
                printWriter2.print("    Wi-Fi network: ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes9));
                printWriter2.print(" received, ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes10));
                printWriter2.print(" sent (packets ");
                printWriter2.print(networkActivityPackets7);
                printWriter2.print(" received, ");
                printWriter2.print(networkActivityPackets8);
                printWriter2.println(" sent)");
            }
            if (fullWifiLockTime == 0) {
                j5 = wifiScanTime;
                if (j5 == 0 && wifiScanCount == 0 && wifiScanBackgroundCount == 0) {
                    j6 = wifiScanActualTime;
                    if (j6 == 0) {
                        if (j7 == 0) {
                            j8 = wifiRunningTime;
                            if (j8 == 0) {
                                j7 = wifiScanBackgroundTime;
                                j9 = j3;
                                j10 = j64;
                                sb2 = sb7;
                                str3 = str;
                            }
                            if (wifiRadioApWakeupCount > 0) {
                                sb2.setLength(0);
                                sb2.append(str3);
                                sb2.append("    WiFi AP wakeups: ");
                                sb2.append(wifiRadioApWakeupCount);
                                printWriter2.println(sb2.toString());
                            }
                            j11 = j9;
                            printControllerActivityIfInteresting(printWriter, sb2, str3 + "  ", WIFI_CONTROLLER_NAME, uid5.getWifiControllerActivity(), i);
                            if (networkActivityBytes11 <= 0 || networkActivityBytes12 > 0) {
                                printWriter.print(str);
                                printWriter2.print("    Bluetooth network: ");
                                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                                printWriter2.print(" received, ");
                                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                                printWriter2.println(" sent");
                            }
                            bluetoothScanTimer = uid5.getBluetoothScanTimer();
                            if (bluetoothScanTimer != null) {
                                totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                                if (totalTimeLocked5 != 0) {
                                    countLocked8 = bluetoothScanTimer.getCountLocked(i);
                                    bluetoothScanBackgroundTimer = uid5.getBluetoothScanBackgroundTimer();
                                    if (bluetoothScanBackgroundTimer != null) {
                                        countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                                    } else {
                                        countLocked9 = 0;
                                    }
                                    totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j57);
                                    if (bluetoothScanBackgroundTimer != null) {
                                        totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j57);
                                    } else {
                                        totalDurationMsLocked7 = 0;
                                    }
                                    if (uid5.getBluetoothScanResultCounter() != null) {
                                        countLocked10 = uid5.getBluetoothScanResultCounter().getCountLocked(i);
                                    } else {
                                        countLocked10 = 0;
                                    }
                                    if (uid5.getBluetoothScanResultBgCounter() != null) {
                                        countLocked11 = uid5.getBluetoothScanResultBgCounter().getCountLocked(i);
                                    } else {
                                        countLocked11 = 0;
                                    }
                                    bluetoothUnoptimizedScanTimer = uid5.getBluetoothUnoptimizedScanTimer();
                                    if (bluetoothUnoptimizedScanTimer != null) {
                                        totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j57);
                                    } else {
                                        totalDurationMsLocked8 = 0;
                                    }
                                    if (bluetoothUnoptimizedScanTimer != null) {
                                        maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j57);
                                    } else {
                                        maxDurationMsLocked = 0;
                                    }
                                    bluetoothUnoptimizedScanBackgroundTimer = uid5.getBluetoothUnoptimizedScanBackgroundTimer();
                                    if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                        totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j57);
                                    } else {
                                        totalDurationMsLocked9 = 0;
                                    }
                                    if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                        maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j57);
                                    } else {
                                        maxDurationMsLocked2 = 0;
                                    }
                                    sb2.setLength(0);
                                    if (totalDurationMsLocked6 != totalTimeLocked5) {
                                        sb2.append(str3);
                                        sb2.append("    Bluetooth Scan (total blamed realtime): ");
                                        formatTimeMs(sb2, totalTimeLocked5);
                                        sb2.append(" (");
                                        sb2.append(countLocked8);
                                        sb2.append(" times)");
                                        if (bluetoothScanTimer.isRunningLocked()) {
                                            sb2.append(" (currently running)");
                                        }
                                        sb2.append("\n");
                                    }
                                    sb2.append(str3);
                                    sb2.append("    Bluetooth Scan (total actual realtime): ");
                                    formatTimeMs(sb2, totalDurationMsLocked6);
                                    sb2.append(" (");
                                    sb2.append(countLocked8);
                                    sb2.append(" times)");
                                    if (bluetoothScanTimer.isRunningLocked()) {
                                        sb2.append(" (currently running)");
                                    }
                                    sb2.append("\n");
                                    if (totalDurationMsLocked7 <= 0 || countLocked9 > 0) {
                                        sb2.append(str3);
                                        sb2.append("    Bluetooth Scan (background realtime): ");
                                        formatTimeMs(sb2, totalDurationMsLocked7);
                                        sb2.append(" (");
                                        sb2.append(countLocked9);
                                        sb2.append(" times)");
                                        if (bluetoothScanBackgroundTimer != null && bluetoothScanBackgroundTimer.isRunningLocked()) {
                                            sb2.append(" (currently running in background)");
                                        }
                                        sb2.append("\n");
                                    }
                                    sb2.append(str3);
                                    sb2.append("    Bluetooth Scan Results: ");
                                    sb2.append(countLocked10);
                                    sb2.append(" (");
                                    sb2.append(countLocked11);
                                    sb2.append(" in background)");
                                    j40 = totalDurationMsLocked8;
                                    if (j40 <= 0) {
                                        j41 = totalDurationMsLocked9;
                                        if (j41 > 0) {
                                        }
                                        printWriter3 = printWriter;
                                        printWriter3.println(sb2.toString());
                                        z3 = true;
                                    } else {
                                        j41 = totalDurationMsLocked9;
                                    }
                                    sb2.append("\n");
                                    sb2.append(str3);
                                    sb2.append("    Unoptimized Bluetooth Scan (realtime): ");
                                    formatTimeMs(sb2, j40);
                                    sb2.append(" (max ");
                                    formatTimeMs(sb2, maxDurationMsLocked);
                                    sb2.append(")");
                                    if (bluetoothUnoptimizedScanTimer != 0 && bluetoothUnoptimizedScanTimer.isRunningLocked()) {
                                        sb2.append(" (currently running unoptimized)");
                                    }
                                    if (bluetoothUnoptimizedScanBackgroundTimer != null && j41 > 0) {
                                        sb2.append("\n");
                                        sb2.append(str3);
                                        sb2.append("    Unoptimized Bluetooth Scan (background realtime): ");
                                        formatTimeMs(sb2, j41);
                                        sb2.append(" (max ");
                                        formatTimeMs(sb2, maxDurationMsLocked2);
                                        sb2.append(")");
                                        if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                            sb2.append(" (currently running unoptimized in background)");
                                        }
                                    }
                                    printWriter3 = printWriter;
                                    printWriter3.println(sb2.toString());
                                    z3 = true;
                                } else {
                                    j10 = j10;
                                    printWriter3 = printWriter2;
                                    j57 = j57;
                                    z3 = false;
                                }
                            } else {
                                j10 = j10;
                                printWriter3 = printWriter2;
                                j57 = j57;
                                z3 = false;
                            }
                            if (uid5.hasUserActivity()) {
                                i22 = 0;
                                z11 = false;
                                while (i22 < 4) {
                                    Uid uid6 = uid5;
                                    userActivityCount = uid6.getUserActivityCount(i22, i);
                                    if (userActivityCount != 0) {
                                        if (z11) {
                                            sb2.append(", ");
                                        } else {
                                            sb2.setLength(0);
                                            sb2.append("    User activity: ");
                                            z11 = true;
                                        }
                                        sb2.append(userActivityCount);
                                        sb2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                        sb2.append(Uid.USER_ACTIVITY_TYPES[i22]);
                                    }
                                    i22++;
                                    uid5 = uid6;
                                }
                                uid = uid5;
                                i7 = i;
                                if (z11) {
                                    printWriter3.println(sb2.toString());
                                }
                            } else {
                                uid = uid5;
                                i7 = i;
                            }
                            wakelockStats = uid.getWakelockStats();
                            size = wakelockStats.size() - 1;
                            j12 = 0;
                            j13 = 0;
                            j14 = 0;
                            jComputeWakeLock = 0;
                            i8 = 0;
                            while (size >= 0) {
                                Uid.Wakelock wakelockValueAt2 = wakelockStats.valueAt(size);
                                sb2.setLength(0);
                                sb2.append(str3);
                                sb2.append("    Wake lock ");
                                sb2.append(wakelockStats.keyAt(size));
                                Uid uid7 = uid;
                                long j68 = j12;
                                ArrayMap<String, ? extends Uid.Wakelock> arrayMap = wakelockStats;
                                int i49 = size;
                                long j69 = j13;
                                long j70 = j10;
                                long j71 = j14;
                                long j72 = j57;
                                String strPrintWakeLock = printWakeLock(sb2, wakelockValueAt2.getWakeTime(1), j70, "full", i, ": ");
                                wakeTime = wakelockValueAt2.getWakeTime(0);
                                String strPrintWakeLock2 = printWakeLock(sb2, wakeTime, j70, Slice.HINT_PARTIAL, i, strPrintWakeLock);
                                if (wakeTime != null) {
                                    subTimer4 = wakeTime.getSubTimer();
                                } else {
                                    subTimer4 = null;
                                }
                                long j73 = j10;
                                StringBuilder sb8 = sb2;
                                printWakeLock(sb8, wakelockValueAt2.getWakeTime(18), j73, "draw", i, printWakeLock(sb8, wakelockValueAt2.getWakeTime(2), j73, Context.WINDOW_SERVICE, i, printWakeLock(sb2, subTimer4, j73, "background partial", i, strPrintWakeLock2)));
                                sb2.append(" realtime");
                                printWriter3.println(sb2.toString());
                                i8++;
                                long jComputeWakeLock3 = j71 + computeWakeLock(wakelockValueAt2.getWakeTime(1), j73, i7);
                                long jComputeWakeLock4 = computeWakeLock(wakelockValueAt2.getWakeTime(0), j73, i7) + j69;
                                long jComputeWakeLock5 = j68 + computeWakeLock(wakelockValueAt2.getWakeTime(2), j73, i7);
                                jComputeWakeLock += computeWakeLock(wakelockValueAt2.getWakeTime(18), j73, i7);
                                uid = uid7;
                                j57 = j72;
                                j14 = jComputeWakeLock3;
                                str3 = str;
                                j13 = jComputeWakeLock4;
                                j12 = jComputeWakeLock5;
                                wakelockStats = arrayMap;
                                size = i49 - 1;
                                z3 = true;
                            }
                            j15 = j12;
                            j16 = j13;
                            z4 = z3;
                            j17 = j14;
                            uid2 = uid;
                            j18 = j10;
                            j19 = j57;
                            j20 = jComputeWakeLock;
                            if (i8 > 1) {
                                if (uid2.getAggregatedPartialWakelockTimer() != null) {
                                    Timer aggregatedPartialWakelockTimer = uid2.getAggregatedPartialWakelockTimer();
                                    long totalDurationMsLocked10 = aggregatedPartialWakelockTimer.getTotalDurationMsLocked(j19);
                                    subTimer3 = aggregatedPartialWakelockTimer.getSubTimer();
                                    if (subTimer3 != null) {
                                        totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j19);
                                    } else {
                                        totalDurationMsLocked5 = 0;
                                    }
                                    j39 = j19;
                                    j37 = totalDurationMsLocked10;
                                    j38 = totalDurationMsLocked5;
                                } else {
                                    j37 = 0;
                                    j38 = 0;
                                    j39 = j19;
                                }
                                if (j37 != 0 && j38 == 0 && j17 == 0 && j16 == 0 && j15 == 0) {
                                    j21 = j39;
                                    printWriter4 = printWriter;
                                } else {
                                    sb2.setLength(0);
                                    sb2.append(str);
                                    sb2.append("    TOTAL wake: ");
                                    if (j17 != 0) {
                                        formatTimeMs(sb2, j17);
                                        sb2.append("full");
                                        z10 = true;
                                    } else {
                                        z10 = false;
                                    }
                                    if (j16 != 0) {
                                        if (z10) {
                                            sb2.append(", ");
                                        }
                                        formatTimeMs(sb2, j16);
                                        sb2.append("blamed partial");
                                        z10 = true;
                                    }
                                    if (j37 != 0) {
                                        if (z10) {
                                            sb2.append(", ");
                                        }
                                        formatTimeMs(sb2, j37);
                                        sb2.append("actual partial");
                                        z10 = true;
                                    }
                                    if (j38 != 0) {
                                        if (z10) {
                                            sb2.append(", ");
                                        }
                                        formatTimeMs(sb2, j38);
                                        sb2.append("actual background partial");
                                        z10 = true;
                                    }
                                    if (j15 != 0) {
                                        if (z10) {
                                            sb2.append(", ");
                                        }
                                        formatTimeMs(sb2, j15);
                                        sb2.append(Context.WINDOW_SERVICE);
                                        z10 = true;
                                    }
                                    if (j20 != 0) {
                                        if (z10) {
                                            sb2.append(",");
                                        }
                                        formatTimeMs(sb2, j20);
                                        sb2.append("draw");
                                    }
                                    sb2.append(" realtime");
                                    j21 = j39;
                                    printWriter4 = printWriter;
                                    printWriter4.println(sb2.toString());
                                }
                            } else {
                                z4 = z4;
                                printWriter4 = printWriter3;
                                j21 = j19;
                            }
                            multicastWakelockStats = uid2.getMulticastWakelockStats();
                            if (multicastWakelockStats != null) {
                                j22 = j18;
                                totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j22, i7);
                                countLocked7 = multicastWakelockStats.getCountLocked(i7);
                                if (totalTimeLocked4 > 0) {
                                    sb2.setLength(0);
                                    str4 = str;
                                    sb2.append(str4);
                                    sb2.append("    WiFi Multicast Wakelock");
                                    sb2.append(" count = ");
                                    sb2.append(countLocked7);
                                    sb2.append(" time = ");
                                    formatTimeMsNoSpace(sb2, (totalTimeLocked4 + 500) / 1000);
                                    printWriter4.println(sb2.toString());
                                }
                                syncStats = uid2.getSyncStats();
                                size2 = syncStats.size() - 1;
                                while (size2 >= 0) {
                                    Timer timerValueAt = syncStats.valueAt(size2);
                                    totalTimeLocked3 = (timerValueAt.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                    countLocked5 = timerValueAt.getCountLocked(i7);
                                    subTimer2 = timerValueAt.getSubTimer();
                                    if (subTimer2 != null) {
                                        totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                                    } else {
                                        totalDurationMsLocked4 = -1;
                                    }
                                    if (subTimer2 != null) {
                                        countLocked6 = subTimer2.getCountLocked(i7);
                                    } else {
                                        countLocked6 = i3;
                                    }
                                    sb2.setLength(0);
                                    sb2.append(str4);
                                    sb2.append("    Sync ");
                                    sb2.append(syncStats.keyAt(size2));
                                    sb2.append(": ");
                                    if (totalTimeLocked3 != 0) {
                                        formatTimeMs(sb2, totalTimeLocked3);
                                        sb2.append("realtime (");
                                        sb2.append(countLocked5);
                                        sb2.append(" times)");
                                        if (totalDurationMsLocked4 > 0) {
                                            sb2.append(", ");
                                            formatTimeMs(sb2, totalDurationMsLocked4);
                                            sb2.append("background (");
                                            sb2.append(countLocked6);
                                            sb2.append(" times)");
                                        }
                                    } else {
                                        sb2.append("(not used)");
                                    }
                                    printWriter4.println(sb2.toString());
                                    size2--;
                                    j21 = j21;
                                    z4 = true;
                                }
                                j23 = j21;
                                jobStats = uid2.getJobStats();
                                size3 = jobStats.size() - 1;
                                while (size3 >= 0) {
                                    Timer timerValueAt2 = jobStats.valueAt(size3);
                                    totalTimeLocked2 = (timerValueAt2.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                    countLocked3 = timerValueAt2.getCountLocked(i7);
                                    subTimer = timerValueAt2.getSubTimer();
                                    if (subTimer != null) {
                                        j36 = j23;
                                        totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                                    } else {
                                        j36 = j23;
                                        totalDurationMsLocked3 = -1;
                                    }
                                    if (subTimer != null) {
                                        countLocked4 = subTimer.getCountLocked(i7);
                                    } else {
                                        countLocked4 = i3;
                                    }
                                    sb2.setLength(0);
                                    sb2.append(str4);
                                    sb2.append("    Job ");
                                    sb2.append(jobStats.keyAt(size3));
                                    sb2.append(": ");
                                    if (totalTimeLocked2 != 0) {
                                        formatTimeMs(sb2, totalTimeLocked2);
                                        sb2.append("realtime (");
                                        sb2.append(countLocked3);
                                        sb2.append(" times)");
                                        if (totalDurationMsLocked3 > 0) {
                                            sb2.append(", ");
                                            formatTimeMs(sb2, totalDurationMsLocked3);
                                            sb2.append("background (");
                                            sb2.append(countLocked4);
                                            sb2.append(" times)");
                                        }
                                    } else {
                                        sb2.append("(not used)");
                                    }
                                    printWriter4.println(sb2.toString());
                                    size3--;
                                    j23 = j36;
                                    j22 = j22;
                                    z4 = true;
                                }
                                long j74 = j22;
                                long j75 = j23;
                                i9 = 0;
                                jobCompletionStats = uid2.getJobCompletionStats();
                                for (size4 = jobCompletionStats.size() - 1; size4 >= 0; size4--) {
                                    sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                                    if (sparseIntArrayValueAt != null) {
                                        printWriter.print(str);
                                        printWriter4.print("    Job Completions ");
                                        printWriter4.print(jobCompletionStats.keyAt(size4));
                                        printWriter4.print(SettingsStringUtil.DELIMITER);
                                        for (i21 = 0; i21 < sparseIntArrayValueAt.size(); i21++) {
                                            printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                            printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                            printWriter4.print("(");
                                            printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                            printWriter4.print("x)");
                                        }
                                        printWriter.println();
                                    }
                                }
                                uid2.getDeferredJobsLineLocked(sb2, i7);
                                if (sb2.length() > 0) {
                                    printWriter4.print("    Jobs deferred on launch ");
                                    printWriter4.println(sb2.toString());
                                }
                                j24 = j74;
                                uid3 = uid2;
                                sb3 = sb2;
                                i10 = i7;
                                j25 = j67;
                                sparseArray = sparseArray8;
                                j26 = j75;
                                i11 = i48;
                                boolean zPrintTimer2 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                                sensorStats = uid3.getSensorStats();
                                size5 = sensorStats.size();
                                z5 = zPrintTimer2;
                                i12 = 0;
                                while (i12 < size5) {
                                    sensorValueAt = sensorStats.valueAt(i12);
                                    sensorStats.keyAt(i12);
                                    sb4 = sb3;
                                    sb4.setLength(i9);
                                    sb4.append(str);
                                    sb4.append("    Sensor ");
                                    handle = sensorValueAt.getHandle();
                                    if (handle == -10000) {
                                        sb4.append("GPS");
                                    } else {
                                        sb4.append(handle);
                                    }
                                    sb4.append(": ");
                                    sensorTime = sensorValueAt.getSensorTime();
                                    if (sensorTime != null) {
                                        totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                                        countLocked = sensorTime.getCountLocked(i10);
                                        sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                        if (sensorBackgroundTime != null) {
                                            countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                                        } else {
                                            countLocked2 = i9;
                                        }
                                        j35 = j26;
                                        totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                                        if (sensorBackgroundTime != null) {
                                            totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                                        } else {
                                            totalDurationMsLocked2 = 0;
                                        }
                                        if (totalTimeLocked != 0) {
                                            if (totalDurationMsLocked != totalTimeLocked) {
                                                formatTimeMs(sb4, totalTimeLocked);
                                                sb4.append("blamed realtime, ");
                                            }
                                            formatTimeMs(sb4, totalDurationMsLocked);
                                            sb4.append("realtime (");
                                            sb4.append(countLocked);
                                            sb4.append(" times)");
                                            if (totalDurationMsLocked2 == 0 || countLocked2 > 0) {
                                                sb4.append(", ");
                                                formatTimeMs(sb4, totalDurationMsLocked2);
                                                sb4.append("background (");
                                                sb4.append(countLocked2);
                                                sb4.append(" times)");
                                            }
                                        } else {
                                            sb4.append("(not used)");
                                        }
                                    } else {
                                        j24 = j24;
                                        sensorStats = sensorStats;
                                        size5 = size5;
                                        j35 = j26;
                                        sb4.append("(not used)");
                                    }
                                    printWriter4.println(sb4.toString());
                                    i12++;
                                    j26 = j35;
                                    sb3 = sb4;
                                    z5 = true;
                                    j24 = j24;
                                    sensorStats = sensorStats;
                                    size5 = size5;
                                    i9 = 0;
                                }
                                j27 = j24;
                                StringBuilder sb9 = sb3;
                                j28 = j26;
                                sb = sb9;
                                zPrintTimer = z5 | printTimer(printWriter, sb9, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                                j29 = 0;
                                i13 = 0;
                                while (i13 < 7) {
                                    Uid uid8 = uid3;
                                    long j76 = j27;
                                    processStateTime = uid8.getProcessStateTime(i13, j76, i10);
                                    if (processStateTime > 0) {
                                        j29 += processStateTime;
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("    ");
                                        sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                                        sb.append(" for: ");
                                        formatTimeMs(sb, (processStateTime + 500) / 1000);
                                        printWriter4.println(sb.toString());
                                        zPrintTimer = true;
                                    }
                                    i13++;
                                    j27 = j76;
                                    uid3 = uid8;
                                }
                                uid4 = uid3;
                                j30 = j27;
                                if (j29 > 0) {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Total running: ");
                                    formatTimeMs(sb, (j29 + 500) / 1000);
                                    printWriter4.println(sb.toString());
                                }
                                userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                                systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                                if (userCpuTimeUs <= 0 || systemCpuTimeUs > 0) {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Total cpu time: u=");
                                    formatTimeMs(sb, userCpuTimeUs / 1000);
                                    sb.append("s=");
                                    formatTimeMs(sb, systemCpuTimeUs / 1000);
                                    printWriter4.println(sb.toString());
                                }
                                cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                                if (cpuFreqTimes != null) {
                                    sb.setLength(0);
                                    sb.append("    Total cpu time per freq:");
                                    for (long j77 : cpuFreqTimes) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                                    }
                                    printWriter4.println(sb.toString());
                                }
                                screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                                if (screenOffCpuFreqTimes != null) {
                                    sb.setLength(0);
                                    sb.append("    Total screen-off cpu time per freq:");
                                    for (long j78 : screenOffCpuFreqTimes) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                                    }
                                    printWriter4.println(sb.toString());
                                }
                                i14 = 0;
                                while (i14 < 7) {
                                    cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                                    if (cpuFreqTimes2 != null) {
                                        sb.setLength(0);
                                        sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                        i18 = 0;
                                        while (i18 < cpuFreqTimes2.length) {
                                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                            i18++;
                                            sparseArray = sparseArray;
                                            j28 = j28;
                                        }
                                        sparseArray3 = sparseArray;
                                        j34 = j28;
                                        printWriter4.println(sb.toString());
                                    } else {
                                        sparseArray3 = sparseArray;
                                        j34 = j28;
                                    }
                                    screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                                    if (screenOffCpuFreqTimes2 != null) {
                                        sb.setLength(0);
                                        sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                        for (long j79 : screenOffCpuFreqTimes2) {
                                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                                        }
                                        printWriter4.println(sb.toString());
                                    }
                                    i14++;
                                    sparseArray = sparseArray3;
                                    j28 = j34;
                                }
                                sparseArray2 = sparseArray;
                                j31 = j28;
                                processStats = uid4.getProcessStats();
                                size6 = processStats.size() - 1;
                                z6 = zPrintTimer;
                                while (size6 >= 0) {
                                    procValueAt = processStats.valueAt(size6);
                                    userTime = procValueAt.getUserTime(i10);
                                    systemTime = procValueAt.getSystemTime(i10);
                                    long j80 = j30;
                                    foregroundTime = procValueAt.getForegroundTime(i10);
                                    starts2 = procValueAt.getStarts(i10);
                                    boolean z19 = z6;
                                    numCrashes = procValueAt.getNumCrashes(i10);
                                    Uid uid9 = uid4;
                                    numAnrs = procValueAt.getNumAnrs(i10);
                                    if (i10 == 0) {
                                        iCountExcessivePowers = procValueAt.countExcessivePowers();
                                    } else {
                                        iCountExcessivePowers = 0;
                                    }
                                    if (userTime != 0 && systemTime == 0 && foregroundTime == 0 && starts2 == 0 && iCountExcessivePowers == 0 && numCrashes == 0 && numAnrs == 0) {
                                        z6 = z19;
                                    } else {
                                        proc = procValueAt;
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("    Proc ");
                                        sb.append(processStats.keyAt(size6));
                                        sb.append(":\n");
                                        sb.append(str);
                                        sb.append("      CPU: ");
                                        formatTimeMs(sb, userTime);
                                        sb.append("usr + ");
                                        formatTimeMs(sb, systemTime);
                                        sb.append("krn ; ");
                                        formatTimeMs(sb, foregroundTime);
                                        sb.append(FOREGROUND_ACTIVITY_DATA);
                                        if (starts2 == 0 || numCrashes != 0 || numAnrs != 0) {
                                            sb.append("\n");
                                            sb.append(str);
                                            sb.append("      ");
                                            if (starts2 != 0) {
                                                sb.append(starts2);
                                                sb.append(" starts");
                                                z9 = true;
                                            } else {
                                                z9 = false;
                                            }
                                            if (numCrashes != 0) {
                                                if (z9) {
                                                    sb.append(", ");
                                                }
                                                sb.append(numCrashes);
                                                sb.append(" crashes");
                                                z9 = true;
                                            }
                                            if (numAnrs != 0) {
                                                if (z9) {
                                                    sb.append(", ");
                                                }
                                                sb.append(numAnrs);
                                                sb.append(" anrs");
                                            }
                                        }
                                        printWriter4.println(sb.toString());
                                        i16 = 0;
                                        while (i16 < iCountExcessivePowers) {
                                            Uid.Proc proc2 = proc;
                                            excessivePower = proc2.getExcessivePower(i16);
                                            if (excessivePower != null) {
                                                printWriter.print(str);
                                                printWriter4.print("      * Killed for ");
                                                if (excessivePower.type == 2) {
                                                    printWriter4.print(CPU_DATA);
                                                } else {
                                                    printWriter4.print("unknown");
                                                }
                                                printWriter4.print(" use: ");
                                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                                printWriter4.print(" over ");
                                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                                if (excessivePower.overTime != 0) {
                                                    printWriter4.print(" (");
                                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                                    printWriter4.println("%)");
                                                }
                                            }
                                            i16++;
                                            proc = proc2;
                                        }
                                        z6 = true;
                                    }
                                    size6--;
                                    j30 = j80;
                                    uid4 = uid9;
                                    i10 = i;
                                }
                                j32 = j30;
                                packageStats = uid4.getPackageStats();
                                size7 = packageStats.size() - 1;
                                z7 = z6;
                                while (size7 >= 0) {
                                    printWriter.print(str);
                                    printWriter4.print("    Apk ");
                                    printWriter4.print(packageStats.keyAt(size7));
                                    printWriter4.println(SettingsStringUtil.DELIMITER);
                                    Uid.Pkg pkgValueAt = packageStats.valueAt(size7);
                                    wakeupAlarmStats = pkgValueAt.getWakeupAlarmStats();
                                    size8 = wakeupAlarmStats.size() - 1;
                                    z8 = false;
                                    while (size8 >= 0) {
                                        printWriter.print(str);
                                        printWriter4.print("      Wakeup alarm ");
                                        printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                        printWriter4.print(": ");
                                        printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                        printWriter4.println(" times");
                                        size8--;
                                        z8 = true;
                                    }
                                    serviceStats = pkgValueAt.getServiceStats();
                                    size9 = serviceStats.size() - 1;
                                    while (size9 >= 0) {
                                        Uid.Pkg.Serv servValueAt = serviceStats.valueAt(size9);
                                        long j81 = j56;
                                        startTime = servValueAt.getStartTime(j81, i);
                                        starts = servValueAt.getStarts(i);
                                        launches = servValueAt.getLaunches(i);
                                        if (startTime == 0 || starts != 0 || launches != 0) {
                                            sb.setLength(0);
                                            sb.append(str);
                                            sb.append("      Service ");
                                            sb.append(serviceStats.keyAt(size9));
                                            sb.append(":\n");
                                            sb.append(str);
                                            sb.append("        Created for: ");
                                            formatTimeMs(sb, startTime / 1000);
                                            sb.append("uptime\n");
                                            sb.append(str);
                                            sb.append("        Starts: ");
                                            sb.append(starts);
                                            sb.append(", launches: ");
                                            sb.append(launches);
                                            printWriter4.println(sb.toString());
                                            z8 = true;
                                        }
                                        size9--;
                                        j56 = j81;
                                    }
                                    long j82 = j56;
                                    if (!z8) {
                                        printWriter.print(str);
                                        printWriter4.println("      (nothing executed)");
                                    }
                                    size7--;
                                    j56 = j82;
                                    z7 = true;
                                }
                                j33 = j56;
                                i15 = 0;
                                if (!z7) {
                                    printWriter.print(str);
                                    printWriter4.println("    (nothing executed)");
                                }
                                i46 = i11 + 1;
                                i32 = i2;
                                printWriter2 = printWriter4;
                                i5 = i15;
                                j56 = j33;
                                i35 = i6;
                                j3 = j11;
                                j58 = j25;
                                sparseArray7 = sparseArray2;
                                j57 = j31;
                                j2 = j32;
                                batteryStats = this;
                            } else {
                                j22 = j18;
                            }
                            str4 = str;
                            syncStats = uid2.getSyncStats();
                            size2 = syncStats.size() - 1;
                            while (size2 >= 0) {
                                Timer timerValueAt3 = syncStats.valueAt(size2);
                                totalTimeLocked3 = (timerValueAt3.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                countLocked5 = timerValueAt3.getCountLocked(i7);
                                subTimer2 = timerValueAt3.getSubTimer();
                                if (subTimer2 != null) {
                                    totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                                } else {
                                    totalDurationMsLocked4 = -1;
                                }
                                if (subTimer2 != null) {
                                    countLocked6 = subTimer2.getCountLocked(i7);
                                } else {
                                    countLocked6 = i3;
                                }
                                sb2.setLength(0);
                                sb2.append(str4);
                                sb2.append("    Sync ");
                                sb2.append(syncStats.keyAt(size2));
                                sb2.append(": ");
                                if (totalTimeLocked3 != 0) {
                                    formatTimeMs(sb2, totalTimeLocked3);
                                    sb2.append("realtime (");
                                    sb2.append(countLocked5);
                                    sb2.append(" times)");
                                    if (totalDurationMsLocked4 > 0) {
                                        sb2.append(", ");
                                        formatTimeMs(sb2, totalDurationMsLocked4);
                                        sb2.append("background (");
                                        sb2.append(countLocked6);
                                        sb2.append(" times)");
                                    }
                                } else {
                                    sb2.append("(not used)");
                                }
                                printWriter4.println(sb2.toString());
                                size2--;
                                j21 = j21;
                                z4 = true;
                            }
                            j23 = j21;
                            jobStats = uid2.getJobStats();
                            size3 = jobStats.size() - 1;
                            while (size3 >= 0) {
                                Timer timerValueAt4 = jobStats.valueAt(size3);
                                totalTimeLocked2 = (timerValueAt4.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                countLocked3 = timerValueAt4.getCountLocked(i7);
                                subTimer = timerValueAt4.getSubTimer();
                                if (subTimer != null) {
                                    j36 = j23;
                                    totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                                } else {
                                    j36 = j23;
                                    totalDurationMsLocked3 = -1;
                                }
                                if (subTimer != null) {
                                    countLocked4 = subTimer.getCountLocked(i7);
                                } else {
                                    countLocked4 = i3;
                                }
                                sb2.setLength(0);
                                sb2.append(str4);
                                sb2.append("    Job ");
                                sb2.append(jobStats.keyAt(size3));
                                sb2.append(": ");
                                if (totalTimeLocked2 != 0) {
                                    formatTimeMs(sb2, totalTimeLocked2);
                                    sb2.append("realtime (");
                                    sb2.append(countLocked3);
                                    sb2.append(" times)");
                                    if (totalDurationMsLocked3 > 0) {
                                        sb2.append(", ");
                                        formatTimeMs(sb2, totalDurationMsLocked3);
                                        sb2.append("background (");
                                        sb2.append(countLocked4);
                                        sb2.append(" times)");
                                    }
                                } else {
                                    sb2.append("(not used)");
                                }
                                printWriter4.println(sb2.toString());
                                size3--;
                                j23 = j36;
                                j22 = j22;
                                z4 = true;
                            }
                            long j710 = j22;
                            long j711 = j23;
                            i9 = 0;
                            jobCompletionStats = uid2.getJobCompletionStats();
                            while (size4 >= 0) {
                                sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                                if (sparseIntArrayValueAt != null) {
                                    printWriter.print(str);
                                    printWriter4.print("    Job Completions ");
                                    printWriter4.print(jobCompletionStats.keyAt(size4));
                                    printWriter4.print(SettingsStringUtil.DELIMITER);
                                    while (i21 < sparseIntArrayValueAt.size()) {
                                        printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                        printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                        printWriter4.print("(");
                                        printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                        printWriter4.print("x)");
                                    }
                                    printWriter.println();
                                }
                            }
                            uid2.getDeferredJobsLineLocked(sb2, i7);
                            if (sb2.length() > 0) {
                                printWriter4.print("    Jobs deferred on launch ");
                                printWriter4.println(sb2.toString());
                            }
                            j24 = j710;
                            uid3 = uid2;
                            sb3 = sb2;
                            i10 = i7;
                            j25 = j67;
                            sparseArray = sparseArray8;
                            j26 = j711;
                            i11 = i48;
                            boolean zPrintTimer3 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                            sensorStats = uid3.getSensorStats();
                            size5 = sensorStats.size();
                            z5 = zPrintTimer3;
                            i12 = 0;
                            while (i12 < size5) {
                                sensorValueAt = sensorStats.valueAt(i12);
                                sensorStats.keyAt(i12);
                                sb4 = sb3;
                                sb4.setLength(i9);
                                sb4.append(str);
                                sb4.append("    Sensor ");
                                handle = sensorValueAt.getHandle();
                                if (handle == -10000) {
                                    sb4.append("GPS");
                                } else {
                                    sb4.append(handle);
                                }
                                sb4.append(": ");
                                sensorTime = sensorValueAt.getSensorTime();
                                if (sensorTime != null) {
                                    totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                                    countLocked = sensorTime.getCountLocked(i10);
                                    sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                    if (sensorBackgroundTime != null) {
                                        countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                                    } else {
                                        countLocked2 = i9;
                                    }
                                    j35 = j26;
                                    totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                                    if (sensorBackgroundTime != null) {
                                        totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                                    } else {
                                        totalDurationMsLocked2 = 0;
                                    }
                                    if (totalTimeLocked != 0) {
                                        if (totalDurationMsLocked != totalTimeLocked) {
                                            formatTimeMs(sb4, totalTimeLocked);
                                            sb4.append("blamed realtime, ");
                                        }
                                        formatTimeMs(sb4, totalDurationMsLocked);
                                        sb4.append("realtime (");
                                        sb4.append(countLocked);
                                        sb4.append(" times)");
                                        if (totalDurationMsLocked2 == 0) {
                                            sb4.append(", ");
                                            formatTimeMs(sb4, totalDurationMsLocked2);
                                            sb4.append("background (");
                                            sb4.append(countLocked2);
                                            sb4.append(" times)");
                                        } else {
                                            sb4.append(", ");
                                            formatTimeMs(sb4, totalDurationMsLocked2);
                                            sb4.append("background (");
                                            sb4.append(countLocked2);
                                            sb4.append(" times)");
                                        }
                                    } else {
                                        sb4.append("(not used)");
                                    }
                                } else {
                                    j24 = j24;
                                    sensorStats = sensorStats;
                                    size5 = size5;
                                    j35 = j26;
                                    sb4.append("(not used)");
                                }
                                printWriter4.println(sb4.toString());
                                i12++;
                                j26 = j35;
                                sb3 = sb4;
                                z5 = true;
                                j24 = j24;
                                sensorStats = sensorStats;
                                size5 = size5;
                                i9 = 0;
                            }
                            j27 = j24;
                            StringBuilder sb10 = sb3;
                            j28 = j26;
                            sb = sb10;
                            zPrintTimer = z5 | printTimer(printWriter, sb10, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                            j29 = 0;
                            i13 = 0;
                            while (i13 < 7) {
                                Uid uid10 = uid3;
                                long j712 = j27;
                                processStateTime = uid10.getProcessStateTime(i13, j712, i10);
                                if (processStateTime > 0) {
                                    j29 += processStateTime;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    ");
                                    sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                                    sb.append(" for: ");
                                    formatTimeMs(sb, (processStateTime + 500) / 1000);
                                    printWriter4.println(sb.toString());
                                    zPrintTimer = true;
                                }
                                i13++;
                                j27 = j712;
                                uid3 = uid10;
                            }
                            uid4 = uid3;
                            j30 = j27;
                            if (j29 > 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total running: ");
                                formatTimeMs(sb, (j29 + 500) / 1000);
                                printWriter4.println(sb.toString());
                            }
                            userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                            systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                            if (userCpuTimeUs <= 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total cpu time: u=");
                                formatTimeMs(sb, userCpuTimeUs / 1000);
                                sb.append("s=");
                                formatTimeMs(sb, systemCpuTimeUs / 1000);
                                printWriter4.println(sb.toString());
                            } else {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total cpu time: u=");
                                formatTimeMs(sb, userCpuTimeUs / 1000);
                                sb.append("s=");
                                formatTimeMs(sb, systemCpuTimeUs / 1000);
                                printWriter4.println(sb.toString());
                            }
                            cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                            if (cpuFreqTimes != null) {
                                sb.setLength(0);
                                sb.append("    Total cpu time per freq:");
                                while (i20 < cpuFreqTimes.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                                }
                                printWriter4.println(sb.toString());
                            }
                            screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                            if (screenOffCpuFreqTimes != null) {
                                sb.setLength(0);
                                sb.append("    Total screen-off cpu time per freq:");
                                while (i19 < screenOffCpuFreqTimes.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                                }
                                printWriter4.println(sb.toString());
                            }
                            i14 = 0;
                            while (i14 < 7) {
                                cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                                if (cpuFreqTimes2 != null) {
                                    sb.setLength(0);
                                    sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                    i18 = 0;
                                    while (i18 < cpuFreqTimes2.length) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                        i18++;
                                        sparseArray = sparseArray;
                                        j28 = j28;
                                    }
                                    sparseArray3 = sparseArray;
                                    j34 = j28;
                                    printWriter4.println(sb.toString());
                                } else {
                                    sparseArray3 = sparseArray;
                                    j34 = j28;
                                }
                                screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                                if (screenOffCpuFreqTimes2 != null) {
                                    sb.setLength(0);
                                    sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                    while (i17 < screenOffCpuFreqTimes2.length) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                                    }
                                    printWriter4.println(sb.toString());
                                }
                                i14++;
                                sparseArray = sparseArray3;
                                j28 = j34;
                            }
                            sparseArray2 = sparseArray;
                            j31 = j28;
                            processStats = uid4.getProcessStats();
                            size6 = processStats.size() - 1;
                            z6 = zPrintTimer;
                            while (size6 >= 0) {
                                procValueAt = processStats.valueAt(size6);
                                userTime = procValueAt.getUserTime(i10);
                                systemTime = procValueAt.getSystemTime(i10);
                                long j83 = j30;
                                foregroundTime = procValueAt.getForegroundTime(i10);
                                starts2 = procValueAt.getStarts(i10);
                                boolean z110 = z6;
                                numCrashes = procValueAt.getNumCrashes(i10);
                                Uid uid11 = uid4;
                                numAnrs = procValueAt.getNumAnrs(i10);
                                if (i10 == 0) {
                                    iCountExcessivePowers = procValueAt.countExcessivePowers();
                                } else {
                                    iCountExcessivePowers = 0;
                                }
                                if (userTime != 0) {
                                    proc = procValueAt;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Proc ");
                                    sb.append(processStats.keyAt(size6));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("      CPU: ");
                                    formatTimeMs(sb, userTime);
                                    sb.append("usr + ");
                                    formatTimeMs(sb, systemTime);
                                    sb.append("krn ; ");
                                    formatTimeMs(sb, foregroundTime);
                                    sb.append(FOREGROUND_ACTIVITY_DATA);
                                    if (starts2 == 0) {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    } else {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    }
                                    printWriter4.println(sb.toString());
                                    i16 = 0;
                                    while (i16 < iCountExcessivePowers) {
                                        Uid.Proc proc3 = proc;
                                        excessivePower = proc3.getExcessivePower(i16);
                                        if (excessivePower != null) {
                                            printWriter.print(str);
                                            printWriter4.print("      * Killed for ");
                                            if (excessivePower.type == 2) {
                                                printWriter4.print(CPU_DATA);
                                            } else {
                                                printWriter4.print("unknown");
                                            }
                                            printWriter4.print(" use: ");
                                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                            printWriter4.print(" over ");
                                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                            if (excessivePower.overTime != 0) {
                                                printWriter4.print(" (");
                                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                                printWriter4.println("%)");
                                            }
                                        }
                                        i16++;
                                        proc = proc3;
                                    }
                                    z6 = true;
                                } else {
                                    proc = procValueAt;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Proc ");
                                    sb.append(processStats.keyAt(size6));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("      CPU: ");
                                    formatTimeMs(sb, userTime);
                                    sb.append("usr + ");
                                    formatTimeMs(sb, systemTime);
                                    sb.append("krn ; ");
                                    formatTimeMs(sb, foregroundTime);
                                    sb.append(FOREGROUND_ACTIVITY_DATA);
                                    if (starts2 == 0) {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    } else {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    }
                                    printWriter4.println(sb.toString());
                                    i16 = 0;
                                    while (i16 < iCountExcessivePowers) {
                                        Uid.Proc proc4 = proc;
                                        excessivePower = proc4.getExcessivePower(i16);
                                        if (excessivePower != null) {
                                            printWriter.print(str);
                                            printWriter4.print("      * Killed for ");
                                            if (excessivePower.type == 2) {
                                                printWriter4.print(CPU_DATA);
                                            } else {
                                                printWriter4.print("unknown");
                                            }
                                            printWriter4.print(" use: ");
                                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                            printWriter4.print(" over ");
                                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                            if (excessivePower.overTime != 0) {
                                                printWriter4.print(" (");
                                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                                printWriter4.println("%)");
                                            }
                                        }
                                        i16++;
                                        proc = proc4;
                                    }
                                    z6 = true;
                                }
                                size6--;
                                j30 = j83;
                                uid4 = uid11;
                                i10 = i;
                            }
                            j32 = j30;
                            packageStats = uid4.getPackageStats();
                            size7 = packageStats.size() - 1;
                            z7 = z6;
                            while (size7 >= 0) {
                                printWriter.print(str);
                                printWriter4.print("    Apk ");
                                printWriter4.print(packageStats.keyAt(size7));
                                printWriter4.println(SettingsStringUtil.DELIMITER);
                                Uid.Pkg pkgValueAt2 = packageStats.valueAt(size7);
                                wakeupAlarmStats = pkgValueAt2.getWakeupAlarmStats();
                                size8 = wakeupAlarmStats.size() - 1;
                                z8 = false;
                                while (size8 >= 0) {
                                    printWriter.print(str);
                                    printWriter4.print("      Wakeup alarm ");
                                    printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                    printWriter4.print(": ");
                                    printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                    printWriter4.println(" times");
                                    size8--;
                                    z8 = true;
                                }
                                serviceStats = pkgValueAt2.getServiceStats();
                                size9 = serviceStats.size() - 1;
                                while (size9 >= 0) {
                                    Uid.Pkg.Serv servValueAt2 = serviceStats.valueAt(size9);
                                    long j84 = j56;
                                    startTime = servValueAt2.getStartTime(j84, i);
                                    starts = servValueAt2.getStarts(i);
                                    launches = servValueAt2.getLaunches(i);
                                    if (startTime == 0) {
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("      Service ");
                                        sb.append(serviceStats.keyAt(size9));
                                        sb.append(":\n");
                                        sb.append(str);
                                        sb.append("        Created for: ");
                                        formatTimeMs(sb, startTime / 1000);
                                        sb.append("uptime\n");
                                        sb.append(str);
                                        sb.append("        Starts: ");
                                        sb.append(starts);
                                        sb.append(", launches: ");
                                        sb.append(launches);
                                        printWriter4.println(sb.toString());
                                        z8 = true;
                                    } else {
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("      Service ");
                                        sb.append(serviceStats.keyAt(size9));
                                        sb.append(":\n");
                                        sb.append(str);
                                        sb.append("        Created for: ");
                                        formatTimeMs(sb, startTime / 1000);
                                        sb.append("uptime\n");
                                        sb.append(str);
                                        sb.append("        Starts: ");
                                        sb.append(starts);
                                        sb.append(", launches: ");
                                        sb.append(launches);
                                        printWriter4.println(sb.toString());
                                        z8 = true;
                                    }
                                    size9--;
                                    j56 = j84;
                                }
                                long j85 = j56;
                                if (!z8) {
                                    printWriter.print(str);
                                    printWriter4.println("      (nothing executed)");
                                }
                                size7--;
                                j56 = j85;
                                z7 = true;
                            }
                            j33 = j56;
                            i15 = 0;
                            if (!z7) {
                                printWriter.print(str);
                                printWriter4.println("    (nothing executed)");
                            }
                            i46 = i11 + 1;
                            i32 = i2;
                            printWriter2 = printWriter4;
                            i5 = i15;
                            j56 = j33;
                            i35 = i6;
                            j3 = j11;
                            j58 = j25;
                            sparseArray7 = sparseArray2;
                            j57 = j31;
                            j2 = j32;
                            batteryStats = this;
                        }
                        j7 = wifiScanBackgroundTime;
                        sb2 = sb7;
                        sb2.setLength(0);
                        long j86 = j7;
                        str3 = str;
                        sb2.append(str3);
                        sb2.append("    Wifi Running: ");
                        long j87 = j6;
                        formatTimeMs(sb2, j8 / 1000);
                        sb2.append("(");
                        j9 = j3;
                        sb2.append(batteryStats.formatRatioLocked(j8, j9));
                        sb2.append(")\n");
                        sb2.append(str3);
                        sb2.append("    Full Wifi Lock: ");
                        formatTimeMs(sb2, fullWifiLockTime / 1000);
                        sb2.append("(");
                        sb2.append(batteryStats.formatRatioLocked(fullWifiLockTime, j9));
                        sb2.append(")\n");
                        sb2.append(str3);
                        sb2.append("    Wifi Scan (blamed): ");
                        formatTimeMs(sb2, j5 / 1000);
                        sb2.append("(");
                        sb2.append(batteryStats.formatRatioLocked(j5, j9));
                        sb2.append(") ");
                        sb2.append(wifiScanCount);
                        sb2.append("x\n");
                        sb2.append(str3);
                        sb2.append("    Wifi Scan (actual): ");
                        formatTimeMs(sb2, j87 / 1000);
                        sb2.append("(");
                        j10 = j64;
                        sb2.append(batteryStats.formatRatioLocked(j87, batteryStats.computeBatteryRealtime(j10, 0)));
                        sb2.append(") ");
                        sb2.append(wifiScanCount);
                        sb2.append("x\n");
                        sb2.append(str3);
                        sb2.append("    Background Wifi Scan: ");
                        formatTimeMs(sb2, j86 / 1000);
                        sb2.append("(");
                        sb2.append(batteryStats.formatRatioLocked(j86, batteryStats.computeBatteryRealtime(j10, 0)));
                        sb2.append(") ");
                        sb2.append(wifiScanBackgroundCount);
                        sb2.append("x");
                        printWriter2.println(sb2.toString());
                        if (wifiRadioApWakeupCount > 0) {
                            sb2.setLength(0);
                            sb2.append(str3);
                            sb2.append("    WiFi AP wakeups: ");
                            sb2.append(wifiRadioApWakeupCount);
                            printWriter2.println(sb2.toString());
                        }
                        j11 = j9;
                        printControllerActivityIfInteresting(printWriter, sb2, str3 + "  ", WIFI_CONTROLLER_NAME, uid5.getWifiControllerActivity(), i);
                        if (networkActivityBytes11 <= 0) {
                            printWriter.print(str);
                            printWriter2.print("    Bluetooth network: ");
                            printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                            printWriter2.print(" received, ");
                            printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                            printWriter2.println(" sent");
                        } else {
                            printWriter.print(str);
                            printWriter2.print("    Bluetooth network: ");
                            printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                            printWriter2.print(" received, ");
                            printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                            printWriter2.println(" sent");
                        }
                        bluetoothScanTimer = uid5.getBluetoothScanTimer();
                        if (bluetoothScanTimer != null) {
                            totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                            if (totalTimeLocked5 != 0) {
                                countLocked8 = bluetoothScanTimer.getCountLocked(i);
                                bluetoothScanBackgroundTimer = uid5.getBluetoothScanBackgroundTimer();
                                if (bluetoothScanBackgroundTimer != null) {
                                    countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                                } else {
                                    countLocked9 = 0;
                                }
                                totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j57);
                                if (bluetoothScanBackgroundTimer != null) {
                                    totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j57);
                                } else {
                                    totalDurationMsLocked7 = 0;
                                }
                                if (uid5.getBluetoothScanResultCounter() != null) {
                                    countLocked10 = uid5.getBluetoothScanResultCounter().getCountLocked(i);
                                } else {
                                    countLocked10 = 0;
                                }
                                if (uid5.getBluetoothScanResultBgCounter() != null) {
                                    countLocked11 = uid5.getBluetoothScanResultBgCounter().getCountLocked(i);
                                } else {
                                    countLocked11 = 0;
                                }
                                bluetoothUnoptimizedScanTimer = uid5.getBluetoothUnoptimizedScanTimer();
                                if (bluetoothUnoptimizedScanTimer != null) {
                                    totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j57);
                                } else {
                                    totalDurationMsLocked8 = 0;
                                }
                                if (bluetoothUnoptimizedScanTimer != null) {
                                    maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j57);
                                } else {
                                    maxDurationMsLocked = 0;
                                }
                                bluetoothUnoptimizedScanBackgroundTimer = uid5.getBluetoothUnoptimizedScanBackgroundTimer();
                                if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                    totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j57);
                                } else {
                                    totalDurationMsLocked9 = 0;
                                }
                                if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                    maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j57);
                                } else {
                                    maxDurationMsLocked2 = 0;
                                }
                                sb2.setLength(0);
                                if (totalDurationMsLocked6 != totalTimeLocked5) {
                                    sb2.append(str3);
                                    sb2.append("    Bluetooth Scan (total blamed realtime): ");
                                    formatTimeMs(sb2, totalTimeLocked5);
                                    sb2.append(" (");
                                    sb2.append(countLocked8);
                                    sb2.append(" times)");
                                    if (bluetoothScanTimer.isRunningLocked()) {
                                        sb2.append(" (currently running)");
                                    }
                                    sb2.append("\n");
                                }
                                sb2.append(str3);
                                sb2.append("    Bluetooth Scan (total actual realtime): ");
                                formatTimeMs(sb2, totalDurationMsLocked6);
                                sb2.append(" (");
                                sb2.append(countLocked8);
                                sb2.append(" times)");
                                if (bluetoothScanTimer.isRunningLocked()) {
                                    sb2.append(" (currently running)");
                                }
                                sb2.append("\n");
                                if (totalDurationMsLocked7 <= 0) {
                                    sb2.append(str3);
                                    sb2.append("    Bluetooth Scan (background realtime): ");
                                    formatTimeMs(sb2, totalDurationMsLocked7);
                                    sb2.append(" (");
                                    sb2.append(countLocked9);
                                    sb2.append(" times)");
                                    if (bluetoothScanBackgroundTimer != null) {
                                        sb2.append(" (currently running in background)");
                                    }
                                    sb2.append("\n");
                                } else {
                                    sb2.append(str3);
                                    sb2.append("    Bluetooth Scan (background realtime): ");
                                    formatTimeMs(sb2, totalDurationMsLocked7);
                                    sb2.append(" (");
                                    sb2.append(countLocked9);
                                    sb2.append(" times)");
                                    if (bluetoothScanBackgroundTimer != null) {
                                        sb2.append(" (currently running in background)");
                                    }
                                    sb2.append("\n");
                                }
                                sb2.append(str3);
                                sb2.append("    Bluetooth Scan Results: ");
                                sb2.append(countLocked10);
                                sb2.append(" (");
                                sb2.append(countLocked11);
                                sb2.append(" in background)");
                                j40 = totalDurationMsLocked8;
                                if (j40 <= 0) {
                                    j41 = totalDurationMsLocked9;
                                    if (j41 > 0) {
                                    }
                                    printWriter3 = printWriter;
                                    printWriter3.println(sb2.toString());
                                    z3 = true;
                                } else {
                                    j41 = totalDurationMsLocked9;
                                }
                                sb2.append("\n");
                                sb2.append(str3);
                                sb2.append("    Unoptimized Bluetooth Scan (realtime): ");
                                formatTimeMs(sb2, j40);
                                sb2.append(" (max ");
                                formatTimeMs(sb2, maxDurationMsLocked);
                                sb2.append(")");
                                if (bluetoothUnoptimizedScanTimer != 0) {
                                    sb2.append(" (currently running unoptimized)");
                                }
                                if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                    sb2.append("\n");
                                    sb2.append(str3);
                                    sb2.append("    Unoptimized Bluetooth Scan (background realtime): ");
                                    formatTimeMs(sb2, j41);
                                    sb2.append(" (max ");
                                    formatTimeMs(sb2, maxDurationMsLocked2);
                                    sb2.append(")");
                                    if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                        sb2.append(" (currently running unoptimized in background)");
                                    }
                                }
                                printWriter3 = printWriter;
                                printWriter3.println(sb2.toString());
                                z3 = true;
                            } else {
                                j10 = j10;
                                printWriter3 = printWriter2;
                                j57 = j57;
                                z3 = false;
                            }
                        } else {
                            j10 = j10;
                            printWriter3 = printWriter2;
                            j57 = j57;
                            z3 = false;
                        }
                        if (uid5.hasUserActivity()) {
                            i22 = 0;
                            z11 = false;
                            while (i22 < 4) {
                                Uid uid12 = uid5;
                                userActivityCount = uid12.getUserActivityCount(i22, i);
                                if (userActivityCount != 0) {
                                    if (z11) {
                                        sb2.setLength(0);
                                        sb2.append("    User activity: ");
                                        z11 = true;
                                    } else {
                                        sb2.append(", ");
                                    }
                                    sb2.append(userActivityCount);
                                    sb2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                    sb2.append(Uid.USER_ACTIVITY_TYPES[i22]);
                                }
                                i22++;
                                uid5 = uid12;
                            }
                            uid = uid5;
                            i7 = i;
                            if (z11) {
                                printWriter3.println(sb2.toString());
                            }
                        } else {
                            uid = uid5;
                            i7 = i;
                        }
                        wakelockStats = uid.getWakelockStats();
                        size = wakelockStats.size() - 1;
                        j12 = 0;
                        j13 = 0;
                        j14 = 0;
                        jComputeWakeLock = 0;
                        i8 = 0;
                        while (size >= 0) {
                            Uid.Wakelock wakelockValueAt3 = wakelockStats.valueAt(size);
                            sb2.setLength(0);
                            sb2.append(str3);
                            sb2.append("    Wake lock ");
                            sb2.append(wakelockStats.keyAt(size));
                            Uid uid13 = uid;
                            long j610 = j12;
                            ArrayMap<String, ? extends Uid.Wakelock> arrayMap2 = wakelockStats;
                            int i410 = size;
                            long j611 = j13;
                            long j713 = j10;
                            long j714 = j14;
                            long j715 = j57;
                            String strPrintWakeLock3 = printWakeLock(sb2, wakelockValueAt3.getWakeTime(1), j713, "full", i, ": ");
                            wakeTime = wakelockValueAt3.getWakeTime(0);
                            String strPrintWakeLock4 = printWakeLock(sb2, wakeTime, j713, Slice.HINT_PARTIAL, i, strPrintWakeLock3);
                            if (wakeTime != null) {
                                subTimer4 = wakeTime.getSubTimer();
                            } else {
                                subTimer4 = null;
                            }
                            long j716 = j10;
                            StringBuilder sb11 = sb2;
                            printWakeLock(sb11, wakelockValueAt3.getWakeTime(18), j716, "draw", i, printWakeLock(sb11, wakelockValueAt3.getWakeTime(2), j716, Context.WINDOW_SERVICE, i, printWakeLock(sb2, subTimer4, j716, "background partial", i, strPrintWakeLock4)));
                            sb2.append(" realtime");
                            printWriter3.println(sb2.toString());
                            i8++;
                            long jComputeWakeLock6 = j714 + computeWakeLock(wakelockValueAt3.getWakeTime(1), j716, i7);
                            long jComputeWakeLock7 = computeWakeLock(wakelockValueAt3.getWakeTime(0), j716, i7) + j611;
                            long jComputeWakeLock8 = j610 + computeWakeLock(wakelockValueAt3.getWakeTime(2), j716, i7);
                            jComputeWakeLock += computeWakeLock(wakelockValueAt3.getWakeTime(18), j716, i7);
                            uid = uid13;
                            j57 = j715;
                            j14 = jComputeWakeLock6;
                            str3 = str;
                            j13 = jComputeWakeLock7;
                            j12 = jComputeWakeLock8;
                            wakelockStats = arrayMap2;
                            size = i410 - 1;
                            z3 = true;
                        }
                        j15 = j12;
                        j16 = j13;
                        z4 = z3;
                        j17 = j14;
                        uid2 = uid;
                        j18 = j10;
                        j19 = j57;
                        j20 = jComputeWakeLock;
                        if (i8 > 1) {
                            if (uid2.getAggregatedPartialWakelockTimer() != null) {
                                Timer aggregatedPartialWakelockTimer2 = uid2.getAggregatedPartialWakelockTimer();
                                long totalDurationMsLocked11 = aggregatedPartialWakelockTimer2.getTotalDurationMsLocked(j19);
                                subTimer3 = aggregatedPartialWakelockTimer2.getSubTimer();
                                if (subTimer3 != null) {
                                    totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j19);
                                } else {
                                    totalDurationMsLocked5 = 0;
                                }
                                j39 = j19;
                                j37 = totalDurationMsLocked11;
                                j38 = totalDurationMsLocked5;
                            } else {
                                j37 = 0;
                                j38 = 0;
                                j39 = j19;
                            }
                            if (j37 != 0) {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("    TOTAL wake: ");
                                if (j17 != 0) {
                                    formatTimeMs(sb2, j17);
                                    sb2.append("full");
                                    z10 = true;
                                } else {
                                    z10 = false;
                                }
                                if (j16 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j16);
                                    sb2.append("blamed partial");
                                    z10 = true;
                                }
                                if (j37 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j37);
                                    sb2.append("actual partial");
                                    z10 = true;
                                }
                                if (j38 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j38);
                                    sb2.append("actual background partial");
                                    z10 = true;
                                }
                                if (j15 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j15);
                                    sb2.append(Context.WINDOW_SERVICE);
                                    z10 = true;
                                }
                                if (j20 != 0) {
                                    if (z10) {
                                        sb2.append(",");
                                    }
                                    formatTimeMs(sb2, j20);
                                    sb2.append("draw");
                                }
                                sb2.append(" realtime");
                                j21 = j39;
                                printWriter4 = printWriter;
                                printWriter4.println(sb2.toString());
                            } else {
                                sb2.setLength(0);
                                sb2.append(str);
                                sb2.append("    TOTAL wake: ");
                                if (j17 != 0) {
                                    formatTimeMs(sb2, j17);
                                    sb2.append("full");
                                    z10 = true;
                                } else {
                                    z10 = false;
                                }
                                if (j16 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j16);
                                    sb2.append("blamed partial");
                                    z10 = true;
                                }
                                if (j37 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j37);
                                    sb2.append("actual partial");
                                    z10 = true;
                                }
                                if (j38 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j38);
                                    sb2.append("actual background partial");
                                    z10 = true;
                                }
                                if (j15 != 0) {
                                    if (z10) {
                                        sb2.append(", ");
                                    }
                                    formatTimeMs(sb2, j15);
                                    sb2.append(Context.WINDOW_SERVICE);
                                    z10 = true;
                                }
                                if (j20 != 0) {
                                    if (z10) {
                                        sb2.append(",");
                                    }
                                    formatTimeMs(sb2, j20);
                                    sb2.append("draw");
                                }
                                sb2.append(" realtime");
                                j21 = j39;
                                printWriter4 = printWriter;
                                printWriter4.println(sb2.toString());
                            }
                        } else {
                            z4 = z4;
                            printWriter4 = printWriter3;
                            j21 = j19;
                        }
                        multicastWakelockStats = uid2.getMulticastWakelockStats();
                        if (multicastWakelockStats != null) {
                            j22 = j18;
                            totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j22, i7);
                            countLocked7 = multicastWakelockStats.getCountLocked(i7);
                            if (totalTimeLocked4 > 0) {
                                sb2.setLength(0);
                                str4 = str;
                                sb2.append(str4);
                                sb2.append("    WiFi Multicast Wakelock");
                                sb2.append(" count = ");
                                sb2.append(countLocked7);
                                sb2.append(" time = ");
                                formatTimeMsNoSpace(sb2, (totalTimeLocked4 + 500) / 1000);
                                printWriter4.println(sb2.toString());
                            }
                            syncStats = uid2.getSyncStats();
                            size2 = syncStats.size() - 1;
                            while (size2 >= 0) {
                                Timer timerValueAt5 = syncStats.valueAt(size2);
                                totalTimeLocked3 = (timerValueAt5.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                countLocked5 = timerValueAt5.getCountLocked(i7);
                                subTimer2 = timerValueAt5.getSubTimer();
                                if (subTimer2 != null) {
                                    totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                                } else {
                                    totalDurationMsLocked4 = -1;
                                }
                                if (subTimer2 != null) {
                                    countLocked6 = subTimer2.getCountLocked(i7);
                                } else {
                                    countLocked6 = i3;
                                }
                                sb2.setLength(0);
                                sb2.append(str4);
                                sb2.append("    Sync ");
                                sb2.append(syncStats.keyAt(size2));
                                sb2.append(": ");
                                if (totalTimeLocked3 != 0) {
                                    formatTimeMs(sb2, totalTimeLocked3);
                                    sb2.append("realtime (");
                                    sb2.append(countLocked5);
                                    sb2.append(" times)");
                                    if (totalDurationMsLocked4 > 0) {
                                        sb2.append(", ");
                                        formatTimeMs(sb2, totalDurationMsLocked4);
                                        sb2.append("background (");
                                        sb2.append(countLocked6);
                                        sb2.append(" times)");
                                    }
                                } else {
                                    sb2.append("(not used)");
                                }
                                printWriter4.println(sb2.toString());
                                size2--;
                                j21 = j21;
                                z4 = true;
                            }
                            j23 = j21;
                            jobStats = uid2.getJobStats();
                            size3 = jobStats.size() - 1;
                            while (size3 >= 0) {
                                Timer timerValueAt6 = jobStats.valueAt(size3);
                                totalTimeLocked2 = (timerValueAt6.getTotalTimeLocked(j22, i7) + 500) / 1000;
                                countLocked3 = timerValueAt6.getCountLocked(i7);
                                subTimer = timerValueAt6.getSubTimer();
                                if (subTimer != null) {
                                    j36 = j23;
                                    totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                                } else {
                                    j36 = j23;
                                    totalDurationMsLocked3 = -1;
                                }
                                if (subTimer != null) {
                                    countLocked4 = subTimer.getCountLocked(i7);
                                } else {
                                    countLocked4 = i3;
                                }
                                sb2.setLength(0);
                                sb2.append(str4);
                                sb2.append("    Job ");
                                sb2.append(jobStats.keyAt(size3));
                                sb2.append(": ");
                                if (totalTimeLocked2 != 0) {
                                    formatTimeMs(sb2, totalTimeLocked2);
                                    sb2.append("realtime (");
                                    sb2.append(countLocked3);
                                    sb2.append(" times)");
                                    if (totalDurationMsLocked3 > 0) {
                                        sb2.append(", ");
                                        formatTimeMs(sb2, totalDurationMsLocked3);
                                        sb2.append("background (");
                                        sb2.append(countLocked4);
                                        sb2.append(" times)");
                                    }
                                } else {
                                    sb2.append("(not used)");
                                }
                                printWriter4.println(sb2.toString());
                                size3--;
                                j23 = j36;
                                j22 = j22;
                                z4 = true;
                            }
                            long j717 = j22;
                            long j718 = j23;
                            i9 = 0;
                            jobCompletionStats = uid2.getJobCompletionStats();
                            while (size4 >= 0) {
                                sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                                if (sparseIntArrayValueAt != null) {
                                    printWriter.print(str);
                                    printWriter4.print("    Job Completions ");
                                    printWriter4.print(jobCompletionStats.keyAt(size4));
                                    printWriter4.print(SettingsStringUtil.DELIMITER);
                                    while (i21 < sparseIntArrayValueAt.size()) {
                                        printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                        printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                        printWriter4.print("(");
                                        printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                        printWriter4.print("x)");
                                    }
                                    printWriter.println();
                                }
                            }
                            uid2.getDeferredJobsLineLocked(sb2, i7);
                            if (sb2.length() > 0) {
                                printWriter4.print("    Jobs deferred on launch ");
                                printWriter4.println(sb2.toString());
                            }
                            j24 = j717;
                            uid3 = uid2;
                            sb3 = sb2;
                            i10 = i7;
                            j25 = j67;
                            sparseArray = sparseArray8;
                            j26 = j718;
                            i11 = i48;
                            boolean zPrintTimer4 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                            sensorStats = uid3.getSensorStats();
                            size5 = sensorStats.size();
                            z5 = zPrintTimer4;
                            i12 = 0;
                            while (i12 < size5) {
                                sensorValueAt = sensorStats.valueAt(i12);
                                sensorStats.keyAt(i12);
                                sb4 = sb3;
                                sb4.setLength(i9);
                                sb4.append(str);
                                sb4.append("    Sensor ");
                                handle = sensorValueAt.getHandle();
                                if (handle == -10000) {
                                    sb4.append("GPS");
                                } else {
                                    sb4.append(handle);
                                }
                                sb4.append(": ");
                                sensorTime = sensorValueAt.getSensorTime();
                                if (sensorTime != null) {
                                    totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                                    countLocked = sensorTime.getCountLocked(i10);
                                    sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                    if (sensorBackgroundTime != null) {
                                        countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                                    } else {
                                        countLocked2 = i9;
                                    }
                                    j35 = j26;
                                    totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                                    if (sensorBackgroundTime != null) {
                                        totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                                    } else {
                                        totalDurationMsLocked2 = 0;
                                    }
                                    if (totalTimeLocked != 0) {
                                        if (totalDurationMsLocked != totalTimeLocked) {
                                            formatTimeMs(sb4, totalTimeLocked);
                                            sb4.append("blamed realtime, ");
                                        }
                                        formatTimeMs(sb4, totalDurationMsLocked);
                                        sb4.append("realtime (");
                                        sb4.append(countLocked);
                                        sb4.append(" times)");
                                        if (totalDurationMsLocked2 == 0) {
                                            sb4.append(", ");
                                            formatTimeMs(sb4, totalDurationMsLocked2);
                                            sb4.append("background (");
                                            sb4.append(countLocked2);
                                            sb4.append(" times)");
                                        } else {
                                            sb4.append(", ");
                                            formatTimeMs(sb4, totalDurationMsLocked2);
                                            sb4.append("background (");
                                            sb4.append(countLocked2);
                                            sb4.append(" times)");
                                        }
                                    } else {
                                        sb4.append("(not used)");
                                    }
                                } else {
                                    j24 = j24;
                                    sensorStats = sensorStats;
                                    size5 = size5;
                                    j35 = j26;
                                    sb4.append("(not used)");
                                }
                                printWriter4.println(sb4.toString());
                                i12++;
                                j26 = j35;
                                sb3 = sb4;
                                z5 = true;
                                j24 = j24;
                                sensorStats = sensorStats;
                                size5 = size5;
                                i9 = 0;
                            }
                            j27 = j24;
                            StringBuilder sb12 = sb3;
                            j28 = j26;
                            sb = sb12;
                            zPrintTimer = z5 | printTimer(printWriter, sb12, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                            j29 = 0;
                            i13 = 0;
                            while (i13 < 7) {
                                Uid uid14 = uid3;
                                long j719 = j27;
                                processStateTime = uid14.getProcessStateTime(i13, j719, i10);
                                if (processStateTime > 0) {
                                    j29 += processStateTime;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    ");
                                    sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                                    sb.append(" for: ");
                                    formatTimeMs(sb, (processStateTime + 500) / 1000);
                                    printWriter4.println(sb.toString());
                                    zPrintTimer = true;
                                }
                                i13++;
                                j27 = j719;
                                uid3 = uid14;
                            }
                            uid4 = uid3;
                            j30 = j27;
                            if (j29 > 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total running: ");
                                formatTimeMs(sb, (j29 + 500) / 1000);
                                printWriter4.println(sb.toString());
                            }
                            userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                            systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                            if (userCpuTimeUs <= 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total cpu time: u=");
                                formatTimeMs(sb, userCpuTimeUs / 1000);
                                sb.append("s=");
                                formatTimeMs(sb, systemCpuTimeUs / 1000);
                                printWriter4.println(sb.toString());
                            } else {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Total cpu time: u=");
                                formatTimeMs(sb, userCpuTimeUs / 1000);
                                sb.append("s=");
                                formatTimeMs(sb, systemCpuTimeUs / 1000);
                                printWriter4.println(sb.toString());
                            }
                            cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                            if (cpuFreqTimes != null) {
                                sb.setLength(0);
                                sb.append("    Total cpu time per freq:");
                                while (i20 < cpuFreqTimes.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                                }
                                printWriter4.println(sb.toString());
                            }
                            screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                            if (screenOffCpuFreqTimes != null) {
                                sb.setLength(0);
                                sb.append("    Total screen-off cpu time per freq:");
                                while (i19 < screenOffCpuFreqTimes.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                                }
                                printWriter4.println(sb.toString());
                            }
                            i14 = 0;
                            while (i14 < 7) {
                                cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                                if (cpuFreqTimes2 != null) {
                                    sb.setLength(0);
                                    sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                    i18 = 0;
                                    while (i18 < cpuFreqTimes2.length) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                        i18++;
                                        sparseArray = sparseArray;
                                        j28 = j28;
                                    }
                                    sparseArray3 = sparseArray;
                                    j34 = j28;
                                    printWriter4.println(sb.toString());
                                } else {
                                    sparseArray3 = sparseArray;
                                    j34 = j28;
                                }
                                screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                                if (screenOffCpuFreqTimes2 != null) {
                                    sb.setLength(0);
                                    sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                    while (i17 < screenOffCpuFreqTimes2.length) {
                                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                                    }
                                    printWriter4.println(sb.toString());
                                }
                                i14++;
                                sparseArray = sparseArray3;
                                j28 = j34;
                            }
                            sparseArray2 = sparseArray;
                            j31 = j28;
                            processStats = uid4.getProcessStats();
                            size6 = processStats.size() - 1;
                            z6 = zPrintTimer;
                            while (size6 >= 0) {
                                procValueAt = processStats.valueAt(size6);
                                userTime = procValueAt.getUserTime(i10);
                                systemTime = procValueAt.getSystemTime(i10);
                                long j88 = j30;
                                foregroundTime = procValueAt.getForegroundTime(i10);
                                starts2 = procValueAt.getStarts(i10);
                                boolean z111 = z6;
                                numCrashes = procValueAt.getNumCrashes(i10);
                                Uid uid15 = uid4;
                                numAnrs = procValueAt.getNumAnrs(i10);
                                if (i10 == 0) {
                                    iCountExcessivePowers = procValueAt.countExcessivePowers();
                                } else {
                                    iCountExcessivePowers = 0;
                                }
                                if (userTime != 0) {
                                    proc = procValueAt;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Proc ");
                                    sb.append(processStats.keyAt(size6));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("      CPU: ");
                                    formatTimeMs(sb, userTime);
                                    sb.append("usr + ");
                                    formatTimeMs(sb, systemTime);
                                    sb.append("krn ; ");
                                    formatTimeMs(sb, foregroundTime);
                                    sb.append(FOREGROUND_ACTIVITY_DATA);
                                    if (starts2 == 0) {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    } else {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    }
                                    printWriter4.println(sb.toString());
                                    i16 = 0;
                                    while (i16 < iCountExcessivePowers) {
                                        Uid.Proc proc5 = proc;
                                        excessivePower = proc5.getExcessivePower(i16);
                                        if (excessivePower != null) {
                                            printWriter.print(str);
                                            printWriter4.print("      * Killed for ");
                                            if (excessivePower.type == 2) {
                                                printWriter4.print(CPU_DATA);
                                            } else {
                                                printWriter4.print("unknown");
                                            }
                                            printWriter4.print(" use: ");
                                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                            printWriter4.print(" over ");
                                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                            if (excessivePower.overTime != 0) {
                                                printWriter4.print(" (");
                                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                                printWriter4.println("%)");
                                            }
                                        }
                                        i16++;
                                        proc = proc5;
                                    }
                                    z6 = true;
                                } else {
                                    proc = procValueAt;
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("    Proc ");
                                    sb.append(processStats.keyAt(size6));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("      CPU: ");
                                    formatTimeMs(sb, userTime);
                                    sb.append("usr + ");
                                    formatTimeMs(sb, systemTime);
                                    sb.append("krn ; ");
                                    formatTimeMs(sb, foregroundTime);
                                    sb.append(FOREGROUND_ACTIVITY_DATA);
                                    if (starts2 == 0) {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    } else {
                                        sb.append("\n");
                                        sb.append(str);
                                        sb.append("      ");
                                        if (starts2 != 0) {
                                            sb.append(starts2);
                                            sb.append(" starts");
                                            z9 = true;
                                        } else {
                                            z9 = false;
                                        }
                                        if (numCrashes != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numCrashes);
                                            sb.append(" crashes");
                                            z9 = true;
                                        }
                                        if (numAnrs != 0) {
                                            if (z9) {
                                                sb.append(", ");
                                            }
                                            sb.append(numAnrs);
                                            sb.append(" anrs");
                                        }
                                    }
                                    printWriter4.println(sb.toString());
                                    i16 = 0;
                                    while (i16 < iCountExcessivePowers) {
                                        Uid.Proc proc6 = proc;
                                        excessivePower = proc6.getExcessivePower(i16);
                                        if (excessivePower != null) {
                                            printWriter.print(str);
                                            printWriter4.print("      * Killed for ");
                                            if (excessivePower.type == 2) {
                                                printWriter4.print(CPU_DATA);
                                            } else {
                                                printWriter4.print("unknown");
                                            }
                                            printWriter4.print(" use: ");
                                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                            printWriter4.print(" over ");
                                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                            if (excessivePower.overTime != 0) {
                                                printWriter4.print(" (");
                                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                                printWriter4.println("%)");
                                            }
                                        }
                                        i16++;
                                        proc = proc6;
                                    }
                                    z6 = true;
                                }
                                size6--;
                                j30 = j88;
                                uid4 = uid15;
                                i10 = i;
                            }
                            j32 = j30;
                            packageStats = uid4.getPackageStats();
                            size7 = packageStats.size() - 1;
                            z7 = z6;
                            while (size7 >= 0) {
                                printWriter.print(str);
                                printWriter4.print("    Apk ");
                                printWriter4.print(packageStats.keyAt(size7));
                                printWriter4.println(SettingsStringUtil.DELIMITER);
                                Uid.Pkg pkgValueAt3 = packageStats.valueAt(size7);
                                wakeupAlarmStats = pkgValueAt3.getWakeupAlarmStats();
                                size8 = wakeupAlarmStats.size() - 1;
                                z8 = false;
                                while (size8 >= 0) {
                                    printWriter.print(str);
                                    printWriter4.print("      Wakeup alarm ");
                                    printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                    printWriter4.print(": ");
                                    printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                    printWriter4.println(" times");
                                    size8--;
                                    z8 = true;
                                }
                                serviceStats = pkgValueAt3.getServiceStats();
                                size9 = serviceStats.size() - 1;
                                while (size9 >= 0) {
                                    Uid.Pkg.Serv servValueAt3 = serviceStats.valueAt(size9);
                                    long j89 = j56;
                                    startTime = servValueAt3.getStartTime(j89, i);
                                    starts = servValueAt3.getStarts(i);
                                    launches = servValueAt3.getLaunches(i);
                                    if (startTime == 0) {
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("      Service ");
                                        sb.append(serviceStats.keyAt(size9));
                                        sb.append(":\n");
                                        sb.append(str);
                                        sb.append("        Created for: ");
                                        formatTimeMs(sb, startTime / 1000);
                                        sb.append("uptime\n");
                                        sb.append(str);
                                        sb.append("        Starts: ");
                                        sb.append(starts);
                                        sb.append(", launches: ");
                                        sb.append(launches);
                                        printWriter4.println(sb.toString());
                                        z8 = true;
                                    } else {
                                        sb.setLength(0);
                                        sb.append(str);
                                        sb.append("      Service ");
                                        sb.append(serviceStats.keyAt(size9));
                                        sb.append(":\n");
                                        sb.append(str);
                                        sb.append("        Created for: ");
                                        formatTimeMs(sb, startTime / 1000);
                                        sb.append("uptime\n");
                                        sb.append(str);
                                        sb.append("        Starts: ");
                                        sb.append(starts);
                                        sb.append(", launches: ");
                                        sb.append(launches);
                                        printWriter4.println(sb.toString());
                                        z8 = true;
                                    }
                                    size9--;
                                    j56 = j89;
                                }
                                long j810 = j56;
                                if (!z8) {
                                    printWriter.print(str);
                                    printWriter4.println("      (nothing executed)");
                                }
                                size7--;
                                j56 = j810;
                                z7 = true;
                            }
                            j33 = j56;
                            i15 = 0;
                            if (!z7) {
                                printWriter.print(str);
                                printWriter4.println("    (nothing executed)");
                            }
                            i46 = i11 + 1;
                            i32 = i2;
                            printWriter2 = printWriter4;
                            i5 = i15;
                            j56 = j33;
                            i35 = i6;
                            j3 = j11;
                            j58 = j25;
                            sparseArray7 = sparseArray2;
                            j57 = j31;
                            j2 = j32;
                            batteryStats = this;
                        } else {
                            j22 = j18;
                        }
                        str4 = str;
                        syncStats = uid2.getSyncStats();
                        size2 = syncStats.size() - 1;
                        while (size2 >= 0) {
                            Timer timerValueAt7 = syncStats.valueAt(size2);
                            totalTimeLocked3 = (timerValueAt7.getTotalTimeLocked(j22, i7) + 500) / 1000;
                            countLocked5 = timerValueAt7.getCountLocked(i7);
                            subTimer2 = timerValueAt7.getSubTimer();
                            if (subTimer2 != null) {
                                totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                            } else {
                                totalDurationMsLocked4 = -1;
                            }
                            if (subTimer2 != null) {
                                countLocked6 = subTimer2.getCountLocked(i7);
                            } else {
                                countLocked6 = i3;
                            }
                            sb2.setLength(0);
                            sb2.append(str4);
                            sb2.append("    Sync ");
                            sb2.append(syncStats.keyAt(size2));
                            sb2.append(": ");
                            if (totalTimeLocked3 != 0) {
                                formatTimeMs(sb2, totalTimeLocked3);
                                sb2.append("realtime (");
                                sb2.append(countLocked5);
                                sb2.append(" times)");
                                if (totalDurationMsLocked4 > 0) {
                                    sb2.append(", ");
                                    formatTimeMs(sb2, totalDurationMsLocked4);
                                    sb2.append("background (");
                                    sb2.append(countLocked6);
                                    sb2.append(" times)");
                                }
                            } else {
                                sb2.append("(not used)");
                            }
                            printWriter4.println(sb2.toString());
                            size2--;
                            j21 = j21;
                            z4 = true;
                        }
                        j23 = j21;
                        jobStats = uid2.getJobStats();
                        size3 = jobStats.size() - 1;
                        while (size3 >= 0) {
                            Timer timerValueAt8 = jobStats.valueAt(size3);
                            totalTimeLocked2 = (timerValueAt8.getTotalTimeLocked(j22, i7) + 500) / 1000;
                            countLocked3 = timerValueAt8.getCountLocked(i7);
                            subTimer = timerValueAt8.getSubTimer();
                            if (subTimer != null) {
                                j36 = j23;
                                totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                            } else {
                                j36 = j23;
                                totalDurationMsLocked3 = -1;
                            }
                            if (subTimer != null) {
                                countLocked4 = subTimer.getCountLocked(i7);
                            } else {
                                countLocked4 = i3;
                            }
                            sb2.setLength(0);
                            sb2.append(str4);
                            sb2.append("    Job ");
                            sb2.append(jobStats.keyAt(size3));
                            sb2.append(": ");
                            if (totalTimeLocked2 != 0) {
                                formatTimeMs(sb2, totalTimeLocked2);
                                sb2.append("realtime (");
                                sb2.append(countLocked3);
                                sb2.append(" times)");
                                if (totalDurationMsLocked3 > 0) {
                                    sb2.append(", ");
                                    formatTimeMs(sb2, totalDurationMsLocked3);
                                    sb2.append("background (");
                                    sb2.append(countLocked4);
                                    sb2.append(" times)");
                                }
                            } else {
                                sb2.append("(not used)");
                            }
                            printWriter4.println(sb2.toString());
                            size3--;
                            j23 = j36;
                            j22 = j22;
                            z4 = true;
                        }
                        long j7110 = j22;
                        long j7111 = j23;
                        i9 = 0;
                        jobCompletionStats = uid2.getJobCompletionStats();
                        while (size4 >= 0) {
                            sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                            if (sparseIntArrayValueAt != null) {
                                printWriter.print(str);
                                printWriter4.print("    Job Completions ");
                                printWriter4.print(jobCompletionStats.keyAt(size4));
                                printWriter4.print(SettingsStringUtil.DELIMITER);
                                while (i21 < sparseIntArrayValueAt.size()) {
                                    printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                    printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                    printWriter4.print("(");
                                    printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                    printWriter4.print("x)");
                                }
                                printWriter.println();
                            }
                        }
                        uid2.getDeferredJobsLineLocked(sb2, i7);
                        if (sb2.length() > 0) {
                            printWriter4.print("    Jobs deferred on launch ");
                            printWriter4.println(sb2.toString());
                        }
                        j24 = j7110;
                        uid3 = uid2;
                        sb3 = sb2;
                        i10 = i7;
                        j25 = j67;
                        sparseArray = sparseArray8;
                        j26 = j7111;
                        i11 = i48;
                        boolean zPrintTimer5 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                        sensorStats = uid3.getSensorStats();
                        size5 = sensorStats.size();
                        z5 = zPrintTimer5;
                        i12 = 0;
                        while (i12 < size5) {
                            sensorValueAt = sensorStats.valueAt(i12);
                            sensorStats.keyAt(i12);
                            sb4 = sb3;
                            sb4.setLength(i9);
                            sb4.append(str);
                            sb4.append("    Sensor ");
                            handle = sensorValueAt.getHandle();
                            if (handle == -10000) {
                                sb4.append("GPS");
                            } else {
                                sb4.append(handle);
                            }
                            sb4.append(": ");
                            sensorTime = sensorValueAt.getSensorTime();
                            if (sensorTime != null) {
                                totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                                countLocked = sensorTime.getCountLocked(i10);
                                sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                if (sensorBackgroundTime != null) {
                                    countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                                } else {
                                    countLocked2 = i9;
                                }
                                j35 = j26;
                                totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                                if (sensorBackgroundTime != null) {
                                    totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                                } else {
                                    totalDurationMsLocked2 = 0;
                                }
                                if (totalTimeLocked != 0) {
                                    if (totalDurationMsLocked != totalTimeLocked) {
                                        formatTimeMs(sb4, totalTimeLocked);
                                        sb4.append("blamed realtime, ");
                                    }
                                    formatTimeMs(sb4, totalDurationMsLocked);
                                    sb4.append("realtime (");
                                    sb4.append(countLocked);
                                    sb4.append(" times)");
                                    if (totalDurationMsLocked2 == 0) {
                                        sb4.append(", ");
                                        formatTimeMs(sb4, totalDurationMsLocked2);
                                        sb4.append("background (");
                                        sb4.append(countLocked2);
                                        sb4.append(" times)");
                                    } else {
                                        sb4.append(", ");
                                        formatTimeMs(sb4, totalDurationMsLocked2);
                                        sb4.append("background (");
                                        sb4.append(countLocked2);
                                        sb4.append(" times)");
                                    }
                                } else {
                                    sb4.append("(not used)");
                                }
                            } else {
                                j24 = j24;
                                sensorStats = sensorStats;
                                size5 = size5;
                                j35 = j26;
                                sb4.append("(not used)");
                            }
                            printWriter4.println(sb4.toString());
                            i12++;
                            j26 = j35;
                            sb3 = sb4;
                            z5 = true;
                            j24 = j24;
                            sensorStats = sensorStats;
                            size5 = size5;
                            i9 = 0;
                        }
                        j27 = j24;
                        StringBuilder sb13 = sb3;
                        j28 = j26;
                        sb = sb13;
                        zPrintTimer = z5 | printTimer(printWriter, sb13, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                        j29 = 0;
                        i13 = 0;
                        while (i13 < 7) {
                            Uid uid16 = uid3;
                            long j7112 = j27;
                            processStateTime = uid16.getProcessStateTime(i13, j7112, i10);
                            if (processStateTime > 0) {
                                j29 += processStateTime;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    ");
                                sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                                sb.append(" for: ");
                                formatTimeMs(sb, (processStateTime + 500) / 1000);
                                printWriter4.println(sb.toString());
                                zPrintTimer = true;
                            }
                            i13++;
                            j27 = j7112;
                            uid3 = uid16;
                        }
                        uid4 = uid3;
                        j30 = j27;
                        if (j29 > 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total running: ");
                            formatTimeMs(sb, (j29 + 500) / 1000);
                            printWriter4.println(sb.toString());
                        }
                        userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                        systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                        if (userCpuTimeUs <= 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total cpu time: u=");
                            formatTimeMs(sb, userCpuTimeUs / 1000);
                            sb.append("s=");
                            formatTimeMs(sb, systemCpuTimeUs / 1000);
                            printWriter4.println(sb.toString());
                        } else {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total cpu time: u=");
                            formatTimeMs(sb, userCpuTimeUs / 1000);
                            sb.append("s=");
                            formatTimeMs(sb, systemCpuTimeUs / 1000);
                            printWriter4.println(sb.toString());
                        }
                        cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                        if (cpuFreqTimes != null) {
                            sb.setLength(0);
                            sb.append("    Total cpu time per freq:");
                            while (i20 < cpuFreqTimes.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                            }
                            printWriter4.println(sb.toString());
                        }
                        screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                        if (screenOffCpuFreqTimes != null) {
                            sb.setLength(0);
                            sb.append("    Total screen-off cpu time per freq:");
                            while (i19 < screenOffCpuFreqTimes.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                            }
                            printWriter4.println(sb.toString());
                        }
                        i14 = 0;
                        while (i14 < 7) {
                            cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                            if (cpuFreqTimes2 != null) {
                                sb.setLength(0);
                                sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                i18 = 0;
                                while (i18 < cpuFreqTimes2.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                    i18++;
                                    sparseArray = sparseArray;
                                    j28 = j28;
                                }
                                sparseArray3 = sparseArray;
                                j34 = j28;
                                printWriter4.println(sb.toString());
                            } else {
                                sparseArray3 = sparseArray;
                                j34 = j28;
                            }
                            screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                            if (screenOffCpuFreqTimes2 != null) {
                                sb.setLength(0);
                                sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                while (i17 < screenOffCpuFreqTimes2.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                                }
                                printWriter4.println(sb.toString());
                            }
                            i14++;
                            sparseArray = sparseArray3;
                            j28 = j34;
                        }
                        sparseArray2 = sparseArray;
                        j31 = j28;
                        processStats = uid4.getProcessStats();
                        size6 = processStats.size() - 1;
                        z6 = zPrintTimer;
                        while (size6 >= 0) {
                            procValueAt = processStats.valueAt(size6);
                            userTime = procValueAt.getUserTime(i10);
                            systemTime = procValueAt.getSystemTime(i10);
                            long j811 = j30;
                            foregroundTime = procValueAt.getForegroundTime(i10);
                            starts2 = procValueAt.getStarts(i10);
                            boolean z112 = z6;
                            numCrashes = procValueAt.getNumCrashes(i10);
                            Uid uid17 = uid4;
                            numAnrs = procValueAt.getNumAnrs(i10);
                            if (i10 == 0) {
                                iCountExcessivePowers = procValueAt.countExcessivePowers();
                            } else {
                                iCountExcessivePowers = 0;
                            }
                            if (userTime != 0) {
                                proc = procValueAt;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Proc ");
                                sb.append(processStats.keyAt(size6));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("      CPU: ");
                                formatTimeMs(sb, userTime);
                                sb.append("usr + ");
                                formatTimeMs(sb, systemTime);
                                sb.append("krn ; ");
                                formatTimeMs(sb, foregroundTime);
                                sb.append(FOREGROUND_ACTIVITY_DATA);
                                if (starts2 == 0) {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                } else {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                }
                                printWriter4.println(sb.toString());
                                i16 = 0;
                                while (i16 < iCountExcessivePowers) {
                                    Uid.Proc proc7 = proc;
                                    excessivePower = proc7.getExcessivePower(i16);
                                    if (excessivePower != null) {
                                        printWriter.print(str);
                                        printWriter4.print("      * Killed for ");
                                        if (excessivePower.type == 2) {
                                            printWriter4.print(CPU_DATA);
                                        } else {
                                            printWriter4.print("unknown");
                                        }
                                        printWriter4.print(" use: ");
                                        TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                        printWriter4.print(" over ");
                                        TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                        if (excessivePower.overTime != 0) {
                                            printWriter4.print(" (");
                                            printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                            printWriter4.println("%)");
                                        }
                                    }
                                    i16++;
                                    proc = proc7;
                                }
                                z6 = true;
                            } else {
                                proc = procValueAt;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Proc ");
                                sb.append(processStats.keyAt(size6));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("      CPU: ");
                                formatTimeMs(sb, userTime);
                                sb.append("usr + ");
                                formatTimeMs(sb, systemTime);
                                sb.append("krn ; ");
                                formatTimeMs(sb, foregroundTime);
                                sb.append(FOREGROUND_ACTIVITY_DATA);
                                if (starts2 == 0) {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                } else {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                }
                                printWriter4.println(sb.toString());
                                i16 = 0;
                                while (i16 < iCountExcessivePowers) {
                                    Uid.Proc proc8 = proc;
                                    excessivePower = proc8.getExcessivePower(i16);
                                    if (excessivePower != null) {
                                        printWriter.print(str);
                                        printWriter4.print("      * Killed for ");
                                        if (excessivePower.type == 2) {
                                            printWriter4.print(CPU_DATA);
                                        } else {
                                            printWriter4.print("unknown");
                                        }
                                        printWriter4.print(" use: ");
                                        TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                        printWriter4.print(" over ");
                                        TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                        if (excessivePower.overTime != 0) {
                                            printWriter4.print(" (");
                                            printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                            printWriter4.println("%)");
                                        }
                                    }
                                    i16++;
                                    proc = proc8;
                                }
                                z6 = true;
                            }
                            size6--;
                            j30 = j811;
                            uid4 = uid17;
                            i10 = i;
                        }
                        j32 = j30;
                        packageStats = uid4.getPackageStats();
                        size7 = packageStats.size() - 1;
                        z7 = z6;
                        while (size7 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("    Apk ");
                            printWriter4.print(packageStats.keyAt(size7));
                            printWriter4.println(SettingsStringUtil.DELIMITER);
                            Uid.Pkg pkgValueAt4 = packageStats.valueAt(size7);
                            wakeupAlarmStats = pkgValueAt4.getWakeupAlarmStats();
                            size8 = wakeupAlarmStats.size() - 1;
                            z8 = false;
                            while (size8 >= 0) {
                                printWriter.print(str);
                                printWriter4.print("      Wakeup alarm ");
                                printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                printWriter4.print(": ");
                                printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                printWriter4.println(" times");
                                size8--;
                                z8 = true;
                            }
                            serviceStats = pkgValueAt4.getServiceStats();
                            size9 = serviceStats.size() - 1;
                            while (size9 >= 0) {
                                Uid.Pkg.Serv servValueAt4 = serviceStats.valueAt(size9);
                                long j812 = j56;
                                startTime = servValueAt4.getStartTime(j812, i);
                                starts = servValueAt4.getStarts(i);
                                launches = servValueAt4.getLaunches(i);
                                if (startTime == 0) {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("      Service ");
                                    sb.append(serviceStats.keyAt(size9));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("        Created for: ");
                                    formatTimeMs(sb, startTime / 1000);
                                    sb.append("uptime\n");
                                    sb.append(str);
                                    sb.append("        Starts: ");
                                    sb.append(starts);
                                    sb.append(", launches: ");
                                    sb.append(launches);
                                    printWriter4.println(sb.toString());
                                    z8 = true;
                                } else {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("      Service ");
                                    sb.append(serviceStats.keyAt(size9));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("        Created for: ");
                                    formatTimeMs(sb, startTime / 1000);
                                    sb.append("uptime\n");
                                    sb.append(str);
                                    sb.append("        Starts: ");
                                    sb.append(starts);
                                    sb.append(", launches: ");
                                    sb.append(launches);
                                    printWriter4.println(sb.toString());
                                    z8 = true;
                                }
                                size9--;
                                j56 = j812;
                            }
                            long j813 = j56;
                            if (!z8) {
                                printWriter.print(str);
                                printWriter4.println("      (nothing executed)");
                            }
                            size7--;
                            j56 = j813;
                            z7 = true;
                        }
                        j33 = j56;
                        i15 = 0;
                        if (!z7) {
                            printWriter.print(str);
                            printWriter4.println("    (nothing executed)");
                        }
                        i46 = i11 + 1;
                        i32 = i2;
                        printWriter2 = printWriter4;
                        i5 = i15;
                        j56 = j33;
                        i35 = i6;
                        j3 = j11;
                        j58 = j25;
                        sparseArray7 = sparseArray2;
                        j57 = j31;
                        j2 = j32;
                        batteryStats = this;
                    }
                    j7 = wifiScanBackgroundTime;
                    j8 = wifiRunningTime;
                    j7 = wifiScanBackgroundTime;
                    sb2 = sb7;
                    sb2.setLength(0);
                    long j814 = j7;
                    str3 = str;
                    sb2.append(str3);
                    sb2.append("    Wifi Running: ");
                    long j815 = j6;
                    formatTimeMs(sb2, j8 / 1000);
                    sb2.append("(");
                    j9 = j3;
                    sb2.append(batteryStats.formatRatioLocked(j8, j9));
                    sb2.append(")\n");
                    sb2.append(str3);
                    sb2.append("    Full Wifi Lock: ");
                    formatTimeMs(sb2, fullWifiLockTime / 1000);
                    sb2.append("(");
                    sb2.append(batteryStats.formatRatioLocked(fullWifiLockTime, j9));
                    sb2.append(")\n");
                    sb2.append(str3);
                    sb2.append("    Wifi Scan (blamed): ");
                    formatTimeMs(sb2, j5 / 1000);
                    sb2.append("(");
                    sb2.append(batteryStats.formatRatioLocked(j5, j9));
                    sb2.append(") ");
                    sb2.append(wifiScanCount);
                    sb2.append("x\n");
                    sb2.append(str3);
                    sb2.append("    Wifi Scan (actual): ");
                    formatTimeMs(sb2, j815 / 1000);
                    sb2.append("(");
                    j10 = j64;
                    sb2.append(batteryStats.formatRatioLocked(j815, batteryStats.computeBatteryRealtime(j10, 0)));
                    sb2.append(") ");
                    sb2.append(wifiScanCount);
                    sb2.append("x\n");
                    sb2.append(str3);
                    sb2.append("    Background Wifi Scan: ");
                    formatTimeMs(sb2, j814 / 1000);
                    sb2.append("(");
                    sb2.append(batteryStats.formatRatioLocked(j814, batteryStats.computeBatteryRealtime(j10, 0)));
                    sb2.append(") ");
                    sb2.append(wifiScanBackgroundCount);
                    sb2.append("x");
                    printWriter2.println(sb2.toString());
                    if (wifiRadioApWakeupCount > 0) {
                        sb2.setLength(0);
                        sb2.append(str3);
                        sb2.append("    WiFi AP wakeups: ");
                        sb2.append(wifiRadioApWakeupCount);
                        printWriter2.println(sb2.toString());
                    }
                    j11 = j9;
                    printControllerActivityIfInteresting(printWriter, sb2, str3 + "  ", WIFI_CONTROLLER_NAME, uid5.getWifiControllerActivity(), i);
                    if (networkActivityBytes11 <= 0) {
                        printWriter.print(str);
                        printWriter2.print("    Bluetooth network: ");
                        printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                        printWriter2.print(" received, ");
                        printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                        printWriter2.println(" sent");
                    } else {
                        printWriter.print(str);
                        printWriter2.print("    Bluetooth network: ");
                        printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                        printWriter2.print(" received, ");
                        printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                        printWriter2.println(" sent");
                    }
                    bluetoothScanTimer = uid5.getBluetoothScanTimer();
                    if (bluetoothScanTimer != null) {
                        totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                        if (totalTimeLocked5 != 0) {
                            countLocked8 = bluetoothScanTimer.getCountLocked(i);
                            bluetoothScanBackgroundTimer = uid5.getBluetoothScanBackgroundTimer();
                            if (bluetoothScanBackgroundTimer != null) {
                                countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                            } else {
                                countLocked9 = 0;
                            }
                            totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j57);
                            if (bluetoothScanBackgroundTimer != null) {
                                totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j57);
                            } else {
                                totalDurationMsLocked7 = 0;
                            }
                            if (uid5.getBluetoothScanResultCounter() != null) {
                                countLocked10 = uid5.getBluetoothScanResultCounter().getCountLocked(i);
                            } else {
                                countLocked10 = 0;
                            }
                            if (uid5.getBluetoothScanResultBgCounter() != null) {
                                countLocked11 = uid5.getBluetoothScanResultBgCounter().getCountLocked(i);
                            } else {
                                countLocked11 = 0;
                            }
                            bluetoothUnoptimizedScanTimer = uid5.getBluetoothUnoptimizedScanTimer();
                            if (bluetoothUnoptimizedScanTimer != null) {
                                totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j57);
                            } else {
                                totalDurationMsLocked8 = 0;
                            }
                            if (bluetoothUnoptimizedScanTimer != null) {
                                maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j57);
                            } else {
                                maxDurationMsLocked = 0;
                            }
                            bluetoothUnoptimizedScanBackgroundTimer = uid5.getBluetoothUnoptimizedScanBackgroundTimer();
                            if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j57);
                            } else {
                                totalDurationMsLocked9 = 0;
                            }
                            if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j57);
                            } else {
                                maxDurationMsLocked2 = 0;
                            }
                            sb2.setLength(0);
                            if (totalDurationMsLocked6 != totalTimeLocked5) {
                                sb2.append(str3);
                                sb2.append("    Bluetooth Scan (total blamed realtime): ");
                                formatTimeMs(sb2, totalTimeLocked5);
                                sb2.append(" (");
                                sb2.append(countLocked8);
                                sb2.append(" times)");
                                if (bluetoothScanTimer.isRunningLocked()) {
                                    sb2.append(" (currently running)");
                                }
                                sb2.append("\n");
                            }
                            sb2.append(str3);
                            sb2.append("    Bluetooth Scan (total actual realtime): ");
                            formatTimeMs(sb2, totalDurationMsLocked6);
                            sb2.append(" (");
                            sb2.append(countLocked8);
                            sb2.append(" times)");
                            if (bluetoothScanTimer.isRunningLocked()) {
                                sb2.append(" (currently running)");
                            }
                            sb2.append("\n");
                            if (totalDurationMsLocked7 <= 0) {
                                sb2.append(str3);
                                sb2.append("    Bluetooth Scan (background realtime): ");
                                formatTimeMs(sb2, totalDurationMsLocked7);
                                sb2.append(" (");
                                sb2.append(countLocked9);
                                sb2.append(" times)");
                                if (bluetoothScanBackgroundTimer != null) {
                                    sb2.append(" (currently running in background)");
                                }
                                sb2.append("\n");
                            } else {
                                sb2.append(str3);
                                sb2.append("    Bluetooth Scan (background realtime): ");
                                formatTimeMs(sb2, totalDurationMsLocked7);
                                sb2.append(" (");
                                sb2.append(countLocked9);
                                sb2.append(" times)");
                                if (bluetoothScanBackgroundTimer != null) {
                                    sb2.append(" (currently running in background)");
                                }
                                sb2.append("\n");
                            }
                            sb2.append(str3);
                            sb2.append("    Bluetooth Scan Results: ");
                            sb2.append(countLocked10);
                            sb2.append(" (");
                            sb2.append(countLocked11);
                            sb2.append(" in background)");
                            j40 = totalDurationMsLocked8;
                            if (j40 <= 0) {
                                j41 = totalDurationMsLocked9;
                                if (j41 > 0) {
                                }
                                printWriter3 = printWriter;
                                printWriter3.println(sb2.toString());
                                z3 = true;
                            } else {
                                j41 = totalDurationMsLocked9;
                            }
                            sb2.append("\n");
                            sb2.append(str3);
                            sb2.append("    Unoptimized Bluetooth Scan (realtime): ");
                            formatTimeMs(sb2, j40);
                            sb2.append(" (max ");
                            formatTimeMs(sb2, maxDurationMsLocked);
                            sb2.append(")");
                            if (bluetoothUnoptimizedScanTimer != 0) {
                                sb2.append(" (currently running unoptimized)");
                            }
                            if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                                sb2.append("\n");
                                sb2.append(str3);
                                sb2.append("    Unoptimized Bluetooth Scan (background realtime): ");
                                formatTimeMs(sb2, j41);
                                sb2.append(" (max ");
                                formatTimeMs(sb2, maxDurationMsLocked2);
                                sb2.append(")");
                                if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                    sb2.append(" (currently running unoptimized in background)");
                                }
                            }
                            printWriter3 = printWriter;
                            printWriter3.println(sb2.toString());
                            z3 = true;
                        } else {
                            j10 = j10;
                            printWriter3 = printWriter2;
                            j57 = j57;
                            z3 = false;
                        }
                    } else {
                        j10 = j10;
                        printWriter3 = printWriter2;
                        j57 = j57;
                        z3 = false;
                    }
                    if (uid5.hasUserActivity()) {
                        i22 = 0;
                        z11 = false;
                        while (i22 < 4) {
                            Uid uid18 = uid5;
                            userActivityCount = uid18.getUserActivityCount(i22, i);
                            if (userActivityCount != 0) {
                                if (z11) {
                                    sb2.setLength(0);
                                    sb2.append("    User activity: ");
                                    z11 = true;
                                } else {
                                    sb2.append(", ");
                                }
                                sb2.append(userActivityCount);
                                sb2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                sb2.append(Uid.USER_ACTIVITY_TYPES[i22]);
                            }
                            i22++;
                            uid5 = uid18;
                        }
                        uid = uid5;
                        i7 = i;
                        if (z11) {
                            printWriter3.println(sb2.toString());
                        }
                    } else {
                        uid = uid5;
                        i7 = i;
                    }
                    wakelockStats = uid.getWakelockStats();
                    size = wakelockStats.size() - 1;
                    j12 = 0;
                    j13 = 0;
                    j14 = 0;
                    jComputeWakeLock = 0;
                    i8 = 0;
                    while (size >= 0) {
                        Uid.Wakelock wakelockValueAt4 = wakelockStats.valueAt(size);
                        sb2.setLength(0);
                        sb2.append(str3);
                        sb2.append("    Wake lock ");
                        sb2.append(wakelockStats.keyAt(size));
                        Uid uid19 = uid;
                        long j612 = j12;
                        ArrayMap<String, ? extends Uid.Wakelock> arrayMap3 = wakelockStats;
                        int i411 = size;
                        long j613 = j13;
                        long j7113 = j10;
                        long j7114 = j14;
                        long j7115 = j57;
                        String strPrintWakeLock5 = printWakeLock(sb2, wakelockValueAt4.getWakeTime(1), j7113, "full", i, ": ");
                        wakeTime = wakelockValueAt4.getWakeTime(0);
                        String strPrintWakeLock6 = printWakeLock(sb2, wakeTime, j7113, Slice.HINT_PARTIAL, i, strPrintWakeLock5);
                        if (wakeTime != null) {
                            subTimer4 = wakeTime.getSubTimer();
                        } else {
                            subTimer4 = null;
                        }
                        long j7116 = j10;
                        StringBuilder sb14 = sb2;
                        printWakeLock(sb14, wakelockValueAt4.getWakeTime(18), j7116, "draw", i, printWakeLock(sb14, wakelockValueAt4.getWakeTime(2), j7116, Context.WINDOW_SERVICE, i, printWakeLock(sb2, subTimer4, j7116, "background partial", i, strPrintWakeLock6)));
                        sb2.append(" realtime");
                        printWriter3.println(sb2.toString());
                        i8++;
                        long jComputeWakeLock9 = j7114 + computeWakeLock(wakelockValueAt4.getWakeTime(1), j7116, i7);
                        long jComputeWakeLock10 = computeWakeLock(wakelockValueAt4.getWakeTime(0), j7116, i7) + j613;
                        long jComputeWakeLock11 = j612 + computeWakeLock(wakelockValueAt4.getWakeTime(2), j7116, i7);
                        jComputeWakeLock += computeWakeLock(wakelockValueAt4.getWakeTime(18), j7116, i7);
                        uid = uid19;
                        j57 = j7115;
                        j14 = jComputeWakeLock9;
                        str3 = str;
                        j13 = jComputeWakeLock10;
                        j12 = jComputeWakeLock11;
                        wakelockStats = arrayMap3;
                        size = i411 - 1;
                        z3 = true;
                    }
                    j15 = j12;
                    j16 = j13;
                    z4 = z3;
                    j17 = j14;
                    uid2 = uid;
                    j18 = j10;
                    j19 = j57;
                    j20 = jComputeWakeLock;
                    if (i8 > 1) {
                        if (uid2.getAggregatedPartialWakelockTimer() != null) {
                            Timer aggregatedPartialWakelockTimer3 = uid2.getAggregatedPartialWakelockTimer();
                            long totalDurationMsLocked12 = aggregatedPartialWakelockTimer3.getTotalDurationMsLocked(j19);
                            subTimer3 = aggregatedPartialWakelockTimer3.getSubTimer();
                            if (subTimer3 != null) {
                                totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j19);
                            } else {
                                totalDurationMsLocked5 = 0;
                            }
                            j39 = j19;
                            j37 = totalDurationMsLocked12;
                            j38 = totalDurationMsLocked5;
                        } else {
                            j37 = 0;
                            j38 = 0;
                            j39 = j19;
                        }
                        if (j37 != 0) {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    TOTAL wake: ");
                            if (j17 != 0) {
                                formatTimeMs(sb2, j17);
                                sb2.append("full");
                                z10 = true;
                            } else {
                                z10 = false;
                            }
                            if (j16 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j16);
                                sb2.append("blamed partial");
                                z10 = true;
                            }
                            if (j37 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j37);
                                sb2.append("actual partial");
                                z10 = true;
                            }
                            if (j38 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j38);
                                sb2.append("actual background partial");
                                z10 = true;
                            }
                            if (j15 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j15);
                                sb2.append(Context.WINDOW_SERVICE);
                                z10 = true;
                            }
                            if (j20 != 0) {
                                if (z10) {
                                    sb2.append(",");
                                }
                                formatTimeMs(sb2, j20);
                                sb2.append("draw");
                            }
                            sb2.append(" realtime");
                            j21 = j39;
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                        } else {
                            sb2.setLength(0);
                            sb2.append(str);
                            sb2.append("    TOTAL wake: ");
                            if (j17 != 0) {
                                formatTimeMs(sb2, j17);
                                sb2.append("full");
                                z10 = true;
                            } else {
                                z10 = false;
                            }
                            if (j16 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j16);
                                sb2.append("blamed partial");
                                z10 = true;
                            }
                            if (j37 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j37);
                                sb2.append("actual partial");
                                z10 = true;
                            }
                            if (j38 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j38);
                                sb2.append("actual background partial");
                                z10 = true;
                            }
                            if (j15 != 0) {
                                if (z10) {
                                    sb2.append(", ");
                                }
                                formatTimeMs(sb2, j15);
                                sb2.append(Context.WINDOW_SERVICE);
                                z10 = true;
                            }
                            if (j20 != 0) {
                                if (z10) {
                                    sb2.append(",");
                                }
                                formatTimeMs(sb2, j20);
                                sb2.append("draw");
                            }
                            sb2.append(" realtime");
                            j21 = j39;
                            printWriter4 = printWriter;
                            printWriter4.println(sb2.toString());
                        }
                    } else {
                        z4 = z4;
                        printWriter4 = printWriter3;
                        j21 = j19;
                    }
                    multicastWakelockStats = uid2.getMulticastWakelockStats();
                    if (multicastWakelockStats != null) {
                        j22 = j18;
                        totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j22, i7);
                        countLocked7 = multicastWakelockStats.getCountLocked(i7);
                        if (totalTimeLocked4 > 0) {
                            sb2.setLength(0);
                            str4 = str;
                            sb2.append(str4);
                            sb2.append("    WiFi Multicast Wakelock");
                            sb2.append(" count = ");
                            sb2.append(countLocked7);
                            sb2.append(" time = ");
                            formatTimeMsNoSpace(sb2, (totalTimeLocked4 + 500) / 1000);
                            printWriter4.println(sb2.toString());
                        }
                        syncStats = uid2.getSyncStats();
                        size2 = syncStats.size() - 1;
                        while (size2 >= 0) {
                            Timer timerValueAt9 = syncStats.valueAt(size2);
                            totalTimeLocked3 = (timerValueAt9.getTotalTimeLocked(j22, i7) + 500) / 1000;
                            countLocked5 = timerValueAt9.getCountLocked(i7);
                            subTimer2 = timerValueAt9.getSubTimer();
                            if (subTimer2 != null) {
                                totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                            } else {
                                totalDurationMsLocked4 = -1;
                            }
                            if (subTimer2 != null) {
                                countLocked6 = subTimer2.getCountLocked(i7);
                            } else {
                                countLocked6 = i3;
                            }
                            sb2.setLength(0);
                            sb2.append(str4);
                            sb2.append("    Sync ");
                            sb2.append(syncStats.keyAt(size2));
                            sb2.append(": ");
                            if (totalTimeLocked3 != 0) {
                                formatTimeMs(sb2, totalTimeLocked3);
                                sb2.append("realtime (");
                                sb2.append(countLocked5);
                                sb2.append(" times)");
                                if (totalDurationMsLocked4 > 0) {
                                    sb2.append(", ");
                                    formatTimeMs(sb2, totalDurationMsLocked4);
                                    sb2.append("background (");
                                    sb2.append(countLocked6);
                                    sb2.append(" times)");
                                }
                            } else {
                                sb2.append("(not used)");
                            }
                            printWriter4.println(sb2.toString());
                            size2--;
                            j21 = j21;
                            z4 = true;
                        }
                        j23 = j21;
                        jobStats = uid2.getJobStats();
                        size3 = jobStats.size() - 1;
                        while (size3 >= 0) {
                            Timer timerValueAt10 = jobStats.valueAt(size3);
                            totalTimeLocked2 = (timerValueAt10.getTotalTimeLocked(j22, i7) + 500) / 1000;
                            countLocked3 = timerValueAt10.getCountLocked(i7);
                            subTimer = timerValueAt10.getSubTimer();
                            if (subTimer != null) {
                                j36 = j23;
                                totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                            } else {
                                j36 = j23;
                                totalDurationMsLocked3 = -1;
                            }
                            if (subTimer != null) {
                                countLocked4 = subTimer.getCountLocked(i7);
                            } else {
                                countLocked4 = i3;
                            }
                            sb2.setLength(0);
                            sb2.append(str4);
                            sb2.append("    Job ");
                            sb2.append(jobStats.keyAt(size3));
                            sb2.append(": ");
                            if (totalTimeLocked2 != 0) {
                                formatTimeMs(sb2, totalTimeLocked2);
                                sb2.append("realtime (");
                                sb2.append(countLocked3);
                                sb2.append(" times)");
                                if (totalDurationMsLocked3 > 0) {
                                    sb2.append(", ");
                                    formatTimeMs(sb2, totalDurationMsLocked3);
                                    sb2.append("background (");
                                    sb2.append(countLocked4);
                                    sb2.append(" times)");
                                }
                            } else {
                                sb2.append("(not used)");
                            }
                            printWriter4.println(sb2.toString());
                            size3--;
                            j23 = j36;
                            j22 = j22;
                            z4 = true;
                        }
                        long j7117 = j22;
                        long j7118 = j23;
                        i9 = 0;
                        jobCompletionStats = uid2.getJobCompletionStats();
                        while (size4 >= 0) {
                            sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                            if (sparseIntArrayValueAt != null) {
                                printWriter.print(str);
                                printWriter4.print("    Job Completions ");
                                printWriter4.print(jobCompletionStats.keyAt(size4));
                                printWriter4.print(SettingsStringUtil.DELIMITER);
                                while (i21 < sparseIntArrayValueAt.size()) {
                                    printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                    printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                    printWriter4.print("(");
                                    printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                    printWriter4.print("x)");
                                }
                                printWriter.println();
                            }
                        }
                        uid2.getDeferredJobsLineLocked(sb2, i7);
                        if (sb2.length() > 0) {
                            printWriter4.print("    Jobs deferred on launch ");
                            printWriter4.println(sb2.toString());
                        }
                        j24 = j7117;
                        uid3 = uid2;
                        sb3 = sb2;
                        i10 = i7;
                        j25 = j67;
                        sparseArray = sparseArray8;
                        j26 = j7118;
                        i11 = i48;
                        boolean zPrintTimer6 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                        sensorStats = uid3.getSensorStats();
                        size5 = sensorStats.size();
                        z5 = zPrintTimer6;
                        i12 = 0;
                        while (i12 < size5) {
                            sensorValueAt = sensorStats.valueAt(i12);
                            sensorStats.keyAt(i12);
                            sb4 = sb3;
                            sb4.setLength(i9);
                            sb4.append(str);
                            sb4.append("    Sensor ");
                            handle = sensorValueAt.getHandle();
                            if (handle == -10000) {
                                sb4.append("GPS");
                            } else {
                                sb4.append(handle);
                            }
                            sb4.append(": ");
                            sensorTime = sensorValueAt.getSensorTime();
                            if (sensorTime != null) {
                                totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                                countLocked = sensorTime.getCountLocked(i10);
                                sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                                if (sensorBackgroundTime != null) {
                                    countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                                } else {
                                    countLocked2 = i9;
                                }
                                j35 = j26;
                                totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                                if (sensorBackgroundTime != null) {
                                    totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                                } else {
                                    totalDurationMsLocked2 = 0;
                                }
                                if (totalTimeLocked != 0) {
                                    if (totalDurationMsLocked != totalTimeLocked) {
                                        formatTimeMs(sb4, totalTimeLocked);
                                        sb4.append("blamed realtime, ");
                                    }
                                    formatTimeMs(sb4, totalDurationMsLocked);
                                    sb4.append("realtime (");
                                    sb4.append(countLocked);
                                    sb4.append(" times)");
                                    if (totalDurationMsLocked2 == 0) {
                                        sb4.append(", ");
                                        formatTimeMs(sb4, totalDurationMsLocked2);
                                        sb4.append("background (");
                                        sb4.append(countLocked2);
                                        sb4.append(" times)");
                                    } else {
                                        sb4.append(", ");
                                        formatTimeMs(sb4, totalDurationMsLocked2);
                                        sb4.append("background (");
                                        sb4.append(countLocked2);
                                        sb4.append(" times)");
                                    }
                                } else {
                                    sb4.append("(not used)");
                                }
                            } else {
                                j24 = j24;
                                sensorStats = sensorStats;
                                size5 = size5;
                                j35 = j26;
                                sb4.append("(not used)");
                            }
                            printWriter4.println(sb4.toString());
                            i12++;
                            j26 = j35;
                            sb3 = sb4;
                            z5 = true;
                            j24 = j24;
                            sensorStats = sensorStats;
                            size5 = size5;
                            i9 = 0;
                        }
                        j27 = j24;
                        StringBuilder sb15 = sb3;
                        j28 = j26;
                        sb = sb15;
                        zPrintTimer = z5 | printTimer(printWriter, sb15, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                        j29 = 0;
                        i13 = 0;
                        while (i13 < 7) {
                            Uid uid110 = uid3;
                            long j7119 = j27;
                            processStateTime = uid110.getProcessStateTime(i13, j7119, i10);
                            if (processStateTime > 0) {
                                j29 += processStateTime;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    ");
                                sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                                sb.append(" for: ");
                                formatTimeMs(sb, (processStateTime + 500) / 1000);
                                printWriter4.println(sb.toString());
                                zPrintTimer = true;
                            }
                            i13++;
                            j27 = j7119;
                            uid3 = uid110;
                        }
                        uid4 = uid3;
                        j30 = j27;
                        if (j29 > 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total running: ");
                            formatTimeMs(sb, (j29 + 500) / 1000);
                            printWriter4.println(sb.toString());
                        }
                        userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                        systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                        if (userCpuTimeUs <= 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total cpu time: u=");
                            formatTimeMs(sb, userCpuTimeUs / 1000);
                            sb.append("s=");
                            formatTimeMs(sb, systemCpuTimeUs / 1000);
                            printWriter4.println(sb.toString());
                        } else {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Total cpu time: u=");
                            formatTimeMs(sb, userCpuTimeUs / 1000);
                            sb.append("s=");
                            formatTimeMs(sb, systemCpuTimeUs / 1000);
                            printWriter4.println(sb.toString());
                        }
                        cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                        if (cpuFreqTimes != null) {
                            sb.setLength(0);
                            sb.append("    Total cpu time per freq:");
                            while (i20 < cpuFreqTimes.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                            }
                            printWriter4.println(sb.toString());
                        }
                        screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                        if (screenOffCpuFreqTimes != null) {
                            sb.setLength(0);
                            sb.append("    Total screen-off cpu time per freq:");
                            while (i19 < screenOffCpuFreqTimes.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                            }
                            printWriter4.println(sb.toString());
                        }
                        i14 = 0;
                        while (i14 < 7) {
                            cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                            if (cpuFreqTimes2 != null) {
                                sb.setLength(0);
                                sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                i18 = 0;
                                while (i18 < cpuFreqTimes2.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                    i18++;
                                    sparseArray = sparseArray;
                                    j28 = j28;
                                }
                                sparseArray3 = sparseArray;
                                j34 = j28;
                                printWriter4.println(sb.toString());
                            } else {
                                sparseArray3 = sparseArray;
                                j34 = j28;
                            }
                            screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                            if (screenOffCpuFreqTimes2 != null) {
                                sb.setLength(0);
                                sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                                while (i17 < screenOffCpuFreqTimes2.length) {
                                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                                }
                                printWriter4.println(sb.toString());
                            }
                            i14++;
                            sparseArray = sparseArray3;
                            j28 = j34;
                        }
                        sparseArray2 = sparseArray;
                        j31 = j28;
                        processStats = uid4.getProcessStats();
                        size6 = processStats.size() - 1;
                        z6 = zPrintTimer;
                        while (size6 >= 0) {
                            procValueAt = processStats.valueAt(size6);
                            userTime = procValueAt.getUserTime(i10);
                            systemTime = procValueAt.getSystemTime(i10);
                            long j816 = j30;
                            foregroundTime = procValueAt.getForegroundTime(i10);
                            starts2 = procValueAt.getStarts(i10);
                            boolean z113 = z6;
                            numCrashes = procValueAt.getNumCrashes(i10);
                            Uid uid111 = uid4;
                            numAnrs = procValueAt.getNumAnrs(i10);
                            if (i10 == 0) {
                                iCountExcessivePowers = procValueAt.countExcessivePowers();
                            } else {
                                iCountExcessivePowers = 0;
                            }
                            if (userTime != 0) {
                                proc = procValueAt;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Proc ");
                                sb.append(processStats.keyAt(size6));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("      CPU: ");
                                formatTimeMs(sb, userTime);
                                sb.append("usr + ");
                                formatTimeMs(sb, systemTime);
                                sb.append("krn ; ");
                                formatTimeMs(sb, foregroundTime);
                                sb.append(FOREGROUND_ACTIVITY_DATA);
                                if (starts2 == 0) {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                } else {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                }
                                printWriter4.println(sb.toString());
                                i16 = 0;
                                while (i16 < iCountExcessivePowers) {
                                    Uid.Proc proc9 = proc;
                                    excessivePower = proc9.getExcessivePower(i16);
                                    if (excessivePower != null) {
                                        printWriter.print(str);
                                        printWriter4.print("      * Killed for ");
                                        if (excessivePower.type == 2) {
                                            printWriter4.print(CPU_DATA);
                                        } else {
                                            printWriter4.print("unknown");
                                        }
                                        printWriter4.print(" use: ");
                                        TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                        printWriter4.print(" over ");
                                        TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                        if (excessivePower.overTime != 0) {
                                            printWriter4.print(" (");
                                            printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                            printWriter4.println("%)");
                                        }
                                    }
                                    i16++;
                                    proc = proc9;
                                }
                                z6 = true;
                            } else {
                                proc = procValueAt;
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("    Proc ");
                                sb.append(processStats.keyAt(size6));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("      CPU: ");
                                formatTimeMs(sb, userTime);
                                sb.append("usr + ");
                                formatTimeMs(sb, systemTime);
                                sb.append("krn ; ");
                                formatTimeMs(sb, foregroundTime);
                                sb.append(FOREGROUND_ACTIVITY_DATA);
                                if (starts2 == 0) {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                } else {
                                    sb.append("\n");
                                    sb.append(str);
                                    sb.append("      ");
                                    if (starts2 != 0) {
                                        sb.append(starts2);
                                        sb.append(" starts");
                                        z9 = true;
                                    } else {
                                        z9 = false;
                                    }
                                    if (numCrashes != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numCrashes);
                                        sb.append(" crashes");
                                        z9 = true;
                                    }
                                    if (numAnrs != 0) {
                                        if (z9) {
                                            sb.append(", ");
                                        }
                                        sb.append(numAnrs);
                                        sb.append(" anrs");
                                    }
                                }
                                printWriter4.println(sb.toString());
                                i16 = 0;
                                while (i16 < iCountExcessivePowers) {
                                    Uid.Proc proc10 = proc;
                                    excessivePower = proc10.getExcessivePower(i16);
                                    if (excessivePower != null) {
                                        printWriter.print(str);
                                        printWriter4.print("      * Killed for ");
                                        if (excessivePower.type == 2) {
                                            printWriter4.print(CPU_DATA);
                                        } else {
                                            printWriter4.print("unknown");
                                        }
                                        printWriter4.print(" use: ");
                                        TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                        printWriter4.print(" over ");
                                        TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                        if (excessivePower.overTime != 0) {
                                            printWriter4.print(" (");
                                            printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                            printWriter4.println("%)");
                                        }
                                    }
                                    i16++;
                                    proc = proc10;
                                }
                                z6 = true;
                            }
                            size6--;
                            j30 = j816;
                            uid4 = uid111;
                            i10 = i;
                        }
                        j32 = j30;
                        packageStats = uid4.getPackageStats();
                        size7 = packageStats.size() - 1;
                        z7 = z6;
                        while (size7 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("    Apk ");
                            printWriter4.print(packageStats.keyAt(size7));
                            printWriter4.println(SettingsStringUtil.DELIMITER);
                            Uid.Pkg pkgValueAt5 = packageStats.valueAt(size7);
                            wakeupAlarmStats = pkgValueAt5.getWakeupAlarmStats();
                            size8 = wakeupAlarmStats.size() - 1;
                            z8 = false;
                            while (size8 >= 0) {
                                printWriter.print(str);
                                printWriter4.print("      Wakeup alarm ");
                                printWriter4.print(wakeupAlarmStats.keyAt(size8));
                                printWriter4.print(": ");
                                printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                                printWriter4.println(" times");
                                size8--;
                                z8 = true;
                            }
                            serviceStats = pkgValueAt5.getServiceStats();
                            size9 = serviceStats.size() - 1;
                            while (size9 >= 0) {
                                Uid.Pkg.Serv servValueAt5 = serviceStats.valueAt(size9);
                                long j817 = j56;
                                startTime = servValueAt5.getStartTime(j817, i);
                                starts = servValueAt5.getStarts(i);
                                launches = servValueAt5.getLaunches(i);
                                if (startTime == 0) {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("      Service ");
                                    sb.append(serviceStats.keyAt(size9));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("        Created for: ");
                                    formatTimeMs(sb, startTime / 1000);
                                    sb.append("uptime\n");
                                    sb.append(str);
                                    sb.append("        Starts: ");
                                    sb.append(starts);
                                    sb.append(", launches: ");
                                    sb.append(launches);
                                    printWriter4.println(sb.toString());
                                    z8 = true;
                                } else {
                                    sb.setLength(0);
                                    sb.append(str);
                                    sb.append("      Service ");
                                    sb.append(serviceStats.keyAt(size9));
                                    sb.append(":\n");
                                    sb.append(str);
                                    sb.append("        Created for: ");
                                    formatTimeMs(sb, startTime / 1000);
                                    sb.append("uptime\n");
                                    sb.append(str);
                                    sb.append("        Starts: ");
                                    sb.append(starts);
                                    sb.append(", launches: ");
                                    sb.append(launches);
                                    printWriter4.println(sb.toString());
                                    z8 = true;
                                }
                                size9--;
                                j56 = j817;
                            }
                            long j818 = j56;
                            if (!z8) {
                                printWriter.print(str);
                                printWriter4.println("      (nothing executed)");
                            }
                            size7--;
                            j56 = j818;
                            z7 = true;
                        }
                        j33 = j56;
                        i15 = 0;
                        if (!z7) {
                            printWriter.print(str);
                            printWriter4.println("    (nothing executed)");
                        }
                        i46 = i11 + 1;
                        i32 = i2;
                        printWriter2 = printWriter4;
                        i5 = i15;
                        j56 = j33;
                        i35 = i6;
                        j3 = j11;
                        j58 = j25;
                        sparseArray7 = sparseArray2;
                        j57 = j31;
                        j2 = j32;
                        batteryStats = this;
                    } else {
                        j22 = j18;
                    }
                    str4 = str;
                    syncStats = uid2.getSyncStats();
                    size2 = syncStats.size() - 1;
                    while (size2 >= 0) {
                        Timer timerValueAt11 = syncStats.valueAt(size2);
                        totalTimeLocked3 = (timerValueAt11.getTotalTimeLocked(j22, i7) + 500) / 1000;
                        countLocked5 = timerValueAt11.getCountLocked(i7);
                        subTimer2 = timerValueAt11.getSubTimer();
                        if (subTimer2 != null) {
                            totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                        } else {
                            totalDurationMsLocked4 = -1;
                        }
                        if (subTimer2 != null) {
                            countLocked6 = subTimer2.getCountLocked(i7);
                        } else {
                            countLocked6 = i3;
                        }
                        sb2.setLength(0);
                        sb2.append(str4);
                        sb2.append("    Sync ");
                        sb2.append(syncStats.keyAt(size2));
                        sb2.append(": ");
                        if (totalTimeLocked3 != 0) {
                            formatTimeMs(sb2, totalTimeLocked3);
                            sb2.append("realtime (");
                            sb2.append(countLocked5);
                            sb2.append(" times)");
                            if (totalDurationMsLocked4 > 0) {
                                sb2.append(", ");
                                formatTimeMs(sb2, totalDurationMsLocked4);
                                sb2.append("background (");
                                sb2.append(countLocked6);
                                sb2.append(" times)");
                            }
                        } else {
                            sb2.append("(not used)");
                        }
                        printWriter4.println(sb2.toString());
                        size2--;
                        j21 = j21;
                        z4 = true;
                    }
                    j23 = j21;
                    jobStats = uid2.getJobStats();
                    size3 = jobStats.size() - 1;
                    while (size3 >= 0) {
                        Timer timerValueAt12 = jobStats.valueAt(size3);
                        totalTimeLocked2 = (timerValueAt12.getTotalTimeLocked(j22, i7) + 500) / 1000;
                        countLocked3 = timerValueAt12.getCountLocked(i7);
                        subTimer = timerValueAt12.getSubTimer();
                        if (subTimer != null) {
                            j36 = j23;
                            totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                        } else {
                            j36 = j23;
                            totalDurationMsLocked3 = -1;
                        }
                        if (subTimer != null) {
                            countLocked4 = subTimer.getCountLocked(i7);
                        } else {
                            countLocked4 = i3;
                        }
                        sb2.setLength(0);
                        sb2.append(str4);
                        sb2.append("    Job ");
                        sb2.append(jobStats.keyAt(size3));
                        sb2.append(": ");
                        if (totalTimeLocked2 != 0) {
                            formatTimeMs(sb2, totalTimeLocked2);
                            sb2.append("realtime (");
                            sb2.append(countLocked3);
                            sb2.append(" times)");
                            if (totalDurationMsLocked3 > 0) {
                                sb2.append(", ");
                                formatTimeMs(sb2, totalDurationMsLocked3);
                                sb2.append("background (");
                                sb2.append(countLocked4);
                                sb2.append(" times)");
                            }
                        } else {
                            sb2.append("(not used)");
                        }
                        printWriter4.println(sb2.toString());
                        size3--;
                        j23 = j36;
                        j22 = j22;
                        z4 = true;
                    }
                    long j71110 = j22;
                    long j71111 = j23;
                    i9 = 0;
                    jobCompletionStats = uid2.getJobCompletionStats();
                    while (size4 >= 0) {
                        sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                        if (sparseIntArrayValueAt != null) {
                            printWriter.print(str);
                            printWriter4.print("    Job Completions ");
                            printWriter4.print(jobCompletionStats.keyAt(size4));
                            printWriter4.print(SettingsStringUtil.DELIMITER);
                            while (i21 < sparseIntArrayValueAt.size()) {
                                printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                printWriter4.print("(");
                                printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                printWriter4.print("x)");
                            }
                            printWriter.println();
                        }
                    }
                    uid2.getDeferredJobsLineLocked(sb2, i7);
                    if (sb2.length() > 0) {
                        printWriter4.print("    Jobs deferred on launch ");
                        printWriter4.println(sb2.toString());
                    }
                    j24 = j71110;
                    uid3 = uid2;
                    sb3 = sb2;
                    i10 = i7;
                    j25 = j67;
                    sparseArray = sparseArray8;
                    j26 = j71111;
                    i11 = i48;
                    boolean zPrintTimer7 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                    sensorStats = uid3.getSensorStats();
                    size5 = sensorStats.size();
                    z5 = zPrintTimer7;
                    i12 = 0;
                    while (i12 < size5) {
                        sensorValueAt = sensorStats.valueAt(i12);
                        sensorStats.keyAt(i12);
                        sb4 = sb3;
                        sb4.setLength(i9);
                        sb4.append(str);
                        sb4.append("    Sensor ");
                        handle = sensorValueAt.getHandle();
                        if (handle == -10000) {
                            sb4.append("GPS");
                        } else {
                            sb4.append(handle);
                        }
                        sb4.append(": ");
                        sensorTime = sensorValueAt.getSensorTime();
                        if (sensorTime != null) {
                            totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                            countLocked = sensorTime.getCountLocked(i10);
                            sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            if (sensorBackgroundTime != null) {
                                countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                            } else {
                                countLocked2 = i9;
                            }
                            j35 = j26;
                            totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                            if (sensorBackgroundTime != null) {
                                totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                            } else {
                                totalDurationMsLocked2 = 0;
                            }
                            if (totalTimeLocked != 0) {
                                if (totalDurationMsLocked != totalTimeLocked) {
                                    formatTimeMs(sb4, totalTimeLocked);
                                    sb4.append("blamed realtime, ");
                                }
                                formatTimeMs(sb4, totalDurationMsLocked);
                                sb4.append("realtime (");
                                sb4.append(countLocked);
                                sb4.append(" times)");
                                if (totalDurationMsLocked2 == 0) {
                                    sb4.append(", ");
                                    formatTimeMs(sb4, totalDurationMsLocked2);
                                    sb4.append("background (");
                                    sb4.append(countLocked2);
                                    sb4.append(" times)");
                                } else {
                                    sb4.append(", ");
                                    formatTimeMs(sb4, totalDurationMsLocked2);
                                    sb4.append("background (");
                                    sb4.append(countLocked2);
                                    sb4.append(" times)");
                                }
                            } else {
                                sb4.append("(not used)");
                            }
                        } else {
                            j24 = j24;
                            sensorStats = sensorStats;
                            size5 = size5;
                            j35 = j26;
                            sb4.append("(not used)");
                        }
                        printWriter4.println(sb4.toString());
                        i12++;
                        j26 = j35;
                        sb3 = sb4;
                        z5 = true;
                        j24 = j24;
                        sensorStats = sensorStats;
                        size5 = size5;
                        i9 = 0;
                    }
                    j27 = j24;
                    StringBuilder sb16 = sb3;
                    j28 = j26;
                    sb = sb16;
                    zPrintTimer = z5 | printTimer(printWriter, sb16, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                    j29 = 0;
                    i13 = 0;
                    while (i13 < 7) {
                        Uid uid112 = uid3;
                        long j71112 = j27;
                        processStateTime = uid112.getProcessStateTime(i13, j71112, i10);
                        if (processStateTime > 0) {
                            j29 += processStateTime;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    ");
                            sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                            sb.append(" for: ");
                            formatTimeMs(sb, (processStateTime + 500) / 1000);
                            printWriter4.println(sb.toString());
                            zPrintTimer = true;
                        }
                        i13++;
                        j27 = j71112;
                        uid3 = uid112;
                    }
                    uid4 = uid3;
                    j30 = j27;
                    if (j29 > 0) {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total running: ");
                        formatTimeMs(sb, (j29 + 500) / 1000);
                        printWriter4.println(sb.toString());
                    }
                    userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                    systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                    if (userCpuTimeUs <= 0) {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total cpu time: u=");
                        formatTimeMs(sb, userCpuTimeUs / 1000);
                        sb.append("s=");
                        formatTimeMs(sb, systemCpuTimeUs / 1000);
                        printWriter4.println(sb.toString());
                    } else {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total cpu time: u=");
                        formatTimeMs(sb, userCpuTimeUs / 1000);
                        sb.append("s=");
                        formatTimeMs(sb, systemCpuTimeUs / 1000);
                        printWriter4.println(sb.toString());
                    }
                    cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                    if (cpuFreqTimes != null) {
                        sb.setLength(0);
                        sb.append("    Total cpu time per freq:");
                        while (i20 < cpuFreqTimes.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                        }
                        printWriter4.println(sb.toString());
                    }
                    screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                    if (screenOffCpuFreqTimes != null) {
                        sb.setLength(0);
                        sb.append("    Total screen-off cpu time per freq:");
                        while (i19 < screenOffCpuFreqTimes.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                        }
                        printWriter4.println(sb.toString());
                    }
                    i14 = 0;
                    while (i14 < 7) {
                        cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                        if (cpuFreqTimes2 != null) {
                            sb.setLength(0);
                            sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            i18 = 0;
                            while (i18 < cpuFreqTimes2.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                i18++;
                                sparseArray = sparseArray;
                                j28 = j28;
                            }
                            sparseArray3 = sparseArray;
                            j34 = j28;
                            printWriter4.println(sb.toString());
                        } else {
                            sparseArray3 = sparseArray;
                            j34 = j28;
                        }
                        screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                        if (screenOffCpuFreqTimes2 != null) {
                            sb.setLength(0);
                            sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            while (i17 < screenOffCpuFreqTimes2.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                            }
                            printWriter4.println(sb.toString());
                        }
                        i14++;
                        sparseArray = sparseArray3;
                        j28 = j34;
                    }
                    sparseArray2 = sparseArray;
                    j31 = j28;
                    processStats = uid4.getProcessStats();
                    size6 = processStats.size() - 1;
                    z6 = zPrintTimer;
                    while (size6 >= 0) {
                        procValueAt = processStats.valueAt(size6);
                        userTime = procValueAt.getUserTime(i10);
                        systemTime = procValueAt.getSystemTime(i10);
                        long j819 = j30;
                        foregroundTime = procValueAt.getForegroundTime(i10);
                        starts2 = procValueAt.getStarts(i10);
                        boolean z114 = z6;
                        numCrashes = procValueAt.getNumCrashes(i10);
                        Uid uid113 = uid4;
                        numAnrs = procValueAt.getNumAnrs(i10);
                        if (i10 == 0) {
                            iCountExcessivePowers = procValueAt.countExcessivePowers();
                        } else {
                            iCountExcessivePowers = 0;
                        }
                        if (userTime != 0) {
                            proc = procValueAt;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Proc ");
                            sb.append(processStats.keyAt(size6));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("      CPU: ");
                            formatTimeMs(sb, userTime);
                            sb.append("usr + ");
                            formatTimeMs(sb, systemTime);
                            sb.append("krn ; ");
                            formatTimeMs(sb, foregroundTime);
                            sb.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 == 0) {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            } else {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            }
                            printWriter4.println(sb.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                Uid.Proc proc11 = proc;
                                excessivePower = proc11.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        printWriter4.print(" (");
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    }
                                }
                                i16++;
                                proc = proc11;
                            }
                            z6 = true;
                        } else {
                            proc = procValueAt;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Proc ");
                            sb.append(processStats.keyAt(size6));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("      CPU: ");
                            formatTimeMs(sb, userTime);
                            sb.append("usr + ");
                            formatTimeMs(sb, systemTime);
                            sb.append("krn ; ");
                            formatTimeMs(sb, foregroundTime);
                            sb.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 == 0) {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            } else {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            }
                            printWriter4.println(sb.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                Uid.Proc proc12 = proc;
                                excessivePower = proc12.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        printWriter4.print(" (");
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    }
                                }
                                i16++;
                                proc = proc12;
                            }
                            z6 = true;
                        }
                        size6--;
                        j30 = j819;
                        uid4 = uid113;
                        i10 = i;
                    }
                    j32 = j30;
                    packageStats = uid4.getPackageStats();
                    size7 = packageStats.size() - 1;
                    z7 = z6;
                    while (size7 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("    Apk ");
                        printWriter4.print(packageStats.keyAt(size7));
                        printWriter4.println(SettingsStringUtil.DELIMITER);
                        Uid.Pkg pkgValueAt6 = packageStats.valueAt(size7);
                        wakeupAlarmStats = pkgValueAt6.getWakeupAlarmStats();
                        size8 = wakeupAlarmStats.size() - 1;
                        z8 = false;
                        while (size8 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("      Wakeup alarm ");
                            printWriter4.print(wakeupAlarmStats.keyAt(size8));
                            printWriter4.print(": ");
                            printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                            printWriter4.println(" times");
                            size8--;
                            z8 = true;
                        }
                        serviceStats = pkgValueAt6.getServiceStats();
                        size9 = serviceStats.size() - 1;
                        while (size9 >= 0) {
                            Uid.Pkg.Serv servValueAt6 = serviceStats.valueAt(size9);
                            long j8110 = j56;
                            startTime = servValueAt6.getStartTime(j8110, i);
                            starts = servValueAt6.getStarts(i);
                            launches = servValueAt6.getLaunches(i);
                            if (startTime == 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("      Service ");
                                sb.append(serviceStats.keyAt(size9));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("        Created for: ");
                                formatTimeMs(sb, startTime / 1000);
                                sb.append("uptime\n");
                                sb.append(str);
                                sb.append("        Starts: ");
                                sb.append(starts);
                                sb.append(", launches: ");
                                sb.append(launches);
                                printWriter4.println(sb.toString());
                                z8 = true;
                            } else {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("      Service ");
                                sb.append(serviceStats.keyAt(size9));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("        Created for: ");
                                formatTimeMs(sb, startTime / 1000);
                                sb.append("uptime\n");
                                sb.append(str);
                                sb.append("        Starts: ");
                                sb.append(starts);
                                sb.append(", launches: ");
                                sb.append(launches);
                                printWriter4.println(sb.toString());
                                z8 = true;
                            }
                            size9--;
                            j56 = j8110;
                        }
                        long j8111 = j56;
                        if (!z8) {
                            printWriter.print(str);
                            printWriter4.println("      (nothing executed)");
                        }
                        size7--;
                        j56 = j8111;
                        z7 = true;
                    }
                    j33 = j56;
                    i15 = 0;
                    if (!z7) {
                        printWriter.print(str);
                        printWriter4.println("    (nothing executed)");
                    }
                    i46 = i11 + 1;
                    i32 = i2;
                    printWriter2 = printWriter4;
                    i5 = i15;
                    j56 = j33;
                    i35 = i6;
                    j3 = j11;
                    j58 = j25;
                    sparseArray7 = sparseArray2;
                    j57 = j31;
                    j2 = j32;
                    batteryStats = this;
                }
                j7 = wifiScanBackgroundTime;
                j7 = wifiScanBackgroundTime;
                j8 = wifiRunningTime;
                j7 = wifiScanBackgroundTime;
                sb2 = sb7;
                sb2.setLength(0);
                long j8112 = j7;
                str3 = str;
                sb2.append(str3);
                sb2.append("    Wifi Running: ");
                long j8113 = j6;
                formatTimeMs(sb2, j8 / 1000);
                sb2.append("(");
                j9 = j3;
                sb2.append(batteryStats.formatRatioLocked(j8, j9));
                sb2.append(")\n");
                sb2.append(str3);
                sb2.append("    Full Wifi Lock: ");
                formatTimeMs(sb2, fullWifiLockTime / 1000);
                sb2.append("(");
                sb2.append(batteryStats.formatRatioLocked(fullWifiLockTime, j9));
                sb2.append(")\n");
                sb2.append(str3);
                sb2.append("    Wifi Scan (blamed): ");
                formatTimeMs(sb2, j5 / 1000);
                sb2.append("(");
                sb2.append(batteryStats.formatRatioLocked(j5, j9));
                sb2.append(") ");
                sb2.append(wifiScanCount);
                sb2.append("x\n");
                sb2.append(str3);
                sb2.append("    Wifi Scan (actual): ");
                formatTimeMs(sb2, j8113 / 1000);
                sb2.append("(");
                j10 = j64;
                sb2.append(batteryStats.formatRatioLocked(j8113, batteryStats.computeBatteryRealtime(j10, 0)));
                sb2.append(") ");
                sb2.append(wifiScanCount);
                sb2.append("x\n");
                sb2.append(str3);
                sb2.append("    Background Wifi Scan: ");
                formatTimeMs(sb2, j8112 / 1000);
                sb2.append("(");
                sb2.append(batteryStats.formatRatioLocked(j8112, batteryStats.computeBatteryRealtime(j10, 0)));
                sb2.append(") ");
                sb2.append(wifiScanBackgroundCount);
                sb2.append("x");
                printWriter2.println(sb2.toString());
                if (wifiRadioApWakeupCount > 0) {
                    sb2.setLength(0);
                    sb2.append(str3);
                    sb2.append("    WiFi AP wakeups: ");
                    sb2.append(wifiRadioApWakeupCount);
                    printWriter2.println(sb2.toString());
                }
                j11 = j9;
                printControllerActivityIfInteresting(printWriter, sb2, str3 + "  ", WIFI_CONTROLLER_NAME, uid5.getWifiControllerActivity(), i);
                if (networkActivityBytes11 <= 0) {
                    printWriter.print(str);
                    printWriter2.print("    Bluetooth network: ");
                    printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                    printWriter2.print(" received, ");
                    printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                    printWriter2.println(" sent");
                } else {
                    printWriter.print(str);
                    printWriter2.print("    Bluetooth network: ");
                    printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                    printWriter2.print(" received, ");
                    printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                    printWriter2.println(" sent");
                }
                bluetoothScanTimer = uid5.getBluetoothScanTimer();
                if (bluetoothScanTimer != null) {
                    totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                    if (totalTimeLocked5 != 0) {
                        countLocked8 = bluetoothScanTimer.getCountLocked(i);
                        bluetoothScanBackgroundTimer = uid5.getBluetoothScanBackgroundTimer();
                        if (bluetoothScanBackgroundTimer != null) {
                            countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                        } else {
                            countLocked9 = 0;
                        }
                        totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j57);
                        if (bluetoothScanBackgroundTimer != null) {
                            totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j57);
                        } else {
                            totalDurationMsLocked7 = 0;
                        }
                        if (uid5.getBluetoothScanResultCounter() != null) {
                            countLocked10 = uid5.getBluetoothScanResultCounter().getCountLocked(i);
                        } else {
                            countLocked10 = 0;
                        }
                        if (uid5.getBluetoothScanResultBgCounter() != null) {
                            countLocked11 = uid5.getBluetoothScanResultBgCounter().getCountLocked(i);
                        } else {
                            countLocked11 = 0;
                        }
                        bluetoothUnoptimizedScanTimer = uid5.getBluetoothUnoptimizedScanTimer();
                        if (bluetoothUnoptimizedScanTimer != null) {
                            totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j57);
                        } else {
                            totalDurationMsLocked8 = 0;
                        }
                        if (bluetoothUnoptimizedScanTimer != null) {
                            maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j57);
                        } else {
                            maxDurationMsLocked = 0;
                        }
                        bluetoothUnoptimizedScanBackgroundTimer = uid5.getBluetoothUnoptimizedScanBackgroundTimer();
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j57);
                        } else {
                            totalDurationMsLocked9 = 0;
                        }
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j57);
                        } else {
                            maxDurationMsLocked2 = 0;
                        }
                        sb2.setLength(0);
                        if (totalDurationMsLocked6 != totalTimeLocked5) {
                            sb2.append(str3);
                            sb2.append("    Bluetooth Scan (total blamed realtime): ");
                            formatTimeMs(sb2, totalTimeLocked5);
                            sb2.append(" (");
                            sb2.append(countLocked8);
                            sb2.append(" times)");
                            if (bluetoothScanTimer.isRunningLocked()) {
                                sb2.append(" (currently running)");
                            }
                            sb2.append("\n");
                        }
                        sb2.append(str3);
                        sb2.append("    Bluetooth Scan (total actual realtime): ");
                        formatTimeMs(sb2, totalDurationMsLocked6);
                        sb2.append(" (");
                        sb2.append(countLocked8);
                        sb2.append(" times)");
                        if (bluetoothScanTimer.isRunningLocked()) {
                            sb2.append(" (currently running)");
                        }
                        sb2.append("\n");
                        if (totalDurationMsLocked7 <= 0) {
                            sb2.append(str3);
                            sb2.append("    Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb2, totalDurationMsLocked7);
                            sb2.append(" (");
                            sb2.append(countLocked9);
                            sb2.append(" times)");
                            if (bluetoothScanBackgroundTimer != null) {
                                sb2.append(" (currently running in background)");
                            }
                            sb2.append("\n");
                        } else {
                            sb2.append(str3);
                            sb2.append("    Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb2, totalDurationMsLocked7);
                            sb2.append(" (");
                            sb2.append(countLocked9);
                            sb2.append(" times)");
                            if (bluetoothScanBackgroundTimer != null) {
                                sb2.append(" (currently running in background)");
                            }
                            sb2.append("\n");
                        }
                        sb2.append(str3);
                        sb2.append("    Bluetooth Scan Results: ");
                        sb2.append(countLocked10);
                        sb2.append(" (");
                        sb2.append(countLocked11);
                        sb2.append(" in background)");
                        j40 = totalDurationMsLocked8;
                        if (j40 <= 0) {
                            j41 = totalDurationMsLocked9;
                            if (j41 > 0) {
                            }
                            printWriter3 = printWriter;
                            printWriter3.println(sb2.toString());
                            z3 = true;
                        } else {
                            j41 = totalDurationMsLocked9;
                        }
                        sb2.append("\n");
                        sb2.append(str3);
                        sb2.append("    Unoptimized Bluetooth Scan (realtime): ");
                        formatTimeMs(sb2, j40);
                        sb2.append(" (max ");
                        formatTimeMs(sb2, maxDurationMsLocked);
                        sb2.append(")");
                        if (bluetoothUnoptimizedScanTimer != 0) {
                            sb2.append(" (currently running unoptimized)");
                        }
                        if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                            sb2.append("\n");
                            sb2.append(str3);
                            sb2.append("    Unoptimized Bluetooth Scan (background realtime): ");
                            formatTimeMs(sb2, j41);
                            sb2.append(" (max ");
                            formatTimeMs(sb2, maxDurationMsLocked2);
                            sb2.append(")");
                            if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                                sb2.append(" (currently running unoptimized in background)");
                            }
                        }
                        printWriter3 = printWriter;
                        printWriter3.println(sb2.toString());
                        z3 = true;
                    } else {
                        j10 = j10;
                        printWriter3 = printWriter2;
                        j57 = j57;
                        z3 = false;
                    }
                } else {
                    j10 = j10;
                    printWriter3 = printWriter2;
                    j57 = j57;
                    z3 = false;
                }
                if (uid5.hasUserActivity()) {
                    i22 = 0;
                    z11 = false;
                    while (i22 < 4) {
                        Uid uid114 = uid5;
                        userActivityCount = uid114.getUserActivityCount(i22, i);
                        if (userActivityCount != 0) {
                            if (z11) {
                                sb2.setLength(0);
                                sb2.append("    User activity: ");
                                z11 = true;
                            } else {
                                sb2.append(", ");
                            }
                            sb2.append(userActivityCount);
                            sb2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                            sb2.append(Uid.USER_ACTIVITY_TYPES[i22]);
                        }
                        i22++;
                        uid5 = uid114;
                    }
                    uid = uid5;
                    i7 = i;
                    if (z11) {
                        printWriter3.println(sb2.toString());
                    }
                } else {
                    uid = uid5;
                    i7 = i;
                }
                wakelockStats = uid.getWakelockStats();
                size = wakelockStats.size() - 1;
                j12 = 0;
                j13 = 0;
                j14 = 0;
                jComputeWakeLock = 0;
                i8 = 0;
                while (size >= 0) {
                    Uid.Wakelock wakelockValueAt5 = wakelockStats.valueAt(size);
                    sb2.setLength(0);
                    sb2.append(str3);
                    sb2.append("    Wake lock ");
                    sb2.append(wakelockStats.keyAt(size));
                    Uid uid115 = uid;
                    long j614 = j12;
                    ArrayMap<String, ? extends Uid.Wakelock> arrayMap4 = wakelockStats;
                    int i412 = size;
                    long j615 = j13;
                    long j71113 = j10;
                    long j71114 = j14;
                    long j71115 = j57;
                    String strPrintWakeLock7 = printWakeLock(sb2, wakelockValueAt5.getWakeTime(1), j71113, "full", i, ": ");
                    wakeTime = wakelockValueAt5.getWakeTime(0);
                    String strPrintWakeLock8 = printWakeLock(sb2, wakeTime, j71113, Slice.HINT_PARTIAL, i, strPrintWakeLock7);
                    if (wakeTime != null) {
                        subTimer4 = wakeTime.getSubTimer();
                    } else {
                        subTimer4 = null;
                    }
                    long j71116 = j10;
                    StringBuilder sb17 = sb2;
                    printWakeLock(sb17, wakelockValueAt5.getWakeTime(18), j71116, "draw", i, printWakeLock(sb17, wakelockValueAt5.getWakeTime(2), j71116, Context.WINDOW_SERVICE, i, printWakeLock(sb2, subTimer4, j71116, "background partial", i, strPrintWakeLock8)));
                    sb2.append(" realtime");
                    printWriter3.println(sb2.toString());
                    i8++;
                    long jComputeWakeLock12 = j71114 + computeWakeLock(wakelockValueAt5.getWakeTime(1), j71116, i7);
                    long jComputeWakeLock13 = computeWakeLock(wakelockValueAt5.getWakeTime(0), j71116, i7) + j615;
                    long jComputeWakeLock14 = j614 + computeWakeLock(wakelockValueAt5.getWakeTime(2), j71116, i7);
                    jComputeWakeLock += computeWakeLock(wakelockValueAt5.getWakeTime(18), j71116, i7);
                    uid = uid115;
                    j57 = j71115;
                    j14 = jComputeWakeLock12;
                    str3 = str;
                    j13 = jComputeWakeLock13;
                    j12 = jComputeWakeLock14;
                    wakelockStats = arrayMap4;
                    size = i412 - 1;
                    z3 = true;
                }
                j15 = j12;
                j16 = j13;
                z4 = z3;
                j17 = j14;
                uid2 = uid;
                j18 = j10;
                j19 = j57;
                j20 = jComputeWakeLock;
                if (i8 > 1) {
                    if (uid2.getAggregatedPartialWakelockTimer() != null) {
                        Timer aggregatedPartialWakelockTimer4 = uid2.getAggregatedPartialWakelockTimer();
                        long totalDurationMsLocked13 = aggregatedPartialWakelockTimer4.getTotalDurationMsLocked(j19);
                        subTimer3 = aggregatedPartialWakelockTimer4.getSubTimer();
                        if (subTimer3 != null) {
                            totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j19);
                        } else {
                            totalDurationMsLocked5 = 0;
                        }
                        j39 = j19;
                        j37 = totalDurationMsLocked13;
                        j38 = totalDurationMsLocked5;
                    } else {
                        j37 = 0;
                        j38 = 0;
                        j39 = j19;
                    }
                    if (j37 != 0) {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    TOTAL wake: ");
                        if (j17 != 0) {
                            formatTimeMs(sb2, j17);
                            sb2.append("full");
                            z10 = true;
                        } else {
                            z10 = false;
                        }
                        if (j16 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j16);
                            sb2.append("blamed partial");
                            z10 = true;
                        }
                        if (j37 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j37);
                            sb2.append("actual partial");
                            z10 = true;
                        }
                        if (j38 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j38);
                            sb2.append("actual background partial");
                            z10 = true;
                        }
                        if (j15 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j15);
                            sb2.append(Context.WINDOW_SERVICE);
                            z10 = true;
                        }
                        if (j20 != 0) {
                            if (z10) {
                                sb2.append(",");
                            }
                            formatTimeMs(sb2, j20);
                            sb2.append("draw");
                        }
                        sb2.append(" realtime");
                        j21 = j39;
                        printWriter4 = printWriter;
                        printWriter4.println(sb2.toString());
                    } else {
                        sb2.setLength(0);
                        sb2.append(str);
                        sb2.append("    TOTAL wake: ");
                        if (j17 != 0) {
                            formatTimeMs(sb2, j17);
                            sb2.append("full");
                            z10 = true;
                        } else {
                            z10 = false;
                        }
                        if (j16 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j16);
                            sb2.append("blamed partial");
                            z10 = true;
                        }
                        if (j37 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j37);
                            sb2.append("actual partial");
                            z10 = true;
                        }
                        if (j38 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j38);
                            sb2.append("actual background partial");
                            z10 = true;
                        }
                        if (j15 != 0) {
                            if (z10) {
                                sb2.append(", ");
                            }
                            formatTimeMs(sb2, j15);
                            sb2.append(Context.WINDOW_SERVICE);
                            z10 = true;
                        }
                        if (j20 != 0) {
                            if (z10) {
                                sb2.append(",");
                            }
                            formatTimeMs(sb2, j20);
                            sb2.append("draw");
                        }
                        sb2.append(" realtime");
                        j21 = j39;
                        printWriter4 = printWriter;
                        printWriter4.println(sb2.toString());
                    }
                } else {
                    z4 = z4;
                    printWriter4 = printWriter3;
                    j21 = j19;
                }
                multicastWakelockStats = uid2.getMulticastWakelockStats();
                if (multicastWakelockStats != null) {
                    j22 = j18;
                    totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j22, i7);
                    countLocked7 = multicastWakelockStats.getCountLocked(i7);
                    if (totalTimeLocked4 > 0) {
                        sb2.setLength(0);
                        str4 = str;
                        sb2.append(str4);
                        sb2.append("    WiFi Multicast Wakelock");
                        sb2.append(" count = ");
                        sb2.append(countLocked7);
                        sb2.append(" time = ");
                        formatTimeMsNoSpace(sb2, (totalTimeLocked4 + 500) / 1000);
                        printWriter4.println(sb2.toString());
                    }
                    syncStats = uid2.getSyncStats();
                    size2 = syncStats.size() - 1;
                    while (size2 >= 0) {
                        Timer timerValueAt13 = syncStats.valueAt(size2);
                        totalTimeLocked3 = (timerValueAt13.getTotalTimeLocked(j22, i7) + 500) / 1000;
                        countLocked5 = timerValueAt13.getCountLocked(i7);
                        subTimer2 = timerValueAt13.getSubTimer();
                        if (subTimer2 != null) {
                            totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                        } else {
                            totalDurationMsLocked4 = -1;
                        }
                        if (subTimer2 != null) {
                            countLocked6 = subTimer2.getCountLocked(i7);
                        } else {
                            countLocked6 = i3;
                        }
                        sb2.setLength(0);
                        sb2.append(str4);
                        sb2.append("    Sync ");
                        sb2.append(syncStats.keyAt(size2));
                        sb2.append(": ");
                        if (totalTimeLocked3 != 0) {
                            formatTimeMs(sb2, totalTimeLocked3);
                            sb2.append("realtime (");
                            sb2.append(countLocked5);
                            sb2.append(" times)");
                            if (totalDurationMsLocked4 > 0) {
                                sb2.append(", ");
                                formatTimeMs(sb2, totalDurationMsLocked4);
                                sb2.append("background (");
                                sb2.append(countLocked6);
                                sb2.append(" times)");
                            }
                        } else {
                            sb2.append("(not used)");
                        }
                        printWriter4.println(sb2.toString());
                        size2--;
                        j21 = j21;
                        z4 = true;
                    }
                    j23 = j21;
                    jobStats = uid2.getJobStats();
                    size3 = jobStats.size() - 1;
                    while (size3 >= 0) {
                        Timer timerValueAt14 = jobStats.valueAt(size3);
                        totalTimeLocked2 = (timerValueAt14.getTotalTimeLocked(j22, i7) + 500) / 1000;
                        countLocked3 = timerValueAt14.getCountLocked(i7);
                        subTimer = timerValueAt14.getSubTimer();
                        if (subTimer != null) {
                            j36 = j23;
                            totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                        } else {
                            j36 = j23;
                            totalDurationMsLocked3 = -1;
                        }
                        if (subTimer != null) {
                            countLocked4 = subTimer.getCountLocked(i7);
                        } else {
                            countLocked4 = i3;
                        }
                        sb2.setLength(0);
                        sb2.append(str4);
                        sb2.append("    Job ");
                        sb2.append(jobStats.keyAt(size3));
                        sb2.append(": ");
                        if (totalTimeLocked2 != 0) {
                            formatTimeMs(sb2, totalTimeLocked2);
                            sb2.append("realtime (");
                            sb2.append(countLocked3);
                            sb2.append(" times)");
                            if (totalDurationMsLocked3 > 0) {
                                sb2.append(", ");
                                formatTimeMs(sb2, totalDurationMsLocked3);
                                sb2.append("background (");
                                sb2.append(countLocked4);
                                sb2.append(" times)");
                            }
                        } else {
                            sb2.append("(not used)");
                        }
                        printWriter4.println(sb2.toString());
                        size3--;
                        j23 = j36;
                        j22 = j22;
                        z4 = true;
                    }
                    long j71117 = j22;
                    long j71118 = j23;
                    i9 = 0;
                    jobCompletionStats = uid2.getJobCompletionStats();
                    while (size4 >= 0) {
                        sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                        if (sparseIntArrayValueAt != null) {
                            printWriter.print(str);
                            printWriter4.print("    Job Completions ");
                            printWriter4.print(jobCompletionStats.keyAt(size4));
                            printWriter4.print(SettingsStringUtil.DELIMITER);
                            while (i21 < sparseIntArrayValueAt.size()) {
                                printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                                printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                                printWriter4.print("(");
                                printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                                printWriter4.print("x)");
                            }
                            printWriter.println();
                        }
                    }
                    uid2.getDeferredJobsLineLocked(sb2, i7);
                    if (sb2.length() > 0) {
                        printWriter4.print("    Jobs deferred on launch ");
                        printWriter4.println(sb2.toString());
                    }
                    j24 = j71117;
                    uid3 = uid2;
                    sb3 = sb2;
                    i10 = i7;
                    j25 = j67;
                    sparseArray = sparseArray8;
                    j26 = j71118;
                    i11 = i48;
                    boolean zPrintTimer8 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                    sensorStats = uid3.getSensorStats();
                    size5 = sensorStats.size();
                    z5 = zPrintTimer8;
                    i12 = 0;
                    while (i12 < size5) {
                        sensorValueAt = sensorStats.valueAt(i12);
                        sensorStats.keyAt(i12);
                        sb4 = sb3;
                        sb4.setLength(i9);
                        sb4.append(str);
                        sb4.append("    Sensor ");
                        handle = sensorValueAt.getHandle();
                        if (handle == -10000) {
                            sb4.append("GPS");
                        } else {
                            sb4.append(handle);
                        }
                        sb4.append(": ");
                        sensorTime = sensorValueAt.getSensorTime();
                        if (sensorTime != null) {
                            totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                            countLocked = sensorTime.getCountLocked(i10);
                            sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                            if (sensorBackgroundTime != null) {
                                countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                            } else {
                                countLocked2 = i9;
                            }
                            j35 = j26;
                            totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                            if (sensorBackgroundTime != null) {
                                totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                            } else {
                                totalDurationMsLocked2 = 0;
                            }
                            if (totalTimeLocked != 0) {
                                if (totalDurationMsLocked != totalTimeLocked) {
                                    formatTimeMs(sb4, totalTimeLocked);
                                    sb4.append("blamed realtime, ");
                                }
                                formatTimeMs(sb4, totalDurationMsLocked);
                                sb4.append("realtime (");
                                sb4.append(countLocked);
                                sb4.append(" times)");
                                if (totalDurationMsLocked2 == 0) {
                                    sb4.append(", ");
                                    formatTimeMs(sb4, totalDurationMsLocked2);
                                    sb4.append("background (");
                                    sb4.append(countLocked2);
                                    sb4.append(" times)");
                                } else {
                                    sb4.append(", ");
                                    formatTimeMs(sb4, totalDurationMsLocked2);
                                    sb4.append("background (");
                                    sb4.append(countLocked2);
                                    sb4.append(" times)");
                                }
                            } else {
                                sb4.append("(not used)");
                            }
                        } else {
                            j24 = j24;
                            sensorStats = sensorStats;
                            size5 = size5;
                            j35 = j26;
                            sb4.append("(not used)");
                        }
                        printWriter4.println(sb4.toString());
                        i12++;
                        j26 = j35;
                        sb3 = sb4;
                        z5 = true;
                        j24 = j24;
                        sensorStats = sensorStats;
                        size5 = size5;
                        i9 = 0;
                    }
                    j27 = j24;
                    StringBuilder sb18 = sb3;
                    j28 = j26;
                    sb = sb18;
                    zPrintTimer = z5 | printTimer(printWriter, sb18, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                    j29 = 0;
                    i13 = 0;
                    while (i13 < 7) {
                        Uid uid116 = uid3;
                        long j71119 = j27;
                        processStateTime = uid116.getProcessStateTime(i13, j71119, i10);
                        if (processStateTime > 0) {
                            j29 += processStateTime;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    ");
                            sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                            sb.append(" for: ");
                            formatTimeMs(sb, (processStateTime + 500) / 1000);
                            printWriter4.println(sb.toString());
                            zPrintTimer = true;
                        }
                        i13++;
                        j27 = j71119;
                        uid3 = uid116;
                    }
                    uid4 = uid3;
                    j30 = j27;
                    if (j29 > 0) {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total running: ");
                        formatTimeMs(sb, (j29 + 500) / 1000);
                        printWriter4.println(sb.toString());
                    }
                    userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                    systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                    if (userCpuTimeUs <= 0) {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total cpu time: u=");
                        formatTimeMs(sb, userCpuTimeUs / 1000);
                        sb.append("s=");
                        formatTimeMs(sb, systemCpuTimeUs / 1000);
                        printWriter4.println(sb.toString());
                    } else {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Total cpu time: u=");
                        formatTimeMs(sb, userCpuTimeUs / 1000);
                        sb.append("s=");
                        formatTimeMs(sb, systemCpuTimeUs / 1000);
                        printWriter4.println(sb.toString());
                    }
                    cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                    if (cpuFreqTimes != null) {
                        sb.setLength(0);
                        sb.append("    Total cpu time per freq:");
                        while (i20 < cpuFreqTimes.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                        }
                        printWriter4.println(sb.toString());
                    }
                    screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                    if (screenOffCpuFreqTimes != null) {
                        sb.setLength(0);
                        sb.append("    Total screen-off cpu time per freq:");
                        while (i19 < screenOffCpuFreqTimes.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                        }
                        printWriter4.println(sb.toString());
                    }
                    i14 = 0;
                    while (i14 < 7) {
                        cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                        if (cpuFreqTimes2 != null) {
                            sb.setLength(0);
                            sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            i18 = 0;
                            while (i18 < cpuFreqTimes2.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                                i18++;
                                sparseArray = sparseArray;
                                j28 = j28;
                            }
                            sparseArray3 = sparseArray;
                            j34 = j28;
                            printWriter4.println(sb.toString());
                        } else {
                            sparseArray3 = sparseArray;
                            j34 = j28;
                        }
                        screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                        if (screenOffCpuFreqTimes2 != null) {
                            sb.setLength(0);
                            sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                            while (i17 < screenOffCpuFreqTimes2.length) {
                                sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                            }
                            printWriter4.println(sb.toString());
                        }
                        i14++;
                        sparseArray = sparseArray3;
                        j28 = j34;
                    }
                    sparseArray2 = sparseArray;
                    j31 = j28;
                    processStats = uid4.getProcessStats();
                    size6 = processStats.size() - 1;
                    z6 = zPrintTimer;
                    while (size6 >= 0) {
                        procValueAt = processStats.valueAt(size6);
                        userTime = procValueAt.getUserTime(i10);
                        systemTime = procValueAt.getSystemTime(i10);
                        long j8114 = j30;
                        foregroundTime = procValueAt.getForegroundTime(i10);
                        starts2 = procValueAt.getStarts(i10);
                        boolean z115 = z6;
                        numCrashes = procValueAt.getNumCrashes(i10);
                        Uid uid117 = uid4;
                        numAnrs = procValueAt.getNumAnrs(i10);
                        if (i10 == 0) {
                            iCountExcessivePowers = procValueAt.countExcessivePowers();
                        } else {
                            iCountExcessivePowers = 0;
                        }
                        if (userTime != 0) {
                            proc = procValueAt;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Proc ");
                            sb.append(processStats.keyAt(size6));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("      CPU: ");
                            formatTimeMs(sb, userTime);
                            sb.append("usr + ");
                            formatTimeMs(sb, systemTime);
                            sb.append("krn ; ");
                            formatTimeMs(sb, foregroundTime);
                            sb.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 == 0) {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            } else {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            }
                            printWriter4.println(sb.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                Uid.Proc proc13 = proc;
                                excessivePower = proc13.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        printWriter4.print(" (");
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    }
                                }
                                i16++;
                                proc = proc13;
                            }
                            z6 = true;
                        } else {
                            proc = procValueAt;
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("    Proc ");
                            sb.append(processStats.keyAt(size6));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("      CPU: ");
                            formatTimeMs(sb, userTime);
                            sb.append("usr + ");
                            formatTimeMs(sb, systemTime);
                            sb.append("krn ; ");
                            formatTimeMs(sb, foregroundTime);
                            sb.append(FOREGROUND_ACTIVITY_DATA);
                            if (starts2 == 0) {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            } else {
                                sb.append("\n");
                                sb.append(str);
                                sb.append("      ");
                                if (starts2 != 0) {
                                    sb.append(starts2);
                                    sb.append(" starts");
                                    z9 = true;
                                } else {
                                    z9 = false;
                                }
                                if (numCrashes != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numCrashes);
                                    sb.append(" crashes");
                                    z9 = true;
                                }
                                if (numAnrs != 0) {
                                    if (z9) {
                                        sb.append(", ");
                                    }
                                    sb.append(numAnrs);
                                    sb.append(" anrs");
                                }
                            }
                            printWriter4.println(sb.toString());
                            i16 = 0;
                            while (i16 < iCountExcessivePowers) {
                                Uid.Proc proc14 = proc;
                                excessivePower = proc14.getExcessivePower(i16);
                                if (excessivePower != null) {
                                    printWriter.print(str);
                                    printWriter4.print("      * Killed for ");
                                    if (excessivePower.type == 2) {
                                        printWriter4.print(CPU_DATA);
                                    } else {
                                        printWriter4.print("unknown");
                                    }
                                    printWriter4.print(" use: ");
                                    TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                    printWriter4.print(" over ");
                                    TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                    if (excessivePower.overTime != 0) {
                                        printWriter4.print(" (");
                                        printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                        printWriter4.println("%)");
                                    }
                                }
                                i16++;
                                proc = proc14;
                            }
                            z6 = true;
                        }
                        size6--;
                        j30 = j8114;
                        uid4 = uid117;
                        i10 = i;
                    }
                    j32 = j30;
                    packageStats = uid4.getPackageStats();
                    size7 = packageStats.size() - 1;
                    z7 = z6;
                    while (size7 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("    Apk ");
                        printWriter4.print(packageStats.keyAt(size7));
                        printWriter4.println(SettingsStringUtil.DELIMITER);
                        Uid.Pkg pkgValueAt7 = packageStats.valueAt(size7);
                        wakeupAlarmStats = pkgValueAt7.getWakeupAlarmStats();
                        size8 = wakeupAlarmStats.size() - 1;
                        z8 = false;
                        while (size8 >= 0) {
                            printWriter.print(str);
                            printWriter4.print("      Wakeup alarm ");
                            printWriter4.print(wakeupAlarmStats.keyAt(size8));
                            printWriter4.print(": ");
                            printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                            printWriter4.println(" times");
                            size8--;
                            z8 = true;
                        }
                        serviceStats = pkgValueAt7.getServiceStats();
                        size9 = serviceStats.size() - 1;
                        while (size9 >= 0) {
                            Uid.Pkg.Serv servValueAt7 = serviceStats.valueAt(size9);
                            long j8115 = j56;
                            startTime = servValueAt7.getStartTime(j8115, i);
                            starts = servValueAt7.getStarts(i);
                            launches = servValueAt7.getLaunches(i);
                            if (startTime == 0) {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("      Service ");
                                sb.append(serviceStats.keyAt(size9));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("        Created for: ");
                                formatTimeMs(sb, startTime / 1000);
                                sb.append("uptime\n");
                                sb.append(str);
                                sb.append("        Starts: ");
                                sb.append(starts);
                                sb.append(", launches: ");
                                sb.append(launches);
                                printWriter4.println(sb.toString());
                                z8 = true;
                            } else {
                                sb.setLength(0);
                                sb.append(str);
                                sb.append("      Service ");
                                sb.append(serviceStats.keyAt(size9));
                                sb.append(":\n");
                                sb.append(str);
                                sb.append("        Created for: ");
                                formatTimeMs(sb, startTime / 1000);
                                sb.append("uptime\n");
                                sb.append(str);
                                sb.append("        Starts: ");
                                sb.append(starts);
                                sb.append(", launches: ");
                                sb.append(launches);
                                printWriter4.println(sb.toString());
                                z8 = true;
                            }
                            size9--;
                            j56 = j8115;
                        }
                        long j8116 = j56;
                        if (!z8) {
                            printWriter.print(str);
                            printWriter4.println("      (nothing executed)");
                        }
                        size7--;
                        j56 = j8116;
                        z7 = true;
                    }
                    j33 = j56;
                    i15 = 0;
                    if (!z7) {
                        printWriter.print(str);
                        printWriter4.println("    (nothing executed)");
                    }
                    i46 = i11 + 1;
                    i32 = i2;
                    printWriter2 = printWriter4;
                    i5 = i15;
                    j56 = j33;
                    i35 = i6;
                    j3 = j11;
                    j58 = j25;
                    sparseArray7 = sparseArray2;
                    j57 = j31;
                    j2 = j32;
                    batteryStats = this;
                } else {
                    j22 = j18;
                }
                str4 = str;
                syncStats = uid2.getSyncStats();
                size2 = syncStats.size() - 1;
                while (size2 >= 0) {
                    Timer timerValueAt15 = syncStats.valueAt(size2);
                    totalTimeLocked3 = (timerValueAt15.getTotalTimeLocked(j22, i7) + 500) / 1000;
                    countLocked5 = timerValueAt15.getCountLocked(i7);
                    subTimer2 = timerValueAt15.getSubTimer();
                    if (subTimer2 != null) {
                        totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                    } else {
                        totalDurationMsLocked4 = -1;
                    }
                    if (subTimer2 != null) {
                        countLocked6 = subTimer2.getCountLocked(i7);
                    } else {
                        countLocked6 = i3;
                    }
                    sb2.setLength(0);
                    sb2.append(str4);
                    sb2.append("    Sync ");
                    sb2.append(syncStats.keyAt(size2));
                    sb2.append(": ");
                    if (totalTimeLocked3 != 0) {
                        formatTimeMs(sb2, totalTimeLocked3);
                        sb2.append("realtime (");
                        sb2.append(countLocked5);
                        sb2.append(" times)");
                        if (totalDurationMsLocked4 > 0) {
                            sb2.append(", ");
                            formatTimeMs(sb2, totalDurationMsLocked4);
                            sb2.append("background (");
                            sb2.append(countLocked6);
                            sb2.append(" times)");
                        }
                    } else {
                        sb2.append("(not used)");
                    }
                    printWriter4.println(sb2.toString());
                    size2--;
                    j21 = j21;
                    z4 = true;
                }
                j23 = j21;
                jobStats = uid2.getJobStats();
                size3 = jobStats.size() - 1;
                while (size3 >= 0) {
                    Timer timerValueAt16 = jobStats.valueAt(size3);
                    totalTimeLocked2 = (timerValueAt16.getTotalTimeLocked(j22, i7) + 500) / 1000;
                    countLocked3 = timerValueAt16.getCountLocked(i7);
                    subTimer = timerValueAt16.getSubTimer();
                    if (subTimer != null) {
                        j36 = j23;
                        totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                    } else {
                        j36 = j23;
                        totalDurationMsLocked3 = -1;
                    }
                    if (subTimer != null) {
                        countLocked4 = subTimer.getCountLocked(i7);
                    } else {
                        countLocked4 = i3;
                    }
                    sb2.setLength(0);
                    sb2.append(str4);
                    sb2.append("    Job ");
                    sb2.append(jobStats.keyAt(size3));
                    sb2.append(": ");
                    if (totalTimeLocked2 != 0) {
                        formatTimeMs(sb2, totalTimeLocked2);
                        sb2.append("realtime (");
                        sb2.append(countLocked3);
                        sb2.append(" times)");
                        if (totalDurationMsLocked3 > 0) {
                            sb2.append(", ");
                            formatTimeMs(sb2, totalDurationMsLocked3);
                            sb2.append("background (");
                            sb2.append(countLocked4);
                            sb2.append(" times)");
                        }
                    } else {
                        sb2.append("(not used)");
                    }
                    printWriter4.println(sb2.toString());
                    size3--;
                    j23 = j36;
                    j22 = j22;
                    z4 = true;
                }
                long j711110 = j22;
                long j711111 = j23;
                i9 = 0;
                jobCompletionStats = uid2.getJobCompletionStats();
                while (size4 >= 0) {
                    sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                    if (sparseIntArrayValueAt != null) {
                        printWriter.print(str);
                        printWriter4.print("    Job Completions ");
                        printWriter4.print(jobCompletionStats.keyAt(size4));
                        printWriter4.print(SettingsStringUtil.DELIMITER);
                        while (i21 < sparseIntArrayValueAt.size()) {
                            printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                            printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                            printWriter4.print("(");
                            printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                            printWriter4.print("x)");
                        }
                        printWriter.println();
                    }
                }
                uid2.getDeferredJobsLineLocked(sb2, i7);
                if (sb2.length() > 0) {
                    printWriter4.print("    Jobs deferred on launch ");
                    printWriter4.println(sb2.toString());
                }
                j24 = j711110;
                uid3 = uid2;
                sb3 = sb2;
                i10 = i7;
                j25 = j67;
                sparseArray = sparseArray8;
                j26 = j711111;
                i11 = i48;
                boolean zPrintTimer9 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                sensorStats = uid3.getSensorStats();
                size5 = sensorStats.size();
                z5 = zPrintTimer9;
                i12 = 0;
                while (i12 < size5) {
                    sensorValueAt = sensorStats.valueAt(i12);
                    sensorStats.keyAt(i12);
                    sb4 = sb3;
                    sb4.setLength(i9);
                    sb4.append(str);
                    sb4.append("    Sensor ");
                    handle = sensorValueAt.getHandle();
                    if (handle == -10000) {
                        sb4.append("GPS");
                    } else {
                        sb4.append(handle);
                    }
                    sb4.append(": ");
                    sensorTime = sensorValueAt.getSensorTime();
                    if (sensorTime != null) {
                        totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                        countLocked = sensorTime.getCountLocked(i10);
                        sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                        if (sensorBackgroundTime != null) {
                            countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                        } else {
                            countLocked2 = i9;
                        }
                        j35 = j26;
                        totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                        if (sensorBackgroundTime != null) {
                            totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                        } else {
                            totalDurationMsLocked2 = 0;
                        }
                        if (totalTimeLocked != 0) {
                            if (totalDurationMsLocked != totalTimeLocked) {
                                formatTimeMs(sb4, totalTimeLocked);
                                sb4.append("blamed realtime, ");
                            }
                            formatTimeMs(sb4, totalDurationMsLocked);
                            sb4.append("realtime (");
                            sb4.append(countLocked);
                            sb4.append(" times)");
                            if (totalDurationMsLocked2 == 0) {
                                sb4.append(", ");
                                formatTimeMs(sb4, totalDurationMsLocked2);
                                sb4.append("background (");
                                sb4.append(countLocked2);
                                sb4.append(" times)");
                            } else {
                                sb4.append(", ");
                                formatTimeMs(sb4, totalDurationMsLocked2);
                                sb4.append("background (");
                                sb4.append(countLocked2);
                                sb4.append(" times)");
                            }
                        } else {
                            sb4.append("(not used)");
                        }
                    } else {
                        j24 = j24;
                        sensorStats = sensorStats;
                        size5 = size5;
                        j35 = j26;
                        sb4.append("(not used)");
                    }
                    printWriter4.println(sb4.toString());
                    i12++;
                    j26 = j35;
                    sb3 = sb4;
                    z5 = true;
                    j24 = j24;
                    sensorStats = sensorStats;
                    size5 = size5;
                    i9 = 0;
                }
                j27 = j24;
                StringBuilder sb19 = sb3;
                j28 = j26;
                sb = sb19;
                zPrintTimer = z5 | printTimer(printWriter, sb19, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                j29 = 0;
                i13 = 0;
                while (i13 < 7) {
                    Uid uid118 = uid3;
                    long j711112 = j27;
                    processStateTime = uid118.getProcessStateTime(i13, j711112, i10);
                    if (processStateTime > 0) {
                        j29 += processStateTime;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    ");
                        sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                        sb.append(" for: ");
                        formatTimeMs(sb, (processStateTime + 500) / 1000);
                        printWriter4.println(sb.toString());
                        zPrintTimer = true;
                    }
                    i13++;
                    j27 = j711112;
                    uid3 = uid118;
                }
                uid4 = uid3;
                j30 = j27;
                if (j29 > 0) {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total running: ");
                    formatTimeMs(sb, (j29 + 500) / 1000);
                    printWriter4.println(sb.toString());
                }
                userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                if (userCpuTimeUs <= 0) {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total cpu time: u=");
                    formatTimeMs(sb, userCpuTimeUs / 1000);
                    sb.append("s=");
                    formatTimeMs(sb, systemCpuTimeUs / 1000);
                    printWriter4.println(sb.toString());
                } else {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total cpu time: u=");
                    formatTimeMs(sb, userCpuTimeUs / 1000);
                    sb.append("s=");
                    formatTimeMs(sb, systemCpuTimeUs / 1000);
                    printWriter4.println(sb.toString());
                }
                cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                if (cpuFreqTimes != null) {
                    sb.setLength(0);
                    sb.append("    Total cpu time per freq:");
                    while (i20 < cpuFreqTimes.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                    }
                    printWriter4.println(sb.toString());
                }
                screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                if (screenOffCpuFreqTimes != null) {
                    sb.setLength(0);
                    sb.append("    Total screen-off cpu time per freq:");
                    while (i19 < screenOffCpuFreqTimes.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                    }
                    printWriter4.println(sb.toString());
                }
                i14 = 0;
                while (i14 < 7) {
                    cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                    if (cpuFreqTimes2 != null) {
                        sb.setLength(0);
                        sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        i18 = 0;
                        while (i18 < cpuFreqTimes2.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                            i18++;
                            sparseArray = sparseArray;
                            j28 = j28;
                        }
                        sparseArray3 = sparseArray;
                        j34 = j28;
                        printWriter4.println(sb.toString());
                    } else {
                        sparseArray3 = sparseArray;
                        j34 = j28;
                    }
                    screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                    if (screenOffCpuFreqTimes2 != null) {
                        sb.setLength(0);
                        sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        while (i17 < screenOffCpuFreqTimes2.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                        }
                        printWriter4.println(sb.toString());
                    }
                    i14++;
                    sparseArray = sparseArray3;
                    j28 = j34;
                }
                sparseArray2 = sparseArray;
                j31 = j28;
                processStats = uid4.getProcessStats();
                size6 = processStats.size() - 1;
                z6 = zPrintTimer;
                while (size6 >= 0) {
                    procValueAt = processStats.valueAt(size6);
                    userTime = procValueAt.getUserTime(i10);
                    systemTime = procValueAt.getSystemTime(i10);
                    long j8117 = j30;
                    foregroundTime = procValueAt.getForegroundTime(i10);
                    starts2 = procValueAt.getStarts(i10);
                    boolean z116 = z6;
                    numCrashes = procValueAt.getNumCrashes(i10);
                    Uid uid119 = uid4;
                    numAnrs = procValueAt.getNumAnrs(i10);
                    if (i10 == 0) {
                        iCountExcessivePowers = procValueAt.countExcessivePowers();
                    } else {
                        iCountExcessivePowers = 0;
                    }
                    if (userTime != 0) {
                        proc = procValueAt;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Proc ");
                        sb.append(processStats.keyAt(size6));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("      CPU: ");
                        formatTimeMs(sb, userTime);
                        sb.append("usr + ");
                        formatTimeMs(sb, systemTime);
                        sb.append("krn ; ");
                        formatTimeMs(sb, foregroundTime);
                        sb.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 == 0) {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        } else {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        }
                        printWriter4.println(sb.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            Uid.Proc proc15 = proc;
                            excessivePower = proc15.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    printWriter4.print(" (");
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                }
                            }
                            i16++;
                            proc = proc15;
                        }
                        z6 = true;
                    } else {
                        proc = procValueAt;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Proc ");
                        sb.append(processStats.keyAt(size6));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("      CPU: ");
                        formatTimeMs(sb, userTime);
                        sb.append("usr + ");
                        formatTimeMs(sb, systemTime);
                        sb.append("krn ; ");
                        formatTimeMs(sb, foregroundTime);
                        sb.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 == 0) {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        } else {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        }
                        printWriter4.println(sb.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            Uid.Proc proc16 = proc;
                            excessivePower = proc16.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    printWriter4.print(" (");
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                }
                            }
                            i16++;
                            proc = proc16;
                        }
                        z6 = true;
                    }
                    size6--;
                    j30 = j8117;
                    uid4 = uid119;
                    i10 = i;
                }
                j32 = j30;
                packageStats = uid4.getPackageStats();
                size7 = packageStats.size() - 1;
                z7 = z6;
                while (size7 >= 0) {
                    printWriter.print(str);
                    printWriter4.print("    Apk ");
                    printWriter4.print(packageStats.keyAt(size7));
                    printWriter4.println(SettingsStringUtil.DELIMITER);
                    Uid.Pkg pkgValueAt8 = packageStats.valueAt(size7);
                    wakeupAlarmStats = pkgValueAt8.getWakeupAlarmStats();
                    size8 = wakeupAlarmStats.size() - 1;
                    z8 = false;
                    while (size8 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("      Wakeup alarm ");
                        printWriter4.print(wakeupAlarmStats.keyAt(size8));
                        printWriter4.print(": ");
                        printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                        printWriter4.println(" times");
                        size8--;
                        z8 = true;
                    }
                    serviceStats = pkgValueAt8.getServiceStats();
                    size9 = serviceStats.size() - 1;
                    while (size9 >= 0) {
                        Uid.Pkg.Serv servValueAt8 = serviceStats.valueAt(size9);
                        long j8118 = j56;
                        startTime = servValueAt8.getStartTime(j8118, i);
                        starts = servValueAt8.getStarts(i);
                        launches = servValueAt8.getLaunches(i);
                        if (startTime == 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("      Service ");
                            sb.append(serviceStats.keyAt(size9));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("        Created for: ");
                            formatTimeMs(sb, startTime / 1000);
                            sb.append("uptime\n");
                            sb.append(str);
                            sb.append("        Starts: ");
                            sb.append(starts);
                            sb.append(", launches: ");
                            sb.append(launches);
                            printWriter4.println(sb.toString());
                            z8 = true;
                        } else {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("      Service ");
                            sb.append(serviceStats.keyAt(size9));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("        Created for: ");
                            formatTimeMs(sb, startTime / 1000);
                            sb.append("uptime\n");
                            sb.append(str);
                            sb.append("        Starts: ");
                            sb.append(starts);
                            sb.append(", launches: ");
                            sb.append(launches);
                            printWriter4.println(sb.toString());
                            z8 = true;
                        }
                        size9--;
                        j56 = j8118;
                    }
                    long j8119 = j56;
                    if (!z8) {
                        printWriter.print(str);
                        printWriter4.println("      (nothing executed)");
                    }
                    size7--;
                    j56 = j8119;
                    z7 = true;
                }
                j33 = j56;
                i15 = 0;
                if (!z7) {
                    printWriter.print(str);
                    printWriter4.println("    (nothing executed)");
                }
                i46 = i11 + 1;
                i32 = i2;
                printWriter2 = printWriter4;
                i5 = i15;
                j56 = j33;
                i35 = i6;
                j3 = j11;
                j58 = j25;
                sparseArray7 = sparseArray2;
                j57 = j31;
                j2 = j32;
                batteryStats = this;
            } else {
                j5 = wifiScanTime;
            }
            j6 = wifiScanActualTime;
            j7 = wifiScanBackgroundTime;
            j7 = wifiScanBackgroundTime;
            j8 = wifiRunningTime;
            j7 = wifiScanBackgroundTime;
            sb2 = sb7;
            sb2.setLength(0);
            long j81110 = j7;
            str3 = str;
            sb2.append(str3);
            sb2.append("    Wifi Running: ");
            long j81111 = j6;
            formatTimeMs(sb2, j8 / 1000);
            sb2.append("(");
            j9 = j3;
            sb2.append(batteryStats.formatRatioLocked(j8, j9));
            sb2.append(")\n");
            sb2.append(str3);
            sb2.append("    Full Wifi Lock: ");
            formatTimeMs(sb2, fullWifiLockTime / 1000);
            sb2.append("(");
            sb2.append(batteryStats.formatRatioLocked(fullWifiLockTime, j9));
            sb2.append(")\n");
            sb2.append(str3);
            sb2.append("    Wifi Scan (blamed): ");
            formatTimeMs(sb2, j5 / 1000);
            sb2.append("(");
            sb2.append(batteryStats.formatRatioLocked(j5, j9));
            sb2.append(") ");
            sb2.append(wifiScanCount);
            sb2.append("x\n");
            sb2.append(str3);
            sb2.append("    Wifi Scan (actual): ");
            formatTimeMs(sb2, j81111 / 1000);
            sb2.append("(");
            j10 = j64;
            sb2.append(batteryStats.formatRatioLocked(j81111, batteryStats.computeBatteryRealtime(j10, 0)));
            sb2.append(") ");
            sb2.append(wifiScanCount);
            sb2.append("x\n");
            sb2.append(str3);
            sb2.append("    Background Wifi Scan: ");
            formatTimeMs(sb2, j81110 / 1000);
            sb2.append("(");
            sb2.append(batteryStats.formatRatioLocked(j81110, batteryStats.computeBatteryRealtime(j10, 0)));
            sb2.append(") ");
            sb2.append(wifiScanBackgroundCount);
            sb2.append("x");
            printWriter2.println(sb2.toString());
            if (wifiRadioApWakeupCount > 0) {
                sb2.setLength(0);
                sb2.append(str3);
                sb2.append("    WiFi AP wakeups: ");
                sb2.append(wifiRadioApWakeupCount);
                printWriter2.println(sb2.toString());
            }
            j11 = j9;
            printControllerActivityIfInteresting(printWriter, sb2, str3 + "  ", WIFI_CONTROLLER_NAME, uid5.getWifiControllerActivity(), i);
            if (networkActivityBytes11 <= 0) {
                printWriter.print(str);
                printWriter2.print("    Bluetooth network: ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                printWriter2.print(" received, ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                printWriter2.println(" sent");
            } else {
                printWriter.print(str);
                printWriter2.print("    Bluetooth network: ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes11));
                printWriter2.print(" received, ");
                printWriter2.print(batteryStats.formatBytesLocked(networkActivityBytes12));
                printWriter2.println(" sent");
            }
            bluetoothScanTimer = uid5.getBluetoothScanTimer();
            if (bluetoothScanTimer != null) {
                totalTimeLocked5 = (bluetoothScanTimer.getTotalTimeLocked(j10, i) + 500) / 1000;
                if (totalTimeLocked5 != 0) {
                    countLocked8 = bluetoothScanTimer.getCountLocked(i);
                    bluetoothScanBackgroundTimer = uid5.getBluetoothScanBackgroundTimer();
                    if (bluetoothScanBackgroundTimer != null) {
                        countLocked9 = bluetoothScanBackgroundTimer.getCountLocked(i);
                    } else {
                        countLocked9 = 0;
                    }
                    totalDurationMsLocked6 = bluetoothScanTimer.getTotalDurationMsLocked(j57);
                    if (bluetoothScanBackgroundTimer != null) {
                        totalDurationMsLocked7 = bluetoothScanBackgroundTimer.getTotalDurationMsLocked(j57);
                    } else {
                        totalDurationMsLocked7 = 0;
                    }
                    if (uid5.getBluetoothScanResultCounter() != null) {
                        countLocked10 = uid5.getBluetoothScanResultCounter().getCountLocked(i);
                    } else {
                        countLocked10 = 0;
                    }
                    if (uid5.getBluetoothScanResultBgCounter() != null) {
                        countLocked11 = uid5.getBluetoothScanResultBgCounter().getCountLocked(i);
                    } else {
                        countLocked11 = 0;
                    }
                    bluetoothUnoptimizedScanTimer = uid5.getBluetoothUnoptimizedScanTimer();
                    if (bluetoothUnoptimizedScanTimer != null) {
                        totalDurationMsLocked8 = bluetoothUnoptimizedScanTimer.getTotalDurationMsLocked(j57);
                    } else {
                        totalDurationMsLocked8 = 0;
                    }
                    if (bluetoothUnoptimizedScanTimer != null) {
                        maxDurationMsLocked = bluetoothUnoptimizedScanTimer.getMaxDurationMsLocked(j57);
                    } else {
                        maxDurationMsLocked = 0;
                    }
                    bluetoothUnoptimizedScanBackgroundTimer = uid5.getBluetoothUnoptimizedScanBackgroundTimer();
                    if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                        totalDurationMsLocked9 = bluetoothUnoptimizedScanBackgroundTimer.getTotalDurationMsLocked(j57);
                    } else {
                        totalDurationMsLocked9 = 0;
                    }
                    if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                        maxDurationMsLocked2 = bluetoothUnoptimizedScanBackgroundTimer.getMaxDurationMsLocked(j57);
                    } else {
                        maxDurationMsLocked2 = 0;
                    }
                    sb2.setLength(0);
                    if (totalDurationMsLocked6 != totalTimeLocked5) {
                        sb2.append(str3);
                        sb2.append("    Bluetooth Scan (total blamed realtime): ");
                        formatTimeMs(sb2, totalTimeLocked5);
                        sb2.append(" (");
                        sb2.append(countLocked8);
                        sb2.append(" times)");
                        if (bluetoothScanTimer.isRunningLocked()) {
                            sb2.append(" (currently running)");
                        }
                        sb2.append("\n");
                    }
                    sb2.append(str3);
                    sb2.append("    Bluetooth Scan (total actual realtime): ");
                    formatTimeMs(sb2, totalDurationMsLocked6);
                    sb2.append(" (");
                    sb2.append(countLocked8);
                    sb2.append(" times)");
                    if (bluetoothScanTimer.isRunningLocked()) {
                        sb2.append(" (currently running)");
                    }
                    sb2.append("\n");
                    if (totalDurationMsLocked7 <= 0) {
                        sb2.append(str3);
                        sb2.append("    Bluetooth Scan (background realtime): ");
                        formatTimeMs(sb2, totalDurationMsLocked7);
                        sb2.append(" (");
                        sb2.append(countLocked9);
                        sb2.append(" times)");
                        if (bluetoothScanBackgroundTimer != null) {
                            sb2.append(" (currently running in background)");
                        }
                        sb2.append("\n");
                    } else {
                        sb2.append(str3);
                        sb2.append("    Bluetooth Scan (background realtime): ");
                        formatTimeMs(sb2, totalDurationMsLocked7);
                        sb2.append(" (");
                        sb2.append(countLocked9);
                        sb2.append(" times)");
                        if (bluetoothScanBackgroundTimer != null) {
                            sb2.append(" (currently running in background)");
                        }
                        sb2.append("\n");
                    }
                    sb2.append(str3);
                    sb2.append("    Bluetooth Scan Results: ");
                    sb2.append(countLocked10);
                    sb2.append(" (");
                    sb2.append(countLocked11);
                    sb2.append(" in background)");
                    j40 = totalDurationMsLocked8;
                    if (j40 <= 0) {
                        j41 = totalDurationMsLocked9;
                        if (j41 > 0) {
                        }
                        printWriter3 = printWriter;
                        printWriter3.println(sb2.toString());
                        z3 = true;
                    } else {
                        j41 = totalDurationMsLocked9;
                    }
                    sb2.append("\n");
                    sb2.append(str3);
                    sb2.append("    Unoptimized Bluetooth Scan (realtime): ");
                    formatTimeMs(sb2, j40);
                    sb2.append(" (max ");
                    formatTimeMs(sb2, maxDurationMsLocked);
                    sb2.append(")");
                    if (bluetoothUnoptimizedScanTimer != 0) {
                        sb2.append(" (currently running unoptimized)");
                    }
                    if (bluetoothUnoptimizedScanBackgroundTimer != null) {
                        sb2.append("\n");
                        sb2.append(str3);
                        sb2.append("    Unoptimized Bluetooth Scan (background realtime): ");
                        formatTimeMs(sb2, j41);
                        sb2.append(" (max ");
                        formatTimeMs(sb2, maxDurationMsLocked2);
                        sb2.append(")");
                        if (bluetoothUnoptimizedScanBackgroundTimer.isRunningLocked()) {
                            sb2.append(" (currently running unoptimized in background)");
                        }
                    }
                    printWriter3 = printWriter;
                    printWriter3.println(sb2.toString());
                    z3 = true;
                } else {
                    j10 = j10;
                    printWriter3 = printWriter2;
                    j57 = j57;
                    z3 = false;
                }
            } else {
                j10 = j10;
                printWriter3 = printWriter2;
                j57 = j57;
                z3 = false;
            }
            if (uid5.hasUserActivity()) {
                i22 = 0;
                z11 = false;
                while (i22 < 4) {
                    Uid uid1110 = uid5;
                    userActivityCount = uid1110.getUserActivityCount(i22, i);
                    if (userActivityCount != 0) {
                        if (z11) {
                            sb2.setLength(0);
                            sb2.append("    User activity: ");
                            z11 = true;
                        } else {
                            sb2.append(", ");
                        }
                        sb2.append(userActivityCount);
                        sb2.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        sb2.append(Uid.USER_ACTIVITY_TYPES[i22]);
                    }
                    i22++;
                    uid5 = uid1110;
                }
                uid = uid5;
                i7 = i;
                if (z11) {
                    printWriter3.println(sb2.toString());
                }
            } else {
                uid = uid5;
                i7 = i;
            }
            wakelockStats = uid.getWakelockStats();
            size = wakelockStats.size() - 1;
            j12 = 0;
            j13 = 0;
            j14 = 0;
            jComputeWakeLock = 0;
            i8 = 0;
            while (size >= 0) {
                Uid.Wakelock wakelockValueAt6 = wakelockStats.valueAt(size);
                sb2.setLength(0);
                sb2.append(str3);
                sb2.append("    Wake lock ");
                sb2.append(wakelockStats.keyAt(size));
                Uid uid1111 = uid;
                long j616 = j12;
                ArrayMap<String, ? extends Uid.Wakelock> arrayMap5 = wakelockStats;
                int i413 = size;
                long j617 = j13;
                long j711113 = j10;
                long j711114 = j14;
                long j711115 = j57;
                String strPrintWakeLock9 = printWakeLock(sb2, wakelockValueAt6.getWakeTime(1), j711113, "full", i, ": ");
                wakeTime = wakelockValueAt6.getWakeTime(0);
                String strPrintWakeLock10 = printWakeLock(sb2, wakeTime, j711113, Slice.HINT_PARTIAL, i, strPrintWakeLock9);
                if (wakeTime != null) {
                    subTimer4 = wakeTime.getSubTimer();
                } else {
                    subTimer4 = null;
                }
                long j711116 = j10;
                StringBuilder sb110 = sb2;
                printWakeLock(sb110, wakelockValueAt6.getWakeTime(18), j711116, "draw", i, printWakeLock(sb110, wakelockValueAt6.getWakeTime(2), j711116, Context.WINDOW_SERVICE, i, printWakeLock(sb2, subTimer4, j711116, "background partial", i, strPrintWakeLock10)));
                sb2.append(" realtime");
                printWriter3.println(sb2.toString());
                i8++;
                long jComputeWakeLock15 = j711114 + computeWakeLock(wakelockValueAt6.getWakeTime(1), j711116, i7);
                long jComputeWakeLock16 = computeWakeLock(wakelockValueAt6.getWakeTime(0), j711116, i7) + j617;
                long jComputeWakeLock17 = j616 + computeWakeLock(wakelockValueAt6.getWakeTime(2), j711116, i7);
                jComputeWakeLock += computeWakeLock(wakelockValueAt6.getWakeTime(18), j711116, i7);
                uid = uid1111;
                j57 = j711115;
                j14 = jComputeWakeLock15;
                str3 = str;
                j13 = jComputeWakeLock16;
                j12 = jComputeWakeLock17;
                wakelockStats = arrayMap5;
                size = i413 - 1;
                z3 = true;
            }
            j15 = j12;
            j16 = j13;
            z4 = z3;
            j17 = j14;
            uid2 = uid;
            j18 = j10;
            j19 = j57;
            j20 = jComputeWakeLock;
            if (i8 > 1) {
                if (uid2.getAggregatedPartialWakelockTimer() != null) {
                    Timer aggregatedPartialWakelockTimer5 = uid2.getAggregatedPartialWakelockTimer();
                    long totalDurationMsLocked14 = aggregatedPartialWakelockTimer5.getTotalDurationMsLocked(j19);
                    subTimer3 = aggregatedPartialWakelockTimer5.getSubTimer();
                    if (subTimer3 != null) {
                        totalDurationMsLocked5 = subTimer3.getTotalDurationMsLocked(j19);
                    } else {
                        totalDurationMsLocked5 = 0;
                    }
                    j39 = j19;
                    j37 = totalDurationMsLocked14;
                    j38 = totalDurationMsLocked5;
                } else {
                    j37 = 0;
                    j38 = 0;
                    j39 = j19;
                }
                if (j37 != 0) {
                    sb2.setLength(0);
                    sb2.append(str);
                    sb2.append("    TOTAL wake: ");
                    if (j17 != 0) {
                        formatTimeMs(sb2, j17);
                        sb2.append("full");
                        z10 = true;
                    } else {
                        z10 = false;
                    }
                    if (j16 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j16);
                        sb2.append("blamed partial");
                        z10 = true;
                    }
                    if (j37 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j37);
                        sb2.append("actual partial");
                        z10 = true;
                    }
                    if (j38 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j38);
                        sb2.append("actual background partial");
                        z10 = true;
                    }
                    if (j15 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j15);
                        sb2.append(Context.WINDOW_SERVICE);
                        z10 = true;
                    }
                    if (j20 != 0) {
                        if (z10) {
                            sb2.append(",");
                        }
                        formatTimeMs(sb2, j20);
                        sb2.append("draw");
                    }
                    sb2.append(" realtime");
                    j21 = j39;
                    printWriter4 = printWriter;
                    printWriter4.println(sb2.toString());
                } else {
                    sb2.setLength(0);
                    sb2.append(str);
                    sb2.append("    TOTAL wake: ");
                    if (j17 != 0) {
                        formatTimeMs(sb2, j17);
                        sb2.append("full");
                        z10 = true;
                    } else {
                        z10 = false;
                    }
                    if (j16 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j16);
                        sb2.append("blamed partial");
                        z10 = true;
                    }
                    if (j37 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j37);
                        sb2.append("actual partial");
                        z10 = true;
                    }
                    if (j38 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j38);
                        sb2.append("actual background partial");
                        z10 = true;
                    }
                    if (j15 != 0) {
                        if (z10) {
                            sb2.append(", ");
                        }
                        formatTimeMs(sb2, j15);
                        sb2.append(Context.WINDOW_SERVICE);
                        z10 = true;
                    }
                    if (j20 != 0) {
                        if (z10) {
                            sb2.append(",");
                        }
                        formatTimeMs(sb2, j20);
                        sb2.append("draw");
                    }
                    sb2.append(" realtime");
                    j21 = j39;
                    printWriter4 = printWriter;
                    printWriter4.println(sb2.toString());
                }
            } else {
                z4 = z4;
                printWriter4 = printWriter3;
                j21 = j19;
            }
            multicastWakelockStats = uid2.getMulticastWakelockStats();
            if (multicastWakelockStats != null) {
                j22 = j18;
                totalTimeLocked4 = multicastWakelockStats.getTotalTimeLocked(j22, i7);
                countLocked7 = multicastWakelockStats.getCountLocked(i7);
                if (totalTimeLocked4 > 0) {
                    sb2.setLength(0);
                    str4 = str;
                    sb2.append(str4);
                    sb2.append("    WiFi Multicast Wakelock");
                    sb2.append(" count = ");
                    sb2.append(countLocked7);
                    sb2.append(" time = ");
                    formatTimeMsNoSpace(sb2, (totalTimeLocked4 + 500) / 1000);
                    printWriter4.println(sb2.toString());
                }
                syncStats = uid2.getSyncStats();
                size2 = syncStats.size() - 1;
                while (size2 >= 0) {
                    Timer timerValueAt17 = syncStats.valueAt(size2);
                    totalTimeLocked3 = (timerValueAt17.getTotalTimeLocked(j22, i7) + 500) / 1000;
                    countLocked5 = timerValueAt17.getCountLocked(i7);
                    subTimer2 = timerValueAt17.getSubTimer();
                    if (subTimer2 != null) {
                        totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                    } else {
                        totalDurationMsLocked4 = -1;
                    }
                    if (subTimer2 != null) {
                        countLocked6 = subTimer2.getCountLocked(i7);
                    } else {
                        countLocked6 = i3;
                    }
                    sb2.setLength(0);
                    sb2.append(str4);
                    sb2.append("    Sync ");
                    sb2.append(syncStats.keyAt(size2));
                    sb2.append(": ");
                    if (totalTimeLocked3 != 0) {
                        formatTimeMs(sb2, totalTimeLocked3);
                        sb2.append("realtime (");
                        sb2.append(countLocked5);
                        sb2.append(" times)");
                        if (totalDurationMsLocked4 > 0) {
                            sb2.append(", ");
                            formatTimeMs(sb2, totalDurationMsLocked4);
                            sb2.append("background (");
                            sb2.append(countLocked6);
                            sb2.append(" times)");
                        }
                    } else {
                        sb2.append("(not used)");
                    }
                    printWriter4.println(sb2.toString());
                    size2--;
                    j21 = j21;
                    z4 = true;
                }
                j23 = j21;
                jobStats = uid2.getJobStats();
                size3 = jobStats.size() - 1;
                while (size3 >= 0) {
                    Timer timerValueAt18 = jobStats.valueAt(size3);
                    totalTimeLocked2 = (timerValueAt18.getTotalTimeLocked(j22, i7) + 500) / 1000;
                    countLocked3 = timerValueAt18.getCountLocked(i7);
                    subTimer = timerValueAt18.getSubTimer();
                    if (subTimer != null) {
                        j36 = j23;
                        totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                    } else {
                        j36 = j23;
                        totalDurationMsLocked3 = -1;
                    }
                    if (subTimer != null) {
                        countLocked4 = subTimer.getCountLocked(i7);
                    } else {
                        countLocked4 = i3;
                    }
                    sb2.setLength(0);
                    sb2.append(str4);
                    sb2.append("    Job ");
                    sb2.append(jobStats.keyAt(size3));
                    sb2.append(": ");
                    if (totalTimeLocked2 != 0) {
                        formatTimeMs(sb2, totalTimeLocked2);
                        sb2.append("realtime (");
                        sb2.append(countLocked3);
                        sb2.append(" times)");
                        if (totalDurationMsLocked3 > 0) {
                            sb2.append(", ");
                            formatTimeMs(sb2, totalDurationMsLocked3);
                            sb2.append("background (");
                            sb2.append(countLocked4);
                            sb2.append(" times)");
                        }
                    } else {
                        sb2.append("(not used)");
                    }
                    printWriter4.println(sb2.toString());
                    size3--;
                    j23 = j36;
                    j22 = j22;
                    z4 = true;
                }
                long j711117 = j22;
                long j711118 = j23;
                i9 = 0;
                jobCompletionStats = uid2.getJobCompletionStats();
                while (size4 >= 0) {
                    sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                    if (sparseIntArrayValueAt != null) {
                        printWriter.print(str);
                        printWriter4.print("    Job Completions ");
                        printWriter4.print(jobCompletionStats.keyAt(size4));
                        printWriter4.print(SettingsStringUtil.DELIMITER);
                        while (i21 < sparseIntArrayValueAt.size()) {
                            printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                            printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                            printWriter4.print("(");
                            printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                            printWriter4.print("x)");
                        }
                        printWriter.println();
                    }
                }
                uid2.getDeferredJobsLineLocked(sb2, i7);
                if (sb2.length() > 0) {
                    printWriter4.print("    Jobs deferred on launch ");
                    printWriter4.println(sb2.toString());
                }
                j24 = j711117;
                uid3 = uid2;
                sb3 = sb2;
                i10 = i7;
                j25 = j67;
                sparseArray = sparseArray8;
                j26 = j711118;
                i11 = i48;
                boolean zPrintTimer10 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
                sensorStats = uid3.getSensorStats();
                size5 = sensorStats.size();
                z5 = zPrintTimer10;
                i12 = 0;
                while (i12 < size5) {
                    sensorValueAt = sensorStats.valueAt(i12);
                    sensorStats.keyAt(i12);
                    sb4 = sb3;
                    sb4.setLength(i9);
                    sb4.append(str);
                    sb4.append("    Sensor ");
                    handle = sensorValueAt.getHandle();
                    if (handle == -10000) {
                        sb4.append("GPS");
                    } else {
                        sb4.append(handle);
                    }
                    sb4.append(": ");
                    sensorTime = sensorValueAt.getSensorTime();
                    if (sensorTime != null) {
                        totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                        countLocked = sensorTime.getCountLocked(i10);
                        sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                        if (sensorBackgroundTime != null) {
                            countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                        } else {
                            countLocked2 = i9;
                        }
                        j35 = j26;
                        totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                        if (sensorBackgroundTime != null) {
                            totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                        } else {
                            totalDurationMsLocked2 = 0;
                        }
                        if (totalTimeLocked != 0) {
                            if (totalDurationMsLocked != totalTimeLocked) {
                                formatTimeMs(sb4, totalTimeLocked);
                                sb4.append("blamed realtime, ");
                            }
                            formatTimeMs(sb4, totalDurationMsLocked);
                            sb4.append("realtime (");
                            sb4.append(countLocked);
                            sb4.append(" times)");
                            if (totalDurationMsLocked2 == 0) {
                                sb4.append(", ");
                                formatTimeMs(sb4, totalDurationMsLocked2);
                                sb4.append("background (");
                                sb4.append(countLocked2);
                                sb4.append(" times)");
                            } else {
                                sb4.append(", ");
                                formatTimeMs(sb4, totalDurationMsLocked2);
                                sb4.append("background (");
                                sb4.append(countLocked2);
                                sb4.append(" times)");
                            }
                        } else {
                            sb4.append("(not used)");
                        }
                    } else {
                        j24 = j24;
                        sensorStats = sensorStats;
                        size5 = size5;
                        j35 = j26;
                        sb4.append("(not used)");
                    }
                    printWriter4.println(sb4.toString());
                    i12++;
                    j26 = j35;
                    sb3 = sb4;
                    z5 = true;
                    j24 = j24;
                    sensorStats = sensorStats;
                    size5 = size5;
                    i9 = 0;
                }
                j27 = j24;
                StringBuilder sb111 = sb3;
                j28 = j26;
                sb = sb111;
                zPrintTimer = z5 | printTimer(printWriter, sb111, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
                j29 = 0;
                i13 = 0;
                while (i13 < 7) {
                    Uid uid1112 = uid3;
                    long j711119 = j27;
                    processStateTime = uid1112.getProcessStateTime(i13, j711119, i10);
                    if (processStateTime > 0) {
                        j29 += processStateTime;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    ");
                        sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                        sb.append(" for: ");
                        formatTimeMs(sb, (processStateTime + 500) / 1000);
                        printWriter4.println(sb.toString());
                        zPrintTimer = true;
                    }
                    i13++;
                    j27 = j711119;
                    uid3 = uid1112;
                }
                uid4 = uid3;
                j30 = j27;
                if (j29 > 0) {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total running: ");
                    formatTimeMs(sb, (j29 + 500) / 1000);
                    printWriter4.println(sb.toString());
                }
                userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
                systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
                if (userCpuTimeUs <= 0) {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total cpu time: u=");
                    formatTimeMs(sb, userCpuTimeUs / 1000);
                    sb.append("s=");
                    formatTimeMs(sb, systemCpuTimeUs / 1000);
                    printWriter4.println(sb.toString());
                } else {
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Total cpu time: u=");
                    formatTimeMs(sb, userCpuTimeUs / 1000);
                    sb.append("s=");
                    formatTimeMs(sb, systemCpuTimeUs / 1000);
                    printWriter4.println(sb.toString());
                }
                cpuFreqTimes = uid4.getCpuFreqTimes(i10);
                if (cpuFreqTimes != null) {
                    sb.setLength(0);
                    sb.append("    Total cpu time per freq:");
                    while (i20 < cpuFreqTimes.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                    }
                    printWriter4.println(sb.toString());
                }
                screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
                if (screenOffCpuFreqTimes != null) {
                    sb.setLength(0);
                    sb.append("    Total screen-off cpu time per freq:");
                    while (i19 < screenOffCpuFreqTimes.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                    }
                    printWriter4.println(sb.toString());
                }
                i14 = 0;
                while (i14 < 7) {
                    cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                    if (cpuFreqTimes2 != null) {
                        sb.setLength(0);
                        sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        i18 = 0;
                        while (i18 < cpuFreqTimes2.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                            i18++;
                            sparseArray = sparseArray;
                            j28 = j28;
                        }
                        sparseArray3 = sparseArray;
                        j34 = j28;
                        printWriter4.println(sb.toString());
                    } else {
                        sparseArray3 = sparseArray;
                        j34 = j28;
                    }
                    screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                    if (screenOffCpuFreqTimes2 != null) {
                        sb.setLength(0);
                        sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                        while (i17 < screenOffCpuFreqTimes2.length) {
                            sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                        }
                        printWriter4.println(sb.toString());
                    }
                    i14++;
                    sparseArray = sparseArray3;
                    j28 = j34;
                }
                sparseArray2 = sparseArray;
                j31 = j28;
                processStats = uid4.getProcessStats();
                size6 = processStats.size() - 1;
                z6 = zPrintTimer;
                while (size6 >= 0) {
                    procValueAt = processStats.valueAt(size6);
                    userTime = procValueAt.getUserTime(i10);
                    systemTime = procValueAt.getSystemTime(i10);
                    long j81112 = j30;
                    foregroundTime = procValueAt.getForegroundTime(i10);
                    starts2 = procValueAt.getStarts(i10);
                    boolean z117 = z6;
                    numCrashes = procValueAt.getNumCrashes(i10);
                    Uid uid1113 = uid4;
                    numAnrs = procValueAt.getNumAnrs(i10);
                    if (i10 == 0) {
                        iCountExcessivePowers = procValueAt.countExcessivePowers();
                    } else {
                        iCountExcessivePowers = 0;
                    }
                    if (userTime != 0) {
                        proc = procValueAt;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Proc ");
                        sb.append(processStats.keyAt(size6));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("      CPU: ");
                        formatTimeMs(sb, userTime);
                        sb.append("usr + ");
                        formatTimeMs(sb, systemTime);
                        sb.append("krn ; ");
                        formatTimeMs(sb, foregroundTime);
                        sb.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 == 0) {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        } else {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        }
                        printWriter4.println(sb.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            Uid.Proc proc17 = proc;
                            excessivePower = proc17.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    printWriter4.print(" (");
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                }
                            }
                            i16++;
                            proc = proc17;
                        }
                        z6 = true;
                    } else {
                        proc = procValueAt;
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("    Proc ");
                        sb.append(processStats.keyAt(size6));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("      CPU: ");
                        formatTimeMs(sb, userTime);
                        sb.append("usr + ");
                        formatTimeMs(sb, systemTime);
                        sb.append("krn ; ");
                        formatTimeMs(sb, foregroundTime);
                        sb.append(FOREGROUND_ACTIVITY_DATA);
                        if (starts2 == 0) {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        } else {
                            sb.append("\n");
                            sb.append(str);
                            sb.append("      ");
                            if (starts2 != 0) {
                                sb.append(starts2);
                                sb.append(" starts");
                                z9 = true;
                            } else {
                                z9 = false;
                            }
                            if (numCrashes != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numCrashes);
                                sb.append(" crashes");
                                z9 = true;
                            }
                            if (numAnrs != 0) {
                                if (z9) {
                                    sb.append(", ");
                                }
                                sb.append(numAnrs);
                                sb.append(" anrs");
                            }
                        }
                        printWriter4.println(sb.toString());
                        i16 = 0;
                        while (i16 < iCountExcessivePowers) {
                            Uid.Proc proc18 = proc;
                            excessivePower = proc18.getExcessivePower(i16);
                            if (excessivePower != null) {
                                printWriter.print(str);
                                printWriter4.print("      * Killed for ");
                                if (excessivePower.type == 2) {
                                    printWriter4.print(CPU_DATA);
                                } else {
                                    printWriter4.print("unknown");
                                }
                                printWriter4.print(" use: ");
                                TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                                printWriter4.print(" over ");
                                TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                                if (excessivePower.overTime != 0) {
                                    printWriter4.print(" (");
                                    printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                    printWriter4.println("%)");
                                }
                            }
                            i16++;
                            proc = proc18;
                        }
                        z6 = true;
                    }
                    size6--;
                    j30 = j81112;
                    uid4 = uid1113;
                    i10 = i;
                }
                j32 = j30;
                packageStats = uid4.getPackageStats();
                size7 = packageStats.size() - 1;
                z7 = z6;
                while (size7 >= 0) {
                    printWriter.print(str);
                    printWriter4.print("    Apk ");
                    printWriter4.print(packageStats.keyAt(size7));
                    printWriter4.println(SettingsStringUtil.DELIMITER);
                    Uid.Pkg pkgValueAt9 = packageStats.valueAt(size7);
                    wakeupAlarmStats = pkgValueAt9.getWakeupAlarmStats();
                    size8 = wakeupAlarmStats.size() - 1;
                    z8 = false;
                    while (size8 >= 0) {
                        printWriter.print(str);
                        printWriter4.print("      Wakeup alarm ");
                        printWriter4.print(wakeupAlarmStats.keyAt(size8));
                        printWriter4.print(": ");
                        printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                        printWriter4.println(" times");
                        size8--;
                        z8 = true;
                    }
                    serviceStats = pkgValueAt9.getServiceStats();
                    size9 = serviceStats.size() - 1;
                    while (size9 >= 0) {
                        Uid.Pkg.Serv servValueAt9 = serviceStats.valueAt(size9);
                        long j81113 = j56;
                        startTime = servValueAt9.getStartTime(j81113, i);
                        starts = servValueAt9.getStarts(i);
                        launches = servValueAt9.getLaunches(i);
                        if (startTime == 0) {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("      Service ");
                            sb.append(serviceStats.keyAt(size9));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("        Created for: ");
                            formatTimeMs(sb, startTime / 1000);
                            sb.append("uptime\n");
                            sb.append(str);
                            sb.append("        Starts: ");
                            sb.append(starts);
                            sb.append(", launches: ");
                            sb.append(launches);
                            printWriter4.println(sb.toString());
                            z8 = true;
                        } else {
                            sb.setLength(0);
                            sb.append(str);
                            sb.append("      Service ");
                            sb.append(serviceStats.keyAt(size9));
                            sb.append(":\n");
                            sb.append(str);
                            sb.append("        Created for: ");
                            formatTimeMs(sb, startTime / 1000);
                            sb.append("uptime\n");
                            sb.append(str);
                            sb.append("        Starts: ");
                            sb.append(starts);
                            sb.append(", launches: ");
                            sb.append(launches);
                            printWriter4.println(sb.toString());
                            z8 = true;
                        }
                        size9--;
                        j56 = j81113;
                    }
                    long j81114 = j56;
                    if (!z8) {
                        printWriter.print(str);
                        printWriter4.println("      (nothing executed)");
                    }
                    size7--;
                    j56 = j81114;
                    z7 = true;
                }
                j33 = j56;
                i15 = 0;
                if (!z7) {
                    printWriter.print(str);
                    printWriter4.println("    (nothing executed)");
                }
                i46 = i11 + 1;
                i32 = i2;
                printWriter2 = printWriter4;
                i5 = i15;
                j56 = j33;
                i35 = i6;
                j3 = j11;
                j58 = j25;
                sparseArray7 = sparseArray2;
                j57 = j31;
                j2 = j32;
                batteryStats = this;
            } else {
                j22 = j18;
            }
            str4 = str;
            syncStats = uid2.getSyncStats();
            size2 = syncStats.size() - 1;
            while (size2 >= 0) {
                Timer timerValueAt19 = syncStats.valueAt(size2);
                totalTimeLocked3 = (timerValueAt19.getTotalTimeLocked(j22, i7) + 500) / 1000;
                countLocked5 = timerValueAt19.getCountLocked(i7);
                subTimer2 = timerValueAt19.getSubTimer();
                if (subTimer2 != null) {
                    totalDurationMsLocked4 = subTimer2.getTotalDurationMsLocked(j21);
                } else {
                    totalDurationMsLocked4 = -1;
                }
                if (subTimer2 != null) {
                    countLocked6 = subTimer2.getCountLocked(i7);
                } else {
                    countLocked6 = i3;
                }
                sb2.setLength(0);
                sb2.append(str4);
                sb2.append("    Sync ");
                sb2.append(syncStats.keyAt(size2));
                sb2.append(": ");
                if (totalTimeLocked3 != 0) {
                    formatTimeMs(sb2, totalTimeLocked3);
                    sb2.append("realtime (");
                    sb2.append(countLocked5);
                    sb2.append(" times)");
                    if (totalDurationMsLocked4 > 0) {
                        sb2.append(", ");
                        formatTimeMs(sb2, totalDurationMsLocked4);
                        sb2.append("background (");
                        sb2.append(countLocked6);
                        sb2.append(" times)");
                    }
                } else {
                    sb2.append("(not used)");
                }
                printWriter4.println(sb2.toString());
                size2--;
                j21 = j21;
                z4 = true;
            }
            j23 = j21;
            jobStats = uid2.getJobStats();
            size3 = jobStats.size() - 1;
            while (size3 >= 0) {
                Timer timerValueAt110 = jobStats.valueAt(size3);
                totalTimeLocked2 = (timerValueAt110.getTotalTimeLocked(j22, i7) + 500) / 1000;
                countLocked3 = timerValueAt110.getCountLocked(i7);
                subTimer = timerValueAt110.getSubTimer();
                if (subTimer != null) {
                    j36 = j23;
                    totalDurationMsLocked3 = subTimer.getTotalDurationMsLocked(j36);
                } else {
                    j36 = j23;
                    totalDurationMsLocked3 = -1;
                }
                if (subTimer != null) {
                    countLocked4 = subTimer.getCountLocked(i7);
                } else {
                    countLocked4 = i3;
                }
                sb2.setLength(0);
                sb2.append(str4);
                sb2.append("    Job ");
                sb2.append(jobStats.keyAt(size3));
                sb2.append(": ");
                if (totalTimeLocked2 != 0) {
                    formatTimeMs(sb2, totalTimeLocked2);
                    sb2.append("realtime (");
                    sb2.append(countLocked3);
                    sb2.append(" times)");
                    if (totalDurationMsLocked3 > 0) {
                        sb2.append(", ");
                        formatTimeMs(sb2, totalDurationMsLocked3);
                        sb2.append("background (");
                        sb2.append(countLocked4);
                        sb2.append(" times)");
                    }
                } else {
                    sb2.append("(not used)");
                }
                printWriter4.println(sb2.toString());
                size3--;
                j23 = j36;
                j22 = j22;
                z4 = true;
            }
            long j7111110 = j22;
            long j7111111 = j23;
            i9 = 0;
            jobCompletionStats = uid2.getJobCompletionStats();
            while (size4 >= 0) {
                sparseIntArrayValueAt = jobCompletionStats.valueAt(size4);
                if (sparseIntArrayValueAt != null) {
                    printWriter.print(str);
                    printWriter4.print("    Job Completions ");
                    printWriter4.print(jobCompletionStats.keyAt(size4));
                    printWriter4.print(SettingsStringUtil.DELIMITER);
                    while (i21 < sparseIntArrayValueAt.size()) {
                        printWriter4.print(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER);
                        printWriter4.print(JobParameters.getReasonName(sparseIntArrayValueAt.keyAt(i21)));
                        printWriter4.print("(");
                        printWriter4.print(sparseIntArrayValueAt.valueAt(i21));
                        printWriter4.print("x)");
                    }
                    printWriter.println();
                }
            }
            uid2.getDeferredJobsLineLocked(sb2, i7);
            if (sb2.length() > 0) {
                printWriter4.print("    Jobs deferred on launch ");
                printWriter4.println(sb2.toString());
            }
            j24 = j7111110;
            uid3 = uid2;
            sb3 = sb2;
            i10 = i7;
            j25 = j67;
            sparseArray = sparseArray8;
            j26 = j7111111;
            i11 = i48;
            boolean zPrintTimer11 = z4 | printTimer(printWriter, sb2, uid2.getFlashlightTurnedOnTimer(), j24, i, str, "Flashlight") | printTimer(printWriter, sb3, uid3.getCameraTurnedOnTimer(), j24, i, str, "Camera") | printTimer(printWriter, sb3, uid3.getVideoTurnedOnTimer(), j24, i, str, "Video") | printTimer(printWriter, sb3, uid3.getAudioTurnedOnTimer(), j24, i, str, "Audio");
            sensorStats = uid3.getSensorStats();
            size5 = sensorStats.size();
            z5 = zPrintTimer11;
            i12 = 0;
            while (i12 < size5) {
                sensorValueAt = sensorStats.valueAt(i12);
                sensorStats.keyAt(i12);
                sb4 = sb3;
                sb4.setLength(i9);
                sb4.append(str);
                sb4.append("    Sensor ");
                handle = sensorValueAt.getHandle();
                if (handle == -10000) {
                    sb4.append("GPS");
                } else {
                    sb4.append(handle);
                }
                sb4.append(": ");
                sensorTime = sensorValueAt.getSensorTime();
                if (sensorTime != null) {
                    totalTimeLocked = (sensorTime.getTotalTimeLocked(j24, i10) + 500) / 1000;
                    countLocked = sensorTime.getCountLocked(i10);
                    sensorBackgroundTime = sensorValueAt.getSensorBackgroundTime();
                    if (sensorBackgroundTime != null) {
                        countLocked2 = sensorBackgroundTime.getCountLocked(i10);
                    } else {
                        countLocked2 = i9;
                    }
                    j35 = j26;
                    totalDurationMsLocked = sensorTime.getTotalDurationMsLocked(j35);
                    if (sensorBackgroundTime != null) {
                        totalDurationMsLocked2 = sensorBackgroundTime.getTotalDurationMsLocked(j35);
                    } else {
                        totalDurationMsLocked2 = 0;
                    }
                    if (totalTimeLocked != 0) {
                        if (totalDurationMsLocked != totalTimeLocked) {
                            formatTimeMs(sb4, totalTimeLocked);
                            sb4.append("blamed realtime, ");
                        }
                        formatTimeMs(sb4, totalDurationMsLocked);
                        sb4.append("realtime (");
                        sb4.append(countLocked);
                        sb4.append(" times)");
                        if (totalDurationMsLocked2 == 0) {
                            sb4.append(", ");
                            formatTimeMs(sb4, totalDurationMsLocked2);
                            sb4.append("background (");
                            sb4.append(countLocked2);
                            sb4.append(" times)");
                        } else {
                            sb4.append(", ");
                            formatTimeMs(sb4, totalDurationMsLocked2);
                            sb4.append("background (");
                            sb4.append(countLocked2);
                            sb4.append(" times)");
                        }
                    } else {
                        sb4.append("(not used)");
                    }
                } else {
                    j24 = j24;
                    sensorStats = sensorStats;
                    size5 = size5;
                    j35 = j26;
                    sb4.append("(not used)");
                }
                printWriter4.println(sb4.toString());
                i12++;
                j26 = j35;
                sb3 = sb4;
                z5 = true;
                j24 = j24;
                sensorStats = sensorStats;
                size5 = size5;
                i9 = 0;
            }
            j27 = j24;
            StringBuilder sb112 = sb3;
            j28 = j26;
            sb = sb112;
            zPrintTimer = z5 | printTimer(printWriter, sb112, uid3.getVibratorOnTimer(), j27, i, str, "Vibrator") | printTimer(printWriter, sb, uid3.getForegroundActivityTimer(), j27, i, str, "Foreground activities") | printTimer(printWriter, sb, uid3.getForegroundServiceTimer(), j27, i, str, "Foreground services");
            j29 = 0;
            i13 = 0;
            while (i13 < 7) {
                Uid uid1114 = uid3;
                long j7111112 = j27;
                processStateTime = uid1114.getProcessStateTime(i13, j7111112, i10);
                if (processStateTime > 0) {
                    j29 += processStateTime;
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    ");
                    sb.append(Uid.PROCESS_STATE_NAMES[i13]);
                    sb.append(" for: ");
                    formatTimeMs(sb, (processStateTime + 500) / 1000);
                    printWriter4.println(sb.toString());
                    zPrintTimer = true;
                }
                i13++;
                j27 = j7111112;
                uid3 = uid1114;
            }
            uid4 = uid3;
            j30 = j27;
            if (j29 > 0) {
                sb.setLength(0);
                sb.append(str);
                sb.append("    Total running: ");
                formatTimeMs(sb, (j29 + 500) / 1000);
                printWriter4.println(sb.toString());
            }
            userCpuTimeUs = uid4.getUserCpuTimeUs(i10);
            systemCpuTimeUs = uid4.getSystemCpuTimeUs(i10);
            if (userCpuTimeUs <= 0) {
                sb.setLength(0);
                sb.append(str);
                sb.append("    Total cpu time: u=");
                formatTimeMs(sb, userCpuTimeUs / 1000);
                sb.append("s=");
                formatTimeMs(sb, systemCpuTimeUs / 1000);
                printWriter4.println(sb.toString());
            } else {
                sb.setLength(0);
                sb.append(str);
                sb.append("    Total cpu time: u=");
                formatTimeMs(sb, userCpuTimeUs / 1000);
                sb.append("s=");
                formatTimeMs(sb, systemCpuTimeUs / 1000);
                printWriter4.println(sb.toString());
            }
            cpuFreqTimes = uid4.getCpuFreqTimes(i10);
            if (cpuFreqTimes != null) {
                sb.setLength(0);
                sb.append("    Total cpu time per freq:");
                while (i20 < cpuFreqTimes.length) {
                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j77);
                }
                printWriter4.println(sb.toString());
            }
            screenOffCpuFreqTimes = uid4.getScreenOffCpuFreqTimes(i10);
            if (screenOffCpuFreqTimes != null) {
                sb.setLength(0);
                sb.append("    Total screen-off cpu time per freq:");
                while (i19 < screenOffCpuFreqTimes.length) {
                    sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j78);
                }
                printWriter4.println(sb.toString());
            }
            i14 = 0;
            while (i14 < 7) {
                cpuFreqTimes2 = uid4.getCpuFreqTimes(i10, i14);
                if (cpuFreqTimes2 != null) {
                    sb.setLength(0);
                    sb.append("    Cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                    i18 = 0;
                    while (i18 < cpuFreqTimes2.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + cpuFreqTimes2[i18]);
                        i18++;
                        sparseArray = sparseArray;
                        j28 = j28;
                    }
                    sparseArray3 = sparseArray;
                    j34 = j28;
                    printWriter4.println(sb.toString());
                } else {
                    sparseArray3 = sparseArray;
                    j34 = j28;
                }
                screenOffCpuFreqTimes2 = uid4.getScreenOffCpuFreqTimes(i10, i14);
                if (screenOffCpuFreqTimes2 != null) {
                    sb.setLength(0);
                    sb.append("   Screen-off cpu times per freq at state " + Uid.PROCESS_STATE_NAMES[i14] + SettingsStringUtil.DELIMITER);
                    while (i17 < screenOffCpuFreqTimes2.length) {
                        sb.append(WifiEnterpriseConfig.CA_CERT_ALIAS_DELIMITER + j79);
                    }
                    printWriter4.println(sb.toString());
                }
                i14++;
                sparseArray = sparseArray3;
                j28 = j34;
            }
            sparseArray2 = sparseArray;
            j31 = j28;
            processStats = uid4.getProcessStats();
            size6 = processStats.size() - 1;
            z6 = zPrintTimer;
            while (size6 >= 0) {
                procValueAt = processStats.valueAt(size6);
                userTime = procValueAt.getUserTime(i10);
                systemTime = procValueAt.getSystemTime(i10);
                long j81115 = j30;
                foregroundTime = procValueAt.getForegroundTime(i10);
                starts2 = procValueAt.getStarts(i10);
                boolean z118 = z6;
                numCrashes = procValueAt.getNumCrashes(i10);
                Uid uid1115 = uid4;
                numAnrs = procValueAt.getNumAnrs(i10);
                if (i10 == 0) {
                    iCountExcessivePowers = procValueAt.countExcessivePowers();
                } else {
                    iCountExcessivePowers = 0;
                }
                if (userTime != 0) {
                    proc = procValueAt;
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Proc ");
                    sb.append(processStats.keyAt(size6));
                    sb.append(":\n");
                    sb.append(str);
                    sb.append("      CPU: ");
                    formatTimeMs(sb, userTime);
                    sb.append("usr + ");
                    formatTimeMs(sb, systemTime);
                    sb.append("krn ; ");
                    formatTimeMs(sb, foregroundTime);
                    sb.append(FOREGROUND_ACTIVITY_DATA);
                    if (starts2 == 0) {
                        sb.append("\n");
                        sb.append(str);
                        sb.append("      ");
                        if (starts2 != 0) {
                            sb.append(starts2);
                            sb.append(" starts");
                            z9 = true;
                        } else {
                            z9 = false;
                        }
                        if (numCrashes != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numCrashes);
                            sb.append(" crashes");
                            z9 = true;
                        }
                        if (numAnrs != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numAnrs);
                            sb.append(" anrs");
                        }
                    } else {
                        sb.append("\n");
                        sb.append(str);
                        sb.append("      ");
                        if (starts2 != 0) {
                            sb.append(starts2);
                            sb.append(" starts");
                            z9 = true;
                        } else {
                            z9 = false;
                        }
                        if (numCrashes != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numCrashes);
                            sb.append(" crashes");
                            z9 = true;
                        }
                        if (numAnrs != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numAnrs);
                            sb.append(" anrs");
                        }
                    }
                    printWriter4.println(sb.toString());
                    i16 = 0;
                    while (i16 < iCountExcessivePowers) {
                        Uid.Proc proc19 = proc;
                        excessivePower = proc19.getExcessivePower(i16);
                        if (excessivePower != null) {
                            printWriter.print(str);
                            printWriter4.print("      * Killed for ");
                            if (excessivePower.type == 2) {
                                printWriter4.print(CPU_DATA);
                            } else {
                                printWriter4.print("unknown");
                            }
                            printWriter4.print(" use: ");
                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                            printWriter4.print(" over ");
                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                            if (excessivePower.overTime != 0) {
                                printWriter4.print(" (");
                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                printWriter4.println("%)");
                            }
                        }
                        i16++;
                        proc = proc19;
                    }
                    z6 = true;
                } else {
                    proc = procValueAt;
                    sb.setLength(0);
                    sb.append(str);
                    sb.append("    Proc ");
                    sb.append(processStats.keyAt(size6));
                    sb.append(":\n");
                    sb.append(str);
                    sb.append("      CPU: ");
                    formatTimeMs(sb, userTime);
                    sb.append("usr + ");
                    formatTimeMs(sb, systemTime);
                    sb.append("krn ; ");
                    formatTimeMs(sb, foregroundTime);
                    sb.append(FOREGROUND_ACTIVITY_DATA);
                    if (starts2 == 0) {
                        sb.append("\n");
                        sb.append(str);
                        sb.append("      ");
                        if (starts2 != 0) {
                            sb.append(starts2);
                            sb.append(" starts");
                            z9 = true;
                        } else {
                            z9 = false;
                        }
                        if (numCrashes != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numCrashes);
                            sb.append(" crashes");
                            z9 = true;
                        }
                        if (numAnrs != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numAnrs);
                            sb.append(" anrs");
                        }
                    } else {
                        sb.append("\n");
                        sb.append(str);
                        sb.append("      ");
                        if (starts2 != 0) {
                            sb.append(starts2);
                            sb.append(" starts");
                            z9 = true;
                        } else {
                            z9 = false;
                        }
                        if (numCrashes != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numCrashes);
                            sb.append(" crashes");
                            z9 = true;
                        }
                        if (numAnrs != 0) {
                            if (z9) {
                                sb.append(", ");
                            }
                            sb.append(numAnrs);
                            sb.append(" anrs");
                        }
                    }
                    printWriter4.println(sb.toString());
                    i16 = 0;
                    while (i16 < iCountExcessivePowers) {
                        Uid.Proc proc110 = proc;
                        excessivePower = proc110.getExcessivePower(i16);
                        if (excessivePower != null) {
                            printWriter.print(str);
                            printWriter4.print("      * Killed for ");
                            if (excessivePower.type == 2) {
                                printWriter4.print(CPU_DATA);
                            } else {
                                printWriter4.print("unknown");
                            }
                            printWriter4.print(" use: ");
                            TimeUtils.formatDuration(excessivePower.usedTime, printWriter4);
                            printWriter4.print(" over ");
                            TimeUtils.formatDuration(excessivePower.overTime, printWriter4);
                            if (excessivePower.overTime != 0) {
                                printWriter4.print(" (");
                                printWriter4.print((excessivePower.usedTime * 100) / excessivePower.overTime);
                                printWriter4.println("%)");
                            }
                        }
                        i16++;
                        proc = proc110;
                    }
                    z6 = true;
                }
                size6--;
                j30 = j81115;
                uid4 = uid1115;
                i10 = i;
            }
            j32 = j30;
            packageStats = uid4.getPackageStats();
            size7 = packageStats.size() - 1;
            z7 = z6;
            while (size7 >= 0) {
                printWriter.print(str);
                printWriter4.print("    Apk ");
                printWriter4.print(packageStats.keyAt(size7));
                printWriter4.println(SettingsStringUtil.DELIMITER);
                Uid.Pkg pkgValueAt10 = packageStats.valueAt(size7);
                wakeupAlarmStats = pkgValueAt10.getWakeupAlarmStats();
                size8 = wakeupAlarmStats.size() - 1;
                z8 = false;
                while (size8 >= 0) {
                    printWriter.print(str);
                    printWriter4.print("      Wakeup alarm ");
                    printWriter4.print(wakeupAlarmStats.keyAt(size8));
                    printWriter4.print(": ");
                    printWriter4.print(wakeupAlarmStats.valueAt(size8).getCountLocked(i));
                    printWriter4.println(" times");
                    size8--;
                    z8 = true;
                }
                serviceStats = pkgValueAt10.getServiceStats();
                size9 = serviceStats.size() - 1;
                while (size9 >= 0) {
                    Uid.Pkg.Serv servValueAt10 = serviceStats.valueAt(size9);
                    long j81116 = j56;
                    startTime = servValueAt10.getStartTime(j81116, i);
                    starts = servValueAt10.getStarts(i);
                    launches = servValueAt10.getLaunches(i);
                    if (startTime == 0) {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("      Service ");
                        sb.append(serviceStats.keyAt(size9));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("        Created for: ");
                        formatTimeMs(sb, startTime / 1000);
                        sb.append("uptime\n");
                        sb.append(str);
                        sb.append("        Starts: ");
                        sb.append(starts);
                        sb.append(", launches: ");
                        sb.append(launches);
                        printWriter4.println(sb.toString());
                        z8 = true;
                    } else {
                        sb.setLength(0);
                        sb.append(str);
                        sb.append("      Service ");
                        sb.append(serviceStats.keyAt(size9));
                        sb.append(":\n");
                        sb.append(str);
                        sb.append("        Created for: ");
                        formatTimeMs(sb, startTime / 1000);
                        sb.append("uptime\n");
                        sb.append(str);
                        sb.append("        Starts: ");
                        sb.append(starts);
                        sb.append(", launches: ");
                        sb.append(launches);
                        printWriter4.println(sb.toString());
                        z8 = true;
                    }
                    size9--;
                    j56 = j81116;
                }
                long j81117 = j56;
                if (!z8) {
                    printWriter.print(str);
                    printWriter4.println("      (nothing executed)");
                }
                size7--;
                j56 = j81117;
                z7 = true;
            }
            j33 = j56;
            i15 = 0;
            if (!z7) {
                printWriter.print(str);
                printWriter4.println("    (nothing executed)");
            }
            i46 = i11 + 1;
            i32 = i2;
            printWriter2 = printWriter4;
            i5 = i15;
            j56 = j33;
            i35 = i6;
            j3 = j11;
            j58 = j25;
            sparseArray7 = sparseArray2;
            j57 = j31;
            j2 = j32;
            batteryStats = this;
        }
    }

    public void dumpProtoLocked(Context context, FileDescriptor fileDescriptor, List<ApplicationInfo> list, int i, long j) {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(fileDescriptor);
        prepareForDumpLocked();
        if ((i & 24) != 0) {
            dumpProtoHistoryLocked(protoOutputStream, i, j);
            protoOutputStream.flush();
            return;
        }
        long jStart = protoOutputStream.start(1146756268033L);
        protoOutputStream.write(1120986464257L, 32);
        protoOutputStream.write(1112396529666L, getParcelVersion());
        protoOutputStream.write(1138166333443L, getStartPlatformVersion());
        protoOutputStream.write(1138166333444L, getEndPlatformVersion());
        if ((i & 4) == 0) {
            BatteryStatsHelper batteryStatsHelper = new BatteryStatsHelper(context, false, (i & 64) != 0);
            batteryStatsHelper.create(this);
            batteryStatsHelper.refreshStats(0, -1);
            dumpProtoAppsLocked(protoOutputStream, batteryStatsHelper, list);
            dumpProtoSystemLocked(protoOutputStream, batteryStatsHelper);
        }
        protoOutputStream.end(jStart);
        protoOutputStream.flush();
    }

    public abstract void finishIteratingHistoryLocked();

    public abstract void finishIteratingOldHistoryLocked();

    final String formatBytesLocked(long j) {
        this.mFormatBuilder.setLength(0);
        if (j < 1024) {
            return j + "B";
        }
        if (j < 1048576) {
            this.mFormatter.format("%.2fKB", Double.valueOf(j / 1024.0d));
            return this.mFormatBuilder.toString();
        }
        if (j < 1073741824) {
            this.mFormatter.format("%.2fMB", Double.valueOf(j / 1048576.0d));
            return this.mFormatBuilder.toString();
        }
        this.mFormatter.format("%.2fGB", Double.valueOf(j / 1.073741824E9d));
        return this.mFormatBuilder.toString();
    }

    public final String formatRatioLocked(long j, long j2) {
        if (j2 == 0) {
            return "--%";
        }
        this.mFormatBuilder.setLength(0);
        this.mFormatter.format("%.1f%%", Float.valueOf((j / j2) * 100.0f));
        return this.mFormatBuilder.toString();
    }

    public abstract long getBatteryRealtime(long j);

    public abstract long getBatteryUptime(long j);

    public abstract ControllerActivityCounter getBluetoothControllerActivity();

    public abstract long getBluetoothScanTime(long j, int i);

    public abstract long getCameraOnTime(long j, int i);

    public abstract LevelStepTracker getChargeLevelStepTracker();

    public abstract long[] getCpuFreqs();

    public abstract long getCurrentDailyStartTime();

    public abstract LevelStepTracker getDailyChargeLevelStepTracker();

    public abstract LevelStepTracker getDailyDischargeLevelStepTracker();

    public abstract DailyItem getDailyItemLocked(int i);

    public abstract ArrayList<PackageChange> getDailyPackageChanges();

    public abstract int getDeviceIdleModeCount(int i, int i2);

    public abstract long getDeviceIdleModeTime(int i, long j, int i2);

    public abstract int getDeviceIdlingCount(int i, int i2);

    public abstract long getDeviceIdlingTime(int i, long j, int i2);

    public abstract int getDischargeAmount(int i);

    public abstract int getDischargeAmountScreenDoze();

    public abstract int getDischargeAmountScreenDozeSinceCharge();

    public abstract int getDischargeAmountScreenOff();

    public abstract int getDischargeAmountScreenOffSinceCharge();

    public abstract int getDischargeAmountScreenOn();

    public abstract int getDischargeAmountScreenOnSinceCharge();

    public abstract int getDischargeCurrentLevel();

    public abstract LevelStepTracker getDischargeLevelStepTracker();

    public abstract int getDischargeStartLevel();

    public abstract String getEndPlatformVersion();

    public abstract int getEstimatedBatteryCapacity();

    public abstract long getFlashlightOnCount(int i);

    public abstract long getFlashlightOnTime(long j, int i);

    public abstract long getGlobalWifiRunningTime(long j, int i);

    public abstract long getGpsBatteryDrainMaMs();

    public abstract long getGpsSignalQualityTime(int i, long j, int i2);

    public abstract int getHighDischargeAmountSinceCharge();

    public abstract long getHistoryBaseTime();

    public abstract int getHistoryStringPoolBytes();

    public abstract int getHistoryStringPoolSize();

    public abstract String getHistoryTagPoolString(int i);

    public abstract int getHistoryTagPoolUid(int i);

    public abstract int getHistoryTotalSize();

    public abstract int getHistoryUsedSize();

    public abstract long getInteractiveTime(long j, int i);

    public abstract boolean getIsOnBattery();

    public abstract LongSparseArray<? extends Timer> getKernelMemoryStats();

    public abstract Map<String, ? extends Timer> getKernelWakelockStats();

    public abstract long getLongestDeviceIdleModeTime(int i);

    public abstract int getLowDischargeAmountSinceCharge();

    public abstract int getMaxLearnedBatteryCapacity();

    public abstract int getMinLearnedBatteryCapacity();

    public abstract long getMobileRadioActiveAdjustedTime(int i);

    public abstract int getMobileRadioActiveCount(int i);

    public abstract long getMobileRadioActiveTime(long j, int i);

    public abstract int getMobileRadioActiveUnknownCount(int i);

    public abstract long getMobileRadioActiveUnknownTime(int i);

    public abstract ControllerActivityCounter getModemControllerActivity();

    public abstract long getNetworkActivityBytes(int i, int i2);

    public abstract long getNetworkActivityPackets(int i, int i2);

    public abstract boolean getNextHistoryLocked(HistoryItem historyItem);

    public abstract long getNextMaxDailyDeadline();

    public abstract long getNextMinDailyDeadline();

    public abstract boolean getNextOldHistoryLocked(HistoryItem historyItem);

    public abstract int getNumConnectivityChange(int i);

    public abstract int getParcelVersion();

    public abstract int getPhoneDataConnectionCount(int i, int i2);

    public abstract long getPhoneDataConnectionTime(int i, long j, int i2);

    public abstract Timer getPhoneDataConnectionTimer(int i);

    public abstract int getPhoneOnCount(int i);

    public abstract long getPhoneOnTime(long j, int i);

    public abstract long getPhoneSignalScanningTime(long j, int i);

    public abstract Timer getPhoneSignalScanningTimer();

    public abstract int getPhoneSignalStrengthCount(int i, int i2);

    public abstract long getPhoneSignalStrengthTime(int i, long j, int i2);

    protected abstract Timer getPhoneSignalStrengthTimer(int i);

    public abstract int getPowerSaveModeEnabledCount(int i);

    public abstract long getPowerSaveModeEnabledTime(long j, int i);

    public abstract Map<String, ? extends Timer> getRpmStats();

    public abstract long getScreenBrightnessTime(int i, long j, int i2);

    public abstract Timer getScreenBrightnessTimer(int i);

    public abstract int getScreenDozeCount(int i);

    public abstract long getScreenDozeTime(long j, int i);

    public abstract Map<String, ? extends Timer> getScreenOffRpmStats();

    public abstract int getScreenOnCount(int i);

    public abstract long getScreenOnTime(long j, int i);

    public abstract long getStartClockTime();

    public abstract int getStartCount();

    public abstract String getStartPlatformVersion();

    public abstract long getUahDischarge(int i);

    public abstract long getUahDischargeDeepDoze(int i);

    public abstract long getUahDischargeLightDoze(int i);

    public abstract long getUahDischargeScreenDoze(int i);

    public abstract long getUahDischargeScreenOff(int i);

    public abstract SparseArray<? extends Uid> getUidStats();

    public abstract Map<String, ? extends Timer> getWakeupReasonStats();

    public abstract long getWifiActiveTime(long j, int i);

    public abstract ControllerActivityCounter getWifiControllerActivity();

    public abstract int getWifiMulticastWakelockCount(int i);

    public abstract long getWifiMulticastWakelockTime(long j, int i);

    public abstract long getWifiOnTime(long j, int i);

    public abstract int getWifiSignalStrengthCount(int i, int i2);

    public abstract long getWifiSignalStrengthTime(int i, long j, int i2);

    public abstract Timer getWifiSignalStrengthTimer(int i);

    public abstract int getWifiStateCount(int i, int i2);

    public abstract long getWifiStateTime(int i, long j, int i2);

    public abstract Timer getWifiStateTimer(int i);

    public abstract int getWifiSupplStateCount(int i, int i2);

    public abstract long getWifiSupplStateTime(int i, long j, int i2);

    public abstract Timer getWifiSupplStateTimer(int i);

    public abstract boolean hasBluetoothActivityReporting();

    public abstract boolean hasModemActivityReporting();

    public abstract boolean hasWifiActivityReporting();

    public void prepareForDumpLocked() {
    }

    public abstract boolean startIteratingHistoryLocked();

    public abstract boolean startIteratingOldHistoryLocked();

    public abstract void writeToParcelWithoutUids(Parcel parcel, int i);
}
