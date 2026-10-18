package com.yftech.libradio.carousellayoutmanager;

import android.view.View;
import androidx.annotation.NonNull;

/* JADX INFO: loaded from: classes4.dex */
public class CarouselZoomPostLayoutListener extends CarouselLayoutManager.PostLayoutListener {
    private final float mScaleMultiplier;

    public CarouselZoomPostLayoutListener() {
        this(0.22f);
    }

    public CarouselZoomPostLayoutListener(float f) {
        this.mScaleMultiplier = f;
    }

    @Override // com.yftech.libradio.carousellayoutmanager.CarouselLayoutManager.PostLayoutListener
    public ItemTransformation transformChild(@NonNull View view, float f, int i) {
        float fSignum;
        float f2;
        float fAbs = 1.0f - (this.mScaleMultiplier * Math.abs(f));
        if (1 == i) {
            fSignum = Math.signum(f) * ((view.getMeasuredHeight() * (1.0f - fAbs)) / 2.0f);
            f2 = 0.0f;
        } else {
            float measuredWidth = (view.getMeasuredWidth() * (1.0f - fAbs)) / 2.0f;
            float fSignum2 = Math.signum(f) * measuredWidth;
            fSignum = (-measuredWidth) * 1.3f;
            f2 = fSignum2;
        }
        return new ItemTransformation(fAbs, fAbs, f2, fSignum);
    }
}
