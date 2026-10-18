package com.android.internal.app.procstats;

import android.os.Debug;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.Process;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.provider.SettingsStringUtil;
import android.text.format.DateFormat;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.DebugUtils;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeUtils;
import android.util.proto.ProtoOutputStream;
import com.android.internal.app.ProcessMap;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.telephony.PhoneConstants;
import dalvik.system.VMRuntime;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes3.dex */
public final class ProcessStats implements Parcelable {
    public static final int ADD_PSS_EXTERNAL = 3;
    public static final int ADD_PSS_EXTERNAL_SLOW = 4;
    public static final int ADD_PSS_INTERNAL_ALL_MEM = 1;
    public static final int ADD_PSS_INTERNAL_ALL_POLL = 2;
    public static final int ADD_PSS_INTERNAL_SINGLE = 0;
    public static final int ADJ_COUNT = 8;
    public static final int ADJ_MEM_FACTOR_COUNT = 4;
    public static final int ADJ_MEM_FACTOR_CRITICAL = 3;
    public static final int ADJ_MEM_FACTOR_LOW = 2;
    public static final int ADJ_MEM_FACTOR_MODERATE = 1;
    public static final int ADJ_MEM_FACTOR_NORMAL = 0;
    public static final int ADJ_NOTHING = -1;
    public static final int ADJ_SCREEN_MOD = 4;
    public static final int ADJ_SCREEN_OFF = 0;
    public static final int ADJ_SCREEN_ON = 4;
    public static long COMMIT_PERIOD = 10800000;
    public static long COMMIT_UPTIME_PERIOD = 3600000;
    static final boolean DEBUG = false;
    static final boolean DEBUG_PARCEL = false;
    public static final int FLAG_COMPLETE = 1;
    public static final int FLAG_SHUTDOWN = 2;
    public static final int FLAG_SYSPROPS = 4;
    private static final int MAGIC = 1347638356;
    private static final int PARCEL_VERSION = 27;
    public static final int PSS_AVERAGE = 2;
    public static final int PSS_COUNT = 10;
    public static final int PSS_MAXIMUM = 3;
    public static final int PSS_MINIMUM = 1;
    public static final int PSS_RSS_AVERAGE = 8;
    public static final int PSS_RSS_MAXIMUM = 9;
    public static final int PSS_RSS_MINIMUM = 7;
    public static final int PSS_SAMPLE_COUNT = 0;
    public static final int PSS_USS_AVERAGE = 5;
    public static final int PSS_USS_MAXIMUM = 6;
    public static final int PSS_USS_MINIMUM = 4;
    public static final String SERVICE_NAME = "procstats";
    public static final int STATE_BACKUP = 4;
    public static final int STATE_CACHED_ACTIVITY = 11;
    public static final int STATE_CACHED_ACTIVITY_CLIENT = 12;
    public static final int STATE_CACHED_EMPTY = 13;
    public static final int STATE_COUNT = 14;
    public static final int STATE_HEAVY_WEIGHT = 8;
    public static final int STATE_HOME = 9;
    public static final int STATE_IMPORTANT_BACKGROUND = 3;
    public static final int STATE_IMPORTANT_FOREGROUND = 2;
    public static final int STATE_LAST_ACTIVITY = 10;
    public static final int STATE_NOTHING = -1;
    public static final int STATE_PERSISTENT = 0;
    public static final int STATE_RECEIVER = 7;
    public static final int STATE_SERVICE = 5;
    public static final int STATE_SERVICE_RESTARTING = 6;
    public static final int STATE_TOP = 1;
    public static final int SYS_MEM_USAGE_CACHED_AVERAGE = 2;
    public static final int SYS_MEM_USAGE_CACHED_MAXIMUM = 3;
    public static final int SYS_MEM_USAGE_CACHED_MINIMUM = 1;
    public static final int SYS_MEM_USAGE_COUNT = 16;
    public static final int SYS_MEM_USAGE_FREE_AVERAGE = 5;
    public static final int SYS_MEM_USAGE_FREE_MAXIMUM = 6;
    public static final int SYS_MEM_USAGE_FREE_MINIMUM = 4;
    public static final int SYS_MEM_USAGE_KERNEL_AVERAGE = 11;
    public static final int SYS_MEM_USAGE_KERNEL_MAXIMUM = 12;
    public static final int SYS_MEM_USAGE_KERNEL_MINIMUM = 10;
    public static final int SYS_MEM_USAGE_NATIVE_AVERAGE = 14;
    public static final int SYS_MEM_USAGE_NATIVE_MAXIMUM = 15;
    public static final int SYS_MEM_USAGE_NATIVE_MINIMUM = 13;
    public static final int SYS_MEM_USAGE_SAMPLE_COUNT = 0;
    public static final int SYS_MEM_USAGE_ZRAM_AVERAGE = 8;
    public static final int SYS_MEM_USAGE_ZRAM_MAXIMUM = 9;
    public static final int SYS_MEM_USAGE_ZRAM_MINIMUM = 7;
    public static final String TAG = "ProcessStats";
    ArrayMap<String, Integer> mCommonStringToIndex;
    public long mExternalPssCount;
    public long mExternalPssTime;
    public long mExternalSlowPssCount;
    public long mExternalSlowPssTime;
    public int mFlags;
    boolean mHasSwappedOutPss;
    ArrayList<String> mIndexToCommonString;
    public long mInternalAllMemPssCount;
    public long mInternalAllMemPssTime;
    public long mInternalAllPollPssCount;
    public long mInternalAllPollPssTime;
    public long mInternalSinglePssCount;
    public long mInternalSinglePssTime;
    public int mMemFactor;
    public final long[] mMemFactorDurations;
    public final ProcessMap<LongSparseArray<PackageState>> mPackages;
    private final ArrayList<String> mPageTypeLabels;
    private final ArrayList<int[]> mPageTypeSizes;
    private final ArrayList<Integer> mPageTypeZones;
    public final ProcessMap<ProcessState> mProcesses;
    public String mReadError;
    boolean mRunning;
    String mRuntime;
    public long mStartTime;
    public final SysMemUsageTable mSysMemUsage;
    public final long[] mSysMemUsageArgs;
    public final SparseMappingTable mTableData;
    public long mTimePeriodEndRealtime;
    public long mTimePeriodEndUptime;
    public long mTimePeriodStartClock;
    public String mTimePeriodStartClockStr;
    public long mTimePeriodStartRealtime;
    public long mTimePeriodStartUptime;
    public static final int[] ALL_MEM_ADJ = {0, 1, 2, 3};
    public static final int[] ALL_SCREEN_ADJ = {0, 4};
    public static final int[] NON_CACHED_PROC_STATES = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    public static final int[] BACKGROUND_PROC_STATES = {2, 3, 4, 8, 5, 6, 7};
    public static final int[] ALL_PROC_STATES = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13};
    private static final Pattern sPageTypeRegex = Pattern.compile("^Node\\s+(\\d+),.*. type\\s+(\\w+)\\s+([\\s\\d]+?)\\s*$");
    public static final Parcelable.Creator<ProcessStats> CREATOR = new Parcelable.Creator<ProcessStats>() { // from class: com.android.internal.app.procstats.ProcessStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProcessStats createFromParcel(Parcel parcel) {
            return new ProcessStats(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProcessStats[] newArray(int i) {
            return new ProcessStats[i];
        }
    };
    static final int[] BAD_TABLE = new int[0];

    public static final class PackageState {
        public final String mPackageName;
        public final ArrayMap<String, ProcessState> mProcesses = new ArrayMap<>();
        public final ArrayMap<String, ServiceState> mServices = new ArrayMap<>();
        public final int mUid;

        public PackageState(String str, int i) {
            this.mUid = i;
            this.mPackageName = str;
        }
    }

    public static final class ProcessDataCollection {
        public long avgPss;
        public long avgRss;
        public long avgUss;
        public long maxPss;
        public long maxRss;
        public long maxUss;
        final int[] memStates;
        public long minPss;
        public long minRss;
        public long minUss;
        public long numPss;
        final int[] procStates;
        final int[] screenStates;
        public long totalTime;

        public ProcessDataCollection(int[] iArr, int[] iArr2, int[] iArr3) {
            this.screenStates = iArr;
            this.memStates = iArr2;
            this.procStates = iArr3;
        }

        void print(PrintWriter printWriter, long j, boolean z) {
            if (this.totalTime > j) {
                printWriter.print(PhoneConstants.APN_TYPE_ALL);
            }
            DumpUtils.printPercent(printWriter, this.totalTime / j);
            if (this.numPss > 0) {
                printWriter.print(" (");
                DebugUtils.printSizeValue(printWriter, this.minPss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.avgPss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.maxPss * 1024);
                printWriter.print("/");
                DebugUtils.printSizeValue(printWriter, this.minUss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.avgUss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.maxUss * 1024);
                printWriter.print("/");
                DebugUtils.printSizeValue(printWriter, this.minRss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.avgRss * 1024);
                printWriter.print(NativeLibraryHelper.CLEAR_ABI_OVERRIDE);
                DebugUtils.printSizeValue(printWriter, this.maxRss * 1024);
                if (z) {
                    printWriter.print(" over ");
                    printWriter.print(this.numPss);
                }
                printWriter.print(")");
            }
        }
    }

    public static final class ProcessStateHolder {
        public final long appVersion;
        public ProcessState state;

        public ProcessStateHolder(long j) {
            this.appVersion = j;
        }
    }

    public static class TotalMemoryUseCollection {
        public boolean hasSwappedOutPss;
        final int[] memStates;
        final int[] screenStates;
        public double sysMemCachedWeight;
        public double sysMemFreeWeight;
        public double sysMemKernelWeight;
        public double sysMemNativeWeight;
        public int sysMemSamples;
        public double sysMemZRamWeight;
        public long totalTime;
        public long[] processStatePss = new long[14];
        public double[] processStateWeight = new double[14];
        public long[] processStateTime = new long[14];
        public int[] processStateSamples = new int[14];
        public long[] sysMemUsage = new long[16];

        public TotalMemoryUseCollection(int[] iArr, int[] iArr2) {
            this.screenStates = iArr;
            this.memStates = iArr2;
        }
    }

    public ProcessStats(Parcel parcel) throws Throwable {
        this.mPackages = new ProcessMap<>();
        this.mProcesses = new ProcessMap<>();
        this.mMemFactorDurations = new long[8];
        this.mMemFactor = -1;
        this.mTableData = new SparseMappingTable();
        this.mSysMemUsageArgs = new long[16];
        this.mSysMemUsage = new SysMemUsageTable(this.mTableData);
        this.mPageTypeZones = new ArrayList<>();
        this.mPageTypeLabels = new ArrayList<>();
        this.mPageTypeSizes = new ArrayList<>();
        reset();
        readFromParcel(parcel);
    }

    public ProcessStats(boolean z) throws Throwable {
        this.mPackages = new ProcessMap<>();
        this.mProcesses = new ProcessMap<>();
        this.mMemFactorDurations = new long[8];
        this.mMemFactor = -1;
        this.mTableData = new SparseMappingTable();
        this.mSysMemUsageArgs = new long[16];
        this.mSysMemUsage = new SysMemUsageTable(this.mTableData);
        this.mPageTypeZones = new ArrayList<>();
        this.mPageTypeLabels = new ArrayList<>();
        this.mPageTypeSizes = new ArrayList<>();
        this.mRunning = z;
        reset();
        if (z) {
            Debug.MemoryInfo memoryInfo = new Debug.MemoryInfo();
            Debug.getMemoryInfo(Process.myPid(), memoryInfo);
            this.mHasSwappedOutPss = memoryInfo.hasSwappedOutPss();
        }
    }

    private void buildTimePeriodStartClockStr() {
        this.mTimePeriodStartClockStr = DateFormat.format("yyyy-MM-dd-HH-mm-ss", this.mTimePeriodStartClock).toString();
    }

    private void dumpFragmentationLocked(PrintWriter printWriter) {
        printWriter.println();
        printWriter.println("Available pages by page size:");
        int size = this.mPageTypeLabels.size();
        for (int i = 0; i < size; i++) {
            printWriter.format("Zone %3d  %14s ", this.mPageTypeZones.get(i), this.mPageTypeLabels.get(i));
            int[] iArr = this.mPageTypeSizes.get(i);
            int length = iArr == null ? 0 : iArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                printWriter.format("%6d", Integer.valueOf(iArr[i2]));
            }
            printWriter.println();
        }
    }

    private boolean readCheckedInt(Parcel parcel, int i, String str) {
        int i2 = parcel.readInt();
        if (i2 == i) {
            return true;
        }
        this.mReadError = "bad " + str + ": " + i2;
        return false;
    }

    private String readCommonString(Parcel parcel, int i) {
        if (i <= 9) {
            return parcel.readString();
        }
        int i2 = parcel.readInt();
        if (i2 >= 0) {
            return this.mIndexToCommonString.get(i2);
        }
        int i3 = ~i2;
        String string = parcel.readString();
        while (this.mIndexToCommonString.size() <= i3) {
            this.mIndexToCommonString.add(null);
        }
        this.mIndexToCommonString.set(i3, string);
        return string;
    }

    private void readCompactedLongArray(Parcel parcel, int i, long[] jArr, int i2) {
        if (i <= 10) {
            parcel.readLongArray(jArr);
            return;
        }
        int length = jArr.length;
        if (i2 > length) {
            throw new RuntimeException("bad array lengths: got " + i2 + " array is " + length);
        }
        int i3 = 0;
        while (i3 < i2) {
            int i4 = parcel.readInt();
            if (i4 >= 0) {
                jArr[i3] = i4;
            } else {
                jArr[i3] = ((long) parcel.readInt()) | (((long) (~i4)) << 32);
            }
            i3++;
        }
        while (i3 < length) {
            jArr[i3] = 0;
            i3++;
        }
    }

    static byte[] readFully(InputStream inputStream, int[] iArr) throws IOException {
        int iAvailable = inputStream.available();
        byte[] bArr = new byte[iAvailable > 0 ? iAvailable + 1 : 16384];
        int i = 0;
        while (true) {
            int i2 = inputStream.read(bArr, i, bArr.length - i);
            if (i2 < 0) {
                iArr[0] = i;
                return bArr;
            }
            i += i2;
            if (i >= bArr.length) {
                byte[] bArr2 = new byte[i + 16384];
                System.arraycopy(bArr, 0, bArr2, 0, i);
                bArr = bArr2;
            }
        }
    }

    private void resetCommon() throws Throwable {
        this.mTimePeriodStartClock = System.currentTimeMillis();
        buildTimePeriodStartClockStr();
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        this.mTimePeriodEndRealtime = jElapsedRealtime;
        this.mTimePeriodStartRealtime = jElapsedRealtime;
        long jUptimeMillis = SystemClock.uptimeMillis();
        this.mTimePeriodEndUptime = jUptimeMillis;
        this.mTimePeriodStartUptime = jUptimeMillis;
        this.mInternalSinglePssCount = 0L;
        this.mInternalSinglePssTime = 0L;
        this.mInternalAllMemPssCount = 0L;
        this.mInternalAllMemPssTime = 0L;
        this.mInternalAllPollPssCount = 0L;
        this.mInternalAllPollPssTime = 0L;
        this.mExternalPssCount = 0L;
        this.mExternalPssTime = 0L;
        this.mExternalSlowPssCount = 0L;
        this.mExternalSlowPssTime = 0L;
        this.mTableData.reset();
        Arrays.fill(this.mMemFactorDurations, 0L);
        this.mSysMemUsage.resetTable();
        this.mStartTime = 0L;
        this.mReadError = null;
        this.mFlags = 0;
        evaluateSystemProperties(true);
        updateFragmentation();
    }

    private static int[] splitAndParseNumbers(String str) {
        int length = str.length();
        int i = 0;
        boolean z = false;
        for (int i2 = 0; i2 < length; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt < '0' || cCharAt > '9') {
                z = false;
            } else if (!z) {
                i++;
                z = true;
            }
        }
        int[] iArr = new int[i];
        int i3 = 0;
        int i4 = 0;
        boolean z2 = z;
        for (int i5 = 0; i5 < length; i5++) {
            char cCharAt2 = str.charAt(i5);
            if (cCharAt2 < '0' || cCharAt2 > '9') {
                if (z2) {
                    iArr[i4] = i3;
                    i4++;
                    z2 = false;
                }
            } else if (z2) {
                i3 = (i3 * 10) + (cCharAt2 - '0');
            } else {
                z2 = true;
                i3 = cCharAt2 - '0';
            }
        }
        if (i > 0) {
            iArr[i - 1] = i3;
        }
        return iArr;
    }

    private void writeCommonString(Parcel parcel, String str) {
        Integer num = this.mCommonStringToIndex.get(str);
        if (num != null) {
            parcel.writeInt(num.intValue());
            return;
        }
        Integer numValueOf = Integer.valueOf(this.mCommonStringToIndex.size());
        this.mCommonStringToIndex.put(str, numValueOf);
        parcel.writeInt(~numValueOf.intValue());
        parcel.writeString(str);
    }

    private void writeCompactedLongArray(Parcel parcel, long[] jArr, int i) {
        for (int i2 = 0; i2 < i; i2++) {
            long j = jArr[i2];
            if (j < 0) {
                Slog.w(TAG, "Time val negative: " + j);
                j = 0L;
            }
            if (j <= 2147483647L) {
                parcel.writeInt((int) j);
            } else {
                parcel.writeInt(~((int) (2147483647L & (j >> 32))));
                parcel.writeInt((int) (j & 4294967295L));
            }
        }
    }

    public void add(ProcessStats processStats) {
        int i;
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map = processStats.mPackages.getMap();
        for (int i2 = 0; i2 < map.size(); i2++) {
            String strKeyAt = map.keyAt(i2);
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt = map.valueAt(i2);
            for (int i3 = 0; i3 < sparseArrayValueAt.size(); i3++) {
                int iKeyAt = sparseArrayValueAt.keyAt(i3);
                LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt.valueAt(i3);
                int i4 = 0;
                while (i4 < longSparseArrayValueAt.size()) {
                    long jKeyAt = longSparseArrayValueAt.keyAt(i4);
                    PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(i4);
                    int size = packageStateValueAt.mProcesses.size();
                    int size2 = packageStateValueAt.mServices.size();
                    int i5 = 0;
                    while (i5 < size) {
                        int i6 = size2;
                        ProcessState processStateValueAt = packageStateValueAt.mProcesses.valueAt(i5);
                        int i7 = size;
                        if (processStateValueAt.getCommonProcess() != processStateValueAt) {
                            i = i6;
                            long j = jKeyAt;
                            ProcessState processStateLocked = getProcessStateLocked(strKeyAt, iKeyAt, jKeyAt, processStateValueAt.getName());
                            if (processStateLocked.getCommonProcess() == processStateLocked) {
                                processStateLocked.setMultiPackage(true);
                                long jUptimeMillis = SystemClock.uptimeMillis();
                                jKeyAt = j;
                                PackageState packageStateLocked = getPackageStateLocked(strKeyAt, iKeyAt, jKeyAt);
                                processStateLocked = processStateLocked.clone(jUptimeMillis);
                                packageStateLocked.mProcesses.put(processStateLocked.getName(), processStateLocked);
                            } else {
                                jKeyAt = j;
                            }
                            processStateLocked.add(processStateValueAt);
                        } else {
                            i = i6;
                        }
                        i5++;
                        size2 = i;
                        packageStateValueAt = packageStateValueAt;
                        size = i7;
                        i4 = i4;
                        longSparseArrayValueAt = longSparseArrayValueAt;
                        map = map;
                        sparseArrayValueAt = sparseArrayValueAt;
                    }
                    int i8 = i4;
                    LongSparseArray<PackageState> longSparseArray = longSparseArrayValueAt;
                    ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> arrayMap = map;
                    SparseArray<LongSparseArray<PackageState>> sparseArray = sparseArrayValueAt;
                    PackageState packageState = packageStateValueAt;
                    int i9 = 0;
                    for (int i10 = size2; i9 < i10; i10 = i10) {
                        ServiceState serviceStateValueAt = packageState.mServices.valueAt(i9);
                        getServiceStateLocked(strKeyAt, iKeyAt, jKeyAt, serviceStateValueAt.getProcessName(), serviceStateValueAt.getName()).add(serviceStateValueAt);
                        i9++;
                    }
                    i4 = i8 + 1;
                    longSparseArrayValueAt = longSparseArray;
                    map = arrayMap;
                    sparseArrayValueAt = sparseArray;
                }
            }
        }
        ArrayMap<String, SparseArray<ProcessState>> map2 = processStats.mProcesses.getMap();
        for (int i11 = 0; i11 < map2.size(); i11++) {
            int i12 = 0;
            for (SparseArray<ProcessState> sparseArrayValueAt2 = map2.valueAt(i11); i12 < sparseArrayValueAt2.size(); sparseArrayValueAt2 = sparseArrayValueAt2) {
                int iKeyAt2 = sparseArrayValueAt2.keyAt(i12);
                ProcessState processStateValueAt2 = sparseArrayValueAt2.valueAt(i12);
                String name = processStateValueAt2.getName();
                String str = processStateValueAt2.getPackage();
                long version = processStateValueAt2.getVersion();
                ProcessState processState = this.mProcesses.get(name, iKeyAt2);
                if (processState == null) {
                    ProcessState processState2 = new ProcessState(this, str, iKeyAt2, version, name);
                    this.mProcesses.put(name, iKeyAt2, processState2);
                    PackageState packageStateLocked2 = getPackageStateLocked(str, iKeyAt2, version);
                    if (!packageStateLocked2.mProcesses.containsKey(name)) {
                        packageStateLocked2.mProcesses.put(name, processState2);
                    }
                    processState = processState2;
                }
                processState.add(processStateValueAt2);
                i12++;
                map2 = map2;
            }
        }
        for (int i13 = 0; i13 < 8; i13++) {
            long[] jArr = this.mMemFactorDurations;
            jArr[i13] = jArr[i13] + processStats.mMemFactorDurations[i13];
        }
        this.mSysMemUsage.mergeStats(processStats.mSysMemUsage);
        if (processStats.mTimePeriodStartClock < this.mTimePeriodStartClock) {
            this.mTimePeriodStartClock = processStats.mTimePeriodStartClock;
            this.mTimePeriodStartClockStr = processStats.mTimePeriodStartClockStr;
        }
        this.mTimePeriodEndRealtime += processStats.mTimePeriodEndRealtime - processStats.mTimePeriodStartRealtime;
        this.mTimePeriodEndUptime += processStats.mTimePeriodEndUptime - processStats.mTimePeriodStartUptime;
        this.mInternalSinglePssCount += processStats.mInternalSinglePssCount;
        this.mInternalSinglePssTime += processStats.mInternalSinglePssTime;
        this.mInternalAllMemPssCount += processStats.mInternalAllMemPssCount;
        this.mInternalAllMemPssTime += processStats.mInternalAllMemPssTime;
        this.mInternalAllPollPssCount += processStats.mInternalAllPollPssCount;
        this.mInternalAllPollPssTime += processStats.mInternalAllPollPssTime;
        this.mExternalPssCount += processStats.mExternalPssCount;
        this.mExternalPssTime += processStats.mExternalPssTime;
        this.mExternalSlowPssCount += processStats.mExternalSlowPssCount;
        this.mExternalSlowPssTime += processStats.mExternalSlowPssTime;
        this.mHasSwappedOutPss |= processStats.mHasSwappedOutPss;
    }

    public void addSysMemUsage(long j, long j2, long j3, long j4, long j5) {
        if (this.mMemFactor != -1) {
            int i = this.mMemFactor * 14;
            this.mSysMemUsageArgs[0] = 1;
            int i2 = 0;
            while (i2 < 3) {
                int i3 = i2 + 1;
                this.mSysMemUsageArgs[i3] = j;
                this.mSysMemUsageArgs[i2 + 4] = j2;
                this.mSysMemUsageArgs[i2 + 7] = j3;
                this.mSysMemUsageArgs[i2 + 10] = j4;
                this.mSysMemUsageArgs[i2 + 13] = j5;
                i2 = i3;
            }
            this.mSysMemUsage.mergeStats(i, this.mSysMemUsageArgs, 0);
        }
    }

    public ArrayList<ProcessState> collectProcessesLocked(int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, long j, String str, boolean z) {
        ArraySet arraySet = new ArraySet();
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map = this.mPackages.getMap();
        for (int i = 0; i < map.size(); i++) {
            String strKeyAt = map.keyAt(i);
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt = map.valueAt(i);
            for (int i2 = 0; i2 < sparseArrayValueAt.size(); i2++) {
                LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt.valueAt(i2);
                int size = longSparseArrayValueAt.size();
                for (int i3 = 0; i3 < size; i3++) {
                    PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(i3);
                    int size2 = packageStateValueAt.mProcesses.size();
                    boolean z2 = str == null || str.equals(strKeyAt);
                    for (int i4 = 0; i4 < size2; i4++) {
                        ProcessState processStateValueAt = packageStateValueAt.mProcesses.valueAt(i4);
                        if ((z2 || str.equals(processStateValueAt.getName())) && (!z || processStateValueAt.isInUse())) {
                            arraySet.add(processStateValueAt.getCommonProcess());
                        }
                    }
                }
            }
        }
        ArrayList<ProcessState> arrayList = new ArrayList<>(arraySet.size());
        for (int i5 = 0; i5 < arraySet.size(); i5++) {
            ProcessState processState = (ProcessState) arraySet.valueAt(i5);
            if (processState.computeProcessTimeLocked(iArr, iArr2, iArr3, j) > 0) {
                arrayList.add(processState);
                if (iArr3 != iArr4) {
                    processState.computeProcessTimeLocked(iArr, iArr2, iArr4, j);
                }
            }
        }
        Collections.sort(arrayList, ProcessState.COMPARATOR);
        return arrayList;
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0090  */
    public void computeTotalMemoryUse(TotalMemoryUseCollection totalMemoryUseCollection, long j) {
        long[] arrayForKey;
        int indexFromKey;
        totalMemoryUseCollection.totalTime = 0L;
        int i = 0;
        for (int i2 = 0; i2 < 14; i2++) {
            totalMemoryUseCollection.processStateWeight[i2] = 0.0d;
            totalMemoryUseCollection.processStatePss[i2] = 0;
            totalMemoryUseCollection.processStateTime[i2] = 0;
            totalMemoryUseCollection.processStateSamples[i2] = 0;
        }
        for (int i3 = 0; i3 < 16; i3++) {
            totalMemoryUseCollection.sysMemUsage[i3] = 0;
        }
        totalMemoryUseCollection.sysMemCachedWeight = 0.0d;
        totalMemoryUseCollection.sysMemFreeWeight = 0.0d;
        totalMemoryUseCollection.sysMemZRamWeight = 0.0d;
        totalMemoryUseCollection.sysMemKernelWeight = 0.0d;
        totalMemoryUseCollection.sysMemNativeWeight = 0.0d;
        totalMemoryUseCollection.sysMemSamples = 0;
        long[] totalMemUsage = this.mSysMemUsage.getTotalMemUsage();
        for (int i4 = 0; i4 < totalMemoryUseCollection.screenStates.length; i4++) {
            int i5 = i;
            while (i5 < totalMemoryUseCollection.memStates.length) {
                int i6 = totalMemoryUseCollection.screenStates[i4] + totalMemoryUseCollection.memStates[i5];
                int i7 = i6 * 14;
                long j2 = this.mMemFactorDurations[i6];
                if (this.mMemFactor == i6) {
                    j2 += j - this.mStartTime;
                }
                totalMemoryUseCollection.totalTime += j2;
                int key = this.mSysMemUsage.getKey((byte) i7);
                if (key != -1) {
                    arrayForKey = this.mSysMemUsage.getArrayForKey(key);
                    indexFromKey = SparseMappingTable.getIndexFromKey(key);
                    if (arrayForKey[indexFromKey + 0] >= 3) {
                        SysMemUsageTable.mergeSysMemUsage(totalMemoryUseCollection.sysMemUsage, i, totalMemUsage, i);
                    } else {
                        arrayForKey = totalMemUsage;
                        indexFromKey = i;
                    }
                } else {
                    arrayForKey = totalMemUsage;
                    indexFromKey = i;
                }
                double d = j2;
                totalMemoryUseCollection.sysMemCachedWeight += arrayForKey[indexFromKey + 2] * d;
                totalMemoryUseCollection.sysMemFreeWeight += arrayForKey[indexFromKey + 5] * d;
                totalMemoryUseCollection.sysMemZRamWeight += arrayForKey[indexFromKey + 8] * d;
                totalMemoryUseCollection.sysMemKernelWeight += arrayForKey[indexFromKey + 11] * d;
                totalMemoryUseCollection.sysMemNativeWeight += arrayForKey[indexFromKey + 14] * d;
                totalMemoryUseCollection.sysMemSamples = (int) (((long) totalMemoryUseCollection.sysMemSamples) + arrayForKey[indexFromKey + 0]);
                i5++;
                i = 0;
            }
        }
        int i8 = i;
        totalMemoryUseCollection.hasSwappedOutPss = this.mHasSwappedOutPss;
        ArrayMap<String, SparseArray<ProcessState>> map = this.mProcesses.getMap();
        for (int i9 = i8; i9 < map.size(); i9++) {
            SparseArray<ProcessState> sparseArrayValueAt = map.valueAt(i9);
            for (int i10 = i8; i10 < sparseArrayValueAt.size(); i10++) {
                sparseArrayValueAt.valueAt(i10).aggregatePss(totalMemoryUseCollection, j);
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void dumpCheckinLocked(PrintWriter printWriter, String str) {
        boolean z;
        long jUptimeMillis = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map = this.mPackages.getMap();
        printWriter.println("vers,5");
        printWriter.print("period,");
        printWriter.print(this.mTimePeriodStartClockStr);
        printWriter.print(",");
        printWriter.print(this.mTimePeriodStartRealtime);
        printWriter.print(",");
        printWriter.print(this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime);
        if ((this.mFlags & 2) != 0) {
            printWriter.print(",shutdown");
            z = false;
        } else {
            z = true;
        }
        if ((this.mFlags & 4) != 0) {
            printWriter.print(",sysprops");
            z = false;
        }
        if ((this.mFlags & 1) != 0) {
            printWriter.print(",complete");
            z = false;
        }
        if (z) {
            printWriter.print(",partial");
        }
        if (this.mHasSwappedOutPss) {
            printWriter.print(",swapped-out-pss");
        }
        printWriter.println();
        printWriter.print("config,");
        printWriter.println(this.mRuntime);
        int i = 0;
        while (i < map.size()) {
            String strKeyAt = map.keyAt(i);
            if (str == 0 || str.equals(strKeyAt)) {
                SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt = map.valueAt(i);
                int i2 = 0;
                while (i2 < sparseArrayValueAt.size()) {
                    int iKeyAt = sparseArrayValueAt.keyAt(i2);
                    LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt.valueAt(i2);
                    int i3 = 0;
                    while (i3 < longSparseArrayValueAt.size()) {
                        long jKeyAt = longSparseArrayValueAt.keyAt(i3);
                        PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(i3);
                        int size = packageStateValueAt.mProcesses.size();
                        int size2 = packageStateValueAt.mServices.size();
                        int i4 = 0;
                        while (i4 < size) {
                            packageStateValueAt.mProcesses.valueAt(i4).dumpPackageProcCheckin(printWriter, strKeyAt, iKeyAt, jKeyAt, packageStateValueAt.mProcesses.keyAt(i4), jUptimeMillis);
                            i4++;
                            strKeyAt = strKeyAt;
                            size2 = size2;
                            packageStateValueAt = packageStateValueAt;
                            size = size;
                            i3 = i3;
                            i = i;
                            map = map;
                            i2 = i2;
                            longSparseArrayValueAt = longSparseArrayValueAt;
                            sparseArrayValueAt = sparseArrayValueAt;
                        }
                        int i5 = size2;
                        int i6 = i3;
                        int i7 = i2;
                        LongSparseArray<PackageState> longSparseArray = longSparseArrayValueAt;
                        SparseArray<LongSparseArray<PackageState>> sparseArray = sparseArrayValueAt;
                        int i8 = i;
                        String str2 = strKeyAt;
                        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> arrayMap = map;
                        PackageState packageState = packageStateValueAt;
                        for (int i9 = 0; i9 < i5; i9++) {
                            packageState.mServices.valueAt(i9).dumpTimesCheckin(printWriter, str2, iKeyAt, jKeyAt, DumpUtils.collapseString(str2, packageState.mServices.keyAt(i9)), jUptimeMillis);
                        }
                        i3 = i6 + 1;
                        strKeyAt = str2;
                        i = i8;
                        map = arrayMap;
                        i2 = i7;
                        longSparseArrayValueAt = longSparseArray;
                        sparseArrayValueAt = sparseArray;
                    }
                    i2++;
                }
            }
            i++;
            map = map;
        }
        ArrayMap<String, SparseArray<ProcessState>> map2 = this.mProcesses.getMap();
        for (int i10 = 0; i10 < map2.size(); i10++) {
            String strKeyAt2 = map2.keyAt(i10);
            SparseArray<ProcessState> sparseArrayValueAt2 = map2.valueAt(i10);
            for (int i11 = 0; i11 < sparseArrayValueAt2.size(); i11++) {
                sparseArrayValueAt2.valueAt(i11).dumpProcCheckin(printWriter, strKeyAt2, sparseArrayValueAt2.keyAt(i11), jUptimeMillis);
            }
        }
        printWriter.print("total");
        DumpUtils.dumpAdjTimesCheckin(printWriter, ",", this.mMemFactorDurations, this.mMemFactor, this.mStartTime, jUptimeMillis);
        printWriter.println();
        int keyCount = this.mSysMemUsage.getKeyCount();
        if (keyCount > 0) {
            printWriter.print("sysmemusage");
            for (int i12 = 0; i12 < keyCount; i12++) {
                int keyAt = this.mSysMemUsage.getKeyAt(i12);
                byte idFromKey = SparseMappingTable.getIdFromKey(keyAt);
                printWriter.print(",");
                DumpUtils.printProcStateTag(printWriter, idFromKey);
                for (int i13 = 0; i13 < 16; i13++) {
                    if (i13 > 1) {
                        printWriter.print(SettingsStringUtil.DELIMITER);
                    }
                    printWriter.print(this.mSysMemUsage.getValue(keyAt, i13));
                }
            }
        }
        printWriter.println();
        TotalMemoryUseCollection totalMemoryUseCollection = new TotalMemoryUseCollection(ALL_SCREEN_ADJ, ALL_MEM_ADJ);
        computeTotalMemoryUse(totalMemoryUseCollection, jUptimeMillis);
        printWriter.print("weights,");
        printWriter.print(totalMemoryUseCollection.totalTime);
        printWriter.print(",");
        printWriter.print(totalMemoryUseCollection.sysMemCachedWeight);
        printWriter.print(SettingsStringUtil.DELIMITER);
        printWriter.print(totalMemoryUseCollection.sysMemSamples);
        printWriter.print(",");
        printWriter.print(totalMemoryUseCollection.sysMemFreeWeight);
        printWriter.print(SettingsStringUtil.DELIMITER);
        printWriter.print(totalMemoryUseCollection.sysMemSamples);
        printWriter.print(",");
        printWriter.print(totalMemoryUseCollection.sysMemZRamWeight);
        printWriter.print(SettingsStringUtil.DELIMITER);
        printWriter.print(totalMemoryUseCollection.sysMemSamples);
        printWriter.print(",");
        printWriter.print(totalMemoryUseCollection.sysMemKernelWeight);
        printWriter.print(SettingsStringUtil.DELIMITER);
        printWriter.print(totalMemoryUseCollection.sysMemSamples);
        printWriter.print(",");
        printWriter.print(totalMemoryUseCollection.sysMemNativeWeight);
        printWriter.print(SettingsStringUtil.DELIMITER);
        printWriter.print(totalMemoryUseCollection.sysMemSamples);
        for (int i14 = 0; i14 < 14; i14++) {
            printWriter.print(",");
            printWriter.print(totalMemoryUseCollection.processStateWeight[i14]);
            printWriter.print(SettingsStringUtil.DELIMITER);
            printWriter.print(totalMemoryUseCollection.processStateSamples[i14]);
        }
        printWriter.println();
        int size3 = this.mPageTypeLabels.size();
        for (int i15 = 0; i15 < size3; i15++) {
            printWriter.print("availablepages,");
            printWriter.print(this.mPageTypeLabels.get(i15));
            printWriter.print(",");
            printWriter.print(this.mPageTypeZones.get(i15));
            printWriter.print(",");
            int[] iArr = this.mPageTypeSizes.get(i15);
            int length = iArr == null ? 0 : iArr.length;
            for (int i16 = 0; i16 < length; i16++) {
                if (i16 != 0) {
                    printWriter.print(",");
                }
                printWriter.print(iArr[i16]);
            }
            printWriter.println();
        }
    }

    void dumpFilteredSummaryLocked(PrintWriter printWriter, String str, String str2, int[] iArr, int[] iArr2, int[] iArr3, int[] iArr4, long j, long j2, String str3, boolean z) {
        ArrayList<ProcessState> arrayListCollectProcessesLocked = collectProcessesLocked(iArr, iArr2, iArr3, iArr4, j, str3, z);
        if (arrayListCollectProcessesLocked.size() > 0) {
            if (str != null) {
                printWriter.println();
                printWriter.println(str);
            }
            DumpUtils.dumpProcessSummaryLocked(printWriter, str2, arrayListCollectProcessesLocked, iArr, iArr2, iArr4, j, j2);
        }
    }

    public void dumpLocked(PrintWriter printWriter, String str, long j, boolean z, boolean z2, boolean z3) {
        boolean z4;
        boolean z5;
        boolean z6;
        PrintWriter printWriter2;
        boolean z7;
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> arrayMap;
        int i;
        boolean z8;
        PrintWriter printWriter3;
        String str2;
        PrintWriter printWriter4;
        boolean z9;
        ProcessStats processStats = this;
        PrintWriter printWriter5 = printWriter;
        String str3 = str;
        boolean z10 = z2;
        long jDumpSingleTime = DumpUtils.dumpSingleTime(null, null, processStats.mMemFactorDurations, processStats.mMemFactor, processStats.mStartTime, j);
        if (processStats.mSysMemUsage.getKeyCount() > 0) {
            printWriter5.println("System memory usage:");
            processStats.mSysMemUsage.dump(printWriter5, "  ", ALL_SCREEN_ADJ, ALL_MEM_ADJ);
            z4 = true;
        } else {
            z4 = false;
        }
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map = processStats.mPackages.getMap();
        int i2 = 0;
        boolean z11 = false;
        while (i2 < map.size()) {
            String strKeyAt = map.keyAt(i2);
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt = map.valueAt(i2);
            boolean z12 = z11;
            boolean z13 = z4;
            int i3 = 0;
            while (i3 < sparseArrayValueAt.size()) {
                int iKeyAt = sparseArrayValueAt.keyAt(i3);
                LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt.valueAt(i3);
                int i4 = 0;
                while (i4 < longSparseArrayValueAt.size()) {
                    long jKeyAt = longSparseArrayValueAt.keyAt(i4);
                    PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(i4);
                    int i5 = i3;
                    int size = packageStateValueAt.mProcesses.size();
                    int i6 = i2;
                    int size2 = packageStateValueAt.mServices.size();
                    boolean z14 = str3 == null || str3.equals(strKeyAt);
                    if (z14) {
                        arrayMap = map;
                        i = i4;
                    } else {
                        arrayMap = map;
                        int i7 = 0;
                        while (true) {
                            if (i7 >= size) {
                                i = i4;
                                z9 = false;
                                break;
                            }
                            i = i4;
                            if (str3.equals(packageStateValueAt.mProcesses.valueAt(i7).getName())) {
                                z9 = true;
                                break;
                            } else {
                                i7++;
                                i4 = i;
                            }
                        }
                        if (!z9) {
                            longSparseArrayValueAt = longSparseArrayValueAt;
                            sparseArrayValueAt = sparseArrayValueAt;
                            iKeyAt = iKeyAt;
                            strKeyAt = strKeyAt;
                            PrintWriter printWriter6 = printWriter5;
                            str2 = str3;
                            printWriter4 = printWriter6;
                        }
                        i4 = i + 1;
                        i3 = i5;
                        i2 = i6;
                        map = arrayMap;
                        longSparseArrayValueAt = longSparseArrayValueAt;
                        sparseArrayValueAt = sparseArrayValueAt;
                        iKeyAt = iKeyAt;
                        strKeyAt = strKeyAt;
                        String str4 = str2;
                        printWriter5 = printWriter4;
                        str3 = str4;
                    }
                    if (size > 0 || size2 > 0) {
                        if (!z12) {
                            if (z13) {
                                printWriter.println();
                            }
                            printWriter5.println("Per-Package Stats:");
                            z13 = true;
                            z12 = true;
                        }
                        printWriter5.print("  * ");
                        printWriter5.print(strKeyAt);
                        printWriter5.print(" / ");
                        UserHandle.formatUid(printWriter5, iKeyAt);
                        printWriter5.print(" / v");
                        printWriter5.print(jKeyAt);
                        printWriter5.println(SettingsStringUtil.DELIMITER);
                    }
                    boolean z15 = z13;
                    boolean z16 = z12;
                    if (!z || z10) {
                        int i8 = 0;
                        while (i8 < size) {
                            ProcessState processStateValueAt = packageStateValueAt.mProcesses.valueAt(i8);
                            if (!z14 && !str3.equals(processStateValueAt.getName())) {
                                z8 = z10;
                                printWriter3 = printWriter5;
                            } else if (!z3 || processStateValueAt.isInUse()) {
                                printWriter5.print("      Process ");
                                printWriter5.print(packageStateValueAt.mProcesses.keyAt(i8));
                                if (processStateValueAt.getCommonProcess().isMultiPackage()) {
                                    printWriter5.print(" (multi, ");
                                } else {
                                    printWriter5.print(" (unique, ");
                                }
                                printWriter5.print(processStateValueAt.getDurationsBucketCount());
                                printWriter5.print(" entries)");
                                printWriter5.println(SettingsStringUtil.DELIMITER);
                                processStateValueAt.dumpProcessState(printWriter, "        ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, j);
                                z8 = z10;
                                printWriter3 = printWriter5;
                                processStateValueAt.dumpPss(printWriter, "        ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                                processStateValueAt.dumpInternalLocked(printWriter3, "        ", z8);
                            } else {
                                printWriter5.print("      (Not active: ");
                                printWriter5.print(packageStateValueAt.mProcesses.keyAt(i8));
                                printWriter5.println(")");
                                z8 = z10;
                                printWriter3 = printWriter5;
                            }
                            i8++;
                            str3 = str3;
                            printWriter5 = printWriter3;
                            size = size;
                            z10 = z8;
                            size2 = size2;
                        }
                    } else {
                        ArrayList arrayList = new ArrayList();
                        for (int i9 = 0; i9 < size; i9++) {
                            ProcessState processStateValueAt2 = packageStateValueAt.mProcesses.valueAt(i9);
                            if ((z14 || str3.equals(processStateValueAt2.getName())) && (!z3 || processStateValueAt2.isInUse())) {
                                arrayList.add(processStateValueAt2);
                            }
                        }
                        DumpUtils.dumpProcessSummaryLocked(printWriter, "      ", arrayList, ALL_SCREEN_ADJ, ALL_MEM_ADJ, NON_CACHED_PROC_STATES, j, jDumpSingleTime);
                    }
                    int i10 = size2;
                    boolean z17 = z10;
                    String str5 = str3;
                    PrintWriter printWriter7 = printWriter5;
                    int i11 = 0;
                    while (i11 < i10) {
                        ServiceState serviceStateValueAt = packageStateValueAt.mServices.valueAt(i11);
                        if (z14 || str5.equals(serviceStateValueAt.getProcessName())) {
                            if (!z3 || serviceStateValueAt.isInUse()) {
                                if (z17) {
                                    printWriter7.print("      Service ");
                                } else {
                                    printWriter7.print("      * ");
                                }
                                printWriter7.print(packageStateValueAt.mServices.keyAt(i11));
                                printWriter7.println(SettingsStringUtil.DELIMITER);
                                printWriter7.print("        Process: ");
                                printWriter7.println(serviceStateValueAt.getProcessName());
                                serviceStateValueAt.dumpStats(printWriter, "        ", "          ", "    ", j, jDumpSingleTime, z, z2);
                            } else {
                                printWriter7.print("      (Not active: ");
                                printWriter7.print(packageStateValueAt.mServices.keyAt(i11));
                                printWriter7.println(")");
                            }
                        }
                        i11++;
                        i10 = i10;
                        z17 = z17;
                        printWriter7 = printWriter7;
                        str5 = str5;
                    }
                    str2 = str5;
                    printWriter4 = printWriter7;
                    z10 = z17;
                    z13 = z15;
                    z12 = z16;
                    i4 = i + 1;
                    i3 = i5;
                    i2 = i6;
                    map = arrayMap;
                    longSparseArrayValueAt = longSparseArrayValueAt;
                    sparseArrayValueAt = sparseArrayValueAt;
                    iKeyAt = iKeyAt;
                    strKeyAt = strKeyAt;
                    String str6 = str2;
                    printWriter5 = printWriter4;
                    str3 = str6;
                }
                i3++;
                printWriter5 = printWriter5;
                str3 = str3;
            }
            i2++;
            z4 = z13;
            z11 = z12;
            processStats = this;
            printWriter5 = printWriter5;
            str3 = str3;
        }
        PrintWriter printWriter8 = printWriter5;
        String str7 = str3;
        PrintWriter printWriter9 = printWriter8;
        ArrayMap<String, SparseArray<ProcessState>> map2 = processStats.mProcesses.getMap();
        boolean z18 = z4;
        int i12 = 0;
        int i13 = 0;
        int i14 = 0;
        boolean z19 = false;
        while (i14 < map2.size()) {
            String strKeyAt2 = map2.keyAt(i14);
            SparseArray<ProcessState> sparseArrayValueAt2 = map2.valueAt(i14);
            boolean z20 = z19;
            int i15 = i12;
            int i16 = i13;
            int i17 = 0;
            while (i17 < sparseArrayValueAt2.size()) {
                int iKeyAt2 = sparseArrayValueAt2.keyAt(i17);
                int i18 = i16 + 1;
                ProcessState processStateValueAt3 = sparseArrayValueAt2.valueAt(i17);
                if (!processStateValueAt3.hasAnyData() && processStateValueAt3.isMultiPackage() && (str7 == null || str7.equals(strKeyAt2) || str7.equals(processStateValueAt3.getPackage()))) {
                    int i19 = i15 + 1;
                    if (z18) {
                        printWriter.println();
                    }
                    if (z20) {
                        z7 = z20;
                    } else {
                        printWriter9.println("Multi-Package Common Processes:");
                        z7 = true;
                    }
                    if (!z3 || processStateValueAt3.isInUse()) {
                        printWriter9.print("  * ");
                        printWriter9.print(strKeyAt2);
                        printWriter9.print(" / ");
                        UserHandle.formatUid(printWriter9, iKeyAt2);
                        printWriter9.print(" (");
                        printWriter9.print(processStateValueAt3.getDurationsBucketCount());
                        printWriter9.print(" entries)");
                        printWriter9.println(SettingsStringUtil.DELIMITER);
                        processStateValueAt3.dumpProcessState(printWriter, "        ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, j);
                        z6 = z10;
                        printWriter2 = printWriter9;
                        processStateValueAt3.dumpPss(printWriter, "        ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES);
                        processStateValueAt3.dumpInternalLocked(printWriter2, "        ", z6);
                    } else {
                        printWriter9.print("      (Not active: ");
                        printWriter9.print(strKeyAt2);
                        printWriter9.println(")");
                        printWriter2 = printWriter9;
                        z6 = z10;
                    }
                    i15 = i19;
                    z20 = z7;
                    z5 = true;
                } else {
                    i17 = i17;
                    z5 = z18;
                    strKeyAt2 = strKeyAt2;
                    sparseArrayValueAt2 = sparseArrayValueAt2;
                    i14 = i14;
                    z6 = z10;
                    printWriter2 = printWriter9;
                }
                i17++;
                str7 = str;
                printWriter9 = printWriter2;
                i14 = i14;
                z10 = z6;
                strKeyAt2 = strKeyAt2;
                sparseArrayValueAt2 = sparseArrayValueAt2;
                z18 = z5;
                i16 = i18;
            }
            i14++;
            str7 = str;
            i13 = i16;
            i12 = i15;
            z19 = z20;
        }
        boolean z21 = z10;
        PrintWriter printWriter10 = printWriter9;
        if (z21) {
            printWriter.println();
            printWriter10.print("  Total procs: ");
            printWriter10.print(i12);
            printWriter10.print(" shown of ");
            printWriter10.print(i13);
            printWriter10.println(" total");
        }
        if (z18) {
            printWriter.println();
        }
        if (z) {
            printWriter10.println("Summary:");
            dumpSummaryLocked(printWriter, str, j, z3);
        } else {
            processStats.dumpTotalsLocked(printWriter10, j);
        }
        if (z21) {
            printWriter.println();
            printWriter10.println("Internal state:");
            printWriter10.print("  mRunning=");
            printWriter10.println(processStats.mRunning);
        }
        dumpFragmentationLocked(printWriter);
    }

    public void dumpSummaryLocked(PrintWriter printWriter, String str, long j, boolean z) {
        dumpFilteredSummaryLocked(printWriter, null, "  ", ALL_SCREEN_ADJ, ALL_MEM_ADJ, ALL_PROC_STATES, NON_CACHED_PROC_STATES, j, DumpUtils.dumpSingleTime(null, null, this.mMemFactorDurations, this.mMemFactor, this.mStartTime, j), str, z);
        printWriter.println();
        dumpTotalsLocked(printWriter, j);
    }

    void dumpTotalsLocked(PrintWriter printWriter, long j) {
        boolean z;
        printWriter.println("Run time Stats:");
        DumpUtils.dumpSingleTime(printWriter, "  ", this.mMemFactorDurations, this.mMemFactor, this.mStartTime, j);
        printWriter.println();
        printWriter.println("Memory usage:");
        TotalMemoryUseCollection totalMemoryUseCollection = new TotalMemoryUseCollection(ALL_SCREEN_ADJ, ALL_MEM_ADJ);
        computeTotalMemoryUse(totalMemoryUseCollection, j);
        boolean z2 = false;
        long jPrintMemoryCategory = printMemoryCategory(printWriter, "  ", "Native ", totalMemoryUseCollection.sysMemNativeWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(printWriter, "  ", "Kernel ", totalMemoryUseCollection.sysMemKernelWeight, totalMemoryUseCollection.totalTime, 0L, totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples);
        for (int i = 0; i < 14; i++) {
            if (i != 6) {
                jPrintMemoryCategory = printMemoryCategory(printWriter, "  ", DumpUtils.STATE_NAMES[i], totalMemoryUseCollection.processStateWeight[i], totalMemoryUseCollection.totalTime, jPrintMemoryCategory, totalMemoryUseCollection.processStateSamples[i]);
            }
        }
        long jPrintMemoryCategory2 = printMemoryCategory(printWriter, "  ", "Z-Ram  ", totalMemoryUseCollection.sysMemZRamWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(printWriter, "  ", "Free   ", totalMemoryUseCollection.sysMemFreeWeight, totalMemoryUseCollection.totalTime, printMemoryCategory(printWriter, "  ", "Cached ", totalMemoryUseCollection.sysMemCachedWeight, totalMemoryUseCollection.totalTime, jPrintMemoryCategory, totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples), totalMemoryUseCollection.sysMemSamples);
        printWriter.print("  TOTAL  : ");
        DebugUtils.printSizeValue(printWriter, jPrintMemoryCategory2);
        printWriter.println();
        printMemoryCategory(printWriter, "  ", DumpUtils.STATE_NAMES[6], totalMemoryUseCollection.processStateWeight[6], totalMemoryUseCollection.totalTime, jPrintMemoryCategory2, totalMemoryUseCollection.processStateSamples[6]);
        printWriter.println();
        printWriter.println("PSS collection stats:");
        printWriter.print("  Internal Single: ");
        printWriter.print(this.mInternalSinglePssCount);
        printWriter.print("x over ");
        TimeUtils.formatDuration(this.mInternalSinglePssTime, printWriter);
        printWriter.println();
        printWriter.print("  Internal All Procs (Memory Change): ");
        printWriter.print(this.mInternalAllMemPssCount);
        printWriter.print("x over ");
        TimeUtils.formatDuration(this.mInternalAllMemPssTime, printWriter);
        printWriter.println();
        printWriter.print("  Internal All Procs (Polling): ");
        printWriter.print(this.mInternalAllPollPssCount);
        printWriter.print("x over ");
        TimeUtils.formatDuration(this.mInternalAllPollPssTime, printWriter);
        printWriter.println();
        printWriter.print("  External: ");
        printWriter.print(this.mExternalPssCount);
        printWriter.print("x over ");
        TimeUtils.formatDuration(this.mExternalPssTime, printWriter);
        printWriter.println();
        printWriter.print("  External Slow: ");
        printWriter.print(this.mExternalSlowPssCount);
        printWriter.print("x over ");
        TimeUtils.formatDuration(this.mExternalSlowPssTime, printWriter);
        printWriter.println();
        printWriter.println();
        printWriter.print("          Start time: ");
        printWriter.print(DateFormat.format("yyyy-MM-dd HH:mm:ss", this.mTimePeriodStartClock));
        printWriter.println();
        printWriter.print("        Total uptime: ");
        TimeUtils.formatDuration((this.mRunning ? SystemClock.uptimeMillis() : this.mTimePeriodEndUptime) - this.mTimePeriodStartUptime, printWriter);
        printWriter.println();
        printWriter.print("  Total elapsed time: ");
        TimeUtils.formatDuration((this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime) - this.mTimePeriodStartRealtime, printWriter);
        if ((this.mFlags & 2) != 0) {
            printWriter.print(" (shutdown)");
            z = false;
        } else {
            z = true;
        }
        if ((this.mFlags & 4) != 0) {
            printWriter.print(" (sysprops)");
            z = false;
        }
        if ((1 & this.mFlags) != 0) {
            printWriter.print(" (complete)");
        } else {
            z2 = z;
        }
        if (z2) {
            printWriter.print(" (partial)");
        }
        if (this.mHasSwappedOutPss) {
            printWriter.print(" (swapped-out-pss)");
        }
        printWriter.print(' ');
        printWriter.print(this.mRuntime);
        printWriter.println();
    }

    public boolean evaluateSystemProperties(boolean z) {
        String str = SystemProperties.get("persist.sys.dalvik.vm.lib.2", VMRuntime.getRuntime().vmLibrary());
        if (Objects.equals(str, this.mRuntime)) {
            return false;
        }
        if (!z) {
            return true;
        }
        this.mRuntime = str;
        return true;
    }

    public PackageState getPackageStateLocked(String str, int i, long j) {
        LongSparseArray<PackageState> longSparseArray = this.mPackages.get(str, i);
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
            this.mPackages.put(str, i, longSparseArray);
        }
        PackageState packageState = longSparseArray.get(j);
        if (packageState != null) {
            return packageState;
        }
        PackageState packageState2 = new PackageState(str, i);
        longSparseArray.put(j, packageState2);
        return packageState2;
    }

    public ProcessState getProcessStateLocked(String str, int i, long j, String str2) {
        ProcessState processState;
        ProcessState processState2;
        PackageState packageStateLocked = getPackageStateLocked(str, i, j);
        ProcessState processState3 = packageStateLocked.mProcesses.get(str2);
        if (processState3 != null) {
            return processState3;
        }
        ProcessState processState4 = this.mProcesses.get(str2, i);
        if (processState4 == null) {
            ProcessState processState5 = new ProcessState(this, str, i, j, str2);
            this.mProcesses.put(str2, i, processState5);
            processState = processState5;
        } else {
            processState = processState4;
        }
        if (processState.isMultiPackage()) {
            processState2 = new ProcessState(processState, str, i, j, str2, SystemClock.uptimeMillis());
        } else if (str.equals(processState.getPackage()) && j == processState.getVersion()) {
            processState2 = processState;
        } else {
            processState.setMultiPackage(true);
            long jUptimeMillis = SystemClock.uptimeMillis();
            PackageState packageStateLocked2 = getPackageStateLocked(processState.getPackage(), i, processState.getVersion());
            if (packageStateLocked2 != null) {
                ProcessState processStateClone = processState.clone(jUptimeMillis);
                packageStateLocked2.mProcesses.put(processState.getName(), processStateClone);
                for (int size = packageStateLocked2.mServices.size() - 1; size >= 0; size--) {
                    ServiceState serviceStateValueAt = packageStateLocked2.mServices.valueAt(size);
                    if (serviceStateValueAt.getProcess() == processState) {
                        serviceStateValueAt.setProcess(processStateClone);
                    }
                }
            } else {
                Slog.w(TAG, "Cloning proc state: no package state " + processState.getPackage() + "/" + i + " for proc " + processState.getName());
            }
            processState2 = new ProcessState(processState, str, i, j, str2, jUptimeMillis);
        }
        packageStateLocked.mProcesses.put(str2, processState2);
        return processState2;
    }

    public ServiceState getServiceStateLocked(String str, int i, long j, String str2, String str3) {
        PackageState packageStateLocked = getPackageStateLocked(str, i, j);
        ServiceState serviceState = packageStateLocked.mServices.get(str3);
        if (serviceState != null) {
            return serviceState;
        }
        ServiceState serviceState2 = new ServiceState(this, str, str3, str2, str2 != null ? getProcessStateLocked(str, i, j, str2) : null);
        packageStateLocked.mServices.put(str3, serviceState2);
        return serviceState2;
    }

    long printMemoryCategory(PrintWriter printWriter, String str, String str2, double d, long j, long j2, int i) {
        if (d == 0.0d) {
            return j2;
        }
        long j3 = (long) ((d * 1024.0d) / j);
        printWriter.print(str);
        printWriter.print(str2);
        printWriter.print(": ");
        DebugUtils.printSizeValue(printWriter, j3);
        printWriter.print(" (");
        printWriter.print(i);
        printWriter.print(" samples)");
        printWriter.println();
        return j2 + j3;
    }

    public void read(InputStream inputStream) throws Throwable {
        try {
            int[] iArr = new int[1];
            byte[] fully = readFully(inputStream, iArr);
            Parcel parcelObtain = Parcel.obtain();
            parcelObtain.unmarshall(fully, 0, iArr[0]);
            parcelObtain.setDataPosition(0);
            inputStream.close();
            readFromParcel(parcelObtain);
        } catch (IOException e) {
            this.mReadError = "caught exception: " + e;
        }
    }

    public void readFromParcel(Parcel parcel) throws Throwable {
        boolean z;
        boolean z2;
        boolean z3 = false;
        boolean z4 = true;
        boolean z5 = this.mPackages.getMap().size() > 0 || this.mProcesses.getMap().size() > 0;
        if (z5) {
            resetSafely();
        }
        if (readCheckedInt(parcel, MAGIC, "magic number")) {
            int i = parcel.readInt();
            if (i != 27) {
                this.mReadError = "bad version: " + i;
                return;
            }
            if (readCheckedInt(parcel, 14, "state count") && readCheckedInt(parcel, 8, "adj count") && readCheckedInt(parcel, 10, "pss count") && readCheckedInt(parcel, 16, "sys mem usage count") && readCheckedInt(parcel, 4096, "longs size")) {
                this.mIndexToCommonString = new ArrayList<>();
                this.mTimePeriodStartClock = parcel.readLong();
                buildTimePeriodStartClockStr();
                this.mTimePeriodStartRealtime = parcel.readLong();
                this.mTimePeriodEndRealtime = parcel.readLong();
                this.mTimePeriodStartUptime = parcel.readLong();
                this.mTimePeriodEndUptime = parcel.readLong();
                this.mInternalSinglePssCount = parcel.readLong();
                this.mInternalSinglePssTime = parcel.readLong();
                this.mInternalAllMemPssCount = parcel.readLong();
                this.mInternalAllMemPssTime = parcel.readLong();
                this.mInternalAllPollPssCount = parcel.readLong();
                this.mInternalAllPollPssTime = parcel.readLong();
                this.mExternalPssCount = parcel.readLong();
                this.mExternalPssTime = parcel.readLong();
                this.mExternalSlowPssCount = parcel.readLong();
                this.mExternalSlowPssTime = parcel.readLong();
                this.mRuntime = parcel.readString();
                this.mHasSwappedOutPss = parcel.readInt() != 0;
                this.mFlags = parcel.readInt();
                this.mTableData.readFromParcel(parcel);
                readCompactedLongArray(parcel, i, this.mMemFactorDurations, this.mMemFactorDurations.length);
                if (this.mSysMemUsage.readFromParcel(parcel)) {
                    int i2 = parcel.readInt();
                    if (i2 < 0) {
                        this.mReadError = "bad process count: " + i2;
                        return;
                    }
                    while (i2 > 0) {
                        int i3 = i2 - 1;
                        String commonString = readCommonString(parcel, i);
                        if (commonString == null) {
                            this.mReadError = "bad process name";
                            return;
                        }
                        int i4 = parcel.readInt();
                        if (i4 < 0) {
                            this.mReadError = "bad uid count: " + i4;
                            return;
                        }
                        while (i4 > 0) {
                            int i5 = i4 - 1;
                            int i6 = parcel.readInt();
                            if (i6 < 0) {
                                this.mReadError = "bad uid: " + i6;
                                return;
                            }
                            String commonString2 = readCommonString(parcel, i);
                            if (commonString2 == null) {
                                this.mReadError = "bad process package name";
                                return;
                            }
                            long j = parcel.readLong();
                            ProcessState processState = z5 ? this.mProcesses.get(commonString, i6) : null;
                            if (processState == null) {
                                ProcessState processState2 = new ProcessState(this, commonString2, i6, j, commonString);
                                if (!processState2.readFromParcel(parcel, true)) {
                                    return;
                                } else {
                                    processState = processState2;
                                }
                            } else if (!processState.readFromParcel(parcel, z3)) {
                                return;
                            }
                            this.mProcesses.put(commonString, i6, processState);
                            i4 = i5;
                            z3 = false;
                        }
                        i2 = i3;
                    }
                    int i7 = parcel.readInt();
                    if (i7 < 0) {
                        this.mReadError = "bad package count: " + i7;
                        return;
                    }
                    while (i7 > 0) {
                        int i8 = i7 - 1;
                        String commonString3 = readCommonString(parcel, i);
                        if (commonString3 == null) {
                            this.mReadError = "bad package name";
                            return;
                        }
                        int i9 = parcel.readInt();
                        if (i9 < 0) {
                            this.mReadError = "bad uid count: " + i9;
                            return;
                        }
                        while (i9 > 0) {
                            int i10 = i9 - 1;
                            int i11 = parcel.readInt();
                            if (i11 < 0) {
                                this.mReadError = "bad uid: " + i11;
                                return;
                            }
                            int i12 = parcel.readInt();
                            if (i12 < 0) {
                                this.mReadError = "bad versions count: " + i12;
                                return;
                            }
                            while (i12 > 0) {
                                int i13 = i12 - 1;
                                long j2 = parcel.readLong();
                                PackageState packageState = new PackageState(commonString3, i11);
                                LongSparseArray<PackageState> longSparseArray = this.mPackages.get(commonString3, i11);
                                if (longSparseArray == null) {
                                    longSparseArray = new LongSparseArray<>();
                                    this.mPackages.put(commonString3, i11, longSparseArray);
                                }
                                longSparseArray.put(j2, packageState);
                                int i14 = parcel.readInt();
                                if (i14 < 0) {
                                    this.mReadError = "bad package process count: " + i14;
                                    return;
                                }
                                while (i14 > 0) {
                                    i14--;
                                    String commonString4 = readCommonString(parcel, i);
                                    if (commonString4 == null) {
                                        this.mReadError = "bad package process name";
                                        return;
                                    }
                                    int i15 = parcel.readInt();
                                    ProcessState processState3 = this.mProcesses.get(commonString4, i11);
                                    if (processState3 == null) {
                                        this.mReadError = "no common proc: " + commonString4;
                                        return;
                                    }
                                    if (i15 != 0) {
                                        ProcessState processState4 = z5 ? packageState.mProcesses.get(commonString4) : null;
                                        if (processState4 == null) {
                                            processState4 = new ProcessState(processState3, commonString3, i11, j2, commonString4, 0L);
                                            z2 = true;
                                            if (!processState4.readFromParcel(parcel, true)) {
                                                return;
                                            }
                                        } else if (!processState4.readFromParcel(parcel, false)) {
                                            return;
                                        } else {
                                            z2 = true;
                                        }
                                        packageState.mProcesses.put(commonString4, processState4);
                                        z = z2;
                                    } else {
                                        i8 = i8;
                                        z = true;
                                        packageState.mProcesses.put(commonString4, processState3);
                                    }
                                    z4 = z;
                                    i8 = i8;
                                }
                                int i16 = i8;
                                boolean z6 = z4;
                                int i17 = parcel.readInt();
                                if (i17 < 0) {
                                    this.mReadError = "bad package service count: " + i17;
                                    return;
                                }
                                while (i17 > 0) {
                                    int i18 = i17 - 1;
                                    String string = parcel.readString();
                                    if (string == null) {
                                        this.mReadError = "bad package service name";
                                        return;
                                    }
                                    String commonString5 = i > 9 ? readCommonString(parcel, i) : null;
                                    ServiceState serviceState = z5 ? packageState.mServices.get(string) : null;
                                    if (serviceState == null) {
                                        serviceState = new ServiceState(this, commonString3, string, commonString5, null);
                                    }
                                    if (!serviceState.readFromParcel(parcel)) {
                                        return;
                                    }
                                    packageState.mServices.put(string, serviceState);
                                    packageState = packageState;
                                    i17 = i18;
                                    commonString3 = commonString3;
                                    z6 = true;
                                }
                                z4 = z6;
                                i12 = i13;
                                i8 = i16;
                            }
                            i9 = i10;
                        }
                        i7 = i8;
                    }
                    int i19 = parcel.readInt();
                    this.mPageTypeZones.clear();
                    this.mPageTypeZones.ensureCapacity(i19);
                    this.mPageTypeLabels.clear();
                    this.mPageTypeLabels.ensureCapacity(i19);
                    this.mPageTypeSizes.clear();
                    this.mPageTypeSizes.ensureCapacity(i19);
                    for (int i20 = 0; i20 < i19; i20++) {
                        this.mPageTypeZones.add(Integer.valueOf(parcel.readInt()));
                        this.mPageTypeLabels.add(parcel.readString());
                        this.mPageTypeSizes.add(parcel.createIntArray());
                    }
                    this.mIndexToCommonString = null;
                }
            }
        }
    }

    public void reset() throws Throwable {
        resetCommon();
        this.mPackages.getMap().clear();
        this.mProcesses.getMap().clear();
        this.mMemFactor = -1;
        this.mStartTime = 0L;
    }

    public void resetSafely() throws Throwable {
        resetCommon();
        long jUptimeMillis = SystemClock.uptimeMillis();
        ArrayMap<String, SparseArray<ProcessState>> map = this.mProcesses.getMap();
        for (int size = map.size() - 1; size >= 0; size--) {
            SparseArray<ProcessState> sparseArrayValueAt = map.valueAt(size);
            for (int size2 = sparseArrayValueAt.size() - 1; size2 >= 0; size2--) {
                sparseArrayValueAt.valueAt(size2).tmpNumInUse = 0;
            }
        }
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map2 = this.mPackages.getMap();
        for (int size3 = map2.size() - 1; size3 >= 0; size3--) {
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt2 = map2.valueAt(size3);
            for (int size4 = sparseArrayValueAt2.size() - 1; size4 >= 0; size4--) {
                LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt2.valueAt(size4);
                for (int size5 = longSparseArrayValueAt.size() - 1; size5 >= 0; size5--) {
                    PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(size5);
                    for (int size6 = packageStateValueAt.mProcesses.size() - 1; size6 >= 0; size6--) {
                        ProcessState processStateValueAt = packageStateValueAt.mProcesses.valueAt(size6);
                        if (processStateValueAt.isInUse()) {
                            processStateValueAt.resetSafely(jUptimeMillis);
                            processStateValueAt.getCommonProcess().tmpNumInUse++;
                            processStateValueAt.getCommonProcess().tmpFoundSubProc = processStateValueAt;
                        } else {
                            packageStateValueAt.mProcesses.valueAt(size6).makeDead();
                            packageStateValueAt.mProcesses.removeAt(size6);
                        }
                    }
                    for (int size7 = packageStateValueAt.mServices.size() - 1; size7 >= 0; size7--) {
                        ServiceState serviceStateValueAt = packageStateValueAt.mServices.valueAt(size7);
                        if (serviceStateValueAt.isInUse()) {
                            serviceStateValueAt.resetSafely(jUptimeMillis);
                        } else {
                            packageStateValueAt.mServices.removeAt(size7);
                        }
                    }
                    if (packageStateValueAt.mProcesses.size() <= 0 && packageStateValueAt.mServices.size() <= 0) {
                        longSparseArrayValueAt.removeAt(size5);
                    }
                }
                if (longSparseArrayValueAt.size() <= 0) {
                    sparseArrayValueAt2.removeAt(size4);
                }
            }
            if (sparseArrayValueAt2.size() <= 0) {
                map2.removeAt(size3);
            }
        }
        for (int size8 = map.size() - 1; size8 >= 0; size8--) {
            SparseArray<ProcessState> sparseArrayValueAt3 = map.valueAt(size8);
            for (int size9 = sparseArrayValueAt3.size() - 1; size9 >= 0; size9--) {
                ProcessState processStateValueAt2 = sparseArrayValueAt3.valueAt(size9);
                if (!processStateValueAt2.isInUse() && processStateValueAt2.tmpNumInUse <= 0) {
                    processStateValueAt2.makeDead();
                    sparseArrayValueAt3.removeAt(size9);
                } else if (!processStateValueAt2.isActive() && processStateValueAt2.isMultiPackage() && processStateValueAt2.tmpNumInUse == 1) {
                    ProcessState processState = processStateValueAt2.tmpFoundSubProc;
                    processState.makeStandalone();
                    sparseArrayValueAt3.setValueAt(size9, processState);
                } else {
                    processStateValueAt2.resetSafely(jUptimeMillis);
                }
            }
            if (sparseArrayValueAt3.size() <= 0) {
                map.removeAt(size8);
            }
        }
        this.mStartTime = jUptimeMillis;
    }

    public void updateFragmentation() throws Throwable {
        BufferedReader bufferedReader;
        Integer numValueOf;
        BufferedReader bufferedReader2 = null;
        try {
            try {
                bufferedReader = new BufferedReader(new FileReader("/proc/pagetypeinfo"));
                try {
                    Matcher matcher = sPageTypeRegex.matcher("");
                    this.mPageTypeZones.clear();
                    this.mPageTypeLabels.clear();
                    this.mPageTypeSizes.clear();
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            try {
                                bufferedReader.close();
                                return;
                            } catch (IOException unused) {
                                return;
                            }
                        }
                        matcher.reset(line);
                        if (matcher.matches() && (numValueOf = Integer.valueOf(matcher.group(1), 10)) != null) {
                            this.mPageTypeZones.add(numValueOf);
                            this.mPageTypeLabels.add(matcher.group(2));
                            this.mPageTypeSizes.add(splitAndParseNumbers(matcher.group(3)));
                        }
                    }
                } catch (IOException unused2) {
                    bufferedReader2 = bufferedReader;
                    this.mPageTypeZones.clear();
                    this.mPageTypeLabels.clear();
                    this.mPageTypeSizes.clear();
                    if (bufferedReader2 != null) {
                        try {
                            bufferedReader2.close();
                        } catch (IOException unused3) {
                        }
                    }
                } catch (Throwable th) {
                    th = th;
                    if (bufferedReader != null) {
                        try {
                            bufferedReader.close();
                        } catch (IOException unused4) {
                        }
                    }
                    throw th;
                }
            } catch (IOException unused5) {
            }
        } catch (Throwable th2) {
            th = th2;
            bufferedReader = bufferedReader2;
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcel(parcel, SystemClock.uptimeMillis(), i);
    }

    public void writeToParcel(Parcel parcel, long j, int i) {
        parcel.writeInt(MAGIC);
        parcel.writeInt(27);
        parcel.writeInt(14);
        parcel.writeInt(8);
        parcel.writeInt(10);
        parcel.writeInt(16);
        parcel.writeInt(4096);
        this.mCommonStringToIndex = new ArrayMap<>(this.mProcesses.size());
        ArrayMap<String, SparseArray<ProcessState>> map = this.mProcesses.getMap();
        int size = map.size();
        for (int i2 = 0; i2 < size; i2++) {
            SparseArray<ProcessState> sparseArrayValueAt = map.valueAt(i2);
            int size2 = sparseArrayValueAt.size();
            for (int i3 = 0; i3 < size2; i3++) {
                sparseArrayValueAt.valueAt(i3).commitStateTime(j);
            }
        }
        ArrayMap<String, SparseArray<LongSparseArray<PackageState>>> map2 = this.mPackages.getMap();
        int size3 = map2.size();
        for (int i4 = 0; i4 < size3; i4++) {
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt2 = map2.valueAt(i4);
            int size4 = sparseArrayValueAt2.size();
            for (int i5 = 0; i5 < size4; i5++) {
                LongSparseArray<PackageState> longSparseArrayValueAt = sparseArrayValueAt2.valueAt(i5);
                int size5 = longSparseArrayValueAt.size();
                int i6 = 0;
                while (i6 < size5) {
                    PackageState packageStateValueAt = longSparseArrayValueAt.valueAt(i6);
                    SparseArray<LongSparseArray<PackageState>> sparseArray = sparseArrayValueAt2;
                    int size6 = packageStateValueAt.mProcesses.size();
                    int i7 = size4;
                    int i8 = 0;
                    while (i8 < size6) {
                        int i9 = size6;
                        ProcessState processStateValueAt = packageStateValueAt.mProcesses.valueAt(i8);
                        LongSparseArray<PackageState> longSparseArray = longSparseArrayValueAt;
                        if (processStateValueAt.getCommonProcess() != processStateValueAt) {
                            processStateValueAt.commitStateTime(j);
                        }
                        i8++;
                        size6 = i9;
                        longSparseArrayValueAt = longSparseArray;
                    }
                    LongSparseArray<PackageState> longSparseArray2 = longSparseArrayValueAt;
                    int size7 = packageStateValueAt.mServices.size();
                    for (int i10 = 0; i10 < size7; i10++) {
                        packageStateValueAt.mServices.valueAt(i10).commitStateTime(j);
                    }
                    i6++;
                    sparseArrayValueAt2 = sparseArray;
                    size4 = i7;
                    longSparseArrayValueAt = longSparseArray2;
                }
            }
        }
        parcel.writeLong(this.mTimePeriodStartClock);
        parcel.writeLong(this.mTimePeriodStartRealtime);
        parcel.writeLong(this.mTimePeriodEndRealtime);
        parcel.writeLong(this.mTimePeriodStartUptime);
        parcel.writeLong(this.mTimePeriodEndUptime);
        parcel.writeLong(this.mInternalSinglePssCount);
        parcel.writeLong(this.mInternalSinglePssTime);
        parcel.writeLong(this.mInternalAllMemPssCount);
        parcel.writeLong(this.mInternalAllMemPssTime);
        parcel.writeLong(this.mInternalAllPollPssCount);
        parcel.writeLong(this.mInternalAllPollPssTime);
        parcel.writeLong(this.mExternalPssCount);
        parcel.writeLong(this.mExternalPssTime);
        parcel.writeLong(this.mExternalSlowPssCount);
        parcel.writeLong(this.mExternalSlowPssTime);
        parcel.writeString(this.mRuntime);
        parcel.writeInt(this.mHasSwappedOutPss ? 1 : 0);
        parcel.writeInt(this.mFlags);
        this.mTableData.writeToParcel(parcel);
        if (this.mMemFactor != -1) {
            long[] jArr = this.mMemFactorDurations;
            int i11 = this.mMemFactor;
            jArr[i11] = jArr[i11] + (j - this.mStartTime);
            this.mStartTime = j;
        }
        writeCompactedLongArray(parcel, this.mMemFactorDurations, this.mMemFactorDurations.length);
        this.mSysMemUsage.writeToParcel(parcel);
        parcel.writeInt(size);
        for (int i12 = 0; i12 < size; i12++) {
            writeCommonString(parcel, map.keyAt(i12));
            SparseArray<ProcessState> sparseArrayValueAt3 = map.valueAt(i12);
            int size8 = sparseArrayValueAt3.size();
            parcel.writeInt(size8);
            for (int i13 = 0; i13 < size8; i13++) {
                parcel.writeInt(sparseArrayValueAt3.keyAt(i13));
                ProcessState processStateValueAt2 = sparseArrayValueAt3.valueAt(i13);
                writeCommonString(parcel, processStateValueAt2.getPackage());
                parcel.writeLong(processStateValueAt2.getVersion());
                processStateValueAt2.writeToParcel(parcel, j);
            }
        }
        parcel.writeInt(size3);
        for (int i14 = 0; i14 < size3; i14++) {
            writeCommonString(parcel, map2.keyAt(i14));
            SparseArray<LongSparseArray<PackageState>> sparseArrayValueAt4 = map2.valueAt(i14);
            int size9 = sparseArrayValueAt4.size();
            parcel.writeInt(size9);
            for (int i15 = 0; i15 < size9; i15++) {
                parcel.writeInt(sparseArrayValueAt4.keyAt(i15));
                LongSparseArray<PackageState> longSparseArrayValueAt2 = sparseArrayValueAt4.valueAt(i15);
                int size10 = longSparseArrayValueAt2.size();
                parcel.writeInt(size10);
                int i16 = 0;
                while (i16 < size10) {
                    parcel.writeLong(longSparseArrayValueAt2.keyAt(i16));
                    PackageState packageStateValueAt2 = longSparseArrayValueAt2.valueAt(i16);
                    int size11 = packageStateValueAt2.mProcesses.size();
                    parcel.writeInt(size11);
                    int i17 = 0;
                    while (i17 < size11) {
                        SparseArray<LongSparseArray<PackageState>> sparseArray2 = sparseArrayValueAt4;
                        writeCommonString(parcel, packageStateValueAt2.mProcesses.keyAt(i17));
                        ProcessState processStateValueAt3 = packageStateValueAt2.mProcesses.valueAt(i17);
                        int i18 = size9;
                        if (processStateValueAt3.getCommonProcess() == processStateValueAt3) {
                            parcel.writeInt(0);
                        } else {
                            parcel.writeInt(1);
                            processStateValueAt3.writeToParcel(parcel, j);
                        }
                        i17++;
                        sparseArrayValueAt4 = sparseArray2;
                        size9 = i18;
                    }
                    SparseArray<LongSparseArray<PackageState>> sparseArray3 = sparseArrayValueAt4;
                    int i19 = size9;
                    int size12 = packageStateValueAt2.mServices.size();
                    parcel.writeInt(size12);
                    for (int i20 = 0; i20 < size12; i20++) {
                        parcel.writeString(packageStateValueAt2.mServices.keyAt(i20));
                        ServiceState serviceStateValueAt = packageStateValueAt2.mServices.valueAt(i20);
                        writeCommonString(parcel, serviceStateValueAt.getProcessName());
                        serviceStateValueAt.writeToParcel(parcel, j);
                    }
                    i16++;
                    sparseArrayValueAt4 = sparseArray3;
                    size9 = i19;
                }
            }
        }
        int size13 = this.mPageTypeLabels.size();
        parcel.writeInt(size13);
        for (int i21 = 0; i21 < size13; i21++) {
            parcel.writeInt(this.mPageTypeZones.get(i21).intValue());
            parcel.writeString(this.mPageTypeLabels.get(i21));
            parcel.writeIntArray(this.mPageTypeSizes.get(i21));
        }
        this.mCommonStringToIndex = null;
    }

    public void writeToProto(ProtoOutputStream protoOutputStream, long j, long j2) {
        boolean z;
        this.mPackages.getMap();
        long jStart = protoOutputStream.start(j);
        protoOutputStream.write(1112396529665L, this.mTimePeriodStartRealtime);
        protoOutputStream.write(1112396529666L, this.mRunning ? SystemClock.elapsedRealtime() : this.mTimePeriodEndRealtime);
        protoOutputStream.write(1112396529667L, this.mTimePeriodStartUptime);
        protoOutputStream.write(1112396529668L, this.mTimePeriodEndUptime);
        protoOutputStream.write(1138166333445L, this.mRuntime);
        protoOutputStream.write(1133871366150L, this.mHasSwappedOutPss);
        if ((this.mFlags & 2) != 0) {
            protoOutputStream.write(2259152797703L, 3);
            z = false;
        } else {
            z = true;
        }
        if ((this.mFlags & 4) != 0) {
            protoOutputStream.write(2259152797703L, 4);
            z = false;
        }
        if ((this.mFlags & 1) != 0) {
            protoOutputStream.write(2259152797703L, 1);
            z = false;
        }
        if (z) {
            protoOutputStream.write(2259152797703L, 2);
        }
        ArrayMap<String, SparseArray<ProcessState>> map = this.mProcesses.getMap();
        for (int i = 0; i < map.size(); i++) {
            String strKeyAt = map.keyAt(i);
            SparseArray<ProcessState> sparseArrayValueAt = map.valueAt(i);
            for (int i2 = 0; i2 < sparseArrayValueAt.size(); i2++) {
                sparseArrayValueAt.valueAt(i2).writeToProto(protoOutputStream, 2246267895816L, strKeyAt, sparseArrayValueAt.keyAt(i2), j2);
            }
        }
        protoOutputStream.end(jStart);
    }
}
