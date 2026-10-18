package com.yftech.car.widget.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.MusicManager;
import com.yftech.car.widget.media.manager.RadioManager;
import com.yftech.car.widget.media.video.VideoDataPlayManager;

/* JADX INFO: loaded from: classes3.dex */
public class PlayAnimation extends AppCompatTextView {
    public static final int RADIO = 5;
    private static final String TAG = "PlayAnimation";
    private boolean isPlay;
    private boolean isSelectedPlay;
    private AnimationDrawable mAnimationDrawable;
    private int mMediaType;
    private Drawable mNoPlayDrawable;
    private boolean mNoPlayIsNumber;
    private int mOfFrames;
    private final Runnable mRunnable;

    public PlayAnimation(Context context) {
        super(context);
        this.isSelectedPlay = false;
        this.mOfFrames = 0;
        this.mMediaType = -1;
        this.mRunnable = new Runnable() { // from class: com.yftech.car.widget.media.view.PlayAnimation.1
            @Override // java.lang.Runnable
            public void run() {
                if (PlayAnimation.this.isPlay && PlayAnimation.this.mAnimationDrawable != null && PlayAnimation.this.getVisibility() == 0) {
                    PlayAnimation.this.postOnAnimationDelayed(PlayAnimation.this.mRunnable, PlayAnimation.this.mAnimationDrawable.getDuration(PlayAnimation.this.mOfFrames));
                    PlayAnimation.this.setCompoundDrawablesRelativeWithIntrinsicBounds(PlayAnimation.this.mAnimationDrawable.getFrame(PlayAnimation.this.mOfFrames), (Drawable) null, (Drawable) null, (Drawable) null);
                    if (PlayAnimation.this.mOfFrames < PlayAnimation.this.mAnimationDrawable.getNumberOfFrames() - 1) {
                        PlayAnimation.access$308(PlayAnimation.this);
                    } else {
                        PlayAnimation.this.mOfFrames = 0;
                    }
                }
            }
        };
    }

    public PlayAnimation(Context context, @Nullable AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public PlayAnimation(Context context, @Nullable AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.isSelectedPlay = false;
        this.mOfFrames = 0;
        this.mMediaType = -1;
        this.mRunnable = new Runnable() { // from class: com.yftech.car.widget.media.view.PlayAnimation.1
            @Override // java.lang.Runnable
            public void run() {
                if (PlayAnimation.this.isPlay && PlayAnimation.this.mAnimationDrawable != null && PlayAnimation.this.getVisibility() == 0) {
                    PlayAnimation.this.postOnAnimationDelayed(PlayAnimation.this.mRunnable, PlayAnimation.this.mAnimationDrawable.getDuration(PlayAnimation.this.mOfFrames));
                    PlayAnimation.this.setCompoundDrawablesRelativeWithIntrinsicBounds(PlayAnimation.this.mAnimationDrawable.getFrame(PlayAnimation.this.mOfFrames), (Drawable) null, (Drawable) null, (Drawable) null);
                    if (PlayAnimation.this.mOfFrames < PlayAnimation.this.mAnimationDrawable.getNumberOfFrames() - 1) {
                        PlayAnimation.access$308(PlayAnimation.this);
                    } else {
                        PlayAnimation.this.mOfFrames = 0;
                    }
                }
            }
        };
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.PlayAnimation);
        Drawable drawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.PlayAnimation_play_drawable);
        this.mNoPlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.PlayAnimation_no_play_drawable);
        this.mNoPlayIsNumber = typedArrayObtainStyledAttributes.getBoolean(R.styleable.PlayAnimation_no_play_is_number, false);
        if (!(drawable instanceof AnimationDrawable)) {
            setCompoundDrawablesRelativeWithIntrinsicBounds(drawable, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            this.mAnimationDrawable = (AnimationDrawable) drawable;
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mAnimationDrawable.getFrame(this.mOfFrames), (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    static /* synthetic */ int access$308(PlayAnimation playAnimation) {
        int i = playAnimation.mOfFrames;
        playAnimation.mOfFrames = i + 1;
        return i;
    }

    private void setNoPlayDrawable() {
        this.isPlay = false;
        removeCallbacks(this.mRunnable);
        setCompoundDrawablesRelativeWithIntrinsicBounds(this.mNoPlayDrawable, (Drawable) null, (Drawable) null, (Drawable) null);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.mMediaType == -1 || !this.isSelectedPlay) {
            return;
        }
        if (this.mMediaType == 0) {
            setPlay(MusicManager.getInstance().isPlaying());
        } else if (this.mMediaType == 1) {
            setPlay(VideoDataPlayManager.getInstance().isPlaying());
        } else if (this.mMediaType == 5) {
            setPlay(RadioManager.getInstance().isPlaying());
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(this.mRunnable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setAnimationDrawable(AnimationDrawable animationDrawable) {
        this.mAnimationDrawable = animationDrawable;
        if (this.mAnimationDrawable == null) {
            setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        } else {
            this.mOfFrames = 0;
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mAnimationDrawable.getFrame(this.mOfFrames), (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    public void setMediaType(int i) {
        this.mMediaType = i;
    }

    public void setNumberText(String str) {
        if (this.mNoPlayIsNumber) {
            setText(str);
        }
    }

    public void setPlay(boolean z) {
        this.isPlay = z;
        if (z) {
            removeCallbacks(this.mRunnable);
            post(this.mRunnable);
        } else {
            if (this.mAnimationDrawable == null || this.mAnimationDrawable.getNumberOfFrames() <= this.mOfFrames) {
                return;
            }
            setCompoundDrawablesRelativeWithIntrinsicBounds(this.mAnimationDrawable.getFrame(this.mOfFrames), (Drawable) null, (Drawable) null, (Drawable) null);
        }
    }

    public void setSelectedPlay(boolean z) {
        this.isSelectedPlay = z;
        if (this.isSelectedPlay) {
            setPlay(MusicManager.getInstance().isPlaying());
        } else {
            setNoPlayDrawable();
        }
    }
}
