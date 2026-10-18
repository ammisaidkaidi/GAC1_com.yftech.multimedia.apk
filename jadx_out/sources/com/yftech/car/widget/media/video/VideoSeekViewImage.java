package com.yftech.car.widget.media.video;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.utils.Loger;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.text.MediaPlayTime;
import com.yftech.car.widget.media.view.MediaImageView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public class VideoSeekViewImage extends FrameLayout {
    private static final String TAG = "VideoSeekViewImage";
    private static boolean mVideoSeekDebug = false;
    private boolean isCreatedPlayer;
    private boolean isPrepared;
    private boolean isPreparedLoading;
    private final MediaFile mCurrentPlay;
    private Drawable mDefaultDrawable;
    private int mInitHeight;
    private int mInitWidth;
    private JzViewOutlineProvider mJzViewOutlineProvider;
    private MediaImageView mMediaImageView;
    private MediaPlayer mMediaPlayer;
    private final MediaPlayer.OnCompletionListener mOnCompletionListener;
    private final MediaPlayer.OnErrorListener mOnErrorListener;
    private final MediaPlayer.OnPreparedListener mOnPreparedListener;
    private final List<MediaPlayTime> mPlayTimeList;
    private int mProgress;
    private boolean mSlideForward;
    private ImageView mSlideForwardImage;
    private Surface mSurface;
    private final List<TextView> mTextViewList;
    private TextureView mTextureView;
    private int mVideoImageBorderRadius;
    private BaseVideo mVideoPlayer;
    private float seekBarShowTimeDistance;
    private float seekBarShowTimeOffset;

    private static class JzViewOutlineProvider extends ViewOutlineProvider {
        private final float mRadius;

        public JzViewOutlineProvider(float f) {
            this.mRadius = f;
            if (VideoSeekViewImage.mVideoSeekDebug) {
                Log.d(VideoSeekViewImage.TAG, "JzViewOutlineProvider: mRadius==" + this.mRadius);
            }
        }

        @Override // android.view.ViewOutlineProvider
        public void getOutline(View view, Outline outline) {
            view.getGlobalVisibleRect(new Rect());
            int width = view.getWidth();
            int height = view.getHeight();
            Rect rect = new Rect(0, 0, width, height);
            outline.setRoundRect(rect, this.mRadius);
            if (VideoSeekViewImage.mVideoSeekDebug) {
                Log.d(VideoSeekViewImage.TAG, "getOutline: selfRect==" + rect + ",width==" + width + ",height==" + height);
            }
        }
    }

    private VideoSeekViewImage(Context context) {
        super(context);
        this.mCurrentPlay = new MediaFile(1);
        this.isPrepared = false;
        this.isPreparedLoading = false;
        this.isCreatedPlayer = true;
        this.mPlayTimeList = new ArrayList();
        this.mTextViewList = new ArrayList();
        this.mOnPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onPrepared: ");
                }
                VideoSeekViewImage.this.isPrepared = true;
                VideoSeekViewImage.this.isPreparedLoading = false;
                VideoSeekViewImage.this.mProgress = 0;
                VideoSeekViewImage.this.setPlayProgress(VideoSeekViewImage.this.mProgress);
            }
        };
        this.mOnErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.3
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                if (!VideoSeekViewImage.mVideoSeekDebug) {
                    return false;
                }
                Log.d(VideoSeekViewImage.TAG, "onError: what==" + i + ",extra==" + i2);
                return false;
            }
        };
        this.mOnCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.4
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onCompletion: ");
                }
            }
        };
    }

    public VideoSeekViewImage(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mCurrentPlay = new MediaFile(1);
        this.isPrepared = false;
        this.isPreparedLoading = false;
        this.isCreatedPlayer = true;
        this.mPlayTimeList = new ArrayList();
        this.mTextViewList = new ArrayList();
        this.mOnPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onPrepared: ");
                }
                VideoSeekViewImage.this.isPrepared = true;
                VideoSeekViewImage.this.isPreparedLoading = false;
                VideoSeekViewImage.this.mProgress = 0;
                VideoSeekViewImage.this.setPlayProgress(VideoSeekViewImage.this.mProgress);
            }
        };
        this.mOnErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.3
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i, int i2) {
                if (!VideoSeekViewImage.mVideoSeekDebug) {
                    return false;
                }
                Log.d(VideoSeekViewImage.TAG, "onError: what==" + i + ",extra==" + i2);
                return false;
            }
        };
        this.mOnCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.4
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onCompletion: ");
                }
            }
        };
        init(context, attributeSet);
    }

    public VideoSeekViewImage(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mCurrentPlay = new MediaFile(1);
        this.isPrepared = false;
        this.isPreparedLoading = false;
        this.isCreatedPlayer = true;
        this.mPlayTimeList = new ArrayList();
        this.mTextViewList = new ArrayList();
        this.mOnPreparedListener = new MediaPlayer.OnPreparedListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.2
            @Override // android.media.MediaPlayer.OnPreparedListener
            public void onPrepared(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onPrepared: ");
                }
                VideoSeekViewImage.this.isPrepared = true;
                VideoSeekViewImage.this.isPreparedLoading = false;
                VideoSeekViewImage.this.mProgress = 0;
                VideoSeekViewImage.this.setPlayProgress(VideoSeekViewImage.this.mProgress);
            }
        };
        this.mOnErrorListener = new MediaPlayer.OnErrorListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.3
            @Override // android.media.MediaPlayer.OnErrorListener
            public boolean onError(MediaPlayer mediaPlayer, int i2, int i3) {
                if (!VideoSeekViewImage.mVideoSeekDebug) {
                    return false;
                }
                Log.d(VideoSeekViewImage.TAG, "onError: what==" + i2 + ",extra==" + i3);
                return false;
            }
        };
        this.mOnCompletionListener = new MediaPlayer.OnCompletionListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.4
            @Override // android.media.MediaPlayer.OnCompletionListener
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onCompletion: ");
                }
            }
        };
        init(context, attributeSet);
    }

    private void createdPlayer() {
        this.mMediaPlayer = new MediaPlayer();
        this.mMediaPlayer.setOnPreparedListener(this.mOnPreparedListener);
        this.mMediaPlayer.setOnErrorListener(this.mOnErrorListener);
        this.mMediaPlayer.setOnCompletionListener(this.mOnCompletionListener);
        if (this.mSurface != null && this.mSurface.isValid()) {
            if (mVideoSeekDebug) {
                Loger.d(TAG, "createdPlayer: setDisplay");
            }
            this.mMediaPlayer.setSurface(this.mSurface);
        }
        if (mVideoSeekDebug) {
            Loger.d(TAG, "createdPlayer: --ok");
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }
        Bitmap bitmapCreateBitmap = (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) ? Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) : Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmapCreateBitmap;
    }

    private void init(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SeekViewImage);
        this.seekBarShowTimeDistance = typedArrayObtainStyledAttributes.getDimension(R.styleable.SeekViewImage_seek_bar_show_image_distance, 0.0f);
        this.seekBarShowTimeOffset = typedArrayObtainStyledAttributes.getDimension(R.styleable.SeekViewImage_seek_bar_show_image_offset, 0.0f);
        boolean z = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekViewImage_video_image_cache_preview, true);
        mVideoSeekDebug = typedArrayObtainStyledAttributes.getBoolean(R.styleable.SeekViewImage_video_image_seek_debug, true);
        this.mVideoImageBorderRadius = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.SeekViewImage_video_image_borderRadius, 0);
        this.mDefaultDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.SeekViewImage_video_image_default_drawable);
        if (!z) {
            this.mMediaImageView = new MediaImageView(context, attributeSet);
            addView(this.mMediaImageView, 0);
            ((FrameLayout.LayoutParams) this.mMediaImageView.getLayoutParams()).gravity = 17;
            if (this.mDefaultDrawable != null) {
                this.mMediaImageView.setImageBitmap(drawableToBitmap(this.mDefaultDrawable));
            }
            if (this.mVideoImageBorderRadius > 0) {
                this.mMediaImageView.setBorderRadius(this.mVideoImageBorderRadius);
                return;
            }
            return;
        }
        this.mTextureView = new TextureView(context);
        addView(this.mTextureView, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.mTextureView.getLayoutParams();
        layoutParams.gravity = 17;
        layoutParams.width = -1;
        layoutParams.height = -1;
        this.mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() { // from class: com.yftech.car.widget.media.video.VideoSeekViewImage.1
            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int i, int i2) {
                VideoSeekViewImage.this.mSurface = new Surface(surfaceTexture);
                if (VideoSeekViewImage.this.mMediaPlayer == null) {
                    Log.e(VideoSeekViewImage.TAG, "onSurfaceTextureAvailable: mMediaPlayer is null");
                    return;
                }
                VideoSeekViewImage.this.mMediaPlayer.setSurface(VideoSeekViewImage.this.mSurface);
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.w(VideoSeekViewImage.TAG, "onSurfaceTextureAvailable: setSurface  ----------------------------ok");
                }
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.w(VideoSeekViewImage.TAG, "onSurfaceTextureDestroyed: ");
                }
                if (VideoSeekViewImage.this.mSurface == null) {
                    return true;
                }
                VideoSeekViewImage.this.mSurface.release();
                VideoSeekViewImage.this.mSurface = null;
                return true;
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int i, int i2) {
                if (VideoSeekViewImage.mVideoSeekDebug) {
                    Log.d(VideoSeekViewImage.TAG, "onSurfaceTextureSizeChanged: ");
                }
            }

            @Override // android.view.TextureView.SurfaceTextureListener
            public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {
            }
        });
        if (this.mVideoImageBorderRadius > 0) {
            if (this.mJzViewOutlineProvider == null) {
                this.mJzViewOutlineProvider = new JzViewOutlineProvider(this.mVideoImageBorderRadius);
            }
            this.mTextureView.setOutlineProvider(this.mJzViewOutlineProvider);
            this.mTextureView.setClipToOutline(true);
        }
    }

    static /* synthetic */ void lambda$setVideoPlayer$0(MediaPlayTime mediaPlayTime) {
        mediaPlayTime.setInitHeight(mediaPlayTime.getHeight());
        mediaPlayTime.setVisibility(8);
    }

    public int getInitHeight() {
        return this.mInitHeight;
    }

    public int getInitWidth() {
        return this.mInitWidth;
    }

    public ImageView getMediaImageView() {
        return this.mMediaImageView;
    }

    public float getSeekBarShowDistance() {
        return this.seekBarShowTimeDistance;
    }

    public float getSeekBarShowOffset() {
        return this.seekBarShowTimeOffset;
    }

    public void initHeight() {
        this.mInitHeight = getHeight();
        this.mInitWidth = getWidth();
    }

    public boolean isShow() {
        return this.mTextureView != null && this.mTextureView.getLayoutParams().height > 0;
    }

    public void loadStart() {
        if (this.mVideoPlayer != null) {
            MediaFile currentPlay = this.mVideoPlayer.getCurrentPlay();
            if (currentPlay == null) {
                Log.e(TAG, "loadStart: currentPlay is null !!!");
                return;
            }
            if (this.mVideoPlayer.isPlayError()) {
                Log.e(TAG, "loadStart: isPlayError");
                return;
            }
            if (currentPlay.getData().equals(this.mCurrentPlay.getData()) && this.isPrepared) {
                if (mVideoSeekDebug) {
                    Log.e(TAG, "loadStart: isPrepared is true");
                    return;
                }
                return;
            }
            if (this.mTextureView != null) {
                if (mVideoSeekDebug) {
                    Log.d(TAG, "loadStart: start loading data path ==" + currentPlay.getData());
                }
                this.mCurrentPlay.setData(currentPlay.getData());
                this.mCurrentPlay.setTitle(currentPlay.getTitle());
                this.mCurrentPlay.setFolder(currentPlay.getFolder());
                this.mCurrentPlay.setFolderPath(currentPlay.getFolderPath());
                this.mCurrentPlay.setId(currentPlay.getId());
                this.isPrepared = false;
                this.isPreparedLoading = true;
                if (this.mMediaPlayer == null) {
                    this.isCreatedPlayer = true;
                    createdPlayer();
                } else {
                    this.isCreatedPlayer = false;
                    try {
                        this.mMediaPlayer.stop();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    }
                    this.mMediaPlayer.reset();
                }
                try {
                    this.mMediaPlayer.setDataSource(this.mCurrentPlay.getData());
                    try {
                        this.mMediaPlayer.prepareAsync();
                    } catch (IllegalStateException e2) {
                        e2.printStackTrace();
                        this.isPreparedLoading = false;
                        releaseMediaPlayer();
                    }
                } catch (IOException | IllegalStateException e3) {
                    e3.printStackTrace();
                    this.isPreparedLoading = false;
                    releaseMediaPlayer();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    public void release() {
        this.mPlayTimeList.clear();
        if (this.mSurface != null) {
            this.mSurface.release();
            this.mSurface = null;
        }
    }

    public void releaseMediaPlayer() {
        Log.i(TAG, "releaseMediaPlayer: ");
        if (this.mMediaPlayer != null) {
            this.mMediaPlayer.stop();
            this.mMediaPlayer.release();
            this.mMediaPlayer = null;
        }
        this.isPrepared = false;
        if (this.mMediaImageView == null || this.mDefaultDrawable == null) {
            return;
        }
        this.mMediaImageView.setImageBitmap(drawableToBitmap(this.mDefaultDrawable));
    }

    public void setPlayProgress(int i) {
        Log.i(TAG, "setPlayProgress: " + i);
        if (this.mProgress != i) {
            this.mSlideForward = i > this.mProgress;
            this.mProgress = i;
        }
        if (!this.mVideoPlayer.isPlayError() && this.mMediaPlayer != null && this.isPrepared) {
            this.mMediaPlayer.seekTo(i);
        }
        for (MediaPlayTime mediaPlayTime : this.mPlayTimeList) {
            mediaPlayTime.updateProgress(i);
            Drawable[] forwardDrawable = mediaPlayTime.getForwardDrawable();
            if (forwardDrawable != null) {
                if (this.mSlideForward) {
                    mediaPlayTime.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, forwardDrawable[1], (Drawable) null);
                } else {
                    mediaPlayTime.setCompoundDrawablesRelativeWithIntrinsicBounds(forwardDrawable[0], (Drawable) null, (Drawable) null, (Drawable) null);
                }
            }
        }
        if (this.mSlideForwardImage != null) {
            this.mSlideForwardImage.setSelected(this.mSlideForward);
        }
    }

    public void setVideoPlayer(BaseVideo baseVideo) {
        this.mVideoPlayer = baseVideo;
        LinkedList linkedList = new LinkedList();
        linkedList.add(this);
        while (true) {
            if (linkedList.isEmpty()) {
                break;
            }
            ViewGroup viewGroup = (ViewGroup) linkedList.removeFirst();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View childAt = viewGroup.getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    linkedList.add((ViewGroup) childAt);
                } else if (childAt instanceof MediaPlayTime) {
                    this.mPlayTimeList.add((MediaPlayTime) childAt);
                } else if (childAt instanceof ImageView) {
                    if (this.mSlideForwardImage == null) {
                        this.mSlideForwardImage = (ImageView) childAt;
                    }
                } else if (childAt instanceof TextView) {
                    this.mTextViewList.add((TextView) childAt);
                }
            }
        }
        if (this.mVideoPlayer == null || this.mPlayTimeList.size() <= 0) {
            return;
        }
        long duration = this.mVideoPlayer.getDuration();
        int progressPosition = this.mVideoPlayer.getProgressPosition();
        for (final MediaPlayTime mediaPlayTime : this.mPlayTimeList) {
            mediaPlayTime.setForwardDrawable();
            mediaPlayTime.updateDuration(duration);
            mediaPlayTime.updateProgress(progressPosition);
            if (mediaPlayTime.isSeekBarShowScrollTime()) {
                mediaPlayTime.setVisibility(0);
                mediaPlayTime.postDelayed(new Runnable() { // from class: com.yftech.car.widget.media.video.-$$Lambda$VideoSeekViewImage$3QMbOZqsQ494h3DjHv7ZCG56g2o
                    @Override // java.lang.Runnable
                    public final void run() {
                        VideoSeekViewImage.lambda$setVideoPlayer$0(mediaPlayTime);
                    }
                }, 10L);
            }
        }
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        if (i == 4) {
            super.setVisibility(i);
            if (this.mTextureView != null) {
                this.mTextureView.setVisibility(i);
            }
            Iterator<TextView> it = this.mTextViewList.iterator();
            while (it.hasNext()) {
                it.next().setVisibility(8);
            }
            return;
        }
        super.setVisibility(i);
        if (this.mTextureView != null) {
            this.mTextureView.setVisibility(0);
            ViewGroup.LayoutParams layoutParams = this.mTextureView.getLayoutParams();
            ViewGroup.LayoutParams layoutParams2 = getLayoutParams();
            if (i != 0) {
                setBackground(null);
                layoutParams.width = this.mInitWidth;
                layoutParams.height = 0;
                Iterator<TextView> it2 = this.mTextViewList.iterator();
                while (it2.hasNext()) {
                    it2.next().setVisibility(8);
                }
            } else if (this.mVideoPlayer.isSoftSolution() || this.mVideoPlayer.isPlayError() || this.mVideoPlayer.getMediaPlayInfoNotPlaying() == 805) {
                setBackground(this.mDefaultDrawable);
                layoutParams.width = this.mInitWidth;
                layoutParams.height = 0;
                Iterator<TextView> it3 = this.mTextViewList.iterator();
                while (it3.hasNext()) {
                    it3.next().setVisibility(0);
                }
            } else {
                setBackground(null);
                layoutParams.width = this.mInitWidth;
                layoutParams.height = this.mInitHeight;
                Iterator<TextView> it4 = this.mTextViewList.iterator();
                while (it4.hasNext()) {
                    it4.next().setVisibility(8);
                }
            }
            for (int i2 = 1; i2 < getChildCount(); i2++) {
                getChildAt(i2).setVisibility(i);
            }
            layoutParams2.width = this.mInitWidth;
            layoutParams2.height = this.mInitHeight;
            requestLayout();
        }
    }

    public void updateDuration(long j) {
        Iterator<MediaPlayTime> it = this.mPlayTimeList.iterator();
        while (it.hasNext()) {
            it.next().updateDuration(j);
        }
    }
}
