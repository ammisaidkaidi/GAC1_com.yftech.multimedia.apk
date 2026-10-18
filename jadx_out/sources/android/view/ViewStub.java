package android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.RemoteViews;
import com.android.internal.R;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public final class ViewStub extends View {
    private OnInflateListener mInflateListener;
    private int mInflatedId;
    private WeakReference<View> mInflatedViewRef;
    private LayoutInflater mInflater;
    private int mLayoutResource;

    public interface OnInflateListener {
        void onInflate(ViewStub viewStub, View view);
    }

    public class ViewReplaceRunnable implements Runnable {
        public final View view;

        ViewReplaceRunnable(View view) {
            this.view = view;
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewStub.this.replaceSelfWithView(this.view, (ViewGroup) ViewStub.this.getParent());
        }
    }

    public ViewStub(Context context) {
        this(context, 0);
    }

    public ViewStub(Context context, int i) {
        this(context, (AttributeSet) null);
        this.mLayoutResource = i;
    }

    public ViewStub(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ViewStub(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ViewStub(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ViewStub, i, i2);
        this.mInflatedId = typedArrayObtainStyledAttributes.getResourceId(2, -1);
        this.mLayoutResource = typedArrayObtainStyledAttributes.getResourceId(1, 0);
        this.mID = typedArrayObtainStyledAttributes.getResourceId(0, -1);
        typedArrayObtainStyledAttributes.recycle();
        setVisibility(8);
        setWillNotDraw(true);
    }

    private View inflateViewNoAdd(ViewGroup viewGroup) {
        View viewInflate = (this.mInflater != null ? this.mInflater : LayoutInflater.from(this.mContext)).inflate(this.mLayoutResource, viewGroup, false);
        if (this.mInflatedId != -1) {
            viewInflate.setId(this.mInflatedId);
        }
        return viewInflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void replaceSelfWithView(View view, ViewGroup viewGroup) {
        int iIndexOfChild = viewGroup.indexOfChild(this);
        viewGroup.removeViewInLayout(this);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams != null) {
            viewGroup.addView(view, iIndexOfChild, layoutParams);
        } else {
            viewGroup.addView(view, iIndexOfChild);
        }
    }

    @Override // android.view.View
    protected void dispatchDraw(Canvas canvas) {
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
    }

    public int getInflatedId() {
        return this.mInflatedId;
    }

    public LayoutInflater getLayoutInflater() {
        return this.mInflater;
    }

    public int getLayoutResource() {
        return this.mLayoutResource;
    }

    public View inflate() {
        ViewParent parent = getParent();
        if (parent == null || !(parent instanceof ViewGroup)) {
            throw new IllegalStateException("ViewStub must have a non-null ViewGroup viewParent");
        }
        if (this.mLayoutResource == 0) {
            throw new IllegalArgumentException("ViewStub must have a valid layoutResource");
        }
        ViewGroup viewGroup = (ViewGroup) parent;
        View viewInflateViewNoAdd = inflateViewNoAdd(viewGroup);
        replaceSelfWithView(viewInflateViewNoAdd, viewGroup);
        this.mInflatedViewRef = new WeakReference<>(viewInflateViewNoAdd);
        if (this.mInflateListener != null) {
            this.mInflateListener.onInflate(this, viewInflateViewNoAdd);
        }
        return viewInflateViewNoAdd;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(0, 0);
    }

    @RemotableViewMethod(asyncImpl = "setInflatedIdAsync")
    public void setInflatedId(int i) {
        this.mInflatedId = i;
    }

    public Runnable setInflatedIdAsync(int i) {
        this.mInflatedId = i;
        return null;
    }

    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.mInflater = layoutInflater;
    }

    @RemotableViewMethod(asyncImpl = "setLayoutResourceAsync")
    public void setLayoutResource(int i) {
        this.mLayoutResource = i;
    }

    public Runnable setLayoutResourceAsync(int i) {
        this.mLayoutResource = i;
        return null;
    }

    public void setOnInflateListener(OnInflateListener onInflateListener) {
        this.mInflateListener = onInflateListener;
    }

    @Override // android.view.View
    @RemotableViewMethod(asyncImpl = "setVisibilityAsync")
    public void setVisibility(int i) {
        if (this.mInflatedViewRef != null) {
            View view = this.mInflatedViewRef.get();
            if (view == null) {
                throw new IllegalStateException("setVisibility called on un-referenced view");
            }
            view.setVisibility(i);
            return;
        }
        super.setVisibility(i);
        if (i == 0 || i == 4) {
            inflate();
        }
    }

    public Runnable setVisibilityAsync(int i) {
        if (i == 0 || i == 4) {
            return new ViewReplaceRunnable(inflateViewNoAdd((ViewGroup) getParent()));
        }
        return null;
    }
}
