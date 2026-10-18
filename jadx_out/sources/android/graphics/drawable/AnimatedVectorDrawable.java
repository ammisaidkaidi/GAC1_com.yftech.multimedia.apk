package android.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.ActivityThread;
import android.app.Application;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.IntArray;
import android.util.Log;
import android.util.LongArray;
import android.util.PathParser;
import android.util.Property;
import android.util.TimeUtils;
import android.view.Choreographer;
import android.view.DisplayListCanvas;
import android.view.RenderNode;
import android.view.RenderNodeAnimatorSetHelper;
import com.android.internal.R;
import com.android.internal.util.VirtualRefBasePtr;
import dalvik.annotation.optimization.FastNative;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AnimatedVectorDrawable extends Drawable implements Animatable2 {
    private static final String ANIMATED_VECTOR = "animated-vector";
    private static final boolean DBG_ANIMATION_VECTOR_DRAWABLE = false;
    private static final String LOGTAG = "AnimatedVectorDrawable";
    private static final String TARGET = "target";
    private AnimatedVectorDrawableState mAnimatedVectorState;
    private ArrayList<Animatable2.AnimationCallback> mAnimationCallbacks;
    private Animator.AnimatorListener mAnimatorListener;
    private VectorDrawableAnimator mAnimatorSet;
    private AnimatorSet mAnimatorSetFromXml;
    private final Drawable.Callback mCallback;
    private boolean mMutated;
    private Resources mRes;

    private static class AnimatedVectorDrawableState extends Drawable.ConstantState {
        ArrayList<Animator> mAnimators;
        int mChangingConfigurations;
        ArrayList<PendingAnimator> mPendingAnims;
        private final boolean mShouldIgnoreInvalidAnim = AnimatedVectorDrawable.shouldIgnoreInvalidAnimation();
        ArrayMap<Animator, String> mTargetNameMap;
        VectorDrawable mVectorDrawable;

        private static class PendingAnimator {
            public final int animResId;
            public final float pathErrorScale;
            public final String target;

            public PendingAnimator(int i, float f, String str) {
                this.animResId = i;
                this.pathErrorScale = f;
                this.target = str;
            }

            public Animator newInstance(Resources resources, Resources.Theme theme) {
                return AnimatorInflater.loadAnimator(resources, theme, this.animResId, this.pathErrorScale);
            }
        }

        public AnimatedVectorDrawableState(AnimatedVectorDrawableState animatedVectorDrawableState, Drawable.Callback callback, Resources resources) {
            if (animatedVectorDrawableState == null) {
                this.mVectorDrawable = new VectorDrawable();
                return;
            }
            this.mChangingConfigurations = animatedVectorDrawableState.mChangingConfigurations;
            if (animatedVectorDrawableState.mVectorDrawable != null) {
                Drawable.ConstantState constantState = animatedVectorDrawableState.mVectorDrawable.getConstantState();
                if (resources != null) {
                    this.mVectorDrawable = (VectorDrawable) constantState.newDrawable(resources);
                } else {
                    this.mVectorDrawable = (VectorDrawable) constantState.newDrawable();
                }
                this.mVectorDrawable = (VectorDrawable) this.mVectorDrawable.mutate();
                this.mVectorDrawable.setCallback(callback);
                this.mVectorDrawable.setLayoutDirection(animatedVectorDrawableState.mVectorDrawable.getLayoutDirection());
                this.mVectorDrawable.setBounds(animatedVectorDrawableState.mVectorDrawable.getBounds());
                this.mVectorDrawable.setAllowCaching(false);
            }
            if (animatedVectorDrawableState.mAnimators != null) {
                this.mAnimators = new ArrayList<>(animatedVectorDrawableState.mAnimators);
            }
            if (animatedVectorDrawableState.mTargetNameMap != null) {
                this.mTargetNameMap = new ArrayMap<>(animatedVectorDrawableState.mTargetNameMap);
            }
            if (animatedVectorDrawableState.mPendingAnims != null) {
                this.mPendingAnims = new ArrayList<>(animatedVectorDrawableState.mPendingAnims);
            }
        }

        private Animator prepareLocalAnimator(int i) {
            Animator animator = this.mAnimators.get(i);
            Animator animatorMo0clone = animator.mo0clone();
            String str = this.mTargetNameMap.get(animator);
            Object targetByName = this.mVectorDrawable.getTargetByName(str);
            if (!this.mShouldIgnoreInvalidAnim) {
                if (targetByName == null) {
                    throw new IllegalStateException("Target with the name \"" + str + "\" cannot be found in the VectorDrawable to be animated.");
                }
                if (!(targetByName instanceof VectorDrawable.VectorDrawableState) && !(targetByName instanceof VectorDrawable.VObject)) {
                    throw new UnsupportedOperationException("Target should be either VGroup, VPath, or ConstantState, " + targetByName.getClass() + " is not supported");
                }
            }
            animatorMo0clone.setTarget(targetByName);
            return animatorMo0clone;
        }

        public void addPendingAnimator(int i, float f, String str) {
            if (this.mPendingAnims == null) {
                this.mPendingAnims = new ArrayList<>(1);
            }
            this.mPendingAnims.add(new PendingAnimator(i, f, str));
        }

        public void addTargetAnimator(String str, Animator animator) {
            if (this.mAnimators == null) {
                this.mAnimators = new ArrayList<>(1);
                this.mTargetNameMap = new ArrayMap<>(1);
            }
            this.mAnimators.add(animator);
            this.mTargetNameMap.put(animator, str);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public boolean canApplyTheme() {
            return (this.mVectorDrawable != null && this.mVectorDrawable.canApplyTheme()) || this.mPendingAnims != null || super.canApplyTheme();
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public int getChangingConfigurations() {
            return this.mChangingConfigurations;
        }

        public void inflatePendingAnimators(Resources resources, Resources.Theme theme) {
            ArrayList<PendingAnimator> arrayList = this.mPendingAnims;
            if (arrayList != null) {
                this.mPendingAnims = null;
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    PendingAnimator pendingAnimator = arrayList.get(i);
                    Animator animatorNewInstance = pendingAnimator.newInstance(resources, theme);
                    AnimatedVectorDrawable.updateAnimatorProperty(animatorNewInstance, pendingAnimator.target, this.mVectorDrawable, this.mShouldIgnoreInvalidAnim);
                    addTargetAnimator(pendingAnimator.target, animatorNewInstance);
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable() {
            return new AnimatedVectorDrawable(this, null);
        }

        @Override // android.graphics.drawable.Drawable.ConstantState
        public Drawable newDrawable(Resources resources) {
            return new AnimatedVectorDrawable(this, resources);
        }

        public void prepareLocalAnimators(AnimatorSet animatorSet, Resources resources) {
            if (this.mPendingAnims != null) {
                if (resources != null) {
                    inflatePendingAnimators(resources, null);
                } else {
                    Log.e(AnimatedVectorDrawable.LOGTAG, "Failed to load animators. Either the AnimatedVectorDrawable must be created using a Resources object or applyTheme() must be called with a non-null Theme object.");
                }
                this.mPendingAnims = null;
            }
            int size = this.mAnimators == null ? 0 : this.mAnimators.size();
            if (size > 0) {
                AnimatorSet.Builder builderPlay = animatorSet.play(prepareLocalAnimator(0));
                for (int i = 1; i < size; i++) {
                    builderPlay.with(prepareLocalAnimator(i));
                }
            }
        }
    }

    private interface VectorDrawableAnimator {
        boolean canReverse();

        void end();

        void init(AnimatorSet animatorSet);

        boolean isInfinite();

        boolean isRunning();

        boolean isStarted();

        void onDraw(Canvas canvas);

        void pause();

        void removeListener(Animator.AnimatorListener animatorListener);

        void reset();

        void resume();

        void reverse();

        void setListener(Animator.AnimatorListener animatorListener);

        void start();
    }

    public static class VectorDrawableAnimatorRT implements VectorDrawableAnimator {
        private static final int END_ANIMATION = 4;
        private static final int MAX_SAMPLE_POINTS = 300;
        private static final int RESET_ANIMATION = 3;
        private static final int REVERSE_ANIMATION = 2;
        private static final int START_ANIMATION = 1;
        private final AnimatedVectorDrawable mDrawable;
        private long mSetPtr;
        private final VirtualRefBasePtr mSetRefBasePtr;
        private Animator.AnimatorListener mListener = null;
        private final LongArray mStartDelays = new LongArray();
        private PropertyValuesHolder.PropertyValues mTmpValues = new PropertyValuesHolder.PropertyValues();
        private boolean mContainsSequentialAnimators = false;
        private boolean mStarted = false;
        private boolean mInitialized = false;
        private boolean mIsReversible = false;
        private boolean mIsInfinite = false;
        private WeakReference<RenderNode> mLastSeenTarget = null;
        private int mLastListenerId = 0;
        private final IntArray mPendingAnimationActions = new IntArray();

        VectorDrawableAnimatorRT(AnimatedVectorDrawable animatedVectorDrawable) {
            this.mSetPtr = 0L;
            this.mDrawable = animatedVectorDrawable;
            this.mSetPtr = AnimatedVectorDrawable.nCreateAnimatorSet();
            this.mSetRefBasePtr = new VirtualRefBasePtr(this.mSetPtr);
        }

        private void addPendingAction(int i) {
            invalidateOwningView();
            this.mPendingAnimationActions.add(i);
        }

        private static void callOnFinished(VectorDrawableAnimatorRT vectorDrawableAnimatorRT, int i) {
            vectorDrawableAnimatorRT.onAnimationEnd(i);
        }

        private static float[] createFloatDataPoints(PropertyValuesHolder.PropertyValues.DataSource dataSource, long j) {
            int frameCount = getFrameCount(j);
            float[] fArr = new float[frameCount];
            float f = frameCount - 1;
            for (int i = 0; i < frameCount; i++) {
                fArr[i] = ((Float) dataSource.getValueAtFraction(i / f)).floatValue();
            }
            return fArr;
        }

        private static int[] createIntDataPoints(PropertyValuesHolder.PropertyValues.DataSource dataSource, long j) {
            int frameCount = getFrameCount(j);
            int[] iArr = new int[frameCount];
            float f = frameCount - 1;
            for (int i = 0; i < frameCount; i++) {
                iArr[i] = ((Integer) dataSource.getValueAtFraction(i / f)).intValue();
            }
            return iArr;
        }

        private void createNativeChildAnimator(long j, long j2, ObjectAnimator objectAnimator) {
            long duration = objectAnimator.getDuration();
            int repeatCount = objectAnimator.getRepeatCount();
            long startDelay = j2 + objectAnimator.getStartDelay();
            long jCreateNativeInterpolator = RenderNodeAnimatorSetHelper.createNativeInterpolator(objectAnimator.getInterpolator(), duration);
            long durationScale = (long) (startDelay * ValueAnimator.getDurationScale());
            long durationScale2 = (long) (duration * ValueAnimator.getDurationScale());
            this.mStartDelays.add(durationScale);
            AnimatedVectorDrawable.nAddAnimator(this.mSetPtr, j, jCreateNativeInterpolator, durationScale, durationScale2, repeatCount, objectAnimator.getRepeatMode());
        }

        private void createRTAnimator(ObjectAnimator objectAnimator, long j) {
            PropertyValuesHolder[] values = objectAnimator.getValues();
            Object target = objectAnimator.getTarget();
            if (target instanceof VectorDrawable.VGroup) {
                createRTAnimatorForGroup(values, objectAnimator, (VectorDrawable.VGroup) target, j);
                return;
            }
            if (!(target instanceof VectorDrawable.VPath)) {
                if (target instanceof VectorDrawable.VectorDrawableState) {
                    createRTAnimatorForRootGroup(values, objectAnimator, (VectorDrawable.VectorDrawableState) target, j);
                    return;
                }
                return;
            }
            for (PropertyValuesHolder propertyValuesHolder : values) {
                propertyValuesHolder.getPropertyValues(this.mTmpValues);
                if ((this.mTmpValues.endValue instanceof PathParser.PathData) && this.mTmpValues.propertyName.equals("pathData")) {
                    createRTAnimatorForPath(objectAnimator, (VectorDrawable.VPath) target, j);
                } else if (target instanceof VectorDrawable.VFullPath) {
                    createRTAnimatorForFullPath(objectAnimator, (VectorDrawable.VFullPath) target, j);
                } else if (!this.mDrawable.mAnimatedVectorState.mShouldIgnoreInvalidAnim) {
                    throw new IllegalArgumentException("ClipPath only supports PathData property");
                }
            }
        }

        private void createRTAnimatorForFullPath(ObjectAnimator objectAnimator, VectorDrawable.VFullPath vFullPath, long j) {
            long jNCreatePathPropertyHolder;
            int propertyIndex = vFullPath.getPropertyIndex(this.mTmpValues.propertyName);
            long nativePtr = vFullPath.getNativePtr();
            if (this.mTmpValues.type == Float.class || this.mTmpValues.type == Float.TYPE) {
                if (propertyIndex < 0) {
                    if (this.mDrawable.mAnimatedVectorState.mShouldIgnoreInvalidAnim) {
                        return;
                    }
                    throw new IllegalArgumentException("Property: " + this.mTmpValues.propertyName + " is not supported for FullPath");
                }
                jNCreatePathPropertyHolder = AnimatedVectorDrawable.nCreatePathPropertyHolder(nativePtr, propertyIndex, ((Float) this.mTmpValues.startValue).floatValue(), ((Float) this.mTmpValues.endValue).floatValue());
                if (this.mTmpValues.dataSource != null) {
                    float[] fArrCreateFloatDataPoints = createFloatDataPoints(this.mTmpValues.dataSource, objectAnimator.getDuration());
                    AnimatedVectorDrawable.nSetPropertyHolderData(jNCreatePathPropertyHolder, fArrCreateFloatDataPoints, fArrCreateFloatDataPoints.length);
                }
            } else {
                if (this.mTmpValues.type != Integer.class && this.mTmpValues.type != Integer.TYPE) {
                    if (this.mDrawable.mAnimatedVectorState.mShouldIgnoreInvalidAnim) {
                        return;
                    }
                    throw new UnsupportedOperationException("Unsupported type: " + this.mTmpValues.type + ". Only float, int or PathData value is supported for Paths.");
                }
                jNCreatePathPropertyHolder = AnimatedVectorDrawable.nCreatePathColorPropertyHolder(nativePtr, propertyIndex, ((Integer) this.mTmpValues.startValue).intValue(), ((Integer) this.mTmpValues.endValue).intValue());
                if (this.mTmpValues.dataSource != null) {
                    int[] iArrCreateIntDataPoints = createIntDataPoints(this.mTmpValues.dataSource, objectAnimator.getDuration());
                    AnimatedVectorDrawable.nSetPropertyHolderData(jNCreatePathPropertyHolder, iArrCreateIntDataPoints, iArrCreateIntDataPoints.length);
                }
            }
            createNativeChildAnimator(jNCreatePathPropertyHolder, j, objectAnimator);
        }

        private void createRTAnimatorForGroup(PropertyValuesHolder[] propertyValuesHolderArr, ObjectAnimator objectAnimator, VectorDrawable.VGroup vGroup, long j) {
            long nativePtr = vGroup.getNativePtr();
            for (PropertyValuesHolder propertyValuesHolder : propertyValuesHolderArr) {
                propertyValuesHolder.getPropertyValues(this.mTmpValues);
                int propertyIndex = VectorDrawable.VGroup.getPropertyIndex(this.mTmpValues.propertyName);
                if ((this.mTmpValues.type == Float.class || this.mTmpValues.type == Float.TYPE) && propertyIndex >= 0) {
                    long jNCreateGroupPropertyHolder = AnimatedVectorDrawable.nCreateGroupPropertyHolder(nativePtr, propertyIndex, ((Float) this.mTmpValues.startValue).floatValue(), ((Float) this.mTmpValues.endValue).floatValue());
                    if (this.mTmpValues.dataSource != null) {
                        float[] fArrCreateFloatDataPoints = createFloatDataPoints(this.mTmpValues.dataSource, objectAnimator.getDuration());
                        AnimatedVectorDrawable.nSetPropertyHolderData(jNCreateGroupPropertyHolder, fArrCreateFloatDataPoints, fArrCreateFloatDataPoints.length);
                    }
                    createNativeChildAnimator(jNCreateGroupPropertyHolder, j, objectAnimator);
                }
            }
        }

        private void createRTAnimatorForPath(ObjectAnimator objectAnimator, VectorDrawable.VPath vPath, long j) {
            createNativeChildAnimator(AnimatedVectorDrawable.nCreatePathDataPropertyHolder(vPath.getNativePtr(), ((PathParser.PathData) this.mTmpValues.startValue).getNativePtr(), ((PathParser.PathData) this.mTmpValues.endValue).getNativePtr()), j, objectAnimator);
        }

        private void createRTAnimatorForRootGroup(PropertyValuesHolder[] propertyValuesHolderArr, ObjectAnimator objectAnimator, VectorDrawable.VectorDrawableState vectorDrawableState, long j) {
            Float f;
            Float f2;
            long nativeRenderer = vectorDrawableState.getNativeRenderer();
            if (!objectAnimator.getPropertyName().equals("alpha")) {
                if (!this.mDrawable.mAnimatedVectorState.mShouldIgnoreInvalidAnim) {
                    throw new UnsupportedOperationException("Only alpha is supported for root group");
                }
                return;
            }
            int i = 0;
            while (true) {
                f = null;
                if (i >= propertyValuesHolderArr.length) {
                    f2 = null;
                    break;
                }
                propertyValuesHolderArr[i].getPropertyValues(this.mTmpValues);
                if (this.mTmpValues.propertyName.equals("alpha")) {
                    f = (Float) this.mTmpValues.startValue;
                    f2 = (Float) this.mTmpValues.endValue;
                    break;
                }
                i++;
            }
            if (f == null && f2 == null) {
                if (!this.mDrawable.mAnimatedVectorState.mShouldIgnoreInvalidAnim) {
                    throw new UnsupportedOperationException("No alpha values are specified");
                }
                return;
            }
            long jNCreateRootAlphaPropertyHolder = AnimatedVectorDrawable.nCreateRootAlphaPropertyHolder(nativeRenderer, f.floatValue(), f2.floatValue());
            if (this.mTmpValues.dataSource != null) {
                float[] fArrCreateFloatDataPoints = createFloatDataPoints(this.mTmpValues.dataSource, objectAnimator.getDuration());
                AnimatedVectorDrawable.nSetPropertyHolderData(jNCreateRootAlphaPropertyHolder, fArrCreateFloatDataPoints, fArrCreateFloatDataPoints.length);
            }
            createNativeChildAnimator(jNCreateRootAlphaPropertyHolder, j, objectAnimator);
        }

        private void endAnimation() {
            AnimatedVectorDrawable.nEnd(this.mSetPtr);
            invalidateOwningView();
        }

        private static int getFrameCount(long j) {
            int iMax = Math.max(2, (int) Math.ceil(j / ((double) ((int) (Choreographer.getInstance().getFrameIntervalNanos() / TimeUtils.NANOS_PER_MS)))));
            if (iMax <= 300) {
                return iMax;
            }
            Log.w(AnimatedVectorDrawable.LOGTAG, "Duration for the animation is too long :" + j + ", the animation will subsample the keyframe or path data.");
            return 300;
        }

        private void handlePendingAction(int i) {
            if (i == 1) {
                startAnimation();
                return;
            }
            if (i == 2) {
                reverseAnimation();
                return;
            }
            if (i == 3) {
                resetAnimation();
                return;
            }
            if (i == 4) {
                endAnimation();
                return;
            }
            throw new UnsupportedOperationException("Animation action " + i + "is not supported");
        }

        private void invalidateOwningView() {
            this.mDrawable.invalidateSelf();
        }

        private void onAnimationEnd(int i) {
            if (i != this.mLastListenerId) {
                return;
            }
            this.mStarted = false;
            invalidateOwningView();
            if (this.mListener != null) {
                this.mListener.onAnimationEnd(null);
            }
        }

        private void parseAnimatorSet(AnimatorSet animatorSet, long j) {
            ArrayList<Animator> childAnimations = animatorSet.getChildAnimations();
            boolean zShouldPlayTogether = animatorSet.shouldPlayTogether();
            for (int i = 0; i < childAnimations.size(); i++) {
                Animator animator = childAnimations.get(i);
                if (animator instanceof AnimatorSet) {
                    parseAnimatorSet((AnimatorSet) animator, j);
                } else if (animator instanceof ObjectAnimator) {
                    createRTAnimator((ObjectAnimator) animator, j);
                }
                if (!zShouldPlayTogether) {
                    j += animator.getTotalDuration();
                    this.mContainsSequentialAnimators = true;
                }
            }
        }

        private void resetAnimation() {
            AnimatedVectorDrawable.nReset(this.mSetPtr);
            invalidateOwningView();
        }

        private void reverseAnimation() {
            this.mStarted = true;
            long j = this.mSetPtr;
            int i = this.mLastListenerId + 1;
            this.mLastListenerId = i;
            AnimatedVectorDrawable.nReverse(j, this, i);
            invalidateOwningView();
            if (this.mListener != null) {
                this.mListener.onAnimationStart(null);
            }
        }

        private void startAnimation() {
            this.mStarted = true;
            long j = this.mSetPtr;
            int i = this.mLastListenerId + 1;
            this.mLastListenerId = i;
            AnimatedVectorDrawable.nStart(j, this, i);
            invalidateOwningView();
            if (this.mListener != null) {
                this.mListener.onAnimationStart(null);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void transferPendingActions(VectorDrawableAnimator vectorDrawableAnimator) {
            for (int i = 0; i < this.mPendingAnimationActions.size(); i++) {
                int i2 = this.mPendingAnimationActions.get(i);
                if (i2 == 1) {
                    vectorDrawableAnimator.start();
                } else if (i2 == 4) {
                    vectorDrawableAnimator.end();
                } else if (i2 == 2) {
                    vectorDrawableAnimator.reverse();
                } else {
                    if (i2 != 3) {
                        throw new UnsupportedOperationException("Animation action " + i2 + "is not supported");
                    }
                    vectorDrawableAnimator.reset();
                }
            }
            this.mPendingAnimationActions.clear();
        }

        private boolean useLastSeenTarget() {
            if (this.mLastSeenTarget != null) {
                return useTarget(this.mLastSeenTarget.get());
            }
            return false;
        }

        private boolean useTarget(RenderNode renderNode) {
            if (renderNode == null || !renderNode.isAttached()) {
                return false;
            }
            renderNode.registerVectorDrawableAnimator(this);
            return true;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean canReverse() {
            return this.mIsReversible;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void end() {
            if (this.mInitialized) {
                if (useLastSeenTarget()) {
                    endAnimation();
                } else {
                    addPendingAction(4);
                }
            }
        }

        public long getAnimatorNativePtr() {
            return this.mSetPtr;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void init(AnimatorSet animatorSet) {
            if (this.mInitialized) {
                throw new UnsupportedOperationException("VectorDrawableAnimator cannot be re-initialized");
            }
            parseAnimatorSet(animatorSet, 0L);
            AnimatedVectorDrawable.nSetVectorDrawableTarget(this.mSetPtr, this.mDrawable.mAnimatedVectorState.mVectorDrawable.getNativeTree());
            this.mInitialized = true;
            this.mIsInfinite = animatorSet.getTotalDuration() == -1;
            this.mIsReversible = true;
            if (this.mContainsSequentialAnimators) {
                this.mIsReversible = false;
                return;
            }
            for (int i = 0; i < this.mStartDelays.size(); i++) {
                if (this.mStartDelays.get(i) > 0) {
                    this.mIsReversible = false;
                    return;
                }
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isInfinite() {
            return this.mIsInfinite;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isRunning() {
            if (this.mInitialized) {
                return this.mStarted;
            }
            return false;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isStarted() {
            return this.mStarted;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void onDraw(Canvas canvas) {
            if (canvas.isHardwareAccelerated()) {
                recordLastSeenTarget((DisplayListCanvas) canvas);
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void pause() {
        }

        protected void recordLastSeenTarget(DisplayListCanvas displayListCanvas) {
            RenderNode target = RenderNodeAnimatorSetHelper.getTarget(displayListCanvas);
            this.mLastSeenTarget = new WeakReference<>(target);
            if ((this.mInitialized || this.mPendingAnimationActions.size() > 0) && useTarget(target)) {
                for (int i = 0; i < this.mPendingAnimationActions.size(); i++) {
                    handlePendingAction(this.mPendingAnimationActions.get(i));
                }
                this.mPendingAnimationActions.clear();
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void removeListener(Animator.AnimatorListener animatorListener) {
            this.mListener = null;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void reset() {
            if (this.mInitialized) {
                if (useLastSeenTarget()) {
                    resetAnimation();
                } else {
                    addPendingAction(3);
                }
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void resume() {
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void reverse() {
            if (this.mIsReversible && this.mInitialized) {
                if (useLastSeenTarget()) {
                    reverseAnimation();
                } else {
                    addPendingAction(2);
                }
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void setListener(Animator.AnimatorListener animatorListener) {
            this.mListener = animatorListener;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void start() {
            if (this.mInitialized) {
                if (useLastSeenTarget()) {
                    startAnimation();
                } else {
                    addPendingAction(1);
                }
            }
        }
    }

    private static class VectorDrawableAnimatorUI implements VectorDrawableAnimator {
        private final Drawable mDrawable;
        private AnimatorSet mSet = null;
        private ArrayList<Animator.AnimatorListener> mListenerArray = null;
        private boolean mIsInfinite = false;

        VectorDrawableAnimatorUI(AnimatedVectorDrawable animatedVectorDrawable) {
            this.mDrawable = animatedVectorDrawable;
        }

        private void invalidateOwningView() {
            this.mDrawable.invalidateSelf();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean canReverse() {
            return this.mSet != null && this.mSet.canReverse();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void end() {
            if (this.mSet == null) {
                return;
            }
            this.mSet.end();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void init(AnimatorSet animatorSet) {
            if (this.mSet != null) {
                throw new UnsupportedOperationException("VectorDrawableAnimator cannot be re-initialized");
            }
            this.mSet = animatorSet.mo0clone();
            this.mIsInfinite = this.mSet.getTotalDuration() == -1;
            if (this.mListenerArray == null || this.mListenerArray.isEmpty()) {
                return;
            }
            for (int i = 0; i < this.mListenerArray.size(); i++) {
                this.mSet.addListener(this.mListenerArray.get(i));
            }
            this.mListenerArray.clear();
            this.mListenerArray = null;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isInfinite() {
            return this.mIsInfinite;
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isRunning() {
            return this.mSet != null && this.mSet.isRunning();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public boolean isStarted() {
            return this.mSet != null && this.mSet.isStarted();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void onDraw(Canvas canvas) {
            if (this.mSet == null || !this.mSet.isStarted()) {
                return;
            }
            invalidateOwningView();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void pause() {
            if (this.mSet == null) {
                return;
            }
            this.mSet.pause();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void removeListener(Animator.AnimatorListener animatorListener) {
            if (this.mSet != null) {
                this.mSet.removeListener(animatorListener);
            } else {
                if (this.mListenerArray == null) {
                    return;
                }
                this.mListenerArray.remove(animatorListener);
            }
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void reset() {
            if (this.mSet == null) {
                return;
            }
            start();
            this.mSet.cancel();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void resume() {
            if (this.mSet == null) {
                return;
            }
            this.mSet.resume();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void reverse() {
            if (this.mSet == null) {
                return;
            }
            this.mSet.reverse();
            invalidateOwningView();
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void setListener(Animator.AnimatorListener animatorListener) {
            if (this.mSet != null) {
                this.mSet.addListener(animatorListener);
                return;
            }
            if (this.mListenerArray == null) {
                this.mListenerArray = new ArrayList<>();
            }
            this.mListenerArray.add(animatorListener);
        }

        @Override // android.graphics.drawable.AnimatedVectorDrawable.VectorDrawableAnimator
        public void start() {
            if (this.mSet == null || this.mSet.isStarted()) {
                return;
            }
            this.mSet.start();
            invalidateOwningView();
        }
    }

    public AnimatedVectorDrawable() {
        this(null, null);
    }

    private AnimatedVectorDrawable(AnimatedVectorDrawableState animatedVectorDrawableState, Resources resources) {
        this.mAnimatorSetFromXml = null;
        this.mAnimationCallbacks = null;
        this.mAnimatorListener = null;
        this.mCallback = new Drawable.Callback() { // from class: android.graphics.drawable.AnimatedVectorDrawable.1
            @Override // android.graphics.drawable.Drawable.Callback
            public void invalidateDrawable(Drawable drawable) {
                AnimatedVectorDrawable.this.invalidateSelf();
            }

            @Override // android.graphics.drawable.Drawable.Callback
            public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                AnimatedVectorDrawable.this.scheduleSelf(runnable, j);
            }

            @Override // android.graphics.drawable.Drawable.Callback
            public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                AnimatedVectorDrawable.this.unscheduleSelf(runnable);
            }
        };
        this.mAnimatedVectorState = new AnimatedVectorDrawableState(animatedVectorDrawableState, this.mCallback, resources);
        this.mAnimatorSet = new VectorDrawableAnimatorRT(this);
        this.mRes = resources;
    }

    private static boolean containsSameValueType(PropertyValuesHolder propertyValuesHolder, Property property) {
        Class valueType = propertyValuesHolder.getValueType();
        Class type = property.getType();
        if (valueType == Float.TYPE || valueType == Float.class) {
            return type == Float.TYPE || type == Float.class;
        }
        if (valueType == Integer.TYPE || valueType == Integer.class) {
            return type == Integer.TYPE || type == Integer.class;
        }
        return valueType == type;
    }

    private void ensureAnimatorSet() {
        if (this.mAnimatorSetFromXml == null) {
            this.mAnimatorSetFromXml = new AnimatorSet();
            this.mAnimatedVectorState.prepareLocalAnimators(this.mAnimatorSetFromXml, this.mRes);
            this.mAnimatorSet.init(this.mAnimatorSetFromXml);
            this.mRes = null;
        }
    }

    private void fallbackOntoUI() {
        if (this.mAnimatorSet instanceof VectorDrawableAnimatorRT) {
            VectorDrawableAnimatorRT vectorDrawableAnimatorRT = (VectorDrawableAnimatorRT) this.mAnimatorSet;
            this.mAnimatorSet = new VectorDrawableAnimatorUI(this);
            if (this.mAnimatorSetFromXml != null) {
                this.mAnimatorSet.init(this.mAnimatorSetFromXml);
            }
            if (vectorDrawableAnimatorRT.mListener != null) {
                this.mAnimatorSet.setListener(vectorDrawableAnimatorRT.mListener);
            }
            vectorDrawableAnimatorRT.transferPendingActions(this.mAnimatorSet);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nAddAnimator(long j, long j2, long j3, long j4, long j5, int i, int i2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native long nCreateAnimatorSet();

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateGroupPropertyHolder(long j, int i, float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreatePathColorPropertyHolder(long j, int i, int i2, int i3);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreatePathDataPropertyHolder(long j, long j2, long j3);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreatePathPropertyHolder(long j, int i, float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native long nCreateRootAlphaPropertyHolder(long j, float f, float f2);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nEnd(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native void nReset(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nReverse(long j, VectorDrawableAnimatorRT vectorDrawableAnimatorRT, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nSetPropertyHolderData(long j, float[] fArr, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nSetPropertyHolderData(long j, int[] iArr, int i);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nSetVectorDrawableTarget(long j, long j2);

    /* JADX INFO: Access modifiers changed from: private */
    public static native void nStart(long j, VectorDrawableAnimatorRT vectorDrawableAnimatorRT, int i);

    private void removeAnimatorSetListener() {
        if (this.mAnimatorListener != null) {
            this.mAnimatorSet.removeListener(this.mAnimatorListener);
            this.mAnimatorListener = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean shouldIgnoreInvalidAnimation() {
        Application applicationCurrentApplication = ActivityThread.currentApplication();
        return applicationCurrentApplication == null || applicationCurrentApplication.getApplicationInfo() == null || applicationCurrentApplication.getApplicationInfo().targetSdkVersion < 24;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void updateAnimatorProperty(Animator animator, String str, VectorDrawable vectorDrawable, boolean z) {
        if (!(animator instanceof ObjectAnimator)) {
            if (animator instanceof AnimatorSet) {
                Iterator<Animator> it = ((AnimatorSet) animator).getChildAnimations().iterator();
                while (it.hasNext()) {
                    updateAnimatorProperty(it.next(), str, vectorDrawable, z);
                }
                return;
            }
            return;
        }
        for (PropertyValuesHolder propertyValuesHolder : ((ObjectAnimator) animator).getValues()) {
            String propertyName = propertyValuesHolder.getPropertyName();
            Object targetByName = vectorDrawable.getTargetByName(str);
            Property property = null;
            if (targetByName instanceof VectorDrawable.VObject) {
                property = ((VectorDrawable.VObject) targetByName).getProperty(propertyName);
            } else if (targetByName instanceof VectorDrawable.VectorDrawableState) {
                property = ((VectorDrawable.VectorDrawableState) targetByName).getProperty(propertyName);
            }
            if (property != null) {
                if (containsSameValueType(propertyValuesHolder, property)) {
                    propertyValuesHolder.setProperty(property);
                } else if (!z) {
                    throw new RuntimeException("Wrong valueType for Property: " + propertyName + ".  Expected type: " + property.getType().toString() + ". Actual type defined in resources: " + propertyValuesHolder.getValueType().toString());
                }
            }
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void applyTheme(Resources.Theme theme) {
        super.applyTheme(theme);
        VectorDrawable vectorDrawable = this.mAnimatedVectorState.mVectorDrawable;
        if (vectorDrawable != null && vectorDrawable.canApplyTheme()) {
            vectorDrawable.applyTheme(theme);
        }
        if (theme != null) {
            this.mAnimatedVectorState.inflatePendingAnimators(theme.getResources(), theme);
        }
        if (this.mAnimatedVectorState.mPendingAnims == null) {
            this.mRes = null;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public boolean canApplyTheme() {
        return (this.mAnimatedVectorState != null && this.mAnimatedVectorState.canApplyTheme()) || super.canApplyTheme();
    }

    public boolean canReverse() {
        return this.mAnimatorSet.canReverse();
    }

    @Override // android.graphics.drawable.Animatable2
    public void clearAnimationCallbacks() {
        removeAnimatorSetListener();
        if (this.mAnimationCallbacks == null) {
            return;
        }
        this.mAnimationCallbacks.clear();
    }

    @Override // android.graphics.drawable.Drawable
    public void clearMutated() {
        super.clearMutated();
        if (this.mAnimatedVectorState.mVectorDrawable != null) {
            this.mAnimatedVectorState.mVectorDrawable.clearMutated();
        }
        this.mMutated = false;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (!canvas.isHardwareAccelerated() && (this.mAnimatorSet instanceof VectorDrawableAnimatorRT) && !this.mAnimatorSet.isRunning() && ((VectorDrawableAnimatorRT) this.mAnimatorSet).mPendingAnimationActions.size() > 0) {
            fallbackOntoUI();
        }
        this.mAnimatorSet.onDraw(canvas);
        this.mAnimatedVectorState.mVectorDrawable.draw(canvas);
    }

    public void forceAnimationOnUI() {
        if (this.mAnimatorSet instanceof VectorDrawableAnimatorRT) {
            if (((VectorDrawableAnimatorRT) this.mAnimatorSet).isRunning()) {
                throw new UnsupportedOperationException("Cannot force Animated Vector Drawable to run on UI thread when the animation has started on RenderThread.");
            }
            fallbackOntoUI();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mAnimatedVectorState.mVectorDrawable.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public int getChangingConfigurations() {
        return this.mAnimatedVectorState.getChangingConfigurations() | super.getChangingConfigurations();
    }

    @Override // android.graphics.drawable.Drawable
    public ColorFilter getColorFilter() {
        return this.mAnimatedVectorState.mVectorDrawable.getColorFilter();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable.ConstantState getConstantState() {
        this.mAnimatedVectorState.mChangingConfigurations = getChangingConfigurations();
        return this.mAnimatedVectorState;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mAnimatedVectorState.mVectorDrawable.getIntrinsicWidth();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public Insets getOpticalInsets() {
        return this.mAnimatedVectorState.mVectorDrawable.getOpticalInsets();
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        this.mAnimatedVectorState.mVectorDrawable.getOutline(outline);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) throws XmlPullParserException, IOException {
        AnimatedVectorDrawableState animatedVectorDrawableState = this.mAnimatedVectorState;
        int eventType = xmlPullParser.getEventType();
        int depth = xmlPullParser.getDepth() + 1;
        float f = 1.0f;
        while (eventType != 1 && (xmlPullParser.getDepth() >= depth || eventType != 3)) {
            if (eventType == 2) {
                String name = xmlPullParser.getName();
                if (ANIMATED_VECTOR.equals(name)) {
                    TypedArray typedArrayObtainAttributes = obtainAttributes(resources, theme, attributeSet, R.styleable.AnimatedVectorDrawable);
                    int resourceId = typedArrayObtainAttributes.getResourceId(0, 0);
                    if (resourceId != 0) {
                        VectorDrawable vectorDrawable = (VectorDrawable) resources.getDrawable(resourceId, theme).mutate();
                        vectorDrawable.setAllowCaching(false);
                        vectorDrawable.setCallback(this.mCallback);
                        float pixelSize = vectorDrawable.getPixelSize();
                        if (animatedVectorDrawableState.mVectorDrawable != null) {
                            animatedVectorDrawableState.mVectorDrawable.setCallback(null);
                        }
                        animatedVectorDrawableState.mVectorDrawable = vectorDrawable;
                        f = pixelSize;
                    }
                    typedArrayObtainAttributes.recycle();
                } else if (TARGET.equals(name)) {
                    TypedArray typedArrayObtainAttributes2 = obtainAttributes(resources, theme, attributeSet, R.styleable.AnimatedVectorDrawableTarget);
                    String string = typedArrayObtainAttributes2.getString(0);
                    int resourceId2 = typedArrayObtainAttributes2.getResourceId(1, 0);
                    if (resourceId2 != 0) {
                        if (theme != null) {
                            Animator animatorLoadAnimator = AnimatorInflater.loadAnimator(resources, theme, resourceId2, f);
                            updateAnimatorProperty(animatorLoadAnimator, string, animatedVectorDrawableState.mVectorDrawable, animatedVectorDrawableState.mShouldIgnoreInvalidAnim);
                            animatedVectorDrawableState.addTargetAnimator(string, animatorLoadAnimator);
                        } else {
                            animatedVectorDrawableState.addPendingAnimator(resourceId2, f, string);
                        }
                    }
                    typedArrayObtainAttributes2.recycle();
                }
            }
            eventType = xmlPullParser.next();
        }
        if (animatedVectorDrawableState.mPendingAnims == null) {
            resources = null;
        }
        this.mRes = resources;
    }

    @Override // android.graphics.drawable.Animatable
    public boolean isRunning() {
        return this.mAnimatorSet.isRunning();
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return this.mAnimatedVectorState.mVectorDrawable.isStateful();
    }

    @Override // android.graphics.drawable.Drawable
    public Drawable mutate() {
        if (!this.mMutated && super.mutate() == this) {
            this.mAnimatedVectorState = new AnimatedVectorDrawableState(this.mAnimatedVectorState, this.mCallback, this.mRes);
            this.mMutated = true;
        }
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        this.mAnimatedVectorState.mVectorDrawable.setBounds(rect);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean onLayoutDirectionChanged(int i) {
        return this.mAnimatedVectorState.mVectorDrawable.setLayoutDirection(i);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        return this.mAnimatedVectorState.mVectorDrawable.setLevel(i);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return this.mAnimatedVectorState.mVectorDrawable.setState(iArr);
    }

    @Override // android.graphics.drawable.Animatable2
    public void registerAnimationCallback(Animatable2.AnimationCallback animationCallback) {
        if (animationCallback == null) {
            return;
        }
        if (this.mAnimationCallbacks == null) {
            this.mAnimationCallbacks = new ArrayList<>();
        }
        this.mAnimationCallbacks.add(animationCallback);
        if (this.mAnimatorListener == null) {
            this.mAnimatorListener = new AnimatorListenerAdapter() { // from class: android.graphics.drawable.AnimatedVectorDrawable.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ArrayList arrayList = new ArrayList(AnimatedVectorDrawable.this.mAnimationCallbacks);
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        ((Animatable2.AnimationCallback) arrayList.get(i)).onAnimationEnd(AnimatedVectorDrawable.this);
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ArrayList arrayList = new ArrayList(AnimatedVectorDrawable.this.mAnimationCallbacks);
                    int size = arrayList.size();
                    for (int i = 0; i < size; i++) {
                        ((Animatable2.AnimationCallback) arrayList.get(i)).onAnimationStart(AnimatedVectorDrawable.this);
                    }
                }
            };
        }
        this.mAnimatorSet.setListener(this.mAnimatorListener);
    }

    public void reset() {
        ensureAnimatorSet();
        this.mAnimatorSet.reset();
    }

    public void reverse() {
        ensureAnimatorSet();
        if (canReverse()) {
            this.mAnimatorSet.reverse();
        } else {
            Log.w(LOGTAG, "AnimatedVectorDrawable can't reverse()");
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mAnimatedVectorState.mVectorDrawable.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mAnimatedVectorState.mVectorDrawable.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float f, float f2) {
        this.mAnimatedVectorState.mVectorDrawable.setHotspot(f, f2);
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspotBounds(int i, int i2, int i3, int i4) {
        this.mAnimatedVectorState.mVectorDrawable.setHotspotBounds(i, i2, i3, i4);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintList(ColorStateList colorStateList) {
        this.mAnimatedVectorState.mVectorDrawable.setTintList(colorStateList);
    }

    @Override // android.graphics.drawable.Drawable
    public void setTintMode(PorterDuff.Mode mode) {
        this.mAnimatedVectorState.mVectorDrawable.setTintMode(mode);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        if (this.mAnimatorSet.isInfinite() && this.mAnimatorSet.isStarted()) {
            if (z) {
                this.mAnimatorSet.resume();
            } else {
                this.mAnimatorSet.pause();
            }
        }
        this.mAnimatedVectorState.mVectorDrawable.setVisible(z, z2);
        return super.setVisible(z, z2);
    }

    @Override // android.graphics.drawable.Animatable
    public void start() {
        ensureAnimatorSet();
        this.mAnimatorSet.start();
    }

    @Override // android.graphics.drawable.Animatable
    public void stop() {
        this.mAnimatorSet.end();
    }

    @Override // android.graphics.drawable.Animatable2
    public boolean unregisterAnimationCallback(Animatable2.AnimationCallback animationCallback) {
        if (this.mAnimationCallbacks == null || animationCallback == null) {
            return false;
        }
        boolean zRemove = this.mAnimationCallbacks.remove(animationCallback);
        if (this.mAnimationCallbacks.size() == 0) {
            removeAnimatorSetListener();
        }
        return zRemove;
    }
}
