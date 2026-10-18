package com.facebook.imagepipeline.memory;

import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.memory.MemoryTrimmableRegistry;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.infer.annotation.Nullsafe;
import java.util.Map;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@Nullsafe(Nullsafe.Mode.LOCAL)
public class FlexByteArrayPool {

    @VisibleForTesting
    final SoftRefByteArrayPool mDelegatePool;
    private final ResourceReleaser<byte[]> mResourceReleaser;

    @VisibleForTesting
    static class SoftRefByteArrayPool extends GenericByteArrayPool {
        public SoftRefByteArrayPool(MemoryTrimmableRegistry memoryTrimmableRegistry, PoolParams poolParams, PoolStatsTracker poolStatsTracker) {
            super(memoryTrimmableRegistry, poolParams, poolStatsTracker);
        }

        @Override // com.facebook.imagepipeline.memory.BasePool
        Bucket<byte[]> newBucket(int i) {
            return new OOMSoftReferenceBucket(getSizeInBytes(i), this.mPoolParams.maxNumThreads, 0);
        }
    }

    public FlexByteArrayPool(MemoryTrimmableRegistry memoryTrimmableRegistry, PoolParams poolParams) {
        Preconditions.checkArgument(Boolean.valueOf(poolParams.maxNumThreads > 0));
        this.mDelegatePool = new SoftRefByteArrayPool(memoryTrimmableRegistry, poolParams, NoOpPoolStatsTracker.getInstance());
        this.mResourceReleaser = new ResourceReleaser<byte[]>() { // from class: com.facebook.imagepipeline.memory.FlexByteArrayPool.1
            @Override // com.facebook.common.references.ResourceReleaser
            public void release(byte[] bArr) {
                FlexByteArrayPool.this.release(bArr);
            }
        };
    }

    public CloseableReference<byte[]> get(int i) {
        return CloseableReference.of(this.mDelegatePool.get(i), this.mResourceReleaser);
    }

    public int getMinBufferSize() {
        return this.mDelegatePool.getMinBufferSize();
    }

    public Map<String, Integer> getStats() {
        return this.mDelegatePool.getStats();
    }

    public void release(byte[] bArr) {
        this.mDelegatePool.release(bArr);
    }
}
