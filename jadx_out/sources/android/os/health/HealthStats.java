package android.os.health;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.ArrayMap;
import java.util.Arrays;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class HealthStats {
    private String mDataType;
    private int[] mMeasurementKeys;
    private long[] mMeasurementValues;
    private int[] mMeasurementsKeys;
    private ArrayMap<String, Long>[] mMeasurementsValues;
    private int[] mStatsKeys;
    private ArrayMap<String, HealthStats>[] mStatsValues;
    private int[] mTimerCounts;
    private int[] mTimerKeys;
    private long[] mTimerTimes;
    private int[] mTimersKeys;
    private ArrayMap<String, TimerStat>[] mTimersValues;

    private HealthStats() {
        throw new RuntimeException("unsupported");
    }

    public HealthStats(Parcel parcel) {
        this.mDataType = parcel.readString();
        int i = parcel.readInt();
        this.mTimerKeys = new int[i];
        this.mTimerCounts = new int[i];
        this.mTimerTimes = new long[i];
        for (int i2 = 0; i2 < i; i2++) {
            this.mTimerKeys[i2] = parcel.readInt();
            this.mTimerCounts[i2] = parcel.readInt();
            this.mTimerTimes[i2] = parcel.readLong();
        }
        int i3 = parcel.readInt();
        this.mMeasurementKeys = new int[i3];
        this.mMeasurementValues = new long[i3];
        for (int i4 = 0; i4 < i3; i4++) {
            this.mMeasurementKeys[i4] = parcel.readInt();
            this.mMeasurementValues[i4] = parcel.readLong();
        }
        int i5 = parcel.readInt();
        this.mStatsKeys = new int[i5];
        this.mStatsValues = new ArrayMap[i5];
        for (int i6 = 0; i6 < i5; i6++) {
            this.mStatsKeys[i6] = parcel.readInt();
            this.mStatsValues[i6] = createHealthStatsMap(parcel);
        }
        int i7 = parcel.readInt();
        this.mTimersKeys = new int[i7];
        this.mTimersValues = new ArrayMap[i7];
        for (int i8 = 0; i8 < i7; i8++) {
            this.mTimersKeys[i8] = parcel.readInt();
            this.mTimersValues[i8] = createParcelableMap(parcel, TimerStat.CREATOR);
        }
        int i9 = parcel.readInt();
        this.mMeasurementsKeys = new int[i9];
        this.mMeasurementsValues = new ArrayMap[i9];
        for (int i10 = 0; i10 < i9; i10++) {
            this.mMeasurementsKeys[i10] = parcel.readInt();
            this.mMeasurementsValues[i10] = createLongsMap(parcel);
        }
    }

    private static ArrayMap<String, HealthStats> createHealthStatsMap(Parcel parcel) {
        int i = parcel.readInt();
        ArrayMap<String, HealthStats> arrayMap = new ArrayMap<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            arrayMap.put(parcel.readString(), new HealthStats(parcel));
        }
        return arrayMap;
    }

    private static ArrayMap<String, Long> createLongsMap(Parcel parcel) {
        int i = parcel.readInt();
        ArrayMap<String, Long> arrayMap = new ArrayMap<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            arrayMap.put(parcel.readString(), Long.valueOf(parcel.readLong()));
        }
        return arrayMap;
    }

    private static <T extends Parcelable> ArrayMap<String, T> createParcelableMap(Parcel parcel, Parcelable.Creator<T> creator) {
        int i = parcel.readInt();
        ArrayMap<String, T> arrayMap = new ArrayMap<>(i);
        for (int i2 = 0; i2 < i; i2++) {
            arrayMap.put(parcel.readString(), creator.createFromParcel(parcel));
        }
        return arrayMap;
    }

    private static int getIndex(int[] iArr, int i) {
        return Arrays.binarySearch(iArr, i);
    }

    public String getDataType() {
        return this.mDataType;
    }

    public long getMeasurement(int i) {
        int index = getIndex(this.mMeasurementKeys, i);
        if (index >= 0) {
            return this.mMeasurementValues[index];
        }
        throw new IndexOutOfBoundsException("Bad measurement key dataType=" + this.mDataType + " key=" + i);
    }

    public int getMeasurementKeyAt(int i) {
        return this.mMeasurementKeys[i];
    }

    public int getMeasurementKeyCount() {
        return this.mMeasurementKeys.length;
    }

    public Map<String, Long> getMeasurements(int i) {
        int index = getIndex(this.mMeasurementsKeys, i);
        if (index >= 0) {
            return this.mMeasurementsValues[index];
        }
        throw new IndexOutOfBoundsException("Bad measurements key dataType=" + this.mDataType + " key=" + i);
    }

    public int getMeasurementsKeyAt(int i) {
        return this.mMeasurementsKeys[i];
    }

    public int getMeasurementsKeyCount() {
        return this.mMeasurementsKeys.length;
    }

    public Map<String, HealthStats> getStats(int i) {
        int index = getIndex(this.mStatsKeys, i);
        if (index >= 0) {
            return this.mStatsValues[index];
        }
        throw new IndexOutOfBoundsException("Bad stats key dataType=" + this.mDataType + " key=" + i);
    }

    public int getStatsKeyAt(int i) {
        return this.mStatsKeys[i];
    }

    public int getStatsKeyCount() {
        return this.mStatsKeys.length;
    }

    public TimerStat getTimer(int i) {
        int index = getIndex(this.mTimerKeys, i);
        if (index >= 0) {
            return new TimerStat(this.mTimerCounts[index], this.mTimerTimes[index]);
        }
        throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + i);
    }

    public int getTimerCount(int i) {
        int index = getIndex(this.mTimerKeys, i);
        if (index >= 0) {
            return this.mTimerCounts[index];
        }
        throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + i);
    }

    public int getTimerKeyAt(int i) {
        return this.mTimerKeys[i];
    }

    public int getTimerKeyCount() {
        return this.mTimerKeys.length;
    }

    public long getTimerTime(int i) {
        int index = getIndex(this.mTimerKeys, i);
        if (index >= 0) {
            return this.mTimerTimes[index];
        }
        throw new IndexOutOfBoundsException("Bad timer key dataType=" + this.mDataType + " key=" + i);
    }

    public Map<String, TimerStat> getTimers(int i) {
        int index = getIndex(this.mTimersKeys, i);
        if (index >= 0) {
            return this.mTimersValues[index];
        }
        throw new IndexOutOfBoundsException("Bad timers key dataType=" + this.mDataType + " key=" + i);
    }

    public int getTimersKeyAt(int i) {
        return this.mTimersKeys[i];
    }

    public int getTimersKeyCount() {
        return this.mTimersKeys.length;
    }

    public boolean hasMeasurement(int i) {
        return getIndex(this.mMeasurementKeys, i) >= 0;
    }

    public boolean hasMeasurements(int i) {
        return getIndex(this.mMeasurementsKeys, i) >= 0;
    }

    public boolean hasStats(int i) {
        return getIndex(this.mStatsKeys, i) >= 0;
    }

    public boolean hasTimer(int i) {
        return getIndex(this.mTimerKeys, i) >= 0;
    }

    public boolean hasTimers(int i) {
        return getIndex(this.mTimersKeys, i) >= 0;
    }
}
