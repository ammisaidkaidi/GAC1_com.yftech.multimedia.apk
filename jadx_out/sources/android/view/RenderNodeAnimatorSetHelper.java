package android.view;

import android.animation.TimeInterpolator;
import com.android.internal.view.animation.FallbackLUTInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

/* JADX INFO: loaded from: classes2.dex */
public class RenderNodeAnimatorSetHelper {
    public static long createNativeInterpolator(TimeInterpolator timeInterpolator, long j) {
        if (timeInterpolator == null) {
            return NativeInterpolatorFactoryHelper.createLinearInterpolator();
        }
        return RenderNodeAnimator.isNativeInterpolator(timeInterpolator) ? ((NativeInterpolatorFactory) timeInterpolator).createNativeInterpolator() : FallbackLUTInterpolator.createNativeInterpolator(timeInterpolator, j);
    }

    public static RenderNode getTarget(DisplayListCanvas displayListCanvas) {
        return displayListCanvas.mNode;
    }
}
