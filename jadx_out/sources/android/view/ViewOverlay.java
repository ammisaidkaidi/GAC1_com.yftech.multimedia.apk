package android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes2.dex */
public class ViewOverlay {
    OverlayViewGroup mOverlayViewGroup;

    static class OverlayViewGroup extends ViewGroup {
        ArrayList<Drawable> mDrawables;
        final View mHostView;

        OverlayViewGroup(Context context, View view) {
            super(context);
            this.mDrawables = null;
            this.mHostView = view;
            this.mAttachInfo = this.mHostView.mAttachInfo;
            this.mRight = view.getWidth();
            this.mBottom = view.getHeight();
            this.mRenderNode.setLeftTopRightBottom(0, 0, this.mRight, this.mBottom);
        }

        public void add(Drawable drawable) {
            if (drawable == null) {
                throw new IllegalArgumentException("drawable must be non-null");
            }
            if (this.mDrawables == null) {
                this.mDrawables = new ArrayList<>();
            }
            if (this.mDrawables.contains(drawable)) {
                return;
            }
            this.mDrawables.add(drawable);
            invalidate(drawable.getBounds());
            drawable.setCallback(this);
        }

        public void add(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view must be non-null");
            }
            if (view.getParent() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                if (viewGroup != this.mHostView && viewGroup.getParent() != null && viewGroup.mAttachInfo != null) {
                    int[] iArr = new int[2];
                    int[] iArr2 = new int[2];
                    viewGroup.getLocationOnScreen(iArr);
                    this.mHostView.getLocationOnScreen(iArr2);
                    view.offsetLeftAndRight(iArr[0] - iArr2[0]);
                    view.offsetTopAndBottom(iArr[1] - iArr2[1]);
                }
                viewGroup.removeView(view);
                if (viewGroup.getLayoutTransition() != null) {
                    viewGroup.getLayoutTransition().cancel(3);
                }
                if (view.getParent() != null) {
                    view.mParent = null;
                }
            }
            super.addView(view);
        }

        public void clear() {
            removeAllViews();
            if (this.mDrawables != null) {
                Iterator<Drawable> it = this.mDrawables.iterator();
                while (it.hasNext()) {
                    it.next().setCallback(null);
                }
                this.mDrawables.clear();
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void dispatchDraw(Canvas canvas) {
            canvas.insertReorderBarrier();
            super.dispatchDraw(canvas);
            canvas.insertInorderBarrier();
            int size = this.mDrawables == null ? 0 : this.mDrawables.size();
            for (int i = 0; i < size; i++) {
                this.mDrawables.get(i).draw(canvas);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            super.invalidate();
            if (this.mHostView != null) {
                this.mHostView.invalidate();
            }
        }

        @Override // android.view.View
        public void invalidate(int i, int i2, int i3, int i4) {
            super.invalidate(i, i2, i3, i4);
            if (this.mHostView != null) {
                this.mHostView.invalidate(i, i2, i3, i4);
            }
        }

        @Override // android.view.View
        public void invalidate(Rect rect) {
            super.invalidate(rect);
            if (this.mHostView != null) {
                this.mHostView.invalidate(rect);
            }
        }

        @Override // android.view.View
        public void invalidate(boolean z) {
            super.invalidate(z);
            if (this.mHostView != null) {
                this.mHostView.invalidate(z);
            }
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public ViewParent invalidateChildInParent(int[] iArr, Rect rect) {
            if (this.mHostView == null) {
                return null;
            }
            rect.offset(iArr[0], iArr[1]);
            if (!(this.mHostView instanceof ViewGroup)) {
                invalidate(rect);
                return null;
            }
            iArr[0] = 0;
            iArr[1] = 0;
            super.invalidateChildInParent(iArr, rect);
            return ((ViewGroup) this.mHostView).invalidateChildInParent(iArr, rect);
        }

        @Override // android.view.View, android.graphics.drawable.Drawable.Callback
        public void invalidateDrawable(Drawable drawable) {
            invalidate(drawable.getBounds());
        }

        @Override // android.view.View
        protected void invalidateParentCaches() {
            super.invalidateParentCaches();
            if (this.mHostView != null) {
                this.mHostView.invalidateParentCaches();
            }
        }

        @Override // android.view.View
        protected void invalidateParentIfNeeded() {
            super.invalidateParentIfNeeded();
            if (this.mHostView != null) {
                this.mHostView.invalidateParentIfNeeded();
            }
        }

        @Override // android.view.View
        void invalidateViewProperty(boolean z, boolean z2) {
            super.invalidateViewProperty(z, z2);
            if (this.mHostView != null) {
                this.mHostView.invalidateViewProperty(z, z2);
            }
        }

        boolean isEmpty() {
            if (getChildCount() == 0) {
                return this.mDrawables == null || this.mDrawables.size() == 0;
            }
            return false;
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void onDescendantInvalidated(View view, View view2) {
            if (this.mHostView != null) {
                if (!(this.mHostView instanceof ViewGroup)) {
                    invalidate();
                } else {
                    ((ViewGroup) this.mHostView).onDescendantInvalidated(this.mHostView, view2);
                    super.onDescendantInvalidated(view, view2);
                }
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        }

        public void remove(Drawable drawable) {
            if (drawable == null) {
                throw new IllegalArgumentException("drawable must be non-null");
            }
            if (this.mDrawables != null) {
                this.mDrawables.remove(drawable);
                invalidate(drawable.getBounds());
                drawable.setCallback(null);
            }
        }

        public void remove(View view) {
            if (view == null) {
                throw new IllegalArgumentException("view must be non-null");
            }
            super.removeView(view);
        }

        @Override // android.view.View
        protected boolean verifyDrawable(Drawable drawable) {
            return super.verifyDrawable(drawable) || (this.mDrawables != null && this.mDrawables.contains(drawable));
        }
    }

    ViewOverlay(Context context, View view) {
        this.mOverlayViewGroup = new OverlayViewGroup(context, view);
    }

    public void add(Drawable drawable) {
        this.mOverlayViewGroup.add(drawable);
    }

    public void clear() {
        this.mOverlayViewGroup.clear();
    }

    ViewGroup getOverlayView() {
        return this.mOverlayViewGroup;
    }

    boolean isEmpty() {
        return this.mOverlayViewGroup.isEmpty();
    }

    public void remove(Drawable drawable) {
        this.mOverlayViewGroup.remove(drawable);
    }
}
