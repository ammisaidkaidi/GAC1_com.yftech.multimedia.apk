package android.accessibilityservice;

import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Parcel;
import android.os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final class GestureDescription {
    private static final long MAX_GESTURE_DURATION_MS = 60000;
    private static final int MAX_STROKE_COUNT = 10;
    private final List<StrokeDescription> mStrokes;
    private final float[] mTempPos;

    public static class Builder {
        private final List<StrokeDescription> mStrokes = new ArrayList();

        public Builder addStroke(StrokeDescription strokeDescription) {
            if (this.mStrokes.size() >= 10) {
                throw new IllegalStateException("Attempting to add too many strokes to a gesture");
            }
            this.mStrokes.add(strokeDescription);
            if (GestureDescription.getTotalDuration(this.mStrokes) <= 60000) {
                return this;
            }
            this.mStrokes.remove(strokeDescription);
            throw new IllegalStateException("Gesture would exceed maximum duration with new stroke");
        }

        public GestureDescription build() {
            if (this.mStrokes.size() != 0) {
                return new GestureDescription(this.mStrokes);
            }
            throw new IllegalStateException("Gestures must have at least one stroke");
        }
    }

    public static class GestureStep implements Parcelable {
        public static final Parcelable.Creator<GestureStep> CREATOR = new Parcelable.Creator<GestureStep>() { // from class: android.accessibilityservice.GestureDescription.GestureStep.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GestureStep createFromParcel(Parcel parcel) {
                return new GestureStep(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public GestureStep[] newArray(int i) {
                return new GestureStep[i];
            }
        };
        public int numTouchPoints;
        public long timeSinceGestureStart;
        public TouchPoint[] touchPoints;

        public GestureStep(long j, int i, TouchPoint[] touchPointArr) {
            this.timeSinceGestureStart = j;
            this.numTouchPoints = i;
            this.touchPoints = new TouchPoint[i];
            for (int i2 = 0; i2 < i; i2++) {
                this.touchPoints[i2] = new TouchPoint(touchPointArr[i2]);
            }
        }

        public GestureStep(Parcel parcel) {
            this.timeSinceGestureStart = parcel.readLong();
            Parcelable[] parcelableArray = parcel.readParcelableArray(TouchPoint.class.getClassLoader());
            this.numTouchPoints = parcelableArray == null ? 0 : parcelableArray.length;
            this.touchPoints = new TouchPoint[this.numTouchPoints];
            for (int i = 0; i < this.numTouchPoints; i++) {
                this.touchPoints[i] = (TouchPoint) parcelableArray[i];
            }
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeLong(this.timeSinceGestureStart);
            parcel.writeParcelableArray(this.touchPoints, i);
        }
    }

    public static class MotionEventGenerator {
        private static TouchPoint[] sCurrentTouchPoints;

        private static TouchPoint[] getCurrentTouchPoints(int i) {
            if (sCurrentTouchPoints == null || sCurrentTouchPoints.length < i) {
                sCurrentTouchPoints = new TouchPoint[i];
                for (int i2 = 0; i2 < i; i2++) {
                    sCurrentTouchPoints[i2] = new TouchPoint();
                }
            }
            return sCurrentTouchPoints;
        }

        public static List<GestureStep> getGestureStepsFromGestureDescription(GestureDescription gestureDescription, int i) {
            ArrayList arrayList = new ArrayList();
            TouchPoint[] currentTouchPoints = getCurrentTouchPoints(gestureDescription.getStrokeCount());
            long nextKeyPointAtLeast = gestureDescription.getNextKeyPointAtLeast(0L);
            int pointsForTime = 0;
            long j = 0;
            while (nextKeyPointAtLeast >= 0) {
                if (pointsForTime != 0) {
                    nextKeyPointAtLeast = Math.min(nextKeyPointAtLeast, j + ((long) i));
                }
                j = nextKeyPointAtLeast;
                pointsForTime = gestureDescription.getPointsForTime(j, currentTouchPoints);
                arrayList.add(new GestureStep(j, pointsForTime, currentTouchPoints));
                nextKeyPointAtLeast = gestureDescription.getNextKeyPointAtLeast(1 + j);
            }
            return arrayList;
        }
    }

    public static class StrokeDescription {
        private static final int INVALID_STROKE_ID = -1;
        static int sIdCounter;
        boolean mContinued;
        int mContinuedStrokeId;
        long mEndTime;
        int mId;
        Path mPath;
        private PathMeasure mPathMeasure;
        long mStartTime;
        float[] mTapLocation;
        private float mTimeToLengthConversion;

        public StrokeDescription(Path path, long j, long j2) {
            this(path, j, j2, false);
        }

        public StrokeDescription(Path path, long j, long j2, boolean z) {
            this.mContinuedStrokeId = -1;
            this.mContinued = z;
            Preconditions.checkArgument(j2 > 0, "Duration must be positive");
            Preconditions.checkArgument(j >= 0, "Start time must not be negative");
            Preconditions.checkArgument(!path.isEmpty(), "Path is empty");
            RectF rectF = new RectF();
            path.computeBounds(rectF, false);
            Preconditions.checkArgument(rectF.bottom >= 0.0f && rectF.top >= 0.0f && rectF.right >= 0.0f && rectF.left >= 0.0f, "Path bounds must not be negative");
            this.mPath = new Path(path);
            this.mPathMeasure = new PathMeasure(path, false);
            if (this.mPathMeasure.getLength() == 0.0f) {
                Path path2 = new Path(path);
                path2.lineTo(-1.0f, -1.0f);
                this.mTapLocation = new float[2];
                new PathMeasure(path2, false).getPosTan(0.0f, this.mTapLocation, null);
            }
            if (this.mPathMeasure.nextContour()) {
                throw new IllegalArgumentException("Path has more than one contour");
            }
            this.mPathMeasure.setPath(this.mPath, false);
            this.mStartTime = j;
            this.mEndTime = j + j2;
            this.mTimeToLengthConversion = getLength() / j2;
            int i = sIdCounter;
            sIdCounter = i + 1;
            this.mId = i;
        }

        public StrokeDescription continueStroke(Path path, long j, long j2, boolean z) {
            if (!this.mContinued) {
                throw new IllegalStateException("Only strokes marked willContinue can be continued");
            }
            StrokeDescription strokeDescription = new StrokeDescription(path, j, j2, z);
            strokeDescription.mContinuedStrokeId = this.mId;
            return strokeDescription;
        }

        public int getContinuedStrokeId() {
            return this.mContinuedStrokeId;
        }

        public long getDuration() {
            return this.mEndTime - this.mStartTime;
        }

        public int getId() {
            return this.mId;
        }

        float getLength() {
            return this.mPathMeasure.getLength();
        }

        public Path getPath() {
            return new Path(this.mPath);
        }

        boolean getPosForTime(long j, float[] fArr) {
            if (this.mTapLocation != null) {
                fArr[0] = this.mTapLocation[0];
                fArr[1] = this.mTapLocation[1];
                return true;
            }
            if (j == this.mEndTime) {
                return this.mPathMeasure.getPosTan(getLength(), fArr, null);
            }
            return this.mPathMeasure.getPosTan(this.mTimeToLengthConversion * (j - this.mStartTime), fArr, null);
        }

        public long getStartTime() {
            return this.mStartTime;
        }

        boolean hasPointForTime(long j) {
            return j >= this.mStartTime && j <= this.mEndTime;
        }

        public boolean willContinue() {
            return this.mContinued;
        }
    }

    public static class TouchPoint implements Parcelable {
        public static final Parcelable.Creator<TouchPoint> CREATOR = new Parcelable.Creator<TouchPoint>() { // from class: android.accessibilityservice.GestureDescription.TouchPoint.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TouchPoint createFromParcel(Parcel parcel) {
                return new TouchPoint(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public TouchPoint[] newArray(int i) {
                return new TouchPoint[i];
            }
        };
        private static final int FLAG_IS_END_OF_PATH = 2;
        private static final int FLAG_IS_START_OF_PATH = 1;
        public int mContinuedStrokeId;
        public boolean mIsEndOfPath;
        public boolean mIsStartOfPath;
        public int mStrokeId;
        public float mX;
        public float mY;

        public TouchPoint() {
        }

        public TouchPoint(TouchPoint touchPoint) {
            copyFrom(touchPoint);
        }

        public TouchPoint(Parcel parcel) {
            this.mStrokeId = parcel.readInt();
            this.mContinuedStrokeId = parcel.readInt();
            int i = parcel.readInt();
            this.mIsStartOfPath = (i & 1) != 0;
            this.mIsEndOfPath = (i & 2) != 0;
            this.mX = parcel.readFloat();
            this.mY = parcel.readFloat();
        }

        public void copyFrom(TouchPoint touchPoint) {
            this.mStrokeId = touchPoint.mStrokeId;
            this.mContinuedStrokeId = touchPoint.mContinuedStrokeId;
            this.mIsStartOfPath = touchPoint.mIsStartOfPath;
            this.mIsEndOfPath = touchPoint.mIsEndOfPath;
            this.mX = touchPoint.mX;
            this.mY = touchPoint.mY;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public String toString() {
            return "TouchPoint{mStrokeId=" + this.mStrokeId + ", mContinuedStrokeId=" + this.mContinuedStrokeId + ", mIsStartOfPath=" + this.mIsStartOfPath + ", mIsEndOfPath=" + this.mIsEndOfPath + ", mX=" + this.mX + ", mY=" + this.mY + '}';
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.mStrokeId);
            parcel.writeInt(this.mContinuedStrokeId);
            parcel.writeInt((this.mIsStartOfPath ? 1 : 0) | (this.mIsEndOfPath ? 2 : 0));
            parcel.writeFloat(this.mX);
            parcel.writeFloat(this.mY);
        }
    }

    private GestureDescription() {
        this.mStrokes = new ArrayList();
        this.mTempPos = new float[2];
    }

    private GestureDescription(List<StrokeDescription> list) {
        this.mStrokes = new ArrayList();
        this.mTempPos = new float[2];
        this.mStrokes.addAll(list);
    }

    public static long getMaxGestureDuration() {
        return 60000L;
    }

    public static int getMaxStrokeCount() {
        return 10;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getNextKeyPointAtLeast(long j) {
        long j2 = Long.MAX_VALUE;
        for (int i = 0; i < this.mStrokes.size(); i++) {
            long j3 = this.mStrokes.get(i).mStartTime;
            if (j3 < j2 && j3 >= j) {
                j2 = j3;
            }
            long j4 = this.mStrokes.get(i).mEndTime;
            if (j4 < j2 && j4 >= j) {
                j2 = j4;
            }
        }
        if (j2 == Long.MAX_VALUE) {
            return -1L;
        }
        return j2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPointsForTime(long j, TouchPoint[] touchPointArr) {
        int i = 0;
        for (int i2 = 0; i2 < this.mStrokes.size(); i2++) {
            StrokeDescription strokeDescription = this.mStrokes.get(i2);
            if (strokeDescription.hasPointForTime(j)) {
                touchPointArr[i].mStrokeId = strokeDescription.getId();
                touchPointArr[i].mContinuedStrokeId = strokeDescription.getContinuedStrokeId();
                touchPointArr[i].mIsStartOfPath = strokeDescription.getContinuedStrokeId() < 0 && j == strokeDescription.mStartTime;
                touchPointArr[i].mIsEndOfPath = !strokeDescription.willContinue() && j == strokeDescription.mEndTime;
                strokeDescription.getPosForTime(j, this.mTempPos);
                touchPointArr[i].mX = Math.round(this.mTempPos[0]);
                touchPointArr[i].mY = Math.round(this.mTempPos[1]);
                i++;
            }
        }
        return i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static long getTotalDuration(List<StrokeDescription> list) {
        long jMax = Long.MIN_VALUE;
        for (int i = 0; i < list.size(); i++) {
            jMax = Math.max(jMax, list.get(i).mEndTime);
        }
        return Math.max(jMax, 0L);
    }

    public StrokeDescription getStroke(int i) {
        return this.mStrokes.get(i);
    }

    public int getStrokeCount() {
        return this.mStrokes.size();
    }
}
