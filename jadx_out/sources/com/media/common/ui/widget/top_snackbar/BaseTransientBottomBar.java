package com.media.common.ui.widget.top_snackbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.google.android.material.behavior.SwipeDismissBehavior;
import com.media.common.ui.widget.R;
import com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public abstract class BaseTransientBottomBar<B extends BaseTransientBottomBar<B>> {
    static final int ANIMATION_DURATION = 250;
    static final int ANIMATION_FADE_DURATION = 180;
    public static final int LENGTH_INDEFINITE = -2;
    public static final int LENGTH_LONG = 0;
    public static final int LENGTH_SHORT = -1;
    static final int MSG_DISMISS = 1;
    static final int MSG_SHOW = 0;
    private static final boolean USE_OFFSET_API;
    static final Handler sHandler;
    private final AccessibilityManager mAccessibilityManager;
    private List<BaseCallback<B>> mCallbacks;
    private final ContentViewCallback mContentViewCallback;
    private final Context mContext;
    private int mDuration;
    final SnackBarManager.Callback mManagerCallback = new SnackBarManager.Callback() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.3
        @Override // com.media.common.ui.widget.top_snackbar.SnackBarManager.Callback
        public void dismiss(int i) {
            BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(1, i, 0, BaseTransientBottomBar.this));
        }

        @Override // com.media.common.ui.widget.top_snackbar.SnackBarManager.Callback
        public void show() {
            BaseTransientBottomBar.sHandler.sendMessage(BaseTransientBottomBar.sHandler.obtainMessage(0, BaseTransientBottomBar.this));
        }
    };
    private final ViewGroup mTargetParent;
    final SnackbarBaseLayout mView;

    public static abstract class BaseCallback<B> {
        public static final int DISMISS_EVENT_ACTION = 1;
        public static final int DISMISS_EVENT_CONSECUTIVE = 4;
        public static final int DISMISS_EVENT_MANUAL = 3;
        public static final int DISMISS_EVENT_SWIPE = 0;
        public static final int DISMISS_EVENT_TIMEOUT = 2;

        @Retention(RetentionPolicy.SOURCE)
        @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
        public @interface DismissEvent {
        }

        public void onDismissed(B b, int i) {
        }

        public void onShown(B b) {
        }
    }

    final class Behavior extends SwipeDismissBehavior<SnackbarBaseLayout> {
        Behavior() {
        }

        @Override // com.google.android.material.behavior.SwipeDismissBehavior
        public boolean canSwipeDismissView(View view) {
            return view instanceof SnackbarBaseLayout;
        }

        /* JADX WARN: Code duplicated, block: B:9:0x0027  */
        @Override // com.google.android.material.behavior.SwipeDismissBehavior, androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
        public boolean onInterceptTouchEvent(CoordinatorLayout coordinatorLayout, SnackbarBaseLayout snackbarBaseLayout, MotionEvent motionEvent) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 3) {
                switch (actionMasked) {
                    case 0:
                        if (coordinatorLayout.isPointInChildBounds(snackbarBaseLayout, (int) motionEvent.getX(), (int) motionEvent.getY())) {
                            SnackBarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                        }
                        break;
                    case 1:
                        SnackBarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                        break;
                }
            } else {
                SnackBarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
            }
            return super.onInterceptTouchEvent(coordinatorLayout, snackbarBaseLayout, motionEvent);
        }
    }

    public interface ContentViewCallback {
        void animateContentIn(int i, int i2);

        void animateContentOut(int i, int i2);
    }

    @IntRange(from = 1)
    @Retention(RetentionPolicy.SOURCE)
    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    public @interface Duration {
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    interface OnAttachStateChangeListener {
        void onViewAttachedToWindow(View view);

        void onViewDetachedFromWindow(View view);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    interface OnLayoutChangeListener {
        void onLayoutChange(View view, int i, int i2, int i3, int i4);
    }

    @RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
    static class SnackbarBaseLayout extends FrameLayout {
        private View.OnAttachStateChangeListener mOnAttachStateChangeListener;
        private View.OnLayoutChangeListener mOnLayoutChangeListener;

        SnackbarBaseLayout(Context context) {
            this(context, null);
        }

        SnackbarBaseLayout(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SnackbarLayout);
            if (typedArrayObtainStyledAttributes.hasValue(R.styleable.SnackbarLayout_elevation)) {
                ViewCompat.setElevation(this, typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.SnackbarLayout_elevation, 0));
            }
            typedArrayObtainStyledAttributes.recycle();
            setClickable(true);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewAttachedToWindow(this);
            }
            ViewCompat.requestApplyInsets(this);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (this.mOnAttachStateChangeListener != null) {
                this.mOnAttachStateChangeListener.onViewDetachedFromWindow(this);
            }
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            if (this.mOnLayoutChangeListener != null) {
                this.mOnLayoutChangeListener.onLayoutChange(this, i, i2, i3, i4, i, i2, i3, i4);
            }
        }

        void setOnAttachStateChangeListener(View.OnAttachStateChangeListener onAttachStateChangeListener) {
            this.mOnAttachStateChangeListener = onAttachStateChangeListener;
        }

        void setOnLayoutChangeListener(View.OnLayoutChangeListener onLayoutChangeListener) {
            this.mOnLayoutChangeListener = onLayoutChangeListener;
        }
    }

    static {
        USE_OFFSET_API = Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT <= 19;
        sHandler = new Handler(Looper.getMainLooper(), new Handler.Callback() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.1
            @Override // android.os.Handler.Callback
            public boolean handleMessage(Message message) throws Throwable {
                switch (message.what) {
                    case 0:
                        ((BaseTransientBottomBar) message.obj).showView();
                        return true;
                    case 1:
                        ((BaseTransientBottomBar) message.obj).hideView(message.arg1);
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    protected BaseTransientBottomBar(@NonNull ViewGroup viewGroup, @NonNull View view, @NonNull ContentViewCallback contentViewCallback) {
        if (viewGroup == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null parent");
        }
        if (view == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null content");
        }
        if (contentViewCallback == null) {
            throw new IllegalArgumentException("Transient bottom bar must have non-null callback");
        }
        this.mTargetParent = viewGroup;
        this.mContentViewCallback = contentViewCallback;
        this.mContext = viewGroup.getContext();
        ThemeUtils.checkAppCompatTheme(this.mContext);
        this.mView = (SnackbarBaseLayout) LayoutInflater.from(this.mContext).inflate(R.layout.design_layout_top_snackbar, this.mTargetParent, false);
        this.mView.addView(view);
        ViewCompat.setAccessibilityLiveRegion(this.mView, 1);
        ViewCompat.setImportantForAccessibility(this.mView, 1);
        ViewCompat.setFitsSystemWindows(this.mView, true);
        ViewCompat.setOnApplyWindowInsetsListener(this.mView, new OnApplyWindowInsetsListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.2
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public WindowInsetsCompat onApplyWindowInsets(View view2, WindowInsetsCompat windowInsetsCompat) {
                view2.setPadding(view2.getPaddingLeft(), view2.getPaddingTop(), view2.getPaddingRight(), windowInsetsCompat.getSystemWindowInsetBottom());
                return windowInsetsCompat;
            }
        });
        this.mAccessibilityManager = (AccessibilityManager) this.mContext.getSystemService(Context.ACCESSIBILITY_SERVICE);
    }

    private void animateViewOut(final int i) throws Throwable {
        if (Build.VERSION.SDK_INT < 12) {
            Animation animationLoadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_out);
            animationLoadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            animationLoadAnimation.setDuration(250L);
            animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.12
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    BaseTransientBottomBar.this.onViewHidden(i);
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }
            });
            this.mView.startAnimation(animationLoadAnimation);
            return;
        }
        this.mView.setTranslationY(0.0f);
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(0, -this.mView.getHeight());
        valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        valueAnimator.setDuration(250L);
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.10
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                BaseTransientBottomBar.this.onViewHidden(i);
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                BaseTransientBottomBar.this.mContentViewCallback.animateContentOut(0, 180);
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.11
            private int mPreviousAnimatedIntValue = 0;

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                int iIntValue = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                if (BaseTransientBottomBar.USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, iIntValue - this.mPreviousAnimatedIntValue);
                } else {
                    BaseTransientBottomBar.this.mView.setTranslationY(iIntValue);
                }
                this.mPreviousAnimatedIntValue = iIntValue;
            }
        });
        valueAnimator.start();
    }

    @NonNull
    public B addCallback(@NonNull BaseCallback<B> baseCallback) {
        if (baseCallback == null) {
            return this;
        }
        if (this.mCallbacks == null) {
            this.mCallbacks = new ArrayList();
        }
        this.mCallbacks.add(baseCallback);
        return this;
    }

    void animateViewIn() throws Throwable {
        if (Build.VERSION.SDK_INT < 12) {
            Animation animationLoadAnimation = android.view.animation.AnimationUtils.loadAnimation(this.mView.getContext(), R.anim.design_snackbar_in);
            animationLoadAnimation.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
            animationLoadAnimation.setDuration(250L);
            animationLoadAnimation.setAnimationListener(new Animation.AnimationListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.9
                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationEnd(Animation animation) {
                    BaseTransientBottomBar.this.onViewShown();
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationRepeat(Animation animation) {
                }

                @Override // android.view.animation.Animation.AnimationListener
                public void onAnimationStart(Animation animation) {
                }
            });
            this.mView.startAnimation(animationLoadAnimation);
            return;
        }
        final int i = -this.mView.getHeight();
        if (USE_OFFSET_API) {
            ViewCompat.offsetTopAndBottom(this.mView, i);
        } else {
            this.mView.setTranslationY(i);
        }
        ValueAnimator valueAnimator = new ValueAnimator();
        valueAnimator.setIntValues(i, 0);
        valueAnimator.setInterpolator(AnimationUtils.FAST_OUT_SLOW_IN_INTERPOLATOR);
        valueAnimator.setDuration(250L);
        valueAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                BaseTransientBottomBar.this.onViewShown();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationStart(Animator animator) {
                BaseTransientBottomBar.this.mContentViewCallback.animateContentIn(70, 180);
            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.8
            private int mPreviousAnimatedIntValue;

            {
                this.mPreviousAnimatedIntValue = i;
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                int iIntValue = ((Integer) valueAnimator2.getAnimatedValue()).intValue();
                if (BaseTransientBottomBar.USE_OFFSET_API) {
                    ViewCompat.offsetTopAndBottom(BaseTransientBottomBar.this.mView, iIntValue - this.mPreviousAnimatedIntValue);
                } else {
                    BaseTransientBottomBar.this.mView.setTranslationY(iIntValue);
                }
                this.mPreviousAnimatedIntValue = iIntValue;
            }
        });
        valueAnimator.start();
    }

    public void dismiss() {
        dispatchDismiss(3);
    }

    void dispatchDismiss(int i) {
        SnackBarManager.getInstance().dismiss(this.mManagerCallback, i);
    }

    @NonNull
    public Context getContext() {
        return this.mContext;
    }

    public int getDuration() {
        return this.mDuration;
    }

    @NonNull
    public View getView() {
        return this.mView;
    }

    final void hideView(int i) throws Throwable {
        if (shouldAnimate() && this.mView.getVisibility() == 0) {
            animateViewOut(i);
        } else {
            onViewHidden(i);
        }
    }

    public boolean isShown() {
        return SnackBarManager.getInstance().isCurrent(this.mManagerCallback);
    }

    public boolean isShownOrQueued() {
        return SnackBarManager.getInstance().isCurrentOrNext(this.mManagerCallback);
    }

    void onViewHidden(int i) {
        SnackBarManager.getInstance().onDismissed(this.mManagerCallback);
        if (this.mCallbacks != null) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                this.mCallbacks.get(size).onDismissed(this, i);
            }
        }
        if (Build.VERSION.SDK_INT < 11) {
            this.mView.setVisibility(8);
        }
        ViewParent parent = this.mView.getParent();
        if (parent instanceof ViewGroup) {
            ((ViewGroup) parent).removeView(this.mView);
        }
    }

    void onViewShown() {
        SnackBarManager.getInstance().onShown(this.mManagerCallback);
        if (this.mCallbacks != null) {
            for (int size = this.mCallbacks.size() - 1; size >= 0; size--) {
                this.mCallbacks.get(size).onShown(this);
            }
        }
    }

    @NonNull
    public B removeCallback(@NonNull BaseCallback<B> baseCallback) {
        if (baseCallback == null || this.mCallbacks == null) {
            return this;
        }
        this.mCallbacks.remove(baseCallback);
        return this;
    }

    @NonNull
    public B setDuration(int i) {
        this.mDuration = i;
        return this;
    }

    boolean shouldAnimate() {
        return !this.mAccessibilityManager.isEnabled();
    }

    public void show() {
        SnackBarManager.getInstance().show(this.mDuration, this.mManagerCallback);
    }

    final void showView() throws Throwable {
        if (this.mView.getParent() == null) {
            ViewGroup.LayoutParams layoutParams = this.mView.getLayoutParams();
            if (layoutParams instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams layoutParams2 = (CoordinatorLayout.LayoutParams) layoutParams;
                Behavior behavior = new Behavior();
                behavior.setStartAlphaSwipeDistance(0.1f);
                behavior.setEndAlphaSwipeDistance(0.6f);
                behavior.setSwipeDirection(0);
                behavior.setListener(new SwipeDismissBehavior.OnDismissListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.4
                    @Override // com.google.android.material.behavior.SwipeDismissBehavior.OnDismissListener
                    public void onDismiss(View view) {
                        view.setVisibility(8);
                        BaseTransientBottomBar.this.dispatchDismiss(0);
                    }

                    @Override // com.google.android.material.behavior.SwipeDismissBehavior.OnDismissListener
                    public void onDragStateChanged(int i) {
                        switch (i) {
                            case 0:
                                SnackBarManager.getInstance().restoreTimeoutIfPaused(BaseTransientBottomBar.this.mManagerCallback);
                                break;
                            case 1:
                            case 2:
                                SnackBarManager.getInstance().pauseTimeout(BaseTransientBottomBar.this.mManagerCallback);
                                break;
                        }
                    }
                });
                layoutParams2.setBehavior(behavior);
                layoutParams2.insetEdge = 80;
            }
            this.mTargetParent.addView(this.mView);
        }
        this.mView.setOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.5
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view) {
                if (BaseTransientBottomBar.this.isShownOrQueued()) {
                    BaseTransientBottomBar.sHandler.post(new Runnable() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.5.1
                        @Override // java.lang.Runnable
                        public void run() {
                            BaseTransientBottomBar.this.onViewHidden(3);
                        }
                    });
                }
            }
        });
        if (!ViewCompat.isLaidOut(this.mView)) {
            this.mView.setOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.media.common.ui.widget.top_snackbar.BaseTransientBottomBar.6
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) throws Throwable {
                    BaseTransientBottomBar.this.mView.setOnLayoutChangeListener(null);
                    if (BaseTransientBottomBar.this.shouldAnimate()) {
                        BaseTransientBottomBar.this.animateViewIn();
                    } else {
                        BaseTransientBottomBar.this.onViewShown();
                    }
                }
            });
        } else if (shouldAnimate()) {
            animateViewIn();
        } else {
            onViewShown();
        }
    }
}
