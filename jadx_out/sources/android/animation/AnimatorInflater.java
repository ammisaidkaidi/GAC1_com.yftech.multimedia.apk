package android.animation;

import android.content.Context;
import android.content.res.ConfigurationBoundResourceCache;
import android.content.res.ConstantState;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.AttributeSet;
import android.util.Log;
import android.util.PathParser;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.AnimationUtils;
import android.view.animation.BaseInterpolator;
import android.view.animation.Interpolator;
import com.android.ims.ImsConfig;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AnimatorInflater {
    private static final boolean DBG_ANIMATOR_INFLATER = false;
    private static final int SEQUENTIALLY = 1;
    private static final String TAG = "AnimatorInflater";
    private static final int TOGETHER = 0;
    private static final int VALUE_TYPE_COLOR = 3;
    private static final int VALUE_TYPE_FLOAT = 0;
    private static final int VALUE_TYPE_INT = 1;
    private static final int VALUE_TYPE_PATH = 2;
    private static final int VALUE_TYPE_UNDEFINED = 4;
    private static final TypedValue sTmpTypedValue = new TypedValue();

    private static class PathDataEvaluator implements TypeEvaluator<PathParser.PathData> {
        private final PathParser.PathData mPathData;

        private PathDataEvaluator() {
            this.mPathData = new PathParser.PathData();
        }

        @Override // android.animation.TypeEvaluator
        public PathParser.PathData evaluate(float f, PathParser.PathData pathData, PathParser.PathData pathData2) {
            if (PathParser.interpolatePathData(this.mPathData, pathData, pathData2, f)) {
                return this.mPathData;
            }
            throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
        }
    }

    private static Animator createAnimatorFromXml(Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, float f) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser), null, 0, f);
    }

    private static Animator createAnimatorFromXml(Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet, AnimatorSet animatorSet, int i, float f) throws Throwable {
        int i2;
        int depth = xmlPullParser.getDepth();
        Animator animatorLoadAnimator = null;
        ArrayList arrayList = null;
        while (true) {
            int next = xmlPullParser.next();
            i2 = 0;
            if ((next == 3 && xmlPullParser.getDepth() <= depth) || next == 1) {
                break;
            }
            if (next == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("objectAnimator")) {
                    animatorLoadAnimator = loadObjectAnimator(resources, theme, attributeSet, f);
                } else {
                    if (name.equals("animator")) {
                        animatorLoadAnimator = loadAnimator(resources, theme, attributeSet, null, f);
                    } else if (name.equals("set")) {
                        AnimatorSet animatorSet2 = new AnimatorSet();
                        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.AnimatorSet, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.AnimatorSet);
                        animatorSet2.appendChangingConfigurations(typedArrayObtainStyledAttributes.getChangingConfigurations());
                        createAnimatorFromXml(resources, theme, xmlPullParser, attributeSet, animatorSet2, typedArrayObtainStyledAttributes.getInt(0, 0), f);
                        typedArrayObtainStyledAttributes.recycle();
                        animatorLoadAnimator = animatorSet2;
                    } else {
                        if (!name.equals("propertyValuesHolder")) {
                            throw new RuntimeException("Unknown animator name: " + xmlPullParser.getName());
                        }
                        PropertyValuesHolder[] propertyValuesHolderArrLoadValues = loadValues(resources, theme, xmlPullParser, Xml.asAttributeSet(xmlPullParser));
                        if (propertyValuesHolderArrLoadValues != null && animatorLoadAnimator != null && (animatorLoadAnimator instanceof ValueAnimator)) {
                            ((ValueAnimator) animatorLoadAnimator).setValues(propertyValuesHolderArrLoadValues);
                        }
                        i2 = 1;
                    }
                    if (animatorSet == null && i2 == 0) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(animatorLoadAnimator);
                    }
                }
                if (animatorSet == null) {
                }
            }
        }
        if (animatorSet != null && arrayList != null) {
            Animator[] animatorArr = new Animator[arrayList.size()];
            Iterator it = arrayList.iterator();
            while (it.hasNext()) {
                animatorArr[i2] = (Animator) it.next();
                i2++;
            }
            if (i == 0) {
                animatorSet.playTogether(animatorArr);
            } else {
                animatorSet.playSequentially(animatorArr);
            }
        }
        return animatorLoadAnimator;
    }

    private static Keyframe createNewKeyframe(Keyframe keyframe, float f) {
        if (keyframe.getType() == Float.TYPE) {
            return Keyframe.ofFloat(f);
        }
        return keyframe.getType() == Integer.TYPE ? Keyframe.ofInt(f) : Keyframe.ofObject(f);
    }

    private static StateListAnimator createStateListAnimatorFromXml(Context context, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        StateListAnimator stateListAnimator = new StateListAnimator();
        while (true) {
            switch (xmlPullParser.next()) {
                case 1:
                case 3:
                    return stateListAnimator;
                case 2:
                    if (ImsConfig.EXTRA_CHANGED_ITEM.equals(xmlPullParser.getName())) {
                        int attributeCount = xmlPullParser.getAttributeCount();
                        int[] iArr = new int[attributeCount];
                        Animator animatorCreateAnimatorFromXml = null;
                        int i = 0;
                        for (int i2 = 0; i2 < attributeCount; i2++) {
                            int attributeNameResource = attributeSet.getAttributeNameResource(i2);
                            if (attributeNameResource == 16843213) {
                                animatorCreateAnimatorFromXml = loadAnimator(context, attributeSet.getAttributeResourceValue(i2, 0));
                            } else {
                                int i3 = i + 1;
                                if (!attributeSet.getAttributeBooleanValue(i2, false)) {
                                    attributeNameResource = -attributeNameResource;
                                }
                                iArr[i] = attributeNameResource;
                                i = i3;
                            }
                        }
                        if (animatorCreateAnimatorFromXml == null) {
                            animatorCreateAnimatorFromXml = createAnimatorFromXml(context.getResources(), context.getTheme(), xmlPullParser, 1.0f);
                        }
                        if (animatorCreateAnimatorFromXml == null) {
                            throw new Resources.NotFoundException("animation state item must have a valid animation");
                        }
                        stateListAnimator.addState(StateSet.trimStateSet(iArr, i), animatorCreateAnimatorFromXml);
                    } else {
                        continue;
                    }
                    break;
            }
        }
    }

    private static void distributeKeyframes(Keyframe[] keyframeArr, float f, int i, int i2) {
        float f2 = f / ((i2 - i) + 2);
        while (i <= i2) {
            keyframeArr[i].setFraction(keyframeArr[i - 1].getFraction() + f2);
            i++;
        }
    }

    private static void dumpKeyframes(Object[] objArr, String str) {
        if (objArr == null || objArr.length == 0) {
            return;
        }
        Log.d(TAG, str);
        int length = objArr.length;
        for (int i = 0; i < length; i++) {
            Keyframe keyframe = (Keyframe) objArr[i];
            StringBuilder sb = new StringBuilder();
            sb.append("Keyframe ");
            sb.append(i);
            sb.append(": fraction ");
            sb.append(keyframe.getFraction() < 0.0f ? "null" : Float.valueOf(keyframe.getFraction()));
            sb.append(", , value : ");
            sb.append(keyframe.hasValue() ? keyframe.getValue() : "null");
            Log.d(TAG, sb.toString());
        }
    }

    private static int getChangingConfigs(Resources resources, int i) {
        int i2;
        synchronized (sTmpTypedValue) {
            resources.getValue(i, sTmpTypedValue, true);
            i2 = sTmpTypedValue.changingConfigurations;
        }
        return i2;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static PropertyValuesHolder getPVH(TypedArray typedArray, int i, int i2, int i3, String str) {
        int color;
        int color2;
        int color3;
        PropertyValuesHolder propertyValuesHolderOfFloat;
        PropertyValuesHolder propertyValuesHolderOfObject;
        TypedValue typedValuePeekValue = typedArray.peekValue(i2);
        Object[] objArr = typedValuePeekValue != null;
        int i4 = objArr != false ? typedValuePeekValue.type : 0;
        TypedValue typedValuePeekValue2 = typedArray.peekValue(i3);
        Object[] objArr2 = typedValuePeekValue2 != null;
        int i5 = objArr2 != false ? typedValuePeekValue2.type : 0;
        if (i == 4) {
            i = ((objArr == true && isColorType(i4)) || (objArr2 == true && isColorType(i5))) ? 3 : 0;
        }
        Object[] objArr3 = i == 0;
        PropertyValuesHolder propertyValuesHolderOfInt = null;
        Object[] objArr4 = 0;
        Object[] objArr5 = 0;
        if (i == 2) {
            String string = typedArray.getString(i2);
            String string2 = typedArray.getString(i3);
            PathParser.PathData pathData = string == null ? null : new PathParser.PathData(string);
            PathParser.PathData pathData2 = string2 == null ? null : new PathParser.PathData(string2);
            if (pathData == null && pathData2 == null) {
                return null;
            }
            if (pathData == null) {
                if (pathData2 != null) {
                    return PropertyValuesHolder.ofObject(str, new PathDataEvaluator(), pathData2);
                }
                return null;
            }
            PathDataEvaluator pathDataEvaluator = new PathDataEvaluator();
            if (pathData2 == null) {
                propertyValuesHolderOfObject = PropertyValuesHolder.ofObject(str, pathDataEvaluator, pathData);
            } else {
                if (!PathParser.canMorph(pathData, pathData2)) {
                    throw new InflateException(" Can't morph from " + string + " to " + string2);
                }
                propertyValuesHolderOfObject = PropertyValuesHolder.ofObject(str, pathDataEvaluator, pathData, pathData2);
            }
            return propertyValuesHolderOfObject;
        }
        ArgbEvaluator argbEvaluator = i == 3 ? ArgbEvaluator.getInstance() : null;
        if (objArr3 == true) {
            if (objArr == true) {
                float dimension = i4 == 5 ? typedArray.getDimension(i2, 0.0f) : typedArray.getFloat(i2, 0.0f);
                if (objArr2 == true) {
                    propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat(str, dimension, i5 == 5 ? typedArray.getDimension(i3, 0.0f) : typedArray.getFloat(i3, 0.0f));
                } else {
                    propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat(str, dimension);
                }
            } else {
                propertyValuesHolderOfFloat = PropertyValuesHolder.ofFloat(str, i5 == 5 ? typedArray.getDimension(i3, 0.0f) : typedArray.getFloat(i3, 0.0f));
            }
            propertyValuesHolderOfInt = propertyValuesHolderOfFloat;
        } else if (objArr == true) {
            if (i4 == 5) {
                color2 = (int) typedArray.getDimension(i2, 0.0f);
            } else {
                color2 = isColorType(i4) ? typedArray.getColor(i2, 0) : typedArray.getInt(i2, 0);
            }
            if (objArr2 == true) {
                if (i5 == 5) {
                    color3 = (int) typedArray.getDimension(i3, 0.0f);
                } else {
                    color3 = isColorType(i5) ? typedArray.getColor(i3, 0) : typedArray.getInt(i3, 0);
                }
                propertyValuesHolderOfInt = PropertyValuesHolder.ofInt(str, color2, color3);
            } else {
                propertyValuesHolderOfInt = PropertyValuesHolder.ofInt(str, color2);
            }
        } else if (objArr2 != false) {
            if (i5 == 5) {
                color = (int) typedArray.getDimension(i3, 0.0f);
            } else {
                color = isColorType(i5) ? typedArray.getColor(i3, 0) : typedArray.getInt(i3, 0);
            }
            propertyValuesHolderOfInt = PropertyValuesHolder.ofInt(str, color);
        }
        if (propertyValuesHolderOfInt == null || argbEvaluator == null) {
            return propertyValuesHolderOfInt;
        }
        propertyValuesHolderOfInt.setEvaluator(argbEvaluator);
        return propertyValuesHolderOfInt;
    }

    private static int inferValueTypeFromValues(TypedArray typedArray, int i, int i2) {
        TypedValue typedValuePeekValue = typedArray.peekValue(i);
        boolean z = typedValuePeekValue != null;
        int i3 = z ? typedValuePeekValue.type : 0;
        TypedValue typedValuePeekValue2 = typedArray.peekValue(i2);
        boolean z2 = typedValuePeekValue2 != null;
        return ((z && isColorType(i3)) || (z2 && isColorType(z2 ? typedValuePeekValue2.type : 0))) ? 3 : 0;
    }

    private static int inferValueTypeOfKeyframe(Resources resources, Resources.Theme theme, AttributeSet attributeSet) {
        int i = 0;
        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.Keyframe, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.Keyframe);
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(0);
        if ((typedValuePeekValue != null) && isColorType(typedValuePeekValue.type)) {
            i = 3;
        }
        typedArrayObtainStyledAttributes.recycle();
        return i;
    }

    private static boolean isColorType(int i) {
        return i >= 28 && i <= 31;
    }

    public static Animator loadAnimator(Context context, int i) throws Resources.NotFoundException {
        return loadAnimator(context.getResources(), context.getTheme(), i);
    }

    public static Animator loadAnimator(Resources resources, Resources.Theme theme, int i) throws Resources.NotFoundException {
        return loadAnimator(resources, theme, i, 1.0f);
    }

    public static Animator loadAnimator(Resources resources, Resources.Theme theme, int i, float f) throws Resources.NotFoundException {
        ConfigurationBoundResourceCache<Animator> animatorCache = resources.getAnimatorCache();
        long j = i;
        Animator configurationBoundResourceCache = animatorCache.getInstance(j, resources, theme);
        if (configurationBoundResourceCache != null) {
            return configurationBoundResourceCache;
        }
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser animation = resources.getAnimation(i);
                try {
                    Animator animatorCreateAnimatorFromXml = createAnimatorFromXml(resources, theme, animation, f);
                    if (animatorCreateAnimatorFromXml != null) {
                        animatorCreateAnimatorFromXml.appendChangingConfigurations(getChangingConfigs(resources, i));
                        ConstantState<Animator> constantStateCreateConstantState = animatorCreateAnimatorFromXml.createConstantState();
                        if (constantStateCreateConstantState != null) {
                            animatorCache.put(j, theme, constantStateCreateConstantState);
                            animatorCreateAnimatorFromXml = constantStateCreateConstantState.newInstance2(resources, theme);
                        }
                    }
                    if (animation != null) {
                        animation.close();
                    }
                    return animatorCreateAnimatorFromXml;
                } catch (IOException e) {
                    e = e;
                    Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                    notFoundException.initCause(e);
                    throw notFoundException;
                } catch (XmlPullParserException e2) {
                    e = e2;
                    Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(i));
                    notFoundException2.initCause(e);
                    throw notFoundException2;
                } catch (Throwable th) {
                    th = th;
                    xmlResourceParser = animation;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (XmlPullParserException e4) {
            e = e4;
        }
    }

    private static ValueAnimator loadAnimator(Resources resources, Resources.Theme theme, AttributeSet attributeSet, ValueAnimator valueAnimator, float f) throws Throwable {
        TypedArray typedArrayObtainStyledAttributes;
        TypedArray typedArrayObtainStyledAttributes2 = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.Animator, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.Animator);
        if (valueAnimator != null) {
            typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.PropertyAnimator, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.PropertyAnimator);
            valueAnimator.appendChangingConfigurations(typedArrayObtainStyledAttributes.getChangingConfigurations());
        } else {
            typedArrayObtainStyledAttributes = null;
        }
        if (valueAnimator == null) {
            valueAnimator = new ValueAnimator();
        }
        valueAnimator.appendChangingConfigurations(typedArrayObtainStyledAttributes2.getChangingConfigurations());
        parseAnimatorFromTypeArray(valueAnimator, typedArrayObtainStyledAttributes2, typedArrayObtainStyledAttributes, f);
        int resourceId = typedArrayObtainStyledAttributes2.getResourceId(0, 0);
        if (resourceId > 0) {
            Interpolator interpolatorLoadInterpolator = AnimationUtils.loadInterpolator(resources, theme, resourceId);
            if (interpolatorLoadInterpolator instanceof BaseInterpolator) {
                valueAnimator.appendChangingConfigurations(((BaseInterpolator) interpolatorLoadInterpolator).getChangingConfiguration());
            }
            valueAnimator.setInterpolator(interpolatorLoadInterpolator);
        }
        typedArrayObtainStyledAttributes2.recycle();
        if (typedArrayObtainStyledAttributes != null) {
            typedArrayObtainStyledAttributes.recycle();
        }
        return valueAnimator;
    }

    /* JADX WARN: Code duplicated, block: B:22:0x0044  */
    private static Keyframe loadKeyframe(Resources resources, Resources.Theme theme, AttributeSet attributeSet, int i) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.Keyframe, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.Keyframe);
        Keyframe keyframeOfFloat = null;
        float f = typedArrayObtainStyledAttributes.getFloat(3, -1.0f);
        TypedValue typedValuePeekValue = typedArrayObtainStyledAttributes.peekValue(0);
        boolean z = typedValuePeekValue != null;
        if (i == 4) {
            i = (z && isColorType(typedValuePeekValue.type)) ? 3 : 0;
        }
        if (!z) {
            keyframeOfFloat = i == 0 ? Keyframe.ofFloat(f) : Keyframe.ofInt(f);
        } else if (i != 3) {
            switch (i) {
                case 0:
                    keyframeOfFloat = Keyframe.ofFloat(f, typedArrayObtainStyledAttributes.getFloat(0, 0.0f));
                    break;
                case 1:
                    keyframeOfFloat = Keyframe.ofInt(f, typedArrayObtainStyledAttributes.getInt(0, 0));
                    break;
            }
        } else {
            keyframeOfFloat = Keyframe.ofInt(f, typedArrayObtainStyledAttributes.getInt(0, 0));
        }
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(1, 0);
        if (resourceId > 0) {
            keyframeOfFloat.setInterpolator(AnimationUtils.loadInterpolator(resources, theme, resourceId));
        }
        typedArrayObtainStyledAttributes.recycle();
        return keyframeOfFloat;
    }

    private static ObjectAnimator loadObjectAnimator(Resources resources, Resources.Theme theme, AttributeSet attributeSet, float f) throws Throwable {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        loadAnimator(resources, theme, attributeSet, objectAnimator, f);
        return objectAnimator;
    }

    private static PropertyValuesHolder loadPvh(Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, String str, int i) throws XmlPullParserException, IOException {
        int size;
        PropertyValuesHolder propertyValuesHolderOfKeyframe = null;
        int iInferValueTypeOfKeyframe = i;
        ArrayList arrayList = null;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 3 || next == 1) {
                break;
            }
            if (xmlPullParser.getName().equals("keyframe")) {
                if (iInferValueTypeOfKeyframe == 4) {
                    iInferValueTypeOfKeyframe = inferValueTypeOfKeyframe(resources, theme, Xml.asAttributeSet(xmlPullParser));
                }
                Keyframe keyframeLoadKeyframe = loadKeyframe(resources, theme, Xml.asAttributeSet(xmlPullParser), iInferValueTypeOfKeyframe);
                if (keyframeLoadKeyframe != null) {
                    if (arrayList == null) {
                        arrayList = new ArrayList();
                    }
                    arrayList.add(keyframeLoadKeyframe);
                }
                xmlPullParser.next();
            }
        }
        if (arrayList != null && (size = arrayList.size()) > 0) {
            Keyframe keyframe = (Keyframe) arrayList.get(0);
            Keyframe keyframe2 = (Keyframe) arrayList.get(size - 1);
            float fraction = keyframe2.getFraction();
            if (fraction < 1.0f) {
                if (fraction < 0.0f) {
                    keyframe2.setFraction(1.0f);
                } else {
                    arrayList.add(arrayList.size(), createNewKeyframe(keyframe2, 1.0f));
                    size++;
                }
            }
            float fraction2 = keyframe.getFraction();
            if (fraction2 != 0.0f) {
                if (fraction2 < 0.0f) {
                    keyframe.setFraction(0.0f);
                } else {
                    arrayList.add(0, createNewKeyframe(keyframe, 0.0f));
                    size++;
                }
            }
            Keyframe[] keyframeArr = new Keyframe[size];
            arrayList.toArray(keyframeArr);
            for (int i2 = 0; i2 < size; i2++) {
                Keyframe keyframe3 = keyframeArr[i2];
                if (keyframe3.getFraction() < 0.0f) {
                    if (i2 == 0) {
                        keyframe3.setFraction(0.0f);
                    } else {
                        int i3 = size - 1;
                        if (i2 == i3) {
                            keyframe3.setFraction(1.0f);
                        } else {
                            int i4 = i2;
                            for (int i5 = i2 + 1; i5 < i3 && keyframeArr[i5].getFraction() < 0.0f; i5++) {
                                i4 = i5;
                            }
                            distributeKeyframes(keyframeArr, keyframeArr[i4 + 1].getFraction() - keyframeArr[i2 - 1].getFraction(), i2, i4);
                        }
                    }
                }
            }
            propertyValuesHolderOfKeyframe = PropertyValuesHolder.ofKeyframe(str, keyframeArr);
            if (iInferValueTypeOfKeyframe == 3) {
                propertyValuesHolderOfKeyframe.setEvaluator(ArgbEvaluator.getInstance());
            }
        }
        return propertyValuesHolderOfKeyframe;
    }

    public static StateListAnimator loadStateListAnimator(Context context, int i) throws Throwable {
        Resources resources = context.getResources();
        ConfigurationBoundResourceCache<StateListAnimator> stateListAnimatorCache = resources.getStateListAnimatorCache();
        Resources.Theme theme = context.getTheme();
        long j = i;
        StateListAnimator configurationBoundResourceCache = stateListAnimatorCache.getInstance(j, resources, theme);
        if (configurationBoundResourceCache != null) {
            return configurationBoundResourceCache;
        }
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser animation = resources.getAnimation(i);
                try {
                    StateListAnimator stateListAnimatorCreateStateListAnimatorFromXml = createStateListAnimatorFromXml(context, animation, Xml.asAttributeSet(animation));
                    if (stateListAnimatorCreateStateListAnimatorFromXml != null) {
                        stateListAnimatorCreateStateListAnimatorFromXml.appendChangingConfigurations(getChangingConfigs(resources, i));
                        ConstantState<StateListAnimator> constantStateCreateConstantState = stateListAnimatorCreateStateListAnimatorFromXml.createConstantState();
                        if (constantStateCreateConstantState != null) {
                            stateListAnimatorCache.put(j, theme, constantStateCreateConstantState);
                            stateListAnimatorCreateStateListAnimatorFromXml = constantStateCreateConstantState.newInstance2(resources, theme);
                        }
                    }
                    if (animation != null) {
                        animation.close();
                    }
                    return stateListAnimatorCreateStateListAnimatorFromXml;
                } catch (IOException e) {
                    e = e;
                    Resources.NotFoundException notFoundException = new Resources.NotFoundException("Can't load state list animator resource ID #0x" + Integer.toHexString(i));
                    notFoundException.initCause(e);
                    throw notFoundException;
                } catch (XmlPullParserException e2) {
                    e = e2;
                    Resources.NotFoundException notFoundException2 = new Resources.NotFoundException("Can't load state list animator resource ID #0x" + Integer.toHexString(i));
                    notFoundException2.initCause(e);
                    throw notFoundException2;
                } catch (Throwable th) {
                    th = th;
                    xmlResourceParser = animation;
                    if (xmlResourceParser != null) {
                        xmlResourceParser.close();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (IOException e3) {
            e = e3;
        } catch (XmlPullParserException e4) {
            e = e4;
        }
    }

    private static PropertyValuesHolder[] loadValues(Resources resources, Resources.Theme theme, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        int i;
        PropertyValuesHolder[] propertyValuesHolderArr = null;
        ArrayList arrayList = null;
        while (true) {
            int eventType = xmlPullParser.getEventType();
            if (eventType == 3 || eventType == 1) {
                break;
            }
            if (eventType != 2) {
                xmlPullParser.next();
            } else {
                if (xmlPullParser.getName().equals("propertyValuesHolder")) {
                    TypedArray typedArrayObtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.PropertyValuesHolder, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.PropertyValuesHolder);
                    String string = typedArrayObtainStyledAttributes.getString(3);
                    int i2 = typedArrayObtainStyledAttributes.getInt(2, 4);
                    PropertyValuesHolder propertyValuesHolderLoadPvh = loadPvh(resources, theme, xmlPullParser, string, i2);
                    if (propertyValuesHolderLoadPvh == null) {
                        propertyValuesHolderLoadPvh = getPVH(typedArrayObtainStyledAttributes, i2, 0, 1, string);
                    }
                    if (propertyValuesHolderLoadPvh != null) {
                        if (arrayList == null) {
                            arrayList = new ArrayList();
                        }
                        arrayList.add(propertyValuesHolderLoadPvh);
                    }
                    typedArrayObtainStyledAttributes.recycle();
                }
                xmlPullParser.next();
            }
        }
        if (arrayList != null) {
            int size = arrayList.size();
            propertyValuesHolderArr = new PropertyValuesHolder[size];
            for (i = 0; i < size; i++) {
                propertyValuesHolderArr[i] = (PropertyValuesHolder) arrayList.get(i);
            }
        }
        return propertyValuesHolderArr;
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator valueAnimator, TypedArray typedArray, TypedArray typedArray2, float f) {
        long j = typedArray.getInt(1, 300);
        long j2 = typedArray.getInt(2, 0);
        int iInferValueTypeFromValues = typedArray.getInt(7, 4);
        if (iInferValueTypeFromValues == 4) {
            iInferValueTypeFromValues = inferValueTypeFromValues(typedArray, 5, 6);
        }
        PropertyValuesHolder pvh = getPVH(typedArray, iInferValueTypeFromValues, 5, 6, "");
        if (pvh != null) {
            valueAnimator.setValues(pvh);
        }
        valueAnimator.setDuration(j);
        valueAnimator.setStartDelay(j2);
        if (typedArray.hasValue(3)) {
            valueAnimator.setRepeatCount(typedArray.getInt(3, 0));
        }
        if (typedArray.hasValue(4)) {
            valueAnimator.setRepeatMode(typedArray.getInt(4, 1));
        }
        if (typedArray2 != null) {
            setupObjectAnimator(valueAnimator, typedArray2, iInferValueTypeFromValues, f);
        }
    }

    private static TypeEvaluator setupAnimatorForPath(ValueAnimator valueAnimator, TypedArray typedArray) {
        String string = typedArray.getString(5);
        String string2 = typedArray.getString(6);
        PathParser.PathData pathData = string == null ? null : new PathParser.PathData(string);
        PathParser.PathData pathData2 = string2 == null ? null : new PathParser.PathData(string2);
        if (pathData == null) {
            if (pathData2 == null) {
                return null;
            }
            valueAnimator.setObjectValues(pathData2);
            return new PathDataEvaluator();
        }
        if (pathData2 != null) {
            valueAnimator.setObjectValues(pathData, pathData2);
            if (!PathParser.canMorph(pathData, pathData2)) {
                throw new InflateException(typedArray.getPositionDescription() + " Can't morph from " + string + " to " + string2);
            }
        } else {
            valueAnimator.setObjectValues(pathData);
        }
        return new PathDataEvaluator();
    }

    private static void setupObjectAnimator(ValueAnimator valueAnimator, TypedArray typedArray, int i, float f) {
        Keyframes keyframesCreateXIntKeyframes;
        Keyframes keyframesCreateYIntKeyframes;
        ObjectAnimator objectAnimator = (ObjectAnimator) valueAnimator;
        String string = typedArray.getString(1);
        if (string == null) {
            objectAnimator.setPropertyName(typedArray.getString(0));
            return;
        }
        String string2 = typedArray.getString(2);
        String string3 = typedArray.getString(3);
        if (i == 2 || i == 4) {
            i = 0;
        }
        if (string2 == null && string3 == null) {
            throw new InflateException(typedArray.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
        }
        PathKeyframes pathKeyframesOfPath = KeyframeSet.ofPath(PathParser.createPathFromPathData(string), f * 0.5f);
        if (i == 0) {
            keyframesCreateXIntKeyframes = pathKeyframesOfPath.createXFloatKeyframes();
            keyframesCreateYIntKeyframes = pathKeyframesOfPath.createYFloatKeyframes();
        } else {
            keyframesCreateXIntKeyframes = pathKeyframesOfPath.createXIntKeyframes();
            keyframesCreateYIntKeyframes = pathKeyframesOfPath.createYIntKeyframes();
        }
        PropertyValuesHolder propertyValuesHolderOfKeyframes = string2 != null ? PropertyValuesHolder.ofKeyframes(string2, keyframesCreateXIntKeyframes) : null;
        PropertyValuesHolder propertyValuesHolderOfKeyframes2 = string3 != null ? PropertyValuesHolder.ofKeyframes(string3, keyframesCreateYIntKeyframes) : null;
        if (propertyValuesHolderOfKeyframes == null) {
            objectAnimator.setValues(propertyValuesHolderOfKeyframes2);
        } else if (propertyValuesHolderOfKeyframes2 == null) {
            objectAnimator.setValues(propertyValuesHolderOfKeyframes);
        } else {
            objectAnimator.setValues(propertyValuesHolderOfKeyframes, propertyValuesHolderOfKeyframes2);
        }
    }

    private static void setupValues(ValueAnimator valueAnimator, TypedArray typedArray, boolean z, boolean z2, int i, boolean z3, int i2) {
        int color;
        int color2;
        int color3;
        if (z) {
            if (!z2) {
                valueAnimator.setFloatValues(i2 == 5 ? typedArray.getDimension(6, 0.0f) : typedArray.getFloat(6, 0.0f));
                return;
            }
            float dimension = i == 5 ? typedArray.getDimension(5, 0.0f) : typedArray.getFloat(5, 0.0f);
            if (z3) {
                valueAnimator.setFloatValues(dimension, i2 == 5 ? typedArray.getDimension(6, 0.0f) : typedArray.getFloat(6, 0.0f));
                return;
            } else {
                valueAnimator.setFloatValues(dimension);
                return;
            }
        }
        if (!z2) {
            if (z3) {
                if (i2 == 5) {
                    color = (int) typedArray.getDimension(6, 0.0f);
                } else {
                    color = isColorType(i2) ? typedArray.getColor(6, 0) : typedArray.getInt(6, 0);
                }
                valueAnimator.setIntValues(color);
                return;
            }
            return;
        }
        if (i == 5) {
            color2 = (int) typedArray.getDimension(5, 0.0f);
        } else {
            color2 = isColorType(i) ? typedArray.getColor(5, 0) : typedArray.getInt(5, 0);
        }
        if (!z3) {
            valueAnimator.setIntValues(color2);
            return;
        }
        if (i2 == 5) {
            color3 = (int) typedArray.getDimension(6, 0.0f);
        } else {
            color3 = isColorType(i2) ? typedArray.getColor(6, 0) : typedArray.getInt(6, 0);
        }
        valueAnimator.setIntValues(color2, color3);
    }
}
