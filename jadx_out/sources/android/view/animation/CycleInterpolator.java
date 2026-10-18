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
public class CycleInterpolator extends BaseInterpolator implements NativeInterpolatorFactory {
    private float mCycles;

    public CycleInterpolator(float f) {
        this.mCycles = f;
    }

    public CycleInterpolator(Context context, AttributeSet attributeSet) {
        this(context.getResources(), context.getTheme(), attributeSet);
    }

    public CycleInterpolator(Resources resources, Resources.Theme theme, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.CycleInterpolator, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.CycleInterpolator);
        this.mCycles = typedArrayObtainStyledAttributes.getFloat(0, 1.0f);
        setChangingConfiguration(typedArrayObtainStyledAttributes.getChangingConfigurations());
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // com.android.internal.view.animation.NativeInterpolatorFactory
    public long createNativeInterpolator() {
        return NativeInterpolatorFactoryHelper.createCycleInterpolator(this.mCycles);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        return (float) Math.sin(((double) (this.mCycles * 2.0f)) * 3.141592653589793d * ((double) f));
    }
}
