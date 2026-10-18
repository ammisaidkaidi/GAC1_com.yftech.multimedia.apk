package android.text;

import android.graphics.Rect;
import android.text.style.MetricAffectingSpan;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes2.dex */
public class PrecomputedText implements Spannable {
    private static final char LINE_FEED = '\n';
    private final int mEnd;
    private final ParagraphInfo[] mParagraphInfo;
    private final Params mParams;
    private final int mStart;
    private final SpannableString mText;

    public static class ParagraphInfo {
        public final MeasuredParagraph measured;
        public final int paragraphEnd;

        public ParagraphInfo(int i, MeasuredParagraph measuredParagraph) {
            this.paragraphEnd = i;
            this.measured = measuredParagraph;
        }
    }

    public static final class Params {
        private final int mBreakStrategy;
        private final int mHyphenationFrequency;
        private final TextPaint mPaint;
        private final TextDirectionHeuristic mTextDir;

        public static class Builder {
            private final TextPaint mPaint;
            private TextDirectionHeuristic mTextDir = TextDirectionHeuristics.FIRSTSTRONG_LTR;
            private int mBreakStrategy = 1;
            private int mHyphenationFrequency = 1;

            public Builder(TextPaint textPaint) {
                this.mPaint = textPaint;
            }

            public Params build() {
                return new Params(this.mPaint, this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency);
            }

            public Builder setBreakStrategy(int i) {
                this.mBreakStrategy = i;
                return this;
            }

            public Builder setHyphenationFrequency(int i) {
                this.mHyphenationFrequency = i;
                return this;
            }

            public Builder setTextDirection(TextDirectionHeuristic textDirectionHeuristic) {
                this.mTextDir = textDirectionHeuristic;
                return this;
            }
        }

        public Params(TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic, int i, int i2) {
            this.mPaint = textPaint;
            this.mTextDir = textDirectionHeuristic;
            this.mBreakStrategy = i;
            this.mHyphenationFrequency = i2;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || !(obj instanceof Params)) {
                return false;
            }
            Params params = (Params) obj;
            return isSameTextMetricsInternal(params.mPaint, params.mTextDir, params.mBreakStrategy, params.mHyphenationFrequency);
        }

        public int getBreakStrategy() {
            return this.mBreakStrategy;
        }

        public int getHyphenationFrequency() {
            return this.mHyphenationFrequency;
        }

        public TextDirectionHeuristic getTextDirection() {
            return this.mTextDir;
        }

        public TextPaint getTextPaint() {
            return this.mPaint;
        }

        public int hashCode() {
            return Objects.hash(Float.valueOf(this.mPaint.getTextSize()), Float.valueOf(this.mPaint.getTextScaleX()), Float.valueOf(this.mPaint.getTextSkewX()), Float.valueOf(this.mPaint.getLetterSpacing()), Float.valueOf(this.mPaint.getWordSpacing()), Integer.valueOf(this.mPaint.getFlags()), this.mPaint.getTextLocales(), this.mPaint.getTypeface(), this.mPaint.getFontVariationSettings(), Boolean.valueOf(this.mPaint.isElegantTextHeight()), this.mTextDir, Integer.valueOf(this.mBreakStrategy), Integer.valueOf(this.mHyphenationFrequency));
        }

        public boolean isSameTextMetricsInternal(TextPaint textPaint, TextDirectionHeuristic textDirectionHeuristic, int i, int i2) {
            return this.mTextDir == textDirectionHeuristic && this.mBreakStrategy == i && this.mHyphenationFrequency == i2 && this.mPaint.equalsForTextMeasurement(textPaint);
        }

        public String toString() {
            return "{textSize=" + this.mPaint.getTextSize() + ", textScaleX=" + this.mPaint.getTextScaleX() + ", textSkewX=" + this.mPaint.getTextSkewX() + ", letterSpacing=" + this.mPaint.getLetterSpacing() + ", textLocale=" + this.mPaint.getTextLocales() + ", typeface=" + this.mPaint.getTypeface() + ", variationSettings=" + this.mPaint.getFontVariationSettings() + ", elegantTextHeight=" + this.mPaint.isElegantTextHeight() + ", textDir=" + this.mTextDir + ", breakStrategy=" + this.mBreakStrategy + ", hyphenationFrequency=" + this.mHyphenationFrequency + "}";
        }
    }

    private PrecomputedText(CharSequence charSequence, int i, int i2, Params params, ParagraphInfo[] paragraphInfoArr) {
        this.mText = new SpannableString(charSequence, true);
        this.mStart = i;
        this.mEnd = i2;
        this.mParams = params;
        this.mParagraphInfo = paragraphInfoArr;
    }

    public static PrecomputedText create(CharSequence charSequence, Params params) {
        return new PrecomputedText(charSequence, 0, charSequence.length(), params, createMeasuredParagraphs(charSequence, params, 0, charSequence.length(), true));
    }

    public static ParagraphInfo[] createMeasuredParagraphs(CharSequence charSequence, Params params, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList();
        Preconditions.checkNotNull(charSequence);
        Preconditions.checkNotNull(params);
        boolean z2 = (params.getBreakStrategy() == 0 || params.getHyphenationFrequency() == 0) ? false : true;
        while (true) {
            int i3 = i;
            if (i3 >= i2) {
                return (ParagraphInfo[]) arrayList.toArray(new ParagraphInfo[arrayList.size()]);
            }
            int iIndexOf = TextUtils.indexOf(charSequence, LINE_FEED, i3, i2);
            i = iIndexOf < 0 ? i2 : iIndexOf + 1;
            arrayList.add(new ParagraphInfo(i, MeasuredParagraph.buildForStaticLayout(params.getTextPaint(), charSequence, i3, i, params.getTextDirection(), z2, z, null)));
        }
    }

    public boolean canUseMeasuredResult(int i, int i2, TextDirectionHeuristic textDirectionHeuristic, TextPaint textPaint, int i3, int i4) {
        this.mParams.getTextPaint();
        return this.mStart == i && this.mEnd == i2 && this.mParams.isSameTextMetricsInternal(textPaint, textDirectionHeuristic, i3, i4);
    }

    @Override // java.lang.CharSequence
    public char charAt(int i) {
        return this.mText.charAt(i);
    }

    public int findParaIndex(int i) {
        for (int i2 = 0; i2 < this.mParagraphInfo.length; i2++) {
            if (i < this.mParagraphInfo[i2].paragraphEnd) {
                return i2;
            }
        }
        throw new IndexOutOfBoundsException("pos must be less than " + this.mParagraphInfo[this.mParagraphInfo.length - 1].paragraphEnd + ", gave " + i);
    }

    public void getBounds(int i, int i2, Rect rect) {
        Preconditions.checkArgument(i >= 0 && i <= this.mText.length(), "invalid start offset");
        Preconditions.checkArgument(i2 >= 0 && i2 <= this.mText.length(), "invalid end offset");
        Preconditions.checkArgument(i <= i2, "start offset can not be larger than end offset");
        Preconditions.checkNotNull(rect);
        if (i == i2) {
            rect.set(0, 0, 0, 0);
            return;
        }
        int iFindParaIndex = findParaIndex(i);
        int paragraphStart = getParagraphStart(iFindParaIndex);
        int paragraphEnd = getParagraphEnd(iFindParaIndex);
        if (i >= paragraphStart && paragraphEnd >= i2) {
            getMeasuredParagraph(iFindParaIndex).getBounds(i - paragraphStart, i2 - paragraphStart, rect);
            return;
        }
        throw new IllegalArgumentException("Cannot measured across the paragraph:para: (" + paragraphStart + ", " + paragraphEnd + "), request: (" + i + ", " + i2 + ")");
    }

    public int getEnd() {
        return this.mEnd;
    }

    public MeasuredParagraph getMeasuredParagraph(int i) {
        return this.mParagraphInfo[i].measured;
    }

    public int getMemoryUsage() {
        int memoryUsage = 0;
        for (int i = 0; i < getParagraphCount(); i++) {
            memoryUsage += getMeasuredParagraph(i).getMemoryUsage();
        }
        return memoryUsage;
    }

    public int getParagraphCount() {
        return this.mParagraphInfo.length;
    }

    public int getParagraphEnd(int i) {
        Preconditions.checkArgumentInRange(i, 0, getParagraphCount(), "paraIndex");
        return this.mParagraphInfo[i].paragraphEnd;
    }

    public ParagraphInfo[] getParagraphInfo() {
        return this.mParagraphInfo;
    }

    public int getParagraphStart(int i) {
        Preconditions.checkArgumentInRange(i, 0, getParagraphCount(), "paraIndex");
        return i == 0 ? this.mStart : getParagraphEnd(i - 1);
    }

    public Params getParams() {
        return this.mParams;
    }

    @Override // android.text.Spanned
    public int getSpanEnd(Object obj) {
        return this.mText.getSpanEnd(obj);
    }

    @Override // android.text.Spanned
    public int getSpanFlags(Object obj) {
        return this.mText.getSpanFlags(obj);
    }

    @Override // android.text.Spanned
    public int getSpanStart(Object obj) {
        return this.mText.getSpanStart(obj);
    }

    @Override // android.text.Spanned
    public <T> T[] getSpans(int i, int i2, Class<T> cls) {
        return (T[]) this.mText.getSpans(i, i2, cls);
    }

    public int getStart() {
        return this.mStart;
    }

    public CharSequence getText() {
        return this.mText;
    }

    public float getWidth(int i, int i2) {
        Preconditions.checkArgument(i >= 0 && i <= this.mText.length(), "invalid start offset");
        Preconditions.checkArgument(i2 >= 0 && i2 <= this.mText.length(), "invalid end offset");
        Preconditions.checkArgument(i <= i2, "start offset can not be larger than end offset");
        if (i == i2) {
            return 0.0f;
        }
        int iFindParaIndex = findParaIndex(i);
        int paragraphStart = getParagraphStart(iFindParaIndex);
        int paragraphEnd = getParagraphEnd(iFindParaIndex);
        if (i >= paragraphStart && paragraphEnd >= i2) {
            return getMeasuredParagraph(iFindParaIndex).getWidth(i - paragraphStart, i2 - paragraphStart);
        }
        throw new IllegalArgumentException("Cannot measured across the paragraph:para: (" + paragraphStart + ", " + paragraphEnd + "), request: (" + i + ", " + i2 + ")");
    }

    @Override // java.lang.CharSequence
    public int length() {
        return this.mText.length();
    }

    @Override // android.text.Spanned
    public int nextSpanTransition(int i, int i2, Class cls) {
        return this.mText.nextSpanTransition(i, i2, cls);
    }

    @Override // android.text.Spannable
    public void removeSpan(Object obj) {
        if (obj instanceof MetricAffectingSpan) {
            throw new IllegalArgumentException("MetricAffectingSpan can not be removed from PrecomputedText.");
        }
        this.mText.removeSpan(obj);
    }

    @Override // android.text.Spannable
    public void setSpan(Object obj, int i, int i2, int i3) {
        if (obj instanceof MetricAffectingSpan) {
            throw new IllegalArgumentException("MetricAffectingSpan can not be set to PrecomputedText.");
        }
        this.mText.setSpan(obj, i, i2, i3);
    }

    @Override // java.lang.CharSequence
    public CharSequence subSequence(int i, int i2) {
        return create(this.mText.subSequence(i, i2), this.mParams);
    }

    @Override // java.lang.CharSequence
    public String toString() {
        return this.mText.toString();
    }
}
