package android.widget;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import com.android.internal.widget.AutoScrollHelper;

/* JADX INFO: loaded from: classes2.dex */
public class DropDownListView extends ListView {
    private boolean mDrawsInPressedState;
    private boolean mHijackFocus;
    private boolean mListSelectionHidden;
    private ResolveHoverRunnable mResolveHoverRunnable;
    private AutoScrollHelper.AbsListViewAutoScroller mScrollHelper;

    private class ResolveHoverRunnable implements Runnable {
        private ResolveHoverRunnable() {
        }

        public void cancel() {
            DropDownListView.this.mResolveHoverRunnable = null;
            DropDownListView.this.removeCallbacks(this);
        }

        public void post() {
            DropDownListView.this.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            DropDownListView.this.mResolveHoverRunnable = null;
            DropDownListView.this.drawableStateChanged();
        }
    }

    public DropDownListView(Context context, boolean z) {
        this(context, z, 16842861);
    }

    public DropDownListView(Context context, boolean z, int i) {
        super(context, null, i);
        this.mHijackFocus = z;
        setCacheColorHint(0);
    }

    private void clearPressedItem() {
        this.mDrawsInPressedState = false;
        setPressed(false);
        updateSelectorState();
        View childAt = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (childAt != null) {
            childAt.setPressed(false);
        }
    }

    private void setPressedItem(View view, int i, float f, float f2) {
        this.mDrawsInPressedState = true;
        drawableHotspotChanged(f, f2);
        if (!isPressed()) {
            setPressed(true);
        }
        if (this.mDataChanged) {
            layoutChildren();
        }
        View childAt = getChildAt(this.mMotionPosition - this.mFirstPosition);
        if (childAt != null && childAt != view && childAt.isPressed()) {
            childAt.setPressed(false);
        }
        this.mMotionPosition = i;
        view.drawableHotspotChanged(f - view.getLeft(), f2 - view.getTop());
        if (!view.isPressed()) {
            view.setPressed(true);
        }
        setSelectedPositionInt(i);
        positionSelectorLikeTouch(i, view, f, f2);
        refreshDrawableState();
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        if (this.mResolveHoverRunnable == null) {
            super.drawableStateChanged();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean hasFocus() {
        return this.mHijackFocus || super.hasFocus();
    }

    @Override // android.view.View
    public boolean hasWindowFocus() {
        return this.mHijackFocus || super.hasWindowFocus();
    }

    @Override // android.view.View
    public boolean isFocused() {
        return this.mHijackFocus || super.isFocused();
    }

    @Override // android.view.View
    public boolean isInTouchMode() {
        return (this.mHijackFocus && this.mListSelectionHidden) || super.isInTouchMode();
    }

    @Override // android.widget.AbsListView
    View obtainView(int i, boolean[] zArr) {
        View viewObtainView = super.obtainView(i, zArr);
        if (viewObtainView instanceof TextView) {
            ((TextView) viewObtainView).setHorizontallyScrolling(true);
        }
        return viewObtainView;
    }

    /* JADX WARN: Code duplicated, block: B:11:0x0019  */
    /* JADX WARN: Code duplicated, block: B:13:0x002a  */
    /* JADX WARN: Code duplicated, block: B:14:0x002c  */
    /* JADX WARN: Code duplicated, block: B:16:0x003d  */
    /* JADX WARN: Code duplicated, block: B:5:0x000c  */
    public boolean onForwardedEvent(MotionEvent motionEvent, int i) {
        boolean z;
        int iFindPointerIndex;
        int x;
        int y;
        int iPointToPosition;
        View childAt;
        boolean z2;
        int actionMasked = motionEvent.getActionMasked();
        switch (actionMasked) {
            case 1:
                z = false;
                iFindPointerIndex = motionEvent.findPointerIndex(i);
                if (iFindPointerIndex < 0) {
                    x = (int) motionEvent.getX(iFindPointerIndex);
                    y = (int) motionEvent.getY(iFindPointerIndex);
                    iPointToPosition = pointToPosition(x, y);
                    if (iPointToPosition == -1) {
                        childAt = getChildAt(iPointToPosition - getFirstVisiblePosition());
                        setPressedItem(childAt, iPointToPosition, x, y);
                        if (actionMasked == 1) {
                            performItemClick(childAt, iPointToPosition, getItemIdAtPosition(iPointToPosition));
                        }
                        z2 = false;
                        z = true;
                    } else {
                        z2 = true;
                    }
                } else {
                    z2 = false;
                    z = false;
                }
                break;
            case 2:
                z = true;
                iFindPointerIndex = motionEvent.findPointerIndex(i);
                if (iFindPointerIndex < 0) {
                    x = (int) motionEvent.getX(iFindPointerIndex);
                    y = (int) motionEvent.getY(iFindPointerIndex);
                    iPointToPosition = pointToPosition(x, y);
                    if (iPointToPosition == -1) {
                        childAt = getChildAt(iPointToPosition - getFirstVisiblePosition());
                        setPressedItem(childAt, iPointToPosition, x, y);
                        if (actionMasked == 1) {
                            performItemClick(childAt, iPointToPosition, getItemIdAtPosition(iPointToPosition));
                        }
                        z2 = false;
                        z = true;
                    } else {
                        z2 = true;
                    }
                } else {
                    z2 = false;
                    z = false;
                }
                break;
            case 3:
                z2 = false;
                z = false;
                break;
            default:
                z2 = false;
                z = true;
                break;
        }
        if (!z || z2) {
            clearPressedItem();
        }
        if (z) {
            if (this.mScrollHelper == null) {
                this.mScrollHelper = new AutoScrollHelper.AbsListViewAutoScroller(this);
            }
            this.mScrollHelper.setEnabled(true);
            this.mScrollHelper.onTouch(this, motionEvent);
        } else if (this.mScrollHelper != null) {
            this.mScrollHelper.setEnabled(false);
        }
        return z;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 10 && this.mResolveHoverRunnable == null) {
            this.mResolveHoverRunnable = new ResolveHoverRunnable();
            this.mResolveHoverRunnable.post();
        }
        boolean zOnHoverEvent = super.onHoverEvent(motionEvent);
        if (actionMasked == 9 || actionMasked == 7) {
            int iPointToPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
            if (iPointToPosition != -1 && iPointToPosition != this.mSelectedPosition) {
                View childAt = getChildAt(iPointToPosition - getFirstVisiblePosition());
                if (childAt.isEnabled()) {
                    requestFocus();
                    positionSelector(iPointToPosition, childAt);
                    setSelectedPositionInt(iPointToPosition);
                    setNextSelectedPositionInt(iPointToPosition);
                }
                updateSelectorState();
            }
        } else if (!super.shouldShowSelector()) {
            setSelectedPositionInt(-1);
            setNextSelectedPositionInt(-1);
        }
        return zOnHoverEvent;
    }

    @Override // android.widget.AbsListView, android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.mResolveHoverRunnable != null) {
            this.mResolveHoverRunnable.cancel();
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setListSelectionHidden(boolean z) {
        this.mListSelectionHidden = z;
    }

    @Override // android.widget.AbsListView
    boolean shouldShowSelector() {
        return isHovered() || super.shouldShowSelector();
    }

    @Override // android.widget.AbsListView
    boolean touchModeDrawsInPressedState() {
        return this.mDrawsInPressedState || super.touchModeDrawsInPressedState();
    }
}
