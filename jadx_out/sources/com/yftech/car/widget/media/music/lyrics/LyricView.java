package com.yftech.car.widget.media.music.lyrics;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telecom.Logging.Session;
import android.text.DynamicLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.OverScroller;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.MusicManager;
import com.yftech.car.widget.media.utils.TaskManager;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes3.dex */
public class LyricView extends View {
    private static final int AUTO_SCROLLER = 4;
    private static final String TAG = "LyricView";
    private static final int TEXT_ALIGN_CENTER = 1;
    private static final int TEXT_ALIGN_LEFT = 0;
    private static final int TEXT_ALIGN_RIGHT = 2;
    private static final int UPDATE_TIME = 3;
    private boolean isFling;
    private boolean isPlay;
    private boolean isPressing;
    private boolean isRightStart;
    private boolean mAddKrc;
    private int mAnimatorSpeed;
    private final ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener;
    private MusicManager.CarMusicControlCallback mCarMusicControlCallback;
    private boolean mClickItemPlay;
    private int mCurrentLineIndex;
    private float mDownX;
    private float mDownY;
    private final TextPaint mEmptyTextPaint;
    private final Handler mHandler;
    private float mHighlightOffset;
    private final TextPaint mHighlightPaint;
    private final TextPaint mHighlightPlayPaint;
    private boolean mIsClick;
    private float mLastMotionY;
    private int mLineSpace;
    private int mLinearMax;
    private final LoadLyricRunnable mLoadLyricRunnable;
    private Lyric mLyric;
    private LyricForm mLyricForm;
    private String mLyricLoading;
    private final HashMap<String, DynamicLayout> mLyricMap;
    private String mLyricMessage;
    private final HashMap<String, DynamicLayout> mLyricPlayMap;
    private Layout.Alignment mLyricTextAlignment;
    private final int mMaxFlingVelocity;
    private final int mMinFlingVelocity;
    private boolean mNeedRevert;
    private String mNoLyric;
    private String mNoPlay;
    private float mOffsetY;
    private OnLyricLoadingListener mOnLyricLoadingListener;
    private final OverScroller mOverScroller;
    private long mPlayTimeMillis;
    private Rect mRect;
    private final int mScaledTouchSlop;
    private boolean mScrollToTouch;
    private final HashMap<String, DynamicLayout> mStaticLayoutHashMap;
    private final TextPaint mTextPaint;
    private Lyric.TimeLineMapper mTimeLineMapper;
    private int mUpdateFrequencyTime;
    private VelocityTracker mVelocityTracker;
    private boolean mVerbatimPlay;
    private int mVerbatimScrollStartOffset;
    private int mVerbatimScrollTopOffset;

    private class LoadLyricRunnable implements Runnable {
        private String mMediaDataPath;

        private LoadLyricRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mMediaDataPath != null) {
                if (LyricView.this.mLyricForm != null) {
                    LyricView.this.mLyricForm.setValid(false);
                    LyricView.this.mLyricForm = null;
                    LyricView.this.mLyricMessage = LyricView.this.mLyricLoading;
                    if (LyricView.this.mOnLyricLoadingListener != null) {
                        LyricView.this.mOnLyricLoadingListener.onLoadLyric(0);
                    }
                    LyricView.this.reset();
                    LyricView.this.postInvalidate();
                }
                LyricView.this.mLyricForm = new LyricForm(LyricView.this.mHandler, LyricView.this.mAddKrc);
                LyricView.this.mLyricForm.parseLyric(LyricView.this, this.mMediaDataPath);
            }
        }
    }

    public interface OnLyricLoadingListener {
        public static final int MSG_LOAD_ERROR = 3;
        public static final int MSG_LOAD_SUCCESS = 1;
        public static final int MSG_NOT_LYRIC = 2;
        public static final int MSG_NO_PLAY = 4;
        public static final int MSG_START_LOADING = 0;

        void onLoadLyric(int i);
    }

    private LyricView(Context context) {
        this(context, null);
    }

    public LyricView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public LyricView(Context context, AttributeSet attributeSet, int i) {
        int dimensionPixelSize;
        super(context, attributeSet, i);
        this.mUpdateFrequencyTime = 40;
        this.mLyricMap = new HashMap<>();
        this.mLyricPlayMap = new HashMap<>();
        this.mStaticLayoutHashMap = new HashMap<>();
        this.mLyricTextAlignment = Layout.Alignment.ALIGN_NORMAL;
        this.mHighlightOffset = 0.5f;
        this.mIsClick = false;
        this.mLyricMessage = null;
        this.mNoPlay = null;
        this.mNoLyric = null;
        this.mLyricLoading = null;
        this.mLinearMax = -1;
        this.mAnimatorSpeed = 300;
        this.mScrollToTouch = true;
        this.mNeedRevert = false;
        this.mHandler = new Handler(Looper.getMainLooper()) { // from class: com.yftech.car.widget.media.music.lyrics.LyricView.1
            @Override // android.os.Handler
            public void handleMessage(Message message) {
                if (message.what != 3) {
                    if (message.what == 4) {
                        LyricView.this.scrollToPosition(LyricView.this.mCurrentLineIndex);
                    }
                } else {
                    LyricView.this.mHandler.sendEmptyMessageDelayed(3, (LyricView.this.mVerbatimPlay && LyricView.this.mLinearMax == 1) ? LyricView.this.mUpdateFrequencyTime : 100L);
                    if (LyricView.this.isPlay) {
                        LyricView.this.updateTimeMills(MusicManager.getInstance().getCurrentPlayProgress());
                    }
                }
            }
        };
        this.mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: com.yftech.car.widget.media.music.lyrics.LyricView.2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                LyricView.this.mOffsetY = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                LyricView.this.refresh();
            }
        };
        this.mLoadLyricRunnable = new LoadLyricRunnable();
        Resources resources = context.getResources();
        int color = resources.getColor(17170443, null);
        int color2 = resources.getColor(17170455, null);
        int color3 = resources.getColor(17170457, null);
        int color4 = resources.getColor(17170456, null);
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.LyricView);
        int dimensionPixelSize2 = 24;
        int i2 = 20;
        if (typedArrayObtainStyledAttributes != null) {
            int dimensionPixelSize3 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.LyricView_lyric_view_text_size, 20);
            dimensionPixelSize2 = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.LyricView_lyric_view_highlight_text_size, dimensionPixelSize3);
            dimensionPixelSize = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.LyricView_lyric_view_toast_text_size, dimensionPixelSize3);
            this.mLineSpace = typedArrayObtainStyledAttributes.getDimensionPixelSize(R.styleable.LyricView_lyric_view_line_spacing, 20);
            color = typedArrayObtainStyledAttributes.getColor(R.styleable.LyricView_lyric_view_text_color, color);
            color2 = typedArrayObtainStyledAttributes.getColor(R.styleable.LyricView_lyric_view_highlight_text_color, color2);
            color3 = typedArrayObtainStyledAttributes.getColor(R.styleable.LyricView_lyric_view_highlight_play_text_color, color3);
            int color5 = typedArrayObtainStyledAttributes.getColor(R.styleable.LyricView_lyric_view_toast_text_color, color3);
            int i3 = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_text_align, 0);
            this.mLinearMax = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_linear_max, -1);
            this.mUpdateFrequencyTime = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_update_frequency_time, 40);
            this.mAnimatorSpeed = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_animator_scroll_duration, 300);
            this.mHighlightOffset = typedArrayObtainStyledAttributes.getFloat(R.styleable.LyricView_lyric_view_highlight_offset, 0.5f);
            this.mVerbatimScrollTopOffset = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_verbatim_scroll_top_offset, 0);
            this.mVerbatimScrollStartOffset = typedArrayObtainStyledAttributes.getInt(R.styleable.LyricView_lyric_view_verbatim_scroll_start_offset, 0);
            this.mNoPlay = typedArrayObtainStyledAttributes.getString(R.styleable.LyricView_lyric_view_no_play_music);
            this.mNoLyric = typedArrayObtainStyledAttributes.getString(R.styleable.LyricView_lyric_view_no_lyric);
            this.mLyricLoading = typedArrayObtainStyledAttributes.getString(R.styleable.LyricView_lyric_view_lyric_loading);
            this.mVerbatimPlay = typedArrayObtainStyledAttributes.getBoolean(R.styleable.LyricView_lyric_view_highlight_verbatim_play, false);
            this.mAddKrc = typedArrayObtainStyledAttributes.getBoolean(R.styleable.LyricView_lyric_view_add_krc, false);
            this.mClickItemPlay = typedArrayObtainStyledAttributes.getBoolean(R.styleable.LyricView_lyric_view_click_item_play, false);
            this.mScrollToTouch = typedArrayObtainStyledAttributes.getBoolean(R.styleable.LyricView_lyric_view_touch, true);
            this.mNeedRevert = typedArrayObtainStyledAttributes.getBoolean(R.styleable.LyricView_lyric_view_need_revert, false);
            Locale locale = Locale.getDefault();
            if (this.mNeedRevert) {
                this.isRightStart = locale.getLanguage().equals("ar") || locale.getLanguage().equals("he");
            }
            switch (i3) {
                case 1:
                    this.mLyricTextAlignment = Layout.Alignment.ALIGN_CENTER;
                    break;
                case 2:
                    this.mLyricTextAlignment = this.isRightStart ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_OPPOSITE;
                    break;
                default:
                    this.mLyricTextAlignment = this.isRightStart ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
                    break;
            }
            typedArrayObtainStyledAttributes.recycle();
            i2 = dimensionPixelSize3;
            color4 = color5;
        } else {
            Log.e(TAG, "LyricView: typedArray is null.");
            dimensionPixelSize = 24;
        }
        if (this.mHighlightOffset < 0.0f) {
            this.mHighlightOffset = 0.0f;
        }
        if (this.mHighlightOffset > 1.0f) {
            this.mHighlightOffset = 1.0f;
        }
        if (this.mNoPlay == null) {
            this.mNoPlay = context.getString(R.string.no_play);
        }
        if (this.mNoLyric == null) {
            this.mNoLyric = context.getString(R.string.no_lyric);
        }
        if (this.mLyricLoading == null) {
            this.mLyricLoading = context.getString(R.string.lyric_loading);
        }
        this.mTextPaint = new TextPaint();
        this.mTextPaint.setAntiAlias(true);
        this.mTextPaint.setTextAlign(Paint.Align.LEFT);
        this.mTextPaint.setColor(color);
        this.mTextPaint.setTextSize(i2);
        this.mHighlightPaint = new TextPaint(this.mTextPaint);
        this.mHighlightPaint.setColor(color2);
        float f = dimensionPixelSize2;
        this.mHighlightPaint.setTextSize(f);
        this.mHighlightPlayPaint = new TextPaint(this.mTextPaint);
        this.mHighlightPlayPaint.setColor(color3);
        this.mHighlightPlayPaint.setTextSize(f);
        this.mHighlightPlayPaint.setFakeBoldText(true);
        this.mEmptyTextPaint = new TextPaint(this.mTextPaint);
        this.mEmptyTextPaint.setColor(color4);
        this.mEmptyTextPaint.setTextSize(dimensionPixelSize);
        this.mOverScroller = new OverScroller(context, new DecelerateInterpolator());
        this.mOverScroller.setFriction(0.1f);
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mScaledTouchSlop = viewConfiguration.getScaledTouchSlop();
        this.mMaxFlingVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
        this.mMinFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    }

    private void drawEmptyText(Canvas canvas) {
        Layout.Alignment alignment;
        canvas.save();
        int availableWidth = getAvailableWidth();
        if (this.mLyricMessage == null) {
            this.mLyricMessage = this.mNoLyric;
        }
        switch (this.mLyricTextAlignment) {
            case ALIGN_CENTER:
                alignment = Layout.Alignment.ALIGN_CENTER;
                break;
            case ALIGN_OPPOSITE:
                alignment = !this.isRightStart ? Layout.Alignment.ALIGN_OPPOSITE : Layout.Alignment.ALIGN_NORMAL;
                break;
            default:
                alignment = !this.isRightStart ? Layout.Alignment.ALIGN_NORMAL : Layout.Alignment.ALIGN_OPPOSITE;
                break;
        }
        Layout.Alignment alignment2 = alignment;
        DynamicLayout dynamicLayoutBuild = Build.VERSION.SDK_INT >= 28 ? DynamicLayout.Builder.obtain(this.mLyricMessage, this.mEmptyTextPaint, availableWidth).setAlignment(alignment2).setLineSpacing(0.0f, 1.0f).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(availableWidth).setIncludePad(false).build() : new DynamicLayout(this.mLyricMessage, this.mEmptyTextPaint, availableWidth, alignment2, 1.0f, 0.0f, false);
        canvas.translate(getPaddingStart(), (getAvailableHeight() - dynamicLayoutBuild.getHeight()) / 2.0f);
        dynamicLayoutBuild.draw(canvas);
        canvas.restore();
    }

    /* JADX WARN: Code duplicated, block: B:126:0x030b  */
    private void drawLyricLine(Canvas canvas, float f, float f2, int i) {
        String strSubstring;
        Bitmap bitmapCreateBitmap;
        String string;
        int i2;
        int iMeasureText;
        if (this.mLyric == null) {
            Log.e(TAG, "drawLyricLine: mLyric is null");
            return;
        }
        String lineText = this.mLyric.getLineText(i);
        DynamicLayout dynamicLayoutBuild = this.mLyricMap.get(lineText);
        int availableWidth = getAvailableWidth();
        int i3 = 1;
        int i4 = 0;
        if (i == this.mCurrentLineIndex) {
            DynamicLayout dynamicLayout = this.mLyricPlayMap.get(lineText);
            if (dynamicLayout == null) {
                if (this.mLinearMax == 1) {
                    String[] strArrSplit = lineText.split("\n");
                    string = strArrSplit.length > 1 ? strArrSplit[0] : lineText;
                    float fMeasureText = this.mHighlightPaint.measureText(string);
                    float f3 = availableWidth;
                    if (fMeasureText > f3) {
                        while (fMeasureText > f3) {
                            string = string.substring(0, string.length() - 1);
                            fMeasureText = this.mHighlightPaint.measureText(string + Session.TRUNCATE_STRING);
                        }
                        string = string + Session.TRUNCATE_STRING;
                    }
                } else {
                    string = lineText;
                }
                dynamicLayoutBuild = Build.VERSION.SDK_INT >= 28 ? DynamicLayout.Builder.obtain(string, this.mHighlightPaint, availableWidth).setAlignment(this.mLyricTextAlignment).setLineSpacing(0.0f, 1.0f).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(availableWidth).setIncludePad(false).build() : new DynamicLayout(string, this.mHighlightPaint, availableWidth, this.mLyricTextAlignment, 1.0f, 0.0f, false);
                this.mLyricPlayMap.put(lineText, dynamicLayoutBuild);
            } else {
                i4 = 0;
                i3 = 1;
                dynamicLayoutBuild = dynamicLayout;
                string = dynamicLayout.getText().toString();
            }
            if (this.mVerbatimPlay && this.mLinearMax == i3) {
                long lineStartTime = this.mLyric.getLineStartTime(this.mCurrentLineIndex);
                if (lineStartTime > 0 && this.mPlayTimeMillis > lineStartTime) {
                    List<KrcPlayEntity> krcPlayEntity = this.mLyric.getKrcPlayEntity(this.mCurrentLineIndex);
                    int i5 = (int) (this.mPlayTimeMillis - lineStartTime);
                    if (krcPlayEntity != null) {
                        int i6 = i4;
                        float fMeasureText2 = 0.0f;
                        KrcPlayEntity krcPlayEntity2 = null;
                        while (i6 < krcPlayEntity.size()) {
                            KrcPlayEntity krcPlayEntity3 = krcPlayEntity.get(i6);
                            int[] playTime = krcPlayEntity3.getPlayTime();
                            int length = playTime.length;
                            for (int i7 = i4; i7 < length; i7++) {
                                i4 += playTime[i7];
                            }
                            if (i4 >= i5) {
                                krcPlayEntity2 = krcPlayEntity3;
                                break;
                            }
                            fMeasureText2 += this.mHighlightPlayPaint.measureText(krcPlayEntity3.getLyrics());
                            if (i6 >= krcPlayEntity.size() - i3) {
                                krcPlayEntity2 = krcPlayEntity3;
                            }
                            i6++;
                            i4 = 0;
                        }
                        if (krcPlayEntity2 != null) {
                            float fMeasureText3 = this.mHighlightPlayPaint.measureText(krcPlayEntity2.getLyrics());
                            int[] playTime2 = krcPlayEntity2.getPlayTime();
                            int i8 = playTime2[i3];
                            float f4 = i5 - playTime2[0];
                            if (f4 < 1.0f) {
                                f4 = 1.0f;
                            }
                            float f5 = (f4 / i8) * fMeasureText3;
                            if (f5 < 1.0f) {
                                f5 = 1.0f;
                            }
                            if (f5 > fMeasureText3) {
                                f5 = fMeasureText3;
                            }
                            float f6 = f5 + fMeasureText2;
                            if (f6 < 1.0f) {
                                f6 = 1.0f;
                            }
                            float f7 = availableWidth;
                            if (f6 > f7) {
                                f6 = f7;
                            }
                            switch (this.mLyricTextAlignment) {
                                case ALIGN_CENTER:
                                    iMeasureText = ((int) (f7 - this.mHighlightPlayPaint.measureText(string))) / 2;
                                    break;
                                case ALIGN_OPPOSITE:
                                    iMeasureText = (int) (f7 - this.mHighlightPlayPaint.measureText(string));
                                    break;
                                default:
                                    iMeasureText = 0;
                                    break;
                            }
                            int i9 = iMeasureText < 0 ? 0 : iMeasureText;
                            bitmapCreateBitmap = Bitmap.createBitmap(((int) f6) + i9, dynamicLayoutBuild.getHeight(), Bitmap.Config.ARGB_8888);
                            Canvas canvas2 = new Canvas(bitmapCreateBitmap);
                            int height = (int) (dynamicLayoutBuild.getHeight() - (dynamicLayoutBuild.getHeight() / 6.0f));
                            if (this.mVerbatimScrollTopOffset != 0) {
                                height += this.mVerbatimScrollTopOffset;
                            }
                            if (this.mVerbatimScrollStartOffset != 0) {
                                i9 += this.mVerbatimScrollStartOffset;
                            }
                            canvas2.drawText(string, i9, height, this.mHighlightPlayPaint);
                        }
                    } else {
                        long lineStartTime2 = this.mLyric.getLineStartTime(this.mCurrentLineIndex + i3) - lineStartTime;
                        if (lineStartTime2 <= 0) {
                            lineStartTime2 = 1000;
                        }
                        float fMeasureText4 = this.mHighlightPlayPaint.measureText(string);
                        int i10 = (int) ((i5 / lineStartTime2) * fMeasureText4);
                        if (i10 <= 0) {
                            i10 = i3;
                        }
                        switch (this.mLyricTextAlignment) {
                            case ALIGN_CENTER:
                                i2 = ((int) (availableWidth - fMeasureText4)) / 2;
                                break;
                            case ALIGN_OPPOSITE:
                                i2 = (int) (availableWidth - fMeasureText4);
                                break;
                            default:
                                i2 = 0;
                                break;
                        }
                        int i11 = i2 < 0 ? 0 : i2;
                        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(i10 + i11, dynamicLayoutBuild.getHeight(), Bitmap.Config.ARGB_8888);
                        Canvas canvas3 = new Canvas(bitmapCreateBitmap2);
                        int height2 = (int) (dynamicLayoutBuild.getHeight() - (dynamicLayoutBuild.getHeight() / 6.0f));
                        if (this.mVerbatimScrollTopOffset != 0) {
                            height2 += this.mVerbatimScrollTopOffset;
                        }
                        if (this.mVerbatimScrollStartOffset != 0) {
                            i11 += this.mVerbatimScrollStartOffset;
                        }
                        canvas3.drawText(string, i11, height2, this.mHighlightPlayPaint);
                        bitmapCreateBitmap = bitmapCreateBitmap2;
                    }
                }
            }
            canvas.save();
            canvas.translate(f, (f2 - (dynamicLayoutBuild.getHeight() / 2.0f)) - this.mOffsetY);
            dynamicLayoutBuild.draw(canvas);
            if (bitmapCreateBitmap != null) {
                canvas.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, (Paint) null);
            }
            canvas.restore();
        }
        if (dynamicLayoutBuild == null) {
            if (this.mLinearMax == 1) {
                String[] strArrSplit2 = lineText.split("\n");
                strSubstring = strArrSplit2.length > 1 ? strArrSplit2[0] : lineText;
                float fMeasureText5 = this.mTextPaint.measureText(strSubstring);
                float f8 = availableWidth;
                if (fMeasureText5 > f8) {
                    while (fMeasureText5 > f8) {
                        strSubstring = strSubstring.substring(0, strSubstring.length() - 1);
                        fMeasureText5 = this.mTextPaint.measureText(strSubstring + Session.TRUNCATE_STRING);
                    }
                    strSubstring = strSubstring + Session.TRUNCATE_STRING;
                }
            } else {
                strSubstring = lineText;
            }
            dynamicLayoutBuild = Build.VERSION.SDK_INT >= 28 ? DynamicLayout.Builder.obtain(strSubstring, this.mTextPaint, availableWidth).setAlignment(this.mLyricTextAlignment).setLineSpacing(0.0f, 1.0f).setEllipsize(TextUtils.TruncateAt.END).setEllipsizedWidth(availableWidth).setIncludePad(false).build() : new DynamicLayout(strSubstring, this.mTextPaint, availableWidth, this.mLyricTextAlignment, 1.0f, 0.0f, false);
            this.mLyricMap.put(lineText, dynamicLayoutBuild);
        }
        bitmapCreateBitmap = null;
        canvas.save();
        canvas.translate(f, (f2 - (dynamicLayoutBuild.getHeight() / 2.0f)) - this.mOffsetY);
        dynamicLayoutBuild.draw(canvas);
        if (bitmapCreateBitmap != null) {
            canvas.drawBitmap(bitmapCreateBitmap, 0.0f, 0.0f, (Paint) null);
        }
        canvas.restore();
    }

    private int getAvailableHeight() {
        return (getHeight() - getPaddingTop()) - getPaddingBottom();
    }

    private int getAvailableWidth() {
        return (getWidth() - getPaddingStart()) - getPaddingEnd();
    }

    private MusicManager.CarMusicControlCallback getCarMusicControlCallback() {
        return new MusicManager.CarMusicControlCallback() { // from class: com.yftech.car.widget.media.music.lyrics.LyricView.3
            @Override // com.yftech.car.widget.media.manager.MusicManager.CarMusicControlCallback, com.yftech.car.widget.media.manager.MusicManager.OnMusicControlListener
            public void onChangePlayMusic(MediaFile mediaFile) {
                LyricView.this.mLyric = null;
                LyricView.this.mTimeLineMapper = null;
                if (mediaFile == null || TextUtils.isEmpty(mediaFile.getData()) || mediaFile.getData().equals("unknown")) {
                    LyricView.this.mLyricMessage = LyricView.this.mNoPlay;
                    if (LyricView.this.mOnLyricLoadingListener != null) {
                        LyricView.this.mOnLyricLoadingListener.onLoadLyric(4);
                    }
                    if (Looper.myLooper() == Looper.getMainLooper()) {
                        LyricView.this.invalidate();
                        return;
                    } else {
                        LyricView.this.postInvalidate();
                        return;
                    }
                }
                if (LyricView.this.mOnLyricLoadingListener != null) {
                    LyricView.this.mOnLyricLoadingListener.onLoadLyric(0);
                }
                LyricView.this.mLyricMessage = LyricView.this.mLyricLoading;
                if (Looper.myLooper() == Looper.getMainLooper()) {
                    LyricView.this.invalidate();
                } else {
                    LyricView.this.postInvalidate();
                }
                if (LyricView.this.mLyricForm != null) {
                    LyricView.this.mLyricForm.setValid(false);
                    LyricView.this.reset();
                    LyricView.this.mLyricForm = null;
                }
                LyricView.this.mLoadLyricRunnable.mMediaDataPath = mediaFile.getData();
                TaskManager.getInstance().threadRunnable(LyricView.this.mLoadLyricRunnable);
            }

            @Override // com.yftech.car.widget.media.manager.MusicManager.CarMusicControlCallback, com.yftech.car.widget.media.manager.MusicManager.OnMusicControlListener
            public void onChangePlaying(boolean z) {
                LyricView.this.isPlay = z;
            }
        };
    }

    private int getLineCount() {
        if (this.mLyric == null) {
            return 0;
        }
        return this.mLyric.getLineCount();
    }

    private float getLineHeight(int i) {
        String str;
        DynamicLayout dynamicLayout;
        String str2;
        if (this.mLyric == null) {
            return 0.0f;
        }
        int availableWidth = getAvailableWidth();
        String lineText = this.mLyric.getLineText(i);
        if (this.mLinearMax == 1) {
            String[] strArrSplit = lineText.split("\n");
            String str3 = strArrSplit.length > 1 ? strArrSplit[0] : lineText;
            if (this.mHighlightPaint.measureText(str3) > availableWidth) {
                str2 = str3.substring(0, 1) + Session.TRUNCATE_STRING;
            } else {
                str2 = str3;
            }
            str = str2;
        } else {
            str = lineText;
        }
        DynamicLayout dynamicLayout2 = this.mStaticLayoutHashMap.get(lineText);
        if (i == this.mCurrentLineIndex) {
            dynamicLayout = new DynamicLayout(str, this.mHighlightPaint, getAvailableWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        } else if (dynamicLayout2 == null) {
            dynamicLayout = new DynamicLayout(str, this.mTextPaint, getAvailableWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            this.mStaticLayoutHashMap.put(lineText, dynamicLayout);
        } else {
            dynamicLayout = dynamicLayout2;
        }
        return dynamicLayout.getHeight();
    }

    private int getLineIndexByTime(long j) {
        if (this.mTimeLineMapper == null) {
            return 0;
        }
        return this.mTimeLineMapper.map(j);
    }

    private synchronized float getOffsetY(int i) {
        float lineHeight;
        lineHeight = 0.0f;
        for (int i2 = 1; i2 <= i; i2++) {
            lineHeight += ((getLineHeight(i2 - 1) + getLineHeight(i2)) / 2.0f) + this.mLineSpace;
        }
        return lineHeight;
    }

    private int getTextHeight(String str, Paint paint) {
        if (this.mRect == null) {
            this.mRect = new Rect();
        }
        paint.getTextBounds(str, 0, str.length(), this.mRect);
        return this.mRect.height();
    }

    private void handleActionUp() {
        if (isOverScrolled() && this.mOffsetY < 0.0f) {
            scrollToPosition(0);
            postScrollToCurrentLine();
            return;
        }
        if (isOverScrolled() && this.mOffsetY > getOffsetY(getLineCount() - 1)) {
            scrollToPosition(getLineCount() - 1);
            postScrollToCurrentLine();
            return;
        }
        this.mVelocityTracker.computeCurrentVelocity(1000, this.mMaxFlingVelocity);
        float yVelocity = this.mVelocityTracker.getYVelocity();
        if (Math.abs(yVelocity) > this.mMinFlingVelocity) {
            this.mOverScroller.fling(0, (int) this.mOffsetY, 0, (int) (-yVelocity), 0, 0, 0, (int) getOffsetY(getLineCount() - 1), 0, (int) getLineHeight(0));
            refresh();
        }
        releaseVelocityTracker();
        postScrollToCurrentLine();
    }

    private boolean isEmpty() {
        return this.mLyric == null || this.mLyric.getLineCount() == 0;
    }

    private boolean isOverScrolled() {
        return this.mOffsetY > getOffsetY(getLineCount() - 1) || this.mOffsetY < 0.0f;
    }

    private void postScrollToCurrentLine() {
        this.mHandler.sendEmptyMessageDelayed(4, 2500L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void refresh() {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    private void releaseVelocityTracker() {
        if (this.mVelocityTracker == null) {
            return;
        }
        this.mVelocityTracker.clear();
        this.mVelocityTracker.recycle();
        this.mVelocityTracker = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reset() {
        this.mLyric = null;
        this.mTimeLineMapper = null;
        this.mLyricMap.clear();
        this.mLyricPlayMap.clear();
        this.mStaticLayoutHashMap.clear();
        this.mCurrentLineIndex = 0;
        this.mOffsetY = 0.0f;
        this.isPressing = false;
        this.isFling = false;
        this.mHandler.removeMessages(4);
        refresh();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void scrollToPosition(int i) {
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.mOffsetY, getOffsetY(i));
        valueAnimatorOfFloat.addUpdateListener(this.mAnimatorUpdateListener);
        valueAnimatorOfFloat.setDuration(this.mAnimatorSpeed);
        valueAnimatorOfFloat.start();
    }

    @Override // android.view.View
    public void computeScroll() {
        super.computeScroll();
        if (this.mOverScroller.computeScrollOffset()) {
            this.mOffsetY = this.mOverScroller.getCurrY();
            refresh();
        }
    }

    @Override // android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: ");
        if (this.mCarMusicControlCallback == null) {
            this.mCarMusicControlCallback = getCarMusicControlCallback();
            MusicManager.getInstance().registerMusicControlCallback(this.mCarMusicControlCallback);
            this.isPlay = MusicManager.getInstance().isPlaying();
            this.mHandler.sendEmptyMessage(3);
            this.mCarMusicControlCallback.onChangePlayMusic(MusicManager.getInstance().getPlayMusic());
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
        this.mHandler.removeMessages(3);
        MusicManager.getInstance().unregisterMusicControlCallback(this.mCarMusicControlCallback);
        this.mCarMusicControlCallback = null;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isEmpty()) {
            drawEmptyText(canvas);
            return;
        }
        float paddingStart = getPaddingStart();
        float availableHeight = getAvailableHeight() * this.mHighlightOffset;
        int lineCount = getLineCount();
        for (int i = 0; i < lineCount; i++) {
            if (i > 0) {
                availableHeight += ((getLineHeight(i - 1) + getLineHeight(i)) / 2.0f) + this.mLineSpace;
            }
            drawLyricLine(canvas, paddingStart, availableHeight, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: ");
        LrcParser.setLinearMax(this.mLinearMax);
        if (this.mCarMusicControlCallback == null) {
            this.mCarMusicControlCallback = getCarMusicControlCallback();
            MusicManager.getInstance().registerMusicControlCallback(this.mCarMusicControlCallback);
            this.isPlay = MusicManager.getInstance().isPlaying();
            this.mHandler.sendEmptyMessage(3);
            this.mCarMusicControlCallback.onChangePlayMusic(MusicManager.getInstance().getPlayMusic());
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEmpty() || !this.mScrollToTouch) {
            return super.onTouchEvent(motionEvent);
        }
        if (this.mVelocityTracker == null) {
            this.mVelocityTracker = VelocityTracker.obtain();
        }
        this.mVelocityTracker.addMovement(motionEvent);
        float lineHeight = 0.0f;
        int i = 0;
        switch (motionEvent.getAction()) {
            case 0:
                this.mHandler.removeMessages(4);
                if (!this.mOverScroller.isFinished()) {
                    this.mOverScroller.abortAnimation();
                }
                this.mDownX = motionEvent.getX();
                this.mDownY = motionEvent.getY();
                this.mLastMotionY = this.mDownY;
                this.isPressing = true;
                this.isFling = false;
                this.mIsClick = true;
                return true;
            case 1:
            case 3:
                if (!this.isFling) {
                    refresh();
                    performClick();
                }
                handleActionUp();
                this.isPressing = false;
                if (this.mIsClick) {
                    this.mIsClick = false;
                    if (this.mClickItemPlay) {
                        float y = motionEvent.getY() - (getAvailableHeight() * this.mHighlightOffset);
                        while (lineHeight < this.mOffsetY + (y - getLineHeight(i))) {
                            i++;
                            lineHeight += getLineHeight(i) + this.mLineSpace;
                        }
                        if (this.mCurrentLineIndex != i || i == 0) {
                            MusicManager.getInstance().setPlayProgress((int) this.mLyric.getLineStartTime(i));
                        }
                        Log.d(TAG, "onTouchEvent: click text==" + this.mLyric.getLineText(i));
                    }
                }
                return true;
            case 2:
                float y2 = motionEvent.getY() - this.mLastMotionY;
                if (Math.abs(y2) > this.mScaledTouchSlop) {
                    this.isFling = true;
                    this.mIsClick = false;
                }
                if (this.isFling) {
                    float offsetY = getOffsetY(getLineCount() - 1);
                    if (this.mOffsetY < 0.0f || this.mOffsetY > offsetY) {
                        y2 /= 3.5f;
                    }
                    this.mOffsetY -= y2;
                    this.mLastMotionY = motionEvent.getY();
                    refresh();
                } else {
                    float fAbs = Math.abs(motionEvent.getX() - this.mDownX);
                    float fAbs2 = Math.abs(motionEvent.getY() - this.mDownY);
                    if (fAbs > this.mScaledTouchSlop || fAbs2 > this.mScaledTouchSlop) {
                        this.mIsClick = false;
                    }
                }
                return true;
            default:
                return true;
        }
    }

    @Override // android.view.View
    public boolean performClick() {
        return super.performClick();
    }

    public void setLyric(Lyric lyric) {
        reset();
        if (lyric == null) {
            this.mLyric = null;
            this.mTimeLineMapper = null;
            if (this.mOnLyricLoadingListener != null) {
                this.mOnLyricLoadingListener.onLoadLyric(2);
            }
        } else {
            this.mLyric = lyric.removeBlankLines();
            this.mTimeLineMapper = this.mLyric.newTimeLineMapper();
        }
        if (lyric == null || Lyric.EMPTY == lyric) {
            if (this.mOnLyricLoadingListener != null) {
                this.mOnLyricLoadingListener.onLoadLyric(2);
            }
        } else if (this.mOnLyricLoadingListener != null) {
            this.mOnLyricLoadingListener.onLoadLyric(1);
        }
        refresh();
        if (this.isPlay) {
            return;
        }
        updateTimeMills(MusicManager.getInstance().getCurrentPlayProgress());
    }

    void setLyricMessageNoLyric() {
        this.mLyricMessage = this.mNoLyric;
    }

    public void setOnLyricLoadingListener(OnLyricLoadingListener onLyricLoadingListener) {
        this.mOnLyricLoadingListener = onLyricLoadingListener;
    }

    public void updateTimeMills(long j) {
        if (isEmpty()) {
            return;
        }
        this.mPlayTimeMillis = j;
        synchronized (this.mTimeLineMapper) {
            int lineIndexByTime = getLineIndexByTime(j);
            if (this.mCurrentLineIndex != lineIndexByTime) {
                this.mCurrentLineIndex = lineIndexByTime;
                if (!this.isPressing && !this.mHandler.hasMessages(4)) {
                    this.mHandler.sendEmptyMessage(4);
                }
                refresh();
                return;
            }
            if (this.mVerbatimPlay && this.mLinearMax == 1) {
                invalidate();
            }
        }
    }
}
