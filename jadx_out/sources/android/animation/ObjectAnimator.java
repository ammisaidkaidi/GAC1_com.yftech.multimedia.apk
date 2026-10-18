package android.animation;

import android.graphics.Path;
import android.graphics.PointF;
import android.util.Property;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public final class ObjectAnimator extends ValueAnimator {
    private static final boolean DBG = false;
    private static final String LOG_TAG = "ObjectAnimator";
    private boolean mAutoCancel = false;
    private Property mProperty;
    private String mPropertyName;
    private WeakReference<Object> mTarget;

    public ObjectAnimator() {
    }

    private <T> ObjectAnimator(T t, Property<T, ?> property) {
        setTarget(t);
        setProperty(property);
    }

    private ObjectAnimator(Object obj, String str) {
        setTarget(obj);
        setPropertyName(str);
    }

    private boolean hasSameTargetAndProperties(Animator animator) {
        if (animator instanceof ObjectAnimator) {
            ObjectAnimator objectAnimator = (ObjectAnimator) animator;
            PropertyValuesHolder[] values = objectAnimator.getValues();
            if (objectAnimator.getTarget() == getTarget() && this.mValues.length == values.length) {
                for (int i = 0; i < this.mValues.length; i++) {
                    PropertyValuesHolder propertyValuesHolder = this.mValues[i];
                    PropertyValuesHolder propertyValuesHolder2 = values[i];
                    if (propertyValuesHolder.getPropertyName() == null || !propertyValuesHolder.getPropertyName().equals(propertyValuesHolder2.getPropertyName())) {
                        return false;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public static <T> ObjectAnimator ofArgb(T t, Property<T, Integer> property, int... iArr) {
        ObjectAnimator objectAnimatorOfInt = ofInt(t, property, iArr);
        objectAnimatorOfInt.setEvaluator(ArgbEvaluator.getInstance());
        return objectAnimatorOfInt;
    }

    public static ObjectAnimator ofArgb(Object obj, String str, int... iArr) {
        ObjectAnimator objectAnimatorOfInt = ofInt(obj, str, iArr);
        objectAnimatorOfInt.setEvaluator(ArgbEvaluator.getInstance());
        return objectAnimatorOfInt;
    }

    public static <T> ObjectAnimator ofFloat(T t, Property<T, Float> property, Property<T, Float> property2, Path path) {
        PathKeyframes pathKeyframesOfPath = KeyframeSet.ofPath(path);
        return ofPropertyValuesHolder(t, PropertyValuesHolder.ofKeyframes(property, pathKeyframesOfPath.createXFloatKeyframes()), PropertyValuesHolder.ofKeyframes(property2, pathKeyframesOfPath.createYFloatKeyframes()));
    }

    public static <T> ObjectAnimator ofFloat(T t, Property<T, Float> property, float... fArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(t, property);
        objectAnimator.setFloatValues(fArr);
        return objectAnimator;
    }

    public static ObjectAnimator ofFloat(Object obj, String str, String str2, Path path) {
        PathKeyframes pathKeyframesOfPath = KeyframeSet.ofPath(path);
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofKeyframes(str, pathKeyframesOfPath.createXFloatKeyframes()), PropertyValuesHolder.ofKeyframes(str2, pathKeyframesOfPath.createYFloatKeyframes()));
    }

    public static ObjectAnimator ofFloat(Object obj, String str, float... fArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(obj, str);
        objectAnimator.setFloatValues(fArr);
        return objectAnimator;
    }

    public static <T> ObjectAnimator ofInt(T t, Property<T, Integer> property, Property<T, Integer> property2, Path path) {
        PathKeyframes pathKeyframesOfPath = KeyframeSet.ofPath(path);
        return ofPropertyValuesHolder(t, PropertyValuesHolder.ofKeyframes(property, pathKeyframesOfPath.createXIntKeyframes()), PropertyValuesHolder.ofKeyframes(property2, pathKeyframesOfPath.createYIntKeyframes()));
    }

    public static <T> ObjectAnimator ofInt(T t, Property<T, Integer> property, int... iArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(t, property);
        objectAnimator.setIntValues(iArr);
        return objectAnimator;
    }

    public static ObjectAnimator ofInt(Object obj, String str, String str2, Path path) {
        PathKeyframes pathKeyframesOfPath = KeyframeSet.ofPath(path);
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofKeyframes(str, pathKeyframesOfPath.createXIntKeyframes()), PropertyValuesHolder.ofKeyframes(str2, pathKeyframesOfPath.createYIntKeyframes()));
    }

    public static ObjectAnimator ofInt(Object obj, String str, int... iArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(obj, str);
        objectAnimator.setIntValues(iArr);
        return objectAnimator;
    }

    @SafeVarargs
    public static <T> ObjectAnimator ofMultiFloat(Object obj, String str, TypeConverter<T, float[]> typeConverter, TypeEvaluator<T> typeEvaluator, T... tArr) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiFloat(str, typeConverter, typeEvaluator, tArr));
    }

    public static ObjectAnimator ofMultiFloat(Object obj, String str, Path path) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiFloat(str, path));
    }

    public static ObjectAnimator ofMultiFloat(Object obj, String str, float[][] fArr) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiFloat(str, fArr));
    }

    @SafeVarargs
    public static <T> ObjectAnimator ofMultiInt(Object obj, String str, TypeConverter<T, int[]> typeConverter, TypeEvaluator<T> typeEvaluator, T... tArr) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiInt(str, typeConverter, typeEvaluator, tArr));
    }

    public static ObjectAnimator ofMultiInt(Object obj, String str, Path path) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiInt(str, path));
    }

    public static ObjectAnimator ofMultiInt(Object obj, String str, int[][] iArr) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofMultiInt(str, iArr));
    }

    @SafeVarargs
    public static <T, V, P> ObjectAnimator ofObject(T t, Property<T, P> property, TypeConverter<V, P> typeConverter, TypeEvaluator<V> typeEvaluator, V... vArr) {
        return ofPropertyValuesHolder(t, PropertyValuesHolder.ofObject(property, typeConverter, typeEvaluator, vArr));
    }

    public static <T, V> ObjectAnimator ofObject(T t, Property<T, V> property, TypeConverter<PointF, V> typeConverter, Path path) {
        return ofPropertyValuesHolder(t, PropertyValuesHolder.ofObject(property, typeConverter, path));
    }

    @SafeVarargs
    public static <T, V> ObjectAnimator ofObject(T t, Property<T, V> property, TypeEvaluator<V> typeEvaluator, V... vArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(t, property);
        objectAnimator.setObjectValues(vArr);
        objectAnimator.setEvaluator(typeEvaluator);
        return objectAnimator;
    }

    public static ObjectAnimator ofObject(Object obj, String str, TypeConverter<PointF, ?> typeConverter, Path path) {
        return ofPropertyValuesHolder(obj, PropertyValuesHolder.ofObject(str, typeConverter, path));
    }

    public static ObjectAnimator ofObject(Object obj, String str, TypeEvaluator typeEvaluator, Object... objArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator(obj, str);
        objectAnimator.setObjectValues(objArr);
        objectAnimator.setEvaluator(typeEvaluator);
        return objectAnimator;
    }

    public static ObjectAnimator ofPropertyValuesHolder(Object obj, PropertyValuesHolder... propertyValuesHolderArr) {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setTarget(obj);
        objectAnimator.setValues(propertyValuesHolderArr);
        return objectAnimator;
    }

    @Override // android.animation.ValueAnimator
    void animateValue(float f) {
        Object target = getTarget();
        if (this.mTarget != null && target == null) {
            cancel();
            return;
        }
        super.animateValue(f);
        int length = this.mValues.length;
        for (int i = 0; i < length; i++) {
            this.mValues[i].setAnimatedValue(target);
        }
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    /* JADX INFO: renamed from: clone */
    public ObjectAnimator mo0clone() {
        return (ObjectAnimator) super.mo0clone();
    }

    @Override // android.animation.ValueAnimator
    String getNameForTrace() {
        return "animator:" + getPropertyName();
    }

    public String getPropertyName() {
        String str = null;
        if (this.mPropertyName != null) {
            return this.mPropertyName;
        }
        if (this.mProperty != null) {
            return this.mProperty.getName();
        }
        if (this.mValues == null || this.mValues.length <= 0) {
            return null;
        }
        for (int i = 0; i < this.mValues.length; i++) {
            str = (i == 0 ? "" : str + ",") + this.mValues[i].getPropertyName();
        }
        return str;
    }

    public Object getTarget() {
        if (this.mTarget == null) {
            return null;
        }
        return this.mTarget.get();
    }

    @Override // android.animation.ValueAnimator
    void initAnimation() {
        if (this.mInitialized) {
            return;
        }
        Object target = getTarget();
        if (target != null) {
            int length = this.mValues.length;
            for (int i = 0; i < length; i++) {
                this.mValues[i].setupSetterAndGetter(target);
            }
        }
        super.initAnimation();
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    boolean isInitialized() {
        return this.mInitialized;
    }

    public void setAutoCancel(boolean z) {
        this.mAutoCancel = z;
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public ObjectAnimator setDuration(long j) {
        super.setDuration(j);
        return this;
    }

    @Override // android.animation.ValueAnimator
    public void setFloatValues(float... fArr) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setFloatValues(fArr);
        } else if (this.mProperty != null) {
            setValues(PropertyValuesHolder.ofFloat((Property<?, Float>) this.mProperty, fArr));
        } else {
            setValues(PropertyValuesHolder.ofFloat(this.mPropertyName, fArr));
        }
    }

    @Override // android.animation.ValueAnimator
    public void setIntValues(int... iArr) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setIntValues(iArr);
        } else if (this.mProperty != null) {
            setValues(PropertyValuesHolder.ofInt((Property<?, Integer>) this.mProperty, iArr));
        } else {
            setValues(PropertyValuesHolder.ofInt(this.mPropertyName, iArr));
        }
    }

    @Override // android.animation.ValueAnimator
    public void setObjectValues(Object... objArr) {
        if (this.mValues != null && this.mValues.length != 0) {
            super.setObjectValues(objArr);
        } else if (this.mProperty != null) {
            setValues(PropertyValuesHolder.ofObject(this.mProperty, (TypeEvaluator) null, objArr));
        } else {
            setValues(PropertyValuesHolder.ofObject(this.mPropertyName, (TypeEvaluator) null, objArr));
        }
    }

    public void setProperty(Property property) {
        if (this.mValues != null) {
            PropertyValuesHolder propertyValuesHolder = this.mValues[0];
            String propertyName = propertyValuesHolder.getPropertyName();
            propertyValuesHolder.setProperty(property);
            this.mValuesMap.remove(propertyName);
            this.mValuesMap.put(this.mPropertyName, propertyValuesHolder);
        }
        if (this.mProperty != null) {
            this.mPropertyName = property.getName();
        }
        this.mProperty = property;
        this.mInitialized = false;
    }

    public void setPropertyName(String str) {
        if (this.mValues != null) {
            PropertyValuesHolder propertyValuesHolder = this.mValues[0];
            String propertyName = propertyValuesHolder.getPropertyName();
            propertyValuesHolder.setPropertyName(str);
            this.mValuesMap.remove(propertyName);
            this.mValuesMap.put(str, propertyValuesHolder);
        }
        this.mPropertyName = str;
        this.mInitialized = false;
    }

    @Override // android.animation.Animator
    public void setTarget(Object obj) {
        if (getTarget() != obj) {
            if (isStarted()) {
                cancel();
            }
            this.mTarget = obj == null ? null : new WeakReference<>(obj);
            this.mInitialized = false;
        }
    }

    @Override // android.animation.Animator
    public void setupEndValues() {
        initAnimation();
        Object target = getTarget();
        if (target != null) {
            int length = this.mValues.length;
            for (int i = 0; i < length; i++) {
                this.mValues[i].setupEndValue(target);
            }
        }
    }

    @Override // android.animation.Animator
    public void setupStartValues() {
        initAnimation();
        Object target = getTarget();
        if (target != null) {
            int length = this.mValues.length;
            for (int i = 0; i < length; i++) {
                this.mValues[i].setupStartValue(target);
            }
        }
    }

    boolean shouldAutoCancel(AnimationHandler.AnimationFrameCallback animationFrameCallback) {
        if (animationFrameCallback != null && (animationFrameCallback instanceof ObjectAnimator)) {
            ObjectAnimator objectAnimator = (ObjectAnimator) animationFrameCallback;
            if (objectAnimator.mAutoCancel && hasSameTargetAndProperties(objectAnimator)) {
                return true;
            }
        }
        return false;
    }

    @Override // android.animation.ValueAnimator, android.animation.Animator
    public void start() {
        AnimationHandler.getInstance().autoCancelBasedOn(this);
        super.start();
    }

    @Override // android.animation.ValueAnimator
    public String toString() {
        String str = "ObjectAnimator@" + Integer.toHexString(hashCode()) + ", target " + getTarget();
        if (this.mValues != null) {
            for (int i = 0; i < this.mValues.length; i++) {
                str = str + "\n    " + this.mValues[i].toString();
            }
        }
        return str;
    }
}
