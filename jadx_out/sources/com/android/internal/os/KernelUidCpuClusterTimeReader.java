package com.android.internal.os;

import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.function.Consumer;

/* JADX INFO: loaded from: classes3.dex */
public class KernelUidCpuClusterTimeReader extends KernelUidCpuTimeReaderBase<Callback> {
    private static final String TAG = "KernelUidCpuClusterTimeReader";
    private double[] mCurTime;
    private long[] mCurTimeRounded;
    private long[] mDeltaTime;
    private SparseArray<double[]> mLastUidPolicyTimeMs;
    private int mNumClusters;
    private int mNumCores;
    private int[] mNumCoresOnCluster;
    private final KernelCpuProcReader mProcReader;

    public interface Callback extends KernelUidCpuTimeReaderBase.Callback {
        void onUidCpuPolicyTime(int i, long[] jArr);
    }

    public KernelUidCpuClusterTimeReader() {
        this.mLastUidPolicyTimeMs = new SparseArray<>();
        this.mNumClusters = -1;
        this.mProcReader = KernelCpuProcReader.getClusterTimeReaderInstance();
    }

    @VisibleForTesting
    public KernelUidCpuClusterTimeReader(KernelCpuProcReader kernelCpuProcReader) {
        this.mLastUidPolicyTimeMs = new SparseArray<>();
        this.mNumClusters = -1;
        this.mProcReader = kernelCpuProcReader;
    }

    public static /* synthetic */ void lambda$readAbsolute$1(KernelUidCpuClusterTimeReader kernelUidCpuClusterTimeReader, Callback callback, IntBuffer intBuffer) {
        int i = intBuffer.get();
        if (kernelUidCpuClusterTimeReader.sumClusterTime(intBuffer, kernelUidCpuClusterTimeReader.mCurTime)) {
            for (int i2 = 0; i2 < kernelUidCpuClusterTimeReader.mNumClusters; i2++) {
                kernelUidCpuClusterTimeReader.mCurTimeRounded[i2] = (long) kernelUidCpuClusterTimeReader.mCurTime[i2];
            }
            callback.onUidCpuPolicyTime(i, kernelUidCpuClusterTimeReader.mCurTimeRounded);
        }
    }

    public static /* synthetic */ void lambda$readDeltaImpl$0(KernelUidCpuClusterTimeReader kernelUidCpuClusterTimeReader, Callback callback, IntBuffer intBuffer) {
        int i = intBuffer.get();
        double[] dArr = kernelUidCpuClusterTimeReader.mLastUidPolicyTimeMs.get(i);
        if (dArr == null) {
            dArr = new double[kernelUidCpuClusterTimeReader.mNumClusters];
            kernelUidCpuClusterTimeReader.mLastUidPolicyTimeMs.put(i, dArr);
        }
        if (kernelUidCpuClusterTimeReader.sumClusterTime(intBuffer, kernelUidCpuClusterTimeReader.mCurTime)) {
            boolean z = true;
            boolean z2 = false;
            for (int i2 = 0; i2 < kernelUidCpuClusterTimeReader.mNumClusters; i2++) {
                kernelUidCpuClusterTimeReader.mDeltaTime[i2] = (long) (kernelUidCpuClusterTimeReader.mCurTime[i2] - dArr[i2]);
                if (kernelUidCpuClusterTimeReader.mDeltaTime[i2] < 0) {
                    Slog.e(TAG, "Negative delta from cluster time proc: " + kernelUidCpuClusterTimeReader.mDeltaTime[i2]);
                    z = false;
                }
                z2 |= kernelUidCpuClusterTimeReader.mDeltaTime[i2] > 0;
            }
            if (z2 && z) {
                System.arraycopy(kernelUidCpuClusterTimeReader.mCurTime, 0, dArr, 0, kernelUidCpuClusterTimeReader.mNumClusters);
                if (callback != null) {
                    callback.onUidCpuPolicyTime(i, kernelUidCpuClusterTimeReader.mDeltaTime);
                }
            }
        }
    }

    private boolean readCoreInfo(IntBuffer intBuffer, int i) {
        int[] iArr = new int[i];
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            iArr[i3] = intBuffer.get();
            i2 += iArr[i3];
        }
        if (i2 <= 0) {
            Slog.e(TAG, "Invalid # cores from cluster time proc file: " + i2);
            return false;
        }
        this.mNumCores = i2;
        this.mNumCoresOnCluster = iArr;
        this.mCurTime = new double[i];
        this.mDeltaTime = new long[i];
        this.mCurTimeRounded = new long[i];
        return true;
    }

    private void readImpl(Consumer<IntBuffer> consumer) {
        synchronized (this.mProcReader) {
            ByteBuffer bytes = this.mProcReader.readBytes();
            if (bytes != null && bytes.remaining() > 4) {
                if ((bytes.remaining() & 3) != 0) {
                    Slog.wtf(TAG, "Cannot parse cluster time proc bytes to int: " + bytes.remaining());
                    return;
                }
                IntBuffer intBufferAsIntBuffer = bytes.asIntBuffer();
                int i = intBufferAsIntBuffer.get();
                if (i <= 0) {
                    Slog.wtf(TAG, "Cluster time format error: " + i);
                    return;
                }
                if (this.mNumClusters == -1) {
                    this.mNumClusters = i;
                }
                if (intBufferAsIntBuffer.remaining() < i) {
                    Slog.wtf(TAG, "Too few data left in the buffer: " + intBufferAsIntBuffer.remaining());
                    return;
                }
                if (this.mNumCores > 0) {
                    intBufferAsIntBuffer.position(intBufferAsIntBuffer.position() + i);
                } else if (!readCoreInfo(intBufferAsIntBuffer, i)) {
                    return;
                }
                if (intBufferAsIntBuffer.remaining() % (this.mNumCores + 1) == 0) {
                    int iRemaining = intBufferAsIntBuffer.remaining() / (this.mNumCores + 1);
                    for (int i2 = 0; i2 < iRemaining; i2++) {
                        consumer.accept(intBufferAsIntBuffer);
                    }
                    return;
                }
                Slog.wtf(TAG, "Cluster time format error: " + intBufferAsIntBuffer.remaining() + " / " + (this.mNumCores + 1));
            }
        }
    }

    private boolean sumClusterTime(IntBuffer intBuffer, double[] dArr) {
        int i = 0;
        boolean z = true;
        while (i < this.mNumClusters) {
            dArr[i] = 0.0d;
            boolean z2 = z;
            for (int i2 = 1; i2 <= this.mNumCoresOnCluster[i]; i2++) {
                int i3 = intBuffer.get();
                if (i3 < 0) {
                    Slog.e(TAG, "Negative time from cluster time proc: " + i3);
                    z2 = false;
                }
                dArr[i] = dArr[i] + ((((double) i3) * 10.0d) / ((double) i2));
            }
            i++;
            z = z2;
        }
        return z;
    }

    public void readAbsolute(final Callback callback) {
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuClusterTimeReader$SvNbuRWT162Eb4ur1GVE0r4GiDo
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KernelUidCpuClusterTimeReader.lambda$readAbsolute$1(this.f$0, callback, (IntBuffer) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.android.internal.os.KernelUidCpuTimeReaderBase
    public void readDeltaImpl(final Callback callback) {
        readImpl(new Consumer() { // from class: com.android.internal.os.-$$Lambda$KernelUidCpuClusterTimeReader$j4vHMa0qvl5KRBiWr-LkFJbasC8
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                KernelUidCpuClusterTimeReader.lambda$readDeltaImpl$0(this.f$0, callback, (IntBuffer) obj);
            }
        });
    }

    public void removeUid(int i) {
        this.mLastUidPolicyTimeMs.delete(i);
    }

    public void removeUidsInRange(int i, int i2) {
        this.mLastUidPolicyTimeMs.put(i, null);
        this.mLastUidPolicyTimeMs.put(i2, null);
        int iIndexOfKey = this.mLastUidPolicyTimeMs.indexOfKey(i);
        this.mLastUidPolicyTimeMs.removeAtRange(iIndexOfKey, (this.mLastUidPolicyTimeMs.indexOfKey(i2) - iIndexOfKey) + 1);
    }
}
