package android.text.method;

import android.text.AutoText;
import android.text.Editable;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;

/* JADX INFO: loaded from: classes2.dex */
public class QwertyKeyListener extends BaseKeyListener {
    private static QwertyKeyListener sFullKeyboardInstance;
    private TextKeyListener.Capitalize mAutoCap;
    private boolean mAutoText;
    private boolean mFullKeyboard;
    private static QwertyKeyListener[] sInstance = new QwertyKeyListener[TextKeyListener.Capitalize.values().length * 2];
    private static SparseArray<String> PICKER_SETS = new SparseArray<>();

    static class Replaced implements NoCopySpan {
        private char[] mText;

        public Replaced(char[] cArr) {
            this.mText = cArr;
        }
    }

    static {
        PICKER_SETS.put(65, "ÀÁÂÄÆÃÅĄĀ");
        PICKER_SETS.put(67, "ÇĆČ");
        PICKER_SETS.put(68, "Ď");
        PICKER_SETS.put(69, "ÈÉÊËĘĚĒ");
        PICKER_SETS.put(71, "Ğ");
        PICKER_SETS.put(76, "Ł");
        PICKER_SETS.put(73, "ÌÍÎÏĪİ");
        PICKER_SETS.put(78, "ÑŃŇ");
        PICKER_SETS.put(79, "ØŒÕÒÓÔÖŌ");
        PICKER_SETS.put(82, "Ř");
        PICKER_SETS.put(83, "ŚŠŞ");
        PICKER_SETS.put(84, "Ť");
        PICKER_SETS.put(85, "ÙÚÛÜŮŪ");
        PICKER_SETS.put(89, "ÝŸ");
        PICKER_SETS.put(90, "ŹŻŽ");
        PICKER_SETS.put(97, "àáâäæãåąā");
        PICKER_SETS.put(99, "çćč");
        PICKER_SETS.put(100, "ď");
        PICKER_SETS.put(101, "èéêëęěē");
        PICKER_SETS.put(103, "ğ");
        PICKER_SETS.put(105, "ìíîïīı");
        PICKER_SETS.put(108, "ł");
        PICKER_SETS.put(110, "ñńň");
        PICKER_SETS.put(111, "øœõòóôöō");
        PICKER_SETS.put(114, "ř");
        PICKER_SETS.put(115, "§ßśšş");
        PICKER_SETS.put(116, "ť");
        PICKER_SETS.put(117, "ùúûüůū");
        PICKER_SETS.put(121, "ýÿ");
        PICKER_SETS.put(122, "źżž");
        PICKER_SETS.put(61185, "…¥•®©±[]{}\\|");
        PICKER_SETS.put(47, "\\");
        PICKER_SETS.put(49, "¹½⅓¼⅛");
        PICKER_SETS.put(50, "²⅔");
        PICKER_SETS.put(51, "³¾⅜");
        PICKER_SETS.put(52, "⁴");
        PICKER_SETS.put(53, "⅝");
        PICKER_SETS.put(55, "⅞");
        PICKER_SETS.put(48, "ⁿ∅");
        PICKER_SETS.put(36, "¢£€¥₣₤₱");
        PICKER_SETS.put(37, "‰");
        PICKER_SETS.put(42, "†‡");
        PICKER_SETS.put(45, "–—");
        PICKER_SETS.put(43, "±");
        PICKER_SETS.put(40, "[{<");
        PICKER_SETS.put(41, "]}>");
        PICKER_SETS.put(33, "¡");
        PICKER_SETS.put(34, "“”«»˝");
        PICKER_SETS.put(63, "¿");
        PICKER_SETS.put(44, "‚„");
        PICKER_SETS.put(61, "≠≈∞");
        PICKER_SETS.put(60, "≤«‹");
        PICKER_SETS.put(62, "≥»›");
    }

    public QwertyKeyListener(TextKeyListener.Capitalize capitalize, boolean z) {
        this(capitalize, z, false);
    }

    private QwertyKeyListener(TextKeyListener.Capitalize capitalize, boolean z, boolean z2) {
        this.mAutoCap = capitalize;
        this.mAutoText = z;
        this.mFullKeyboard = z2;
    }

    public static QwertyKeyListener getInstance(boolean z, TextKeyListener.Capitalize capitalize) {
        int iOrdinal = (capitalize.ordinal() * 2) + (z ? 1 : 0);
        if (sInstance[iOrdinal] == null) {
            sInstance[iOrdinal] = new QwertyKeyListener(capitalize, z);
        }
        return sInstance[iOrdinal];
    }

    public static QwertyKeyListener getInstanceForFullKeyboard() {
        if (sFullKeyboardInstance == null) {
            sFullKeyboardInstance = new QwertyKeyListener(TextKeyListener.Capitalize.NONE, false, true);
        }
        return sFullKeyboardInstance;
    }

    private String getReplacement(CharSequence charSequence, int i, int i2, View view) {
        boolean z;
        int i3;
        int i4 = i2 - i;
        String upperCase = AutoText.get(charSequence, i, i2, view);
        if (upperCase == null) {
            upperCase = AutoText.get(TextUtils.substring(charSequence, i, i2).toLowerCase(), 0, i4, view);
            if (upperCase == null) {
                return null;
            }
            z = true;
        } else {
            z = false;
        }
        if (z) {
            i3 = 0;
            for (int i5 = i; i5 < i2; i5++) {
                if (Character.isUpperCase(charSequence.charAt(i5))) {
                    i3++;
                }
            }
        } else {
            i3 = 0;
        }
        if (i3 != 0) {
            upperCase = (i3 != 1 && i3 == i4) ? upperCase.toUpperCase() : toTitleCase(upperCase);
        }
        if (upperCase.length() == i4 && TextUtils.regionMatches(charSequence, i, upperCase, 0, i4)) {
            return null;
        }
        return upperCase;
    }

    public static void markAsReplaced(Spannable spannable, int i, int i2, String str) {
        for (Replaced replaced : (Replaced[]) spannable.getSpans(0, spannable.length(), Replaced.class)) {
            spannable.removeSpan(replaced);
        }
        int length = str.length();
        char[] cArr = new char[length];
        str.getChars(0, length, cArr, 0);
        spannable.setSpan(new Replaced(cArr), i, i2, 33);
    }

    private boolean showCharacterPicker(View view, Editable editable, char c, boolean z, int i) {
        String str = PICKER_SETS.get(c);
        if (str == null) {
            return false;
        }
        if (i == 1) {
            new CharacterPickerDialog(view.getContext(), view, editable, str, z).show();
        }
        return true;
    }

    private static String toTitleCase(String str) {
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return makeTextContentType(this.mAutoCap, this.mAutoText);
    }

    @Override // android.text.method.BaseKeyListener, android.text.method.MetaKeyKeyListener, android.text.method.KeyListener
    public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
        int i2;
        int i3;
        int i4;
        boolean z;
        boolean z2;
        int selectionEnd;
        int selectionEnd2;
        boolean z3;
        int deadChar;
        int i5;
        int i6;
        int repeatCount;
        char cCharAt;
        int prefs = view != null ? TextKeyListener.getInstance().getPrefs(view.getContext()) : 0;
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd3 = Selection.getSelectionEnd(editable);
        int iMin = Math.min(selectionStart, selectionEnd3);
        int iMax = Math.max(selectionStart, selectionEnd3);
        if (iMin < 0 || iMax < 0) {
            Selection.setSelection(editable, 0, 0);
            i2 = 0;
            i3 = 0;
        } else {
            i3 = iMax;
            i2 = iMin;
        }
        int spanStart = editable.getSpanStart(TextKeyListener.ACTIVE);
        int spanEnd = editable.getSpanEnd(TextKeyListener.ACTIVE);
        int unicodeChar = keyEvent.getUnicodeChar(getMetaState(editable, keyEvent));
        if (!this.mFullKeyboard && (repeatCount = keyEvent.getRepeatCount()) > 0 && i2 == i3 && i2 > 0 && (((cCharAt = editable.charAt(i2 - 1)) == unicodeChar || cCharAt == Character.toUpperCase(unicodeChar)) && view != null)) {
            if (showCharacterPicker(view, editable, cCharAt, false, repeatCount)) {
                resetMetaState(editable);
                return true;
            }
        }
        if (unicodeChar == 61185) {
            if (view != null) {
                showCharacterPicker(view, editable, KeyCharacterMap.PICKER_DIALOG_INPUT, true, 1);
            }
            resetMetaState(editable);
            return true;
        }
        if (unicodeChar == 61184) {
            if (i2 == i3) {
                i5 = i3;
                while (i5 > 0 && i3 - i5 < 4 && Character.digit(editable.charAt(i5 - 1), 16) >= 0) {
                    i5--;
                }
            } else {
                i5 = i2;
            }
            try {
                i6 = Integer.parseInt(TextUtils.substring(editable, i5, i3), 16);
            } catch (NumberFormatException unused) {
                i6 = -1;
            }
            unicodeChar = i6;
            if (unicodeChar >= 0) {
                Selection.setSelection(editable, i5, i3);
                i2 = i5;
            } else {
                unicodeChar = 0;
            }
        }
        if (unicodeChar == 0) {
            if (i == 67) {
                if (keyEvent.hasNoModifiers()) {
                    i4 = 2;
                } else {
                    i4 = 2;
                    if (keyEvent.hasModifiers(2)) {
                    }
                }
                if (i2 == i3) {
                    if (editable.getSpanEnd(TextKeyListener.LAST_TYPED) != i2 || editable.charAt(i2 - 1) == '\n') {
                        i4 = 1;
                    }
                    Replaced[] replacedArr = (Replaced[]) editable.getSpans(i2 - i4, i2, Replaced.class);
                    if (replacedArr.length > 0) {
                        int spanStart2 = editable.getSpanStart(replacedArr[0]);
                        int spanEnd2 = editable.getSpanEnd(replacedArr[0]);
                        CharSequence str = new String(replacedArr[0].mText);
                        editable.removeSpan(replacedArr[0]);
                        if (i2 < spanEnd2) {
                            adjustMetaAfterKeypress(editable);
                            return super.onKeyDown(view, editable, i, keyEvent);
                        }
                        editable.setSpan(TextKeyListener.INHIBIT_REPLACEMENT, spanEnd2, spanEnd2, 34);
                        editable.replace(spanStart2, spanEnd2, str);
                        int spanStart3 = editable.getSpanStart(TextKeyListener.INHIBIT_REPLACEMENT);
                        int i7 = spanStart3 - 1;
                        if (i7 >= 0) {
                            editable.setSpan(TextKeyListener.INHIBIT_REPLACEMENT, i7, spanStart3, 33);
                        } else {
                            editable.removeSpan(TextKeyListener.INHIBIT_REPLACEMENT);
                        }
                        adjustMetaAfterKeypress(editable);
                        return true;
                    }
                }
            }
            return super.onKeyDown(view, editable, i, keyEvent);
        }
        if ((Integer.MIN_VALUE & unicodeChar) != 0) {
            unicodeChar &= Integer.MAX_VALUE;
            z = true;
        } else {
            z = false;
        }
        if (spanStart == i2 && spanEnd == i3) {
            if ((i3 - i2) - 1 != 0 || (deadChar = KeyEvent.getDeadChar(editable.charAt(i2), unicodeChar)) == 0) {
                z2 = z;
                z3 = false;
            } else {
                unicodeChar = deadChar;
                z3 = true;
                z2 = false;
            }
            if (!z3) {
                Selection.setSelection(editable, i3);
                editable.removeSpan(TextKeyListener.ACTIVE);
                i2 = i3;
            }
        } else {
            z2 = z;
        }
        if ((prefs & 1) != 0 && Character.isLowerCase(unicodeChar) && TextKeyListener.shouldCap(this.mAutoCap, editable, i2)) {
            int spanEnd3 = editable.getSpanEnd(TextKeyListener.CAPPED);
            int spanFlags = editable.getSpanFlags(TextKeyListener.CAPPED);
            if (spanEnd3 == i2 && ((spanFlags >> 16) & 65535) == unicodeChar) {
                editable.removeSpan(TextKeyListener.CAPPED);
            } else {
                int i8 = unicodeChar << 16;
                unicodeChar = Character.toUpperCase(unicodeChar);
                if (i2 == 0) {
                    editable.setSpan(TextKeyListener.CAPPED, 0, 0, i8 | 17);
                } else {
                    editable.setSpan(TextKeyListener.CAPPED, i2 - 1, i2, i8 | 33);
                }
            }
        }
        if (i2 != i3) {
            Selection.setSelection(editable, i3);
        }
        editable.setSpan(OLD_SEL_START, i2, i2, 17);
        editable.replace(i2, i3, String.valueOf((char) unicodeChar));
        int spanStart4 = editable.getSpanStart(OLD_SEL_START);
        int selectionEnd4 = Selection.getSelectionEnd(editable);
        if (spanStart4 < selectionEnd4) {
            editable.setSpan(TextKeyListener.LAST_TYPED, spanStart4, selectionEnd4, 33);
            if (z2) {
                Selection.setSelection(editable, spanStart4, selectionEnd4);
                editable.setSpan(TextKeyListener.ACTIVE, spanStart4, selectionEnd4, 33);
            }
        }
        adjustMetaAfterKeypress(editable);
        if ((prefs & 2) != 0 && this.mAutoText && ((unicodeChar == 32 || unicodeChar == 9 || unicodeChar == 10 || unicodeChar == 44 || unicodeChar == 46 || unicodeChar == 33 || unicodeChar == 63 || unicodeChar == 34 || Character.getType(unicodeChar) == 22) && editable.getSpanEnd(TextKeyListener.INHIBIT_REPLACEMENT) != spanStart4)) {
            int i9 = spanStart4;
            while (i9 > 0) {
                char cCharAt2 = editable.charAt(i9 - 1);
                if (cCharAt2 != '\'' && !Character.isLetter(cCharAt2)) {
                    break;
                }
                i9--;
            }
            CharSequence replacement = getReplacement(editable, i9, spanStart4, view);
            if (replacement != null) {
                for (Object obj : (Replaced[]) editable.getSpans(0, editable.length(), Replaced.class)) {
                    editable.removeSpan(obj);
                }
                char[] cArr = new char[spanStart4 - i9];
                TextUtils.getChars(editable, i9, spanStart4, cArr, 0);
                editable.setSpan(new Replaced(cArr), i9, spanStart4, 33);
                editable.replace(i9, spanStart4, replacement);
            }
        }
        if ((prefs & 4) != 0 && this.mAutoText && (selectionEnd2 = (selectionEnd = Selection.getSelectionEnd(editable)) - 3) >= 0) {
            int i10 = selectionEnd - 1;
            if (editable.charAt(i10) == ' ') {
                int i11 = selectionEnd - 2;
                if (editable.charAt(i11) == ' ') {
                    char cCharAt3 = editable.charAt(selectionEnd2);
                    while (selectionEnd2 > 0 && (cCharAt3 == '\"' || Character.getType(cCharAt3) == 22)) {
                        cCharAt3 = editable.charAt(selectionEnd2 - 1);
                        selectionEnd2--;
                    }
                    if (Character.isLetter(cCharAt3) || Character.isDigit(cCharAt3)) {
                        editable.replace(i11, i10, ".");
                    }
                }
            }
        }
        return true;
    }
}
