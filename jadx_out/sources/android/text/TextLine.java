package android.text;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.style.CharacterStyle;
import android.text.style.MetricAffectingSpan;
import android.text.style.ReplacementSpan;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes2.dex */
@VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
public class TextLine {
    private static final boolean DEBUG = false;
    private static final int TAB_INCREMENT = 20;
    private static final TextLine[] sCached = new TextLine[3];
    private float mAddedWidth;
    private char[] mChars;
    private boolean mCharsValid;
    private PrecomputedText mComputed;
    private int mDir;
    private Layout.Directions mDirections;
    private boolean mHasTabs;
    private int mLen;
    private TextPaint mPaint;
    private Spanned mSpanned;
    private int mStart;
    private Layout.TabStops mTabs;
    private CharSequence mText;
    private final TextPaint mWorkPaint = new TextPaint();
    private final TextPaint mActivePaint = new TextPaint();
    private final SpanSet<MetricAffectingSpan> mMetricAffectingSpanSpanSet = new SpanSet<>(MetricAffectingSpan.class);
    private final SpanSet<CharacterStyle> mCharacterStyleSpanSet = new SpanSet<>(CharacterStyle.class);
    private final SpanSet<ReplacementSpan> mReplacementSpanSpanSet = new SpanSet<>(ReplacementSpan.class);
    private final DecorationInfo mDecorationInfo = new DecorationInfo();
    private final ArrayList<DecorationInfo> mDecorations = new ArrayList<>();

    private static final class DecorationInfo {
        public int end;
        public boolean isStrikeThruText;
        public boolean isUnderlineText;
        public int start;
        public int underlineColor;
        public float underlineThickness;

        private DecorationInfo() {
            this.start = -1;
            this.end = -1;
        }

        public DecorationInfo copyInfo() {
            DecorationInfo decorationInfo = new DecorationInfo();
            decorationInfo.isStrikeThruText = this.isStrikeThruText;
            decorationInfo.isUnderlineText = this.isUnderlineText;
            decorationInfo.underlineColor = this.underlineColor;
            decorationInfo.underlineThickness = this.underlineThickness;
            return decorationInfo;
        }

        public boolean hasDecoration() {
            return this.isStrikeThruText || this.isUnderlineText || this.underlineColor != 0;
        }
    }

    private int adjustHyphenEdit(int i, int i2, int i3) {
        if (i > 0) {
            i3 &= -25;
        }
        return i2 < this.mLen ? i3 & (-8) : i3;
    }

    private int countStretchableSpaces(int i, int i2) {
        int i3 = 0;
        while (i < i2) {
            if (isStretchableWhitespace(this.mCharsValid ? this.mChars[i] : this.mText.charAt(this.mStart + i))) {
                i3++;
            }
            i++;
        }
        return i3;
    }

    private float drawRun(Canvas canvas, int i, int i2, boolean z, float f, int i3, int i4, int i5, boolean z2) {
        if ((this.mDir == 1) != z) {
            return handleRun(i, i2, i2, z, canvas, f, i3, i4, i5, null, z2);
        }
        float f2 = -measureRun(i, i2, i2, z, null);
        handleRun(i, i2, i2, z, canvas, f + f2, i3, i4, i5, null, false);
        return f2;
    }

    private static void drawStroke(TextPaint textPaint, Canvas canvas, int i, float f, float f2, float f3, float f4, float f5) {
        float f6 = f5 + textPaint.baselineShift + f;
        int color = textPaint.getColor();
        Paint.Style style = textPaint.getStyle();
        boolean zIsAntiAlias = textPaint.isAntiAlias();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setColor(i);
        canvas.drawRect(f3, f6, f4, f6 + f2, textPaint);
        textPaint.setStyle(style);
        textPaint.setColor(color);
        textPaint.setAntiAlias(zIsAntiAlias);
    }

    private void drawTextRun(Canvas canvas, TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, float f, int i5) {
        if (this.mCharsValid) {
            canvas.drawTextRun(this.mChars, i, i2 - i, i3, i4 - i3, f, i5, z, textPaint);
        } else {
            int i6 = this.mStart;
            canvas.drawTextRun(this.mText, i6 + i, i6 + i2, i6 + i3, i6 + i4, f, i5, z, textPaint);
        }
    }

    private static void expandMetricsFromPaint(Paint.FontMetricsInt fontMetricsInt, TextPaint textPaint) {
        int i = fontMetricsInt.top;
        int i2 = fontMetricsInt.ascent;
        int i3 = fontMetricsInt.descent;
        int i4 = fontMetricsInt.bottom;
        int i5 = fontMetricsInt.leading;
        textPaint.getFontMetricsInt(fontMetricsInt);
        updateMetrics(fontMetricsInt, i, i2, i3, i4, i5);
    }

    private void extractDecorationInfo(TextPaint textPaint, DecorationInfo decorationInfo) {
        decorationInfo.isStrikeThruText = textPaint.isStrikeThruText();
        if (decorationInfo.isStrikeThruText) {
            textPaint.setStrikeThruText(false);
        }
        decorationInfo.isUnderlineText = textPaint.isUnderlineText();
        if (decorationInfo.isUnderlineText) {
            textPaint.setUnderlineText(false);
        }
        decorationInfo.underlineColor = textPaint.underlineColor;
        decorationInfo.underlineThickness = textPaint.underlineThickness;
        textPaint.setUnderlineText(0, 0.0f);
    }

    private int getOffsetBeforeAfter(int i, int i2, int i3, boolean z, int i4, boolean z2) {
        if (i >= 0) {
            if (i4 != (z2 ? this.mLen : 0)) {
                TextPaint textPaint = this.mWorkPaint;
                textPaint.set(this.mPaint);
                textPaint.setWordSpacing(this.mAddedWidth);
                if (this.mSpanned != null) {
                    int i5 = z2 ? i4 + 1 : i4;
                    int i6 = this.mStart + i3;
                    while (true) {
                        i3 = this.mSpanned.nextSpanTransition(this.mStart + i2, i6, MetricAffectingSpan.class) - this.mStart;
                        if (i3 >= i5) {
                            break;
                        }
                        i2 = i3;
                    }
                    MetricAffectingSpan[] metricAffectingSpanArr = (MetricAffectingSpan[]) TextUtils.removeEmptySpans((MetricAffectingSpan[]) this.mSpanned.getSpans(this.mStart + i2, this.mStart + i3, MetricAffectingSpan.class), this.mSpanned, MetricAffectingSpan.class);
                    if (metricAffectingSpanArr.length > 0) {
                        ReplacementSpan replacementSpan = null;
                        for (MetricAffectingSpan metricAffectingSpan : metricAffectingSpanArr) {
                            if (metricAffectingSpan instanceof ReplacementSpan) {
                                replacementSpan = (ReplacementSpan) metricAffectingSpan;
                            } else {
                                metricAffectingSpan.updateMeasureState(textPaint);
                            }
                        }
                        if (replacementSpan != null) {
                            return z2 ? i3 : i2;
                        }
                    }
                }
                int i7 = i2;
                int i8 = z2 ? 0 : 2;
                if (this.mCharsValid) {
                    return textPaint.getTextRunCursor(this.mChars, i7, i3 - i7, z ? 1 : 0, i4, i8);
                }
                return textPaint.getTextRunCursor(this.mText, i7 + this.mStart, this.mStart + i3, z ? 1 : 0, this.mStart + i4, i8) - this.mStart;
            }
        }
        return z2 ? TextUtils.getOffsetAfter(this.mText, i4 + this.mStart) - this.mStart : TextUtils.getOffsetBefore(this.mText, i4 + this.mStart) - this.mStart;
    }

    private float getRunAdvance(TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, int i5) {
        if (this.mCharsValid) {
            return textPaint.getRunAdvance(this.mChars, i, i2, i3, i4, z, i5);
        }
        int i6 = this.mStart;
        return this.mComputed == null ? textPaint.getRunAdvance(this.mText, i6 + i, i6 + i2, i6 + i3, i6 + i4, z, i6 + i5) : this.mComputed.getWidth(i + i6, i2 + i6);
    }

    private float handleReplacement(ReplacementSpan replacementSpan, TextPaint textPaint, int i, int i2, boolean z, Canvas canvas, float f, int i3, int i4, int i5, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        float f2;
        int i11 = this.mStart + i;
        int i12 = this.mStart + i2;
        if (z2 || (canvas != null && z)) {
            boolean z3 = fontMetricsInt != null;
            if (z3) {
                int i13 = fontMetricsInt.top;
                i6 = i13;
                i7 = fontMetricsInt.ascent;
                i8 = fontMetricsInt.descent;
                i9 = fontMetricsInt.bottom;
                i10 = fontMetricsInt.leading;
            } else {
                i6 = 0;
                i7 = 0;
                i8 = 0;
                i9 = 0;
                i10 = 0;
            }
            float size = replacementSpan.getSize(textPaint, this.mText, i11, i12, fontMetricsInt);
            if (z3) {
                f2 = size;
                updateMetrics(fontMetricsInt, i6, i7, i8, i9, i10);
            } else {
                f2 = size;
            }
        } else {
            f2 = 0.0f;
        }
        float f3 = f2;
        if (canvas != null) {
            replacementSpan.draw(canvas, this.mText, i11, i12, z ? f - f3 : f, i3, i4, i5, textPaint);
        }
        return z ? -f3 : f3;
    }

    /* JADX WARN: Code duplicated, block: B:13:0x0026  */
    /* JADX WARN: Code duplicated, block: B:71:0x01e7  */
    /* JADX WARN: Code duplicated, block: B:72:0x01f9  */
    private float handleRun(int i, int i2, int i3, boolean z, Canvas canvas, float f, int i4, int i5, int i6, Paint.FontMetricsInt fontMetricsInt, boolean z2) {
        boolean z3;
        int i7;
        float fHandleText;
        int i8;
        TextPaint textPaint;
        TextPaint textPaint2;
        int i9;
        TextLine textLine = this;
        int i10 = i2;
        int i11 = i3;
        if (i10 < i || i10 > i11) {
            throw new IndexOutOfBoundsException("measureLimit (" + i2 + ") is out of start (" + i + ") and limit (" + i3 + ") bounds");
        }
        if (i == i10) {
            TextPaint textPaint3 = textLine.mWorkPaint;
            textPaint3.set(textLine.mPaint);
            if (fontMetricsInt == null) {
                return 0.0f;
            }
            expandMetricsFromPaint(fontMetricsInt, textPaint3);
            return 0.0f;
        }
        if (textLine.mSpanned == null) {
            z3 = false;
        } else {
            textLine.mMetricAffectingSpanSpanSet.init(textLine.mSpanned, textLine.mStart + i, textLine.mStart + i11);
            textLine.mCharacterStyleSpanSet.init(textLine.mSpanned, textLine.mStart + i, textLine.mStart + i11);
            if (textLine.mMetricAffectingSpanSpanSet.numberOfSpans == 0 && textLine.mCharacterStyleSpanSet.numberOfSpans == 0) {
                z3 = false;
            } else {
                z3 = true;
            }
        }
        if (!z3) {
            TextPaint textPaint4 = textLine.mWorkPaint;
            textPaint4.set(textLine.mPaint);
            textPaint4.setHyphenEdit(textLine.adjustHyphenEdit(i, i11, textPaint4.getHyphenEdit()));
            return handleText(textPaint4, i, i3, i, i3, z, canvas, f, i4, i5, i6, fontMetricsInt, z2, i2, null);
        }
        float fHandleText2 = f;
        int i12 = i;
        while (i12 < i10) {
            TextPaint textPaint5 = textLine.mWorkPaint;
            textPaint5.set(textLine.mPaint);
            int nextTransition = textLine.mMetricAffectingSpanSpanSet.getNextTransition(textLine.mStart + i12, textLine.mStart + i11) - textLine.mStart;
            int iMin = Math.min(nextTransition, i10);
            ReplacementSpan replacementSpan = null;
            for (int i13 = 0; i13 < textLine.mMetricAffectingSpanSpanSet.numberOfSpans; i13++) {
                if (textLine.mMetricAffectingSpanSpanSet.spanStarts[i13] < textLine.mStart + iMin && textLine.mMetricAffectingSpanSpanSet.spanEnds[i13] > textLine.mStart + i12) {
                    MetricAffectingSpan metricAffectingSpan = textLine.mMetricAffectingSpanSpanSet.spans[i13];
                    if (metricAffectingSpan instanceof ReplacementSpan) {
                        replacementSpan = (ReplacementSpan) metricAffectingSpan;
                    } else {
                        metricAffectingSpan.updateDrawState(textPaint5);
                    }
                }
            }
            if (replacementSpan != null) {
                i7 = nextTransition;
                fHandleText = handleReplacement(replacementSpan, textPaint5, i12, iMin, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || iMin < i10);
            } else {
                i7 = nextTransition;
                TextPaint textPaint6 = textLine.mActivePaint;
                textPaint6.set(textLine.mPaint);
                DecorationInfo decorationInfo = textLine.mDecorationInfo;
                textLine.mDecorations.clear();
                int i14 = iMin;
                int i15 = i12;
                int i16 = i15;
                while (i16 < iMin) {
                    int nextTransition2 = textLine.mCharacterStyleSpanSet.getNextTransition(textLine.mStart + i16, textLine.mStart + i7) - textLine.mStart;
                    int iMin2 = Math.min(nextTransition2, iMin);
                    textPaint5.set(textLine.mPaint);
                    for (int i17 = 0; i17 < textLine.mCharacterStyleSpanSet.numberOfSpans; i17++) {
                        if (textLine.mCharacterStyleSpanSet.spanStarts[i17] < textLine.mStart + iMin2 && textLine.mCharacterStyleSpanSet.spanEnds[i17] > textLine.mStart + i16) {
                            textLine.mCharacterStyleSpanSet.spans[i17].updateDrawState(textPaint5);
                        }
                    }
                    textLine.extractDecorationInfo(textPaint5, decorationInfo);
                    if (i16 == i12) {
                        textPaint6.set(textPaint5);
                    } else {
                        if (!textPaint5.hasEqualAttributes(textPaint6)) {
                            textPaint6.setHyphenEdit(textLine.adjustHyphenEdit(i15, i14, textLine.mPaint.getHyphenEdit()));
                            i8 = i16;
                            fHandleText2 += handleText(textPaint6, i15, i14, i12, i7, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || i14 < i10, Math.min(i14, iMin), textLine.mDecorations);
                            textPaint = textPaint5;
                            textPaint2 = textPaint6;
                            textPaint2.set(textPaint);
                            textLine = this;
                            textLine.mDecorations.clear();
                            i15 = i8;
                        }
                        if (decorationInfo.hasDecoration()) {
                            DecorationInfo decorationInfoCopyInfo = decorationInfo.copyInfo();
                            decorationInfoCopyInfo.start = i8;
                            i9 = nextTransition2;
                            decorationInfoCopyInfo.end = i9;
                            textLine.mDecorations.add(decorationInfoCopyInfo);
                        } else {
                            i9 = nextTransition2;
                        }
                        textPaint5 = textPaint;
                        textPaint6 = textPaint2;
                        i14 = i9;
                        i16 = i14;
                        iMin = iMin;
                        decorationInfo = decorationInfo;
                        i12 = i12;
                        i10 = i2;
                    }
                    i8 = i16;
                    textPaint = textPaint5;
                    textPaint2 = textPaint6;
                    if (decorationInfo.hasDecoration()) {
                        DecorationInfo decorationInfoCopyInfo2 = decorationInfo.copyInfo();
                        decorationInfoCopyInfo2.start = i8;
                        i9 = nextTransition2;
                        decorationInfoCopyInfo2.end = i9;
                        textLine.mDecorations.add(decorationInfoCopyInfo2);
                    } else {
                        i9 = nextTransition2;
                    }
                    textPaint5 = textPaint;
                    textPaint6 = textPaint2;
                    i14 = i9;
                    i16 = i14;
                    iMin = iMin;
                    decorationInfo = decorationInfo;
                    i12 = i12;
                    i10 = i2;
                }
                int i18 = iMin;
                int i19 = i12;
                TextPaint textPaint7 = textPaint6;
                textPaint7.setHyphenEdit(textLine.adjustHyphenEdit(i15, i14, textLine.mPaint.getHyphenEdit()));
                fHandleText = handleText(textPaint7, i15, i14, i19, i7, z, canvas, fHandleText2, i4, i5, i6, fontMetricsInt, z2 || i14 < i2, Math.min(i14, i18), textLine.mDecorations);
            }
            fHandleText2 += fHandleText;
            i12 = i7;
            i11 = i3;
            i10 = i2;
            textLine = this;
        }
        return fHandleText2 - f;
    }

    private float handleText(TextPaint textPaint, int i, int i2, int i3, int i4, boolean z, Canvas canvas, float f, int i5, int i6, int i7, Paint.FontMetricsInt fontMetricsInt, boolean z2, int i8, ArrayList<DecorationInfo> arrayList) {
        float f2;
        float f3;
        float f4;
        float f5;
        float f6;
        int i9 = i;
        textPaint.setWordSpacing(this.mAddedWidth);
        if (fontMetricsInt != null) {
            expandMetricsFromPaint(fontMetricsInt, textPaint);
        }
        if (i2 == i9) {
            return 0.0f;
        }
        int size = arrayList == null ? 0 : arrayList.size();
        float runAdvance = (z2 || !(canvas == null || (textPaint.bgColor == 0 && size == 0 && !z))) ? getRunAdvance(textPaint, i, i2, i3, i4, z, i8) : 0.0f;
        if (canvas != null) {
            if (z) {
                f4 = f;
                f3 = f - runAdvance;
            } else {
                f3 = f;
                f4 = f + runAdvance;
            }
            if (textPaint.bgColor != 0) {
                int color = textPaint.getColor();
                Paint.Style style = textPaint.getStyle();
                textPaint.setColor(textPaint.bgColor);
                textPaint.setStyle(Paint.Style.FILL);
                canvas.drawRect(f3, i5, f4, i7, textPaint);
                textPaint.setStyle(style);
                textPaint.setColor(color);
            }
            if (size != 0) {
                int i10 = 0;
                while (i10 < size) {
                    DecorationInfo decorationInfo = arrayList.get(i10);
                    int iMax = Math.max(decorationInfo.start, i9);
                    int iMin = Math.min(decorationInfo.end, i8);
                    float f7 = runAdvance;
                    int i11 = i10;
                    float runAdvance2 = getRunAdvance(textPaint, i, i2, i3, i4, z, iMax);
                    float runAdvance3 = getRunAdvance(textPaint, i, i2, i3, i4, z, iMin);
                    if (z) {
                        float f8 = f4 - runAdvance2;
                        f5 = f4 - runAdvance3;
                        f6 = f8;
                    } else {
                        f5 = runAdvance2 + f3;
                        f6 = runAdvance3 + f3;
                    }
                    if (decorationInfo.underlineColor != 0) {
                        drawStroke(textPaint, canvas, decorationInfo.underlineColor, textPaint.getUnderlinePosition(), decorationInfo.underlineThickness, f5, f6, i6);
                    }
                    if (decorationInfo.isUnderlineText) {
                        drawStroke(textPaint, canvas, textPaint.getColor(), textPaint.getUnderlinePosition(), Math.max(textPaint.getUnderlineThickness(), 1.0f), f5, f6, i6);
                    }
                    if (decorationInfo.isStrikeThruText) {
                        drawStroke(textPaint, canvas, textPaint.getColor(), textPaint.getStrikeThruPosition(), Math.max(textPaint.getStrikeThruThickness(), 1.0f), f5, f6, i6);
                    }
                    i10 = i11 + 1;
                    runAdvance = f7;
                    size = size;
                    i9 = i;
                }
            }
            f2 = runAdvance;
            drawTextRun(canvas, textPaint, i, i2, i3, i4, z, f3, i6 + textPaint.baselineShift);
        } else {
            f2 = runAdvance;
        }
        return z ? -f2 : f2;
    }

    public static boolean isLineEndSpace(char c) {
        return c == ' ' || c == '\t' || c == 5760 || (8192 <= c && c <= 8202 && c != 8199) || c == 8287 || c == 12288;
    }

    private boolean isStretchableWhitespace(int i) {
        return i == 32;
    }

    private float measureRun(int i, int i2, int i3, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        return handleRun(i, i2, i3, z, null, 0.0f, 0, 0, 0, fontMetricsInt, true);
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public static TextLine obtain() {
        synchronized (sCached) {
            int length = sCached.length;
            do {
                length--;
                if (length < 0) {
                    return new TextLine();
                }
            } while (sCached[length] == null);
            TextLine textLine = sCached[length];
            sCached[length] = null;
            return textLine;
        }
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public static TextLine recycle(TextLine textLine) {
        textLine.mText = null;
        textLine.mPaint = null;
        textLine.mDirections = null;
        textLine.mSpanned = null;
        textLine.mTabs = null;
        textLine.mChars = null;
        textLine.mComputed = null;
        textLine.mMetricAffectingSpanSpanSet.recycle();
        textLine.mCharacterStyleSpanSet.recycle();
        textLine.mReplacementSpanSpanSet.recycle();
        synchronized (sCached) {
            for (int i = 0; i < sCached.length; i++) {
                if (sCached[i] == null) {
                    sCached[i] = textLine;
                    break;
                }
            }
        }
        return null;
    }

    static void updateMetrics(Paint.FontMetricsInt fontMetricsInt, int i, int i2, int i3, int i4, int i5) {
        fontMetricsInt.top = Math.min(fontMetricsInt.top, i);
        fontMetricsInt.ascent = Math.min(fontMetricsInt.ascent, i2);
        fontMetricsInt.descent = Math.max(fontMetricsInt.descent, i3);
        fontMetricsInt.bottom = Math.max(fontMetricsInt.bottom, i4);
        fontMetricsInt.leading = Math.max(fontMetricsInt.leading, i5);
    }

    void draw(Canvas canvas, float f, int i, int i2, int i3) {
        int i4;
        int i5;
        int i6;
        int iCodePointAt;
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                drawRun(canvas, 0, this.mLen, false, f, i, i2, i3, false);
                return;
            } else if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                drawRun(canvas, 0, this.mLen, true, f, i, i2, i3, false);
                return;
            }
        }
        float f2 = 0.0f;
        int[] iArr = this.mDirections.mDirections;
        int length = iArr.length - 2;
        int i7 = 0;
        while (i7 < iArr.length) {
            int i8 = iArr[i7];
            int i9 = i7 + 1;
            int i10 = (iArr[i9] & 67108863) + i8;
            if (i10 > this.mLen) {
                i10 = this.mLen;
            }
            int i11 = i10;
            if (i8 > this.mLen) {
                return;
            }
            boolean z = (iArr[i9] & 67108864) != 0;
            float fDrawRun = f2;
            int i12 = this.mHasTabs ? i8 : i11;
            int i13 = i8;
            while (i12 <= i11) {
                if (!this.mHasTabs || i12 >= i11) {
                    i4 = 0;
                } else {
                    char c = this.mChars[i12];
                    if (c < 55296 || c >= 56320 || (i5 = i12 + 1) >= i11 || (iCodePointAt = Character.codePointAt(this.mChars, i12)) <= 65535) {
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i6 = iCodePointAt;
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i4 = i6;
                    } else {
                        i6 = c;
                        i6 = c;
                        i6 = c;
                        i6 = iCodePointAt;
                    }
                    i12 = i5 + 1;
                }
                if (i12 == i11 || i4 == 9) {
                    int i14 = i4;
                    int i15 = i12;
                    fDrawRun += drawRun(canvas, i13, i12, z, f + fDrawRun, i, i2, i3, (i7 == length && i12 == this.mLen) ? false : true);
                    if (i14 == 9) {
                        fDrawRun = this.mDir * nextTab(this.mDir * fDrawRun);
                    }
                    i13 = i15 + 1;
                    i5 = i15;
                } else {
                    i5 = i12;
                }
                i12 = i5 + 1;
            }
            i7 += 2;
            f2 = fDrawRun;
        }
    }

    /* JADX WARN: Code duplicated, block: B:61:0x00bb  */
    int getOffsetToLeftRightOf(int i, boolean z) {
        int length;
        int i2;
        int i3;
        boolean z2;
        int i4;
        int offsetBeforeAfter;
        int i5;
        int i6;
        int i7;
        boolean z3;
        int i8;
        int i9 = this.mLen;
        boolean z4 = this.mDir == -1;
        int[] iArr = this.mDirections.mDirections;
        if (i == 0) {
            offsetBeforeAfter = -1;
            i4 = 0;
            length = -2;
        } else if (i == i9) {
            i4 = 0;
            length = iArr.length;
            offsetBeforeAfter = -1;
        } else {
            int i10 = i9;
            int i11 = 0;
            int i12 = 0;
            while (true) {
                if (i11 >= iArr.length) {
                    length = i11;
                    i2 = i10;
                    i3 = i12;
                    z2 = false;
                    i4 = 0;
                    break;
                }
                i12 = iArr[i11] + 0;
                if (i >= i12) {
                    int i13 = i11 + 1;
                    int i14 = (iArr[i13] & 67108863) + i12;
                    if (i14 > i9) {
                        i14 = i9;
                    }
                    if (i < i14) {
                        int i15 = (iArr[i13] >>> 26) & 63;
                        if (i != i12) {
                            length = i11;
                            i4 = i15;
                            i3 = i12;
                            i2 = i14;
                            z2 = false;
                            break;
                        }
                        int i16 = i - 1;
                        int i17 = 0;
                        while (true) {
                            if (i17 >= iArr.length) {
                                i5 = i12;
                                z2 = false;
                                break;
                            }
                            i5 = iArr[i17] + 0;
                            if (i16 >= i5) {
                                int i18 = i17 + 1;
                                int i19 = i5 + (iArr[i18] & 67108863);
                                if (i19 > i9) {
                                    i19 = i9;
                                }
                                if (i16 < i19 && (i6 = (iArr[i18] >>> 26) & 63) < i15) {
                                    i11 = i17;
                                    i15 = i6;
                                    i14 = i19;
                                    z2 = true;
                                    break;
                                }
                            }
                            i17 += 2;
                        }
                        length = i11;
                        i4 = i15;
                        i3 = i5;
                        i2 = i14;
                        break;
                    }
                    i10 = i14;
                }
                i11 += 2;
            }
            if (length == iArr.length) {
                offsetBeforeAfter = -1;
            } else {
                boolean z5 = (i4 & 1) != 0;
                boolean z6 = z == z5;
                if (i == (z6 ? i2 : i3) && z6 == z2) {
                    offsetBeforeAfter = -1;
                } else {
                    boolean z7 = z6;
                    offsetBeforeAfter = getOffsetBeforeAfter(length, i3, i2, z5, i, z6);
                    if (!z7) {
                        i2 = i3;
                    }
                    if (offsetBeforeAfter != i2) {
                        return offsetBeforeAfter;
                    }
                }
            }
        }
        do {
            boolean z8 = z == z4;
            length += z8 ? 2 : -2;
            if (length < 0 || length >= iArr.length) {
                if (offsetBeforeAfter == -1) {
                    if (z8) {
                        return this.mLen + 1;
                    }
                    return -1;
                }
                if (offsetBeforeAfter > i9) {
                    return offsetBeforeAfter;
                }
                if (z8) {
                    return i9;
                }
                return 0;
            }
            i7 = 0 + iArr[length];
            int i20 = length + 1;
            int i21 = (iArr[i20] & 67108863) + i7;
            int i22 = i21 > i9 ? i9 : i21;
            int i23 = (iArr[i20] >>> 26) & 63;
            boolean z9 = (i23 & 1) != 0;
            if (z == z9) {
                i8 = -1;
                z3 = true;
            } else {
                z3 = false;
                i8 = -1;
            }
            if (offsetBeforeAfter != i8) {
                if (i23 < i4) {
                    return z3 ? i7 : i22;
                }
                return offsetBeforeAfter;
            }
            i4 = i23;
            offsetBeforeAfter = getOffsetBeforeAfter(length, i7, i22, z9, z3 ? i7 : i22, z3);
            if (z3) {
                i7 = i22;
            }
        } while (offsetBeforeAfter == i7);
        return offsetBeforeAfter;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void justify(float f) {
        int i = this.mLen;
        while (i > 0 && isLineEndSpace(this.mText.charAt((this.mStart + i) - 1))) {
            i--;
        }
        int iCountStretchableSpaces = countStretchableSpaces(0, i);
        if (iCountStretchableSpaces == 0) {
            return;
        }
        this.mAddedWidth = (f - Math.abs(measure(i, false, null))) / iCountStretchableSpaces;
    }

    float measure(int i, boolean z, Paint.FontMetricsInt fontMetricsInt) {
        int i2;
        int i3;
        int i4;
        int i5;
        int iCodePointAt;
        int i6 = z ? i - 1 : i;
        float f = 0.0f;
        if (i6 < 0) {
            return 0.0f;
        }
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                return measureRun(0, i, this.mLen, false, fontMetricsInt);
            }
            if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                return measureRun(0, i, this.mLen, true, fontMetricsInt);
            }
        }
        char[] cArr = this.mChars;
        int[] iArr = this.mDirections.mDirections;
        int i7 = 0;
        while (i7 < iArr.length) {
            int i8 = iArr[i7];
            int i9 = i7 + 1;
            int i10 = (iArr[i9] & 67108863) + i8;
            if (i10 > this.mLen) {
                i10 = this.mLen;
            }
            int i11 = i10;
            if (i8 > this.mLen) {
                break;
            }
            boolean z2 = (iArr[i9] & 67108864) != 0;
            float f2 = f;
            int i12 = i8;
            int i13 = this.mHasTabs ? i8 : i11;
            while (i13 <= i11) {
                if (!this.mHasTabs || i13 >= i11) {
                    i2 = 0;
                } else {
                    char c = cArr[i13];
                    if (c < 55296 || c >= 56320 || (i4 = i13 + 1) >= i11 || (iCodePointAt = Character.codePointAt(cArr, i13)) <= 65535) {
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = iCodePointAt;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i2 = i5;
                    }
                    i5 = c;
                    i5 = c;
                    i5 = c;
                    i5 = iCodePointAt;
                    i13 = i4 + 1;
                }
                if (i13 == i11 || i2 == 9) {
                    boolean z3 = i6 >= i12 && i6 < i13;
                    boolean z4 = (this.mDir == -1) == z2;
                    if (z3 && z4) {
                        return f2 + measureRun(i12, i, i13, z2, fontMetricsInt);
                    }
                    int i14 = i2;
                    int i15 = i12;
                    int i16 = i13;
                    float fMeasureRun = measureRun(i12, i13, i13, z2, fontMetricsInt);
                    if (!z4) {
                        fMeasureRun = -fMeasureRun;
                    }
                    f2 += fMeasureRun;
                    if (z3) {
                        return f2 + measureRun(i15, i, i16, z2, null);
                    }
                    if (i14 == 9) {
                        i3 = i16;
                        if (i == i3) {
                            return f2;
                        }
                        float fNextTab = this.mDir * nextTab(this.mDir * f2);
                        if (i6 == i3) {
                            return fNextTab;
                        }
                        f2 = fNextTab;
                    } else {
                        i3 = i16;
                    }
                    i12 = i3 + 1;
                    i4 = i3;
                } else {
                    i4 = i13;
                }
                i5 = c;
                i5 = c;
                i5 = c;
                i5 = iCodePointAt;
                i13 = i4 + 1;
            }
            i7 += 2;
            f = f2;
        }
        return f;
    }

    float[] measureAllOffsets(boolean[] zArr, Paint.FontMetricsInt fontMetricsInt) {
        int i;
        int i2;
        int i3;
        int i4;
        char c;
        int i5;
        int iCodePointAt;
        int i6 = 1;
        float[] fArr = new float[this.mLen + 1];
        int[] iArr = new int[this.mLen + 1];
        int i7 = 0;
        for (int i8 = 0; i8 < iArr.length; i8++) {
            iArr[i8] = zArr[i8] ? i8 - 1 : i8;
        }
        float f = 0.0f;
        if (iArr[0] < 0) {
            fArr[0] = 0.0f;
        }
        if (!this.mHasTabs) {
            if (this.mDirections == Layout.DIRS_ALL_LEFT_TO_RIGHT) {
                while (i7 <= this.mLen) {
                    fArr[i7] = measureRun(0, i7, this.mLen, false, fontMetricsInt);
                    i7++;
                }
                return fArr;
            }
            if (this.mDirections == Layout.DIRS_ALL_RIGHT_TO_LEFT) {
                while (i7 <= this.mLen) {
                    fArr[i7] = measureRun(0, i7, this.mLen, true, fontMetricsInt);
                    i7++;
                }
                return fArr;
            }
        }
        char[] cArr = this.mChars;
        int[] iArr2 = this.mDirections.mDirections;
        int i9 = 0;
        while (i9 < iArr2.length) {
            int i10 = iArr2[i9];
            int i11 = i9 + 1;
            int i12 = (iArr2[i11] & 67108863) + i10;
            if (i12 > this.mLen) {
                i12 = this.mLen;
            }
            int i13 = i12;
            if (i10 > this.mLen) {
                break;
            }
            int i14 = (iArr2[i11] & 67108864) != 0 ? i6 : i7;
            int i15 = i10;
            float f2 = f;
            int i16 = this.mHasTabs ? i10 : i13;
            while (i16 <= i13) {
                if (!this.mHasTabs || i16 >= i13) {
                    i = i7;
                } else {
                    c = cArr[i16];
                    if (c < 55296 || c >= 56320 || (i2 = i16 + 1) >= i13 || (iCodePointAt = Character.codePointAt(cArr, i16)) <= 65535) {
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = iCodePointAt;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i = i5;
                    } else {
                        i5 = c;
                        i5 = c;
                        i5 = c;
                        i5 = iCodePointAt;
                        i3 = i6;
                    }
                    i16 = i2 + 1;
                    i6 = i3;
                    cArr = cArr;
                    i7 = 0;
                }
                if (i16 == i13 || i == 9) {
                    int i17 = (this.mDir == -1 ? i6 : i7) == i14 ? i6 : i7;
                    int i18 = i;
                    int i19 = i15;
                    int i20 = i16;
                    float fMeasureRun = measureRun(i15, i16, i16, i14, fontMetricsInt);
                    if (i17 == 0) {
                        fMeasureRun = -fMeasureRun;
                    }
                    float f3 = f2 + fMeasureRun;
                    if (i17 == 0) {
                        f2 = f3;
                    }
                    Paint.FontMetricsInt fontMetricsInt2 = i17 != 0 ? fontMetricsInt : null;
                    int i21 = i19;
                    while (i21 <= i20 && i21 <= this.mLen) {
                        if (iArr[i21] < i19 || iArr[i21] >= i20) {
                            i4 = i21;
                        } else {
                            i4 = i21;
                            fArr[i4] = f2 + measureRun(i19, i21, i20, i14, fontMetricsInt2);
                        }
                        i21 = i4 + 1;
                    }
                    if (i18 == 9) {
                        if (iArr[i20] == i20) {
                            fArr[i20] = f3;
                        }
                        float fNextTab = this.mDir * nextTab(this.mDir * f3);
                        int i22 = i20 + 1;
                        if (iArr[i22] == i20) {
                            fArr[i22] = fNextTab;
                        }
                        f2 = fNextTab;
                    } else {
                        f2 = f3;
                    }
                    i15 = i20 + 1;
                    i2 = i20;
                    i3 = 1;
                } else {
                    i2 = i16;
                    i5 = c;
                    i5 = c;
                    i5 = c;
                    i5 = iCodePointAt;
                    i3 = i6;
                }
                i16 = i2 + 1;
                i6 = i3;
                cArr = cArr;
                i7 = 0;
            }
            i9 += 2;
            f = f2;
            i7 = 0;
        }
        if (iArr[this.mLen] == this.mLen) {
            fArr[this.mLen] = f;
        }
        return fArr;
    }

    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public float metrics(Paint.FontMetricsInt fontMetricsInt) {
        return measure(this.mLen, false, fontMetricsInt);
    }

    float nextTab(float f) {
        return this.mTabs != null ? this.mTabs.nextTab(f) : Layout.TabStops.nextDefaultStop(f, 20);
    }

    /* JADX WARN: Code duplicated, block: B:9:0x0031  */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    public void set(TextPaint textPaint, CharSequence charSequence, int i, int i2, int i3, Layout.Directions directions, boolean z, Layout.TabStops tabStops) {
        boolean z2;
        this.mPaint = textPaint;
        this.mText = charSequence;
        this.mStart = i;
        this.mLen = i2 - i;
        this.mDir = i3;
        this.mDirections = directions;
        if (this.mDirections == null) {
            throw new IllegalArgumentException("Directions cannot be null");
        }
        this.mHasTabs = z;
        this.mSpanned = null;
        if (charSequence instanceof Spanned) {
            this.mSpanned = (Spanned) charSequence;
            this.mReplacementSpanSpanSet.init(this.mSpanned, i, i2);
            if (this.mReplacementSpanSpanSet.numberOfSpans > 0) {
                z2 = true;
            } else {
                z2 = false;
            }
        } else {
            z2 = false;
        }
        this.mComputed = null;
        if (charSequence instanceof PrecomputedText) {
            this.mComputed = (PrecomputedText) charSequence;
            if (!this.mComputed.getParams().getTextPaint().equalsForTextMeasurement(textPaint)) {
                this.mComputed = null;
            }
        }
        this.mCharsValid = z2 || z || directions != Layout.DIRS_ALL_LEFT_TO_RIGHT;
        if (this.mCharsValid) {
            if (this.mChars == null || this.mChars.length < this.mLen) {
                this.mChars = ArrayUtils.newUnpaddedCharArray(this.mLen);
            }
            TextUtils.getChars(charSequence, i, i2, this.mChars, 0);
            if (z2) {
                char[] cArr = this.mChars;
                int i4 = i;
                while (i4 < i2) {
                    int nextTransition = this.mReplacementSpanSpanSet.getNextTransition(i4, i2);
                    if (this.mReplacementSpanSpanSet.hasSpansIntersecting(i4, nextTransition)) {
                        int i5 = i4 - i;
                        cArr[i5] = 65532;
                        int i6 = nextTransition - i;
                        for (int i7 = i5 + 1; i7 < i6; i7++) {
                            cArr[i7] = 65279;
                        }
                    }
                    i4 = nextTransition;
                }
            }
        }
        this.mTabs = tabStops;
        this.mAddedWidth = 0.0f;
    }
}
