package com.android.internal.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.util.IntProperty;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;

/* JADX INFO: loaded from: classes3.dex */
public class MessagingPropertyAnimator implements View.OnLayoutChangeListener {
    private static final long APPEAR_ANIMATION_LENGTH = 210;
    private static final int TAG_ALPHA_ANIMATOR = 16909368;
    private static final int TAG_FIRST_LAYOUT = 16909369;
    private static final int TAG_LAYOUT_TOP = 16909370;
    private static final int TAG_TOP = 16909372;
    private static final int TAG_TOP_ANIMATOR = 16909371;
    private static final Interpolator ALPHA_IN = new PathInterpolator(0.4f, 0.0f, 1.0f, 1.0f);
    public static final Interpolator ALPHA_OUT = new PathInterpolator(0.0f, 0.0f, 0.8f, 1.0f);
    private static final ViewClippingUtil.ClippingParameters CLIPPING_PARAMETERS = new ViewClippingUtil.ClippingParameters() { // from class: com.android.internal.widget.-$$Lambda$MessagingPropertyAnimator$7coWc0tjIUC7grCXucNFbpYTxDI
        @Override // com.android.internal.widget.ViewClippingUtil.ClippingParameters
        public final boolean shouldFinish(View view) {
            return MessagingPropertyAnimator.lambda$static$0(view);
        }
    };
    private static final IntProperty<View> TOP = new IntProperty<View>("top") { // from class: com.android.internal.widget.MessagingPropertyAnimator.1
        @Override // android.util.Property
        public Integer get(View view) {
            return Integer.valueOf(MessagingPropertyAnimator.getTop(view));
        }

        @Override // android.util.IntProperty
        public void setValue(View view, int i) {
            MessagingPropertyAnimator.setTop(view, i);
        }
    };

    public static void fadeIn(final View view) {
        ObjectAnimator objectAnimator = (ObjectAnimator) view.getTag(16909368);
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (view.getVisibility() == 4) {
            view.setVisibility(0);
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f);
        view.setAlpha(0.0f);
        objectAnimatorOfFloat.setInterpolator(ALPHA_IN);
        objectAnimatorOfFloat.setDuration(APPEAR_ANIMATION_LENGTH);
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.widget.MessagingPropertyAnimator.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setTagInternal(16909368, null);
                MessagingPropertyAnimator.updateLayerType(view, false);
            }
        });
        updateLayerType(view, true);
        view.setTagInternal(16909368, objectAnimatorOfFloat);
        objectAnimatorOfFloat.start();
    }

    public static void fadeOut(final View view, final Runnable runnable) {
        ObjectAnimator objectAnimator = (ObjectAnimator) view.getTag(16909368);
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (!view.isShown() || (MessagingLinearLayout.isGone(view) && !isHidingAnimated(view))) {
            view.setAlpha(0.0f);
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, View.ALPHA, view.getAlpha(), 0.0f);
        objectAnimatorOfFloat.setInterpolator(ALPHA_OUT);
        objectAnimatorOfFloat.setDuration(APPEAR_ANIMATION_LENGTH);
        objectAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.widget.MessagingPropertyAnimator.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setTagInternal(16909368, null);
                MessagingPropertyAnimator.updateLayerType(view, false);
                if (runnable != null) {
                    runnable.run();
                }
            }
        });
        updateLayerType(view, true);
        view.setTagInternal(16909368, objectAnimatorOfFloat);
        objectAnimatorOfFloat.start();
    }

    public static int getLayoutTop(View view) {
        Integer num = (Integer) view.getTag(16909370);
        return num == null ? getTop(view) : num.intValue();
    }

    public static int getTop(View view) {
        Integer num = (Integer) view.getTag(16909372);
        return num == null ? view.getTop() : num.intValue();
    }

    public static boolean isAnimatingAlpha(View view) {
        return view.getTag(16909368) != null;
    }

    public static boolean isAnimatingTranslation(View view) {
        return view.getTag(16909371) != null;
    }

    private static boolean isFirstLayout(View view) {
        Boolean bool = (Boolean) view.getTag(16909369);
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static boolean isHidingAnimated(View view) {
        if (view instanceof MessagingLinearLayout.MessagingChild) {
            return ((MessagingLinearLayout.MessagingChild) view).isHidingAnimated();
        }
        return false;
    }

    static /* synthetic */ boolean lambda$static$0(View view) {
        return view.getId() == 16909125;
    }

    public static void recycle(View view) {
        setFirstLayout(view, true);
    }

    public static void setClippingDeactivated(View view, boolean z) {
        ViewClippingUtil.setClippingDeactivated(view, z, CLIPPING_PARAMETERS);
    }

    private static void setFirstLayout(View view, boolean z) {
        view.setTagInternal(16909369, Boolean.valueOf(z));
    }

    private static void setLayoutTop(View view, int i) {
        view.setTagInternal(16909370, Integer.valueOf(i));
    }

    public static void setToLaidOutPosition(View view) {
        setTop(view, getLayoutTop(view));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setTop(View view, int i) {
        view.setTagInternal(16909372, Integer.valueOf(i));
        updateTopAndBottom(view);
    }

    public static void startLocalTranslationFrom(View view, int i, Interpolator interpolator) {
        startTopAnimation(view, getTop(view) + i, getLayoutTop(view), interpolator);
    }

    public static void startLocalTranslationTo(View view, int i, Interpolator interpolator) {
        int top = getTop(view);
        startTopAnimation(view, top, i + top, interpolator);
    }

    private static void startTopAnimation(final View view, int i, int i2, Interpolator interpolator) {
        ObjectAnimator objectAnimator = (ObjectAnimator) view.getTag(16909371);
        if (objectAnimator != null) {
            objectAnimator.cancel();
        }
        if (!view.isShown() || i == i2 || (MessagingLinearLayout.isGone(view) && !isHidingAnimated(view))) {
            setTop(view, i2);
            return;
        }
        ObjectAnimator objectAnimatorOfInt = ObjectAnimator.ofInt(view, TOP, i, i2);
        setTop(view, i);
        objectAnimatorOfInt.setInterpolator(interpolator);
        objectAnimatorOfInt.setDuration(APPEAR_ANIMATION_LENGTH);
        objectAnimatorOfInt.addListener(new AnimatorListenerAdapter() { // from class: com.android.internal.widget.MessagingPropertyAnimator.2
            public boolean mCancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.mCancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                view.setTagInternal(16909371, null);
                MessagingPropertyAnimator.setClippingDeactivated(view, false);
            }
        });
        setClippingDeactivated(view, true);
        view.setTagInternal(16909371, objectAnimatorOfInt);
        objectAnimatorOfInt.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateLayerType(View view, boolean z) {
        if (view.hasOverlappingRendering() && z) {
            view.setLayerType(2, null);
        } else if (view.getLayerType() == 2) {
            view.setLayerType(0, null);
        }
    }

    private static void updateTopAndBottom(View view) {
        int top = getTop(view);
        int height = view.getHeight();
        view.setTop(top);
        view.setBottom(height + top);
    }

    @Override // android.view.View.OnLayoutChangeListener
    public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        setLayoutTop(view, i2);
        if (!isFirstLayout(view)) {
            startTopAnimation(view, getTop(view), i2, MessagingLayout.FAST_OUT_SLOW_IN);
        } else {
            setFirstLayout(view, false);
            setTop(view, i2);
        }
    }
}
