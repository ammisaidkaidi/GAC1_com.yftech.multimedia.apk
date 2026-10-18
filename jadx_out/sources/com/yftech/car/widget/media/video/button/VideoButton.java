package com.yftech.car.widget.media.video.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import androidx.appcompat.widget.AppCompatButton;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.video.BaseVideo;

/* JADX INFO: loaded from: classes3.dex */
public class VideoButton extends AppCompatButton implements View.OnClickListener, View.OnLongClickListener {
    public static final int ALBUM = 1004;
    public static final int ARTIST = 1003;
    public static final int DIRECTORY = 1006;
    public static final int FOLDER = 1001;
    public static final int GENRE = 1005;
    public static final int NEXT = 2;
    public static final int PAUSE = 1;
    public static final int PLAY = 0;
    public static final int PLAY_LIST = 1100;
    public static final int PLAY_PAUSE = 4;
    public static final int PREVIOUS = 3;
    public static final int SWITCH_MODE = 5;
    public static final int SWITCH_SCALE = 6;
    public static final int SWITCH_USB1 = 100;
    public static final int SWITCH_USB2 = 101;
    public static final int SWITCH_USB3 = 102;
    public static final int SWITCH_USB4 = 103;
    public static final int SWITCH_USB5 = 104;
    private static final String TAG = "MediaButton";
    public static final int TITLE = 1002;
    private Drawable mAdaptationDrawable;
    private String mAdaptationText;
    private int mClickFunction;
    private int mClickLongFunction;
    private Drawable mDrawable;
    private Drawable mDrawableSelected;
    private Drawable mFullScreenDrawable;
    private String mFullScreenText;
    private OnClickFunctionListener mFunctionListener;
    private Drawable mListLoopDrawable;
    private String mListLoopText;
    private int mMediaDataListId;
    private View.OnClickListener mOnClickListener;
    private Drawable mOneToOneDrawable;
    private String mOneToOneText;
    private Drawable mRandomPlayDrawable;
    private String mRandomPlayText;
    private String mSelected;
    private Drawable mSequencePlayDrawable;
    private String mSequencePlayText;
    private Drawable mSinglePlayDrawable;
    private String mSinglePlayText;
    private String mText;

    public interface OnClickFunctionListener {
        void onClickFunction(int i, int i2);
    }

    private VideoButton(Context context) {
        super(context);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
    }

    public VideoButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        initStatus(context, attributeSet);
    }

    public VideoButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        initStatus(context, attributeSet);
    }

    private void initStatus(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.VideoButton);
        this.mClickFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.VideoButton_video_click_function, -1);
        this.mClickLongFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.VideoButton_video_long_click_function, -1);
        this.mDrawableSelected = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_background_activated);
        this.mSelected = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_activated_text);
        this.mListLoopText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_list_loop_text);
        this.mSinglePlayText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_single_play_text);
        this.mRandomPlayText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_random_play_text);
        this.mSequencePlayText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_sequence_play_text);
        this.mListLoopDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_list_loop_drawable);
        this.mSinglePlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_single_play_drawable);
        this.mRandomPlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_random_play_drawable);
        this.mSequencePlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_sequence_play_drawable);
        this.mAdaptationText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_adaptation_text);
        this.mFullScreenText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_full_screen_text);
        this.mOneToOneText = typedArrayObtainStyledAttributes.getString(R.styleable.VideoButton_video_one_to_one_text);
        this.mAdaptationDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_adaptation_drawable);
        this.mFullScreenDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_full_screen_drawable);
        this.mOneToOneDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.VideoButton_video_one_to_one_drawable);
        this.mMediaDataListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.VideoButton_video_media_data_list_id, -1);
    }

    public boolean isFunction(int i) {
        return this.mClickFunction == i || this.mClickLongFunction == i;
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        if (this.mClickFunction != -1 && this.mFunctionListener != null) {
            this.mFunctionListener.onClickFunction(this.mMediaDataListId, this.mClickFunction);
        }
        if (this.mOnClickListener != null) {
            this.mOnClickListener.onClick(view);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, "onDetachedFromWindow: ");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        Log.d(TAG, "onFinishInflate: ");
        CharSequence text = getText();
        if (text != null) {
            this.mText = text.toString();
        }
        this.mDrawable = getBackground();
        setOnClickListener(this);
    }

    @Override // android.view.View.OnLongClickListener
    public boolean onLongClick(View view) {
        if (this.mClickLongFunction == -1 || this.mFunctionListener == null) {
            return true;
        }
        this.mFunctionListener.onClickFunction(this.mMediaDataListId, this.mClickLongFunction);
        return true;
    }

    public void setActivatedStatus(boolean z) {
        if (this.mDrawableSelected != null) {
            setBackground(z ? this.mDrawableSelected : this.mDrawable);
        }
        if (this.mSelected != null) {
            setText(z ? this.mSelected : this.mText);
        }
    }

    public void setOnClickFunctionListener(OnClickFunctionListener onClickFunctionListener) {
        this.mFunctionListener = onClickFunctionListener;
    }

    public void setUsbStatus(int i) {
    }

    public void setVideoOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setVideoPlayer(BaseVideo baseVideo) {
        if (this.mClickFunction == 4) {
            setActivatedStatus(baseVideo.isPlaying());
        }
        if (this.mClickLongFunction != -1) {
            setOnLongClickListener(this);
        }
    }

    public void updatePlayMode(int i) {
        switch (i) {
            case 100:
                setText(this.mListLoopText);
                if (this.mListLoopDrawable != null) {
                    setBackground(this.mListLoopDrawable);
                }
                break;
            case 101:
                setText(this.mSinglePlayText);
                if (this.mSinglePlayDrawable != null) {
                    setBackground(this.mSinglePlayDrawable);
                }
                break;
            case 102:
                setText(this.mRandomPlayText);
                if (this.mRandomPlayDrawable != null) {
                    setBackground(this.mRandomPlayDrawable);
                }
                break;
            case 103:
                setText(this.mSequencePlayText);
                if (this.mSequencePlayDrawable != null) {
                    setBackground(this.mSequencePlayDrawable);
                }
                break;
            default:
                Log.e(TAG, "updatePlayMode: playMode==" + i);
                break;
        }
    }

    public void updatePlayScale(int i) {
        switch (i) {
            case 200:
                setText(this.mAdaptationText);
                if (this.mAdaptationDrawable != null) {
                    setBackground(this.mAdaptationDrawable);
                }
                break;
            case 201:
                setText(this.mFullScreenText);
                if (this.mFullScreenDrawable != null) {
                    setBackground(this.mFullScreenDrawable);
                }
                break;
            case 202:
                setText(this.mOneToOneText);
                if (this.mOneToOneDrawable != null) {
                    setBackground(this.mOneToOneDrawable);
                }
                break;
            default:
                Log.e(TAG, "updatePlayScale: playScale==" + i);
                break;
        }
    }
}
