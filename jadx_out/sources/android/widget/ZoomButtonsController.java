package android.widget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewRootImpl;
import android.view.WindowManager;
import com.android.internal.R;
import com.google.android.material.badge.BadgeDrawable;

/* JADX INFO: loaded from: classes2.dex */
@Deprecated
public class ZoomButtonsController implements View.OnTouchListener {
    private static final int MSG_DISMISS_ZOOM_CONTROLS = 3;
    private static final int MSG_POST_CONFIGURATION_CHANGED = 2;
    private static final int MSG_POST_SET_VISIBLE = 4;
    private static final String TAG = "ZoomButtonsController";
    private static final int ZOOM_CONTROLS_TIMEOUT = (int) ViewConfiguration.getZoomControlsTimeout();
    private static final int ZOOM_CONTROLS_TOUCH_PADDING = 20;
    private OnZoomListener mCallback;
    private final FrameLayout mContainer;
    private WindowManager.LayoutParams mContainerLayoutParams;
    private final Context mContext;
    private ZoomControls mControls;
    private boolean mIsVisible;
    private final View mOwnerView;
    private Runnable mPostedVisibleInitializer;
    private boolean mReleaseTouchListenerOnUp;
    private int mTouchPaddingScaledSq;
    private View mTouchTargetView;
    private final WindowManager mWindowManager;
    private boolean mAutoDismissControls = true;
    private final int[] mOwnerViewRawLocation = new int[2];
    private final int[] mContainerRawLocation = new int[2];
    private final int[] mTouchTargetWindowLocation = new int[2];
    private final Rect mTempRect = new Rect();
    private final int[] mTempIntArray = new int[2];
    private final IntentFilter mConfigurationChangedFilter = new IntentFilter(Intent.ACTION_CONFIGURATION_CHANGED);
    private final BroadcastReceiver mConfigurationChangedReceiver = new BroadcastReceiver() { // from class: android.widget.ZoomButtonsController.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (ZoomButtonsController.this.mIsVisible) {
                ZoomButtonsController.this.mHandler.removeMessages(2);
                ZoomButtonsController.this.mHandler.sendEmptyMessage(2);
            }
        }
    };
    private final Handler mHandler = new Handler() { // from class: android.widget.ZoomButtonsController.2
        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 2:
                    ZoomButtonsController.this.onPostConfigurationChanged();
                    break;
                case 3:
                    ZoomButtonsController.this.setVisible(false);
                    break;
                case 4:
                    if (ZoomButtonsController.this.mOwnerView.getWindowToken() != null) {
                        ZoomButtonsController.this.setVisible(true);
                    } else {
                        Log.e(ZoomButtonsController.TAG, "Cannot make the zoom controller visible if the owner view is not attached to a window.");
                    }
                    break;
            }
        }
    };

    private class Container extends FrameLayout {
        public Container(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            if (ZoomButtonsController.this.onContainerKey(keyEvent)) {
                return true;
            }
            return super.dispatchKeyEvent(keyEvent);
        }
    }

    public interface OnZoomListener {
        void onVisibilityChanged(boolean z);

        void onZoom(boolean z);
    }

    public ZoomButtonsController(View view) {
        this.mContext = view.getContext();
        this.mWindowManager = (WindowManager) this.mContext.getSystemService(Context.WINDOW_SERVICE);
        this.mOwnerView = view;
        this.mTouchPaddingScaledSq = (int) (this.mContext.getResources().getDisplayMetrics().density * 20.0f);
        this.mTouchPaddingScaledSq *= this.mTouchPaddingScaledSq;
        this.mContainer = createContainer();
    }

    private FrameLayout createContainer() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(-2, -2);
        layoutParams.gravity = BadgeDrawable.TOP_START;
        layoutParams.flags = 131608;
        layoutParams.height = -2;
        layoutParams.width = -1;
        layoutParams.type = 1000;
        layoutParams.format = -3;
        layoutParams.windowAnimations = R.style.Animation_ZoomButtons;
        this.mContainerLayoutParams = layoutParams;
        Container container = new Container(this.mContext);
        container.setLayoutParams(layoutParams);
        container.setMeasureAllChildren(true);
        ((LayoutInflater) this.mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoom_container, container);
        this.mControls = (ZoomControls) container.findViewById(R.id.zoomControls);
        this.mControls.setOnZoomInClickListener(new View.OnClickListener() { // from class: android.widget.ZoomButtonsController.3
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ZoomButtonsController.this.dismissControlsDelayed(ZoomButtonsController.ZOOM_CONTROLS_TIMEOUT);
                if (ZoomButtonsController.this.mCallback != null) {
                    ZoomButtonsController.this.mCallback.onZoom(true);
                }
            }
        });
        this.mControls.setOnZoomOutClickListener(new View.OnClickListener() { // from class: android.widget.ZoomButtonsController.4
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                ZoomButtonsController.this.dismissControlsDelayed(ZoomButtonsController.ZOOM_CONTROLS_TIMEOUT);
                if (ZoomButtonsController.this.mCallback != null) {
                    ZoomButtonsController.this.mCallback.onZoom(false);
                }
            }
        });
        return container;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dismissControlsDelayed(int i) {
        if (this.mAutoDismissControls) {
            this.mHandler.removeMessages(3);
            this.mHandler.sendEmptyMessageDelayed(3, i);
        }
    }

    private View findViewForTouch(int i, int i2) {
        int i3 = i - this.mContainerRawLocation[0];
        int i4 = i2 - this.mContainerRawLocation[1];
        Rect rect = this.mTempRect;
        View view = null;
        int i5 = Integer.MAX_VALUE;
        for (int childCount = this.mContainer.getChildCount() - 1; childCount >= 0; childCount--) {
            View childAt = this.mContainer.getChildAt(childCount);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(rect);
                if (rect.contains(i3, i4)) {
                    return childAt;
                }
                int iMin = (i3 < rect.left || i3 > rect.right) ? Math.min(Math.abs(rect.left - i3), Math.abs(i3 - rect.right)) : 0;
                int iMin2 = (i4 < rect.top || i4 > rect.bottom) ? Math.min(Math.abs(rect.top - i4), Math.abs(i4 - rect.bottom)) : 0;
                int i6 = (iMin * iMin) + (iMin2 * iMin2);
                if (i6 < this.mTouchPaddingScaledSq && i6 < i5) {
                    view = childAt;
                    i5 = i6;
                }
            }
        }
        return view;
    }

    private boolean isInterestingKey(int i) {
        if (i == 4 || i == 66) {
            return true;
        }
        switch (i) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
                return true;
            default:
                return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean onContainerKey(KeyEvent keyEvent) {
        KeyEvent.DispatcherState keyDispatcherState;
        int keyCode = keyEvent.getKeyCode();
        if (!isInterestingKey(keyCode)) {
            ViewRootImpl viewRootImpl = this.mOwnerView.getViewRootImpl();
            if (viewRootImpl != null) {
                viewRootImpl.dispatchInputEvent(keyEvent);
            }
            return true;
        }
        if (keyCode != 4) {
            dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
        } else {
            if (keyEvent.getAction() == 0 && keyEvent.getRepeatCount() == 0) {
                if (this.mOwnerView != null && (keyDispatcherState = this.mOwnerView.getKeyDispatcherState()) != null) {
                    keyDispatcherState.startTracking(keyEvent, this);
                }
                return true;
            }
            if (keyEvent.getAction() == 1 && keyEvent.isTracking() && !keyEvent.isCanceled()) {
                setVisible(false);
                return true;
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPostConfigurationChanged() {
        dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
        refreshPositioningVariables();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refreshPositioningVariables() {
        if (this.mOwnerView.getWindowToken() == null) {
            return;
        }
        int height = this.mOwnerView.getHeight();
        int width = this.mOwnerView.getWidth();
        int height2 = height - this.mContainer.getHeight();
        this.mOwnerView.getLocationOnScreen(this.mOwnerViewRawLocation);
        this.mContainerRawLocation[0] = this.mOwnerViewRawLocation[0];
        this.mContainerRawLocation[1] = this.mOwnerViewRawLocation[1] + height2;
        int[] iArr = this.mTempIntArray;
        this.mOwnerView.getLocationInWindow(iArr);
        this.mContainerLayoutParams.x = iArr[0];
        this.mContainerLayoutParams.width = width;
        this.mContainerLayoutParams.y = iArr[1] + height2;
        if (this.mIsVisible) {
            this.mWindowManager.updateViewLayout(this.mContainer, this.mContainerLayoutParams);
        }
    }

    private void setTouchTargetView(View view) {
        this.mTouchTargetView = view;
        if (view != null) {
            view.getLocationInWindow(this.mTouchTargetWindowLocation);
        }
    }

    public ViewGroup getContainer() {
        return this.mContainer;
    }

    public View getZoomControls() {
        return this.mControls;
    }

    public boolean isAutoDismissed() {
        return this.mAutoDismissControls;
    }

    public boolean isVisible() {
        return this.mIsVisible;
    }

    /* JADX WARN: Code duplicated, block: B:16:0x0041  */
    @Override // android.view.View.OnTouchListener
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (motionEvent.getPointerCount() > 1) {
            return false;
        }
        if (this.mReleaseTouchListenerOnUp) {
            if (action == 1 || action == 3) {
                this.mOwnerView.setOnTouchListener(null);
                setTouchTargetView(null);
                this.mReleaseTouchListenerOnUp = false;
            }
            return true;
        }
        dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
        View viewFindViewForTouch = this.mTouchTargetView;
        if (action != 3) {
            switch (action) {
                case 0:
                    viewFindViewForTouch = findViewForTouch((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
                    setTouchTargetView(viewFindViewForTouch);
                    break;
                case 1:
                    setTouchTargetView(null);
                    break;
            }
        } else {
            setTouchTargetView(null);
        }
        if (viewFindViewForTouch == null) {
            return false;
        }
        int i = this.mContainerRawLocation[0] + this.mTouchTargetWindowLocation[0];
        int i2 = this.mContainerRawLocation[1] + this.mTouchTargetWindowLocation[1];
        MotionEvent motionEventObtain = MotionEvent.obtain(motionEvent);
        motionEventObtain.offsetLocation(this.mOwnerViewRawLocation[0] - i, this.mOwnerViewRawLocation[1] - i2);
        float x = motionEventObtain.getX();
        float y = motionEventObtain.getY();
        if (x < 0.0f && x > -20.0f) {
            motionEventObtain.offsetLocation(-x, 0.0f);
        }
        if (y < 0.0f && y > -20.0f) {
            motionEventObtain.offsetLocation(0.0f, -y);
        }
        boolean zDispatchTouchEvent = viewFindViewForTouch.dispatchTouchEvent(motionEventObtain);
        motionEventObtain.recycle();
        return zDispatchTouchEvent;
    }

    public void setAutoDismissed(boolean z) {
        if (this.mAutoDismissControls == z) {
            return;
        }
        this.mAutoDismissControls = z;
    }

    public void setFocusable(boolean z) {
        int i = this.mContainerLayoutParams.flags;
        if (z) {
            this.mContainerLayoutParams.flags &= -9;
        } else {
            this.mContainerLayoutParams.flags |= 8;
        }
        if (this.mContainerLayoutParams.flags == i || !this.mIsVisible) {
            return;
        }
        this.mWindowManager.updateViewLayout(this.mContainer, this.mContainerLayoutParams);
    }

    public void setOnZoomListener(OnZoomListener onZoomListener) {
        this.mCallback = onZoomListener;
    }

    public void setVisible(boolean z) {
        if (z) {
            if (this.mOwnerView.getWindowToken() == null) {
                if (this.mHandler.hasMessages(4)) {
                    return;
                }
                this.mHandler.sendEmptyMessage(4);
                return;
            }
            dismissControlsDelayed(ZOOM_CONTROLS_TIMEOUT);
        }
        if (this.mIsVisible == z) {
            return;
        }
        this.mIsVisible = z;
        if (!z) {
            if (this.mTouchTargetView != null) {
                this.mReleaseTouchListenerOnUp = true;
            } else {
                this.mOwnerView.setOnTouchListener(null);
            }
            this.mContext.unregisterReceiver(this.mConfigurationChangedReceiver);
            this.mWindowManager.removeViewImmediate(this.mContainer);
            this.mHandler.removeCallbacks(this.mPostedVisibleInitializer);
            if (this.mCallback != null) {
                this.mCallback.onVisibilityChanged(false);
                return;
            }
            return;
        }
        if (this.mContainerLayoutParams.token == null) {
            this.mContainerLayoutParams.token = this.mOwnerView.getWindowToken();
        }
        this.mWindowManager.addView(this.mContainer, this.mContainerLayoutParams);
        if (this.mPostedVisibleInitializer == null) {
            this.mPostedVisibleInitializer = new Runnable() { // from class: android.widget.ZoomButtonsController.5
                @Override // java.lang.Runnable
                public void run() {
                    ZoomButtonsController.this.refreshPositioningVariables();
                    if (ZoomButtonsController.this.mCallback != null) {
                        ZoomButtonsController.this.mCallback.onVisibilityChanged(true);
                    }
                }
            };
        }
        this.mHandler.post(this.mPostedVisibleInitializer);
        this.mContext.registerReceiver(this.mConfigurationChangedReceiver, this.mConfigurationChangedFilter);
        this.mOwnerView.setOnTouchListener(this);
        this.mReleaseTouchListenerOnUp = false;
    }

    public void setZoomInEnabled(boolean z) {
        this.mControls.setIsZoomInEnabled(z);
    }

    public void setZoomOutEnabled(boolean z) {
        this.mControls.setIsZoomOutEnabled(z);
    }

    public void setZoomSpeed(long j) {
        this.mControls.setZoomSpeed(j);
    }
}
