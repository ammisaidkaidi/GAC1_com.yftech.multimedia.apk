package android.util;

import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import libcore.util.EmptyArray;

/* JADX INFO: loaded from: classes2.dex */
public class SparseBooleanArray implements Cloneable {
    private int[] mKeys;
    private int mSize;
    private boolean[] mValues;

    public SparseBooleanArray() {
        this(10);
    }

    public SparseBooleanArray(int i) {
        if (i == 0) {
            this.mKeys = EmptyArray.INT;
            this.mValues = EmptyArray.BOOLEAN;
        } else {
            this.mKeys = ArrayUtils.newUnpaddedIntArray(i);
            this.mValues = new boolean[this.mKeys.length];
        }
        this.mSize = 0;
    }

    public void append(int i, boolean z) {
        if (this.mSize != 0 && i <= this.mKeys[this.mSize - 1]) {
            put(i, z);
            return;
        }
        this.mKeys = GrowingArrayUtils.append(this.mKeys, this.mSize, i);
        this.mValues = GrowingArrayUtils.append(this.mValues, this.mSize, z);
        this.mSize++;
    }

    public void clear() {
        this.mSize = 0;
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public SparseBooleanArray m36clone() {
        try {
            SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) super.clone();
            try {
                sparseBooleanArray.mKeys = (int[]) this.mKeys.clone();
                sparseBooleanArray.mValues = (boolean[]) this.mValues.clone();
                return sparseBooleanArray;
            } catch (CloneNotSupportedException unused) {
                return sparseBooleanArray;
            }
        } catch (CloneNotSupportedException unused2) {
            return null;
        }
    }

    public void delete(int i) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            int i2 = iBinarySearch + 1;
            System.arraycopy(this.mKeys, i2, this.mKeys, iBinarySearch, this.mSize - i2);
            System.arraycopy(this.mValues, i2, this.mValues, iBinarySearch, this.mSize - i2);
            this.mSize--;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SparseBooleanArray)) {
            return false;
        }
        SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) obj;
        if (this.mSize != sparseBooleanArray.mSize) {
            return false;
        }
        for (int i = 0; i < this.mSize; i++) {
            if (this.mKeys[i] != sparseBooleanArray.mKeys[i] || this.mValues[i] != sparseBooleanArray.mValues[i]) {
                return false;
            }
        }
        return true;
    }

    public boolean get(int i) {
        return get(i, false);
    }

    public boolean get(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        return iBinarySearch < 0 ? z : this.mValues[iBinarySearch];
    }

    public int hashCode() {
        int i = this.mSize;
        for (int i2 = 0; i2 < this.mSize; i2++) {
            i = ((i * 31) + this.mKeys[i2]) | (this.mValues[i2] ? 1 : 0);
        }
        return i;
    }

    public int indexOfKey(int i) {
        return ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
    }

    public int indexOfValue(boolean z) {
        for (int i = 0; i < this.mSize; i++) {
            if (this.mValues[i] == z) {
                return i;
            }
        }
        return -1;
    }

    public int keyAt(int i) {
        return this.mKeys[i];
    }

    public void put(int i, boolean z) {
        int iBinarySearch = ContainerHelpers.binarySearch(this.mKeys, this.mSize, i);
        if (iBinarySearch >= 0) {
            this.mValues[iBinarySearch] = z;
            return;
        }
        int i2 = ~iBinarySearch;
        this.mKeys = GrowingArrayUtils.insert(this.mKeys, this.mSize, i2, i);
        this.mValues = GrowingArrayUtils.insert(this.mValues, this.mSize, i2, z);
        this.mSize++;
    }

    public void removeAt(int i) {
        int i2 = i + 1;
        System.arraycopy(this.mKeys, i2, this.mKeys, i, this.mSize - i2);
        System.arraycopy(this.mValues, i2, this.mValues, i, this.mSize - i2);
        this.mSize--;
    }

    public void setKeyAt(int i, int i2) {
        this.mKeys[i] = i2;
    }

    public void setValueAt(int i, boolean z) {
        this.mValues[i] = z;
    }

    public int size() {
        return this.mSize;
    }

    public String toString() {
        if (size() <= 0) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder(this.mSize * 28);
        sb.append('{');
        for (int i = 0; i < this.mSize; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(keyAt(i));
            sb.append('=');
            sb.append(valueAt(i));
        }
        sb.append('}');
        return sb.toString();
    }

    public boolean valueAt(int i) {
        return this.mValues[i];
    }
}
