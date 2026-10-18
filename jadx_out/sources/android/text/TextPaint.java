package android.text;

import android.graphics.Paint;

/* JADX INFO: loaded from: classes2.dex */
public class TextPaint extends Paint {
    public int baselineShift;
    public int bgColor;
    public float density;
    public int[] drawableState;
    public int linkColor;
    public int underlineColor;
    public float underlineThickness;

    public TextPaint() {
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    public TextPaint(int i) {
        super(i);
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    public TextPaint(Paint paint) {
        super(paint);
        this.density = 1.0f;
        this.underlineColor = 0;
    }

    @Override // android.graphics.Paint
    public float getUnderlineThickness() {
        return this.underlineColor != 0 ? this.underlineThickness : super.getUnderlineThickness();
    }

    public boolean hasEqualAttributes(TextPaint textPaint) {
        return this.bgColor == textPaint.bgColor && this.baselineShift == textPaint.baselineShift && this.linkColor == textPaint.linkColor && this.drawableState == textPaint.drawableState && this.density == textPaint.density && this.underlineColor == textPaint.underlineColor && this.underlineThickness == textPaint.underlineThickness && super.hasEqualAttributes((Paint) textPaint);
    }

    public void set(TextPaint textPaint) {
        super.set((Paint) textPaint);
        this.bgColor = textPaint.bgColor;
        this.baselineShift = textPaint.baselineShift;
        this.linkColor = textPaint.linkColor;
        this.drawableState = textPaint.drawableState;
        this.density = textPaint.density;
        this.underlineColor = textPaint.underlineColor;
        this.underlineThickness = textPaint.underlineThickness;
    }

    public void setUnderlineText(int i, float f) {
        this.underlineColor = i;
        this.underlineThickness = f;
    }
}
