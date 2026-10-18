package com.yftech.car.widget.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.utils.AnimationUtil;

/* JADX INFO: loaded from: classes3.dex */
public class MediaImageLoading extends AppCompatTextView {
    private static final String TAG = "MediaImageLoading";
    private AnimationDrawable mAnimationDrawable;
    private Drawable mBackground;
    private boolean mIsHide;
    private boolean mIsLoading;
    private Drawable mLoadingFinishDrawable;
    private int mLoadingMediaDataListId;
    private int mOfFrames;
    private int mRotateAnimationTime;
    private final Runnable mRunnable;
    private String mText;

    public MediaImageLoading(@NonNull Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public MediaImageLoading(@NonNull Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mOfFrames = 0;
        this.mIsHide = false;
        this.mRunnable = new Runnable() { // from class: com.yftech.car.widget.media.view.MediaImageLoading.1
            @Override // java.lang.Runnable
            public void run() {
                if (MediaImageLoading.this.mAnimationDrawable == null || MediaImageLoading.this.getVisibility() != 0) {
                    return;
                }
                MediaImageLoading.this.postOnAnimationDelayed(MediaImageLoading.this.mRunnable, MediaImageLoading.this.mAnimationDrawable.getDuration(MediaImageLoading.this.mOfFrames));
                MediaImageLoading.this.setBackground(MediaImageLoading.this.mAnimationDrawable.getFrame(MediaImageLoading.this.mOfFrames));
                if (MediaImageLoading.this.mOfFrames < MediaImageLoading.this.mAnimationDrawable.getNumberOfFrames() - 1) {
                    MediaImageLoading.access$208(MediaImageLoading.this);
                } else {
                    MediaImageLoading.this.mOfFrames = 0;
                }
            }
        };
        if (attributeSet != null) {
            TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MediaImageLoading);
            this.mRotateAnimationTime = typedArrayObtainStyledAttributes.getInt(R.styleable.MediaImageLoading_rotate_animation_time, 0);
            this.mLoadingFinishDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MediaImageLoading_loading_finish_drawable);
            this.mLoadingMediaDataListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MediaImageLoading_loading_media_data_list_id, -1);
        }
    }

    static /* synthetic */ int access$208(MediaImageLoading mediaImageLoading) {
        int i = mediaImageLoading.mOfFrames;
        mediaImageLoading.mOfFrames = i + 1;
        return i;
    }

    public static /* synthetic */ void lambda$setHide$0(MediaImageLoading mediaImageLoading, MediaFile mediaFile) {
        if (mediaFile == null || mediaFile.getType() == 4) {
            mediaImageLoading.setBackground(null);
        }
    }

    public int getMediaDataListId() {
        return this.mLoadingMediaDataListId;
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mIsHide) {
            return;
        }
        if (!this.mIsLoading) {
            clearAnimation();
            removeCallbacks(this.mRunnable);
            setBackground(this.mLoadingFinishDrawable);
            return;
        }
        if (this.mAnimationDrawable != null) {
            removeCallbacks(this.mRunnable);
            post(this.mRunnable);
        } else {
            setBackground(this.mBackground);
        }
        if (this.mRotateAnimationTime <= 99 || !this.mIsLoading) {
            return;
        }
        clearAnimation();
        AnimationUtil.setRotationAnimation(this, this.mRotateAnimationTime);
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mRunnable);
        clearAnimation();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mBackground = getBackground();
        CharSequence text = getText();
        if (text != null) {
            this.mText = (String) text;
        } else {
            this.mText = "";
        }
        if (this.mBackground instanceof AnimationDrawable) {
            this.mAnimationDrawable = (AnimationDrawable) this.mBackground;
        }
    }

    public void setHide(final MediaFile mediaFile) {
        this.mIsHide = true;
        clearAnimation();
        setBackground(null);
        postOnAnimation(new Runnable() { // from class: com.yftech.car.widget.media.view.-$$Lambda$MediaImageLoading$OKESigXfNYb10Y4JfT85kkMYFeQ
            @Override // java.lang.Runnable
            public final void run() {
                MediaImageLoading.lambda$setHide$0(this.f$0, mediaFile);
            }
        });
    }

    public void setLoadingStatus(boolean z) {
        this.mIsHide = false;
        this.mIsLoading = z;
        clearAnimation();
        if (!z) {
            removeCallbacks(this.mRunnable);
            setBackground(this.mLoadingFinishDrawable);
            setText("");
            return;
        }
        if (this.mRotateAnimationTime > 99) {
            AnimationUtil.setRotationAnimation(this, this.mRotateAnimationTime);
        }
        if (this.mAnimationDrawable != null) {
            removeCallbacks(this.mRunnable);
            post(this.mRunnable);
        } else {
            setBackground(this.mBackground);
        }
        setText(this.mText);
    }
}
