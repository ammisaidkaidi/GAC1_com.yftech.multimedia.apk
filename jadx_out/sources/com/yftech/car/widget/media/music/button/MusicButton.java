package com.yftech.car.widget.media.music.button;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import com.yftech.car.media.data.entity.MediaFile;
import com.yftech.car.widget.media.R;
import com.yftech.car.widget.media.manager.MediaDataManager;
import com.yftech.car.widget.media.manager.MusicManager;

/* JADX INFO: loaded from: classes3.dex */
public class MusicButton extends AppCompatButton {
    public static final int ALBUM = 1004;
    public static final int ARTIST = 1003;
    public static final int COLLECT = 1000;
    public static final int DIRECTORY = 1006;
    private static final int FAST_BACK = 8;
    private static final int FAST_FORWARD = 7;
    public static final int FOLDER = 1001;
    public static final int GENRE = 1005;
    private static final int NEXT = 2;
    private static final int PAUSE = 1;
    private static final int PLAY = 0;
    public static final int PLAY_DIRECTORY_ALL = 9;
    public static final int PLAY_LIST = 1100;
    public static final int PLAY_PAUSE = 4;
    private static final int PREVIOUS = 3;
    public static final int SWITCH_COLLECT = 6;
    public static final int SWITCH_MODE = 5;
    public static final int SWITCH_USB1 = 100;
    public static final int SWITCH_USB2 = 101;
    public static final int SWITCH_USB3 = 102;
    public static final int SWITCH_USB4 = 103;
    public static final int SWITCH_USB5 = 104;
    private static final String TAG = "MediaButton";
    public static final int TITLE = 1002;
    private boolean isActivatedStatus;
    private int mClickFunction;
    private int mClickLongFunction;
    private Drawable mDrawable;
    private Drawable mDrawableSelected;
    private final Runnable mFastBackRunnable;
    private final Runnable mFastForwardRunnable;
    private OnClickFunctionListener mFunctionListener;
    private Drawable mListLoopDrawable;
    private String mListLoopText;
    private int mMediaDataListId;
    private final View.OnClickListener mOnClick;
    private View.OnClickListener mOnClickListener;
    private final View.OnLongClickListener mOnLongClick;
    private View.OnLongClickListener mOnLongClickListener;
    private final View.OnTouchListener mOnTouchListener;
    private int mPressKeyEvent;
    private Drawable mRandomPlayDrawable;
    private String mRandomPlayText;
    private String mSelected;
    private Drawable mSequencePlayDrawable;
    private String mSequencePlayText;
    private Drawable mSinglePlayDrawable;
    private String mSinglePlayText;
    private boolean mSwitchUsbPlay;
    private String mText;

    public interface OnClickFunctionListener {
        void onClickFunction(int i, int i2);
    }

    private MusicButton(Context context) {
        super(context);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mPressKeyEvent = -1;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MusicButton.this.mClickFunction == 9 && MusicButton.this.isActivatedStatus) {
                    Log.e(MusicButton.TAG, "onClick: PLAY_DIRECTORY_ALL isActivatedStatus true");
                    return;
                }
                MusicButton.this.setClickFunction(MusicButton.this.mClickFunction);
                if (MusicButton.this.mFunctionListener != null) {
                    MusicButton.this.mFunctionListener.onClickFunction(MusicButton.this.mMediaDataListId, MusicButton.this.mClickFunction);
                }
                if (MusicButton.this.mOnClickListener != null) {
                    MusicButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                MusicButton.this.setClickFunction(MusicButton.this.mClickLongFunction);
                if (MusicButton.this.mOnLongClickListener == null) {
                    return true;
                }
                MusicButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
        this.mOnTouchListener = new View.OnTouchListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.3
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                Log.d(MusicButton.TAG, "onTouch: action==" + action);
                if (action != 3) {
                    switch (action) {
                        case 0:
                            if (MusicButton.this.mClickFunction != 7 && MusicButton.this.mClickFunction != 8) {
                                if (MusicButton.this.mClickLongFunction == 7 || MusicButton.this.mClickLongFunction == 8) {
                                    switch (MusicButton.this.mClickLongFunction) {
                                        case 7:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastForwardRunnable, 1000L);
                                            break;
                                        case 8:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastBackRunnable, 1000L);
                                            break;
                                    }
                                }
                            } else {
                                switch (MusicButton.this.mClickFunction) {
                                    case 7:
                                        MusicButton.this.mFastForwardRunnable.run();
                                        break;
                                    case 8:
                                        MusicButton.this.mFastBackRunnable.run();
                                        break;
                                }
                            }
                            return false;
                        case 1:
                            break;
                        default:
                            return false;
                    }
                }
                MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                if (MusicButton.this.mPressKeyEvent == -1) {
                    return false;
                }
                if (MusicButton.this.mPressKeyEvent == 7) {
                    MusicManager.getInstance().fastForward(action);
                } else if (MusicButton.this.mPressKeyEvent == 8) {
                    MusicManager.getInstance().fastBack(action);
                }
                MusicButton.this.mPressKeyEvent = -1;
                view.setPressed(false);
                return true;
            }
        };
        this.mFastForwardRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.4
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 7;
                MusicManager.getInstance().fastForward(0);
            }
        };
        this.mFastBackRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.5
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 8;
                MusicManager.getInstance().fastBack(0);
            }
        };
    }

    public MusicButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mPressKeyEvent = -1;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MusicButton.this.mClickFunction == 9 && MusicButton.this.isActivatedStatus) {
                    Log.e(MusicButton.TAG, "onClick: PLAY_DIRECTORY_ALL isActivatedStatus true");
                    return;
                }
                MusicButton.this.setClickFunction(MusicButton.this.mClickFunction);
                if (MusicButton.this.mFunctionListener != null) {
                    MusicButton.this.mFunctionListener.onClickFunction(MusicButton.this.mMediaDataListId, MusicButton.this.mClickFunction);
                }
                if (MusicButton.this.mOnClickListener != null) {
                    MusicButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                MusicButton.this.setClickFunction(MusicButton.this.mClickLongFunction);
                if (MusicButton.this.mOnLongClickListener == null) {
                    return true;
                }
                MusicButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
        this.mOnTouchListener = new View.OnTouchListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.3
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                Log.d(MusicButton.TAG, "onTouch: action==" + action);
                if (action != 3) {
                    switch (action) {
                        case 0:
                            if (MusicButton.this.mClickFunction != 7 && MusicButton.this.mClickFunction != 8) {
                                if (MusicButton.this.mClickLongFunction == 7 || MusicButton.this.mClickLongFunction == 8) {
                                    switch (MusicButton.this.mClickLongFunction) {
                                        case 7:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastForwardRunnable, 1000L);
                                            break;
                                        case 8:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastBackRunnable, 1000L);
                                            break;
                                    }
                                }
                            } else {
                                switch (MusicButton.this.mClickFunction) {
                                    case 7:
                                        MusicButton.this.mFastForwardRunnable.run();
                                        break;
                                    case 8:
                                        MusicButton.this.mFastBackRunnable.run();
                                        break;
                                }
                            }
                            return false;
                        case 1:
                            break;
                        default:
                            return false;
                    }
                }
                MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                if (MusicButton.this.mPressKeyEvent == -1) {
                    return false;
                }
                if (MusicButton.this.mPressKeyEvent == 7) {
                    MusicManager.getInstance().fastForward(action);
                } else if (MusicButton.this.mPressKeyEvent == 8) {
                    MusicManager.getInstance().fastBack(action);
                }
                MusicButton.this.mPressKeyEvent = -1;
                view.setPressed(false);
                return true;
            }
        };
        this.mFastForwardRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.4
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 7;
                MusicManager.getInstance().fastForward(0);
            }
        };
        this.mFastBackRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.5
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 8;
                MusicManager.getInstance().fastBack(0);
            }
        };
        initStatus(context, attributeSet);
    }

    public MusicButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mClickLongFunction = -1;
        this.mText = "";
        this.mSelected = null;
        this.mPressKeyEvent = -1;
        this.mOnClick = new View.OnClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.1
            @Override // android.view.View.OnClickListener
            public void onClick(View view) {
                if (MusicButton.this.mClickFunction == 9 && MusicButton.this.isActivatedStatus) {
                    Log.e(MusicButton.TAG, "onClick: PLAY_DIRECTORY_ALL isActivatedStatus true");
                    return;
                }
                MusicButton.this.setClickFunction(MusicButton.this.mClickFunction);
                if (MusicButton.this.mFunctionListener != null) {
                    MusicButton.this.mFunctionListener.onClickFunction(MusicButton.this.mMediaDataListId, MusicButton.this.mClickFunction);
                }
                if (MusicButton.this.mOnClickListener != null) {
                    MusicButton.this.mOnClickListener.onClick(view);
                }
            }
        };
        this.mOnLongClick = new View.OnLongClickListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.2
            @Override // android.view.View.OnLongClickListener
            public boolean onLongClick(View view) {
                MusicButton.this.setClickFunction(MusicButton.this.mClickLongFunction);
                if (MusicButton.this.mOnLongClickListener == null) {
                    return true;
                }
                MusicButton.this.mOnLongClickListener.onLongClick(view);
                return true;
            }
        };
        this.mOnTouchListener = new View.OnTouchListener() { // from class: com.yftech.car.widget.media.music.button.MusicButton.3
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.view.View.OnTouchListener
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                Log.d(MusicButton.TAG, "onTouch: action==" + action);
                if (action != 3) {
                    switch (action) {
                        case 0:
                            if (MusicButton.this.mClickFunction != 7 && MusicButton.this.mClickFunction != 8) {
                                if (MusicButton.this.mClickLongFunction == 7 || MusicButton.this.mClickLongFunction == 8) {
                                    switch (MusicButton.this.mClickLongFunction) {
                                        case 7:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastForwardRunnable, 1000L);
                                            break;
                                        case 8:
                                            MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                                            MusicButton.this.postOnAnimationDelayed(MusicButton.this.mFastBackRunnable, 1000L);
                                            break;
                                    }
                                }
                            } else {
                                switch (MusicButton.this.mClickFunction) {
                                    case 7:
                                        MusicButton.this.mFastForwardRunnable.run();
                                        break;
                                    case 8:
                                        MusicButton.this.mFastBackRunnable.run();
                                        break;
                                }
                            }
                            return false;
                        case 1:
                            break;
                        default:
                            return false;
                    }
                }
                MusicButton.this.removeCallbacks(MusicButton.this.mFastForwardRunnable);
                MusicButton.this.removeCallbacks(MusicButton.this.mFastBackRunnable);
                if (MusicButton.this.mPressKeyEvent == -1) {
                    return false;
                }
                if (MusicButton.this.mPressKeyEvent == 7) {
                    MusicManager.getInstance().fastForward(action);
                } else if (MusicButton.this.mPressKeyEvent == 8) {
                    MusicManager.getInstance().fastBack(action);
                }
                MusicButton.this.mPressKeyEvent = -1;
                view.setPressed(false);
                return true;
            }
        };
        this.mFastForwardRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.4
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 7;
                MusicManager.getInstance().fastForward(0);
            }
        };
        this.mFastBackRunnable = new Runnable() { // from class: com.yftech.car.widget.media.music.button.MusicButton.5
            @Override // java.lang.Runnable
            public void run() {
                MusicButton.this.mPressKeyEvent = 8;
                MusicManager.getInstance().fastBack(0);
            }
        };
        initStatus(context, attributeSet);
    }

    private void initStatus(Context context, AttributeSet attributeSet) {
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.MusicButton);
        this.mClickFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.MusicButton_music_click_function, 0);
        this.mClickLongFunction = typedArrayObtainStyledAttributes.getInt(R.styleable.MusicButton_music_long_click_function, -1);
        this.mDrawableSelected = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MusicButton_music_background_activated);
        this.mSelected = typedArrayObtainStyledAttributes.getString(R.styleable.MusicButton_music_activated_text);
        this.mListLoopText = typedArrayObtainStyledAttributes.getString(R.styleable.MusicButton_music_list_loop_text);
        this.mSinglePlayText = typedArrayObtainStyledAttributes.getString(R.styleable.MusicButton_music_single_play_text);
        this.mRandomPlayText = typedArrayObtainStyledAttributes.getString(R.styleable.MusicButton_music_random_play_text);
        this.mSequencePlayText = typedArrayObtainStyledAttributes.getString(R.styleable.MusicButton_music_sequence_play_text);
        this.mListLoopDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MusicButton_music_list_loop_drawable);
        this.mSinglePlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MusicButton_music_single_play_drawable);
        this.mRandomPlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MusicButton_music_random_play_drawable);
        this.mSequencePlayDrawable = typedArrayObtainStyledAttributes.getDrawable(R.styleable.MusicButton_music_sequence_play_drawable);
        this.mMediaDataListId = typedArrayObtainStyledAttributes.getResourceId(R.styleable.MusicButton_music_media_data_list_id, -1);
        this.mSwitchUsbPlay = typedArrayObtainStyledAttributes.getBoolean(R.styleable.MusicButton_music_switch_usb_play, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setClickFunction(int i) {
        Log.d(TAG, "setClickFunction: mClickFunction==" + i);
        switch (i) {
            case 0:
                MusicManager.getInstance().play();
                break;
            case 1:
                MusicManager.getInstance().pause();
                break;
            case 2:
                MusicManager.getInstance().next();
                break;
            case 3:
                MusicManager.getInstance().previous();
                break;
            case 4:
                MusicManager.getInstance().switchPlayPause();
                break;
            case 5:
                MusicManager.getInstance().switchPlayMode();
                break;
            case 6:
                MusicManager.getInstance().addCollectItem(null);
                break;
            default:
                int i2 = i - 100;
                if (i2 >= 0 && i2 <= 4 && this.mSwitchUsbPlay) {
                    MusicManager.getInstance().setCurrentUSB((byte) i2, true);
                    break;
                }
                break;
        }
    }

    public boolean isActivatedStatus() {
        return this.isActivatedStatus;
    }

    public boolean isFunction(int i) {
        return this.mClickFunction == i || this.mClickLongFunction == i;
    }

    public boolean isUsbFunction() {
        return (this.mClickFunction + (-100) >= 0 && this.mClickFunction + (-100) <= 4) || (this.mClickLongFunction + (-100) >= 0 && this.mClickLongFunction + (-100) <= 4);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, "onAttachedToWindow: mClickFunction==" + this.mClickFunction);
        switch (this.mClickFunction) {
            case 4:
                setActivatedStatus(MusicManager.getInstance().isPlaying());
                break;
            case 5:
                updatePlayMode(MusicManager.getInstance().getPlayMode());
                break;
            case 6:
                setActivatedStatus(MusicManager.getInstance().isCollectMusic(MusicManager.getInstance().getPlayMusic()));
                break;
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
        setOnClickListener(this.mOnClick);
        MediaFile playMusic = null;
        switch (this.mClickFunction) {
            case 4:
                setActivatedStatus(MusicManager.getInstance().isPlaying());
                break;
            case 5:
                updatePlayMode(MusicManager.getInstance().getPlayMode());
                break;
            case 6:
                playMusic = MusicManager.getInstance().getPlayMusic();
                setActivatedStatus(MusicManager.getInstance().isCollectMusic(playMusic));
                break;
        }
        if (this.mClickLongFunction != -1) {
            setOnLongClickListener(this.mOnLongClick);
        }
        if (this.mClickFunction == 7 || this.mClickFunction == 8 || this.mClickLongFunction == 7 || this.mClickLongFunction == 8) {
            setOnTouchListener(this.mOnTouchListener);
        }
        if (this.mClickFunction - 100 >= 0 || this.mClickLongFunction - 100 >= 4) {
            if (playMusic == null) {
                playMusic = MusicManager.getInstance().getPlayMusic();
            }
            setUsbStatus(MediaDataManager.getInstance().getUsbIndex(playMusic.getData()));
        }
    }

    public void setActivatedStatus(boolean z) {
        this.isActivatedStatus = z;
        if (this.mDrawableSelected != null) {
            setBackground(z ? this.mDrawableSelected : this.mDrawable);
        }
        if (this.mSelected != null) {
            setText(z ? this.mSelected : this.mText);
        }
    }

    @Deprecated
    public void setMusicOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public void setOnClickFunctionListener(OnClickFunctionListener onClickFunctionListener) {
        this.mFunctionListener = onClickFunctionListener;
    }

    @Override // android.view.View
    public void setOnClickListener(@Nullable View.OnClickListener onClickListener) {
        if (onClickListener == this.mOnClick) {
            super.setOnClickListener(onClickListener);
        } else {
            this.mOnClickListener = onClickListener;
        }
    }

    @Override // android.view.View
    public void setOnLongClickListener(@Nullable View.OnLongClickListener onLongClickListener) {
        if (this.mOnLongClick == onLongClickListener) {
            super.setOnLongClickListener(onLongClickListener);
        } else {
            this.mOnLongClickListener = onLongClickListener;
        }
    }

    public void setUsbStatus(int i) {
        if (isUsbFunction()) {
            setActivatedStatus(this.mClickFunction + (-100) == i || this.mClickLongFunction + (-100) == i);
        }
    }

    public void updatePlayMode(int i) {
        switch (i) {
            case 0:
                setText(this.mListLoopText);
                if (this.mListLoopDrawable != null) {
                    setBackground(this.mListLoopDrawable);
                }
                break;
            case 1:
                setText(this.mSinglePlayText);
                if (this.mSinglePlayDrawable != null) {
                    setBackground(this.mSinglePlayDrawable);
                }
                break;
            case 2:
                setText(this.mRandomPlayText);
                if (this.mRandomPlayDrawable != null) {
                    setBackground(this.mRandomPlayDrawable);
                }
                break;
            case 3:
                setText(this.mSequencePlayText);
                if (this.mSequencePlayDrawable != null) {
                    setBackground(this.mSequencePlayDrawable);
                }
                break;
            default:
                Log.e(TAG, "onFinishInflate: playMode==" + i);
                break;
        }
    }
}
