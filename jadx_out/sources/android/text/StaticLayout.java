package android.text;

import android.graphics.Paint;
import android.text.style.LineHeightSpan;
import android.util.Log;
import android.util.Pools;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.GrowingArrayUtils;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;

/* JADX INFO: loaded from: classes2.dex */
public class StaticLayout extends Layout {
    private static final char CHAR_NEW_LINE = '\n';
    private static final int COLUMNS_ELLIPSIZE = 7;
    private static final int COLUMNS_NORMAL = 5;
    private static final int DEFAULT_MAX_LINE_HEIGHT = -1;
    private static final int DESCENT = 2;
    private static final int DIR = 0;
    private static final int DIR_SHIFT = 30;
    private static final int ELLIPSIS_COUNT = 6;
    private static final int ELLIPSIS_START = 5;
    private static final int EXTRA = 3;
    private static final double EXTRA_ROUNDING = 0.5d;
    private static final int HYPHEN = 4;
    private static final int HYPHEN_MASK = 255;
    private static final int START = 0;
    private static final int START_MASK = 536870911;
    private static final int TAB = 0;
    private static final int TAB_INCREMENT = 20;
    private static final int TAB_MASK = 536870912;
    static final String TAG = "StaticLayout";
    private static final int TOP = 1;
    private int mBottomPadding;
    private int mColumns;
    private boolean mEllipsized;
    private int mEllipsizedWidth;
    private int[] mLeftIndents;
    private int[] mLeftPaddings;
    private int mLineCount;
    private Layout.Directions[] mLineDirections;
    private int[] mLines;
    private int mMaxLineHeight;
    private int mMaximumVisibleLineCount;
    private int[] mRightIndents;
    private int[] mRightPaddings;
    private int mTopPadding;

    public static final class Builder {
        private static final Pools.SynchronizedPool<Builder> sPool = new Pools.SynchronizedPool<>(3);
        private boolean mAddLastLineLineSpacing;
        private Layout.Alignment mAlignment;
        private int mBreakStrategy;
        private TextUtils.TruncateAt mEllipsize;
        private int mEllipsizedWidth;
        private int mEnd;
        private boolean mFallbackLineSpacing;
        private final Paint.FontMetricsInt mFontMetricsInt = new Paint.FontMetricsInt();
        private int mHyphenationFrequency;
        private boolean mIncludePad;
        private int mJustificationMode;
        private int[] mLeftIndents;
        private int[] mLeftPaddings;
        private int mMaxLines;
        private TextPaint mPaint;
        private int[] mRightIndents;
        private int[] mRightPaddings;
        private float mSpacingAdd;
        private float mSpacingMult;
        private int mStart;
        private CharSequence mText;
        private TextDirectionHeuristic mTextDir;
        private int mWidth;

        private Builder() {
        }

        public static Builder obtain(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3) {
            Builder builderAcquire = sPool.acquire();
            if (builderAcquire == null) {
                builderAcquire = new Builder();
            }
            builderAcquire.mText = charSequence;
            builderAcquire.mStart = i;
            builderAcquire.mEnd = i2;
            builderAcquire.mPaint = textPaint;
            builderAcquire.mWidth = i3;
            builderAcquire.mAlignment = Layout.Alignment.ALIGN_NORMAL;
            builderAcquire.mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
            builderAcquire.mSpacingMult = 1.0f;
            builderAcquire.mSpacingAdd = 0.0f;
            builderAcquire.mIncludePad = true;
            builderAcquire.mFallbackLineSpacing = false;
            builderAcquire.mEllipsizedWidth = i3;
            builderAcquire.mEllipsize = null;
            builderAcquire.mMaxLines = Integer.MAX_VALUE;
            builderAcquire.mBreakStrategy = 0;
            builderAcquire.mHyphenationFrequency = 0;
            builderAcquire.mJustificationMode = 0;
            return builderAcquire;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static void recycle(Builder builder) {
            builder.mPaint = null;
            builder.mText = null;
            builder.mLeftIndents = null;
            builder.mRightIndents = null;
            builder.mLeftPaddings = null;
            builder.mRightPaddings = null;
            sPool.release(builder);
        }

        public StaticLayout build() {
            StaticLayout staticLayout = new StaticLayout(this);
            recycle(this);
            return staticLayout;
        }

        void finish() {
            this.mText = null;
            this.mPaint = null;
            this.mLeftIndents = null;
            this.mRightIndents = null;
            this.mLeftPaddings = null;
            this.mRightPaddings = null;
        }

        Builder setAddLastLineLineSpacing(boolean z) {
            this.mAddLastLineLineSpacing = z;
            return this;
        }

        public Builder setAlignment(Layout.Alignment alignment) {
            this.mAlignment = alignment;
            return this;
        }

        public Builder setAvailablePaddings(int[] iArr, int[] iArr2) {
            this.mLeftPaddings = iArr;
            this.mRightPaddings = iArr2;
            return this;
        }

        public Builder setBreakStrategy(int i) {
            this.mBreakStrategy = i;
            return this;
        }

        public Builder setEllipsize(TextUtils.TruncateAt truncateAt) {
            this.mEllipsize = truncateAt;
            return this;
        }

        public Builder setEllipsizedWidth(int i) {
            this.mEllipsizedWidth = i;
            return this;
        }

        public Builder setHyphenationFrequency(int i) {
            this.mHyphenationFrequency = i;
            return this;
        }

        public Builder setIncludePad(boolean z) {
            this.mIncludePad = z;
            return this;
        }

        public Builder setIndents(int[] iArr, int[] iArr2) {
            this.mLeftIndents = iArr;
            this.mRightIndents = iArr2;
            return this;
        }

        public Builder setJustificationMode(int i) {
            this.mJustificationMode = i;
            return this;
        }

        public Builder setLineSpacing(float f, float f2) {
            this.mSpacingAdd = f;
            this.mSpacingMult = f2;
            return this;
        }

        public Builder setMaxLines(int i) {
            this.mMaxLines = i;
            return this;
        }

        public Builder setPaint(TextPaint textPaint) {
            this.mPaint = textPaint;
            return this;
        }

        public Builder setText(CharSequence charSequence) {
            return setText(charSequence, 0, charSequence.length());
        }

        public Builder setText(CharSequence charSequence, int i, int i2) {
            this.mText = charSequence;
            this.mStart = i;
            this.mEnd = i2;
            return this;
        }

        public Builder setTextDirection(TextDirectionHeuristic textDirectionHeuristic) {
            this.mTextDir = textDirectionHeuristic;
            return this;
        }

        public Builder setUseLineSpacingFromFallbacks(boolean z) {
            this.mFallbackLineSpacing = z;
            return this;
        }

        public Builder setWidth(int i) {
            this.mWidth = i;
            if (this.mEllipsize == null) {
                this.mEllipsizedWidth = i;
            }
            return this;
        }
    }

    static class LineBreaks {
        private static final int INITIAL_SIZE = 16;
        public int[] breaks = new int[16];
        public float[] widths = new float[16];
        public float[] ascents = new float[16];
        public float[] descents = new float[16];
        public int[] flags = new int[16];

        LineBreaks() {
        }
    }

    private StaticLayout(Builder builder) {
        super(builder.mEllipsize == null ? builder.mText : builder.mText instanceof Spanned ? new Layout.SpannedEllipsizer(builder.mText) : new Layout.Ellipsizer(builder.mText), builder.mPaint, builder.mWidth, builder.mAlignment, builder.mTextDir, builder.mSpacingMult, builder.mSpacingAdd);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        if (builder.mEllipsize != null) {
            Layout.Ellipsizer ellipsizer = (Layout.Ellipsizer) getText();
            ellipsizer.mLayout = this;
            ellipsizer.mWidth = builder.mEllipsizedWidth;
            ellipsizer.mMethod = builder.mEllipsize;
            this.mEllipsizedWidth = builder.mEllipsizedWidth;
            this.mColumns = 7;
        } else {
            this.mColumns = 5;
            this.mEllipsizedWidth = builder.mWidth;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = builder.mMaxLines;
        this.mLeftIndents = builder.mLeftIndents;
        this.mRightIndents = builder.mRightIndents;
        this.mLeftPaddings = builder.mLeftPaddings;
        this.mRightPaddings = builder.mRightPaddings;
        setJustificationMode(builder.mJustificationMode);
        generate(builder, builder.mIncludePad, builder.mIncludePad);
    }

    StaticLayout(CharSequence charSequence) {
        super(charSequence, null, 0, null, 0.0f, 0.0f);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        this.mColumns = 7;
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, i, i2, textPaint, i3, alignment, f, f2, z, null, 0);
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4) {
        this(charSequence, i, i2, textPaint, i3, alignment, TextDirectionHeuristics.FIRSTSTRONG_LTR, f, f2, z, truncateAt, i4, Integer.MAX_VALUE);
    }

    /* JADX WARN: Illegal instructions before constructor call */
    @Deprecated
    public StaticLayout(CharSequence charSequence, int i, int i2, TextPaint textPaint, int i3, Layout.Alignment alignment, TextDirectionHeuristic textDirectionHeuristic, float f, float f2, boolean z, TextUtils.TruncateAt truncateAt, int i4, int i5) {
        CharSequence spannedEllipsizer;
        if (truncateAt == null) {
            spannedEllipsizer = charSequence;
        } else {
            spannedEllipsizer = charSequence instanceof Spanned ? new Layout.SpannedEllipsizer(charSequence) : new Layout.Ellipsizer(charSequence);
        }
        super(spannedEllipsizer, textPaint, i3, alignment, textDirectionHeuristic, f, f2);
        this.mMaxLineHeight = -1;
        this.mMaximumVisibleLineCount = Integer.MAX_VALUE;
        Builder maxLines = Builder.obtain(charSequence, i, i2, textPaint, i3).setAlignment(alignment).setTextDirection(textDirectionHeuristic).setLineSpacing(f2, f).setIncludePad(z).setEllipsizedWidth(i4).setEllipsize(truncateAt).setMaxLines(i5);
        if (truncateAt != null) {
            Layout.Ellipsizer ellipsizer = (Layout.Ellipsizer) getText();
            ellipsizer.mLayout = this;
            ellipsizer.mWidth = i4;
            ellipsizer.mMethod = truncateAt;
            this.mEllipsizedWidth = i4;
            this.mColumns = 7;
        } else {
            this.mColumns = 5;
            this.mEllipsizedWidth = i3;
        }
        this.mLineDirections = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, 2);
        this.mLines = ArrayUtils.newUnpaddedIntArray(this.mColumns * 2);
        this.mMaximumVisibleLineCount = i5;
        generate(maxLines, maxLines.mIncludePad, maxLines.mIncludePad);
        Builder.recycle(maxLines);
    }

    @Deprecated
    public StaticLayout(CharSequence charSequence, TextPaint textPaint, int i, Layout.Alignment alignment, float f, float f2, boolean z) {
        this(charSequence, 0, charSequence.length(), textPaint, i, alignment, f, f2, z);
    }

    private void calculateEllipsis(int i, int i2, float[] fArr, int i3, float f, TextUtils.TruncateAt truncateAt, int i4, float f2, TextPaint textPaint, boolean z) {
        int i5;
        float totalInsets = f - getTotalInsets(i4);
        int i6 = 0;
        if (f2 <= totalInsets && !z) {
            this.mLines[(this.mColumns * i4) + 5] = 0;
            this.mLines[(this.mColumns * i4) + 6] = 0;
            return;
        }
        float fMeasureText = textPaint.measureText(TextUtils.getEllipsisString(truncateAt));
        int i7 = i2 - i;
        float f3 = 0.0f;
        if (truncateAt == TextUtils.TruncateAt.START) {
            if (this.mMaximumVisibleLineCount == 1) {
                int i8 = i7;
                float f4 = 0.0f;
                while (i8 > 0) {
                    f4 += fArr[((i8 - 1) + i) - i3];
                    if (f4 + fMeasureText > totalInsets) {
                        while (i8 < i7 && fArr[(i8 + i) - i3] == 0.0f) {
                            i8++;
                        }
                        break;
                    }
                    i8--;
                }
                i5 = i8;
            } else {
                if (Log.isLoggable(TAG, 5)) {
                    Log.w(TAG, "Start Ellipsis only supported with one line");
                }
                i5 = 0;
            }
        } else if (truncateAt == TextUtils.TruncateAt.END || truncateAt == TextUtils.TruncateAt.MARQUEE || truncateAt == TextUtils.TruncateAt.END_SMALL) {
            while (i6 < i7) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 + fMeasureText > totalInsets) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i7 - i6;
            if (z && i5 == 0 && i7 > 0) {
                i6 = i7 - 1;
                i5 = 1;
            }
        } else if (this.mMaximumVisibleLineCount == 1) {
            float f5 = totalInsets - fMeasureText;
            float f6 = f5 / 2.0f;
            int i9 = i7;
            float f7 = 0.0f;
            while (i9 > 0) {
                float f8 = fArr[((i9 - 1) + i) - i3] + f7;
                if (f8 > f6) {
                    while (i9 < i7 && fArr[(i9 + i) - i3] == 0.0f) {
                        i9++;
                    }
                    break;
                }
                i9--;
                f7 = f8;
            }
            float f9 = f5 - f7;
            while (i6 < i9) {
                f3 += fArr[(i6 + i) - i3];
                if (f3 > f9) {
                    break;
                } else {
                    i6++;
                }
            }
            i5 = i9 - i6;
        } else {
            if (Log.isLoggable(TAG, 5)) {
                Log.w(TAG, "Middle Ellipsis only supported with one line");
            }
            i5 = 0;
        }
        this.mEllipsized = true;
        this.mLines[(this.mColumns * i4) + 5] = i6;
        this.mLines[(this.mColumns * i4) + 6] = i5;
    }

    private float getTotalInsets(int i) {
        int i2 = this.mLeftIndents != null ? this.mLeftIndents[Math.min(i, this.mLeftIndents.length - 1)] : 0;
        if (this.mRightIndents != null) {
            i2 += this.mRightIndents[Math.min(i, this.mRightIndents.length - 1)];
        }
        return i2;
    }

    private static native int nComputeLineBreaks(long j, char[] cArr, long j2, int i, float f, int i2, float f2, int[] iArr, int i3, int i4, LineBreaks lineBreaks, int i5, int[] iArr2, float[] fArr, float[] fArr2, float[] fArr3, int[] iArr3, float[] fArr4);

    @CriticalNative
    private static native void nFinish(long j);

    @FastNative
    private static native long nInit(int i, int i2, boolean z, int[] iArr, int[] iArr2, int[] iArr3);

    /* JADX WARN: Code duplicated, block: B:49:0x011c  */
    /* JADX WARN: Code duplicated, block: B:52:0x0137  */
    private int out(CharSequence charSequence, int i, int i2, int i3, int i4, int i5, int i6, int i7, float f, float f2, LineHeightSpan[] lineHeightSpanArr, int[] iArr, Paint.FontMetricsInt fontMetricsInt, int i8, boolean z, MeasuredParagraph measuredParagraph, int i9, boolean z2, boolean z3, boolean z4, char[] cArr, float[] fArr, int i10, TextUtils.TruncateAt truncateAt, float f3, float f4, TextPaint textPaint, boolean z5) {
        int[] iArr2;
        int i11;
        TextUtils.TruncateAt truncateAt2;
        int i12;
        int i13;
        int i14;
        int i15;
        int i16;
        int i17;
        int i18;
        boolean z6;
        int i19;
        int i20;
        boolean z7;
        int i21;
        int i22 = i9;
        int i23 = i10;
        TextUtils.TruncateAt truncateAt3 = truncateAt;
        int i24 = this.mLineCount;
        int i25 = i24 * this.mColumns;
        boolean z8 = true;
        int i26 = i25 + this.mColumns + 1;
        int[] iArr3 = this.mLines;
        int paragraphDir = measuredParagraph.getParagraphDir();
        if (i26 >= iArr3.length) {
            int[] iArrNewUnpaddedIntArray = ArrayUtils.newUnpaddedIntArray(GrowingArrayUtils.growSize(i26));
            System.arraycopy(iArr3, 0, iArrNewUnpaddedIntArray, 0, iArr3.length);
            this.mLines = iArrNewUnpaddedIntArray;
            iArr2 = iArrNewUnpaddedIntArray;
        } else {
            iArr2 = iArr3;
        }
        if (i24 >= this.mLineDirections.length) {
            Layout.Directions[] directionsArr = (Layout.Directions[]) ArrayUtils.newUnpaddedArray(Layout.Directions.class, GrowingArrayUtils.growSize(i24));
            System.arraycopy(this.mLineDirections, 0, directionsArr, 0, this.mLineDirections.length);
            this.mLineDirections = directionsArr;
        }
        if (lineHeightSpanArr != null) {
            fontMetricsInt.ascent = i3;
            fontMetricsInt.descent = i4;
            fontMetricsInt.top = i5;
            fontMetricsInt.bottom = i6;
            int i27 = 0;
            while (i27 < lineHeightSpanArr.length) {
                if (lineHeightSpanArr[i27] instanceof LineHeightSpan.WithDensity) {
                    i21 = i27;
                    ((LineHeightSpan.WithDensity) lineHeightSpanArr[i27]).chooseHeight(charSequence, i, i2, iArr[i27], i7, fontMetricsInt, textPaint);
                } else {
                    i21 = i27;
                    lineHeightSpanArr[i21].chooseHeight(charSequence, i, i2, iArr[i21], i7, fontMetricsInt);
                }
                i27 = i21 + 1;
                i22 = i22;
                i23 = i23;
                truncateAt3 = truncateAt3;
                i24 = i24;
                z8 = true;
            }
            i11 = i24;
            truncateAt2 = truncateAt3;
            i12 = i23;
            i13 = i22;
            i15 = fontMetricsInt.ascent;
            i16 = fontMetricsInt.descent;
            i17 = fontMetricsInt.top;
            i14 = fontMetricsInt.bottom;
        } else {
            i11 = i24;
            truncateAt2 = truncateAt3;
            i12 = i23;
            i13 = i22;
            i14 = i6;
            i15 = i3;
            i16 = i4;
            i17 = i5;
        }
        boolean z9 = i11 == 0;
        boolean z10 = i11 + 1 == this.mMaximumVisibleLineCount;
        if (truncateAt2 != null) {
            if (z5) {
                i20 = 1;
                z7 = this.mLineCount + 1 == this.mMaximumVisibleLineCount;
                if ((((this.mMaximumVisibleLineCount != i20 && z5) || (z9 && !z5)) && truncateAt2 != TextUtils.TruncateAt.MARQUEE) || (!z9 && ((z10 || !z5) && truncateAt2 == TextUtils.TruncateAt.END))) {
                    calculateEllipsis(i, i2, fArr, i10, f3, truncateAt, i11, f4, textPaint, z7);
                }
            } else {
                i20 = 1;
            }
            if ((((this.mMaximumVisibleLineCount != i20 && z5) || (z9 && !z5)) && truncateAt2 != TextUtils.TruncateAt.MARQUEE) || (!z9 && ((z10 || !z5) && truncateAt2 == TextUtils.TruncateAt.END))) {
                calculateEllipsis(i, i2, fArr, i10, f3, truncateAt, i11, f4, textPaint, z7);
            }
        }
        if (this.mEllipsized) {
            z6 = true;
            i18 = i;
        } else {
            boolean z11 = i12 != i13 && i13 > 0 && charSequence.charAt(i13 + (-1)) == '\n';
            if (i2 != i13 || z11) {
                i18 = i;
                z6 = i18 == i13 && z11;
            } else {
                z6 = true;
                i18 = i;
            }
        }
        if (z9) {
            if (z3) {
                this.mTopPadding = i17 - i15;
            }
            if (z2) {
                i15 = i17;
            }
        }
        if (z6) {
            if (z3) {
                this.mBottomPadding = i14 - i16;
            }
            if (z2) {
                i16 = i14;
            }
        }
        if (!z || (!z4 && z6)) {
            i19 = 0;
        } else {
            double d = ((i16 - i15) * (f - 1.0f)) + f2;
            i19 = d >= 0.0d ? (int) (d + EXTRA_ROUNDING) : -((int) ((-d) + EXTRA_ROUNDING));
        }
        int i28 = i25 + 0;
        iArr2[i28] = i18;
        int i29 = i12;
        iArr2[i25 + 1] = i7;
        iArr2[i25 + 2] = i16 + i19;
        iArr2[i25 + 3] = i19;
        if (!this.mEllipsized && z10) {
            if (!z2) {
                i14 = i16;
            }
            this.mMaxLineHeight = i7 + (i14 - i15);
        }
        int i30 = i7 + (i16 - i15) + i19;
        iArr2[i25 + this.mColumns + 0] = i2;
        iArr2[i25 + this.mColumns + 1] = i30;
        iArr2[i28] = iArr2[i28] | (i8 & 536870912);
        iArr2[i25 + 4] = i8;
        iArr2[i28] = iArr2[i28] | (paragraphDir << 30);
        this.mLineDirections[i11] = measuredParagraph.getDirections(i18 - i29, i2 - i29);
        this.mLineCount++;
        return i30;
    }

    /* JADX WARN: Code duplicated, block: B:123:0x02dc  */
    /* JADX WARN: Code duplicated, block: B:126:0x02f0 A[Catch: all -> 0x0571, TryCatch #0 {all -> 0x0571, blocks: (B:52:0x0121, B:57:0x012f, B:59:0x0135, B:60:0x0146, B:62:0x0149, B:64:0x0165, B:65:0x016f, B:66:0x0176, B:85:0x01d3, B:87:0x01de, B:88:0x01e2, B:90:0x01e5, B:91:0x01f0, B:93:0x01fb, B:95:0x026d, B:97:0x0273, B:99:0x027a, B:109:0x0290, B:111:0x0299, B:113:0x029f, B:121:0x02bd, B:118:0x02b0, B:120:0x02b4, B:116:0x02aa, B:122:0x02cb, B:126:0x02f0, B:128:0x031a, B:129:0x031c, B:131:0x0320, B:132:0x0322, B:134:0x0326, B:135:0x0328, B:137:0x032c, B:140:0x0332, B:142:0x0337, B:145:0x0343, B:147:0x0348, B:152:0x0355, B:155:0x0363, B:157:0x0372, B:159:0x03ed, B:161:0x0403, B:163:0x040d, B:176:0x0515, B:180:0x0520, B:182:0x0526, B:70:0x0190, B:74:0x019a, B:76:0x019d, B:78:0x01a5, B:80:0x01b2, B:79:0x01b0, B:72:0x0194, B:56:0x0128), top: B:188:0x0121 }] */
    /* JADX WARN: Code duplicated, block: B:128:0x031a A[Catch: all -> 0x0571, TryCatch #0 {all -> 0x0571, blocks: (B:52:0x0121, B:57:0x012f, B:59:0x0135, B:60:0x0146, B:62:0x0149, B:64:0x0165, B:65:0x016f, B:66:0x0176, B:85:0x01d3, B:87:0x01de, B:88:0x01e2, B:90:0x01e5, B:91:0x01f0, B:93:0x01fb, B:95:0x026d, B:97:0x0273, B:99:0x027a, B:109:0x0290, B:111:0x0299, B:113:0x029f, B:121:0x02bd, B:118:0x02b0, B:120:0x02b4, B:116:0x02aa, B:122:0x02cb, B:126:0x02f0, B:128:0x031a, B:129:0x031c, B:131:0x0320, B:132:0x0322, B:134:0x0326, B:135:0x0328, B:137:0x032c, B:140:0x0332, B:142:0x0337, B:145:0x0343, B:147:0x0348, B:152:0x0355, B:155:0x0363, B:157:0x0372, B:159:0x03ed, B:161:0x0403, B:163:0x040d, B:176:0x0515, B:180:0x0520, B:182:0x0526, B:70:0x0190, B:74:0x019a, B:76:0x019d, B:78:0x01a5, B:80:0x01b2, B:79:0x01b0, B:72:0x0194, B:56:0x0128), top: B:188:0x0121 }] */
    /* JADX WARN: Code duplicated, block: B:131:0x0320 A[Catch: all -> 0x0571, TryCatch #0 {all -> 0x0571, blocks: (B:52:0x0121, B:57:0x012f, B:59:0x0135, B:60:0x0146, B:62:0x0149, B:64:0x0165, B:65:0x016f, B:66:0x0176, B:85:0x01d3, B:87:0x01de, B:88:0x01e2, B:90:0x01e5, B:91:0x01f0, B:93:0x01fb, B:95:0x026d, B:97:0x0273, B:99:0x027a, B:109:0x0290, B:111:0x0299, B:113:0x029f, B:121:0x02bd, B:118:0x02b0, B:120:0x02b4, B:116:0x02aa, B:122:0x02cb, B:126:0x02f0, B:128:0x031a, B:129:0x031c, B:131:0x0320, B:132:0x0322, B:134:0x0326, B:135:0x0328, B:137:0x032c, B:140:0x0332, B:142:0x0337, B:145:0x0343, B:147:0x0348, B:152:0x0355, B:155:0x0363, B:157:0x0372, B:159:0x03ed, B:161:0x0403, B:163:0x040d, B:176:0x0515, B:180:0x0520, B:182:0x0526, B:70:0x0190, B:74:0x019a, B:76:0x019d, B:78:0x01a5, B:80:0x01b2, B:79:0x01b0, B:72:0x0194, B:56:0x0128), top: B:188:0x0121 }] */
    /* JADX WARN: Code duplicated, block: B:134:0x0326 A[Catch: all -> 0x0571, TryCatch #0 {all -> 0x0571, blocks: (B:52:0x0121, B:57:0x012f, B:59:0x0135, B:60:0x0146, B:62:0x0149, B:64:0x0165, B:65:0x016f, B:66:0x0176, B:85:0x01d3, B:87:0x01de, B:88:0x01e2, B:90:0x01e5, B:91:0x01f0, B:93:0x01fb, B:95:0x026d, B:97:0x0273, B:99:0x027a, B:109:0x0290, B:111:0x0299, B:113:0x029f, B:121:0x02bd, B:118:0x02b0, B:120:0x02b4, B:116:0x02aa, B:122:0x02cb, B:126:0x02f0, B:128:0x031a, B:129:0x031c, B:131:0x0320, B:132:0x0322, B:134:0x0326, B:135:0x0328, B:137:0x032c, B:140:0x0332, B:142:0x0337, B:145:0x0343, B:147:0x0348, B:152:0x0355, B:155:0x0363, B:157:0x0372, B:159:0x03ed, B:161:0x0403, B:163:0x040d, B:176:0x0515, B:180:0x0520, B:182:0x0526, B:70:0x0190, B:74:0x019a, B:76:0x019d, B:78:0x01a5, B:80:0x01b2, B:79:0x01b0, B:72:0x0194, B:56:0x0128), top: B:188:0x0121 }] */
    /* JADX WARN: Code duplicated, block: B:137:0x032c A[Catch: all -> 0x0571, TryCatch #0 {all -> 0x0571, blocks: (B:52:0x0121, B:57:0x012f, B:59:0x0135, B:60:0x0146, B:62:0x0149, B:64:0x0165, B:65:0x016f, B:66:0x0176, B:85:0x01d3, B:87:0x01de, B:88:0x01e2, B:90:0x01e5, B:91:0x01f0, B:93:0x01fb, B:95:0x026d, B:97:0x0273, B:99:0x027a, B:109:0x0290, B:111:0x0299, B:113:0x029f, B:121:0x02bd, B:118:0x02b0, B:120:0x02b4, B:116:0x02aa, B:122:0x02cb, B:126:0x02f0, B:128:0x031a, B:129:0x031c, B:131:0x0320, B:132:0x0322, B:134:0x0326, B:135:0x0328, B:137:0x032c, B:140:0x0332, B:142:0x0337, B:145:0x0343, B:147:0x0348, B:152:0x0355, B:155:0x0363, B:157:0x0372, B:159:0x03ed, B:161:0x0403, B:163:0x040d, B:176:0x0515, B:180:0x0520, B:182:0x0526, B:70:0x0190, B:74:0x019a, B:76:0x019d, B:78:0x01a5, B:80:0x01b2, B:79:0x01b0, B:72:0x0194, B:56:0x0128), top: B:188:0x0121 }] */
    /* JADX WARN: Code duplicated, block: B:92:0x01f8  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r9v24, types: [int] */
    /* JADX WARN: Type inference failed for: r9v37 */
    /* JADX WARN: Type inference failed for: r9v38 */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:144:0x0341
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:162)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeEndlessLoop(LoopRegionMaker.java:590)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:82)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:111)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:109)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    void generate(android.text.StaticLayout.Builder r94, boolean r95, boolean r96) {
        /*
            Method dump skipped, instruction units count: 1398
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.text.StaticLayout.generate(android.text.StaticLayout$Builder, boolean, boolean):void");
    }

    @Override // android.text.Layout
    public int getBottomPadding() {
        return this.mBottomPadding;
    }

    @Override // android.text.Layout
    public int getEllipsisCount(int i) {
        if (this.mColumns < 7) {
            return 0;
        }
        return this.mLines[(this.mColumns * i) + 6];
    }

    @Override // android.text.Layout
    public int getEllipsisStart(int i) {
        if (this.mColumns < 7) {
            return 0;
        }
        return this.mLines[(this.mColumns * i) + 5];
    }

    @Override // android.text.Layout
    public int getEllipsizedWidth() {
        return this.mEllipsizedWidth;
    }

    @Override // android.text.Layout
    public int getHeight(boolean z) {
        if (z && this.mLineCount >= this.mMaximumVisibleLineCount && this.mMaxLineHeight == -1 && Log.isLoggable(TAG, 5)) {
            Log.w(TAG, "maxLineHeight should not be -1.  maxLines:" + this.mMaximumVisibleLineCount + " lineCount:" + this.mLineCount);
        }
        return (!z || this.mLineCount < this.mMaximumVisibleLineCount || this.mMaxLineHeight == -1) ? super.getHeight() : this.mMaxLineHeight;
    }

    @Override // android.text.Layout
    public int getHyphen(int i) {
        return this.mLines[(this.mColumns * i) + 4] & 255;
    }

    @Override // android.text.Layout
    public int getIndentAdjust(int i, Layout.Alignment alignment) {
        if (alignment == Layout.Alignment.ALIGN_LEFT) {
            if (this.mLeftIndents == null) {
                return 0;
            }
            return this.mLeftIndents[Math.min(i, this.mLeftIndents.length - 1)];
        }
        if (alignment == Layout.Alignment.ALIGN_RIGHT) {
            if (this.mRightIndents == null) {
                return 0;
            }
            return -this.mRightIndents[Math.min(i, this.mRightIndents.length - 1)];
        }
        if (alignment == Layout.Alignment.ALIGN_CENTER) {
            return ((this.mLeftIndents != null ? this.mLeftIndents[Math.min(i, this.mLeftIndents.length - 1)] : 0) - (this.mRightIndents != null ? this.mRightIndents[Math.min(i, this.mRightIndents.length - 1)] : 0)) >> 1;
        }
        throw new AssertionError("unhandled alignment " + alignment);
    }

    @Override // android.text.Layout
    public boolean getLineContainsTab(int i) {
        return (this.mLines[(this.mColumns * i) + 0] & 536870912) != 0;
    }

    @Override // android.text.Layout
    public int getLineCount() {
        return this.mLineCount;
    }

    @Override // android.text.Layout
    public int getLineDescent(int i) {
        return this.mLines[(this.mColumns * i) + 2];
    }

    @Override // android.text.Layout
    public final Layout.Directions getLineDirections(int i) {
        if (i <= getLineCount()) {
            return this.mLineDirections[i];
        }
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override // android.text.Layout
    public int getLineExtra(int i) {
        return this.mLines[(this.mColumns * i) + 3];
    }

    @Override // android.text.Layout
    public int getLineForVertical(int i) {
        int i2 = this.mLineCount;
        int[] iArr = this.mLines;
        int i3 = -1;
        while (i2 - i3 > 1) {
            int i4 = (i2 + i3) >> 1;
            if (iArr[(this.mColumns * i4) + 1] > i) {
                i2 = i4;
            } else {
                i3 = i4;
            }
        }
        if (i3 < 0) {
            return 0;
        }
        return i3;
    }

    @Override // android.text.Layout
    public int getLineStart(int i) {
        return this.mLines[(this.mColumns * i) + 0] & 536870911;
    }

    @Override // android.text.Layout
    public int getLineTop(int i) {
        return this.mLines[(this.mColumns * i) + 1];
    }

    @Override // android.text.Layout
    public int getParagraphDirection(int i) {
        return this.mLines[(this.mColumns * i) + 0] >> 30;
    }

    @Override // android.text.Layout
    public int getTopPadding() {
        return this.mTopPadding;
    }
}
