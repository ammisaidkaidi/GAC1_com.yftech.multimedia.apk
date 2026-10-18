package android.text.style;

import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Parcel;
import android.text.ParcelableSpan;
import android.text.TextPaint;

/* JADX INFO: loaded from: classes2.dex */
public class StyleSpan extends MetricAffectingSpan implements ParcelableSpan {
    private final int mStyle;

    public StyleSpan(int i) {
        this.mStyle = i;
    }

    public StyleSpan(Parcel parcel) {
        this.mStyle = parcel.readInt();
    }

    private static void apply(Paint paint, int i) {
        Typeface typeface = paint.getTypeface();
        int style = i | (typeface == null ? 0 : typeface.getStyle());
        Typeface typefaceDefaultFromStyle = typeface == null ? Typeface.defaultFromStyle(style) : Typeface.create(typeface, style);
        int i2 = style & (~typefaceDefaultFromStyle.getStyle());
        if ((i2 & 1) != 0) {
            paint.setFakeBoldText(true);
        }
        if ((i2 & 2) != 0) {
            paint.setTextSkewX(-0.25f);
        }
        paint.setTypeface(typefaceDefaultFromStyle);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return getSpanTypeIdInternal();
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeIdInternal() {
        return 7;
    }

    public int getStyle() {
        return this.mStyle;
    }

    @Override // android.text.style.CharacterStyle
    public void updateDrawState(TextPaint textPaint) {
        apply(textPaint, this.mStyle);
    }

    @Override // android.text.style.MetricAffectingSpan
    public void updateMeasureState(TextPaint textPaint) {
        apply(textPaint, this.mStyle);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        writeToParcelInternal(parcel, i);
    }

    @Override // android.text.ParcelableSpan
    public void writeToParcelInternal(Parcel parcel, int i) {
        parcel.writeInt(this.mStyle);
    }
}
