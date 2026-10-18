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
public class DecelerateInterpolator extends BaseInterpolator implements NativeInterpolatorFactory {
    private float mFactor;

    public DecelerateInterpolator() {
        this.mFactor = 1.0f;
    }

    public DecelerateInterpolator(float f) {
        this.mFactor = 1.0f;
        this.mFactor = f;
    }

    public DecelerateInterpolator(Context context, AttributeSet attributeSet) {
        this(context.getResources(), context.getTheme(), attributeSet);
    }

    public DecelerateInterpolator(Resources resources, Resources.Theme theme, AttributeSet attributeSet) {
        this.mFactor = 1.0f;
        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.DecelerateInterpolator, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.DecelerateInterpolator);
        this.mFactor = typedArrayObtainStyledAttributes.getFloat(0, 1.0f);
        setChangingConfiguration(typedArrayObtainStyledAttributes.getChangingConfigurations());
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // com.android.internal.view.animation.NativeInterpolatorFactory
    public long createNativeInterpolator() {
        return NativeInterpolatorFactoryHelper.createDecelerateInterpolator(this.mFactor);
    }

    @Override // android.animation.TimeInterpolator
    public float getInterpolation(float f) {
        if (this.mFactor != 1.0f) {
            return (float) (1.0d - Math.pow(1.0f - f, this.mFactor * 2.0f));
        }
        float f2 = 1.0f - f;
        return 1.0f - (f2 * f2);
    }
}
