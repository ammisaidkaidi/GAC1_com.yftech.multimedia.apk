package android.text;

/* JADX INFO: loaded from: classes2.dex */
public interface Spannable extends Spanned {

    public static class Factory {
        private static Factory sInstance = new Factory();

        public static Factory getInstance() {
            return sInstance;
        }

        public Spannable newSpannable(CharSequence charSequence) {
            return new SpannableString(charSequence);
        }
    }

    void removeSpan(Object obj);

    default void removeSpan(Object obj, int i) {
        removeSpan(obj);
    }

    void setSpan(Object obj, int i, int i2, int i3);
}
