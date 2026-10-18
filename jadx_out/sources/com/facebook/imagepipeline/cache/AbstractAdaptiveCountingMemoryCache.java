package com.facebook.imagepipeline.cache;

import android.graphics.Bitmap;
import android.os.SystemClock;
import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Objects;
import com.facebook.common.internal.Preconditions;
import com.facebook.common.internal.Predicate;
import com.facebook.common.internal.Supplier;
import com.facebook.common.logging.FLog;
import com.facebook.common.memory.MemoryTrimType;
import com.facebook.common.references.CloseableReference;
import com.facebook.common.references.ResourceReleaser;
import com.facebook.infer.annotation.Nullsafe;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@Nullsafe(Nullsafe.Mode.STRICT)
public abstract class AbstractAdaptiveCountingMemoryCache<K, V> implements CountingMemoryCache<K, V> {
    static final int DEFAULT_ADAPTIVE_RATE_PROMIL = 10;
    static final int DEFAULT_LFU_FRACTION_PROMIL = 500;
    static final int MAX_FRACTION_PROMIL = 900;

    @VisibleForTesting
    static final int MIN_FRACTION_PROMIL = 100;
    private static final String TAG = "AbstractArcCountingMemoryCache";
    static final int TOTAL_PROMIL = 1000;

    @GuardedBy("this")
    @VisibleForTesting
    final int mAdaptiveRatePromil;
    private final MemoryCache.CacheTrimStrategy mCacheTrimStrategy;

    @GuardedBy("this")
    @VisibleForTesting
    final CountingLruMap<K, Entry<K, V>> mCachedEntries;
    private final int mFrequentlyUsedThreshold;

    @GuardedBy("this")
    @VisibleForTesting
    final int mGhostListMaxSize;

    @GuardedBy("this")
    @VisibleForTesting
    int mLFUFractionPromil;

    @GuardedBy("this")
    private long mLastCacheParamsCheck;

    @GuardedBy("this")
    @VisibleForTesting
    final CountingLruMap<K, Entry<K, V>> mLeastFrequentlyUsedExclusiveEntries;

    @GuardedBy("this")
    @VisibleForTesting
    final AbstractAdaptiveCountingMemoryCache<K, V>.IntMapArrayList<K> mLeastFrequentlyUsedKeysGhostList;

    @GuardedBy("this")
    protected MemoryCacheParams mMemoryCacheParams;
    private final Supplier<MemoryCacheParams> mMemoryCacheParamsSupplier;

    @GuardedBy("this")
    @VisibleForTesting
    final CountingLruMap<K, Entry<K, V>> mMostFrequentlyUsedExclusiveEntries;

    @GuardedBy("this")
    @VisibleForTesting
    final ArrayList<K> mMostFrequentlyUsedKeysGhostList;
    private final ValueDescriptor<V> mValueDescriptor;

    enum ArrayListType {
        LFU,
        MFU
    }

    @VisibleForTesting
    static class Entry<K, V> {
        public final K key;

        @Nullable
        public final CountingMemoryCache.EntryStateObserver<K> observer;
        public final CloseableReference<V> valueRef;
        public int clientCount = 0;
        public boolean isOrphan = false;
        public int accessCount = 0;

        private Entry(K k, CloseableReference<V> closeableReference, @Nullable CountingMemoryCache.EntryStateObserver<K> entryStateObserver) {
            this.key = (K) Preconditions.checkNotNull(k);
            this.valueRef = (CloseableReference) Preconditions.checkNotNull(CloseableReference.cloneOrNull(closeableReference));
            this.observer = entryStateObserver;
        }

        @VisibleForTesting
        static <K, V> Entry<K, V> of(K k, CloseableReference<V> closeableReference, @Nullable CountingMemoryCache.EntryStateObserver<K> entryStateObserver) {
            return new Entry<>(k, closeableReference, entryStateObserver);
        }
    }

    @VisibleForTesting
    class IntMapArrayList<E> {
        private final ArrayList<E> mFirstList;
        private final int mMaxCapacity;
        private final ArrayList<Integer> mSecondList;

        public IntMapArrayList(int i) {
            this.mFirstList = new ArrayList<>(i);
            this.mSecondList = new ArrayList<>(i);
            this.mMaxCapacity = i;
        }

        public void addPair(E e, Integer num) {
            if (this.mFirstList.size() == this.mMaxCapacity) {
                this.mFirstList.remove(0);
                this.mSecondList.remove(0);
            }
            this.mFirstList.add(e);
            this.mSecondList.add(num);
        }

        public boolean contains(E e) {
            return this.mFirstList.contains(e);
        }

        @Nullable
        public Integer getValue(E e) {
            int iIndexOf = this.mFirstList.indexOf(e);
            if (iIndexOf < 0) {
                return null;
            }
            return this.mSecondList.get(iIndexOf);
        }

        public void increaseValueIfExists(E e) {
            int iIndexOf = this.mFirstList.indexOf(e);
            if (iIndexOf < 0) {
                return;
            }
            Integer numValueOf = Integer.valueOf(this.mSecondList.get(iIndexOf).intValue() + 1);
            if (iIndexOf == this.mMaxCapacity - 1) {
                this.mSecondList.set(this.mMaxCapacity - 1, numValueOf);
                return;
            }
            this.mFirstList.remove(iIndexOf);
            this.mSecondList.remove(iIndexOf);
            this.mFirstList.add(e);
            this.mSecondList.add(numValueOf);
        }

        public int size() {
            return this.mFirstList.size();
        }
    }

    public AbstractAdaptiveCountingMemoryCache(Supplier<MemoryCacheParams> supplier, MemoryCache.CacheTrimStrategy cacheTrimStrategy, ValueDescriptor<V> valueDescriptor, int i, int i2, int i3, int i4) {
        FLog.d(TAG, "Create Adaptive Replacement Cache");
        this.mValueDescriptor = valueDescriptor;
        this.mLeastFrequentlyUsedExclusiveEntries = new CountingLruMap<>(wrapValueDescriptor(valueDescriptor));
        this.mMostFrequentlyUsedExclusiveEntries = new CountingLruMap<>(wrapValueDescriptor(valueDescriptor));
        this.mCachedEntries = new CountingLruMap<>(wrapValueDescriptor(valueDescriptor));
        this.mCacheTrimStrategy = cacheTrimStrategy;
        this.mMemoryCacheParamsSupplier = supplier;
        this.mMemoryCacheParams = (MemoryCacheParams) Preconditions.checkNotNull(this.mMemoryCacheParamsSupplier.get(), "mMemoryCacheParamsSupplier returned null");
        this.mLastCacheParamsCheck = SystemClock.uptimeMillis();
        this.mFrequentlyUsedThreshold = i2;
        this.mGhostListMaxSize = i3;
        this.mLeastFrequentlyUsedKeysGhostList = new IntMapArrayList<>(this.mGhostListMaxSize);
        this.mMostFrequentlyUsedKeysGhostList = new ArrayList<>(this.mGhostListMaxSize);
        if (i4 < 100 || i4 > 900) {
            this.mLFUFractionPromil = 500;
            logIllegalLfuFraction();
        } else {
            this.mLFUFractionPromil = i4;
        }
        if (i > 0 && i < 1000) {
            this.mAdaptiveRatePromil = i;
        } else {
            this.mAdaptiveRatePromil = 10;
            logIllegalAdaptiveRate();
        }
    }

    private synchronized void addElementToGhostList(K k, int i, ArrayListType arrayListType) {
        if (arrayListType == ArrayListType.LFU) {
            this.mLeastFrequentlyUsedKeysGhostList.addPair(k, Integer.valueOf(i));
        } else {
            if (this.mMostFrequentlyUsedKeysGhostList.size() == this.mGhostListMaxSize) {
                this.mMostFrequentlyUsedKeysGhostList.remove(0);
            }
            this.mMostFrequentlyUsedKeysGhostList.add(k);
        }
    }

    private synchronized boolean canCacheNewValue(V v) {
        int sizeInBytes;
        sizeInBytes = this.mValueDescriptor.getSizeInBytes(v);
        return sizeInBytes <= this.mMemoryCacheParams.maxCacheEntrySize && getInUseCount() <= this.mMemoryCacheParams.maxCacheEntries - 1 && getInUseSizeInBytes() <= this.mMemoryCacheParams.maxCacheSize - sizeInBytes;
    }

    private synchronized void decreaseClientCount(Entry<K, V> entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(entry.clientCount > 0);
        entry.clientCount--;
    }

    private synchronized void increaseAccessCount(Entry<K, V> entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(!entry.isOrphan);
        entry.accessCount++;
    }

    private synchronized void increaseCounters(Entry<K, V> entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(!entry.isOrphan);
        entry.clientCount++;
        increaseAccessCount(entry);
    }

    private synchronized void makeOrphan(Entry<K, V> entry) {
        Preconditions.checkNotNull(entry);
        Preconditions.checkState(!entry.isOrphan);
        entry.isOrphan = true;
    }

    private synchronized void makeOrphans(@Nullable ArrayList<Entry<K, V>> arrayList) {
        if (arrayList != null) {
            Iterator<Entry<K, V>> it = arrayList.iterator();
            while (it.hasNext()) {
                makeOrphan(it.next());
            }
        }
    }

    private synchronized void makeOrphans(@Nullable ArrayList<Entry<K, V>> arrayList, @Nullable ArrayList<Entry<K, V>> arrayList2) {
        makeOrphans(arrayList);
        makeOrphans(arrayList2);
    }

    private synchronized boolean maybeAddToExclusives(Entry<K, V> entry) {
        if (entry.isOrphan || entry.clientCount != 0) {
            return false;
        }
        if (entry.accessCount > this.mFrequentlyUsedThreshold) {
            this.mMostFrequentlyUsedExclusiveEntries.put(entry.key, entry);
        } else {
            this.mLeastFrequentlyUsedExclusiveEntries.put(entry.key, entry);
        }
        return true;
    }

    private void maybeClose(@Nullable ArrayList<Entry<K, V>> arrayList) {
        if (arrayList != null) {
            Iterator<Entry<K, V>> it = arrayList.iterator();
            while (it.hasNext()) {
                CloseableReference.closeSafely((CloseableReference<?>) referenceToClose(it.next()));
            }
        }
    }

    private void maybeClose(@Nullable ArrayList<Entry<K, V>> arrayList, @Nullable ArrayList<Entry<K, V>> arrayList2) {
        maybeClose(arrayList);
        maybeClose(arrayList2);
    }

    private void maybeNotifyExclusiveEntriesRemoval(@Nullable ArrayList<Entry<K, V>> arrayList, @Nullable ArrayList<Entry<K, V>> arrayList2) {
        maybeNotifyExclusiveEntryRemoval(arrayList);
        maybeNotifyExclusiveEntryRemoval(arrayList2);
    }

    private static <K, V> void maybeNotifyExclusiveEntryInsertion(@Nullable Entry<K, V> entry) {
        if (entry == null || entry.observer == null) {
            return;
        }
        entry.observer.onExclusivityChanged(entry.key, true);
    }

    private static <K, V> void maybeNotifyExclusiveEntryRemoval(@Nullable Entry<K, V> entry) {
        if (entry == null || entry.observer == null) {
            return;
        }
        entry.observer.onExclusivityChanged(entry.key, false);
    }

    private void maybeNotifyExclusiveEntryRemoval(@Nullable Entry<K, V> entry, @Nullable Entry<K, V> entry2) {
        maybeNotifyExclusiveEntryRemoval(entry);
        maybeNotifyExclusiveEntryRemoval(entry2);
    }

    private void maybeNotifyExclusiveEntryRemoval(@Nullable ArrayList<Entry<K, V>> arrayList) {
        if (arrayList != null) {
            Iterator<Entry<K, V>> it = arrayList.iterator();
            while (it.hasNext()) {
                maybeNotifyExclusiveEntryRemoval(it.next());
            }
        }
    }

    private synchronized void maybeUpdateCacheFraction(K k) {
        if (this.mLeastFrequentlyUsedKeysGhostList.contains(k)) {
            if (this.mLFUFractionPromil + this.mAdaptiveRatePromil <= 900) {
                this.mLFUFractionPromil += this.mAdaptiveRatePromil;
            }
            this.mLeastFrequentlyUsedKeysGhostList.increaseValueIfExists(k);
        } else if (this.mLFUFractionPromil - this.mAdaptiveRatePromil >= 100 && this.mMostFrequentlyUsedKeysGhostList.contains(k)) {
            this.mLFUFractionPromil -= this.mAdaptiveRatePromil;
        }
    }

    private synchronized void maybeUpdateCacheParams() {
        if (this.mLastCacheParamsCheck + this.mMemoryCacheParams.paramsCheckIntervalMs > SystemClock.uptimeMillis()) {
            return;
        }
        this.mLastCacheParamsCheck = SystemClock.uptimeMillis();
        this.mMemoryCacheParams = (MemoryCacheParams) Preconditions.checkNotNull(this.mMemoryCacheParamsSupplier.get(), "mMemoryCacheParamsSupplier returned null");
    }

    private synchronized CloseableReference<V> newClientReference(final Entry<K, V> entry) {
        increaseCounters(entry);
        return CloseableReference.of(entry.valueRef.get(), new ResourceReleaser<V>() { // from class: com.facebook.imagepipeline.cache.AbstractAdaptiveCountingMemoryCache.2
            @Override // com.facebook.common.references.ResourceReleaser
            public void release(V v) {
                AbstractAdaptiveCountingMemoryCache.this.releaseClientReference(entry);
            }
        });
    }

    @Nullable
    private synchronized CloseableReference<V> referenceToClose(Entry<K, V> entry) {
        Preconditions.checkNotNull(entry);
        return (entry.isOrphan && entry.clientCount == 0) ? entry.valueRef : null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseClientReference(Entry<K, V> entry) {
        boolean zMaybeAddToExclusives;
        CloseableReference<V> closeableReferenceReferenceToClose;
        Preconditions.checkNotNull(entry);
        synchronized (this) {
            decreaseClientCount(entry);
            zMaybeAddToExclusives = maybeAddToExclusives(entry);
            closeableReferenceReferenceToClose = referenceToClose(entry);
        }
        CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceReferenceToClose);
        if (!zMaybeAddToExclusives) {
            entry = null;
        }
        maybeNotifyExclusiveEntryInsertion(entry);
        maybeUpdateCacheParams();
        maybeEvictEntries();
    }

    /* JADX WARN: Multi-variable type inference failed */
    @Nullable
    private synchronized ArrayList<Entry<K, V>> trimExclusivelyOwnedEntries(int i, int i2, CountingLruMap<K, Entry<K, V>> countingLruMap, ArrayListType arrayListType) {
        int iMax = Math.max(i, 0);
        int iMax2 = Math.max(i2, 0);
        if (countingLruMap.getCount() <= iMax && countingLruMap.getSizeInBytes() <= iMax2) {
            return null;
        }
        ArrayList<Entry<K, V>> arrayList = new ArrayList<>();
        while (true) {
            if (countingLruMap.getCount() <= iMax && countingLruMap.getSizeInBytes() <= iMax2) {
                return arrayList;
            }
            Object objCheckNotNull = Preconditions.checkNotNull(countingLruMap.getFirstKey());
            addElementToGhostList(objCheckNotNull, ((Entry) Preconditions.checkNotNull(countingLruMap.get(objCheckNotNull))).accessCount, arrayListType);
            countingLruMap.remove(objCheckNotNull);
            arrayList.add(this.mCachedEntries.remove((K) objCheckNotNull));
        }
    }

    private ValueDescriptor<Entry<K, V>> wrapValueDescriptor(final ValueDescriptor<V> valueDescriptor) {
        return new ValueDescriptor<Entry<K, V>>() { // from class: com.facebook.imagepipeline.cache.AbstractAdaptiveCountingMemoryCache.1
            @Override // com.facebook.imagepipeline.cache.ValueDescriptor
            public int getSizeInBytes(Entry<K, V> entry) {
                return valueDescriptor.getSizeInBytes(entry.valueRef.get());
            }
        };
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    @Nullable
    public CloseableReference<V> cache(K k, CloseableReference<V> closeableReference) {
        return cache(k, closeableReference, null);
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    @Nullable
    public CloseableReference<V> cache(K k, CloseableReference<V> closeableReference, @Nullable CountingMemoryCache.EntryStateObserver<K> entryStateObserver) {
        Entry<K, V> entryRemove;
        Entry<K, V> entryRemove2;
        CloseableReference<V> closeableReferenceNewClientReference;
        CloseableReference<V> closeableReferenceReferenceToClose;
        Preconditions.checkNotNull(k);
        Preconditions.checkNotNull(closeableReference);
        maybeUpdateCacheParams();
        synchronized (this) {
            entryRemove = this.mLeastFrequentlyUsedExclusiveEntries.remove(k);
            entryRemove2 = this.mMostFrequentlyUsedExclusiveEntries.remove(k);
            Preconditions.checkState(entryRemove == null || entryRemove2 == null);
            Entry<K, V> entryRemove3 = this.mCachedEntries.remove(k);
            closeableReferenceNewClientReference = null;
            if (entryRemove3 != null) {
                makeOrphan(entryRemove3);
                closeableReferenceReferenceToClose = referenceToClose(entryRemove3);
            } else {
                closeableReferenceReferenceToClose = null;
            }
            if (canCacheNewValue(closeableReference.get())) {
                Entry<K, V> entryOf = Entry.of(k, closeableReference, entryStateObserver);
                Integer value = this.mLeastFrequentlyUsedKeysGhostList.getValue(k);
                entryOf.accessCount = value != null ? value.intValue() : 0;
                this.mCachedEntries.put(k, entryOf);
                closeableReferenceNewClientReference = newClientReference(entryOf);
            }
        }
        CloseableReference.closeSafely((CloseableReference<?>) closeableReferenceReferenceToClose);
        maybeNotifyExclusiveEntryRemoval(entryRemove, entryRemove2);
        maybeEvictEntries();
        return closeableReferenceNewClientReference;
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public void clear() {
        ArrayList<Entry<K, V>> arrayListClear;
        ArrayList<Entry<K, V>> arrayListClear2;
        ArrayList<Entry<K, V>> arrayListClear3;
        synchronized (this) {
            arrayListClear = this.mLeastFrequentlyUsedExclusiveEntries.clear();
            arrayListClear2 = this.mMostFrequentlyUsedExclusiveEntries.clear();
            arrayListClear3 = this.mCachedEntries.clear();
            makeOrphans(arrayListClear3);
        }
        maybeClose(arrayListClear3);
        maybeNotifyExclusiveEntriesRemoval(arrayListClear, arrayListClear2);
        maybeUpdateCacheParams();
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public synchronized boolean contains(Predicate<K> predicate) {
        return !this.mCachedEntries.getMatchingEntries(predicate).isEmpty();
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public synchronized boolean contains(K k) {
        return this.mCachedEntries.contains(k);
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    @Nullable
    public CloseableReference<V> get(K k) {
        Entry<K, V> entryRemove;
        Entry<K, V> entryRemove2;
        CloseableReference<V> closeableReferenceNewClientReference;
        Preconditions.checkNotNull(k);
        synchronized (this) {
            entryRemove = this.mLeastFrequentlyUsedExclusiveEntries.remove(k);
            entryRemove2 = this.mMostFrequentlyUsedExclusiveEntries.remove(k);
            Entry<K, V> entry = this.mCachedEntries.get(k);
            if (entry != null) {
                closeableReferenceNewClientReference = newClientReference(entry);
            } else {
                maybeUpdateCacheFraction(k);
                closeableReferenceNewClientReference = null;
            }
        }
        maybeNotifyExclusiveEntryRemoval(entryRemove, entryRemove2);
        maybeUpdateCacheParams();
        maybeEvictEntries();
        return closeableReferenceNewClientReference;
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public CountingLruMap getCachedEntries() {
        return this.mCachedEntries;
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public synchronized int getCount() {
        return this.mCachedEntries.getCount();
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public synchronized int getEvictionQueueCount() {
        return this.mLeastFrequentlyUsedExclusiveEntries.getCount() + this.mMostFrequentlyUsedExclusiveEntries.getCount();
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public synchronized int getEvictionQueueSizeInBytes() {
        return this.mLeastFrequentlyUsedExclusiveEntries.getSizeInBytes() + this.mMostFrequentlyUsedExclusiveEntries.getSizeInBytes();
    }

    public synchronized int getInUseCount() {
        return (this.mCachedEntries.getCount() - this.mLeastFrequentlyUsedExclusiveEntries.getCount()) - this.mMostFrequentlyUsedExclusiveEntries.getCount();
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public synchronized int getInUseSizeInBytes() {
        return (this.mCachedEntries.getSizeInBytes() - this.mLeastFrequentlyUsedExclusiveEntries.getSizeInBytes()) - this.mMostFrequentlyUsedExclusiveEntries.getSizeInBytes();
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public MemoryCacheParams getMemoryCacheParams() {
        return this.mMemoryCacheParams;
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public Map<Bitmap, Object> getOtherEntries() {
        return Collections.emptyMap();
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public synchronized int getSizeInBytes() {
        return this.mCachedEntries.getSizeInBytes();
    }

    protected abstract void logIllegalAdaptiveRate();

    protected abstract void logIllegalLfuFraction();

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    public void maybeEvictEntries() {
        ArrayList<Entry<K, V>> arrayListTrimExclusivelyOwnedEntries;
        ArrayList<Entry<K, V>> arrayListTrimExclusivelyOwnedEntries2;
        synchronized (this) {
            int iMin = Math.min(this.mMemoryCacheParams.maxEvictionQueueEntries, this.mMemoryCacheParams.maxCacheEntries - getInUseCount());
            int iMin2 = Math.min(this.mMemoryCacheParams.maxEvictionQueueSize, this.mMemoryCacheParams.maxCacheSize - getInUseSizeInBytes());
            int i = (int) ((((long) iMin) * ((long) this.mLFUFractionPromil)) / 1000);
            int i2 = (int) ((((long) iMin2) * ((long) this.mLFUFractionPromil)) / 1000);
            arrayListTrimExclusivelyOwnedEntries = trimExclusivelyOwnedEntries(i, i2, this.mLeastFrequentlyUsedExclusiveEntries, ArrayListType.LFU);
            arrayListTrimExclusivelyOwnedEntries2 = trimExclusivelyOwnedEntries(iMin - i, iMin2 - i2, this.mMostFrequentlyUsedExclusiveEntries, ArrayListType.MFU);
            makeOrphans(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
        }
        maybeClose(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
        maybeNotifyExclusiveEntriesRemoval(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public void probe(K k) {
        Preconditions.checkNotNull(k);
        synchronized (this) {
            Entry<K, V> entryRemove = this.mLeastFrequentlyUsedExclusiveEntries.remove(k);
            if (entryRemove == null) {
                entryRemove = this.mMostFrequentlyUsedExclusiveEntries.remove(k);
            }
            if (entryRemove != null) {
                increaseAccessCount(entryRemove);
                maybeAddToExclusives(entryRemove);
            }
        }
    }

    @Override // com.facebook.imagepipeline.cache.MemoryCache
    public int removeAll(Predicate<K> predicate) {
        ArrayList<Entry<K, V>> arrayListRemoveAll;
        ArrayList<Entry<K, V>> arrayListRemoveAll2;
        ArrayList<Entry<K, V>> arrayListRemoveAll3;
        synchronized (this) {
            arrayListRemoveAll = this.mLeastFrequentlyUsedExclusiveEntries.removeAll(predicate);
            arrayListRemoveAll2 = this.mMostFrequentlyUsedExclusiveEntries.removeAll(predicate);
            arrayListRemoveAll3 = this.mCachedEntries.removeAll(predicate);
            makeOrphans(arrayListRemoveAll3);
        }
        maybeClose(arrayListRemoveAll3);
        maybeNotifyExclusiveEntriesRemoval(arrayListRemoveAll, arrayListRemoveAll2);
        maybeUpdateCacheParams();
        maybeEvictEntries();
        return arrayListRemoveAll3.size();
    }

    public String reportData() {
        return Objects.toStringHelper("CountingMemoryCache").add("cached_entries_count:", this.mCachedEntries.getCount()).add("exclusive_entries_count", getEvictionQueueCount()).toString();
    }

    @Override // com.facebook.imagepipeline.cache.CountingMemoryCache
    @Nullable
    public CloseableReference<V> reuse(K k) {
        Entry<K, V> entryRemove;
        boolean z;
        CloseableReference<V> closeableReference;
        Preconditions.checkNotNull(k);
        synchronized (this) {
            entryRemove = this.mLeastFrequentlyUsedExclusiveEntries.remove(k);
            if (entryRemove == null) {
                entryRemove = this.mMostFrequentlyUsedExclusiveEntries.remove(k);
            }
            z = true;
            if (entryRemove != null) {
                Entry<K, V> entryRemove2 = this.mCachedEntries.remove(k);
                Preconditions.checkNotNull(entryRemove2);
                Preconditions.checkState(entryRemove2.clientCount == 0);
                closeableReference = entryRemove2.valueRef;
            } else {
                closeableReference = null;
                z = false;
            }
        }
        if (z) {
            maybeNotifyExclusiveEntryRemoval(entryRemove);
        }
        return closeableReference;
    }

    @Override // com.facebook.common.memory.MemoryTrimmable
    public void trim(MemoryTrimType memoryTrimType) {
        ArrayList<Entry<K, V>> arrayListTrimExclusivelyOwnedEntries;
        ArrayList<Entry<K, V>> arrayListTrimExclusivelyOwnedEntries2;
        double trimRatio = this.mCacheTrimStrategy.getTrimRatio(memoryTrimType);
        synchronized (this) {
            int sizeInBytes = ((int) (((double) this.mCachedEntries.getSizeInBytes()) * (1.0d - trimRatio))) - getInUseSizeInBytes();
            int i = 0;
            int iMax = Math.max(0, sizeInBytes);
            int sizeInBytes2 = this.mMostFrequentlyUsedExclusiveEntries.getSizeInBytes();
            int iMax2 = Math.max(0, iMax - sizeInBytes2);
            if (iMax > sizeInBytes2) {
                iMax = sizeInBytes2;
                i = iMax2;
            }
            arrayListTrimExclusivelyOwnedEntries = trimExclusivelyOwnedEntries(Integer.MAX_VALUE, i, this.mLeastFrequentlyUsedExclusiveEntries, ArrayListType.LFU);
            arrayListTrimExclusivelyOwnedEntries2 = trimExclusivelyOwnedEntries(Integer.MAX_VALUE, iMax, this.mMostFrequentlyUsedExclusiveEntries, ArrayListType.MFU);
            makeOrphans(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
        }
        maybeClose(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
        maybeNotifyExclusiveEntriesRemoval(arrayListTrimExclusivelyOwnedEntries, arrayListTrimExclusivelyOwnedEntries2);
        maybeUpdateCacheParams();
        maybeEvictEntries();
    }
}
