package android.widget;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.ImageDecoder;
import android.graphics.Matrix;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.TtmlUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import java.io.IOException;

/* JADX INFO: loaded from: classes2.dex */
@RemoteViews.RemoteView
public class ImageView extends View {
    private static final String LOG_TAG = "ImageView";
    private static boolean sCompatAdjustViewBounds;
    private static boolean sCompatDone;
    private static boolean sCompatDrawableVisibilityDispatch;
    private static boolean sCompatUseCorrectStreamDensity;
    private boolean mAdjustViewBounds;
    private int mAlpha;
    private int mBaseline;
    private boolean mBaselineAlignBottom;
    private ColorFilter mColorFilter;
    private boolean mColorMod;
    private boolean mCropToPadding;
    private Matrix mDrawMatrix;
    private Drawable mDrawable;
    private int mDrawableHeight;
    private ColorStateList mDrawableTintList;
    private PorterDuff.Mode mDrawableTintMode;
    private int mDrawableWidth;
    private boolean mHasColorFilter;
    private boolean mHasDrawableTint;
    private boolean mHasDrawableTintMode;
    private boolean mHaveFrame;
    private int mLevel;
    private Matrix mMatrix;
    private int mMaxHeight;
    private int mMaxWidth;
    private boolean mMergeState;
    private BitmapDrawable mRecycleableBitmapDrawable;
    private int mResource;
    private ScaleType mScaleType;
    private int[] mState;
    private final RectF mTempDst;
    private final RectF mTempSrc;
    private Uri mUri;
    private final int mViewAlphaScale;
    private Xfermode mXfermode;
    private static final ScaleType[] sScaleTypeArray = {ScaleType.MATRIX, ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER, ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP, ScaleType.CENTER_INSIDE};
    private static final Matrix.ScaleToFit[] sS2FArray = {Matrix.ScaleToFit.FILL, Matrix.ScaleToFit.START, Matrix.ScaleToFit.CENTER, Matrix.ScaleToFit.END};

    private class ImageDrawableCallback implements Runnable {
        private final Drawable drawable;
        private final int resource;
        private final Uri uri;

        ImageDrawableCallback(Drawable drawable, Uri uri, int i) {
            this.drawable = drawable;
            this.uri = uri;
            this.resource = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            ImageView.this.setImageDrawable(this.drawable);
            ImageView.this.mUri = this.uri;
            ImageView.this.mResource = this.resource;
        }
    }

    public enum ScaleType {
        MATRIX(0),
        FIT_XY(1),
        FIT_START(2),
        FIT_CENTER(3),
        FIT_END(4),
        CENTER(5),
        CENTER_CROP(6),
        CENTER_INSIDE(7);

        final int nativeInt;

        ScaleType(int i) {
            this.nativeInt = i;
        }
    }

    public ImageView(Context context) {
        super(context);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mColorFilter = null;
        this.mHasColorFilter = false;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mRecycleableBitmapDrawable = null;
        this.mDrawableTintList = null;
        this.mDrawableTintMode = null;
        this.mHasDrawableTint = false;
        this.mHasDrawableTintMode = false;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        initImageView();
    }

    public ImageView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public ImageView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public ImageView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mResource = 0;
        this.mHaveFrame = false;
        this.mAdjustViewBounds = false;
        this.mMaxWidth = Integer.MAX_VALUE;
        this.mMaxHeight = Integer.MAX_VALUE;
        this.mColorFilter = null;
        this.mHasColorFilter = false;
        this.mAlpha = 255;
        this.mViewAlphaScale = 256;
        this.mColorMod = false;
        this.mDrawable = null;
        this.mRecycleableBitmapDrawable = null;
        this.mDrawableTintList = null;
        this.mDrawableTintMode = null;
        this.mHasDrawableTint = false;
        this.mHasDrawableTintMode = false;
        this.mState = null;
        this.mMergeState = false;
        this.mLevel = 0;
        this.mDrawMatrix = null;
        this.mTempSrc = new RectF();
        this.mTempDst = new RectF();
        this.mBaseline = -1;
        this.mBaselineAlignBottom = false;
        initImageView();
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(2);
        }
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.ImageView, i, i2);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(0);
        if (drawable != null) {
            setImageDrawable(drawable);
        }
        this.mBaselineAlignBottom = typedArrayObtainStyledAttributes.getBoolean(6, false);
        this.mBaseline = typedArrayObtainStyledAttributes.getDimensionPixelSize(8, -1);
        setAdjustViewBounds(typedArrayObtainStyledAttributes.getBoolean(2, false));
        setMaxWidth(typedArrayObtainStyledAttributes.getDimensionPixelSize(3, Integer.MAX_VALUE));
        setMaxHeight(typedArrayObtainStyledAttributes.getDimensionPixelSize(4, Integer.MAX_VALUE));
        int i3 = typedArrayObtainStyledAttributes.getInt(1, -1);
        if (i3 >= 0) {
            setScaleType(sScaleTypeArray[i3]);
        }
        if (typedArrayObtainStyledAttributes.hasValue(5)) {
            this.mDrawableTintList = typedArrayObtainStyledAttributes.getColorStateList(5);
            this.mHasDrawableTint = true;
            this.mDrawableTintMode = PorterDuff.Mode.SRC_ATOP;
            this.mHasDrawableTintMode = true;
        }
        if (typedArrayObtainStyledAttributes.hasValue(9)) {
            this.mDrawableTintMode = Drawable.parseTintMode(typedArrayObtainStyledAttributes.getInt(9, -1), this.mDrawableTintMode);
            this.mHasDrawableTintMode = true;
        }
        applyImageTint();
        int i4 = typedArrayObtainStyledAttributes.getInt(10, 255);
        if (i4 != 255) {
            setImageAlpha(i4);
        }
        this.mCropToPadding = typedArrayObtainStyledAttributes.getBoolean(7, false);
        typedArrayObtainStyledAttributes.recycle();
    }

    private void applyColorMod() {
        if (this.mDrawable == null || !this.mColorMod) {
            return;
        }
        this.mDrawable = this.mDrawable.mutate();
        if (this.mHasColorFilter) {
            this.mDrawable.setColorFilter(this.mColorFilter);
        }
        this.mDrawable.setXfermode(this.mXfermode);
        this.mDrawable.setAlpha((this.mAlpha * 256) >> 8);
    }

    private void applyImageTint() {
        if (this.mDrawable != null) {
            if (this.mHasDrawableTint || this.mHasDrawableTintMode) {
                this.mDrawable = this.mDrawable.mutate();
                if (this.mHasDrawableTint) {
                    this.mDrawable.setTintList(this.mDrawableTintList);
                }
                if (this.mHasDrawableTintMode) {
                    this.mDrawable.setTintMode(this.mDrawableTintMode);
                }
                if (this.mDrawable.isStateful()) {
                    this.mDrawable.setState(getDrawableState());
                }
            }
        }
    }

    private void configureBounds() {
        float f;
        float f2;
        if (this.mDrawable == null || !this.mHaveFrame) {
            return;
        }
        int i = this.mDrawableWidth;
        int i2 = this.mDrawableHeight;
        int width = (getWidth() - this.mPaddingLeft) - this.mPaddingRight;
        int height = (getHeight() - this.mPaddingTop) - this.mPaddingBottom;
        boolean z = (i < 0 || width == i) && (i2 < 0 || height == i2);
        if (i <= 0 || i2 <= 0 || ScaleType.FIT_XY == this.mScaleType) {
            this.mDrawable.setBounds(0, 0, width, height);
            this.mDrawMatrix = null;
            return;
        }
        this.mDrawable.setBounds(0, 0, i, i2);
        if (ScaleType.MATRIX == this.mScaleType) {
            if (this.mMatrix.isIdentity()) {
                this.mDrawMatrix = null;
                return;
            } else {
                this.mDrawMatrix = this.mMatrix;
                return;
            }
        }
        if (z) {
            this.mDrawMatrix = null;
            return;
        }
        if (ScaleType.CENTER == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            this.mDrawMatrix.setTranslate(Math.round((width - i) * 0.5f), Math.round((height - i2) * 0.5f));
            return;
        }
        float f3 = 0.0f;
        if (ScaleType.CENTER_CROP == this.mScaleType) {
            this.mDrawMatrix = this.mMatrix;
            if (i * height > width * i2) {
                float f4 = height / i2;
                f2 = 0.0f;
                f3 = (width - (i * f4)) * 0.5f;
                f = f4;
            } else {
                f = width / i;
                f2 = (height - (i2 * f)) * 0.5f;
            }
            this.mDrawMatrix.setScale(f, f);
            this.mDrawMatrix.postTranslate(Math.round(f3), Math.round(f2));
            return;
        }
        if (ScaleType.CENTER_INSIDE != this.mScaleType) {
            this.mTempSrc.set(0.0f, 0.0f, i, i2);
            this.mTempDst.set(0.0f, 0.0f, width, height);
            this.mDrawMatrix = this.mMatrix;
            this.mDrawMatrix.setRectToRect(this.mTempSrc, this.mTempDst, scaleTypeToScaleToFit(this.mScaleType));
            return;
        }
        this.mDrawMatrix = this.mMatrix;
        float fMin = (i > width || i2 > height) ? Math.min(width / i, height / i2) : 1.0f;
        float fRound = Math.round((width - (i * fMin)) * 0.5f);
        float fRound2 = Math.round((height - (i2 * fMin)) * 0.5f);
        this.mDrawMatrix.setScale(fMin, fMin);
        this.mDrawMatrix.postTranslate(fRound, fRound2);
    }

    private Drawable getDrawableFromUri(Uri uri) {
        String scheme = uri.getScheme();
        if ("android.resource".equals(scheme)) {
            try {
                ContentResolver.OpenResourceIdResult resourceId = this.mContext.getContentResolver().getResourceId(uri);
                return resourceId.r.getDrawable(resourceId.id, this.mContext.getTheme());
            } catch (Exception e) {
                Log.w(LOG_TAG, "Unable to open content: " + uri, e);
            }
        } else {
            if (!"content".equals(scheme) && !"file".equals(scheme)) {
                return Drawable.createFromPath(uri.toString());
            }
            try {
                return ImageDecoder.decodeDrawable(ImageDecoder.createSource(this.mContext.getContentResolver(), uri, sCompatUseCorrectStreamDensity ? getResources() : null), new ImageDecoder.OnHeaderDecodedListener() { // from class: android.widget.-$$Lambda$ImageView$GWf2-Z-LHjSbTbrF-I3WzfR0LeM
                    @Override // android.graphics.ImageDecoder.OnHeaderDecodedListener
                    public final void onHeaderDecoded(ImageDecoder imageDecoder, ImageDecoder.ImageInfo imageInfo, ImageDecoder.Source source) {
                        imageDecoder.setAllocator(1);
                    }
                });
            } catch (IOException e2) {
                Log.w(LOG_TAG, "Unable to open content: " + uri, e2);
            }
        }
        return null;
    }

    private void initImageView() {
        this.mMatrix = new Matrix();
        this.mScaleType = ScaleType.FIT_CENTER;
        if (sCompatDone) {
            return;
        }
        int i = this.mContext.getApplicationInfo().targetSdkVersion;
        sCompatAdjustViewBounds = i <= 17;
        sCompatUseCorrectStreamDensity = i > 23;
        sCompatDrawableVisibilityDispatch = i < 24;
        sCompatDone = true;
    }

    private boolean isFilledByImage() {
        if (this.mDrawable == null) {
            return false;
        }
        Rect bounds = this.mDrawable.getBounds();
        Matrix matrix = this.mDrawMatrix;
        if (matrix == null) {
            return bounds.left <= 0 && bounds.top <= 0 && bounds.right >= getWidth() && bounds.bottom >= getHeight();
        }
        if (!matrix.rectStaysRect()) {
            return false;
        }
        RectF rectF = this.mTempSrc;
        RectF rectF2 = this.mTempDst;
        rectF.set(bounds);
        matrix.mapRect(rectF2, rectF);
        return rectF2.left <= 0.0f && rectF2.top <= 0.0f && rectF2.right >= ((float) getWidth()) && rectF2.bottom >= ((float) getHeight());
    }

    private void resizeFromDrawable() {
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            if (intrinsicWidth < 0) {
                intrinsicWidth = this.mDrawableWidth;
            }
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicHeight < 0) {
                intrinsicHeight = this.mDrawableHeight;
            }
            if (intrinsicWidth == this.mDrawableWidth && intrinsicHeight == this.mDrawableHeight) {
                return;
            }
            this.mDrawableWidth = intrinsicWidth;
            this.mDrawableHeight = intrinsicHeight;
            requestLayout();
        }
    }

    private int resolveAdjustedSize(int i, int i2, int i3) {
        int mode = View.MeasureSpec.getMode(i3);
        int size = View.MeasureSpec.getSize(i3);
        if (mode == Integer.MIN_VALUE) {
            return Math.min(Math.min(i, size), i2);
        }
        if (mode != 0) {
            return mode != 1073741824 ? i : size;
        }
        return Math.min(i, i2);
    }

    private void resolveUri() {
        Drawable drawable;
        if (this.mDrawable == null && getResources() != null) {
            if (this.mResource != 0) {
                try {
                    drawable = this.mContext.getDrawable(this.mResource);
                } catch (Exception e) {
                    Log.w(LOG_TAG, "Unable to find resource: " + this.mResource, e);
                    this.mResource = 0;
                    drawable = null;
                }
            } else {
                if (this.mUri == null) {
                    return;
                }
                drawable = getDrawableFromUri(this.mUri);
                if (drawable == null) {
                    Log.w(LOG_TAG, "resolveUri failed on bad bitmap uri: " + this.mUri);
                    this.mUri = null;
                }
            }
            updateDrawable(drawable);
        }
    }

    private static Matrix.ScaleToFit scaleTypeToScaleToFit(ScaleType scaleType) {
        return sS2FArray[scaleType.nativeInt - 1];
    }

    /* JADX WARN: Code restructure failed: missing block: B:32:0x0063, code lost:
    
        r3 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void updateDrawable(Drawable drawable) {
        boolean z;
        if (drawable != this.mRecycleableBitmapDrawable && this.mRecycleableBitmapDrawable != null) {
            this.mRecycleableBitmapDrawable.setBitmap(null);
        }
        boolean z2 = false;
        if (this.mDrawable != null) {
            z = this.mDrawable == drawable;
            this.mDrawable.setCallback(null);
            unscheduleDrawable(this.mDrawable);
            if (!sCompatDrawableVisibilityDispatch && !z && isAttachedToWindow()) {
                this.mDrawable.setVisible(false, false);
            }
        } else {
            z = false;
        }
        this.mDrawable = drawable;
        if (drawable == null) {
            this.mDrawableHeight = -1;
            this.mDrawableWidth = -1;
            return;
        }
        drawable.setCallback(this);
        drawable.setLayoutDirection(getLayoutDirection());
        if (drawable.isStateful()) {
            drawable.setState(getDrawableState());
        }
        if (!z || sCompatDrawableVisibilityDispatch) {
            if (sCompatDrawableVisibilityDispatch) {
            }
            drawable.setVisible(z2, true);
        }
        drawable.setLevel(this.mLevel);
        this.mDrawableWidth = drawable.getIntrinsicWidth();
        this.mDrawableHeight = drawable.getIntrinsicHeight();
        applyImageTint();
        applyColorMod();
        configureBounds();
    }

    public void animateTransform(Matrix matrix) {
        if (this.mDrawable == null) {
            return;
        }
        if (matrix == null) {
            this.mDrawable.setBounds(0, 0, getWidth(), getHeight());
        } else {
            this.mDrawable.setBounds(0, 0, this.mDrawableWidth, this.mDrawableHeight);
            if (this.mDrawMatrix == null) {
                this.mDrawMatrix = new Matrix();
            }
            this.mDrawMatrix.set(matrix);
        }
        invalidate();
    }

    public final void clearColorFilter() {
        setColorFilter((ColorFilter) null);
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        if (this.mDrawable != null) {
            this.mDrawable.setHotspot(f, f2);
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mDrawable;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    protected void encodeProperties(ViewHierarchyEncoder viewHierarchyEncoder) {
        super.encodeProperties(viewHierarchyEncoder);
        viewHierarchyEncoder.addProperty("layout:baseline", getBaseline());
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return ImageView.class.getName();
    }

    public boolean getAdjustViewBounds() {
        return this.mAdjustViewBounds;
    }

    @Override // android.view.View
    @ViewDebug.ExportedProperty(category = TtmlUtils.TAG_LAYOUT)
    public int getBaseline() {
        return this.mBaselineAlignBottom ? getMeasuredHeight() : this.mBaseline;
    }

    public boolean getBaselineAlignBottom() {
        return this.mBaselineAlignBottom;
    }

    public ColorFilter getColorFilter() {
        return this.mColorFilter;
    }

    public boolean getCropToPadding() {
        return this.mCropToPadding;
    }

    public Drawable getDrawable() {
        if (this.mDrawable == this.mRecycleableBitmapDrawable) {
            this.mRecycleableBitmapDrawable = null;
        }
        return this.mDrawable;
    }

    public int getImageAlpha() {
        return this.mAlpha;
    }

    public Matrix getImageMatrix() {
        return this.mDrawMatrix == null ? new Matrix(Matrix.IDENTITY_MATRIX) : this.mDrawMatrix;
    }

    public ColorStateList getImageTintList() {
        return this.mDrawableTintList;
    }

    public PorterDuff.Mode getImageTintMode() {
        return this.mDrawableTintMode;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public ScaleType getScaleType() {
        return this.mScaleType;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return (getBackground() == null || getBackground().getCurrent() == null) ? false : true;
    }

    @Override // android.view.View, android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        if (drawable != this.mDrawable) {
            super.invalidateDrawable(drawable);
            return;
        }
        if (drawable != null) {
            int intrinsicWidth = drawable.getIntrinsicWidth();
            int intrinsicHeight = drawable.getIntrinsicHeight();
            if (intrinsicWidth != this.mDrawableWidth || intrinsicHeight != this.mDrawableHeight) {
                this.mDrawableWidth = intrinsicWidth;
                this.mDrawableHeight = intrinsicHeight;
                configureBounds();
            }
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean isDefaultFocusHighlightNeeded(Drawable drawable, Drawable drawable2) {
        return super.isDefaultFocusHighlightNeeded(drawable, drawable2) && (this.mDrawable == null || !this.mDrawable.isStateful() || !this.mDrawable.hasFocusStateSpecified());
    }

    @Override // android.view.View
    public boolean isOpaque() {
        return super.isOpaque() || (this.mDrawable != null && this.mXfermode == null && this.mDrawable.getOpacity() == -1 && ((this.mAlpha * 256) >> 8) == 255 && isFilledByImage());
    }

    @Override // android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        if (this.mDrawable != null) {
            this.mDrawable.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mDrawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        this.mDrawable.setVisible(getVisibility() == 0, false);
    }

    @Override // android.view.View
    public int[] onCreateDrawableState(int i) {
        if (this.mState == null) {
            return super.onCreateDrawableState(i);
        }
        return !this.mMergeState ? this.mState : mergeDrawableStates(super.onCreateDrawableState(i + this.mState.length), this.mState);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (this.mDrawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        this.mDrawable.setVisible(false, false);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.mDrawable == null || this.mDrawableWidth == 0 || this.mDrawableHeight == 0) {
            return;
        }
        if (this.mDrawMatrix == null && this.mPaddingTop == 0 && this.mPaddingLeft == 0) {
            this.mDrawable.draw(canvas);
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        if (this.mCropToPadding) {
            int i = this.mScrollX;
            int i2 = this.mScrollY;
            canvas.clipRect(this.mPaddingLeft + i, this.mPaddingTop + i2, ((i + this.mRight) - this.mLeft) - this.mPaddingRight, ((i2 + this.mBottom) - this.mTop) - this.mPaddingBottom);
        }
        canvas.translate(this.mPaddingLeft, this.mPaddingTop);
        if (this.mDrawMatrix != null) {
            canvas.concat(this.mDrawMatrix);
        }
        this.mDrawable.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    /* JADX WARN: Code duplicated, block: B:38:0x00b2 A[PHI: r5
      0x00b2: PHI (r5v9 int) = (r5v7 int), (r5v11 int) binds: [B:30:0x009a, B:36:0x00ac] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:48:0x00d1 A[PHI: r5 r9
      0x00d1: PHI (r5v8 int) = (r5v7 int), (r5v7 int), (r5v10 int), (r5v10 int), (r5v10 int) binds: [B:27:0x0080, B:29:0x0098, B:39:0x00b4, B:40:0x00b6, B:46:0x00cc] A[DONT_GENERATE, DONT_INLINE]
      0x00d1: PHI (r9v7 int) = (r9v6 int), (r9v6 int), (r9v6 int), (r9v6 int), (r9v8 int) binds: [B:27:0x0080, B:29:0x0098, B:39:0x00b4, B:40:0x00b6, B:46:0x00cc] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        int i3;
        int i4;
        float f;
        boolean z;
        boolean z2;
        int iResolveSizeAndState;
        int iResolveSizeAndState2;
        boolean z3;
        resolveUri();
        int mode = View.MeasureSpec.getMode(i);
        int mode2 = View.MeasureSpec.getMode(i2);
        if (this.mDrawable == null) {
            this.mDrawableWidth = -1;
            this.mDrawableHeight = -1;
            f = 0.0f;
            z = false;
            z2 = false;
            i3 = 0;
            i4 = 0;
        } else {
            i3 = this.mDrawableWidth;
            i4 = this.mDrawableHeight;
            if (i3 <= 0) {
                i3 = 1;
            }
            if (i4 <= 0) {
                i4 = 1;
            }
            if (this.mAdjustViewBounds) {
                z = mode != 1073741824;
                z2 = mode2 != 1073741824;
                f = i3 / i4;
            } else {
                f = 0.0f;
                z = false;
                z2 = false;
            }
        }
        int i5 = this.mPaddingLeft;
        int i6 = this.mPaddingRight;
        int i7 = this.mPaddingTop;
        int i8 = this.mPaddingBottom;
        if (z || z2) {
            int iResolveAdjustedSize = resolveAdjustedSize(i3 + i5 + i6, this.mMaxWidth, i);
            int iResolveAdjustedSize2 = resolveAdjustedSize(i4 + i7 + i8, this.mMaxHeight, i2);
            if (f != 0.0f) {
                float f2 = (iResolveAdjustedSize2 - i7) - i8;
                if (Math.abs((((iResolveAdjustedSize - i5) - i6) / f2) - f) <= 1.0E-7d) {
                    iResolveSizeAndState = iResolveAdjustedSize;
                    iResolveSizeAndState2 = iResolveAdjustedSize2;
                } else {
                    if (z) {
                        int i9 = ((int) (f2 * f)) + i5 + i6;
                        if (!z2 && !sCompatAdjustViewBounds) {
                            iResolveAdjustedSize = resolveAdjustedSize(i9, this.mMaxWidth, i);
                        }
                        if (i9 <= iResolveAdjustedSize) {
                            iResolveAdjustedSize = i9;
                            z3 = true;
                        } else {
                            z3 = false;
                        }
                    } else {
                        z3 = false;
                    }
                    if (z3 || !z2) {
                        iResolveSizeAndState = iResolveAdjustedSize;
                        iResolveSizeAndState2 = iResolveAdjustedSize2;
                    } else {
                        int i10 = ((int) (((iResolveAdjustedSize - i5) - i6) / f)) + i7 + i8;
                        if (!z && !sCompatAdjustViewBounds) {
                            iResolveAdjustedSize2 = resolveAdjustedSize(i10, this.mMaxHeight, i2);
                        }
                        if (i10 <= iResolveAdjustedSize2) {
                            iResolveSizeAndState2 = i10;
                            iResolveSizeAndState = iResolveAdjustedSize;
                        } else {
                            iResolveSizeAndState = iResolveAdjustedSize;
                            iResolveSizeAndState2 = iResolveAdjustedSize2;
                        }
                    }
                }
            } else {
                iResolveSizeAndState = iResolveAdjustedSize;
                iResolveSizeAndState2 = iResolveAdjustedSize2;
            }
        } else {
            int iMax = Math.max(i3 + i5 + i6, getSuggestedMinimumWidth());
            int iMax2 = Math.max(i4 + i7 + i8, getSuggestedMinimumHeight());
            iResolveSizeAndState = resolveSizeAndState(iMax, i, 0);
            iResolveSizeAndState2 = resolveSizeAndState(iMax2, i2, 0);
        }
        setMeasuredDimension(iResolveSizeAndState, iResolveSizeAndState2);
    }

    @Override // android.view.View
    public void onPopulateAccessibilityEventInternal(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEventInternal(accessibilityEvent);
        CharSequence contentDescription = getContentDescription();
        if (TextUtils.isEmpty(contentDescription)) {
            return;
        }
        accessibilityEvent.getText().add(contentDescription);
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int i) {
        super.onRtlPropertiesChanged(i);
        if (this.mDrawable != null) {
            this.mDrawable.setLayoutDirection(i);
        }
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        if (this.mDrawable == null || sCompatDrawableVisibilityDispatch) {
            return;
        }
        this.mDrawable.setVisible(z, false);
    }

    @RemotableViewMethod
    public void setAdjustViewBounds(boolean z) {
        this.mAdjustViewBounds = z;
        if (z) {
            setScaleType(ScaleType.FIT_CENTER);
        }
    }

    @RemotableViewMethod
    @Deprecated
    public void setAlpha(int i) {
        int i2 = i & 255;
        if (this.mAlpha != i2) {
            this.mAlpha = i2;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public void setBaseline(int i) {
        if (this.mBaseline != i) {
            this.mBaseline = i;
            requestLayout();
        }
    }

    public void setBaselineAlignBottom(boolean z) {
        if (this.mBaselineAlignBottom != z) {
            this.mBaselineAlignBottom = z;
            requestLayout();
        }
    }

    @RemotableViewMethod
    public final void setColorFilter(int i) {
        setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    }

    public final void setColorFilter(int i, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(i, mode));
    }

    public void setColorFilter(ColorFilter colorFilter) {
        if (this.mColorFilter != colorFilter) {
            this.mColorFilter = colorFilter;
            this.mHasColorFilter = true;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    public void setCropToPadding(boolean z) {
        if (this.mCropToPadding != z) {
            this.mCropToPadding = z;
            requestLayout();
            invalidate();
        }
    }

    @Override // android.view.View
    protected boolean setFrame(int i, int i2, int i3, int i4) {
        boolean frame = super.setFrame(i, i2, i3, i4);
        this.mHaveFrame = true;
        configureBounds();
        return frame;
    }

    @RemotableViewMethod
    public void setImageAlpha(int i) {
        setAlpha(i);
    }

    @RemotableViewMethod
    public void setImageBitmap(Bitmap bitmap) {
        this.mDrawable = null;
        if (this.mRecycleableBitmapDrawable == null) {
            this.mRecycleableBitmapDrawable = new BitmapDrawable(this.mContext.getResources(), bitmap);
        } else {
            this.mRecycleableBitmapDrawable.setBitmap(bitmap);
        }
        setImageDrawable(this.mRecycleableBitmapDrawable);
    }

    public void setImageDrawable(Drawable drawable) {
        if (this.mDrawable != drawable) {
            this.mResource = 0;
            this.mUri = null;
            int i = this.mDrawableWidth;
            int i2 = this.mDrawableHeight;
            updateDrawable(drawable);
            if (i != this.mDrawableWidth || i2 != this.mDrawableHeight) {
                requestLayout();
            }
            invalidate();
        }
    }

    @RemotableViewMethod(asyncImpl = "setImageIconAsync")
    public void setImageIcon(Icon icon) {
        setImageDrawable(icon == null ? null : icon.loadDrawable(this.mContext));
    }

    public Runnable setImageIconAsync(Icon icon) {
        return new ImageDrawableCallback(icon == null ? null : icon.loadDrawable(this.mContext), null, 0);
    }

    @RemotableViewMethod
    public void setImageLevel(int i) {
        this.mLevel = i;
        if (this.mDrawable != null) {
            this.mDrawable.setLevel(i);
            resizeFromDrawable();
        }
    }

    public void setImageMatrix(Matrix matrix) {
        if (matrix != null && matrix.isIdentity()) {
            matrix = null;
        }
        if ((matrix != null || this.mMatrix.isIdentity()) && (matrix == null || this.mMatrix.equals(matrix))) {
            return;
        }
        this.mMatrix.set(matrix);
        configureBounds();
        invalidate();
    }

    @RemotableViewMethod(asyncImpl = "setImageResourceAsync")
    public void setImageResource(int i) {
        int i2 = this.mDrawableWidth;
        int i3 = this.mDrawableHeight;
        updateDrawable(null);
        this.mResource = i;
        this.mUri = null;
        resolveUri();
        if (i2 != this.mDrawableWidth || i3 != this.mDrawableHeight) {
            requestLayout();
        }
        invalidate();
    }

    public Runnable setImageResourceAsync(int i) {
        Drawable drawable;
        if (i != 0) {
            try {
                drawable = getContext().getDrawable(i);
            } catch (Exception e) {
                Log.w(LOG_TAG, "Unable to find resource: " + i, e);
                i = 0;
                drawable = null;
            }
        } else {
            drawable = null;
        }
        return new ImageDrawableCallback(drawable, null, i);
    }

    public void setImageState(int[] iArr, boolean z) {
        this.mState = iArr;
        this.mMergeState = z;
        if (this.mDrawable != null) {
            refreshDrawableState();
            resizeFromDrawable();
        }
    }

    public void setImageTintList(ColorStateList colorStateList) {
        this.mDrawableTintList = colorStateList;
        this.mHasDrawableTint = true;
        applyImageTint();
    }

    public void setImageTintMode(PorterDuff.Mode mode) {
        this.mDrawableTintMode = mode;
        this.mHasDrawableTintMode = true;
        applyImageTint();
    }

    @RemotableViewMethod(asyncImpl = "setImageURIAsync")
    public void setImageURI(Uri uri) {
        if (this.mResource == 0) {
            if (this.mUri == uri) {
                return;
            }
            if (uri != null && this.mUri != null && uri.equals(this.mUri)) {
                return;
            }
        }
        updateDrawable(null);
        this.mResource = 0;
        this.mUri = uri;
        int i = this.mDrawableWidth;
        int i2 = this.mDrawableHeight;
        resolveUri();
        if (i != this.mDrawableWidth || i2 != this.mDrawableHeight) {
            requestLayout();
        }
        invalidate();
    }

    public Runnable setImageURIAsync(Uri uri) {
        if (this.mResource == 0 && (this.mUri == uri || (uri != null && this.mUri != null && uri.equals(this.mUri)))) {
            return null;
        }
        Drawable drawableFromUri = uri == null ? null : getDrawableFromUri(uri);
        if (drawableFromUri == null) {
            uri = null;
        }
        return new ImageDrawableCallback(drawableFromUri, uri, 0);
    }

    @RemotableViewMethod
    public void setMaxHeight(int i) {
        this.mMaxHeight = i;
    }

    @RemotableViewMethod
    public void setMaxWidth(int i) {
        this.mMaxWidth = i;
    }

    public void setScaleType(ScaleType scaleType) {
        if (scaleType == null) {
            throw new NullPointerException();
        }
        if (this.mScaleType != scaleType) {
            this.mScaleType = scaleType;
            requestLayout();
            invalidate();
        }
    }

    @Override // android.view.View
    public void setSelected(boolean z) {
        super.setSelected(z);
        resizeFromDrawable();
    }

    @Override // android.view.View
    @RemotableViewMethod
    public void setVisibility(int i) {
        super.setVisibility(i);
        if (this.mDrawable == null || !sCompatDrawableVisibilityDispatch) {
            return;
        }
        this.mDrawable.setVisible(i == 0, false);
    }

    public final void setXfermode(Xfermode xfermode) {
        if (this.mXfermode != xfermode) {
            this.mXfermode = xfermode;
            this.mColorMod = true;
            applyColorMod();
            invalidate();
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return this.mDrawable == drawable || super.verifyDrawable(drawable);
    }
}
