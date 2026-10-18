package android.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.PointerIcon;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;
import com.android.internal.view.menu.ShowableListMenu;

/* JADX INFO: loaded from: classes2.dex */
public class Spinner extends AbsSpinner implements DialogInterface.OnClickListener {
    private static final int MAX_ITEMS_MEASURED = 15;
    public static final int MODE_DIALOG = 0;
    public static final int MODE_DROPDOWN = 1;
    private static final int MODE_THEME = -1;
    private static final String TAG = "Spinner";
    private boolean mDisableChildrenWhenDisabled;
    int mDropDownWidth;
    private ForwardingListener mForwardingListener;
    private int mGravity;
    private SpinnerPopup mPopup;
    private final Context mPopupContext;
    private SpinnerAdapter mTempAdapter;
    private final Rect mTempRect;

    private class DialogPopup implements SpinnerPopup, DialogInterface.OnClickListener {
        private ListAdapter mListAdapter;
        private AlertDialog mPopup;
        private CharSequence mPrompt;

        private DialogPopup() {
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void dismiss() {
            if (this.mPopup != null) {
                this.mPopup.dismiss();
                this.mPopup = null;
            }
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public Drawable getBackground() {
            return null;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mPrompt;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public int getHorizontalOffset() {
            return 0;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public int getVerticalOffset() {
            return 0;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public boolean isShowing() {
            if (this.mPopup != null) {
                return this.mPopup.isShowing();
            }
            return false;
        }

        @Override // android.content.DialogInterface.OnClickListener
        public void onClick(DialogInterface dialogInterface, int i) {
            Spinner.this.setSelection(i);
            if (Spinner.this.mOnItemClickListener != null) {
                Spinner.this.performItemClick(null, i, this.mListAdapter.getItemId(i));
            }
            dismiss();
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            this.mListAdapter = listAdapter;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setBackgroundDrawable(Drawable drawable) {
            Log.e(Spinner.TAG, "Cannot set popup background for MODE_DIALOG, ignoring");
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setHorizontalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set horizontal offset for MODE_DIALOG, ignoring");
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mPrompt = charSequence;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setVerticalOffset(int i) {
            Log.e(Spinner.TAG, "Cannot set vertical offset for MODE_DIALOG, ignoring");
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void show(int i, int i2) {
            if (this.mListAdapter == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(Spinner.this.getPopupContext());
            if (this.mPrompt != null) {
                builder.setTitle(this.mPrompt);
            }
            this.mPopup = builder.setSingleChoiceItems(this.mListAdapter, Spinner.this.getSelectedItemPosition(), this).create();
            ListView listView = this.mPopup.getListView();
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            this.mPopup.show();
        }
    }

    private static class DropDownAdapter implements ListAdapter, SpinnerAdapter {
        private SpinnerAdapter mAdapter;
        private ListAdapter mListAdapter;

        public DropDownAdapter(SpinnerAdapter spinnerAdapter, Resources.Theme theme) {
            this.mAdapter = spinnerAdapter;
            if (spinnerAdapter instanceof ListAdapter) {
                this.mListAdapter = (ListAdapter) spinnerAdapter;
            }
            if (theme == null || !(spinnerAdapter instanceof ThemedSpinnerAdapter)) {
                return;
            }
            ThemedSpinnerAdapter themedSpinnerAdapter = (ThemedSpinnerAdapter) spinnerAdapter;
            if (themedSpinnerAdapter.getDropDownViewTheme() == null) {
                themedSpinnerAdapter.setDropDownViewTheme(theme);
            }
        }

        @Override // android.widget.ListAdapter
        public boolean areAllItemsEnabled() {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.areAllItemsEnabled();
            }
            return true;
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.mAdapter == null) {
                return 0;
            }
            return this.mAdapter.getCount();
        }

        @Override // android.widget.SpinnerAdapter
        public View getDropDownView(int i, View view, ViewGroup viewGroup) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public Object getItem(int i) {
            if (this.mAdapter == null) {
                return null;
            }
            return this.mAdapter.getItem(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            if (this.mAdapter == null) {
                return -1L;
            }
            return this.mAdapter.getItemId(i);
        }

        @Override // android.widget.Adapter
        public int getItemViewType(int i) {
            return 0;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            return getDropDownView(i, view, viewGroup);
        }

        @Override // android.widget.Adapter
        public int getViewTypeCount() {
            return 1;
        }

        @Override // android.widget.Adapter
        public boolean hasStableIds() {
            return this.mAdapter != null && this.mAdapter.hasStableIds();
        }

        @Override // android.widget.Adapter
        public boolean isEmpty() {
            return getCount() == 0;
        }

        @Override // android.widget.ListAdapter
        public boolean isEnabled(int i) {
            ListAdapter listAdapter = this.mListAdapter;
            if (listAdapter != null) {
                return listAdapter.isEnabled(i);
            }
            return true;
        }

        @Override // android.widget.Adapter
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(dataSetObserver);
            }
        }

        @Override // android.widget.Adapter
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(dataSetObserver);
            }
        }
    }

    private class DropdownPopup extends ListPopupWindow implements SpinnerPopup {
        private ListAdapter mAdapter;
        private CharSequence mHintText;

        public DropdownPopup(Context context, AttributeSet attributeSet, int i, int i2) {
            super(context, attributeSet, i, i2);
            setAnchorView(Spinner.this);
            setModal(true);
            setPromptPosition(0);
            setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: android.widget.Spinner.DropdownPopup.1
                @Override // android.widget.AdapterView.OnItemClickListener
                public void onItemClick(AdapterView adapterView, View view, int i3, long j) {
                    Spinner.this.setSelection(i3);
                    if (Spinner.this.mOnItemClickListener != null) {
                        Spinner.this.performItemClick(view, i3, DropdownPopup.this.mAdapter.getItemId(i3));
                    }
                    DropdownPopup.this.dismiss();
                }
            });
        }

        void computeContentWidth() {
            Drawable background = getBackground();
            int i = 0;
            if (background != null) {
                background.getPadding(Spinner.this.mTempRect);
                i = Spinner.this.isLayoutRtl() ? Spinner.this.mTempRect.right : -Spinner.this.mTempRect.left;
            } else {
                Rect rect = Spinner.this.mTempRect;
                Spinner.this.mTempRect.right = 0;
                rect.left = 0;
            }
            int paddingLeft = Spinner.this.getPaddingLeft();
            int paddingRight = Spinner.this.getPaddingRight();
            int width = Spinner.this.getWidth();
            if (Spinner.this.mDropDownWidth == -2) {
                int iMeasureContentWidth = Spinner.this.measureContentWidth((SpinnerAdapter) this.mAdapter, getBackground());
                int i2 = (Spinner.this.mContext.getResources().getDisplayMetrics().widthPixels - Spinner.this.mTempRect.left) - Spinner.this.mTempRect.right;
                if (iMeasureContentWidth > i2) {
                    iMeasureContentWidth = i2;
                }
                setContentWidth(Math.max(iMeasureContentWidth, (width - paddingLeft) - paddingRight));
            } else if (Spinner.this.mDropDownWidth == -1) {
                setContentWidth((width - paddingLeft) - paddingRight);
            } else {
                setContentWidth(Spinner.this.mDropDownWidth);
            }
            setHorizontalOffset(Spinner.this.isLayoutRtl() ? i + ((width - paddingRight) - getWidth()) : i + paddingLeft);
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public CharSequence getHintText() {
            return this.mHintText;
        }

        @Override // android.widget.ListPopupWindow, android.widget.Spinner.SpinnerPopup
        public void setAdapter(ListAdapter listAdapter) {
            super.setAdapter(listAdapter);
            this.mAdapter = listAdapter;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void setPromptText(CharSequence charSequence) {
            this.mHintText = charSequence;
        }

        @Override // android.widget.Spinner.SpinnerPopup
        public void show(int i, int i2) {
            ViewTreeObserver viewTreeObserver;
            boolean zIsShowing = isShowing();
            computeContentWidth();
            setInputMethodMode(2);
            super.show();
            ListView listView = getListView();
            listView.setChoiceMode(1);
            listView.setTextDirection(i);
            listView.setTextAlignment(i2);
            setSelection(Spinner.this.getSelectedItemPosition());
            if (zIsShowing || (viewTreeObserver = Spinner.this.getViewTreeObserver()) == null) {
                return;
            }
            final ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.widget.Spinner.DropdownPopup.2
                @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
                public void onGlobalLayout() {
                    if (!Spinner.this.isVisibleToUser()) {
                        DropdownPopup.this.dismiss();
                    } else {
                        DropdownPopup.this.computeContentWidth();
                        DropdownPopup.super.show();
                    }
                }
            };
            viewTreeObserver.addOnGlobalLayoutListener(onGlobalLayoutListener);
            setOnDismissListener(new PopupWindow.OnDismissListener() { // from class: android.widget.Spinner.DropdownPopup.3
                @Override // android.widget.PopupWindow.OnDismissListener
                public void onDismiss() {
                    ViewTreeObserver viewTreeObserver2 = Spinner.this.getViewTreeObserver();
                    if (viewTreeObserver2 != null) {
                        viewTreeObserver2.removeOnGlobalLayoutListener(onGlobalLayoutListener);
                    }
                }
            });
        }
    }

    static class SavedState extends AbsSpinner.SavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.Spinner.SavedState.1
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
        boolean showDropdown;

        private SavedState(Parcel parcel) {
            super(parcel);
            this.showDropdown = parcel.readByte() != 0;
        }

        SavedState(Parcelable parcelable) {
            super(parcelable);
        }

        @Override // android.widget.AbsSpinner.SavedState, android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            super.writeToParcel(parcel, i);
            parcel.writeByte(this.showDropdown ? (byte) 1 : (byte) 0);
        }
    }

    private interface SpinnerPopup {
        void dismiss();

        Drawable getBackground();

        CharSequence getHintText();

        int getHorizontalOffset();

        int getVerticalOffset();

        boolean isShowing();

        void setAdapter(ListAdapter listAdapter);

        void setBackgroundDrawable(Drawable drawable);

        void setHorizontalOffset(int i);

        void setPromptText(CharSequence charSequence);

        void setVerticalOffset(int i);

        void show(int i, int i2);
    }

    public Spinner(Context context) {
        this(context, (AttributeSet) null);
    }

    public Spinner(Context context, int i) {
        this(context, null, 16842881, i);
    }

    public Spinner(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 16842881);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0, -1);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2) {
        this(context, attributeSet, i, 0, i2);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2, int i3) {
        this(context, attributeSet, i, i2, i3, null);
    }

    public Spinner(Context context, AttributeSet attributeSet, int i, int i2, int i3, Resources.Theme theme) {
        super(context, attributeSet, i, i2);
        this.mTempRect = new Rect();
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.Spinner, i, i2);
        if (theme != null) {
            this.mPopupContext = new ContextThemeWrapper(context, theme);
        } else {
            int resourceId = typedArrayObtainStyledAttributes.getResourceId(7, 0);
            if (resourceId != 0) {
                this.mPopupContext = new ContextThemeWrapper(context, resourceId);
            } else {
                this.mPopupContext = context;
            }
        }
        switch (i3 == -1 ? typedArrayObtainStyledAttributes.getInt(5, 0) : i3) {
            case 0:
                this.mPopup = new DialogPopup();
                this.mPopup.setPromptText(typedArrayObtainStyledAttributes.getString(3));
                break;
            case 1:
                final DropdownPopup dropdownPopup = new DropdownPopup(this.mPopupContext, attributeSet, i, i2);
                TypedArray typedArrayObtainStyledAttributes2 = this.mPopupContext.obtainStyledAttributes(attributeSet, R.styleable.Spinner, i, i2);
                this.mDropDownWidth = typedArrayObtainStyledAttributes2.getLayoutDimension(4, -2);
                if (typedArrayObtainStyledAttributes2.hasValueOrEmpty(1)) {
                    dropdownPopup.setListSelector(typedArrayObtainStyledAttributes2.getDrawable(1));
                }
                dropdownPopup.setBackgroundDrawable(typedArrayObtainStyledAttributes2.getDrawable(2));
                dropdownPopup.setPromptText(typedArrayObtainStyledAttributes.getString(3));
                typedArrayObtainStyledAttributes2.recycle();
                this.mPopup = dropdownPopup;
                this.mForwardingListener = new ForwardingListener(this) { // from class: android.widget.Spinner.1
                    @Override // android.widget.ForwardingListener
                    public ShowableListMenu getPopup() {
                        return dropdownPopup;
                    }

                    @Override // android.widget.ForwardingListener
                    public boolean onForwardingStarted() {
                        if (Spinner.this.mPopup.isShowing()) {
                            return true;
                        }
                        Spinner.this.mPopup.show(Spinner.this.getTextDirection(), Spinner.this.getTextAlignment());
                        return true;
                    }
                };
                break;
        }
        this.mGravity = typedArrayObtainStyledAttributes.getInt(0, 17);
        this.mDisableChildrenWhenDisabled = typedArrayObtainStyledAttributes.getBoolean(8, false);
        typedArrayObtainStyledAttributes.recycle();
        if (this.mTempAdapter != null) {
            setAdapter(this.mTempAdapter);
            this.mTempAdapter = null;
        }
    }

    private View makeView(int i, boolean z) {
        View view;
        if (!this.mDataChanged && (view = this.mRecycler.get(i)) != null) {
            setUpChild(view, z);
            return view;
        }
        View view2 = this.mAdapter.getView(i, null, this);
        setUpChild(view2, z);
        return view2;
    }

    private void setUpChild(View view, boolean z) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = generateDefaultLayoutParams();
        }
        addViewInLayout(view, 0, layoutParams);
        view.setSelected(hasFocus());
        if (this.mDisableChildrenWhenDisabled) {
            view.setEnabled(isEnabled());
        }
        view.measure(ViewGroup.getChildMeasureSpec(this.mWidthMeasureSpec, this.mSpinnerPadding.left + this.mSpinnerPadding.right, layoutParams.width), ViewGroup.getChildMeasureSpec(this.mHeightMeasureSpec, this.mSpinnerPadding.top + this.mSpinnerPadding.bottom, layoutParams.height));
        int measuredHeight = this.mSpinnerPadding.top + ((((getMeasuredHeight() - this.mSpinnerPadding.bottom) - this.mSpinnerPadding.top) - view.getMeasuredHeight()) / 2);
        view.layout(0, measuredHeight, view.getMeasuredWidth() + 0, view.getMeasuredHeight() + measuredHeight);
        if (z) {
            return;
        }
        removeViewInLayout(view);
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return Spinner.class.getName();
    }

    @Override // android.view.View
    public int getBaseline() {
        View childAt;
        int baseline;
        if (getChildCount() > 0) {
            childAt = getChildAt(0);
        } else if (this.mAdapter == null || this.mAdapter.getCount() <= 0) {
            childAt = null;
        } else {
            View viewMakeView = makeView(0, false);
            this.mRecycler.put(0, viewMakeView);
            childAt = viewMakeView;
        }
        if (childAt == null || (baseline = childAt.getBaseline()) < 0) {
            return -1;
        }
        return childAt.getTop() + baseline;
    }

    public int getDropDownHorizontalOffset() {
        return this.mPopup.getHorizontalOffset();
    }

    public int getDropDownVerticalOffset() {
        return this.mPopup.getVerticalOffset();
    }

    public int getDropDownWidth() {
        return this.mDropDownWidth;
    }

    public int getGravity() {
        return this.mGravity;
    }

    public Drawable getPopupBackground() {
        return this.mPopup.getBackground();
    }

    public Context getPopupContext() {
        return this.mPopupContext;
    }

    public CharSequence getPrompt() {
        return this.mPopup.getHintText();
    }

    public boolean isPopupShowing() {
        return this.mPopup != null && this.mPopup.isShowing();
    }

    @Override // android.widget.AbsSpinner
    void layout(int i, boolean z) {
        int i2 = this.mSpinnerPadding.left;
        int i3 = ((this.mRight - this.mLeft) - this.mSpinnerPadding.left) - this.mSpinnerPadding.right;
        if (this.mDataChanged) {
            handleDataChanged();
        }
        if (this.mItemCount == 0) {
            resetList();
            return;
        }
        if (this.mNextSelectedPosition >= 0) {
            setSelectedPositionInt(this.mNextSelectedPosition);
        }
        recycleAllViews();
        removeAllViewsInLayout();
        this.mFirstPosition = this.mSelectedPosition;
        if (this.mAdapter != null) {
            View viewMakeView = makeView(this.mSelectedPosition, true);
            int measuredWidth = viewMakeView.getMeasuredWidth();
            int absoluteGravity = Gravity.getAbsoluteGravity(this.mGravity, getLayoutDirection()) & 7;
            if (absoluteGravity == 1) {
                i2 = (i2 + (i3 / 2)) - (measuredWidth / 2);
            } else if (absoluteGravity == 5) {
                i2 = (i2 + i3) - measuredWidth;
            }
            viewMakeView.offsetLeftAndRight(i2);
        }
        this.mRecycler.clear();
        invalidate();
        checkSelectionChanged();
        this.mDataChanged = false;
        this.mNeedSync = false;
        setNextSelectedPositionInt(this.mSelectedPosition);
    }

    int measureContentWidth(SpinnerAdapter spinnerAdapter, Drawable drawable) {
        int i = 0;
        if (spinnerAdapter == null) {
            return 0;
        }
        int iMakeSafeMeasureSpec = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredWidth(), 0);
        int iMakeSafeMeasureSpec2 = View.MeasureSpec.makeSafeMeasureSpec(getMeasuredHeight(), 0);
        int iMax = Math.max(0, getSelectedItemPosition());
        int iMin = Math.min(spinnerAdapter.getCount(), iMax + 15);
        int iMax2 = 0;
        View view = null;
        for (int iMax3 = Math.max(0, iMax - (15 - (iMin - iMax))); iMax3 < iMin; iMax3++) {
            int itemViewType = spinnerAdapter.getItemViewType(iMax3);
            if (itemViewType != i) {
                view = null;
                i = itemViewType;
            }
            view = spinnerAdapter.getView(iMax3, view, this);
            if (view.getLayoutParams() == null) {
                view.setLayoutParams(new ViewGroup.LayoutParams(-2, -2));
            }
            view.measure(iMakeSafeMeasureSpec, iMakeSafeMeasureSpec2);
            iMax2 = Math.max(iMax2, view.getMeasuredWidth());
        }
        if (drawable == null) {
            return iMax2;
        }
        drawable.getPadding(this.mTempRect);
        return iMax2 + this.mTempRect.left + this.mTempRect.right;
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialogInterface, int i) {
        setSelection(i);
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mPopup == null || !this.mPopup.isShowing()) {
            return;
        }
        this.mPopup.dismiss();
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfoInternal(accessibilityNodeInfo);
        if (this.mAdapter != null) {
            accessibilityNodeInfo.setCanOpenPopup(true);
        }
    }

    @Override // android.widget.AdapterView, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mInLayout = true;
        layout(0, false);
        this.mInLayout = false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsSpinner, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        if (this.mPopup == null || View.MeasureSpec.getMode(i) != Integer.MIN_VALUE) {
            return;
        }
        setMeasuredDimension(Math.min(Math.max(getMeasuredWidth(), measureContentWidth(getAdapter(), getBackground())), View.MeasureSpec.getSize(i)), getMeasuredHeight());
    }

    @Override // android.view.ViewGroup, android.view.View
    public PointerIcon onResolvePointerIcon(MotionEvent motionEvent, int i) {
        return (getPointerIcon() == null && isClickable() && isEnabled()) ? PointerIcon.getSystemIcon(getContext(), 1002) : super.onResolvePointerIcon(motionEvent, i);
    }

    @Override // android.widget.AbsSpinner, android.view.View
    public void onRestoreInstanceState(Parcelable parcelable) {
        ViewTreeObserver viewTreeObserver;
        SavedState savedState = (SavedState) parcelable;
        super.onRestoreInstanceState(savedState.getSuperState());
        if (!savedState.showDropdown || (viewTreeObserver = getViewTreeObserver()) == null) {
            return;
        }
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() { // from class: android.widget.Spinner.2
            @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
            public void onGlobalLayout() {
                if (!Spinner.this.mPopup.isShowing()) {
                    Spinner.this.mPopup.show(Spinner.this.getTextDirection(), Spinner.this.getTextAlignment());
                }
                ViewTreeObserver viewTreeObserver2 = Spinner.this.getViewTreeObserver();
                if (viewTreeObserver2 != null) {
                    viewTreeObserver2.removeOnGlobalLayoutListener(this);
                }
            }
        });
    }

    @Override // android.widget.AbsSpinner, android.view.View
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.showDropdown = this.mPopup != null && this.mPopup.isShowing();
        return savedState;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mForwardingListener == null || !this.mForwardingListener.onTouch(this, motionEvent)) {
            return super.onTouchEvent(motionEvent);
        }
        return true;
    }

    @Override // android.view.View
    public boolean performClick() {
        boolean zPerformClick = super.performClick();
        if (!zPerformClick) {
            zPerformClick = true;
            if (!this.mPopup.isShowing()) {
                this.mPopup.show(getTextDirection(), getTextAlignment());
            }
        }
        return zPerformClick;
    }

    @Override // android.widget.AbsSpinner, android.widget.AdapterView
    public void setAdapter(SpinnerAdapter spinnerAdapter) {
        if (this.mPopup == null) {
            this.mTempAdapter = spinnerAdapter;
            return;
        }
        super.setAdapter(spinnerAdapter);
        this.mRecycler.clear();
        if (this.mContext.getApplicationInfo().targetSdkVersion >= 21 && spinnerAdapter != null && spinnerAdapter.getViewTypeCount() != 1) {
            throw new IllegalArgumentException("Spinner adapter view type count must be 1");
        }
        this.mPopup.setAdapter(new DropDownAdapter(spinnerAdapter, (this.mPopupContext == null ? this.mContext : this.mPopupContext).getTheme()));
    }

    public void setDropDownHorizontalOffset(int i) {
        this.mPopup.setHorizontalOffset(i);
    }

    public void setDropDownVerticalOffset(int i) {
        this.mPopup.setVerticalOffset(i);
    }

    public void setDropDownWidth(int i) {
        if (this.mPopup instanceof DropdownPopup) {
            this.mDropDownWidth = i;
        } else {
            Log.e(TAG, "Cannot set dropdown width for MODE_DIALOG, ignoring");
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        if (this.mDisableChildrenWhenDisabled) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).setEnabled(z);
            }
        }
    }

    public void setGravity(int i) {
        if (this.mGravity != i) {
            if ((i & 7) == 0) {
                i |= 8388611;
            }
            this.mGravity = i;
            requestLayout();
        }
    }

    @Override // android.widget.AdapterView
    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        throw new RuntimeException("setOnItemClickListener cannot be used with a spinner.");
    }

    public void setOnItemClickListenerInt(AdapterView.OnItemClickListener onItemClickListener) {
        super.setOnItemClickListener(onItemClickListener);
    }

    public void setPopupBackgroundDrawable(Drawable drawable) {
        if (this.mPopup instanceof DropdownPopup) {
            this.mPopup.setBackgroundDrawable(drawable);
        } else {
            Log.e(TAG, "setPopupBackgroundDrawable: incompatible spinner mode; ignoring...");
        }
    }

    public void setPopupBackgroundResource(int i) {
        setPopupBackgroundDrawable(getPopupContext().getDrawable(i));
    }

    public void setPrompt(CharSequence charSequence) {
        this.mPopup.setPromptText(charSequence);
    }

    public void setPromptId(int i) {
        setPrompt(getContext().getText(i));
    }
}
