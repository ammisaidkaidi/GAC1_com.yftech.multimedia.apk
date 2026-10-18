package android.widget;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.UndoManager;
import android.content.res.ColorStateList;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.BaseCanvas;
import android.graphics.Canvas;
import android.graphics.Insets;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormatSymbols;
import android.mtp.MtpConstants;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.FileObserver;
import android.os.LocaleList;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.ParcelableParcel;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.BoringLayout;
import android.text.DynamicLayout;
import android.text.Editable;
import android.text.GetChars;
import android.text.GraphicsOperations;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.PrecomputedText;
import android.text.Selection;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.AllCapsTransformationMethod;
import android.text.method.ArrowKeyMovementMethod;
import android.text.method.DateKeyListener;
import android.text.method.DateTimeKeyListener;
import android.text.method.DialerKeyListener;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.LinkMovementMethod;
import android.text.method.MetaKeyKeyListener;
import android.text.method.MovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.method.SingleLineTransformationMethod;
import android.text.method.TextKeyListener;
import android.text.method.TimeKeyListener;
import android.text.method.TransformationMethod;
import android.text.method.TransformationMethod2;
import android.text.method.WordIterator;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ParagraphStyle;
import android.text.style.SpellCheckSpan;
import android.text.style.SuggestionSpan;
import android.text.style.URLSpan;
import android.text.style.UpdateAppearance;
import android.text.util.Linkify;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.IntArray;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.ActionMode;
import android.view.Choreographer;
import android.view.ContextMenu;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.ViewParent;
import android.view.ViewRootImpl;
import android.view.ViewStructure;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AnimationUtils;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.autofill.Helper;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;
import android.view.textclassifier.TextClassification;
import android.view.textclassifier.TextClassificationContext;
import android.view.textclassifier.TextClassificationManager;
import android.view.textclassifier.TextClassifier;
import android.view.textclassifier.TextLinks;
import android.view.textservice.SpellCheckerSubtype;
import android.view.textservice.TextServicesManager;
import com.android.internal.R;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.logging.nano.MetricsProto;
import com.android.internal.util.FastMath;
import com.android.internal.util.Preconditions;
import com.android.internal.widget.EditableInputConnection;
import com.google.android.material.badge.BadgeDrawable;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;
import libcore.util.EmptyArray;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class TextView extends View implements ViewTreeObserver.OnPreDrawListener {
    static final int ACCESSIBILITY_ACTION_PROCESS_TEXT_START_ID = 268435712;
    private static final int ACCESSIBILITY_ACTION_SHARE = 268435456;
    private static final int ANIMATED_SCROLL_GAP = 250;
    public static final int AUTO_SIZE_TEXT_TYPE_NONE = 0;
    public static final int AUTO_SIZE_TEXT_TYPE_UNIFORM = 1;
    private static final int CHANGE_WATCHER_PRIORITY = 100;
    static final boolean DEBUG_EXTRACT = false;
    private static final int DECIMAL = 4;
    private static final int DEFAULT_AUTO_SIZE_GRANULARITY_IN_PX = 1;
    private static final int DEFAULT_AUTO_SIZE_MAX_TEXT_SIZE_IN_SP = 112;
    private static final int DEFAULT_AUTO_SIZE_MIN_TEXT_SIZE_IN_SP = 12;
    private static final int DEFAULT_TYPEFACE = -1;
    private static final int DEVICE_PROVISIONED_NO = 1;
    private static final int DEVICE_PROVISIONED_UNKNOWN = 0;
    private static final int DEVICE_PROVISIONED_YES = 2;
    private static final int ELLIPSIZE_END = 3;
    private static final int ELLIPSIZE_MARQUEE = 4;
    private static final int ELLIPSIZE_MIDDLE = 2;
    private static final int ELLIPSIZE_NONE = 0;
    private static final int ELLIPSIZE_NOT_SET = -1;
    private static final int ELLIPSIZE_START = 1;
    private static final int EMS = 1;
    private static final int FLOATING_TOOLBAR_SELECT_ALL_REFRESH_DELAY = 500;
    static final int ID_ASSIST = 16908353;
    static final int ID_AUTOFILL = 16908355;
    static final int ID_COPY = 16908321;
    static final int ID_CUT = 16908320;
    static final int ID_PASTE = 16908322;
    static final int ID_PASTE_AS_PLAIN_TEXT = 16908337;
    static final int ID_REDO = 16908339;
    static final int ID_REPLACE = 16908340;
    static final int ID_SELECT_ALL = 16908319;
    static final int ID_SHARE = 16908341;
    static final int ID_UNDO = 16908338;
    private static final int KEY_DOWN_HANDLED_BY_KEY_LISTENER = 1;
    private static final int KEY_DOWN_HANDLED_BY_MOVEMENT_METHOD = 2;
    private static final int KEY_EVENT_HANDLED = -1;
    private static final int KEY_EVENT_NOT_HANDLED = 0;
    private static final int LINES = 1;
    static final String LOG_TAG = "TextView";
    private static final int MARQUEE_FADE_NORMAL = 0;
    private static final int MARQUEE_FADE_SWITCH_SHOW_ELLIPSIS = 1;
    private static final int MARQUEE_FADE_SWITCH_SHOW_FADE = 2;
    private static final int MONOSPACE = 3;
    private static final int PIXELS = 2;
    static final int PROCESS_TEXT_REQUEST_CODE = 100;
    private static final int SANS = 1;
    private static final int SERIF = 2;
    private static final int SIGNED = 2;

    @VisibleForTesting
    public static final BoringLayout.Metrics UNKNOWN_BORING;
    private static final float UNSET_AUTO_SIZE_UNIFORM_CONFIGURATION_VALUE = -1.0f;
    static final int VERY_WIDE = 1048576;
    static long sLastCutCopyOrTextChangedTime;
    private boolean mAllowTransformationLengthChange;
    private int mAutoLinkMask;
    private float mAutoSizeMaxTextSizeInPx;
    private float mAutoSizeMinTextSizeInPx;
    private float mAutoSizeStepGranularityInPx;
    private int[] mAutoSizeTextSizesInPx;
    private int mAutoSizeTextType;
    private BoringLayout.Metrics mBoring;
    private int mBreakStrategy;
    private BufferType mBufferType;
    private ChangeWatcher mChangeWatcher;
    private CharWrapper mCharWrapper;
    private int mCurHintTextColor;

    @ViewDebug.ExportedProperty(category = "text")
    private int mCurTextColor;
    private volatile Locale mCurrentSpellCheckerLocaleCache;
    int mCursorDrawableRes;
    private int mDeferScroll;
    private int mDesiredHeightAtMeasure;
    private int mDeviceProvisionedState;
    Drawables mDrawables;
    private Editable.Factory mEditableFactory;
    private Editor mEditor;
    private TextUtils.TruncateAt mEllipsize;
    private InputFilter[] mFilters;
    private boolean mFreezesText;

    @ViewDebug.ExportedProperty(category = "text")
    private int mGravity;
    private boolean mHasPresetAutoSizeValues;
    int mHighlightColor;
    private final Paint mHighlightPaint;
    private Path mHighlightPath;
    private boolean mHighlightPathBogus;
    private CharSequence mHint;
    private BoringLayout.Metrics mHintBoring;
    private Layout mHintLayout;
    private ColorStateList mHintTextColor;
    private boolean mHorizontallyScrolling;
    private int mHyphenationFrequency;
    private boolean mIncludePad;
    private boolean mIsRestrictedAcrossUser;
    private int mJustificationMode;
    private int mLastLayoutDirection;
    private long mLastScroll;
    private CharSequence mLastValueSentToAutofillManager;
    private Layout mLayout;
    private ColorStateList mLinkTextColor;
    private boolean mLinksClickable;
    private boolean mListenerChanged;
    private ArrayList<TextWatcher> mListeners;
    private boolean mLocalesChanged;
    private Marquee mMarquee;
    private int mMarqueeFadeMode;
    private int mMarqueeRepeatLimit;
    private int mMaxMode;
    private int mMaxWidth;
    private int mMaxWidthMode;
    private int mMaximum;
    private int mMinMode;
    private int mMinWidth;
    private int mMinWidthMode;
    private int mMinimum;
    private MovementMethod mMovement;
    private boolean mNeedsAutoSizeText;
    private int mOldMaxMode;
    private int mOldMaximum;
    private boolean mPreDrawListenerDetached;
    private boolean mPreDrawRegistered;
    private PrecomputedText mPrecomputed;
    private boolean mPreventDefaultMovement;
    private boolean mRestartMarquee;
    private BoringLayout mSavedHintLayout;
    private BoringLayout mSavedLayout;
    private Layout mSavedMarqueeModeLayout;
    private Scroller mScroller;
    private int mShadowColor;
    private float mShadowDx;
    private float mShadowDy;
    private float mShadowRadius;
    private boolean mSingleLine;
    private float mSpacingAdd;
    private float mSpacingMult;
    private Spannable mSpannable;
    private Spannable.Factory mSpannableFactory;
    private Rect mTempRect;
    private TextPaint mTempTextPaint;

    @ViewDebug.ExportedProperty(category = "text")
    private CharSequence mText;
    private TextClassifier mTextClassificationSession;
    private TextClassifier mTextClassifier;
    private ColorStateList mTextColor;
    private TextDirectionHeuristic mTextDir;
    int mTextEditSuggestionContainerLayout;
    int mTextEditSuggestionHighlightStyle;
    int mTextEditSuggestionItemLayout;
    private int mTextId;
    private final TextPaint mTextPaint;
    int mTextSelectHandleLeftRes;
    int mTextSelectHandleRes;
    int mTextSelectHandleRightRes;
    private boolean mTextSetFromXmlOrResourceId;
    private TransformationMethod mTransformation;
    private CharSequence mTransformed;
    boolean mUseFallbackLineSpacing;
    private final boolean mUseInternationalizedInput;
    private boolean mUserSetTextScaleX;
    private static final float[] TEMP_POSITION = new float[2];
    private static final RectF TEMP_RECTF = new RectF();
    private static final InputFilter[] NO_FILTERS = new InputFilter[0];
    private static final Spanned EMPTY_SPANNED = new SpannedString("");
    private static final int[] MULTILINE_STATE_SET = {16843597};
    private static final SparseIntArray sAppearanceValues = new SparseIntArray();

    @Retention(RetentionPolicy.SOURCE)
    public @interface AutoSizeTextType {
    }

    public enum BufferType {
        NORMAL,
        SPANNABLE,
        EDITABLE
    }

    private class ChangeWatcher implements TextWatcher, SpanWatcher {
        private CharSequence mBeforeText;

        private ChangeWatcher() {
        }

        @Override // android.text.TextWatcher
        public void afterTextChanged(Editable editable) {
            TextView.this.sendAfterTextChanged(editable);
            if (MetaKeyKeyListener.getMetaState(editable, 2048) != 0) {
                MetaKeyKeyListener.stopSelecting(TextView.this, editable);
            }
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (AccessibilityManager.getInstance(TextView.this.mContext).isEnabled() && TextView.this.mTransformed != null) {
                this.mBeforeText = TextView.this.mTransformed.toString();
            }
            TextView.this.sendBeforeTextChanged(charSequence, i, i2, i3);
        }

        @Override // android.text.SpanWatcher
        public void onSpanAdded(Spannable spannable, Object obj, int i, int i2) {
            TextView.this.spanChange(spannable, obj, -1, i, -1, i2);
        }

        @Override // android.text.SpanWatcher
        public void onSpanChanged(Spannable spannable, Object obj, int i, int i2, int i3, int i4) {
            TextView.this.spanChange(spannable, obj, i, i3, i2, i4);
        }

        @Override // android.text.SpanWatcher
        public void onSpanRemoved(Spannable spannable, Object obj, int i, int i2) {
            TextView.this.spanChange(spannable, obj, i, -1, i2, -1);
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            TextView.this.handleTextChanged(charSequence, i, i2, i3);
            if (AccessibilityManager.getInstance(TextView.this.mContext).isEnabled()) {
                if (TextView.this.isFocused() || (TextView.this.isSelected() && TextView.this.isShown())) {
                    TextView.this.sendAccessibilityEventTypeViewTextChanged(this.mBeforeText, i, i2, i3);
                    this.mBeforeText = null;
                }
            }
        }
    }

    private static class CharWrapper implements CharSequence, GetChars, GraphicsOperations {
        private char[] mChars;
        private int mLength;
        private int mStart;

        public CharWrapper(char[] cArr, int i, int i2) {
            this.mChars = cArr;
            this.mStart = i;
            this.mLength = i2;
        }

        @Override // java.lang.CharSequence
        public char charAt(int i) {
            return this.mChars[i + this.mStart];
        }

        @Override // android.text.GraphicsOperations
        public void drawText(BaseCanvas baseCanvas, int i, int i2, float f, float f2, Paint paint) {
            baseCanvas.drawText(this.mChars, i + this.mStart, i2 - i, f, f2, paint);
        }

        @Override // android.text.GraphicsOperations
        public void drawTextRun(BaseCanvas baseCanvas, int i, int i2, int i3, int i4, float f, float f2, boolean z, Paint paint) {
            baseCanvas.drawTextRun(this.mChars, this.mStart + i, i2 - i, i3 + this.mStart, i4 - i3, f, f2, z, paint);
        }

        @Override // android.text.GetChars
        public void getChars(int i, int i2, char[] cArr, int i3) {
            if (i >= 0 && i2 >= 0 && i <= this.mLength && i2 <= this.mLength) {
                System.arraycopy(this.mChars, this.mStart + i, cArr, i3, i2 - i);
                return;
            }
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }

        @Override // android.text.GraphicsOperations
        public float getTextRunAdvances(int i, int i2, int i3, int i4, boolean z, float[] fArr, int i5, Paint paint) {
            return paint.getTextRunAdvances(this.mChars, this.mStart + i, i2 - i, i3 + this.mStart, i4 - i3, z, fArr, i5);
        }

        @Override // android.text.GraphicsOperations
        public int getTextRunCursor(int i, int i2, int i3, int i4, int i5, Paint paint) {
            return paint.getTextRunCursor(this.mChars, i + this.mStart, i2 - i, i3, i4 + this.mStart, i5);
        }

        @Override // android.text.GraphicsOperations
        public int getTextWidths(int i, int i2, float[] fArr, Paint paint) {
            return paint.getTextWidths(this.mChars, this.mStart + i, i2 - i, fArr);
        }

        @Override // java.lang.CharSequence
        public int length() {
            return this.mLength;
        }

        @Override // android.text.GraphicsOperations
        public float measureText(int i, int i2, Paint paint) {
            return paint.measureText(this.mChars, this.mStart + i, i2 - i);
        }

        void set(char[] cArr, int i, int i2) {
            this.mChars = cArr;
            this.mStart = i;
            this.mLength = i2;
        }

        @Override // java.lang.CharSequence
        public CharSequence subSequence(int i, int i2) {
            if (i >= 0 && i2 >= 0 && i <= this.mLength && i2 <= this.mLength) {
                return new String(this.mChars, this.mStart + i, i2 - i);
            }
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }

        @Override // java.lang.CharSequence
        public String toString() {
            return new String(this.mChars, this.mStart, this.mLength);
        }
    }

    static class Drawables {
        static final int BOTTOM = 3;
        static final int DRAWABLE_LEFT = 1;
        static final int DRAWABLE_NONE = -1;
        static final int DRAWABLE_RIGHT = 0;
        static final int LEFT = 0;
        static final int RIGHT = 2;
        static final int TOP = 1;
        Drawable mDrawableEnd;
        Drawable mDrawableError;
        int mDrawableHeightEnd;
        int mDrawableHeightError;
        int mDrawableHeightLeft;
        int mDrawableHeightRight;
        int mDrawableHeightStart;
        int mDrawableHeightTemp;
        Drawable mDrawableLeftInitial;
        int mDrawablePadding;
        Drawable mDrawableRightInitial;
        int mDrawableSizeBottom;
        int mDrawableSizeEnd;
        int mDrawableSizeError;
        int mDrawableSizeLeft;
        int mDrawableSizeRight;
        int mDrawableSizeStart;
        int mDrawableSizeTemp;
        int mDrawableSizeTop;
        Drawable mDrawableStart;
        Drawable mDrawableTemp;
        int mDrawableWidthBottom;
        int mDrawableWidthTop;
        boolean mHasTint;
        boolean mHasTintMode;
        boolean mIsRtlCompatibilityMode;
        boolean mOverride;
        ColorStateList mTintList;
        PorterDuff.Mode mTintMode;
        final Rect mCompoundRect = new Rect();
        final Drawable[] mShowing = new Drawable[4];
        int mDrawableSaved = -1;

        public Drawables(Context context) {
            this.mIsRtlCompatibilityMode = context.getApplicationInfo().targetSdkVersion < 17 || !context.getApplicationInfo().hasRtlSupport();
            this.mOverride = false;
        }

        private void applyErrorDrawableIfNeeded(int i) {
            switch (this.mDrawableSaved) {
                case 0:
                    this.mShowing[2] = this.mDrawableTemp;
                    this.mDrawableSizeRight = this.mDrawableSizeTemp;
                    this.mDrawableHeightRight = this.mDrawableHeightTemp;
                    break;
                case 1:
                    this.mShowing[0] = this.mDrawableTemp;
                    this.mDrawableSizeLeft = this.mDrawableSizeTemp;
                    this.mDrawableHeightLeft = this.mDrawableHeightTemp;
                    break;
            }
            if (this.mDrawableError != null) {
                if (i != 1) {
                    this.mDrawableSaved = 0;
                    this.mDrawableTemp = this.mShowing[2];
                    this.mDrawableSizeTemp = this.mDrawableSizeRight;
                    this.mDrawableHeightTemp = this.mDrawableHeightRight;
                    this.mShowing[2] = this.mDrawableError;
                    this.mDrawableSizeRight = this.mDrawableSizeError;
                    this.mDrawableHeightRight = this.mDrawableHeightError;
                    return;
                }
                this.mDrawableSaved = 1;
                this.mDrawableTemp = this.mShowing[0];
                this.mDrawableSizeTemp = this.mDrawableSizeLeft;
                this.mDrawableHeightTemp = this.mDrawableHeightLeft;
                this.mShowing[0] = this.mDrawableError;
                this.mDrawableSizeLeft = this.mDrawableSizeError;
                this.mDrawableHeightLeft = this.mDrawableHeightError;
            }
        }

        public boolean hasMetadata() {
            return this.mDrawablePadding != 0 || this.mHasTintMode || this.mHasTint;
        }

        public boolean resolveWithLayoutDirection(int i) {
            Drawable drawable = this.mShowing[0];
            Drawable drawable2 = this.mShowing[2];
            this.mShowing[0] = this.mDrawableLeftInitial;
            this.mShowing[2] = this.mDrawableRightInitial;
            if (this.mIsRtlCompatibilityMode) {
                if (this.mDrawableStart != null && this.mShowing[0] == null) {
                    this.mShowing[0] = this.mDrawableStart;
                    this.mDrawableSizeLeft = this.mDrawableSizeStart;
                    this.mDrawableHeightLeft = this.mDrawableHeightStart;
                }
                if (this.mDrawableEnd != null && this.mShowing[2] == null) {
                    this.mShowing[2] = this.mDrawableEnd;
                    this.mDrawableSizeRight = this.mDrawableSizeEnd;
                    this.mDrawableHeightRight = this.mDrawableHeightEnd;
                }
            } else if (i != 1) {
                if (this.mOverride) {
                    this.mShowing[0] = this.mDrawableStart;
                    this.mDrawableSizeLeft = this.mDrawableSizeStart;
                    this.mDrawableHeightLeft = this.mDrawableHeightStart;
                    this.mShowing[2] = this.mDrawableEnd;
                    this.mDrawableSizeRight = this.mDrawableSizeEnd;
                    this.mDrawableHeightRight = this.mDrawableHeightEnd;
                }
            } else if (this.mOverride) {
                this.mShowing[2] = this.mDrawableStart;
                this.mDrawableSizeRight = this.mDrawableSizeStart;
                this.mDrawableHeightRight = this.mDrawableHeightStart;
                this.mShowing[0] = this.mDrawableEnd;
                this.mDrawableSizeLeft = this.mDrawableSizeEnd;
                this.mDrawableHeightLeft = this.mDrawableHeightEnd;
            }
            applyErrorDrawableIfNeeded(i);
            return (this.mShowing[0] == drawable && this.mShowing[2] == drawable2) ? false : true;
        }

        public void setErrorDrawable(Drawable drawable, TextView textView) {
            if (this.mDrawableError != drawable && this.mDrawableError != null) {
                this.mDrawableError.setCallback(null);
            }
            this.mDrawableError = drawable;
            if (this.mDrawableError == null) {
                this.mDrawableHeightError = 0;
                this.mDrawableSizeError = 0;
                return;
            }
            Rect rect = this.mCompoundRect;
            this.mDrawableError.setState(textView.getDrawableState());
            this.mDrawableError.copyBounds(rect);
            this.mDrawableError.setCallback(textView);
            this.mDrawableSizeError = rect.width();
            this.mDrawableHeightError = rect.height();
        }
    }

    private static final class Marquee {
        private static final int MARQUEE_DELAY = 1200;
        private static final float MARQUEE_DELTA_MAX = 0.07f;
        private static final int MARQUEE_DP_PER_SECOND = 30;
        private static final byte MARQUEE_RUNNING = 2;
        private static final byte MARQUEE_STARTING = 1;
        private static final byte MARQUEE_STOPPED = 0;
        private float mFadeStop;
        private float mGhostOffset;
        private float mGhostStart;
        private long mLastAnimationMs;
        private float mMaxFadeScroll;
        private float mMaxScroll;
        private final float mPixelsPerMs;
        private int mRepeatLimit;
        private float mScroll;
        private final WeakReference<TextView> mView;
        private byte mStatus = 0;
        private Choreographer.FrameCallback mTickCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.1
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long j) {
                Marquee.this.tick();
            }
        };
        private Choreographer.FrameCallback mStartCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.2
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long j) {
                Marquee.this.mStatus = (byte) 2;
                Marquee.this.mLastAnimationMs = Marquee.this.mChoreographer.getFrameTime();
                Marquee.this.tick();
            }
        };
        private Choreographer.FrameCallback mRestartCallback = new Choreographer.FrameCallback() { // from class: android.widget.TextView.Marquee.3
            @Override // android.view.Choreographer.FrameCallback
            public void doFrame(long j) {
                if (Marquee.this.mStatus == 2) {
                    if (Marquee.this.mRepeatLimit >= 0) {
                        Marquee.access$910(Marquee.this);
                    }
                    Marquee.this.start(Marquee.this.mRepeatLimit);
                }
            }
        };
        private final Choreographer mChoreographer = Choreographer.getInstance();

        Marquee(TextView textView) {
            this.mPixelsPerMs = (textView.getContext().getResources().getDisplayMetrics().density * 30.0f) / 1000.0f;
            this.mView = new WeakReference<>(textView);
        }

        static /* synthetic */ int access$910(Marquee marquee) {
            int i = marquee.mRepeatLimit;
            marquee.mRepeatLimit = i - 1;
            return i;
        }

        private void resetScroll() {
            this.mScroll = 0.0f;
            TextView textView = this.mView.get();
            if (textView != null) {
                textView.invalidate();
            }
        }

        float getGhostOffset() {
            return this.mGhostOffset;
        }

        float getMaxFadeScroll() {
            return this.mMaxFadeScroll;
        }

        float getScroll() {
            return this.mScroll;
        }

        boolean isRunning() {
            return this.mStatus == 2;
        }

        boolean isStopped() {
            return this.mStatus == 0;
        }

        boolean shouldDrawGhost() {
            return this.mStatus == 2 && this.mScroll > this.mGhostStart;
        }

        boolean shouldDrawLeftFade() {
            return this.mScroll <= this.mFadeStop;
        }

        void start(int i) {
            if (i == 0) {
                stop();
                return;
            }
            this.mRepeatLimit = i;
            TextView textView = this.mView.get();
            if (textView == null || textView.mLayout == null) {
                return;
            }
            this.mStatus = (byte) 1;
            this.mScroll = 0.0f;
            int width = (textView.getWidth() - textView.getCompoundPaddingLeft()) - textView.getCompoundPaddingRight();
            float lineWidth = textView.mLayout.getLineWidth(0);
            float f = width;
            float f2 = f / 3.0f;
            this.mGhostStart = (lineWidth - f) + f2;
            this.mMaxScroll = this.mGhostStart + f;
            this.mGhostOffset = f2 + lineWidth;
            this.mFadeStop = (f / 6.0f) + lineWidth;
            this.mMaxFadeScroll = this.mGhostStart + lineWidth + lineWidth;
            textView.invalidate();
            this.mChoreographer.postFrameCallback(this.mStartCallback);
        }

        void stop() {
            this.mStatus = (byte) 0;
            this.mChoreographer.removeFrameCallback(this.mStartCallback);
            this.mChoreographer.removeFrameCallback(this.mRestartCallback);
            this.mChoreographer.removeFrameCallback(this.mTickCallback);
            resetScroll();
        }

        void tick() {
            if (this.mStatus != 2) {
                return;
            }
            this.mChoreographer.removeFrameCallback(this.mTickCallback);
            TextView textView = this.mView.get();
            if (textView != null) {
                if (textView.isFocused() || textView.isSelected()) {
                    long frameTime = this.mChoreographer.getFrameTime();
                    long j = frameTime - this.mLastAnimationMs;
                    this.mLastAnimationMs = frameTime;
                    this.mScroll += j * this.mPixelsPerMs;
                    if (this.mScroll > this.mMaxScroll) {
                        this.mScroll = this.mMaxScroll;
                        this.mChoreographer.postFrameCallbackDelayed(this.mRestartCallback, 1200L);
                    } else {
                        this.mChoreographer.postFrameCallback(this.mTickCallback);
                    }
                    textView.invalidate();
                }
            }
        }
    }

    public interface OnEditorActionListener {
        boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent);
    }

    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.TextView.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel parcel) {
                return new SavedState(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int i) {
                return new SavedState[i];
            }
        };
        ParcelableParcel editorState;
        CharSequence error;
        boolean frozenWithFocus;
        int selEnd;
        int selStart;
        CharSequence text;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.selStart = -1;
            this.selEnd = -1;
            this.selStart = parcel.readInt();
            this.selEnd = parcel.readInt();
            this.frozenWithFocus = parcel.readInt() != 0;
            this.text = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            if (parcel.readInt() != 0) {
                this.error = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            }
            if (parcel.readInt() != 0) {
                this.editorState = ParcelableParcel.CREATOR.createFromParcel(parcel);
            }
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
            this.selStart = -1;
            this.selEnd = -1;
        }

        public String toString() {
            String str = "TextView.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " start=" + this.selStart + " end=" + this.selEnd;
            if (this.text != null) {
                str = str + " text=" + ((Object) this.text);
            }
            return str + "}";
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeInt(this.selStart);
            parcel.writeInt(this.selEnd);
            parcel.writeInt(this.frozenWithFocus ? 1 : 0);
            TextUtils.writeToParcel(this.text, parcel, i);
            if (this.error == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                TextUtils.writeToParcel(this.error, parcel, i);
            }
            if (this.editorState == null) {
                parcel.writeInt(0);
            } else {
                parcel.writeInt(1);
                this.editorState.writeToParcel(parcel, i);
            }
        }
    }

    private static class TextAppearanceAttributes {
        boolean mAllCaps;
        boolean mElegant;
        boolean mFallbackLineSpacing;
        String mFontFamily;
        boolean mFontFamilyExplicit;
        String mFontFeatureSettings;
        Typeface mFontTypeface;
        int mFontWeight;
        boolean mHasElegant;
        boolean mHasFallbackLineSpacing;
        boolean mHasLetterSpacing;
        float mLetterSpacing;
        int mShadowColor;
        float mShadowDx;
        float mShadowDy;
        float mShadowRadius;
        int mStyleIndex;
        ColorStateList mTextColor;
        int mTextColorHighlight;
        ColorStateList mTextColorHint;
        ColorStateList mTextColorLink;
        int mTextSize;
        int mTypefaceIndex;

        private TextAppearanceAttributes() {
            this.mTextColorHighlight = 0;
            this.mTextColor = null;
            this.mTextColorHint = null;
            this.mTextColorLink = null;
            this.mTextSize = 0;
            this.mFontFamily = null;
            this.mFontTypeface = null;
            this.mFontFamilyExplicit = false;
            this.mTypefaceIndex = -1;
            this.mStyleIndex = -1;
            this.mFontWeight = -1;
            this.mAllCaps = false;
            this.mShadowColor = 0;
            this.mShadowDx = 0.0f;
            this.mShadowDy = 0.0f;
            this.mShadowRadius = 0.0f;
            this.mHasElegant = false;
            this.mElegant = false;
            this.mHasFallbackLineSpacing = false;
            this.mFallbackLineSpacing = false;
            this.mHasLetterSpacing = false;
            this.mLetterSpacing = 0.0f;
            this.mFontFeatureSettings = null;
        }

        public String toString() {
            return "TextAppearanceAttributes {\n    mTextColorHighlight:" + this.mTextColorHighlight + "\n    mTextColor:" + this.mTextColor + "\n    mTextColorHint:" + this.mTextColorHint + "\n    mTextColorLink:" + this.mTextColorLink + "\n    mTextSize:" + this.mTextSize + "\n    mFontFamily:" + this.mFontFamily + "\n    mFontTypeface:" + this.mFontTypeface + "\n    mFontFamilyExplicit:" + this.mFontFamilyExplicit + "\n    mTypefaceIndex:" + this.mTypefaceIndex + "\n    mStyleIndex:" + this.mStyleIndex + "\n    mFontWeight:" + this.mFontWeight + "\n    mAllCaps:" + this.mAllCaps + "\n    mShadowColor:" + this.mShadowColor + "\n    mShadowDx:" + this.mShadowDx + "\n    mShadowDy:" + this.mShadowDy + "\n    mShadowRadius:" + this.mShadowRadius + "\n    mHasElegant:" + this.mHasElegant + "\n    mElegant:" + this.mElegant + "\n    mHasFallbackLineSpacing:" + this.mHasFallbackLineSpacing + "\n    mFallbackLineSpacing:" + this.mFallbackLineSpacing + "\n    mHasLetterSpacing:" + this.mHasLetterSpacing + "\n    mLetterSpacing:" + this.mLetterSpacing + "\n    mFontFeatureSettings:" + this.mFontFeatureSettings + "\n}";
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface XMLTypefaceAttr {
    }

    static {
        sAppearanceValues.put(6, 4);
        sAppearanceValues.put(5, 3);
        sAppearanceValues.put(7, 5);
        sAppearanceValues.put(8, 6);
        sAppearanceValues.put(2, 0);
        sAppearanceValues.put(3, 1);
        sAppearanceValues.put(75, 12);
        sAppearanceValues.put(4, 2);
        sAppearanceValues.put(94, 17);
        sAppearanceValues.put(72, 11);
        sAppearanceValues.put(36, 7);
        sAppearanceValues.put(37, 8);
        sAppearanceValues.put(38, 9);
        sAppearanceValues.put(39, 10);
        sAppearanceValues.put(76, 13);
        sAppearanceValues.put(90, 16);
        sAppearanceValues.put(77, 14);
        sAppearanceValues.put(78, 15);
        UNKNOWN_BORING = new BoringLayout.Metrics();
    }

    public TextView(Context context) {
        this(context, null);
    }

    public TextView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842884);
    }

    public TextView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    /* JADX WARN: Code duplicated, block: B:213:0x096b  */
    /* JADX WARN: Code duplicated, block: B:215:0x0972  */
    /* JADX WARN: Code duplicated, block: B:217:0x097e  */
    /* JADX WARN: Code duplicated, block: B:223:0x0992  */
    /* JADX WARN: Code duplicated, block: B:225:0x0996  */
    /* JADX WARN: Code duplicated, block: B:226:0x09a0  */
    /* JADX WARN: Code duplicated, block: B:228:0x09a4  */
    /* JADX WARN: Code duplicated, block: B:229:0x09ae  */
    /* JADX WARN: Code duplicated, block: B:231:0x09b1  */
    /* JADX WARN: Code duplicated, block: B:240:0x09e5  */
    /* JADX WARN: Code duplicated, block: B:243:0x09eb  */
    /* JADX WARN: Code duplicated, block: B:245:0x09f5  */
    /* JADX WARN: Code duplicated, block: B:246:0x09fd  */
    /* JADX WARN: Code duplicated, block: B:248:0x0a0a  */
    /* JADX WARN: Code duplicated, block: B:249:0x0a10  */
    /* JADX WARN: Code duplicated, block: B:250:0x0a16  */
    /* JADX WARN: Code duplicated, block: B:257:0x0a26  */
    /* JADX WARN: Code duplicated, block: B:265:0x0a3a  */
    /* JADX WARN: Code duplicated, block: B:267:0x0a3d  */
    /* JADX WARN: Code duplicated, block: B:268:0x0a43  */
    /* JADX WARN: Code duplicated, block: B:271:0x0a4a  */
    /* JADX WARN: Code duplicated, block: B:274:0x0a55  */
    /* JADX WARN: Code duplicated, block: B:276:0x0a66  */
    /* JADX WARN: Code duplicated, block: B:279:0x0a72  */
    /* JADX WARN: Code duplicated, block: B:282:0x0a78  */
    /* JADX WARN: Code duplicated, block: B:289:0x0a94  */
    /* JADX WARN: Code duplicated, block: B:295:0x0aa0  */
    /* JADX WARN: Code duplicated, block: B:301:0x0aac  */
    /* JADX WARN: Code duplicated, block: B:304:0x0ab9  */
    /* JADX WARN: Code duplicated, block: B:306:0x0ac1  */
    /* JADX WARN: Code duplicated, block: B:308:0x0ac5  */
    /* JADX WARN: Code duplicated, block: B:309:0x0aca  */
    /* JADX WARN: Code duplicated, block: B:311:0x0ad1  */
    /* JADX WARN: Code duplicated, block: B:313:0x0adc  */
    /* JADX WARN: Code duplicated, block: B:315:0x0ae2  */
    /* JADX WARN: Code duplicated, block: B:317:0x0ae6  */
    /* JADX WARN: Code duplicated, block: B:318:0x0ae8  */
    /* JADX WARN: Code duplicated, block: B:319:0x0aea  */
    /* JADX WARN: Code duplicated, block: B:324:0x0af9  */
    /* JADX WARN: Code duplicated, block: B:327:0x0b06  */
    /* JADX WARN: Code duplicated, block: B:330:0x0b11  */
    /* JADX WARN: Code duplicated, block: B:331:0x0b16  */
    /* JADX WARN: Code duplicated, block: B:334:0x0b1d  */
    /* JADX WARN: Code duplicated, block: B:336:0x0b21  */
    /* JADX WARN: Code duplicated, block: B:338:0x0b25  */
    /* JADX WARN: Code duplicated, block: B:340:0x0b33  */
    /* JADX WARN: Code duplicated, block: B:341:0x0b3b  */
    /* JADX WARN: Code duplicated, block: B:344:0x0b42  */
    /* JADX WARN: Code duplicated, block: B:347:0x0b4e  */
    /* JADX WARN: Code duplicated, block: B:348:0x0b51  */
    /* JADX WARN: Code duplicated, block: B:352:0x0b5c  */
    /* JADX WARN: Code duplicated, block: B:354:0x0b62  */
    /* JADX WARN: Code duplicated, block: B:357:0x0b69  */
    /* JADX WARN: Code duplicated, block: B:360:0x0b70  */
    /* JADX WARN: Code duplicated, block: B:382:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Failed to find 'out' block for switch in B:18:0x0161. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:198:0x0932. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:19:0x0164. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:20:0x0167. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:21:0x016a. Please report as an issue. */
    /* JADX WARN: Failed to find 'out' block for switch in B:22:0x016d. Please report as an issue. */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r13v4, types: [android.content.res.TypedArray] */
    /* JADX WARN: Type inference failed for: r14v10, types: [boolean] */
    /* JADX WARN: Type inference failed for: r14v13 */
    /* JADX WARN: Type inference failed for: r14v19 */
    /* JADX WARN: Type inference failed for: r14v32, types: [boolean] */
    /* JADX WARN: Type inference failed for: r52v0, types: [android.widget.TextView] */
    /* JADX WARN: Type inference failed for: r8v0 */
    /* JADX WARN: Type inference failed for: r8v1, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r8v11 */
    public TextView(Context context, AttributeSet attributeSet, int i, int i2) {
        boolean z;
        BufferType bufferType;
        ColorStateList colorStateList;
        Context context2;
        boolean z2;
        int i3;
        boolean z3;
        boolean z4;
        TextAppearanceAttributes textAppearanceAttributes;
        int i4;
        boolean z5;
        CharSequence charSequence;
        TypedArray typedArrayObtainStyledAttributes;
        boolean z6;
        boolean z7;
        boolean z8;
        int focusable;
        int indexCount;
        boolean z9;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        DisplayMetrics displayMetrics;
        int i10;
        float f;
        int index;
        TypedValue typedValue;
        int i11;
        TextKeyListener.Capitalize capitalize;
        int i12;
        int i13;
        super(context, attributeSet, i, i2);
        this.mEditableFactory = Editable.Factory.getInstance();
        this.mSpannableFactory = Spannable.Factory.getInstance();
        this.mMarqueeRepeatLimit = 3;
        int i14 = -1;
        this.mLastLayoutDirection = -1;
        ?? r8 = 0;
        this.mMarqueeFadeMode = 0;
        this.mBufferType = BufferType.NORMAL;
        this.mLocalesChanged = false;
        this.mListenerChanged = false;
        this.mGravity = BadgeDrawable.TOP_START;
        this.mLinksClickable = true;
        this.mSpacingMult = 1.0f;
        this.mSpacingAdd = 0.0f;
        this.mMaximum = Integer.MAX_VALUE;
        this.mMaxMode = 1;
        this.mMinimum = 0;
        this.mMinMode = 1;
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxWidthMode = 2;
        this.mMinWidth = 0;
        this.mMinWidthMode = 2;
        this.mDesiredHeightAtMeasure = -1;
        this.mIncludePad = true;
        this.mDeferScroll = -1;
        this.mFilters = NO_FILTERS;
        this.mHighlightColor = 1714664933;
        this.mHighlightPathBogus = true;
        this.mDeviceProvisionedState = 0;
        this.mAutoSizeTextType = 0;
        this.mNeedsAutoSizeText = false;
        float f2 = -1.0f;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = EmptyArray.INT;
        this.mHasPresetAutoSizeValues = false;
        this.mTextSetFromXmlOrResourceId = false;
        this.mTextId = 0;
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        setTextInternal("");
        Resources resources = getResources();
        CompatibilityInfo compatibilityInfo = resources.getCompatibilityInfo();
        this.mTextPaint = new TextPaint(1);
        this.mTextPaint.density = resources.getDisplayMetrics().density;
        this.mTextPaint.setCompatibilityScaling(compatibilityInfo.applicationScale);
        this.mHighlightPaint = new Paint(1);
        this.mHighlightPaint.setCompatibilityScaling(compatibilityInfo.applicationScale);
        this.mMovement = getDefaultMovementMethod();
        this.mTransformation = null;
        TextAppearanceAttributes textAppearanceAttributes2 = new TextAppearanceAttributes();
        textAppearanceAttributes2.mTextColor = ColorStateList.valueOf(-16777216);
        textAppearanceAttributes2.mTextSize = 15;
        this.mBreakStrategy = 0;
        this.mHyphenationFrequency = 0;
        this.mJustificationMode = 0;
        Resources.Theme theme = context.getTheme();
        TypedArray typedArrayObtainStyledAttributes2 = theme.obtainStyledAttributes(attributeSet, R.styleable.TextViewAppearance, i, i2);
        int resourceId = typedArrayObtainStyledAttributes2.getResourceId(0, -1);
        typedArrayObtainStyledAttributes2.recycle();
        TypedArray typedArrayObtainStyledAttributes3 = resourceId != -1 ? theme.obtainStyledAttributes(resourceId, R.styleable.TextAppearance) : null;
        if (typedArrayObtainStyledAttributes3 != null) {
            readTextAppearance(context, typedArrayObtainStyledAttributes3, textAppearanceAttributes2, false);
            textAppearanceAttributes2.mFontFamilyExplicit = false;
            typedArrayObtainStyledAttributes3.recycle();
        }
        boolean defaultEditable = getDefaultEditable();
        ?? ObtainStyledAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.TextView, i, i2);
        readTextAppearance(context, ObtainStyledAttributes, textAppearanceAttributes2, true);
        int indexCount2 = ObtainStyledAttributes.getIndexCount();
        int i15 = -1;
        int i16 = -1;
        int i17 = -1;
        int dimensionPixelSize = -1;
        int dimensionPixelSize2 = -1;
        int dimensionPixelSize3 = -1;
        boolean z10 = false;
        int i18 = 0;
        boolean z11 = false;
        boolean z12 = false;
        int i19 = 0;
        boolean z13 = false;
        boolean z14 = false;
        int dimensionPixelSize4 = 0;
        boolean z15 = false;
        boolean z16 = defaultEditable;
        float fApplyDimension = -1.0f;
        float dimension = -1.0f;
        float dimension2 = -1.0f;
        CharSequence text = "";
        PorterDuff.Mode tintMode = null;
        CharSequence text2 = null;
        CharSequence text3 = null;
        Drawable drawable = null;
        Drawable drawable2 = null;
        Drawable drawable3 = null;
        Drawable drawable4 = null;
        Drawable drawable5 = null;
        Drawable drawable6 = null;
        ColorStateList colorStateList2 = null;
        CharSequence text4 = null;
        int i20 = 0;
        int inputType = 0;
        while (i20 < indexCount2) {
            int index2 = ObtainStyledAttributes.getIndex(i20);
            if (index2 == 0) {
                indexCount2 = indexCount2;
                textAppearanceAttributes2 = textAppearanceAttributes2;
                i18 = i18;
                z11 = z11;
                z12 = z12;
                i19 = i19;
                z13 = z13;
                i16 = i16;
                z14 = z14;
                z16 = z16;
                dimensionPixelSize4 = dimensionPixelSize4;
                tintMode = tintMode;
                setEnabled(ObtainStyledAttributes.getBoolean(index2, isEnabled()));
            } else if (index2 != 67) {
                switch (index2) {
                    case 9:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        i15 = ObtainStyledAttributes.getInt(index2, i15);
                        break;
                    case 10:
                        setGravity(ObtainStyledAttributes.getInt(index2, i14));
                        break;
                    case 11:
                        this.mAutoLinkMask = ObtainStyledAttributes.getInt(index2, r8 == true ? 1 : 0);
                        break;
                    case 12:
                        this.mLinksClickable = ObtainStyledAttributes.getBoolean(index2, true);
                        break;
                    case 13:
                        setMaxWidth(ObtainStyledAttributes.getDimensionPixelSize(index2, i14));
                        break;
                    case 14:
                        setMaxHeight(ObtainStyledAttributes.getDimensionPixelSize(index2, i14));
                        break;
                    case 15:
                        setMinWidth(ObtainStyledAttributes.getDimensionPixelSize(index2, i14));
                        break;
                    case 16:
                        setMinHeight(ObtainStyledAttributes.getDimensionPixelSize(index2, i14));
                        break;
                    case 17:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        i18 = ObtainStyledAttributes.getInt(index2, i18);
                        break;
                    case 18:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        this.mTextId = ObtainStyledAttributes.getResourceId(index2, r8 == true ? 1 : 0);
                        text = ObtainStyledAttributes.getText(index2);
                        z15 = true;
                        break;
                    case 19:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        text4 = ObtainStyledAttributes.getText(index2);
                        break;
                    case 20:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        i13 = dimensionPixelSize4;
                        tintMode = tintMode;
                        setTextScaleX(ObtainStyledAttributes.getFloat(index2, 1.0f));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 21:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        i13 = dimensionPixelSize4;
                        tintMode = tintMode;
                        if (!ObtainStyledAttributes.getBoolean(index2, true)) {
                            setCursorVisible(false);
                        }
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 22:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i21 = i14;
                        i13 = dimensionPixelSize4;
                        setMaxLines(ObtainStyledAttributes.getInt(index2, i21));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 23:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i22 = i14;
                        i13 = dimensionPixelSize4;
                        setLines(ObtainStyledAttributes.getInt(index2, i22));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 24:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i23 = i14;
                        i13 = dimensionPixelSize4;
                        setHeight(ObtainStyledAttributes.getDimensionPixelSize(index2, i23));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 25:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i24 = i14;
                        i13 = dimensionPixelSize4;
                        setMinLines(ObtainStyledAttributes.getInt(index2, i24));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 26:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i25 = i14;
                        i13 = dimensionPixelSize4;
                        setMaxEms(ObtainStyledAttributes.getInt(index2, i25));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 27:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i26 = i14;
                        i13 = dimensionPixelSize4;
                        setEms(ObtainStyledAttributes.getInt(index2, i26));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 28:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i27 = i14;
                        i13 = dimensionPixelSize4;
                        setWidth(ObtainStyledAttributes.getDimensionPixelSize(index2, i27));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 29:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        tintMode = tintMode;
                        int i28 = i14;
                        i13 = dimensionPixelSize4;
                        setMinEms(ObtainStyledAttributes.getInt(index2, i28));
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 30:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = z11;
                        z12 = z12;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z16 = z16;
                        i13 = dimensionPixelSize4;
                        tintMode = tintMode;
                        ?? r14 = r8;
                        z14 = z14;
                        if (ObtainStyledAttributes.getBoolean(index2, r14)) {
                            setHorizontallyScrolling(true);
                        }
                        dimensionPixelSize4 = i13;
                        i18 = i18;
                        break;
                    case 31:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z10 = ObtainStyledAttributes.getBoolean(index2, z10);
                        break;
                    case 32:
                        indexCount2 = indexCount2;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        z11 = ObtainStyledAttributes.getBoolean(index2, z11);
                        break;
                    case 33:
                        z12 = ObtainStyledAttributes.getBoolean(index2, z12);
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        break;
                    case 34:
                        indexCount2 = indexCount2;
                        i19 = i19;
                        z13 = z13;
                        i16 = i16;
                        z14 = z14;
                        z16 = z16;
                        int i29 = dimensionPixelSize4;
                        tintMode = tintMode;
                        if (!ObtainStyledAttributes.getBoolean(index2, true)) {
                            setIncludeFontPadding(false);
                        }
                        dimensionPixelSize4 = i29;
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        i18 = i18;
                        z11 = z11;
                        z12 = z12;
                        break;
                    case 35:
                        i17 = ObtainStyledAttributes.getInt(index2, i14);
                        textAppearanceAttributes2 = textAppearanceAttributes2;
                        break;
                    default:
                        switch (index2) {
                            case 40:
                                i19 = ObtainStyledAttributes.getInt(index2, i19);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 41:
                                text3 = ObtainStyledAttributes.getText(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 42:
                                z13 = ObtainStyledAttributes.getBoolean(index2, z13);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 43:
                                text2 = ObtainStyledAttributes.getText(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 44:
                                i16 = ObtainStyledAttributes.getInt(index2, i16);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 45:
                                z14 = ObtainStyledAttributes.getBoolean(index2, z14);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 46:
                                z16 = ObtainStyledAttributes.getBoolean(index2, z16);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 47:
                                tintMode = tintMode;
                                this.mFreezesText = ObtainStyledAttributes.getBoolean(index2, r8);
                                indexCount2 = indexCount2;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                break;
                            case 48:
                                drawable2 = ObtainStyledAttributes.getDrawable(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 49:
                                drawable4 = ObtainStyledAttributes.getDrawable(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 50:
                                drawable = ObtainStyledAttributes.getDrawable(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 51:
                                drawable3 = ObtainStyledAttributes.getDrawable(index2);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 52:
                                dimensionPixelSize4 = ObtainStyledAttributes.getDimensionPixelSize(index2, dimensionPixelSize4);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 53:
                                tintMode = tintMode;
                                this.mSpacingAdd = ObtainStyledAttributes.getDimensionPixelSize(index2, (int) this.mSpacingAdd);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 54:
                                tintMode = tintMode;
                                this.mSpacingMult = ObtainStyledAttributes.getFloat(index2, this.mSpacingMult);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 55:
                                tintMode = tintMode;
                                setMarqueeRepeatLimit(ObtainStyledAttributes.getInt(index2, this.mMarqueeRepeatLimit));
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 56:
                                inputType = ObtainStyledAttributes.getInt(index2, r8);
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                break;
                            case 57:
                                tintMode = tintMode;
                                setPrivateImeOptions(ObtainStyledAttributes.getString(index2));
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 58:
                                tintMode = tintMode;
                                try {
                                    setInputExtras(ObtainStyledAttributes.getResourceId(index2, r8));
                                } catch (IOException e) {
                                    Log.w(LOG_TAG, "Failure reading input extras", e);
                                } catch (XmlPullParserException e2) {
                                    Log.w(LOG_TAG, "Failure reading input extras", e2);
                                }
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 59:
                                tintMode = tintMode;
                                createEditorIfNeeded();
                                this.mEditor.createInputContentTypeIfNeeded();
                                this.mEditor.mInputContentType.imeOptions = ObtainStyledAttributes.getInt(index2, this.mEditor.mInputContentType.imeOptions);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 60:
                                tintMode = tintMode;
                                createEditorIfNeeded();
                                this.mEditor.createInputContentTypeIfNeeded();
                                this.mEditor.mInputContentType.imeActionLabel = ObtainStyledAttributes.getText(index2);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 61:
                                tintMode = tintMode;
                                createEditorIfNeeded();
                                this.mEditor.createInputContentTypeIfNeeded();
                                this.mEditor.mInputContentType.imeActionId = ObtainStyledAttributes.getInt(index2, this.mEditor.mInputContentType.imeActionId);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 62:
                                tintMode = tintMode;
                                this.mTextSelectHandleLeftRes = ObtainStyledAttributes.getResourceId(index2, r8);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 63:
                                tintMode = tintMode;
                                this.mTextSelectHandleRightRes = ObtainStyledAttributes.getResourceId(index2, r8);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            case 64:
                                tintMode = tintMode;
                                this.mTextSelectHandleRes = ObtainStyledAttributes.getResourceId(index2, r8);
                                indexCount2 = indexCount2;
                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                i18 = i18;
                                z11 = z11;
                                z12 = z12;
                                i19 = i19;
                                z13 = z13;
                                i16 = i16;
                                z14 = z14;
                                z16 = z16;
                                dimensionPixelSize4 = dimensionPixelSize4;
                                break;
                            default:
                                switch (index2) {
                                    case 70:
                                        tintMode = tintMode;
                                        this.mCursorDrawableRes = ObtainStyledAttributes.getResourceId(index2, r8);
                                        indexCount2 = indexCount2;
                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                        i18 = i18;
                                        z11 = z11;
                                        z12 = z12;
                                        i19 = i19;
                                        z13 = z13;
                                        i16 = i16;
                                        z14 = z14;
                                        z16 = z16;
                                        dimensionPixelSize4 = dimensionPixelSize4;
                                        break;
                                    case 71:
                                        tintMode = tintMode;
                                        this.mTextEditSuggestionItemLayout = ObtainStyledAttributes.getResourceId(index2, r8);
                                        indexCount2 = indexCount2;
                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                        i18 = i18;
                                        z11 = z11;
                                        z12 = z12;
                                        i19 = i19;
                                        z13 = z13;
                                        i16 = i16;
                                        z14 = z14;
                                        z16 = z16;
                                        dimensionPixelSize4 = dimensionPixelSize4;
                                        break;
                                    default:
                                        switch (index2) {
                                            case 73:
                                                drawable5 = ObtainStyledAttributes.getDrawable(index2);
                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                break;
                                            case 74:
                                                drawable6 = ObtainStyledAttributes.getDrawable(index2);
                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                break;
                                            default:
                                                switch (index2) {
                                                    case 79:
                                                        colorStateList2 = ObtainStyledAttributes.getColorStateList(index2);
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        break;
                                                    case 80:
                                                        tintMode = Drawable.parseTintMode(ObtainStyledAttributes.getInt(index2, i14), tintMode);
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        break;
                                                    case 81:
                                                        this.mBreakStrategy = ObtainStyledAttributes.getInt(index2, r8);
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    case 82:
                                                        this.mHyphenationFrequency = ObtainStyledAttributes.getInt(index2, r8);
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    case 83:
                                                        createEditorIfNeeded();
                                                        this.mEditor.mAllowUndo = ObtainStyledAttributes.getBoolean(index2, true);
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    case 84:
                                                        this.mAutoSizeTextType = ObtainStyledAttributes.getInt(index2, r8);
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    case 85:
                                                        dimension2 = ObtainStyledAttributes.getDimension(index2, f2);
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        break;
                                                    case 86:
                                                        int resourceId2 = ObtainStyledAttributes.getResourceId(index2, r8);
                                                        if (resourceId2 > 0) {
                                                            TypedArray typedArrayObtainTypedArray = ObtainStyledAttributes.getResources().obtainTypedArray(resourceId2);
                                                            setupAutoSizeUniformPresetSizes(typedArrayObtainTypedArray);
                                                            typedArrayObtainTypedArray.recycle();
                                                        }
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    case 87:
                                                        fApplyDimension = ObtainStyledAttributes.getDimension(index2, f2);
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        break;
                                                    case 88:
                                                        dimension = ObtainStyledAttributes.getDimension(index2, f2);
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        break;
                                                    case 89:
                                                        this.mJustificationMode = ObtainStyledAttributes.getInt(index2, r8);
                                                        indexCount2 = indexCount2;
                                                        textAppearanceAttributes2 = textAppearanceAttributes2;
                                                        i18 = i18;
                                                        z11 = z11;
                                                        z12 = z12;
                                                        i19 = i19;
                                                        z13 = z13;
                                                        i16 = i16;
                                                        z14 = z14;
                                                        z16 = z16;
                                                        dimensionPixelSize4 = dimensionPixelSize4;
                                                        tintMode = tintMode;
                                                        break;
                                                    default:
                                                        switch (index2) {
                                                            case 91:
                                                                dimensionPixelSize = ObtainStyledAttributes.getDimensionPixelSize(index2, i14);
                                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                                break;
                                                            case 92:
                                                                dimensionPixelSize2 = ObtainStyledAttributes.getDimensionPixelSize(index2, i14);
                                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                                break;
                                                            case 93:
                                                                dimensionPixelSize3 = ObtainStyledAttributes.getDimensionPixelSize(index2, i14);
                                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                                break;
                                                            default:
                                                                switch (index2) {
                                                                    case 95:
                                                                        this.mTextEditSuggestionContainerLayout = ObtainStyledAttributes.getResourceId(index2, r8);
                                                                        break;
                                                                    case 96:
                                                                        this.mTextEditSuggestionHighlightStyle = ObtainStyledAttributes.getResourceId(index2, r8);
                                                                        break;
                                                                }
                                                                indexCount2 = indexCount2;
                                                                textAppearanceAttributes2 = textAppearanceAttributes2;
                                                                i18 = i18;
                                                                z11 = z11;
                                                                z12 = z12;
                                                                i19 = i19;
                                                                z13 = z13;
                                                                i16 = i16;
                                                                z14 = z14;
                                                                z16 = z16;
                                                                dimensionPixelSize4 = dimensionPixelSize4;
                                                                tintMode = tintMode;
                                                                break;
                                                        }
                                                        break;
                                                }
                                                break;
                                        }
                                        break;
                                }
                                break;
                        }
                        break;
                }
                i20++;
                indexCount2 = indexCount2;
                textAppearanceAttributes2 = textAppearanceAttributes2;
                i14 = -1;
                r8 = 0;
                f2 = -1.0f;
            } else {
                indexCount2 = indexCount2;
                textAppearanceAttributes2 = textAppearanceAttributes2;
                i18 = i18;
                z11 = z11;
                z12 = z12;
                i19 = i19;
                z13 = z13;
                i16 = i16;
                z16 = z16;
                dimensionPixelSize4 = dimensionPixelSize4;
                tintMode = tintMode;
                ?? r15 = r8;
                z14 = z14;
                setTextIsSelectable(ObtainStyledAttributes.getBoolean(index2, r15));
            }
            z13 = z13;
            i19 = i19;
            z12 = z12;
            i16 = i16;
            i18 = i18;
            z14 = z14;
            tintMode = tintMode;
            z16 = z16;
            z11 = z11;
            dimensionPixelSize4 = dimensionPixelSize4;
            i15 = i15;
            i20++;
            indexCount2 = indexCount2;
            textAppearanceAttributes2 = textAppearanceAttributes2;
            i14 = -1;
            r8 = 0;
            f2 = -1.0f;
        }
        TextAppearanceAttributes textAppearanceAttributes3 = textAppearanceAttributes2;
        int i30 = i15;
        int i31 = i18;
        boolean z17 = z11;
        boolean z18 = z12;
        int i32 = i19;
        boolean z19 = z13;
        int i33 = i16;
        boolean z20 = z14;
        boolean z21 = z16;
        int i34 = dimensionPixelSize4;
        PorterDuff.Mode mode = tintMode;
        ObtainStyledAttributes.recycle();
        BufferType bufferType2 = BufferType.EDITABLE;
        int i35 = inputType & FileObserver.ALL_EVENTS;
        boolean z22 = i35 == 129;
        boolean z23 = i35 == 225;
        boolean z24 = i35 == 18;
        int i36 = context.getApplicationInfo().targetSdkVersion;
        this.mUseInternationalizedInput = i36 >= 26;
        this.mUseFallbackLineSpacing = i36 >= 28;
        if (text2 != null) {
            try {
                Class<?> cls = Class.forName(text2.toString());
                try {
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = (KeyListener) cls.newInstance();
                    try {
                        Editor editor = this.mEditor;
                        if (inputType == 0) {
                            inputType = this.mEditor.mKeyListener.getInputType();
                        }
                        editor.mInputType = inputType;
                    } catch (IncompatibleClassChangeError unused) {
                        this.mEditor.mInputType = 1;
                    }
                } catch (IllegalAccessException e3) {
                    throw new RuntimeException(e3);
                } catch (InstantiationException e4) {
                    throw new RuntimeException(e4);
                }
            } catch (ClassNotFoundException e5) {
                throw new RuntimeException(e5);
            }
        } else {
            if (text3 == null) {
                if (inputType != 0) {
                    setInputType(inputType, true);
                    z = !isMultilineInputType(inputType);
                    bufferType = bufferType2;
                } else if (z19) {
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = DialerKeyListener.getInstance();
                    this.mEditor.mInputType = 3;
                } else if (i32 != 0) {
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = DigitsKeyListener.getInstance(null, (i32 & 2) != 0, (i32 & 4) != 0);
                    this.mEditor.mInputType = this.mEditor.mKeyListener.getInputType();
                } else if (z20 || i33 != -1) {
                    switch (i33) {
                        case 1:
                            capitalize = TextKeyListener.Capitalize.SENTENCES;
                            i12 = 16385;
                            break;
                        case 2:
                            capitalize = TextKeyListener.Capitalize.WORDS;
                            i12 = MtpConstants.RESPONSE_OK;
                            break;
                        case 3:
                            capitalize = TextKeyListener.Capitalize.CHARACTERS;
                            i12 = 4097;
                            break;
                        default:
                            capitalize = TextKeyListener.Capitalize.NONE;
                            i12 = 1;
                            break;
                    }
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = TextKeyListener.getInstance(z20, capitalize);
                    this.mEditor.mInputType = i12;
                } else if (z21) {
                    createEditorIfNeeded();
                    this.mEditor.mKeyListener = TextKeyListener.getInstance();
                    this.mEditor.mInputType = 1;
                } else {
                    if (!isTextSelectable()) {
                        if (this.mEditor != null) {
                            this.mEditor.mKeyListener = null;
                        }
                        switch (i31) {
                            case 0:
                                bufferType = BufferType.NORMAL;
                                break;
                            case 1:
                                bufferType = BufferType.SPANNABLE;
                                break;
                            case 2:
                                bufferType = BufferType.EDITABLE;
                                break;
                        }
                    } else {
                        if (this.mEditor != null) {
                            this.mEditor.mKeyListener = null;
                            this.mEditor.mInputType = 0;
                        }
                        bufferType = BufferType.SPANNABLE;
                        setMovementMethod(ArrowKeyMovementMethod.getInstance());
                    }
                    z = z17;
                }
                if (this.mEditor != null) {
                    this.mEditor.adjustInputType(z10, z22, z23, z24);
                }
                if (z18) {
                    createEditorIfNeeded();
                    this.mEditor.mSelectAllOnFocus = true;
                    if (bufferType == BufferType.NORMAL) {
                        bufferType = BufferType.SPANNABLE;
                    }
                }
                colorStateList = colorStateList2;
                if (colorStateList == null || mode != null) {
                    if (this.mDrawables == null) {
                        context2 = context;
                        this.mDrawables = new Drawables(context2);
                    } else {
                        context2 = context;
                    }
                    if (colorStateList != null) {
                        this.mDrawables.mTintList = colorStateList;
                        z2 = true;
                        this.mDrawables.mHasTint = true;
                    } else {
                        z2 = true;
                    }
                    if (mode != null) {
                        this.mDrawables.mTintMode = mode;
                        this.mDrawables.mHasTintMode = z2;
                    }
                } else {
                    context2 = context;
                }
                setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
                setRelativeDrawablesIfNeeded(drawable5, drawable6);
                setCompoundDrawablePadding(i34);
                setInputTypeSingleLine(z);
                applySingleLine(z, z, z);
                if (z || getKeyListener() != null) {
                    i3 = i30;
                } else {
                    i3 = i30;
                    if (i3 == -1) {
                        i3 = 3;
                    }
                }
                switch (i3) {
                    case 1:
                        setEllipsize(TextUtils.TruncateAt.START);
                        break;
                    case 2:
                        setEllipsize(TextUtils.TruncateAt.MIDDLE);
                        break;
                    case 3:
                        setEllipsize(TextUtils.TruncateAt.END);
                        break;
                    case 4:
                        if (ViewConfiguration.get(context).isFadingMarqueeEnabled()) {
                            setHorizontalFadingEdgeEnabled(true);
                            this.mMarqueeFadeMode = 0;
                        } else {
                            setHorizontalFadingEdgeEnabled(false);
                            this.mMarqueeFadeMode = 1;
                        }
                        setEllipsize(TextUtils.TruncateAt.MARQUEE);
                        break;
                }
                if (!z10 || z22 || z23 || z24) {
                    z3 = true;
                } else {
                    z3 = false;
                }
                if (!z3 || (this.mEditor != null && (this.mEditor.mInputType & FileObserver.ALL_EVENTS) == 129)) {
                    z4 = true;
                } else {
                    z4 = false;
                }
                if (z4) {
                    textAppearanceAttributes = textAppearanceAttributes3;
                    textAppearanceAttributes.mTypefaceIndex = 3;
                } else {
                    textAppearanceAttributes = textAppearanceAttributes3;
                }
                applyTextAppearance(textAppearanceAttributes);
                if (z3) {
                    setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
                i4 = i17;
                if (i4 >= 0) {
                    z5 = true;
                    setFilters(new InputFilter[]{new InputFilter.LengthFilter(i4)});
                } else {
                    z5 = true;
                    setFilters(NO_FILTERS);
                }
                setText(text, bufferType);
                if (z15) {
                    this.mTextSetFromXmlOrResourceId = z5;
                }
                charSequence = text4;
                if (charSequence != null) {
                    setHint(charSequence);
                }
                typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.View, i, i2);
                if (this.mMovement == null || getKeyListener() != null) {
                    z6 = true;
                } else {
                    z6 = false;
                }
                if (!z6 || isClickable()) {
                    z7 = true;
                } else {
                    z7 = false;
                }
                if (!z6 || isLongClickable()) {
                    z8 = true;
                } else {
                    z8 = false;
                }
                focusable = getFocusable();
                indexCount = typedArrayObtainStyledAttributes.getIndexCount();
                z9 = z8;
                for (i5 = 0; i5 < indexCount; i5++) {
                    index = typedArrayObtainStyledAttributes.getIndex(i5);
                    if (index != 19) {
                        switch (index) {
                            case 30:
                                z7 = typedArrayObtainStyledAttributes.getBoolean(index, z7);
                                break;
                            case 31:
                                z9 = typedArrayObtainStyledAttributes.getBoolean(index, z9);
                                break;
                        }
                    } else {
                        typedValue = new TypedValue();
                        if (typedArrayObtainStyledAttributes.getValue(index, typedValue)) {
                            if (typedValue.type == 18) {
                                i11 = typedValue.data;
                            } else if (typedValue.data == 0) {
                                i11 = 0;
                            } else {
                                i11 = 1;
                            }
                            focusable = i11;
                        }
                    }
                }
                typedArrayObtainStyledAttributes.recycle();
                if (focusable != getFocusable()) {
                    setFocusable(focusable);
                }
                setClickable(z7);
                setLongClickable(z9);
                if (this.mEditor != null) {
                    this.mEditor.prepareCursorControllers();
                }
                if (getImportantForAccessibility() == 0) {
                    i6 = 1;
                    setImportantForAccessibility(1);
                } else {
                    i6 = 1;
                }
                if (supportsAutoSizeText()) {
                    this.mAutoSizeTextType = 0;
                } else if (this.mAutoSizeTextType == i6) {
                    if (!this.mHasPresetAutoSizeValues) {
                        displayMetrics = getResources().getDisplayMetrics();
                        if (fApplyDimension == -1.0f) {
                            i10 = 2;
                            fApplyDimension = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                        } else {
                            i10 = 2;
                        }
                        float f3 = fApplyDimension;
                        float fApplyDimension2 = dimension == -1.0f ? TypedValue.applyDimension(i10, 112.0f, displayMetrics) : dimension;
                        if (dimension2 == -1.0f) {
                            f = 1.0f;
                        } else {
                            f = dimension2;
                        }
                        validateAndSetAutoSizeTextTypeUniformConfiguration(f3, fApplyDimension2, f);
                    }
                    setupAutoSizeText();
                }
                i7 = dimensionPixelSize;
                if (i7 >= 0) {
                    setFirstBaselineToTopHeight(i7);
                }
                i8 = dimensionPixelSize2;
                if (i8 >= 0) {
                    setLastBaselineToBottomHeight(i8);
                }
                i9 = dimensionPixelSize3;
                if (i9 >= 0) {
                    setLineHeight(i9);
                }
            }
            createEditorIfNeeded();
            this.mEditor.mKeyListener = DigitsKeyListener.getInstance(text3.toString());
            this.mEditor.mInputType = inputType == 0 ? 1 : inputType;
        }
        z = z17;
        bufferType = bufferType2;
        if (this.mEditor != null) {
            this.mEditor.adjustInputType(z10, z22, z23, z24);
        }
        if (z18) {
            createEditorIfNeeded();
            this.mEditor.mSelectAllOnFocus = true;
            if (bufferType == BufferType.NORMAL) {
                bufferType = BufferType.SPANNABLE;
            }
        }
        colorStateList = colorStateList2;
        if (colorStateList == null) {
            if (this.mDrawables == null) {
                context2 = context;
                this.mDrawables = new Drawables(context2);
            } else {
                context2 = context;
            }
            if (colorStateList != null) {
                this.mDrawables.mTintList = colorStateList;
                z2 = true;
                this.mDrawables.mHasTint = true;
            } else {
                z2 = true;
            }
            if (mode != null) {
                this.mDrawables.mTintMode = mode;
                this.mDrawables.mHasTintMode = z2;
            }
        } else {
            if (this.mDrawables == null) {
                context2 = context;
                this.mDrawables = new Drawables(context2);
            } else {
                context2 = context;
            }
            if (colorStateList != null) {
                this.mDrawables.mTintList = colorStateList;
                z2 = true;
                this.mDrawables.mHasTint = true;
            } else {
                z2 = true;
            }
            if (mode != null) {
                this.mDrawables.mTintMode = mode;
                this.mDrawables.mHasTintMode = z2;
            }
        }
        setCompoundDrawablesWithIntrinsicBounds(drawable, drawable2, drawable3, drawable4);
        setRelativeDrawablesIfNeeded(drawable5, drawable6);
        setCompoundDrawablePadding(i34);
        setInputTypeSingleLine(z);
        applySingleLine(z, z, z);
        if (z) {
            i3 = i30;
        } else {
            i3 = i30;
        }
        switch (i3) {
            case 1:
                setEllipsize(TextUtils.TruncateAt.START);
                break;
            case 2:
                setEllipsize(TextUtils.TruncateAt.MIDDLE);
                break;
            case 3:
                setEllipsize(TextUtils.TruncateAt.END);
                break;
            case 4:
                if (ViewConfiguration.get(context).isFadingMarqueeEnabled()) {
                    setHorizontalFadingEdgeEnabled(true);
                    this.mMarqueeFadeMode = 0;
                } else {
                    setHorizontalFadingEdgeEnabled(false);
                    this.mMarqueeFadeMode = 1;
                }
                setEllipsize(TextUtils.TruncateAt.MARQUEE);
                break;
        }
        if (z10) {
            z3 = true;
        } else {
            z3 = true;
        }
        if (z3) {
            z4 = true;
        } else {
            z4 = true;
        }
        if (z4) {
            textAppearanceAttributes = textAppearanceAttributes3;
            textAppearanceAttributes.mTypefaceIndex = 3;
        } else {
            textAppearanceAttributes = textAppearanceAttributes3;
        }
        applyTextAppearance(textAppearanceAttributes);
        if (z3) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        i4 = i17;
        if (i4 >= 0) {
            z5 = true;
            setFilters(new InputFilter[]{new InputFilter.LengthFilter(i4)});
        } else {
            z5 = true;
            setFilters(NO_FILTERS);
        }
        setText(text, bufferType);
        if (z15) {
            this.mTextSetFromXmlOrResourceId = z5;
        }
        charSequence = text4;
        if (charSequence != null) {
            setHint(charSequence);
        }
        typedArrayObtainStyledAttributes = context2.obtainStyledAttributes(attributeSet, R.styleable.View, i, i2);
        if (this.mMovement == null) {
            z6 = true;
        } else {
            z6 = true;
        }
        if (z6) {
            z7 = true;
        } else {
            z7 = true;
        }
        if (z6) {
            z8 = true;
        } else {
            z8 = true;
        }
        focusable = getFocusable();
        indexCount = typedArrayObtainStyledAttributes.getIndexCount();
        z9 = z8;
        while (i5 < indexCount) {
            index = typedArrayObtainStyledAttributes.getIndex(i5);
            if (index != 19) {
                switch (index) {
                    case 30:
                        z7 = typedArrayObtainStyledAttributes.getBoolean(index, z7);
                        break;
                    case 31:
                        z9 = typedArrayObtainStyledAttributes.getBoolean(index, z9);
                        break;
                }
            } else {
                typedValue = new TypedValue();
                if (typedArrayObtainStyledAttributes.getValue(index, typedValue)) {
                    if (typedValue.type == 18) {
                        i11 = typedValue.data;
                    } else if (typedValue.data == 0) {
                        i11 = 0;
                    } else {
                        i11 = 1;
                    }
                    focusable = i11;
                }
            }
        }
        typedArrayObtainStyledAttributes.recycle();
        if (focusable != getFocusable()) {
            setFocusable(focusable);
        }
        setClickable(z7);
        setLongClickable(z9);
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
        if (getImportantForAccessibility() == 0) {
            i6 = 1;
            setImportantForAccessibility(1);
        } else {
            i6 = 1;
        }
        if (supportsAutoSizeText()) {
            this.mAutoSizeTextType = 0;
        } else if (this.mAutoSizeTextType == i6) {
            if (!this.mHasPresetAutoSizeValues) {
                displayMetrics = getResources().getDisplayMetrics();
                if (fApplyDimension == -1.0f) {
                    i10 = 2;
                    fApplyDimension = TypedValue.applyDimension(2, 12.0f, displayMetrics);
                } else {
                    i10 = 2;
                }
                float f4 = fApplyDimension;
                float fApplyDimension3 = dimension == -1.0f ? TypedValue.applyDimension(i10, 112.0f, displayMetrics) : dimension;
                if (dimension2 == -1.0f) {
                    f = 1.0f;
                } else {
                    f = dimension2;
                }
                validateAndSetAutoSizeTextTypeUniformConfiguration(f4, fApplyDimension3, f);
            }
            setupAutoSizeText();
        }
        i7 = dimensionPixelSize;
        if (i7 >= 0) {
            setFirstBaselineToTopHeight(i7);
        }
        i8 = dimensionPixelSize2;
        if (i8 >= 0) {
            setLastBaselineToBottomHeight(i8);
        }
        i9 = dimensionPixelSize3;
        if (i9 >= 0) {
            setLineHeight(i9);
        }
    }

    private void applyCompoundDrawableTint() {
        if (this.mDrawables == null) {
            return;
        }
        if (this.mDrawables.mHasTint || this.mDrawables.mHasTintMode) {
            ColorStateList colorStateList = this.mDrawables.mTintList;
            PorterDuff.Mode mode = this.mDrawables.mTintMode;
            boolean z = this.mDrawables.mHasTint;
            boolean z2 = this.mDrawables.mHasTintMode;
            int[] drawableState = getDrawableState();
            for (Drawable drawable : this.mDrawables.mShowing) {
                if (drawable != null && drawable != this.mDrawables.mDrawableError) {
                    drawable.mutate();
                    if (z) {
                        drawable.setTintList(colorStateList);
                    }
                    if (z2) {
                        drawable.setTintMode(mode);
                    }
                    if (drawable.isStateful()) {
                        drawable.setState(drawableState);
                    }
                }
            }
        }
    }

    private void applySingleLine(boolean z, boolean z2, boolean z3) {
        this.mSingleLine = z;
        if (z) {
            setLines(1);
            setHorizontallyScrolling(true);
            if (z2) {
                setTransformationMethod(SingleLineTransformationMethod.getInstance());
                return;
            }
            return;
        }
        if (z3) {
            setMaxLines(Integer.MAX_VALUE);
        }
        setHorizontallyScrolling(false);
        if (z2) {
            setTransformationMethod(null);
        }
    }

    private void applyTextAppearance(TextAppearanceAttributes textAppearanceAttributes) {
        if (textAppearanceAttributes.mTextColor != null) {
            setTextColor(textAppearanceAttributes.mTextColor);
        }
        if (textAppearanceAttributes.mTextColorHint != null) {
            setHintTextColor(textAppearanceAttributes.mTextColorHint);
        }
        if (textAppearanceAttributes.mTextColorLink != null) {
            setLinkTextColor(textAppearanceAttributes.mTextColorLink);
        }
        if (textAppearanceAttributes.mTextColorHighlight != 0) {
            setHighlightColor(textAppearanceAttributes.mTextColorHighlight);
        }
        if (textAppearanceAttributes.mTextSize != 0) {
            setRawTextSize(textAppearanceAttributes.mTextSize, true);
        }
        if (textAppearanceAttributes.mTypefaceIndex != -1 && !textAppearanceAttributes.mFontFamilyExplicit) {
            textAppearanceAttributes.mFontFamily = null;
        }
        setTypefaceFromAttrs(textAppearanceAttributes.mFontTypeface, textAppearanceAttributes.mFontFamily, textAppearanceAttributes.mTypefaceIndex, textAppearanceAttributes.mStyleIndex, textAppearanceAttributes.mFontWeight);
        if (textAppearanceAttributes.mShadowColor != 0) {
            setShadowLayer(textAppearanceAttributes.mShadowRadius, textAppearanceAttributes.mShadowDx, textAppearanceAttributes.mShadowDy, textAppearanceAttributes.mShadowColor);
        }
        if (textAppearanceAttributes.mAllCaps) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        }
        if (textAppearanceAttributes.mHasElegant) {
            setElegantTextHeight(textAppearanceAttributes.mElegant);
        }
        if (textAppearanceAttributes.mHasFallbackLineSpacing) {
            setFallbackLineSpacing(textAppearanceAttributes.mFallbackLineSpacing);
        }
        if (textAppearanceAttributes.mHasLetterSpacing) {
            setLetterSpacing(textAppearanceAttributes.mLetterSpacing);
        }
        if (textAppearanceAttributes.mFontFeatureSettings != null) {
            setFontFeatureSettings(textAppearanceAttributes.mFontFeatureSettings);
        }
    }

    private void assumeLayout() {
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (compoundPaddingLeft < 1) {
            compoundPaddingLeft = 0;
        }
        int i = compoundPaddingLeft;
        makeNewLayout(this.mHorizontallyScrolling ? 1048576 : i, i, UNKNOWN_BORING, UNKNOWN_BORING, i, false);
    }

    private void autoSizeText() {
        if (isAutoSizeEnabled()) {
            if (this.mNeedsAutoSizeText) {
                if (getMeasuredWidth() <= 0 || getMeasuredHeight() <= 0) {
                    return;
                }
                int measuredWidth = this.mHorizontallyScrolling ? 1048576 : (getMeasuredWidth() - getTotalPaddingLeft()) - getTotalPaddingRight();
                int measuredHeight = (getMeasuredHeight() - getExtendedPaddingBottom()) - getExtendedPaddingTop();
                if (measuredWidth <= 0 || measuredHeight <= 0) {
                    return;
                }
                synchronized (TEMP_RECTF) {
                    TEMP_RECTF.setEmpty();
                    TEMP_RECTF.right = measuredWidth;
                    TEMP_RECTF.bottom = measuredHeight;
                    float fFindLargestTextSizeWhichFits = findLargestTextSizeWhichFits(TEMP_RECTF);
                    if (fFindLargestTextSizeWhichFits != getTextSize()) {
                        setTextSizeInternal(0, fFindLargestTextSizeWhichFits, false);
                        makeNewLayout(measuredWidth, 0, UNKNOWN_BORING, UNKNOWN_BORING, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
                    }
                }
            }
            this.mNeedsAutoSizeText = true;
        }
    }

    private boolean bringTextIntoView() {
        int iCeil;
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        int lineCount = (this.mGravity & 112) == 80 ? layout.getLineCount() - 1 : 0;
        Layout.Alignment paragraphAlignment = layout.getParagraphAlignment(lineCount);
        int paragraphDirection = layout.getParagraphDirection(lineCount);
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int extendedPaddingTop = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int height = layout.getHeight();
        if (paragraphAlignment == Layout.Alignment.ALIGN_NORMAL) {
            paragraphAlignment = paragraphDirection == 1 ? Layout.Alignment.ALIGN_LEFT : Layout.Alignment.ALIGN_RIGHT;
        } else if (paragraphAlignment == Layout.Alignment.ALIGN_OPPOSITE) {
            paragraphAlignment = paragraphDirection == 1 ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT;
        }
        if (paragraphAlignment == Layout.Alignment.ALIGN_CENTER) {
            iCeil = (int) Math.floor(layout.getLineLeft(lineCount));
            int iCeil2 = (int) Math.ceil(layout.getLineRight(lineCount));
            if (iCeil2 - iCeil < compoundPaddingLeft) {
                iCeil = ((iCeil2 + iCeil) / 2) - (compoundPaddingLeft / 2);
            } else if (paragraphDirection < 0) {
                iCeil = iCeil2 - compoundPaddingLeft;
            }
        } else {
            iCeil = paragraphAlignment == Layout.Alignment.ALIGN_RIGHT ? ((int) Math.ceil(layout.getLineRight(lineCount))) - compoundPaddingLeft : (int) Math.floor(layout.getLineLeft(lineCount));
        }
        int i = (height >= extendedPaddingTop && (this.mGravity & 112) == 80) ? height - extendedPaddingTop : 0;
        if (iCeil == this.mScrollX && i == this.mScrollY) {
            return false;
        }
        scrollTo(iCeil, i);
        return true;
    }

    private boolean canMarquee() {
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        if (compoundPaddingLeft <= 0) {
            return false;
        }
        float f = compoundPaddingLeft;
        return this.mLayout.getLineWidth(0) > f || !(this.mMarqueeFadeMode == 0 || this.mSavedMarqueeModeLayout == null || this.mSavedMarqueeModeLayout.getLineWidth(0) <= f);
    }

    private void changeListenerLocaleTo(Locale locale) {
        KeyListener dateTimeKeyListener;
        if (this.mListenerChanged || this.mEditor == null) {
            return;
        }
        KeyListener keyListener = this.mEditor.mKeyListener;
        if (keyListener instanceof DigitsKeyListener) {
            dateTimeKeyListener = DigitsKeyListener.getInstance(locale, (DigitsKeyListener) keyListener);
        } else if (keyListener instanceof DateKeyListener) {
            dateTimeKeyListener = DateKeyListener.getInstance(locale);
        } else if (keyListener instanceof TimeKeyListener) {
            dateTimeKeyListener = TimeKeyListener.getInstance(locale);
        } else if (!(keyListener instanceof DateTimeKeyListener)) {
            return;
        } else {
            dateTimeKeyListener = DateTimeKeyListener.getInstance(locale);
        }
        boolean zIsPasswordInputType = isPasswordInputType(this.mEditor.mInputType);
        setKeyListenerOnly(dateTimeKeyListener);
        setInputTypeFromEditor();
        if (zIsPasswordInputType) {
            int i = this.mEditor.mInputType & 15;
            if (i == 1) {
                this.mEditor.mInputType |= 128;
            } else if (i == 2) {
                this.mEditor.mInputType |= 16;
            }
        }
    }

    private void checkForRelayout() {
        if ((this.mLayoutParams.width == -2 && (this.mMaxWidthMode != this.mMinWidthMode || this.mMaxWidth != this.mMinWidth)) || ((this.mHint != null && this.mHintLayout == null) || ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight() <= 0)) {
            nullLayouts();
            requestLayout();
            invalidate();
            return;
        }
        int height = this.mLayout.getHeight();
        makeNewLayout(this.mLayout.getWidth(), this.mHintLayout == null ? 0 : this.mHintLayout.getWidth(), UNKNOWN_BORING, UNKNOWN_BORING, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
        if (this.mEllipsize != TextUtils.TruncateAt.MARQUEE) {
            if (this.mLayoutParams.height != -2 && this.mLayoutParams.height != -1) {
                autoSizeText();
                invalidate();
                return;
            } else if (this.mLayout.getHeight() == height && (this.mHintLayout == null || this.mHintLayout.getHeight() == height)) {
                autoSizeText();
                invalidate();
                return;
            }
        }
        requestLayout();
        invalidate();
    }

    /* JADX WARN: Code duplicated, block: B:19:0x0036 A[PHI: r2
      0x0036: PHI (r2v1 boolean) = (r2v0 boolean), (r2v2 boolean), (r2v2 boolean), (r2v2 boolean), (r2v2 boolean) binds: [B:3:0x0004, B:13:0x0027, B:15:0x002b, B:17:0x0033, B:10:0x001f] A[DONT_GENERATE, DONT_INLINE]] */
    private void checkForResize() {
        boolean z = true;
        boolean z2 = false;
        if (this.mLayout == null) {
            z = z2;
        } else {
            if (this.mLayoutParams.width == -2) {
                invalidate();
                z2 = true;
            }
            if (this.mLayoutParams.height != -2 ? this.mLayoutParams.height != -1 || this.mDesiredHeightAtMeasure < 0 || getDesiredHeight() == this.mDesiredHeightAtMeasure : getDesiredHeight() == getHeight()) {
                z = z2;
            }
        }
        if (z) {
            requestLayout();
        }
    }

    private int[] cleanupAutoSizePresetSizes(int[] iArr) {
        int length = iArr.length;
        if (length == 0) {
            return iArr;
        }
        Arrays.sort(iArr);
        IntArray intArray = new IntArray();
        for (int i : iArr) {
            if (i > 0 && intArray.binarySearch(i) < 0) {
                intArray.add(i);
            }
        }
        return length == intArray.size() ? iArr : intArray.toArray();
    }

    private void clearAutoSizeConfiguration() {
        this.mAutoSizeTextType = 0;
        this.mAutoSizeMinTextSizeInPx = -1.0f;
        this.mAutoSizeMaxTextSizeInPx = -1.0f;
        this.mAutoSizeStepGranularityInPx = -1.0f;
        this.mAutoSizeTextSizesInPx = EmptyArray.INT;
        this.mNeedsAutoSizeText = false;
    }

    private boolean compressText(float f) {
        if (!isHardwareAccelerated() && f > 0.0f && this.mLayout != null && getLineCount() == 1 && !this.mUserSetTextScaleX && this.mTextPaint.getTextScaleX() == 1.0f) {
            float lineWidth = ((this.mLayout.getLineWidth(0) + 1.0f) - f) / f;
            if (lineWidth > 0.0f && lineWidth <= 0.07f) {
                this.mTextPaint.setTextScaleX((1.0f - lineWidth) - 0.005f);
                post(new Runnable() { // from class: android.widget.TextView.2
                    @Override // java.lang.Runnable
                    public void run() {
                        TextView.this.requestLayout();
                    }
                });
                return true;
            }
        }
        return false;
    }

    private void convertFromViewportToContentCoordinates(Rect rect) {
        int iViewportToContentHorizontalOffset = viewportToContentHorizontalOffset();
        rect.left += iViewportToContentHorizontalOffset;
        rect.right += iViewportToContentHorizontalOffset;
        int iViewportToContentVerticalOffset = viewportToContentVerticalOffset();
        rect.top += iViewportToContentVerticalOffset;
        rect.bottom += iViewportToContentVerticalOffset;
    }

    private void createEditorIfNeeded() {
        if (this.mEditor == null) {
            this.mEditor = new Editor(this);
        }
    }

    private static int desired(Layout layout) {
        int lineCount = layout.getLineCount();
        CharSequence text = layout.getText();
        for (int i = 0; i < lineCount - 1; i++) {
            if (text.charAt(layout.getLineEnd(i) - 1) != '\n') {
                return -1;
            }
        }
        float fMax = 0.0f;
        for (int i2 = 0; i2 < lineCount; i2++) {
            fMax = Math.max(fMax, layout.getLineWidth(i2));
        }
        return (int) Math.ceil(fMax);
    }

    private int doKeyDown(int i, KeyEvent keyEvent, KeyEvent keyEvent2) {
        boolean z;
        boolean z2;
        if (!isEnabled()) {
            return 0;
        }
        if (keyEvent.getRepeatCount() == 0 && !KeyEvent.isModifierKey(i)) {
            this.mPreventDefaultMovement = false;
        }
        if (i != 4) {
            if (i != 23) {
                if (i != 61) {
                    if (i != 66) {
                        switch (i) {
                            case 277:
                                if (keyEvent.hasNoModifiers() && canCut() && onTextContextMenuItem(16908320)) {
                                    return -1;
                                }
                                break;
                            case 278:
                                if (keyEvent.hasNoModifiers() && canCopy() && onTextContextMenuItem(16908321)) {
                                    return -1;
                                }
                                break;
                            case 279:
                                if (keyEvent.hasNoModifiers() && canPaste() && onTextContextMenuItem(16908322)) {
                                    return -1;
                                }
                                break;
                        }
                    } else if (keyEvent.hasNoModifiers()) {
                        if (this.mEditor != null && this.mEditor.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, 0, keyEvent)) {
                            this.mEditor.mInputContentType.enterDown = true;
                            return -1;
                        }
                        if ((keyEvent.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) {
                            return hasOnClickListeners() ? 0 : -1;
                        }
                    }
                } else if ((keyEvent.hasNoModifiers() || keyEvent.hasModifiers(1)) && shouldAdvanceFocusOnTab()) {
                    return 0;
                }
            } else if (keyEvent.hasNoModifiers() && shouldAdvanceFocusOnEnter()) {
                return 0;
            }
        } else if (this.mEditor != null && this.mEditor.getTextActionMode() != null) {
            stopTextActionMode();
            return -1;
        }
        if (this.mEditor != null && this.mEditor.mKeyListener != null) {
            if (keyEvent2 != null) {
                try {
                    beginBatchEdit();
                    boolean zOnKeyOther = this.mEditor.mKeyListener.onKeyOther(this, (Editable) this.mText, keyEvent2);
                    hideErrorIfUnchanged();
                    if (zOnKeyOther) {
                        endBatchEdit();
                        return -1;
                    }
                    endBatchEdit();
                    z2 = false;
                } catch (AbstractMethodError unused) {
                    endBatchEdit();
                    z2 = true;
                } catch (Throwable th) {
                    endBatchEdit();
                    throw th;
                }
            } else {
                z2 = true;
            }
            if (z2) {
                beginBatchEdit();
                boolean zOnKeyDown = this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, i, keyEvent);
                endBatchEdit();
                hideErrorIfUnchanged();
                if (zOnKeyDown) {
                    return 1;
                }
            }
        }
        if (this.mMovement != null && this.mLayout != null) {
            if (keyEvent2 != null) {
                try {
                    if (this.mMovement.onKeyOther(this, this.mSpannable, keyEvent2)) {
                        return -1;
                    }
                    z = false;
                } catch (AbstractMethodError unused2) {
                    z = true;
                }
            } else {
                z = true;
            }
            if (z && this.mMovement.onKeyDown(this, this.mSpannable, i, keyEvent)) {
                if (keyEvent.getRepeatCount() != 0 || KeyEvent.isModifierKey(i)) {
                    return 2;
                }
                this.mPreventDefaultMovement = true;
                return 2;
            }
            if (keyEvent.getSource() == 257 && isDirectionalNavigationKey(i)) {
                return -1;
            }
        }
        return (!this.mPreventDefaultMovement || KeyEvent.isModifierKey(i)) ? 0 : -1;
    }

    private void ensureIterableTextForAccessibilitySelectable() {
        if (this.mText instanceof Spannable) {
            return;
        }
        setText(this.mText, BufferType.SPANNABLE);
    }

    private int findLargestTextSizeWhichFits(RectF rectF) {
        int length = this.mAutoSizeTextSizesInPx.length;
        if (length == 0) {
            throw new IllegalStateException("No available text sizes to choose from.");
        }
        int i = 0;
        int i2 = 1;
        int i3 = length - 1;
        while (true) {
            int i4 = i2;
            int i5 = i;
            i = i4;
            while (i <= i3) {
                int i6 = (i + i3) / 2;
                if (suggestedSizeFitsInSpace(this.mAutoSizeTextSizesInPx[i6], rectF)) {
                    i2 = i6 + 1;
                } else {
                    i5 = i6 - 1;
                    i3 = i5;
                }
            }
            return this.mAutoSizeTextSizesInPx[i5];
        }
    }

    private void fixFocusableAndClickableSettings() {
        if (this.mMovement == null && (this.mEditor == null || this.mEditor.mKeyListener == null)) {
            setFocusable(16);
            setClickable(false);
            setLongClickable(false);
        } else {
            setFocusable(1);
            setClickable(true);
            setLongClickable(true);
        }
    }

    private int getBottomVerticalOffset(boolean z) {
        int boxHeight;
        int height;
        int i = this.mGravity & 112;
        Layout layout = this.mLayout;
        if (!z && this.mText.length() == 0 && this.mHintLayout != null) {
            layout = this.mHintLayout;
        }
        if (i == 80 || (height = layout.getHeight()) >= (boxHeight = getBoxHeight(layout))) {
            return 0;
        }
        return i == 48 ? boxHeight - height : (boxHeight - height) >> 1;
    }

    private int getBoxHeight(Layout layout) {
        Insets opticalInsets = isLayoutModeOptical(this.mParent) ? getOpticalInsets() : Insets.NONE;
        return (getMeasuredHeight() - (layout == this.mHintLayout ? getCompoundPaddingTop() + getCompoundPaddingBottom() : getExtendedPaddingTop() + getExtendedPaddingBottom())) + opticalInsets.top + opticalInsets.bottom;
    }

    private Locale getCustomLocaleForKeyListenerOrNull() {
        LocaleList imeHintLocales;
        if (this.mUseInternationalizedInput && (imeHintLocales = getImeHintLocales()) != null) {
            return imeHintLocales.get(0);
        }
        return null;
    }

    private int getDesiredHeight() {
        return Math.max(getDesiredHeight(this.mLayout, true), getDesiredHeight(this.mHintLayout, this.mEllipsize != null));
    }

    private int getDesiredHeight(Layout layout, boolean z) {
        if (layout == null) {
            return 0;
        }
        int height = layout.getHeight(z);
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            height = Math.max(Math.max(height, drawables.mDrawableHeightLeft), drawables.mDrawableHeightRight);
        }
        int lineCount = layout.getLineCount();
        int compoundPaddingTop = getCompoundPaddingTop() + getCompoundPaddingBottom();
        int iMax = height + compoundPaddingTop;
        if (this.mMaxMode != 1) {
            iMax = Math.min(iMax, this.mMaximum);
        } else if (z && lineCount > this.mMaximum && ((layout instanceof DynamicLayout) || (layout instanceof BoringLayout))) {
            int lineTop = layout.getLineTop(this.mMaximum);
            if (drawables != null) {
                lineTop = Math.max(Math.max(lineTop, drawables.mDrawableHeightLeft), drawables.mDrawableHeightRight);
            }
            iMax = lineTop + compoundPaddingTop;
            lineCount = this.mMaximum;
        }
        if (this.mMinMode != 1) {
            iMax = Math.max(iMax, this.mMinimum);
        } else if (lineCount < this.mMinimum) {
            iMax += getLineHeight() * (this.mMinimum - lineCount);
        }
        return Math.max(iMax, getSuggestedMinimumHeight());
    }

    private float getHorizontalFadingEdgeStrength(float f, float f2) {
        int horizontalFadingEdgeLength = getHorizontalFadingEdgeLength();
        if (horizontalFadingEdgeLength == 0) {
            return 0.0f;
        }
        float fAbs = Math.abs(f - f2);
        float f3 = horizontalFadingEdgeLength;
        if (fAbs > f3) {
            return 1.0f;
        }
        return fAbs / f3;
    }

    private void getInterestingRect(Rect rect, int i) {
        convertFromViewportToContentCoordinates(rect);
        if (i == 0) {
            rect.top -= getExtendedPaddingTop();
        }
        if (i == this.mLayout.getLineCount() - 1) {
            rect.bottom += getExtendedPaddingBottom();
        }
    }

    private Layout.Alignment getLayoutAlignment() {
        switch (getTextAlignment()) {
            case 1:
                int i = this.mGravity & 8388615;
                if (i == 1) {
                    return Layout.Alignment.ALIGN_CENTER;
                }
                if (i == 3) {
                    return Layout.Alignment.ALIGN_LEFT;
                }
                if (i == 5) {
                    return Layout.Alignment.ALIGN_RIGHT;
                }
                if (i != 8388611 && i == 8388613) {
                    return Layout.Alignment.ALIGN_OPPOSITE;
                }
                return Layout.Alignment.ALIGN_NORMAL;
            case 2:
                return Layout.Alignment.ALIGN_NORMAL;
            case 3:
                return Layout.Alignment.ALIGN_OPPOSITE;
            case 4:
                return Layout.Alignment.ALIGN_CENTER;
            case 5:
                return getLayoutDirection() == 1 ? Layout.Alignment.ALIGN_RIGHT : Layout.Alignment.ALIGN_LEFT;
            case 6:
                return getLayoutDirection() == 1 ? Layout.Alignment.ALIGN_LEFT : Layout.Alignment.ALIGN_RIGHT;
            default:
                return Layout.Alignment.ALIGN_NORMAL;
        }
    }

    public static int getTextColor(Context context, TypedArray typedArray, int i) {
        ColorStateList textColors = getTextColors(context, typedArray);
        return textColors == null ? i : textColors.getDefaultColor();
    }

    public static ColorStateList getTextColors(Context context, TypedArray typedArray) {
        int resourceId;
        if (typedArray == null) {
            throw new NullPointerException();
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(android.R.styleable.TextView);
        ColorStateList colorStateList = typedArrayObtainStyledAttributes.getColorStateList(5);
        if (colorStateList == null && (resourceId = typedArrayObtainStyledAttributes.getResourceId(1, 0)) != 0) {
            TypedArray typedArrayObtainStyledAttributes2 = context.obtainStyledAttributes(resourceId, android.R.styleable.TextAppearance);
            colorStateList = typedArrayObtainStyledAttributes2.getColorStateList(3);
            typedArrayObtainStyledAttributes2.recycle();
        }
        typedArrayObtainStyledAttributes.recycle();
        return colorStateList;
    }

    private CharSequence getTextForAccessibility() {
        return TextUtils.isEmpty(this.mText) ? this.mHint : TextUtils.trimToParcelableSize(this.mTransformed);
    }

    private Locale getTextServicesLocale(boolean z) {
        updateTextServicesLocaleAsync();
        return (this.mCurrentSpellCheckerLocaleCache != null || z) ? this.mCurrentSpellCheckerLocaleCache : Locale.getDefault();
    }

    private Path getUpdatedHighlightPath() {
        Paint paint = this.mHighlightPaint;
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        if (this.mMovement != null && ((isFocused() || isPressed()) && selectionStart >= 0)) {
            if (selectionStart != selectionEnd) {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selectionStart, selectionEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                paint.setColor(this.mHighlightColor);
                paint.setStyle(Paint.Style.FILL);
                return this.mHighlightPath;
            }
            if (this.mEditor != null && this.mEditor.shouldRenderCursor()) {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getCursorPath(selectionStart, this.mHighlightPath, this.mText);
                    this.mEditor.updateCursorPosition();
                    this.mHighlightPathBogus = false;
                }
                paint.setColor(this.mCurTextColor);
                paint.setStyle(Paint.Style.STROKE);
                return this.mHighlightPath;
            }
        }
        return null;
    }

    private boolean hasSpannableText() {
        return this.mText != null && (this.mText instanceof Spannable);
    }

    private void invalidateCursor(int i, int i2, int i3) {
        if (i >= 0 || i2 >= 0 || i3 >= 0) {
            invalidateRegion(Math.min(Math.min(i, i2), i3), Math.max(Math.max(i, i2), i3), true);
        }
    }

    private boolean isAutoSizeEnabled() {
        return supportsAutoSizeText() && this.mAutoSizeTextType != 0;
    }

    private boolean isAutofillable() {
        return getAutofillType() != 0;
    }

    private boolean isDirectionalNavigationKey(int i) {
        switch (i) {
            case 19:
            case 20:
            case 21:
            case 22:
                return true;
            default:
                return false;
        }
    }

    private boolean isMarqueeFadeEnabled() {
        return this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 1;
    }

    private static boolean isMultilineInputType(int i) {
        return (i & 131087) == 131073;
    }

    static boolean isPasswordInputType(int i) {
        int i2 = i & FileObserver.ALL_EVENTS;
        return i2 == 129 || i2 == 225 || i2 == 18;
    }

    private boolean isShowingHint() {
        return TextUtils.isEmpty(this.mText) && !TextUtils.isEmpty(this.mHint);
    }

    private static boolean isVisiblePasswordInputType(int i) {
        return (i & FileObserver.ALL_EVENTS) == 145;
    }

    static /* synthetic */ void lambda$handleClick$1(TextClassification textClassification) {
        if (textClassification == null) {
            Log.d(LOG_TAG, "Timeout while classifying text");
            return;
        }
        if (textClassification.getActions().isEmpty()) {
            Log.d(LOG_TAG, "No link action to perform");
            return;
        }
        try {
            textClassification.getActions().get(0).getActionIntent().send();
        } catch (PendingIntent.CanceledException e) {
            Log.e(LOG_TAG, "Error sending PendingIntent", e);
        }
    }

    private void notifyAutoFillManagerAfterTextChangedIfNeeded() {
        AutofillManager autofillManager;
        if (isAutofillable() && (autofillManager = (AutofillManager) this.mContext.getSystemService(AutofillManager.class)) != null) {
            if (this.mLastValueSentToAutofillManager != null && this.mLastValueSentToAutofillManager.equals(this.mText)) {
                if (Helper.sVerbose) {
                    Log.v(LOG_TAG, "not notifying AFM on unchanged text");
                }
            } else {
                if (Helper.sVerbose) {
                    Log.v(LOG_TAG, "notifying AFM after text changed");
                }
                autofillManager.notifyValueChanged(this);
                this.mLastValueSentToAutofillManager = this.mText;
            }
        }
    }

    private void onProvideAutoStructureForAssistOrAutofill(ViewStructure viewStructure, boolean z) {
        int lineAtCoordinateUnclamped;
        int lineAtCoordinateUnclamped2;
        boolean z2 = hasPasswordTransformationMethod() || isPasswordInputType(getInputType());
        if (z) {
            viewStructure.setDataIsSensitive(!this.mTextSetFromXmlOrResourceId);
            if (this.mTextId != 0) {
                try {
                    viewStructure.setTextIdEntry(getResources().getResourceEntryName(this.mTextId));
                } catch (Resources.NotFoundException e) {
                    if (Helper.sVerbose) {
                        Log.v(LOG_TAG, "onProvideAutofillStructure(): cannot set name for text id " + this.mTextId + ": " + e.getMessage());
                    }
                }
            }
        }
        if (!z2 || z) {
            if (this.mLayout == null) {
                assumeLayout();
            }
            Layout layout = this.mLayout;
            int lineCount = layout.getLineCount();
            if (lineCount <= 1) {
                CharSequence text = getText();
                if (z) {
                    viewStructure.setText(text);
                } else {
                    viewStructure.setText(text, getSelectionStart(), getSelectionEnd());
                }
            } else {
                int[] iArr = new int[2];
                getLocationInWindow(iArr);
                int i = iArr[1];
                ViewParent parent = getParent();
                View view = this;
                while (parent instanceof View) {
                    view = (View) parent;
                    parent = view.getParent();
                }
                int height = view.getHeight();
                if (i >= 0) {
                    lineAtCoordinateUnclamped2 = getLineAtCoordinateUnclamped(0.0f);
                    lineAtCoordinateUnclamped = getLineAtCoordinateUnclamped(height - 1);
                } else {
                    int lineAtCoordinateUnclamped3 = getLineAtCoordinateUnclamped(-i);
                    lineAtCoordinateUnclamped = getLineAtCoordinateUnclamped((height - 1) - i);
                    lineAtCoordinateUnclamped2 = lineAtCoordinateUnclamped3;
                }
                int i2 = lineAtCoordinateUnclamped - lineAtCoordinateUnclamped2;
                int i3 = i2 / 2;
                int i4 = lineAtCoordinateUnclamped2 - i3;
                if (i4 < 0) {
                    i4 = 0;
                }
                int i5 = i3 + lineAtCoordinateUnclamped;
                if (i5 >= lineCount) {
                    i5 = lineCount - 1;
                }
                int lineStart = layout.getLineStart(i4);
                int lineEnd = layout.getLineEnd(i5);
                int selectionStart = getSelectionStart();
                int selectionEnd = getSelectionEnd();
                if (selectionStart < selectionEnd) {
                    if (selectionStart < lineStart) {
                        lineStart = selectionStart;
                    }
                    if (selectionEnd > lineEnd) {
                        lineEnd = selectionEnd;
                    }
                }
                CharSequence text2 = getText();
                if (lineStart > 0 || lineEnd < text2.length()) {
                    text2 = text2.subSequence(lineStart, lineEnd);
                }
                if (z) {
                    viewStructure.setText(text2);
                } else {
                    viewStructure.setText(text2, selectionStart - lineStart, selectionEnd - lineStart);
                    int i6 = i2 + 1;
                    int[] iArr2 = new int[i6];
                    int[] iArr3 = new int[i6];
                    int baselineOffset = getBaselineOffset();
                    for (int i7 = lineAtCoordinateUnclamped2; i7 <= lineAtCoordinateUnclamped; i7++) {
                        int i8 = i7 - lineAtCoordinateUnclamped2;
                        iArr2[i8] = layout.getLineStart(i7);
                        iArr3[i8] = layout.getLineBaseline(i7) + baselineOffset;
                    }
                    viewStructure.setTextLines(iArr2, iArr3);
                }
            }
            if (z) {
                viewStructure.setMinTextEms(getMinEms());
                viewStructure.setMaxTextEms(getMaxEms());
                int max = -1;
                for (InputFilter inputFilter : getFilters()) {
                    if (inputFilter instanceof InputFilter.LengthFilter) {
                        max = ((InputFilter.LengthFilter) inputFilter).getMax();
                        break;
                    }
                }
                viewStructure.setMaxTextLength(max);
            } else {
                int typefaceStyle = getTypefaceStyle();
                i = (typefaceStyle & 1) != 0 ? 1 : 0;
                if ((typefaceStyle & 2) != 0) {
                    i |= 2;
                }
                int flags = this.mTextPaint.getFlags();
                if ((flags & 32) != 0) {
                    i |= 1;
                }
                if ((flags & 8) != 0) {
                    i |= 4;
                }
                if ((flags & 16) != 0) {
                    i |= 8;
                }
                viewStructure.setTextStyle(getTextSize(), getCurrentTextColor(), 1, i);
            }
        }
        viewStructure.setHint(getHint());
        viewStructure.setInputType(getInputType());
    }

    private int[] parseDimensionArray(TypedArray typedArray) {
        if (typedArray == null) {
            return null;
        }
        int[] iArr = new int[typedArray.length()];
        for (int i = 0; i < iArr.length; i++) {
            iArr[i] = typedArray.getDimensionPixelSize(i, 0);
        }
        return iArr;
    }

    private void paste(int i, int i2, boolean z) {
        CharSequence charSequenceCoerceToText;
        ClipData primaryClip = ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        if (primaryClip != null) {
            boolean z2 = false;
            for (int i3 = 0; i3 < primaryClip.getItemCount(); i3++) {
                if (z) {
                    charSequenceCoerceToText = primaryClip.getItemAt(i3).coerceToStyledText(getContext());
                } else {
                    charSequenceCoerceToText = primaryClip.getItemAt(i3).coerceToText(getContext());
                    if (charSequenceCoerceToText instanceof Spanned) {
                        charSequenceCoerceToText = charSequenceCoerceToText.toString();
                    }
                }
                if (charSequenceCoerceToText != null) {
                    if (z2) {
                        ((Editable) this.mText).insert(getSelectionEnd(), "\n");
                        ((Editable) this.mText).insert(getSelectionEnd(), charSequenceCoerceToText);
                    } else {
                        Selection.setSelection(this.mSpannable, i2);
                        ((Editable) this.mText).replace(i, i2, charSequenceCoerceToText);
                        z2 = true;
                    }
                }
            }
            sLastCutCopyOrTextChangedTime = 0L;
        }
    }

    private boolean performAccessibilityActionClick(Bundle bundle) {
        boolean z;
        if (!isEnabled()) {
            return false;
        }
        if (isClickable() || isLongClickable()) {
            if (isFocusable() && !isFocused()) {
                requestFocus();
            }
            performClick();
            z = true;
        } else {
            z = false;
        }
        if ((this.mMovement == null && !onCheckIsTextEditor()) || !hasSpannableText() || this.mLayout == null) {
            return z;
        }
        if ((!isTextEditable() && !isTextSelectable()) || !isFocused()) {
            return z;
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        viewClicked(inputMethodManagerPeekInstance);
        return (isTextSelectable() || !this.mEditor.mShowSoftInputOnFocus || inputMethodManagerPeekInstance == null) ? z : z | inputMethodManagerPeekInstance.showSoftInput(this, 0);
    }

    public static void preloadFontCache() {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTypeface(Typeface.DEFAULT);
        paint.measureText("H");
    }

    private void prepareDrawableForDisplay(Drawable drawable) {
        if (drawable == null) {
            return;
        }
        drawable.setLayoutDirection(getLayoutDirection());
        if (drawable.isStateful()) {
            drawable.setState(getDrawableState());
            drawable.jumpToCurrentState();
        }
    }

    private void readTextAppearance(Context context, TypedArray typedArray, TextAppearanceAttributes textAppearanceAttributes, boolean z) {
        int i;
        int indexCount = typedArray.getIndexCount();
        for (int i2 = 0; i2 < indexCount; i2++) {
            int index = typedArray.getIndex(i2);
            if (z) {
                i = sAppearanceValues.get(index, -1);
                if (i == -1) {
                }
            } else {
                i = index;
            }
            switch (i) {
                case 0:
                    textAppearanceAttributes.mTextSize = typedArray.getDimensionPixelSize(index, textAppearanceAttributes.mTextSize);
                    break;
                case 1:
                    textAppearanceAttributes.mTypefaceIndex = typedArray.getInt(index, textAppearanceAttributes.mTypefaceIndex);
                    if (textAppearanceAttributes.mTypefaceIndex != -1 && !textAppearanceAttributes.mFontFamilyExplicit) {
                        textAppearanceAttributes.mFontFamily = null;
                    }
                    break;
                case 2:
                    textAppearanceAttributes.mStyleIndex = typedArray.getInt(index, textAppearanceAttributes.mStyleIndex);
                    break;
                case 3:
                    textAppearanceAttributes.mTextColor = typedArray.getColorStateList(index);
                    break;
                case 4:
                    textAppearanceAttributes.mTextColorHighlight = typedArray.getColor(index, textAppearanceAttributes.mTextColorHighlight);
                    break;
                case 5:
                    textAppearanceAttributes.mTextColorHint = typedArray.getColorStateList(index);
                    break;
                case 6:
                    textAppearanceAttributes.mTextColorLink = typedArray.getColorStateList(index);
                    break;
                case 7:
                    textAppearanceAttributes.mShadowColor = typedArray.getInt(index, textAppearanceAttributes.mShadowColor);
                    break;
                case 8:
                    textAppearanceAttributes.mShadowDx = typedArray.getFloat(index, textAppearanceAttributes.mShadowDx);
                    break;
                case 9:
                    textAppearanceAttributes.mShadowDy = typedArray.getFloat(index, textAppearanceAttributes.mShadowDy);
                    break;
                case 10:
                    textAppearanceAttributes.mShadowRadius = typedArray.getFloat(index, textAppearanceAttributes.mShadowRadius);
                    break;
                case 11:
                    textAppearanceAttributes.mAllCaps = typedArray.getBoolean(index, textAppearanceAttributes.mAllCaps);
                    break;
                case 12:
                    if (!context.isRestricted() && context.canLoadUnsafeResources()) {
                        try {
                            textAppearanceAttributes.mFontTypeface = typedArray.getFont(index);
                            break;
                        } catch (Resources.NotFoundException | UnsupportedOperationException unused) {
                        }
                    }
                    if (textAppearanceAttributes.mFontTypeface == null) {
                        textAppearanceAttributes.mFontFamily = typedArray.getString(index);
                    }
                    textAppearanceAttributes.mFontFamilyExplicit = true;
                    break;
                case 13:
                    textAppearanceAttributes.mHasElegant = true;
                    textAppearanceAttributes.mElegant = typedArray.getBoolean(index, textAppearanceAttributes.mElegant);
                    break;
                case 14:
                    textAppearanceAttributes.mHasLetterSpacing = true;
                    textAppearanceAttributes.mLetterSpacing = typedArray.getFloat(index, textAppearanceAttributes.mLetterSpacing);
                    break;
                case 15:
                    textAppearanceAttributes.mFontFeatureSettings = typedArray.getString(index);
                    break;
                case 16:
                    textAppearanceAttributes.mHasFallbackLineSpacing = true;
                    textAppearanceAttributes.mFallbackLineSpacing = typedArray.getBoolean(index, textAppearanceAttributes.mFallbackLineSpacing);
                    break;
                case 17:
                    textAppearanceAttributes.mFontWeight = typedArray.getInt(index, textAppearanceAttributes.mFontWeight);
                    break;
            }
        }
    }

    private void registerForPreDraw() {
        if (this.mPreDrawRegistered) {
            return;
        }
        getViewTreeObserver().addOnPreDrawListener(this);
        this.mPreDrawRegistered = true;
    }

    private <T> void removeIntersectingNonAdjacentSpans(int i, int i2, Class<T> cls) {
        if (this.mText instanceof Editable) {
            Editable editable = (Editable) this.mText;
            Object[] spans = editable.getSpans(i, i2, cls);
            int length = spans.length;
            for (int i3 = 0; i3 < length; i3++) {
                int spanStart = editable.getSpanStart(spans[i3]);
                if (editable.getSpanEnd(spans[i3]) == i || spanStart == i2) {
                    return;
                }
                editable.removeSpan(spans[i3]);
            }
        }
    }

    static void removeParcelableSpans(Spannable spannable, int i, int i2) {
        Object[] spans = spannable.getSpans(i, i2, ParcelableSpan.class);
        int length = spans.length;
        while (length > 0) {
            length--;
            spannable.removeSpan(spans[length]);
        }
    }

    private void requestAutofill() {
        AutofillManager autofillManager = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
        if (autofillManager != null) {
            autofillManager.requestAutofill(this);
        }
    }

    private void resolveStyleAndSetTypeface(Typeface typeface, int i, int i2) {
        if (i2 >= 0) {
            setTypeface(Typeface.create(typeface, Math.min(1000, i2), (i & 2) != 0));
        } else {
            setTypeface(typeface, i);
        }
    }

    private void restartMarqueeIfNeeded() {
        if (this.mRestartMarquee && this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            this.mRestartMarquee = false;
            startMarquee();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendBeforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> arrayList = this.mListeners;
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                arrayList.get(i4).beforeTextChanged(charSequence, i, i2, i3);
            }
        }
        int i5 = i2 + i;
        removeIntersectingNonAdjacentSpans(i, i5, SpellCheckSpan.class);
        removeIntersectingNonAdjacentSpans(i, i5, SuggestionSpan.class);
    }

    private void setFilters(Editable editable, InputFilter[] inputFilterArr) {
        if (this.mEditor != null) {
            int i = 1;
            boolean z = this.mEditor.mUndoInputFilter != null;
            boolean z2 = this.mEditor.mKeyListener instanceof InputFilter;
            int i2 = z ? 1 : 0;
            if (z2) {
                i2++;
            }
            if (i2 > 0) {
                InputFilter[] inputFilterArr2 = new InputFilter[inputFilterArr.length + i2];
                System.arraycopy(inputFilterArr, 0, inputFilterArr2, 0, inputFilterArr.length);
                if (z) {
                    inputFilterArr2[inputFilterArr.length] = this.mEditor.mUndoInputFilter;
                } else {
                    i = 0;
                }
                if (z2) {
                    inputFilterArr2[inputFilterArr.length + i] = (InputFilter) this.mEditor.mKeyListener;
                }
                editable.setFilters(inputFilterArr2);
                return;
            }
        }
        editable.setFilters(inputFilterArr);
    }

    private void setHintInternal(CharSequence charSequence) {
        this.mHint = TextUtils.stringOrSpannedString(charSequence);
        if (this.mLayout != null) {
            checkForRelayout();
        }
        if (this.mText.length() == 0) {
            invalidate();
        }
        if (this.mEditor == null || this.mText.length() != 0 || this.mHint == null) {
            return;
        }
        this.mEditor.invalidateTextDisplayList();
    }

    private void setInputType(int i, boolean z) {
        KeyListener dialerKeyListener;
        TextKeyListener.Capitalize capitalize;
        int i2 = i & 15;
        if (i2 == 1) {
            boolean z2 = (32768 & i) != 0;
            if ((i & 4096) != 0) {
                capitalize = TextKeyListener.Capitalize.CHARACTERS;
            } else if ((i & 8192) != 0) {
                capitalize = TextKeyListener.Capitalize.WORDS;
            } else {
                capitalize = (i & 16384) != 0 ? TextKeyListener.Capitalize.SENTENCES : TextKeyListener.Capitalize.NONE;
            }
            dialerKeyListener = TextKeyListener.getInstance(z2, capitalize);
        } else if (i2 == 2) {
            Locale customLocaleForKeyListenerOrNull = getCustomLocaleForKeyListenerOrNull();
            DigitsKeyListener digitsKeyListener = DigitsKeyListener.getInstance(customLocaleForKeyListenerOrNull, (i & 4096) != 0, (i & 8192) != 0);
            if (customLocaleForKeyListenerOrNull != null) {
                int inputType = digitsKeyListener.getInputType();
                if ((inputType & 15) != 2) {
                    if ((i & 16) != 0) {
                        inputType |= 128;
                    }
                    i = inputType;
                }
            }
            dialerKeyListener = digitsKeyListener;
        } else if (i2 == 4) {
            Locale customLocaleForKeyListenerOrNull2 = getCustomLocaleForKeyListenerOrNull();
            int i3 = i & InputType.TYPE_MASK_VARIATION;
            if (i3 != 16) {
                dialerKeyListener = i3 != 32 ? DateTimeKeyListener.getInstance(customLocaleForKeyListenerOrNull2) : TimeKeyListener.getInstance(customLocaleForKeyListenerOrNull2);
            } else {
                dialerKeyListener = DateKeyListener.getInstance(customLocaleForKeyListenerOrNull2);
            }
            if (this.mUseInternationalizedInput) {
                i = dialerKeyListener.getInputType();
            }
        } else {
            dialerKeyListener = i2 == 3 ? DialerKeyListener.getInstance() : TextKeyListener.getInstance();
        }
        setRawInputType(i);
        this.mListenerChanged = false;
        if (!z) {
            setKeyListenerOnly(dialerKeyListener);
        } else {
            createEditorIfNeeded();
            this.mEditor.mKeyListener = dialerKeyListener;
        }
    }

    private void setInputTypeFromEditor() {
        try {
            this.mEditor.mInputType = this.mEditor.mKeyListener.getInputType();
        } catch (IncompatibleClassChangeError unused) {
            this.mEditor.mInputType = 1;
        }
        setInputTypeSingleLine(this.mSingleLine);
    }

    private void setInputTypeSingleLine(boolean z) {
        if (this.mEditor == null || (this.mEditor.mInputType & 15) != 1) {
            return;
        }
        if (z) {
            this.mEditor.mInputType &= -131073;
        } else {
            this.mEditor.mInputType |= 131072;
        }
    }

    private void setKeyListenerOnly(KeyListener keyListener) {
        if (this.mEditor == null && keyListener == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mKeyListener != keyListener) {
            this.mEditor.mKeyListener = keyListener;
            if (keyListener != null && !(this.mText instanceof Editable)) {
                setText(this.mText);
            }
            setFilters((Editable) this.mText, this.mFilters);
        }
    }

    private boolean setPrimaryClip(ClipData clipData) {
        try {
            ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).setPrimaryClip(clipData);
            sLastCutCopyOrTextChangedTime = SystemClock.uptimeMillis();
            return true;
        } catch (Throwable unused) {
            return false;
        }
    }

    private void setRawTextSize(float f, boolean z) {
        if (f != this.mTextPaint.getTextSize()) {
            this.mTextPaint.setTextSize(f);
            if (!z || this.mLayout == null) {
                return;
            }
            this.mNeedsAutoSizeText = false;
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    private void setRelativeDrawablesIfNeeded(Drawable drawable, Drawable drawable2) {
        if ((drawable == null && drawable2 == null) ? false : true) {
            Drawables drawables = this.mDrawables;
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = true;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableStart = drawable;
                drawables.mDrawableSizeStart = rect.width();
                drawables.mDrawableHeightStart = rect.height();
            } else {
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
            }
            if (drawable2 != null) {
                drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableEnd = drawable2;
                drawables.mDrawableSizeEnd = rect.width();
                drawables.mDrawableHeightEnd = rect.height();
            } else {
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
            }
            resetResolvedDrawables();
            resolveDrawables();
            applyCompoundDrawableTint();
        }
    }

    /* JADX WARN: Code duplicated, block: B:88:0x0165  */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    private void setText(CharSequence charSequence, BufferType bufferType, boolean z, int i) {
        CharSequence charSequenceNewSpannable;
        CharSequence charSequence2;
        Spannable spannableNewSpannable;
        this.mTextSetFromXmlOrResourceId = false;
        if (charSequence == null) {
            charSequence = "";
        }
        if (!isSuggestionsEnabled()) {
            charSequence = removeSuggestionSpans(charSequence);
        }
        if (!this.mUserSetTextScaleX) {
            this.mTextPaint.setTextScaleX(1.0f);
        }
        if ((charSequence instanceof Spanned) && ((Spanned) charSequence).getSpanStart(TextUtils.TruncateAt.MARQUEE) >= 0) {
            if (ViewConfiguration.get(this.mContext).isFadingMarqueeEnabled()) {
                setHorizontalFadingEdgeEnabled(true);
                this.mMarqueeFadeMode = 0;
            } else {
                setHorizontalFadingEdgeEnabled(false);
                this.mMarqueeFadeMode = 1;
            }
            setEllipsize(TextUtils.TruncateAt.MARQUEE);
        }
        int length = this.mFilters.length;
        CharSequence charSequence3 = charSequence;
        int i2 = 0;
        while (i2 < length) {
            CharSequence charSequenceFilter = this.mFilters[i2].filter(charSequence3, 0, charSequence3.length(), EMPTY_SPANNED, 0, 0);
            if (charSequenceFilter != null) {
                charSequence3 = charSequenceFilter;
            }
            i2++;
            charSequence3 = charSequence3;
        }
        if (z) {
            if (this.mText != null) {
                i = this.mText.length();
                sendBeforeTextChanged(this.mText, 0, i, charSequence3.length());
            } else {
                sendBeforeTextChanged("", 0, 0, charSequence3.length());
            }
        }
        boolean z2 = (this.mListeners == null || this.mListeners.size() == 0) ? false : true;
        PrecomputedText precomputedText = charSequence3 instanceof PrecomputedText ? (PrecomputedText) charSequence3 : null;
        if (bufferType == BufferType.EDITABLE || getKeyListener() != null || z2) {
            createEditorIfNeeded();
            this.mEditor.forgetUndoRedo();
            Editable editableNewEditable = this.mEditableFactory.newEditable(charSequence3);
            setFilters(editableNewEditable, this.mFilters);
            InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
            charSequenceNewSpannable = editableNewEditable;
            if (inputMethodManagerPeekInstance != null) {
                inputMethodManagerPeekInstance.restartInput(this);
                charSequenceNewSpannable = editableNewEditable;
            }
        } else if (precomputedText != null) {
            if (this.mTextDir == null) {
                this.mTextDir = getTextDirectionHeuristic();
            }
            charSequenceNewSpannable = charSequence3;
            if (!precomputedText.getParams().isSameTextMetricsInternal(getPaint(), this.mTextDir, this.mBreakStrategy, this.mHyphenationFrequency)) {
                throw new IllegalArgumentException("PrecomputedText's Parameters don't match the parameters of this TextView.Consider using setTextMetricsParams(precomputedText.getParams()) to override the settings of this TextView: PrecomputedText: " + precomputedText.getParams() + "TextView: " + getTextMetricsParams());
            }
        } else if (bufferType == BufferType.SPANNABLE || this.mMovement != null) {
            charSequenceNewSpannable = this.mSpannableFactory.newSpannable(charSequence3);
        } else if (!(charSequence3 instanceof CharWrapper)) {
            charSequenceNewSpannable = charSequence3;
            charSequenceNewSpannable = TextUtils.stringOrSpannedString(charSequence3);
        }
        charSequenceNewSpannable = charSequence3;
        if (this.mAutoLinkMask == 0) {
            charSequence2 = charSequenceNewSpannable;
        } else {
            spannableNewSpannable = (bufferType == BufferType.EDITABLE || (charSequenceNewSpannable instanceof Spannable)) ? (Spannable) charSequenceNewSpannable : this.mSpannableFactory.newSpannable(charSequenceNewSpannable);
            if (Linkify.addLinks(spannableNewSpannable, this.mAutoLinkMask)) {
                bufferType = bufferType == BufferType.EDITABLE ? BufferType.EDITABLE : BufferType.SPANNABLE;
                setTextInternal(spannableNewSpannable);
                charSequence2 = spannableNewSpannable;
                if (this.mLinksClickable && !textCanBeSelected()) {
                    charSequence2 = spannableNewSpannable;
                    setMovementMethod(LinkMovementMethod.getInstance());
                    charSequence2 = spannableNewSpannable;
                }
            } else {
                charSequence2 = charSequenceNewSpannable;
            }
        }
        charSequence2 = spannableNewSpannable;
        this.mBufferType = bufferType;
        setTextInternal(charSequence2);
        if (this.mTransformation == null) {
            this.mTransformed = charSequence2;
        } else {
            this.mTransformed = this.mTransformation.getTransformation(charSequence2, this);
        }
        int length2 = charSequence2.length();
        if ((charSequence2 instanceof Spannable) && !this.mAllowTransformationLengthChange) {
            Spannable spannable = (Spannable) charSequence2;
            for (ChangeWatcher changeWatcher : (ChangeWatcher[]) spannable.getSpans(0, spannable.length(), ChangeWatcher.class)) {
                spannable.removeSpan(changeWatcher);
            }
            if (this.mChangeWatcher == null) {
                this.mChangeWatcher = new ChangeWatcher();
            }
            spannable.setSpan(this.mChangeWatcher, 0, length2, 6553618);
            if (this.mEditor != null) {
                this.mEditor.addSpanWatchers(spannable);
            }
            if (this.mTransformation != null) {
                spannable.setSpan(this.mTransformation, 0, length2, 18);
            }
            if (this.mMovement != null) {
                this.mMovement.initialize(this, spannable);
                if (this.mEditor != null) {
                    this.mEditor.mSelectionMoved = false;
                }
            }
        }
        if (this.mLayout != null) {
            checkForRelayout();
        }
        sendOnTextChanged(charSequence2, 0, i, length2);
        onTextChanged(charSequence2, 0, i, length2);
        notifyViewAccessibilityStateChangedIfNeeded(2);
        if (z2) {
            sendAfterTextChanged((Editable) charSequence2);
        } else {
            notifyAutoFillManagerAfterTextChangedIfNeeded();
        }
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    private void setTextInternal(CharSequence charSequence) {
        this.mText = charSequence;
        this.mSpannable = charSequence instanceof Spannable ? (Spannable) charSequence : null;
        this.mPrecomputed = charSequence instanceof PrecomputedText ? (PrecomputedText) charSequence : null;
    }

    private void setTextSizeInternal(int i, float f, boolean z) {
        Context context = getContext();
        setRawTextSize(TypedValue.applyDimension(i, f, (context == null ? Resources.getSystem() : context.getResources()).getDisplayMetrics()), z);
    }

    private void setTypefaceFromAttrs(Typeface typeface, String str, int i, int i2, int i3) {
        if (typeface == null && str != null) {
            resolveStyleAndSetTypeface(Typeface.create(str, 0), i2, i3);
            return;
        }
        if (typeface != null) {
            resolveStyleAndSetTypeface(typeface, i2, i3);
            return;
        }
        switch (i) {
            case 1:
                resolveStyleAndSetTypeface(Typeface.SANS_SERIF, i2, i3);
                break;
            case 2:
                resolveStyleAndSetTypeface(Typeface.SERIF, i2, i3);
                break;
            case 3:
                resolveStyleAndSetTypeface(Typeface.MONOSPACE, i2, i3);
                break;
            default:
                resolveStyleAndSetTypeface(null, i2, i3);
                break;
        }
    }

    private boolean setupAutoSizeText() {
        if (supportsAutoSizeText() && this.mAutoSizeTextType == 1) {
            if (!this.mHasPresetAutoSizeValues || this.mAutoSizeTextSizesInPx.length == 0) {
                int iFloor = ((int) Math.floor((this.mAutoSizeMaxTextSizeInPx - this.mAutoSizeMinTextSizeInPx) / this.mAutoSizeStepGranularityInPx)) + 1;
                int[] iArr = new int[iFloor];
                for (int i = 0; i < iFloor; i++) {
                    iArr[i] = Math.round(this.mAutoSizeMinTextSizeInPx + (i * this.mAutoSizeStepGranularityInPx));
                }
                this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(iArr);
            }
            this.mNeedsAutoSizeText = true;
        } else {
            this.mNeedsAutoSizeText = false;
        }
        return this.mNeedsAutoSizeText;
    }

    private void setupAutoSizeUniformPresetSizes(TypedArray typedArray) {
        int length = typedArray.length();
        int[] iArr = new int[length];
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                iArr[i] = typedArray.getDimensionPixelSize(i, -1);
            }
            this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(iArr);
            setupAutoSizeUniformPresetSizesConfiguration();
        }
    }

    private boolean setupAutoSizeUniformPresetSizesConfiguration() {
        int length = this.mAutoSizeTextSizesInPx.length;
        this.mHasPresetAutoSizeValues = length > 0;
        if (this.mHasPresetAutoSizeValues) {
            this.mAutoSizeTextType = 1;
            this.mAutoSizeMinTextSizeInPx = this.mAutoSizeTextSizesInPx[0];
            this.mAutoSizeMaxTextSizeInPx = this.mAutoSizeTextSizesInPx[length - 1];
            this.mAutoSizeStepGranularityInPx = -1.0f;
        }
        return this.mHasPresetAutoSizeValues;
    }

    private void shareSelectedText() {
        String selectedText = getSelectedText();
        if (selectedText == null || selectedText.isEmpty()) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        intent.removeExtra(Intent.EXTRA_TEXT);
        intent.putExtra(Intent.EXTRA_TEXT, (String) TextUtils.trimToParcelableSize(selectedText));
        getContext().startActivity(Intent.createChooser(intent, null));
        Selection.setSelection(this.mSpannable, getSelectionEnd());
    }

    private boolean shouldAdvanceFocusOnEnter() {
        int i;
        if (getKeyListener() == null) {
            return false;
        }
        if (this.mSingleLine) {
            return true;
        }
        return this.mEditor != null && (this.mEditor.mInputType & 15) == 1 && ((i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION) == 32 || i == 48);
    }

    private boolean shouldAdvanceFocusOnTab() {
        int i;
        return getKeyListener() == null || this.mSingleLine || this.mEditor == null || (this.mEditor.mInputType & 15) != 1 || !((i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION) == 262144 || i == 131072);
    }

    private void startMarquee() {
        if (getKeyListener() == null && !compressText((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight())) {
            if (this.mMarquee == null || this.mMarquee.isStopped()) {
                if ((isFocused() || isSelected()) && getLineCount() == 1 && canMarquee()) {
                    if (this.mMarqueeFadeMode == 1) {
                        this.mMarqueeFadeMode = 2;
                        Layout layout = this.mLayout;
                        this.mLayout = this.mSavedMarqueeModeLayout;
                        this.mSavedMarqueeModeLayout = layout;
                        setHorizontalFadingEdgeEnabled(true);
                        requestLayout();
                        invalidate();
                    }
                    if (this.mMarquee == null) {
                        this.mMarquee = new Marquee(this);
                    }
                    this.mMarquee.start(this.mMarqueeRepeatLimit);
                }
            }
        }
    }

    private void startStopMarquee(boolean z) {
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE) {
            if (z) {
                startMarquee();
            } else {
                stopMarquee();
            }
        }
    }

    private void stopMarquee() {
        if (this.mMarquee != null && !this.mMarquee.isStopped()) {
            this.mMarquee.stop();
        }
        if (this.mMarqueeFadeMode == 2) {
            this.mMarqueeFadeMode = 1;
            Layout layout = this.mSavedMarqueeModeLayout;
            this.mSavedMarqueeModeLayout = this.mLayout;
            this.mLayout = layout;
            setHorizontalFadingEdgeEnabled(false);
            requestLayout();
            invalidate();
        }
    }

    private boolean suggestedSizeFitsInSpace(int i, RectF rectF) {
        CharSequence text = this.mTransformed != null ? this.mTransformed : getText();
        int maxLines = getMaxLines();
        if (this.mTempTextPaint == null) {
            this.mTempTextPaint = new TextPaint();
        } else {
            this.mTempTextPaint.reset();
        }
        this.mTempTextPaint.set(getPaint());
        this.mTempTextPaint.setTextSize(i);
        StaticLayout.Builder builderObtain = StaticLayout.Builder.obtain(text, 0, text.length(), this.mTempTextPaint, Math.round(rectF.right));
        builderObtain.setAlignment(getLayoutAlignment()).setLineSpacing(getLineSpacingExtra(), getLineSpacingMultiplier()).setIncludePad(getIncludeFontPadding()).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).setBreakStrategy(getBreakStrategy()).setHyphenationFrequency(getHyphenationFrequency()).setJustificationMode(getJustificationMode()).setMaxLines(this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE).setTextDirection(getTextDirectionHeuristic());
        StaticLayout staticLayoutBuild = builderObtain.build();
        return (maxLines == -1 || staticLayoutBuild.getLineCount() <= maxLines) && ((float) staticLayoutBuild.getHeight()) <= rectF.bottom;
    }

    private void unregisterForPreDraw() {
        getViewTreeObserver().removeOnPreDrawListener(this);
        this.mPreDrawRegistered = false;
        this.mPreDrawListenerDetached = false;
    }

    private void updateTextColors() {
        boolean z;
        int colorForState;
        int colorForState2;
        int[] drawableState = getDrawableState();
        int colorForState3 = this.mTextColor.getColorForState(drawableState, 0);
        if (colorForState3 != this.mCurTextColor) {
            this.mCurTextColor = colorForState3;
            z = true;
        } else {
            z = false;
        }
        if (this.mLinkTextColor != null && (colorForState2 = this.mLinkTextColor.getColorForState(drawableState, 0)) != this.mTextPaint.linkColor) {
            this.mTextPaint.linkColor = colorForState2;
            z = true;
        }
        if (this.mHintTextColor != null && (colorForState = this.mHintTextColor.getColorForState(drawableState, 0)) != this.mCurHintTextColor) {
            this.mCurHintTextColor = colorForState;
            if (this.mText.length() == 0) {
                z = true;
            }
        }
        if (z) {
            if (this.mEditor != null) {
                this.mEditor.invalidateTextDisplayList();
            }
            invalidate();
        }
    }

    private void updateTextServicesLocaleAsync() {
        AsyncTask.execute(new Runnable() { // from class: android.widget.TextView.3
            @Override // java.lang.Runnable
            public void run() {
                TextView.this.updateTextServicesLocaleLocked();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateTextServicesLocaleLocked() {
        SpellCheckerSubtype currentSpellCheckerSubtype = ((TextServicesManager) this.mContext.getSystemService(Context.TEXT_SERVICES_MANAGER_SERVICE)).getCurrentSpellCheckerSubtype(true);
        this.mCurrentSpellCheckerLocaleCache = currentSpellCheckerSubtype != null ? currentSpellCheckerSubtype.getLocaleObject() : null;
    }

    private void validateAndSetAutoSizeTextTypeUniformConfiguration(float f, float f2, float f3) {
        if (f <= 0.0f) {
            throw new IllegalArgumentException("Minimum auto-size text size (" + f + "px) is less or equal to (0px)");
        }
        if (f2 <= f) {
            throw new IllegalArgumentException("Maximum auto-size text size (" + f2 + "px) is less or equal to minimum auto-size text size (" + f + "px)");
        }
        if (f3 <= 0.0f) {
            throw new IllegalArgumentException("The auto-size step granularity (" + f3 + "px) is less or equal to (0px)");
        }
        this.mAutoSizeTextType = 1;
        this.mAutoSizeMinTextSizeInPx = f;
        this.mAutoSizeMaxTextSizeInPx = f2;
        this.mAutoSizeStepGranularityInPx = f3;
        this.mHasPresetAutoSizeValues = false;
    }

    @Override // android.view.View
    public void addExtraDataToAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo, String str, Bundle bundle) {
        RectF characterBounds;
        if (bundle != null && str.equals(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY)) {
            int i = bundle.getInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_START_INDEX, -1);
            int i2 = bundle.getInt(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_ARG_LENGTH, -1);
            if (i2 <= 0 || i < 0 || i >= this.mText.length()) {
                Log.e(LOG_TAG, "Invalid arguments for accessibility character locations");
                return;
            }
            RectF[] rectFArr = new RectF[i2];
            CursorAnchorInfo.Builder builder = new CursorAnchorInfo.Builder();
            populateCharacterBounds(builder, i, i + i2, viewportToContentHorizontalOffset(), viewportToContentVerticalOffset());
            CursorAnchorInfo cursorAnchorInfoBuild = builder.setMatrix(null).build();
            for (int i3 = 0; i3 < i2; i3++) {
                int i4 = i + i3;
                if ((cursorAnchorInfoBuild.getCharacterBoundsFlags(i4) & 1) == 1 && (characterBounds = cursorAnchorInfoBuild.getCharacterBounds(i4)) != null) {
                    mapRectFromViewToScreenCoords(characterBounds, true);
                    rectFArr[i3] = characterBounds;
                }
            }
            accessibilityNodeInfo.getExtras().putParcelableArray(str, rectFArr);
        }
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(textWatcher);
    }

    public final void append(CharSequence charSequence) {
        append(charSequence, 0, charSequence.length());
    }

    public void append(CharSequence charSequence, int i, int i2) {
        if (!(this.mText instanceof Editable)) {
            setText(this.mText, BufferType.EDITABLE);
        }
        ((Editable) this.mText).append(charSequence, i, i2);
        if (this.mAutoLinkMask == 0 || !Linkify.addLinks(this.mSpannable, this.mAutoLinkMask) || !this.mLinksClickable || textCanBeSelected()) {
            return;
        }
        setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override // android.view.View
    public void autofill(AutofillValue autofillValue) {
        if (autofillValue.isText() && isTextEditable()) {
            setText(autofillValue.getTextValue(), this.mBufferType, true, 0);
            CharSequence text = getText();
            if (text instanceof Spannable) {
                Selection.setSelection((Spannable) text, text.length());
                return;
            }
            return;
        }
        Log.w(LOG_TAG, autofillValue + " could not be autofilled into " + this);
    }

    public void beginBatchEdit() {
        if (this.mEditor != null) {
            this.mEditor.beginBatchEdit();
        }
    }

    public boolean bringPointIntoView(int i) {
        int paragraphDirection;
        boolean z;
        if (isLayoutRequested()) {
            this.mDeferScroll = i;
            return false;
        }
        Layout layout = isShowingHint() ? this.mHintLayout : this.mLayout;
        if (layout == null) {
            return false;
        }
        int lineForOffset = layout.getLineForOffset(i);
        switch (layout.getParagraphAlignment(lineForOffset)) {
            case ALIGN_LEFT:
                paragraphDirection = 1;
                break;
            case ALIGN_RIGHT:
                paragraphDirection = -1;
                break;
            case ALIGN_NORMAL:
                paragraphDirection = layout.getParagraphDirection(lineForOffset);
                break;
            case ALIGN_OPPOSITE:
                paragraphDirection = -layout.getParagraphDirection(lineForOffset);
                break;
            default:
                paragraphDirection = 0;
                break;
        }
        int primaryHorizontal = (int) layout.getPrimaryHorizontal(i, paragraphDirection > 0);
        int lineTop = layout.getLineTop(lineForOffset);
        int lineTop2 = layout.getLineTop(lineForOffset + 1);
        int iFloor = (int) Math.floor(layout.getLineLeft(lineForOffset));
        int iCeil = (int) Math.ceil(layout.getLineRight(lineForOffset));
        int height = layout.getHeight();
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int extendedPaddingTop = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        if (!this.mHorizontallyScrolling && iCeil - iFloor > compoundPaddingLeft && iCeil > primaryHorizontal) {
            iCeil = Math.max(primaryHorizontal, iFloor + compoundPaddingLeft);
        }
        int i2 = (lineTop2 - lineTop) / 2;
        int i3 = extendedPaddingTop / 4;
        if (i2 <= i3) {
            i3 = i2;
        }
        int i4 = compoundPaddingLeft / 4;
        if (i2 <= i4) {
            i4 = i2;
        }
        int i5 = this.mScrollX;
        int i6 = this.mScrollY;
        if (lineTop - i6 < i3) {
            i6 = lineTop - i3;
        }
        int i7 = extendedPaddingTop - i3;
        if (lineTop2 - i6 > i7) {
            i6 = lineTop2 - i7;
        }
        int i8 = height - i6 < extendedPaddingTop ? height - extendedPaddingTop : i6;
        if (0 - i8 > 0) {
            i8 = 0;
        }
        if (paragraphDirection != 0) {
            if (primaryHorizontal - i5 < i4) {
                i5 = primaryHorizontal - i4;
            }
            int i9 = compoundPaddingLeft - i4;
            if (primaryHorizontal - i5 > i9) {
                i5 = primaryHorizontal - i9;
            }
        }
        if (paragraphDirection < 0) {
            if (iFloor - i5 <= 0) {
                iFloor = i5;
            }
            if (iCeil - iFloor < compoundPaddingLeft) {
                iFloor = iCeil - compoundPaddingLeft;
            }
        } else if (paragraphDirection > 0) {
            int i10 = iCeil - i5 < compoundPaddingLeft ? iCeil - compoundPaddingLeft : i5;
            if (iFloor - i10 <= 0) {
                iFloor = i10;
            }
        } else {
            int i11 = iCeil - iFloor;
            if (i11 <= compoundPaddingLeft) {
                iFloor -= (compoundPaddingLeft - i11) / 2;
            } else if (primaryHorizontal > iCeil - i4) {
                iFloor = iCeil - compoundPaddingLeft;
            } else if (primaryHorizontal >= iFloor + i4 && iFloor <= i5) {
                if (iCeil < i5 + compoundPaddingLeft) {
                    iFloor = iCeil - compoundPaddingLeft;
                } else {
                    iFloor = primaryHorizontal - i5 < i4 ? primaryHorizontal - i4 : i5;
                    int i12 = compoundPaddingLeft - i4;
                    if (primaryHorizontal - iFloor > i12) {
                        iFloor = primaryHorizontal - i12;
                    }
                }
            }
        }
        if (iFloor == this.mScrollX && i8 == this.mScrollY) {
            z = false;
        } else {
            if (this.mScroller == null) {
                scrollTo(iFloor, i8);
            } else {
                long jCurrentAnimationTimeMillis = AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll;
                int i13 = iFloor - this.mScrollX;
                int i14 = i8 - this.mScrollY;
                if (jCurrentAnimationTimeMillis > 250) {
                    this.mScroller.startScroll(this.mScrollX, this.mScrollY, i13, i14);
                    awakenScrollBars(this.mScroller.getDuration());
                    invalidate();
                } else {
                    if (!this.mScroller.isFinished()) {
                        this.mScroller.abortAnimation();
                    }
                    scrollBy(i13, i14);
                }
                this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
            }
            z = true;
        }
        if (isFocused()) {
            if (this.mTempRect == null) {
                this.mTempRect = new Rect();
            }
            this.mTempRect.set(primaryHorizontal - 2, lineTop, primaryHorizontal + 2, lineTop2);
            getInterestingRect(this.mTempRect, lineForOffset);
            this.mTempRect.offset(this.mScrollX, this.mScrollY);
            if (requestRectangleOnScreen(this.mTempRect)) {
                return true;
            }
        }
        return z;
    }

    boolean canCopy() {
        return (!this.mIsRestrictedAcrossUser || UserHandle.myUserId() == ActivityManager.getCurrentUser()) && !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && this.mEditor != null;
    }

    boolean canCut() {
        return (!this.mIsRestrictedAcrossUser || UserHandle.myUserId() == ActivityManager.getCurrentUser()) && !hasPasswordTransformationMethod() && this.mText.length() > 0 && hasSelection() && (this.mText instanceof Editable) && this.mEditor != null && this.mEditor.mKeyListener != null;
    }

    boolean canPaste() {
        return (!this.mIsRestrictedAcrossUser || UserHandle.myUserId() == ActivityManager.getCurrentUser()) && (this.mText instanceof Editable) && this.mEditor != null && this.mEditor.mKeyListener != null && getSelectionStart() >= 0 && getSelectionEnd() >= 0 && ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).hasPrimaryClip();
    }

    boolean canPasteAsPlainText() {
        if (!canPaste()) {
            return false;
        }
        ClipData primaryClip = ((ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE)).getPrimaryClip();
        ClipDescription description = primaryClip.getDescription();
        boolean zHasMimeType = description.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);
        CharSequence text = primaryClip.getItemAt(0).getText();
        if (zHasMimeType && (text instanceof Spanned) && TextUtils.hasStyleSpan((Spanned) text)) {
            return true;
        }
        return description.hasMimeType(ClipDescription.MIMETYPE_TEXT_HTML);
    }

    boolean canProcessText() {
        if (getId() == -1) {
            return false;
        }
        return canShare();
    }

    boolean canRedo() {
        return this.mEditor != null && this.mEditor.canRedo();
    }

    boolean canRequestAutofill() {
        AutofillManager autofillManager;
        if (isAutofillable() && (autofillManager = (AutofillManager) this.mContext.getSystemService(AutofillManager.class)) != null) {
            return autofillManager.isEnabled();
        }
        return false;
    }

    boolean canSelectAllText() {
        return (!canSelectText() || hasPasswordTransformationMethod() || (getSelectionStart() == 0 && getSelectionEnd() == this.mText.length())) ? false : true;
    }

    boolean canSelectText() {
        return (this.mText.length() == 0 || this.mEditor == null || !this.mEditor.hasSelectionController()) ? false : true;
    }

    boolean canShare() {
        if (getContext().canStartActivityForResult() && isDeviceProvisioned()) {
            return canCopy();
        }
        return false;
    }

    boolean canUndo() {
        return this.mEditor != null && this.mEditor.canUndo();
    }

    @Override // android.view.View
    public void cancelLongPress() {
        super.cancelLongPress();
        if (this.mEditor != null) {
            this.mEditor.mIgnoreActionUpEvent = true;
        }
    }

    public void clearComposingText() {
        if (this.mText instanceof Spannable) {
            BaseInputConnection.removeComposingSpans(this.mSpannable);
        }
    }

    @Override // android.view.View
    protected int computeHorizontalScrollRange() {
        if (this.mLayout != null) {
            return (this.mSingleLine && (this.mGravity & 7) == 3) ? (int) this.mLayout.getLineWidth(0) : this.mLayout.getWidth();
        }
        return super.computeHorizontalScrollRange();
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mScroller == null || !this.mScroller.computeScrollOffset()) {
            return;
        }
        this.mScrollX = this.mScroller.getCurrX();
        this.mScrollY = this.mScroller.getCurrY();
        invalidateParentCaches();
        postInvalidate();
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return (getHeight() - getCompoundPaddingTop()) - getCompoundPaddingBottom();
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return this.mLayout != null ? this.mLayout.getHeight() : super.computeVerticalScrollRange();
    }

    float convertToLocalHorizontalCoordinate(float f) {
        return Math.min((getWidth() - getTotalPaddingRight()) - 1, Math.max(0.0f, f - getTotalPaddingLeft())) + getScrollX();
    }

    @Override // android.view.View
    public void debug(int i) {
        String str;
        super.debug(i);
        String str2 = debugIndent(i) + "frame={" + this.mLeft + ", " + this.mTop + ", " + this.mRight + ", " + this.mBottom + "} scroll={" + this.mScrollX + ", " + this.mScrollY + "} ";
        if (this.mText != null) {
            str = str2 + "mText=\"" + ((Object) this.mText) + "\" ";
            if (this.mLayout != null) {
                str = str + "mLayout width=" + this.mLayout.getWidth() + " height=" + this.mLayout.getHeight();
            }
        } else {
            str = str2 + "mText=NULL";
        }
        Log.d("View", str);
    }

    protected void deleteText_internal(int i, int i2) {
        ((Editable) this.mText).delete(i, i2);
    }

    public boolean didTouchFocusSelect() {
        return this.mEditor != null && this.mEditor.mTouchFocusSelected;
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        if (this.mDrawables != null) {
            for (Drawable drawable : this.mDrawables.mShowing) {
                if (drawable != null) {
                    drawable.setHotspot(f, f2);
                }
            }
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if ((this.mTextColor != null && this.mTextColor.isStateful()) || ((this.mHintTextColor != null && this.mHintTextColor.isStateful()) || (this.mLinkTextColor != null && this.mLinkTextColor.isStateful()))) {
            updateTextColors();
        }
        if (this.mDrawables != null) {
            int[] drawableState = getDrawableState();
            for (Drawable drawable : this.mDrawables.mShowing) {
                if (drawable != null && drawable.isStateful() && drawable.setState(drawableState)) {
                    invalidateDrawable(drawable);
                }
            }
        }
    }

    @Override // android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        TextUtils.TruncateAt ellipsize = getEllipsize();
        viewHierarchyEncoder.addProperty("text:ellipsize", ellipsize == null ? null : ellipsize.name());
        viewHierarchyEncoder.addProperty("text:textSize", getTextSize());
        viewHierarchyEncoder.addProperty("text:scaledTextSize", getScaledTextSize());
        viewHierarchyEncoder.addProperty("text:typefaceStyle", getTypefaceStyle());
        viewHierarchyEncoder.addProperty("text:selectionStart", getSelectionStart());
        viewHierarchyEncoder.addProperty("text:selectionEnd", getSelectionEnd());
        viewHierarchyEncoder.addProperty("text:curTextColor", this.mCurTextColor);
        viewHierarchyEncoder.addProperty("text:text", this.mText != null ? this.mText.toString() : null);
        viewHierarchyEncoder.addProperty("text:gravity", this.mGravity);
    }

    public void endBatchEdit() {
        if (this.mEditor != null) {
            this.mEditor.endBatchEdit();
        }
    }

    public boolean extractText(ExtractedTextRequest extractedTextRequest, ExtractedText extractedText) {
        createEditorIfNeeded();
        return this.mEditor.extractText(extractedTextRequest, extractedText);
    }

    @Override // android.view.View
    public void findViewsWithText(ArrayList<View> arrayList, CharSequence charSequence, int i) {
        super.findViewsWithText(arrayList, charSequence, i);
        if (arrayList.contains(this) || (i & 1) == 0 || TextUtils.isEmpty(charSequence) || TextUtils.isEmpty(this.mText)) {
            return;
        }
        if (this.mText.toString().toLowerCase().contains(charSequence.toString().toLowerCase())) {
            arrayList.add(this);
        }
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return TextView.class.getName();
    }

    @Override // android.view.View
    public int getAccessibilitySelectionEnd() {
        return getSelectionEnd();
    }

    @Override // android.view.View
    public int getAccessibilitySelectionStart() {
        return getSelectionStart();
    }

    public final int getAutoLinkMask() {
        return this.mAutoLinkMask;
    }

    public int getAutoSizeMaxTextSize() {
        return Math.round(this.mAutoSizeMaxTextSizeInPx);
    }

    public int getAutoSizeMinTextSize() {
        return Math.round(this.mAutoSizeMinTextSizeInPx);
    }

    public int getAutoSizeStepGranularity() {
        return Math.round(this.mAutoSizeStepGranularityInPx);
    }

    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextSizesInPx;
    }

    public int getAutoSizeTextType() {
        return this.mAutoSizeTextType;
    }

    @Override // android.view.View
    public int getAutofillType() {
        return isTextEditable() ? 1 : 0;
    }

    @Override // android.view.View
    public AutofillValue getAutofillValue() {
        if (isTextEditable()) {
            return AutofillValue.forText(TextUtils.trimToParcelableSize(getText()));
        }
        return null;
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mLayout == null ? super.getBaseline() : getBaselineOffset() + this.mLayout.getLineBaseline(0);
    }

    int getBaselineOffset() {
        int verticalOffset = (this.mGravity & 112) != 48 ? getVerticalOffset(true) : 0;
        if (isLayoutModeOptical(this.mParent)) {
            verticalOffset -= getOpticalInsets().top;
        }
        return getExtendedPaddingTop() + verticalOffset;
    }

    @Override // android.view.View
    protected int getBottomPaddingOffset() {
        return (int) Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
    }

    public int getBreakStrategy() {
        return this.mBreakStrategy;
    }

    public int getCompoundDrawablePadding() {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            return drawables.mDrawablePadding;
        }
        return 0;
    }

    public ColorStateList getCompoundDrawableTintList() {
        if (this.mDrawables != null) {
            return this.mDrawables.mTintList;
        }
        return null;
    }

    public PorterDuff.Mode getCompoundDrawableTintMode() {
        if (this.mDrawables != null) {
            return this.mDrawables.mTintMode;
        }
        return null;
    }

    public Drawable[] getCompoundDrawables() {
        Drawables drawables = this.mDrawables;
        return drawables != null ? (Drawable[]) drawables.mShowing.clone() : new Drawable[]{null, null, null, null};
    }

    public Drawable[] getCompoundDrawablesRelative() {
        Drawables drawables = this.mDrawables;
        return drawables != null ? new Drawable[]{drawables.mDrawableStart, drawables.mShowing[1], drawables.mDrawableEnd, drawables.mShowing[3]} : new Drawable[]{null, null, null, null};
    }

    public int getCompoundPaddingBottom() {
        Drawables drawables = this.mDrawables;
        return (drawables == null || drawables.mShowing[3] == null) ? this.mPaddingBottom : this.mPaddingBottom + drawables.mDrawablePadding + drawables.mDrawableSizeBottom;
    }

    public int getCompoundPaddingEnd() {
        resolveDrawables();
        return getLayoutDirection() != 1 ? getCompoundPaddingRight() : getCompoundPaddingLeft();
    }

    public int getCompoundPaddingLeft() {
        Drawables drawables = this.mDrawables;
        return (drawables == null || drawables.mShowing[0] == null) ? this.mPaddingLeft : this.mPaddingLeft + drawables.mDrawablePadding + drawables.mDrawableSizeLeft;
    }

    public int getCompoundPaddingRight() {
        Drawables drawables = this.mDrawables;
        return (drawables == null || drawables.mShowing[2] == null) ? this.mPaddingRight : this.mPaddingRight + drawables.mDrawablePadding + drawables.mDrawableSizeRight;
    }

    public int getCompoundPaddingStart() {
        resolveDrawables();
        return getLayoutDirection() != 1 ? getCompoundPaddingLeft() : getCompoundPaddingRight();
    }

    public int getCompoundPaddingTop() {
        Drawables drawables = this.mDrawables;
        return (drawables == null || drawables.mShowing[1] == null) ? this.mPaddingTop : this.mPaddingTop + drawables.mDrawablePadding + drawables.mDrawableSizeTop;
    }

    public final int getCurrentHintTextColor() {
        return this.mHintTextColor != null ? this.mCurHintTextColor : this.mCurTextColor;
    }

    public final int getCurrentTextColor() {
        return this.mCurTextColor;
    }

    public ActionMode.Callback getCustomInsertionActionModeCallback() {
        if (this.mEditor == null) {
            return null;
        }
        return this.mEditor.mCustomInsertionActionModeCallback;
    }

    public ActionMode.Callback getCustomSelectionActionModeCallback() {
        if (this.mEditor == null) {
            return null;
        }
        return this.mEditor.mCustomSelectionActionModeCallback;
    }

    protected boolean getDefaultEditable() {
        return false;
    }

    protected MovementMethod getDefaultMovementMethod() {
        return null;
    }

    public Editable getEditableText() {
        if (this.mText instanceof Editable) {
            return (Editable) this.mText;
        }
        return null;
    }

    @VisibleForTesting
    public final Editor getEditorForTesting() {
        return this.mEditor;
    }

    @ViewDebug.ExportedProperty
    public TextUtils.TruncateAt getEllipsize() {
        return this.mEllipsize;
    }

    public CharSequence getError() {
        if (this.mEditor == null) {
            return null;
        }
        return this.mEditor.mError;
    }

    public int getExtendedPaddingBottom() {
        if (this.mMaxMode != 1) {
            return getCompoundPaddingBottom();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingBottom();
        }
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int height = (getHeight() - compoundPaddingTop) - compoundPaddingBottom;
        int lineTop = this.mLayout.getLineTop(this.mMaximum);
        if (lineTop >= height) {
            return compoundPaddingBottom;
        }
        int i = this.mGravity & 112;
        if (i == 48) {
            return (compoundPaddingBottom + height) - lineTop;
        }
        return i == 80 ? compoundPaddingBottom : compoundPaddingBottom + ((height - lineTop) / 2);
    }

    public int getExtendedPaddingTop() {
        int i;
        if (this.mMaxMode != 1) {
            return getCompoundPaddingTop();
        }
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mLayout.getLineCount() <= this.mMaximum) {
            return getCompoundPaddingTop();
        }
        int compoundPaddingTop = getCompoundPaddingTop();
        int height = (getHeight() - compoundPaddingTop) - getCompoundPaddingBottom();
        int lineTop = this.mLayout.getLineTop(this.mMaximum);
        if (lineTop < height && (i = this.mGravity & 112) != 48) {
            return i == 80 ? (compoundPaddingTop + height) - lineTop : compoundPaddingTop + ((height - lineTop) / 2);
        }
        return compoundPaddingTop;
    }

    @Override // android.view.View
    protected int getFadeHeight(boolean z) {
        if (this.mLayout != null) {
            return this.mLayout.getHeight();
        }
        return 0;
    }

    @Override // android.view.View
    protected int getFadeTop(boolean z) {
        if (this.mLayout == null) {
            return 0;
        }
        int verticalOffset = (this.mGravity & 112) != 48 ? getVerticalOffset(true) : 0;
        if (z) {
            verticalOffset += getTopPaddingOffset();
        }
        return getExtendedPaddingTop() + verticalOffset;
    }

    public InputFilter[] getFilters() {
        return this.mFilters;
    }

    public int getFirstBaselineToTopHeight() {
        return getPaddingTop() - getPaint().getFontMetricsInt().top;
    }

    @Override // android.view.View
    public void getFocusedRect(Rect rect) {
        if (this.mLayout == null) {
            super.getFocusedRect(rect);
            return;
        }
        int selectionEnd = getSelectionEnd();
        if (selectionEnd < 0) {
            super.getFocusedRect(rect);
            return;
        }
        int selectionStart = getSelectionStart();
        if (selectionStart < 0 || selectionStart >= selectionEnd) {
            int lineForOffset = this.mLayout.getLineForOffset(selectionEnd);
            rect.top = this.mLayout.getLineTop(lineForOffset);
            rect.bottom = this.mLayout.getLineBottom(lineForOffset);
            rect.left = ((int) this.mLayout.getPrimaryHorizontal(selectionEnd)) - 2;
            rect.right = rect.left + 4;
        } else {
            int lineForOffset2 = this.mLayout.getLineForOffset(selectionStart);
            int lineForOffset3 = this.mLayout.getLineForOffset(selectionEnd);
            rect.top = this.mLayout.getLineTop(lineForOffset2);
            rect.bottom = this.mLayout.getLineBottom(lineForOffset3);
            if (lineForOffset2 == lineForOffset3) {
                rect.left = (int) this.mLayout.getPrimaryHorizontal(selectionStart);
                rect.right = (int) this.mLayout.getPrimaryHorizontal(selectionEnd);
            } else {
                if (this.mHighlightPathBogus) {
                    if (this.mHighlightPath == null) {
                        this.mHighlightPath = new Path();
                    }
                    this.mHighlightPath.reset();
                    this.mLayout.getSelectionPath(selectionStart, selectionEnd, this.mHighlightPath);
                    this.mHighlightPathBogus = false;
                }
                synchronized (TEMP_RECTF) {
                    this.mHighlightPath.computeBounds(TEMP_RECTF, true);
                    rect.left = ((int) TEMP_RECTF.left) - 1;
                    rect.right = ((int) TEMP_RECTF.right) + 1;
                }
            }
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            extendedPaddingTop += getVerticalOffset(false);
        }
        rect.offset(compoundPaddingLeft, extendedPaddingTop);
        rect.bottom += getExtendedPaddingBottom();
    }

    public String getFontFeatureSettings() {
        return this.mTextPaint.getFontFeatureSettings();
    }

    public String getFontVariationSettings() {
        return this.mTextPaint.getFontVariationSettings();
    }

    public boolean getFreezesText() {
        return this.mFreezesText;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public int getHighlightColor() {
        return this.mHighlightColor;
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getHint() {
        return this.mHint;
    }

    final Layout getHintLayout() {
        return this.mHintLayout;
    }

    public final ColorStateList getHintTextColors() {
        return this.mHintTextColor;
    }

    public int getHorizontalOffsetForDrawables() {
        return 0;
    }

    public boolean getHorizontallyScrolling() {
        return this.mHorizontallyScrolling;
    }

    public int getHyphenationFrequency() {
        return this.mHyphenationFrequency;
    }

    public int getImeActionId() {
        if (this.mEditor == null || this.mEditor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeActionId;
    }

    public CharSequence getImeActionLabel() {
        if (this.mEditor == null || this.mEditor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.imeActionLabel;
    }

    public LocaleList getImeHintLocales() {
        if (this.mEditor == null || this.mEditor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.imeHintLocales;
    }

    public int getImeOptions() {
        if (this.mEditor == null || this.mEditor.mInputContentType == null) {
            return 0;
        }
        return this.mEditor.mInputContentType.imeOptions;
    }

    public boolean getIncludeFontPadding() {
        return this.mIncludePad;
    }

    public Bundle getInputExtras(boolean z) {
        if (this.mEditor == null && !z) {
            return null;
        }
        createEditorIfNeeded();
        if (this.mEditor.mInputContentType == null) {
            if (!z) {
                return null;
            }
            this.mEditor.createInputContentTypeIfNeeded();
        }
        if (this.mEditor.mInputContentType.extras == null) {
            if (!z) {
                return null;
            }
            this.mEditor.mInputContentType.extras = new Bundle();
        }
        return this.mEditor.mInputContentType.extras;
    }

    public int getInputType() {
        if (this.mEditor == null) {
            return 0;
        }
        return this.mEditor.mInputType;
    }

    @Override // android.view.View
    public CharSequence getIterableTextForAccessibility() {
        return this.mText;
    }

    @Override // android.view.View
    public android.view.AccessibilityIterators.TextSegmentIterator getIteratorForGranularity(int i) {
        if (i == 4) {
            Spannable spannable = (Spannable) getIterableTextForAccessibility();
            if (!TextUtils.isEmpty(spannable) && getLayout() != null) {
                AccessibilityIterators.LineTextSegmentIterator lineTextSegmentIterator = AccessibilityIterators.LineTextSegmentIterator.getInstance();
                lineTextSegmentIterator.initialize(spannable, getLayout());
                return lineTextSegmentIterator;
            }
        } else if (i == 16 && !TextUtils.isEmpty((Spannable) getIterableTextForAccessibility()) && getLayout() != null) {
            AccessibilityIterators.PageTextSegmentIterator pageTextSegmentIterator = AccessibilityIterators.PageTextSegmentIterator.getInstance();
            pageTextSegmentIterator.initialize(this);
            return pageTextSegmentIterator;
        }
        return super.getIteratorForGranularity(i);
    }

    public int getJustificationMode() {
        return this.mJustificationMode;
    }

    public final KeyListener getKeyListener() {
        if (this.mEditor == null) {
            return null;
        }
        return this.mEditor.mKeyListener;
    }

    public int getLastBaselineToBottomHeight() {
        return getPaddingBottom() + getPaint().getFontMetricsInt().bottom;
    }

    public final Layout getLayout() {
        return this.mLayout;
    }

    @Override // android.view.View
    protected float getLeftFadingEdgeStrength() {
        if (isMarqueeFadeEnabled() && this.mMarquee != null && !this.mMarquee.isStopped()) {
            Marquee marquee = this.mMarquee;
            if (marquee.shouldDrawLeftFade()) {
                return getHorizontalFadingEdgeStrength(marquee.getScroll(), 0.0f);
            }
            return 0.0f;
        }
        if (getLineCount() != 1) {
            return super.getLeftFadingEdgeStrength();
        }
        float lineLeft = getLayout().getLineLeft(0);
        if (lineLeft > this.mScrollX) {
            return 0.0f;
        }
        return getHorizontalFadingEdgeStrength(this.mScrollX, lineLeft);
    }

    @Override // android.view.View
    protected int getLeftPaddingOffset() {
        return (getCompoundPaddingLeft() - this.mPaddingLeft) + ((int) Math.min(0.0f, this.mShadowDx - this.mShadowRadius));
    }

    public float getLetterSpacing() {
        return this.mTextPaint.getLetterSpacing();
    }

    int getLineAtCoordinate(float f) {
        return getLayout().getLineForVertical((int) (Math.min((getHeight() - getTotalPaddingBottom()) - 1, Math.max(0.0f, f - getTotalPaddingTop())) + getScrollY()));
    }

    int getLineAtCoordinateUnclamped(float f) {
        return getLayout().getLineForVertical((int) ((f - getTotalPaddingTop()) + getScrollY()));
    }

    public int getLineBounds(int i, Rect rect) {
        if (this.mLayout == null) {
            if (rect != null) {
                rect.set(0, 0, 0, 0);
            }
            return 0;
        }
        int lineBounds = this.mLayout.getLineBounds(i, rect);
        int extendedPaddingTop = getExtendedPaddingTop();
        if ((this.mGravity & 112) != 48) {
            extendedPaddingTop += getVerticalOffset(true);
        }
        if (rect != null) {
            rect.offset(getCompoundPaddingLeft(), extendedPaddingTop);
        }
        return lineBounds + extendedPaddingTop;
    }

    public int getLineCount() {
        if (this.mLayout != null) {
            return this.mLayout.getLineCount();
        }
        return 0;
    }

    public int getLineHeight() {
        return FastMath.round((this.mTextPaint.getFontMetricsInt(null) * this.mSpacingMult) + this.mSpacingAdd);
    }

    public float getLineSpacingExtra() {
        return this.mSpacingAdd;
    }

    public float getLineSpacingMultiplier() {
        return this.mSpacingMult;
    }

    public final ColorStateList getLinkTextColors() {
        return this.mLinkTextColor;
    }

    public final boolean getLinksClickable() {
        return this.mLinksClickable;
    }

    public int getMarqueeRepeatLimit() {
        return this.mMarqueeRepeatLimit;
    }

    public int getMaxEms() {
        if (this.mMaxWidthMode == 1) {
            return this.mMaxWidth;
        }
        return -1;
    }

    public int getMaxHeight() {
        if (this.mMaxMode == 2) {
            return this.mMaximum;
        }
        return -1;
    }

    public int getMaxLines() {
        if (this.mMaxMode == 1) {
            return this.mMaximum;
        }
        return -1;
    }

    public int getMaxWidth() {
        if (this.mMaxWidthMode == 2) {
            return this.mMaxWidth;
        }
        return -1;
    }

    public int getMinEms() {
        if (this.mMinWidthMode == 1) {
            return this.mMinWidth;
        }
        return -1;
    }

    public int getMinHeight() {
        if (this.mMinMode == 2) {
            return this.mMinimum;
        }
        return -1;
    }

    public int getMinLines() {
        if (this.mMinMode == 1) {
            return this.mMinimum;
        }
        return -1;
    }

    public int getMinWidth() {
        if (this.mMinWidthMode == 2) {
            return this.mMinWidth;
        }
        return -1;
    }

    public final MovementMethod getMovementMethod() {
        return this.mMovement;
    }

    int getOffsetAtCoordinate(int i, float f) {
        return getLayout().getOffsetForHorizontal(i, convertToLocalHorizontalCoordinate(f));
    }

    public int getOffsetForPosition(float f, float f2) {
        if (getLayout() == null) {
            return -1;
        }
        return getOffsetAtCoordinate(getLineAtCoordinate(f2), f);
    }

    public TextPaint getPaint() {
        return this.mTextPaint;
    }

    public int getPaintFlags() {
        return this.mTextPaint.getFlags();
    }

    public String getPrivateImeOptions() {
        if (this.mEditor == null || this.mEditor.mInputContentType == null) {
            return null;
        }
        return this.mEditor.mInputContentType.privateImeOptions;
    }

    @Override // android.view.View
    protected float getRightFadingEdgeStrength() {
        if (isMarqueeFadeEnabled() && this.mMarquee != null && !this.mMarquee.isStopped()) {
            Marquee marquee = this.mMarquee;
            return getHorizontalFadingEdgeStrength(marquee.getMaxFadeScroll(), marquee.getScroll());
        }
        if (getLineCount() != 1) {
            return super.getRightFadingEdgeStrength();
        }
        float width = this.mScrollX + ((getWidth() - getCompoundPaddingLeft()) - getCompoundPaddingRight());
        float lineRight = getLayout().getLineRight(0);
        if (lineRight < width) {
            return 0.0f;
        }
        return getHorizontalFadingEdgeStrength(width, lineRight);
    }

    @Override // android.view.View
    protected int getRightPaddingOffset() {
        return (-(getCompoundPaddingRight() - this.mPaddingRight)) + ((int) Math.max(0.0f, this.mShadowDx + this.mShadowRadius));
    }

    @ViewDebug.ExportedProperty(category = "text")
    public float getScaledTextSize() {
        return this.mTextPaint.getTextSize() / this.mTextPaint.density;
    }

    String getSelectedText() {
        if (!hasSelection()) {
            return null;
        }
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        return String.valueOf(selectionStart > selectionEnd ? this.mText.subSequence(selectionEnd, selectionStart) : this.mText.subSequence(selectionStart, selectionEnd));
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionEnd() {
        return Selection.getSelectionEnd(getText());
    }

    @ViewDebug.ExportedProperty(category = "text")
    public int getSelectionStart() {
        return Selection.getSelectionStart(getText());
    }

    public int getShadowColor() {
        return this.mShadowColor;
    }

    public float getShadowDx() {
        return this.mShadowDx;
    }

    public float getShadowDy() {
        return this.mShadowDy;
    }

    public float getShadowRadius() {
        return this.mShadowRadius;
    }

    public final boolean getShowSoftInputOnFocus() {
        return this.mEditor == null || this.mEditor.mShowSoftInputOnFocus;
    }

    public Locale getSpellCheckerLocale() {
        return getTextServicesLocale(true);
    }

    @ViewDebug.CapturedViewProperty
    public CharSequence getText() {
        return this.mText;
    }

    TextClassifier getTextClassificationSession() {
        String str;
        if (this.mTextClassificationSession == null || this.mTextClassificationSession.isDestroyed()) {
            TextClassificationManager textClassificationManager = (TextClassificationManager) this.mContext.getSystemService(TextClassificationManager.class);
            if (textClassificationManager != null) {
                if (isTextEditable()) {
                    str = TextClassifier.WIDGET_TYPE_EDITTEXT;
                } else {
                    str = isTextSelectable() ? TextClassifier.WIDGET_TYPE_TEXTVIEW : TextClassifier.WIDGET_TYPE_UNSELECTABLE_TEXTVIEW;
                }
                TextClassificationContext textClassificationContextBuild = new TextClassificationContext.Builder(this.mContext.getPackageName(), str).build();
                if (this.mTextClassifier != null) {
                    this.mTextClassificationSession = textClassificationManager.createTextClassificationSession(textClassificationContextBuild, this.mTextClassifier);
                } else {
                    this.mTextClassificationSession = textClassificationManager.createTextClassificationSession(textClassificationContextBuild);
                }
            } else {
                this.mTextClassificationSession = TextClassifier.NO_OP;
            }
        }
        return this.mTextClassificationSession;
    }

    public TextClassifier getTextClassifier() {
        if (this.mTextClassifier != null) {
            return this.mTextClassifier;
        }
        TextClassificationManager textClassificationManager = (TextClassificationManager) this.mContext.getSystemService(TextClassificationManager.class);
        return textClassificationManager != null ? textClassificationManager.getTextClassifier() : TextClassifier.NO_OP;
    }

    public final ColorStateList getTextColors() {
        return this.mTextColor;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public TextDirectionHeuristic getTextDirectionHeuristic() {
        if (hasPasswordTransformationMethod()) {
            return TextDirectionHeuristics.LTR;
        }
        if (this.mEditor != null && (this.mEditor.mInputType & 15) == 3) {
            byte directionality = Character.getDirectionality(DecimalFormatSymbols.getInstance(getTextLocale()).getDigitStrings()[0].codePointAt(0));
            return (directionality == 1 || directionality == 2) ? TextDirectionHeuristics.RTL : TextDirectionHeuristics.LTR;
        }
        boolean z = getLayoutDirection() == 1;
        switch (getTextDirection()) {
            case 2:
                return TextDirectionHeuristics.ANYRTL_LTR;
            case 3:
                return TextDirectionHeuristics.LTR;
            case 4:
                return TextDirectionHeuristics.RTL;
            case 5:
                return TextDirectionHeuristics.LOCALE;
            case 6:
                return TextDirectionHeuristics.FIRSTSTRONG_LTR;
            case 7:
                return TextDirectionHeuristics.FIRSTSTRONG_RTL;
            default:
                return z ? TextDirectionHeuristics.FIRSTSTRONG_RTL : TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
    }

    public Locale getTextLocale() {
        return this.mTextPaint.getTextLocale();
    }

    public LocaleList getTextLocales() {
        return this.mTextPaint.getTextLocales();
    }

    public PrecomputedText.Params getTextMetricsParams() {
        return new PrecomputedText.Params(new TextPaint(this.mTextPaint), getTextDirectionHeuristic(), this.mBreakStrategy, this.mHyphenationFrequency);
    }

    public float getTextScaleX() {
        return this.mTextPaint.getTextScaleX();
    }

    public Locale getTextServicesLocale() {
        return getTextServicesLocale(false);
    }

    @ViewDebug.ExportedProperty(category = "text")
    public float getTextSize() {
        return this.mTextPaint.getTextSize();
    }

    @Override // android.view.View
    protected int getTopPaddingOffset() {
        return (int) Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
    }

    public int getTotalPaddingBottom() {
        return getExtendedPaddingBottom() + getBottomVerticalOffset(true);
    }

    public int getTotalPaddingEnd() {
        return getCompoundPaddingEnd();
    }

    public int getTotalPaddingLeft() {
        return getCompoundPaddingLeft();
    }

    public int getTotalPaddingRight() {
        return getCompoundPaddingRight();
    }

    public int getTotalPaddingStart() {
        return getCompoundPaddingStart();
    }

    public int getTotalPaddingTop() {
        return getExtendedPaddingTop() + getVerticalOffset(true);
    }

    public final TransformationMethod getTransformationMethod() {
        return this.mTransformation;
    }

    CharSequence getTransformedText(int i, int i2) {
        return removeSuggestionSpans(this.mTransformed.subSequence(i, i2));
    }

    public Typeface getTypeface() {
        return this.mTextPaint.getTypeface();
    }

    @ViewDebug.ExportedProperty(category = "text", mapping = {@ViewDebug.IntToString(from = 0, to = "NORMAL"), @ViewDebug.IntToString(from = 1, to = "BOLD"), @ViewDebug.IntToString(from = 2, to = "ITALIC"), @ViewDebug.IntToString(from = 3, to = "BOLD_ITALIC")})
    public int getTypefaceStyle() {
        Typeface typeface = this.mTextPaint.getTypeface();
        if (typeface != null) {
            return typeface.getStyle();
        }
        return 0;
    }

    public final UndoManager getUndoManager() {
        throw new UnsupportedOperationException("not implemented");
    }

    public URLSpan[] getUrls() {
        return this.mText instanceof Spanned ? (URLSpan[]) ((Spanned) this.mText).getSpans(0, this.mText.length(), URLSpan.class) : new URLSpan[0];
    }

    int getVerticalOffset(boolean z) {
        int boxHeight;
        int height;
        int i = this.mGravity & 112;
        Layout layout = this.mLayout;
        if (!z && this.mText.length() == 0 && this.mHintLayout != null) {
            layout = this.mHintLayout;
        }
        if (i == 48 || (height = layout.getHeight()) >= (boxHeight = getBoxHeight(layout))) {
            return 0;
        }
        return i == 80 ? boxHeight - height : (boxHeight - height) >> 1;
    }

    public WordIterator getWordIterator() {
        if (this.mEditor != null) {
            return this.mEditor.getWordIterator();
        }
        return null;
    }

    public boolean handleBackInTextActionModeIfNeeded(KeyEvent keyEvent) {
        if (this.mEditor == null || this.mEditor.getTextActionMode() == null) {
            return false;
        }
        if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
            KeyEvent.DispatcherState keyDispatcherState = getKeyDispatcherState();
            if (keyDispatcherState != null) {
                keyDispatcherState.startTracking(keyEvent, this);
            }
            return true;
        }
        if (keyEvent.getAction() == 1) {
            KeyEvent.DispatcherState keyDispatcherState2 = getKeyDispatcherState();
            if (keyDispatcherState2 != null) {
                keyDispatcherState2.handleUpEvent(keyEvent);
            }
            if (keyEvent.isTracking() && !keyEvent.isCanceled()) {
                stopTextActionMode();
                return true;
            }
        }
        return false;
    }

    public boolean handleClick(TextLinks.TextLinkSpan textLinkSpan) {
        Preconditions.checkNotNull(textLinkSpan);
        if (!(this.mText instanceof Spanned)) {
            return false;
        }
        Spanned spanned = (Spanned) this.mText;
        int spanStart = spanned.getSpanStart(textLinkSpan);
        int spanEnd = spanned.getSpanEnd(textLinkSpan);
        if (spanStart < 0 || spanEnd > this.mText.length() || spanStart >= spanEnd) {
            return false;
        }
        final TextClassification.Request requestBuild = new TextClassification.Request.Builder(this.mText, spanStart, spanEnd).setDefaultLocales(getTextLocales()).build();
        Supplier supplier = new Supplier() { // from class: android.widget.-$$Lambda$TextView$DJlzb7VS7J_1890Kto7GAApQDN0
            @Override // java.util.function.Supplier
            public final Object get() {
                return this.f$0.getTextClassifier().classifyText(requestBuild);
            }
        };
        CompletableFuture.supplyAsync(supplier).completeOnTimeout(null, 1L, TimeUnit.SECONDS).thenAccept((Consumer) new Consumer() { // from class: android.widget.-$$Lambda$TextView$jQz3_DIfGrNeNdu_95_wi6UkW4E
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TextView.lambda$handleClick$1((TextClassification) obj);
            }
        });
        return true;
    }

    void handleTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        sLastCutCopyOrTextChangedTime = 0L;
        Editor.InputMethodState inputMethodState = this.mEditor == null ? null : this.mEditor.mInputMethodState;
        if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
            updateAfterEdit();
        }
        if (inputMethodState != null) {
            inputMethodState.mContentChanged = true;
            if (inputMethodState.mChangedStart < 0) {
                inputMethodState.mChangedStart = i;
                inputMethodState.mChangedEnd = i + i2;
            } else {
                inputMethodState.mChangedStart = Math.min(inputMethodState.mChangedStart, i);
                inputMethodState.mChangedEnd = Math.max(inputMethodState.mChangedEnd, (i + i2) - inputMethodState.mChangedDelta);
            }
            inputMethodState.mChangedDelta += i3 - i2;
        }
        resetErrorChangedFlag();
        sendOnTextChanged(charSequence, i, i2, i3);
        onTextChanged(charSequence, i, i2, i3);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return !(getBackground() == null || getBackground().getCurrent() == null) || this.mSpannable != null || hasSelection() || isHorizontalFadingEdgeEnabled();
    }

    boolean hasPasswordTransformationMethod() {
        return this.mTransformation instanceof PasswordTransformationMethod;
    }

    public boolean hasSelection() {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        return selectionStart >= 0 && selectionEnd > 0 && selectionStart != selectionEnd;
    }

    public void hideErrorIfUnchanged() {
        if (this.mEditor == null || this.mEditor.mError == null || this.mEditor.mErrorWasChanged) {
            return;
        }
        setError(null, null);
    }

    public void hideFloatingToolbar(int i) {
        if (this.mEditor != null) {
            this.mEditor.hideFloatingToolbar(i);
        }
    }

    void invalidateCursor() {
        int selectionEnd = getSelectionEnd();
        invalidateCursor(selectionEnd, selectionEnd, selectionEnd);
    }

    void invalidateCursorPath() {
        if (this.mHighlightPathBogus) {
            invalidateCursor();
            return;
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop() + getVerticalOffset(true);
        if (this.mEditor.mDrawableForCursor != null) {
            Rect bounds = this.mEditor.mDrawableForCursor.getBounds();
            invalidate(bounds.left + compoundPaddingLeft, bounds.top + extendedPaddingTop, bounds.right + compoundPaddingLeft, bounds.bottom + extendedPaddingTop);
            return;
        }
        synchronized (TEMP_RECTF) {
            float fCeil = (float) Math.ceil(this.mTextPaint.getStrokeWidth());
            if (fCeil < 1.0f) {
                fCeil = 1.0f;
            }
            float f = fCeil / 2.0f;
            this.mHighlightPath.computeBounds(TEMP_RECTF, false);
            float f2 = compoundPaddingLeft;
            float f3 = extendedPaddingTop;
            invalidate((int) Math.floor((TEMP_RECTF.left + f2) - f), (int) Math.floor((TEMP_RECTF.top + f3) - f), (int) Math.ceil(f2 + TEMP_RECTF.right + f), (int) Math.ceil(f3 + TEMP_RECTF.bottom + f));
        }
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        boolean z = false;
        if (verifyDrawable(drawable)) {
            Rect bounds = drawable.getBounds();
            int compoundPaddingRight = this.mScrollX;
            int i = this.mScrollY;
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                if (drawable == drawables.mShowing[0]) {
                    int compoundPaddingTop = getCompoundPaddingTop();
                    int compoundPaddingBottom = ((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop;
                    compoundPaddingRight += this.mPaddingLeft;
                    i += compoundPaddingTop + ((compoundPaddingBottom - drawables.mDrawableHeightLeft) / 2);
                } else if (drawable == drawables.mShowing[2]) {
                    int compoundPaddingTop2 = getCompoundPaddingTop();
                    int compoundPaddingBottom2 = ((this.mBottom - this.mTop) - getCompoundPaddingBottom()) - compoundPaddingTop2;
                    compoundPaddingRight += ((this.mRight - this.mLeft) - this.mPaddingRight) - drawables.mDrawableSizeRight;
                    i += compoundPaddingTop2 + ((compoundPaddingBottom2 - drawables.mDrawableHeightRight) / 2);
                } else if (drawable == drawables.mShowing[1]) {
                    int compoundPaddingLeft = getCompoundPaddingLeft();
                    compoundPaddingRight += compoundPaddingLeft + (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft) - drawables.mDrawableWidthTop) / 2);
                    i += this.mPaddingTop;
                } else if (drawable == drawables.mShowing[3]) {
                    int compoundPaddingLeft2 = getCompoundPaddingLeft();
                    compoundPaddingRight += compoundPaddingLeft2 + (((((this.mRight - this.mLeft) - getCompoundPaddingRight()) - compoundPaddingLeft2) - drawables.mDrawableWidthBottom) / 2);
                    i += ((this.mBottom - this.mTop) - this.mPaddingBottom) - drawables.mDrawableSizeBottom;
                }
                z = true;
            }
            if (z) {
                invalidate(bounds.left + compoundPaddingRight, bounds.top + i, bounds.right + compoundPaddingRight, bounds.bottom + i);
            }
        }
        if (z) {
            return;
        }
        super.invalidateDrawable(drawable);
    }

    void invalidateRegion(int i, int i2, boolean z) {
        int width;
        int primaryHorizontal;
        if (this.mLayout == null) {
            invalidate();
            return;
        }
        int lineForOffset = this.mLayout.getLineForOffset(i);
        int lineTop = this.mLayout.getLineTop(lineForOffset);
        if (lineForOffset > 0) {
            lineTop -= this.mLayout.getLineDescent(lineForOffset - 1);
        }
        int lineForOffset2 = i == i2 ? lineForOffset : this.mLayout.getLineForOffset(i2);
        int lineBottom = this.mLayout.getLineBottom(lineForOffset2);
        if (z && this.mEditor != null && this.mEditor.mDrawableForCursor != null) {
            Rect bounds = this.mEditor.mDrawableForCursor.getBounds();
            lineTop = Math.min(lineTop, bounds.top);
            lineBottom = Math.max(lineBottom, bounds.bottom);
        }
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int extendedPaddingTop = getExtendedPaddingTop() + getVerticalOffset(true);
        if (lineForOffset != lineForOffset2 || z) {
            width = getWidth() - getCompoundPaddingRight();
            primaryHorizontal = compoundPaddingLeft;
        } else {
            primaryHorizontal = ((int) this.mLayout.getPrimaryHorizontal(i)) + compoundPaddingLeft;
            width = ((int) (((double) this.mLayout.getPrimaryHorizontal(i2)) + 1.0d)) + compoundPaddingLeft;
        }
        invalidate(this.mScrollX + primaryHorizontal, lineTop + extendedPaddingTop, this.mScrollX + width, extendedPaddingTop + lineBottom);
    }

    @Override // android.view.View
    public boolean isAccessibilitySelectionExtendable() {
        return true;
    }

    public boolean isAllCaps() {
        TransformationMethod transformationMethod = getTransformationMethod();
        return transformationMethod != null && (transformationMethod instanceof AllCapsTransformationMethod);
    }

    public boolean isCursorVisible() {
        if (this.mEditor == null) {
            return true;
        }
        return this.mEditor.mCursorVisible;
    }

    boolean isDeviceProvisioned() {
        if (this.mDeviceProvisionedState == 0) {
            this.mDeviceProvisionedState = Settings.Global.getInt(this.mContext.getContentResolver(), "device_provisioned", 0) != 0 ? 2 : 1;
        }
        return this.mDeviceProvisionedState == 2;
    }

    public boolean isElegantTextHeight() {
        return this.mTextPaint.isElegantTextHeight();
    }

    public boolean isFallbackLineSpacing() {
        return this.mUseFallbackLineSpacing;
    }

    boolean isInBatchEditMode() {
        if (this.mEditor == null) {
            return false;
        }
        Editor.InputMethodState inputMethodState = this.mEditor.mInputMethodState;
        if (inputMethodState != null) {
            return inputMethodState.mBatchEditNesting > 0;
        }
        return this.mEditor.mInBatchEditControllers;
    }

    public boolean isInExtractedMode() {
        return false;
    }

    public boolean isInputMethodTarget() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        return inputMethodManagerPeekInstance != null && inputMethodManagerPeekInstance.isActive(this);
    }

    @Override // android.view.View
    protected boolean isPaddingOffsetRequired() {
        return (this.mShadowRadius == 0.0f && this.mDrawables == null) ? false : true;
    }

    public boolean isPositionVisible(float f, float f2) {
        synchronized (TEMP_POSITION) {
            float[] fArr = TEMP_POSITION;
            fArr[0] = f;
            fArr[1] = f2;
            View view = this;
            while (view != null) {
                if (view != this) {
                    fArr[0] = fArr[0] - view.getScrollX();
                    fArr[1] = fArr[1] - view.getScrollY();
                }
                if (fArr[0] >= 0.0f && fArr[1] >= 0.0f && fArr[0] <= view.getWidth() && fArr[1] <= view.getHeight()) {
                    if (!view.getMatrix().isIdentity()) {
                        view.getMatrix().mapPoints(fArr);
                    }
                    fArr[0] = fArr[0] + view.getLeft();
                    fArr[1] = fArr[1] + view.getTop();
                    Object parent = view.getParent();
                    view = parent instanceof View ? (View) parent : null;
                }
                return false;
            }
            return true;
        }
    }

    boolean isSingleLine() {
        return this.mSingleLine;
    }

    public boolean isSuggestionsEnabled() {
        if (this.mEditor == null || (this.mEditor.mInputType & 15) != 1 || (this.mEditor.mInputType & 524288) > 0) {
            return false;
        }
        int i = this.mEditor.mInputType & InputType.TYPE_MASK_VARIATION;
        return i == 0 || i == 48 || i == 80 || i == 64 || i == 160;
    }

    boolean isTextEditable() {
        return (this.mText instanceof Editable) && onCheckIsTextEditor() && isEnabled();
    }

    public boolean isTextSelectable() {
        if (this.mEditor == null) {
            return false;
        }
        return this.mEditor.mTextIsSelectable;
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mDrawables != null) {
            for (Drawable drawable : this.mDrawables.mShowing) {
                if (drawable != null) {
                    drawable.jumpToCurrentState();
                }
            }
        }
    }

    public int length() {
        return this.mText.length();
    }

    @VisibleForTesting
    public void makeNewLayout(int i, int i2, BoringLayout.Metrics metrics, BoringLayout.Metrics metrics2, int i3, boolean z) {
        int i4;
        boolean z2;
        int i5;
        BoringLayout.Metrics metrics3;
        Layout.Alignment alignment;
        int i6;
        int i7 = i3;
        stopMarquee();
        this.mOldMaximum = this.mMaximum;
        this.mOldMaxMode = this.mMaxMode;
        this.mHighlightPathBogus = true;
        int i8 = i < 0 ? 0 : i;
        int i9 = i2 < 0 ? 0 : i2;
        Layout.Alignment layoutAlignment = getLayoutAlignment();
        boolean z3 = this.mSingleLine && this.mLayout != null && (layoutAlignment == Layout.Alignment.ALIGN_NORMAL || layoutAlignment == Layout.Alignment.ALIGN_OPPOSITE);
        int paragraphDirection = z3 ? this.mLayout.getParagraphDirection(0) : 0;
        boolean z4 = this.mEllipsize != null && getKeyListener() == null;
        boolean z5 = this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode != 0;
        TextUtils.TruncateAt truncateAt = this.mEllipsize;
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && this.mMarqueeFadeMode == 1) {
            truncateAt = TextUtils.TruncateAt.END_SMALL;
        }
        TextUtils.TruncateAt truncateAt2 = truncateAt;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        this.mLayout = makeSingleLayout(i8, metrics, i3, layoutAlignment, z4, truncateAt2, truncateAt2 == this.mEllipsize);
        if (z5) {
            this.mSavedMarqueeModeLayout = makeSingleLayout(i8, metrics, i3, layoutAlignment, z4, truncateAt2 == TextUtils.TruncateAt.MARQUEE ? TextUtils.TruncateAt.END : TextUtils.TruncateAt.MARQUEE, truncateAt2 != this.mEllipsize);
        }
        boolean z6 = this.mEllipsize != null;
        this.mHintLayout = null;
        if (this.mHint != null) {
            int i10 = z6 ? i8 : i9;
            if (metrics2 == UNKNOWN_BORING) {
                BoringLayout.Metrics metricsIsBoring = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, this.mHintBoring);
                if (metricsIsBoring != null) {
                    this.mHintBoring = metricsIsBoring;
                }
                metrics3 = metricsIsBoring;
            } else {
                metrics3 = metrics2;
            }
            if (metrics3 == null) {
                i4 = paragraphDirection;
                alignment = layoutAlignment;
                i6 = i10;
                i5 = 0;
            } else {
                if (metrics3.width <= i10 && (!z6 || metrics3.width <= i7)) {
                    if (this.mSavedHintLayout != null) {
                        this.mHintLayout = this.mSavedHintLayout.replaceOrMake(this.mHint, this.mTextPaint, i10, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad);
                    } else {
                        this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, i10, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad);
                    }
                    this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
                } else if (z6 && metrics3.width <= i10) {
                    if (this.mSavedHintLayout != null) {
                        alignment = layoutAlignment;
                        i4 = paragraphDirection;
                        i6 = i10;
                        i5 = 0;
                        this.mHintLayout = this.mSavedHintLayout.replaceOrMake(this.mHint, this.mTextPaint, i10, alignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad, this.mEllipsize, i3);
                    } else {
                        i4 = paragraphDirection;
                        alignment = layoutAlignment;
                        i6 = i10;
                        i5 = 0;
                        this.mHintLayout = BoringLayout.make(this.mHint, this.mTextPaint, i6, layoutAlignment, this.mSpacingMult, this.mSpacingAdd, metrics3, this.mIncludePad, this.mEllipsize, i3);
                    }
                }
                i4 = paragraphDirection;
                alignment = layoutAlignment;
                i6 = i10;
                i5 = 0;
            }
            if (this.mHintLayout == null) {
                z2 = true;
                StaticLayout.Builder maxLines = StaticLayout.Builder.obtain(this.mHint, i5, this.mHint.length(), this.mTextPaint, i6).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setMaxLines(this.mMaxMode == 1 ? this.mMaximum : Integer.MAX_VALUE);
                if (z6) {
                    maxLines.setEllipsize(this.mEllipsize).setEllipsizedWidth(i7);
                }
                this.mHintLayout = maxLines.build();
            } else {
                z2 = true;
            }
        } else {
            i4 = paragraphDirection;
            z2 = true;
            i7 = i7;
            i5 = 0;
        }
        if (z || (z3 && i4 != this.mLayout.getParagraphDirection(i5))) {
            registerForPreDraw();
        }
        if (this.mEllipsize == TextUtils.TruncateAt.MARQUEE && !compressText(i7)) {
            int i11 = this.mLayoutParams.height;
            if (i11 == -2 || i11 == -1) {
                this.mRestartMarquee = z2;
            } else {
                startMarquee();
            }
        }
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    /* JADX WARN: Code duplicated, block: B:39:0x0102  */
    /* JADX WARN: Code duplicated, block: B:41:0x0146  */
    /* JADX WARN: Code duplicated, block: B:42:0x0149  */
    /* JADX WARN: Code duplicated, block: B:45:0x0152  */
    /* JADX WARN: Code duplicated, block: B:48:? A[RETURN, SYNTHETIC] */
    protected Layout makeSingleLayout(int i, BoringLayout.Metrics metrics, int i2, Layout.Alignment alignment, boolean z, TextUtils.TruncateAt truncateAt, boolean z2) {
        BoringLayout.Metrics metrics2;
        int i3;
        StaticLayout.Builder maxLines;
        Layout layoutMake = null;
        layoutMake = null;
        layoutMake = null;
        if (!useDynamicLayout()) {
            if (metrics == UNKNOWN_BORING) {
                BoringLayout.Metrics metricsIsBoring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
                if (metricsIsBoring != null) {
                    this.mBoring = metricsIsBoring;
                }
                metrics2 = metricsIsBoring;
            } else {
                metrics2 = metrics;
            }
            if (metrics2 != null) {
                if (metrics2.width <= i && (truncateAt == null || metrics2.width <= i2)) {
                    layoutMake = (!z2 || this.mSavedLayout == null) ? BoringLayout.make(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad) : this.mSavedLayout.replaceOrMake(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad);
                    if (z2) {
                        this.mSavedLayout = (BoringLayout) layoutMake;
                    }
                } else if (z && metrics2.width <= i) {
                    layoutMake = (!z2 || this.mSavedLayout == null) ? BoringLayout.make(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad, truncateAt, i2) : this.mSavedLayout.replaceOrMake(this.mTransformed, this.mTextPaint, i, alignment, this.mSpacingMult, this.mSpacingAdd, metrics2, this.mIncludePad, truncateAt, i2);
                }
            }
            if (layoutMake == null) {
                return layoutMake;
            }
            StaticLayout.Builder justificationMode = StaticLayout.Builder.obtain(this.mTransformed, 0, this.mTransformed.length(), this.mTextPaint, i).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode);
            if (this.mMaxMode == 1) {
                i3 = this.mMaximum;
            } else {
                i3 = Integer.MAX_VALUE;
            }
            maxLines = justificationMode.setMaxLines(i3);
            if (z) {
                maxLines.setEllipsize(truncateAt).setEllipsizedWidth(i2);
            }
            return maxLines.build();
        }
        layoutMake = DynamicLayout.Builder.obtain(this.mText, this.mTextPaint, i).setDisplayText(this.mTransformed).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode).setEllipsize(getKeyListener() == null ? truncateAt : null).setEllipsizedWidth(i2).build();
        if (layoutMake == null) {
            return layoutMake;
        }
        StaticLayout.Builder justificationMode2 = StaticLayout.Builder.obtain(this.mTransformed, 0, this.mTransformed.length(), this.mTextPaint, i).setAlignment(alignment).setTextDirection(this.mTextDir).setLineSpacing(this.mSpacingAdd, this.mSpacingMult).setIncludePad(this.mIncludePad).setUseLineSpacingFromFallbacks(this.mUseFallbackLineSpacing).setBreakStrategy(this.mBreakStrategy).setHyphenationFrequency(this.mHyphenationFrequency).setJustificationMode(this.mJustificationMode);
        if (this.mMaxMode == 1) {
            i3 = this.mMaximum;
        } else {
            i3 = Integer.MAX_VALUE;
        }
        maxLines = justificationMode2.setMaxLines(i3);
        if (z) {
            maxLines.setEllipsize(truncateAt).setEllipsizedWidth(i2);
        }
        return maxLines.build();
    }

    public boolean moveCursorToVisibleOffset() {
        int selectionStart;
        if (!(this.mText instanceof Spannable) || (selectionStart = getSelectionStart()) != getSelectionEnd()) {
            return false;
        }
        int lineForOffset = this.mLayout.getLineForOffset(selectionStart);
        int lineTop = this.mLayout.getLineTop(lineForOffset);
        int lineTop2 = this.mLayout.getLineTop(lineForOffset + 1);
        int extendedPaddingTop = ((this.mBottom - this.mTop) - getExtendedPaddingTop()) - getExtendedPaddingBottom();
        int i = lineTop2 - lineTop;
        int i2 = i / 2;
        int i3 = extendedPaddingTop / 4;
        if (i2 > i3) {
            i2 = i3;
        }
        int i4 = this.mScrollY;
        int i5 = i4 + i2;
        if (lineTop < i5) {
            lineForOffset = this.mLayout.getLineForVertical(i5 + i);
        } else {
            int i6 = (extendedPaddingTop + i4) - i2;
            if (lineTop2 > i6) {
                lineForOffset = this.mLayout.getLineForVertical(i6 - i);
            }
        }
        int compoundPaddingLeft = ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int i7 = this.mScrollX;
        int offsetForHorizontal = this.mLayout.getOffsetForHorizontal(lineForOffset, i7);
        int offsetForHorizontal2 = this.mLayout.getOffsetForHorizontal(lineForOffset, compoundPaddingLeft + i7);
        int i8 = offsetForHorizontal < offsetForHorizontal2 ? offsetForHorizontal : offsetForHorizontal2;
        if (offsetForHorizontal > offsetForHorizontal2) {
            offsetForHorizontal2 = offsetForHorizontal;
        }
        if (selectionStart < i8) {
            offsetForHorizontal2 = i8;
        } else if (selectionStart <= offsetForHorizontal2) {
            offsetForHorizontal2 = selectionStart;
        }
        if (offsetForHorizontal2 == selectionStart) {
            return false;
        }
        Selection.setSelection(this.mSpannable, offsetForHorizontal2);
        return true;
    }

    @VisibleForTesting
    public void nullLayouts() {
        if ((this.mLayout instanceof BoringLayout) && this.mSavedLayout == null) {
            this.mSavedLayout = (BoringLayout) this.mLayout;
        }
        if ((this.mHintLayout instanceof BoringLayout) && this.mSavedHintLayout == null) {
            this.mSavedHintLayout = (BoringLayout) this.mHintLayout;
        }
        this.mHintLayout = null;
        this.mLayout = null;
        this.mSavedMarqueeModeLayout = null;
        this.mHintBoring = null;
        this.mBoring = null;
        if (this.mEditor != null) {
            this.mEditor.prepareCursorControllers();
        }
    }

    @Override // android.view.View
    public void onActivityResult(int i, int i2, Intent intent) {
        if (i == 100) {
            if (i2 != -1 || intent == null) {
                if (this.mSpannable != null) {
                    Selection.setSelection(this.mSpannable, getSelectionEnd());
                    return;
                }
                return;
            }
            CharSequence charSequenceExtra = intent.getCharSequenceExtra(Intent.EXTRA_PROCESS_TEXT);
            if (charSequenceExtra != null) {
                if (!isTextEditable()) {
                    if (charSequenceExtra.length() > 0) {
                        Toast.makeText(getContext(), String.valueOf(charSequenceExtra), 1).show();
                    }
                } else {
                    replaceSelectionWithText(charSequenceExtra);
                    if (this.mEditor != null) {
                        this.mEditor.refreshTextActionMode();
                    }
                }
            }
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mEditor != null) {
            this.mEditor.onAttachedToWindow();
        }
        if (this.mPreDrawListenerDetached) {
            getViewTreeObserver().addOnPreDrawListener(this);
            this.mPreDrawListenerDetached = false;
        }
    }

    public void onBeginBatchEdit() {
    }

    @Override // android.view.View
    public boolean onCheckIsTextEditor() {
        return (this.mEditor == null || this.mEditor.mInputType == 0) ? false : true;
    }

    public void onCommitCompletion(CompletionInfo completionInfo) {
    }

    public void onCommitCorrection(CorrectionInfo correctionInfo) {
        if (this.mEditor != null) {
            this.mEditor.onCommitCorrection(correctionInfo);
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (this.mLocalesChanged) {
            return;
        }
        this.mTextPaint.setTextLocales(LocaleList.getDefault());
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    @Override // android.view.View
    protected void onCreateContextMenu(ContextMenu contextMenu) {
        if (this.mEditor != null) {
            this.mEditor.onCreateContextMenu(contextMenu);
        }
    }

    @Override // android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArrOnCreateDrawableState;
        if (this.mSingleLine) {
            iArrOnCreateDrawableState = super.onCreateDrawableState(i);
        } else {
            iArrOnCreateDrawableState = super.onCreateDrawableState(i + 1);
            mergeDrawableStates(iArrOnCreateDrawableState, MULTILINE_STATE_SET);
        }
        if (isTextSelectable()) {
            int length = iArrOnCreateDrawableState.length;
            for (int i2 = 0; i2 < length; i2++) {
                if (iArrOnCreateDrawableState[i2] == 16842919) {
                    int[] iArr = new int[length - 1];
                    System.arraycopy(iArrOnCreateDrawableState, 0, iArr, 0, i2);
                    System.arraycopy(iArrOnCreateDrawableState, i2 + 1, iArr, i2, (length - i2) - 1);
                    return iArr;
                }
            }
        }
        return iArrOnCreateDrawableState;
    }

    @Override // android.view.View
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        if (onCheckIsTextEditor() && isEnabled()) {
            this.mEditor.createInputMethodStateIfNeeded();
            editorInfo.inputType = getInputType();
            if (this.mEditor.mInputContentType != null) {
                editorInfo.imeOptions = this.mEditor.mInputContentType.imeOptions;
                editorInfo.privateImeOptions = this.mEditor.mInputContentType.privateImeOptions;
                editorInfo.actionLabel = this.mEditor.mInputContentType.imeActionLabel;
                editorInfo.actionId = this.mEditor.mInputContentType.imeActionId;
                editorInfo.extras = this.mEditor.mInputContentType.extras;
                editorInfo.hintLocales = this.mEditor.mInputContentType.imeHintLocales;
            } else {
                editorInfo.imeOptions = 0;
                editorInfo.hintLocales = null;
            }
            if (focusSearch(130) != null) {
                editorInfo.imeOptions |= 134217728;
            }
            if (focusSearch(33) != null) {
                editorInfo.imeOptions |= 67108864;
            }
            if ((editorInfo.imeOptions & 255) == 0) {
                if ((editorInfo.imeOptions & 134217728) != 0) {
                    editorInfo.imeOptions |= 5;
                } else {
                    editorInfo.imeOptions |= 6;
                }
                if (!shouldAdvanceFocusOnEnter()) {
                    editorInfo.imeOptions |= 1073741824;
                }
            }
            if (isMultilineInputType(editorInfo.inputType)) {
                editorInfo.imeOptions |= 1073741824;
            }
            editorInfo.hintText = this.mHint;
            if (this.mText instanceof Editable) {
                EditableInputConnection editableInputConnection = new EditableInputConnection(this);
                editorInfo.initialSelStart = getSelectionStart();
                editorInfo.initialSelEnd = getSelectionEnd();
                editorInfo.initialCapsMode = editableInputConnection.getCursorCapsMode(getInputType());
                return editableInputConnection;
            }
        }
        return null;
    }

    @Override // android.view.View
    protected void onDetachedFromWindowInternal() {
        if (this.mPreDrawRegistered) {
            getViewTreeObserver().removeOnPreDrawListener(this);
            this.mPreDrawListenerDetached = true;
        }
        resetResolvedDrawables();
        if (this.mEditor != null) {
            this.mEditor.onDetachedFromWindow();
        }
        super.onDetachedFromWindowInternal();
    }

    @Override // android.view.View
    public boolean onDragEvent(DragEvent dragEvent) {
        int action = dragEvent.getAction();
        if (action == 5) {
            requestFocus();
            return true;
        }
        switch (action) {
            case 1:
                return this.mEditor != null && this.mEditor.hasInsertionController();
            case 2:
                if (this.mText instanceof Spannable) {
                    Selection.setSelection(this.mSpannable, getOffsetForPosition(dragEvent.getX(), dragEvent.getY()));
                }
                return true;
            case 3:
                if (this.mEditor != null) {
                    this.mEditor.onDrop(dragEvent);
                }
                return true;
            default:
                return true;
        }
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int verticalOffset;
        int verticalOffset2;
        restartMarqueeIfNeeded();
        super.onDraw(canvas);
        int compoundPaddingLeft = getCompoundPaddingLeft();
        int compoundPaddingTop = getCompoundPaddingTop();
        int compoundPaddingRight = getCompoundPaddingRight();
        int compoundPaddingBottom = getCompoundPaddingBottom();
        int i = this.mScrollX;
        int i2 = this.mScrollY;
        int i3 = this.mRight;
        int i4 = this.mLeft;
        int i5 = this.mBottom;
        int i6 = this.mTop;
        boolean zIsLayoutRtl = isLayoutRtl();
        int horizontalOffsetForDrawables = getHorizontalOffsetForDrawables();
        int i7 = zIsLayoutRtl ? 0 : horizontalOffsetForDrawables;
        if (!zIsLayoutRtl) {
            horizontalOffsetForDrawables = 0;
        }
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            int i8 = ((i5 - i6) - compoundPaddingBottom) - compoundPaddingTop;
            int i9 = ((i3 - i4) - compoundPaddingRight) - compoundPaddingLeft;
            if (drawables.mShowing[0] != null) {
                canvas.save();
                canvas.translate(this.mPaddingLeft + i + i7, i2 + compoundPaddingTop + ((i8 - drawables.mDrawableHeightLeft) / 2));
                drawables.mShowing[0].draw(canvas);
                canvas.restore();
            }
            if (drawables.mShowing[2] != null) {
                canvas.save();
                canvas.translate(((((i + i3) - i4) - this.mPaddingRight) - drawables.mDrawableSizeRight) - horizontalOffsetForDrawables, i2 + compoundPaddingTop + ((i8 - drawables.mDrawableHeightRight) / 2));
                drawables.mShowing[2].draw(canvas);
                canvas.restore();
            }
            if (drawables.mShowing[1] != null) {
                canvas.save();
                canvas.translate(i + compoundPaddingLeft + ((i9 - drawables.mDrawableWidthTop) / 2), this.mPaddingTop + i2);
                drawables.mShowing[1].draw(canvas);
                canvas.restore();
            }
            if (drawables.mShowing[3] != null) {
                canvas.save();
                canvas.translate(i + compoundPaddingLeft + ((i9 - drawables.mDrawableWidthBottom) / 2), (((i2 + i5) - i6) - this.mPaddingBottom) - drawables.mDrawableSizeBottom);
                drawables.mShowing[3].draw(canvas);
                canvas.restore();
            }
        }
        int i10 = this.mCurTextColor;
        if (this.mLayout == null) {
            assumeLayout();
        }
        Layout layout = this.mLayout;
        if (this.mHint != null && this.mText.length() == 0) {
            if (this.mHintTextColor != null) {
                i10 = this.mCurHintTextColor;
            }
            layout = this.mHintLayout;
        }
        this.mTextPaint.setColor(i10);
        this.mTextPaint.drawableState = getDrawableState();
        canvas.save();
        int extendedPaddingTop = getExtendedPaddingTop();
        int extendedPaddingBottom = getExtendedPaddingBottom();
        int height = this.mLayout.getHeight() - (((this.mBottom - this.mTop) - compoundPaddingBottom) - compoundPaddingTop);
        float fMin = compoundPaddingLeft + i;
        float fMin2 = i2 == 0 ? 0.0f : extendedPaddingTop + i2;
        float compoundPaddingRight2 = ((i3 - i4) - getCompoundPaddingRight()) + i;
        int i11 = (i5 - i6) + i2;
        if (i2 == height) {
            extendedPaddingBottom = 0;
        }
        float fMax = i11 - extendedPaddingBottom;
        if (this.mShadowRadius != 0.0f) {
            fMin += Math.min(0.0f, this.mShadowDx - this.mShadowRadius);
            compoundPaddingRight2 += Math.max(0.0f, this.mShadowDx + this.mShadowRadius);
            fMin2 += Math.min(0.0f, this.mShadowDy - this.mShadowRadius);
            fMax += Math.max(0.0f, this.mShadowDy + this.mShadowRadius);
        }
        canvas.clipRect(fMin, fMin2, compoundPaddingRight2, fMax);
        if ((this.mGravity & 112) != 48) {
            verticalOffset2 = getVerticalOffset(false);
            verticalOffset = getVerticalOffset(true);
        } else {
            verticalOffset = 0;
            verticalOffset2 = 0;
        }
        canvas.translate(compoundPaddingLeft, extendedPaddingTop + verticalOffset2);
        int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection());
        if (isMarqueeFadeEnabled()) {
            if (!this.mSingleLine && getLineCount() == 1 && canMarquee() && (absoluteGravity & 7) != 3) {
                canvas.translate(layout.getParagraphDirection(0) * (this.mLayout.getLineRight(0) - ((this.mRight - this.mLeft) - (getCompoundPaddingLeft() + getCompoundPaddingRight()))), 0.0f);
            }
            if (this.mMarquee != null && this.mMarquee.isRunning()) {
                canvas.translate(layout.getParagraphDirection(0) * (-this.mMarquee.getScroll()), 0.0f);
            }
        }
        int i12 = verticalOffset - verticalOffset2;
        Path updatedHighlightPath = getUpdatedHighlightPath();
        if (this.mEditor != null) {
            this.mEditor.onDraw(canvas, layout, updatedHighlightPath, this.mHighlightPaint, i12);
        } else {
            layout.draw(canvas, updatedHighlightPath, this.mHighlightPaint, i12);
        }
        if (this.mMarquee != null && this.mMarquee.shouldDrawGhost()) {
            canvas.translate(layout.getParagraphDirection(0) * this.mMarquee.getGhostOffset(), 0.0f);
            layout.draw(canvas, updatedHighlightPath, this.mHighlightPaint, i12);
        }
        canvas.restore();
    }

    public void onEditorAction(int i) {
        Editor.InputContentType inputContentType = this.mEditor == null ? null : this.mEditor.mInputContentType;
        if (inputContentType != null) {
            if (inputContentType.onEditorActionListener != null && inputContentType.onEditorActionListener.onEditorAction(this, i, null)) {
                return;
            }
            if (i == 5) {
                View viewFocusSearch = focusSearch(2);
                if (viewFocusSearch != null && !viewFocusSearch.requestFocus(2)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            }
            if (i == 7) {
                View viewFocusSearch2 = focusSearch(1);
                if (viewFocusSearch2 != null && !viewFocusSearch2.requestFocus(1)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                return;
            }
            if (i == 6) {
                InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                if (inputMethodManagerPeekInstance == null || !inputMethodManagerPeekInstance.isActive(this)) {
                    return;
                }
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
                return;
            }
        }
        ViewRootImpl viewRootImpl = getViewRootImpl();
        if (viewRootImpl != null) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(jUptimeMillis, jUptimeMillis, 0, 66, 0, 0, -1, 0, 22));
            viewRootImpl.dispatchKeyFromIme(new KeyEvent(SystemClock.uptimeMillis(), jUptimeMillis, 1, 66, 0, 0, -1, 0, 22));
        }
    }

    public void onEndBatchEdit() {
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean z, int i, Rect rect) {
        if (isTemporarilyDetached()) {
            super.onFocusChanged(z, i, rect);
            return;
        }
        if (this.mEditor != null) {
            this.mEditor.onFocusChanged(z, i);
        }
        if (z && this.mSpannable != null) {
            MetaKeyKeyListener.resetMetaState(this.mSpannable);
        }
        startStopMarquee(z);
        if (this.mTransformation != null) {
            this.mTransformation.onFocusChanged(this, this.mText, z, i, rect);
        }
        super.onFocusChanged(z, i, rect);
    }

    @Override // android.view.View
    public boolean onGenericMotionEvent(MotionEvent motionEvent) {
        if (this.mMovement != null && (this.mText instanceof Spannable) && this.mLayout != null) {
            try {
                if (this.mMovement.onGenericMotionEvent(this, this.mSpannable, motionEvent)) {
                    return true;
                }
            } catch (AbstractMethodError unused) {
            }
        }
        return super.onGenericMotionEvent(motionEvent);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEventInternal(accessibilityEvent);
        accessibilityEvent.setPassword(hasPasswordTransformationMethod());
        if (accessibilityEvent.getEventType() == 8192) {
            accessibilityEvent.setFromIndex(Selection.getSelectionStart(this.mText));
            accessibilityEvent.setToIndex(Selection.getSelectionEnd(this.mText));
            accessibilityEvent.setItemCount(this.mText.length());
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        accessibilityNodeInfo.setPassword(hasPasswordTransformationMethod());
        accessibilityNodeInfo.setText(getTextForAccessibility());
        accessibilityNodeInfo.setHintText(this.mHint);
        accessibilityNodeInfo.setShowingHintText(isShowingHint());
        if (this.mBufferType == BufferType.EDITABLE) {
            accessibilityNodeInfo.setEditable(true);
            if (isEnabled()) {
                accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_TEXT);
            }
        }
        if (this.mEditor != null) {
            accessibilityNodeInfo.setInputType(this.mEditor.mInputType);
            if (this.mEditor.mError != null) {
                accessibilityNodeInfo.setContentInvalid(true);
                accessibilityNodeInfo.setError(this.mEditor.mError);
            }
        }
        if (!TextUtils.isEmpty(this.mText)) {
            accessibilityNodeInfo.addAction(256);
            accessibilityNodeInfo.addAction(512);
            accessibilityNodeInfo.setMovementGranularities(31);
            accessibilityNodeInfo.addAction(131072);
            accessibilityNodeInfo.setAvailableExtraData(Arrays.asList(AccessibilityNodeInfo.EXTRA_DATA_TEXT_CHARACTER_LOCATION_KEY));
        }
        if (isFocused()) {
            if (canCopy()) {
                accessibilityNodeInfo.addAction(16384);
            }
            if (canPaste()) {
                accessibilityNodeInfo.addAction(32768);
            }
            if (canCut()) {
                accessibilityNodeInfo.addAction(65536);
            }
            if (canShare()) {
                accessibilityNodeInfo.addAction(new AccessibilityNodeInfo.AccessibilityAction(268435456, getResources().getString(R.string.share)));
            }
            if (canProcessText()) {
                this.mEditor.mProcessTextIntentActionsHandler.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
            }
        }
        int length = this.mFilters.length;
        for (int i = 0; i < length; i++) {
            InputFilter inputFilter = this.mFilters[i];
            if (inputFilter instanceof InputFilter.LengthFilter) {
                accessibilityNodeInfo.setMaxTextLength(((InputFilter.LengthFilter) inputFilter).getMax());
            }
        }
        if (isSingleLine()) {
            return;
        }
        accessibilityNodeInfo.setMultiLine(true);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (doKeyDown(i, keyEvent, null) == 0) {
            return super.onKeyDown(i, keyEvent);
        }
        return true;
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent) {
        KeyEvent keyEventChangeAction = KeyEvent.changeAction(keyEvent, 0);
        int iDoKeyDown = doKeyDown(i, keyEventChangeAction, keyEvent);
        if (iDoKeyDown == 0) {
            return super.onKeyMultiple(i, i2, keyEvent);
        }
        if (iDoKeyDown == -1) {
            return true;
        }
        int i3 = i2 - 1;
        KeyEvent keyEventChangeAction2 = KeyEvent.changeAction(keyEvent, 1);
        if (iDoKeyDown != 1) {
            if (iDoKeyDown == 2) {
                this.mMovement.onKeyUp(this, this.mSpannable, i, keyEventChangeAction2);
                while (true) {
                    i3--;
                    if (i3 <= 0) {
                        break;
                    }
                    this.mMovement.onKeyDown(this, this.mSpannable, i, keyEventChangeAction);
                    this.mMovement.onKeyUp(this, this.mSpannable, i, keyEventChangeAction2);
                }
            }
        } else {
            this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEventChangeAction2);
            while (true) {
                i3--;
                if (i3 <= 0) {
                    break;
                }
                this.mEditor.mKeyListener.onKeyDown(this, (Editable) this.mText, i, keyEventChangeAction);
                this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEventChangeAction2);
            }
            hideErrorIfUnchanged();
        }
        return true;
    }

    @Override // android.view.View
    public boolean onKeyPreIme(int i, KeyEvent keyEvent) {
        if (i == 4 && handleBackInTextActionModeIfNeeded(keyEvent)) {
            return true;
        }
        return super.onKeyPreIme(i, keyEvent);
    }

    @Override // android.view.View
    public boolean onKeyShortcut(int i, KeyEvent keyEvent) {
        if (keyEvent.hasModifiers(4096)) {
            if (i != 29) {
                if (i != 31) {
                    if (i != 50) {
                        if (i != 52) {
                            if (i == 54 && canUndo()) {
                                return onTextContextMenuItem(16908338);
                            }
                        } else if (canCut()) {
                            return onTextContextMenuItem(16908320);
                        }
                    } else if (canPaste()) {
                        return onTextContextMenuItem(16908322);
                    }
                } else if (canCopy()) {
                    return onTextContextMenuItem(16908321);
                }
            } else if (canSelectText()) {
                return onTextContextMenuItem(16908319);
            }
        } else if (keyEvent.hasModifiers(4097)) {
            if (i != 50) {
                if (i == 54 && canRedo()) {
                    return onTextContextMenuItem(16908339);
                }
            } else if (canPaste()) {
                return onTextContextMenuItem(16908337);
            }
        }
        return super.onKeyShortcut(i, keyEvent);
    }

    @Override // android.view.View, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        InputMethodManager inputMethodManagerPeekInstance;
        if (!isEnabled()) {
            return super.onKeyUp(i, keyEvent);
        }
        if (!KeyEvent.isModifierKey(i)) {
            this.mPreventDefaultMovement = false;
        }
        if (i == 23) {
            if (keyEvent.hasNoModifiers() && !hasOnClickListeners() && this.mMovement != null && (this.mText instanceof Editable) && this.mLayout != null && onCheckIsTextEditor()) {
                InputMethodManager inputMethodManagerPeekInstance2 = InputMethodManager.peekInstance();
                viewClicked(inputMethodManagerPeekInstance2);
                if (inputMethodManagerPeekInstance2 != null && getShowSoftInputOnFocus()) {
                    inputMethodManagerPeekInstance2.showSoftInput(this, 0);
                }
            }
            return super.onKeyUp(i, keyEvent);
        }
        if (i != 66 || !keyEvent.hasNoModifiers()) {
            if (this.mEditor != null && this.mEditor.mKeyListener != null && this.mEditor.mKeyListener.onKeyUp(this, (Editable) this.mText, i, keyEvent)) {
                return true;
            }
            if (this.mMovement == null || this.mLayout == null || !this.mMovement.onKeyUp(this, this.mSpannable, i, keyEvent)) {
                return super.onKeyUp(i, keyEvent);
            }
            return true;
        }
        if (this.mEditor != null && this.mEditor.mInputContentType != null && this.mEditor.mInputContentType.onEditorActionListener != null && this.mEditor.mInputContentType.enterDown) {
            this.mEditor.mInputContentType.enterDown = false;
            if (this.mEditor.mInputContentType.onEditorActionListener.onEditorAction(this, 0, keyEvent)) {
                return true;
            }
        }
        if (((keyEvent.getFlags() & 16) != 0 || shouldAdvanceFocusOnEnter()) && !hasOnClickListeners()) {
            View viewFocusSearch = focusSearch(130);
            if (viewFocusSearch != null) {
                if (!viewFocusSearch.requestFocus(130)) {
                    throw new IllegalStateException("focus search returned a view that wasn't able to take focus!");
                }
                super.onKeyUp(i, keyEvent);
                return true;
            }
            if ((keyEvent.getFlags() & 16) != 0 && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null && inputMethodManagerPeekInstance.isActive(this)) {
                inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        if (this.mDeferScroll >= 0) {
            int i5 = this.mDeferScroll;
            this.mDeferScroll = -1;
            bringPointIntoView(Math.min(i5, this.mText.length()));
        }
        autoSizeText();
    }

    void onLocaleChanged() {
        this.mEditor.onLocaleChanged();
    }

    /* JADX WARN: Code duplicated, block: B:59:0x00f1 A[PHI: r3 r15
      0x00f1: PHI (r3v1 android.text.BoringLayout$Metrics) = (r3v0 android.text.BoringLayout$Metrics), (r3v3 android.text.BoringLayout$Metrics) binds: [B:38:0x009f, B:58:0x00ef] A[DONT_GENERATE, DONT_INLINE]
      0x00f1: PHI (r15v2 int) = (r15v1 int), (r15v5 int) binds: [B:38:0x009f, B:58:0x00ef] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        boolean z;
        int iMax;
        int i3;
        int iMin;
        int i4;
        BoringLayout.Metrics metrics;
        int i5;
        int i6;
        int i7;
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        BoringLayout.Metrics metricsIsBoring = UNKNOWN_BORING;
        BoringLayout.Metrics metricsIsBoring2 = UNKNOWN_BORING;
        if (this.mTextDir == null) {
            this.mTextDir = getTextDirectionHeuristic();
        }
        float f = mode == Integer.MIN_VALUE ? size : Float.MAX_VALUE;
        if (mode == 1073741824) {
            iMin = size;
            metrics = metricsIsBoring2;
            i4 = -1;
            z = false;
        } else {
            int iDesired = (this.mLayout == null || this.mEllipsize != null) ? -1 : desired(this.mLayout);
            if (iDesired < 0) {
                metricsIsBoring = BoringLayout.isBoring(this.mTransformed, this.mTextPaint, this.mTextDir, this.mBoring);
                if (metricsIsBoring != null) {
                    this.mBoring = metricsIsBoring;
                }
                z = false;
            } else {
                z = true;
            }
            if (metricsIsBoring == null || metricsIsBoring == UNKNOWN_BORING) {
                if (iDesired < 0) {
                    iDesired = (int) Math.ceil(Layout.getDesiredWidthWithLimit(this.mTransformed, 0, this.mTransformed.length(), this.mTextPaint, this.mTextDir, f));
                }
                iMax = iDesired;
                i3 = iMax;
            } else {
                iMax = metricsIsBoring.width;
                i3 = iDesired;
            }
            Drawables drawables = this.mDrawables;
            if (drawables != null) {
                iMax = Math.max(Math.max(iMax, drawables.mDrawableWidthTop), drawables.mDrawableWidthBottom);
            }
            int i8 = iMax;
            if (this.mHint == null) {
                i8 = i8;
            } else {
                int iDesired2 = (this.mHintLayout == null || this.mEllipsize != null) ? -1 : desired(this.mHintLayout);
                if (iDesired2 < 0 && (metricsIsBoring2 = BoringLayout.isBoring(this.mHint, this.mTextPaint, this.mTextDir, this.mHintBoring)) != null) {
                    this.mHintBoring = metricsIsBoring2;
                }
                if (metricsIsBoring2 == null || metricsIsBoring2 == UNKNOWN_BORING) {
                    if (iDesired2 < 0) {
                        iDesired2 = (int) Math.ceil(Layout.getDesiredWidthWithLimit(this.mHint, 0, this.mHint.length(), this.mTextPaint, this.mTextDir, f));
                    }
                    i8 = iDesired2;
                } else {
                    i8 = i8;
                    i8 = metricsIsBoring2.width;
                }
                if (i8 > i8) {
                    i8 = i8;
                }
            }
            int compoundPaddingLeft = i8 + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int iMin2 = this.mMaxWidthMode == 1 ? Math.min(compoundPaddingLeft, this.mMaxWidth * getLineHeight()) : Math.min(compoundPaddingLeft, this.mMaxWidth);
            int iMax2 = Math.max(this.mMinWidthMode == 1 ? Math.max(iMin2, this.mMinWidth * getLineHeight()) : Math.max(iMin2, this.mMinWidth), getSuggestedMinimumWidth());
            if (mode == Integer.MIN_VALUE) {
                iMin = Math.min(size, iMax2);
                metrics = metricsIsBoring2;
                i4 = i3;
            } else {
                iMin = iMax2;
                i4 = i3;
                metrics = metricsIsBoring2;
            }
        }
        BoringLayout.Metrics metrics2 = metricsIsBoring;
        int compoundPaddingLeft2 = (iMin - getCompoundPaddingLeft()) - getCompoundPaddingRight();
        int i9 = this.mHorizontallyScrolling ? 1048576 : compoundPaddingLeft2;
        int width = this.mHintLayout == null ? i9 : this.mHintLayout.getWidth();
        if (this.mLayout == null) {
            i5 = -1;
            i6 = 0;
            i7 = 1073741824;
            makeNewLayout(i9, i9, metrics2, metrics, (iMin - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
        } else {
            i5 = -1;
            i6 = 0;
            i7 = 1073741824;
            boolean z2 = (this.mLayout.getWidth() == i9 && width == i9 && this.mLayout.getEllipsizedWidth() == (iMin - getCompoundPaddingLeft()) - getCompoundPaddingRight()) ? false : true;
            boolean z3 = this.mHint == null && this.mEllipsize == null && i9 > this.mLayout.getWidth() && ((this.mLayout instanceof BoringLayout) || (z && i4 >= 0 && i4 <= i9));
            boolean z4 = (this.mMaxMode == this.mOldMaxMode && this.mMaximum == this.mOldMaximum) ? false : true;
            if (z2 || z4) {
                if (z4 || !z3) {
                    makeNewLayout(i9, i9, metrics2, metrics, (iMin - getCompoundPaddingLeft()) - getCompoundPaddingRight(), false);
                } else {
                    this.mLayout.increaseWidthTo(i9);
                }
            }
        }
        if (mode2 == i7) {
            this.mDesiredHeightAtMeasure = i5;
        } else {
            int desiredHeight = getDesiredHeight();
            this.mDesiredHeightAtMeasure = desiredHeight;
            size2 = mode2 == Integer.MIN_VALUE ? Math.min(desiredHeight, size2) : desiredHeight;
        }
        int compoundPaddingTop = (size2 - getCompoundPaddingTop()) - getCompoundPaddingBottom();
        if (this.mMaxMode == 1 && this.mLayout.getLineCount() > this.mMaximum) {
            compoundPaddingTop = Math.min(compoundPaddingTop, this.mLayout.getLineTop(this.mMaximum));
        }
        if (this.mMovement != null || this.mLayout.getWidth() > compoundPaddingLeft2 || this.mLayout.getHeight() > compoundPaddingTop) {
            registerForPreDraw();
        } else {
            scrollTo(i6, i6);
        }
        setMeasuredDimension(iMin, size2);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEventInternal(accessibilityEvent);
        CharSequence textForAccessibility = getTextForAccessibility();
        if (TextUtils.isEmpty(textForAccessibility)) {
            return;
        }
        accessibilityEvent.getText().add(textForAccessibility);
    }

    @Override // android.view.ViewTreeObserver.OnPreDrawListener
    public boolean onPreDraw() {
        if (this.mLayout == null) {
            assumeLayout();
        }
        if (this.mMovement != null) {
            int selectionEnd = getSelectionEnd();
            if (this.mEditor != null && this.mEditor.mSelectionModifierCursorController != null && this.mEditor.mSelectionModifierCursorController.isSelectionStartDragged()) {
                selectionEnd = getSelectionStart();
            }
            if (selectionEnd < 0 && (this.mGravity & 112) == 80) {
                selectionEnd = this.mText.length();
            }
            if (selectionEnd >= 0) {
                bringPointIntoView(selectionEnd);
            }
        } else {
            bringTextIntoView();
        }
        if (this.mEditor != null && this.mEditor.mCreatedWithASelection) {
            this.mEditor.refreshTextActionMode();
            this.mEditor.mCreatedWithASelection = false;
        }
        unregisterForPreDraw();
        return true;
    }

    public boolean onPrivateIMECommand(String str, Bundle bundle) {
        return false;
    }

    @Override // android.view.View
    public void onProvideAutofillStructure(ViewStructure viewStructure, int i) {
        super.onProvideAutofillStructure(viewStructure, i);
        onProvideAutoStructureForAssistOrAutofill(viewStructure, true);
    }

    @Override // android.view.View
    public void onProvideStructure(ViewStructure viewStructure) {
        super.onProvideStructure(viewStructure);
        onProvideAutoStructureForAssistOrAutofill(viewStructure, false);
    }

    @Override // android.view.View
    public void onResolveDrawables(int i) {
        if (this.mLastLayoutDirection == i) {
            return;
        }
        this.mLastLayoutDirection = i;
        if (this.mDrawables == null || !this.mDrawables.resolveWithLayoutDirection(i)) {
            return;
        }
        prepareDrawableForDisplay(this.mDrawables.mShowing[0]);
        prepareDrawableForDisplay(this.mDrawables.mShowing[2]);
        applyCompoundDrawableTint();
    }

    @Override // android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
        if (this.mSpannable != null && this.mLinksClickable) {
            int offsetForPosition = getOffsetForPosition(motionEvent.getX(i), motionEvent.getY(i));
            if (((ClickableSpan[]) this.mSpannable.getSpans(offsetForPosition, offsetForPosition, ClickableSpan.class)).length > 0) {
                return PointerIcon.getSystemIcon(this.mContext, 1002);
            }
        }
        return (isTextSelectable() || isTextEditable()) ? PointerIcon.getSystemIcon(this.mContext, 1008) : super.onResolvePointerIcon(motionEvent, i);
    }

    @Override // android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        if (!(parcelable instanceof SavedState)) {
            super.onRestoreInstanceState(parcelable);
            return;
        }
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (savedState.text != null) {
            setText(savedState.text);
        }
        if (savedState.selStart >= 0 && savedState.selEnd >= 0 && this.mSpannable != null) {
            int length = this.mText.length();
            if (savedState.selStart > length || savedState.selEnd > length) {
                Log.e(LOG_TAG, "Saved cursor position " + savedState.selStart + "/" + savedState.selEnd + " out of range for " + (savedState.text != null ? "(restored) " : "") + "text " + ((Object) this.mText));
            } else {
                Selection.setSelection(this.mSpannable, savedState.selStart, savedState.selEnd);
                if (savedState.frozenWithFocus) {
                    createEditorIfNeeded();
                    this.mEditor.mFrozenWithFocus = true;
                }
            }
        }
        if (savedState.error != null) {
            final CharSequence charSequence = savedState.error;
            post(new Runnable() { // from class: android.widget.TextView.1
                @Override // java.lang.Runnable
                public void run() {
                    if (TextView.this.mEditor == null || !TextView.this.mEditor.mErrorWasChanged) {
                        TextView.this.setError(charSequence);
                    }
                }
            });
        }
        if (savedState.editorState != null) {
            createEditorIfNeeded();
            this.mEditor.restoreInstanceState(savedState.editorState);
        }
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        TextDirectionHeuristic textDirectionHeuristic = getTextDirectionHeuristic();
        if (this.mTextDir != textDirectionHeuristic) {
            this.mTextDir = textDirectionHeuristic;
            if (this.mLayout != null) {
                checkForRelayout();
            }
        }
    }

    @Override // android.view.View
    public Parcelable onSaveInstanceState() {
        int selectionEnd;
        Parcelable parcelableOnSaveInstanceState = super.onSaveInstanceState();
        boolean freezesText = getFreezesText();
        int selectionStart = -1;
        boolean z = false;
        if (this.mText != null) {
            selectionStart = getSelectionStart();
            selectionEnd = getSelectionEnd();
            if (selectionStart >= 0 || selectionEnd >= 0) {
                z = true;
            }
        } else {
            selectionEnd = -1;
        }
        if (!freezesText && !z) {
            return parcelableOnSaveInstanceState;
        }
        SavedState savedState = new SavedState(parcelableOnSaveInstanceState);
        if (freezesText) {
            if (this.mText instanceof Spanned) {
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.mText);
                if (this.mEditor != null) {
                    removeMisspelledSpans(spannableStringBuilder);
                    spannableStringBuilder.removeSpan(this.mEditor.mSuggestionRangeSpan);
                }
                savedState.text = spannableStringBuilder;
            } else {
                savedState.text = this.mText.toString();
            }
        }
        if (z) {
            savedState.selStart = selectionStart;
            savedState.selEnd = selectionEnd;
        }
        if (isFocused() && selectionStart >= 0 && selectionEnd >= 0) {
            savedState.frozenWithFocus = true;
        }
        savedState.error = getError();
        if (this.mEditor != null) {
            savedState.editorState = this.mEditor.saveInstanceState();
        }
        return savedState;
    }

    @Override // android.view.View
    public void onScreenStateChanged(int i) {
        super.onScreenStateChanged(i);
        if (this.mEditor != null) {
            this.mEditor.onScreenStateChanged(i);
        }
    }

    @Override // android.view.View
    protected void onScrollChanged(int i, int i2, int i3, int i4) {
        super.onScrollChanged(i, i2, i3, i4);
        if (this.mEditor != null) {
            this.mEditor.onScrollChanged();
        }
    }

    protected void onSelectionChanged(int i, int i2) {
        sendAccessibilityEvent(8192);
    }

    protected void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public boolean onTextContextMenuItem(int i) {
        int iMax;
        int length = this.mText.length();
        if (isFocused()) {
            int selectionStart = getSelectionStart();
            int selectionEnd = getSelectionEnd();
            iMax = Math.max(0, Math.min(selectionStart, selectionEnd));
            length = Math.max(0, Math.max(selectionStart, selectionEnd));
        } else {
            iMax = 0;
        }
        if (i == 16908355) {
            requestAutofill();
            stopTextActionMode();
            return true;
        }
        switch (i) {
            case 16908319:
                boolean zHasSelection = hasSelection();
                selectAllText();
                if (this.mEditor != null && zHasSelection) {
                    this.mEditor.invalidateActionModeAsync();
                }
                return true;
            case 16908320:
                if (setPrimaryClip(ClipData.newPlainText(null, getTransformedText(iMax, length)))) {
                    deleteText_internal(iMax, length);
                } else {
                    Toast.makeText(getContext(), R.string.failed_to_copy_to_clipboard, 0).show();
                }
                return true;
            case 16908321:
                int selectionStart2 = getSelectionStart();
                int selectionEnd2 = getSelectionEnd();
                if (setPrimaryClip(ClipData.newPlainText(null, getTransformedText(Math.max(0, Math.min(selectionStart2, selectionEnd2)), Math.max(0, Math.max(selectionStart2, selectionEnd2)))))) {
                    stopTextActionMode();
                } else {
                    Toast.makeText(getContext(), R.string.failed_to_copy_to_clipboard, 0).show();
                }
                return true;
            case 16908322:
                paste(iMax, length, true);
                return true;
            default:
                switch (i) {
                    case 16908337:
                        paste(iMax, length, false);
                        return true;
                    case 16908338:
                        if (this.mEditor != null) {
                            this.mEditor.undo();
                        }
                        return true;
                    case 16908339:
                        if (this.mEditor != null) {
                            this.mEditor.redo();
                        }
                        return true;
                    case 16908340:
                        if (this.mEditor != null) {
                            this.mEditor.replace();
                        }
                        return true;
                    case 16908341:
                        shareSelectedText();
                        return true;
                    default:
                        return false;
                }
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (this.mEditor != null) {
            this.mEditor.onTouchEvent(motionEvent);
            if (this.mEditor.mSelectionModifierCursorController != null && this.mEditor.mSelectionModifierCursorController.isDragAcceleratorActive()) {
                return true;
            }
        }
        boolean zOnTouchEvent = super.onTouchEvent(motionEvent);
        if (this.mEditor != null && this.mEditor.mDiscardNextActionUp && actionMasked == 1) {
            this.mEditor.mDiscardNextActionUp = false;
            if (this.mEditor.mIsInsertionActionModeStartPending) {
                this.mEditor.startInsertionActionMode();
                this.mEditor.mIsInsertionActionModeStartPending = false;
            }
            return zOnTouchEvent;
        }
        boolean z = actionMasked == 1 && (this.mEditor == null || !this.mEditor.mIgnoreActionUpEvent) && isFocused();
        if ((this.mMovement != null || onCheckIsTextEditor()) && isEnabled() && (this.mText instanceof Spannable) && this.mLayout != null) {
            boolean zOnTouchEvent2 = this.mMovement != null ? this.mMovement.onTouchEvent(this, this.mSpannable, motionEvent) | false : false;
            boolean zIsTextSelectable = isTextSelectable();
            if (z && this.mLinksClickable && this.mAutoLinkMask != 0 && zIsTextSelectable) {
                ClickableSpan[] clickableSpanArr = (ClickableSpan[]) this.mSpannable.getSpans(getSelectionStart(), getSelectionEnd(), ClickableSpan.class);
                if (clickableSpanArr.length > 0) {
                    clickableSpanArr[0].onClick(this);
                    zOnTouchEvent2 = true;
                }
            }
            if (z && (isTextEditable() || zIsTextSelectable)) {
                InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
                viewClicked(inputMethodManagerPeekInstance);
                if (isTextEditable() && this.mEditor.mShowSoftInputOnFocus && inputMethodManagerPeekInstance != null) {
                    inputMethodManagerPeekInstance.showSoftInput(this, 0);
                }
                this.mEditor.onTouchUpEvent(motionEvent);
                zOnTouchEvent2 = true;
            }
            if (zOnTouchEvent2) {
                return true;
            }
        }
        return zOnTouchEvent;
    }

    @Override // android.view.View
    public boolean onTrackballEvent(MotionEvent motionEvent) {
        if (this.mMovement == null || this.mSpannable == null || this.mLayout == null || !this.mMovement.onTrackballEvent(this, this.mSpannable, motionEvent)) {
            return super.onTrackballEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View
    protected void onVisibilityChanged(View view, int i) {
        super.onVisibilityChanged(view, i);
        if (this.mEditor == null || i == 0) {
            return;
        }
        this.mEditor.hideCursorAndSpanControllers();
        stopTextActionMode();
    }

    @Override // android.view.View
    public void onWindowFocusChanged(boolean z) {
        super.onWindowFocusChanged(z);
        if (this.mEditor != null) {
            this.mEditor.onWindowFocusChanged(z);
        }
        startStopMarquee(z);
    }

    @Override // android.view.View
    public boolean performAccessibilityActionInternal(int i, Bundle bundle) {
        int length;
        if (this.mEditor != null && this.mEditor.mProcessTextIntentActionsHandler.performAccessibilityAction(i)) {
            return true;
        }
        if (i == 16) {
            return performAccessibilityActionClick(bundle);
        }
        if (i == 256 || i == 512) {
            ensureIterableTextForAccessibilitySelectable();
            return super.performAccessibilityActionInternal(i, bundle);
        }
        if (i == 16384) {
            return isFocused() && canCopy() && onTextContextMenuItem(16908321);
        }
        if (i == 32768) {
            return isFocused() && canPaste() && onTextContextMenuItem(16908322);
        }
        if (i == 65536) {
            return isFocused() && canCut() && onTextContextMenuItem(16908320);
        }
        if (i != 131072) {
            if (i != 2097152) {
                if (i != 268435456) {
                    return super.performAccessibilityActionInternal(i, bundle);
                }
                return isFocused() && canShare() && onTextContextMenuItem(16908341);
            }
            if (!isEnabled() || this.mBufferType != BufferType.EDITABLE) {
                return false;
            }
            setText(bundle != null ? bundle.getCharSequence("ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE") : null);
            if (this.mText != null && (length = this.mText.length()) > 0) {
                Selection.setSelection(this.mSpannable, length);
            }
            return true;
        }
        ensureIterableTextForAccessibilitySelectable();
        CharSequence iterableTextForAccessibility = getIterableTextForAccessibility();
        if (iterableTextForAccessibility == null) {
            return false;
        }
        int i2 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_START_INT", -1) : -1;
        int i3 = bundle != null ? bundle.getInt("ACTION_ARGUMENT_SELECTION_END_INT", -1) : -1;
        if (getSelectionStart() != i2 || getSelectionEnd() != i3) {
            if (i2 == i3 && i3 == -1) {
                Selection.removeSelection((Spannable) iterableTextForAccessibility);
                return true;
            }
            if (i2 >= 0 && i2 <= i3 && i3 <= iterableTextForAccessibility.length()) {
                Selection.setSelection((Spannable) iterableTextForAccessibility, i2, i3);
                if (this.mEditor != null) {
                    this.mEditor.startSelectionActionModeAsync(false);
                }
                return true;
            }
        }
        return false;
    }

    @Override // android.view.View
    public boolean performLongClick() {
        if (this.mEditor != null) {
            this.mEditor.mIsBeingLongClicked = true;
        }
        boolean zPerformLongClick = super.performLongClick();
        boolean z = zPerformLongClick;
        if (this.mEditor != null) {
            zPerformLongClick |= this.mEditor.performLongClick(zPerformLongClick);
            this.mEditor.mIsBeingLongClicked = false;
        }
        if (zPerformLongClick) {
            if (!z) {
                performHapticFeedback(0);
            }
            if (this.mEditor != null) {
                this.mEditor.mDiscardNextActionUp = true;
            }
        } else {
            MetricsLogger.action(this.mContext, MetricsProto.MetricsEvent.TEXT_LONGPRESS, 0);
        }
        return zPerformLongClick;
    }

    public void populateCharacterBounds(CursorAnchorInfo.Builder builder, int i, int i2, float f, float f2) {
        int i3 = i;
        int i4 = i2;
        int lineForOffset = this.mLayout.getLineForOffset(i3);
        int lineForOffset2 = this.mLayout.getLineForOffset(i4 - 1);
        while (lineForOffset <= lineForOffset2) {
            int lineStart = this.mLayout.getLineStart(lineForOffset);
            int lineEnd = this.mLayout.getLineEnd(lineForOffset);
            int iMax = Math.max(lineStart, i3);
            int iMin = Math.min(lineEnd, i4);
            boolean z = this.mLayout.getParagraphDirection(lineForOffset) == 1;
            float[] fArr = new float[iMin - iMax];
            this.mLayout.getPaint().getTextWidths(this.mTransformed, iMax, iMin, fArr);
            float lineTop = this.mLayout.getLineTop(lineForOffset);
            float lineBottom = this.mLayout.getLineBottom(lineForOffset);
            for (int i5 = iMax; i5 < iMin; i5++) {
                float f3 = fArr[i5 - iMax];
                boolean zIsRtlCharAt = this.mLayout.isRtlCharAt(i5);
                float primaryHorizontal = this.mLayout.getPrimaryHorizontal(i5);
                float secondaryHorizontal = this.mLayout.getSecondaryHorizontal(i5);
                if (!z) {
                    if (zIsRtlCharAt) {
                        secondaryHorizontal = primaryHorizontal - f3;
                    } else {
                        primaryHorizontal = secondaryHorizontal + f3;
                    }
                    float f4 = primaryHorizontal;
                    primaryHorizontal = secondaryHorizontal;
                    secondaryHorizontal = f4;
                } else if (zIsRtlCharAt) {
                    primaryHorizontal = secondaryHorizontal - f3;
                } else {
                    secondaryHorizontal = primaryHorizontal + f3;
                }
                float f5 = primaryHorizontal + f;
                float f6 = secondaryHorizontal + f;
                float f7 = lineTop + f2;
                float f8 = lineBottom + f2;
                boolean zIsPositionVisible = isPositionVisible(f5, f7);
                boolean zIsPositionVisible2 = isPositionVisible(f6, f8);
                int i6 = (zIsPositionVisible || zIsPositionVisible2) ? 1 : 0;
                if (!zIsPositionVisible || !zIsPositionVisible2) {
                    i6 |= 2;
                }
                builder.addCharacterBounds(i5, f5, f7, f6, f8, zIsRtlCharAt ? i6 | 4 : i6);
            }
            lineForOffset++;
            i3 = i;
            i4 = i2;
        }
    }

    void removeAdjacentSuggestionSpans(int i) {
        if (this.mText instanceof Editable) {
            Editable editable = (Editable) this.mText;
            SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) editable.getSpans(i, i, SuggestionSpan.class);
            int length = suggestionSpanArr.length;
            for (int i2 = 0; i2 < length; i2++) {
                int spanStart = editable.getSpanStart(suggestionSpanArr[i2]);
                int spanEnd = editable.getSpanEnd(suggestionSpanArr[i2]);
                if ((spanEnd == i || spanStart == i) && SpellChecker.haveWordBoundariesChanged(editable, i, i, spanStart, spanEnd)) {
                    editable.removeSpan(suggestionSpanArr[i2]);
                }
            }
        }
    }

    void removeMisspelledSpans(Spannable spannable) {
        SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannable.getSpans(0, spannable.length(), SuggestionSpan.class);
        for (int i = 0; i < suggestionSpanArr.length; i++) {
            int flags = suggestionSpanArr[i].getFlags();
            if ((flags & 1) != 0 && (flags & 2) != 0) {
                spannable.removeSpan(suggestionSpanArr[i]);
            }
        }
    }

    CharSequence removeSuggestionSpans(CharSequence charSequence) {
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        Spannable spannableNewSpannable = charSequence instanceof Spannable ? (Spannable) charSequence : this.mSpannableFactory.newSpannable(charSequence);
        SuggestionSpan[] suggestionSpanArr = (SuggestionSpan[]) spannableNewSpannable.getSpans(0, charSequence.length(), SuggestionSpan.class);
        if (suggestionSpanArr.length == 0) {
            return charSequence;
        }
        for (SuggestionSpan suggestionSpan : suggestionSpanArr) {
            spannableNewSpannable.removeSpan(suggestionSpan);
        }
        return spannableNewSpannable;
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        int iIndexOf;
        if (this.mListeners == null || (iIndexOf = this.mListeners.indexOf(textWatcher)) < 0) {
            return;
        }
        this.mListeners.remove(iIndexOf);
    }

    void replaceSelectionWithText(CharSequence charSequence) {
        ((Editable) this.mText).replace(getSelectionStart(), getSelectionEnd(), charSequence);
    }

    protected void replaceText_internal(int i, int i2, CharSequence charSequence) {
        ((Editable) this.mText).replace(i, i2, charSequence);
    }

    public boolean requestActionMode(TextLinks.TextLinkSpan textLinkSpan) {
        Preconditions.checkNotNull(textLinkSpan);
        if (!(this.mText instanceof Spanned)) {
            return false;
        }
        int spanStart = ((Spanned) this.mText).getSpanStart(textLinkSpan);
        int spanEnd = ((Spanned) this.mText).getSpanEnd(textLinkSpan);
        if (spanStart < 0 || spanEnd > this.mText.length() || spanStart >= spanEnd) {
            return false;
        }
        createEditorIfNeeded();
        this.mEditor.startLinkActionModeAsync(spanStart, spanEnd);
        return true;
    }

    public void resetErrorChangedFlag() {
        if (this.mEditor != null) {
            this.mEditor.mErrorWasChanged = false;
        }
    }

    @Override // android.view.View
    protected void resetResolvedDrawables() {
        super.resetResolvedDrawables();
        this.mLastLayoutDirection = -1;
    }

    boolean selectAllText() {
        if (this.mEditor != null) {
            hideFloatingToolbar(500);
        }
        int length = this.mText.length();
        Selection.setSelection(this.mSpannable, 0, length);
        return length > 0;
    }

    @Override // android.view.View
    public void sendAccessibilityEventInternal(int i) {
        if (i == 32768 && this.mEditor != null) {
            this.mEditor.mProcessTextIntentActionsHandler.initializeAccessibilityActions();
        }
        super.sendAccessibilityEventInternal(i);
    }

    void sendAccessibilityEventTypeViewTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16);
        accessibilityEventObtain.setFromIndex(i);
        accessibilityEventObtain.setRemovedCount(i2);
        accessibilityEventObtain.setAddedCount(i3);
        accessibilityEventObtain.setBeforeText(charSequence);
        sendAccessibilityEventUnchecked(accessibilityEventObtain);
    }

    @Override // android.view.View, android.view.accessibility.AccessibilityEventSource
    public void sendAccessibilityEventUnchecked(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent.getEventType() == 4096) {
            return;
        }
        super.sendAccessibilityEventUnchecked(accessibilityEvent);
    }

    void sendAfterTextChanged(Editable editable) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> arrayList = this.mListeners;
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                arrayList.get(i).afterTextChanged(editable);
            }
        }
        notifyAutoFillManagerAfterTextChangedIfNeeded();
        hideErrorIfUnchanged();
    }

    void sendOnTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        if (this.mListeners != null) {
            ArrayList<TextWatcher> arrayList = this.mListeners;
            int size = arrayList.size();
            for (int i4 = 0; i4 < size; i4++) {
                arrayList.get(i4).onTextChanged(charSequence, i, i2, i3);
            }
        }
        if (this.mEditor != null) {
            this.mEditor.sendOnTextChanged(i, i2, i3);
        }
    }

    @Override // android.view.View
    public void setAccessibilitySelection(int i, int i2) {
        if (getAccessibilitySelectionStart() == i && getAccessibilitySelectionEnd() == i2) {
            return;
        }
        CharSequence iterableTextForAccessibility = getIterableTextForAccessibility();
        if (Math.min(i, i2) < 0 || Math.max(i, i2) > iterableTextForAccessibility.length()) {
            Selection.removeSelection((Spannable) iterableTextForAccessibility);
        } else {
            Selection.setSelection((Spannable) iterableTextForAccessibility, i, i2);
        }
        if (this.mEditor != null) {
            this.mEditor.hideCursorAndSpanControllers();
            this.mEditor.stopTextActionMode();
        }
    }

    public void setAllCaps(boolean z) {
        if (z) {
            setTransformationMethod(new AllCapsTransformationMethod(getContext()));
        } else {
            setTransformationMethod(null);
        }
    }

    @RemotableViewMethod
    public final void setAutoLinkMask(int i) {
        this.mAutoLinkMask = i;
    }

    public void setAutoSizeTextTypeUniformWithConfiguration(int i, int i2, int i3, int i4) {
        if (supportsAutoSizeText()) {
            DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
            validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(i4, i, displayMetrics), TypedValue.applyDimension(i4, i2, displayMetrics), TypedValue.applyDimension(i4, i3, displayMetrics));
            if (setupAutoSizeText()) {
                autoSizeText();
                invalidate();
            }
        }
    }

    public void setAutoSizeTextTypeUniformWithPresetSizes(int[] iArr, int i) {
        if (supportsAutoSizeText()) {
            int length = iArr.length;
            if (length > 0) {
                int[] iArrCopyOf = new int[length];
                if (i == 0) {
                    iArrCopyOf = Arrays.copyOf(iArr, length);
                } else {
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    for (int i2 = 0; i2 < length; i2++) {
                        iArrCopyOf[i2] = Math.round(TypedValue.applyDimension(i, iArr[i2], displayMetrics));
                    }
                }
                this.mAutoSizeTextSizesInPx = cleanupAutoSizePresetSizes(iArrCopyOf);
                if (!setupAutoSizeUniformPresetSizesConfiguration()) {
                    throw new IllegalArgumentException("None of the preset sizes is valid: " + Arrays.toString(iArr));
                }
            } else {
                this.mHasPresetAutoSizeValues = false;
            }
            if (setupAutoSizeText()) {
                autoSizeText();
                invalidate();
            }
        }
    }

    public void setAutoSizeTextTypeWithDefaults(int i) {
        if (supportsAutoSizeText()) {
            switch (i) {
                case 0:
                    clearAutoSizeConfiguration();
                    return;
                case 1:
                    DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                    validateAndSetAutoSizeTextTypeUniformConfiguration(TypedValue.applyDimension(2, 12.0f, displayMetrics), TypedValue.applyDimension(2, 112.0f, displayMetrics), 1.0f);
                    if (setupAutoSizeText()) {
                        autoSizeText();
                        invalidate();
                        return;
                    }
                    return;
                default:
                    throw new IllegalArgumentException("Unknown auto-size text type: " + i);
            }
        }
    }

    public void setBreakStrategy(int i) {
        this.mBreakStrategy = i;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setCompoundDrawablePadding(int i) {
        Drawables drawables = this.mDrawables;
        if (i != 0) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            drawables.mDrawablePadding = i;
        } else if (drawables != null) {
            drawables.mDrawablePadding = i;
        }
        invalidate();
        requestLayout();
    }

    public void setCompoundDrawableTintList(ColorStateList colorStateList) {
        if (this.mDrawables == null) {
            this.mDrawables = new Drawables(getContext());
        }
        this.mDrawables.mTintList = colorStateList;
        this.mDrawables.mHasTint = true;
        applyCompoundDrawableTint();
    }

    public void setCompoundDrawableTintMode(PorterDuff.Mode mode) {
        if (this.mDrawables == null) {
            this.mDrawables = new Drawables(getContext());
        }
        this.mDrawables.mTintMode = mode;
        this.mDrawables.mHasTintMode = true;
        applyCompoundDrawableTint();
    }

    public void setCompoundDrawables(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            if (drawables.mDrawableStart != null) {
                drawables.mDrawableStart.setCallback(null);
            }
            drawables.mDrawableStart = null;
            if (drawables.mDrawableEnd != null) {
                drawables.mDrawableEnd.setCallback(null);
            }
            drawables.mDrawableEnd = null;
            drawables.mDrawableHeightStart = 0;
            drawables.mDrawableSizeStart = 0;
            drawables.mDrawableHeightEnd = 0;
            drawables.mDrawableSizeEnd = 0;
        }
        if ((drawable == null && drawable2 == null && drawable3 == null && drawable4 == null) ? false : true) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = false;
            if (drawables.mShowing[0] != drawable && drawables.mShowing[0] != null) {
                drawables.mShowing[0].setCallback(null);
            }
            drawables.mShowing[0] = drawable;
            if (drawables.mShowing[1] != drawable2 && drawables.mShowing[1] != null) {
                drawables.mShowing[1].setCallback(null);
            }
            drawables.mShowing[1] = drawable2;
            if (drawables.mShowing[2] != drawable3 && drawables.mShowing[2] != null) {
                drawables.mShowing[2].setCallback(null);
            }
            drawables.mShowing[2] = drawable3;
            if (drawables.mShowing[3] != drawable4 && drawables.mShowing[3] != null) {
                drawables.mShowing[3].setCallback(null);
            }
            drawables.mShowing[3] = drawable4;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableSizeLeft = rect.width();
                drawables.mDrawableHeightLeft = rect.height();
            } else {
                drawables.mDrawableHeightLeft = 0;
                drawables.mDrawableSizeLeft = 0;
            }
            if (drawable3 != null) {
                drawable3.setState(drawableState);
                drawable3.copyBounds(rect);
                drawable3.setCallback(this);
                drawables.mDrawableSizeRight = rect.width();
                drawables.mDrawableHeightRight = rect.height();
            } else {
                drawables.mDrawableHeightRight = 0;
                drawables.mDrawableSizeRight = 0;
            }
            if (drawable2 != null) {
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableSizeTop = rect.height();
                drawables.mDrawableWidthTop = rect.width();
            } else {
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
            }
            if (drawable4 != null) {
                drawable4.setState(drawableState);
                drawable4.copyBounds(rect);
                drawable4.setCallback(this);
                drawables.mDrawableSizeBottom = rect.height();
                drawables.mDrawableWidthBottom = rect.width();
            } else {
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        } else if (drawables != null) {
            if (drawables.hasMetadata()) {
                for (int length = drawables.mShowing.length - 1; length >= 0; length--) {
                    if (drawables.mShowing[length] != null) {
                        drawables.mShowing[length].setCallback(null);
                    }
                    drawables.mShowing[length] = null;
                }
                drawables.mDrawableHeightLeft = 0;
                drawables.mDrawableSizeLeft = 0;
                drawables.mDrawableHeightRight = 0;
                drawables.mDrawableSizeRight = 0;
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            } else {
                this.mDrawables = null;
            }
        }
        if (drawables != null) {
            drawables.mDrawableLeftInitial = drawable;
            drawables.mDrawableRightInitial = drawable3;
        }
        resetResolvedDrawables();
        resolveDrawables();
        applyCompoundDrawableTint();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelative(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        Drawables drawables = this.mDrawables;
        if (drawables != null) {
            if (drawables.mShowing[0] != null) {
                drawables.mShowing[0].setCallback(null);
            }
            Drawable[] drawableArr = drawables.mShowing;
            drawables.mDrawableLeftInitial = null;
            drawableArr[0] = null;
            if (drawables.mShowing[2] != null) {
                drawables.mShowing[2].setCallback(null);
            }
            Drawable[] drawableArr2 = drawables.mShowing;
            drawables.mDrawableRightInitial = null;
            drawableArr2[2] = null;
            drawables.mDrawableHeightLeft = 0;
            drawables.mDrawableSizeLeft = 0;
            drawables.mDrawableHeightRight = 0;
            drawables.mDrawableSizeRight = 0;
        }
        if ((drawable == null && drawable2 == null && drawable3 == null && drawable4 == null) ? false : true) {
            if (drawables == null) {
                drawables = new Drawables(getContext());
                this.mDrawables = drawables;
            }
            this.mDrawables.mOverride = true;
            if (drawables.mDrawableStart != drawable && drawables.mDrawableStart != null) {
                drawables.mDrawableStart.setCallback(null);
            }
            drawables.mDrawableStart = drawable;
            if (drawables.mShowing[1] != drawable2 && drawables.mShowing[1] != null) {
                drawables.mShowing[1].setCallback(null);
            }
            drawables.mShowing[1] = drawable2;
            if (drawables.mDrawableEnd != drawable3 && drawables.mDrawableEnd != null) {
                drawables.mDrawableEnd.setCallback(null);
            }
            drawables.mDrawableEnd = drawable3;
            if (drawables.mShowing[3] != drawable4 && drawables.mShowing[3] != null) {
                drawables.mShowing[3].setCallback(null);
            }
            drawables.mShowing[3] = drawable4;
            Rect rect = drawables.mCompoundRect;
            int[] drawableState = getDrawableState();
            if (drawable != null) {
                drawable.setState(drawableState);
                drawable.copyBounds(rect);
                drawable.setCallback(this);
                drawables.mDrawableSizeStart = rect.width();
                drawables.mDrawableHeightStart = rect.height();
            } else {
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
            }
            if (drawable3 != null) {
                drawable3.setState(drawableState);
                drawable3.copyBounds(rect);
                drawable3.setCallback(this);
                drawables.mDrawableSizeEnd = rect.width();
                drawables.mDrawableHeightEnd = rect.height();
            } else {
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
            }
            if (drawable2 != null) {
                drawable2.setState(drawableState);
                drawable2.copyBounds(rect);
                drawable2.setCallback(this);
                drawables.mDrawableSizeTop = rect.height();
                drawables.mDrawableWidthTop = rect.width();
            } else {
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
            }
            if (drawable4 != null) {
                drawable4.setState(drawableState);
                drawable4.copyBounds(rect);
                drawable4.setCallback(this);
                drawables.mDrawableSizeBottom = rect.height();
                drawables.mDrawableWidthBottom = rect.width();
            } else {
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            }
        } else if (drawables != null) {
            if (drawables.hasMetadata()) {
                if (drawables.mDrawableStart != null) {
                    drawables.mDrawableStart.setCallback(null);
                }
                drawables.mDrawableStart = null;
                if (drawables.mShowing[1] != null) {
                    drawables.mShowing[1].setCallback(null);
                }
                drawables.mShowing[1] = null;
                if (drawables.mDrawableEnd != null) {
                    drawables.mDrawableEnd.setCallback(null);
                }
                drawables.mDrawableEnd = null;
                if (drawables.mShowing[3] != null) {
                    drawables.mShowing[3].setCallback(null);
                }
                drawables.mShowing[3] = null;
                drawables.mDrawableHeightStart = 0;
                drawables.mDrawableSizeStart = 0;
                drawables.mDrawableHeightEnd = 0;
                drawables.mDrawableSizeEnd = 0;
                drawables.mDrawableWidthTop = 0;
                drawables.mDrawableSizeTop = 0;
                drawables.mDrawableWidthBottom = 0;
                drawables.mDrawableSizeBottom = 0;
            } else {
                this.mDrawables = null;
            }
        }
        resetResolvedDrawables();
        resolveDrawables();
        invalidate();
        requestLayout();
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Context context = getContext();
        setCompoundDrawablesRelativeWithIntrinsicBounds(i != 0 ? context.getDrawable(i) : null, i2 != 0 ? context.getDrawable(i2) : null, i3 != 0 ? context.getDrawable(i3) : null, i4 != 0 ? context.getDrawable(i4) : null);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesRelativeWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if (drawable3 != null) {
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
        }
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
        }
        if (drawable4 != null) {
            drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight());
        }
        setCompoundDrawablesRelative(drawable, drawable2, drawable3, drawable4);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(int i, int i2, int i3, int i4) {
        Context context = getContext();
        setCompoundDrawablesWithIntrinsicBounds(i != 0 ? context.getDrawable(i) : null, i2 != 0 ? context.getDrawable(i2) : null, i3 != 0 ? context.getDrawable(i3) : null, i4 != 0 ? context.getDrawable(i4) : null);
    }

    @RemotableViewMethod
    public void setCompoundDrawablesWithIntrinsicBounds(Drawable drawable, Drawable drawable2, Drawable drawable3, Drawable drawable4) {
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        }
        if (drawable3 != null) {
            drawable3.setBounds(0, 0, drawable3.getIntrinsicWidth(), drawable3.getIntrinsicHeight());
        }
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, drawable2.getIntrinsicWidth(), drawable2.getIntrinsicHeight());
        }
        if (drawable4 != null) {
            drawable4.setBounds(0, 0, drawable4.getIntrinsicWidth(), drawable4.getIntrinsicHeight());
        }
        setCompoundDrawables(drawable, drawable2, drawable3, drawable4);
    }

    protected void setCursorPosition_internal(int i, int i2) {
        Selection.setSelection((Editable) this.mText, i, i2);
    }

    @RemotableViewMethod
    public void setCursorVisible(boolean z) {
        if (z && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        if (this.mEditor.mCursorVisible != z) {
            this.mEditor.mCursorVisible = z;
            invalidate();
            this.mEditor.makeBlink();
            this.mEditor.prepareCursorControllers();
        }
    }

    public void setCustomInsertionActionModeCallback(ActionMode.Callback callback) {
        createEditorIfNeeded();
        this.mEditor.mCustomInsertionActionModeCallback = callback;
    }

    public void setCustomSelectionActionModeCallback(ActionMode.Callback callback) {
        createEditorIfNeeded();
        this.mEditor.mCustomSelectionActionModeCallback = callback;
    }

    public final void setEditableFactory(Editable.Factory factory) {
        this.mEditableFactory = factory;
        setText(this.mText);
    }

    public void setElegantTextHeight(boolean z) {
        if (z != this.mTextPaint.isElegantTextHeight()) {
            this.mTextPaint.setElegantTextHeight(z);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setEllipsize(TextUtils.TruncateAt truncateAt) {
        if (this.mEllipsize != truncateAt) {
            this.mEllipsize = truncateAt;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    @RemotableViewMethod
    public void setEms(int i) {
        this.mMinWidth = i;
        this.mMaxWidth = i;
        this.mMinWidthMode = 1;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setEnabled(boolean z) {
        InputMethodManager inputMethodManagerPeekInstance;
        InputMethodManager inputMethodManagerPeekInstance2;
        if (z == isEnabled()) {
            return;
        }
        if (!z && (inputMethodManagerPeekInstance2 = InputMethodManager.peekInstance()) != null && inputMethodManagerPeekInstance2.isActive(this)) {
            inputMethodManagerPeekInstance2.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        super.setEnabled(z);
        if (z && (inputMethodManagerPeekInstance = InputMethodManager.peekInstance()) != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
        if (this.mEditor != null) {
            this.mEditor.invalidateTextDisplayList();
            this.mEditor.prepareCursorControllers();
            this.mEditor.makeBlink();
        }
    }

    @RemotableViewMethod
    public void setError(CharSequence charSequence) {
        if (charSequence == null) {
            setError(null, null);
            return;
        }
        Drawable drawable = getContext().getDrawable(R.drawable.indicator_input_error);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        setError(charSequence, drawable);
    }

    public void setError(CharSequence charSequence, Drawable drawable) {
        createEditorIfNeeded();
        this.mEditor.setError(charSequence, drawable);
        notifyViewAccessibilityStateChangedIfNeeded(0);
    }

    public void setExtractedText(ExtractedText extractedText) {
        int i;
        Editable editableText = getEditableText();
        if (extractedText.text != null) {
            if (editableText == null) {
                setText(extractedText.text, BufferType.EDITABLE);
            } else {
                int length = editableText.length();
                if (extractedText.partialStartOffset >= 0) {
                    length = editableText.length();
                    int i2 = extractedText.partialStartOffset;
                    if (i2 > length) {
                        i2 = length;
                    }
                    int i3 = extractedText.partialEndOffset;
                    if (i3 > length) {
                        i = i2;
                    } else {
                        i = i2;
                        length = i3;
                    }
                } else {
                    i = 0;
                }
                removeParcelableSpans(editableText, i, length);
                if (!TextUtils.equals(editableText.subSequence(i, length), extractedText.text)) {
                    editableText.replace(i, length, extractedText.text);
                } else if (extractedText.text instanceof Spanned) {
                    TextUtils.copySpansFrom((Spanned) extractedText.text, 0, length - i, Object.class, editableText, i);
                }
            }
        }
        Spannable spannable = (Spannable) getText();
        int length2 = spannable.length();
        int i4 = extractedText.selectionStart;
        if (i4 < 0) {
            i4 = 0;
        } else if (i4 > length2) {
            i4 = length2;
        }
        int i5 = extractedText.selectionEnd;
        if (i5 < 0) {
            length2 = 0;
        } else if (i5 <= length2) {
            length2 = i5;
        }
        Selection.setSelection(spannable, i4, length2);
        if ((extractedText.flags & 2) != 0) {
            MetaKeyKeyListener.startSelecting(this, spannable);
        } else {
            MetaKeyKeyListener.stopSelecting(this, spannable);
        }
        setHintInternal(extractedText.hint);
    }

    public void setExtracting(ExtractedTextRequest extractedTextRequest) {
        if (this.mEditor.mInputMethodState != null) {
            this.mEditor.mInputMethodState.mExtractedTextRequest = extractedTextRequest;
        }
        this.mEditor.hideCursorAndSpanControllers();
        stopTextActionMode();
        if (this.mEditor.mSelectionModifierCursorController != null) {
            this.mEditor.mSelectionModifierCursorController.resetTouchOffsets();
        }
    }

    public void setFallbackLineSpacing(boolean z) {
        if (this.mUseFallbackLineSpacing != z) {
            this.mUseFallbackLineSpacing = z;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setFilters(InputFilter[] inputFilterArr) {
        if (inputFilterArr == null) {
            throw new IllegalArgumentException();
        }
        this.mFilters = inputFilterArr;
        if (this.mText instanceof Editable) {
            setFilters((Editable) this.mText, inputFilterArr);
        }
    }

    public void setFirstBaselineToTopHeight(int i) {
        Preconditions.checkArgumentNonnegative(i);
        Paint.FontMetricsInt fontMetricsInt = getPaint().getFontMetricsInt();
        int i2 = getIncludeFontPadding() ? fontMetricsInt.top : fontMetricsInt.ascent;
        if (i > Math.abs(i2)) {
            setPadding(getPaddingLeft(), i - (-i2), getPaddingRight(), getPaddingBottom());
        }
    }

    @RemotableViewMethod
    public void setFontFeatureSettings(String str) {
        if (str != this.mTextPaint.getFontFeatureSettings()) {
            this.mTextPaint.setFontFeatureSettings(str);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public boolean setFontVariationSettings(String str) {
        String fontVariationSettings = this.mTextPaint.getFontVariationSettings();
        if (str == fontVariationSettings) {
            return true;
        }
        if (str != null && str.equals(fontVariationSettings)) {
            return true;
        }
        boolean fontVariationSettings2 = this.mTextPaint.setFontVariationSettings(str);
        if (fontVariationSettings2 && this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
        return fontVariationSettings2;
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        if (this.mEditor != null) {
            this.mEditor.setFrame();
        }
        restartMarqueeIfNeeded();
        return frame;
    }

    @RemotableViewMethod
    public void setFreezesText(boolean z) {
        this.mFreezesText = z;
    }

    public void setGravity(int i) {
        if ((i & 8388615) == 0) {
            i |= 8388611;
        }
        if ((i & 112) == 0) {
            i |= 48;
        }
        boolean z = (i & 8388615) != (8388615 & this.mGravity);
        if (i != this.mGravity) {
            invalidate();
        }
        this.mGravity = i;
        if (this.mLayout == null || !z) {
            return;
        }
        makeNewLayout(this.mLayout.getWidth(), this.mHintLayout != null ? this.mHintLayout.getWidth() : 0, UNKNOWN_BORING, UNKNOWN_BORING, ((this.mRight - this.mLeft) - getCompoundPaddingLeft()) - getCompoundPaddingRight(), true);
    }

    @RemotableViewMethod
    public void setHeight(int i) {
        this.mMinimum = i;
        this.mMaximum = i;
        this.mMinMode = 2;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setHighlightColor(int i) {
        if (this.mHighlightColor != i) {
            this.mHighlightColor = i;
            invalidate();
        }
    }

    @RemotableViewMethod
    public final void setHint(int i) {
        setHint(getContext().getResources().getText(i));
    }

    @RemotableViewMethod
    public final void setHint(CharSequence charSequence) {
        setHintInternal(charSequence);
        if (this.mEditor == null || !isInputMethodTarget()) {
            return;
        }
        this.mEditor.reportExtractedText();
    }

    @RemotableViewMethod
    public final void setHintTextColor(int i) {
        this.mHintTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    public final void setHintTextColor(ColorStateList colorStateList) {
        this.mHintTextColor = colorStateList;
        updateTextColors();
    }

    public void setHorizontallyScrolling(boolean z) {
        if (this.mHorizontallyScrolling != z) {
            this.mHorizontallyScrolling = z;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setHyphenationFrequency(int i) {
        this.mHyphenationFrequency = i;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setImeActionLabel(CharSequence charSequence, int i) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeActionLabel = charSequence;
        this.mEditor.mInputContentType.imeActionId = i;
    }

    public void setImeHintLocales(LocaleList localeList) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeHintLocales = localeList;
        if (this.mUseInternationalizedInput) {
            changeListenerLocaleTo(localeList == null ? null : localeList.get(0));
        }
    }

    public void setImeOptions(int i) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.imeOptions = i;
    }

    public void setIncludeFontPadding(boolean z) {
        if (this.mIncludePad != z) {
            this.mIncludePad = z;
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setInputExtras(int i) throws XmlPullParserException, IOException {
        createEditorIfNeeded();
        XmlResourceParser xml = getResources().getXml(i);
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.extras = new Bundle();
        getResources().parseBundleExtras(xml, this.mEditor.mInputContentType.extras);
    }

    public void setInputType(int i) {
        boolean zIsPasswordInputType = isPasswordInputType(getInputType());
        boolean zIsVisiblePasswordInputType = isVisiblePasswordInputType(getInputType());
        boolean z = false;
        setInputType(i, false);
        boolean zIsPasswordInputType2 = isPasswordInputType(i);
        boolean zIsVisiblePasswordInputType2 = isVisiblePasswordInputType(i);
        if (zIsPasswordInputType2) {
            setTransformationMethod(PasswordTransformationMethod.getInstance());
            setTypefaceFromAttrs(null, null, 3, 0, -1);
        } else if (zIsVisiblePasswordInputType2) {
            z = this.mTransformation == PasswordTransformationMethod.getInstance();
            setTypefaceFromAttrs(null, null, 3, 0, -1);
        } else if (zIsPasswordInputType || zIsVisiblePasswordInputType) {
            setTypefaceFromAttrs(null, null, -1, 0, -1);
            if (this.mTransformation == PasswordTransformationMethod.getInstance()) {
                z = true;
            }
        }
        boolean z2 = !isMultilineInputType(i);
        if (this.mSingleLine != z2 || z) {
            applySingleLine(z2, !zIsPasswordInputType2, true);
        }
        if (!isSuggestionsEnabled()) {
            setTextInternal(removeSuggestionSpans(this.mText));
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
    }

    public void setJustificationMode(int i) {
        this.mJustificationMode = i;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setKeyListener(KeyListener keyListener) {
        this.mListenerChanged = true;
        setKeyListenerOnly(keyListener);
        fixFocusableAndClickableSettings();
        if (keyListener != null) {
            createEditorIfNeeded();
            setInputTypeFromEditor();
        } else if (this.mEditor != null) {
            this.mEditor.mInputType = 0;
        }
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            inputMethodManagerPeekInstance.restartInput(this);
        }
    }

    public void setLastBaselineToBottomHeight(int i) {
        Preconditions.checkArgumentNonnegative(i);
        Paint.FontMetricsInt fontMetricsInt = getPaint().getFontMetricsInt();
        int i2 = getIncludeFontPadding() ? fontMetricsInt.bottom : fontMetricsInt.descent;
        if (i > Math.abs(i2)) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), i - i2);
        }
    }

    @RemotableViewMethod
    public void setLetterSpacing(float f) {
        if (f != this.mTextPaint.getLetterSpacing()) {
            this.mTextPaint.setLetterSpacing(f);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setLineHeight(int i) {
        Preconditions.checkArgumentNonnegative(i);
        int fontMetricsInt = getPaint().getFontMetricsInt(null);
        if (i != fontMetricsInt) {
            setLineSpacing(i - fontMetricsInt, 1.0f);
        }
    }

    public void setLineSpacing(float f, float f2) {
        if (this.mSpacingAdd == f && this.mSpacingMult == f2) {
            return;
        }
        this.mSpacingAdd = f;
        this.mSpacingMult = f2;
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setLines(int i) {
        this.mMinimum = i;
        this.mMaximum = i;
        this.mMinMode = 1;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public final void setLinkTextColor(int i) {
        this.mLinkTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    public final void setLinkTextColor(ColorStateList colorStateList) {
        this.mLinkTextColor = colorStateList;
        updateTextColors();
    }

    @RemotableViewMethod
    public final void setLinksClickable(boolean z) {
        this.mLinksClickable = z;
    }

    public void setMarqueeRepeatLimit(int i) {
        this.mMarqueeRepeatLimit = i;
    }

    @RemotableViewMethod
    public void setMaxEms(int i) {
        this.mMaxWidth = i;
        this.mMaxWidthMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMaxHeight(int i) {
        this.mMaximum = i;
        this.mMaxMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMaxLines(int i) {
        this.mMaximum = i;
        this.mMaxMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinEms(int i) {
        this.mMinWidth = i;
        this.mMinWidthMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinHeight(int i) {
        this.mMinimum = i;
        this.mMinMode = 2;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinLines(int i) {
        this.mMinimum = i;
        this.mMinMode = 1;
        requestLayout();
        invalidate();
    }

    @RemotableViewMethod
    public void setMinWidth(int i) {
        this.mMinWidth = i;
        this.mMinWidthMode = 2;
        requestLayout();
        invalidate();
    }

    public final void setMovementMethod(MovementMethod movementMethod) {
        if (this.mMovement != movementMethod) {
            this.mMovement = movementMethod;
            if (movementMethod != null && this.mSpannable == null) {
                setText(this.mText);
            }
            fixFocusableAndClickableSettings();
            if (this.mEditor != null) {
                this.mEditor.prepareCursorControllers();
            }
        }
    }

    public void setOnEditorActionListener(OnEditorActionListener onEditorActionListener) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.onEditorActionListener = onEditorActionListener;
    }

    @Override // android.view.View
    public void setPadding(int i, int i2, int i3, int i4) {
        if (i != this.mPaddingLeft || i3 != this.mPaddingRight || i2 != this.mPaddingTop || i4 != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPadding(i, i2, i3, i4);
        invalidate();
    }

    @Override // android.view.View
    public void setPaddingRelative(int i, int i2, int i3, int i4) {
        if (i != getPaddingStart() || i3 != getPaddingEnd() || i2 != this.mPaddingTop || i4 != this.mPaddingBottom) {
            nullLayouts();
        }
        super.setPaddingRelative(i, i2, i3, i4);
        invalidate();
    }

    @RemotableViewMethod
    public void setPaintFlags(int i) {
        if (this.mTextPaint.getFlags() != i) {
            this.mTextPaint.setFlags(i);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setPrivateImeOptions(String str) {
        createEditorIfNeeded();
        this.mEditor.createInputContentTypeIfNeeded();
        this.mEditor.mInputContentType.privateImeOptions = str;
    }

    public void setRawInputType(int i) {
        if (i == 0 && this.mEditor == null) {
            return;
        }
        createEditorIfNeeded();
        this.mEditor.mInputType = i;
    }

    public final void setRestrictedAcrossUser(boolean z) {
        this.mIsRestrictedAcrossUser = z;
    }

    public void setScroller(Scroller scroller) {
        this.mScroller = scroller;
    }

    @RemotableViewMethod
    public void setSelectAllOnFocus(boolean z) {
        createEditorIfNeeded();
        this.mEditor.mSelectAllOnFocus = z;
        if (!z || (this.mText instanceof Spannable)) {
            return;
        }
        setText(this.mText, BufferType.SPANNABLE);
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        boolean zIsSelected = isSelected();
        super.setSelected(z);
        if (z == zIsSelected || this.mEllipsize != TextUtils.TruncateAt.MARQUEE) {
            return;
        }
        if (z) {
            startMarquee();
        } else {
            stopMarquee();
        }
    }

    public void setShadowLayer(float f, float f2, float f3, int i) {
        this.mTextPaint.setShadowLayer(f, f2, f3, i);
        this.mShadowRadius = f;
        this.mShadowDx = f2;
        this.mShadowDy = f3;
        this.mShadowColor = i;
        if (this.mEditor != null) {
            this.mEditor.invalidateTextDisplayList();
            this.mEditor.invalidateHandlesAndActionMode();
        }
        invalidate();
    }

    @RemotableViewMethod
    public final void setShowSoftInputOnFocus(boolean z) {
        createEditorIfNeeded();
        this.mEditor.mShowSoftInputOnFocus = z;
    }

    public void setSingleLine() {
        setSingleLine(true);
    }

    @RemotableViewMethod
    public void setSingleLine(boolean z) {
        setInputTypeSingleLine(z);
        applySingleLine(z, true, true);
    }

    protected void setSpan_internal(Object obj, int i, int i2, int i3) {
        ((Editable) this.mText).setSpan(obj, i, i2, i3);
    }

    public final void setSpannableFactory(Spannable.Factory factory) {
        this.mSpannableFactory = factory;
        setText(this.mText);
    }

    @RemotableViewMethod
    public final void setText(int i) {
        setText(getContext().getResources().getText(i));
        this.mTextSetFromXmlOrResourceId = true;
        this.mTextId = i;
    }

    public final void setText(int i, BufferType bufferType) {
        setText(getContext().getResources().getText(i), bufferType);
        this.mTextSetFromXmlOrResourceId = true;
        this.mTextId = i;
    }

    @RemotableViewMethod
    public final void setText(CharSequence charSequence) {
        setText(charSequence, this.mBufferType);
    }

    public void setText(CharSequence charSequence, BufferType bufferType) {
        setText(charSequence, bufferType, true, 0);
        if (this.mCharWrapper != null) {
            this.mCharWrapper.mChars = null;
        }
    }

    public final void setText(char[] cArr, int i, int i2) {
        int length;
        if (i < 0 || i2 < 0 || i + i2 > cArr.length) {
            throw new IndexOutOfBoundsException(i + ", " + i2);
        }
        if (this.mText != null) {
            length = this.mText.length();
            sendBeforeTextChanged(this.mText, 0, length, i2);
        } else {
            sendBeforeTextChanged("", 0, 0, i2);
            length = 0;
        }
        if (this.mCharWrapper == null) {
            this.mCharWrapper = new CharWrapper(cArr, i, i2);
        } else {
            this.mCharWrapper.set(cArr, i, i2);
        }
        setText(this.mCharWrapper, this.mBufferType, false, length);
    }

    public void setTextAppearance(int i) {
        setTextAppearance(this.mContext, i);
    }

    @Deprecated
    public void setTextAppearance(Context context, int i) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(i, android.R.styleable.TextAppearance);
        TextAppearanceAttributes textAppearanceAttributes = new TextAppearanceAttributes();
        readTextAppearance(context, typedArrayObtainStyledAttributes, textAppearanceAttributes, false);
        typedArrayObtainStyledAttributes.recycle();
        applyTextAppearance(textAppearanceAttributes);
    }

    public void setTextClassifier(TextClassifier textClassifier) {
        this.mTextClassifier = textClassifier;
    }

    @RemotableViewMethod
    public void setTextColor(int i) {
        this.mTextColor = ColorStateList.valueOf(i);
        updateTextColors();
    }

    @RemotableViewMethod
    public void setTextColor(ColorStateList colorStateList) {
        if (colorStateList == null) {
            throw new NullPointerException();
        }
        this.mTextColor = colorStateList;
        updateTextColors();
    }

    public void setTextIsSelectable(boolean z) {
        if (z || this.mEditor != null) {
            createEditorIfNeeded();
            if (this.mEditor.mTextIsSelectable == z) {
                return;
            }
            this.mEditor.mTextIsSelectable = z;
            setFocusableInTouchMode(z);
            setFocusable(16);
            setClickable(z);
            setLongClickable(z);
            setMovementMethod(z ? ArrowKeyMovementMethod.getInstance() : null);
            setText(this.mText, z ? BufferType.SPANNABLE : BufferType.NORMAL);
            this.mEditor.prepareCursorControllers();
        }
    }

    @RemotableViewMethod
    public final void setTextKeepState(CharSequence charSequence) {
        setTextKeepState(charSequence, this.mBufferType);
    }

    public final void setTextKeepState(CharSequence charSequence, BufferType bufferType) {
        int selectionStart = getSelectionStart();
        int selectionEnd = getSelectionEnd();
        int length = charSequence.length();
        setText(charSequence, bufferType);
        if ((selectionStart >= 0 || selectionEnd >= 0) && this.mSpannable != null) {
            Selection.setSelection(this.mSpannable, Math.max(0, Math.min(selectionStart, length)), Math.max(0, Math.min(selectionEnd, length)));
        }
    }

    public void setTextLocale(Locale locale) {
        this.mLocalesChanged = true;
        this.mTextPaint.setTextLocale(locale);
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setTextLocales(LocaleList localeList) {
        this.mLocalesChanged = true;
        this.mTextPaint.setTextLocales(localeList);
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    public void setTextMetricsParams(PrecomputedText.Params params) {
        this.mTextPaint.set(params.getTextPaint());
        this.mUserSetTextScaleX = true;
        this.mTextDir = params.getTextDirection();
        this.mBreakStrategy = params.getBreakStrategy();
        this.mHyphenationFrequency = params.getHyphenationFrequency();
        if (this.mLayout != null) {
            nullLayouts();
            requestLayout();
            invalidate();
        }
    }

    @RemotableViewMethod
    public void setTextScaleX(float f) {
        if (f != this.mTextPaint.getTextScaleX()) {
            this.mUserSetTextScaleX = true;
            this.mTextPaint.setTextScaleX(f);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    @RemotableViewMethod
    public void setTextSize(float f) {
        setTextSize(2, f);
    }

    public void setTextSize(int i, float f) {
        if (isAutoSizeEnabled()) {
            return;
        }
        setTextSizeInternal(i, f, true);
    }

    public final void setTransformationMethod(TransformationMethod transformationMethod) {
        if (transformationMethod == this.mTransformation) {
            return;
        }
        if (this.mTransformation != null && this.mSpannable != null) {
            this.mSpannable.removeSpan(this.mTransformation);
        }
        this.mTransformation = transformationMethod;
        if (transformationMethod instanceof TransformationMethod2) {
            TransformationMethod2 transformationMethod2 = (TransformationMethod2) transformationMethod;
            this.mAllowTransformationLengthChange = (isTextSelectable() || (this.mText instanceof Editable)) ? false : true;
            transformationMethod2.setLengthChangesAllowed(this.mAllowTransformationLengthChange);
        } else {
            this.mAllowTransformationLengthChange = false;
        }
        setText(this.mText);
        if (hasPasswordTransformationMethod()) {
            notifyViewAccessibilityStateChangedIfNeeded(0);
        }
        this.mTextDir = getTextDirectionHeuristic();
    }

    public void setTypeface(Typeface typeface) {
        if (this.mTextPaint.getTypeface() != typeface) {
            this.mTextPaint.setTypeface(typeface);
            if (this.mLayout != null) {
                nullLayouts();
                requestLayout();
                invalidate();
            }
        }
    }

    public void setTypeface(Typeface typeface, int i) {
        if (i <= 0) {
            this.mTextPaint.setFakeBoldText(false);
            this.mTextPaint.setTextSkewX(0.0f);
            setTypeface(typeface);
        } else {
            Typeface typefaceDefaultFromStyle = typeface == null ? Typeface.defaultFromStyle(i) : Typeface.create(typeface, i);
            setTypeface(typefaceDefaultFromStyle);
            int i2 = (~(typefaceDefaultFromStyle != null ? typefaceDefaultFromStyle.getStyle() : 0)) & i;
            this.mTextPaint.setFakeBoldText((i2 & 1) != 0);
            this.mTextPaint.setTextSkewX((i2 & 2) != 0 ? -0.25f : 0.0f);
        }
    }

    public final void setUndoManager(UndoManager undoManager, String str) {
        throw new UnsupportedOperationException("not implemented");
    }

    @RemotableViewMethod
    public void setWidth(int i) {
        this.mMinWidth = i;
        this.mMaxWidth = i;
        this.mMinWidthMode = 2;
        this.mMaxWidthMode = 2;
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    public boolean showContextMenu() {
        if (this.mEditor != null) {
            this.mEditor.setContextMenuAnchor(Float.NaN, Float.NaN);
        }
        return super.showContextMenu();
    }

    @Override // android.view.View
    public boolean showContextMenu(float f, float f2) {
        if (this.mEditor != null) {
            this.mEditor.setContextMenuAnchor(f, f2);
        }
        return super.showContextMenu(f, f2);
    }

    void spanChange(Spanned spanned, Object obj, int i, int i2, int i3, int i4) {
        int selectionEnd;
        boolean z;
        Editor.InputMethodState inputMethodState = this.mEditor == null ? null : this.mEditor.mInputMethodState;
        int selectionStart = -1;
        if (obj == Selection.SELECTION_END) {
            if (i >= 0 || i2 >= 0) {
                invalidateCursor(Selection.getSelectionStart(spanned), i, i2);
                checkForResize();
                registerForPreDraw();
                if (this.mEditor != null) {
                    this.mEditor.makeBlink();
                }
            }
            selectionEnd = i2;
            z = true;
        } else {
            selectionEnd = -1;
            z = false;
        }
        if (obj == Selection.SELECTION_START) {
            if (i >= 0 || i2 >= 0) {
                invalidateCursor(Selection.getSelectionEnd(spanned), i, i2);
            }
            selectionStart = i2;
            z = true;
        }
        if (z) {
            this.mHighlightPathBogus = true;
            if (this.mEditor != null && !isFocused()) {
                this.mEditor.mSelectionMoved = true;
            }
            if ((spanned.getSpanFlags(obj) & 512) == 0) {
                if (selectionStart < 0) {
                    selectionStart = Selection.getSelectionStart(spanned);
                }
                if (selectionEnd < 0) {
                    selectionEnd = Selection.getSelectionEnd(spanned);
                }
                if (this.mEditor != null) {
                    this.mEditor.refreshTextActionMode();
                    if (!hasSelection() && this.mEditor.getTextActionMode() == null && hasTransientState()) {
                        setHasTransientState(false);
                    }
                }
                onSelectionChanged(selectionStart, selectionEnd);
            }
        }
        if ((obj instanceof UpdateAppearance) || (obj instanceof ParagraphStyle) || (obj instanceof CharacterStyle)) {
            if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
                invalidate();
                this.mHighlightPathBogus = true;
                checkForResize();
            } else {
                inputMethodState.mContentChanged = true;
            }
            if (this.mEditor != null) {
                if (i >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, i, i3);
                }
                if (i2 >= 0) {
                    this.mEditor.invalidateTextDisplayList(this.mLayout, i2, i4);
                }
                this.mEditor.invalidateHandlesAndActionMode();
            }
        }
        if (MetaKeyKeyListener.isMetaTracker(spanned, obj)) {
            this.mHighlightPathBogus = true;
            if (inputMethodState != null && MetaKeyKeyListener.isSelectingMetaTracker(spanned, obj)) {
                inputMethodState.mSelectionModeChanged = true;
            }
            if (Selection.getSelectionStart(spanned) >= 0) {
                if (inputMethodState == null || inputMethodState.mBatchEditNesting == 0) {
                    invalidateCursor();
                } else {
                    inputMethodState.mCursorChanged = true;
                }
            }
        }
        if ((obj instanceof ParcelableSpan) && inputMethodState != null && inputMethodState.mExtractedTextRequest != null) {
            if (inputMethodState.mBatchEditNesting != 0) {
                if (i >= 0) {
                    if (inputMethodState.mChangedStart > i) {
                        inputMethodState.mChangedStart = i;
                    }
                    if (inputMethodState.mChangedStart > i3) {
                        inputMethodState.mChangedStart = i3;
                    }
                }
                if (i2 >= 0) {
                    if (inputMethodState.mChangedStart > i2) {
                        inputMethodState.mChangedStart = i2;
                    }
                    if (inputMethodState.mChangedStart > i4) {
                        inputMethodState.mChangedStart = i4;
                    }
                }
            } else {
                inputMethodState.mContentChanged = true;
            }
        }
        if (this.mEditor == null || this.mEditor.mSpellChecker == null || i2 >= 0 || !(obj instanceof SpellCheckSpan)) {
            return;
        }
        this.mEditor.mSpellChecker.onSpellCheckSpanRemoved((SpellCheckSpan) obj);
    }

    protected void stopTextActionMode() {
        if (this.mEditor != null) {
            this.mEditor.stopTextActionMode();
        }
    }

    protected boolean supportsAutoSizeText() {
        return true;
    }

    boolean textCanBeSelected() {
        if (this.mMovement == null || !this.mMovement.canSelectArbitrarily()) {
            return false;
        }
        return isTextEditable() || (isTextSelectable() && (this.mText instanceof Spannable) && isEnabled());
    }

    void updateAfterEdit() {
        invalidate();
        int selectionStart = getSelectionStart();
        if (selectionStart >= 0 || (this.mGravity & 112) == 80) {
            registerForPreDraw();
        }
        checkForResize();
        if (selectionStart >= 0) {
            this.mHighlightPathBogus = true;
            if (this.mEditor != null) {
                this.mEditor.makeBlink();
            }
            bringPointIntoView(selectionStart);
        }
    }

    @VisibleForTesting
    public boolean useDynamicLayout() {
        return isTextSelectable() || (this.mSpannable != null && this.mPrecomputed == null);
    }

    boolean usesNoOpTextClassifier() {
        return getTextClassifier() == TextClassifier.NO_OP;
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        boolean zVerifyDrawable = super.verifyDrawable(drawable);
        if (!zVerifyDrawable && this.mDrawables != null) {
            for (Drawable drawable2 : this.mDrawables.mShowing) {
                if (drawable == drawable2) {
                    return true;
                }
            }
        }
        return zVerifyDrawable;
    }

    protected void viewClicked(InputMethodManager inputMethodManager) {
        if (inputMethodManager != null) {
            inputMethodManager.viewClicked(this);
        }
    }

    int viewportToContentHorizontalOffset() {
        return getCompoundPaddingLeft() - this.mScrollX;
    }

    int viewportToContentVerticalOffset() {
        int extendedPaddingTop = getExtendedPaddingTop() - this.mScrollY;
        return (this.mGravity & 112) != 48 ? extendedPaddingTop + getVerticalOffset(false) : extendedPaddingTop;
    }
}
