package com.facebook.imagepipeline.cache;

import androidx.annotation.VisibleForTesting;
import com.facebook.common.internal.Predicate;
import com.facebook.infer.annotation.Nullsafe;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@Nullsafe(Nullsafe.Mode.STRICT)
public class CountingLruMap<K, V> {

    @GuardedBy("this")
    private final LinkedHashMap<K, V> mMap = new LinkedHashMap<>();

    @GuardedBy("this")
    private int mSizeInBytes = 0;
    private final ValueDescriptor<V> mValueDescriptor;

    public CountingLruMap(ValueDescriptor<V> valueDescriptor) {
        this.mValueDescriptor = valueDescriptor;
    }

    private int getValueSizeInBytes(@Nullable V v) {
        if (v == null) {
            return 0;
        }
        return this.mValueDescriptor.getSizeInBytes(v);
    }

    public synchronized ArrayList<V> clear() {
        ArrayList<V> arrayList;
        arrayList = new ArrayList<>((Collection<? extends V>) this.mMap.values());
        this.mMap.clear();
        this.mSizeInBytes = 0;
        return arrayList;
    }

    public synchronized boolean contains(K k) {
        return this.mMap.containsKey(k);
    }

    @Nullable
    public synchronized V get(K k) {
        return this.mMap.get(k);
    }

    public synchronized int getCount() {
        return this.mMap.size();
    }

    @Nullable
    public synchronized K getFirstKey() {
        return this.mMap.isEmpty() ? null : this.mMap.keySet().iterator().next();
    }

    @VisibleForTesting
    synchronized ArrayList<K> getKeys() {
        return new ArrayList<>(this.mMap.keySet());
    }

    public synchronized ArrayList<Map.Entry<K, V>> getMatchingEntries(@Nullable Predicate<K> predicate) {
        ArrayList<Map.Entry<K, V>> arrayList;
        arrayList = new ArrayList<>(this.mMap.entrySet().size());
        for (Map.Entry<K, V> entry : this.mMap.entrySet()) {
            if (predicate == null || predicate.apply(entry.getKey())) {
                arrayList.add(entry);
            }
        }
        return arrayList;
    }

    public synchronized int getSizeInBytes() {
        return this.mSizeInBytes;
    }

    @VisibleForTesting
    synchronized ArrayList<V> getValues() {
        return new ArrayList<>(this.mMap.values());
    }

    @Nullable
    public synchronized V put(K k, V v) {
        V vRemove;
        vRemove = this.mMap.remove(k);
        this.mSizeInBytes -= getValueSizeInBytes(vRemove);
        this.mMap.put(k, v);
        this.mSizeInBytes += getValueSizeInBytes(v);
        return vRemove;
    }

    @Nullable
    public synchronized V remove(K k) {
        V vRemove;
        vRemove = this.mMap.remove(k);
        this.mSizeInBytes -= getValueSizeInBytes(vRemove);
        return vRemove;
    }

    public synchronized ArrayList<V> removeAll(@Nullable Predicate<K> predicate) {
        ArrayList<V> arrayList;
        arrayList = new ArrayList<>();
        Iterator<Map.Entry<K, V>> it = this.mMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> next = it.next();
            if (predicate == null || predicate.apply(next.getKey())) {
                arrayList.add(next.getValue());
                this.mSizeInBytes -= getValueSizeInBytes(next.getValue());
                it.remove();
            }
        }
        return arrayList;
    }
}
