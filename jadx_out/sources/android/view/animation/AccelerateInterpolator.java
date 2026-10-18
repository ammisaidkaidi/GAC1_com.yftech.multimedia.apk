package android.view.animation;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import com.android.internal.R;
import com.android.internal.view.animation.HasNativeInterpolator;
import com.android.internal.view.animation.NativeInterpolatorFactory;
import com.android.internal.view.animation.NativeInterpolatorFactoryHelper;

/* JADX INFO: loaded from: classes2.dex */
@HasNativeInterpolator
public class AccelerateInterpolator extends BaseInterpolator implements NativeInterpolatorFactory {
    private final double mDoubleFactor;
    private final float mFactor;

    public AccelerateInterpolator() {
        this.mFactor = 1.0f;
        this.mDoubleFactor = 2.0d;
    }

    public AccelerateInterpolator(float f) {
        this.mFactor = f;
        this.mDoubleFactor = this.mFactor * 2.0f;
    }

    public AccelerateInterpolator(Context context, AttributeSet attributeSet) {
        this(context.getResources(), context.getTheme(), attributeSet);
    }

    public AccelerateInterpolator(Resources resources, Resources.Theme theme, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.AccelerateInterpolator, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.AccelerateInterpolator);
        this.mFactor = typedArrayObtainStyledAttributes.getFloat(0, 1.0f);
        this.mDoubleFactor = this.mFactor * 2.0f;
        setChangingConfiguration(typedArrayObtainStyledAttributes.getChangingConfigurations());
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // com.android.internal.view.animation.NativeInterpolatorFactory
    public long createNativeInterpolator() {
        return NativeInterpolatorFactoryHelper.createAccelerateInterpolator(this.mFactor);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return this.mFactor == 1.0f ? f * f : (float) Math.pow(f, this.mDoubleFactor);
    }
}
