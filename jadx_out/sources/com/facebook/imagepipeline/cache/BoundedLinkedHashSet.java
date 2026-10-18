package com.facebook.imagepipeline.cache;

import com.facebook.infer.annotation.Nullsafe;
import java.util.LinkedHashSet;
import javax.annotation.concurrent.ThreadSafe;

/* JADX INFO: loaded from: classes3.dex */
@ThreadSafe
@Nullsafe(Nullsafe.Mode.STRICT)
public class BoundedLinkedHashSet<E> {
    private LinkedHashSet<E> mLinkedHashSet;
    private int mMaxSize;

    public BoundedLinkedHashSet(int i) {
        this.mLinkedHashSet = new LinkedHashSet<>(i);
        this.mMaxSize = i;
    }

    public synchronized boolean add(E e) {
        if (this.mLinkedHashSet.size() == this.mMaxSize) {
            this.mLinkedHashSet.remove(this.mLinkedHashSet.iterator().next());
        }
        this.mLinkedHashSet.remove(e);
        return this.mLinkedHashSet.add(e);
    }

    public synchronized boolean contains(E e) {
        return this.mLinkedHashSet.contains(e);
    }
}
