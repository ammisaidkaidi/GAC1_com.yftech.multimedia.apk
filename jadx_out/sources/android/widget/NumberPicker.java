package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.method.NumberKeyListener;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.R;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import libcore.icu.LocaleData;

/* JADX INFO: loaded from: classes2.dex */
public class NumberPicker extends LinearLayout {
    private static final int DEFAULT_LAYOUT_RESOURCE_ID = 17367206;
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 1;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 3;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private AccessibilityNodeProviderImpl mAccessibilityNodeProvider;
    private final Scroller mAdjustScroller;
    private BeginSoftInputOnLongPressCommand mBeginSoftInputOnLongPressCommand;
    private int mBottomSelectionDividerBottom;
    private ChangeCurrentByOneFromLongPressCommand mChangeCurrentByOneFromLongPressCommand;
    private final boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private final ImageButton mDecrementButton;
    private boolean mDecrementVirtualButtonPressed;
    private String[] mDisplayedValues;
    private final Scroller mFlingScroller;
    private Formatter mFormatter;
    private final boolean mHasSelectorWheel;
    private boolean mHideWheelUntilFocused;
    private boolean mIgnoreMoveEvents;
    private final ImageButton mIncrementButton;
    private boolean mIncrementVirtualButtonPressed;
    private int mInitialScrollOffset;
    private final EditText mInputText;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private final int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private boolean mPerformClickOnTap;
    private final PressedStateHelper mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private final Drawable mSelectionDivider;
    private final int mSelectionDividerHeight;
    private final int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private final Paint mSelectorWheelPaint;
    private SetSelectionCommand mSetSelectionCommand;
    private final int mSolidColor;
    private final int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private final Drawable mVirtualButtonPressedDrawable;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelPreferred;
    private static final TwoDigitFormatter sTwoDigitFormatter = new TwoDigitFormatter();
    private static final char[] DIGIT_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311};

    class AccessibilityNodeProviderImpl extends AccessibilityNodeProvider {
        private static final int UNDEFINED = Integer.MIN_VALUE;
        private static final int VIRTUAL_VIEW_ID_DECREMENT = 3;
        private static final int VIRTUAL_VIEW_ID_INCREMENT = 1;
        private static final int VIRTUAL_VIEW_ID_INPUT = 2;
        private final Rect mTempRect = new Rect();
        private final int[] mTempArray = new int[2];
        private int mAccessibilityFocusedView = Integer.MIN_VALUE;

        AccessibilityNodeProviderImpl() {
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForNumberPicker(int i, int i2, int i3, int i4) {
            AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain();
            accessibilityNodeInfoObtain.setClassName(NumberPicker.class.getName());
            accessibilityNodeInfoObtain.setPackageName(NumberPicker.this.mContext.getPackageName());
            accessibilityNodeInfoObtain.setSource(NumberPicker.this);
            if (hasVirtualDecrementButton()) {
                accessibilityNodeInfoObtain.addChild(NumberPicker.this, 3);
            }
            accessibilityNodeInfoObtain.addChild(NumberPicker.this, 2);
            if (hasVirtualIncrementButton()) {
                accessibilityNodeInfoObtain.addChild(NumberPicker.this, 1);
            }
            accessibilityNodeInfoObtain.setParent((View) NumberPicker.this.getParentForAccessibility());
            accessibilityNodeInfoObtain.setEnabled(NumberPicker.this.isEnabled());
            accessibilityNodeInfoObtain.setScrollable(true);
            float f = NumberPicker.this.getContext().getResources().getCompatibilityInfo().applicationScale;
            Rect rect = this.mTempRect;
            rect.set(i, i2, i3, i4);
            rect.scale(f);
            accessibilityNodeInfoObtain.setBoundsInParent(rect);
            accessibilityNodeInfoObtain.setVisibleToUser(NumberPicker.this.isVisibleToUser());
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            rect.scale(f);
            accessibilityNodeInfoObtain.setBoundsInScreen(rect);
            if (this.mAccessibilityFocusedView != -1) {
                accessibilityNodeInfoObtain.addAction(64);
            }
            if (this.mAccessibilityFocusedView == -1) {
                accessibilityNodeInfoObtain.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue()) {
                    accessibilityNodeInfoObtain.addAction(4096);
                }
                if (NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue()) {
                    accessibilityNodeInfoObtain.addAction(8192);
                }
            }
            return accessibilityNodeInfoObtain;
        }

        private AccessibilityNodeInfo createAccessibilityNodeInfoForVirtualButton(int i, String str, int i2, int i3, int i4, int i5) {
            AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain();
            accessibilityNodeInfoObtain.setClassName(Button.class.getName());
            accessibilityNodeInfoObtain.setPackageName(NumberPicker.this.mContext.getPackageName());
            accessibilityNodeInfoObtain.setSource(NumberPicker.this, i);
            accessibilityNodeInfoObtain.setParent(NumberPicker.this);
            accessibilityNodeInfoObtain.setText(str);
            accessibilityNodeInfoObtain.setClickable(true);
            accessibilityNodeInfoObtain.setLongClickable(true);
            accessibilityNodeInfoObtain.setEnabled(NumberPicker.this.isEnabled());
            Rect rect = this.mTempRect;
            rect.set(i2, i3, i4, i5);
            accessibilityNodeInfoObtain.setVisibleToUser(NumberPicker.this.isVisibleToUser(rect));
            accessibilityNodeInfoObtain.setBoundsInParent(rect);
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            accessibilityNodeInfoObtain.setBoundsInScreen(rect);
            if (this.mAccessibilityFocusedView != i) {
                accessibilityNodeInfoObtain.addAction(64);
            }
            if (this.mAccessibilityFocusedView == i) {
                accessibilityNodeInfoObtain.addAction(128);
            }
            if (NumberPicker.this.isEnabled()) {
                accessibilityNodeInfoObtain.addAction(16);
            }
            return accessibilityNodeInfoObtain;
        }

        private AccessibilityNodeInfo createAccessibiltyNodeInfoForInputText(int i, int i2, int i3, int i4) {
            AccessibilityNodeInfo accessibilityNodeInfoCreateAccessibilityNodeInfo = NumberPicker.this.mInputText.createAccessibilityNodeInfo();
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setSource(NumberPicker.this, 2);
            if (this.mAccessibilityFocusedView != 2) {
                accessibilityNodeInfoCreateAccessibilityNodeInfo.addAction(64);
            }
            if (this.mAccessibilityFocusedView == 2) {
                accessibilityNodeInfoCreateAccessibilityNodeInfo.addAction(128);
            }
            Rect rect = this.mTempRect;
            rect.set(i, i2, i3, i4);
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setVisibleToUser(NumberPicker.this.isVisibleToUser(rect));
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setBoundsInParent(rect);
            int[] iArr = this.mTempArray;
            NumberPicker.this.getLocationOnScreen(iArr);
            rect.offset(iArr[0], iArr[1]);
            accessibilityNodeInfoCreateAccessibilityNodeInfo.setBoundsInScreen(rect);
            return accessibilityNodeInfoCreateAccessibilityNodeInfo;
        }

        private void findAccessibilityNodeInfosByTextInChild(String str, int i, List<AccessibilityNodeInfo> list) {
            switch (i) {
                case 1:
                    String virtualIncrementButtonText = getVirtualIncrementButtonText();
                    if (!TextUtils.isEmpty(virtualIncrementButtonText) && virtualIncrementButtonText.toString().toLowerCase().contains(str)) {
                        list.add(createAccessibilityNodeInfo(1));
                        break;
                    }
                    break;
                case 2:
                    Editable text = NumberPicker.this.mInputText.getText();
                    if (!TextUtils.isEmpty(text) && text.toString().toLowerCase().contains(str)) {
                        list.add(createAccessibilityNodeInfo(2));
                        break;
                    } else {
                        Editable text2 = NumberPicker.this.mInputText.getText();
                        if (!TextUtils.isEmpty(text2) && text2.toString().toLowerCase().contains(str)) {
                            list.add(createAccessibilityNodeInfo(2));
                            break;
                        }
                    }
                    break;
                case 3:
                    String virtualDecrementButtonText = getVirtualDecrementButtonText();
                    if (!TextUtils.isEmpty(virtualDecrementButtonText) && virtualDecrementButtonText.toString().toLowerCase().contains(str)) {
                        list.add(createAccessibilityNodeInfo(3));
                        break;
                    }
                    break;
            }
        }

        private String getVirtualDecrementButtonText() {
            int wrappedSelectorIndex = NumberPicker.this.mValue - 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                wrappedSelectorIndex = NumberPicker.this.getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            if (wrappedSelectorIndex >= NumberPicker.this.mMinValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(wrappedSelectorIndex) : NumberPicker.this.mDisplayedValues[wrappedSelectorIndex - NumberPicker.this.mMinValue];
            }
            return null;
        }

        private String getVirtualIncrementButtonText() {
            int wrappedSelectorIndex = NumberPicker.this.mValue + 1;
            if (NumberPicker.this.mWrapSelectorWheel) {
                wrappedSelectorIndex = NumberPicker.this.getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            if (wrappedSelectorIndex <= NumberPicker.this.mMaxValue) {
                return NumberPicker.this.mDisplayedValues == null ? NumberPicker.this.formatNumber(wrappedSelectorIndex) : NumberPicker.this.mDisplayedValues[wrappedSelectorIndex - NumberPicker.this.mMinValue];
            }
            return null;
        }

        private boolean hasVirtualDecrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() > NumberPicker.this.getMinValue();
        }

        private boolean hasVirtualIncrementButton() {
            return NumberPicker.this.getWrapSelectorWheel() || NumberPicker.this.getValue() < NumberPicker.this.getMaxValue();
        }

        private void sendAccessibilityEventForVirtualButton(int i, int i2, String str) {
            if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i2);
                accessibilityEventObtain.setClassName(Button.class.getName());
                accessibilityEventObtain.setPackageName(NumberPicker.this.mContext.getPackageName());
                accessibilityEventObtain.getText().add(str);
                accessibilityEventObtain.setEnabled(NumberPicker.this.isEnabled());
                accessibilityEventObtain.setSource(NumberPicker.this, i);
                NumberPicker.this.requestSendAccessibilityEvent(NumberPicker.this, accessibilityEventObtain);
            }
        }

        private void sendAccessibilityEventForVirtualText(int i) {
            if (AccessibilityManager.getInstance(NumberPicker.this.mContext).isEnabled()) {
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i);
                NumberPicker.this.mInputText.onInitializeAccessibilityEvent(accessibilityEventObtain);
                NumberPicker.this.mInputText.onPopulateAccessibilityEvent(accessibilityEventObtain);
                accessibilityEventObtain.setSource(NumberPicker.this, 2);
                NumberPicker.this.requestSendAccessibilityEvent(NumberPicker.this, accessibilityEventObtain);
            }
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
            if (i == -1) {
                return createAccessibilityNodeInfoForNumberPicker(NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
            }
            switch (i) {
                case 1:
                    return createAccessibilityNodeInfoForVirtualButton(1, getVirtualIncrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mScrollY + (NumberPicker.this.mBottom - NumberPicker.this.mTop));
                case 2:
                    return createAccessibiltyNodeInfoForInputText(NumberPicker.this.mScrollX, NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mBottomSelectionDividerBottom - NumberPicker.this.mSelectionDividerHeight);
                case 3:
                    return createAccessibilityNodeInfoForVirtualButton(3, getVirtualDecrementButtonText(), NumberPicker.this.mScrollX, NumberPicker.this.mScrollY, NumberPicker.this.mScrollX + (NumberPicker.this.mRight - NumberPicker.this.mLeft), NumberPicker.this.mTopSelectionDividerTop + NumberPicker.this.mSelectionDividerHeight);
                default:
                    return super.createAccessibilityNodeInfo(i);
            }
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public List<AccessibilityNodeInfo> findAccessibilityNodeInfosByText(String str, int i) {
            if (TextUtils.isEmpty(str)) {
                return Collections.emptyList();
            }
            String lowerCase = str.toLowerCase();
            ArrayList arrayList = new ArrayList();
            if (i != -1) {
                switch (i) {
                    case 1:
                    case 2:
                    case 3:
                        findAccessibilityNodeInfosByTextInChild(lowerCase, i, arrayList);
                        return arrayList;
                    default:
                        return super.findAccessibilityNodeInfosByText(str, i);
                }
            }
            findAccessibilityNodeInfosByTextInChild(lowerCase, 3, arrayList);
            findAccessibilityNodeInfosByTextInChild(lowerCase, 2, arrayList);
            findAccessibilityNodeInfosByTextInChild(lowerCase, 1, arrayList);
            return arrayList;
        }

        @Override // android.view.accessibility.AccessibilityNodeProvider
        public boolean performAction(int i, int i2, Bundle bundle) {
            if (i != -1) {
                switch (i) {
                    case 1:
                        if (i2 == 16) {
                            if (!NumberPicker.this.isEnabled()) {
                                return false;
                            }
                            NumberPicker.this.changeValueByOne(true);
                            sendAccessibilityEventForVirtualView(i, 1);
                            return true;
                        }
                        if (i2 == 64) {
                            if (this.mAccessibilityFocusedView == i) {
                                return false;
                            }
                            this.mAccessibilityFocusedView = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                            return true;
                        }
                        if (i2 != 128 || this.mAccessibilityFocusedView != i) {
                            return false;
                        }
                        this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                        sendAccessibilityEventForVirtualView(i, 65536);
                        NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                        return true;
                    case 2:
                        if (i2 == 16) {
                            if (!NumberPicker.this.isEnabled()) {
                                return false;
                            }
                            NumberPicker.this.performClick();
                            return true;
                        }
                        if (i2 == 32) {
                            if (!NumberPicker.this.isEnabled()) {
                                return false;
                            }
                            NumberPicker.this.performLongClick();
                            return true;
                        }
                        if (i2 == 64) {
                            if (this.mAccessibilityFocusedView == i) {
                                return false;
                            }
                            this.mAccessibilityFocusedView = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                            NumberPicker.this.mInputText.invalidate();
                            return true;
                        }
                        if (i2 == 128) {
                            if (this.mAccessibilityFocusedView != i) {
                                return false;
                            }
                            this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                            sendAccessibilityEventForVirtualView(i, 65536);
                            NumberPicker.this.mInputText.invalidate();
                            return true;
                        }
                        switch (i2) {
                            case 1:
                                if (!NumberPicker.this.isEnabled() || NumberPicker.this.mInputText.isFocused()) {
                                    return false;
                                }
                                return NumberPicker.this.mInputText.requestFocus();
                            case 2:
                                if (!NumberPicker.this.isEnabled() || !NumberPicker.this.mInputText.isFocused()) {
                                    return false;
                                }
                                NumberPicker.this.mInputText.clearFocus();
                                return true;
                            default:
                                return NumberPicker.this.mInputText.performAccessibilityAction(i2, bundle);
                        }
                    case 3:
                        if (i2 == 16) {
                            if (!NumberPicker.this.isEnabled()) {
                                return false;
                            }
                            NumberPicker.this.changeValueByOne(i == 1);
                            sendAccessibilityEventForVirtualView(i, 1);
                            return true;
                        }
                        if (i2 == 64) {
                            if (this.mAccessibilityFocusedView == i) {
                                return false;
                            }
                            this.mAccessibilityFocusedView = i;
                            sendAccessibilityEventForVirtualView(i, 32768);
                            NumberPicker.this.invalidate(0, 0, NumberPicker.this.mRight, NumberPicker.this.mTopSelectionDividerTop);
                            return true;
                        }
                        if (i2 != 128 || this.mAccessibilityFocusedView != i) {
                            return false;
                        }
                        this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                        sendAccessibilityEventForVirtualView(i, 65536);
                        NumberPicker.this.invalidate(0, 0, NumberPicker.this.mRight, NumberPicker.this.mTopSelectionDividerTop);
                        return true;
                }
            }
            if (i2 == 64) {
                if (this.mAccessibilityFocusedView == i) {
                    return false;
                }
                this.mAccessibilityFocusedView = i;
                NumberPicker.this.requestAccessibilityFocus();
                return true;
            }
            if (i2 == 128) {
                if (this.mAccessibilityFocusedView != i) {
                    return false;
                }
                this.mAccessibilityFocusedView = Integer.MIN_VALUE;
                NumberPicker.this.clearAccessibilityFocus();
                return true;
            }
            if (i2 == 4096) {
                if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() >= NumberPicker.this.getMaxValue())) {
                    return false;
                }
                NumberPicker.this.changeValueByOne(true);
                return true;
            }
            if (i2 == 8192) {
                if (!NumberPicker.this.isEnabled() || (!NumberPicker.this.getWrapSelectorWheel() && NumberPicker.this.getValue() <= NumberPicker.this.getMinValue())) {
                    return false;
                }
                NumberPicker.this.changeValueByOne(false);
                return true;
            }
            return super.performAction(i, i2, bundle);
        }

        public void sendAccessibilityEventForVirtualView(int i, int i2) {
            switch (i) {
                case 1:
                    if (hasVirtualIncrementButton()) {
                        sendAccessibilityEventForVirtualButton(i, i2, getVirtualIncrementButtonText());
                    }
                    break;
                case 2:
                    sendAccessibilityEventForVirtualText(i2);
                    break;
                case 3:
                    if (hasVirtualDecrementButton()) {
                        sendAccessibilityEventForVirtualButton(i, i2, getVirtualDecrementButtonText());
                    }
                    break;
            }
        }
    }

    class BeginSoftInputOnLongPressCommand implements Runnable {
        BeginSoftInputOnLongPressCommand() {
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.performLongClick();
        }
    }

    class ChangeCurrentByOneFromLongPressCommand implements Runnable {
        private boolean mIncrement;

        ChangeCurrentByOneFromLongPressCommand() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setStep(boolean z) {
            this.mIncrement = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            NumberPicker.this.changeValueByOne(this.mIncrement);
            NumberPicker.this.postDelayed(this, NumberPicker.this.mLongPressUpdateInterval);
        }
    }

    public static class CustomEditText extends EditText {
        public CustomEditText(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override // android.widget.TextView
        public void onEditorAction(int i) {
            super.onEditorAction(i);
            if (i == 6) {
                clearFocus();
            }
        }
    }

    public interface Formatter {
        String format(int i);
    }

    class InputTextFilter extends NumberKeyListener {
        InputTextFilter() {
        }

        @Override // android.text.method.NumberKeyListener, android.text.InputFilter
        public CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
            if (NumberPicker.this.mSetSelectionCommand != null) {
                NumberPicker.this.mSetSelectionCommand.cancel();
            }
            if (NumberPicker.this.mDisplayedValues == null) {
                CharSequence charSequenceFilter = super.filter(charSequence, i, i2, spanned, i3, i4);
                if (charSequenceFilter == null) {
                    charSequenceFilter = charSequence.subSequence(i, i2);
                }
                String str = String.valueOf(spanned.subSequence(0, i3)) + ((Object) charSequenceFilter) + ((Object) spanned.subSequence(i4, spanned.length()));
                if ("".equals(str)) {
                    return str;
                }
                return (NumberPicker.this.getSelectedPos(str) > NumberPicker.this.mMaxValue || str.length() > String.valueOf(NumberPicker.this.mMaxValue).length()) ? "" : charSequenceFilter;
            }
            String strValueOf = String.valueOf(charSequence.subSequence(i, i2));
            if (TextUtils.isEmpty(strValueOf)) {
                return "";
            }
            String str2 = String.valueOf(spanned.subSequence(0, i3)) + ((Object) strValueOf) + ((Object) spanned.subSequence(i4, spanned.length()));
            String lowerCase = String.valueOf(str2).toLowerCase();
            for (String str3 : NumberPicker.this.mDisplayedValues) {
                if (str3.toLowerCase().startsWith(lowerCase)) {
                    NumberPicker.this.postSetSelectionCommand(str2.length(), str3.length());
                    return str3.subSequence(i3, str3.length());
                }
            }
            return "";
        }

        @Override // android.text.method.NumberKeyListener
        protected char[] getAcceptedChars() {
            return NumberPicker.DIGIT_CHARACTERS;
        }

        @Override // android.text.method.KeyListener
        public int getInputType() {
            return 1;
        }
    }

    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        @Retention(RetentionPolicy.SOURCE)
        public @interface ScrollState {
        }

        void onScrollStateChange(NumberPicker numberPicker, int i);
    }

    public interface OnValueChangeListener {
        void onValueChange(NumberPicker numberPicker, int i, int i2);
    }

    class PressedStateHelper implements Runnable {
        public static final int BUTTON_DECREMENT = 2;
        public static final int BUTTON_INCREMENT = 1;
        private final int MODE_PRESS = 1;
        private final int MODE_TAPPED = 2;
        private int mManagedButton;
        private int mMode;

        PressedStateHelper() {
        }

        public void buttonPressDelayed(int i) {
            cancel();
            this.mMode = 1;
            this.mManagedButton = i;
            NumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        public void buttonTapped(int i) {
            cancel();
            this.mMode = 2;
            this.mManagedButton = i;
            NumberPicker.this.post(this);
        }

        public void cancel() {
            this.mMode = 0;
            this.mManagedButton = 0;
            NumberPicker.this.removeCallbacks(this);
            if (NumberPicker.this.mIncrementVirtualButtonPressed) {
                NumberPicker.this.mIncrementVirtualButtonPressed = false;
                NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
            }
            NumberPicker.this.mDecrementVirtualButtonPressed = false;
            if (NumberPicker.this.mDecrementVirtualButtonPressed) {
                NumberPicker.this.invalidate(0, 0, NumberPicker.this.mRight, NumberPicker.this.mTopSelectionDividerTop);
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            switch (this.mMode) {
                case 1:
                    switch (this.mManagedButton) {
                        case 1:
                            NumberPicker.this.mIncrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                            break;
                        case 2:
                            NumberPicker.this.mDecrementVirtualButtonPressed = true;
                            NumberPicker.this.invalidate(0, 0, NumberPicker.this.mRight, NumberPicker.this.mTopSelectionDividerTop);
                            break;
                    }
                    break;
                case 2:
                    switch (this.mManagedButton) {
                        case 1:
                            if (!NumberPicker.this.mIncrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.access$1280(NumberPicker.this, 1);
                            NumberPicker.this.invalidate(0, NumberPicker.this.mBottomSelectionDividerBottom, NumberPicker.this.mRight, NumberPicker.this.mBottom);
                            break;
                        case 2:
                            if (!NumberPicker.this.mDecrementVirtualButtonPressed) {
                                NumberPicker.this.postDelayed(this, ViewConfiguration.getPressedStateDuration());
                            }
                            NumberPicker.access$1680(NumberPicker.this, 1);
                            NumberPicker.this.invalidate(0, 0, NumberPicker.this.mRight, NumberPicker.this.mTopSelectionDividerTop);
                            break;
                    }
                    break;
            }
        }
    }

    private static class SetSelectionCommand implements Runnable {
        private final EditText mInputText;
        private boolean mPosted;
        private int mSelectionEnd;
        private int mSelectionStart;

        public SetSelectionCommand(EditText editText) {
            this.mInputText = editText;
        }

        public void cancel() {
            if (this.mPosted) {
                this.mInputText.removeCallbacks(this);
                this.mPosted = false;
            }
        }

        public void post(int i, int i2) {
            this.mSelectionStart = i;
            this.mSelectionEnd = i2;
            if (this.mPosted) {
                return;
            }
            this.mInputText.post(this);
            this.mPosted = true;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.mPosted = false;
            this.mInputText.setSelection(this.mSelectionStart, this.mSelectionEnd);
        }
    }

    private static class TwoDigitFormatter implements Formatter {
        java.util.Formatter mFmt;
        char mZeroDigit;
        final StringBuilder mBuilder = new StringBuilder();
        final Object[] mArgs = new Object[1];

        TwoDigitFormatter() {
            init(Locale.getDefault());
        }

        private java.util.Formatter createFormatter(Locale locale) {
            return new java.util.Formatter(this.mBuilder, locale);
        }

        private static char getZeroDigit(Locale locale) {
            return LocaleData.get(locale).zeroDigit;
        }

        private void init(Locale locale) {
            this.mFmt = createFormatter(locale);
            this.mZeroDigit = getZeroDigit(locale);
        }

        @Override // android.widget.NumberPicker.Formatter
        public String format(int i) {
            Locale locale = Locale.getDefault();
            if (this.mZeroDigit != getZeroDigit(locale)) {
                init(locale);
            }
            this.mArgs[0] = Integer.valueOf(i);
            this.mBuilder.delete(0, this.mBuilder.length());
            this.mFmt.format("%02d", this.mArgs);
            return this.mFmt.toString();
        }
    }

    public NumberPicker(Context context) {
        this(context, null);
    }

    public NumberPicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16844068);
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public NumberPicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mWrapSelectorWheelPreferred = true;
        this.mLongPressUpdateInterval = DEFAULT_LONG_PRESS_UPDATE_INTERVAL;
        this.mSelectorIndexToStringCache = new SparseArray<>();
        this.mSelectorIndices = new int[3];
        this.mInitialScrollOffset = Integer.MIN_VALUE;
        this.mScrollState = 0;
        this.mLastHandledDownDpadKeyCode = -1;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.NumberPicker, i, i2);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(2, 17367206);
        this.mHasSelectorWheel = resourceId != 17367206;
        this.mHideWheelUntilFocused = typedArrayObtainStyledAttributes.getBoolean(1, false);
        this.mSolidColor = typedArrayObtainStyledAttributes.getColor(0, 0);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(7);
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setLayoutDirection(getLayoutDirection());
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        this.mSelectionDivider = drawable;
        this.mSelectionDividerHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, (int) TypedValue.applyDimension(1, 2.0f, getResources().getDisplayMetrics()));
        this.mSelectionDividersDistance = typedArrayObtainStyledAttributes.getDimensionPixelSize(9, (int) TypedValue.applyDimension(1, 48.0f, getResources().getDisplayMetrics()));
        this.mMinHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(5, -1);
        this.mMaxHeight = typedArrayObtainStyledAttributes.getDimensionPixelSize(3, -1);
        if (this.mMinHeight != -1 && this.mMaxHeight != -1 && this.mMinHeight > this.mMaxHeight) {
            throw new IllegalArgumentException("minHeight > maxHeight");
        }
        this.mMinWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(6, -1);
        this.mMaxWidth = typedArrayObtainStyledAttributes.getDimensionPixelSize(4, -1);
        if (this.mMinWidth != -1 && this.mMaxWidth != -1 && this.mMinWidth > this.mMaxWidth) {
            throw new IllegalArgumentException("minWidth > maxWidth");
        }
        this.mComputeMaxWidth = this.mMaxWidth == -1;
        this.mVirtualButtonPressedDrawable = typedArrayObtainStyledAttributes.getDrawable(10);
        typedArrayObtainStyledAttributes.recycle();
        this.mPressedStateHelper = new PressedStateHelper();
        setWillNotDraw(!this.mHasSelectorWheel);
        ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(resourceId, (ViewGroup) this, true);
        View.OnClickListener onClickListener = new View.OnClickListener() { // from class: android.widget.NumberPicker.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                NumberPicker.this.hideSoftInput();
                NumberPicker.this.mInputText.clearFocus();
                if (view.getId() == 16908974) {
                    NumberPicker.this.changeValueByOne(true);
                } else {
                    NumberPicker.this.changeValueByOne(false);
                }
            }
        };
        View.OnLongClickListener onLongClickListener = new View.OnLongClickListener() { // from class: android.widget.NumberPicker.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                NumberPicker.this.hideSoftInput();
                NumberPicker.this.mInputText.clearFocus();
                if (view.getId() == 16908974) {
                    NumberPicker.this.postChangeCurrentByOneFromLongPress(true, 0L);
                } else {
                    NumberPicker.this.postChangeCurrentByOneFromLongPress(false, 0L);
                }
                return true;
            }
        };
        if (this.mHasSelectorWheel) {
            this.mIncrementButton = null;
        } else {
            this.mIncrementButton = (ImageButton) findViewById(R.id.increment);
            this.mIncrementButton.setOnClickListener(onClickListener);
            this.mIncrementButton.setOnLongClickListener(onLongClickListener);
        }
        if (this.mHasSelectorWheel) {
            this.mDecrementButton = null;
        } else {
            this.mDecrementButton = (ImageButton) findViewById(R.id.decrement);
            this.mDecrementButton.setOnClickListener(onClickListener);
            this.mDecrementButton.setOnLongClickListener(onLongClickListener);
        }
        this.mInputText = (EditText) findViewById(R.id.numberpicker_input);
        this.mInputText.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: android.widget.NumberPicker.3
            @Override // android.view.View.OnFocusChangeListener
            public void onFocusChange(View view, boolean z) {
                if (z) {
                    NumberPicker.this.mInputText.selectAll();
                } else {
                    NumberPicker.this.mInputText.setSelection(0, 0);
                    NumberPicker.this.validateInputTextView(view);
                }
            }
        });
        this.mInputText.setFilters(new InputFilter[]{new InputTextFilter()});
        this.mInputText.setAccessibilityLiveRegion(1);
        this.mInputText.setRawInputType(2);
        this.mInputText.setImeOptions(6);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMinimumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        this.mMaximumFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity() / 8;
        this.mTextSize = (int) this.mInputText.getTextSize();
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(this.mTextSize);
        paint.setTypeface(this.mInputText.getTypeface());
        paint.setColor(this.mInputText.getTextColors().getColorForState(ENABLED_STATE_SET, -1));
        this.mSelectorWheelPaint = paint;
        this.mFlingScroller = new Scroller(getContext(), null, true);
        this.mAdjustScroller = new Scroller(getContext(), new DecelerateInterpolator(2.5f));
        updateInputTextView();
        if (getImportantForAccessibility() == 0) {
            setImportantForAccessibility(1);
        }
        if (getFocusable() == 16) {
            setFocusable(1);
            setFocusableInTouchMode(true);
        }
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$1280(NumberPicker numberPicker, int i) {
        ?? r2 = (i ^ (numberPicker.mIncrementVirtualButtonPressed ? 1 : 0)) == true ? (byte) 1 : (byte) 0;
        numberPicker.mIncrementVirtualButtonPressed = r2;
        return r2;
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [boolean, byte] */
    static /* synthetic */ boolean access$1680(NumberPicker numberPicker, int i) {
        ?? r2 = (i ^ (numberPicker.mDecrementVirtualButtonPressed ? 1 : 0)) == true ? (byte) 1 : (byte) 0;
        numberPicker.mDecrementVirtualButtonPressed = r2;
        return r2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeValueByOne(boolean z) {
        if (!this.mHasSelectorWheel) {
            if (z) {
                setValueInternal(this.mValue + 1, true);
                return;
            } else {
                setValueInternal(this.mValue - 1, true);
                return;
            }
        }
        hideSoftInput();
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (z) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, 300);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, 300);
        }
        invalidate();
    }

    private void decrementSelectorIndices(int[] iArr) {
        for (int length = iArr.length - 1; length > 0; length--) {
            iArr[length] = iArr[length - 1];
        }
        int i = iArr[1] - 1;
        if (this.mWrapSelectorWheel && i < this.mMinValue) {
            i = this.mMaxValue;
        }
        iArr[0] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void ensureCachedScrollSelectorValue(int i) {
        String number;
        SparseArray<String> sparseArray = this.mSelectorIndexToStringCache;
        if (sparseArray.get(i) != null) {
            return;
        }
        if (i < this.mMinValue || i > this.mMaxValue) {
            number = "";
        } else if (this.mDisplayedValues != null) {
            number = this.mDisplayedValues[i - this.mMinValue];
        } else {
            number = formatNumber(i);
        }
        sparseArray.put(i, number);
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i == 0) {
            return false;
        }
        this.mPreviousScrollerY = 0;
        if (Math.abs(i) > this.mSelectorElementHeight / 2) {
            i += i > 0 ? -this.mSelectorElementHeight : this.mSelectorElementHeight;
        }
        this.mAdjustScroller.startScroll(0, 0, 0, i, 800);
        invalidate();
        return true;
    }

    private void fling(int i) {
        this.mPreviousScrollerY = 0;
        if (i > 0) {
            this.mFlingScroller.fling(0, 0, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        } else {
            this.mFlingScroller.fling(0, Integer.MAX_VALUE, 0, i, 0, 0, 0, Integer.MAX_VALUE);
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String formatNumber(int i) {
        return this.mFormatter != null ? this.mFormatter.format(i) : formatNumberWithLocale(i);
    }

    private static String formatNumberWithLocale(int i) {
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(i));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSelectedPos(String str) {
        try {
            if (this.mDisplayedValues == null) {
                return Integer.parseInt(str);
            }
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                str = str.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
                    return this.mMinValue + i;
                }
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException unused) {
            return this.mMinValue;
        }
    }

    public static final Formatter getTwoDigitFormatter() {
        return sTwoDigitFormatter;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getWrappedSelectorIndex(int i) {
        if (i > this.mMaxValue) {
            return (this.mMinValue + ((i - this.mMaxValue) % (this.mMaxValue - this.mMinValue))) - 1;
        }
        return i < this.mMinValue ? (this.mMaxValue - ((this.mMinValue - i) % (this.mMaxValue - this.mMinValue))) + 1 : i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideSoftInput() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null && inputMethodManagerPeekInstance.isActive(this.mInputText)) {
            inputMethodManagerPeekInstance.hideSoftInputFromWindow(getWindowToken(), 0);
        }
        if (this.mHasSelectorWheel) {
            this.mInputText.setVisibility(4);
        }
    }

    private void incrementSelectorIndices(int[] iArr) {
        int i = 0;
        while (i < iArr.length - 1) {
            int i2 = i + 1;
            iArr[i] = iArr[i2];
            i = i2;
        }
        int i3 = iArr[iArr.length - 2] + 1;
        if (this.mWrapSelectorWheel && i3 > this.mMaxValue) {
            i3 = this.mMinValue;
        }
        iArr[iArr.length - 1] = i3;
        ensureCachedScrollSelectorValue(i3);
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((this.mBottom - this.mTop) - this.mTextSize) / 2);
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((this.mBottom - this.mTop) - (iArr.length * this.mTextSize)) / iArr.length) + 0.5f);
        this.mSelectorElementHeight = this.mTextSize + this.mSelectorTextGapHeight;
        this.mInitialScrollOffset = (this.mInputText.getBaseline() + this.mInputText.getTop()) - (this.mSelectorElementHeight * 1);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
        updateInputTextView();
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int wrappedSelectorIndex = (i - 1) + value;
            if (this.mWrapSelectorWheel) {
                wrappedSelectorIndex = getWrappedSelectorIndex(wrappedSelectorIndex);
            }
            iArr[i] = wrappedSelectorIndex;
            ensureCachedScrollSelectorValue(iArr[i]);
        }
    }

    private int makeMeasureSpec(int i, int i2) {
        if (i2 == -1) {
            return i;
        }
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        if (mode == Integer.MIN_VALUE) {
            return View.MeasureSpec.makeMeasureSpec(Math.min(size, i2), 1073741824);
        }
        if (mode == 0) {
            return View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
        }
        if (mode == 1073741824) {
            return i;
        }
        throw new IllegalArgumentException("Unknown measure mode: " + mode);
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int finalY = scroller.getFinalY() - scroller.getCurrY();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalY) % this.mSelectorElementHeight);
        if (i == 0) {
            return false;
        }
        if (Math.abs(i) > this.mSelectorElementHeight / 2) {
            i = i > 0 ? i - this.mSelectorElementHeight : i + this.mSelectorElementHeight;
        }
        scrollBy(0, finalY + i);
        return true;
    }

    private void notifyChange(int i, int i2) {
        if (this.mOnValueChangeListener != null) {
            this.mOnValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState == i) {
            return;
        }
        this.mScrollState = i;
        if (this.mOnScrollListener != null) {
            this.mOnScrollListener.onScrollStateChange(this, i);
        }
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            ensureScrollWheelAdjusted();
            updateInputTextView();
            onScrollStateChange(0);
        } else if (this.mScrollState != 1) {
            updateInputTextView();
        }
    }

    private void postBeginSoftInputOnLongPressCommand() {
        if (this.mBeginSoftInputOnLongPressCommand == null) {
            this.mBeginSoftInputOnLongPressCommand = new BeginSoftInputOnLongPressCommand();
        } else {
            removeCallbacks(this.mBeginSoftInputOnLongPressCommand);
        }
        postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        if (this.mChangeCurrentByOneFromLongPressCommand == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new ChangeCurrentByOneFromLongPressCommand();
        } else {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        this.mChangeCurrentByOneFromLongPressCommand.setStep(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postSetSelectionCommand(int i, int i2) {
        if (this.mSetSelectionCommand == null) {
            this.mSetSelectionCommand = new SetSelectionCommand(this.mInputText);
        }
        this.mSetSelectionCommand.post(i, i2);
    }

    private void removeAllCallbacks() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
        if (this.mSetSelectionCommand != null) {
            this.mSetSelectionCommand.cancel();
        }
        if (this.mBeginSoftInputOnLongPressCommand != null) {
            removeCallbacks(this.mBeginSoftInputOnLongPressCommand);
        }
        this.mPressedStateHelper.cancel();
    }

    private void removeBeginSoftInputCommand() {
        if (this.mBeginSoftInputOnLongPressCommand != null) {
            removeCallbacks(this.mBeginSoftInputOnLongPressCommand);
        }
    }

    private void removeChangeCurrentByOneFromLongPress() {
        if (this.mChangeCurrentByOneFromLongPressCommand != null) {
            removeCallbacks(this.mChangeCurrentByOneFromLongPressCommand);
        }
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    private void setValueInternal(int i, boolean z) {
        if (this.mValue == i) {
            return;
        }
        int wrappedSelectorIndex = this.mWrapSelectorWheel ? getWrappedSelectorIndex(i) : Math.min(Math.max(i, this.mMinValue), this.mMaxValue);
        int i2 = this.mValue;
        this.mValue = wrappedSelectorIndex;
        if (this.mScrollState != 2) {
            updateInputTextView();
        }
        if (z) {
            notifyChange(i2, wrappedSelectorIndex);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    private void showSoftInput() {
        InputMethodManager inputMethodManagerPeekInstance = InputMethodManager.peekInstance();
        if (inputMethodManagerPeekInstance != null) {
            if (this.mHasSelectorWheel) {
                this.mInputText.setVisibility(0);
            }
            this.mInputText.requestFocus();
            inputMethodManagerPeekInstance.showSoftInput(this.mInputText, 0);
        }
    }

    private void tryComputeMaxWidth() {
        int i;
        if (this.mComputeMaxWidth) {
            int i2 = 0;
            if (this.mDisplayedValues == null) {
                float f = 0.0f;
                for (int i3 = 0; i3 <= 9; i3++) {
                    float fMeasureText = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i3));
                    if (fMeasureText > f) {
                        f = fMeasureText;
                    }
                }
                for (int i4 = this.mMaxValue; i4 > 0; i4 /= 10) {
                    i2++;
                }
                i = (int) (i2 * f);
            } else {
                int length = this.mDisplayedValues.length;
                int i5 = 0;
                while (i2 < length) {
                    float fMeasureText2 = this.mSelectorWheelPaint.measureText(this.mDisplayedValues[i2]);
                    if (fMeasureText2 > i5) {
                        i5 = (int) fMeasureText2;
                    }
                    i2++;
                }
                i = i5;
            }
            int paddingLeft = i + this.mInputText.getPaddingLeft() + this.mInputText.getPaddingRight();
            if (this.mMaxWidth != paddingLeft) {
                if (paddingLeft > this.mMinWidth) {
                    this.mMaxWidth = paddingLeft;
                } else {
                    this.mMaxWidth = this.mMinWidth;
                }
                invalidate();
            }
        }
    }

    private boolean updateInputTextView() {
        String number = this.mDisplayedValues == null ? formatNumber(this.mValue) : this.mDisplayedValues[this.mValue - this.mMinValue];
        if (!TextUtils.isEmpty(number)) {
            Editable text = this.mInputText.getText();
            if (!number.equals(text.toString())) {
                this.mInputText.setText(number);
                if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
                    return true;
                }
                AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(16);
                this.mInputText.onInitializeAccessibilityEvent(accessibilityEventObtain);
                this.mInputText.onPopulateAccessibilityEvent(accessibilityEventObtain);
                accessibilityEventObtain.setFromIndex(0);
                accessibilityEventObtain.setRemovedCount(text.length());
                accessibilityEventObtain.setAddedCount(number.length());
                accessibilityEventObtain.setBeforeText(text);
                accessibilityEventObtain.setSource(this, 2);
                requestSendAccessibilityEvent(this, accessibilityEventObtain);
                return true;
            }
        }
        return false;
    }

    private void updateWrapSelectorWheel() {
        boolean z = false;
        if ((this.mMaxValue - this.mMinValue >= this.mSelectorIndices.length) && this.mWrapSelectorWheelPreferred) {
            z = true;
        }
        this.mWrapSelectorWheel = z;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void validateInputTextView(View view) {
        String strValueOf = String.valueOf(((TextView) view).getText());
        if (TextUtils.isEmpty(strValueOf)) {
            updateInputTextView();
        } else {
            setValueInternal(getSelectedPos(strValueOf.toString()), true);
        }
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override // android.view.View
    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected boolean dispatchHoverEvent(MotionEvent motionEvent) {
        int i;
        if (!this.mHasSelectorWheel) {
            return super.dispatchHoverEvent(motionEvent);
        }
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled()) {
            return false;
        }
        int y = (int) motionEvent.getY();
        if (y < this.mTopSelectionDividerTop) {
            i = 3;
        } else {
            i = y > this.mBottomSelectionDividerBottom ? 1 : 2;
        }
        int actionMasked = motionEvent.getActionMasked();
        AccessibilityNodeProviderImpl accessibilityNodeProviderImpl = (AccessibilityNodeProviderImpl) getAccessibilityNodeProvider();
        if (actionMasked == 7) {
            if (this.mLastHoveredChildVirtualViewId == i || this.mLastHoveredChildVirtualViewId == -1) {
                return false;
            }
            accessibilityNodeProviderImpl.sendAccessibilityEventForVirtualView(this.mLastHoveredChildVirtualViewId, 256);
            accessibilityNodeProviderImpl.sendAccessibilityEventForVirtualView(i, 128);
            this.mLastHoveredChildVirtualViewId = i;
            accessibilityNodeProviderImpl.performAction(i, 64, null);
            return false;
        }
        switch (actionMasked) {
            case 9:
                accessibilityNodeProviderImpl.sendAccessibilityEventForVirtualView(i, 128);
                this.mLastHoveredChildVirtualViewId = i;
                accessibilityNodeProviderImpl.performAction(i, 64, null);
                return false;
            case 10:
                accessibilityNodeProviderImpl.sendAccessibilityEventForVirtualView(i, 256);
                this.mLastHoveredChildVirtualViewId = -1;
                return false;
            default:
                return false;
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode != 23 && keyCode != 66) {
            switch (keyCode) {
                case 19:
                case 20:
                    if (this.mHasSelectorWheel) {
                        switch (keyEvent.getAction()) {
                            case 0:
                                if (this.mWrapSelectorWheel || (keyCode != 20 ? getValue() > getMinValue() : getValue() < getMaxValue())) {
                                    requestFocus();
                                    this.mLastHandledDownDpadKeyCode = keyCode;
                                    removeAllCallbacks();
                                    if (this.mFlingScroller.isFinished()) {
                                        changeValueByOne(keyCode == 20);
                                    }
                                    return true;
                                }
                                break;
                            case 1:
                                if (this.mLastHandledDownDpadKeyCode == keyCode) {
                                    this.mLastHandledDownDpadKeyCode = -1;
                                    return true;
                                }
                                break;
                        }
                    }
                    break;
            }
        } else {
            removeAllCallbacks();
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (!this.mHasSelectorWheel) {
            return super.getAccessibilityNodeProvider();
        }
        if (this.mAccessibilityNodeProvider == null) {
            this.mAccessibilityNodeProvider = new AccessibilityNodeProviderImpl();
        }
        return this.mAccessibilityNodeProvider;
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public CharSequence getDisplayedValueForCurrentSelection() {
        return this.mSelectorIndexToStringCache.get(getValue());
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mSolidColor;
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public int getValue() {
        return this.mValue;
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mSelectionDivider != null) {
            this.mSelectionDivider.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onDraw(Canvas canvas) {
        if (!this.mHasSelectorWheel) {
            super.onDraw(canvas);
            return;
        }
        boolean zHasFocus = this.mHideWheelUntilFocused ? hasFocus() : true;
        float f = (this.mRight - this.mLeft) / 2;
        float f2 = this.mCurrentScrollOffset;
        if (zHasFocus && this.mVirtualButtonPressedDrawable != null && this.mScrollState == 0) {
            if (this.mDecrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(0, 0, this.mRight, this.mTopSelectionDividerTop);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
            if (this.mIncrementVirtualButtonPressed) {
                this.mVirtualButtonPressedDrawable.setState(PRESSED_STATE_SET);
                this.mVirtualButtonPressedDrawable.setBounds(0, this.mBottomSelectionDividerBottom, this.mRight, this.mBottom);
                this.mVirtualButtonPressedDrawable.draw(canvas);
            }
        }
        int[] iArr = this.mSelectorIndices;
        float f3 = f2;
        for (int i = 0; i < iArr.length; i++) {
            String str = this.mSelectorIndexToStringCache.get(iArr[i]);
            if ((zHasFocus && i != 1) || (i == 1 && this.mInputText.getVisibility() != 0)) {
                canvas.drawText(str, f, f3, this.mSelectorWheelPaint);
            }
            f3 += this.mSelectorElementHeight;
        }
        if (!zHasFocus || this.mSelectionDivider == null) {
            return;
        }
        int i2 = this.mTopSelectionDividerTop;
        this.mSelectionDivider.setBounds(0, i2, this.mRight, this.mSelectionDividerHeight + i2);
        this.mSelectionDivider.draw(canvas);
        int i3 = this.mBottomSelectionDividerBottom;
        this.mSelectionDivider.setBounds(0, i3 - this.mSelectionDividerHeight, this.mRight, i3);
        this.mSelectionDivider.draw(canvas);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onInitializeAccessibilityEventInternal(accessibilityEvent);
        accessibilityEvent.setClassName(NumberPicker.class.getName());
        accessibilityEvent.setScrollable(true);
        accessibilityEvent.setScrollY((this.mMinValue + this.mValue) * this.mSelectorElementHeight);
        accessibilityEvent.setMaxScrollY((this.mMaxValue - this.mMinValue) * this.mSelectorElementHeight);
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.mHasSelectorWheel || !isEnabled() || motionEvent.getActionMasked() != 0) {
            return false;
        }
        removeAllCallbacks();
        hideSoftInput();
        float y = motionEvent.getY();
        this.mLastDownEventY = y;
        this.mLastDownOrMoveEventY = y;
        this.mLastDownEventTime = motionEvent.getEventTime();
        this.mIgnoreMoveEvents = false;
        this.mPerformClickOnTap = false;
        if (this.mLastDownEventY < this.mTopSelectionDividerTop) {
            if (this.mScrollState == 0) {
                this.mPressedStateHelper.buttonPressDelayed(2);
            }
        } else if (this.mLastDownEventY > this.mBottomSelectionDividerBottom && this.mScrollState == 0) {
            this.mPressedStateHelper.buttonPressDelayed(1);
        }
        getParent().requestDisallowInterceptTouchEvent(true);
        if (!this.mFlingScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
            onScrollStateChange(0);
        } else if (!this.mAdjustScroller.isFinished()) {
            this.mFlingScroller.forceFinished(true);
            this.mAdjustScroller.forceFinished(true);
        } else if (this.mLastDownEventY < this.mTopSelectionDividerTop) {
            postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
        } else if (this.mLastDownEventY > this.mBottomSelectionDividerBottom) {
            postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
        } else {
            this.mPerformClickOnTap = true;
            postBeginSoftInputOnLongPressCommand();
        }
        return true;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (!this.mHasSelectorWheel) {
            super.onLayout(z, i, i2, i3, i4);
            return;
        }
        int measuredWidth = getMeasuredWidth();
        int measuredHeight = getMeasuredHeight();
        int measuredWidth2 = this.mInputText.getMeasuredWidth();
        int measuredHeight2 = this.mInputText.getMeasuredHeight();
        int i5 = (measuredWidth - measuredWidth2) / 2;
        int i6 = (measuredHeight - measuredHeight2) / 2;
        this.mInputText.layout(i5, i6, measuredWidth2 + i5, measuredHeight2 + i6);
        if (z) {
            initializeSelectorWheel();
            initializeFadingEdges();
            this.mTopSelectionDividerTop = ((getHeight() - this.mSelectionDividersDistance) / 2) - this.mSelectionDividerHeight;
            this.mBottomSelectionDividerBottom = this.mTopSelectionDividerTop + (this.mSelectionDividerHeight * 2) + this.mSelectionDividersDistance;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        if (!this.mHasSelectorWheel) {
            super.onMeasure(i, i2);
        } else {
            super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
            setMeasuredDimension(resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i), resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
        }
    }

    @Override // android.view.View
    public void onResolveDrawables(int i) {
        super.onResolveDrawables(i);
        if (this.mSelectionDivider != null) {
            this.mSelectionDivider.setLayoutDirection(i);
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || !this.mHasSelectorWheel) {
            return false;
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        switch (motionEvent.getActionMasked()) {
            case 1:
                removeBeginSoftInputCommand();
                removeChangeCurrentByOneFromLongPress();
                this.mPressedStateHelper.cancel();
                VelocityTracker velocityTracker = this.mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
                int yVelocity = (int) velocityTracker.getYVelocity();
                if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                    fling(yVelocity);
                    onScrollStateChange(2);
                } else {
                    int y = (int) motionEvent.getY();
                    int iAbs = (int) Math.abs(y - this.mLastDownEventY);
                    long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                    if (iAbs > this.mTouchSlop || eventTime >= ViewConfiguration.getTapTimeout()) {
                        ensureScrollWheelAdjusted();
                    } else if (this.mPerformClickOnTap) {
                        this.mPerformClickOnTap = false;
                        performClick();
                    } else {
                        int i = (y / this.mSelectorElementHeight) - 1;
                        if (i > 0) {
                            changeValueByOne(true);
                            this.mPressedStateHelper.buttonTapped(1);
                        } else if (i < 0) {
                            changeValueByOne(false);
                            this.mPressedStateHelper.buttonTapped(2);
                        }
                    }
                    onScrollStateChange(0);
                }
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
                return true;
            case 2:
                if (!this.mIgnoreMoveEvents) {
                    float y2 = motionEvent.getY();
                    if (this.mScrollState == 1) {
                        scrollBy(0, (int) (y2 - this.mLastDownOrMoveEventY));
                        invalidate();
                    } else if (((int) Math.abs(y2 - this.mLastDownEventY)) > this.mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(1);
                    }
                    this.mLastDownOrMoveEventY = y2;
                }
                return true;
            default:
                return true;
        }
    }

    @Override // android.view.View
    public boolean performClick() {
        if (!this.mHasSelectorWheel) {
            return super.performClick();
        }
        if (super.performClick()) {
            return true;
        }
        showSoftInput();
        return true;
    }

    @Override // android.view.View
    public boolean performLongClick() {
        if (!this.mHasSelectorWheel) {
            return super.performLongClick();
        }
        if (!super.performLongClick()) {
            showSoftInput();
            this.mIgnoreMoveEvents = true;
        }
        return true;
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        int[] iArr = this.mSelectorIndices;
        int i3 = this.mCurrentScrollOffset;
        if (!this.mWrapSelectorWheel && i2 > 0 && iArr[1] <= this.mMinValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            return;
        }
        if (!this.mWrapSelectorWheel && i2 < 0 && iArr[1] >= this.mMaxValue) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            return;
        }
        this.mCurrentScrollOffset += i2;
        while (this.mCurrentScrollOffset - this.mInitialScrollOffset > this.mSelectorTextGapHeight) {
            this.mCurrentScrollOffset -= this.mSelectorElementHeight;
            decrementSelectorIndices(iArr);
            setValueInternal(iArr[1], true);
            if (!this.mWrapSelectorWheel && iArr[1] <= this.mMinValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
        while (this.mCurrentScrollOffset - this.mInitialScrollOffset < (-this.mSelectorTextGapHeight)) {
            this.mCurrentScrollOffset += this.mSelectorElementHeight;
            incrementSelectorIndices(iArr);
            setValueInternal(iArr[1], true);
            if (!this.mWrapSelectorWheel && iArr[1] >= this.mMaxValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
        if (i3 != this.mCurrentScrollOffset) {
            onScrollChanged(0, this.mCurrentScrollOffset, 0, i3);
        }
    }

    public void setDisplayedValues(String[] strArr) {
        if (this.mDisplayedValues == strArr) {
            return;
        }
        this.mDisplayedValues = strArr;
        if (this.mDisplayedValues != null) {
            this.mInputText.setRawInputType(ConnectivityManager.CALLBACK_PRECHECK);
        } else {
            this.mInputText.setRawInputType(2);
        }
        updateInputTextView();
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (!this.mHasSelectorWheel) {
            this.mIncrementButton.setEnabled(z);
        }
        if (!this.mHasSelectorWheel) {
            this.mDecrementButton.setEnabled(z);
        }
        this.mInputText.setEnabled(z);
    }

    public void setFormatter(Formatter formatter) {
        if (formatter == this.mFormatter) {
            return;
        }
        this.mFormatter = formatter;
        initializeSelectorWheelIndices();
        updateInputTextView();
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.mMaxValue = i;
        if (this.mMaxValue < this.mValue) {
            this.mValue = this.mMaxValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public void setMinValue(int i) {
        if (this.mMinValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        this.mMinValue = i;
        if (this.mMinValue > this.mValue) {
            this.mValue = this.mMinValue;
        }
        updateWrapSelectorWheel();
        initializeSelectorWheelIndices();
        updateInputTextView();
        tryComputeMaxWidth();
        invalidate();
    }

    public void setOnLongPressUpdateInterval(long j) {
        this.mLongPressUpdateInterval = j;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    public void setWrapSelectorWheel(boolean z) {
        this.mWrapSelectorWheelPreferred = z;
        updateWrapSelectorWheel();
    }
}
