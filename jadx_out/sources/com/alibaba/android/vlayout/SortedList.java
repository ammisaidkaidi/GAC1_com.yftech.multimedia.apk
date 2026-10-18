package com.alibaba.android.vlayout;

import java.lang.reflect.Array;

/* JADX INFO: loaded from: classes3.dex */
public class SortedList<T> {
    private static final int CAPACITY_GROWTH = 10;
    private static final int DELETION = 2;
    private static final int INSERTION = 1;
    public static final int INVALID_POSITION = -1;
    private static final int LOOKUP = 4;
    private static final int MIN_CAPACITY = 10;
    private BatchedCallback mBatchedCallback;
    private Callback mCallback;
    T[] mData;
    private int mSize;
    private final Class<T> mTClass;

    public static class BatchedCallback<T2> extends Callback<T2> {
        static final int TYPE_ADD = 1;
        static final int TYPE_CHANGE = 3;
        static final int TYPE_MOVE = 4;
        static final int TYPE_NONE = 0;
        static final int TYPE_REMOVE = 2;
        private final Callback<T2> mWrappedCallback;
        int mLastEventType = 0;
        int mLastEventPosition = -1;
        int mLastEventCount = -1;

        public BatchedCallback(Callback<T2> callback) {
            this.mWrappedCallback = callback;
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public boolean areContentsTheSame(T2 t2, T2 t3) {
            return this.mWrappedCallback.areContentsTheSame(t2, t3);
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public boolean areItemsTheSame(T2 t2, T2 t3) {
            return this.mWrappedCallback.areItemsTheSame(t2, t3);
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public int compare(T2 t2, T2 t3) {
            return this.mWrappedCallback.compare(t2, t3);
        }

        public void dispatchLastEvent() {
            if (this.mLastEventType == 0) {
                return;
            }
            switch (this.mLastEventType) {
                case 1:
                    this.mWrappedCallback.onInserted(this.mLastEventPosition, this.mLastEventCount);
                    break;
                case 2:
                    this.mWrappedCallback.onRemoved(this.mLastEventPosition, this.mLastEventCount);
                    break;
                case 3:
                    this.mWrappedCallback.onChanged(this.mLastEventPosition, this.mLastEventCount);
                    break;
            }
            this.mLastEventType = 0;
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public void onChanged(int i, int i2) {
            int i3;
            if (this.mLastEventType == 3 && i <= this.mLastEventPosition + this.mLastEventCount && (i3 = i + i2) >= this.mLastEventPosition) {
                int i4 = this.mLastEventPosition + this.mLastEventCount;
                this.mLastEventPosition = Math.min(i, this.mLastEventPosition);
                this.mLastEventCount = Math.max(i4, i3) - this.mLastEventPosition;
            } else {
                dispatchLastEvent();
                this.mLastEventPosition = i;
                this.mLastEventCount = i2;
                this.mLastEventType = 3;
            }
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public void onInserted(int i, int i2) {
            if (this.mLastEventType == 1 && i >= this.mLastEventPosition && i <= this.mLastEventPosition + this.mLastEventCount) {
                this.mLastEventCount += i2;
                this.mLastEventPosition = Math.min(i, this.mLastEventPosition);
            } else {
                dispatchLastEvent();
                this.mLastEventPosition = i;
                this.mLastEventCount = i2;
                this.mLastEventType = 1;
            }
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public void onMoved(int i, int i2) {
            dispatchLastEvent();
            this.mWrappedCallback.onMoved(i, i2);
        }

        @Override // com.alibaba.android.vlayout.SortedList.Callback
        public void onRemoved(int i, int i2) {
            if (this.mLastEventType == 2 && this.mLastEventPosition == i) {
                this.mLastEventCount += i2;
                return;
            }
            dispatchLastEvent();
            this.mLastEventPosition = i;
            this.mLastEventCount = i2;
            this.mLastEventType = 2;
        }
    }

    public static abstract class Callback<T2> {
        public abstract boolean areContentsTheSame(T2 t2, T2 t3);

        public abstract boolean areItemsTheSame(T2 t2, T2 t3);

        public abstract int compare(T2 t2, T2 t3);

        public abstract void onChanged(int i, int i2);

        public abstract void onInserted(int i, int i2);

        public abstract void onMoved(int i, int i2);

        public abstract void onRemoved(int i, int i2);
    }

    public SortedList(Class<T> cls, Callback<T> callback) {
        this(cls, callback, 10);
    }

    public SortedList(Class<T> cls, Callback<T> callback, int i) {
        this.mTClass = cls;
        this.mData = (T[]) ((Object[]) Array.newInstance((Class<?>) cls, i));
        this.mCallback = callback;
        this.mSize = 0;
    }

    private int add(T t, boolean z) {
        int iFindIndexOf = findIndexOf(t, 1);
        if (iFindIndexOf == -1) {
            iFindIndexOf = 0;
        } else if (iFindIndexOf < this.mSize) {
            T t2 = this.mData[iFindIndexOf];
            if (this.mCallback.areItemsTheSame(t2, t)) {
                if (this.mCallback.areContentsTheSame(t2, t)) {
                    this.mData[iFindIndexOf] = t;
                    return iFindIndexOf;
                }
                this.mData[iFindIndexOf] = t;
                this.mCallback.onChanged(iFindIndexOf, 1);
                return iFindIndexOf;
            }
        }
        addToData(iFindIndexOf, t);
        if (z) {
            this.mCallback.onInserted(iFindIndexOf, 1);
        }
        return iFindIndexOf;
    }

    private void addToData(int i, T t) {
        if (i > this.mSize) {
            throw new IndexOutOfBoundsException("cannot add item to " + i + " because size is " + this.mSize);
        }
        if (this.mSize == this.mData.length) {
            T[] tArr = (T[]) ((Object[]) Array.newInstance((Class<?>) this.mTClass, this.mData.length + 10));
            System.arraycopy(this.mData, 0, tArr, 0, i);
            tArr[i] = t;
            System.arraycopy(this.mData, i, tArr, i + 1, this.mSize - i);
            this.mData = tArr;
        } else {
            System.arraycopy(this.mData, i, this.mData, i + 1, this.mSize - i);
            this.mData[i] = t;
        }
        this.mSize++;
    }

    private int findIndexOf(T t, int i) {
        int i2 = this.mSize;
        int i3 = 0;
        while (i3 < i2) {
            int i4 = (i3 + i2) / 2;
            T t2 = this.mData[i4];
            int iCompare = this.mCallback.compare(t2, t);
            if (iCompare < 0) {
                i3 = i4 + 1;
            } else {
                if (iCompare == 0) {
                    if (this.mCallback.areItemsTheSame(t2, t)) {
                        return i4;
                    }
                    int iLinearEqualitySearch = linearEqualitySearch(t, i4, i3, i2);
                    return (i == 1 && iLinearEqualitySearch == -1) ? i4 : iLinearEqualitySearch;
                }
                i2 = i4;
            }
        }
        if (i == 1) {
            return i3;
        }
        return -1;
    }

    private int linearEqualitySearch(T t, int i, int i2, int i3) {
        T t2;
        for (int i4 = i - 1; i4 >= i2; i4--) {
            T t3 = this.mData[i4];
            if (this.mCallback.compare(t3, t) != 0) {
                break;
            }
            if (this.mCallback.areItemsTheSame(t3, t)) {
                return i4;
            }
        }
        do {
            i++;
            if (i >= i3) {
                return -1;
            }
            t2 = this.mData[i];
            if (this.mCallback.compare(t2, t) != 0) {
                return -1;
            }
        } while (!this.mCallback.areItemsTheSame(t2, t));
        return i;
    }

    private boolean remove(T t, boolean z) {
        int iFindIndexOf = findIndexOf(t, 2);
        if (iFindIndexOf == -1) {
            return false;
        }
        removeItemAtIndex(iFindIndexOf, z);
        return true;
    }

    private void removeItemAtIndex(int i, boolean z) {
        System.arraycopy(this.mData, i + 1, this.mData, i, (this.mSize - i) - 1);
        this.mSize--;
        this.mData[this.mSize] = null;
        if (z) {
            this.mCallback.onRemoved(i, 1);
        }
    }

    public int add(T t) {
        return add(t, true);
    }

    public void beginBatchedUpdates() {
        if (this.mCallback instanceof BatchedCallback) {
            return;
        }
        if (this.mBatchedCallback == null) {
            this.mBatchedCallback = new BatchedCallback(this.mCallback);
        }
        this.mCallback = this.mBatchedCallback;
    }

    public void endBatchedUpdates() {
        if (this.mCallback instanceof BatchedCallback) {
            ((BatchedCallback) this.mCallback).dispatchLastEvent();
        }
        if (this.mCallback == this.mBatchedCallback) {
            this.mCallback = this.mBatchedCallback.mWrappedCallback;
        }
    }

    public T get(int i) throws IndexOutOfBoundsException {
        if (i < this.mSize && i >= 0) {
            return this.mData[i];
        }
        throw new IndexOutOfBoundsException("Asked to get item at " + i + " but size is " + this.mSize);
    }

    public int indexOf(T t) {
        return findIndexOf(t, 4);
    }

    public void recalculatePositionOfItemAt(int i) {
        T t = get(i);
        removeItemAtIndex(i, false);
        int iAdd = add(t, false);
        if (i != iAdd) {
            this.mCallback.onMoved(i, iAdd);
        }
    }

    public boolean remove(T t) {
        return remove(t, true);
    }

    public T removeItemAt(int i) {
        T t = get(i);
        removeItemAtIndex(i, true);
        return t;
    }

    public int size() {
        return this.mSize;
    }

    public void updateItemAt(int i, T t) {
        T t2 = get(i);
        boolean z = t2 == t || !this.mCallback.areContentsTheSame(t2, t);
        if (t2 != t && this.mCallback.compare(t2, t) == 0) {
            this.mData[i] = t;
            if (z) {
                this.mCallback.onChanged(i, 1);
                return;
            }
            return;
        }
        if (z) {
            this.mCallback.onChanged(i, 1);
        }
        removeItemAtIndex(i, false);
        int iAdd = add(t, false);
        if (i != iAdd) {
            this.mCallback.onMoved(i, iAdd);
        }
    }
}
