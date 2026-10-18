package com.yftech.car.widget.media.photo.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import androidx.viewpager.widget.ViewPager;

/* JADX INFO: loaded from: classes3.dex */
public class ZoomOutPageTransformer extends Animation implements ViewPager.PageTransformer {
    private static final float MIN_ALPHA = 0.7f;
    private static final float MIN_SCALE = 0.8f;
    private Context context;

    public ZoomOutPageTransformer() {
        setDuration(500L);
    }

    public void animator_moveX(View view, float f, float f2) {
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view, "x", f, f2);
        objectAnimatorOfFloat.setDuration(500L);
        objectAnimatorOfFloat.start();
    }

    @Override // androidx.viewpager.widget.ViewPager.PageTransformer
    public void transformPage(View view, float f) {
        int width = view.getWidth();
        int height = view.getHeight();
        if (f < -1.0f) {
            view.setAlpha(0.0f);
            return;
        }
        if (f <= 1.0f) {
            float fMax = Math.max(MIN_SCALE, 1.0f - Math.abs(f));
            float f2 = 1.0f - fMax;
            float f3 = (height * f2) / 2.0f;
            float f4 = (width * f2) / 2.0f;
            if (f < 0.0f) {
                view.setTranslationX(f4 - (f3 / 2.0f));
            } else {
                view.setTranslationX((-f4) + (f3 / 2.0f));
            }
            view.setScaleX(fMax);
            view.setScaleY(fMax);
            view.setAlpha((((fMax - MIN_SCALE) / 0.19999999f) * 0.3f) + MIN_ALPHA);
        }
    }
}
