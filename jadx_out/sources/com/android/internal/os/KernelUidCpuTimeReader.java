package com.android.internal.os;

import android.os.StrictMode;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import com.android.internal.content.NativeLibraryHelper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public class KernelUidCpuTimeReader extends KernelUidCpuTimeReaderBase<Callback> {
    private static final String TAG = "KernelUidCpuTimeReader";
    private static final String sProcFile = "/proc/uid_cputime/show_uid_stat";
    private static final String sRemoveUidProcFile = "/proc/uid_cputime/remove_uid_range";
    private SparseLongArray mLastUserTimeUs = new SparseLongArray();
    private SparseLongArray mLastSystemTimeUs = new SparseLongArray();
    private long mLastTimeReadUs = 0;

    public interface Callback extends KernelUidCpuTimeReaderBase.Callback {
        void onUidCpuTime(int i, long j, long j2);
    }

    private static /* synthetic */ void $closeResource(Throwable th, AutoCloseable autoCloseable) throws Exception {
        if (th == null) {
            autoCloseable.close();
            return;
        }
        try {
            autoCloseable.close();
        } catch (Throwable th2) {
            th.addSuppressed(th2);
        }
    }

    private void removeUidsFromKernelModule(int i, int i2) {
        Slog.d(TAG, "Removing uids " + i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + i2);
        int iAllowThreadDiskWritesMask = StrictMode.allowThreadDiskWritesMask();
        try {
            try {
                FileWriter fileWriter = new FileWriter(sRemoveUidProcFile);
                Throwable th = null;
                try {
                    fileWriter.write(i + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + i2);
                    fileWriter.flush();
                    $closeResource(null, fileWriter);
                } catch (Throwable th2) {
                    $closeResource(th, fileWriter);
                    throw th2;
                }
            } catch (Throwable th3) {
                StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
                throw th3;
            }
        } catch (IOException e) {
            Slog.e(TAG, "failed to remove uids " + i + " - " + i2 + " from uid_cputime module", e);
        }
        StrictMode.setThreadPolicyMask(iAllowThreadDiskWritesMask);
    }

    public void readAbsolute(Callback callback) {
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(sProcFile));
                Throwable th = null;
                try {
                    try {
                        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(' ');
                        while (true) {
                            String line = bufferedReader.readLine();
                            if (line == null) {
                                break;
                            }
                            simpleStringSplitter.setString(line);
                            String next = simpleStringSplitter.next();
                            callback.onUidCpuTime(Integer.parseInt(next.substring(0, next.length() - 1), 10), Long.parseLong(simpleStringSplitter.next(), 10), Long.parseLong(simpleStringSplitter.next(), 10));
                            $closeResource(th, bufferedReader);
                            throw th;
                        }
                        $closeResource(null, bufferedReader);
                        StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    } catch (Throwable th2) {
                        th = th2;
                        throw th;
                    }
                } catch (Throwable th3) {
                    $closeResource(th, bufferedReader);
                    throw th3;
                }
            } catch (IOException e) {
                Slog.e(TAG, "Failed to read uid_cputime: " + e.getMessage());
            }
        } catch (Throwable th4) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            throw th4;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code duplicated, block: B:34:0x0134 A[Catch: all -> 0x0142, Throwable -> 0x0145, TRY_LEAVE, TryCatch #5 {all -> 0x0142, Throwable -> 0x0145, blocks: (B:4:0x0019, B:5:0x0020, B:7:0x0026, B:9:0x0050, B:11:0x0058, B:13:0x0060, B:32:0x0126, B:34:0x0134, B:19:0x0083), top: B:60:0x0019 }] */
    /* JADX WARN: Code duplicated, block: B:64:0x0139 A[SYNTHETIC] */
    @Override // com.android.internal.os.KernelUidCpuTimeReaderBase
    public void readDeltaImpl(Callback callback) {
        Throwable th;
        long j;
        long j2;
        long j3;
        boolean z;
        long jValueAt;
        long jValueAt2;
        int iAllowThreadDiskReadsMask = StrictMode.allowThreadDiskReadsMask();
        long j4 = 1000;
        long jElapsedRealtime = SystemClock.elapsedRealtime() * 1000;
        try {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(sProcFile));
                try {
                    TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(' ');
                    while (true) {
                        String line = bufferedReader.readLine();
                        if (line == null) {
                            break;
                        }
                        simpleStringSplitter.setString(line);
                        String next = simpleStringSplitter.next();
                        int i = Integer.parseInt(next.substring(0, next.length() - 1), 10);
                        long j5 = Long.parseLong(simpleStringSplitter.next(), 10);
                        long j6 = Long.parseLong(simpleStringSplitter.next(), 10);
                        if (callback != null) {
                            if (this.mLastTimeReadUs != 0) {
                                int iIndexOfKey = this.mLastUserTimeUs.indexOfKey(i);
                                if (iIndexOfKey >= 0) {
                                    jValueAt = j5 - this.mLastUserTimeUs.valueAt(iIndexOfKey);
                                    jValueAt2 = j6 - this.mLastSystemTimeUs.valueAt(iIndexOfKey);
                                    j6 = j6;
                                    long j7 = jElapsedRealtime - this.mLastTimeReadUs;
                                    if (jValueAt < 0 || jValueAt2 < 0) {
                                        StringBuilder sb = new StringBuilder("Malformed cpu data for UID=");
                                        sb.append(i);
                                        sb.append("!\n");
                                        sb.append("Time between reads: ");
                                        TimeUtils.formatDuration(j7 / 1000, sb);
                                        sb.append("\n");
                                        sb.append("Previous times: u=");
                                        TimeUtils.formatDuration(this.mLastUserTimeUs.valueAt(iIndexOfKey) / 1000, sb);
                                        sb.append(" s=");
                                        j = 1000;
                                        TimeUtils.formatDuration(this.mLastSystemTimeUs.valueAt(iIndexOfKey) / 1000, sb);
                                        sb.append("\nCurrent times: u=");
                                        TimeUtils.formatDuration(j5 / 1000, sb);
                                        sb.append(" s=");
                                        TimeUtils.formatDuration(j6 / 1000, sb);
                                        sb.append("\nDelta: u=");
                                        TimeUtils.formatDuration(jValueAt / 1000, sb);
                                        sb.append(" s=");
                                        TimeUtils.formatDuration(jValueAt2 / 1000, sb);
                                        Slog.e(TAG, sb.toString());
                                        jValueAt = 0;
                                        jValueAt2 = 0;
                                    } else {
                                        j = 1000;
                                    }
                                } else {
                                    j6 = j6;
                                    j = 1000;
                                    jValueAt = j5;
                                    jValueAt2 = j6;
                                }
                                z = (jValueAt == 0 && jValueAt2 == 0) ? false : true;
                                j2 = jValueAt;
                                j3 = jValueAt2;
                            } else {
                                j = 1000;
                            }
                            this.mLastUserTimeUs.put(i, j5);
                            this.mLastSystemTimeUs.put(i, j6);
                            if (z) {
                                callback.onUidCpuTime(i, j2, j3);
                            }
                            j4 = j;
                        } else {
                            j = j4;
                        }
                        j2 = j5;
                        j3 = j6;
                        z = false;
                        this.mLastUserTimeUs.put(i, j5);
                        this.mLastSystemTimeUs.put(i, j6);
                        if (z) {
                            callback.onUidCpuTime(i, j2, j3);
                        }
                        j4 = j;
                    }
                    $closeResource(null, bufferedReader);
                    StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
                    this.mLastTimeReadUs = jElapsedRealtime;
                } catch (Throwable th2) {
                    try {
                        throw th2;
                    } catch (Throwable th3) {
                        th = th3;
                        th = th2;
                        $closeResource(th, bufferedReader);
                        throw th;
                    }
                }
            } catch (IOException e) {
                Slog.e(TAG, "Failed to read uid_cputime: " + e.getMessage());
            }
        } catch (Throwable th4) {
            StrictMode.setThreadPolicyMask(iAllowThreadDiskReadsMask);
            throw th4;
        }
    }

    public void removeUid(int i) {
        int iIndexOfKey = this.mLastSystemTimeUs.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            this.mLastSystemTimeUs.removeAt(iIndexOfKey);
            this.mLastUserTimeUs.removeAt(iIndexOfKey);
        }
        removeUidsFromKernelModule(i, i);
    }

    public void removeUidsInRange(int i, int i2) {
        if (i2 < i) {
            return;
        }
        this.mLastSystemTimeUs.put(i, 0L);
        this.mLastUserTimeUs.put(i, 0L);
        this.mLastSystemTimeUs.put(i2, 0L);
        this.mLastUserTimeUs.put(i2, 0L);
        int iIndexOfKey = this.mLastSystemTimeUs.indexOfKey(i);
        int iIndexOfKey2 = (this.mLastSystemTimeUs.indexOfKey(i2) - iIndexOfKey) + 1;
        this.mLastSystemTimeUs.removeAtRange(iIndexOfKey, iIndexOfKey2);
        this.mLastUserTimeUs.removeAtRange(iIndexOfKey, iIndexOfKey2);
        removeUidsFromKernelModule(i, i2);
    }
}
