package android.text.method;

import android.graphics.Paint;
import android.icu.lang.UCharacter;
import android.mtp.MtpConstants;
import android.text.Editable;
import android.text.Emoji;
import android.text.Layout;
import android.text.NoCopySpan;
import android.text.Selection;
import android.text.Spanned;
import android.text.style.ReplacementSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import com.android.internal.annotations.GuardedBy;

/* JADX INFO: loaded from: classes2.dex */
public abstract class BaseKeyListener extends MetaKeyKeyListener implements KeyListener {
    private static final int CARRIAGE_RETURN = 13;
    private static final int LINE_FEED = 10;
    static final Object OLD_SEL_START = new NoCopySpan.Concrete();

    @GuardedBy("mLock")
    static Paint sCachedPaint = null;
    private final Object mLock = new Object();

    private static int adjustReplacementSpan(CharSequence charSequence, int i, boolean z) {
        if (!(charSequence instanceof Spanned)) {
            return i;
        }
        Spanned spanned = (Spanned) charSequence;
        ReplacementSpan[] replacementSpanArr = (ReplacementSpan[]) spanned.getSpans(i, i, ReplacementSpan.class);
        for (int i2 = 0; i2 < replacementSpanArr.length; i2++) {
            int spanStart = spanned.getSpanStart(replacementSpanArr[i2]);
            int spanEnd = spanned.getSpanEnd(replacementSpanArr[i2]);
            if (spanStart < i && spanEnd > i) {
                if (!z) {
                    spanStart = spanEnd;
                }
                i = spanStart;
            }
        }
        return i;
    }

    private boolean backspaceOrForwardDelete(View view, Editable editable, int i, KeyEvent keyEvent, boolean z) {
        int offsetForBackspaceKey;
        Paint paint;
        Paint paint2;
        if (!KeyEvent.metaStateHasNoModifiers(keyEvent.getMetaState() & (-28916))) {
            return false;
        }
        if (deleteSelection(view, editable)) {
            return true;
        }
        boolean z2 = (keyEvent.getMetaState() & 4096) != 0;
        boolean z3 = getMetaState(editable, 1, keyEvent) == 1;
        boolean z4 = getMetaState(editable, 2, keyEvent) == 1;
        if (z2) {
            if (z4 || z3) {
                return false;
            }
            return deleteUntilWordBoundary(view, editable, z);
        }
        if (z4 && deleteLine(view, editable)) {
            return true;
        }
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (z) {
            if (view instanceof TextView) {
                paint2 = ((TextView) view).getPaint();
            } else {
                synchronized (this.mLock) {
                    if (sCachedPaint == null) {
                        sCachedPaint = new Paint();
                    }
                    paint = sCachedPaint;
                }
                paint2 = paint;
            }
            offsetForBackspaceKey = getOffsetForForwardDeleteKey(editable, selectionEnd, paint2);
        } else {
            offsetForBackspaceKey = getOffsetForBackspaceKey(editable, selectionEnd);
        }
        if (selectionEnd == offsetForBackspaceKey) {
            return false;
        }
        editable.delete(Math.min(selectionEnd, offsetForBackspaceKey), Math.max(selectionEnd, offsetForBackspaceKey));
        return true;
    }

    private boolean deleteLine(View view, Editable editable) {
        Layout layout;
        int lineForOffset;
        int lineStart;
        int lineEnd;
        if (!(view instanceof TextView) || (layout = ((TextView) view).getLayout()) == null || (lineEnd = layout.getLineEnd(lineForOffset)) == (lineStart = layout.getLineStart((lineForOffset = layout.getLineForOffset(Selection.getSelectionStart(editable)))))) {
            return false;
        }
        editable.delete(lineStart, lineEnd);
        return true;
    }

    private boolean deleteSelection(View view, Editable editable) {
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionEnd < selectionStart) {
            selectionEnd = selectionStart;
            selectionStart = selectionEnd;
        }
        if (selectionStart == selectionEnd) {
            return false;
        }
        editable.delete(selectionStart, selectionEnd);
        return true;
    }

    private boolean deleteUntilWordBoundary(View view, Editable editable, boolean z) {
        int length;
        int selectionStart = Selection.getSelectionStart(editable);
        if (selectionStart != Selection.getSelectionEnd(editable)) {
            return false;
        }
        if ((!z && selectionStart == 0) || (z && selectionStart == editable.length())) {
            return false;
        }
        WordIterator wordIterator = view instanceof TextView ? ((TextView) view).getWordIterator() : null;
        if (wordIterator == null) {
            wordIterator = new WordIterator();
        }
        if (z) {
            wordIterator.setCharSequence(editable, selectionStart, editable.length());
            int iFollowing = wordIterator.following(selectionStart);
            length = iFollowing == -1 ? editable.length() : iFollowing;
        } else {
            wordIterator.setCharSequence(editable, 0, selectionStart);
            int iPreceding = wordIterator.preceding(selectionStart);
            if (iPreceding == -1) {
                length = selectionStart;
                selectionStart = 0;
            } else {
                length = selectionStart;
                selectionStart = iPreceding;
            }
        }
        editable.delete(selectionStart, length);
        return true;
    }

    /* JADX WARN: Code duplicated, block: B:100:0x015c A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:71:0x0118 A[PHI: r4
      0x0118: PHI (r4v33 int) = 
      (r4v8 int)
      (r4v1 int)
      (r4v18 int)
      (r4v1 int)
      (r4v19 int)
      (r4v1 int)
      (r4v20 int)
      (r4v1 int)
      (r4v21 int)
      (r4v1 int)
      (r4v22 int)
      (r4v1 int)
      (r4v1 int)
      (r4v23 int)
      (r4v1 int)
      (r4v1 int)
      (r4v1 int)
      (r4v1 int)
      (r4v1 int)
      (r4v35 int)
      (r4v36 int)
     binds: [B:94:0x0156, B:69:0x0114, B:70:0x0116, B:67:0x010c, B:68:0x010e, B:59:0x00ef, B:60:0x00f1, B:56:0x00e3, B:57:0x00e5, B:50:0x00ca, B:51:0x00cc, B:45:0x00b8, B:47:0x00be, B:48:0x00c0, B:38:0x00a0, B:35:0x0095, B:25:0x006e, B:22:0x0062, B:18:0x0057, B:16:0x0050, B:15:0x0049] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:96:0x015a A[ADDED_TO_REGION] */
    private static int getOffsetForBackspaceKey(CharSequence charSequence, int i) {
        int i2;
        int i3;
        int iCharCount;
        int i4;
        if (i <= 1) {
            return 0;
        }
        int iCharCount2 = i;
        int i5 = 0;
        int iCharCount3 = 0;
        int i6 = 0;
        do {
            int iCodePointBefore = Character.codePointBefore(charSequence, iCharCount2);
            iCharCount2 -= Character.charCount(iCodePointBefore);
            int i7 = 4;
            switch (i5) {
                case 0:
                    int iCharCount4 = Character.charCount(iCodePointBefore);
                    if (iCodePointBefore == 10) {
                        iCharCount3 = iCharCount4;
                        i5 = 1;
                    } else {
                        if (isVariationSelector(iCodePointBefore)) {
                            i2 = 6;
                        } else if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                            iCharCount3 = iCharCount4;
                            i5 = 10;
                        } else if (Emoji.isEmojiModifier(iCodePointBefore)) {
                            iCharCount3 = iCharCount4;
                            i5 = i7;
                        } else if (iCodePointBefore == Emoji.COMBINING_ENCLOSING_KEYCAP) {
                            iCharCount3 = iCharCount4;
                            i5 = 2;
                        } else if (Emoji.isEmoji(iCodePointBefore)) {
                            iCharCount3 = iCharCount4;
                            i5 = 7;
                        } else if (iCodePointBefore == Emoji.CANCEL_TAG) {
                            i2 = 12;
                        } else {
                            iCharCount3 = iCharCount4;
                            i5 = 13;
                        }
                        i3 = i2;
                        iCharCount3 = iCharCount4;
                        i5 = i3;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 1:
                    if (iCodePointBefore == 13) {
                        iCharCount3++;
                    }
                    i5 = 13;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 2:
                    if (!isVariationSelector(iCodePointBefore)) {
                        if (Emoji.isKeycapBase(iCodePointBefore)) {
                            iCharCount3 += Character.charCount(iCodePointBefore);
                        }
                        i5 = 13;
                        if (iCharCount2 > 0) {
                        }
                        return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                    }
                    iCharCount = Character.charCount(iCodePointBefore);
                    i4 = 3;
                    i3 = i4;
                    i6 = iCharCount;
                    i5 = i3;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 3:
                    if (Emoji.isKeycapBase(iCodePointBefore)) {
                        iCharCount3 += Character.charCount(iCodePointBefore) + i6;
                    }
                    i5 = 13;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 4:
                    if (!isVariationSelector(iCodePointBefore)) {
                        if (Emoji.isEmojiModifierBase(iCodePointBefore)) {
                            iCharCount3 += Character.charCount(iCodePointBefore);
                        }
                        i5 = 13;
                        if (iCharCount2 > 0) {
                        }
                        return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                    }
                    iCharCount = Character.charCount(iCodePointBefore);
                    i4 = 5;
                    i3 = i4;
                    i6 = iCharCount;
                    i5 = i3;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 5:
                    if (Emoji.isEmojiModifierBase(iCodePointBefore)) {
                        iCharCount3 += Character.charCount(iCodePointBefore) + i6;
                    }
                    i5 = 13;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 6:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount3 += Character.charCount(iCodePointBefore);
                        i5 = 7;
                        if (iCharCount2 > 0) {
                        }
                        return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                    }
                    if (!isVariationSelector(iCodePointBefore) && UCharacter.getCombiningClass(iCodePointBefore) == 0) {
                        iCharCount3 += Character.charCount(iCodePointBefore);
                    }
                    i5 = 13;
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 7:
                    if (iCodePointBefore == Emoji.ZERO_WIDTH_JOINER) {
                        i5 = 8;
                    } else {
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 8:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount3 += Character.charCount(iCodePointBefore) + 1;
                        if (!Emoji.isEmojiModifier(iCodePointBefore)) {
                            i7 = 7;
                        }
                        i5 = i7;
                        if (iCharCount2 > 0) {
                        }
                        return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                    }
                    if (isVariationSelector(iCodePointBefore)) {
                        iCharCount = Character.charCount(iCodePointBefore);
                        i4 = 9;
                        i3 = i4;
                        i6 = iCharCount;
                        i5 = i3;
                    } else {
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 9:
                    if (Emoji.isEmoji(iCodePointBefore)) {
                        iCharCount3 += i6 + 1 + Character.charCount(iCodePointBefore);
                        i6 = 0;
                        i5 = 7;
                    } else {
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 10:
                    if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                        iCharCount3 += 2;
                        i5 = 11;
                    } else {
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 11:
                    if (Emoji.isRegionalIndicatorSymbol(iCodePointBefore)) {
                        iCharCount3 -= 2;
                        i5 = 10;
                    } else {
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                case 12:
                    if (Emoji.isTagSpecChar(iCodePointBefore)) {
                        iCharCount3 += 2;
                    } else {
                        iCharCount3 = Emoji.isEmoji(iCodePointBefore) ? iCharCount3 + Character.charCount(iCodePointBefore) : 2;
                        i5 = 13;
                    }
                    if (iCharCount2 > 0) {
                    }
                    return adjustReplacementSpan(charSequence, i - iCharCount3, true);
                default:
                    throw new IllegalArgumentException("state " + i5 + " is unknown");
            }
        } while (i5 != 13);
        return adjustReplacementSpan(charSequence, i - iCharCount3, true);
    }

    private static int getOffsetForForwardDeleteKey(CharSequence charSequence, int i, Paint paint) {
        int length = charSequence.length();
        return i >= length + (-1) ? length : adjustReplacementSpan(charSequence, paint.getTextRunCursor(charSequence, i, length, 0, i, 0), false);
    }

    private static boolean isVariationSelector(int i) {
        return UCharacter.hasBinaryProperty(i, 36);
    }

    static int makeTextContentType(TextKeyListener.Capitalize capitalize, boolean z) {
        int i;
        switch (capitalize) {
            case CHARACTERS:
                i = 4097;
                break;
            case WORDS:
                i = MtpConstants.RESPONSE_OK;
                break;
            case SENTENCES:
                i = 16385;
                break;
            default:
                i = 1;
                break;
        }
        return z ? i | 32768 : i;
    }

    public boolean backspace(View view, Editable editable, int i, KeyEvent keyEvent) {
        return backspaceOrForwardDelete(view, editable, i, keyEvent, false);
    }

    public boolean forwardDelete(View view, Editable editable, int i, KeyEvent keyEvent) {
        return backspaceOrForwardDelete(view, editable, i, keyEvent, true);
    }

    @Override // android.text.method.MetaKeyKeyListener, android.text.method.KeyListener
    public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
        boolean zBackspace;
        if (i != 67) {
            zBackspace = i != 112 ? false : forwardDelete(view, editable, i, keyEvent);
        } else {
            zBackspace = backspace(view, editable, i, keyEvent);
        }
        if (!zBackspace) {
            return super.onKeyDown(view, editable, i, keyEvent);
        }
        adjustMetaAfterKeypress(editable);
        return true;
    }

    @Override // android.text.method.KeyListener
    public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
        if (keyEvent.getAction() != 2 || keyEvent.getKeyCode() != 0) {
            return false;
        }
        int selectionStart = Selection.getSelectionStart(editable);
        int selectionEnd = Selection.getSelectionEnd(editable);
        if (selectionEnd < selectionStart) {
            selectionEnd = selectionStart;
            selectionStart = selectionEnd;
        }
        String characters = keyEvent.getCharacters();
        if (characters == null) {
            return false;
        }
        editable.replace(selectionStart, selectionEnd, characters);
        return true;
    }
}
