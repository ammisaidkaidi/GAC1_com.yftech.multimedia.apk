package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.internal.R;

/* JADX INFO: loaded from: classes2.dex */
public class ViewAnimator extends FrameLayout {
    boolean mAnimateFirstTime;
    boolean mFirstTime;
    Animation mInAnimation;
    Animation mOutAnimation;
    int mWhichChild;

    public ViewAnimator(Context context) {
        super(context);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        initViewAnimator(context, null);
    }

    public ViewAnimator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mWhichChild = 0;
        this.mFirstTime = true;
        this.mAnimateFirstTime = true;
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewAnimator);
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        if (resourceId > 0) {
            setInAnimation(context, resourceId);
        }
        int resourceId2 = typedArrayObtainStyledAttributes.getResourceId(1, 0);
        if (resourceId2 > 0) {
            setOutAnimation(context, resourceId2);
        }
        setAnimateFirstView(typedArrayObtainStyledAttributes.getBoolean(2, true));
        typedArrayObtainStyledAttributes.recycle();
        initViewAnimator(context, attributeSet);
    }

    private void initViewAnimator(Context context, AttributeSet attributeSet) {
        if (attributeSet == null) {
            this.mMeasureAllChildren = true;
            return;
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.FrameLayout);
        setMeasureAllChildren(typedArrayObtainStyledAttributes.getBoolean(0, true));
        typedArrayObtainStyledAttributes.recycle();
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        if (getChildCount() == 1) {
            view.setVisibility(0);
        } else {
            view.setVisibility(8);
        }
        if (i < 0 || this.mWhichChild < i) {
            return;
        }
        setDisplayedChild(this.mWhichChild + 1);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return ViewAnimator.class.getName();
    }

    public boolean getAnimateFirstView() {
        return this.mAnimateFirstTime;
    }

    @Override // android.view.View
    public int getBaseline() {
        return getCurrentView() != null ? getCurrentView().getBaseline() : super.getBaseline();
    }

    public View getCurrentView() {
        return getChildAt(this.mWhichChild);
    }

    public int getDisplayedChild() {
        return this.mWhichChild;
    }

    public Animation getInAnimation() {
        return this.mInAnimation;
    }

    public Animation getOutAnimation() {
        return this.mOutAnimation;
    }

    @Override // android.view.ViewGroup
    public void removeAllViews() {
        super.removeAllViews();
        this.mWhichChild = 0;
        this.mFirstTime = true;
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        int iIndexOfChild = indexOfChild(view);
        if (iIndexOfChild >= 0) {
            removeViewAt(iIndexOfChild);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewAt(int i) {
        super.removeViewAt(i);
        int childCount = getChildCount();
        if (childCount == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
        } else if (this.mWhichChild >= childCount) {
            setDisplayedChild(childCount - 1);
        } else if (this.mWhichChild == i) {
            setDisplayedChild(this.mWhichChild);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewInLayout(View view) {
        removeView(view);
    }

    @Override // android.view.ViewGroup
    public void removeViews(int i, int i2) {
        super.removeViews(i, i2);
        if (getChildCount() == 0) {
            this.mWhichChild = 0;
            this.mFirstTime = true;
        } else {
            if (this.mWhichChild < i || this.mWhichChild >= i + i2) {
                return;
            }
            setDisplayedChild(this.mWhichChild);
        }
    }

    @Override // android.view.ViewGroup
    public void removeViewsInLayout(int i, int i2) {
        removeViews(i, i2);
    }

    public void setAnimateFirstView(boolean z) {
        this.mAnimateFirstTime = z;
    }

    @RemotableViewMethod
    public void setDisplayedChild(int i) {
        this.mWhichChild = i;
        if (i >= getChildCount()) {
            this.mWhichChild = 0;
        } else if (i < 0) {
            this.mWhichChild = getChildCount() - 1;
        }
        boolean z = getFocusedChild() != null;
        showOnly(this.mWhichChild);
        if (z) {
            requestFocus(2);
        }
    }

    public void setInAnimation(Context context, int i) {
        setInAnimation(AnimationUtils.loadAnimation(context, i));
    }

    public void setInAnimation(Animation animation) {
        this.mInAnimation = animation;
    }

    public void setOutAnimation(Context context, int i) {
        setOutAnimation(AnimationUtils.loadAnimation(context, i));
    }

    public void setOutAnimation(Animation animation) {
        this.mOutAnimation = animation;
    }

    @RemotableViewMethod
    public void showNext() {
        setDisplayedChild(this.mWhichChild + 1);
    }

    void showOnly(int i) {
        showOnly(i, !this.mFirstTime || this.mAnimateFirstTime);
    }

    void showOnly(int i, boolean z) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (i2 == i) {
                if (z && this.mInAnimation != null) {
                    childAt.startAnimation(this.mInAnimation);
                }
                childAt.setVisibility(0);
                this.mFirstTime = false;
            } else {
                if (z && this.mOutAnimation != null && childAt.getVisibility() == 0) {
                    childAt.startAnimation(this.mOutAnimation);
                } else if (childAt.getAnimation() == this.mInAnimation) {
                    childAt.clearAnimation();
                }
                childAt.setVisibility(8);
            }
        }
    }

    @RemotableViewMethod
    public void showPrevious() {
        setDisplayedChild(this.mWhichChild - 1);
    }
}
