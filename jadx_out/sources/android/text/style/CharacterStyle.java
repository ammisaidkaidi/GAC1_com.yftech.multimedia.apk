package android.text.style;

import android.text.TextPaint;

/* JADX INFO: loaded from: classes2.dex */
public abstract class CharacterStyle {

    private static class Passthrough extends CharacterStyle {
        private CharacterStyle mStyle;

        public Passthrough(CharacterStyle characterStyle) {
            this.mStyle = characterStyle;
        }

        @Override // android.text.style.CharacterStyle
        public CharacterStyle getUnderlying() {
            return this.mStyle.getUnderlying();
        }

        @Override // android.text.style.CharacterStyle
        public void updateDrawState(TextPaint textPaint) {
            this.mStyle.updateDrawState(textPaint);
        }
    }

    public static CharacterStyle wrap(CharacterStyle characterStyle) {
        return characterStyle instanceof MetricAffectingSpan ? new MetricAffectingSpan.Passthrough((MetricAffectingSpan) characterStyle) : new Passthrough(characterStyle);
    }

    public CharacterStyle getUnderlying() {
        return this;
    }

    public abstract void updateDrawState(TextPaint textPaint);
}
